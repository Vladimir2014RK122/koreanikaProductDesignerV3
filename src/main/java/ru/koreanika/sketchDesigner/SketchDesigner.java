package ru.koreanika.sketchDesigner;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.common.ConnectPoints.CornerConnectPoint;
import ru.koreanika.common.material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.Dimensions.Dimension;
import ru.koreanika.sketchDesigner.Dimensions.LinearDimension;
import ru.koreanika.sketchDesigner.Edge.EdgeManager;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.*;
import ru.koreanika.sketchDesigner.ShapeManager.ShapeManager;
import ru.koreanika.sketchDesigner.Shapes.*;
import ru.koreanika.sketchDesigner.lists.CellFactory;
import ru.koreanika.sketchDesigner.lists.FeatureListElement;
import ru.koreanika.sketchDesigner.lists.FeaturesCellFactory;
import ru.koreanika.sketchDesigner.lists.ListElement;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.print.PrinterDialog;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class SketchDesigner {

    //Main control elements:
    static AnchorPane rootAnchorPaneSketchDesigner, anchorPaneTableTopShapes;
    private final EventBus eventBus;
    SplitPane splitPaneMain;
    Button btnCreateProject, btnOpenProject, btnSaveProject, btnSaveAsProject;

    //Elements(Left) side:
    AnchorPane anchorPaneElements;
    AnchorPane anchorPaneShapesListWrapper;
    AnchorPane anchorPaneElementsMenu;
    AnchorPane anchorPaneMenu;
    Accordion accordionItems;
    TitledPane titledPaneTabletops, titledPaneWallPanels, titledPaneWindowsill, titledPaneFoot, titledPaneSink, titledPaneGroove, titledPaneRods, titledPaneCutouts;
    TabPane tabPaneLeftSide;
    Tab tableTopTab, wallPanelTab, sinkTab, groovesTab, rodsTab;

    ListView<ListElement> tableTopListView;
    ListView<ListElement> wallPanelListView;
    ListView<ListElement> windowsillListView;
    ListView<ListElement> footListView;
    ListView<FeatureListElement> sinkListView, groovesListView, rodsListView, cutoutsListView;

    private static ArrayList<SketchShape> sketchShapesList = new ArrayList<>();
    private static ArrayList<SketchShapeUnion> sketchShapeUnionsList = new ArrayList<>();


    //SketchPane(Center)
    private final double SKETCH_PANE_WIDTH = 5000;
    private final double SKETCH_PANE_HEIGHT = 6000;
    AnchorPane anchorPaneCenter;
    ScrollPane scrollPaneCenter;
    static Pane sketchPane;
    Button btnPrint;
    Button btnScaleMinus, btnScalePlus;
    Button btnRotateLeft, btnRotateRight;
    Button btnAddDimH, btnAddDimV;
    Button btnCreateUnion, btnCrashUnion;
    Button btnShapeManager;
    public static SketchObject draggedShape = null;

    double anchorXPannable = 0;
    double anchorYPannable = 0;

    static Group sketchElementsGroup = new Group();

    //Settings(Right) side:
    AnchorPane anchorPaneSettings;
    ScrollPane scrollPaneShapeSettings;
    public static AnchorPane anchorPaneShapeSettings;

    private static Button btnCancelSettings, btnSaveSettings;

    public static ArrayList<SketchShape> selectedSketchObjects = new ArrayList<SketchShape>();
    public static boolean multipleSelectionMode = false;

    //select edges
    public static ArrayList<SketchEdge> selectedEdges = new ArrayList<>(10);
    public static boolean multipleSelectionModeEdges = false;
    public static Material selectedEdgeMaterial;

    //Dimensions
    private static boolean addDimensionsMode = false;
    private static int dimensionType;
    private static LinkedHashSet<CornerConnectPoint> connectPointsForDimensionsSet = new LinkedHashSet<>();
    private static ObservableSet<CornerConnectPoint> connectPointsForDimensionsObservableSet = FXCollections.observableSet(connectPointsForDimensionsSet);
    private static ArrayList<LinearDimension> allDimensions = new ArrayList<>();


    //printing:
    BooleanProperty selectPrintingZoneMode = new SimpleBooleanProperty(false);
    Rectangle selectZoneForPrinting;
    double startSelectPrintZoneX = 0;
    double startSelectPrintZoneY = 0;
    boolean printingZoneStartSelecting = false;

    public SketchDesigner() {

        sketchShapesList.clear();
        selectedSketchObjects.clear();
        SketchShape.shapeCounter = 0;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/sketchDesigner.fxml"));
        try {
            rootAnchorPaneSketchDesigner = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        initControlElements();
        setControlElementsLogic();


        initZoom();

        eventBus = ServiceLocator.getService("EventBus", EventBus.class);
    }


//    public Group getSketchElementsGroup() {
//        return sketchElementsGroup;
//    }

    public static double getSketchPaneScale() {
        return sketchElementsGroup.getScaleX();
    }

    public static void setInstanceFromJson(JSONObject sketchDesigner) {

        JSONArray sketchDesignerElements = (JSONArray) sketchDesigner.get("elements");

        sketchPane.getChildren().clear();
        SketchDesigner.getSketchShapesList().clear();
        SketchDesigner.getSketchShapeUnionsList().clear();
        allDimensions.clear();


        for (int i = 0; i < sketchDesignerElements.size(); i++) {
            JSONObject element = (JSONObject) sketchDesignerElements.get(i);

            JSONArray sketchDesignerXY = (JSONArray) element.get("sketchDesignerXY");
            double x = ((Double) sketchDesignerXY.get(0)).doubleValue();
            double y = ((Double) sketchDesignerXY.get(1)).doubleValue();

            SketchShape shape = null;
            ElementTypes elementType = ElementTypes.TABLETOP;
            if (ElementTypes.TABLETOP.toString().equals((String) element.get("elementType")))
                elementType = ElementTypes.TABLETOP;
            else if (ElementTypes.WALL_PANEL.toString().equals((String) element.get("elementType")))
                elementType = ElementTypes.WALL_PANEL;

            if (ShapeType.RECTANGLE.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeRectangle(x, y, elementType, sketchPane);
            } else if (ShapeType.RECTANGLE_CUT_CORNER.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeRectangleCutCorner(x, y, elementType, sketchPane);
            } else if (ShapeType.RECTANGLE_CIRCLE_CORNER.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeRectangleCircleCorner(x, y, elementType, sketchPane);
            } else if (ShapeType.RECTANGLE_CIRCLE_CORNER_INTO.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeRectangleCircleCornerInto(x, y, elementType, sketchPane);
            } else if (ShapeType.TRIANGLE.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeTriangle(x, y, elementType, sketchPane);
            } else if (ShapeType.CIRCLE_HALF.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeCircleHalf(x, y, elementType, sketchPane);
            } else if (ShapeType.CIRCLE.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeCircle(x, y, elementType, sketchPane);
            } else if (ShapeType.TRAPEZE.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeTrapeze(x, y, elementType, sketchPane);
            } else if (ShapeType.RHOMBUS.toString().equals((String) element.get("shapeType"))) {

                shape = new SketchShapeRhombus(x, y, elementType, sketchPane);
            }

            sketchPane.getChildren().add(shape);
            if (shape != null) {

                shape.initFromJson(element);//shape add himself to pane
                SketchDesigner.getSketchShapesList().add(shape);
                //sketchPane.getChildren().add(shape);
                //shape.rotateShape(((Double)element.get("rotateAngle")).doubleValue());
            }
        }

        JSONArray sketchDesignerUnions = (JSONArray) sketchDesigner.get("unions");
        for (int i = 0; i < sketchDesignerUnions.size(); i++) {
            JSONObject jsonUnion = (JSONObject) sketchDesignerUnions.get(i);
            SketchShapeUnion union = SketchShapeUnion.initFromJson(jsonUnion);
            if (union != null) {
                SketchDesigner.getSketchShapeUnionsList().add(union);
//                sketchPane.getChildren().add(union);
            }
        }

        //set shapes with opacity < 1 to front view:
        for (SketchShape sh : sketchShapesList) {
            if (sh.getOpacity() < 1.0) {
                sh.toFront();
            }
        }

        for (SketchShapeUnion shUnion : sketchShapeUnionsList) {
//            if(shUnion.getOpacity() < 1.0){
//                shUnion.toFront();
//            }
        }

        JSONArray sketchDesignerDimensions = (JSONArray) sketchDesigner.get("dimensions");
        if (sketchDesignerDimensions == null) return;
        for (int i = 0; i < sketchDesignerDimensions.size(); i++) {
            JSONObject dimension = (JSONObject) sketchDesignerDimensions.get(i);

            LinearDimension ld = new LinearDimension(dimension);
            sketchPane.getChildren().add(ld);
            allDimensions.add(ld);

        }

    }

    public AnchorPane getRootAnchorPaneSketchDesigner() {
        return rootAnchorPaneSketchDesigner;
    }

    public static void updateMaterialsInProject() {
        //MainWindow.receiptListListenersToggle(false);
        for (SketchShape shape : sketchShapesList) {
            shape.updateMaterialList();
        }
        //MainWindow.receiptListListenersToggle(true);
        //MainWindow.refreshListReceipt();
    }

    public static void updateEdgesHeightsInProject() {
        //MainWindow.receiptListListenersToggle(false);
        for (SketchShape shape : sketchShapesList) {
            shape.updateEdgesHeight();
        }
        //MainWindow.receiptListListenersToggle(true);
        //MainWindow.refreshListReceipt();
    }

    public void initControlElements() {

        //Main control elements:
        splitPaneMain = (SplitPane) rootAnchorPaneSketchDesigner.lookup("#splitPaneMain");
        anchorPaneMenu = (AnchorPane) rootAnchorPaneSketchDesigner.lookup("#anchorPaneMenu");

        //Elements side:
        anchorPaneElements = (AnchorPane) splitPaneMain.getItems().get(0);
        anchorPaneElementsMenu = (AnchorPane) anchorPaneElements.lookup("#anchorPaneElementsMenu");
        anchorPaneShapesListWrapper = (AnchorPane) anchorPaneElements.lookup("#anchorPaneShapesListWrapper");


        accordionItems = (Accordion) anchorPaneElements.lookup("#accordionItems");
        titledPaneTabletops = accordionItems.getPanes().get(0);
        titledPaneWallPanels = accordionItems.getPanes().get(1);
        titledPaneWindowsill = accordionItems.getPanes().get(2);
        titledPaneFoot = accordionItems.getPanes().get(3);
        titledPaneSink = accordionItems.getPanes().get(4);
        titledPaneGroove = accordionItems.getPanes().get(5);
        titledPaneRods = accordionItems.getPanes().get(6);
        titledPaneCutouts = accordionItems.getPanes().get(7);
//        tabPaneLeftSide = (TabPane) anchorPaneElements.lookup("#tabPaneLeftSide");
//        tableTopTab = tabPaneLeftSide.getTabs().get(0);
//        wallPanelTab = tabPaneLeftSide.getTabs().get(1);
//        sinkTab = tabPaneLeftSide.getTabs().get(2);
//        groovesTab = tabPaneLeftSide.getTabs().get(3);
//        rodsTab = tabPaneLeftSide.getTabs().get(4);

        initTableTopList();
        initWallPanelList();
        initWindowsillList();
        initFootList();
        initSinkList();
        initGrooveList();
        initRodsList();
        initCutoutsList();

        //SketchPane(Center):
        anchorPaneCenter = (AnchorPane) splitPaneMain.getItems().get(1);
        scrollPaneCenter = (ScrollPane) anchorPaneCenter.lookup("#scrollPaneCenter");
        sketchPane = (Pane) scrollPaneCenter.getContent();
        btnScaleMinus = (Button) anchorPaneMenu.lookup("#btnScaleMinus");
        btnScalePlus = (Button) anchorPaneMenu.lookup("#btnScalePlus");
        btnRotateLeft = (Button) anchorPaneMenu.lookup("#btnRotateLeft");
        btnRotateRight = (Button) anchorPaneMenu.lookup("#btnRotateRight");
        btnAddDimH = (Button) anchorPaneMenu.lookup("#btnAddDimH");
        btnAddDimV = (Button) anchorPaneMenu.lookup("#btnAddDimV");
        btnShapeManager = (Button) anchorPaneMenu.lookup("#btnShapeManager");
        btnPrint = (Button) anchorPaneMenu.lookup("#btnPrint");

        btnCreateUnion = (Button) anchorPaneMenu.lookup("#btnCreateUnion");
        btnCrashUnion = (Button) anchorPaneMenu.lookup("#btnCrashUnion");

        //btnCreateUnion.setDisable(true);
        //btnCrashUnion.setDisable(true);
//        btnRotateLeft.setVisible(false);
//        btnRotateRight.setVisible(false);

        scrollPaneCenter.setPannable(false);
        scrollPaneCenter.setVvalue(0.5);
        scrollPaneCenter.setHvalue(0.5);
        //scrollPaneCenter.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        sketchElementsGroup.getChildren().add(sketchPane);
        scrollPaneCenter.setContent(sketchElementsGroup);

        initSketchPane(SKETCH_PANE_WIDTH, SKETCH_PANE_HEIGHT);

        //Settings side(Right):
        anchorPaneSettings = (AnchorPane) splitPaneMain.getItems().get(2);
        scrollPaneShapeSettings = (ScrollPane) anchorPaneSettings.lookup("#scrollPaneShapeSettings");
        anchorPaneShapeSettings = (AnchorPane) scrollPaneShapeSettings.getContent();

        btnCancelSettings = (Button) anchorPaneSettings.lookup("#btnCancelSettings");
        btnSaveSettings = (Button) anchorPaneSettings.lookup("#btnSaveSettings");

        btnCancelSettings.setVisible(false);
        btnSaveSettings.setVisible(false);

        scrollPaneShapeSettings.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    }

    public void initTableTopList() {

        tableTopListView = new ListView<ListElement>();
        tableTopListView.setId("#tableTopListView");
        tableTopListView.setCellFactory(new CellFactory());
        tableTopListView.getItems().add(new ListElement("name 1", SketchShapeRectangle.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeRectangle.getStaticTooltipForListCell(), ShapeType.RECTANGLE, ElementTypes.TABLETOP));
        tableTopListView.getItems().add(new ListElement("name 2", SketchShapeRectangleCutCorner.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeRectangleCutCorner.getStaticTooltipForListCell(), ShapeType.RECTANGLE_CUT_CORNER, ElementTypes.TABLETOP));
        tableTopListView.getItems().add(new ListElement("name 3", SketchShapeRectangleCircleCorner.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeRectangleCircleCorner.getStaticTooltipForListCell(), ShapeType.RECTANGLE_CIRCLE_CORNER, ElementTypes.TABLETOP));
        tableTopListView.getItems().add(new ListElement("name 4", SketchShapeRectangleCircleCornerInto.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeRectangleCircleCornerInto.getStaticTooltipForListCell(), ShapeType.RECTANGLE_CIRCLE_CORNER_INTO, ElementTypes.TABLETOP));
        tableTopListView.getItems().add(new ListElement("name 5", SketchShapeTriangle.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeTriangle.getStaticTooltipForListCell(), ShapeType.TRIANGLE, ElementTypes.TABLETOP));
        tableTopListView.getItems().add(new ListElement("name 6", SketchShapeCircleHalf.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeCircleHalf.getStaticTooltipForListCell(), ShapeType.CIRCLE_HALF, ElementTypes.TABLETOP));
        tableTopListView.getItems().add(new ListElement("name 7", SketchShapeCircle.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeCircle.getStaticTooltipForListCell(), ShapeType.CIRCLE, ElementTypes.TABLETOP));
        tableTopListView.getItems().add(new ListElement("name 7", SketchShapeTrapeze.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeTrapeze.getStaticTooltipForListCell(), ShapeType.TRAPEZE, ElementTypes.TABLETOP));
        tableTopListView.getItems().add(new ListElement("name 7", SketchShapeRhombus.getStaticViewForListCell(ElementTypes.TABLETOP), SketchShapeRhombus.getStaticTooltipForListCell(), ShapeType.RHOMBUS, ElementTypes.TABLETOP));
        //tableTopListView.getItems().add(new ListElement("name 2", TableTopShapeRectangle.getStaticViewForListCell(), TableTopShapeRectangle.DRAG_SHAPE_DATA_FORMAT, ShapeType.RECTANGLE, ElementTypes.TABLETOP));
        //tableTopListView.getItems().add(new ListElement("name 3", TableTopShapeRectangle.getStaticViewForListCell(), TableTopShapeRectangle.DRAG_SHAPE_DATA_FORMAT, ShapeType.RECTANGLE, ElementTypes.TABLETOP));
        ((AnchorPane) (titledPaneTabletops.getContent())).getChildren().add(tableTopListView);
        AnchorPane.setTopAnchor(tableTopListView, 0.0);
        AnchorPane.setBottomAnchor(tableTopListView, 0.0);
        AnchorPane.setLeftAnchor(tableTopListView, 0.0);
        AnchorPane.setRightAnchor(tableTopListView, 0.0);
    }

    public void initWallPanelList() {

        wallPanelListView = new ListView<ListElement>();
        wallPanelListView.setCellFactory(new CellFactory());
        wallPanelListView.getItems().add(new ListElement("name 1", SketchShapeRectangle.getStaticViewForListCell(ElementTypes.WALL_PANEL), SketchShapeRectangle.getStaticTooltipForListCell(), ShapeType.RECTANGLE, ElementTypes.WALL_PANEL));
        //tableTopListView.getItems().add(new ListElement("name 2", TableTopShapeRectangle.getStaticViewForListCell(), TableTopShapeRectangle.DRAG_SHAPE_DATA_FORMAT, ShapeType.RECTANGLE, ElementTypes.TABLETOP));
        //tableTopListView.getItems().add(new ListElement("name 3", TableTopShapeRectangle.getStaticViewForListCell(), TableTopShapeRectangle.DRAG_SHAPE_DATA_FORMAT, ShapeType.RECTANGLE, ElementTypes.TABLETOP));
        ((AnchorPane) (titledPaneWallPanels.getContent())).getChildren().add(wallPanelListView);
        AnchorPane.setTopAnchor(wallPanelListView, 0.0);
        AnchorPane.setBottomAnchor(wallPanelListView, 0.0);
        AnchorPane.setLeftAnchor(wallPanelListView, 0.0);
        AnchorPane.setRightAnchor(wallPanelListView, 0.0);
    }

    public void initWindowsillList() {

        windowsillListView = new ListView<ListElement>();
        windowsillListView.setCellFactory(new CellFactory());
        windowsillListView.getItems().add(new ListElement("name 1", SketchShapeRectangle.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeRectangle.getStaticTooltipForListCell(), ShapeType.RECTANGLE, ElementTypes.WINDOWSILL));
        windowsillListView.getItems().add(new ListElement("name 2", SketchShapeRectangleCutCorner.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeRectangleCutCorner.getStaticTooltipForListCell(), ShapeType.RECTANGLE_CUT_CORNER, ElementTypes.WINDOWSILL));
        windowsillListView.getItems().add(new ListElement("name 3", SketchShapeRectangleCircleCorner.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeRectangleCircleCorner.getStaticTooltipForListCell(), ShapeType.RECTANGLE_CIRCLE_CORNER, ElementTypes.WINDOWSILL));
        windowsillListView.getItems().add(new ListElement("name 4", SketchShapeRectangleCircleCornerInto.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeRectangleCircleCornerInto.getStaticTooltipForListCell(), ShapeType.RECTANGLE_CIRCLE_CORNER_INTO, ElementTypes.WINDOWSILL));
        windowsillListView.getItems().add(new ListElement("name 5", SketchShapeTriangle.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeTriangle.getStaticTooltipForListCell(), ShapeType.TRIANGLE, ElementTypes.WINDOWSILL));
        windowsillListView.getItems().add(new ListElement("name 6", SketchShapeCircleHalf.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeCircleHalf.getStaticTooltipForListCell(), ShapeType.CIRCLE_HALF, ElementTypes.WINDOWSILL));
        windowsillListView.getItems().add(new ListElement("name 6", SketchShapeCircle.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeCircle.getStaticTooltipForListCell(), ShapeType.CIRCLE, ElementTypes.WINDOWSILL));
        windowsillListView.getItems().add(new ListElement("name 6", SketchShapeTrapeze.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeTrapeze.getStaticTooltipForListCell(), ShapeType.TRAPEZE, ElementTypes.WINDOWSILL));
        windowsillListView.getItems().add(new ListElement("name 6", SketchShapeRhombus.getStaticViewForListCell(ElementTypes.WINDOWSILL), SketchShapeRhombus.getStaticTooltipForListCell(), ShapeType.RHOMBUS, ElementTypes.WINDOWSILL));
        ((AnchorPane) (titledPaneWindowsill.getContent())).getChildren().add(windowsillListView);
        AnchorPane.setTopAnchor(windowsillListView, 0.0);
        AnchorPane.setBottomAnchor(windowsillListView, 0.0);
        AnchorPane.setLeftAnchor(windowsillListView, 0.0);
        AnchorPane.setRightAnchor(windowsillListView, 0.0);
    }

    public void initFootList() {

        footListView = new ListView<ListElement>();
        footListView.setCellFactory(new CellFactory());
        footListView.getItems().add(new ListElement("name 1", SketchShapeRectangle.getStaticViewForListCell(ElementTypes.FOOT), SketchShapeRectangle.getStaticTooltipForListCell(), ShapeType.RECTANGLE, ElementTypes.FOOT));
        ((AnchorPane) (titledPaneFoot.getContent())).getChildren().add(footListView);
        AnchorPane.setTopAnchor(footListView, 0.0);
        AnchorPane.setBottomAnchor(footListView, 0.0);
        AnchorPane.setLeftAnchor(footListView, 0.0);
        AnchorPane.setRightAnchor(footListView, 0.0);
    }

    public void initSinkList() {
        sinkListView = new ListView<FeatureListElement>();
        sinkListView.setId("#sinkListView");
        sinkListView.setCellFactory(new FeaturesCellFactory());

        for (int i = 1; i <= 11; i++) {
            sinkListView.getItems().add(new FeatureListElement("Раковина " + i, Sink.getIconImageForList(i),
                    FeatureType.SINK, i, Sink.getTooltipForList(i)));
        }
        sinkListView.getItems().add(new FeatureListElement("Раковина " + 18, Sink.getIconImageForList(18),
                FeatureType.SINK, 18, Sink.getTooltipForList(18)));


        for (int i = 12; i <= 17; i++) {
            //Sink sink = new Sink(i);
            if (i == 15) continue;//delete this sink from list
            sinkListView.getItems().add(new FeatureListElement("Раковина " + i, Sink.getIconImageForList(i),
                    FeatureType.SINK, i, Sink.getTooltipForList(i)));
        }

        sinkListView.setMaxWidth(120);
        ((AnchorPane) (titledPaneSink.getContent())).getChildren().add(sinkListView);
        AnchorPane.setTopAnchor(sinkListView, 0.0);
        AnchorPane.setBottomAnchor(sinkListView, 0.0);
        AnchorPane.setLeftAnchor(sinkListView, 0.0);
        AnchorPane.setRightAnchor(sinkListView, 0.0);
    }

    public void initGrooveList() {
        groovesListView = new ListView<FeatureListElement>();
        groovesListView.setCellFactory(new FeaturesCellFactory());
        for (int i = 1; i <= 4; i++) {
            //Grooves grooves = new Grooves(i);
            groovesListView.getItems().add(new FeatureListElement("Проточка " + i, Grooves.getIconImageForList(i), FeatureType.GROOVES, i, Grooves.getTooltipForList(i)));
        }

        ((AnchorPane) (titledPaneGroove.getContent())).getChildren().add(groovesListView);
        AnchorPane.setTopAnchor(groovesListView, 0.0);
        AnchorPane.setBottomAnchor(groovesListView, 0.0);
        AnchorPane.setLeftAnchor(groovesListView, 0.0);
        AnchorPane.setRightAnchor(groovesListView, 0.0);
    }

    public void initRodsList() {
        rodsListView = new ListView<FeatureListElement>();
        rodsListView.setCellFactory(new FeaturesCellFactory());
        for (int i = 1; i <= 2; i++) {
            //Rods rods = new Rods(i);
            rodsListView.getItems().add(new FeatureListElement("Прутки " + i, Rods.getIconImageForList(i), FeatureType.RODS, i, Rods.getTooltipForList(i)));
        }

        ((AnchorPane) (titledPaneRods.getContent())).getChildren().add(rodsListView);
        AnchorPane.setTopAnchor(rodsListView, 0.0);
        AnchorPane.setBottomAnchor(rodsListView, 0.0);
        AnchorPane.setLeftAnchor(rodsListView, 0.0);
        AnchorPane.setRightAnchor(rodsListView, 0.0);
    }

    public void initCutoutsList() {
        cutoutsListView = new ListView<FeatureListElement>();
        cutoutsListView.setCellFactory(new FeaturesCellFactory());
        for (int i = 1; i <= 14; i++) {
            //Rods rods = new Rods(i);
            if (i == 8 || i == 9 || i == 10 || i == 11 || i == 12) continue;
            cutoutsListView.getItems().add(new FeatureListElement("Вырез " + i, Cutout.getIconImageForList(i), FeatureType.CUTOUTS, i, Cutout.getTooltipForList(i)));
        }

        ((AnchorPane) (titledPaneCutouts.getContent())).getChildren().add(cutoutsListView);
        AnchorPane.setTopAnchor(cutoutsListView, 0.0);
        AnchorPane.setBottomAnchor(cutoutsListView, 0.0);
        AnchorPane.setLeftAnchor(cutoutsListView, 0.0);
        AnchorPane.setRightAnchor(cutoutsListView, 0.0);
    }

    private void setControlElementsLogic() {

        selectPrintingZoneMode.addListener((observable, oldValue, newValue) -> {
            if (newValue.booleanValue()) {
                sketchPane.getChildren().forEach(node -> node.setMouseTransparent(true));
            } else {
                sketchPane.getChildren().forEach(node -> node.setMouseTransparent(false));
            }
        });

        btnPrint.setOnMouseClicked(event -> selectPrintingType());

        btnCreateUnion.setOnMouseClicked(event -> createUnionShape());
        btnCrashUnion.setOnMouseClicked(event -> crashUnionShape());
        //Main control elements:

        //Settings side:
        scrollPaneShapeSettings.widthProperty().addListener((observable, oldValue, newValue) -> {
            anchorPaneShapeSettings.setPrefWidth(newValue.doubleValue());
        });
        scrollPaneShapeSettings.heightProperty().addListener((observable, oldValue, newValue) -> {
            anchorPaneShapeSettings.setPrefHeight(newValue.doubleValue());
        });


        rootAnchorPaneSketchDesigner.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.COMMAND) {
                multipleSelectionMode = true;
            } else if (event.getCode() == KeyCode.DELETE) {


                for (SketchObject skObj : selectedSketchObjects) {
                    deleteSketchObject(skObj);
                }
                for (LinearDimension ld : allDimensions) {
                    if (ld.isSelected()) {
                        sketchPane.getChildren().remove(ld);
                        allDimensions.remove(ld);
                        break;
                    }
                }
                selectedSketchObjects.clear();

                CutDesigner.getInstance().refreshCutView();


                //MainWindow.refreshListReceipt();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                setAddDimensionsMode(false);
            } else if (event.getCode() == KeyCode.ALT && multipleSelectionMode) {
                multipleSelectionModeEdges = true;
            }
        });
        rootAnchorPaneSketchDesigner.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.COMMAND) {
                multipleSelectionMode = false;
            } else if (event.getCode() == KeyCode.ALT && multipleSelectionModeEdges) {
                multipleSelectionModeEdges = false;
                multipleSelectionMode = false;

                if (selectedEdges.size() == 0) return;
                EdgeManager edgeManager = new EdgeManager(selectedEdges);
                edgeManager.show(sketchPane.getScene(), selectedEdges.get(0));

//                for(SketchEdge edge : selectedEdges){
//                    edge.select(false);
//                }
            }
        });


        connectPointsForDimensionsObservableSet.addListener(new SetChangeListener<CornerConnectPoint>() {
            @Override
            public void onChanged(Change<? extends CornerConnectPoint> change) {
                //System.out.println("connectPointsForDimensionsSetListener = " + connectPointsForDimensionsObservableSet.size());
                if (connectPointsForDimensionsObservableSet.size() == 2) {

                    //create dimenson

                    Iterator<CornerConnectPoint> it = connectPointsForDimensionsObservableSet.iterator();
                    CornerConnectPoint connectPoint1 = it.next();
                    CornerConnectPoint connectPoint2 = it.next();

                    LinearDimension linearDimension = new LinearDimension(connectPoint1, connectPoint2, dimensionType);
                    sketchPane.getChildren().add(linearDimension);
                    linearDimension.show();
                    allDimensions.add(linearDimension);

                    for (SketchShape shape : sketchShapesList) {
                        shape.addDimensionsMode(false);
                    }
                    for (SketchShapeUnion shapeUnion : sketchShapeUnionsList) {
                        shapeUnion.addDimensionsMode(false);
                    }
                    connectPointsForDimensionsObservableSet.clear();
                }
            }
        });
    }

    private void selectPrintingType() {

        Stage selectPrintZoneStage;
        Scene selectPrintZoneScene;
        Window windowOwner = rootAnchorPaneSketchDesigner.getScene().getWindow();

        AnchorPane rootAnchorPane = null;
        Button btnAutoZone = null, btnManualZone = null;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/SketchDesignerPrintZoneSelect.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (rootAnchorPane != null) {
            selectPrintZoneScene = new Scene(rootAnchorPane);

            btnAutoZone = (Button) rootAnchorPane.lookup("#btnAutoZone");
            btnManualZone = (Button) rootAnchorPane.lookup("#btnManualZone");


            selectPrintZoneStage = new Stage();
            selectPrintZoneStage.setTitle("Печать");
            selectPrintZoneStage.initOwner(windowOwner);
            selectPrintZoneStage.setScene(selectPrintZoneScene);
            selectPrintZoneStage.setX(windowOwner.getX() + windowOwner.getWidth() / 2 - selectPrintZoneScene.getWidth() / 2);
            selectPrintZoneStage.setY(windowOwner.getY() + windowOwner.getHeight() / 2 - selectPrintZoneScene.getHeight() / 2);
            selectPrintZoneStage.initModality(Modality.APPLICATION_MODAL);
            selectPrintZoneStage.setResizable(false);

            selectPrintZoneStage.show();

            btnAutoZone.setOnMouseClicked(event -> {
                selectPrintZoneStage.close();
                printSketchDesigner(PrintType.AUTO_ZONE);
            });

            btnManualZone.setOnMouseClicked(event -> {
                selectPrintZoneStage.close();
                unSelectAllShapes();
                //create rectangle print zone:

                selectPrintingZoneMode.setValue(true);
                unSelectAllShapes();
            });
        }


    }

    private void printSketchDesigner(PrintType printType) {

        //automatic zone

        double pixelScale = 4;

        if (printType == PrintType.AUTO_ZONE) {
            double minX = 10000, minY = 10000;
            double maxX = 0, maxY = 0;

            for (Node node : sketchPane.getChildren()) {

                if (node.getBoundsInParent().getMinX() < minX) minX = node.getBoundsInParent().getMinX();
                if (node.getBoundsInParent().getMinY() < minY) minY = node.getBoundsInParent().getMinY();

                if (node.getBoundsInParent().getMaxX() > maxX) maxX = node.getBoundsInParent().getMaxX();
                if (node.getBoundsInParent().getMaxY() > maxY) maxY = node.getBoundsInParent().getMaxY();

            }


            //create snapshot:
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setViewport(new Rectangle2D(minX * pixelScale, minY * pixelScale, maxX - minX, maxY - minY));
            snapshotParameters.setFill(Color.TRANSPARENT);

            snapshotParameters.setTransform(new Scale(pixelScale, pixelScale));

            WritableImage writableImage = new WritableImage((int) Math.rint(pixelScale * (maxX - minX)), (int) Math.rint(pixelScale * ((maxY - minY) + 60)));

            Background sketchPaneBackground = sketchPane.getBackground();
            sketchPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            allDimensions.forEach(linearDimension -> linearDimension.changeColor(Color.BLACK));

            writableImage = sketchPane.snapshot(snapshotParameters, writableImage);

            sketchPane.setBackground(sketchPaneBackground);
            allDimensions.forEach(linearDimension -> linearDimension.changeColor(Dimension.NORMAL_COLOR));
            //WritableImage writableImage = sketchPane.snapshot(snapshotParameters, null);

            //printZone:
            ImageView imageViewForPrint = new ImageView(writableImage);
            ArrayList<Node> nodesForPrinting = new ArrayList<>();
            nodesForPrinting.add(imageViewForPrint);
            PrinterDialog.showPrinterDialog(rootAnchorPaneSketchDesigner.getScene().getWindow(), nodesForPrinting, true);
        } else {
//            System.out.println("selectZoneForPrinting.getX() = " + selectZoneForPrinting.getX());
//            System.out.println("selectZoneForPrinting.getY() = " + selectZoneForPrinting.getY());
//            System.out.println("selectZoneForPrinting.getWidth() = " + selectZoneForPrinting.getWidth());
//            System.out.println("selectZoneForPrinting.getHeight() = " + selectZoneForPrinting.getHeight());

            //create snapshot:
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setViewport(new Rectangle2D(selectZoneForPrinting.getX() * pixelScale, selectZoneForPrinting.getY() * pixelScale, selectZoneForPrinting.getWidth(), selectZoneForPrinting.getHeight()));
            snapshotParameters.setFill(Color.TRANSPARENT);
            snapshotParameters.setTransform(new Scale(pixelScale, pixelScale));

            WritableImage writableImage = new WritableImage((int) Math.rint(pixelScale * selectZoneForPrinting.getWidth()), (int) Math.rint(pixelScale * selectZoneForPrinting.getHeight()));

            Background sketchPaneBackground = sketchPane.getBackground();
            sketchPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            allDimensions.forEach(linearDimension -> linearDimension.changeColor(Color.BLACK));

            writableImage = sketchPane.snapshot(snapshotParameters, writableImage);

            sketchPane.setBackground(sketchPaneBackground);
            allDimensions.forEach(linearDimension -> linearDimension.changeColor(Dimension.NORMAL_COLOR));

            //printZone:
            ImageView imageViewForPrint = new ImageView(writableImage);
            ArrayList<Node> nodesForPrinting = new ArrayList<>();
            nodesForPrinting.add(imageViewForPrint);
            PrinterDialog.showPrinterDialog(rootAnchorPaneSketchDesigner.getScene().getWindow(), nodesForPrinting, true);
        }


        //custom zone
    }


    private void initSketchPane(double width, double height) {

        sketchPane.setPrefSize(width, height);

        sketchPane.setOnMouseClicked(event -> {

            unSelectAllShapes();

            for (LinearDimension ld : allDimensions) {
                ld.selectDimension(false);
            }
        });
        sketchPane.setOnDragOver(event -> {
            //System.out.println("drag over");
            event.acceptTransferModes(TransferMode.MOVE);

        });
        sketchPane.setOnDragDropped(event -> {
            //System.out.println("drag dropped");
            Dragboard db = event.getDragboard();

            if (db.getContent(SketchShape.DRAG_DATA_FORMAT_SHAPE_TYPE) == null) return;
            if (db.getContent(SketchShape.DRAG_DATA_FORMAT_ELEMENT_TYPE) == null) return;

            ShapeType shapeType = (ShapeType) db.getContent(SketchShape.DRAG_DATA_FORMAT_SHAPE_TYPE);

            ElementTypes elementType = (ElementTypes) db.getContent(SketchShape.DRAG_DATA_FORMAT_ELEMENT_TYPE);


            if (shapeType == ShapeType.RECTANGLE) {
                SketchShapeRectangle shape = new SketchShapeRectangle(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            } else if (shapeType == ShapeType.RECTANGLE_CUT_CORNER) {

                SketchShapeRectangleCutCorner shape = new SketchShapeRectangleCutCorner(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            } else if (shapeType == ShapeType.RECTANGLE_CIRCLE_CORNER) {

                SketchShapeRectangleCircleCorner shape = new SketchShapeRectangleCircleCorner(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            } else if (shapeType == ShapeType.RECTANGLE_CIRCLE_CORNER_INTO) {

                SketchShapeRectangleCircleCornerInto shape = new SketchShapeRectangleCircleCornerInto(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            } else if (shapeType == ShapeType.TRIANGLE) {

                SketchShapeTriangle shape = new SketchShapeTriangle(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            } else if (shapeType == ShapeType.CIRCLE_HALF) {

                SketchShapeCircleHalf shape = new SketchShapeCircleHalf(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            } else if (shapeType == ShapeType.CIRCLE) {

                SketchShapeCircle shape = new SketchShapeCircle(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            } else if (shapeType == ShapeType.TRAPEZE) {

                SketchShapeTrapeze shape = new SketchShapeTrapeze(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            } else if (shapeType == ShapeType.RHOMBUS) {

                SketchShapeRhombus shape = new SketchShapeRhombus(event.getX(), event.getY(), elementType, sketchPane);
                sketchPane.getChildren().add(shape);
                SketchDesigner.getSketchShapesList().add(shape);
            }


        });

        sketchPane.setOnMousePressed(event -> {
            if (selectPrintingZoneMode.getValue()) {
                startSelectPrintZoneX = event.getX();
                startSelectPrintZoneY = event.getY();
                selectZoneForPrinting = new Rectangle(startSelectPrintZoneX, startSelectPrintZoneY, 0, 0);
                selectZoneForPrinting.setStroke(Color.GREEN);
                selectZoneForPrinting.setFill(Color.TRANSPARENT);
                sketchPane.getChildren().add(selectZoneForPrinting);
                printingZoneStartSelecting = true;

                return;
            }

            if (event.getButton() == MouseButton.PRIMARY) {
                scrollPaneCenter.setPannable(true);
            }
            for (SketchShape sketchShape : sketchShapesList) {
                sketchShape.deSelectAllEdges();
            }
            selectedEdges.clear();
            selectedEdgeMaterial = null;
        });

        sketchPane.setOnMouseDragged(event -> {
            if (selectPrintingZoneMode.getValue()) {
                if (event.getX() > startSelectPrintZoneX) {
                    selectZoneForPrinting.setWidth(event.getX() - selectZoneForPrinting.getX());
                } else {
                    double rectWidth = startSelectPrintZoneX - event.getX();
                    selectZoneForPrinting.setX(event.getX());
                    selectZoneForPrinting.setWidth(rectWidth);
                }

                if (event.getY() > startSelectPrintZoneY) {
                    selectZoneForPrinting.setHeight(event.getY() - selectZoneForPrinting.getY());
                } else {
                    double rectHeight = startSelectPrintZoneY - event.getY();
                    selectZoneForPrinting.setY(event.getY());
                    selectZoneForPrinting.setHeight(rectHeight);
                }

            }
        });

        sketchPane.setOnMouseReleased(event -> {
            if (selectPrintingZoneMode.getValue()) {

                selectPrintingZoneMode.setValue(false);
                sketchPane.getChildren().remove(selectZoneForPrinting);
                printSketchDesigner(PrintType.CUSTOM_ZONE);
                return;
            }

            if (event.getButton() == MouseButton.PRIMARY) {
                scrollPaneCenter.setPannable(false);
            }
        });

        btnScalePlus.setOnMouseClicked(event -> {
            sketchElementsGroup.setScaleX(sketchElementsGroup.getScaleX() + 0.1);
            sketchElementsGroup.setScaleY(sketchElementsGroup.getScaleY() + 0.1);

        });

        btnScaleMinus.setOnMouseClicked(event -> {
            sketchElementsGroup.setScaleX(sketchElementsGroup.getScaleX() - 0.1);
            sketchElementsGroup.setScaleY(sketchElementsGroup.getScaleY() - 0.1);
        });

        btnRotateLeft.setOnMouseClicked(event -> {

            double angle = -5;
            if (selectedSketchObjects.size() != 1) {
                int selectedSize = selectedSketchObjects.size();
                if (selectedSketchObjects.get(0).isContainInUnion()) {
                    int unionShapesSize = selectedSketchObjects.get(0).getSketchShapeUnionOwner().getSketchShapesInUnion().size();
                    if (selectedSize == unionShapesSize) {
                        selectedSketchObjects.get(0).getSketchShapeUnionOwner().rotate(angle);
                    }
                }
            } else {
                selectedSketchObjects.get(0).rotateShape(angle);
            }

        });
        btnRotateRight.setOnMouseClicked(event -> {

            double angle = 5;
            if (selectedSketchObjects.size() != 1) {
                int selectedSize = selectedSketchObjects.size();
                if (selectedSketchObjects.get(0).isContainInUnion()) {
                    int unionShapesSize = selectedSketchObjects.get(0).getSketchShapeUnionOwner().getSketchShapesInUnion().size();
                    if (selectedSize == unionShapesSize) {
                        selectedSketchObjects.get(0).getSketchShapeUnionOwner().rotate(angle);
                    }
                }
            } else {
                selectedSketchObjects.get(0).rotateShape(angle);
            }

        });

        btnAddDimH.setOnMouseClicked(event -> {
            setAddDimensionsMode(true);
            dimensionType = LinearDimension.HORIZONTAL_TYPE;
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.INFO, "Выберите две точки привязки"));
        });
        btnAddDimV.setOnMouseClicked(event -> {
            setAddDimensionsMode(true);
            dimensionType = LinearDimension.VERTICAL_TYPE;
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.INFO, "Выберите две точки привязки"));
        });

        btnShapeManager.setOnMouseClicked(event -> {
            if (selectedSketchObjects.size() == 1 && selectedSketchObjects.get(0) instanceof SketchShape) {
                ShapeManager.show(selectedSketchObjects.get(0).getScene(), (SketchShape) selectedSketchObjects.get(0), null);
            }
        });

    }

    private void initZoom() {

        scrollPaneCenter.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                System.out.println("scroll x = " + event.getDeltaX() + " y = " + event.getDeltaY());
                System.out.println("workGroup.getScaleX() = " + sketchElementsGroup.getScaleX());
                if (event.getDeltaY() > 0) {
                    if (sketchElementsGroup.getScaleX() <= 5) {
                        sketchElementsGroup.setScaleX(sketchElementsGroup.getScaleX() + 0.1);
                        sketchElementsGroup.setScaleY(sketchElementsGroup.getScaleY() + 0.1);

                        sketchShapesList.forEach(shape -> {
                            shape.setEdgesZoneWidth(5 / (sketchElementsGroup.getScaleX() / 2.0));
                            shape.setWidthConnectPoint(10 / (Math.ceil(sketchElementsGroup.getScaleX() / 2)));
                        });
                    }
                } else if (event.getDeltaY() < 0) {
                    if (sketchElementsGroup.getScaleX() >= 0.3) {
                        sketchElementsGroup.setScaleX(sketchElementsGroup.getScaleX() - 0.1);
                        sketchElementsGroup.setScaleY(sketchElementsGroup.getScaleY() - 0.1);

                        sketchShapesList.forEach(shape -> {
                            shape.setEdgesZoneWidth(5 / (sketchElementsGroup.getScaleX() / 2.0));
                            shape.setWidthConnectPoint(10 / (Math.ceil(sketchElementsGroup.getScaleX() / 2)));
                        });
                    }
                }
                event.consume();
            }
        });
    }

    public static boolean getSelectionModeForEdges() {
        return multipleSelectionModeEdges;
    }

    public static ArrayList<SketchEdge> getSelectedEdges() {
        return selectedEdges;
    }

    public static Material getSelectedEdgeMaterial() {
        return selectedEdgeMaterial;
    }

    public static void setSelectedEdgeMaterial(Material selectedEdgeMaterial) {
        SketchDesigner.selectedEdgeMaterial = selectedEdgeMaterial;
    }

    public static void setDraggedShape(SketchObject draggedShape) {
        SketchDesigner.draggedShape = draggedShape;
    }

    public static Pane getSketchPane() {
        return sketchPane;
    }

    public static SketchObject getDraggedShape() {
        return draggedShape;
    }

    public static void unSelectAllShapes() {
        selectedSketchObjects.clear();
        for (Node shape : sketchPane.getChildren()) {

            if (shape instanceof SketchObject) {
                ((SketchObject) shape).unSelectShape();
            }

        }
        hideShapeSettings();
    }

    public static void allDimensionsToFront() {
        for (int i = 0; i < sketchPane.getChildren().size(); i++) {
            if (sketchPane.getChildren().get(i) instanceof Dimension) {
                sketchPane.getChildren().get(i).toFront();
                //System.out.println(sketchPane.getChildren().get(i));
            }
        }
    }

    public static void showShapeSettings() {
        rootAnchorPaneSketchDesigner.requestFocus();
        if (selectedSketchObjects.get(0) instanceof SketchShape) {
            AnchorPane shapeSettings = ((SketchShape) selectedSketchObjects.get(0)).getShapeSettings();
            anchorPaneShapeSettings.getChildren().clear();
            anchorPaneShapeSettings.getChildren().add(shapeSettings);
            AnchorPane.setTopAnchor(shapeSettings, 0.0);
            AnchorPane.setBottomAnchor(shapeSettings, 0.0);
            AnchorPane.setLeftAnchor(shapeSettings, 0.0);
            AnchorPane.setRightAnchor(shapeSettings, 0.0);

            btnSaveSettings.setVisible(true);
            btnCancelSettings.setVisible(true);

            btnSaveSettings.setOnMouseClicked(event -> selectedSketchObjects.get(0).shapeSettingsSaveBtnClicked());
            btnCancelSettings.setOnMouseClicked(event -> selectedSketchObjects.get(0).shapeSettingsCancelBtnClicked());
        }

    }


    public static Button getBtnSaveSettings() {
        return btnSaveSettings;
    }

    public static Button getBtnCancelSettings() {
        return btnCancelSettings;
    }

    public static boolean isAddDimensionsMode() {
        return addDimensionsMode;
    }


    public static void setAddDimensionsMode(boolean addDimensionsMode) {
        SketchDesigner.addDimensionsMode = addDimensionsMode;

        if (SketchDesigner.addDimensionsMode) {
            for (SketchShape shape : sketchShapesList) {
                if (!shape.isContainInUnion()) {
                    shape.addDimensionsMode(true);
                    shape.setMouseTransparent(false);
                }
            }
            for (SketchShapeUnion shapeUnion : sketchShapeUnionsList) {
//                shapeUnion.addDimensionsMode(true);
//                shapeUnion.setMouseTransparent(false);
            }
            //sketchPane.setMouseTransparent(true);
        } else {

            for (SketchShape shape : sketchShapesList) {
                if (!shape.isContainInUnion()) {
                    shape.addDimensionsMode(false);
                    shape.setMouseTransparent(true);
                }
            }
            for (SketchShapeUnion shapeUnion : sketchShapeUnionsList) {
//                shapeUnion.addDimensionsMode(false);
//                shapeUnion.setMouseTransparent(true);
            }
            //sketchPane.setMouseTransparent(false);
            connectPointsForDimensionsObservableSet.clear();

        }

        System.out.println("SketchDesigner: Add dimensions mode = " + addDimensionsMode);
    }

    public static void addPointForDimension(CornerConnectPoint point) {
        connectPointsForDimensionsObservableSet.add(point);
    }

    public static ArrayList<LinearDimension> getAllDimensions() {
        return allDimensions;
    }

    public static void hideShapeSettings() {
        anchorPaneShapeSettings.getChildren().clear();

        btnSaveSettings.setVisible(false);
        btnCancelSettings.setVisible(false);
    }

    public static ArrayList<SketchShape> getSketchShapesList() {
        return sketchShapesList;
    }

    public static ArrayList<SketchShapeUnion> getSketchShapeUnionsList() {
        return sketchShapeUnionsList;
    }


    public static void createUnionShape() {

        SketchShapeUnion sketchShapeUnion = SketchShapeUnion.createSketchShapeUnion(selectedSketchObjects);
        if (sketchShapeUnion != null) {
            for (SketchShape sketchShape : sketchShapeUnion.getSketchShapesInUnion()) {
                CutShape cutShape = sketchShape.getCutShape();
                CutDesigner.getInstance().getCutPane().deleteCutShape(cutShape);
                CutDesigner.getInstance().usedShapesNumberList.remove(cutShape.getShapeNumber());
                sketchShape.edgesDisable(true);
                sketchShape.setSaveMaterialImageOnEdges(true);
            }

            SketchDesigner.getSketchShapeUnionsList().add(sketchShapeUnion);
            //sketchPane.getChildren().add(sketchShapeUnion);

        }


    }

    public static void crashUnionShape() {

        System.out.println("Crash Union");
        int selectedSize = selectedSketchObjects.size();
        if (selectedSketchObjects.get(0).isContainInUnion()) {
            int unionShapesSize = selectedSketchObjects.get(0).getSketchShapeUnionOwner().getSketchShapesInUnion().size();
            if (selectedSize == unionShapesSize) {

                for (SketchShape sketchShape : selectedSketchObjects) {
                    CutDesigner.getInstance().getCutPane().deleteCutShape(sketchShape.getShapeNumber());
                }


                sketchShapeUnionsList.remove((selectedSketchObjects.get(0)).getSketchShapeUnionOwner());

                for (SketchShape sketchShape : ((SketchShape) selectedSketchObjects.get(0)).getSketchShapeUnionOwner().getSketchShapesInUnion()) {
                    sketchShape.setSketchShapeUnionOwner(false, null);
                    sketchShape.unSelectShape();
                    sketchShape.edgesDisable(false);
                }
                System.out.println("sketchShapeUnionsList = " + sketchShapeUnionsList);
                selectedSketchObjects.clear();

            } else {
                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Выберите одно объединение", null);
            }
        } else {
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Выберите одно объединение", null);
        }


    }

    public void deleteSketchObject(SketchObject sketchObject) {
        if (sketchObject instanceof SketchShape) {
            CutDesigner.getInstance().getCutPane().deleteCutShape(((SketchShape) sketchObject).getCutShapeWithoutRefresh());
            sketchShapesList.remove(sketchObject);

            System.out.println("DELETE SHAPE");

            SketchShape sketchShape = (SketchShape) sketchObject;
            if (sketchShape.isContainInUnion()) {
                SketchDesigner.getSketchShapeUnionsList().remove(sketchShape.getSketchShapeUnionOwner());
                CutDesigner.getInstance().usedShapeUnionsNumberList.remove(sketchShape.getSketchShapeUnionOwner().getUnionNumber());
            }
            //CutDesigner.getCutPane().deleteCutShape(sh.getShapeNumber());
        }
        sketchPane.getChildren().remove(sketchObject);

        hideShapeSettings();

        CutDesigner.getInstance().refreshCutView();
    }


    public static SketchShape getSketchShape(int shapeNumber, ElementTypes elementTypes) {
        //if(elementType == ElementTypes.TABLETOP){
        for (SketchShape shape : sketchShapesList) {
            if (shape.getShapeNumber() == shapeNumber) {
                return shape;
            }
        }
        //}
        return null;
    }

    public static SketchShape getSketchShape(int shapeNumber) {
        //if(elementType == ElementTypes.TABLETOP){
        for (SketchShape shape : sketchShapesList) {
            if (shape.getShapeNumber() == shapeNumber) {
                return shape;
            }
        }
        //}
        return null;
    }

    public static SketchShapeUnion getSketchShapeUnion(int shapeUnionNumber) {
        //if(elementType == ElementTypes.TABLETOP){
        for (SketchShapeUnion shape : sketchShapeUnionsList) {
            if (shape.getUnionNumber() == shapeUnionNumber) {
                return shape;
            }
        }
        //}
        return null;
    }

}

enum PrintType {
    AUTO_ZONE,
    CUSTOM_ZONE
}
