package ru.koreanika.utils.Receipt;

import ru.koreanika.Common.Material.Material;

import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutObject;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.cutDesigner.Shapes.CutShapeAdditionalFeature;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.Edge;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.*;
import ru.koreanika.sketchDesigner.Joint;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.sketchDesigner.Shapes.ShapeType;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.Items.SinkItem;
import ru.koreanika.tableDesigner.Items.TableDesignerItem;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.News.NewsCard;
import ru.koreanika.utils.News.NewsCardStockCondition;
import ru.koreanika.utils.News.NewsCardStockItem;
import ru.koreanika.utils.News.NewsController;
import ru.koreanika.project.ProjectHandler;


import java.util.*;

public class Receipt {

    private static Map<Material, ArrayList<Material.MaterialSheet>> materialAndSheets = new LinkedHashMap<>();

    private static Map<Material, Map<Integer, Double>> materialSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> shapesSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> tableTopsSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> wallPanelsSquaresCalcType2 = new LinkedHashMap<>();
    private static Map<Material, Map<Integer, Double>> windowsillsSquaresCalcType2 = new LinkedHashMap<>();


    private static Map<Material, Map<Integer, Double>> tableTopSquaresCalcType1Result = new LinkedHashMap<>();
    ; //< Material, <depth,square> >
    private static Map<Material, Map<Integer, Double>> tableTopSquaresCalcType2Result = new LinkedHashMap<>();

    private static Map<Material, Map<Integer, Double>> wallPanelSquaresCalcType1Result = new LinkedHashMap<>();
    ;
    private static Map<Material, Map<Integer, Double>> wallPanelSquaresCalcType2Result = new LinkedHashMap<>();

    private static Map<Material, Map<Integer, Double>> windowsillSquaresCalcType1Result = new LinkedHashMap<>();
    ;
    private static Map<Material, Map<Integer, Double>> windowsillSquaresCalcType2Result = new LinkedHashMap<>();

    private static Map<CutObject, Double> cutObjectsAndSquareCalcType1 = new LinkedHashMap<>();
    private static Map<CutObject, Double> cutObjectsAndPriceCalcType1 = new LinkedHashMap<>();
    private static Map<CutObject, Double> cutObjectsAndSquareCalcType2 = new LinkedHashMap<>();
    private static Map<CutObject, Double> cutObjectsAndPriceCalcType2 = new LinkedHashMap<>();
    private static Map<Sink, Double> cutSinkAndSquareCalcType2 = new LinkedHashMap<>();
    private static Map<Sink, Double> cutSinkAndPriceCalcType2 = new LinkedHashMap<>();

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

    //receiptItems for additional features:
    private static Map<AdditionalFeature, ReceiptItem> additionalFeaturesMap = new LinkedHashMap<>();


    //edges and borders:
    private static Map<String, Double> edgesAndBordersPriceMap = new LinkedHashMap<>();
    private static Map<String, Double> edgesAndBordersLenMap = new LinkedHashMap<>();
    private static Map<SketchEdge, Double> edgesAndBordersObjectsLenMap = new LinkedHashMap<>();
    private static Map<String, ImageView> edgesAndBordersImagesMap = new LinkedHashMap<>();

    private static Map<Material, ReceiptItem> stoneHemReceiptItemMap = new LinkedHashMap<>();
    private static Map<Material, ReceiptItem> leakGrooveReceiptItemMap = new LinkedHashMap<>();


    //private static double additionalPricePercent = 0.0;
    //private static ArrayList<ReceiptItem> listOfAdditionalPricesForAcryl = new ArrayList<>();
    private static double additionalPriceCoefficientForAcryl = 0.0;
    private static boolean quartzInProject = false;
    public static boolean pricesActual = true;// it is need for adding message in receipt that prices are not actual.

    private static Map<String, ImageView> mainImagesMap = new LinkedHashMap<>();

    private static ArrayList<ReceiptItem> mainEdgesAndBordersReceiptItemsList = new ArrayList<>();

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
        System.out.println("\n\n*** START calculate materials ***\n");

        Map<Material.MaterialSheet, ArrayList<CutShape>> sheetsAndShapes = CutDesigner.getInstance().getCutPane().getSheetsAndShapesOnItMap();
        Map<Material.MaterialSheet, Double> sheetsAndUsesSumSquare = new LinkedHashMap<>();

        Map<Material, Integer> materialAndNumberOfShapes = new LinkedHashMap<>();//need for calculate delivery

        System.out.println("Cut shapes on sheets:");
        for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {

            System.out.println("sheet: " + entry.getKey().getMaterial().getName().replace("$", " "));

            for (CutShape cutShape : entry.getValue()) {
                System.out.print(cutShape.getShapeNumber() + " ");
            }

            System.out.println("");
        }

        System.out.println("Cut shapes into its sheets squares:");
        for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {

            System.out.println("sheet: " + entry.getKey().getMaterial().getName().replace("$", " "));
            double CutShapesSumSquare = 0;
            for (CutShape cutShape : entry.getValue()) {
                CutShapesSumSquare += cutShape.getShapeSquare();
            }
            CutShapesSumSquare /= 10000;
            sheetsAndUsesSumSquare.put(entry.getKey(), Double.valueOf(CutShapesSumSquare));

            System.out.println("Shapes square = " + CutShapesSumSquare);
        }

        //fill materialAndNumberOfShapes
        for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
            Material material = cutShape.getMaterial();

            if (materialAndNumberOfShapes.containsKey(material)) {
                materialAndNumberOfShapes.put(material, Integer.valueOf(materialAndNumberOfShapes.get(material).intValue() + 1));
            } else {
                materialAndNumberOfShapes.put(material, Integer.valueOf(1));
            }
        }

        System.out.println(materialAndNumberOfShapes);



        materialStocks.clear();//stock Name, stockResultSize in RUR
        //get receipts for cutShapes which calculate in cut mode

        receiptLog = "";
        System.out.println(" \n*** Кварц/DECTONE/Натуральный камень ***");
        receiptLog += "CUARZ MATERIAL:";
        for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {

            //double Pm2Material = entry.getKey().getMaterial().getPrice(entry.getValue().get(0).getElementType(), entry.getKey().getDepth());//old
            double Pm2Material = entry.getKey().getPrice(entry.getValue().get(0).getElementType(), entry.getKey().getDepth());
//            double Slist = entry.getKey().getUsesList() * (entry.getKey().getMinMaterialHeight() * entry.getKey().getMinMaterialWidth()) / 1000000;//in meters
            double Slist = entry.getKey().getUsesList() * (entry.getKey().getMinSheetSquare()) / 1000000;//in meters
            double Ssum = sheetsAndUsesSumSquare.get(entry.getKey()).doubleValue();
            double Swaste = Slist - Ssum;
            double Plist = Pm2Material * Slist;
            double numberOfSheetWasCutted = (((entry.getKey().getSheetMinHeight() * entry.getKey().getSheetMinWidth()*2) / 1000000)/ Slist) - 1;
            int numberOfShapes = entry.getValue().size();

            String currency = entry.getKey().getSheetCurrency();

            if (entry.getKey().getMaterial().getCalculationType() == 1) continue;

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
            receiptLog += "\nИспользование листа в % = " + (Ssum/Slist);
            receiptLog += "\nНеиспользованный остаток = " + (Swaste);

            System.out.println("\n***************************");
            System.out.println("sheet: " + entry.getKey().getMaterial().getName().replace("$", " "));
            System.out.println("Pm2Material = " + Pm2Material);
            System.out.println("Plist = " + Plist);
            System.out.println("Slist = " + Slist);
            System.out.println("Ssum = " + Ssum);
            System.out.println("numberOfShapes = " + numberOfShapes);
            System.out.println("numberOfSheetWasCutted = " + numberOfSheetWasCutted);
            System.out.println("Sheet uses in percent = " + (Ssum/Slist));

            for (CutShape cutShape : entry.getValue()) {

                double Sshape = cutShape.getShapeSquare() / 10000;
                allSquare += Sshape;
                double MaterialPrice = Plist * (Sshape / Ssum);

                SketchShape sketchShape = (SketchShape) cutShape.getSketchObjectOwner();

                String name = "none";
                String units = "м. кв.";
                double count = 1;
                double pseudoCount = Sshape;


                double coefficient = ((SketchShape) cutShape.getSketchObjectOwner()).getWorkCoefficient();
                double priceForOne = 0;

                name = sketchShape.getProductName();
                double WorkPrice = Sshape * Pm2Material * coefficient;
                Material material = cutShape.getMaterial();
                //System.out.println(ProjectHandler.getMaterialsDeliveryFromManufacture());
                //System.out.println(material.getSubType());

                double deliveryCommonPrice = 0;
//                if (ProjectHandler.getMaterialsDeliveryFromManufacture().get(material.getSubType()) != null) {
//                    deliveryCommonPrice = ProjectHandler.getMaterialsDeliveryFromManufacture().get(material.getSubType()).doubleValue();
//                }
                deliveryCommonPrice = material.getDeliveryFromManufacture();
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

                if((Ssum/Slist) < 0.5){
                    WorkPrice *= (1.5 - (Ssum/Slist));
                    receiptLog += "\nКоэффициент за использование менее 50% листа = " + (1.5 - (Ssum/Slist));
                    System.out.println("Coefficient for uses sheet < 50% = " + (1.5 - (Ssum/Slist)));
                }
                if(numberOfSheetWasCutted != 0){
                    double sheetCuttinngPriceFromManufacture = material.getSheetCuttingPrice() * numberOfSheetWasCutted;
                    if (currency.equals("USD")) {
                        sheetCuttinngPriceFromManufacture = material.getSheetCuttingPrice() / MainWindow.getUSDValue().doubleValue();//in USD
                    }else if (currency.equals("EUR")) {
                        sheetCuttinngPriceFromManufacture = material.getSheetCuttingPrice() / MainWindow.getEURValue().doubleValue();//in EUR
                    }

                    if(entry.getKey().isAdditionalSheet()){
                        sheetCuttinngPriceFromManufacture = 0;
                    }

                    WorkPrice += (sheetCuttinngPriceFromManufacture/numberOfShapes);
                    System.out.println("SheetCuttingManufactire add price = " + (sheetCuttinngPriceFromManufacture/numberOfShapes));
                    receiptLog += "\n Стоимость разреза материала (+ к стоимости обработки каждого изделия)= " + (sheetCuttinngPriceFromManufacture/numberOfShapes);
                }


                //MaterialPrice *= MaterialPrice*ProjectHandler.getPriceMaterialCoefficient().doubleValue();

                if(material.getName().indexOf("Dektone") != -1 ||
                        material.getMainType().indexOf("Кварцекерамический камень") != -1 ||
                        material.getMainType().indexOf("Мраморный агломерат") != -1 ||
                        material.getMainType().indexOf("Натуральный камень") != -1){

//                    WorkPrice = MaterialPrice * coefficient;
                    //остаток * цену м2 * коэф стеновой - 0.1 и распределить между всеми изделииями
                    WorkPrice += Pm2Material*Swaste*(Sshape / Ssum)*(material.getWallPanelCoefficientList().get(0) - 0.1);

                    receiptLog += "\nПлощадь остатка для фигуры (остаток*(Sфигуры/Sостатка))) = " + (Swaste*(Sshape / Ssum));
                    receiptLog += "\nКоэффициент для стеновой панели = " + (material.getWallPanelCoefficientList().get(0));
                    receiptLog += "\nДоп стоимость работ для остатка (ценам2*остаток*(Sфигуры/Sостатка)*(КоэфСтеновойПанели - 0.1)) = " + (Pm2Material*Swaste*(Sshape / Ssum)*(material.getWallPanelCoefficientList().get(0) - 0.1));
                }

                String stockName = NewsController.getNewsController().getMaterialStockSize(material).split("##")[0];
                double stockSize = Double.parseDouble(NewsController.getNewsController().getMaterialStockSize(material).split("##")[1]);
                double stockResultSizeForShape = MaterialPrice * stockSize;

                if (currency.equals("USD")) {
                    stockResultSizeForShape = stockResultSizeForShape * MainWindow.getUSDValue().doubleValue();//in USD
                } else if (currency.equals("EUR")) {
                    stockResultSizeForShape = stockResultSizeForShape * MainWindow.getEURValue().doubleValue();//in EUR
                }

                if(stockSize != 0){
                    Double d = materialStocks.get(stockName);
                    if(d == null) d = 0.0;
                    d += stockResultSizeForShape;
                    materialStocks.put(stockName, d);
                }


                receiptLog += "\nСтоимость обработки = " + WorkPrice + currency;
                receiptLog += "\nСтоимость доставки = " + deliveryPrice + currency;
                receiptLog += "\nОбщая стоимость = " + (MaterialPrice + WorkPrice + deliveryPrice + currency);
                receiptLog += "\nСкидка по акции = "  + stockName + " = " + stockResultSizeForShape + " RUB";

                System.out.println("WorkPrice = " + WorkPrice);
                System.out.println("deliveryPrice = " + deliveryPrice);

                System.out.println("All price for shape = " + (MaterialPrice + WorkPrice + deliveryPrice) + currency);

                ReceiptItem receiptItem = null;


                if(entry.getKey().isActualPrice()){
                    receiptItem = new ReceiptItem(name, units, 0, pseudoCount, currency, Pm2Material, MaterialPrice + WorkPrice + deliveryPrice);
                }else{
                    receiptItem = new ReceiptItem(name, units, 0, pseudoCount, currency, Pm2Material, MaterialPrice + WorkPrice + deliveryPrice, "red");
                    pricesActual = false;
                }


                cutShapesAndReceiptItem.put(cutShape, receiptItem);



            }




            quartzInProject = true;//need for acryl additional price coefficient
        }

        /* part for recalculate prices for stone elements for show it proportional*/
        if(true){
            double allShapesS = 0;
            double allShapesPrice = 0;

            //get all prices and all square:
            Map<CutShape, ReceiptItem> savedMap = new LinkedHashMap<>(cutShapesAndReceiptItem);
            for(Map.Entry<CutShape, ReceiptItem> entry : savedMap.entrySet()){
                ReceiptItem receiptItem = entry.getValue();

                double shapeS = receiptItem.getPseudoCountDouble();
                //double shapePrice = receiptItem.getAdditionalPrice();
                double shapePrice = receiptItem.getAllPriceInRURDouble();

                allShapesS += shapeS;
                allShapesPrice += shapePrice;

//                System.out.println("shapeS = " + shapeS);
//                System.out.println("allShapesS = " + allShapesS);
//                System.out.println("shapePrice = " + shapePrice);
//                System.out.println("allShapesPrice = " + allShapesPrice);
            }

            for(Map.Entry<CutShape, ReceiptItem> entry : savedMap.entrySet()){
                ReceiptItem receiptItem = entry.getValue();

                double shapeS = receiptItem.getPseudoCountDouble();
                double newShapePrice = (shapeS/allShapesS)*allShapesPrice;

//                System.out.println("shapeS = " + shapeS);
//                System.out.println("allShapesS = " + allShapesS);
//                System.out.println("allShapesPrice = " + allShapesPrice);
//                System.out.println("newShapePrice = " + newShapePrice);

                receiptItem.setAdditionalPrice(newShapePrice);
                receiptItem.setCurrency("RUB");
//                System.out.println("getAllPriceInRUR = " + receiptItem.getAllPriceInRUR());
            }

        }


//        for(Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()){
//            if(entry.getValue().getAllPriceInRURDouble() > 800000){
//                System.out.println("high");
//            }
//            System.out.println("***** PRICE IN RUR = " + entry.getValue().getAllPriceInRUR() + "currency + " + entry.getValue().getCurrency());
//            System.out.println("***** PRICE  = " + entry.getValue().getAllPriceInRURDouble() + "currency + " + entry.getValue().getCurrency());
//        }



        //get receipts for cutShapes which calculate in square mode:
        // create additional coefficients:
        Map<Material, Double> mapMaterialsAndSquares = new LinkedHashMap<>();
        Map<Material, Double> mapMaterialsAndAdditionalCoefficients = new LinkedHashMap<>();

        receiptLog += "\n**************************";
        receiptLog += "\n*** ACRYLS MATERIAL ***";
        System.out.println(" \nACRYLS MATERIAL:");
        {
            cutObjectsAndSquareCalcType1.clear();
            for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
                //System.out.println("cutShape.getMaterial().getCalculationType() = " + cutShape.getMaterial().getCalculationType());
                if (cutShape.getMaterial().getCalculationType() == 2) continue;
                double S = cutShape.getShapeSquare();
                //System.out.println(cutShape + "S=" + S);
                S = Math.ceil(S / Math.pow(ProjectHandler.getCommonShapeScale(), 2));
                S = S / 1000000;//mm^2 to m^2
                allSquare += S;
                cutObjectsAndSquareCalcType1.put(cutShape, Double.valueOf(S));

//                //get SumSquare for all Acryl materials:
//                {
//                    Double Ssum = mapMaterialsAndSquares.get(cutShape.getMaterial());
//                    if(Ssum == null){
//                        Ssum = S;
//                    }else{
//                        Ssum = Ssum.doubleValue() + S;
//                    }
//                    mapMaterialsAndSquares.put(cutShape.getMaterial(), Ssum);
//                }

            }


            /** get summ square for all acryl products: */
            double summSquare = 0.0;
            for (Map.Entry<CutObject, Double> entry : cutObjectsAndSquareCalcType1.entrySet()) {

                Material material = entry.getKey().getMaterial();

                if (material.getName().indexOf("Акриловый камень") != -1 ||
                        material.getName().indexOf("Полиэфирный камень") != -1 ||
                        material.getName().contains("Массив") ||
                        material.getName().contains("Массив_шпон")) {
                    summSquare += entry.getValue().doubleValue();

                }
            }

            /** get Cutshape with the most cost: */
            CutShape expensiveCutShape = null;
            for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {
                if (entry.getKey().getMaterial().getCalculationType() == 2) continue;
                for (CutShape cutShape : entry.getValue()) {
                    if(expensiveCutShape == null ||
                            expensiveCutShape.getMaterial().getPrice(expensiveCutShape.getElementType(), expensiveCutShape.getDepth()) <
                                    cutShape.getMaterial().getPrice(expensiveCutShape.getElementType(), expensiveCutShape.getDepth())){
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


                if(quartzInProject){
                    additionalPriceCoefficientForAcryl = 0.0;
                }
            }
            receiptLog += "\nДополнительный коэффициент за маленькое изделие = " + additionalPriceCoefficientForAcryl;
            System.out.println("additionalPriceCoefficientForAcryl = " + additionalPriceCoefficientForAcryl);



            System.out.println("\n Parts: ");

            for (Map.Entry<Material.MaterialSheet, ArrayList<CutShape>> entry : sheetsAndShapes.entrySet()) {

                Material.MaterialSheet materialSheet = entry.getKey();
                Material material = materialSheet.getMaterial();

                double Pm2Material = materialSheet.getPrice(entry.getValue().get(0).getElementType(), entry.getKey().getDepth());
                double Slist = entry.getKey().getUsesList() * (entry.getKey().getSheetMinHeight() * entry.getKey().getSheetMinWidth()) / 1000000;//in meters
                double Ssum = sheetsAndUsesSumSquare.get(entry.getKey()).doubleValue();
                double Plist = Pm2Material * Slist;



//            System.out.println("sheet: " + entry.getKey().getMaterial().getName().replace("$", " "));
//
//            System.out.println("Pm2Material = " + Pm2Material);
//            System.out.println("Plist = " + Plist);
//            System.out.println("Slist = " + Slist);
//            System.out.println("Ssum = " + Ssum);

                if (entry.getKey().getMaterial().getCalculationType() == 2) continue;

                for (CutShape cutShape : entry.getValue()) {

                    SketchShape sketchShape = (SketchShape) cutShape.getSketchObjectOwner();

                    double Sshape = cutShape.getShapeSquare() / 10000;
                    //double MaterialPrice = Plist * (Sshape / Ssum);


                    if(summSquare < 1.0 && cutShape == expensiveCutShape) Sshape += 1.0 -  summSquare;

                    String name = sketchShape.getProductName();
                    String units = "м. кв.";
                    double count = Sshape;
                    double pseudoCount = Sshape;
                    String currency = material.getCurrency();




                    double priceForOne = materialSheet.getPrice(cutShape.getElementType(), sketchShape.getShapeDepth());


                    double deliveryPrice = 0;
                    if (ProjectHandler.getMaterialsDeliveryFromManufacturer().get(material.getSubType()) != null) {

                        double deliveryCommonPrice = ProjectHandler.getMaterialsDeliveryFromManufacturer().get(material.getSubType()).doubleValue();
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

                    if(stockSize != 0){
                        Double d = materialStocks.get(stockName);
                        if(d == null) d = 0.0;
                        d += stockResultSizeForShape;
                        materialStocks.put(stockName, d);
                    }


                    receiptLog += "\nЦена за м2 = " + priceForOne + " " + currency;
                    receiptLog += "\nКоличество = " + count;
                    receiptLog += "\nСтоимость доставки = " + deliveryPrice;

                    receiptLog += "\nобщая стоимость = " + (priceForOne * count + deliveryPrice);
                    receiptLog += "\nСкидка по акции: " + stockName + " = " + stockResultSizeForShape + " RUB";
                    receiptLog += "\n";

                    System.out.println("priceForOne = " + priceForOne);
                    System.out.println("count = " + count);
                    System.out.println("deliveryPrice = " + deliveryPrice);
                    System.out.println("\n");

                    //priceForOne += priceForOne*(additionalPricePercent/100.0);

                    //System.out.println("priceForOne with coefficient = " + priceForOne);

                    ReceiptItem receiptItem = null;
                    if(entry.getKey().isActualPrice()){
                        receiptItem = new ReceiptItem(name, units, count, pseudoCount, currency, priceForOne, deliveryPrice);
                    }else{
                        receiptItem = new ReceiptItem(name, units, count, pseudoCount, currency, priceForOne, deliveryPrice, "red");
                        pricesActual = false;
                    }
                    cutShapesAndReceiptItem.put(cutShape, receiptItem);
                }

            }


        }

        receiptLog += "\n ВСЕ СКИДКИ:";
        for(Map.Entry<String, Double> entry : materialStocks.entrySet()){
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

            double coefficient = sketchShape.getMaterial().getTableTopCoefficientList().get(0);
            double priceForOne = sketchShape.getMaterial().getPrice(sketchShape.getElementType(), sketchShape.getShapeDepth());

            //double workPart = priceForOne * coefficient * count;

            //priceForOne *= ProjectHandler.getPriceMaterialCoefficient().doubleValue();

            ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
            cuttableSinkAndReceiptItem.put(sink, receiptItem);
        }


    }

    public static void calculateItemsStocks(){
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

            for(TableDesignerItem tableDesignerItem : SinkItem.getTableDesignerItemsList()){

                if(tableDesignerItem instanceof SinkItem){
                    SinkItem sinkItem = (SinkItem) tableDesignerItem;
                    String model = sinkItem.getModel().split(" ")[0];
                    Material sinkMaterial = sinkItem.getMaterial();



                    for(NewsCard nc : stockSinks){

                        System.out.println(model + " | "+ nc.getStockItemModel() + ", " + sinkMaterial.getMainType() + " | " + nc.getStockConditionMaterialTypes());

                        if(nc.getStockCondition() == NewsCardStockCondition.MATERIALCOUNT){
                            double materialCount = nc.getStockConditionCount();
                            double calcCount = 0;
                            for(Map.Entry<CutShape, ReceiptItem> entry : cutShapesAndReceiptItem.entrySet()){
                                if(nc.getStockConditionMaterialTypes().contains(entry.getKey().getMaterial().getMainType())){
                                    calcCount += entry.getKey().getShapeSquare()/10000.0;
                                }
                            }

                            System.out.println("calcCount = " + calcCount);
                            System.out.println("StockConditionCount = " + materialCount);

                            if(nc.getStockItemModel().contains(model) && nc.getStockConditionMaterialTypes().contains(sinkMaterial.getMainType()) && calcCount >= nc.getStockConditionCount()){

                                double stockPrice = 0;
                                if(itemStocks.get(nc.getHeader()) == null){
                                    stockPrice = sinkItem.getOnlySinkPrice() * nc.getStockSize();
                                }else{
                                    stockPrice = itemStocks.get(nc.getHeader()) + sinkItem.getOnlySinkPrice() * nc.getStockSize();
                                }
                                System.out.println("sinkItem.getOnlySinkPrice() = " + sinkItem.getOnlySinkPrice());
                                System.out.println("nc.getStockSize() = " + nc.getStockSize());
                                itemStocks.put(nc.getHeader(), stockPrice);

                            }


                        }


                    }
                    //sink.get

                }
            }
        }


    }

    public static double getUsesSlabs() {
        usesSlabs = 0;
        for(Material.MaterialSheet ms : CutDesigner.getInstance().getCutPane().getUsedMaterialSheetsList()){
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

    public static Map<CutObject, Double> getCutObjectsAndSquareCalcType1() {
        return cutObjectsAndSquareCalcType1;
    }

    public static Map<CutObject, Double> getCutObjectsAndSquareCalcType2() {
        return cutObjectsAndSquareCalcType2;
    }

    public static Map<Sink, Double> getCutSinkAndSquareCalcType2() {
        return cutSinkAndSquareCalcType2;
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

    public static Map<String, Double> getEdgesAndBordersLenMap() {
        return edgesAndBordersLenMap;
    }

    public static Map<String, ImageView> getEdgesAndBordersImagesMap() {
        return edgesAndBordersImagesMap;
    }

    public static Map<SketchEdge, Double> getEdgesAndBordersObjectsLenMap() {
        return edgesAndBordersObjectsLenMap;
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

    public static Map<Material, ArrayList<Material.MaterialSheet>> getMaterialAndSheets() {
        return materialAndSheets;
    }

    public static ArrayList<ReceiptItem> getMainEdgesAndBordersReceiptItemsList() {
        return mainEdgesAndBordersReceiptItemsList;
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

    public static Map<String, ImageView> getMainImagesMap() {
        return mainImagesMap;
    }

    public static double getAdditionalPriceCoefficientForAcryl() {
        return additionalPriceCoefficientForAcryl;
    }

    public static boolean isQuartzInProject() {
        return quartzInProject;
    }

    public static void calcMaterialsSquareMode() {

//        tableTopSquaresCalcType1Result = getElementAndSquares(1, ElementTypes.TABLETOP);
//        wallPanelSquaresCalcType1Result = getElementAndSquares(1, ElementTypes.WALL_PANEL);
//        windowsillSquaresCalcType1Result = getElementAndSquares(1, ElementTypes.WINDOWSILL);


        cutObjectsAndSquareCalcType1.clear();
        for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
            //System.out.println("cutShape.getMaterial().getCalculationType() = " + cutShape.getMaterial().getCalculationType());
            if (cutShape.getMaterial().getCalculationType() == 2) continue;
            double S = cutShape.getShapeSquare();
            System.out.println(cutShape + "S=" + S);
            S = Math.ceil(S / Math.pow(ProjectHandler.getCommonShapeScale(), 2));
            S = S / 1000000;//mm^2 to m^2
            cutObjectsAndSquareCalcType1.put(cutShape, Double.valueOf(S));
        }
        for (CutShapeAdditionalFeature cutShapeAdditionalFeature : CutDesigner.getInstance().getCutShapeAdditionalFeaturesList()) {
            if (cutShapeAdditionalFeature.getMaterial().getCalculationType() == 2) continue;
            double S = cutShapeAdditionalFeature.getShapeSquare();
            S = Math.ceil(S / Math.pow(ProjectHandler.getCommonShapeScale(), 2));
            S = S / 1000000;//mm^2 to m^2
            cutObjectsAndSquareCalcType1.put(cutShapeAdditionalFeature, Double.valueOf(S));
        }

        /** check >= 1m2 */

        Map<Material, Double> materialAndSquares = new LinkedHashMap<>();
        for (Map.Entry<CutObject, Double> entry : cutObjectsAndSquareCalcType1.entrySet()) {
            Material material = entry.getKey().getMaterial();
            if (materialAndSquares.get(material) == null) {
                Double s = entry.getValue().doubleValue();
                materialAndSquares.put(material, s);
            } else {
                Double s = materialAndSquares.get(material) + entry.getValue();
                materialAndSquares.put(material, s);
            }
        }

        for (Map.Entry<Material, Double> entry : materialAndSquares.entrySet()) {
            double s = entry.getValue().doubleValue();
            if (entry.getValue().doubleValue() < 1) {
                double delta = 1.0 - entry.getValue().doubleValue();


                for (Map.Entry<CutObject, Double> entry1 : cutObjectsAndSquareCalcType1.entrySet()) {
                    Material material = entry1.getKey().getMaterial();
                    if (entry.getKey() == material) {

                        double shapeS = entry1.getValue().doubleValue();

                        double newS = shapeS + (shapeS / s) * delta;

                        entry1.setValue(Double.valueOf(newS));

                    }
                }
            }
        }


        //System.out.println("cutObjectsAndSquareCalcType1 = " + cutObjectsAndSquareCalcType1);

    }



    private static Map<Material, Map<Integer, Double>> getElementAndSquares(int calcType, ElementTypes elementTypes) {
        Map<Material, Map<Integer, Double>> map = new LinkedHashMap<>();

        for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {

            if (cutShape.getMaterial().getCalculationType() != calcType) continue;
            if (cutShape.getElementType() != elementTypes) continue;

            double cutShapeSquare = cutShape.getShapeSquare();
            cutShapeSquare = Math.ceil(cutShapeSquare / Math.pow(ProjectHandler.getCommonShapeScale(), 2));
            cutShapeSquare = cutShapeSquare / 1000000;//mm^2 to m^2

            Map<Integer, Double> depthsAndSquare = map.get(cutShape.getMaterial());
            if (depthsAndSquare == null) {
                depthsAndSquare = new LinkedHashMap<>();
                depthsAndSquare.put(Integer.valueOf(cutShape.getDepth()), Double.valueOf(cutShapeSquare));
            } else {

                if (depthsAndSquare.get(Integer.valueOf(cutShape.getDepth())) == null) {
                    double square = cutShapeSquare;
                    depthsAndSquare.put(Integer.valueOf(cutShape.getDepth()), Double.valueOf(square));
                } else {
                    double square = depthsAndSquare.get(Integer.valueOf(cutShape.getDepth())).doubleValue() + cutShapeSquare;
                    depthsAndSquare.put(Integer.valueOf(cutShape.getDepth()), Double.valueOf(square));
                }
            }

            map.put(cutShape.getMaterial(), depthsAndSquare);
        }

        return map;
    }


    //calculate Features
    public static void calculateFeatures() {

        additionalFeaturesMap.clear();
        sinkAndReceiptItem.clear();
        sinkInstallTypesAndReceiptItem.clear();
        sinkEdgeTypesAndReceiptItem.clear();

        cutoutAndReceiptItem.clear();

        jointReceiptItemsList.clear();
        radiusElementReceiptItemMap.clear();

        //Set<String> edgesAndBordersImgPathsList = new LinkedHashSet<>();

        Set<Integer> sinkInstallTypesSet = new LinkedHashSet<>();
        Set<Integer> sinkEdgeTypesSet = new LinkedHashSet<>();

        Set<Integer> cutoutTypesSet = new LinkedHashSet<>();

        Set<Integer> groovesTypesSet = new LinkedHashSet<>();
        Set<Integer> rodsTypesSet = new LinkedHashSet<>();

        for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
            SketchShape sketchShape = (SketchShape) cutShape.getSketchObjectOwner();

            for (AdditionalFeature feature : sketchShape.getFeaturesList()) {

                if (feature instanceof Sink) {
                    Sink sink = (Sink) feature;
                    Material material = sink.getSketchShapeOwner().getMaterial();
                    String name = "Раковина";
                    String units = "шт";
                    double count = 1;
                    String currency = material.getSinkCurrency();
//                    System.out.println(material.getAvailableSinkModels());
//                    System.out.println(sink.getModel());
                    double priceForOne;
                    if (sink.getSubType() == Sink.SINK_TYPE_16 || sink.getSubType() == Sink.SINK_TYPE_17) {
                        priceForOne = 0;
                    } else {
                        priceForOne = (material.getAvailableSinkModels().get(sink.getModel())) / 100;
                    }


                    if (sink.getSubType() != Sink.SINK_TYPE_16 & sink.getSubType() != Sink.SINK_TYPE_17) {
                        ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                        sinkAndReceiptItem.put(sink, receiptItem);
                    }


                    name = "Вклейка мойки, Вариант №" + (sink.getInstallType() + 1);
                    units = "шт";
                    count = 1;
                    currency = material.getSinkInstallTypeCurrency();
                    //System.out.println(material.getSinkInstallTypesAndPrices());

                    if (sink.getSubType() == Sink.SINK_TYPE_16 && sink.getInstallType() == Sink.SINK_INSTALL_TYPE_1) {
                        name = "Установка накладной мойки";
                        priceForOne = 0;
                    } else {
                        name = "Вклейка мойки";
                        priceForOne = (material.getSinkInstallTypesAndPrices().get(0)) / 100;
                    }
                    ReceiptItem receiptItemForInstallType = new ReceiptItem(name, units, count, currency, priceForOne);
                    sinkInstallTypesAndReceiptItem.put(sink, receiptItemForInstallType);
                    sinkInstallTypesSet.add(Integer.valueOf(sink.getInstallType()));

                    name = "none";
                    units = "шт";
                    count = 1;
                    currency = material.getSinkEdgeTypeCurrency();
                    priceForOne = 0;
                    //System.out.println(material.getSinkEdgeTypesCircleAndPrices());
                    //System.out.println(material.getSinkEdgeTypesRectangleAndPrices());
                    if (sink.getCutForm() == Sink.SINK_CUTOUT_RECTANGLE_FORM) {
                        name = "Обработка прямолинейной кромки мойки, Вариант №" + (sink.getEdgeType() + 1);
                        priceForOne = (material.getSinkEdgeTypesRectangleAndPrices().get(sink.getEdgeType())) / 100;
                    } else if (sink.getCutForm() == Sink.SINK_CUTOUT_CIRCLE_FORM) {
                        name = "Обработка криволинейной кромки мойки, Вариант №" + (sink.getEdgeType() + 1);
                        priceForOne = (material.getSinkEdgeTypesCircleAndPrices().get(sink.getEdgeType())) / 100;
                    }

                    ReceiptItem receiptItemForEdgeType = new ReceiptItem(name, units, count, currency, priceForOne);
                    sinkEdgeTypesAndReceiptItem.put(sink, receiptItemForEdgeType);
                    sinkEdgeTypesSet.add(Integer.valueOf(sink.getEdgeType()));


                    ImageView imageView = Sink.getImageForReceipt(sink.getSubType());
                    receiptImageItemsList.add(new ReceiptImageItem("Раковина", imageView));


                } else if (feature instanceof Cutout) {

                    Cutout cutout = (Cutout) feature;
                    Material material = cutout.getSketchShapeOwner().getMaterial();

                    String name = "Вырез ";
                    if (cutout.getSubType() == Cutout.CUTOUT_TYPE_1) name += "под питьевой кран. d = 12мм";
                    else if (cutout.getSubType() == Cutout.CUTOUT_TYPE_2) name += "под смеситель. d = 35мм";
                    else if (cutout.getSubType() == Cutout.CUTOUT_TYPE_3) name += "под варочную панель/раковину.";
                    else if (cutout.getSubType() == Cutout.CUTOUT_TYPE_4) name += "под розетку. d = 65мм";
                    else if (cutout.getSubType() == Cutout.CUTOUT_TYPE_5) name += "под накладную мойку.";
                    else if (cutout.getSubType() == Cutout.CUTOUT_TYPE_6)
                        name += "под варочную панель вровень со столешницей.";
                    else if (cutout.getSubType() == Cutout.CUTOUT_TYPE_7) name += "под радиатор.";
                    String units = "шт";
                    double count = 1;
                    String currency = material.getCutoutCurrency();
//                    System.out.println("material.getCutoutTypesAndPrices() = " + material.getCutoutTypesAndPrices());
//                  System.out.println(sink.getModel());
                    double priceForOne = (material.getCutoutTypesAndPrices().get(cutout.getSubType())) / 100.0;

                    ReceiptItem receiptItemForCutout = new ReceiptItem(name, units, count, currency, priceForOne);
                    cutoutAndReceiptItem.put(cutout, receiptItemForCutout);

                    cutoutTypesSet.add(Integer.valueOf(cutout.getSubType()));

                } else if (feature instanceof Grooves) {
                    Grooves grooves = (Grooves) feature;
                    Material material = grooves.getSketchShapeOwner().getMaterial();
                    String name = "Проточки для стока воды";
                    String units = "шт";
                    double count = 1;
                    String currency = material.getGroovesCurrency();
                    System.out.println("material.getGroovesTypesAndPrices() =" + material.getGroovesTypesAndPrices());
//                    System.out.println(sink.getModel());
                    double priceForOne = (material.getGroovesTypesAndPrices().get(grooves.getSubType() - 1)) / 100.0;


                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    groovesAndReceiptItem.put(grooves, receiptItem);

                    groovesTypesSet.add(Integer.valueOf(grooves.getSubType()));

                } else if (feature instanceof Rods) {
                    Rods rods = (Rods) feature;
                    Material material = rods.getSketchShapeOwner().getMaterial();
                    String name = "Подставка под горячее";
                    String units = "шт";
                    double count = 1;
                    String currency = material.getRodsCurrency();
                    System.out.println("material.getGroovesTypesAndPrices() =" + material.getGroovesTypesAndPrices());
//                    System.out.println(sink.getModel());
                    double priceForOne = (material.getRodsTypesAndPrices().get(rods.getSubType() - 1)) / 100;


                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    rodsAndReceiptItem.put(rods, receiptItem);

                    rodsTypesSet.add(Integer.valueOf(rods.getSubType()));

                }

                //additionalFeaturesList.add(feature);
            }
        }

        for (Integer integer : sinkInstallTypesSet) {
            ReceiptImageItem receiptImageItem = new ReceiptImageItem("Вклейка мойки, тип " + (integer.intValue() + 1) + "", Sink.getImageInstallType(integer.intValue()));
            receiptImageItemsList.add(receiptImageItem);
        }

        for (Integer integer : sinkEdgeTypesSet) {
            ReceiptImageItem receiptImageItem = new ReceiptImageItem("Кромка мойки, тип " + (integer.intValue() + 1) + "", Sink.getImageEdgeType(integer.intValue()));
            receiptImageItemsList.add(receiptImageItem);
        }

        for (Integer integer : cutoutTypesSet) {
            ReceiptImageItem receiptImageItem = new ReceiptImageItem("Вырез", Cutout.getImageForReceipt(integer));
            receiptImageItemsList.add(receiptImageItem);
        }

        for (Integer integer : groovesTypesSet) {
            ReceiptImageItem receiptImageItem = new ReceiptImageItem("Проточки для воды", Grooves.getImageForReceipt(integer));
            receiptImageItemsList.add(receiptImageItem);
        }

        for (Integer integer : rodsTypesSet) {
            ReceiptImageItem receiptImageItem = new ReceiptImageItem("Подставка под горячее", Rods.getImageForReceipt(integer));
            receiptImageItemsList.add(receiptImageItem);
        }


        //add joints
        {
            double countStraight = 0;
            double countAngles = 0;
            String currency = "";
            String name = "none";
            String units = "м.п.";
            double priceForOne = 0;
            for (SketchShape sketchShape : SketchDesigner.getSketchShapesList()) {
                sketchShape.calculateAllJoints();
                Material material = sketchShape.getMaterial();

                for (Joint joint : sketchShape.getJoints()) {

                    currency = material.getJointsCurrency();
                    if (joint.getType() == Joint.JointType.STRAIGHT) {

                        countStraight += (joint.getLen() / ProjectHandler.getCommonShapeScale()) / 1000;

                    } else {

                        countAngles += (joint.getLen() / ProjectHandler.getCommonShapeScale()) / 1000;
                    }

                    priceForOne = (material.getJointsTypesAndPrices().get(joint.getType().ordinal())) / 100;

                }
            }

            if (countStraight != 0) {
                name = "Соединение элементов по прямому стыку";
                ReceiptItem receiptItem = new ReceiptItem(name, units, countStraight / 2, currency, priceForOne);
                jointReceiptItemsList.add(receiptItem);
            }
            if (countAngles != 0) {
                name = "Соединение элементов по косому стыку";
                ReceiptItem receiptItem = new ReceiptItem(name, units, countAngles / 2, currency, priceForOne);
                jointReceiptItemsList.add(receiptItem);
            }
        }


        // add radius elements
        for (SketchShape sketchShape : SketchDesigner.getSketchShapesList()) {

//            sketchShape.calculateAllJoints();

            Material material = sketchShape.getMaterial();

//            for(Joint joint : sketchShape.getJoints()){
//
//
//
//                boolean contain = false;
//                for(Map.Entry<Joint, ReceiptItem> entry : jointAndReceiptItem.entrySet()){
//                    if(joint.getShapeOwner().equals(entry.getKey().getAnotherShape())){
//                        contain = true;
//                        break;
//                    }
//                }
//                if(!contain){
//
//                    String name = "none";
//                    if(joint.getType() == Joint.JointType.STRAIGHT){
//                        name = "Соединение элементов по прямому стыку";
//                    }else{
//                        name = "Соединение элементов по косому стыку";
//                    }
//
//                    String units = "м.п.";
//                    double count = (joint.getLen() / ProjectHandler.getCommonShapeScale())/1000;
//                    String currency = material.getJointsCurrency();
//                    double priceForOne = (material.getJointsTypesAndPrices().get(joint.getType().ordinal())) / 100;
//
//                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
//
//                    jointAndReceiptItem.put(joint, receiptItem);
//                }
//
//            }


            if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                    material.getName().indexOf("Натуральный камень") != -1 ||
                    material.getName().indexOf("Dektone") != -1 ||
                    material.getName().indexOf("Мраморный агломерат") != -1 ||
                    material.getMainType().indexOf("Кварцекерамический камень") != -1) {


                if (sketchShape.getShapeType() == ShapeType.RECTANGLE_CIRCLE_CORNER || sketchShape.getShapeType() == ShapeType.RECTANGLE_CIRCLE_CORNER_INTO) {
                    String name = "Радиусный элемент";
                    String units = "шт.";
                    String currency = material.getRadiusElementCurrency();
                    double count = 1;
                    double priceForOne = material.getRadiusElementPrice() / 100;
                    if (radiusElementReceiptItemMap.containsKey(material)) {
                        count += Double.parseDouble(radiusElementReceiptItemMap.get(material).getCount());
                    }
                    ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
                    radiusElementReceiptItemMap.put(material, receiptItem);
                }
            }
        }


        //System.out.println("edgesAndBordersLenMap = " + edgesAndBordersLenMap);
    }

    public static void calculateEdgesAndBorders() {
        mainImagesMap.clear();

        //edgesAndBordersPricesMap.clear();
        edgesAndBordersLenMap.clear();
        edgesAndBordersObjectsLenMap.clear();
        edgesAndBordersReceiptItemMap.clear();
        //edgesAndBordersImagesMap.clear();
        leakGrooveReceiptItemMap.clear();
        stoneHemReceiptItemMap.clear();


        for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
            SketchShape sketchShape = (SketchShape) cutShape.getSketchObjectOwner();

            Material material = sketchShape.getMaterial();
            for (SketchEdge sketchEdge : sketchShape.getEdges()) {
                if (sketchEdge.isDefined()) {

                    //add image
                    //edgesAndBordersImagesMap.put(sketchEdge.getName(), sketchEdge.getImageView());
                    String name = "none";
                    if (sketchEdge instanceof Edge) {
                        name = "Фото выбраной кромки";
                    } else {
                        name = "Фото пристеночного бортика";
                    }


                    //add len:
                    double len = sketchShape.getEdgeOrBorderLength(sketchEdge);

                    boolean contain = false;
                    for (Map.Entry<SketchEdge, Double> entry : edgesAndBordersObjectsLenMap.entrySet()) {
                        if (entry.getKey().getImgPath().equals(sketchEdge.getImgPath())) {
                            contain = true;
                            entry.setValue(Double.valueOf(entry.getValue().doubleValue() + len));
                            break;
                        }
                    }

                    if (!contain) {
                        edgesAndBordersObjectsLenMap.put(sketchEdge, Double.valueOf(len));
                        ReceiptImageItem receiptImageItem = new ReceiptImageItem(name, sketchEdge.getImageView());
                        receiptImageItemsList.add(receiptImageItem);
                    }


                    if (sketchEdge instanceof Edge && ((Edge) sketchEdge).isStoneHemOrLeakGroove()) {

                        if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                                material.getName().indexOf("Натуральный камень") != -1 ||
                                material.getName().indexOf("Dektone") != -1 ||
                                material.getName().indexOf("Мраморный агломерат") != -1 ||
                                material.getMainType().indexOf("Кварцекерамический камень") != -1) {


                            name = "Выборка капельника";
                            String units = "м.п.";
                            String currency = material.getLeakGrooveCurrency();
                            double priceForOne = material.getLeakGroovePrice() / 100;

                            len = len / 1000;

                            if (leakGrooveReceiptItemMap.containsKey(material)) {
                                len += Double.parseDouble(leakGrooveReceiptItemMap.get(material).getCount());
                            }
                            ReceiptItem receiptItem = new ReceiptItem(name, units, len, currency, priceForOne);
                            leakGrooveReceiptItemMap.put(material, receiptItem);

                        } else {

                            name = "подгиб камня к каплесборником";
                            String units = "м.п.";
                            String currency = material.getStoneHemCurrency();
                            double priceForOne = material.getStoneHemPrice() / 100;

                            len = len / 1000;

                            if (stoneHemReceiptItemMap.containsKey(material)) {
                                len += Double.parseDouble(stoneHemReceiptItemMap.get(material).getCount());
                            }
                            ReceiptItem receiptItem = new ReceiptItem(name, units, len, currency, priceForOne);
                            stoneHemReceiptItemMap.put(material, receiptItem);
                        }
                    }


                }
            }
        }

        for (Map.Entry<SketchEdge, Double> entry : edgesAndBordersObjectsLenMap.entrySet()) {

            String name = "none";
            String units = "м. п.";
            double count = entry.getValue() / 1000;
            String currency = entry.getKey().getCurrency();
            double priceForOne = entry.getKey().getPrice();

            if (entry.getKey() instanceof Edge) {
                name = "Лицевая кромка";
            } else {
                name = "Пристеночный плинтус";
            }

            ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
            edgesAndBordersReceiptItemMap.put(entry.getKey(), receiptItem);

        }

        //for topCutTypes
        Set<Integer> borderTopCutTypes = new LinkedHashSet<>();//forImages
        Set<Integer> borderSideCutTypes = new LinkedHashSet<>();//forImages
        bordersTopCutReceiptItemMap.clear();
        bordersSideCutReceiptItemMap.clear();
        // Map<Integer, Double> topCutTypesAndLen = new LinkedHashMap<>();
        for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
            SketchShape sketchShape = (SketchShape) cutShape.getSketchObjectOwner();

            for (SketchEdge sketchEdge : sketchShape.getEdges()) {
                if (sketchEdge.isDefined()) {
                    if (sketchEdge instanceof Border) {

                        Border border = (Border) sketchEdge;
                        double len = sketchShape.getEdgeOrBorderLength(border);
//                        int type = border.getType();
//                        if(topCutTypesAndLen.containsKey(new Integer(type))){
//                            len += topCutTypesAndLen.get(new Integer(type)).doubleValue();
//                        }
//                        topCutTypesAndLen.put(new Integer(type), new Double(len));

                        String name = "Обработка верхней грани бортика";
                        String units = "м. п.";
                        double count = len / 1000;
                        String currency = "RUB";
                        Material material = border.getSketchEdgeOwner().getMaterial();
                        //System.out.println(material.getBorderTopCutTypesAndPrices());
                        //System.out.println(border.getBorderCutType());
                        double priceForOne = material.getBorderTopCutTypesAndPrices().get(Integer.valueOf(border.getBorderCutType() - 1)) / 100;

                        ReceiptItem receiptItemTopCut = new ReceiptItem(name, units, count, currency, priceForOne);
                        bordersTopCutReceiptItemMap.put(border, receiptItemTopCut);
                        //System.out.println(border);

                        name = "Запил бортика";
                        units = "шт";
                        count = border.getBorderAnglesCutType() - 1;
                        currency = border.getCurrency();
                        //System.out.println("border.getBorderSideCutType() = " + border.getBorderSideCutType());
                        //System.out.println("material.getBorderSideCutTypesAndPrices() = " + material.getBorderSideCutTypesAndPrices());
                        priceForOne = material.getBorderSideCutTypesAndPrices().get(Integer.valueOf(border.getBorderSideCutType() - 1)) / 100;

                        ReceiptItem receiptItemSideCut = new ReceiptItem(name, units, count, currency, priceForOne);
                        bordersSideCutReceiptItemMap.put(border, receiptItemSideCut);

                        borderTopCutTypes.add(border.getBorderCutType());
                        if (border.getBorderAnglesCutType() != Border.BORDER_ANGLE_CUT_NONE) {
                            borderSideCutTypes.add(border.getBorderSideCutType());
                        }

                    }
                }
            }
        }

        for (Integer integer : borderTopCutTypes) {
            ImageView imageView = new ImageView(new Image(ProjectHandler.class.getResource("/styles/images/edgeManager/borderCut" + integer.intValue() + ".png").toString()));
            receiptImageItemsList.add(new ReceiptImageItem("Грань бортика", imageView));
        }
        for (Integer integer : borderSideCutTypes) {
            ImageView imageView = new ImageView(new Image(ProjectHandler.class.getResource("/styles/images/edgeManager/borderSideCut" + integer.intValue() + ".png").toString()));
            receiptImageItemsList.add(new ReceiptImageItem("Запил бортика", imageView));
        }

    }
}
