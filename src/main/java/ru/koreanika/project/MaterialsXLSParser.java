package ru.koreanika.project;

import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingElement;
import ru.koreanika.Common.PlumbingElementForSale.PlumbingType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MaterialsXLSParser {

    public static final String CURRENT_USER_PASSWORD = "9031713970";

    private final String materialsListPath;
    private final String analogsListPath;

    public MaterialsXLSParser(String materialsListPath, String analogsListPath) {
        this.materialsListPath = materialsListPath;
        this.analogsListPath = analogsListPath;
    }

    public void fillMaterialsList(List<Material> materialsListAvailable,
                                  List<PlumbingElement> plumbingElementsList,
                                  Set<PlumbingType> availablePlumbingTypes,
                                  Map<String, Double> materialsDeliveryFromManufacture) throws ParseXLSFileException {

        File xlsFile = new File(materialsListPath);
        Biff8EncryptionKey.setCurrentUserPassword(CURRENT_USER_PASSWORD);

        HSSFWorkbook wb = null;
        try (InputStream in = new FileInputStream(xlsFile)) {
            wb = new HSSFWorkbook(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        materialsListAvailable.clear();
        plumbingElementsList.clear();
        availablePlumbingTypes.clear();
        materialsDeliveryFromManufacture.clear();

        plumbingElementsList.addAll(PlumbingElementXlsReader.fillDataFromXls(wb));

        for (PlumbingElement pe : plumbingElementsList) {
            if (pe.isAvailable()) {
                availablePlumbingTypes.add(pe.getPlumbingType());
            }
        }

        Sheet sheet = wb.getSheetAt(0);
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

                //prices:
                Map<Integer, Integer> tableTopDepthsAndPricesMap = material.getTableTopDepthsAndPrices();
                Map<Integer, Integer> wallPanelDepthsAndPricesMap = material.getWallPanelDepthsAndPrices();
                Map<Integer, Integer> windowSillDepthsAndPricesMap = material.getWindowSillDepthsAndPrices();
                Map<Integer, Integer> footDepthsAndPricesMap = material.getFootDepthsAndPrices();

                tableTopDepthsAndPricesMap.clear();
                wallPanelDepthsAndPricesMap.clear();
                windowSillDepthsAndPricesMap.clear();
                footDepthsAndPricesMap.clear();

                List<Double> tableTopCoefficientList = material.getTableTopCoefficientList();
                List<Double> wallPanelCoefficientList = material.getWallPanelCoefficientList();
                List<Double> windowSillCoefficientList = material.getWindowSillCoefficientList();
                List<Double> footCoefficientList = material.getFootCoefficientList();

                tableTopCoefficientList.clear();
                wallPanelCoefficientList.clear();
                windowSillCoefficientList.clear();
                footCoefficientList.clear();

                // coefficient for TableTop:
                if (row.getCell(11).getCellType() != CellType.NUMERIC) {
                    tableTopCoefficientList.add(1.0);
                } else {
                    tableTopCoefficientList.add(row.getCell(11).getNumericCellValue());
                }

                if (row.getCell(12).getCellType() != CellType.NUMERIC) {
                    tableTopCoefficientList.add(1.0);
                } else {
                    tableTopCoefficientList.add(row.getCell(12).getNumericCellValue());
                }

                if (row.getCell(13).getCellType() != CellType.NUMERIC) {
                    tableTopCoefficientList.add(1.0);
                } else {
                    tableTopCoefficientList.add(row.getCell(13).getNumericCellValue());
                }

                if (row.getCell(14).getCellType() != CellType.NUMERIC) {
                    tableTopCoefficientList.add(1.0);
                } else {
                    tableTopCoefficientList.add(row.getCell(14).getNumericCellValue());
                }

                if (row.getCell(15).getCellType() != CellType.NUMERIC) {
                    tableTopCoefficientList.add(1.0);
                } else {
                    tableTopCoefficientList.add(row.getCell(15).getNumericCellValue());
                }

                // coefficient for WallPanel:

                if (row.getCell(16).getCellType() != CellType.NUMERIC) {
                    wallPanelCoefficientList.add(1.0);
                } else {
                    wallPanelCoefficientList.add(row.getCell(16).getNumericCellValue());
                }

                if (row.getCell(17).getCellType() != CellType.NUMERIC) {
                    wallPanelCoefficientList.add(1.0);
                } else {
                    wallPanelCoefficientList.add(row.getCell(17).getNumericCellValue());
                }

                if (row.getCell(18).getCellType() != CellType.NUMERIC) {
                    wallPanelCoefficientList.add(1.0);
                } else {
                    wallPanelCoefficientList.add(row.getCell(18).getNumericCellValue());
                }

                // coefficient for WindowSill:
                if (row.getCell(19).getCellType() != CellType.NUMERIC) {
                    windowSillCoefficientList.add(1.0);
                } else {
                    windowSillCoefficientList.add(row.getCell(19).getNumericCellValue());
                }

                if (row.getCell(20).getCellType() != CellType.NUMERIC) {
                    windowSillCoefficientList.add(1.0);
                } else {
                    windowSillCoefficientList.add(row.getCell(20).getNumericCellValue());
                }

                if (row.getCell(21).getCellType() != CellType.NUMERIC) {
                    windowSillCoefficientList.add(1.0);
                } else {
                    windowSillCoefficientList.add(row.getCell(21).getNumericCellValue());
                }

                if (row.getCell(22).getCellType() != CellType.NUMERIC) {
                    windowSillCoefficientList.add(1.0);
                } else {
                    windowSillCoefficientList.add(row.getCell(22).getNumericCellValue());
                }

                if (row.getCell(23).getCellType() != CellType.NUMERIC) {
                    windowSillCoefficientList.add(1.0);
                } else {
                    windowSillCoefficientList.add(row.getCell(23).getNumericCellValue());
                }

                // coefficient for Foot:
                if (row.getCell(24).getCellType() != CellType.NUMERIC) {
                    footCoefficientList.add(1.0);
                } else {
                    footCoefficientList.add(row.getCell(24).getNumericCellValue());
                }

                if (row.getCell(25).getCellType() != CellType.NUMERIC) {
                    footCoefficientList.add(1.0);
                } else {
                    footCoefficientList.add(row.getCell(25).getNumericCellValue());
                }

                if (row.getCell(26).getCellType() != CellType.NUMERIC) {
                    footCoefficientList.add(1.0);
                } else {
                    footCoefficientList.add(row.getCell(26).getNumericCellValue());
                }

                if (row.getCell(27).getCellType() != CellType.NUMERIC) {
                    footCoefficientList.add(1.0);
                } else {
                    footCoefficientList.add(row.getCell(27).getNumericCellValue());
                }

                if (row.getCell(28).getCellType() != CellType.NUMERIC) {
                    footCoefficientList.add(1.0);
                } else {
                    footCoefficientList.add(row.getCell(28).getNumericCellValue());
                }

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
                    } else if (material.getMainType().contains("Массив") ||
                            material.getMainType().contains("Массив_шпон")) {

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

                {
                    int type = 16;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int) price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                {
                    int type = 17;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int) price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                {
                    int type = 19;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int) price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                {
                    int type = 20;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int) price);
                    material.getSinkCommonCurrency().put(type, currency);
                }
                {
                    int type = 21;
                    double price = 0;
                    String currency = "RUB";

                    material.getSinkCommonTypesAndPrices().put(type, (int) price);
                    material.getSinkCommonCurrency().put(type, currency);
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
                        double price = 0;
                        String currency = "RUB";
                        if (row.getCell(174).getStringCellValue().equals("-")) {
                            price = 0;
                            currency = "RUB";
                        } else {
                            String priceStr = row.getCell(174).getStringCellValue();
                            priceStr = priceStr.replaceAll(",", ".");
                            price = Double.parseDouble(priceStr.split("=")[1]);
                            currency = priceStr.split("=")[0];
                        }

                        material.setCutoutCurrency(currency.toUpperCase());
                        material.getCutoutTypesAndPrices().put(7, (int) (price * 100));
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

                    if (row.getCell(i).getStringCellValue().equals("-")) continue;

                    String priceStr = row.getCell(i).getStringCellValue();
                    priceStr = priceStr.replaceAll(",", ".");
                    double price = Double.parseDouble(priceStr.split("=")[1]);
                    String currency = priceStr.split("=")[0];
                    material.setSinkInstallTypeCurrency(currency.toUpperCase());
                    material.getSinkInstallTypesAndPrices().put(i - 180, (int) (price * 100));
                }

                /** add sinks edge Types: */
                material.getSinkEdgeTypesRectangleAndPrices().clear();
                for (int i = 169; i <= 170; i++) {
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
                    }

                    material.setSinkEdgeTypeCurrency(currency.toUpperCase());
                    material.getSinkEdgeTypesRectangleAndPrices().put(i - 169, (int) (price * 100));
                }

                material.getSinkEdgeTypesCircleAndPrices().clear();
                for (int i = 171; i <= 172; i++) {

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
                    }
                    material.setSinkEdgeTypeCurrency(currency.toUpperCase());
                    material.getSinkEdgeTypesCircleAndPrices().put(i - 171, (int) (price * 100));
                }

                /** add cutout Types: */
                material.getCutoutTypesAndPrices().clear();

                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(161).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(161).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(1, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(160).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(160).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(2, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(167).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(167).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(3, (int) (price * 100));
                }

                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(162).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(162).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(4, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(168).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(168).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(5, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(173).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(173).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(6, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(174).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(174).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(7, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(167).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(167).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(8, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(186).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(186).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(13, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(187).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(187).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(14, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(193).getStringCellValue().equals("-") || row.getCell(193).getStringCellValue().isEmpty()) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(193).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(15, (int) (price * 100));
                }
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(194).getStringCellValue().equals("-") || row.getCell(194).getStringCellValue().isEmpty()) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(194).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setCutoutCurrency(currency.toUpperCase());
                    material.getCutoutTypesAndPrices().put(16, (int) (price * 100));
                }

                /** add siphons: (unavailable in this tab)*/
                material.getSiphonsTypesAndPrices().clear();
                material.setSiphonsCurrency("RUB");
                material.getSiphonsTypesAndPrices().put(0, 180000);
                material.getSiphonsTypesAndPrices().put(1, 360000);

                /** add joints: */
                material.getJointsTypesAndPrices().clear();
                for (int i = 163; i <= 164; i++) {
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
                    }
                    material.setJointsCurrency(currency.toUpperCase());
                    material.getJointsTypesAndPrices().put(i - 163, (int) (price * 100));
                }

                /** add plywoods: */
                material.getPlywoodPrices().clear();
                for (int i = 165; i <= 166; i++) {
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
                    }
                    material.getPlywoodCurrency().add(currency.toUpperCase());
                    material.getPlywoodPrices().add((int) (price * 100));
                }

                //add stonePolishing element:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(178).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(178).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setStonePolishingCurrency(currency.toUpperCase());
                    material.setStonePolishingPrice((int) (price * 100));
                }

                //add metalFooting element:
                {
                    material.getMetalFootingPrices().clear();
                    for (int i = 176; i <= 177; i++) {
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
                        }
                        material.getMetalFootingCurrency().add(currency.toUpperCase());
                        material.getMetalFootingPrices().add((int) (price * 100));
                    }
                }

                //add radius element:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(179).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(179).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }
                    material.setRadiusElementCurrency(currency.toUpperCase());
                    material.setRadiusElementPrice((int) (price * 100));
                }

                //add stone hem element:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(182).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(182).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }

                    material.setStoneHemCurrency(currency.toUpperCase());
                    material.setStoneHemPrice((int) (price * 100));
                }

                //add leakGroove element:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(175).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(175).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }
                    material.setLeakGrooveCurrency(currency.toUpperCase());
                    material.setLeakGroovePrice((int) (price * 100));
                }

                //add manual lifting:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(183).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(183).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }
                    material.setManualLiftingCurrency(currency.toUpperCase());
                    material.setManualLiftingPrice((int) (price * 100));
                }

                //add delivery price for inside MKAD:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(190).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(190).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }
                    material.setDeliveryInsideMKADCurrency(currency.toUpperCase());
                    material.setDeliveryInsideMKADPrice((int) (price));
                }

                //add measurer:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(188).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(188).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }
                    material.setMeasurerCurrency(currency.toUpperCase());
                    material.setMeasurerPrice((int) (price));
                }

                //add measurer price for km  outside MKAD:
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(189).getStringCellValue().equals("-")) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(189).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }
                    material.setMeasurerKMCurrency(currency.toUpperCase());
                    material.setMeasurerKMPrice((int) (price));
                }

                //add delivery price from manufacture
                {
                    double price = 0;
                    String currency = "RUB";
                    if (row.getCell(191).getStringCellValue().equals("-") || row.getCell(191).getStringCellValue().isEmpty()) {
                        price = 0;
                        currency = "RUB";
                    } else {
                        String priceStr = row.getCell(191).getStringCellValue();
                        priceStr = priceStr.replaceAll(",", ".");
                        price = Double.parseDouble(priceStr.split("=")[1]);
                        currency = priceStr.split("=")[0];
                    }
                    material.setDeliveryFromManufactureCurrency(currency.toUpperCase());
                    material.setDeliveryFromManufacture((int) (price));
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
        /** set prices without depency of MATERIAL */

        /** fill delivery from manufacture*/
        sheet = wb.getSheet("delivery");
        it = sheet.iterator();
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

        /** fill analogs for materials: */
        {
            HSSFWorkbook hssfWorkbook = null;
            try {
                InputStream inputStream = new FileInputStream(analogsListPath);
                hssfWorkbook = new HSSFWorkbook(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //for acrylic stone:
            Sheet sheetAnalogs = hssfWorkbook.getSheetAt(0);
            Iterator<Row> iterator = sheetAnalogs.iterator();
            iterator.next();

            while (iterator.hasNext()) {
                Row row = iterator.next();

                ArrayList<String> localListAnalogs = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    if (row.getCell(i * 5 + 1) == null || row.getCell(i * 5 + 2) == null || row.getCell(i * 5 + 3) == null || row.getCell(i * 5 + 4) == null) {
                        continue;
                    }
                    String materialName = row.getCell(i * 5 + 1).getStringCellValue() + "$" + row.getCell(i * 5 + 2).getStringCellValue() + "$" +
                            row.getCell(i * 5 + 3).getStringCellValue() + "$" + row.getCell(i * 5 + 4).getStringCellValue() + "$";
                    localListAnalogs.add(materialName);
                }

                //add Analogs to materials instances:
                for (String analogName : localListAnalogs) {
                    for (Material m : materialsListAvailable) {
                        if (m.getName().contains(analogName)) {
                            //add analogs to material:
                            m.getAnalogsList().clear();
                            for (String analogNameForAdd : localListAnalogs) {
                                //getMaterial by name:
                                for (Material mForAdd : materialsListAvailable) {
                                    if (mForAdd.getName().contains(analogNameForAdd)) {
                                        //add analog material:
                                        m.getAnalogsList().add(mForAdd);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //for quarz stone:
            Sheet sheetAnalogs1 = hssfWorkbook.getSheetAt(1);
            Iterator<Row> iterator1 = sheetAnalogs1.iterator();
            iterator1.next();

            while (iterator1.hasNext()) {
                Row row = iterator1.next();

                ArrayList<String> localListAnalogs = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    if (row.getCell(i * 5 + 1) == null || row.getCell(i * 5 + 2) == null || row.getCell(i * 5 + 3) == null || row.getCell(i * 5 + 4) == null) {
                        continue;
                    }
                    String materialName = row.getCell(i * 5 + 1).getStringCellValue() + "$" + row.getCell(i * 5 + 2).getStringCellValue() + "$" +
                            row.getCell(i * 5 + 3).getStringCellValue() + "$" + row.getCell(i * 5 + 4).getStringCellValue() + "$";
                    localListAnalogs.add(materialName);
                }

                //add Analogs to materials instances:
                for (String analogName : localListAnalogs) {

                    for (Material m : materialsListAvailable) {
                        if (m.getName().contains(analogName)) {
                            //add analogs to material:
                            m.getAnalogsList().clear();
                            for (String analogNameForAdd : localListAnalogs) {
                                //getMaterial by name:
                                for (Material mForAdd : materialsListAvailable) {
                                    if (mForAdd.getName().contains(analogNameForAdd)) {
                                        //add analog material:
                                        m.getAnalogsList().add(mForAdd);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            sheet = wb.getSheetAt(0);
        }

    }

}
