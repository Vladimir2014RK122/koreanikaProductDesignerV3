package ru.koreanika.cutDesigner;


import ru.koreanika.Common.ConnectPoints.CornerConnectPoint;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.RepresentToJson;

import ru.koreanika.cutDesigner.ListStatistics.StatisticCellItem;
import ru.koreanika.cutDesigner.ListStatistics.StatisticsCellFactory;
import ru.koreanika.cutDesigner.Shapes.*;

import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;

import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

import org.json.simple.JSONObject;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.Dimensions.LinearDimension;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.PrinterHandler.PrinterDialog;
import ru.koreanika.utils.ProjectHandler;

import java.io.IOException;
import java.util.*;

public class CutDesigner implements RepresentToJson {

    public static final DataFormat CELL_FORMAT = new DataFormat("CELL_FORMAT");
    public static final DataFormat SHAPE_NUMBER_DATA_FORMAT = new DataFormat("SHAPE_NUMBER_DATA_FORMAT");
    public static final DataFormat ELEMENT_DATA_FORMAT = new DataFormat("ELEMENT_TYPE_DATA_FORMAT");

    //for feature in Treeview
    public static final DataFormat SHAPE_OWNER_DF = new DataFormat("SHAPE_OWNER_DF");
    public static final DataFormat FEATURE_NUMBER_DF = new DataFormat("FEATURE_NUMBER_DF");

    public static double CUT_SHAPES_CUTSHIFT = 2.0 * ProjectHandler.getCommonShapeScale();//shift between cut shapes for cutting it


    private static CutDesigner cutDesigner;
    private final EventBus eventBus;

    private AnchorPane anchorPaneCutDesignerRoot;
    private AnchorPane anchorPaneRootCutShapeInfo;

    //SplitPane splitPaneMain;


    //Menu:
    AnchorPane anchorPaneMenu;
    Button btnPrint;
    Button btnAutoCut;
    Button btnRotateLeft, btnRotateRight, btnRotateCustom;
    Button btnAddDimH, btnAddDimV;
    Button btnUpdateStatistics;

    private ChoiceBox<String> choiceBoxAddMaterialSheet;
    private Button btnAddMaterialSheet;

    //Work pane zone:
//    AnchorPane anchorPaneWorkPane;
    private ScrollPane scrollPaneWorkPane;
    //private static Pane underCutPane = new Pane();
    private CutPane cutPane;
    private Group rootGroup;


    //Info zone
    private Label labelInfoMaterial, labelInfoSize;
    //private static SplitPane splitPaneInfo;
    //private static ScrollPane scrollPaneStatistics;
    //private static AnchorPane anchorPaneShapeInfo;
    //private static ScrollPane ScrollPaneShapeInfo;
//    private Label labelShapeNumber, labelShapeMaterial, labelDepth, labelShapeWidth, labelShapeHeight;
//    private Label labelNoInfo;
    //private static TableView<MaterialSheetInfoRow> tableViewStatistics;

    ListView<StatisticCellItem> listViewStatistics;
    //private ArrayList<MaterialSheetInfoRow> materialSheetInfoRowsList = new ArrayList<>();
    private ArrayList<StatisticCellItem> materialSheetStatisticsListObservable = new ArrayList<>();


//    static AnchorPane anchorPaneTreeViewShapes;

//    static TreeView<TreeCellProjectElement> treeViewProjectElements;
//    static TabPane tabPaneRoot;

    //    public static ArrayList<CutSheet> sheetsList = new ArrayList<>();
    public ArrayList<Integer> usedShapesNumberList = new ArrayList<>();
    public ArrayList<Integer> usedShapeUnionsNumberList = new ArrayList<>();

    public ObservableList<CutObject> selectedShapes = FXCollections.observableArrayList();
    public boolean multipleSelectionMode = false;

    //cut shapes objects:
    private ArrayList<CutShape> cutShapesList = new ArrayList<>();
    private ArrayList<CutShapeEdge> cutShapeEdgesList = new ArrayList<>();
    private ArrayList<CutShapeUnion> cutShapeUnionsList = new ArrayList<>();
    private ArrayList<CutShapeAdditionalFeature> cutShapeAdditionalFeaturesList = new ArrayList<>();

    //Dimensions
    private boolean addDimensionsMode = false;
    private int dimensionType;
    private LinkedHashSet<CornerConnectPoint> connectPointsForDimensionsSet = new LinkedHashSet<>();
    private ObservableSet<CornerConnectPoint> connectPointsForDimensionsObservableSet = FXCollections.observableSet(connectPointsForDimensionsSet);
    private ArrayList<LinearDimension> allDimensions = new ArrayList<>();


    Scale cutPaneScale = new Scale();

    private CutDesigner() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/CutDesigner/cutDesigner.fxml"));
        try {
            anchorPaneCutDesignerRoot = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/CutDesigner/cutShapeInfo.fxml"));
        try {
            anchorPaneRootCutShapeInfo = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        initControlElements();
        setControlElementsLogic();

        usedShapesNumberList.clear();
        usedShapeUnionsNumberList.clear();
        selectedShapes.clear();
        multipleSelectionMode = false;
        cutShapesList.clear();
        cutShapeEdgesList.clear();
        cutShapeUnionsList.clear();
        cutShapeAdditionalFeaturesList.clear();

        //initZoom();
        //createNewTab();
        //initZoom();

        eventBus = ServiceLocator.getService("EventBus", EventBus.class);
    }

    public synchronized static CutDesigner getInstance() {


        if (cutDesigner == null) {
            cutDesigner = new CutDesigner();
        }
        return cutDesigner;
    }

    public CutDesigner createNewCutDesigner() {

        cutDesigner = new CutDesigner();

        return cutDesigner;
    }


    public ArrayList<CutShape> getCutShapesList() {
        return cutShapesList;
    }

    public ArrayList<CutShapeEdge> getCutShapeEdgesList() {
        return cutShapeEdgesList;
    }

    public ArrayList<CutShapeUnion> getCutShapeUnionsList() {
        return cutShapeUnionsList;
    }

    public ArrayList<CutShapeAdditionalFeature> getCutShapeAdditionalFeaturesList() {
        return cutShapeAdditionalFeaturesList;
    }

    public void unSelectAllShapes() {
        cutPane.unSelectAllShapes();
    }

    public void addPointForDimension(CornerConnectPoint cornerConnectPoint) {
        connectPointsForDimensionsObservableSet.add(cornerConnectPoint);
    }

    public ArrayList<LinearDimension> getAllDimensions() {
        return allDimensions;
    }

    public CutPane getCutPane() {
        return cutPane;
    }

    public CutShape getCutShape(int shapeNumber, ElementTypes elementType) {
        for (CutShape cutShape : cutShapesList) {
            if (cutShape.getShapeNumber() == shapeNumber && cutShape.getElementType() == elementType) {
                return cutShape;
            }
        }
        return null;
    }

    public AnchorPane getRootAnchorPaneCutDesigner() {

//        if(ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE){
//            if(splitPaneMain.getItems().size() == 1)splitPaneMain.getItems().add(0, anchorPaneTreeViewShapes);
//
//
//        }else{
//            if(splitPaneMain.getItems().size() == 2)splitPaneMain.getItems().remove(0);
//            //receipt zone:
//            //anchorPaneReceiptRoot = (AnchorPane)splitPaneRoot.getItems().get(0);
//        }

        return anchorPaneCutDesignerRoot;
    }

    public void initControlElements() {
//        anchorPaneStatistics = (AnchorPane) anchorPaneCutDesignerRoot.lookup("#anchorPaneStatistics");
        selectedShapes.clear();

        //menu:
        anchorPaneMenu = (AnchorPane) anchorPaneCutDesignerRoot.lookup("#anchorPaneCutDesignerRoot");

        listViewStatistics = (ListView<StatisticCellItem>) anchorPaneCutDesignerRoot.lookup("#listViewStatistics");
        listViewStatistics.setCellFactory(new StatisticsCellFactory());
        scrollPaneWorkPane = (ScrollPane) anchorPaneCutDesignerRoot.lookup("#scrollPaneWorkPane");
        cutPane = new CutPane(this);
        rootGroup = new Group(cutPane);

        scrollPaneWorkPane.setContent(rootGroup);
        scrollPaneWorkPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneWorkPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        choiceBoxAddMaterialSheet = (ChoiceBox<String>) anchorPaneMenu.lookup("#choiceBoxAddMaterialSheet");


        btnAddMaterialSheet = (Button) anchorPaneMenu.lookup("#btnAddMaterialSheet");

        //Menu:
        btnPrint = (Button) anchorPaneMenu.lookup("#btnPrint");
        btnAutoCut = (Button) anchorPaneMenu.lookup("#btnAutoCut");

        btnRotateLeft = (Button) anchorPaneMenu.lookup("#btnRotateLeft");
        btnRotateRight = (Button) anchorPaneMenu.lookup("#btnRotateRight");
        btnRotateCustom = (Button) anchorPaneMenu.lookup("#btnRotateCustom");
        btnAddDimH = (Button) anchorPaneMenu.lookup("#btnAddDimH");
        btnAddDimV = (Button) anchorPaneMenu.lookup("#btnAddDimV");
        btnUpdateStatistics = (Button) anchorPaneMenu.lookup("#btnUpdateStatistics");

        labelInfoMaterial = (Label) anchorPaneMenu.lookup("#labelInfoMaterial");
        labelInfoSize = (Label) anchorPaneMenu.lookup("#labelInfoSize");
//        btnUpdateStatistics.setVisible(false);
//        btnPrint.setVisible(false);
        btnRotateCustom.setVisible(false);
        btnAddDimH.setVisible(false);
        btnAddDimV.setVisible(false);

        choiceBoxAddMaterialSheet.setVisible(false);
        btnAddMaterialSheet.setVisible(false);
        btnUpdateStatistics.setVisible(false);
//        treeViewProjectElements = (TreeView<TreeCellProjectElement>) anchorPaneTreeViewShapes.lookup("#treeViewProjectElements");
//        createElementsTreeView();

    }

    private void setControlElementsLogic() {
        scrollPaneWorkPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            //cutPane.setPrefWidth(newValue.doubleValue());
            cutPane.setPrefWidth(newValue.doubleValue() * cutPaneScale.getX());
        });
        scrollPaneWorkPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            //cutPane.setPrefHeight(newValue.doubleValue());
            cutPane.setPrefHeight(newValue.doubleValue() * cutPaneScale.getX());
        });

//        scrollPaneStatistics.heightProperty().addListener((observable, oldValue, newValue) -> {
//            tableViewStatistics.setPrefHeight(newValue.doubleValue());
//        });


        btnAddMaterialSheet.setOnMouseClicked(event -> {

//            Material materialForSheet = null;
//            int depth = 0;
//            for(Material material : ProjectHandler.getMaterialsListInProject()){
//                String[] nameDepth = material.getName().split("#");
//                String[] nameArray = nameDepth[0].split("\\$");
//
//                depth = Integer.parseInt(nameDepth[1]);
//                if (choiceBoxAddMaterialSheet.getSelectionModel().getSelectedItem().equals(nameArray[1] + " " + nameArray[2] + " - " + nameDepth[1] + "мм")) {
//                    materialForSheet = material;
//                    break;
//                }
//            }
//
//            if(materialForSheet == null || depth == 0) return;
//
//            boolean successAdded = false;
//            for(Material.MaterialSheet materialSheet : materialForSheet.getAvailableAdditionalSheets()){
//                if(materialSheet.getSheetDepth() == depth){
//                    if(cutPane.getUsedMaterialSheetsList().contains(materialSheet)){
//                        cutPane.addMaterialSheet(materialSheet);
//                        successAdded = true;
//                        break;
//                    }
//                }
//
//            }
//
//            if(!successAdded){
//                cutPane.addMaterialSheet(materialForSheet.createMainMaterialSheet(depth));
//            }


            for (String materialName : ProjectHandler.getMaterialsUsesInProjectObservable()) {
                String[] nameDepth = materialName.split("#");
                String[] nameArray = nameDepth[0].split("\\$");
                if (choiceBoxAddMaterialSheet.getSelectionModel().getSelectedItem().equals(nameArray[2] + " " + nameArray[3] + " - " + nameDepth[1] + "мм")) {
                    Material.MaterialSheet materialSheet = cutPane.addMaterialSheet(materialName);

                    if(materialSheet == null){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Добавление листа");
                        alert.setHeaderText("Добавление листа невозможно");
                        alert.setContentText("Количество доступных листов закончилось");
                        alert.show();
                    }
                    break;
                }
            }
            //cutPane.addMaterialSheet(choiceBoxAddMaterialSheet.getSelectionModel().getSelectedItem());
        });

        btnPrint.setOnMouseClicked(event -> printCutDesigner());

        btnAutoCut.setOnAction(actionEvent -> autoCutting(false));

        btnRotateRight.setOnMouseClicked(event -> {
            double angle = 30;

            if (selectedShapes.size() != 0) {


                if (selectedShapes.get(0) instanceof CutShapeEdge && ((CutShapeEdge) selectedShapes.get(0)).getOwner().isSaveMaterialImage()) {
                    ((CutShapeEdge) selectedShapes.get(0)).getOwner().selectShape();
                    selectedShapes.set(0, ((CutShapeEdge) selectedShapes.get(0)).getOwner());
                    //((CutShapeEdge)selectedShapes.get(0)).getOwner().rotateShapeLocal(30);
                }
                if (selectedShapes.get(0) instanceof CutShape) {

                    CutShape cutShape = (CutShape) selectedShapes.get(0);
                    if (cutShape.isContainInUnion()) {
                        CutShapeUnion cutShapeUnion = cutShape.getCutShapeUnionOwner();
                        cutShapeUnion.rotate(angle);
                    } else {
                        selectedShapes.get(0).rotateShapeLocal(angle);
                    }
                } else if (selectedShapes.get(0) instanceof CutShapeEdge && !((CutShapeEdge) selectedShapes.get(0)).getOwner().isSaveMaterialImage()) {
                    selectedShapes.get(0).rotateShapeLocal(angle);
                } else if (selectedShapes.get(0) instanceof CutShapeAdditionalFeature) {
                    selectedShapes.get(0).rotateShapeLocal(angle);
                }

                selectedShapes.get(0).selectShape();
            }
        });

        btnRotateLeft.setOnMouseClicked(event -> {
            System.out.println("rotate BTN");
            double angle = -30;


            if (selectedShapes.size() != 0) {

                if (selectedShapes.get(0) instanceof CutShapeEdge && ((CutShapeEdge) selectedShapes.get(0)).getOwner().isSaveMaterialImage()) {
                    ((CutShapeEdge) selectedShapes.get(0)).getOwner().selectShape();
                    selectedShapes.set(0, ((CutShapeEdge) selectedShapes.get(0)).getOwner());
                    //((CutShapeEdge)selectedShapes.get(0)).getOwner().rotateShapeLocal(30);
                }
                if (selectedShapes.get(0) instanceof CutShape) {
                    CutShape cutShape = (CutShape) selectedShapes.get(0);
                    System.out.println("rotate SHAPE");
                    if (cutShape.isContainInUnion()) {
                        CutShapeUnion cutShapeUnion = cutShape.getCutShapeUnionOwner();
                        cutShapeUnion.rotate(angle);
                    } else {
                        selectedShapes.get(0).rotateShapeLocal(angle);
                    }
                } else if (selectedShapes.get(0) instanceof CutShapeEdge && !((CutShapeEdge) selectedShapes.get(0)).getOwner().isSaveMaterialImage()) {
                    selectedShapes.get(0).rotateShapeLocal(angle);
                } else if (selectedShapes.get(0) instanceof CutShapeAdditionalFeature) {
                    selectedShapes.get(0).rotateShapeLocal(angle);
                }

                selectedShapes.get(0).selectShape();
            }
        });

        btnRotateCustom.setOnMouseClicked(event -> {
            //unused
        });

        btnAddDimH.setOnMouseClicked(event -> {

            setAddDimensionsMode(true);
            dimensionType = LinearDimension.HORIZONTAL_TYPE;
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.INFO, "Выберите две точки привязки"));
            //show all connect points
            //select 2 points
            //addDimensionForTwoPoints
        });
        btnAddDimV.setOnMouseClicked(event -> {
            //show all connect points
            //select 2 points
            //addDimensionForTwoPoints
            setAddDimensionsMode(true);
            dimensionType = LinearDimension.VERTICAL_TYPE;
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.INFO, "Выберите две точки привязки"));
        });

        btnUpdateStatistics.setOnMouseClicked(event -> {
            updateStatistics();
        });

        anchorPaneCutDesignerRoot.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.DELETE) {
//
//                for (CutObject cutObject : selectedShapes) {
//                    CutShape cutShape = null;
//                    if (cutObject instanceof CutShape) {
//                        cutShape = (CutShape) cutObject;
//                        cutPane.deleteCutShape(cutShape);
//                    } else if (cutObject instanceof CutShapeEdge) {
//                        cutShape = ((CutShapeEdge) cutObject).getOwner();
//                        cutPane.deleteCutShape(cutShape);
//                    } else if (cutObject instanceof CutShapeAdditionalFeature) {
//                        cutPane.deleteCutShape(cutObject);
//                    }
//
//                }
//
//                cutPane.unSelectAllShapes();
//            }
        });


        connectPointsForDimensionsObservableSet.addListener(new SetChangeListener<CornerConnectPoint>() {
            @Override
            public void onChanged(Change<? extends CornerConnectPoint> change) {
                System.out.println("connectPointsForDimensionsSetListener = " + connectPointsForDimensionsObservableSet.size());
                if (connectPointsForDimensionsObservableSet.size() == 2) {

                    //create dimenson

                    Iterator<CornerConnectPoint> it = connectPointsForDimensionsObservableSet.iterator();
                    CornerConnectPoint connectPoint1 = it.next();
                    CornerConnectPoint connectPoint2 = it.next();

                    LinearDimension linearDimension = new LinearDimension(connectPoint1, connectPoint2, dimensionType);

                    cutPane.getChildren().add(linearDimension);
                    linearDimension.show();
                    allDimensions.add(linearDimension);
                    linearDimension.setDimensionColor(Color.BLACK);
                    for (CutShape shape : cutShapesList) {
                        shape.addDimensionsMode(false);
                    }
                    connectPointsForDimensionsObservableSet.clear();
                }
            }
        });

        listViewStatistics.getSelectionModel().selectedItemProperty().addListener((observableValue, statisticCellItem, newValue) -> {


            for(StatisticCellItem item : listViewStatistics.getItems()){
                item.changeMaximize(false);
            }
            if(newValue == null)return;
            newValue.changeMaximize(true);
        });

        selectedShapes.addListener((ListChangeListener<? super CutObject>) change -> {

            if(selectedShapes.size() != 0){
                labelInfoMaterial.setText(selectedShapes.get(0).getMaterial().getReceiptName());
                labelInfoSize.setText(selectedShapes.get(0).getSizesInfo());
            }

        });
    }

    private void printCutDesigner() {
        ArrayList<Node> materialsListForPrinting = new ArrayList<>();

        double pixelScale = 2;

        for (Material.MaterialSheet materialSheet : cutPane.getUsedMaterialSheetsList()) {

            SnapshotParameters materialSheetSnapshotParameters = new SnapshotParameters();
            //materialSheetSnapshotParameters.setFill(Color.TRANSPARENT);
            materialSheetSnapshotParameters.setViewport(new Rectangle2D(materialSheet.getTranslateX() * pixelScale, materialSheet.getTranslateY() * pixelScale, materialSheet.getWidth(), materialSheet.getHeight()));
            materialSheetSnapshotParameters.setTransform(new Scale(pixelScale, pixelScale));

            WritableImage materialSheetWritableImage = new WritableImage((int) Math.rint(pixelScale * materialSheet.getWidth()), (int) Math.rint(pixelScale * materialSheet.getHeight()));
            materialSheetWritableImage = cutPane.snapshot(materialSheetSnapshotParameters, materialSheetWritableImage);
            ImageView materialSheetImageView = new ImageView(materialSheetWritableImage);

            Text materialName = new Text(materialSheet.getMaterial().getReceiptName());
//            materialName.setPrefWidth(300);
//            materialName.setPrefHeight(100);

//            materialName.setTextFill(Color.BLACK);
            materialName.setFont(Font.font(15));

            Pane pane = new Pane();
            pane.setPrefSize(100, 100);
            pane.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

            pane.getChildren().add(materialName);
            materialName.setTranslateX(0);
            materialName.setTranslateY(15);

            pane.getChildren().add(materialSheetImageView);
            materialSheetImageView.setTranslateX(0);
            materialSheetImageView.setTranslateY(20);


            //result:
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            snapshotParameters.setViewport(new Rectangle2D(0, 0, materialSheet.getWidth(), materialSheet.getHeight() + 30));
            snapshotParameters.setTransform(new Scale(pixelScale, pixelScale));

            System.out.println(pane.getBoundsInLocal().getWidth());
            System.out.println(pane.getBoundsInLocal().getHeight());
            WritableImage writableImage = new WritableImage((int) Math.rint(pixelScale * materialSheetWritableImage.getWidth()), (int) Math.rint(pixelScale * (materialSheetWritableImage.getHeight() + 60)));
            writableImage = pane.snapshot(snapshotParameters, writableImage);
            ImageView imageView = new ImageView(writableImage);

            materialsListForPrinting.add(pane);
        }

        PrinterDialog.showPrinterDialog(anchorPaneCutDesignerRoot.getScene().getWindow(), materialsListForPrinting, true);
    }

    public void setAddDimensionsMode(boolean addDimensionsMode) {
        this.addDimensionsMode = addDimensionsMode;

        if (addDimensionsMode) {
            for (CutShape shape : cutShapesList) {
                shape.addDimensionsMode(true);
                shape.setMouseTransparent(false);

            }
            //sketchPane.setMouseTransparent(true);
        } else {

            for (CutShape shape : cutShapesList) {
                shape.addDimensionsMode(false);
                shape.setMouseTransparent(true);

            }
            //sketchPane.setMouseTransparent(false);
            connectPointsForDimensionsObservableSet.clear();

        }

        //System.out.println("SketchDesigner: Add dimensions mode = " + addDimensionsMode );
    }

    //    public static void updateMaterialsInProject(){
////        for(CutSheet cutSheet : sheetsList){
////            cutSheet.refreshMaterialChoiceBox();
////        }
//    }
    private void initZoom() {
        rootGroup.getTransforms().add(cutPaneScale);
        scrollPaneWorkPane.setPannable(true);
        scrollPaneWorkPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                System.out.println("scroll x = " + event.getDeltaX() + " y = " + event.getDeltaY());
                System.out.println("cutPaneScale.getX() = " + cutPaneScale.getX());
                if (event.getDeltaY() > 0) {
                    if (cutPaneScale.getX() <= 10) {
//                        rootGroup.setScaleX(rootGroup.getScaleX() + 0.1);
//                        rootGroup.setScaleY(rootGroup.getScaleY() + 0.1);
                        cutPaneScale.setX(cutPaneScale.getX() + 0.1);
                        cutPaneScale.setY(cutPaneScale.getY() + 0.1);


                        getCutShapesList().forEach(cutShape -> {
                            if (cutShape.getPolygon().getStrokeWidth() > 0.1) {
                                cutShape.getPolygon().setStrokeWidth(cutShape.getPolygon().getStrokeWidth() - 0.05);
                            }

                        });
                        getCutShapeEdgesList().forEach(cutShape -> {
                            if (cutShape.getPolygon().getStrokeWidth() > 0.1) {
                                cutShape.getPolygon().setStrokeWidth(cutShape.getPolygon().getStrokeWidth() - 0.05);
                            }

                        });

                    }
                } else if (event.getDeltaY() < 0) {
                    if (cutPaneScale.getX() >= 1.1) {
                        cutPaneScale.setX(cutPaneScale.getX() - 0.1);
                        cutPaneScale.setY(cutPaneScale.getY() - 0.1);
//                        rootGroup.setScaleX(rootGroup.getScaleX() - 0.1);
//                        rootGroup.setScaleY(rootGroup.getScaleY() - 0.1);
                        getCutShapesList().forEach(cutShape -> {
                            if (cutShape.getPolygon().getStrokeWidth() < 1) {
                                cutShape.getPolygon().setStrokeWidth(cutShape.getPolygon().getStrokeWidth() + 0.05);
                            }
                        });
                        getCutShapeEdgesList().forEach(cutShape -> {
                            if (cutShape.getPolygon().getStrokeWidth() < 1) {
                                cutShape.getPolygon().setStrokeWidth(cutShape.getPolygon().getStrokeWidth() + 0.05);
                            }
                        });
                    }
                }


                //CutPane.setCutPaneScale(cutPaneScale.getX());
//                double cutPaneScale = CutPane.getCutPaneScale();
                //scrollPaneWorkPane.setHmax(cutPane.getPrefWidth()*cutPaneScale);
                //cutPane.setPrefWidth(1000*cutPaneScale);
//                scrollPaneWorkPane.setFitToWidth(true);
//                scrollPaneWorkPane.setFitToHeight(true);
                //scrollPaneWorkPane.setScaleShape(true);
                //scrollPaneWorkPane.setVmax(150);
                System.out.println("cutPane.getOriginalCutPaneWidth() = " + cutPane.getOriginalCutPaneWidth());
                System.out.println("cutPane.getOriginalCutPaneHeight() = " + cutPane.getOriginalCutPaneHeight());
                cutPane.setPrefWidth(cutPane.getOriginalCutPaneWidth() * cutPaneScale.getX());
                cutPane.setPrefHeight(cutPane.getOriginalCutPaneHeight() * cutPaneScale.getX());
//
//                System.out.println(scrollPaneWorkPane.getHmax());
//                System.out.println(scrollPaneWorkPane.getHmin());
//                System.out.println(cutPane.getBoundsInLocal().getMinX());
//                System.out.println(rootGroup.getBoundsInParent());
//                System.out.println(rootGroup.getBoundsInLocal());
//                System.out.println(rootGroup.getLayoutBounds());
//                underCutPane.setPrefWidth(3000);
//                underCutPane.setPrefHeight(3000);
//                scrollPaneWorkPane.setPrefViewportWidth(3000);
//                scrollPaneWorkPane.setPrefViewportHeight(3000);

//
//                //.setTranslateX(-rootGroup.getBoundsInParent().getMinX());
//                cutPane.setTranslateX(-rootGroup.getBoundsInParent().getMinX());

                event.consume();
            }
        });
    }

    public Group getRootGroup() {
        return rootGroup;
    }

//    public static void createElementsTreeView() {
//        //System.out.println("treeItemRoot.getChildren().size() =");
//        treeViewProjectElements.setCellFactory(new CellFactory());
//        TreeItem<TreeCellProjectElement> treeItemRoot = new TreeItem<>(new TreeCellProjectElement("Root"));
//        treeViewProjectElements.setRoot(treeItemRoot);
//        treeItemRoot.setExpanded(true);
//        treeViewProjectElements.setShowRoot(false);
//
//
//        TreeItem<TreeCellProjectElement> treeItemTableTopFolder = new TreeItem<>(new TreeCellProjectElement("Столешница"));
//        TreeItem<TreeCellProjectElement> treeItemWallPanelFolder = new TreeItem<>(new TreeCellProjectElement("Фартук"));
//        TreeItem<TreeCellProjectElement> treeItemWindowSillFolder = new TreeItem<>(new TreeCellProjectElement("Подоконник"));
//        TreeItem<TreeCellProjectElement> treeItemFootFolder = new TreeItem<>(new TreeCellProjectElement("Опора"));
//        TreeItem<TreeCellProjectElement> treeItemSinkFolder = new TreeItem<>(new TreeCellProjectElement("Раковины"));
//
//        treeItemTableTopFolder.setExpanded(true);
//        treeItemWallPanelFolder.setExpanded(true);
//        treeItemWindowSillFolder.setExpanded(true);
//        treeItemFootFolder.setExpanded(true);
//        treeItemSinkFolder.setExpanded(true);
//
//        treeItemRoot.getChildren().add(treeItemTableTopFolder);
//        treeItemRoot.getChildren().add(treeItemWallPanelFolder);
//        treeItemRoot.getChildren().add(treeItemWindowSillFolder);
//        treeItemRoot.getChildren().add(treeItemFootFolder);
//        treeItemRoot.getChildren().add(treeItemSinkFolder);
//
//        //shapes:
//        for (SketchShape shape : SketchDesigner.getSketchShapesList()) {
//            if (shape.isContainInUnion()) continue;
//            TreeItem<TreeCellProjectElement> item = new TreeItem<>(new TreeCellCutShape(TreeCellProjectElement.ELEMENT_TYPE,
//                    shape.getShapeNumber(),
//                    shape.getElementType(),
//                    //shape.getDragShapeFormat(),
//                    shape.getShapeType(),
//                    shape.getViewForListCell(), shape.getTooltipForListCell()));
//
//            if (shape.getElementType() == ElementTypes.TABLETOP) treeItemTableTopFolder.getChildren().add(item);
//            else if (shape.getElementType() == ElementTypes.WALL_PANEL) treeItemWallPanelFolder.getChildren().add(item);
//            else if (shape.getElementType() == ElementTypes.WINDOWSILL)
//                treeItemWindowSillFolder.getChildren().add(item);
//            else if (shape.getElementType() == ElementTypes.FOOT) treeItemFootFolder.getChildren().add(item);
//        }
//        //unions:
//        //System.out.println("SketchDesigner.getSketchShapeUnionsList() = " + SketchDesigner.getSketchShapeUnionsList().size());
//        for (SketchShapeUnion shUnion : SketchDesigner.getSketchShapeUnionsList()) {
//
//            TreeItem<TreeCellProjectElement> item = new TreeItem<>(new TreeCellCutShape(TreeCellProjectElement.ELEMENT_TYPE,
//                    shUnion.getUnionNumber(),
//                    ElementTypes.UNION,
//                    //shape.getDragShapeFormat(),
//                    ShapeType.UNION,
//                    shUnion.getViewForListCell(), shUnion.getTooltipForListCell()));
//
//            if (shUnion.getUnionType() == ElementTypes.TABLETOP) treeItemTableTopFolder.getChildren().add(item);
//            else if (shUnion.getUnionType() == ElementTypes.WALL_PANEL) treeItemWallPanelFolder.getChildren().add(item);
//            else if (shUnion.getUnionType() == ElementTypes.WINDOWSILL)
//                treeItemWindowSillFolder.getChildren().add(item);
//            else if (shUnion.getUnionType() == ElementTypes.FOOT) treeItemFootFolder.getChildren().add(item);
//        }
//
//        //TreeItem<TreeCellProjectElement> treeItemWallMountFolder = new TreeItem<>(new TreeCellProjectElement("Фартук"));
//        //treeItemRoot.getChildren().add(treeItemWallMountFolder);
//
//        //Sink
//        for (SketchShape shape : SketchDesigner.getSketchShapesList()) {
//
//            for (AdditionalFeature feature : shape.getFeaturesList()) {
//                if (feature instanceof Sink) {
//
//                    Sink sink = (Sink) feature;
//
//                    if (sink.isCuttable()) {
//                        TreeItem<TreeCellProjectElement> item = new TreeItem<>(new TreeCellFeature(
//                                TreeCellProjectElement.ELEMENT_TYPE,
//                                sink.getViewForListCell(),
//                                sink.getTooltipForListCell(),
//                                sink.getSketchShapeOwner().getShapeNumber(),
//                                sink.getFeatureNumber()));
//
//                        treeItemSinkFolder.getChildren().add(item);
//                    }
//
//
//                }
//            }
//        }
//
//    }
//

    public void refreshCutView() {

        choiceBoxAddMaterialSheet.getItems().clear();
        Set<String> materialSet = new LinkedHashSet<>();

        for (String nameMaterial : ProjectHandler.getMaterialsUsesInProjectObservable()) {
            String[] nameDepth = nameMaterial.split("#");
            String[] nameArray = nameDepth[0].split("\\$");
            materialSet.add(nameArray[2] + " " + nameArray[3] + " - " + nameDepth[1] + "мм");
        }
        for (String nameMaterial : materialSet) {
            //choiceBoxAddMaterialSheet.getItems().add(nameMaterial);
            choiceBoxAddMaterialSheet.getItems().add(nameMaterial);
        }
        choiceBoxAddMaterialSheet.getSelectionModel().select(0);


//        if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
//            createElementsTreeView();
//        } else if (ProjectHandler.getProjectType() == ProjectType.TABLE_TYPE) {
//            //hide shape tree
//        }
        cutPane.refreshCutPaneView();
        //CutPane.refreshStatistics(materialSheetStatisticsListObservable);

        updateStatistics();

    }

    public synchronized void updateStatistics() {
        CutPane.refreshStatistics(materialSheetStatisticsListObservable);

        listViewStatistics.getItems().clear();
        listViewStatistics.getItems().addAll(materialSheetStatisticsListObservable);

        if(listViewStatistics.getItems().size()!= 0){
            listViewStatistics.getItems().get(0).changeMaximize(true);
        }
    }


//    public void updateShapeInfo() {
//        if (selectedShapes.size() != 0) {
//            if (selectedShapes.get(0) instanceof CutShape) {
//                CutShape cutShape = (CutShape) selectedShapes.get(0);
//                labelShapeNumber.setText("" + cutShape.getShapeNumber());
//                labelShapeMaterial.setText("" + cutShape.getMaterial().getReceiptName());
//                labelShapeMaterial.getTooltip().setText(cutShape.getMaterial().getName());
//                labelDepth.setText(String.format(Locale.ENGLISH, "%d мм", SketchDesigner.getSketchShape(cutShape.getShapeNumber(), cutShape.getElementType()).getShapeDepth()));
//                labelShapeWidth.setText(String.format(Locale.ENGLISH, "%.0f мм", cutShape.getPolygon().getBoundsInParent().getWidth()/ProjectHandler.getCommonShapeScale()));
//                labelShapeHeight.setText(String.format(Locale.ENGLISH, "%.0f мм", cutShape.getPolygon().getBoundsInParent().getHeight()/ProjectHandler.getCommonShapeScale()));
//                for (Node node : anchorPaneRootCutShapeInfo.getChildren()) {
//                    node.setVisible(true);
//                }
//                labelNoInfo.setVisible(false);
//            }
//
//
//        } else {
//            for (Node node : anchorPaneRootCutShapeInfo.getChildren()) {
//                node.setVisible(false);
//            }
//            labelNoInfo.setVisible(true);
//
//        }
//
//    }

    public ScrollPane getScrollPaneWorkPane() {
        return scrollPaneWorkPane;
    }

    public void autoCutting(boolean invokeFromReceipt) {
//        createElementsTreeView();
        allDimensions.clear();

        cutPane.startAutoCutting(invokeFromReceipt);

    }


    @Override
    public JSONObject getJsonView() {
        JSONObject object = new JSONObject();
        object = cutPane.getJsonView();
        return object;
    }

    @Override
    public void initFromJson(JSONObject jsonObject) {
        //sheetsList.clear();
        usedShapesNumberList.clear();
        allDimensions.clear();

        //cutPane.getChildren().clear();

        cutPane.initFromJson(jsonObject);
    }
}
