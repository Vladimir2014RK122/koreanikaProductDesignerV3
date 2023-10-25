package ru.koreanika.catalog;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MaterialsXLSParser {

    private final String materialsXLSPath;

    public MaterialsXLSParser(String materialsXLSPath) {
        this.materialsXLSPath = materialsXLSPath;
    }

    public void populateCatalogs(List<Material> materialsListAvailable, List<PlumbingElement> plumbingElementsList,
                                 Set<PlumbingType> availablePlumbingTypes,
                                 Map<String, Double> materialsDeliveryFromManufacturer) throws IOException {
        try (InputStream in = new FileInputStream(materialsXLSPath)) {
            try (HSSFWorkbook workbook = new HSSFWorkbook(in)) {
                fillMaterialsAvailable(materialsListAvailable, workbook);
                fillPlumbingElements(plumbingElementsList, availablePlumbingTypes, workbook);
                fillDeliveryFromManufacturer(materialsDeliveryFromManufacturer, workbook);
            }
        }
    }

    private void fillMaterialsAvailable(List<Material> materialsListAvailable, HSSFWorkbook workbook) {
        materialsListAvailable.clear();

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();
        it.next();
        it.next();
        it.next();
        int position = 2;

        int cell = 0;
        try {
            while (it.hasNext()) {
                position++;
                Row row = it.next();
                if (row.getCell(0).getStringCellValue().isEmpty() || row.getCell(5).getStringCellValue().isEmpty()) {
                    break;
                }
                int width = 0;
                int height = 0;

                if (!row.getCell(5).getStringCellValue().equals("-")) {
                    String[] size = row.getCell(5).getStringCellValue().split("-");
                    width = Integer.parseInt(size[0]);
                    height = Integer.parseInt(size[1]);
                }

                int minWidth = 0;
                int minHeight = 0;
                if (!row.getCell(6).getStringCellValue().equals("-")) {
                    String[] minSize = row.getCell(6).getStringCellValue().split("-");
                    minWidth = Integer.parseInt(minSize[0]);
                    minHeight = Integer.parseInt(minSize[1]);
                }

                String imgPath;
                if (row.getCell(8) != null && !row.getCell(8).getStringCellValue().isEmpty()) {
                    imgPath = row.getCell(8).getStringCellValue();
                } else {
                    imgPath = "no_img.png";
                }

                //depths:
                List<String> depths = new ArrayList<>();

                String cellValue = row.getCell(0).getStringCellValue();
                if (cellValue.contains("Кварцевый агломерат") || cellValue.contains("Натуральный камень") ||
                        cellValue.contains("Dektone") || cellValue.contains("Мраморный агломерат") ||
                        cellValue.contains("Кварцекерамический камень")) {
                    if (!row.getCell(10).getStringCellValue().equals("-")) {
                        String[] depthAndPriceArr1 = row.getCell(10).getStringCellValue().split("/");
                        for (String depthAndPrice : depthAndPriceArr1) {
                            String[] arr = depthAndPrice.split("=");
                            int depth = Integer.parseInt(arr[0].substring(1));
                            depths.add("" + depth);
                        }
                    }
                } else if (cellValue.contains("Массив") || cellValue.contains("Массив_шпон")) {
                    if (!row.getCell(11).getStringCellValue().equals("-")) {
                        String[] depthAndPriceArr1 = row.getCell(11).getStringCellValue().split("/");
                        for (String depthAndPrice : depthAndPriceArr1) {
                            String[] arr = depthAndPrice.split("=");
                            if (arr[1].contains(",")) {
                                arr[1] = arr[1].replace(",", ".");
                            }
                            int depth = Integer.parseInt(arr[0].substring(1));
                            depths.add("" + depth);
                        }
                    }
                } else {
                    depths.add("12");
                }

                Material material = new Material(row.getCell(4).getStringCellValue(),
                        row.getCell(0).getStringCellValue(),
                        row.getCell(1).getStringCellValue(),
                        row.getCell(2).getStringCellValue(),
                        row.getCell(3).getStringCellValue(), width, height, imgPath, new ArrayList<>(depths));

                material.setCurrency(row.getCell(9).getStringCellValue().toUpperCase());

                // coefficients
                for (int i : List.of(11, 12, 13, 14, 15)) {
                    material.getTableTopCoefficientList().add(getNumericCellValueOrDefault(row.getCell(i), 1.0));
                }
                for (int i : List.of(16, 17, 18)) {
                    material.getWallPanelCoefficientList().add(getNumericCellValueOrDefault(row.getCell(i), 1.0));
                }
                for (int i : List.of(19, 20, 21, 22, 23)) {
                    material.getWindowSillCoefficientList().add(getNumericCellValueOrDefault(row.getCell(i), 1.0));
                }
                for (int i : List.of(24, 25, 26, 27, 28)) {
                    material.getFootCoefficientList().add(getNumericCellValueOrDefault(row.getCell(i), 1.0));
                }

                // prices
                Map<Integer, Integer> tableTopDepthsAndPricesMap = material.getTableTopDepthsAndPrices();
                Map<Integer, Integer> wallPanelDepthsAndPricesMap = material.getWallPanelDepthsAndPrices();
                Map<Integer, Integer> windowSillDepthsAndPricesMap = material.getWindowSillDepthsAndPrices();
                Map<Integer, Integer> footDepthsAndPricesMap = material.getFootDepthsAndPrices();

                tableTopDepthsAndPricesMap.clear();
                wallPanelDepthsAndPricesMap.clear();
                windowSillDepthsAndPricesMap.clear();
                footDepthsAndPricesMap.clear();

                if (!row.getCell(5).getStringCellValue().equals("-")) {
                    if (material.getMainType().contains("Кварцевый агломерат") ||
                            material.getMainType().contains("Натуральный камень") ||
                            material.getMainType().contains("Dektone") ||
                            material.getMainType().contains("Кварцекерамический камень") ||
                            material.getMainType().contains("Мраморный агломерат")) {

                        //depths and prices:
                        String[] depthAndPriceArr = row.getCell(10).getStringCellValue().split("/");

                        for (String depthAndPrice : depthAndPriceArr) {
                            String[] arr = depthAndPrice.split("=");
                            int price = (int) (Double.parseDouble(arr[1].replace(",", ".")) * 100);
                            price = (int) (price / ((width * height) / 1000000.0));
                            int depth = Integer.parseInt(arr[0].substring(1));

                            tableTopDepthsAndPricesMap.put(depth, price);
                            wallPanelDepthsAndPricesMap.put(depth, price);
                            windowSillDepthsAndPricesMap.put(depth, price);
                            footDepthsAndPricesMap.put(depth, price);
                        }
                    } else if (material.getMainType().contains("Массив") || material.getMainType().contains("Массив_шпон")) {

                        //for TableTop:
                        if (!row.getCell(11).getStringCellValue().equals("-")) {
                            String[] depthAndPriceArr = row.getCell(11).getStringCellValue().split("/");
                            for (String depthAndPrice : depthAndPriceArr) {
                                int depth = 0;
                                int price = 0;

                                String[] arr = depthAndPrice.split("=");
                                if (arr[1].contains(",")) {
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));

                                tableTopDepthsAndPricesMap.put(depth, price);
                            }
                        }

                        //for WallPanel:
                        if (!row.getCell(16).getStringCellValue().equals("-")) {
                            String[] depthAndPriceArr = row.getCell(16).getStringCellValue().split("/");
                            for (String depthAndPrice : depthAndPriceArr) {
                                int depth = 0;
                                int price = 0;

                                String[] arr = depthAndPrice.split("=");
                                if (arr[1].contains(",")) {
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));

                                wallPanelDepthsAndPricesMap.put(depth, price);
                            }
                        }

                        //for WindowSill:
                        if (!row.getCell(19).getStringCellValue().equals("-")) {
                            String[] depthAndPriceArr = row.getCell(19).getStringCellValue().split("/");
                            for (String depthAndPrice : depthAndPriceArr) {
                                int depth = 0;
                                int price = 0;

                                String[] arr = depthAndPrice.split("=");
                                if (arr[1].contains(",")) {
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));

                                windowSillDepthsAndPricesMap.put(depth, price);
                            }
                        }

                        //for Foot:
                        if (!row.getCell(24).getStringCellValue().equals("-")) {
                            String[] depthAndPriceArr = row.getCell(24).getStringCellValue().split("/");
                            for (String depthAndPrice : depthAndPriceArr) {
                                int depth = 0;
                                int price = 0;
                                String[] arr = depthAndPrice.split("=");
                                if (arr[1].contains(",")) {
                                    arr[1] = arr[1].replace(",", ".");
                                }
                                price = (int) (Double.parseDouble(arr[1]) * 100);
                                depth = Integer.parseInt(arr[0].substring(1));
                                footDepthsAndPricesMap.put(depth, price);
                            }
                        }
                    } else {
                        int depth = 12;

                        //for TableTop:
                        int price = (int) (row.getCell(11).getNumericCellValue() * 100);
                        tableTopDepthsAndPricesMap.put(depth, price);

                        //for WallPanel:
                        price = (int) (row.getCell(16).getNumericCellValue() * 100);
                        wallPanelDepthsAndPricesMap.put(depth, price);

                        //for WindowSill:
                        price = (int) (row.getCell(19).getNumericCellValue() * 100);
                        windowSillDepthsAndPricesMap.put(depth, price);

                        //for Foot:
                        price = (int) (row.getCell(24).getNumericCellValue() * 100);
                        footDepthsAndPricesMap.put(depth, price);
                    }
                }

                material.setCalculationType((int) row.getCell(29).getNumericCellValue());
                material.setMinMaterialSize(minWidth, minHeight);

                material.setMinCountSlabs((int) row.getCell(30).getNumericCellValue());

                /** add sinks: */
                material.getAvailableSinkTypes().clear();
                material.getAvailableSinkModels().clear();

                if (!row.getCell(31).getStringCellValue().equals("-")) {
                    String availableSinks = row.getCell(31).getStringCellValue();
                    String[] availableSinksArr = availableSinks.split("/");
                    for (String s : availableSinksArr) {
                        material.getAvailableSinkTypes().add(Integer.valueOf(s));
                    }
                }

                for (int i = 32; i <= 88; i++) {
                    String model = sheet.getRow(2).getCell(i).getStringCellValue();

                    if (row.getCell(i).getStringCellValue().equals("-") || row.getCell(i).getStringCellValue().isEmpty()) {
                        continue;
                    }

                    String priceStr = row.getCell(i).getStringCellValue();
                    priceStr = priceStr.replaceAll(",", ".");
                    double price = Double.parseDouble(priceStr.split("=")[1]);
                    String currency = priceStr.split("=")[0];
                    material.setSinkCurrency(currency.toUpperCase());
                    material.getAvailableSinkModels().put(model, (int) (price * 100));
                }

                for (int type : List.of(16, 17, 19, 20, 21)) {
                    material.getSinkCommonTypesAndPrices().put(type, 0);
                    material.getSinkCommonCurrency().put(type, "RUB");
                }

                /** Pallets: */
                for (int i = 89; i <= 96; i++) {
                    String model = sheet.getRow(2).getCell(i).getStringCellValue();

                    if (row.getCell(i).getStringCellValue().equals("-") || row.getCell(i).getStringCellValue().isEmpty()) {
                        continue;
                    }

                    String priceStr = row.getCell(i).getStringCellValue();
                    priceStr = priceStr.replaceAll(",", ".");
                    double price = Double.parseDouble(priceStr.split("=")[1]);
                    String currency = priceStr.split("=")[0];
                    material.setPalletsCurrency(currency.toUpperCase());
                    material.getPalletsModelsAndPrices().put(model, (int) (price * 100));
                }

                /** add grooves: */
                material.getGroovesTypesAndPrices().clear();
                material.getAvailableGroovesTypes().clear();
                for (int i = 97; i <= 100; i++) {

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];

                        material.getAvailableGroovesTypes().add(i - 97 + 1);
                    }
                    material.setGroovesCurrency(currency.toUpperCase());
                    material.getGroovesTypesAndPrices().put(i - 97, (int) (price * 100));
                }

                /** add rods: */
                material.getRodsTypesAndPrices().clear();
                material.getAvailableRodsTypes().clear();
                for (int i = 101; i <= 102; i++) {

                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                        material.getAvailableRodsTypes().add(i - 101 + 1);
                    }
                    material.setRodsCurrency(currency.toUpperCase());
                    material.getRodsTypesAndPrices().put(i - 101, (int) (price * 100));
                }

                /** edges heights and prices: */
                if (material.getMainType().contains("Кварцевый агломерат") ||
                        material.getMainType().contains("Натуральный камень") ||
                        material.getMainType().contains("Dektone") ||
                        material.getMainType().contains("Мраморный агломерат") ||
                        material.getMainType().contains("Кварцекерамический камень")) {
                    for (int i = 122; i <= 148; i++) {
                        if (!row.getCell(i).getStringCellValue().equals("-")) {
                            String priceStr = row.getCell(i).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            String currency = priceStr.split("=")[0];
                            double price = Double.parseDouble(priceStr.split("=")[1]);
                            Integer edgeNumber = (int) sheet.getRow(2).getCell(i).getNumericCellValue();
                            material.setEdgesCurrency(currency.toUpperCase());
                            material.getEdgesAndPrices().put(edgeNumber, price);
                        }
                    }
                } else {
                    for (int i = 105; i <= 121; i++) {
                        if (!row.getCell(i).getStringCellValue().equals("-")) {
                            String priceStr = row.getCell(i).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            String currency = priceStr.split("=")[0];
                            double price = Double.parseDouble(priceStr.split("=")[1]);

                            Integer edgeNumber = (int) sheet.getRow(2).getCell(i).getNumericCellValue();
                            material.setEdgesCurrency(currency.toUpperCase());
                            material.getEdgesAndPrices().put(edgeNumber, price);
                        }
                    }
                }

                /** borders heights and prices: */
                Map<Integer, Integer> borderTypesAndPrices = material.getBorderTypesAndPrices();
                Map<Integer, Integer> borderTopCutTypesAndPrices = material.getBorderTopCutTypesAndPrices();
                Map<Integer, Integer> borderSideCutTypesAndPrices = material.getBorderSideCutTypesAndPrices();

                if (material.getMainType().contains("Кварцевый агломерат") ||
                        material.getMainType().contains("Натуральный камень") ||
                        material.getMainType().contains("Dektone") ||
                        material.getMainType().contains("Мраморный агломерат") ||
                        material.getMainType().contains("Кварцекерамический камень")) {

                    String currency;
                    double price;

                    if (!row.getCell(149).getStringCellValue().equals("-")) {
                        String priceStr = row.getCell(149).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        currency = priceStr.split("=")[0].toUpperCase();
                        price = Double.parseDouble(priceStr.split("=")[1]);
                    } else {
                        currency = "RUB";
                        price = 0;
                    }
                    material.setBorderCurrency(currency);
                    borderTypesAndPrices.put(0, (int) (price * 100));
                } else {
                    for (int i = 150; i <= 153; i++) {
                        String currency;
                        double price;
                        if (!row.getCell(i).getStringCellValue().equals("-")) {
                            String priceStr = row.getCell(i).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            currency = (priceStr.split("=")[0]).toUpperCase();
                            price = Double.parseDouble(priceStr.split("=")[1]);
                        } else {
                            currency = "RUB";
                            price = 0;
                        }
                        material.setBorderCurrency(currency);
                        borderTypesAndPrices.put(i - 150, (int) (price * 100));
                    }

                    //cutout for radiator
                    {
                        Price price = parsePriceCell(row.getCell(174));
                        material.setCutoutCurrency(price.currency);
                        material.getCutoutTypesAndPrices().put(7, (int) (price.price * 100));
                    }
                }

                for (int i = 154; i <= 157; i++) {
                    double price;
                    if (!row.getCell(i).getStringCellValue().equals("-")) {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                    } else {
                        price = 0;
                    }
                    borderTopCutTypesAndPrices.put(i - 154, (int) (price * 100));
                }

                for (int i = 158; i <= 159; i++) {
                    double price;
                    if (!row.getCell(i).getStringCellValue().equals("-")) {
                        String priceStr = row.getCell(i).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                    } else {
                        price = 0;
                    }
                    borderSideCutTypesAndPrices.put(i - 158, (int) (price * 100));
                }

                //add sinks installTypes:
                material.getSinkInstallTypesAndPrices().clear();
                for (int i = 180; i <= 181; i++) {
                    if (row.getCell(i).getStringCellValue().equals("-")) {
                        continue;
                    }
                    Price price = parsePriceCell(row.getCell(i));
                    material.setSinkInstallTypeCurrency(price.currency);
                    material.getSinkInstallTypesAndPrices().put(i - 180, (int) (price.price * 100));
                }

                /** add sinks edge Types: */
                material.getSinkEdgeTypesRectangleAndPrices().clear();
                for (int i = 169; i <= 170; i++) {
                    Price price = parsePriceCell(row.getCell(i));
                    material.setSinkEdgeTypeCurrency(price.currency);
                    material.getSinkEdgeTypesRectangleAndPrices().put(i - 169, (int) (price.price * 100));
                }

                material.getSinkEdgeTypesCircleAndPrices().clear();
                for (int i = 171; i <= 172; i++) {
                    Price price = parsePriceCell(row.getCell(i));
                    material.setSinkEdgeTypeCurrency(price.currency);
                    material.getSinkEdgeTypesCircleAndPrices().put(i - 171, (int) (price.price * 100));
                }

                // add cutout types
                Map<Integer, Integer> indexToCell = new HashMap<>();
                indexToCell.put(1, 161);
                indexToCell.put(2, 160);
                indexToCell.put(3, 167);
                indexToCell.put(4, 162);
                indexToCell.put(5, 168);
                indexToCell.put(6, 173);
                indexToCell.put(7, 174);
                indexToCell.put(8, 167);
                indexToCell.put(13, 186);
                indexToCell.put(14, 187);
                indexToCell.put(15, 193);
                indexToCell.put(16, 194);

                material.getCutoutTypesAndPrices().clear();
                for (Map.Entry<Integer, Integer> entry : indexToCell.entrySet()) {
                    Price price = parsePriceCell(row.getCell(entry.getValue()));
                    material.setCutoutCurrency(price.currency);
                    material.getCutoutTypesAndPrices().put(entry.getKey(), (int) (price.price * 100));
                }

                /** add siphons: (unavailable in this tab)*/
                material.getSiphonsTypesAndPrices().clear();
                material.setSiphonsCurrency("RUB");
                material.getSiphonsTypesAndPrices().put(0, 180000);
                material.getSiphonsTypesAndPrices().put(1, 360000);

                /** add joints: */
                material.getJointsTypesAndPrices().clear();
                for (int i = 163; i <= 164; i++) {
                    Price price = parsePriceCell(row.getCell(i));
                    material.setJointsCurrency(price.currency);
                    material.getJointsTypesAndPrices().put(i - 163, (int) (price.price * 100));
                }

                /** add plywoods: */
                material.getPlywoodPrices().clear();
                for (int i = 165; i <= 166; i++) {
                    Price price = parsePriceCell(row.getCell(i));
                    material.getPlywoodCurrency().add(price.currency);
                    material.getPlywoodPrices().add((int) (price.price * 100));
                }

                //add stonePolishing element:
                {
                    Price price = parsePriceCell(row.getCell(178));
                    material.setStonePolishingCurrency(price.currency);
                    material.setStonePolishingPrice((int) (price.price * 100));
                }

                //add metalFooting element:
                {
                    material.getMetalFootingPrices().clear();
                    for (int i = 176; i <= 177; i++) {
                        Price price = parsePriceCell(row.getCell(i));
                        material.getMetalFootingCurrency().add(price.currency);
                        material.getMetalFootingPrices().add((int) (price.price * 100));
                    }
                }

                //add radius element:
                {
                    Price price = parsePriceCell(row.getCell(179));
                    material.setRadiusElementCurrency(price.currency);
                    material.setRadiusElementPrice((int) (price.price * 100));
                }

                //add stone hem element:
                {
                    Price price = parsePriceCell(row.getCell(182));
                    material.setStoneHemCurrency(price.currency);
                    material.setStoneHemPrice((int) (price.price * 100));
                }

                //add leakGroove element:
                {
                    Price price = parsePriceCell(row.getCell(175));
                    material.setLeakGrooveCurrency(price.currency);
                    material.setLeakGroovePrice((int) (price.price * 100));
                }

                //add manual lifting:
                {
                    Price price = parsePriceCell(row.getCell(183));
                    material.setManualLiftingCurrency(price.currency);
                    material.setManualLiftingPrice((int) (price.price * 100));
                }

                //add delivery price for inside MKAD:
                {
                    Price price = parsePriceCell(row.getCell(190));
                    material.setDeliveryInsideMKADCurrency(price.currency);
                    material.setDeliveryInsideMKADPrice((int) (price.price));
                }

                //add measurer:
                {
                    Price price = parsePriceCell(row.getCell(188));
                    material.setMeasurerCurrency(price.currency);
                    material.setMeasurerPrice((int) (price.price));
                }

                //add measurer price for km  outside MKAD:
                {
                    Price price = parsePriceCell(row.getCell(189));
                    material.setMeasurerKMCurrency(price.currency);
                    material.setMeasurerKMPrice((int) (price.price));
                }

                //add delivery price from manufacture
                {
                    Price price = parsePriceCell(row.getCell(191));
                    material.setDeliveryFromManufactureCurrency(price.currency);
                    material.setDeliveryFromManufacture((int) (price.price));
                }

                //add sheet cutting price from manufacture
                {
                    material.setSheetCuttingCurrency("RUB");
                    material.setSheetCuttingPrice((int) (1000.0));
                }

                /** add notification 1: */
                {
                    int notification1 = (int) row.getCell(184).getNumericCellValue();
                    material.setNotification1(notification1);
                }

                /** add notification 2: */
                {
                    int notification2 = (int) row.getCell(185).getNumericCellValue();
                    material.setNotification2(notification2);
                }

                /** add promotion info: */
                cell = 195;
                if (row.getCell(cell).getCellType() == CellType.STRING) {
                    String value = row.getCell(cell).getStringCellValue();
                    material.setPromotion(value.equalsIgnoreCase("yes"));
                } else {
                    material.setPromotion(false);
                }

                /** add visual properties: */
                cell = 196;
                if (row.getCell(cell).getCellType() == CellType.STRING &&
                        !row.getCell(cell).getStringCellValue().equals("-") &&
                        !row.getCell(cell).getStringCellValue().equalsIgnoreCase("н/д")) {

                    String[] values = row.getCell(cell).getStringCellValue().split("##");
                    material.getVisualProperties().clear();

                    if (values.length >= 1) {
                        material.getVisualProperties().put(Material.VIS_PROP_COLOR, values[0]);
                    }
                    if (values.length >= 2) {
                        material.getVisualProperties().put(Material.VIS_PROP_TEXTURE, values[1]);
                    }
                    if (values.length >= 3) {
                        material.getVisualProperties().put(Material.VIS_PROP_SURFACE, values[2]);
                    }
                } else {
                    material.getVisualProperties().clear();
                }
                materialsListAvailable.add(material);
            }
        } catch (Exception e) {
            System.err.println("Error in Materials File pos:" + position);
            e.printStackTrace();
        }
    }

    private void fillPlumbingElements(List<PlumbingElement> plumbingElementsList,
                                      Set<PlumbingType> availablePlumbingTypes, HSSFWorkbook workbook) {
        plumbingElementsList.clear();
        availablePlumbingTypes.clear();

        Sheet sheet = workbook.getSheet("ExternalElement");
        Iterator<Row> it = sheet.iterator();
        it.next();

        while (it.hasNext()) {
            Row row = it.next();

            if (getPlumbingCellByName(row, "id").getCellType() != CellType.NUMERIC) {
                System.err.println("WARNING: [PlumbingElementXLSReader] Skipping plumbing element in row " +
                        row.getRowNum() + ", id non-numeric");
                break;
            }

            int id = getPlumbingIntegerByName(row, "id");

            String name = getPlumbingStringByName(row, "name");
            List<String> models = Arrays.asList(getPlumbingStringByName(row, "models").split(","));
            List<String> sizes = Arrays.asList(getPlumbingStringByName(row, "sizes").split(","));
            String currency = getPlumbingStringByName(row, "currency").toUpperCase();

            String[] chunks = getPlumbingStringByName(row, "prices").split(",");
            List<Double> prices = Arrays.stream(chunks).map(Double::parseDouble).toList();

            PlumbingType type = PlumbingType.getByNumber(getPlumbingIntegerByName(row, "type"));
            boolean available = getPlumbingIntegerByName(row, "available") != 0;

            if (available) {
                availablePlumbingTypes.add(type);
            }
            plumbingElementsList.add(new PlumbingElement(id, type, available, name, models, sizes, currency, prices));
        }
    }

    private void fillDeliveryFromManufacturer(Map<String, Double> materialsDeliveryFromManufacture, HSSFWorkbook workbook) {
        materialsDeliveryFromManufacture.clear();

        HSSFSheet sheet = workbook.getSheet("delivery");
        Iterator<Row> it = sheet.iterator();
        it.next();
        while (it.hasNext()) {
            Row row = it.next();
            if (row.getCell(1).getStringCellValue().isEmpty()) {
                break;
            }
            String groupName = row.getCell(1).getStringCellValue();
            Double price = row.getCell(2).getNumericCellValue();
            materialsDeliveryFromManufacture.put(groupName, price);
        }
    }

    private record Price(String currency, double price) {
    }

    private static Price parsePriceCell(Cell cell) {
        String cellValue = cell.getStringCellValue();
        if (cellValue.isEmpty() || cellValue.equals("-")) {
            return new Price("RUB", 0.0);
        } else {
            String[] chunks = cellValue.replaceAll(",", ".").split("=");
            return new Price(chunks[0].toUpperCase(), Double.parseDouble(chunks[1]));
        }
    }

    private static double getNumericCellValueOrDefault(Cell cell, double defaultValue) {
        return (cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : defaultValue);
    }

    private static int getPlumbingIntegerByName(Row row, String columName) {
        return (int) getPlumbingCellByName(row, columName).getNumericCellValue();
    }

    private static String getPlumbingStringByName(Row row, String columName) {
        return getPlumbingCellByName(row, columName).getStringCellValue();
    }

    private static Cell getPlumbingCellByName(Row row, String columnName) {
        return switch (columnName) {
            case "id" -> row.getCell(0);
            case "type" -> row.getCell(1);
            case "available" -> row.getCell(2);
            case "name" -> row.getCell(3);
            case "models" -> row.getCell(4);
            case "sizes" -> row.getCell(5);
            case "prices" -> row.getCell(6);
            case "currency" -> row.getCell(7);
            default -> null;
        };
    }

}
