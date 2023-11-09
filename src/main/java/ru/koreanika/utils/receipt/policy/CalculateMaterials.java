package ru.koreanika.utils.receipt.policy;

import ru.koreanika.catalog.Catalogs;
import ru.koreanika.common.material.Material;
import ru.koreanika.common.material.MaterialSheet;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutObject;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.project.Project;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.news.NewsController;
import ru.koreanika.utils.receipt.domain.Receipt;
import ru.koreanika.utils.receipt.domain.ReceiptItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalculateMaterials implements Command {

    public static void calculateMaterials() {
        new CalculateMaterials().execute();
    }

    @Override
    public void execute() {
        Receipt.getMaterialAndSheets().clear();
        Receipt.getMaterialSquaresCalcType2().clear();
        Receipt.getShapesSquaresCalcType2().clear();

        Receipt.getTableTopsSquaresCalcType2().clear();
        Receipt.getWallPanelsSquaresCalcType2().clear();
        Receipt.getWindowsillsSquaresCalcType2().clear();

        Receipt.getTableTopSquaresCalcType1Result().clear();
        Receipt.getTableTopSquaresCalcType2Result().clear();
        Receipt.getWallPanelSquaresCalcType1Result().clear();
        Receipt.getWallPanelSquaresCalcType2Result().clear();
        Receipt.getWindowsillSquaresCalcType1Result().clear();
        Receipt.getWindowsillSquaresCalcType2Result().clear();

        Receipt.getCutShapesAndReceiptItem().clear();
        Receipt.getReceiptImageItemsList().clear();
        Receipt.pricesActual = true;

        Receipt.quartzInProject = false;

        Receipt.allSquare = 0.0;

        /** new calculate, more correct instead previous version:  START*/
        Map<MaterialSheet, List<CutShape>> sheetsAndShapes = CutDesigner.getInstance().getCutPane().getSheetsAndShapesOnItMap();
        Map<MaterialSheet, Double> sheetsAndUsesSumSquare = new LinkedHashMap<>();

        Map<Material, Integer> materialAndNumberOfShapes = new LinkedHashMap<>();//need for calculate delivery

        for (Map.Entry<MaterialSheet, List<CutShape>> entry : sheetsAndShapes.entrySet()) {
            double CutShapesSumSquare = 0;
            for (CutShape cutShape : entry.getValue()) {
                CutShapesSumSquare += cutShape.getShapeSquare();
            }
            CutShapesSumSquare /= 10000;
            sheetsAndUsesSumSquare.put(entry.getKey(), CutShapesSumSquare);
        }

        //fill materialAndNumberOfShapes
        for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
            Material material = cutShape.getMaterial();
            if (materialAndNumberOfShapes.containsKey(material)) {
                materialAndNumberOfShapes.put(material, materialAndNumberOfShapes.get(material) + 1);
            } else {
                materialAndNumberOfShapes.put(material, 1);
            }
        }

        Receipt.getMaterialStocks().clear();//stock Name, stockResultSize in RUR
        //get receipts for cutShapes which calculate in cut mode

        Receipt.receiptLog = "CUARZ MATERIAL:";
        for (Map.Entry<MaterialSheet, List<CutShape>> entry : sheetsAndShapes.entrySet()) {
            double Pm2Material = entry.getKey().getPrice(entry.getValue().get(0).getElementType(), entry.getKey().getDepth());
            double Slist = entry.getKey().getUsesList() * (entry.getKey().getMinSheetSquare()) / 1000000;//in meters
            double Ssum = sheetsAndUsesSumSquare.get(entry.getKey());
            double Swaste = Slist - Ssum;
            double Plist = Pm2Material * Slist;
            double numberOfSheetWasCutted = (((entry.getKey().getSheetMinHeight() * entry.getKey().getSheetMinWidth() * 2) / 1000000) / Slist) - 1;
            int numberOfShapes = entry.getValue().size();

            String currency = entry.getKey().getSheetCurrency();

            if (entry.getKey().getMaterial().getCalculationType() == 1) {
                continue;
            }

            Receipt.receiptLog += "\n************ ЛИСТ ***************";
            Receipt.receiptLog += "\nЛист: " + entry.getKey().getMaterial().getName().replace("$", " ");
            Receipt.receiptLog += "\nЦена м2 материала = " + Pm2Material + " " + currency;
            Receipt.receiptLog += "\nЦена всего листа = " + Plist + " " + currency;
            Receipt.receiptLog += "\nКоэффициенты для столешницы = " + entry.getKey().getMaterial().getTableTopCoefficientList();
            Receipt.receiptLog += "\nКоэффициенты для cтеновой панели = " + entry.getKey().getMaterial().getWallPanelCoefficientList();
            Receipt.receiptLog += "\nКоэффициенты для подоконника = " + entry.getKey().getMaterial().getWindowSillCoefficientList();
            Receipt.receiptLog += "\nКоэффициенты для опоры = " + entry.getKey().getMaterial().getFootCoefficientList();
            Receipt.receiptLog += "\nПлощадь листа = " + Slist;
            Receipt.receiptLog += "\nСуммарная площадь изделий = " + Ssum;
            Receipt.receiptLog += "\nКоличество изделий = " + numberOfShapes;
            Receipt.receiptLog += "\nКоличество разрезов листа = " + numberOfSheetWasCutted;
            Receipt.receiptLog += "\nИспользование листа в % = " + (Ssum / Slist);
            Receipt.receiptLog += "\nНеиспользованный остаток = " + (Swaste);

            for (CutShape cutShape : entry.getValue()) {
                double Sshape = cutShape.getShapeSquare() / 10000;
                Receipt.allSquare += Sshape;
                double MaterialPrice = Plist * (Sshape / Ssum);

                SketchShape sketchShape = (SketchShape) cutShape.getSketchObjectOwner();

                String units = "м. кв.";
                double pseudoCount = Sshape;
                double coefficient = ((SketchShape) cutShape.getSketchObjectOwner()).getWorkCoefficient();

                String name = sketchShape.getProductName();
                double WorkPrice = Sshape * Pm2Material * coefficient;
                Material material = cutShape.getMaterial();

                double deliveryCommonPrice = material.getDeliveryFromManufacture();
                int shapesNumber = materialAndNumberOfShapes.get(material);
                double deliveryPrice = deliveryCommonPrice / shapesNumber;//in RUR
                if (currency.equals("USD")) {
                    deliveryPrice = deliveryPrice / MainWindow.getUSDValue().doubleValue();//in USD
                } else if (currency.equals("EUR")) {
                    deliveryPrice = deliveryPrice / MainWindow.getEURValue().doubleValue();//in EUR
                }

                Receipt.receiptLog += "\n---------------------------------";
                Receipt.receiptLog += "\nНомер изделия = " + sketchShape.getShapeNumber();
                Receipt.receiptLog += "\nСтоимость доставки материала = " + deliveryCommonPrice;
                Receipt.receiptLog += "\nПлощадь изделия = " + Sshape;
                Receipt.receiptLog += "\nСтоимость материала для изделия = " + MaterialPrice;
                Receipt.receiptLog += "\nКоэффициент на обработку = " + coefficient;

                System.out.println("---------------------------------");
                System.out.println("\nShape number = " + sketchShape.getShapeNumber());
                System.out.println("deliveryCommonPrice for Material = " + deliveryCommonPrice);
                System.out.println("Sshape = " + Sshape);
                System.out.println("MaterialPrice for this piece = " + MaterialPrice);
                System.out.println("work Coefficient = " + coefficient);

                if ((Ssum / Slist) < 0.5) {
                    WorkPrice *= (1.5 - (Ssum / Slist));
                    Receipt.receiptLog += "\nКоэффициент за использование менее 50% листа = " + (1.5 - (Ssum / Slist));
                }

                if (numberOfSheetWasCutted != 0) {
                    double sheetCuttinngPriceFromManufacture = material.getSheetCuttingPrice() * numberOfSheetWasCutted;
                    if (currency.equals("USD")) {
                        sheetCuttinngPriceFromManufacture = material.getSheetCuttingPrice() / MainWindow.getUSDValue().doubleValue();//in USD
                    } else if (currency.equals("EUR")) {
                        sheetCuttinngPriceFromManufacture = material.getSheetCuttingPrice() / MainWindow.getEURValue().doubleValue();//in EUR
                    }

                    if (entry.getKey().isAdditionalSheet()) {
                        sheetCuttinngPriceFromManufacture = 0;
                    }

                    WorkPrice += (sheetCuttinngPriceFromManufacture / numberOfShapes);
                    Receipt.receiptLog += "\n Стоимость разреза материала (+ к стоимости обработки каждого изделия)= " + (sheetCuttinngPriceFromManufacture / numberOfShapes);
                }

                if (material.getName().indexOf("Dektone") != -1 ||
                        material.getMainType().indexOf("Кварцекерамический камень") != -1 ||
                        material.getMainType().indexOf("Мраморный агломерат") != -1 ||
                        material.getMainType().indexOf("Натуральный камень") != -1) {

//                    WorkPrice = MaterialPrice * coefficient;
                    //остаток * цену м2 * коэф стеновой - 0.1 и распределить между всеми изделииями
                    WorkPrice += Pm2Material * Swaste * (Sshape / Ssum) * (material.getWallPanelCoefficientList().get(0) - 0.1);

                    Receipt.receiptLog += "\nПлощадь остатка для фигуры (остаток*(Sфигуры/Sостатка))) = " + (Swaste * (Sshape / Ssum));
                    Receipt.receiptLog += "\nКоэффициент для стеновой панели = " + (material.getWallPanelCoefficientList().get(0));
                    Receipt.receiptLog += "\nДоп стоимость работ для остатка (ценам2*остаток*(Sфигуры/Sостатка)*(КоэфСтеновойПанели - 0.1)) = " + (Pm2Material * Swaste * (Sshape / Ssum) * (material.getWallPanelCoefficientList().get(0) - 0.1));
                }

                String stockName = NewsController.getNewsController().getMaterialStockSize(material).split("##")[0];
                double stockSize = Double.parseDouble(NewsController.getNewsController().getMaterialStockSize(material).split("##")[1]);
                double stockResultSizeForShape = MaterialPrice * stockSize;

                if (currency.equals("USD")) {
                    stockResultSizeForShape = stockResultSizeForShape * MainWindow.getUSDValue().doubleValue();//in USD
                } else if (currency.equals("EUR")) {
                    stockResultSizeForShape = stockResultSizeForShape * MainWindow.getEURValue().doubleValue();//in EUR
                }

                if (stockSize != 0) {
                    Double d = Receipt.getMaterialStocks().get(stockName);
                    if (d == null) d = 0.0;
                    d += stockResultSizeForShape;
                    Receipt.getMaterialStocks().put(stockName, d);
                }

                Receipt.receiptLog += "\nСтоимость обработки = " + WorkPrice + currency;
                Receipt.receiptLog += "\nСтоимость доставки = " + deliveryPrice + currency;
                Receipt.receiptLog += "\nОбщая стоимость = " + (MaterialPrice + WorkPrice + deliveryPrice + currency);
                Receipt.receiptLog += "\nСкидка по акции = " + stockName + " = " + stockResultSizeForShape + " RUB";

                ReceiptItem receiptItem;
                if (entry.getKey().isActualPrice()) {
                    receiptItem = new ReceiptItem(name, units, 0, pseudoCount, currency, Pm2Material, MaterialPrice + WorkPrice + deliveryPrice);
                } else {
                    receiptItem = new ReceiptItem(name, units, 0, pseudoCount, currency, Pm2Material, MaterialPrice + WorkPrice + deliveryPrice, "red");
                    Receipt.pricesActual = false;
                }

                Receipt.getCutShapesAndReceiptItem().put(cutShape, receiptItem);
            }
            Receipt.quartzInProject = true;//need for acryl additional price coefficient
        }

        /* part for recalculate prices for stone elements for show it proportional*/
        double allShapesS = 0;
        double allShapesPrice = 0;

        //get all prices and all square:
        Map<CutShape, ReceiptItem> savedMap = new LinkedHashMap<>(Receipt.getCutShapesAndReceiptItem());
        for (Map.Entry<CutShape, ReceiptItem> entry : savedMap.entrySet()) {
            ReceiptItem receiptItem = entry.getValue();

            double shapeS = receiptItem.getPseudoCountDouble();
            double shapePrice = receiptItem.getAllPriceInRURDouble();

            allShapesS += shapeS;
            allShapesPrice += shapePrice;
        }

        for (Map.Entry<CutShape, ReceiptItem> entry : savedMap.entrySet()) {
            ReceiptItem receiptItem = entry.getValue();

            double shapeS = receiptItem.getPseudoCountDouble();
            double newShapePrice = (shapeS / allShapesS) * allShapesPrice;

            receiptItem.setAdditionalPrice(newShapePrice);
            receiptItem.setCurrency("RUB");
        }

        //get receipts for cutShapes which calculate in square mode:
        // create additional coefficients:
        Receipt.receiptLog += "\n**************************";
        Receipt.receiptLog += "\n*** ACRYLS MATERIAL ***";

        {
            Receipt.getCutObjectsAndSquareCalcType1().clear();
            for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
                if (cutShape.getMaterial().getCalculationType() == 2) {
                    continue;
                }
                double S = cutShape.getShapeSquare();
                S = Math.ceil(S / Math.pow(Project.getCommonShapeScale(), 2));
                S = S / 1000000;//mm^2 to m^2
                Receipt.allSquare += S;
                Receipt.getCutObjectsAndSquareCalcType1().put(cutShape, S);
            }

            /** get summ square for all acryl products: */
            double summSquare = 0.0;
            for (Map.Entry<CutObject, Double> entry : Receipt.getCutObjectsAndSquareCalcType1().entrySet()) {
                Material material = entry.getKey().getMaterial();
                if (material.getName().contains("Акриловый камень") ||
                        material.getName().contains("Полиэфирный камень") ||
                        material.getName().contains("Массив") ||
                        material.getName().contains("Массив_шпон")) {
                    summSquare += entry.getValue();
                }
            }

            /** get Cutshape with the most cost: */
            CutShape expensiveCutShape = null;
            for (Map.Entry<MaterialSheet, List<CutShape>> entry : sheetsAndShapes.entrySet()) {
                if (entry.getKey().getMaterial().getCalculationType() == 2) continue;
                for (CutShape cutShape : entry.getValue()) {
                    if (expensiveCutShape == null ||
                            expensiveCutShape.getMaterial().getPrice(expensiveCutShape.getElementType(), expensiveCutShape.getDepth()) <
                                    cutShape.getMaterial().getPrice(expensiveCutShape.getElementType(), expensiveCutShape.getDepth())) {
                        expensiveCutShape = cutShape;
                    }
                }
            }

            /** calculate Additional price coefficient for Acryl */
            Receipt.additionalPriceCoefficientForAcryl = 0.0;
            {
                if (/*sumSquare >= 1 && */summSquare <= 1.1) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.25;
                } else if (summSquare > 1.1 && summSquare <= 1.2) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.225;
                } else if (summSquare > 1.2 && summSquare <= 1.3) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.2;
                } else if (summSquare > 1.3 && summSquare <= 1.4) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.175;
                } else if (summSquare > 1.4 && summSquare <= 1.5) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.15;
                } else if (summSquare > 1.5 && summSquare <= 1.6) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.125;
                } else if (summSquare > 1.6 && summSquare <= 1.7) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.1;
                } else if (summSquare > 1.7 && summSquare <= 1.8) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.075;
                } else if (summSquare > 1.8 && summSquare <= 1.9) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.05;
                } else if (summSquare > 1.9 && summSquare <= 2) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.025;
                } else if (summSquare >= 2) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.0;
                }

                if (Receipt.quartzInProject) {
                    Receipt.additionalPriceCoefficientForAcryl = 0.0;
                }
            }
            Receipt.receiptLog += "\nДополнительный коэффициент за маленькое изделие = " + Receipt.additionalPriceCoefficientForAcryl;

            for (Map.Entry<MaterialSheet, List<CutShape>> entry : sheetsAndShapes.entrySet()) {
                MaterialSheet materialSheet = entry.getKey();
                Material material = materialSheet.getMaterial();

                if (entry.getKey().getMaterial().getCalculationType() == 2) {
                    continue;
                }

                for (CutShape cutShape : entry.getValue()) {
                    SketchShape sketchShape = (SketchShape) cutShape.getSketchObjectOwner();
                    double Sshape = cutShape.getShapeSquare() / 10000;
                    if (summSquare < 1.0 && cutShape == expensiveCutShape) {
                        Sshape += 1.0 - summSquare;
                    }

                    String name = sketchShape.getProductName();
                    String units = "м. кв.";
                    double count = Sshape;
                    double pseudoCount = Sshape;
                    String currency = material.getCurrency();

                    double priceForOne = materialSheet.getPrice(cutShape.getElementType(), sketchShape.getShapeDepth());

                    double deliveryPrice = 0;
                    if (Catalogs.getMaterialsDeliveryFromManufacturer().get(material.getSubType()) != null) {
                        double deliveryCommonPrice = Catalogs.getMaterialsDeliveryFromManufacturer().get(material.getSubType());
                        int shapesNumber = materialAndNumberOfShapes.get(material);
                        deliveryPrice = deliveryCommonPrice / shapesNumber;//in RUR
                        if (currency.equals("USD")) {
                            deliveryPrice = deliveryPrice / MainWindow.getUSDValue().doubleValue();//in USD
                        } else if (currency.equals("EUR")) {
                            deliveryPrice = deliveryPrice / MainWindow.getEURValue().doubleValue();//in EUR
                        }
                    }

                    String stockName = NewsController.getNewsController().getMaterialStockSize(material).split("##")[0];
                    double stockSize = Double.parseDouble(NewsController.getNewsController().getMaterialStockSize(material).split("##")[1]);
                    double stockResultSizeForShape = priceForOne * count * stockSize;

                    if (currency.equals("USD")) {
                        stockResultSizeForShape = stockResultSizeForShape * MainWindow.getUSDValue().doubleValue();//in USD
                    } else if (currency.equals("EUR")) {
                        stockResultSizeForShape = stockResultSizeForShape * MainWindow.getEURValue().doubleValue();//in EUR
                    }

                    if (stockSize != 0) {
                        Double d = Receipt.getMaterialStocks().get(stockName);
                        if (d == null) d = 0.0;
                        d += stockResultSizeForShape;
                        Receipt.getMaterialStocks().put(stockName, d);
                    }

                    Receipt.receiptLog += "\nЦена за м2 = " + priceForOne + " " + currency;
                    Receipt.receiptLog += "\nКоличество = " + count;
                    Receipt.receiptLog += "\nСтоимость доставки = " + deliveryPrice;

                    Receipt.receiptLog += "\nобщая стоимость = " + (priceForOne * count + deliveryPrice);
                    Receipt.receiptLog += "\nСкидка по акции: " + stockName + " = " + stockResultSizeForShape + " RUB";
                    Receipt.receiptLog += "\n";

                    ReceiptItem receiptItem;
                    if (entry.getKey().isActualPrice()) {
                        receiptItem = new ReceiptItem(name, units, count, pseudoCount, currency, priceForOne, deliveryPrice);
                    } else {
                        receiptItem = new ReceiptItem(name, units, count, pseudoCount, currency, priceForOne, deliveryPrice, "red");
                        Receipt.pricesActual = false;
                    }
                    Receipt.getCutShapesAndReceiptItem().put(cutShape, receiptItem);
                }
            }
        }

        Receipt.receiptLog += "\n ВСЕ СКИДКИ:";
        for (Map.Entry<String, Double> entry : Receipt.getMaterialStocks().entrySet()) {
            Receipt.receiptLog += "\n " + entry.getKey() + " = " + entry.getValue() + "RUB";
        }

        /** new calculate, more correct instead previous version:  END*/

        //add receipt items for Sink:
        Receipt.getCuttableSinkAndReceiptItem().clear();

        for (Map.Entry<Sink, Double> entry : Receipt.getCutSinkAndSquareCalcType2().entrySet()) {
            Sink sink = entry.getKey();

            SketchShape sketchShape = sink.getSketchShapeOwner();

            String name = "Раковина";
            String units = "м. кв.";

            double count = entry.getValue();
            String currency = sketchShape.getMaterial().getCurrency();
            double priceForOne = sketchShape.getMaterial().getPrice(sketchShape.getElementType(), sketchShape.getShapeDepth());

            ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
            Receipt.getCuttableSinkAndReceiptItem().put(sink, receiptItem);
        }
    }

}
