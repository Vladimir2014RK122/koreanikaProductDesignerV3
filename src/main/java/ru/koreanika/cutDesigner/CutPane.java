package ru.koreanika.cutDesigner;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.RepresentToJson;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.utils.Main;
import ru.koreanika.cutDesigner.ListStatistics.StatisticCellItem;
import ru.koreanika.cutDesigner.Shapes.*;
import ru.koreanika.cutDesigner.TreeViewProjectElements.TreeCellCutShape;
import ru.koreanika.cutDesigner.TreeViewProjectElements.TreeCellFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.project.Project;
import ru.koreanika.project.ProjectType;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.Dimensions.LinearDimension;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.sketchDesigner.Shapes.SketchShapeUnion;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.utils.*;


import java.util.*;

public class CutPane extends Pane implements RepresentToJson {

    private static double cutPaneScale = 1;
    private static Scale scale = new Scale(1.0, 1.0);
    public static SimpleDoubleProperty cutSheetScaleProperty = new SimpleDoubleProperty();

    private final EventBus eventBus;
    private final ProjectHandler projectHandler;

    private double originalCutPaneWidth = 0;
    private double originalCutPaneHeight = 0;

    private static Map<String, ArrayList<Material.MaterialSheet>> materialSheetsMap = new LinkedHashMap<>();

    LoadingProgressDialog cuttingProgressDialog;
    private static Thread autoCutThread;

    public Group cutObjectsGroup = new Group();

    private static boolean externalStopAutoCutting = false;

    static CutDesigner cutDesigner;

    public CutPane(CutDesigner cutDesigner) {
        this.cutDesigner = cutDesigner;
        materialSheetsMap.clear();
        this.setId("cutPane");
        this.setPrefSize(100.0, 100.0);

        this.getChildren().add(cutObjectsGroup);

        this.getTransforms().add(scale);
        initDragAndDrop();
        initZoom();

        eventBus = ServiceLocator.getService("EventBus", EventBus.class);
        projectHandler = ServiceLocator.getService("ProjectHandler", ProjectHandler.class);
    }

    BooleanProperty finishedCuttingThread = new SimpleBooleanProperty(false);

    /*CALCULATE STATISTICS START --->>>*/
    public static void refreshStatistics(ArrayList<StatisticCellItem> materialSheetStatisticsListObservable) {
        materialSheetStatisticsListObservable.clear();
        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {
            double percentSumm = 0;
            int sheetsCount = entry.getValue().size();
            for (Material.MaterialSheet materialSheet : entry.getValue()) {
                //update statistic for this sheet
                percentSumm +=Double.valueOf(calculateUsesSheetInPercent(materialSheet));

            }
            materialSheetStatisticsListObservable.add(new StatisticCellItem(entry.getKey(), Double.valueOf(entry.getKey().split("#")[1]), percentSumm/sheetsCount));
        }

        hideHalfOfMaterialIfNotUsed();
    }

    public static int calculateUsesSheetInPercent(Material.MaterialSheet materialSheet) {
        double squareSheet = materialSheet.getPrefWidth() * materialSheet.getPrefHeight();
        double squareShapesSum = 0;
        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            if (checkOverMaterialOrNot(cutShape, materialSheet)) {
                squareShapesSum += getPolygonSquare(cutShape.getPolygon());
            }
        }

        for (CutShapeEdge cutShapeEdge : cutDesigner.getCutShapeEdgesList()) {
            if (checkOverMaterialOrNot(cutShapeEdge, materialSheet)) {
                squareShapesSum += getPolygonSquare(cutShapeEdge.getPolygon());
            }
        }

        return (int) ((squareShapesSum * 100) / squareSheet);
    }

    private static double getPolygonSquare(Polygon polygon) {
        //Gaus theorem
        double S = 0;
        double firstPart = 0;
        double secondPart = 0;
        for (int i = 0; i < polygon.getPoints().size() - 3; i++) {
            if (i % 2 == 0) {
                //X
                double x = polygon.getPoints().get(i).doubleValue();
                double y = polygon.getPoints().get(i + 3).doubleValue();
                firstPart += x * y;
            }
        }
        for (int i = 0; i < polygon.getPoints().size() - 1; i++) {
            if (i % 2 != 0) {
                //Y
                double x = polygon.getPoints().get(i + 1).doubleValue();
                double y = polygon.getPoints().get(i).doubleValue();
                secondPart -= x * y;
            }
        }

        S = (firstPart + secondPart) / 2;

        return S;
    }
    /*CALCULATE STATISTICS END <<<---*/

    public Group getCutObjectsGroup() {
        return cutObjectsGroup;
    }

    public static void hideHalfOfMaterialIfNotUsed() {
        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {
            //if(entry.getKey().indexOf("Кварцевый") != -1){
            if (true) {
                for (Material.MaterialSheet materialSheet : entry.getValue()) {
                    System.out.println("1112 materialSheet.getPartsOfSheet() = " + materialSheet.getPartsOfSheet());
                    System.out.println("materialSheet.getCuttingDirection() = " + materialSheet.getCuttingDirection());
                    if (materialSheet.getCuttingDirection().equals("h")) {

                        System.out.println("materialSheet.getPartsOfSheet() = " + materialSheet.getPartsOfSheet());

                        if(materialSheet.getPartsOfSheet() == 1){

                            System.out.println("for 1 = " + materialSheet.getPartsOfSheet());

                            boolean halfFirstHide = true;

                            for (CutShape cutShape : cutDesigner.getCutShapesList()) {
                                if (checkOverMaterialOrNot(cutShape, materialSheet) && cutShape.getMaterial().equals(materialSheet.getMaterial())) {
                                    halfFirstHide = false;
                                }
                            }

                            for (CutShapeEdge cutShapeEdge : cutDesigner.getCutShapeEdgesList()) {
                                if (checkOverMaterialOrNot(cutShapeEdge, materialSheet) && cutShapeEdge.getOwner().getMaterial().equals(materialSheet.getMaterial())) {
                                    halfFirstHide = false;
                                }
                            }

                            for (CutShapeAdditionalFeature cutShapeAdditionalFeature : cutDesigner.getCutShapeAdditionalFeaturesList()) {
                                if (checkOverMaterialOrNot(cutShapeAdditionalFeature, materialSheet) && cutShapeAdditionalFeature.getMaterial().equals(materialSheet.getMaterial())) {
                                    halfFirstHide = false;
                                }
                            }

                            materialSheet.hideHorizontalHalf(1, halfFirstHide);
                            System.out.println("HIDE 1 PART");

                        }else if(materialSheet.getPartsOfSheet() == 2){

                            //System.out.println("for 2 = " + materialSheet.getPartsOfSheet());

                            boolean halfFirstHide = true;
                            boolean halfSecondHide = true;
                            for (CutShape cutShape : cutDesigner.getCutShapesList()) {

                                Bounds sheetBounds = materialSheet.getPolygon().localToScene(materialSheet.getPolygon().getBoundsInLocal());
                                Bounds cutShapeBounds = cutShape.getPolygon().localToScene(cutShape.getPolygon().getBoundsInLocal());

                                if (checkOverMaterialOrNot(cutShape, materialSheet) && cutShape.getMaterial().equals(materialSheet.getMaterial())) {
                                    halfFirstHide = false;
                                    if (cutShapeBounds.getMaxY() > sheetBounds.getMinY() + sheetBounds.getHeight() / 2) {
                                        halfSecondHide = false;
                                    }

                                }
                            }

                            for (CutShapeEdge cutShapeEdge : cutDesigner.getCutShapeEdgesList()) {

                                Bounds sheetBounds = materialSheet.getPolygon().localToScene(materialSheet.getPolygon().getBoundsInLocal());
                                Bounds cutShapeEdgeBounds = cutShapeEdge.getPolygon().localToScene(cutShapeEdge.getPolygon().getBoundsInLocal());

                                if (checkOverMaterialOrNot(cutShapeEdge, materialSheet) && cutShapeEdge.getOwner().getMaterial().equals(materialSheet.getMaterial())) {
                                    halfFirstHide = false;
                                    if (cutShapeEdgeBounds.getMaxY() > sheetBounds.getMinY() + sheetBounds.getHeight() / 2) {
                                        halfSecondHide = false;
                                    }

                                }
                            }

                            for (CutShapeAdditionalFeature cutShapeAdditionalFeature : cutDesigner.getCutShapeAdditionalFeaturesList()) {

                                Bounds sheetBounds = materialSheet.getPolygon().localToScene(materialSheet.getPolygon().getBoundsInLocal());
                                Bounds cutShapeAddFeatureBounds = cutShapeAdditionalFeature.getPolygon().localToScene(cutShapeAdditionalFeature.getPolygon().getBoundsInLocal());

                                if (checkOverMaterialOrNot(cutShapeAdditionalFeature, materialSheet) && cutShapeAdditionalFeature.getMaterial().equals(materialSheet.getMaterial())) {
                                    halfFirstHide = false;
                                    if (cutShapeAddFeatureBounds.getMaxY() > sheetBounds.getMinY() + sheetBounds.getHeight() / 2) {
                                        halfSecondHide = false;
                                    }

                                }
                            }


                            materialSheet.hideHorizontalHalf(1, halfFirstHide);
                            materialSheet.hideHorizontalHalf(2, halfSecondHide);
                        }


                    } else if (materialSheet.getCuttingDirection().equals("v")) {


                        boolean half1Hide = true;
                        boolean half2Hide = true;
                        boolean half3Hide = true;
                        boolean half4Hide = true;
                        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
                            if (checkOverMaterialOrNot(cutShape, materialSheet) && cutShape.getMaterial().equals(materialSheet.getMaterial())) {

                                double materialScale = materialSheet.getMaterialScale();
                                double minWidth = materialSheet.getSheetMinWidth() * cutPaneScale;


                                Bounds sheetBounds = materialSheet.getPolygon().localToScene(materialSheet.getPolygon().getBoundsInLocal());
                                Bounds cutShapeBounds = cutShape.getPolygon().localToScene(cutShape.getPolygon().getBoundsInLocal());

                                if (cutShapeBounds.getMaxX() > sheetBounds.getMinX()) {
                                    half1Hide = false;
                                }
                                if (cutShapeBounds.getMaxX() > sheetBounds.getMinX() + (minWidth * materialScale)) {
                                    half2Hide = false;
                                }
                                if (materialSheet.getPartsOfSheet() == 4) {
                                    if (cutShapeBounds.getMaxX() > sheetBounds.getMinX() + ((minWidth * materialScale) * 2.0)) {
                                        half3Hide = false;
                                    }
                                    if (cutShapeBounds.getMaxX() > sheetBounds.getMinX() + ((minWidth * materialScale) * 3.0)) {
                                        half4Hide = false;
                                    }
                                }

                            }
                        }

                        for (CutShapeEdge cutShapeEdge : cutDesigner.getCutShapeEdgesList()) {
                            if (checkOverMaterialOrNot(cutShapeEdge, materialSheet) && cutShapeEdge.getOwner().getMaterial().equals(materialSheet.getMaterial())) {

                                double materialScale = materialSheet.getMaterialScale();
                                double minWidth = materialSheet.getSheetMinWidth() * cutPaneScale;

                                Bounds sheetBounds = materialSheet.getPolygon().localToScene(materialSheet.getPolygon().getBoundsInLocal());
                                Bounds cutShapeEdgeBounds = cutShapeEdge.getPolygon().localToScene(cutShapeEdge.getPolygon().getBoundsInLocal());

                                if (cutShapeEdgeBounds.getMaxX() > sheetBounds.getMinX()) {
                                    half1Hide = false;
                                }
                                if (cutShapeEdgeBounds.getMaxX() > sheetBounds.getMinX() + (minWidth * materialScale)) {
                                    half2Hide = false;
                                }

                                if (materialSheet.getPartsOfSheet() == 4) {
                                    if (cutShapeEdgeBounds.getMaxX() > sheetBounds.getMinX() + ((minWidth * materialScale) * 2)) {
                                        half3Hide = false;
                                    }
                                    if (cutShapeEdgeBounds.getMaxX() > sheetBounds.getMinX() + ((minWidth * materialScale) * 3)) {
                                        half4Hide = false;
                                    }
                                }
                            }
                        }

                        for (CutShapeAdditionalFeature cutShapeAdditionalFeature : cutDesigner.getCutShapeAdditionalFeaturesList()) {
                            if (checkOverMaterialOrNot(cutShapeAdditionalFeature, materialSheet) && cutShapeAdditionalFeature.getMaterial().equals(materialSheet.getMaterial())) {

                                double materialScale = materialSheet.getMaterialScale();
                                double minWidth = materialSheet.getSheetMinWidth() * cutPaneScale;

                                Bounds sheetBounds = materialSheet.getPolygon().localToScene(materialSheet.getPolygon().getBoundsInLocal());
                                Bounds cutShapeAddFeatureBounds = cutShapeAdditionalFeature.getPolygon().localToScene(cutShapeAdditionalFeature.getPolygon().getBoundsInLocal());

                                if (cutShapeAddFeatureBounds.getMaxX() > sheetBounds.getMinX()) {
                                    half1Hide = false;
                                }
                                if (cutShapeAddFeatureBounds.getMaxX() > sheetBounds.getMinX() + (minWidth * materialScale)) {
                                    half2Hide = false;
                                }
                                if (materialSheet.getPartsOfSheet() == 4) {
                                    if (cutShapeAddFeatureBounds.getMaxX() > sheetBounds.getMinX() + ((minWidth * materialScale) * 2)) {
                                        half3Hide = false;
                                    }
                                    if (cutShapeAddFeatureBounds.getMaxX() > sheetBounds.getMinX() + ((minWidth * materialScale) * 3)) {
                                        half4Hide = false;
                                    }
                                }

                            }
                        }

                        materialSheet.hideVerticalHalf(1, half1Hide);
                        materialSheet.hideVerticalHalf(2, half2Hide);
                        materialSheet.hideVerticalHalf(3, half3Hide);
                        materialSheet.hideVerticalHalf(4, half4Hide);
                    }

                }
            }
        }
    }

    public void deleteCutShape(CutObject cutObject) {

        if (cutObject instanceof CutShapeEdge) {
            deleteCutShape(((CutShapeEdge) cutObject).getOwner());
        } else if (cutObject instanceof CutShape) {

            CutShape cutShape = (CutShape) cutObject;
            if (cutShape.isContainInUnion()) {
                cutDesigner.usedShapeUnionsNumberList.remove(Integer.valueOf(cutShape.getUnionNumber()));
                cutDesigner.getCutShapeUnionsList().remove(cutShape.getCutShapeUnionOwner());
            }


            //this.getChildren().remove(cutObject);
            cutObjectsGroup.getChildren().remove(cutObject);
            cutDesigner.getCutShapesList().remove(cutObject);
            cutDesigner.usedShapesNumberList.remove(Integer.valueOf(cutShape.getShapeNumber()));
            SketchShape sketchShape = SketchDesigner.getSketchShape(cutShape.getShapeNumber());
//            System.out.println(sketchShape);
//            System.out.println(cutShape.getShapeNumber());
//            for(SketchShape shape : SketchDesigner.getSketchShapesList()){
//                System.out.println(SketchDesigner.getSketchShapesList());
//            }

            //sketchShape.deleteCutShape();
            Iterator<LinearDimension> it = cutDesigner.getAllDimensions().iterator();
            while (it.hasNext()) {
                LinearDimension dim = it.next();
                if (dim.getConnectPoint1().getParent() == cutObject || dim.getConnectPoint2().getParent() == cutObject) {
                    this.getChildren().remove(dim);
                    it.remove();
                }
            }

            Iterator<CutShapeEdge> itEdges = ((CutShape) cutObject).getCutShapeEdgesList().iterator();
//            System.out.println("EDGES SIZE:" + ((CutShape)cutObject).getCutShapeEdgesList());
            while (itEdges.hasNext()) {
                CutShapeEdge edge = itEdges.next();
                cutDesigner.getCutShapeEdgesList().remove(edge);
                if (edge.getOwner().getShapeNumber() == ((CutShape) cutObject).getShapeNumber()) {
                    //this.getChildren().remove(edge);
                    cutObjectsGroup.getChildren().remove(edge);
                }


                //remove dimensions:
                Iterator<LinearDimension> itDimensions = cutDesigner.getAllDimensions().iterator();
                while (itDimensions.hasNext()) {
                    LinearDimension dim = itDimensions.next();
                    if (dim.getConnectPoint1().getParent() == cutObject || dim.getConnectPoint2().getParent() == cutObject) {
                        this.getChildren().remove(dim);
                        itDimensions.remove();
                    }
                }
            }
        } else if (cutObject instanceof CutShapeAdditionalFeature) {

            Sink sink = (Sink) (((CutShapeAdditionalFeature) cutObject).getFeatureOwner());
            for (CutShapeAdditionalFeature cutShapeAdditionalFeature : sink.getCutShapes()) {
                cutDesigner.getCutShapeAdditionalFeaturesList().remove(cutShapeAdditionalFeature);
                //this.getChildren().remove(cutShapeAdditionalFeature);
                cutObjectsGroup.getChildren().remove(cutShapeAdditionalFeature);
            }

        }

    }

    public void deleteCutShape(int number) {

        ArrayList<CutShape> shapesForDelete = new ArrayList<>();
        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            if (cutShape.getShapeNumber() == number) {
                deleteCutShape(cutShape);
                break;
            }
        }

    }

    public CutShape getCutShapeByNumber(int number) {
        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            if (cutShape.getShapeNumber() == number) {
                return cutShape;
            }
        }

        return null;

    }

    public void initDragAndDrop() {

        this.setOnMousePressed(event -> {
            if (cutDesigner.selectedShapes.size() != 0) {
                cutDesigner.unSelectAllShapes();
                refreshCutPaneView();
            }
            for (LinearDimension ld : cutDesigner.getAllDimensions()) {
                ld.selectDimension(false);
                ld.toFront();
            }

        });
        this.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);

        });
        this.setOnDragDropped(event -> {
            //System.out.println("drag dropped");
            Dragboard db = event.getDragboard();


            if (db.getContent(CutDesigner.CELL_FORMAT) != null) {
                if (db.getContent(CutDesigner.CELL_FORMAT).equals(TreeCellCutShape.TREE_CELL_FORMAT)) {

                    int shapeNumber = (Integer) db.getContent(CutDesigner.SHAPE_NUMBER_DATA_FORMAT);
                    ElementTypes elementType = (ElementTypes) db.getContent(CutDesigner.ELEMENT_DATA_FORMAT);
                    //System.out.println("ELEMENT TYPE: " + elementType);
                    if (elementType == ElementTypes.UNION) {

                        for (Integer n : cutDesigner.usedShapeUnionsNumberList) {
                            if (n.intValue() == shapeNumber) {
                                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Элемент уже размещен"));
                                return;
                            }
                        }
                        SketchShapeUnion sketchShapeUnion = SketchDesigner.getSketchShapeUnion(shapeNumber);
                        CutShapeUnion cutShapeUnion = new CutShapeUnion(sketchShapeUnion);

                        cutDesigner.getCutShapeUnionsList().add(cutShapeUnion);
                        cutDesigner.usedShapeUnionsNumberList.add(Integer.valueOf(shapeNumber));
                        for (int i = 0; i < cutShapeUnion.getCutShapesInUnionList().size(); i++) {

                            SketchShape sketchShape = sketchShapeUnion.getSketchShapesInUnion().get(i);

                            CutShape cutShape = cutShapeUnion.getCutShapesInUnionList().get(i);
                            cutShape.setContainInUnion(true);
                            cutShape.setUnionNumber(cutShapeUnion.getUnionNumber());
                            cutShape.setCutShapeUnionOwner(cutShapeUnion);

                            //this.getChildren().add(cutShape);
                            cutObjectsGroup.getChildren().add(cutShape);

                            cutShape.setTranslateX(event.getX() + sketchShapeUnion.getSketchShapesPositions().get(i).getX());
                            cutShape.setTranslateY(event.getY() + sketchShapeUnion.getSketchShapesPositions().get(i).getY());

                            cutDesigner.getCutShapesList().add(cutShape);
                            cutDesigner.usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));

                            for (CutShapeEdge cutShapeEdge : cutShape.getCutShapeEdgesList()) {
                                if (cutShapeEdge == null || cutShapeEdge.getStartCoordinate() == null) continue;
                                //this.getChildren().add(cutShapeEdge);
                                cutObjectsGroup.getChildren().add(cutShapeEdge);

                                cutShapeEdge.setUnionNumber(cutShapeUnion.getUnionNumber());
                                cutShapeEdge.setCutShapeUnionOwner(cutShapeUnion);
                                cutShapeEdge.setContainInUnion(true);

                                cutShapeEdge.setTranslateX(cutShape.getTranslateX() + cutShapeEdge.getStartCoordinate().getX());
                                cutShapeEdge.setTranslateY(cutShape.getTranslateY() + cutShapeEdge.getStartCoordinate().getY());
                                cutDesigner.getCutShapeEdgesList().add(cutShapeEdge);

                                //cutShapeEdge.rotateShapeGlobal(cutShape.getRotate(), cutShape.localToParent(cutShape.getGlobalCenter()));
                            }
                            cutShape.rotateShapeLocal(sketchShape.getRotateTransform().getAngle());

                        }

                        refreshCutPaneView();
                        cutShapeUnion.setRotatePivots();


                    } else {
                        for (Integer n : cutDesigner.usedShapesNumberList) {
                            if (n.intValue() == shapeNumber) {
                                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Элемент уже размещен"));
                                return;
                            }
                        }

                        SketchShape sketchShape = SketchDesigner.getSketchShape(shapeNumber, elementType);

                        CutShape cutShape = sketchShape.getCutShape();
                        cutShape.setTranslateX(event.getX());
                        cutShape.setTranslateY(event.getY());
                        //this.getChildren().add(cutShape);
                        cutObjectsGroup.getChildren().add(cutShape);
                        cutDesigner.getCutShapesList().add(cutShape);
                        cutDesigner.usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));
                        for (CutShapeEdge cutShapeEdge : sketchShape.getCutShapeEdgesList()) {
                            if (cutShapeEdge == null || cutShapeEdge.getStartCoordinate() == null) continue;
                            //this.getChildren().add(cutShapeEdge);
                            cutObjectsGroup.getChildren().add(cutShapeEdge);
                            cutShapeEdge.setTranslateX(event.getX() + cutShapeEdge.getStartCoordinate().getX());
                            cutShapeEdge.setTranslateY(event.getY() + cutShapeEdge.getStartCoordinate().getY());
                            cutDesigner.getCutShapeEdgesList().add(cutShapeEdge);
                        }

                        refreshCutPaneView();
                    }

                } else if (db.getContent(CutDesigner.CELL_FORMAT).equals(TreeCellFeature.TREE_CELL_FORMAT)) {

                    int sketchShapeOwnerNumber = ((Integer) db.getContent(CutDesigner.SHAPE_OWNER_DF)).intValue();
                    int featureNumber = ((Integer) db.getContent(CutDesigner.FEATURE_NUMBER_DF)).intValue();

                    SketchShape shape = SketchDesigner.getSketchShape(sketchShapeOwnerNumber);
                    Sink sink = (Sink) shape.getFeatureByNumber(featureNumber);

                    if (sink != null) {
                        for (CutShapeAdditionalFeature cutShapeAdditionalFeature : sink.getCutShapes()) {
                            //if(this.getChildren().contains(cutShapeAdditionalFeature)) {
                            if (cutObjectsGroup.getChildren().contains(cutShapeAdditionalFeature)) {
                                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Элемент уже размещен"));
                                break;
                            }
                            //System.out.println("cutShapeAdditionalFeature = " + cutShapeAdditionalFeature);
                            //System.out.println("cutShapeAdditionalFeature = " + cutShapeAdditionalFeature + "polygon = " + cutShapeAdditionalFeature.getPolygon());
                            if (cutShapeAdditionalFeature != null && cutShapeAdditionalFeature.getPolygon() != null) {
                                //this.getChildren().add(cutShapeAdditionalFeature);
                                cutObjectsGroup.getChildren().add(cutShapeAdditionalFeature);
                                cutShapeAdditionalFeature.setTranslateX(event.getX() + cutShapeAdditionalFeature.getShiftCoordinate().getX());
                                cutShapeAdditionalFeature.setTranslateY(event.getY() + cutShapeAdditionalFeature.getShiftCoordinate().getY());
                                if (cutShapeAdditionalFeature.getRotateTransform().getAngle() != 0) {
                                    cutShapeAdditionalFeature.rotateShapeLocal(-cutShapeAdditionalFeature.getRotateTransform().getAngle());
                                }
                                // System.out.println(cutShapeAdditionalFeature.getChildren());
                                cutDesigner.getCutShapeAdditionalFeaturesList().add(cutShapeAdditionalFeature);

                            }
                        }
                    }

                    //System.out.println(this.getChildren());


                }

                hideHalfOfMaterialIfNotUsed();
            }
        });
    }


    private void initZoom() {
        cutDesigner.getScrollPaneWorkPane().setPannable(true);

        this.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                // System.out.println("scroll x = " + event.getDeltaX() + " y = " + event.getDeltaY());
                // System.out.println("workGroup.getScaleX() = " + cutObjectsGroup.getScaleX());
                if (event.getDeltaY() > 0) {
                    if (cutObjectsGroup.getScaleX() <= 15) {
                        setCutPaneScale(cutPaneScale + 0.1);
                    }
                } else if (event.getDeltaY() < 0) {
                    if (cutObjectsGroup.getScaleX() >= 0.3) {
                        setCutPaneScale(cutPaneScale - 0.1);
                    }
                }

                event.consume();
            }
        });
    }

    public static void setExternalStopAutoCutting(boolean value) {
        externalStopAutoCutting = value;
    }

    public static boolean isExternalStopAutoCutting() {
        return externalStopAutoCutting;
    }

    public void setCutPaneScale(double cutPaneScale) {
        if (cutPaneScale >= 0.3 && cutPaneScale <= 5) {
//            cutObjectsGroup.setScaleX(cutPaneScale);
//            cutObjectsGroup.setScaleY(cutPaneScale);
            scale.setX(cutPaneScale);
            scale.setY(cutPaneScale);

            this.cutPaneScale = cutPaneScale;


            for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {

                for (Material.MaterialSheet sheet : entry.getValue()) {
                    for (ConnectPoint connectPoint : sheet.getConnectPointArrayList()) {
                        connectPoint.changeSide(10.0 / (cutPaneScale));
                    }
                }
            }

            for (CutShape cutShape : cutDesigner.getCutShapesList()) {

                for (ConnectPoint connectPoint : cutShape.getConnectPoints()) {
                    connectPoint.changeSide(10.0 / (cutPaneScale));
                }
            }

            for (CutShapeEdge cutShapeEdge : cutDesigner.getCutShapeEdgesList()) {

                for (ConnectPoint connectPoint : cutShapeEdge.getConnectPoints()) {
                    connectPoint.changeSide(10.0 / (cutPaneScale));
                }
            }

        } else {
            return;
        }


        //CutDesigner.getScrollPaneWorkPane().setHmax(CutDesigner.getScrollPaneWorkPane().getHmax()*cutPaneScale);
        //CutDesigner.getScrollPaneWorkPane().setVmax(CutDesigner.getScrollPaneWorkPane().getVmax()*cutPaneScale);

//        double minX = cutObjectsGroup.getBoundsInParent().getMinX();
//        double minY = cutObjectsGroup.getBoundsInParent().getMinX();
//        for(Node node : cutObjectsGroup.getChildren()){
//            node.setTranslateX(node.getTranslateX() - minX);
//            node.setTranslateY(node.getTranslateY() - minY);
//        }
    }

    public static double getCutPaneScale() {
        return cutPaneScale;
    }

    public double getOriginalCutPaneHeight() {
        return originalCutPaneHeight;
    }

    public double getOriginalCutPaneWidth() {
        return originalCutPaneWidth;
    }

    public Material.MaterialSheet addMaterialSheet(String nameMaterial) {

        double hValue = cutDesigner.getScrollPaneWorkPane().getHvalue();
        double vValue = cutDesigner.getScrollPaneWorkPane().getVvalue();
        //nameMaterial = nameMaterial.split("-")[0];
        //System.out.println("add new sheet Material : " + nameMaterial);
        for (Material material : Project.getMaterials()) {
            String nameM = material.getName();
            String nameF = nameMaterial.split("#")[0];

            if (material.getName().equals(nameMaterial.split("#")[0])) {
                int depth = Integer.parseInt(nameMaterial.split("#")[1]);


                if (materialSheetsMap.get(nameMaterial) == null) {
                    materialSheetsMap.put(nameMaterial, new ArrayList<Material.MaterialSheet>());
                }

                Material.MaterialSheet materialSheet = null;

                //add additional sheet:
                boolean successAdded = false;
                if(material.isUseAdditionalSheets()){
                    for(Material.MaterialSheet msh : material.getAvailableAdditionalSheets()){
                        if(!materialSheetsMap.get(nameMaterial).contains(msh)){
                            materialSheet = msh;
                            materialSheetsMap.get(nameMaterial).add(materialSheet);
                            successAdded = true;
                            break;
                        }
                    }
                }

                //add main sheet:
                if(material.isUseMainSheets()){
                    if(!successAdded){
                        materialSheet = material.createMainMaterialSheet(depth);
                        materialSheetsMap.get(nameMaterial).add(materialSheet);
                        successAdded = true;
                    }
                }

                if(successAdded == false){
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Добавление листа");
//                    alert.setHeaderText("Добавление листа невозможно");
//                    alert.setContentText("Количество доступных листов закончилось");
//                    alert.show();
                }



                //refreshCutPaneView();
                refreshCutPaneView();

                return materialSheet;
            }
        }

        cutDesigner.getScrollPaneWorkPane().setHvalue(hValue);
        cutDesigner.getScrollPaneWorkPane().setVvalue(vValue);

        return null;
    }




    public ArrayList<Material.MaterialSheet> getUsedMaterialSheetsList() {

        ArrayList<Material.MaterialSheet> list = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {
            list.addAll(entry.getValue());
        }

        return list;
    }

    /**
     * Should bu invoked after auto cutting, returned only correct placed cutShapes !!!
     */
    public Map<Material.MaterialSheet, ArrayList<CutShape>> getSheetsAndShapesOnItMap() {

        Map<Material.MaterialSheet, ArrayList<CutShape>> resultMap = new LinkedHashMap<>();


        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {

            for (Material.MaterialSheet sheet : entry.getValue()) {

                for (CutShape cutShape : cutDesigner.getCutShapesList()) {

                    System.out.println("SHAPE Over Sheet:" + cutShape.getShapeNumber()
                            + ", cutShape.isCorrectPlaced() = " + cutShape.isCorrectPlaced()
                            + ", checkOverMaterialOrNot(cutShape, sheet) = " + checkOverMaterialOrNot(cutShape, sheet));

                    if (cutShape.isCorrectPlaced() && checkOverMaterialOrNot(cutShape, sheet)) {

                        ArrayList<CutShape> shapesList = resultMap.get(sheet);
                        if (shapesList == null) {
                            shapesList = new ArrayList<>();
                        }
                        shapesList.add(cutShape);
                        resultMap.put(sheet, shapesList);
                    }
                }

            }

        }

        return resultMap;
    }

    private void deleteMaterialSheet(String nameMaterial) {
        if(materialSheetsMap.get(nameMaterial) == null) return;
        materialSheetsMap.get(nameMaterial).remove(materialSheetsMap.get(nameMaterial).size() - 1);
        if (materialSheetsMap.get(nameMaterial).size() == 0) {
            materialSheetsMap.remove(nameMaterial);
        }
        refreshCutPaneView();
    }

    private void deleteMaterialSheet(Material.MaterialSheet materialSheet) {
        String mNameWithDeth = materialSheet.getMaterial().getName() + "#" + materialSheet.getSheetDepth();
        materialSheetsMap.get(mNameWithDeth).remove(materialSheetsMap.get(mNameWithDeth).size() - 1);
        if (materialSheetsMap.get(mNameWithDeth).size() == 0) {
            materialSheetsMap.remove(mNameWithDeth);
        }
        refreshCutPaneView();
    }

    public static Map<String, ArrayList<Material.MaterialSheet>> getMaterialSheetsMap() {
        return materialSheetsMap;
    }

    public void refreshCutPaneView() {
        //System.out.println("REFRESH CUT VIEW INTO");

        ArrayList<CutShapeAdditionalFeature> cutShapeAdditionalFeatureArrayList = new ArrayList<>();
        //for(Node node : this.getChildren()){
        for (Node node : cutObjectsGroup.getChildren()) {
            if (node instanceof CutShapeAdditionalFeature) {
                SketchShape sketchShape = ((CutShapeAdditionalFeature) node).getFeatureOwner().getSketchShapeOwner();
                if (SketchDesigner.getSketchShapesList().contains(sketchShape) && sketchShape.getFeaturesList().contains(((CutShapeAdditionalFeature) node).getFeatureOwner())) {
                    cutShapeAdditionalFeatureArrayList.add((CutShapeAdditionalFeature) node);
                } else {
                    cutDesigner.getCutShapeAdditionalFeaturesList().remove(node);
                }
            }
        }

        //this.getChildren().clear();
        cutObjectsGroup.getChildren().clear();
        //System.out.println("cutObjectsGroup.getChildren() = " + cutObjectsGroup.getChildren());
        //System.out.println(cutObjectsGroup.getChildren().toString());

        //check if materials deleted from project:
        ArrayList<String> keysForDelete = new ArrayList<>();

        ArrayList<Material.MaterialSheet> sheetsForDelete = new ArrayList<>();
        ArrayList<Material.MaterialSheet> sheetsForAdd = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {
            Material material = null;
            int depth = Integer.parseInt(entry.getKey().split("#")[1]);

            for (Material m : Project.getMaterials()) {
                if (m.getName().equals(entry.getKey().split("#")[0])) {
                    material = m;
                    break;
                }
            }
            if (material == null){
                keysForDelete.add(entry.getKey());
            }else{
                //check additional sheets
                if(material.isUseAdditionalSheets()){
                    int usesSheetsCount = entry.getValue().size();

                    if(usesSheetsCount < material.getAvailableAdditionalSheets().size()){

                        sheetsForDelete.addAll(entry.getValue());
                        for(int i =0;i< usesSheetsCount;i++){
                            sheetsForAdd.add(material.getAvailableAdditionalSheets().get(i));
                        }
                    }else{

                        for(Material.MaterialSheet materialSheet : entry.getValue()){
                            if(materialSheet.isAdditionalSheet())sheetsForDelete.add(materialSheet);
                        }

                        sheetsForAdd.addAll(material.getAvailableAdditionalSheets());

                    }
                }else{

                    for(Material.MaterialSheet sheet : entry.getValue()){
                        if(sheet.isAdditionalSheet()) sheetsForDelete.add(sheet);
                    }
//                    int size = entry.getValue().size();
//                    sheetsForDelete.addAll(entry.getValue());
//                    for(int i =0;i< size;i++){
//                        sheetsForAdd.add(material.createMainMaterialSheet(depth));
//                    }
                }

                if(!material.isUseMainSheets()){
                    for(Material.MaterialSheet materialSheet : entry.getValue()){
                        if(!materialSheet.isAdditionalSheet()){
                            sheetsForDelete.add(materialSheet);
                        }
                    }
                }

            }
        }

        for (String key : keysForDelete) {
            materialSheetsMap.remove(key);
        }

        for(Material.MaterialSheet materialSheet : sheetsForDelete){
            for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()){
                entry.getValue().remove(materialSheet);
            }
        }

        //Collections.reverse(sheetsForAdd);//need for correct sequence of adding sheets with different sizes
        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()){

            int index = 0;
            for(Material.MaterialSheet materialSheet : sheetsForAdd){
                if(entry.getKey().equals(materialSheet.getMaterial().getName() + "#" + materialSheet.getSheetDepth())){
                    entry.getValue().add(index++, materialSheet);
                }
                //System.out.println("materialSheet.getSheetHeight() = " + materialSheet.getSheetHeight());

            }
        }



        double lastY = 0;
        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {
            Material material = null;

            for (Material m : Project.getMaterials()) {
                if (m.getName().equals(entry.getKey().split("#")[0])) {
                    material = m;
                    break;
                }
            }
            if (material == null) continue;

            //create label
            int index = 0;
            double maxY = 0;

            Rectangle rectBackground = new Rectangle(10, (int) (42 + lastY), 400, 2);
            rectBackground.setFill(new Color(1, 1, 1, 0.2));
            //this.getChildren().add(rectBackground);
            cutObjectsGroup.getChildren().add(rectBackground);



            Button btnDel = new Button();
            btnDel.setId("btnDelMaterialSheet");
            btnDel.setPrefSize(30, 30);
            btnDel.setOnMouseClicked(event -> deleteMaterialSheet(entry.getKey()));
            btnDel.setTranslateX(110);
            btnDel.setTranslateY(10 + lastY);
            //this.getChildren().add(btnDel);
            cutObjectsGroup.getChildren().add(btnDel);

            Button btnAdd = new Button("Добавить");
            btnAdd.setId("btnAddMaterialSheet");
            btnAdd.setPrefSize(94, 30);
            btnAdd.setOnMouseClicked(event -> addMaterialSheet(entry.getKey()));
            btnAdd.setTranslateX(10);
            btnAdd.setTranslateY(10 + lastY);
            //this.getChildren().add(btnAdd);
            cutObjectsGroup.getChildren().add(btnAdd);

            Label labelMaterial = new Label(entry.getKey().split("#")[0].split("\\$")[1] +
                    " - " +
                    entry.getKey().split("#")[0].split("\\$")[entry.getKey().split("#")[0].split("\\$").length - 5] +
                    " - " +
                    entry.getKey().split("#")[1] +
                    "мм");
            labelMaterial.getStyleClass().add("labelSheetName");
            labelMaterial.setAlignment(Pos.CENTER_LEFT);
            labelMaterial.setPrefHeight(30);
            labelMaterial.setPrefWidth(300);
            labelMaterial.setTranslateX(10);
            labelMaterial.setTranslateY(40 + lastY);
            //this.getChildren().add(labelMaterial);
            cutObjectsGroup.getChildren().add(labelMaterial);

            lastY += 60;

            double previousX = 10;
            double maxHeight = 0;
            double maxWidth = 5000 * Project.getCommonShapeScale() * 2 + 50;
            for (Material.MaterialSheet ms : entry.getValue()){
                if(maxHeight<ms.getSheetHeight())maxHeight = ms.getSheetHeight();
            }
            maxHeight *= Project.getCommonShapeScale();

            //System.out.println("cutObjectsGroup.getChildren() = " + cutObjectsGroup.getChildren());
            //System.out.println("entry.getValue() = " + entry.getValue());

            for (Material.MaterialSheet ms : entry.getValue()) {
                Material.MaterialSheet materialSheet = ms;

                //double zoneWidth = materialSheet.getMaterial().getMaterialWidth()*2*ProjectHandler.getCommonShapeScale();

                if(cutObjectsGroup.getChildren().contains(materialSheet)){
                    //System.out.println("DUPLICATED materialSheet = " + materialSheet);
                    //System.out.println("cutObjectsGroup.getChildren() = " + cutObjectsGroup.getChildren());

                    //continue;
                }
                cutObjectsGroup.getChildren().add(materialSheet);

                if (this.getPrefWidth() < material.getMaterialWidth() * Project.getCommonShapeScale() * 2 + 50) {
                    originalCutPaneWidth = (material.getMaterialWidth() * Project.getCommonShapeScale() * 2 + 50);
                    this.setPrefWidth(material.getMaterialWidth() * Project.getCommonShapeScale() * 2 + 50);
                    this.setMinWidth(material.getMaterialWidth() * Project.getCommonShapeScale() * 2 + 50);
                }
                //System.out.println("originalCutPaneWidth = " + maxWidth);
                //System.out.println("previousX + ms.getSheetWidth()*ProjectHandler.getCommonShapeScale() = " + (previousX + ms.getSheetWidth()*ProjectHandler.getCommonShapeScale()));
                if(previousX + ms.getSheetWidth()* Project.getCommonShapeScale() > maxWidth){
                    previousX = 10;
                    lastY += maxHeight + 50;
                    ms.setTranslateX(previousX);
                    ms.setTranslateY(lastY+10);
                }else{
                    ms.setTranslateX(previousX);
                    ms.setTranslateY(lastY+10);
                }
                //ms.setTranslateX((index % 2 == 0) ? 10 : (material.getMaterialWidth() * ProjectHandler.getCommonShapeScale() + 20));
               // ms.setTranslateY((index / 2) * (material.getMaterialHeight() * ProjectHandler.getCommonShapeScale() + 10) + 10 + lastY);
                previousX += ms.getSheetWidth()* Project.getCommonShapeScale() + 10;
                index++;

//                System.out.println("ms.getBoundsInParent().getMaxY() = " + ms.getBoundsInParent().getMaxY());
//                System.out.println("this.getPrefHeight() = " + this.getPrefHeight());
                if (ms.getBoundsInParent().getMaxY() > this.getPrefHeight()) {
                    originalCutPaneHeight = ms.getBoundsInParent().getMaxY() + 10;
                    this.setPrefHeight(ms.getBoundsInParent().getMaxY() + 10);
                    //this.setHeight(ms.getBoundsInParent().getMaxY() + 10);
                    this.setMinHeight(ms.getBoundsInParent().getMaxY() + 10);
                }

                if (ms.getBoundsInParent().getMaxX() > this.getPrefWidth()) {
                    originalCutPaneWidth = ms.getBoundsInParent().getMaxX() + 10;
                    this.setPrefWidth(ms.getBoundsInParent().getMaxX() + 10);
                    //this.setWidth(ms.getBoundsInParent().getMaxX() + 10);
                    this.setMinWidth(ms.getBoundsInParent().getMaxX() + 10);
                }
                maxY = ms.getBoundsInParent().getMaxY();

            }
            lastY += maxHeight + 50;
        }


        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            //if(!cutShape.isContainInUnion())cutShape.refreshShapeView();
            cutShape.refreshShapeView();
        }

        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            //this.getChildren().add(cutShape);
            cutObjectsGroup.getChildren().add(cutShape);
            //cutShape.toFront();

            for (CutShapeEdge cutShapeEdge : cutShape.getCutShapeEdgesList()) {

                if (cutShapeEdge == null || cutShapeEdge.getStartCoordinate() == null) {
                    cutDesigner.getCutShapeEdgesList().remove(cutShapeEdge);
                    continue;
                }

                //this.getChildren().add(cutShapeEdge);
                cutObjectsGroup.getChildren().add(cutShapeEdge);
                if (!cutDesigner.getCutShapeEdgesList().contains(cutShapeEdge)) {

                    double saveAngleShape = cutShape.getRotateAngle();
                    double saveAngleEdge = cutShapeEdge.getRotateAngle();
                    cutShape.rotateShapeLocal(-saveAngleShape);
                    cutShapeEdge.rotateShapeLocal(-saveAngleEdge);
                    System.out.println("saveAngleEdge = " + saveAngleEdge);

                    cutShapeEdge.setTranslateX(cutShape.getTranslateX() + cutShapeEdge.getStartCoordinate().getX());
                    cutShapeEdge.setTranslateY(cutShape.getTranslateY() + cutShapeEdge.getStartCoordinate().getY());

                    cutShape.rotateShapeLocal(saveAngleShape);
                    cutShapeEdge.rotateShapeGlobal(saveAngleShape, cutShape.getGlobalCenter());

                    cutDesigner.getCutShapeEdgesList().add(cutShapeEdge);
                }


            }

        }


        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            //if(!cutShape.isContainInUnion())cutShape.refreshShapeView();
            cutShape.refreshShapeView();
            //System.out.println("cutShape.isContainInUnion() = " + cutShape.isContainInUnion());
        }
//        for(CutShapeEdge cutShapeEdge : CutDesigner.getCutShapeEdgesList()){
//            this.getChildren().add(cutShapeEdge);
//            //cutShapeEdge.toFront();
//        }

        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            //cutShape.refreshShapeView();
            cutShape.checkCorrectPlaceOrNot();
        }
        for (CutShapeEdge cutShapeEdge : cutDesigner.getCutShapeEdgesList()) {
            //cutShape.refreshShapeView();
            cutShapeEdge.checkCorrectPlaceOrNot();
        }

        //refresh features:


        for (CutShapeAdditionalFeature feature : cutShapeAdditionalFeatureArrayList) {
            //this.getChildren().add(feature);
            cutObjectsGroup.getChildren().add(feature);
        }


        hideHalfOfMaterialIfNotUsed();
        for (CutShapeAdditionalFeature cutShapeAdditionalFeature : cutDesigner.getCutShapeAdditionalFeaturesList()) {
            cutShapeAdditionalFeature.checkCorrectPlaceOrNot();
        }


        //Platform.runLater(()->ru.koreanika.cutDesigner.updateStatistics());

    }

    public static Thread getAutoCutThread() {
        return autoCutThread;
    }

    public void startAutoCutting(boolean invokeFromReceipt) {

        //double savedScale = getCutPaneScale();
        setCutPaneScale(1.0);
        externalStopAutoCutting = false;

        cuttingProgressDialog = new LoadingProgressDialog(Main.getMainScene());
        cuttingProgressDialog.show();

        cutDesigner.getRootGroup().getChildren().remove(cutDesigner.getCutPane());
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: transparent;");
        pane.setPrefSize(this.getPrefWidth(), this.getPrefHeight());
        cutDesigner.getRootGroup().getChildren().add(pane);
        autoCutThread = new Thread(new Runnable() {
            @Override
            public void run() {

                autoCutting();
                //CutDesigner.getRootGroup().getChildren().add(this);

                Platform.runLater(() -> {
                    cutDesigner.getRootGroup().getChildren().remove(pane);
                    cutDesigner.getRootGroup().getChildren().add(cutDesigner.getCutPane());
                    cuttingProgressDialog.close();
                    cutDesigner.updateStatistics();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println("STOP AUTOCUTTING");


                        e.printStackTrace();
                    }

                    if (invokeFromReceipt) {
                        Main.getMainWindow().showReceipt();
                    } else {
                        if (Boolean.parseBoolean(Main.getProperty("autosave.afterCut"))) {
                            projectHandler.saveProject();
                        }
                    }

                    cutDesigner.updateStatistics();
                });
            }
        });
        //autoCutting();
        autoCutThread.start();

    }

    ArrayList<String> sheetDeleting = new ArrayList<>();
    ArrayList<CutObject> deleteList = new ArrayList<>();
    ArrayList<CutShapeAdditionalFeature> cutShapeAdditionalFeaturesForPlacing = new ArrayList<>();
    ArrayList<CutShape> cutShapesForPlacing = new ArrayList<>();
    ArrayList<CutShapeEdge> cutShapeEdgesForPlacing = new ArrayList<>();
    ArrayList<CutShapeUnion> cutShapeUnionForPlacing = new ArrayList<>();

    public void prepareCutPaneForAutoCutting(){
        sheetDeleting.clear();
        deleteList.clear();
        cutShapeAdditionalFeaturesForPlacing.clear();
        cutShapesForPlacing.clear();
        cutShapeEdgesForPlacing.clear();
        cutShapeUnionForPlacing.clear();



        /** DELETE ALL SHEETS START */
        System.out.println("************************ materialSheetsMap = " + materialSheetsMap.toString());
        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {

            for (Material.MaterialSheet sheet : entry.getValue()) {
                System.out.println("************************ sheetDeleting = " + sheet.isActualPrice());
               if(sheet.isActualPrice()) sheetDeleting.add(entry.getKey());
            }
        }
        for (String name : sheetDeleting) {
            deleteMaterialSheet(name);
        }


        /** DELETE ALL SHEETS END */



        //remove all objects which placed incorrect:
        for (CutObject cutObject : cutDesigner.getCutShapesList()) {
            //if (!cutObject.isCorrectPlaced()) {
                deleteList.add(cutObject);
            //}
        }
        for (CutObject cutObject : cutDesigner.getCutShapeAdditionalFeaturesList()) {
            if (!cutObject.isCorrectPlaced()) {
                deleteList.add(cutObject);
            }
        }

        System.out.println("create deleteList for Shapes size = " + deleteList.size());
        for (CutObject cutObject : cutDesigner.getCutShapeEdgesList()) {
            if (!cutObject.isCorrectPlaced()) {
                deleteList.add(cutObject);
            }
        }

        for (CutObject cutObject : deleteList) {

            deleteCutShape(cutObject);

            if (cutObject.isContainInUnion()) {
                System.out.println("DELETE UNIION");
                for (CutShape cutShape : cutObject.getCutShapeUnionOwner().getCutShapesInUnionList()) {
                    deleteCutShape(cutShape);
                }
            }
        }
        System.out.println("DELETE CUT SHAPES");

        System.out.println("NEXT STEP");

        // create list for AdditionalFeatures which should be placed on CutPane:

        for (SketchShape shape : SketchDesigner.getSketchShapesList()) {
            for (AdditionalFeature feature : shape.getFeaturesList()) {
                if (feature instanceof Sink) {
                    Sink sink = (Sink) feature;
                    if (sink.isCuttable()) {
                        for (CutShapeAdditionalFeature feature1 : sink.getCutShapes()) {
                            //if(!this.getChildren().contains(feature1)){
                            if (!cutObjectsGroup.getChildren().contains(feature1)) {
                                cutShapeAdditionalFeaturesForPlacing.add(feature1);
                            }
                        }

                    }
                }
            }
        }
        System.out.println("create cutShapeAdditionalFeaturesForPlacing list size = " + cutShapeAdditionalFeaturesForPlacing.size());

        // create list for shapes which should be placed on CutPane:

        for (SketchShape shape : SketchDesigner.getSketchShapesList()) {
            CutShape cutShape = shape.getCutShape();
            if (shape.getCutShape() == null) {
                cutShape = shape.getCutShape();
            }
            if (!cutDesigner.getCutShapesList().contains(cutShape)) {
                if (!shape.isContainInUnion()) {
                    cutShapesForPlacing.add(cutShape);
                }
            }
        }
        System.out.println("create cutShapesForPlacing list size = " + cutShapesForPlacing.size());

        // create list for shapeEdges which should be placed on CutPane:
        for (CutShape cutShape : cutShapesForPlacing) {
            if (!cutShape.isSaveMaterialImage()) {
                for (CutShapeEdge edge : cutShape.getCutShapeEdgesList()) {
                    if (edge != null && edge.getStartCoordinate() != null) {
                        if (!edge.isContainInUnion()) {
                            cutShapeEdgesForPlacing.add(edge);
                        }

                    }
                }
            }
        }
        System.out.println("create cutShapeEdgesForPlacing : " + cutShapeEdgesForPlacing.size());

        //create union forCutShape:

        for (SketchShapeUnion sketchShapeUnion : SketchDesigner.getSketchShapeUnionsList()) {
            if (!cutDesigner.usedShapeUnionsNumberList.contains(sketchShapeUnion.getUnionNumber())) {
                cutShapeUnionForPlacing.add(new CutShapeUnion(sketchShapeUnion));
            }
        }
        System.out.println("create cutShapeUnionForPlacing : " + cutShapeUnionForPlacing.size());

    }

    public double getEffectiveCutCoefficient(){

        //get all shapes squares
        double shapesSquare = 0.0;
        for (CutObject cutObject : cutDesigner.getCutShapesList()) {
            if(cutObject instanceof CutShape){
                CutShape cutShape = (CutShape) cutObject;
                shapesSquare += cutShape.getShapeSquare()/Math.pow(Project.getCommonShapeScale(), 2);
            }
        }

        refreshCutPaneView();

        //get all sheets square
        double sheetsSquare = 0.0;
        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {
            for (Material.MaterialSheet sheet : entry.getValue()) {

                sheetsSquare += sheet.getSheetSquare()* (sheet.getUsesSlabs());
            }
        }

        shapesSquare /= 1000000;//in meters
        sheetsSquare /= 1000000;//in meters

        System.out.println("shapesSquare = " + shapesSquare);
        System.out.println("sheetsSquare = " + sheetsSquare);


        return shapesSquare/sheetsSquare;
    }

    public void autoCutting() {

        //if only one shape incorrect start autoCut:
        boolean onlyOneIncorrect = false;
        for (CutObject cutObject : cutDesigner.getCutShapesList()) {
            if (!cutObject.isCorrectPlaced()) {
                onlyOneIncorrect = true;
                break;
            }
        }
        if(!onlyOneIncorrect){
            return;
        }




        double progressStep = 0;


        /*** NEW TYPE OF PACKING ***/



        ArrayList<Double> resultCoefficientsOfCutting = new ArrayList<>();
        {
            ArrayList<CutShape> allCutShapes = new ArrayList<>();
            for (SketchShape shape : SketchDesigner.getSketchShapesList()) {
//                CutShape cutShape = shape.getCutShape();
//                if (shape.getCutShape() == null) {
//                    cutShape = shape.getCutShape();
//                }
                allCutShapes.add(shape.getCutShape());
            }
//            System.out.println("allCutShapes = " + allCutShapes.toString());

            //sorting by sheets types and shapes
            LinkedHashMap<String, ArrayList<CutShape>> sheetsTypeAndCutShapes = new LinkedHashMap<>();

            //add sheets with old prices
            for(Material.MaterialSheet oldSheet : getUsedMaterialSheetsList()){
                String sheetType = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                sheetsTypeAndCutShapes.put(sheetType, new ArrayList<CutShape>());
            }

            for(CutShape cutShape : allCutShapes){
                String sheetType = cutShape.getMaterial().getName() + "#" + cutShape.getDepth();

                ArrayList<CutShape> cutShapesList = sheetsTypeAndCutShapes.get(sheetType);
                if(cutShapesList == null){
                    cutShapesList = new ArrayList<>();
                }
                cutShapesList.add(cutShape);
                sheetsTypeAndCutShapes.put(sheetType, cutShapesList);
            }

//            System.out.println("sheetsTypeAndCutShapes = " + sheetsTypeAndCutShapes.toString());


            double numberOfCuttingTypes = 16.0;


            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 1); cuttingProgressDialog.setValue(1.0/numberOfCuttingTypes);});
            //pack type 1, sort by height
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size()
                        + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
//                System.out.println("oldSheets = " + oldSheets);

                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;

//                        System.out.println("NEED SHEET NAME = " + entry.getKey());
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
//                            System.out.println("OLD SHEET NAME = " + sheetName);

                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
//                                System.out.println("break");
                                break;
                            }
                        }

//                        System.out.println("newSheet = " + newSheet);

                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeOne(newSheet, remainderCutShapes, new SortCutShapesByHeight());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 2);cuttingProgressDialog.setValue(2.0/numberOfCuttingTypes);});
            //pack type 1, sort by width
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeOne(newSheet, remainderCutShapes, new SortCutShapesByWidth());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 3);cuttingProgressDialog.setValue(3.0/numberOfCuttingTypes);});
            //pack type 1, sort by Square
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeOne(newSheet, remainderCutShapes, new SortCutShapesBySquare());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 4);cuttingProgressDialog.setValue(4.0/numberOfCuttingTypes);});
            //pack type 1, sort by Perimeter
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeOne(newSheet, remainderCutShapes, new SortCutShapesByPerimeter());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }




            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 5);cuttingProgressDialog.setValue(5.0/numberOfCuttingTypes);});
            //pack type 2, sort by height
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());


                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        System.out.println("Cutshapes on Pane before:");
                        for(Node n : cutObjectsGroup.getChildren()){
                            if(n instanceof CutShape){
                                System.out.print(" " + n);
                            }
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
//                            for(CutShape cutShape : entry.getValue()){
                            for(CutShape cutShape : remainderCutShapes){
                                System.out.println("ERROR Cutshape: " + cutShape);
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeTwo(newSheet, remainderCutShapes, new SortCutShapesByHeight());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 6);cuttingProgressDialog.setValue(6.0/numberOfCuttingTypes);});
            //pack type 2, sort by width
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeTwo(newSheet, remainderCutShapes, new SortCutShapesByWidth());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 7);cuttingProgressDialog.setValue(7.0/numberOfCuttingTypes);});
            //pack type 2, sort by Square
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeTwo(newSheet, remainderCutShapes, new SortCutShapesBySquare());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 8);cuttingProgressDialog.setValue(8.0/numberOfCuttingTypes);});
            //pack type 2, sort by Perimeter
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeTwo(newSheet, remainderCutShapes, new SortCutShapesByPerimeter());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }



            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 9);cuttingProgressDialog.setValue(9.0/numberOfCuttingTypes);});
            //pack type 3, sort by height
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeThree(newSheet, remainderCutShapes, new SortCutShapesByHeight());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 10);cuttingProgressDialog.setValue(10.0/numberOfCuttingTypes);});
            //pack type 3, sort by width
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeThree(newSheet, remainderCutShapes, new SortCutShapesByWidth());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 11);cuttingProgressDialog.setValue(11.0/numberOfCuttingTypes);});
            //pack type 3, sort by Square
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeThree(newSheet, remainderCutShapes, new SortCutShapesBySquare());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 12);cuttingProgressDialog.setValue(12.0/numberOfCuttingTypes);});
            //pack type 3, sort by Perimeter
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeThree(newSheet, remainderCutShapes, new SortCutShapesByPerimeter());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }



            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 13);cuttingProgressDialog.setValue(13.0/numberOfCuttingTypes);});
            //pack type 4, sort by height
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeFour(newSheet, remainderCutShapes, new SortCutShapesByHeight());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 14);cuttingProgressDialog.setValue(14.0/numberOfCuttingTypes);});
            //pack type 4, sort by width
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeFour(newSheet, remainderCutShapes, new SortCutShapesByWidth());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 15);cuttingProgressDialog.setValue(15.0/numberOfCuttingTypes);});
            //pack type 4, sort by Square
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeFour(newSheet, remainderCutShapes, new SortCutShapesBySquare());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }
            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Вычисление раскроя " + 16);cuttingProgressDialog.setValue(16.0/numberOfCuttingTypes);});
            //pack type 4, sort by Perimeter
            {

                prepareCutPaneForAutoCutting();

                progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                    ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                    while(true){
                        Material.MaterialSheet newSheet = null;
                        for(Material.MaterialSheet oldSheet : oldSheets){
                            String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();
                            if(sheetName.equals(entry.getKey())){
                                newSheet = oldSheet;
                                break;
                            }
                        }
                        if(newSheet != null){
                            oldSheets.remove(newSheet);
                        }else{
                            newSheet = addMaterialSheet(entry.getKey());
                        }

                        if(newSheet == null) {
                            System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                            for(CutShape cutShape : remainderCutShapes){
                                cutObjectsGroup.getChildren().add(cutShape);
                                cutDesigner.getCutShapesList().add(cutShape);
                                cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                            }
                            break;
                        }else{
                            remainderCutShapes = CutShapesPacking.packTypeFour(newSheet, remainderCutShapes, new SortCutShapesByPerimeter());
                            //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                            //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                            for(CutShape cutShape : entry.getValue()){
                                if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                }
                            }

                            if(remainderCutShapes.size() == 0) break;
                        }
                    }
                }

                resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
            }


            System.out.println("resultCoefficientsOfCutting = " + resultCoefficientsOfCutting.toString());

            Double maxCoeff = Collections.max(resultCoefficientsOfCutting);
            System.out.println("maxCoeff = " + maxCoeff);
            int maxCoeffIndex = resultCoefficientsOfCutting.indexOf(maxCoeff);
            System.out.println("maxCoeffIndex = " + maxCoeffIndex);

            Platform.runLater(() -> {cuttingProgressDialog.setMessage("Выбран раскрой " + (maxCoeffIndex+1));cuttingProgressDialog.setValue(cuttingProgressDialog.getValue() + 12/numberOfCuttingTypes);});

            if(maxCoeffIndex == 0){
                //TYPE 1, sort by height

                //pack type 1, sort by height
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeOne(newSheet, remainderCutShapes, new SortCutShapesByHeight());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 1){
                //TYPE 1, sort by width

                //pack type 1, sort by width
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeOne(newSheet, remainderCutShapes, new SortCutShapesByWidth());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }

            }else if(maxCoeffIndex == 2){
                //TYPE 1, sort by square

                //pack type 1, sort by Square
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeOne(newSheet, remainderCutShapes, new SortCutShapesBySquare());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 3){
                //TYPE 1, sort by perimeter

                //pack type 1, sort by Perimeter
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeOne(newSheet, remainderCutShapes, new SortCutShapesByPerimeter());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 4){
                //TYPE 2, sort by height

                //pack type 2, sort by height
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeTwo(newSheet, remainderCutShapes, new SortCutShapesByHeight());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 5){
                //TYPE 2, sort by width

                //pack type 2, sort by width
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeTwo(newSheet, remainderCutShapes, new SortCutShapesByWidth());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 6){
                //TYPE 2, sort by square

                //pack type 2, sort by Square
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeTwo(newSheet, remainderCutShapes, new SortCutShapesBySquare());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 7){
                //TYPE 2, sort by perimeter

                //pack type 2, sort by Perimeter
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeTwo(newSheet, remainderCutShapes, new SortCutShapesByPerimeter());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 8){
                //TYPE 3, sort by height

                //pack type 3, sort by height
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeThree(newSheet, remainderCutShapes, new SortCutShapesByHeight());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 9){
                //TYPE 3, sort by width

                //pack type 3, sort by width
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeThree(newSheet, remainderCutShapes, new SortCutShapesByWidth());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 10){
                //TYPE 3, sort by square

                //pack type 3, sort by Square
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for (Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()) {

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while (true) {
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if (newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for (CutShape cutShape : remainderCutShapes) {
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            } else {
                                remainderCutShapes = CutShapesPacking.packTypeThree(newSheet, remainderCutShapes, new SortCutShapesBySquare());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for (CutShape cutShape : entry.getValue()) {
                                    if (!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)) {

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if (remainderCutShapes.size() == 0) break;
                            }
                        }
                    }
                }

            }else if(maxCoeffIndex == 11){
                //TYPE 3, sort by perimeter
                //pack type 3, sort by Perimeter
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for (Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()) {

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while (true) {
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if (newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for (CutShape cutShape : remainderCutShapes) {
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            } else {
                                remainderCutShapes = CutShapesPacking.packTypeThree(newSheet, remainderCutShapes, new SortCutShapesByPerimeter());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for (CutShape cutShape : entry.getValue()) {
                                    if (!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)) {

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if (remainderCutShapes.size() == 0) break;
                            }
                        }
                    }
                }
            }else if(maxCoeffIndex == 12){
                //TYPE 4, sort by height
                //pack type 4, sort by height
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeFour(newSheet, remainderCutShapes, new SortCutShapesByHeight());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 13){
                //TYPE 4, sort by width
                //pack type 4, sort by width
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeFour(newSheet, remainderCutShapes, new SortCutShapesByWidth());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 14){
                //TYPE 4, sort by square
                //pack type 4, sort by Square
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeFour(newSheet, remainderCutShapes, new SortCutShapesBySquare());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }else if(maxCoeffIndex == 15){
                //TYPE 4, sort by perimeter
                //pack type 4, sort by Perimeter
                {

                    prepareCutPaneForAutoCutting();

                    progressStep = 1.0 / (cutShapeEdgesForPlacing.size() + cutShapesForPlacing.size() + cutShapeAdditionalFeaturesForPlacing.size());

                    ArrayList<Material.MaterialSheet> oldSheets = new ArrayList<>(getUsedMaterialSheetsList());
                    for(Map.Entry<String, ArrayList<CutShape>> entry : sheetsTypeAndCutShapes.entrySet()){

                        ArrayList<CutShape> remainderCutShapes = new ArrayList<>(entry.getValue());
                        while(true){
                            Material.MaterialSheet newSheet = null;
                            for(Material.MaterialSheet oldSheet : oldSheets){
                                String sheetName = oldSheet.getMaterial().getName() + "#" + oldSheet.getSheetDepth();

                                if(sheetName.equals(entry.getKey())){
                                    newSheet = oldSheet;
                                    break;
                                }
                            }
                            if(newSheet != null){
                                oldSheets.remove(newSheet);
                            }else{
                                newSheet = addMaterialSheet(entry.getKey());
                            }

                            if(newSheet == null) {
                                System.out.println("NO AVAILABLE SHEETS FOR ADDING : " + entry.getKey());
                                for(CutShape cutShape : remainderCutShapes){
                                    cutObjectsGroup.getChildren().add(cutShape);
                                    cutDesigner.getCutShapesList().add(cutShape);
                                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                                }
                                break;
                            }else{
                                remainderCutShapes = CutShapesPacking.packTypeFour(newSheet, remainderCutShapes, new SortCutShapesByPerimeter());
                                //remainderCutShapes = CutShapesPacking.packTypeHorizontal(newSheet, remainderCutShapes);
                                //remainderCutShapes = CutShapesPacking.packTypeVertical(newSheet, remainderCutShapes);
                                for(CutShape cutShape : entry.getValue()){
                                    if(!remainderCutShapes.contains(cutShape) && !cutObjectsGroup.getChildren().contains(cutShape)){

                                        cutObjectsGroup.getChildren().add(cutShape);
                                        cutDesigner.getCutShapesList().add(cutShape);
                                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());

                                    }
                                }

                                if(remainderCutShapes.size() == 0) break;
                            }
                        }
                    }

                    resultCoefficientsOfCutting.add(getEffectiveCutCoefficient());
                }
            }

        }

        Platform.runLater(() -> {
            cuttingProgressDialog.close();
        });

        refreshCutPaneView();

        if(true)return;










        /** SORTING cutShapeEdgesForPlacing FROM BIG TO SMALL START */
        {
            boolean run = true;
            while (run) {
                run = false;
                for (int i = 0; i < cutShapeEdgesForPlacing.size() - 1; i++) {
                    if (cutShapeEdgesForPlacing.get(i).getEdgeSquare() < cutShapeEdgesForPlacing.get(i + 1).getEdgeSquare()) {
                        Collections.swap(cutShapeEdgesForPlacing, i, i + 1);
                        run = true;
                    }
                }
            }
        }
        /** SORTING cutShapeEdgesForPlacing FROM BIG TO SMALL END*/
        /** SORTING cutShapesForPlacing FROM BIG TO SMALL START */
        {
            boolean run = true;
            while (run) {
                run = false;
                for (int i = 0; i < cutShapesForPlacing.size() - 1; i++) {
                    if (cutShapesForPlacing.get(i).getShapeSquare() < cutShapesForPlacing.get(i + 1).getShapeSquare()) {
                        Collections.swap(cutShapesForPlacing, i, i + 1);
                        run = true;
                    }
                }
            }
        }

        //sort by Material after sorting by size:
        {
            Map<Material, ArrayList<CutShape>> materialAndShapes = new LinkedHashMap<>();
            for (CutShape cutShape : cutShapesForPlacing) {

                ArrayList<CutShape> cutShapes = materialAndShapes.get(cutShape.getMaterial());
                if (cutShapes == null) {
                    cutShapes = new ArrayList<>();
                }
                cutShapes.add(cutShape);
                materialAndShapes.put(cutShape.getMaterial(), cutShapes);

            }
            cutShapesForPlacing.clear();
            for (Map.Entry<Material, ArrayList<CutShape>> entry : materialAndShapes.entrySet()) {
                for (CutShape cutShape : entry.getValue()) {
                    cutShapesForPlacing.add(cutShape);
                }
            }
        }

        /** SORTING cutShapesForPlacing FROM BIG TO SMALL END*/
        /** SORTING cutShapeAdditionalFeature FROM BIG TO SMALL START */
        {
            boolean run = true;
            while (run) {
                run = false;
                for (int i = 0; i < cutShapeAdditionalFeaturesForPlacing.size() - 1; i++) {
                    if (cutShapeAdditionalFeaturesForPlacing.get(i).getShapeSquare() < cutShapeAdditionalFeaturesForPlacing.get(i + 1).getShapeSquare()) {
                        Collections.swap(cutShapeAdditionalFeaturesForPlacing, i, i + 1);
                        run = true;
                    }
                }
            }
        }
        /** SORTING cutShapeAdditionalFeature FROM BIG TO SMALL END*/

        //start cutting cutEdges: (at first because addSheet() invoke refresh and add wrong edges automatic)
        for (CutShapeEdge cutShapeEdge : cutShapeEdgesForPlacing) {

            Platform.runLater(() -> {
                cuttingProgressDialog.setMessage("Размещение кромки №" + cutShapeEdge.getOwner().getShapeNumber());
            });

            System.out.println("\nTry to cutting EDGE for Shape#" + cutShapeEdge.getOwner().getShapeNumber());
            //check is there material sheet:
            ArrayList<Material.MaterialSheet> compatibleSheets = new ArrayList<>();
            for (Material.MaterialSheet sheet : getUsedMaterialSheetsList()) {
                if (cutShapeEdge.getOwner().getMaterial() == sheet.getMaterial() && cutShapeEdge.getOwner().getDepth() == sheet.getDepth()) {
                    //add compatible sheet:
                    compatibleSheets.add(sheet);
                }
            }
            int result = 0;
            if (compatibleSheets.size() != 0) {
                //have compatible sheet, try to auto cutting:
                System.out.println("have compatible sheets");
                for (Material.MaterialSheet sheet : compatibleSheets) {
                    //try to cutting with this sheet:
                    //result = autoCutOnMaterialSheet(sheet, cutShapeEdge);
                    result = autoCutOnMaterialSheet(sheet, cutShapeEdge);

                    if (result == 1) break;
                }
                if (result == 0) {
                    System.out.println("can't cutting on compatible sheets");
                    Material.MaterialSheet newSheet = addMaterialSheet(cutShapeEdge.getOwner().getMaterial().getName() + "#" + cutShapeEdge.getOwner().getDepth());
                    result = autoCutOnMaterialSheet(newSheet, cutShapeEdge);
                }
            } else {
                //no compatible sheet, try to create:
                System.out.println("No compatible sheets");
                Material.MaterialSheet newSheet = addMaterialSheet(cutShapeEdge.getOwner().getMaterial().getName() + "#" + cutShapeEdge.getOwner().getDepth());
                result = autoCutOnMaterialSheet(newSheet, cutShapeEdge);
            }
            System.out.println("Result of cutting : " + result);

            double finalProgressStep1 = progressStep;
            Platform.runLater(() -> {
                cuttingProgressDialog.setValue(cuttingProgressDialog.getValue() + finalProgressStep1);
            });
        }


        //start cutting cutShapes :
        for (CutShape cutShape : cutShapesForPlacing) {

            Platform.runLater(() -> {
                cuttingProgressDialog.setMessage("Размещение фигуры №" + cutShape.getShapeNumber());
            });


            System.out.println("\nTry to cutting shape #" + cutShape.getShapeNumber());

            //check is there material sheet:
            ArrayList<Material.MaterialSheet> compatibleSheets = new ArrayList<>();
            for (Material.MaterialSheet sheet : getUsedMaterialSheetsList()) {
                if (cutShape.getMaterial() == sheet.getMaterial() && cutShape.getDepth() == sheet.getDepth()) {
                    //add compatible sheet:
                    compatibleSheets.add(sheet);
                }
            }
            int result = 0;
            if (compatibleSheets.size() != 0) {
                //have compatible sheet, try to auto cutting:
                System.out.println("have compatible sheets");

                for (Material.MaterialSheet sheet : compatibleSheets) {
                    //try to cutting with this sheet:
                    result = autoCutOnMaterialSheet(sheet, cutShape);

                    if (result == 1) break;


                }
                if (result == 0) {
                    System.out.println("can't cutting on compatible sheets");
                    Material.MaterialSheet newSheet = addMaterialSheet(cutShape.getMaterial().getName() + "#" + cutShape.getDepth());
                    if(newSheet == null) {
                        result = 0;
                        System.out.println("NO AVAILABLE SHEETS FOR ADDING");
                        cutObjectsGroup.getChildren().add(cutShape);
                        cutDesigner.getCutShapesList().add(cutShape);
                        cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                    }else{
                        result = autoCutOnMaterialSheet(newSheet, cutShape);
                    }


                }

            } else {
                //no compatible sheet, try to create:
                System.out.println("No compatible sheets");
                Material.MaterialSheet newSheet = addMaterialSheet(cutShape.getMaterial().getName() + "#" + cutShape.getDepth());
                if(newSheet == null) {
                    result = 0;
                    System.out.println("NO AVAILABLE SHEETS FOR ADDING");
                    cutObjectsGroup.getChildren().add(cutShape);
                    cutDesigner.getCutShapesList().add(cutShape);
                    cutDesigner.usedShapesNumberList.add(cutShape.getShapeNumber());
                }else{
                    result = autoCutOnMaterialSheet(newSheet, cutShape);
                }



            }
            System.out.println("Result of cutting : " + result);


            double finalProgressStep = progressStep;
            Platform.runLater(() -> {
                cuttingProgressDialog.setValue(cuttingProgressDialog.getValue() + finalProgressStep);
            });
        }


        //start cutting unions:
        for (CutShapeUnion cutShapeUnion : cutShapeUnionForPlacing) {

            Platform.runLater(() -> {
                cuttingProgressDialog.setMessage("Размещение объединения №" + cutShapeUnion.getUnionNumber());
            });
            System.out.println("\nTry to cutting union #" + cutShapeUnion.getUnionNumber());

            //check is there material sheet:
            ArrayList<Material.MaterialSheet> compatibleSheets = new ArrayList<>();
            for (Material.MaterialSheet sheet : getUsedMaterialSheetsList()) {
                if (cutShapeUnion.getMaterial() == sheet.getMaterial() && cutShapeUnion.getDepth() == sheet.getDepth()) {
                    //add compatible sheet:
                    compatibleSheets.add(sheet);
                }
            }
            boolean result = false;
            if (compatibleSheets.size() != 0) {
                //have compatible sheet, try to auto cutting:
                System.out.println("have compatible sheets");

                for (Material.MaterialSheet sheet : compatibleSheets) {
                    //try to cutting with this sheet:
                    result = autoCutUnionOnMaterialSheet(sheet, cutShapeUnion);

                    if (result) break;

                }
                if (!result) {
                    System.out.println("can't cutting on compatible sheets");
                    Material.MaterialSheet newSheet = addMaterialSheet(cutShapeUnion.getMaterial().getName() + "#" + cutShapeUnion.getDepth());
                    result = autoCutUnionOnMaterialSheet(newSheet, cutShapeUnion);

                }

            } else {
                //no compatible sheet, try to create:
                System.out.println("No compatible sheets");
                Material.MaterialSheet newSheet = addMaterialSheet(cutShapeUnion.getMaterial().getName() + "#" + cutShapeUnion.getDepth());
                result = autoCutUnionOnMaterialSheet(newSheet, cutShapeUnion);


            }
            System.out.println("Result of cutting : " + result);


            double finalProgressStep2 = progressStep;
            Platform.runLater(() -> {
                cuttingProgressDialog.setValue(cuttingProgressDialog.getValue() + finalProgressStep2);
            });
        }


        //start cutting cutShapeAdditionalFeatures:
        for (CutShapeAdditionalFeature cutShapeAdditionalFeature : cutShapeAdditionalFeaturesForPlacing) {

            Platform.runLater(() -> {
                cuttingProgressDialog.setMessage("Размещение раковины №" + cutShapeAdditionalFeature.getFeatureOwner().getFeatureNumber());
            });


            System.out.println("\nTry to cutting Feature #" + cutShapeAdditionalFeature.getFeatureOwner().getFeatureNumber());

            //check is there material sheet:
            ArrayList<Material.MaterialSheet> compatibleSheets = new ArrayList<>();
            for (Material.MaterialSheet sheet : getUsedMaterialSheetsList()) {
                if (cutShapeAdditionalFeature.getMaterial() == sheet.getMaterial() && cutShapeAdditionalFeature.getDepth() == sheet.getDepth()) {
                    //add compatible sheet:
                    compatibleSheets.add(sheet);
                }
            }
            int result = 0;
            if (compatibleSheets.size() != 0) {
                //have compatible sheet, try to auto cutting:
                System.out.println("have compatible sheets");

                for (Material.MaterialSheet sheet : compatibleSheets) {
                    //try to cutting with this sheet:
                    result = autoCutOnMaterialSheet(sheet, cutShapeAdditionalFeature);

                    if (result == 1) break;

                }
                if (result == 0) {
                    System.out.println("can't cutting on compatible sheets");
                    Material.MaterialSheet newSheet = addMaterialSheet(cutShapeAdditionalFeature.getMaterial().getName() + "#" + cutShapeAdditionalFeature.getDepth());
                    result = autoCutOnMaterialSheet(newSheet, cutShapeAdditionalFeature);

                }

            } else {
                //no compatible sheet, try to create:
                System.out.println("No compatible sheets");
                Material.MaterialSheet newSheet = addMaterialSheet(cutShapeAdditionalFeature.getMaterial().getName() + "#" + cutShapeAdditionalFeature.getDepth());
                result = autoCutOnMaterialSheet(newSheet, cutShapeAdditionalFeature);


            }
            System.out.println("Result of cutting : " + result);


            double finalProgressStep3 = progressStep;
            Platform.runLater(() -> {
                cuttingProgressDialog.setValue(cuttingProgressDialog.getValue() + finalProgressStep3);
            });
        }

        //refreshCutPaneView();

    }

    private Point2D autoCutOnSheet(Material.MaterialSheet materialSheet, CutObject cutObject) {

        if (materialSheet == null || cutObject == null) return null;

        Shape cutObjectShape = cutObject.getPolygon();

        //getSheet Path:
        Shape sheetShape = materialSheet.getPolygon();
        Bounds sheetBounds;

        Shape cutShapeUnion = null;
        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            if (cutShapeUnion == null) {
                cutShapeUnion = cutShape.getPolygon();
            } else {
                cutShapeUnion = Shape.union(cutShapeUnion, cutShape.getPolygon());
            }
        }
        for (CutShapeEdge cutShapeEdge : cutDesigner.getCutShapeEdgesList()) {
            if (cutShapeUnion == null) {
                cutShapeUnion = cutShapeEdge.getPolygon();
            } else {
                cutShapeUnion = Shape.union(cutShapeUnion, cutShapeEdge.getPolygon());
            }
        }

        //System.out.println(sheetShape.getBoundsInLocal());
        //System.out.println(sheetShape.getBoundsInParent());

        if (cutShapeUnion != null) {
            sheetShape = Shape.subtract(sheetShape, cutShapeUnion);
        }

        sheetBounds = sheetShape.getBoundsInLocal();
        //this.getChildren().add(sheetShape);

        for (double x = sheetBounds.getMinX(); x < sheetBounds.getMaxX(); x += 1) {
            for (double y = sheetBounds.getMinY(); y < sheetBounds.getMaxY(); y += 1) {

                cutObjectShape.setTranslateX(x);
                cutObjectShape.setTranslateY(y);

                double square1 = CutObject.pathSquare((Path) Shape.intersect(sheetShape, cutObjectShape));
                double square2 = CutObject.getPolygonSquare(cutObject.getPolygon());
                if (square1 + 0.1 <= square2) {
                    return new Point2D(x, y);
                }
            }
        }

        return null;
    }

    private boolean autoCutUnionOnMaterialSheet(Material.MaterialSheet materialSheet, CutShapeUnion cutShapeUnion) {


        double startX = materialSheet.getTranslateX();
        double startY = materialSheet.getTranslateY();
        //System.out.println("START: " + this.getChildren().toString());
        Point2D findingPoint = new Point2D(startX, startY);

        cutDesigner.getCutShapeUnionsList().add(cutShapeUnion);
        cutDesigner.usedShapeUnionsNumberList.add(Integer.valueOf(cutShapeUnion.getUnionNumber()));
        for (int i = 0; i < cutShapeUnion.getCutShapesInUnionList().size(); i++) {

            SketchShape sketchShape = cutShapeUnion.getSketchShapeUnion().getSketchShapesInUnion().get(i);

            CutShape cutShape = cutShapeUnion.getCutShapesInUnionList().get(i);
            cutShape.setContainInUnion(true);
            cutShape.setUnionNumber(cutShapeUnion.getUnionNumber());
            cutShape.setCutShapeUnionOwner(cutShapeUnion);

            //this.getChildren().add(cutShape);
            cutObjectsGroup.getChildren().add(cutShape);

            cutShape.setTranslateX(startX + cutShapeUnion.getSketchShapeUnion().getSketchShapesPositions().get(i).getX());
            cutShape.setTranslateY(startY + cutShapeUnion.getSketchShapeUnion().getSketchShapesPositions().get(i).getY());

            cutDesigner.getCutShapesList().add(cutShape);
            cutDesigner.usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));

            for (CutShapeEdge cutShapeEdge : cutShape.getCutShapeEdgesList()) {
                if (cutShapeEdge == null || cutShapeEdge.getStartCoordinate() == null) continue;
                //this.getChildren().add(cutShapeEdge);
                cutObjectsGroup.getChildren().add(cutShapeEdge);

                cutShapeEdge.setUnionNumber(cutShapeUnion.getUnionNumber());
                cutShapeEdge.setCutShapeUnionOwner(cutShapeUnion);
                cutShapeEdge.setContainInUnion(true);

                cutShapeEdge.setTranslateX(cutShape.getTranslateX() + cutShapeEdge.getStartCoordinate().getX());
                cutShapeEdge.setTranslateY(cutShape.getTranslateY() + cutShapeEdge.getStartCoordinate().getY());
                cutDesigner.getCutShapeEdgesList().add(cutShapeEdge);

                //cutShapeEdge.rotateShapeGlobal(cutShape.getRotate(), cutShape.localToParent(cutShape.getGlobalCenter()));
            }
            cutShape.rotateShapeLocal(sketchShape.getRotateTransform().getAngle());

        }

        //return true;
        ArrayList<Point2D> cutShapesStartPosition = new ArrayList<>();
        for (CutShape cutShape : cutShapeUnion.getCutShapesInUnionList()) {
            cutShapesStartPosition.add(new Point2D(cutShape.getTranslateX(), cutShape.getTranslateY()));
        }

        //moving.
        cutShapeUnion.setRotatePivots();
        for (int i = 0; i < 2; i++) {

            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            for (CutShape cutShape1 : cutShapeUnion.getCutShapesInUnionList()) {
                Bounds bounds = cutDesigner.getCutPane().sceneToLocal(cutShape1.localToScene(cutShape1.getPolygon().getBoundsInLocal()));
                if (bounds.getMinX() < minX) minX = bounds.getMinX();
                if (bounds.getMinY() < minY) minY = bounds.getMinY();
            }

            double shiftX = materialSheet.getTranslateX() - minX;
            double shiftY = materialSheet.getTranslateY() - minY;

//            for(int j =0;j< cutShapeUnion.getCutShapesInUnionList().size();j++) {
//
//                CutShape cutShape = cutShapeUnion.getCutShapesInUnionList().get(j);
//                cutShape.setTranslateX(cutShape.getTranslateX() + shiftX);
//                cutShape.setTranslateY(cutShape.getTranslateY() + shiftY);
//
//            }


//            for(int k =0;k<cutShapesStartPosition.size();k++ ){
//                cutShapesStartPosition.set(k, new Point2D(cutShapesStartPosition.get(k).getX() + shiftX, cutShapesStartPosition.get(k).getY() + shiftY));
//            }


            for (int j = 0; j < cutShapeUnion.getCutShapesInUnionList().size(); j++) {

                CutShape cutShape = cutShapeUnion.getCutShapesInUnionList().get(j);
                cutShape.setTranslateX(cutShape.getTranslateX() + shiftX);
                cutShape.setTranslateY(cutShape.getTranslateY() + shiftY);
                for (CutShapeEdge edge : cutShape.getCutShapeEdgesList()) {
                    edge.setTranslateX(edge.getTranslateX() + shiftX);
                    edge.setTranslateY(edge.getTranslateY() + shiftY);
                }

            }
            // return true;
            //}
            cutShapesStartPosition.clear();
            for (CutShape cutShape : cutShapeUnion.getCutShapesInUnionList()) {
                cutShapesStartPosition.add(new Point2D(cutShape.getTranslateX(), cutShape.getTranslateY()));
            }

//            if(i == 1){
//                return true;
//            }


            for (double y = 0; y <= materialSheet.getPrefHeight(); y += 1) {
                for (double x = 0; x <= materialSheet.getPrefWidth(); x += 1) {

                    for (int j = 0; j < cutShapeUnion.getCutShapesInUnionList().size(); j++) {

                        CutShape cutShape = cutShapeUnion.getCutShapesInUnionList().get(j);


                        double deltaX = (cutShapesStartPosition.get(j).getX() + x) - cutShape.getTranslateX();
                        double deltaY = (cutShapesStartPosition.get(j).getY() + y) - cutShape.getTranslateY();

                        findingPoint = new Point2D(x, y);


                        cutShape.setTranslateX(cutShapesStartPosition.get(j).getX() + x);
                        cutShape.setTranslateY(cutShapesStartPosition.get(j).getY() + y);

                        for (CutShapeEdge edge : cutShape.getCutShapeEdgesList()) {
                            if (edge == null || edge.getStartCoordinate() == null) continue;
                            edge.setTranslateX(edge.getTranslateX() + deltaX);
                            edge.setTranslateY(edge.getTranslateY() + deltaY);
                        }

                    }

                    boolean correctPlaced = true;
                    for (CutShape cutShape : cutShapeUnion.getCutShapesInUnionList()) {
                        if (!cutShape.checkCorrectPlaceOrNot()) {
                            correctPlaced = false;
                            break;
                        }
                        for (CutShapeEdge cutShapeEdge : cutShape.getCutShapeEdgesList()) {
                            if (cutShapeEdge == null || cutShapeEdge.getStartCoordinate() == null) continue;
                            if (!cutShapeEdge.checkCorrectPlaceOrNot()) {
                                correctPlaced = false;
                                break;
                            }
                        }
                    }
                    if (correctPlaced) return true;


//
//                    if(y == 17 && x == 180 && i ==1){
//
//                        System.out.println("*************************");
//                        for(CutShape cutShape : cutShapeUnion.getCutShapesInUnionList()){
//                            cutShape.checkCorrectPlaceOrNot();
//                            System.out.println("CUTSHAPE CORRECT PLACED = " + cutShape.checkCorrectPlaceOrNot());
//                            for(CutShapeEdge cutShapeEdge : cutShape.getCutShapeEdgesList()){
//                                if(cutShapeEdge == null || cutShapeEdge.getStartCoordinate() == null) continue;
//                                System.out.println("CUTEDGES CORRECT PLACED = " + cutShapeEdge.checkCorrectPlaceOrNot());
//                                System.out.println("cutShapeEdge.getPolygon().getFill() = " + cutShapeEdge.getShapeColor());
//                            }
//                        }
//                        System.out.println("*************************");
//
//                        return true;
//                    }


                    //System.out.println("X = " + x + ", Y = " + y);

                }
            }


            if (i == 0) {
                cutShapeUnion.rotate(90);
            }

        }
//
        for (CutShape cutShape : cutShapeUnion.getCutShapesInUnionList()) {
            cutDesigner.getCutPane().deleteCutShape(cutShape);
            cutDesigner.getCutShapesList().remove(cutShape);
            cutDesigner.usedShapesNumberList.remove(Integer.valueOf(cutShape.getShapeNumber()));
        }
        cutDesigner.usedShapeUnionsNumberList.remove(Integer.valueOf(cutShapeUnion.getUnionNumber()));
        cutDesigner.getCutShapeUnionsList().remove(cutShapeUnion);


        return false;
        //return true;
    }

    /* 0 - false 1- ok 2 - stop*/
    private int autoCutOnMaterialSheet(Material.MaterialSheet materialSheet, CutObject cutObject) {

        int step = 1;//10mm

        double startX = materialSheet.getTranslateX();
        double startY = materialSheet.getTranslateY();
        //System.out.println("START: " + this.getChildren().toString());
        Point2D findingPoint = new Point2D(startX, startY);

        if (cutObject instanceof CutShape) {
            //this.getChildren().add(cutObject);
            cutObjectsGroup.getChildren().add(cutObject);
            cutDesigner.getCutShapesList().add((CutShape) cutObject);
            cutDesigner.usedShapesNumberList.add(Integer.valueOf(((CutShape) cutObject).getShapeNumber()));
            // System.out.println("CutShape #" + ((CutShape) cutObject).getShapeNumber() + " isSaveMaterialImage() = " + ((CutShape) cutObject).isSaveMaterialImage());
            if (((CutShape) cutObject).isSaveMaterialImage()) {
                for (CutShapeEdge edge : ((CutShape) cutObject).getCutShapeEdgesList()) {
                    if (edge == null || edge.getStartCoordinate() == null) continue;
                    //this.getChildren().add(edge);
                    cutObjectsGroup.getChildren().add(edge);
                    edge.setTranslateX(((CutShape) cutObject).getTranslateX() + edge.getStartCoordinate().getX());
                    edge.setTranslateY(((CutShape) cutObject).getTranslateY() + edge.getStartCoordinate().getY());

                    cutDesigner.getCutShapeEdgesList().add(edge);
                }
            }

        } else if (cutObject instanceof CutShapeEdge) {
            //System.out.println("IN EDGES: " + cutObject);
            //this.getChildren().add(cutObject);
            cutObjectsGroup.getChildren().add(cutObject);
            cutDesigner.getCutShapeEdgesList().add((CutShapeEdge) cutObject);
        } else if (cutObject instanceof CutShapeAdditionalFeature) {
            //this.getChildren().add(cutObject);
            cutObjectsGroup.getChildren().add(cutObject);
            cutDesigner.getCutShapeAdditionalFeaturesList().add((CutShapeAdditionalFeature) cutObject);
        }


        //moving.
        for (int i = 0; i < 2; i++) {


            for (double y = startY; y <= startY + materialSheet.getPrefHeight(); y += step) {
                for (double x = startX; x <= startX + materialSheet.getPrefWidth(); x += step) {

                    if(externalStopAutoCutting == true) return 1;

                    Bounds bounds = cutDesigner.getCutPane().sceneToLocal(cutObject.localToScene(cutObject.getPolygon().getBoundsInParent()));
                    double minX = bounds.getMinX();
                    double minY = bounds.getMinY();
                    double deltaX = cutObject.getTranslateX() - minX;
                    double deltaY = cutObject.getTranslateY() - minY;


                    findingPoint = new Point2D(x, y);

                    if (cutObject instanceof CutShape) {
                        CutShape cutShape = (CutShape) cutObject;
                        if (cutShape.isSaveMaterialImage()) {

                            double oldTranslateX = cutShape.getTranslateX() - (x + deltaX);
                            double oldTranslateY = cutShape.getTranslateY() - (y + deltaY);

                            cutShape.setTranslateX(x + deltaX);
                            cutShape.setTranslateY(y + deltaY);
                            for (CutShapeEdge edge : cutShape.getCutShapeEdgesList()) {
                                if (edge == null || edge.getStartCoordinate() == null) continue;
                                edge.setTranslateX(edge.getTranslateX() - oldTranslateX);
                                edge.setTranslateY(edge.getTranslateY() - oldTranslateY);
                            }

                        } else {
                            cutShape.setTranslateX(x + deltaX);
                            cutShape.setTranslateY(y + deltaY);


                            //if(i == 1) return true;//for stop after rotate. Debug procedure
                        }

                    } else if (cutObject instanceof CutShapeEdge) {
                        cutObject.setTranslateX(x);
                        cutObject.setTranslateY(y);
                    } else if (cutObject instanceof CutShapeAdditionalFeature) {
                        cutObject.setTranslateX(x);
                        cutObject.setTranslateY(y);
                    }

                    if (cutObject.isCorrectPlaced()) {
                        return 1;
                    }

                    //System.out.println("X = " + x + ", Y = " + y);

                }
            }


            if (i == 0) {
                cutObject.rotateShapeLocal(90);
                System.out.println("ROTATE SHAPE " + cutObject.getRotateAngle());
            }

        }
//
        cutObject.rotateShapeLocal(-90);
        if (cutObject instanceof CutShape) {
            //this.getChildren().remove(cutObject);
            cutObjectsGroup.getChildren().remove(cutObject);
            cutDesigner.getCutShapesList().remove(cutObject);
            cutDesigner.usedShapesNumberList.remove(Integer.valueOf(((CutShape) cutObject).getShapeNumber()));
            if (((CutShape) cutObject).isSaveMaterialImage()) {
                for (CutShapeEdge edge : ((CutShape) cutObject).getCutShapeEdgesList()) {

                    //this.getChildren().remove(edge);
                    cutObjectsGroup.getChildren().remove(edge);
                    cutDesigner.getCutShapeEdgesList().remove(edge);
                }
            }

        } else if (cutObject instanceof CutShapeEdge) {
            //this.getChildren().remove(cutObject);
            cutObjectsGroup.getChildren().remove(cutObject);
            cutDesigner.getCutShapeEdgesList().remove((CutShapeEdge) cutObject);
        } else if (cutObject instanceof CutShapeAdditionalFeature) {
            //this.getChildren().remove(cutObject);
            cutObjectsGroup.getChildren().remove(cutObject);
            cutDesigner.getCutShapeAdditionalFeaturesList().remove(cutObject);
        }


        return 0;
        //return true;
    }


    public static boolean checkIntoMaterialOrNot(CutObject cutObj) throws IndexOutOfBoundsException {

        String materialName = "";
        int materialDepth = 0;
        if (cutObj instanceof CutShape) {
            materialName = ((CutShape) cutObj).getMaterial().getName();
            materialDepth = ((CutShape) cutObj).getDepth();
        } else if (cutObj instanceof CutShapeEdge) {
            materialName = ((CutShapeEdge) cutObj).getOwner().getMaterial().getName();
            materialDepth = ((CutShapeEdge) cutObj).getOwner().getDepth();
        } else if (cutObj instanceof CutShapeAdditionalFeature) {
            materialName = ((CutShapeAdditionalFeature) cutObj).getMaterial().getName();
            materialDepth = ((CutShapeAdditionalFeature) cutObj).getDepth();
        }

        if(!(cutObj instanceof CutShape)) return false;
        CutShape cutShape = (CutShape) cutObj;

        boolean into = false;
        if (materialSheetsMap.get(materialName + "#" + materialDepth) == null) return into;
        for (Material.MaterialSheet materialSheet : materialSheetsMap.get(materialName + "#" + materialDepth)) {

            Bounds sheetBounds = materialSheet.localToParent(materialSheet.getPolygon().getBoundsInParent());
            Bounds cutObjectBounds = cutObj.localToParent(cutObj.getPolygon().getBoundsInParent());

            if(sheetBounds.getWidth() >= cutObjectBounds.getWidth() && sheetBounds.getHeight() >= cutObjectBounds.getHeight()){

                if(sheetBounds.getMinX() - 0.001 <= cutObjectBounds.getMinX() && sheetBounds.getMinY() - 0.001 <= cutObjectBounds.getMinY()){

                    if(sheetBounds.getMaxX() + 0.001 >= cutObjectBounds.getMaxX() && sheetBounds.getMaxY() + 0.001 >= cutObjectBounds.getMaxY()){
                        into = true;
                        break;
                    }

                }

            }
        }
        return into;
    }


    public static Polygon getPolygonOnCutPaneCoordinate(CutObject cutObj) {
        ArrayList<Point2D> thisPolygonPoints = new ArrayList<>();
        for (int i = 0; i < cutObj.getPolygon().getPoints().size(); i += 2) {
            double x = cutObj.getPolygon().getPoints().get(i);
            double y = cutObj.getPolygon().getPoints().get(i + 1);
            Point2D pointOnCutPane = cutDesigner.getCutPane().sceneToLocal(cutObj.localToScene(x, y));
            thisPolygonPoints.add(pointOnCutPane);
        }
        Polygon objPolygon = new Polygon();
        for (Point2D p : thisPolygonPoints) {
            objPolygon.getPoints().add(p.getX());
            objPolygon.getPoints().add(p.getY());
        }
        return objPolygon;
    }

    public static boolean checkOverMaterialOrNot(CutObject cutObj, Material.MaterialSheet materialSheet) {

        String materialName = "";
        int materialDepth = 0;
        if (cutObj instanceof CutShape) {
            materialName = ((CutShape) cutObj).getMaterial().getName();
            materialDepth = ((CutShape) cutObj).getDepth();
        } else if (cutObj instanceof CutShapeEdge) {
            materialName = ((CutShapeEdge) cutObj).getOwner().getMaterial().getName();
            materialDepth = ((CutShapeEdge) cutObj).getOwner().getDepth();
        } else if (cutObj instanceof CutShapeAdditionalFeature) {
            materialName = ((CutShapeAdditionalFeature) cutObj).getMaterial().getName();
            materialDepth = ((CutShapeAdditionalFeature) cutObj).getDepth();
        }

        boolean into = false;
        if (materialSheetsMap.get(materialName + "#" + materialDepth) == null) return into;

        ArrayList<Point2D> polygonPoints = new ArrayList<>();
//            System.out.println("cutObj= " + cutObj);
//            System.out.println("cutObj.getPolygon()= " + cutObj.getPolygon());
//            System.out.println("cutObj.getPolygon().getPoints()= " + cutObj.getPolygon().getPoints());
        if(cutObj.getPolygon() == null) return into;

        for (int i = 0; i < cutObj.getPolygon().getPoints().size(); i += 2) {
            double x = cutObj.getPolygon().getPoints().get(i);
            double y = cutObj.getPolygon().getPoints().get(i + 1);
            polygonPoints.add(new Point2D(x, y));
        }

        int count = 0;
        for (Point2D p : polygonPoints) {
            Point2D pointOnCutPane = cutObj.localToParent(p);

            Bounds bounds = cutDesigner.getCutPane().sceneToLocal(materialSheet.localToScene(materialSheet.getPolygon().getBoundsInParent()));
            //Manual check that point into bounds with 0.001
            if((pointOnCutPane.getX() >= bounds.getMinX() - 0.001  && pointOnCutPane.getX() <= bounds.getMaxX() + 0.001)
                    && (pointOnCutPane.getY() >= bounds.getMinY() - 0.001  && pointOnCutPane.getY() <= bounds.getMaxY() + 0.001)){
                count++;
            }

        }

        if (count == polygonPoints.size()) {
            into = true;
        }
        return into;


//        boolean overMaterial = false;
//        ArrayList<Point2D> thisPolygonPoints = new ArrayList<>();
//        for (int i = 0; i < cutObj.getPolygon().getPoints().size(); i += 2) {
//            double x = cutObj.getPolygon().getPoints().get(i);
//            double y = cutObj.getPolygon().getPoints().get(i + 1);
//            Point2D pointOnCutPane = CutDesigner.getCutPane().sceneToLocal(cutObj.localToScene(x, y));
//            thisPolygonPoints.add(pointOnCutPane);
//        }
//        Polygon objPolygon = new Polygon();
//        for (Point2D p : thisPolygonPoints) {
//            objPolygon.getPoints().add(p.getX());
//            objPolygon.getPoints().add(p.getY());
//        }
//
//
//        ArrayList<Point2D> materialPolygonPoints = new ArrayList<>();
//        for (int i = 0; i < materialSheet.getPolygon().getPoints().size(); i += 2) {
//            double x = materialSheet.getPolygon().getPoints().get(i);
//            double y = materialSheet.getPolygon().getPoints().get(i + 1);
//            Point2D pointOnCutPane = CutDesigner.getCutPane().sceneToLocal(materialSheet.localToScene(x, y));
//            materialPolygonPoints.add(pointOnCutPane);
//        }
//        Polygon materialPolygon = new Polygon();
//        for (Point2D p : materialPolygonPoints) {
//            materialPolygon.getPoints().add(p.getX());
//            materialPolygon.getPoints().add(p.getY());
//        }
//
//        if (((Path) Shape.intersect(objPolygon, materialPolygon)).getElements().size() != 0) {
//            overMaterial = true;
//        }
//
//        return overMaterial;
    }

    public static boolean isCutShapeOverCutShape(CutObject cutObject1, CutObject cutObject2) throws IndexOutOfBoundsException {

        boolean result = false;
        //get cutObject1 poligon:
        if(cutObject1.getCutZonePolygon() == null) return false;
        ArrayList<Point2D> polygon1Points = new ArrayList<>();
        for (int i = 0; i < cutObject1.getCutZonePolygon().getPoints().size(); i += 2) {
            double x = cutObject1.getCutZonePolygon().getPoints().get(i);
            double y = cutObject1.getCutZonePolygon().getPoints().get(i + 1);
            Point2D pointOnCutPane = cutObject1.localToParent(x, y); /*CutDesigner.getCutPane().sceneToLocal(cutObject1.localToScene(x, y));*/
            polygon1Points.add(pointOnCutPane);
        }
        Polygon polygon1 = new Polygon();
        for (Point2D p : polygon1Points) {
            polygon1.getPoints().add(p.getX());
            polygon1.getPoints().add(p.getY());
        }

        //get cutObject2 poligon:
        if(cutObject2.getCutZonePolygon() == null) return false;
        ArrayList<Point2D> polygon2Points = new ArrayList<>();
        for (int i = 0; i < cutObject2.getCutZonePolygon().getPoints().size(); i += 2) {
            double x = cutObject2.getCutZonePolygon().getPoints().get(i);
            double y = cutObject2.getCutZonePolygon().getPoints().get(i + 1);
            Point2D pointOnCutPane = cutObject2.localToParent(x, y);/*CutDesigner.getCutPane().sceneToLocal(cutObject1.localToScene(x, y));*/
            polygon2Points.add(pointOnCutPane);
        }
        Polygon polygon2 = new Polygon();
        for (Point2D p : polygon2Points) {
            polygon2.getPoints().add(p.getX());
            polygon2.getPoints().add(p.getY());
        }

        if (((Path) Shape.intersect(polygon1, polygon2)).getElements().size() != 0) {
            //System.out.println("OVER! " + otherCutObject);
            if (Math.abs(CutObject.pathSquare(((Path) Shape.intersect(polygon1, polygon2)))) > 0.5) {
                //System.out.println("OVER! but < 0.5 = " + Math.abs(pathSquare(((Path)Shape.intersect(thisPolygon, otherCutObjectPolygon)))));
                //result =  true;
                return true;
            }
            //result =  false;

        }
        return false;
    }

    public void unSelectAllShapes() {
        cutDesigner.selectedShapes.clear();
        //for(Node shape : this.getChildren()){
        for (Node shape : cutObjectsGroup.getChildren()) {

            if (shape instanceof CutObject) {
                ((CutObject) shape).unSelectShape();
            }

        }
    }

    @Override
    public JSONObject getJsonView() {

        cutDesigner.refreshCutView(); //this need for correct calculate usesList variable in MaterialSheets

        JSONObject jsonObject = new JSONObject();
        //all material Sheets
        JSONArray materialSheetsArray = new JSONArray();
        for (Map.Entry<String, ArrayList<Material.MaterialSheet>> entry : materialSheetsMap.entrySet()) {

            JSONObject materialObject = new JSONObject();
            materialObject.put("name", entry.getKey());
            //materialObject.put("size", entry.getValue().size());

            System.out.println("SAVE SHEETS - " + entry.getValue().size());
            //add sheets:
            JSONArray jsonSheets = new JSONArray();
            for(Material.MaterialSheet materialSheet : entry.getValue()){

                JSONObject jsonMaterialSheet = new JSONObject();

                //add prices:
                JSONArray jsonTableTopDepthsAndPrices = new JSONArray();
                JSONArray jsonWallPanelDepthsAndPrices = new JSONArray();
                JSONArray jsonWindowSillDepthsAndPrices = new JSONArray();
                JSONArray jsonFootDepthsAndPrices = new JSONArray();
                for(Map.Entry<Integer, Integer> depthPrice : materialSheet.getTableTopDepthsAndPrices().entrySet()){
                    jsonTableTopDepthsAndPrices.add(depthPrice.getKey().toString() + "=" + depthPrice.getValue().toString());
                }
                for(Map.Entry<Integer, Integer> depthPrice : materialSheet.getWallPanelDepthsAndPrices().entrySet()){
                    jsonWallPanelDepthsAndPrices.add(depthPrice.getKey().toString() + "=" + depthPrice.getValue().toString());
                }
                for(Map.Entry<Integer, Integer> depthPrice : materialSheet.getWindowSillDepthsAndPrices().entrySet()){
                    jsonWindowSillDepthsAndPrices.add(depthPrice.getKey().toString() + "=" + depthPrice.getValue().toString());
                }
                for(Map.Entry<Integer, Integer> depthPrice : materialSheet.getFootDepthsAndPrices().entrySet()){
                    jsonFootDepthsAndPrices.add(depthPrice.getKey().toString() + "=" + depthPrice.getValue().toString());
                }

                jsonMaterialSheet.put("tableTopDepthsAndPrices", jsonTableTopDepthsAndPrices);
                jsonMaterialSheet.put("wallPanelDepthsAndPrices", jsonWallPanelDepthsAndPrices);
                jsonMaterialSheet.put("windowSillDepthsAndPrices", jsonWindowSillDepthsAndPrices);
                jsonMaterialSheet.put("footDepthsAndPrices", jsonFootDepthsAndPrices);

                //add coefficients:
                JSONArray jsonTableTopCoefficients = new JSONArray();
                JSONArray jsonWallPanelCoefficients = new JSONArray();
                JSONArray jsonWindowSillCoefficients = new JSONArray();
                JSONArray jsonFootCoefficients = new JSONArray();
                for(Double coefficient : materialSheet.getTableTopCoefficientList()){
                    jsonTableTopCoefficients.add(coefficient);
                }
                for(Double coefficient : materialSheet.getWallPanelCoefficientList()){
                    jsonWallPanelCoefficients.add(coefficient);
                }
                for(Double coefficient : materialSheet.getWindowSillCoefficientList()){
                    jsonWindowSillCoefficients.add(coefficient);
                }
                for(Double coefficient : materialSheet.getFootCoefficientList()){
                    jsonFootCoefficients.add(coefficient);
                }

                jsonMaterialSheet.put("tableTopCoefficients", jsonTableTopCoefficients);
                jsonMaterialSheet.put("wallPanelCoefficients", jsonWallPanelCoefficients);
                jsonMaterialSheet.put("windowSillCoefficients", jsonWindowSillCoefficients);
                jsonMaterialSheet.put("footCoefficients", jsonFootCoefficients);


                jsonMaterialSheet.put("usesList", materialSheet.getUsesList());//if this not uses this use new price.
                //System.out.println("materialSheet.getUsesList() = " + materialSheet.getUsesList());

                jsonSheets.add(jsonMaterialSheet);
            }
            materialObject.put("materialSheets", jsonSheets);

            materialSheetsArray.add(materialObject);
        }

        ArrayList<CutShape> savedList = new ArrayList<>(cutDesigner.getCutShapesList());
        if (Project.getProjectType() == ProjectType.TABLE_TYPE) {
            cutDesigner.getCutShapesList().clear();
        }


        //all shapes
        JSONArray shapesArray = new JSONArray();
        for (CutShape cutShape : cutDesigner.getCutShapesList()) {
            shapesArray.add(cutShape.getJsonView());
        }


        //all unions
        JSONArray unionsArray = new JSONArray();
        for (CutShapeUnion cutShapeUnion : cutDesigner.getCutShapeUnionsList()) {
            unionsArray.add(cutShapeUnion.getJsonView());
        }
//        System.out.println(getShapesList().toString());

        //all dimensions
        JSONArray dimensionsArray = new JSONArray();
        for (int i = 0; i < cutDesigner.getAllDimensions().size(); i++) {
            dimensionsArray.add(cutDesigner.getAllDimensions().get(i).getJsonView());
        }


        jsonObject.put("materialSheetsArray", materialSheetsArray);
        jsonObject.put("shapesArray", shapesArray);
        jsonObject.put("unionsArray", unionsArray);
        jsonObject.put("dimensionsArray", dimensionsArray);
        //jsonObject.put("cutPaneScale", cutPaneScale);


        if (Project.getProjectType() == ProjectType.TABLE_TYPE) {
            cutDesigner.getCutShapesList().addAll(savedList);
        }

        return jsonObject;
    }

    @Override
    public void initFromJson(JSONObject jsonObject) {

        JSONArray materialSheetsArray = (JSONArray) jsonObject.get("materialSheetsArray");
        JSONArray shapesArray = (JSONArray) jsonObject.get("shapesArray");
        JSONArray unionsArray = (JSONArray) jsonObject.get("unionsArray");
//        JSONArray shapesEdgesArray = (JSONArray) jsonObject.get("shapesEdgesArray");
        JSONArray dimensionsArray = (JSONArray) jsonObject.get("dimensionsArray");

        //cutPaneScale = (Double)jsonObject.get("cutPaneScale");

        //init MAterials Sheets
        materialSheetsMap.clear();
        for (Object obj : materialSheetsArray) {
            JSONObject jObj = (JSONObject) obj;

            //get sheets:

            if(jObj.get("materialSheets") != null){
                JSONArray jsonSheets = (JSONArray) jObj.get("materialSheets");

                //System.out.println("materialSheet JSON COUNT = " + jsonSheets.size());

                for(Object objSheet : jsonSheets){
                    JSONObject jsonMaterialSheet = (JSONObject) objSheet;

                    //if(jObj.get("name") == null) break;

                    Material.MaterialSheet materialSheet = addMaterialSheet(((String) jObj.get("name")));
                    //if(materialSheet == null) break;



                    //getPrices
                    JSONArray jsonTableTopDepthsAndPrices = (JSONArray) jsonMaterialSheet.get("tableTopDepthsAndPrices");
                    JSONArray jsonWallPanelDepthsAndPrices = (JSONArray) jsonMaterialSheet.get("wallPanelDepthsAndPrices");
                    JSONArray jsonWindowSillDepthsAndPrices = (JSONArray) jsonMaterialSheet.get("windowSillDepthsAndPrices");
                    JSONArray jsonFootDepthsAndPrices = (JSONArray) jsonMaterialSheet.get("footDepthsAndPrices");

                    materialSheet.getTableTopDepthsAndPrices().clear();
                    materialSheet.getWallPanelDepthsAndPrices().clear();
                    materialSheet.getWindowSillDepthsAndPrices().clear();
                    materialSheet.getFootDepthsAndPrices().clear();

                    for(Object objDepthPrice : jsonTableTopDepthsAndPrices){
                        int depth = Integer.parseInt(((String)objDepthPrice).split("=")[0]);
                        int price = Integer.parseInt(((String)objDepthPrice).split("=")[1]);
                        materialSheet.getTableTopDepthsAndPrices().put(Integer.valueOf(depth), Integer.valueOf(price));
                    }

                    for(Object objDepthPrice : jsonWallPanelDepthsAndPrices){
                        int depth = Integer.parseInt(((String)objDepthPrice).split("=")[0]);
                        int price = Integer.parseInt(((String)objDepthPrice).split("=")[1]);
                        materialSheet.getWallPanelDepthsAndPrices().put(Integer.valueOf(depth), Integer.valueOf(price));
                    }

                    for(Object objDepthPrice : jsonWindowSillDepthsAndPrices){
                        int depth = Integer.parseInt(((String)objDepthPrice).split("=")[0]);
                        int price = Integer.parseInt(((String)objDepthPrice).split("=")[1]);
                        materialSheet.getWindowSillDepthsAndPrices().put(Integer.valueOf(depth), Integer.valueOf(price));
                    }

                    for(Object objDepthPrice : jsonFootDepthsAndPrices){
                        int depth = Integer.parseInt(((String)objDepthPrice).split("=")[0]);
                        int price = Integer.parseInt(((String)objDepthPrice).split("=")[1]);
                        materialSheet.getFootDepthsAndPrices().put(Integer.valueOf(depth), Integer.valueOf(price));
                    }



                    //getCoefficients
                    JSONArray jsonTableTopCoefficients = (JSONArray) jsonMaterialSheet.get("tableTopCoefficients");
                    JSONArray jsonWallPanelCoefficients = (JSONArray) jsonMaterialSheet.get("wallPanelCoefficients");
                    JSONArray jsonWindowSillCoefficients = (JSONArray) jsonMaterialSheet.get("windowSillCoefficients");
                    JSONArray jsonFootCoefficients = (JSONArray) jsonMaterialSheet.get("footCoefficients");

                    materialSheet.getTableTopCoefficientList().clear();
                    materialSheet.getWallPanelCoefficientList().clear();
                    materialSheet.getWindowSillCoefficientList().clear();
                    materialSheet.getFootCoefficientList().clear();

                    for(Object objCoefficient : jsonTableTopCoefficients){
                        Double coefficient = ((Double)objCoefficient);
                        materialSheet.getTableTopCoefficientList().add(coefficient);
                    }
                    for(Object objCoefficient : jsonWallPanelCoefficients){
                        Double coefficient = ((Double)objCoefficient);
                        materialSheet.getWallPanelCoefficientList().add(coefficient);
                    }
                    for(Object objCoefficient : jsonWindowSillCoefficients){
                        Double coefficient = ((Double)objCoefficient);
                        materialSheet.getWindowSillCoefficientList().add(coefficient);
                    }
                    for(Object objCoefficient : jsonFootCoefficients){
                        Double coefficient = ((Double)objCoefficient);
                        materialSheet.getFootCoefficientList().add(coefficient);
                    }

                    if(jsonMaterialSheet.get("usesList") != null){
                        int usesList = ((Long)jsonMaterialSheet.get("usesList")).intValue();
                        materialSheet.setUsesList(usesList);
                        //System.out.println("******** usesList = " + materialSheet.getRawUsesList());
                    }


                }
            }else{
                //for old projects:
                System.out.println("old project = ");
                for (int i = 0; i < ((Long) jObj.get("size")).intValue(); i++) {
                    Material.MaterialSheet materialSheet = addMaterialSheet(((String) jObj.get("name")));

                }
            }
        }


        //get shapes and shapeEdges:
        cutObjectsGroup.getChildren().clear();
        cutDesigner.usedShapesNumberList.clear();
        cutDesigner.getCutShapesList().clear();
        cutDesigner.getCutShapeEdgesList().clear();


        for (Object obj : shapesArray) {
            JSONObject jsonObj = (JSONObject) obj;

            int shapeNumber = ((Long) jsonObj.get("shapeNumber")).intValue();
            ElementTypes elementType = ElementTypes.valueOf(((String) jsonObj.get("elementType")));
            SketchShape sketchShape = SketchDesigner.getSketchShape(shapeNumber, elementType);

            CutShape cutShape = sketchShape.getCutShape();
            cutDesigner.getCutShapesList().add(cutShape);

            //this.getChildren().add(cutShape);
            if (!cutObjectsGroup.getChildren().contains(cutShape)) {
                cutObjectsGroup.getChildren().add(cutShape);
                System.err.println("CutPane contain dublicate Children SHAPE");
            }

            for (CutShapeEdge cutShapeEdge : cutShape.getCutShapeEdgesList()) {
                if (cutShapeEdge.getStartCoordinate() != null) {
                    //this.getChildren().add(cutShapeEdge);
                    if (!cutObjectsGroup.getChildren().contains(cutShapeEdge)) {
                        cutObjectsGroup.getChildren().add(cutShapeEdge);
                        System.err.println("CutPane contain dublicate Children  EDGE");
                    }
                    cutDesigner.getCutShapeEdgesList().add(cutShapeEdge);
                }
            }


            cutShape.initFromJson(jsonObj);
            cutDesigner.usedShapesNumberList.add(Integer.valueOf(cutShape.getShapeNumber()));

        }

        cutDesigner.getCutShapeUnionsList().clear();
        for (Object obj : unionsArray) {
            JSONObject jsonObj = (JSONObject) obj;
            CutShapeUnion cutShapeUnion = CutShapeUnion.initFromJson(jsonObj);
            cutDesigner.getCutShapeUnionsList().add(cutShapeUnion);
            cutDesigner.usedShapeUnionsNumberList.add(Integer.valueOf(cutShapeUnion.getUnionNumber()));
        }


    }
}
