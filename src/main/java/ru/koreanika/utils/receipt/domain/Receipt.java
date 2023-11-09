package ru.koreanika.utils.receipt.domain;

import lombok.Getter;
import ru.koreanika.common.material.Material;
import ru.koreanika.common.material.MaterialSheet;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutObject;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.Cutout;
import ru.koreanika.sketchDesigner.Features.Grooves;
import ru.koreanika.sketchDesigner.Features.Rods;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.utils.receipt.ui.component.ReceiptImageItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Receipt {

    @Getter
    public static boolean pricesActual = true;// it is need for adding message in receipt that prices are not actual.

    @Getter
    public static double additionalPriceCoefficientForAcryl = 0.0;

    @Getter
    public static boolean quartzInProject = false;

    @Getter
    public static String receiptLog = "";

    @Getter
    public static double allSquare = 0.0;

    @Getter
    private static Map<Material, ArrayList<MaterialSheet>> materialAndSheets = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> materialSquaresCalcType2 = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> shapesSquaresCalcType2 = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> tableTopsSquaresCalcType2 = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> wallPanelsSquaresCalcType2 = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> windowsillsSquaresCalcType2 = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> tableTopSquaresCalcType1Result = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> tableTopSquaresCalcType2Result = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> wallPanelSquaresCalcType1Result = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> wallPanelSquaresCalcType2Result = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> windowsillSquaresCalcType1Result = new LinkedHashMap<>();

    @Getter
    private static Map<Material, Map<Integer, Double>> windowsillSquaresCalcType2Result = new LinkedHashMap<>();

    @Getter
    private static Map<CutObject, Double> cutObjectsAndSquareCalcType1 = new LinkedHashMap<>();

    @Getter
    private static Map<CutObject, Double> cutObjectsAndSquareCalcType2 = new LinkedHashMap<>();

    @Getter
    private static Map<Sink, Double> cutSinkAndSquareCalcType2 = new LinkedHashMap<>();

    //receiptItems for cut shapes and materials:
    @Getter
    private static Map<CutShape, ReceiptItem> cutShapesAndReceiptItem = new LinkedHashMap<>();

    //receiptItems for cuttable Sink:
    @Getter
    private static Map<Sink, ReceiptItem> cuttableSinkAndReceiptItem = new LinkedHashMap<>();

    //receiptItems for edges and borders:
    @Getter
    private static Map<SketchEdge, ReceiptItem> edgesAndBordersReceiptItemMap = new LinkedHashMap<>();

    @Getter
    private static Map<Border, ReceiptItem> bordersTopCutReceiptItemMap = new LinkedHashMap<>();

    @Getter
    private static Map<Border, ReceiptItem> bordersSideCutReceiptItemMap = new LinkedHashMap<>();

    //receiptItems for Sink:
    @Getter
    private static Map<Sink, ReceiptItem> sinkAndReceiptItem = new LinkedHashMap<>();

    @Getter
    private static Map<Sink, ReceiptItem> sinkInstallTypesAndReceiptItem = new LinkedHashMap<>();

    @Getter
    private static Map<Sink, ReceiptItem> sinkEdgeTypesAndReceiptItem = new LinkedHashMap<>();

    //receiptItems for Cutouts:
    @Getter
    private static Map<Cutout, ReceiptItem> cutoutAndReceiptItem = new LinkedHashMap<>();

    //receiptItems for Grooves:
    @Getter
    private static Map<Grooves, ReceiptItem> groovesAndReceiptItem = new LinkedHashMap<>();

    //receiptItems for Rods:
    @Getter
    private static Map<Rods, ReceiptItem> rodsAndReceiptItem = new LinkedHashMap<>();

    //receiptItems for radius element:
    @Getter
    private static Map<Material, ReceiptItem> radiusElementReceiptItemMap = new LinkedHashMap<>();

    //receiptItems for Joints:
    @Getter
    private static List<ReceiptItem> jointReceiptItemsList = new ArrayList<>();

    //images for receipt:
    @Getter
    private static ArrayList<ReceiptImageItem> receiptImageItemsList = new ArrayList<>();

    @Getter
    private static Map<Material, ReceiptItem> stoneHemReceiptItemMap = new LinkedHashMap<>();

    @Getter
    private static Map<Material, ReceiptItem> leakGrooveReceiptItemMap = new LinkedHashMap<>();

    @Getter
    private static LinkedHashMap<String, Double> materialStocks = new LinkedHashMap<>();//stock Name, stockResultSize in RUR

    @Getter
    private static LinkedHashMap<String, Double> itemStocks = new LinkedHashMap<>();//stock Name, stockResultSize in RUR

    public static double getUsesSlabs() {
        double usesSlabs = 0;
        for (MaterialSheet ms : CutDesigner.getInstance().getCutPane().getUsedMaterialSheetsList()) {
            usesSlabs += ms.getUsesSlabs();
        }
        return usesSlabs;
    }

}
