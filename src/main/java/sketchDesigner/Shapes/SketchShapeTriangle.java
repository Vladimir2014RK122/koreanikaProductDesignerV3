package sketchDesigner.Shapes;

import Common.ConnectPoints.ConnectPoint;
import Common.ConnectPoints.CornerConnectPoint;
import Common.Material.Material;
import cutDesigner.CutDesigner;
import cutDesigner.Shapes.CutShape;
import cutDesigner.Shapes.CutShapeEdge;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sketchDesigner.Dimensions.LinearDimension;
import sketchDesigner.Edge.Border;
import sketchDesigner.Edge.Edge;
import sketchDesigner.Edge.EdgeManager;
import sketchDesigner.Edge.SketchEdge;
import sketchDesigner.Features.AdditionalFeature;
import sketchDesigner.Joint;
import sketchDesigner.SketchDesigner;
import utils.InfoMessage;
import utils.MainWindow;
import utils.ProjectHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static Common.MyMath.lineIntersection;

public class SketchShapeTriangle extends SketchShape {

    Pane sketchPane;

    Point2D[] points;
    double sizeAShape = 60;
    //double sizeDShape = 20;
    double sizeBShape = 60;
    double sizeCShape = 20;
    double connectAreaWidth = 0;

    //private Image imageForFill = null;

    //connect points:
    //ArrayList<CornerConnectPoint> connectionPoints = new ArrayList<>();
    //ArrayList<ConnectPoint> cutShapeConnectPoints = new ArrayList<>(Arrays.asList(new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint()));

    CutShapeEdge cutShapeEdgeA;
    CutShapeEdge cutShapeEdgeB;
    CutShapeEdge cutShapeEdgeC;
    //CutShapeEdge cutShapeEdgeD;
    //CutShapeEdge cutShapeEdgeE;

    //sides free or busy for connect with other shapes
    boolean leftSideFree = true;
    boolean rightSideFree = true;
    boolean topSideFree = true;
    boolean bottomSideFree = true;

    //Shape edges
    Point2D[] sideAShapeEdge = new Point2D[2];
    Point2D[] sideBShapeEdge = new Point2D[2];
    Point2D[] sideCShapeEdge = new Point2D[2];
    Point2D[] sideDShapeEdge = new Point2D[2];

    //Element edges
    double widthEdge = 5;

    SketchEdge sideAEdge = null;
    SketchEdge sideBEdge = null;
    SketchEdge sideCEdge = null;
//    SketchEdge sideDEdge = null;
//    SketchEdge sideEEdge = null;

    Polygon triangleIconSideAEdge;
    Polygon triangleIconSideBEdge;
    Polygon triangleIconSideCEdge;
//    Polygon triangleIconSideDEdge;
//    Polygon triangleIconSideEEdge;

    Line lineGrooveAEdge;
    Line lineGrooveBEdge;
    Line lineGrooveCEdge;
//    Line lineGrooveDEdge;
//    Line lineGrooveEEdge;

    //Joints:
    Line lineAJoint = null;
    Line lineBJoint = null;
    Line lineCJoint = null;
//    Line lineDJoint = null;
//    Line lineEJoint = null;

    ArrayList<Joint> sideAJointsList = new ArrayList<>();
    ArrayList<Joint> sideBJointsList = new ArrayList<>();
    ArrayList<Joint> sideCJointsList = new ArrayList<>();
//    ArrayList<Joint> sideDJointsList = new ArrayList<>();
//    ArrayList<Joint> sideEJointsList = new ArrayList<>();


    //links to connected shapes:
    private SketchShape leftConnectedShape, rightConnectedShape, topConnectedShape, bottomConnectedShape;

    //Shape settings:
    boolean materialDefault = true; //default = true;
    boolean edgesHeightsDefault = true; //default = true;


    int edgeHeight = 0;
    int borderHeight = 0;

    double sizeAReal = sizeAShape / commonShapeScale;
    double sizeBReal = sizeBShape / commonShapeScale;
    double sizeCReal = Math.pow(Math.pow(sizeAReal, 2) + Math.pow(sizeBReal, 2), 0.5);
//    double sizeDReal = sizeDShape/commonShapeScale;

    CheckBox checkBoxMaterialDefault, checkBoxDefaultHeights, checkBoxSaveImage;
    ChoiceBox<String> choiceBoxMaterial;
    ChoiceBox<String> choiceBoxMaterialDepth;
    TextField textFieldASize, textFieldBSize, textFieldCSize;
    TextField textFieldX, textFieldY;
    TextField textFieldEdgeHeight, textFieldBorderHeight;

    Group groupEdges;
    Button btnRotateRight, btnRotateLeft;

    Pane paneShapeView;
    Polygon polygonSettingsShape;
    Label labelA, labelB, labelC, labelD;

    boolean correctEdgeHeight = true, correctBorderHeight = true, correctX = true, correctY = true, correctASize = true, correctBSize = true, correctCSize = true, correctDSize = true;


    public SketchShapeTriangle(double layoutX, double layoutY, ElementTypes elementType, Pane sketchPane) {
        setChildShape(this);

        shapeType = ShapeType.TRIANGLE;
        //dragShapeFormat = new DataFormat(elementType.toString());
        this.elementType = elementType;
        setTranslateX(layoutX);
        setTranslateY(layoutY);
        this.sketchPane = sketchPane;

        if (elementType == ElementTypes.TABLETOP) {
            shapeColor = SketchShape.TABLE_TOP_COLOR;
            imagePath = TABLE_TOP_IMAGE_PATH;
        } else if (elementType == ElementTypes.WALL_PANEL) {
            shapeColor = SketchShape.WALL_PANEL_COLOR;
            imagePath = WALL_PANEL_IMAGE_PATH;
        } else if (elementType == ElementTypes.WINDOWSILL) {
            shapeColor = SketchShape.WINDOWSILL_COLOR;
            imagePath = WINDOWSILL_IMAGE_PATH;
        } else if (elementType == ElementTypes.FOOT) {
            shapeColor = SketchShape.FOOT_COLOR;
            imagePath = FOOT_IMAGE_PATH;
        }


        initShapeMaterial(ProjectHandler.getDefaultMaterial(), ProjectHandler.getDefaultMaterial().getDefaultDepth());
        setEdgesHeights(true, ProjectHandler.getDefaultMaterial().getDefaultDepth(), Border.DEFAULT_HEIGHT);

        initShapeSettings();
        initShapeSettingsControlLogic();
        createContextMenu();

        points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(sizeAShape, sizeBShape),
                new Point2D(0.0, sizeBShape)
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY()
        );


        // create ImagePattern
        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
            //System.err.println("CANT FILL RECTANGLE SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            //System.err.println("CANT FILL RECTANGLE SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
            polygon.setFill(image_pattern);
        }

        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.INSIDE);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(sizeBShape + connectAreaWidth * 2);
        setPrefWidth(sizeAShape + connectAreaWidth * 2);

        //setStyle("-fx-background-color: Blue"); //SET PANE COLOR UNDER SHAPE

        getChildren().add(polygon);

        updateShapeNumber();
        initEdgesZones();
        initConnectionPoints();

        refreshEdgeView();

        //sketchPane.getChildren().add(this);
    }

    public SketchShapeTriangle(ElementTypes elementType, Material material, int depth, double sizeA, double sizeB) {

        this.sizeAReal = sizeA;
        this.sizeBReal = sizeB;

        sizeAShape = sizeAReal * commonShapeScale;
        sizeBShape = sizeBReal * commonShapeScale;
//        sizeCShape = sizeCReal*commonShapeScale;
//        sizeDShape = sizeDReal*commonShapeScale;

        initShapeMaterial(material, depth);

        setChildShape(this);

        shapeType = ShapeType.TRIANGLE;
        //dragShapeFormat = new DataFormat(elementType.toString());
        this.elementType = elementType;

        if (elementType == ElementTypes.TABLETOP) {
            shapeColor = SketchShape.TABLE_TOP_COLOR;
            imagePath = TABLE_TOP_IMAGE_PATH;
        } else if (elementType == ElementTypes.WALL_PANEL) {
            shapeColor = SketchShape.WALL_PANEL_COLOR;
            imagePath = WALL_PANEL_IMAGE_PATH;
        } else if (elementType == ElementTypes.WINDOWSILL) {
            shapeColor = SketchShape.WINDOWSILL_COLOR;
            imagePath = WINDOWSILL_IMAGE_PATH;
        } else if (elementType == ElementTypes.FOOT) {
            shapeColor = SketchShape.FOOT_COLOR;
            imagePath = FOOT_IMAGE_PATH;
        }


//        initShapeMaterial(ProjectHandler.getDefaultMaterial(), ProjectHandler.getDefaultMaterial().getDefaultDepth());
//        setEdgesHeights(true, ProjectHandler.getDefaultMaterial().getDefaultDepth(), Border.DEFAULT_HEIGHT);
//
        initShapeSettings();
        initShapeSettingsControlLogic();
        createContextMenu();

        points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(sizeAShape, sizeBShape),
                new Point2D(0.0, sizeBShape)
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY()
        );


        // create ImagePattern
        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
            //System.err.println("CANT FILL RECTANGLE SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            //System.err.println("CANT FILL RECTANGLE SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
            polygon.setFill(image_pattern);
        }

        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.INSIDE);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(sizeBShape + connectAreaWidth * 2);
        setPrefWidth(sizeAShape + connectAreaWidth * 2);

        //setStyle("-fx-background-color: Blue"); //SET PANE COLOR UNDER SHAPE

        getChildren().add(polygon);

        updateShapeNumber();
        initEdgesZones();
        initConnectionPoints();

        refreshEdgeView();

        //sketchPane.getChildren().add(this);
    }

    @Override
    public void initConnectionPoints() {
//        if(connectionPoints.size() != 0){
//            for(ConnectPoint connectPoint : connectionPoints){
//                getChildren().remove(connectPoint);
//            }
//            connectionPoints.clear();
//        }

        if (connectionPoints.size() == 0) {
            for (int i = 0; i < 3; i++) {
                connectionPoints.add(new CornerConnectPoint(this));
            }
        }


        connectionPoints.get(0).setTranslateX(-(widthConnectPoint / 2));
        connectionPoints.get(0).setTranslateY(-(widthConnectPoint / 2));
        connectionPoints.get(0).hide();

        connectionPoints.get(1).setTranslateX(sizeAShape - (widthConnectPoint / 2));
        connectionPoints.get(1).setTranslateY(sizeBShape - (widthConnectPoint / 2));
        connectionPoints.get(1).hide();

        connectionPoints.get(2).setTranslateX(-(widthConnectPoint / 2));
        connectionPoints.get(2).setTranslateY(sizeBShape - (widthConnectPoint / 2));
        connectionPoints.get(2).hide();


        for (ConnectPoint connectPoint : connectionPoints) {
            getChildren().remove(connectPoint);
        }
        for (ConnectPoint connectPoint : connectionPoints) {
            getChildren().add(connectPoint);
        }
    }

    @Override
    public void setWidthConnectPoint(double widthConnectPoint) {
        if (widthConnectPoint > 10) {
            this.widthConnectPoint = 10;
        } else {
            this.widthConnectPoint = widthConnectPoint;
        }
        connectionPoints.forEach(connectPoint -> {
            connectPoint.changeSide(this.widthConnectPoint);
        });
        initConnectionPoints();
    }

    @Override
    public ArrayList<ConnectPoint> getConnectPoints() {
        ArrayList<ConnectPoint> connectPoints = new ArrayList<>();

        for (ConnectPoint connectPoint : connectionPoints) {
            connectPoints.add(connectPoint);
        }
        return connectPoints;
    }

    @Override
    public void showConnectionPoints() {
        if (containInUnion) return;
        for (ConnectPoint connectPoint : connectionPoints) {
            connectPoint.show();
        }
    }

    @Override
    public void hideConnectionPoints() {
        for (ConnectPoint connectPoint : connectionPoints) {
            connectPoint.hide();
        }
    }

    @Override
    public void edgesDisable(boolean edgesDisable) {
        sideAEdge.setDisable(edgesDisable);
        sideBEdge.setDisable(edgesDisable);
        sideCEdge.setDisable(edgesDisable);
//        sideDEdge.setDisable(edgesDisable);
//        sideEEdge.setDisable(edgesDisable);
    }

    @Override
    public boolean isConnectedToShapeOutOfUnion() {
        return false;
    }

    @Override
    public double getVerticalSize() {
        return sizeBShape;
    }

    @Override
    public double getHorizontalSize() {
        return sizeAShape;
    }

    @Override
    public Point2D getRotationPivot() {
        Point2D pivot = new Point2D(getPrefWidth() / 2, getPrefHeight() / 2);

        return pivot;
    }

    @Override
    public void updateShapeNumber() {
        if (labelShapeNumber == null) {
            labelShapeNumber = new Label(String.valueOf(thisShapeNumber));
            labelShapeNumber.setId("shapeNumberLabel");
            labelShapeNumber.setPrefHeight(15);
            labelShapeNumber.setPrefWidth(15);
            labelShapeNumber.setFont(Font.font(8));
            //labelShapeNumber.setStyle("-fx-text-fill:#B3B4B4;");
            labelShapeNumber.setAlignment(Pos.CENTER);
        } else {
            labelShapeNumber.setText(String.valueOf(thisShapeNumber));
        }
        getChildren().remove(labelShapeNumber);
        getChildren().add(labelShapeNumber);

        labelShapeNumber.setTranslateX(0);
        labelShapeNumber.setTranslateY(sizeBShape - 15);
        labelShapeNumber.setMouseTransparent(true);
    }

    @Override
    public CutShape getCutShapeFromJSON(JSONObject obj) {
        return null;
    }

    /**
     * CUT SHAPE START
     */
    @Override
    public void refreshCutShapeView() {
        if (cutShape == null) {
            createCutShape();
            createCutShapeFeatures();
            createCutEdges();
            //System.out.println("CREATE CUT SHAPE RectangleTriangle " + thisShapeNumber);
        } else {

            updateCutShapeView();
            updateCutShapeFeatures();
            updateCutEdgesView();

        }
    }

    private void createCutShape() {
        cutShape = new CutShape(this);

        //return polygon, areas around, connect points
        cutShape.getChildren().clear();

        //create Cut zone polygon
        ArrayList<Point2D> cutZonePolygonPoints = null;
        Polygon cutZonePolygon = null;
        {
            Point2D p1 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);
            Point2D p2 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);
            Point2D p3 = new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);

            double angle = Math.toDegrees(Math.atan((sizeAShape) / (sizeBShape)));

            Point2D pivot = new Point2D(sizeAShape, sizeBShape);
            Point2D p5 = SketchShape.rotatePoint(new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape),
                    pivot, -angle);

            pivot = new Point2D(0.0, 0.0);
            Point2D p6 = SketchShape.rotatePoint(new Point2D(CutDesigner.CUT_SHAPES_CUTSHIFT, 0.0),
                    pivot, -angle);

            Point2D p4 = lineIntersection(p6, p5, p2, p3);
            Point2D p7 = lineIntersection(p6, p5, p1, p2);

            cutZonePolygonPoints = new ArrayList<>();
            cutZonePolygonPoints.addAll(Arrays.asList(p1,p2,p3,p4,p5,p6,p7));

            cutZonePolygon = new Polygon();
            for (Point2D p : cutZonePolygonPoints) {
                cutZonePolygon.getPoints().add(new Double(p.getX()));
                cutZonePolygon.getPoints().add(new Double(p.getY()));
            }

            cutZonePolygon.setTranslateX(0.0);
            cutZonePolygon.setTranslateY(0.0);
            cutZonePolygon.setStyle("-fx-background-color: yellow; -fx-opacity: 10%");
            cutZonePolygon.setStrokeType(StrokeType.INSIDE);
            cutZonePolygon.setStroke(Color.BLACK);
            cutShape.setCutZonePolygon(cutZonePolygon);
            cutShape.getChildren().add(cutZonePolygon);
        }

        //create main poligon:
        ArrayList<Point2D> polygonPoints = null;
        Polygon cutShapePolygon = null;
        {
            // CREATE POINTS
            polygonPoints = new ArrayList<>();
            polygonPoints.add(new Point2D(0.0, 0.0));
            polygonPoints.add(new Point2D(sizeAShape, sizeBShape));
            polygonPoints.add(new Point2D(0.0, sizeBShape));

            cutShapePolygon = new Polygon();
            for (Point2D p : polygonPoints) {
                cutShapePolygon.getPoints().add(new Double(p.getX()));
                cutShapePolygon.getPoints().add(new Double(p.getY()));
            }


            cutShapePolygon.setTranslateX(0.0);
            cutShapePolygon.setTranslateY(0.0);
            cutShapePolygon.setFill(shapeColor);

            cutShapePolygon.setStroke(Color.GRAY);

            cutShapePolygon.setStrokeType(StrokeType.INSIDE);
            cutShape.setPolygon(cutShapePolygon);
            cutShape.getChildren().add(cutShapePolygon);
        }

        cutShape.setPrefWidth(cutShapePolygon.getBoundsInLocal().getWidth());
        cutShape.setPrefHeight(cutShapePolygon.getBoundsInLocal().getHeight());




        //create connect points:
        cutShapeConnectPoints.clear();
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));

        cutShapeConnectPoints.get(0).changeSetPoint(polygonPoints.get(0));
        cutShapeConnectPoints.get(1).changeSetPoint(polygonPoints.get(1));
        cutShapeConnectPoints.get(2).changeSetPoint(polygonPoints.get(2));

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(cutZonePolygonPoints.get(6));
            cutShapeConnectPoints.get(1).changeSetPointShift(cutZonePolygonPoints.get(3));
            cutShapeConnectPoints.get(2).changeSetPointShift(cutZonePolygonPoints.get(1));
        }

        cutShape.setConnectPoints(cutShapeConnectPoints);
        for (ConnectPoint connectPoint : cutShapeConnectPoints) {
            cutShape.getChildren().add(connectPoint);
        }
        cutShape.hideConnectionPoints();

        //add label with shape number
        cutShape.refreshLabelNumber();

        //CREATE DIMENSIONS

        double sizeH = polygonPoints.get(1).getX();
        double sizeY = polygonPoints.get(polygonPoints.size() - 1).getY();

        Label dimensionVLabel = new Label();
        dimensionVLabel.setId("dimensionVLabel");
        dimensionVLabel.setPickOnBounds(false);

        dimensionVLabel.setAlignment(Pos.CENTER);
        dimensionVLabel.setPrefWidth(sizeY);
        //dimensionVLabel.setPrefWidth(60);
        dimensionVLabel.setPrefHeight(8);
        dimensionVLabel.setTranslateX(0.0);
        dimensionVLabel.setTranslateY(sizeY);
        dimensionVLabel.setText(String.format("%.0f", sizeY / commonShapeScale));
        dimensionVLabel.setFont(Font.font(8));
        //dimensionVLabel.setRotate(-90);
        Rotate rotateV = new Rotate(-90);
        dimensionVLabel.getTransforms().add(rotateV);

        cutShape.setDimensionV(dimensionVLabel);

        Label dimensionHLabel = new Label();
        dimensionHLabel.setId("dimensionHLabel");
        dimensionHLabel.setPickOnBounds(false);
        double shiftX = 2;
        double shiftY = sizeY - 5;

        dimensionHLabel.setAlignment(Pos.CENTER);
        dimensionHLabel.setPrefWidth(sizeH);
        dimensionHLabel.setTranslateX(0.0);
        dimensionHLabel.setTranslateY(sizeBShape - 10);
        dimensionHLabel.setText(String.format("%.0f", sizeH / commonShapeScale));
        dimensionHLabel.setFont(Font.font(8));

        cutShape.setDimensionH(dimensionHLabel);

        //set rotation pivot
        cutShape.setRotationPivot(getRotationPivot());
    }

    private void createCutShapeFeatures() {
        double saveAngle = rotateAngle;
        this.rotateShape(-rotateAngle);
        cutShape.getFeaturesList().clear();
        for (AdditionalFeature feature : getFeaturesList()) {
            AdditionalFeature newFeature = AdditionalFeature.getDuplicateFeature(feature);
            cutShape.getChildren().add(newFeature);
            cutShape.getFeaturesList().add(newFeature);

            newFeature.rotate(feature.getShapeScheme().getRotate());
            newFeature.setTranslateX(feature.getTranslateX());
            newFeature.setTranslateY(feature.getTranslateY());
            newFeature.setMouseTransparent(true);
        }
        this.rotateShape(saveAngle);
    }

    private void createCutEdges() {

        createACutEdge();
        createBCutEdge();
        createCCutEdge();
//        createDCutEdge();
//        createECutEdge();

        cutShapesEdgesList.clear();
        cutShapesEdgesList.addAll(Arrays.asList(cutShapeEdgeA, cutShapeEdgeB, cutShapeEdgeC));
        cutShape.setCutShapeEdgesList(cutShapesEdgesList);

    }

    private void createACutEdge() {
        cutShapeEdgeA = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideAEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }


        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(sizeAShape, 0.0));
        pointsForEdge.add(new Point2D(sizeAShape, h));
        pointsForEdge.add(new Point2D(0.0, h));

        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

//            cutShapeEdgeTop.setDepth(shapeDepth);
        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

//            cutShapeEdgeTop.setPadding(new Insets(0));
        cutShapeEdgeA.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeA.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeA.getChildren().remove(cutShapeEdgeA.getPolygon());
        cutShapeEdgeA.setPolygon(edgePolygon);
        cutShapeEdgeA.getChildren().add(edgePolygon);


        //create connect points:
        ArrayList<ConnectPoint> topEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeA);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeA);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeA);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeA);

        topEdgeConnectPoints.add(point1);
        topEdgeConnectPoints.add(point2);
        topEdgeConnectPoints.add(point3);
        topEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : topEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeA);
        }

        cutShapeEdgeA.setConnectPoints(topEdgeConnectPoints);
        for (ConnectPoint connectPoint : topEdgeConnectPoints) {
            cutShapeEdgeA.getChildren().add(connectPoint);
        }
        cutShapeEdgeA.hideConnectionPoints();

        if (sideAEdge.isDefined()) {
            cutShapeEdgeA.setStartCoordinate(new Point2D(0.0, sizeBShape));
        } else {
            cutShapeEdgeA.setStartCoordinate(null);
        }
    }

    private void createBCutEdge() {

        cutShapeEdgeB = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideBEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }


        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(0.0, sizeBShape));
        pointsForEdge.add(new Point2D(h, sizeBShape));
        pointsForEdge.add(new Point2D(h, 0.0));

        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeEdgeB.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeB.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeB.getChildren().remove(cutShapeEdgeB.getPolygon());
        cutShapeEdgeB.setPolygon(edgePolygon);
        cutShapeEdgeB.getChildren().add(edgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> bottomEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeB);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeB);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeB);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeB);

        bottomEdgeConnectPoints.add(point1);
        bottomEdgeConnectPoints.add(point2);
        bottomEdgeConnectPoints.add(point3);
        bottomEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : bottomEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeB);
        }
        for (ConnectPoint connectPoint : bottomEdgeConnectPoints) {
            cutShapeEdgeB.getChildren().add(connectPoint);
        }

        cutShapeEdgeB.setConnectPoints(bottomEdgeConnectPoints);


        cutShapeEdgeB.hideConnectionPoints();

        if (sideBEdge.isDefined()) {
            cutShapeEdgeB.setStartCoordinate(new Point2D(-h, 0));
        } else {
            cutShapeEdgeB.setStartCoordinate(null);
        }
    }

    private void createCCutEdge() {

        cutShapeEdgeC = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideCEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }

        // CREATE POINTS
        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(h, 0.0));
        pointsForEdge.add(new Point2D(sizeAShape + h, sizeBShape));
        pointsForEdge.add(new Point2D(sizeAShape, sizeBShape));

        //ROTATE TWO POINTS
        double angle = Math.toDegrees(Math.atan((sizeAShape) / (sizeBShape)));
        Point2D p1 = SketchShape.rotatePoint(pointsForEdge.get(1), pointsForEdge.get(0), angle);
        Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(3), angle);

        //pointsForEdge.set(1, p1);
        //pointsForEdge.set(2, p2);


        //ADD POINTS TO POLYGON:
        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeEdgeC.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeC.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeC.getChildren().remove(cutShapeEdgeC.getPolygon());
        cutShapeEdgeC.setPolygon(edgePolygon);
        cutShapeEdgeC.getChildren().add(edgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> rightEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeC);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeC);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeC);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeC);

        rightEdgeConnectPoints.add(point1);
        rightEdgeConnectPoints.add(point2);
        rightEdgeConnectPoints.add(point3);
        rightEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeC);
        }
        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            cutShapeEdgeC.getChildren().add(connectPoint);
        }

        cutShapeEdgeC.setConnectPoints(rightEdgeConnectPoints);
        cutShapeEdgeC.hideConnectionPoints();

        if (sideCEdge.isDefined()) {
            cutShapeEdgeC.setStartCoordinate(new Point2D(0.0, 0.0));
        } else {
            cutShapeEdgeC.setStartCoordinate(null);
        }
    }

    private void updateCutShapeView() {

        cutShape.setSizesInfo("ШхВ "+ (int)(sizeAShape/ProjectHandler.getCommonShapeScale()) +
                "x" + (int)(sizeBShape/ProjectHandler.getCommonShapeScale()));

        //create Cut zone polygon
        ArrayList<Point2D> cutZonePolygonPoints = null;
        Polygon cutZonePolygon = null;
        {
            Point2D p1 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);
            Point2D p2 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);
            Point2D p3 = new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);

            double angle = Math.toDegrees(Math.atan((sizeAShape) / (sizeBShape)));

            Point2D pivot = new Point2D(sizeAShape, sizeBShape);
            Point2D p5 = SketchShape.rotatePoint(new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape),
                    pivot, -angle);

            pivot = new Point2D(0.0, 0.0);
            Point2D p6 = SketchShape.rotatePoint(new Point2D(CutDesigner.CUT_SHAPES_CUTSHIFT, 0.0),
                    pivot, -angle);

            Point2D p4 = lineIntersection(p6, p5, p2, p3);
            Point2D p7 = lineIntersection(p6, p5, p1, p2);

            cutZonePolygonPoints = new ArrayList<>();
            cutZonePolygonPoints.addAll(Arrays.asList(p1,p2,p3,p4,p5,p6,p7));

            cutZonePolygon = cutShape.getCutZonePolygon();
            cutZonePolygon.getPoints().clear();
            for (Point2D p : cutZonePolygonPoints) {
                cutZonePolygon.getPoints().add(new Double(p.getX()));
                cutZonePolygon.getPoints().add(new Double(p.getY()));
            }

        }
        //create main poligon
        ArrayList<Point2D> polygonPoints = null;
        {
            // CREATE POINTS
            polygonPoints = new ArrayList<>();
            polygonPoints.add(new Point2D(0.0, 0.0));
            polygonPoints.add(new Point2D(sizeAShape, sizeBShape));
            polygonPoints.add(new Point2D(0.0, sizeBShape));

            Polygon cutShapePolygon = cutShape.getPolygon();
            cutShapePolygon.getPoints().clear();

            for (Point2D p : polygonPoints) {
                cutShapePolygon.getPoints().add(new Double(p.getX()));
                cutShapePolygon.getPoints().add(new Double(p.getY()));
            }
        }

        //cutShapePolygon.setFill(shapeColor);

        cutShapeConnectPoints.get(0).changeSetPoint(polygonPoints.get(0));
        cutShapeConnectPoints.get(1).changeSetPoint(polygonPoints.get(1));
        cutShapeConnectPoints.get(2).changeSetPoint(polygonPoints.get(2));

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(cutZonePolygonPoints.get(6));
            cutShapeConnectPoints.get(1).changeSetPointShift(cutZonePolygonPoints.get(3));
            cutShapeConnectPoints.get(2).changeSetPointShift(cutZonePolygonPoints.get(1));
        }

        //Update dimensions labels:
        double sizeH = polygonPoints.get(1).getX();
        double sizeY = polygonPoints.get(polygonPoints.size() - 1).getY();

        //cutShape.getDimensionVLabel().setRotate(0);
        cutShape.getDimensionVLabel().setPrefWidth(sizeY);
        cutShape.getDimensionVLabel().setTranslateX(0.0);
        cutShape.getDimensionVLabel().setTranslateY(sizeY);
        cutShape.getDimensionVLabel().setText(String.format("%.0f", sizeY / ProjectHandler.getCommonShapeScale()));
        cutShape.getDimensionVLabel().toFront();


        double shiftX = 2;
        double shiftY = sizeY - 5;

        //cutShape.getDimensionHLabel().setRotate(0);
        cutShape.getDimensionHLabel().setPrefWidth(sizeH);
        cutShape.getDimensionHLabel().setTranslateX(0.0);
        cutShape.getDimensionHLabel().setTranslateY(sizeBShape - 12);
        cutShape.getDimensionHLabel().setText(String.format("%.0f", sizeH / ProjectHandler.getCommonShapeScale()));
        cutShape.getDimensionHLabel().toFront();

        cutShape.refreshLabelNumber();
    }

    private void updateCutShapeFeatures() {
        Iterator<Node> it = cutShape.getChildren().iterator();
        while (it.hasNext()) {
            if (it.next() instanceof AdditionalFeature) {
                it.remove();
            }
        }

        createCutShapeFeatures();
    }

    private void updateCutEdgesView() {

        if (edgeHeight <= MIN_EDGE_HEIGHT_FOR_CUTSHAPE && (shapeMaterial.getName().indexOf("Акриловый камень") != -1 || shapeMaterial.getName().indexOf("Полиэфирный камень") != -1)) {

            if (sideAEdge instanceof Border) {
                updateACutEdge();
            } else {
                cutShapeEdgeA.setStartCoordinate(null);
            }

            if (sideBEdge instanceof Border) {
                updateBCutEdge();
            } else {
                cutShapeEdgeB.setStartCoordinate(null);
            }

            if (sideCEdge instanceof Border) {
                updateCCutEdge();
            } else {
                cutShapeEdgeC.setStartCoordinate(null);
            }

        } else {
            updateACutEdge();
            updateBCutEdge();
            updateCCutEdge();

        }

    }

    private void updateACutEdge() {

        if (sideAEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideAEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(sizeAShape, 0.0));
            pointsForEdge.add(new Point2D(sizeAShape, h));
            pointsForEdge.add(new Point2D(0.0, h));

            Polygon edgePolygon = cutShapeEdgeA.getPolygon();
            edgePolygon.getPoints().clear();

            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeA.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeA.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());


            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeA.getConnectPoints();

            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeA.setStartCoordinate(new Point2D(0.0, sizeBShape));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeA.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeA.getStartCoordinate().getX());
                cutShapeEdgeA.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeA.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeA.setStartCoordinate(null);
        }
    }

    private void updateBCutEdge() {

        if (sideBEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideBEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(0.0, sizeBShape));
            pointsForEdge.add(new Point2D(h, sizeBShape));
            pointsForEdge.add(new Point2D(h, 0.0));

            Polygon edgePolygon = cutShapeEdgeB.getPolygon();
            edgePolygon.getPoints().clear();
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeB.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeB.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

            //cutShapeEdgeBottom.setStartCoordinate(new Point2D(cutShapeEdgeBottom.getTranslateX(), cutShapeEdgeBottom.getTranslateY()));

            //create connect points:
            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeB.getConnectPoints();
            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeB.setStartCoordinate(new Point2D(-h, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeB.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeB.getStartCoordinate().getX());
                cutShapeEdgeB.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeB.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeB.setStartCoordinate(null);
        }
    }

    private void updateCCutEdge() {

        if (sideCEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideCEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(h, 0.0));
            pointsForEdge.add(new Point2D(sizeAShape + h, sizeBShape));
            pointsForEdge.add(new Point2D(sizeAShape, sizeBShape));

            //ROTATE TWO POINTS
            double angle = Math.toDegrees(Math.atan((sizeAShape) / (sizeBShape)));
            Point2D p1 = SketchShape.rotatePoint(pointsForEdge.get(1), pointsForEdge.get(0), -angle);
            Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(3), -angle);

            pointsForEdge.set(1, p1);
            pointsForEdge.set(2, p2);

            Polygon edgePolygon = cutShapeEdgeC.getPolygon();
            edgePolygon.getPoints().clear();
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeC.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeC.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

            // cutShapeEdgeLeft.setStartCoordinate(new Point2D(cutShapeEdgeLeft.getTranslateX(), cutShapeEdgeLeft.getTranslateY()));

            //connect points:
            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeC.getConnectPoints();
            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeC.setStartCoordinate(new Point2D(0.0, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeC.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeC.getStartCoordinate().getX());
                cutShapeEdgeC.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeC.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeC.setStartCoordinate(null);
        }
    }

    /** CUT SHAPE END */

    /**
     * SETTINGS START
     */
    @Override
    public AnchorPane getShapeSettings() {
        refreshShapeSettings();
        return shapeSettings;
    }

    @Override
    public void initShapeSettings() {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/sketchShapeTriangleSettings.fxml"));
        try {
            shapeSettings = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ScrollPane scrollPane = (ScrollPane) shapeSettings.lookup("#scrollPane");
        settingsRootAnchorPane = (AnchorPane) scrollPane.getContent();

        checkBoxMaterialDefault = (CheckBox) settingsRootAnchorPane.lookup("#checkBoxMaterialDefault");
        checkBoxDefaultHeights = (CheckBox) settingsRootAnchorPane.lookup("#checkBoxDefaultHeights");


        choiceBoxMaterial = (ChoiceBox<String>) settingsRootAnchorPane.lookup("#choiceBoxMaterial");
        choiceBoxMaterialDepth = (ChoiceBox<String>) settingsRootAnchorPane.lookup("#choiceBoxMaterialDepth");
        checkBoxSaveImage = (CheckBox) settingsRootAnchorPane.lookup("#checkBoxSaveImage");

        textFieldASize = (TextField) settingsRootAnchorPane.lookup("#textFieldASize");
        textFieldBSize = (TextField) settingsRootAnchorPane.lookup("#textFieldBSize");
        textFieldCSize = (TextField) settingsRootAnchorPane.lookup("#textFieldCSize");


        textFieldX = (TextField) settingsRootAnchorPane.lookup("#textFieldX");
        textFieldY = (TextField) settingsRootAnchorPane.lookup("#textFieldY");
        textFieldEdgeHeight = (TextField) settingsRootAnchorPane.lookup("#textFieldEdgeHeight");
        textFieldBorderHeight = (TextField) settingsRootAnchorPane.lookup("#textFieldBorderHeight");


        groupEdges = (Group) settingsRootAnchorPane.lookup("#groupEdges");

        if (elementType == ElementTypes.WALL_PANEL || elementType == ElementTypes.FOOT) {
            groupEdges.setVisible(false);
        }


        btnRotateLeft = (Button) settingsRootAnchorPane.lookup("#btnRotateLeft");
        btnRotateRight = (Button) settingsRootAnchorPane.lookup("#btnRotateRight");

        //init shape view in settings
        paneShapeView = (Pane) settingsRootAnchorPane.lookup("#paneShapeView");


        Point2D[] points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(0.0, 90),
                new Point2D(90, 90)
        };
        polygonSettingsShape = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY()
        );

        double shiftXToCenter = paneShapeView.getPrefWidth() / 2 - 45;
        double shiftYToCenter = paneShapeView.getPrefHeight() / 2 - 45;

        for (int i = 0; i < polygonSettingsShape.getPoints().size(); i += 2) {
            double newX = polygonSettingsShape.getPoints().get(i).doubleValue() + shiftXToCenter;
            double newY = polygonSettingsShape.getPoints().get(i + 1).doubleValue() + shiftYToCenter;
            polygonSettingsShape.getPoints().set(i, newX);
            polygonSettingsShape.getPoints().set(i + 1, newY);
        }

        polygonSettingsShape.setFill(Color.BLUEVIOLET);
        paneShapeView.getChildren().add(polygonSettingsShape);


        labelA = new Label("A");
        labelA.setAlignment(Pos.CENTER);
        labelA.setPrefSize(30.0, 30.0);
        labelA.setTranslateX(95);
        labelA.setTranslateY(120);

        labelB = new Label("B");
        labelB.setAlignment(Pos.CENTER);
        labelB.setPrefSize(30.0, 30.0);
        labelB.setTranslateX(45);
        labelB.setTranslateY(65);

        labelC = new Label("C");
        labelC.setAlignment(Pos.CENTER);
        labelC.setPrefSize(30.0, 30.0);
        labelC.setTranslateX(110);
        labelC.setTranslateY(60);

        paneShapeView.getChildren().addAll(labelA, labelB, labelC);

        checkBoxMaterialDefault.setSelected(materialDefault);
        checkBoxDefaultHeights.setSelected(edgesHeightsDefault);

        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getReceiptName());
        if (checkBoxMaterialDefault.isSelected()) {
            choiceBoxMaterial.setDisable(true);
            choiceBoxMaterialDepth.setDisable(true);
        }

        if (checkBoxDefaultHeights.isSelected()) {
            textFieldEdgeHeight.setDisable(true);
            textFieldBorderHeight.setDisable(true);
        }

        for (String s : shapeMaterial.getDepths()) {
            choiceBoxMaterialDepth.getItems().add(s);
        }
        choiceBoxMaterialDepth.getSelectionModel().select(String.valueOf(shapeDepth));

        //System.out.println("INIT SHAPE SETTINGS ");
        textFieldASize.setText(String.format("%.0f", sizeAReal));
        textFieldBSize.setText(String.format("%.0f", sizeBReal));
        textFieldCSize.setText(String.format("%.0f", sizeCReal));

        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));
        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
        textFieldBorderHeight.setText(String.valueOf(borderHeight));
    }

    @Override
    public void initShapeSettingsControlLogic() {

        checkBoxMaterialDefault.setOnMouseClicked(event -> {
            if (checkBoxMaterialDefault.isSelected()) {
                choiceBoxMaterial.getSelectionModel().select(ProjectHandler.getDefaultMaterial().getReceiptName());
                choiceBoxMaterial.setDisable(true);

                choiceBoxMaterialDepth.getSelectionModel().select(String.valueOf(shapeMaterial.getDefaultDepth()));
                choiceBoxMaterialDepth.setDisable(true);

                if (ProjectHandler.getDefaultMaterial().getName().indexOf("Акриловый камень") != -1 || ProjectHandler.getDefaultMaterial().getName().indexOf("Полиэфирный камень") != -1) {

                    checkBoxSaveImage.setDisable(true);
                    checkBoxSaveImage.setSelected(false);
                    saveMaterialImageOnEdges = false;
                } else {
                    checkBoxSaveImage.setDisable(false);
                }

            } else {
                choiceBoxMaterial.setDisable(false);
                choiceBoxMaterialDepth.setDisable(false);
            }
        });
        checkBoxDefaultHeights.setOnMouseClicked(event -> {
            if (checkBoxDefaultHeights.isSelected()) {
//                edgeHeight = shapeDepth;
//                borderHeight = Border.DEFAULT_HEIGHT;
                if (shapeMaterial.getName().indexOf("Акриловый камень") != -1) {
                    if (elementType == ElementTypes.TABLETOP) {
                        textFieldEdgeHeight.setText("" + 40);
                    } else if (elementType == ElementTypes.WALL_PANEL) {
                        textFieldEdgeHeight.setText(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem());
                    } else if (elementType == ElementTypes.WINDOWSILL) {
                        textFieldEdgeHeight.setText("" + 40);
                    } else if (elementType == ElementTypes.FOOT) {
                        textFieldEdgeHeight.setText("" + 40);
                    }

                } else {
                    textFieldEdgeHeight.setText(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem());
                }

                textFieldBorderHeight.setText("" + Border.DEFAULT_HEIGHT);
                textFieldEdgeHeight.setDisable(true);
                textFieldBorderHeight.setDisable(true);
            } else {
                textFieldEdgeHeight.setDisable(false);
                textFieldBorderHeight.setDisable(false);
            }
        });

        choiceBoxMaterial.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            //System.out.println("shapeMaterial = " + shapeMaterial.getName());
            if (newValue == null) return;


            for (Material m : ProjectHandler.getMaterialsListInProject()) {
                if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                    choiceBoxMaterialDepth.getItems().clear();
                    for (String s : m.getDepths()) {
                        choiceBoxMaterialDepth.getItems().add(s);
                    }
                    choiceBoxMaterialDepth.getSelectionModel().select(String.valueOf(m.getDefaultDepth()));

                    if (m.getName().indexOf("Акриловый камень") != -1 || m.getName().indexOf("Полиэфирный камень") != -1) {

                        checkBoxSaveImage.setDisable(true);
                        checkBoxSaveImage.setSelected(false);
                        saveMaterialImageOnEdges = false;
                    } else {
                        checkBoxSaveImage.setDisable(false);
                    }
                }
            }


        });

        choiceBoxMaterialDepth.setOnAction(event -> {
            try {
                int selectedDepth = Integer.parseInt(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem());
                int typedEdgeHeight = Integer.parseInt(textFieldEdgeHeight.getText());

                if (selectedDepth > typedEdgeHeight) {
                    textFieldEdgeHeight.setText("" + selectedDepth);
                }
            } catch (NumberFormatException ex) {
            }

        });

        checkBoxSaveImage.setOnMouseClicked(event -> {
        });

        textFieldASize.textProperty().addListener(observable -> {
            double value;
            try {
                value = Double.parseDouble(textFieldASize.getText());
                correctASize = true;
                textFieldASize.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 10 || value > 100000) {
                    correctASize = false;
                    textFieldASize.setStyle("-fx-text-fill: red");
                } else {

                    double a = Double.parseDouble(textFieldASize.getText());
                    double b = Double.parseDouble(textFieldBSize.getText());
                    sizeCReal = Math.pow(Math.pow(a, 2) + Math.pow(b, 2), 0.5);
                    textFieldCSize.setText(String.format("%.0f", sizeCReal));

                }


            } catch (NumberFormatException ex) {
                correctASize = false;
                textFieldASize.setStyle("-fx-text-fill: red");
            }
            //check that it correct
        });
        textFieldBSize.textProperty().addListener(observable -> {
            //check that it correct
            double value;
            try {
                value = Double.parseDouble(textFieldBSize.getText());
                correctBSize = true;
                textFieldBSize.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 10 || value > 10000) {
                    correctBSize = false;
                    textFieldBSize.setStyle("-fx-text-fill: red");
                } else {
                    double a = Double.parseDouble(textFieldASize.getText());
                    double b = Double.parseDouble(textFieldBSize.getText());
                    sizeCReal = Math.pow(Math.pow(a, 2) + Math.pow(b, 2), 0.5);
                    textFieldCSize.setText(String.format("%.0f", sizeCReal));
                }
            } catch (NumberFormatException ex) {
                correctBSize = false;
                textFieldBSize.setStyle("-fx-text-fill: red");
            }
        });
//        textFieldCSize.textProperty().addListener(observable -> {
//            //check that it correct
//            double value;
//            try{
//                value = Double.parseDouble(textFieldCSize.getText());
//                correctCSize = true;
//                textFieldCSize.setStyle("-fx-text-fill: #B3B4B4");
//                if(value < 10 || value > 10000){
//                    correctCSize = false;
//                    textFieldCSize.setStyle("-fx-text-fill: red");
//                }
//            }catch(NumberFormatException ex){
//                correctCSize = false;
//                textFieldCSize.setStyle("-fx-text-fill: red");
//            }
//        });


        textFieldX.textProperty().addListener(observable -> {
            //check that it correct
            double value;
            try {
                value = Double.parseDouble(textFieldX.getText());
                correctX = true;
                textFieldX.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 10 || value > sketchPane.getPrefWidth() - 10) {
                    correctX = false;
                    textFieldX.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctX = false;
                textFieldX.setStyle("-fx-text-fill: red");
            }
        });
        textFieldY.textProperty().addListener(observable -> {
            //check that it correct
            double value;
            try {
                value = Double.parseDouble(textFieldY.getText());
                correctY = true;
                textFieldY.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 10 || value > sketchPane.getPrefHeight() - 10) {
                    correctY = false;
                    textFieldY.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctY = false;
                textFieldY.setStyle("-fx-text-fill: red");
            }
        });

        textFieldEdgeHeight.textProperty().addListener(observable -> {
            int value;
            try {
                value = Integer.parseInt(textFieldEdgeHeight.getText());
                correctEdgeHeight = true;
                textFieldEdgeHeight.setStyle("-fx-text-fill: #B3B4B4");
                int selectedDepth = Integer.parseInt(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem());
                if (value < selectedDepth || value > Edge.MAX_HEIGHT) {
                    correctEdgeHeight = false;
                    textFieldEdgeHeight.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctEdgeHeight = false;
                textFieldEdgeHeight.setStyle("-fx-text-fill: red");
            }
        });
        textFieldBorderHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            int value;
            try {
                value = Integer.parseInt(textFieldBorderHeight.getText());
                correctBorderHeight = true;
                textFieldBorderHeight.setStyle("-fx-text-fill: #B3B4B4");
                if (value < Border.MIN_HEIGHT || value > Border.MAX_HEIGHT) {
                    correctBorderHeight = false;
                    textFieldBorderHeight.setStyle("-fx-text-fill: red");
                }

            } catch (NumberFormatException ex) {
                correctBorderHeight = false;
                textFieldBorderHeight.setStyle("-fx-text-fill: red");
            }
        });

        translateXProperty().addListener((observable, oldValue, newValue) -> {
            textFieldX.setText(String.format("%.0f", getTranslateX()));
        });
        translateYProperty().addListener((observable, oldValue, newValue) -> {
            textFieldY.setText(String.format("%.0f", getTranslateY()));
        });


        btnRotateLeft.setOnMouseClicked(event -> {
            this.rotateShape(-90);
        });
        btnRotateRight.setOnMouseClicked(event -> {
            this.rotateShape(+90);
        });

        shapeSettings.heightProperty().addListener((observable, oldValue, newValue) -> {
            settingsRootAnchorPane.setPrefHeight(newValue.doubleValue());
        });
    }

    @Override
    public void refreshShapeSettings() {
        System.out.println("REFRESH SHAPE SETTINGS ");

        checkBoxMaterialDefault.setSelected(materialDefault);
        checkBoxDefaultHeights.setSelected(edgesHeightsDefault);

        choiceBoxMaterial.getItems().clear();
        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        if (!choiceBoxMaterial.getItems().contains(shapeMaterial.getReceiptName())) {
            setShapeMaterial(ProjectHandler.getDefaultMaterial(), ProjectHandler.getDefaultMaterial().getDefaultDepth());
            //shapeMaterial = ProjectHandler.getDefaultMaterial();
        }
        choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getReceiptName());

        if (checkBoxMaterialDefault.isSelected()) {
            choiceBoxMaterial.setDisable(true);
            choiceBoxMaterialDepth.setDisable(true);
        } else {
            choiceBoxMaterial.setDisable(false);
            choiceBoxMaterialDepth.setDisable(false);
        }

        if (checkBoxDefaultHeights.isSelected()) {
            textFieldEdgeHeight.setDisable(true);
            textFieldBorderHeight.setDisable(true);
        } else {
            textFieldEdgeHeight.setDisable(false);
            textFieldBorderHeight.setDisable(false);
        }

        if (saveMaterialImageOnEdges) {
            checkBoxSaveImage.setSelected(true);
        }

        choiceBoxMaterialDepth.getItems().clear();
        for (String s : shapeMaterial.getDepths()) {
            choiceBoxMaterialDepth.getItems().add(s);
        }
        choiceBoxMaterialDepth.getSelectionModel().select(String.valueOf(shapeDepth));

        textFieldASize.setText(String.format("%.0f", sizeAReal));
        textFieldBSize.setText(String.format("%.0f", sizeBReal));
        textFieldCSize.setText(String.format("%.0f", sizeCReal));


        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));

        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
        textFieldBorderHeight.setText(String.valueOf(borderHeight));

    }

    @Override
    public void shapeSettingsSaveBtnClicked() {
        System.out.println("SAVE");
        if ((!correctBorderHeight) || (!correctEdgeHeight) || (!correctASize) || (!correctBSize) || (!correctCSize) || (!correctDSize) || (!correctX) || (!correctY)) {
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Проверьте введенные данные!", null);
            return;
        }

        saveMaterialImageOnEdges = checkBoxSaveImage.isSelected();

        sizeAReal = Integer.parseInt(textFieldASize.getText());
        sizeBReal = Integer.parseInt(textFieldBSize.getText());
        sizeCReal = Math.pow(Math.pow(sizeAReal, 2) + Math.pow(sizeBReal, 2), 0.5);

        double minMaterialSize = (shapeMaterial.getMaterialWidth() < shapeMaterial.getMaterialHeight()) ? shapeMaterial.getMaterialWidth() : shapeMaterial.getMaterialHeight();
        double maxMaterialSize = (shapeMaterial.getMaterialWidth() > shapeMaterial.getMaterialHeight()) ? shapeMaterial.getMaterialWidth() : shapeMaterial.getMaterialHeight();

        if ((sizeAReal > minMaterialSize && sizeBReal > minMaterialSize) || (sizeAReal > maxMaterialSize || sizeBReal > maxMaterialSize)) {
            sizeAReal = sizeAShape / commonShapeScale;
            sizeBReal = sizeBShape / commonShapeScale;
            //System.out.println("WIDTH 2 = " + sizeAReal);
            if ((sizeAReal > shapeMaterial.getMaterialWidth() && sizeAReal > shapeMaterial.getMaterialHeight()) || (sizeBReal > shapeMaterial.getMaterialWidth() && sizeBReal > shapeMaterial.getMaterialHeight())) {
                sizeAReal = shapeMaterial.getMaterialWidth();
                sizeBReal = shapeMaterial.getMaterialHeight();
                sizeAShape = sizeAReal * commonShapeScale;
                sizeBShape = sizeBReal * commonShapeScale;
            }
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Не соответствует размеру материала. Необходимо разделить фигуру", null);

            sizeAShape = sizeAReal * commonShapeScale;
            sizeBShape = sizeBReal * commonShapeScale;
//            sizeCShape = sizeCReal*commonShapeScale;

            textFieldASize.setText(String.format("%.0f", sizeAReal));
            textFieldBSize.setText(String.format("%.0f", sizeBReal));
//            textFieldCSize.setText(String.format("%.0f", sizeCReal));

            return;
        } else {
            sizeAShape = sizeAReal * commonShapeScale;
            sizeBShape = sizeBReal * commonShapeScale;
            //System.out.println("WIDTH 3 = " + sizeAReal);
        }

        sizeAShape = sizeAReal * commonShapeScale;
        sizeBShape = sizeBReal * commonShapeScale;
        sizeCShape = sizeCReal * commonShapeScale;


        textFieldASize.setText(String.format("%.0f", sizeAReal));
        textFieldBSize.setText(String.format("%.0f", sizeBReal));
        textFieldCSize.setText(String.format("%.0f", sizeCReal));


        setTranslateX(Double.parseDouble(textFieldX.getText()));
        setTranslateY(Double.parseDouble(textFieldY.getText()));


        materialDefault = checkBoxMaterialDefault.isSelected();
        edgesHeightsDefault = checkBoxDefaultHeights.isSelected() ? true : false;

        if (checkBoxMaterialDefault.isSelected()) {
            if (shapeDepth != ProjectHandler.getDefaultMaterial().getDefaultDepth() || (!shapeMaterial.getName().equals(ProjectHandler.getDefaultMaterial()))) {
                setShapeMaterial(ProjectHandler.getDefaultMaterial(), ProjectHandler.getDefaultMaterial().getDefaultDepth());
                choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getReceiptName());
            }
        } else {
            for (Material material : ProjectHandler.getMaterialsListInProject()) {
                if (choiceBoxMaterial.getSelectionModel().getSelectedItem().equals(material.getReceiptName())) {
                    setShapeMaterial(material, Integer.parseInt(choiceBoxMaterialDepth.getSelectionModel().getSelectedItem()));
                }
            }
        }

//        if(edgeHeight != Integer.parseInt(textFieldEdgeHeight.getText()) || borderHeight != Integer.parseInt(textFieldBorderHeight.getText())){
        setEdgesHeights(false, Integer.parseInt(textFieldEdgeHeight.getText()), Integer.parseInt(textFieldBorderHeight.getText()));
//        }

        rebuildShapeView();
        selectShape();

    }

    @Override
    public void shapeSettingsCancelBtnClicked() {
        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));

        refreshShapeSettings();
    }

    public void rotateShapeInSettings(double angle) {

        Point2D pivot = new Point2D(paneShapeView.getPrefWidth() / 2, paneShapeView.getPrefHeight() / 2);

        SketchObject.rotatePolygon(polygonSettingsShape, pivot, angle);

        double newAX = ((labelA.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelA.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newAY = ((labelA.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelA.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelA.setTranslateX(newAX - 15);
        labelA.setTranslateY(newAY - 15);

        double newBX = ((labelB.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelB.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newBY = ((labelB.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelB.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelB.setTranslateX(newBX - 15);
        labelB.setTranslateY(newBY - 15);

        double newCX = ((labelC.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelC.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newCY = ((labelC.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelC.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelC.setTranslateX(newCX - 15);
        labelC.setTranslateY(newCY - 15);

    }

    /**
     * SETTINGS  END
     */


    @Override
    public ArrayList<Point2D[]> getShapeEdges() {
        return null;
    }

    @Override
    public int getEdgeHeight() {
        return edgeHeight;
    }

    @Override
    public int getBorderHeight() {
        return borderHeight;
    }

    @Override
    public double getShapeWidth() {
        return sizeAReal;
    }

    @Override
    public double getShapeHeight() {
        return sizeBReal;
    }


    public void edgeManagerShow(SketchEdge edge) {
        if (SketchDesigner.getSelectionModeForEdges()) {
            if (!SketchDesigner.getSelectedEdges().contains(edge)) {
                if (SketchDesigner.getSelectedEdgeMaterial() == null || SketchDesigner.getSelectedEdgeMaterial().getName().equals(shapeMaterial.getName())) {
                    SketchDesigner.setSelectedEdgeMaterial(shapeMaterial);
                    SketchDesigner.getSelectedEdges().add(edge);
                    edge.select(true);
                } else {
                    InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Разные типы Материалов", null);
                }

            }

        } else {
            ArrayList<SketchEdge> edgesList = new ArrayList<>();
            edgesList.add(edge);
            EdgeManager edgeManager = new EdgeManager(edgesList);
            edgeManager.show(this.getScene(), edge);
        }
    }

    @Override
    public void rebuildShapeView() {

        double saveAngle = rotateAngle;
        rotateShape(-rotateAngle);

        sketchPane.getChildren().remove(dimensionsPane);
        getChildren().clear();
        points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(0.0, sizeBShape),
                new Point2D(sizeAShape, sizeBShape)
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY()
        );

//        if(imageForFill == null){
//            polygon.setFill(shapeColor);
//            System.err.println("CANT FILL RECTANGLE SHAPE");
//        }else {
//            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
//            polygon.setFill(image_pattern);
//        }
        unSelectShape();

        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.CENTERED);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(sizeBShape + connectAreaWidth * 2);
        setPrefWidth(sizeAShape + connectAreaWidth * 2);
        //setStyle("-fx-background-color: Blue");

        getChildren().add(polygon);
        //getChildren().add(allDimensions);

        updateShapeNumber();


        if (elementType == ElementTypes.TABLETOP || elementType == ElementTypes.FOOT) {
            initEdgesZones();
            refreshEdgeView();
        }

        initConnectionPoints();
        //CutDesigner.refreshCutView();

        for (AdditionalFeature feature : featuresList) {
            getChildren().add(feature);
            feature.toFront();
        }

        rotateShape(saveAngle);

    }

    @Override
    public void deleteShape() {
        ProjectHandler.getMaterialsUsesInProjectObservable().remove(shapeMaterial.getName() + "#" + shapeDepth);
        if (elementType == ElementTypes.TABLETOP)
            ProjectHandler.getDepthsTableTopsUsesInProjectObservable().remove(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            ProjectHandler.getDepthsWallPanelsUsesInProjectObservable().remove(String.valueOf(shapeDepth));

        ProjectHandler.getEdgesHeightsUsesInProjectObservable().remove(String.valueOf(edgeHeight));
        ProjectHandler.getBordersHeightsUsesInProjectObservable().remove(String.valueOf(borderHeight));

        SketchDesigner.getSketchPane().getChildren().remove(this);
        SketchDesigner.getSketchShapesList().remove(this);
        if (topConnectedShape != null) topConnectedShape.disconnectFromShape(this);
        if (bottomConnectedShape != null) bottomConnectedShape.disconnectFromShape(this);
        if (leftConnectedShape != null) leftConnectedShape.disconnectFromShape(this);
        if (rightConnectedShape != null) rightConnectedShape.disconnectFromShape(this);


        for (String s : ProjectHandler.getMaterialsUsesInProjectObservable()) {
            System.out.println(s);
        }

        for (String s : ProjectHandler.getDepthsTableTopsUsesInProjectObservable()) {
            System.out.println(s);
        }

        Iterator<LinearDimension> it = SketchDesigner.getAllDimensions().iterator();
        while (it.hasNext()) {
            LinearDimension ld = it.next();
            if (ld.getConnectPoint1().getParent().equals(this) || ld.getConnectPoint2().getParent().equals(this)) {
                it.remove();
                sketchPane.getChildren().remove(ld);
            }
        }
    }

    @Override
    public void disconnectFromShape(SketchShape connectedShape) {

    }

    @Override
    public void createContextMenu() {
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem disconnectMenuItem = new MenuItem("Disconnect");
        MenuItem unionMenuItem = new MenuItem("Union");
        //MenuItem disconnectMenuItem = new MenuItem("Create");

        shapeContextMenu.getItems().add(deleteMenuItem);
        shapeContextMenu.getItems().add(disconnectMenuItem);
        //shapeContextMenu.getItems().add(unionMenuItem);

        deleteMenuItem.setOnAction(event -> {
            deleteShape();

        });

        disconnectMenuItem.setOnAction(event -> {
            //disconnectFromAll();
        });
        unionMenuItem.setOnAction(event -> {
            SketchDesigner.createUnionShape();
        });
    }

    @Override
    public void setEdgesZoneWidth(double widthEdge) {
        if (widthEdge <= 5) {
            this.widthEdge = widthEdge;
        } else {
            this.widthEdge = 5;
        }
        initEdgesZones();
    }

    @Override
    public void initEdgesZones() {

        //sideAEdge
        {
            if (sideAEdge == null) sideAEdge = new SketchEdge();
            sideAEdge.getPoints().clear();

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, sizeBShape));
            pointsForEdge.add(new Point2D(sizeAShape, sizeBShape));
            pointsForEdge.add(new Point2D(sizeAShape, sizeBShape - widthEdge));
            pointsForEdge.add(new Point2D(0.0, sizeBShape - widthEdge));
            //ROTATE Point
            double angle = Math.toDegrees(Math.atan((sizeBShape) / (sizeAShape)));
            //Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), -90 + angle);
            //pointsForEdge.set(2, p2);

            sideAEdge.getPoints().addAll(
                    pointsForEdge.get(0).getX(), pointsForEdge.get(0).getY(),
                    pointsForEdge.get(1).getX(), pointsForEdge.get(1).getY(),
                    pointsForEdge.get(2).getX(), pointsForEdge.get(2).getY(),
                    pointsForEdge.get(3).getX(), pointsForEdge.get(3).getY()
            );
            getChildren().remove(sideAEdge);
            getChildren().add(sideAEdge);
            sideAEdge.setOnMouseClicked(event -> edgeManagerShow(sideAEdge));
        }

        //sideBEdge
        {
            if (sideBEdge == null) sideBEdge = new SketchEdge();
            sideBEdge.getPoints().clear();

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(0.0, sizeBShape));
            pointsForEdge.add(new Point2D(widthEdge, sizeBShape));
            pointsForEdge.add(new Point2D(widthEdge, 0.0));
            //ROTATE Point
            double angle = Math.toDegrees(Math.atan((sizeBShape) / (sizeAShape)));
            //Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), 90 - angle);
            //pointsForEdge.set(3, p3);

            sideBEdge.getPoints().addAll(
                    pointsForEdge.get(0).getX(), pointsForEdge.get(0).getY(),
                    pointsForEdge.get(1).getX(), pointsForEdge.get(1).getY(),
                    pointsForEdge.get(2).getX(), pointsForEdge.get(2).getY(),
                    pointsForEdge.get(3).getX(), pointsForEdge.get(3).getY()
            );
            getChildren().remove(sideBEdge);
            getChildren().add(sideBEdge);
            sideBEdge.setOnMouseClicked(event -> edgeManagerShow(sideBEdge));
        }

        //sideCEdge
        {
            if (sideCEdge == null) sideCEdge = new SketchEdge();
            sideCEdge.getPoints().clear();

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(sizeAShape, sizeBShape));
            pointsForEdge.add(new Point2D(sizeAShape, sizeBShape + widthEdge));
            pointsForEdge.add(new Point2D(0.0, widthEdge));
            //ROTATE Point
            double angle = Math.toDegrees(Math.atan((sizeBShape) / (sizeAShape)));
            Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), 90 - angle);
            Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), 90 - angle);
            pointsForEdge.set(2, p2);
            pointsForEdge.set(3, p3);

            sideCEdge.getPoints().addAll(
                    pointsForEdge.get(0).getX(), pointsForEdge.get(0).getY(),
                    pointsForEdge.get(1).getX(), pointsForEdge.get(1).getY(),
                    pointsForEdge.get(2).getX(), pointsForEdge.get(2).getY(),
                    pointsForEdge.get(3).getX(), pointsForEdge.get(3).getY()
            );
            getChildren().remove(sideCEdge);
            getChildren().add(sideCEdge);
            sideCEdge.setOnMouseClicked(event -> edgeManagerShow(sideCEdge));
        }


        sideAEdge.setSketchEdgeOwner(this);
        sideBEdge.setSketchEdgeOwner(this);
        sideCEdge.setSketchEdgeOwner(this);

    }

    @Override
    public void changeElementEdge(SketchEdge edge, SketchEdge newEdge) {
        newEdge.setSketchEdgeOwner(this);
        if (sideAEdge == edge) {

            if (!sideAEdge.getName().equals(newEdge.getName())) {
                if (sideAEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideAEdge);
                } else if (sideAEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideAEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }
            getChildren().remove(sideAEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideAEdge.getPoints());
            sideAEdge = newEdge;
            getChildren().add(sideAEdge);
            sideAEdge.setOnMouseClicked(event -> edgeManagerShow(sideAEdge));

        } else if (sideBEdge == edge) {

            if (!sideBEdge.getName().equals(newEdge.getName())) {
                if (sideBEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideBEdge);
                } else if (sideBEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideBEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }
            getChildren().remove(sideBEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideBEdge.getPoints());
            sideBEdge = newEdge;
            getChildren().add(sideBEdge);
            sideBEdge.setOnMouseClicked(event -> edgeManagerShow(sideBEdge));

        } else if (sideCEdge == edge) {

            if (!sideCEdge.getName().equals(newEdge.getName())) {
                if (sideCEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideCEdge);
                } else if (sideCEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideCEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }
            getChildren().remove(sideCEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideCEdge.getPoints());
            sideCEdge = newEdge;
            getChildren().add(sideCEdge);
            sideCEdge.setOnMouseClicked(event -> edgeManagerShow(sideCEdge));
        }
        //initEdgesZones();
        refreshEdgeView();
        //refreshShapeSettings();
        //rotateEdgesIcons(rotateAngle);
    }

    @Override
    public ArrayList<SketchEdge> getEdges() {
        return new ArrayList<SketchEdge>(Arrays.asList(sideAEdge, sideBEdge, sideCEdge));
    }

    @Override
    public void selectEdge(SketchEdge edge) {

    }

    @Override
    public void deSelectEdge(SketchEdge edge) {

    }

    @Override
    public void deSelectAllEdges() {

    }

    private void refreshEdgeView() {

        getChildren().remove(triangleIconSideAEdge);
        getChildren().remove(triangleIconSideBEdge);
        getChildren().remove(triangleIconSideCEdge);


        getChildren().remove(lineGrooveAEdge);
        getChildren().remove(lineGrooveBEdge);
        getChildren().remove(lineGrooveCEdge);


        double translateX = 0;
        double translateY = 0;

        double angleA = Math.toDegrees(Math.atan((sizeBShape) / (sizeAShape)));
        double angleB = Math.toDegrees(Math.atan((sizeAShape) / (sizeBShape)));

        if (sideAEdge instanceof Edge) {
            triangleIconSideAEdge = new Polygon(
                    0.0, 0.0,
                    -2.5, -5.0,
                    2.5, -5.0);
            translateY = sizeBShape;
        } else {
            triangleIconSideAEdge = new Polygon(
                    0.0, 0.0,
                    5.0, 5.0,
                    2.5, 2.5,
                    5.0, 0.0,
                    0.0, 5.0,
                    2.5, 2.5,
                    0.0, 0.0);
            translateY = sizeBShape - 5 - 2.5;
            triangleIconSideAEdge.setStroke(Color.BLACK);
        }
        triangleIconSideAEdge.setFill(Color.BLACK);
        for (int i = 0; i < triangleIconSideAEdge.getPoints().size(); i += 2) {
            double newX = triangleIconSideAEdge.getPoints().get(i).doubleValue() + sizeAShape / 2 - 2.5;
            double newY = triangleIconSideAEdge.getPoints().get(i + 1).doubleValue() + translateY;
            triangleIconSideAEdge.getPoints().set(i, newX);
            triangleIconSideAEdge.getPoints().set(i + 1, newY);
        }
        getChildren().add(triangleIconSideAEdge);
        Tooltip.install(triangleIconSideAEdge, sideAEdge.getTooltip());
        SketchObject.rotatePolygon(triangleIconSideAEdge, getRotationPivot(), rotateAngle);
        if (sideAEdge.isDefined()) {
            triangleIconSideAEdge.setVisible(true);

            if (sideAEdge instanceof Edge && ((Edge) sideAEdge).isStoneHemOrLeakGroove()) {

                double p2x = sizeAShape - 5 / Math.tan(Math.toRadians(angleA));

                lineGrooveAEdge = new Line(0, sizeBShape - 5, p2x, sizeBShape - 5);
                lineGrooveAEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveAEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveAEdge);
                lineGrooveAEdge.setVisible((true));
            }

        } else {
            triangleIconSideAEdge.setVisible(false);
        }


        if (sideBEdge instanceof Edge) {
            triangleIconSideBEdge = new Polygon(
                    0.0, 0.0,
                    0.0, 5.0,
                    -5.0, 2.5);
            translateX = 5.0;
        } else {
            triangleIconSideBEdge = new Polygon(
                    0.0, 0.0,
                    5.0, 5.0,
                    2.5, 2.5,
                    5.0, 0.0,
                    0.0, 5.0,
                    2.5, 2.5,
                    0.0, 0.0);
            translateX = 2.5;
            triangleIconSideBEdge.setStroke(Color.BLACK);
        }
        triangleIconSideBEdge.setFill(Color.BLACK);
        for (int i = 0; i < triangleIconSideBEdge.getPoints().size(); i += 2) {
            double newX = triangleIconSideBEdge.getPoints().get(i).doubleValue() + translateX;
            double newY = triangleIconSideBEdge.getPoints().get(i + 1).doubleValue() + sizeBShape / 2 - 2.5;
            triangleIconSideBEdge.getPoints().set(i, newX);
            triangleIconSideBEdge.getPoints().set(i + 1, newY);
        }
        getChildren().add(triangleIconSideBEdge);
        Tooltip.install(triangleIconSideBEdge, sideBEdge.getTooltip());
        SketchObject.rotatePolygon(triangleIconSideBEdge, getRotationPivot(), rotateAngle);
        if (sideBEdge.isDefined()) {
            triangleIconSideBEdge.setVisible(true);

            if (sideBEdge instanceof Edge && ((Edge) sideBEdge).isStoneHemOrLeakGroove()) {

                double p1y = 5 / Math.tan(Math.toRadians(angleB));

                lineGrooveBEdge = new Line(5, p1y, 5, sizeBShape);
                lineGrooveBEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveBEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveBEdge);
                lineGrooveBEdge.setVisible((true));
            }

        } else {
            triangleIconSideBEdge.setVisible(false);
        }


        if (sideCEdge instanceof Edge) {
            triangleIconSideCEdge = new Polygon(
                    0.0, 0.0,
                    2.5, 5.0,
                    -2.5, 5.0);
            translateX = (sizeAShape) / 2;
            translateY = (sizeBShape) / 2;
        } else {
            triangleIconSideCEdge = new Polygon(
                    -2.5, 0.0,
                    2.5, 5.0,
                    0.0, 2.5,
                    2.5, 0.0,
                    -2.5, 5.0,
                    0.0, 2.5,
                    -2.5, 0.0);
            translateX = (sizeAShape) / 2;
            translateY = (sizeBShape) / 2;
            triangleIconSideCEdge.setStroke(Color.BLACK);
        }
        //triangleIconSideCEdge.setRotate(angle);
        SketchObject.rotatePolygon(triangleIconSideCEdge, new Point2D(0.0, 0.0), angleA);
        triangleIconSideCEdge.setFill(Color.BLACK);
        for (int i = 0; i < triangleIconSideCEdge.getPoints().size(); i += 2) {
            double newX = triangleIconSideCEdge.getPoints().get(i).doubleValue() + translateX;
            double newY = triangleIconSideCEdge.getPoints().get(i + 1).doubleValue() + translateY;
            triangleIconSideCEdge.getPoints().set(i, newX);
            triangleIconSideCEdge.getPoints().set(i + 1, newY);
        }

        getChildren().add(triangleIconSideCEdge);
        Tooltip.install(triangleIconSideCEdge, sideCEdge.getTooltip());
        SketchObject.rotatePolygon(triangleIconSideCEdge, getRotationPivot(), rotateAngle);
        if (sideCEdge.isDefined()) {
            triangleIconSideCEdge.setVisible(true);

            if (sideCEdge instanceof Edge && ((Edge) sideCEdge).isStoneHemOrLeakGroove()) {

                double p1y = 5 / Math.tanh(Math.toRadians(angleB));
                double p2x = sizeAShape - 5 / Math.sin(Math.toRadians(angleA));
                lineGrooveCEdge = new Line(0, p1y, p2x, sizeBShape);
                lineGrooveCEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveCEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveCEdge);
                lineGrooveCEdge.setVisible((true));
            }

        } else {
            triangleIconSideCEdge.setVisible(false);
        }
    }

    @Override
    public void updateMaterialList() {
        choiceBoxMaterial.getItems().clear();
        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getName());
        }
        if (!choiceBoxMaterial.getItems().contains(shapeMaterial.getName())) {
            shapeMaterial = ProjectHandler.getDefaultMaterial();
        }
        choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getName());
    }

    @Override
    public void updateEdgesHeight() {
        if (edgesHeightsDefault) {
            setEdgesHeights(false, shapeDepth, Border.DEFAULT_HEIGHT);
        }
        CutDesigner.getInstance().refreshCutView();
    }

    @Override
    public double getEdgeOrBorderLength(SketchEdge edge) {
        if (edge == sideAEdge) return sizeAReal;
        else if (edge == sideBEdge) return sizeBReal;
        else if (edge == sideCEdge) return Math.pow(Math.pow(sizeAReal, 2) + Math.pow(sizeBReal, 2), 0.5);

        return 0;
    }

    @Override
    public double getEdgesLength() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Edge)) length += sizeAReal;
        if (sideBEdge != null && (sideBEdge instanceof Edge)) length += sizeBReal;
        if (sideCEdge != null && (sideCEdge instanceof Edge))
            length += Math.pow(Math.pow(sizeAReal, 2) + Math.pow(sizeBReal, 2), 0.5);

        return length;

    }

    @Override
    public double getBordersType1Length() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Border) && sideAEdge.getName().indexOf("1") != -1)
            length += sizeAReal;
        if (sideBEdge != null && (sideBEdge instanceof Border) && sideBEdge.getName().indexOf("1") != -1)
            length += sizeBReal;
        if (sideCEdge != null && (sideCEdge instanceof Border) && sideCEdge.getName().indexOf("1") != -1)
            length += Math.pow(Math.pow(sizeAReal, 2) + Math.pow(sizeBReal, 2), 0.5);

        return length;
    }

    @Override
    public double getBordersType2Length() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Border) && sideAEdge.getName().indexOf("2") != -1)
            length += sizeAReal;
        if (sideBEdge != null && (sideBEdge instanceof Border) && sideBEdge.getName().indexOf("2") != -1)
            length += sizeBReal;
        if (sideCEdge != null && (sideCEdge instanceof Border) && sideCEdge.getName().indexOf("2") != -1)
            length += Math.pow(Math.pow(sizeAReal, 2) + Math.pow(sizeBReal, 2), 0.5);

        return length;
    }


    @Override
    public void rotateShape(double angle) {
        super.rotateShape(angle);

        rotateShapeInSettings(angle);
    }


    private void rotateFeatures(double angle) {
        for (AdditionalFeature feature : featuresList) {

            feature.setRotate(rotateAngle);
            double x = feature.getTranslateX() + feature.getBoundsInLocal().getWidth() / 2;
            double y = feature.getTranslateY() + feature.getBoundsInLocal().getHeight() / 2;
            Point2D newPoint = SketchObject.rotatePoint(new Point2D(x, y), new Point2D(getRotationPivot().getX(), getRotationPivot().getY()), angle);
            feature.setTranslateX(newPoint.getX() - feature.getBoundsInLocal().getWidth() / 2);
            feature.setTranslateY(newPoint.getY() - feature.getBoundsInLocal().getHeight() / 2);
        }
    }

    @Override
    public void addDimensionsMode(boolean mode) {
        for (ConnectPoint p : connectionPoints) {
            p.setSelectionMode(mode);
        }
        addDimensionMode = mode;
    }

    /**
     * JOINTS
     */

    @Override
    public void clearJointsLists() {
        sideAJointsList.clear();
        sideBJointsList.clear();
        sideCJointsList.clear();
    }

    @Override
    public void refreshLinesForJoints() {
        Point2D point1;
        Point2D point2;

        point1 = new Point2D(0.0, 0.0);
        point2 = new Point2D(sizeAShape, 0.0);
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineAJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(0.0, 0.0);
        point2 = new Point2D(0.0, sizeBShape);
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineBJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(sizeAShape, 0.0);
        point2 = new Point2D(sizeAShape, sizeBShape);
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineCJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    @Override
    public ArrayList<Line> getLineForJoints() {
        refreshLinesForJoints();
        return new ArrayList<Line>(Arrays.asList(lineAJoint, lineBJoint, lineCJoint));
    }

    @Override
    public void addJoint(Line lineForJointSide, Joint newJoint) {
        if (lineForJointSide.equals(lineAJoint)) {
            sideAJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineBJoint)) {
            sideBJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineCJoint)) {
            sideCJointsList.add(newJoint);
        }
    }

    @Override
    public ArrayList<Joint> getJoints() {
        ArrayList<Joint> list = new ArrayList<>();

        list.addAll(sideAJointsList);
        list.addAll(sideBJointsList);
        list.addAll(sideCJointsList);

        return list;
    }

    /**
     * JOINTS
     */

    public void initShapeMaterial(Material material, int depth) {
        shapeMaterial = material;
        this.shapeDepth = depth;

        if (shapeMaterial.getName().indexOf("Акриловый камень") != -1 || shapeMaterial.getName().indexOf("Полиэфирный камень") != -1) {

//            checkBoxSaveImage.setDisable(true);
//            checkBoxSaveImage.setSelected(false);
//            saveMaterialImageOnEdges = false;

            if (elementType == ElementTypes.TABLETOP) {
                edgeHeight = 40;
            } else if (elementType == ElementTypes.WALL_PANEL) {
                edgeHeight = shapeDepth;
                textFieldEdgeHeight.setText("" + shapeDepth);
            } else if (elementType == ElementTypes.WINDOWSILL) {
                edgeHeight = 40;
            } else if (elementType == ElementTypes.FOOT) {
                edgeHeight = 40;
            }

        } else {

//            checkBoxSaveImage.setDisable(false);

            edgeHeight = shapeDepth;
        }


        ProjectHandler.getMaterialsUsesInProjectObservable().add(shapeMaterial.getName() + "#" + shapeDepth);

        if (elementType == ElementTypes.TABLETOP)
            ProjectHandler.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            ProjectHandler.getDepthsWallPanelsUsesInProjectObservable().add(String.valueOf(shapeDepth));

    }

    public void setShapeMaterial(Material material, int depth) {

        if (material != shapeMaterial) {

            if (featuresList.size() != 0) {
                for (AdditionalFeature feature : featuresList) {
                    this.getChildren().remove(feature);
                }
                featuresList.clear();

                MainWindow.showInfoMessage(InfoMessage.MessageType.WARNING, "Дополнительные элементы были удалены");
            }

        }

        if (shapeMaterial != null)
            ProjectHandler.getMaterialsUsesInProjectObservable().remove(shapeMaterial.getName() + "#" + shapeDepth);

        if (elementType == ElementTypes.TABLETOP)
            ProjectHandler.getDepthsTableTopsUsesInProjectObservable().remove(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            ProjectHandler.getDepthsWallPanelsUsesInProjectObservable().remove(String.valueOf(shapeDepth));

        shapeMaterial = material;
        this.shapeDepth = depth;

        ProjectHandler.getMaterialsUsesInProjectObservable().add(shapeMaterial.getName() + "#" + shapeDepth);

        if (elementType == ElementTypes.TABLETOP)
            ProjectHandler.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            ProjectHandler.getDepthsWallPanelsUsesInProjectObservable().add(String.valueOf(shapeDepth));

        if (checkBoxDefaultHeights.isSelected()) {
            if (shapeMaterial.getName().indexOf("Акриловый камень") != -1) {
                if (elementType == ElementTypes.TABLETOP) {
                    edgeHeight = 40;
                    textFieldEdgeHeight.setText("" + 40);
                } else if (elementType == ElementTypes.WALL_PANEL) {
                    edgeHeight = shapeDepth;
                    textFieldEdgeHeight.setText("" + shapeDepth);
                } else if (elementType == ElementTypes.WINDOWSILL) {
                    textFieldEdgeHeight.setText("" + 40);
                    edgeHeight = 40;
                } else if (elementType == ElementTypes.FOOT) {
                    textFieldEdgeHeight.setText("" + 40);
                    edgeHeight = 40;
                }
            }
        } else {
            try {
                int typedEdgeHeight = Integer.parseInt(textFieldEdgeHeight.getText());
                if (shapeDepth > typedEdgeHeight) {
                    edgeHeight = shapeDepth;
                    textFieldEdgeHeight.setText("" + shapeDepth);
                } else {
                    edgeHeight = typedEdgeHeight;
                }
            } catch (NumberFormatException ex) {
            }
        }

        if (material.getName().indexOf("Акриловый камень") != -1 || material.getName().indexOf("Полиэфирный камень") != -1) {

            checkBoxSaveImage.setDisable(true);
            checkBoxSaveImage.setSelected(false);
            saveMaterialImageOnEdges = false;

            if (sideAEdge.getType() != 1) {
                changeElementEdge(sideAEdge, new SketchEdge());
            }
            if (sideBEdge.getType() != 1) {
                changeElementEdge(sideBEdge, new SketchEdge());
            }
            if (sideCEdge.getType() != 1) {
                changeElementEdge(sideCEdge, new SketchEdge());
            }
        } else if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                material.getName().indexOf("Натуральный камень") != -1 ||
                material.getName().indexOf("Dektone") != -1 ||
                material.getName().indexOf("Мраморный агломерат") != -1 ||
                material.getName().indexOf("Кварцекерамический камень") != -1) {

            checkBoxSaveImage.setDisable(false);

            if (sideAEdge.getType() != 2) {
                changeElementEdge(sideAEdge, new SketchEdge());
            }
            if (sideBEdge.getType() != 2) {
                changeElementEdge(sideBEdge, new SketchEdge());
            }
            if (sideCEdge.getType() != 2) {
                changeElementEdge(sideCEdge, new SketchEdge());
            }
        }


    }


    public void setEdgesHeights(boolean init, int edgeHeight, int borderHeight) {

        boolean haveBorder = true;
        if (!init) {
            ProjectHandler.getEdgesHeightsUsesInProjectObservable().remove("" + this.edgeHeight);
            if (haveBorder) ProjectHandler.getBordersHeightsUsesInProjectObservable().remove("" + this.borderHeight);
        }

        if (edgesHeightsDefault) {
            if (shapeMaterial.getName().indexOf("Акриловый камень") != -1) {
                if (elementType == ElementTypes.TABLETOP) {
                    this.edgeHeight = 40;
                } else if (elementType == ElementTypes.WALL_PANEL) {
                    this.edgeHeight = shapeDepth;

                } else if (elementType == ElementTypes.WINDOWSILL) {
                    this.edgeHeight = 40;
                } else if (elementType == ElementTypes.FOOT) {
                    this.edgeHeight = 40;
                }
            } else {
                this.edgeHeight = shapeDepth;
            }
        } else {
            if (shapeDepth > edgeHeight) {
                this.edgeHeight = shapeDepth;
            } else {
                this.edgeHeight = edgeHeight;
            }
        }

        if (shapeMaterial != null && (shapeMaterial.getName().indexOf("Кварцевый агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Натуральный камень") != -1 ||
                shapeMaterial.getName().indexOf("Dektone") != -1 ||
                shapeMaterial.getName().indexOf("Мраморный агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Кварцекерамический камень") != -1)) {
            System.out.println(this.edgeHeight + " " + shapeDepth);
            if (this.edgeHeight == shapeDepth) {
                if (sideAEdge instanceof Edge && ((Edge) sideAEdge).getSubType() > 7)
                    changeElementEdge(sideAEdge, new SketchEdge());
                if (sideBEdge instanceof Edge && ((Edge) sideBEdge).getSubType() > 7)
                    changeElementEdge(sideBEdge, new SketchEdge());
                if (sideCEdge instanceof Edge && ((Edge) sideCEdge).getSubType() > 7)
                    changeElementEdge(sideCEdge, new SketchEdge());

            } else if (this.edgeHeight > shapeDepth) {
                if (sideAEdge instanceof Edge && ((Edge) sideAEdge).getSubType() <= 7)
                    changeElementEdge(sideAEdge, new SketchEdge());
                if (sideBEdge instanceof Edge && ((Edge) sideBEdge).getSubType() <= 7)
                    changeElementEdge(sideBEdge, new SketchEdge());
                if (sideCEdge instanceof Edge && ((Edge) sideCEdge).getSubType() <= 7)
                    changeElementEdge(sideCEdge, new SketchEdge());
            }
        }

        if (textFieldEdgeHeight != null) textFieldEdgeHeight.setText("" + this.edgeHeight);
        this.borderHeight = borderHeight;

        ProjectHandler.getEdgesHeightsUsesInProjectObservable().add("" + this.edgeHeight);
        if (haveBorder) ProjectHandler.getBordersHeightsUsesInProjectObservable().add("" + this.borderHeight);
    }

    @Override
    public void setShapeDepth(int shapeDepth) {
        System.out.println("String.valueOf(shapeDepth)" + String.valueOf(shapeDepth));

        if (elementType == ElementTypes.TABLETOP)
            ProjectHandler.getDepthsTableTopsUsesInProjectObservable().remove(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            ProjectHandler.getDepthsWallPanelsUsesInProjectObservable().remove(String.valueOf(shapeDepth));
        super.setShapeDepth(shapeDepth);

        // ProjectHandler.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
        if (elementType == ElementTypes.TABLETOP)
            ProjectHandler.getDepthsTableTopsUsesInProjectObservable().add(String.valueOf(shapeDepth));
        else if (elementType == ElementTypes.WALL_PANEL)
            ProjectHandler.getDepthsWallPanelsUsesInProjectObservable().add(String.valueOf(shapeDepth));

        if (shapeDepth > edgeHeight) {
            edgeHeight = shapeDepth;
            textFieldEdgeHeight.setText("" + shapeDepth);
        }

    }

    @Override
    public Node getViewForListCell() {
        Pane pane = new Pane();

        double newScale;
        if (sizeAShape > sizeBShape) {
            newScale = 30.0 / sizeAShape;
        } else {
            newScale = 30.0 / sizeBShape;
        }


        Point2D[] points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(0.0, sizeBShape * newScale),
                new Point2D(sizeAShape * newScale, sizeBShape * newScale)
        };

        Polygon polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY()
        );

        pane.getChildren().add(polygon);
        polygon.setFill(Color.BLUE);
        Label labelShapeNumber = new Label(String.valueOf(thisShapeNumber));
        labelShapeNumber.setStyle("-fx-text-fill:#B3B4B4;");
        /*pane.setTranslateX(0);
        pane.setTranslateY(0);*/
        pane.getChildren().add(labelShapeNumber);

        //pane.setStyle("-fx-background-color:red;");


        //polygon.setScaleX(newScale);
        //polygon.setScaleY(newScale);
        return pane;
    }

    @Override
    public Tooltip getTooltipForListCell() {
        return new Tooltip("Фигура");
    }

    public static Node getStaticViewForListCell(ElementTypes elementType) {
        Pane pane = new Pane();
        Point2D[] points = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(0, 100),
                new Point2D(100, 100)
        };
        Polygon polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY()
        );
        pane.getChildren().add(polygon);
        if (elementType == ElementTypes.TABLETOP) polygon.setFill(TABLE_TOP_COLOR);
        else if (elementType == ElementTypes.WALL_PANEL) polygon.setFill(WALL_PANEL_COLOR);
        else if (elementType == ElementTypes.WINDOWSILL) polygon.setFill(WINDOWSILL_COLOR);
        else if (elementType == ElementTypes.FOOT) polygon.setFill(FOOT_COLOR);
        return pane;
    }

    public static Tooltip getStaticTooltipForListCell() {
        return new Tooltip("Фигура");
    }

    @Override
    public void unSelectShape() {
        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            System.err.println("CANT FILL Triangle SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
            polygon.setFill(image_pattern);
        }
    }

    @Override
    public JSONObject getJsonView() {
        JSONObject object = new JSONObject();

        object.put("shapeNumber", thisShapeNumber);
        object.put("elementType", elementType.toString());
        object.put("shapeType", shapeType.toString());
        object.put("sizeAReal", sizeAReal);
        object.put("sizeBReal", sizeBReal);
        object.put("sizeCReal", sizeCReal);
        object.put("material", shapeMaterial.getName());
        object.put("shapeDepth", shapeDepth);
        object.put("edgesHeightsDefault", edgesHeightsDefault);
        object.put("saveMaterialImageOnEdges", saveMaterialImageOnEdges);
        object.put("edgeHeight", edgeHeight);
        object.put("borderHeight", borderHeight);
        object.put("rotateAngle", rotateTransform.getAngle());
        object.put("opacity", this.getOpacity());

        JSONObject sideAEdgeObject = new JSONObject();
        JSONObject sideBEdgeObject = new JSONObject();
        JSONObject sideCEdgeObject = new JSONObject();
        JSONObject sideDEdgeObject = new JSONObject();
        JSONObject sideEEdgeObject = new JSONObject();

        sideAEdgeObject.put("edgeType", (sideAEdge instanceof Edge) ? "edge" : "border");
        sideAEdgeObject.put("name", sideAEdge.getName());
        if (sideAEdge instanceof Border) {
            sideAEdgeObject.put("topCutType", ((Border) sideAEdge).getBorderCutType());
            sideAEdgeObject.put("sideCutType", ((Border) sideAEdge).getBorderSideCutType());
            sideAEdgeObject.put("anglesCutType", ((Border) sideAEdge).getBorderAnglesCutType());
        } else if (sideAEdge instanceof Edge) {
            sideAEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideAEdge).isStoneHemOrLeakGroove());
        }

        sideBEdgeObject.put("edgeType", (sideBEdge instanceof Edge) ? "edge" : "border");
        sideBEdgeObject.put("name", sideBEdge.getName());
        if (sideBEdge instanceof Border) {
            sideBEdgeObject.put("topCutType", ((Border) sideBEdge).getBorderCutType());
            sideBEdgeObject.put("sideCutType", ((Border) sideBEdge).getBorderSideCutType());
            sideBEdgeObject.put("anglesCutType", ((Border) sideBEdge).getBorderAnglesCutType());
        } else if (sideBEdge instanceof Edge) {
            sideBEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideBEdge).isStoneHemOrLeakGroove());
        }

        sideCEdgeObject.put("edgeType", (sideCEdge instanceof Edge) ? "edge" : "border");
        sideCEdgeObject.put("name", sideCEdge.getName());
        if (sideCEdge instanceof Border) {
            sideCEdgeObject.put("topCutType", ((Border) sideCEdge).getBorderCutType());
            sideCEdgeObject.put("sideCutType", ((Border) sideCEdge).getBorderSideCutType());
            sideCEdgeObject.put("anglesCutType", ((Border) sideCEdge).getBorderAnglesCutType());
        } else if (sideCEdge instanceof Edge) {
            sideCEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideCEdge).isStoneHemOrLeakGroove());
        }

        object.put("sideAEdge", sideAEdgeObject);
        object.put("sideBEdge", sideBEdgeObject);
        object.put("sideCEdge", sideCEdgeObject);
        object.put("sideDEdge", sideDEdgeObject);
        object.put("sideEEdge", sideEEdgeObject);

        JSONArray sketchDesignerXY = new JSONArray();
        sketchDesignerXY.add(getTranslateX());
        sketchDesignerXY.add(getTranslateY());
        object.put("sketchDesignerXY", sketchDesignerXY);

        rotateFeatures(-rotateAngle);
        JSONArray featuresArray = new JSONArray();
        for (AdditionalFeature feature : featuresList) {
            featuresArray.add(feature.getJsonView());
        }
        rotateFeatures(rotateAngle);
        object.put("featuresArray", featuresArray);

        return object;
    }

    @Override
    public void initFromJson(JSONObject jsonObject) {
        thisShapeNumber = ((Long) jsonObject.get("shapeNumber")).intValue();
        elementType = ElementTypes.valueOf(((String) jsonObject.get("elementType")));
        shapeType = ShapeType.valueOf(((String) jsonObject.get("shapeType")));
        sizeAReal = ((Double) jsonObject.get("sizeAReal")).doubleValue();
        sizeBReal = ((Double) jsonObject.get("sizeBReal")).doubleValue();
        sizeCReal = ((Double) jsonObject.get("sizeCReal")).doubleValue();


        edgesHeightsDefault = ((Boolean) jsonObject.get("edgesHeightsDefault")).booleanValue();
        saveMaterialImageOnEdges = ((Boolean) jsonObject.get("saveMaterialImageOnEdges")).booleanValue();
        this.setOpacity(((Double) jsonObject.get("opacity")).doubleValue());

        setEdgesHeights(false, ((Long) jsonObject.get("edgeHeight")).intValue(), ((Long) jsonObject.get("borderHeight")).intValue());

        System.out.println("initFromJson shapeNumber = " + thisShapeNumber);
        String materialName = ((String) jsonObject.get("material"));
        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            if (materialName.equals(material.getName())) {
                setShapeMaterial(material, ((Long) jsonObject.get("shapeDepth")).intValue());
                break;
            }
        }


        //init edges:
        JSONObject sideAEdgeObject = ((JSONObject) jsonObject.get("sideAEdge"));
        JSONObject sideBEdgeObject = ((JSONObject) jsonObject.get("sideBEdge"));
        JSONObject sideCEdgeObject = ((JSONObject) jsonObject.get("sideCEdge"));

        int edgeType = 0;
        if (shapeMaterial.getName().indexOf("Акриловый камень") != -1 || shapeMaterial.getName().indexOf("Полиэфирный камень") != -1) {
            edgeType = 1;
        } else if (shapeMaterial.getName().indexOf("Кварцевый агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Натуральный камень") != -1 ||
                shapeMaterial.getName().indexOf("Dektone") != -1 ||
                shapeMaterial.getName().indexOf("Мраморный агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Кварцекерамический камень") != -1) {
            edgeType = 2;
        }

        String sideAEdgeType = (String) sideAEdgeObject.get("edgeType");
        String sideAEdgeName = (String) sideAEdgeObject.get("name");
        if (sideAEdgeType.equals("edge")) {
            changeElementEdge(sideAEdge, new Edge(sideAEdgeName, edgeType));
            ((Edge) sideAEdge).setStoneHemOrLeakGroove(((Boolean) sideAEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideAEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideAEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideAEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideAEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideAEdge, border);
        }
        //changeElementEdge(sideAEdge, (sideAEdgeType.equals("edge"))? new Edge(sideAEdgeName, edgeType) : new Border(sideAEdgeName, edgeType) );

        String sideBEdgeType = (String) sideBEdgeObject.get("edgeType");
        String sideBEdgeName = (String) sideBEdgeObject.get("name");
        if (sideBEdgeType.equals("edge")) {
            changeElementEdge(sideBEdge, new Edge(sideBEdgeName, edgeType));
            ((Edge) sideBEdge).setStoneHemOrLeakGroove(((Boolean) sideBEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideBEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideBEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideBEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideBEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideBEdge, border);
        }
        //changeElementEdge(sideBEdge, (sideBEdgeType.equals("edge"))? new Edge(sideBEdgeName, edgeType) : new Border(sideBEdgeName, edgeType) );

        String sideCEdgeType = (String) sideCEdgeObject.get("edgeType");
        String sideCEdgeName = (String) sideCEdgeObject.get("name");
        if (sideCEdgeType.equals("edge")) {
            changeElementEdge(sideCEdge, new Edge(sideCEdgeName, edgeType));
            ((Edge) sideCEdge).setStoneHemOrLeakGroove(((Boolean) sideCEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideCEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideCEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideCEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideCEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideCEdge, border);
        }
        //changeElementEdge(sideCEdge, (sideCEdgeType.equals("edge"))? new Edge(sideCEdgeName, edgeType) : new Border(sideCEdgeName, edgeType) );


        JSONArray sketchDesignerXY = (JSONArray) (jsonObject.get("sketchDesignerXY"));
        this.setTranslateX(((Double) sketchDesignerXY.get(0)).doubleValue());
        this.setTranslateY(((Double) sketchDesignerXY.get(1)).doubleValue());


        if (shapeMaterial.getName().equals(ProjectHandler.getDefaultMaterial().getName())) {
            materialDefault = true;
        } else {
            materialDefault = false;
        }
        sizeAShape = sizeAReal * commonShapeScale;
        sizeBShape = sizeBReal * commonShapeScale;
//        sizeCShape = sizeCReal*commonShapeScale;


        JSONArray featuresArray = (JSONArray) (jsonObject.get("featuresArray"));
        for (Object o : featuresArray) {
            JSONObject jsonObj = (JSONObject) o;
            AdditionalFeature feature = AdditionalFeature.initFromJson(jsonObj, this);
            addFeature(feature);

        }

        rebuildShapeView();
        refreshShapeSettings();
        rotateShape(((Double) jsonObject.get("rotateAngle")).doubleValue());
    }
}
