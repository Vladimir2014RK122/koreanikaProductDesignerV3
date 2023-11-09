package ru.koreanika.utils.receipt;

import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutObject;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.Cutout;
import ru.koreanika.sketchDesigner.Features.Grooves;
import ru.koreanika.sketchDesigner.Features.Rods;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.tableDesigner.Items.SinkItem;
import ru.koreanika.tableDesigner.Items.TableDesignerItem;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.News.NewsCard;
import ru.koreanika.utils.News.NewsCardStockCondition;
import ru.koreanika.utils.News.NewsCardStockItem;
import ru.koreanika.utils.News.NewsController;
import ru.koreanika.utils.ProjectHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Receipt {

    public static boolean pricesActual = true;// it is need for adding message in receipt that prices are not actual.
    private static Map<Material, ArrayList<Material.MaterialSheet>> materialAndSheets = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> materialSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> shapesSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> tableTopsSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> wallPanelsSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> windowsillsSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> tableTopSquaresCalcType1Result = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> tableTopSquaresCalcType2Result = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> wallPanelSquaresCalcType1Result = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> wallPanelSquaresCalcType2Result = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> windowsillSquaresCalcType1Result = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> windowsillSquaresCalcType2Result = new LinkedHashMap<>();
    private static Map<CutObject, Double> cutObjectsAndSquareCalcType1 = new LinkedHashMap<>();
    private static Map<CutObject, Double> cutObjectsAndSquareCalcType2 = new LinkedHashMap<>();
    private static Map<Sink, Double> cutSinkAndSquareCalcType2 = new LinkedHashMap<>();
    //receiptItems for cut shapes and materials:
    private static Map<CutShape, ReceiptItem> cutShapesAndReceiptItem = new LinkedHashMap<>();
    //receiptItems for cuttable Sink:
    private static Map<Sink, ReceiptItem> cuttableSinkAndReceiptItem = new LinkedHashMap<>();
    //receiptItems for edges and borders:
    private static Map<SketchEdge, ReceiptItem> edgesAndBordersReceiptItemMap = new LinkedHashMap<>();
    private static Map<Border, ReceiptItem> bordersTopCutReceiptItemMap = new LinkedHashMap<>();
    private static Map<Border, ReceiptItem> bordersSideCutReceiptItemMap = new LinkedHashMap<>();
    //receiptItems for Sink:
    private static Map<Sink, ReceiptItem> sinkAndReceiptItem = new LinkedHashMap<>();
    private static Map<Sink, ReceiptItem> sinkInstallTypesAndReceiptItem = new LinkedHashMap<>();
    private static Map<Sink, ReceiptItem> sinkEdgeTypesAndReceiptItem = new LinkedHashMap<>();
    //receiptItems for Cutouts:
    private static Map<Cutout, ReceiptItem> cutoutAndReceiptItem = new LinkedHashMap<>();
    //receiptItems for Grooves:
    private static Map<Grooves, ReceiptItem> groovesAndReceiptItem = new LinkedHashMap<>();
    //receiptItems for Rods:
    private static Map<Rods, ReceiptItem> rodsAndReceiptItem = new LinkedHashMap<>();
    //receiptItems for radius element:
    private static Map<Material, ReceiptItem> radiusElementReceiptItemMap = new LinkedHashMap<>();
    //receiptItems for Joints:
    private static List<ReceiptItem> jointReceiptItemsList = new ArrayList<>();
    //images for receipt:
    private static ArrayList<ReceiptImageItem> receiptImageItemsList = new ArrayList<>();
    private static Map<Material, ReceiptItem> stoneHemReceiptItemMap = new LinkedHashMap<>();
    private static Map<Material, ReceiptItem> leakGrooveReceiptItemMap = new LinkedHashMap<>();
    private static double additionalPriceCoefficientForAcryl = 0.0;
    private static boolean quartzInProject = false;
    private static LinkedHashMap<String, Double> materialStocks = new LinkedHashMap<>();//stock Name, stockResultSize in RUR
    private static LinkedHashMap<String, Double> itemStocks = new LinkedHashMap<>();//stock Name, stockResultSize in RUR

    private static String receiptLog = "";

    private static double usesSlabs = 0;
    private static double allSquare = 0.0;

    //calculate Materials
    public static void calculateMaterials() {
        materialAndSheets.clear();
        materialSquaresCalcType2.clear();
        shapesSquaresCalcType2.clear();

        tableTopsSquaresCalcType2.clear();
        wallPanelsSquaresCalcType2.clear();
        windowsillsSquaresCalcType2.clear();

        tableTopSquaresCalcType1Result.clear();
        tableTopSquaresCalcType2Result.clear();
        wallPanelSquaresCalcType1Result.clear();
        wallPanelSquaresCalcType2Result.clear();
        windowsillSquaresCalcType1Result.clear();
        windowsillSquaresCalcType2Result.clear();

        cutShapesAndReceiptItem.clear();
        receiptImageItemsList.clear();
        pricesActual = true;

        quartzInProject = false;

        allSquare = 0.0;

        /** new calculate, more correct instead previous version:  START*/
        Map<Material.MaterialSheet, ArrayList<CutShape>> sheetsAndShapes = CutDesigner.getInstance().getCutPane().getSheetsAndShapesOnItMap();
        Map<Material.MaterialSheet, Double> sheetsAndUsesSumSquare = new LinkedHashMap<>();

        Map<Material, Integer> materialAndNumberOfShapes = new LinkedHashMap<>();//need for calculate delivery

        for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {
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

        materialStocks.clear();//stock Name, stockResultSize in RUR
        //get receipts for cutShapes which calculate in cut mode

        receiptLog = "CUARZ MATERIAL:";
        for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {
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

            receiptLog += "\n************ ЛИСТ ***************";
            receiptLog += "\nЛист: " + entry.getKey().getMaterial().getName().replace("$", " ");
            receiptLog += "\nЦена м2 материала = " + Pm2Material + " " + currency;
            receiptLog += "\nЦена всего листа = " + Plist + " " + currency;
            receiptLog += "\nКоэффициенты для столешницы = " + entry.getKey().getMaterial().getTableTopCoefficientList();
            receiptLog += "\nКоэффициенты для cтеновой панели = " + entry.getKey().getMaterial().getWallPanelCoefficientList();
            receiptLog += "\nКоэффициенты для подоконника = " + entry.getKey().getMaterial().getWindowSillCoefficientList();
            receiptLog += "\nКоэффициенты для опоры = " + entry.getKey().getMaterial().getFootCoefficientList();
            receiptLog += "\nПлощадь листа = " + Slist;
            receiptLog += "\nСуммарная площадь изделий = " + Ssum;
            receiptLog += "\nКоличество изделий = " + numberOfShapes;
            receiptLog += "\nКоличество разрезов листа = " + numberOfSheetWasCutted;
            receiptLog += "\nИспользование листа в % = " + (Ssum / Slist);
            receiptLog += "\nНеиспользованный остаток = " + (Swaste);

            for (CutShape cutShape : entry.getValue()) {
                double Sshape = cutShape.getShapeSquare() / 10000;
                allSquare += Sshape;
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

                receiptLog += "\n---------------------------------";
                receiptLog += "\nНомер изделия = " + sketchShape.getShapeNumber();
                receiptLog += "\nСтоимость доставки материала = " + deliveryCommonPrice;
                receiptLog += "\nПлощадь изделия = " + Sshape;
                receiptLog += "\nСтоимость материала для изделия = " + MaterialPrice;
                receiptLog += "\nКоэффициент на обработку = " + coefficient;

                System.out.println("---------------------------------");
                System.out.println("\nShape number = " + sketchShape.getShapeNumber());
                System.out.println("deliveryCommonPrice for Material = " + deliveryCommonPrice);
                System.out.println("Sshape = " + Sshape);
                System.out.println("MaterialPrice for this piece = " + MaterialPrice);
                System.out.println("work Coefficient = " + coefficient);

                if ((Ssum / Slist) < 0.5) {
                    WorkPrice *= (1.5 - (Ssum / Slist));
                    receiptLog += "\nКоэффициент за использование менее 50% листа = " + (1.5 - (Ssum / Slist));
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
                    receiptLog += "\n Стоимость разреза материала (+ к стоимости обработки каждого изделия)= " + (sheetCuttinngPriceFromManufacture / numberOfShapes);
                }

                if (material.getName().indexOf("Dektone") != -1 ||
                        material.getMainType().indexOf("Кварцекерамический камень") != -1 ||
                        material.getMainType().indexOf("Мраморный агломерат") != -1 ||
                        material.getMainType().indexOf("Натуральный камень") != -1) {

//                    WorkPrice = MaterialPrice * coefficient;
                    //остаток * цену м2 * коэф стеновой - 0.1 и распределить между всеми изделииями
                    WorkPrice += Pm2Material * Swaste * (Sshape / Ssum) * (material.getWallPanelCoefficientList().get(0) - 0.1);

                    receiptLog += "\nПлощадь остатка для фигуры (остаток*(Sфигуры/Sостатка))) = " + (Swaste * (Sshape / Ssum));
                    receiptLog += "\nКоэффициент для стеновой панели = " + (material.getWallPanelCoefficientList().get(0));
                    receiptLog += "\nДоп стоимость работ для остатка (ценам2*остаток*(Sфигуры/Sостатка)*(КоэфСтеновойПанели - 0.1)) = " + (Pm2Material * Swaste * (Sshape / Ssum) * (material.getWallPanelCoefficientList().get(0) - 0.1));
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
                    Double d = materialStocks.get(stockName);
                    if (d == null) d = 0.0;
                    d += stockResultSizeForShape;
                    materialStocks.put(stockName, d);
                }

                receiptLog += "\nСтоимость обработки = " + WorkPrice + currency;
                receiptLog += "\nСтоимость доставки = " + deliveryPrice + currency;
                receiptLog += "\nОбщая стоимость = " + (MaterialPrice + WorkPrice + deliveryPrice + currency);
                receiptLog += "\nСкидка по акции = " + stockName + " = " + stockResultSizeForShape + " RUB";

                ReceiptItem receiptItem;
                if (entry.getKey().isActualPrice()) {
                    receiptItem = new ReceiptItem(name, units, 0, pseudoCount, currency, Pm2Material, MaterialPrice + WorkPrice + deliveryPrice);
                } else {
                    receiptItem = new ReceiptItem(name, units, 0, pseudoCount, currency, Pm2Material, MaterialPrice + WorkPrice + deliveryPrice, "red");
                    pricesActual = false;
                }

                cutShapesAndReceiptItem.put(cutShape, receiptItem);
            }
            quartzInProject = true;//need for acryl additional price coefficient
        }

        /* part for recalculate prices for stone elements for show it proportional*/
        double allShapesS = 0;
        double allShapesPrice = 0;

        //get all prices and all square:
        Map<CutShape, ReceiptItem> savedMap = new LinkedHashMap<>(cutShapesAndReceiptItem);
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
        receiptLog += "\n**************************";
        receiptLog += "\n*** ACRYLS MATERIAL ***";

        {
            cutObjectsAndSquareCalcType1.clear();
            for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
                if (cutShape.getMaterial().getCalculationType() == 2) {
                    continue;
                }
                double S = cutShape.getShapeSquare();
                S = Math.ceil(S / Math.pow(ProjectHandler.getCommonShapeScale(), 2));
                S = S / 1000000;//mm^2 to m^2
                allSquare += S;
                cutObjectsAndSquareCalcType1.put(cutShape, S);
            }

            /** get summ square for all acryl products: */
            double summSquare = 0.0;
            for (Map.Entry<CutObject, Double> entry : cutObjectsAndSquareCalcType1.entrySet()) {
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
            for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {
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
            additionalPriceCoefficientForAcryl = 0.0;
            {
                if (/*sumSquare >= 1 && */summSquare <= 1.1) {
                    additionalPriceCoefficientForAcryl = 0.25;
                } else if (summSquare > 1.1 && summSquare <= 1.2) {
                    additionalPriceCoefficientForAcryl = 0.225;
                } else if (summSquare > 1.2 && summSquare <= 1.3) {
                    additionalPriceCoefficientForAcryl = 0.2;
                } else if (summSquare > 1.3 && summSquare <= 1.4) {
                    additionalPriceCoefficientForAcryl = 0.175;
                } else if (summSquare > 1.4 && summSquare <= 1.5) {
                    additionalPriceCoefficientForAcryl = 0.15;
                } else if (summSquare > 1.5 && summSquare <= 1.6) {
                    additionalPriceCoefficientForAcryl = 0.125;
                } else if (summSquare > 1.6 && summSquare <= 1.7) {
                    additionalPriceCoefficientForAcryl = 0.1;
                } else if (summSquare > 1.7 && summSquare <= 1.8) {
                    additionalPriceCoefficientForAcryl = 0.075;
                } else if (summSquare > 1.8 && summSquare <= 1.9) {
                    additionalPriceCoefficientForAcryl = 0.05;
                } else if (summSquare > 1.9 && summSquare <= 2) {
                    additionalPriceCoefficientForAcryl = 0.025;
                } else if (summSquare >= 2) {
                    additionalPriceCoefficientForAcryl = 0.0;
                }

                if (quartzInProject) {
                    additionalPriceCoefficientForAcryl = 0.0;
                }
            }
            receiptLog += "\nДополнительный коэффициент за маленькое изделие = " + additionalPriceCoefficientForAcryl;

            for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {
                Material.MaterialSheet materialSheet = entry.getKey();
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
                    if (ProjectHandler.getMaterialsDeliveryFromManufacture().get(material.getSubType()) != null) {
                        double deliveryCommonPrice = ProjectHandler.getMaterialsDeliveryFromManufacture().get(material.getSubType());
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
                        Double d = materialStocks.get(stockName);
                        if (d == null) d = 0.0;
                        d += stockResultSizeForShape;
                        materialStocks.put(stockName, d);
                    }

                    receiptLog += "\nЦена за м2 = " + priceForOne + " " + currency;
                    receiptLog += "\nКоличество = " + count;
                    receiptLog += "\nСтоимость доставки = " + deliveryPrice;

                    receiptLog += "\nобщая стоимость = " + (priceForOne * count + deliveryPrice);
                    receiptLog += "\nСкидка по акции: " + stockName + " = " + stockResultSizeForShape + " RUB";
                    receiptLog += "\n";

                    ReceiptItem receiptItem;
                    if (entry.getKey().isActualPrice()) {
                        receiptItem = new ReceiptItem(name, units, count, pseudoCount, currency, priceForOne, deliveryPrice);
                    } else {
                        receiptItem = new ReceiptItem(name, units, count, pseudoCount, currency, priceForOne, deliveryPrice, "red");
                        pricesActual = false;
                    }
                    cutShapesAndReceiptItem.put(cutShape, receiptItem);
                }
            }
        }

        receiptLog += "\n ВСЕ СКИДКИ:";
        for (Map.Entry<String, Double> entry : materialStocks.entrySet()) {
            receiptLog += "\n " + entry.getKey() + " = " + entry.getValue() + "RUB";
        }

        /** new calculate, more correct instead previous version:  END*/

        //add receipt items for Sink:
        cuttableSinkAndReceiptItem.clear();

        for (Map.Entry<Sink, Double> entry : cutSinkAndSquareCalcType2.entrySet()) {
            Sink sink = entry.getKey();

            SketchShape sketchShape = sink.getSketchShapeOwner();

            String name = "Раковина";
            String units = "м. кв.";

            double count = entry.getValue();
            String currency = sketchShape.getMaterial().getCurrency();
            double priceForOne = sketchShape.getMaterial().getPrice(sketchShape.getElementType(), sketchShape.getShapeDepth());

            ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
            cuttableSinkAndReceiptItem.put(sink, receiptItem);
        }
    }

    public static void calculateItemsStocks() {
        ArrayList<NewsCard> list = NewsController.getNewsController().getStockItemCards();

        itemStocks.clear();
        //sink
        {
            ArrayList<NewsCard> stockSinks = new ArrayList<>();
            for (NewsCard nc : list) {
                if (nc.getStockItem() == NewsCardStockItem.SINK) {
                    stockSinks.add(nc);
                }
            }

            for (TableDesignerItem tableDesignerItem : SinkItem.getTableDesignerItemsList()) {
                if (tableDesignerItem instanceof SinkItem) {
                    SinkItem sinkItem = (SinkItem) tableDesignerItem;
                    String model = sinkItem.getModel().split(" ")[0];
                    Material sinkMaterial = sinkItem.getMaterial();

                    for (NewsCard nc : stockSinks) {
                        if (nc.getStockCondition() == NewsCardStockCondition.MATERIALCOUNT) {
                            double calcCount = 0;
                            for (Map.Entry<CutShape, ReceiptItem> entry : cutShapesAndReceiptItem.entrySet()) {
                                if (nc.getStockConditionMaterialTypes().contains(entry.getKey().getMaterial().getMainType())) {
                                    calcCount += entry.getKey().getShapeSquare() / 10000.0;
                                }
                            }

                            if (nc.getStockItemModel().contains(model) && nc.getStockConditionMaterialTypes().contains(sinkMaterial.getMainType()) && calcCount >= nc.getStockConditionCount()) {
                                double stockPrice = 0;
                                if (itemStocks.get(nc.getHeader()) == null) {
                                    stockPrice = sinkItem.getOnlySinkPrice() * nc.getStockSize();
                                } else {
                                    stockPrice = itemStocks.get(nc.getHeader()) + sinkItem.getOnlySinkPrice() * nc.getStockSize();
                                }
                                itemStocks.put(nc.getHeader(), stockPrice);
                            }
                        }
                    }
                }
            }
        }
    }

    public static double getUsesSlabs() {
        usesSlabs = 0;
        for (Material.MaterialSheet ms : CutDesigner.getInstance().getCutPane().getUsedMaterialSheetsList()) {
            usesSlabs += ms.getUsesSlabs();
        }
        return usesSlabs;
    }

    public static double getAllSquare() {
        return allSquare;
    }

    public static String getReceiptLog() {
        return receiptLog;
    }

    public static Map<Border, ReceiptItem> getBordersSideCutReceiptItemMap() {
        return bordersSideCutReceiptItemMap;
    }

    public static Map<Border, ReceiptItem> getBordersTopCutReceiptItemMap() {
        return bordersTopCutReceiptItemMap;
    }

    public static Map<SketchEdge, ReceiptItem> getEdgesAndBordersReceiptItemMap() {
        return edgesAndBordersReceiptItemMap;
    }

    public static LinkedHashMap<String, Double> getMaterialStocks() {
        return materialStocks;
    }

    public static LinkedHashMap<String, Double> getItemStocks() {
        return itemStocks;
    }

    public static Map<CutShape, ReceiptItem> getCutShapesAndReceiptItem() {
        return cutShapesAndReceiptItem;
    }

    public static Map<Sink, ReceiptItem> getCuttableSinkAndReceiptItem() {
        return cuttableSinkAndReceiptItem;
    }

    public static Map<Sink, ReceiptItem> getSinkAndReceiptItem() {
        return sinkAndReceiptItem;
    }

    public static Map<Sink, ReceiptItem> getSinkInstallTypesAndReceiptItem() {
        return sinkInstallTypesAndReceiptItem;
    }

    public static Map<Sink, ReceiptItem> getSinkEdgeTypesAndReceiptItem() {
        return sinkEdgeTypesAndReceiptItem;
    }

    public static Map<Cutout, ReceiptItem> getCutoutAndReceiptItem() {
        return cutoutAndReceiptItem;
    }

    public static Map<Rods, ReceiptItem> getRodsAndReceiptItem() {
        return rodsAndReceiptItem;
    }

    public static Map<Grooves, ReceiptItem> getGroovesAndReceiptItem() {
        return groovesAndReceiptItem;
    }

    public static Map<Material, ReceiptItem> getRadiusElementReceiptItemMap() {
        return radiusElementReceiptItemMap;
    }

    public static List<ReceiptItem> getJointReceiptItemsList() {
        return jointReceiptItemsList;
    }

    public static Map<Material, ReceiptItem> getLeakGrooveReceiptItemMap() {
        return leakGrooveReceiptItemMap;
    }

    public static Map<Material, ReceiptItem> getStoneHemReceiptItemMap() {
        return stoneHemReceiptItemMap;
    }

    public static ArrayList<ReceiptImageItem> getReceiptImageItemsList() {
        return receiptImageItemsList;
    }

    public static double getAdditionalPriceCoefficientForAcryl() {
        return additionalPriceCoefficientForAcryl;
    }

}
