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
import javafx.scene.shape.*;
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

public class SketchShapeCircleHalf extends SketchShape {

    Pane sketchPane;

    ArrayList<Point2D> pointsList;
    double sizeAShape = 60;
    //    double sizeBShape = 60;
    double sizeRadiusShape = 30;
    double connectAreaWidth = 0;

    //private Image imageForFill = null;

    //connect points:
    //ArrayList<CornerConnectPoint> connectionPoints = new ArrayList<>();
    //ArrayList<ConnectPoint> cutShapeConnectPoints = new ArrayList<>(Arrays.asList(new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint()));

    CutShapeEdge cutShapeEdgeA;
    //    CutShapeEdge cutShapeEdgeB;
//    CutShapeEdge cutShapeEdgeC;
//    CutShapeEdge cutShapeEdgeD;
    CutShapeEdge cutShapeEdgeRadius;

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
    //    SketchEdge sideBEdge = null;
//    SketchEdge sideCEdge = null;
//    SketchEdge sideDEdge = null;
    SketchEdge sideRadiusEdge = null;

    Polygon triangleIconSideAEdge;
    //    Polygon triangleIconSideBEdge;
//    Polygon triangleIconSideCEdge;
//    Polygon triangleIconSideDEdge;
    Polygon triangleIconSideRadiusEdge;


    Line lineGrooveAEdge;
    //    Line lineGrooveBEdge;
//    Line lineGrooveCEdge;
//    Line lineGrooveDEdge;
    Arc arcGrooveRadiusEdge;

    //Joints:
    Line lineAJoint = null;
//    Line lineBJoint = null;
//    Line lineCJoint = null;
//    Line lineDJoint = null;

    ArrayList<Joint> sideAJointsList = new ArrayList<>();
//    ArrayList<Joint> sideBJointsList = new ArrayList<>();
//    ArrayList<Joint> sideCJointsList = new ArrayList<>();
//    ArrayList<Joint> sideDJointsList = new ArrayList<>();


    //links to connected shapes:
    private SketchShape leftConnectedShape, rightConnectedShape, topConnectedShape, bottomConnectedShape;

    //Shape settings:
    boolean materialDefault = true; //default = true;
    boolean edgesHeightsDefault = true; //default = true;


    int edgeHeight = 0;
    int borderHeight = 0;

    double sizeAReal = (sizeRadiusShape / commonShapeScale) * 2;
    //    double sizeBReal = sizeBShape/commonShapeScale;
    double sizeRadiusReal = sizeRadiusShape / commonShapeScale;

    CheckBox checkBoxMaterialDefault, checkBoxDefaultHeights, checkBoxSaveImage;
    ChoiceBox<String> choiceBoxMaterial;
    ChoiceBox<String> choiceBoxMaterialDepth;
    TextField textFieldASize, textFieldBSize, textFieldRadiusSize;
    TextField textFieldX, textFieldY;
    TextField textFieldEdgeHeight, textFieldBorderHeight;

    Group groupEdges;
    Button btnRotateRight, btnRotateLeft;

    Pane paneShapeView;
    Polygon polygonSettingsShape;
    Label labelA, labelRadius;

    boolean correctEdgeHeight = true, correctBorderHeight = true, correctX = true, correctY = true, correctASize = true, correctBSize = true, correctRadiusSize = true;


    public SketchShapeCircleHalf(double layoutX, double layoutY, ElementTypes elementType, Pane sketchPane) {
        setChildShape(this);

        shapeType = ShapeType.CIRCLE_HALF;
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


        // CREATE POINTS
        pointsList = new ArrayList<>();
        pointsList.add(new Point2D(0.0, sizeRadiusShape));
        pointsList.add(new Point2D(sizeRadiusShape * 2, sizeRadiusShape));


        //ROTATE CORNER
        Point2D pivot = new Point2D(sizeRadiusShape, sizeRadiusShape);
        for (int i = 1; i < 180; i++) {
            double originX = pointsList.get(pointsList.size() - 1).getX();
            double originY = pointsList.get(pointsList.size() - 1).getY();
            double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
            double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
            pointsList.add(new Point2D(X, Y));
        }


        //CREATE POLYGON
        polygon = new Polygon();
        for (Point2D p : pointsList) {
            polygon.getPoints().add(new Double(p.getX()));
            polygon.getPoints().add(new Double(p.getY()));
        }


        // create ImagePattern
        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
            System.err.println("CANT FILL CIRCLE HALF SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            System.err.println("CANT FILL CIRCLE HALF SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
            polygon.setFill(image_pattern);
        }

        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.INSIDE);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(sizeRadiusShape + connectAreaWidth * 2);
        setPrefWidth(sizeRadiusShape * 2 + connectAreaWidth * 2);

        //setStyle("-fx-background-color: Blue"); //SET PANE COLOR UNDER SHAPE

        getChildren().add(polygon);


        updateShapeNumber();
        initEdgesZones();
        initConnectionPoints();
        refreshEdgeView();

        //sketchPane.getChildren().add(this);
    }

    public SketchShapeCircleHalf(ElementTypes elementType, Material material, int depth, double radius) {

        this.sizeAReal = radius * 2;
        this.sizeRadiusReal = radius;

        sizeAShape = sizeAReal * commonShapeScale;

        sizeRadiusShape = sizeRadiusReal * commonShapeScale;

        initShapeMaterial(material, depth);

        setChildShape(this);

        shapeType = ShapeType.CIRCLE_HALF;
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


        //initShapeMaterial(ProjectHandler.getDefaultMaterial(), ProjectHandler.getDefaultMaterial().getDefaultDepth());
//        setEdgesHeights(true, ProjectHandler.getDefaultMaterial().getDefaultDepth(), Border.DEFAULT_HEIGHT);
//
        initShapeSettings();
        initShapeSettingsControlLogic();
        createContextMenu();


        // CREATE POINTS
        pointsList = new ArrayList<>();
        pointsList.add(new Point2D(0.0, sizeRadiusShape));
        pointsList.add(new Point2D(sizeRadiusShape * 2, sizeRadiusShape));


        //ROTATE CORNER
        Point2D pivot = new Point2D(sizeRadiusShape, sizeRadiusShape);
        for (int i = 1; i < 180; i++) {
            double originX = pointsList.get(pointsList.size() - 1).getX();
            double originY = pointsList.get(pointsList.size() - 1).getY();
            double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
            double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
            pointsList.add(new Point2D(X, Y));
        }

        //CREATE POLYGON
        polygon = new Polygon();
        for (Point2D p : pointsList) {
            polygon.getPoints().add(new Double(p.getX()));
            polygon.getPoints().add(new Double(p.getY()));
        }


        // create ImagePattern
        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
            System.err.println("CANT FILL CIRCLE HALF SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            System.err.println("CANT FILL CIRCLE HALF SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
            polygon.setFill(image_pattern);
        }

        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.INSIDE);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(sizeRadiusShape + connectAreaWidth * 2);
        setPrefWidth(sizeRadiusShape * 2 + connectAreaWidth * 2);

        //setStyle("-fx-background-color: Blue"); //SET PANE COLOR UNDER SHAPE

        getChildren().add(polygon);


        updateShapeNumber();
        initEdgesZones();
        initConnectionPoints();
        refreshEdgeView();
    }

    @Override
    public void initConnectionPoints() {

        if (connectionPoints.size() == 0) {
            for (int i = 0; i < 4; i++) {
                connectionPoints.add(new CornerConnectPoint(this));
            }
        }

        connectionPoints.get(0).setTranslateX(-(widthConnectPoint / 2));
        connectionPoints.get(0).setTranslateY(sizeRadiusShape - (widthConnectPoint / 2));
        connectionPoints.get(0).hide();

        connectionPoints.get(1).setTranslateX(sizeRadiusShape - (widthConnectPoint / 2));
        connectionPoints.get(1).setTranslateY(sizeRadiusShape - (widthConnectPoint / 2));
        connectionPoints.get(1).hide();

        connectionPoints.get(2).setTranslateX(sizeRadiusShape * 2 - (widthConnectPoint / 2));
        connectionPoints.get(2).setTranslateY(sizeRadiusShape - (widthConnectPoint / 2));
        connectionPoints.get(2).hide();

        connectionPoints.get(3).setTranslateX(sizeRadiusShape - (widthConnectPoint / 2));
        connectionPoints.get(3).setTranslateY(-(widthConnectPoint / 2));
        connectionPoints.get(3).hide();


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
        sideRadiusEdge.setDisable(edgesDisable);
    }

    @Override
    public boolean isConnectedToShapeOutOfUnion() {
        return false;
    }

    @Override
    public double getVerticalSize() {
        return sizeRadiusShape;
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

        labelShapeNumber.setTranslateX(sizeRadiusShape - 7.5);
        labelShapeNumber.setTranslateY(sizeRadiusShape - 20);
        labelShapeNumber.setMouseTransparent(true);
    }

    @Override
    public CutShape getCutShapeFromJSON(JSONObject obj) {
        return null;
    }

    /* CUT SHAPE START */
    @Override
    public void refreshCutShapeView() {
        if (cutShape == null) {
            createCutShape();
            createCutShapeFeatures();
            createCutEdges();
            //System.out.println("CREATE CUT SHAPE CIRCLE HALF " + thisShapeNumber);
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

            // CREATE POINTS
            cutZonePolygonPoints = new ArrayList<>();
            cutZonePolygonPoints.add(new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape));
            cutZonePolygonPoints.add(new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutZonePolygonPoints.add(new Point2D(sizeRadiusShape * 2 + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutZonePolygonPoints.add(new Point2D(sizeRadiusShape * 2 + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape));
            //ROTATE CORNER
            Point2D pivot = new Point2D(sizeRadiusShape, sizeRadiusShape);
            for (int i = 1; i < 180; i++) {
                double originX = cutZonePolygonPoints.get(cutZonePolygonPoints.size() - 1).getX();
                double originY = cutZonePolygonPoints.get(cutZonePolygonPoints.size() - 1).getY();
                double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
                double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
                cutZonePolygonPoints.add(new Point2D(X, Y));
            }

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
            polygonPoints.add(new Point2D(0.0, sizeRadiusShape));
            polygonPoints.add(new Point2D(sizeRadiusShape * 2, sizeRadiusShape));

            //ROTATE CORNER
            Point2D pivot = new Point2D(sizeRadiusShape, sizeRadiusShape);
            for (int i = 1; i < 180; i++) {
                double originX = polygonPoints.get(polygonPoints.size() - 1).getX();
                double originY = polygonPoints.get(polygonPoints.size() - 1).getY();
                double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
                double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
                polygonPoints.add(new Point2D(X, Y));
            }

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
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));

        cutShapeConnectPoints.get(0).changeSetPoint(new Point2D(0.0, sizeRadiusShape));
        cutShapeConnectPoints.get(1).changeSetPoint(new Point2D(sizeRadiusShape, sizeRadiusShape));
        cutShapeConnectPoints.get(2).changeSetPoint(new Point2D(sizeRadiusShape * 2, sizeRadiusShape));
        cutShapeConnectPoints.get(3).changeSetPoint(new Point2D(sizeRadiusShape, 0.0));

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutShapeConnectPoints.get(1).changeSetPointShift(new Point2D(sizeRadiusShape, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutShapeConnectPoints.get(2).changeSetPointShift(new Point2D(sizeRadiusShape * 2 + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutShapeConnectPoints.get(3).changeSetPointShift(new Point2D(sizeRadiusShape, -1 * CutDesigner.CUT_SHAPES_CUTSHIFT));
        }

        cutShape.setConnectPoints(cutShapeConnectPoints);
        for (ConnectPoint connectPoint : cutShapeConnectPoints) {
            cutShape.getChildren().add(connectPoint);
        }
        cutShape.hideConnectionPoints();

        //add label with shape number
        cutShape.refreshLabelNumber();

        //CREATE DIMENSIONS

        double sizeH = sizeRadiusShape * 2;
        double sizeY = sizeRadiusShape;

        Label dimensionVLabel = new Label();
        dimensionVLabel.setId("dimensionVLabel");
        dimensionVLabel.setPickOnBounds(false);

        dimensionVLabel.setAlignment(Pos.CENTER);
        dimensionVLabel.setPrefWidth(sizeH);
        //dimensionVLabel.setPrefWidth(60);
        dimensionVLabel.setPrefHeight(8);
        dimensionVLabel.setTranslateX(0.0);
        dimensionVLabel.setTranslateY(0.0);
        dimensionVLabel.setText(String.format("%.0f", sizeY / commonShapeScale));
        dimensionVLabel.setFont(Font.font(8));
        //dimensionVLabel.setRotate(-90);
        Rotate rotateV = new Rotate(-90);
        //dimensionVLabel.getTransforms().add(rotateV);

        cutShape.setDimensionV(dimensionVLabel);

        Label dimensionHLabel = new Label();
        dimensionHLabel.setId("dimensionHLabel");
        dimensionHLabel.setPickOnBounds(false);
        double shiftX = 2;
        double shiftY = sizeY - 5;

        dimensionHLabel.setAlignment(Pos.CENTER);
        dimensionHLabel.setPrefWidth(sizeH);
        dimensionHLabel.setTranslateX(0.0);
        dimensionHLabel.setTranslateY(sizeRadiusShape - 11);
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
        createRadiusCutEdge();

        cutShapesEdgesList.clear();
        cutShapesEdgesList.addAll(Arrays.asList(cutShapeEdgeA, cutShapeEdgeRadius));
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
        pointsForEdge.add(new Point2D(sizeRadiusShape * 2, 0.0));
        pointsForEdge.add(new Point2D(sizeRadiusShape * 2, h));
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
            cutShapeEdgeA.setStartCoordinate(new Point2D(0.0, sizeRadiusShape));
        } else {
            cutShapeEdgeA.setStartCoordinate(null);
        }
    }

    private void createRadiusCutEdge() {

        cutShapeEdgeRadius = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideRadiusEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }

        // CREATE POINTS

        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(-h, 0.0));

        //ROTATE CORNER
        Point2D pivotEdge = new Point2D(sizeRadiusShape, 0.0);
        for (int i = 1; i < 180; i++) {
            double originX = pointsForEdge.get(pointsForEdge.size() - 1).getX();
            double originY = pointsForEdge.get(pointsForEdge.size() - 1).getY();
            double X = (originX - pivotEdge.getX()) * Math.cos(Math.toRadians(1)) - (originY - pivotEdge.getY()) * Math.sin(Math.toRadians(1)) + pivotEdge.getX();
            double Y = (originX - pivotEdge.getX()) * Math.sin(Math.toRadians(1)) + (originY - pivotEdge.getY()) * Math.cos(Math.toRadians(1)) + pivotEdge.getY();
            pointsForEdge.add(new Point2D(X, Y));
        }
        pointsForEdge.add(new Point2D(sizeRadiusShape * 2 + h, 0.0));
        pointsForEdge.add(new Point2D(sizeRadiusShape * 2, 0.0));

        for (int i = 1; i < 180; i++) {
            double originX = pointsForEdge.get(pointsForEdge.size() - 1).getX();
            double originY = pointsForEdge.get(pointsForEdge.size() - 1).getY();
            double X = (originX - pivotEdge.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivotEdge.getY()) * Math.sin(Math.toRadians(-1)) + pivotEdge.getX();
            double Y = (originX - pivotEdge.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivotEdge.getY()) * Math.cos(Math.toRadians(-1)) + pivotEdge.getY();
            pointsForEdge.add(new Point2D(X, Y));
        }

        //ADD POINTS TO POLYGON:
        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeEdgeRadius.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeRadius.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeRadius.getChildren().remove(cutShapeEdgeRadius.getPolygon());
        cutShapeEdgeRadius.setPolygon(edgePolygon);
        cutShapeEdgeRadius.getChildren().add(edgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> rightEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeRadius);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeRadius);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeRadius);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeRadius);

        rightEdgeConnectPoints.add(point1);
        rightEdgeConnectPoints.add(point2);
        rightEdgeConnectPoints.add(point3);
        rightEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(181));
        point4.changeSetPoint(pointsForEdge.get(182));

        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeRadius);
        }
        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            cutShapeEdgeRadius.getChildren().add(connectPoint);
        }

        cutShapeEdgeRadius.setConnectPoints(rightEdgeConnectPoints);
        cutShapeEdgeRadius.hideConnectionPoints();

        if (sideRadiusEdge.isDefined()) {
            cutShapeEdgeRadius.setStartCoordinate(new Point2D(0.0, sizeRadiusShape));
        } else {
            cutShapeEdgeRadius.setStartCoordinate(null);
        }
    }

    private void updateCutShapeView() {

        cutShape.setSizesInfo("R "+ (int)(sizeRadiusShape/ProjectHandler.getCommonShapeScale()));

        //create Cut zone polygon
        ArrayList<Point2D> cutZonePolygonPoints = null;
        Polygon cutZonePolygon = null;
        {

            // CREATE POINTS
            cutZonePolygonPoints = new ArrayList<>();
            cutZonePolygonPoints.add(new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape));
            cutZonePolygonPoints.add(new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutZonePolygonPoints.add(new Point2D(sizeRadiusShape * 2 + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutZonePolygonPoints.add(new Point2D(sizeRadiusShape * 2 + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape));
            //ROTATE CORNER
            Point2D pivot = new Point2D(sizeRadiusShape, sizeRadiusShape);
            for (int i = 1; i < 180; i++) {
                double originX = cutZonePolygonPoints.get(cutZonePolygonPoints.size() - 1).getX();
                double originY = cutZonePolygonPoints.get(cutZonePolygonPoints.size() - 1).getY();
                double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
                double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
                cutZonePolygonPoints.add(new Point2D(X, Y));
            }

            cutZonePolygon = cutShape.getCutZonePolygon();
            cutZonePolygon.getPoints().clear();
            for (Point2D p : cutZonePolygonPoints) {
                cutZonePolygon.getPoints().add(new Double(p.getX()));
                cutZonePolygon.getPoints().add(new Double(p.getY()));
            }
        }

        //create main poligon
        ArrayList<Point2D> polygonPoints = null;
        Polygon cutShapePolygon = null;
        {
            // CREATE POINTS
            polygonPoints = new ArrayList<>();
            polygonPoints.add(new Point2D(0.0, sizeRadiusShape));
            polygonPoints.add(new Point2D(sizeRadiusShape * 2, sizeRadiusShape));

            //ROTATE CORNER
            Point2D pivot = new Point2D(sizeRadiusShape, sizeRadiusShape);
            for (int i = 1; i < 180; i++) {
                double originX = polygonPoints.get(polygonPoints.size() - 1).getX();
                double originY = polygonPoints.get(polygonPoints.size() - 1).getY();
                double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
                double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
                polygonPoints.add(new Point2D(X, Y));
            }


            cutShapePolygon = cutShape.getPolygon();
            cutShapePolygon.getPoints().clear();

            for (Point2D p : polygonPoints) {
                cutShapePolygon.getPoints().add(new Double(p.getX()));
                cutShapePolygon.getPoints().add(new Double(p.getY()));
            }
        }
        //cutShapePolygon.setFill(shapeColor);

        cutShapeConnectPoints.get(0).changeSetPoint(new Point2D(0.0, sizeRadiusShape));
        cutShapeConnectPoints.get(1).changeSetPoint(new Point2D(sizeRadiusShape, sizeRadiusShape));
        cutShapeConnectPoints.get(2).changeSetPoint(new Point2D(sizeRadiusShape * 2, sizeRadiusShape));
        cutShapeConnectPoints.get(3).changeSetPoint(new Point2D(sizeRadiusShape, 0.0));

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutShapeConnectPoints.get(1).changeSetPointShift(new Point2D(sizeRadiusShape, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutShapeConnectPoints.get(2).changeSetPointShift(new Point2D(sizeRadiusShape * 2 + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeRadiusShape + CutDesigner.CUT_SHAPES_CUTSHIFT));
            cutShapeConnectPoints.get(3).changeSetPointShift(new Point2D(sizeRadiusShape, -1 * CutDesigner.CUT_SHAPES_CUTSHIFT));
        }

        //Update dimensions labels:
        double sizeH = sizeRadiusShape * 2;
        double sizeY = sizeRadiusShape;

        //cutShape.getDimensionVLabel().setRotate(0);
        cutShape.getDimensionVLabel().setPrefWidth(sizeH);
        cutShape.getDimensionVLabel().setTranslateX(0.0);
        cutShape.getDimensionVLabel().setTranslateY(0.0);
        cutShape.getDimensionVLabel().setText(String.format("%.0f", sizeY / ProjectHandler.getCommonShapeScale()));
        cutShape.getDimensionVLabel().toFront();


        double shiftX = 2;
        double shiftY = sizeY - 5;

        //cutShape.getDimensionHLabel().setRotate(0);
        cutShape.getDimensionHLabel().setPrefWidth(sizeH);
        cutShape.getDimensionHLabel().setTranslateX(0.0);
        cutShape.getDimensionHLabel().setTranslateY(sizeRadiusShape - 11);
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

            if (sideRadiusEdge instanceof Border) {
                updateRadiusCutEdge();
            } else {
                cutShapeEdgeRadius.setStartCoordinate(null);
            }

        } else {
            updateACutEdge();
            updateRadiusCutEdge();
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
            pointsForEdge.add(new Point2D(sizeRadiusShape * 2, 0.0));
            pointsForEdge.add(new Point2D(sizeRadiusShape * 2, h));
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

            cutShapeEdgeA.setStartCoordinate(new Point2D(0.0, sizeRadiusShape));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeA.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeA.getStartCoordinate().getX());
                cutShapeEdgeA.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeA.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeA.setStartCoordinate(null);
        }
    }

    private void updateRadiusCutEdge() {

        if (sideRadiusEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideRadiusEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);

            Polygon edgePolygon = cutShapeEdgeRadius.getPolygon();
            edgePolygon.getPoints().clear();
            // CREATE POINTS


            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(-h, 0.0));

            //ROTATE CORNER
            Point2D pivotEdge = new Point2D(sizeRadiusShape, 0.0);
            for (int i = 1; i < 180; i++) {
                double originX = pointsForEdge.get(pointsForEdge.size() - 1).getX();
                double originY = pointsForEdge.get(pointsForEdge.size() - 1).getY();
                double X = (originX - pivotEdge.getX()) * Math.cos(Math.toRadians(1)) - (originY - pivotEdge.getY()) * Math.sin(Math.toRadians(1)) + pivotEdge.getX();
                double Y = (originX - pivotEdge.getX()) * Math.sin(Math.toRadians(1)) + (originY - pivotEdge.getY()) * Math.cos(Math.toRadians(1)) + pivotEdge.getY();
                pointsForEdge.add(new Point2D(X, Y));
            }
            pointsForEdge.add(new Point2D(sizeRadiusShape * 2 + h, 0.0));
            pointsForEdge.add(new Point2D(sizeRadiusShape * 2, 0.0));

            for (int i = 1; i < 180; i++) {
                double originX = pointsForEdge.get(pointsForEdge.size() - 1).getX();
                double originY = pointsForEdge.get(pointsForEdge.size() - 1).getY();
                double X = (originX - pivotEdge.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivotEdge.getY()) * Math.sin(Math.toRadians(-1)) + pivotEdge.getX();
                double Y = (originX - pivotEdge.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivotEdge.getY()) * Math.cos(Math.toRadians(-1)) + pivotEdge.getY();
                pointsForEdge.add(new Point2D(X, Y));
            }

            //ADD POINTS TO POLYGON:
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeRadius.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeRadius.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

            //System.out.println("edgePolygon.getBoundsInLocal().getWidth() = " + edgePolygon.getBoundsInLocal().getWidth());
            //System.out.println("edgePolygon.getBoundsInLocal().getHeight() = " + edgePolygon.getBoundsInLocal().getHeight());
            //System.out.println("sizeRadiusShape = " + sizeRadiusShape);
            //cutShapeEdgeRadius.setStyle("-fx-background-color: red");

            //cutShapeEdgeRight.setStartCoordinate(new Point2D(cutShapeEdgeRight.getTranslateX(), cutShapeEdgeRight.getTranslateY()));

            //connect points:
            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeRadius.getConnectPoints();
            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(181));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(182));

            cutShapeEdgeRadius.setStartCoordinate(new Point2D(0.0, sizeRadiusShape));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeRadius.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeRadius.getStartCoordinate().getX());
                cutShapeEdgeRadius.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeRadius.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeRadius.setStartCoordinate(null);
        }

    }

    /* CUT SHAPE END */


    @Override
    public AnchorPane getShapeSettings() {
        refreshShapeSettings();
        return shapeSettings;
    }

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
        return sizeRadiusReal;
    }

    @Override
    public void initShapeSettings() {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/sketchShapeCircleHalfSettings.fxml"));
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
        textFieldRadiusSize = (TextField) settingsRootAnchorPane.lookup("#textFieldRadiusSize");
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

        // CREATE POINTS
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(new Point2D(0.0, 45));
        points.add(new Point2D(90, 45));

        //ROTATE CORNER
        Point2D pivot = new Point2D(45, 45);
        for (int i = 1; i < 180; i++) {
            double originX = points.get(points.size() - 1).getX();
            double originY = points.get(points.size() - 1).getY();
            double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
            double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
            points.add(new Point2D(X, Y));
        }

        //CREATE POLYGON
        polygonSettingsShape = new Polygon();
        for (Point2D p : points) {
            polygonSettingsShape.getPoints().add(new Double(p.getX()));
            polygonSettingsShape.getPoints().add(new Double(p.getY()));
        }
        //SHIFT POLYGON TO CENTER
        double shiftXToCenter = paneShapeView.getPrefWidth() / 2 - 45;
        double shiftYToCenter = paneShapeView.getPrefHeight() / 2 - 45;

        for (int i = 0; i < polygonSettingsShape.getPoints().size(); i += 2) {
            double newX = polygonSettingsShape.getPoints().get(i).doubleValue() + shiftXToCenter;
            double newY = polygonSettingsShape.getPoints().get(i + 1).doubleValue() + shiftYToCenter;
            polygonSettingsShape.getPoints().set(i, newX);
            polygonSettingsShape.getPoints().set(i + 1, newY);
        }


//        System.out.println("paneShapeView.getPrefWidth()/2 = " + paneShapeView.getPrefWidth()/2);
//        System.out.println("paneShapeView.getPrefHeight()/2 = " + paneShapeView.getPrefHeight()/2);
//
//        System.out.println("polygonSettingsShape.getTranslateX() = " + polygonSettingsShape.getTranslateX());
//        System.out.println("polygonSettingsShape.getTranslateY() = " + polygonSettingsShape.getTranslateY());
        polygonSettingsShape.setFill(Color.BLUEVIOLET);
        paneShapeView.getChildren().add(polygonSettingsShape);


        labelA = new Label("A");
        labelA.setAlignment(Pos.CENTER);
        labelA.setPrefSize(30.0, 30.0);
        labelA.setTranslateX(100);
        labelA.setTranslateY(75);

        labelRadius = new Label("R");
        labelRadius.setAlignment(Pos.CENTER);
        labelRadius.setPrefSize(30.0, 30.0);
        labelRadius.setTranslateX(100);
        labelRadius.setTranslateY(10);


        paneShapeView.getChildren().addAll(labelA, labelRadius);


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
        textFieldASize.setText(String.format("%.0f", sizeRadiusReal * 2));
        textFieldRadiusSize.setText(String.format("%.0f", sizeRadiusReal));
//        textFieldDSize.setText(String.format("%.0f", sizeDReal));
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
        checkBoxSaveImage.setOnMouseClicked(event -> {
        });

        choiceBoxMaterial.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            //System.out.println("shapeMaterial = " + shapeMaterial.getName());
            if (newValue == null) return;
            //if(!choiceBoxMaterial.getSelectionModel().getSelectedItem().equals(shapeMaterial.getReceiptName())){

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

            //}
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

        textFieldASize.textProperty().addListener(observable -> {
            double value;
            try {
                value = Double.parseDouble(textFieldASize.getText());
                double sizeRadius = Double.parseDouble(textFieldRadiusSize.getText());
                correctASize = true;
                textFieldASize.setStyle("-fx-text-fill: #B3B4B4");
                if ((value < 10 || value > 100000) || value < sizeRadius) {
                    correctASize = false;
                    textFieldASize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctASize = false;
                textFieldASize.setStyle("-fx-text-fill: red");
            }
            //check that it correct
        });

        textFieldRadiusSize.textProperty().addListener(observable -> {
            //check that it correct
            double value;
            try {
                value = Double.parseDouble(textFieldRadiusSize.getText());
                double sizeA = Double.parseDouble(textFieldASize.getText());
                correctRadiusSize = true;
                textFieldRadiusSize.setStyle("-fx-text-fill: #B3B4B4");
                if (value < 100) {
                    correctRadiusSize = false;
                    textFieldRadiusSize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctRadiusSize = false;
                textFieldRadiusSize.setStyle("-fx-text-fill: red");
            }
        });


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
        textFieldRadiusSize.setText(String.format("%.0f", sizeRadiusReal));
        //textFieldDSize.setText(String.format("%.0f", sizeDReal));

        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));

        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
        textFieldBorderHeight.setText(String.valueOf(borderHeight));

    }

    @Override
    public void shapeSettingsSaveBtnClicked() {
        System.out.println("SAVE");
        if ((!correctBorderHeight) || (!correctEdgeHeight) || (!correctASize) || (!correctBSize) || (!correctRadiusSize) || (!correctX) || (!correctY)) {
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Проверьте введенные данные!", null);
            return;
        }

        saveMaterialImageOnEdges = checkBoxSaveImage.isSelected();
        sizeRadiusReal = Integer.parseInt(textFieldRadiusSize.getText());
        sizeAReal = sizeRadiusReal * 2;


        //sizeDReal = Integer.parseInt(textFieldDSize.getText());

        double minMaterialSize = (shapeMaterial.getMaterialWidth() < shapeMaterial.getMaterialHeight()) ? shapeMaterial.getMaterialWidth() : shapeMaterial.getMaterialHeight();
        double maxMaterialSize = (shapeMaterial.getMaterialWidth() > shapeMaterial.getMaterialHeight()) ? shapeMaterial.getMaterialWidth() : shapeMaterial.getMaterialHeight();

        if ((sizeAReal > minMaterialSize && sizeRadiusReal > minMaterialSize) || (sizeAReal > maxMaterialSize || sizeRadiusReal > maxMaterialSize)) {
            sizeAReal = sizeAShape / commonShapeScale;
            sizeRadiusReal = sizeRadiusReal / commonShapeScale;
            //System.out.println("WIDTH 2 = " + sizeAReal);
            if ((sizeAReal > shapeMaterial.getMaterialWidth() && sizeAReal > shapeMaterial.getMaterialHeight()) || (sizeRadiusReal > shapeMaterial.getMaterialWidth() && sizeRadiusReal > shapeMaterial.getMaterialHeight())) {
                sizeAReal = shapeMaterial.getMaterialWidth();
                sizeAShape = sizeAReal * commonShapeScale;
            }
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Не соответствует размеру материала. Необходимо разделить фигуру", null);

            sizeAShape = sizeAReal * commonShapeScale;
            sizeRadiusShape = sizeRadiusReal * commonShapeScale;
            textFieldASize.setText(String.format("%.0f", sizeAReal));
            textFieldRadiusSize.setText(String.format("%.0f", sizeRadiusReal));

            return;
        } else {
            sizeAShape = sizeAReal * commonShapeScale;
            sizeRadiusShape = sizeRadiusReal * commonShapeScale;
            //System.out.println("WIDTH 3 = " + sizeAReal);
        }

        sizeAShape = sizeAReal * commonShapeScale;
        sizeRadiusShape = sizeRadiusReal * commonShapeScale;
        //sizeDShape = sizeDReal*commonShapeScale;


        textFieldASize.setText(String.format("%.0f", sizeAReal));
        textFieldRadiusSize.setText(String.format("%.0f", sizeRadiusReal));
        //textFieldDSize.setText(String.format("%.0f", sizeDReal));


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
        // CREATE POINTS
        pointsList = new ArrayList<>();
        pointsList.add(new Point2D(0.0, sizeRadiusShape));
        pointsList.add(new Point2D(sizeRadiusShape * 2, sizeRadiusShape));

        //ROTATE CORNER
        Point2D pivot = new Point2D(sizeRadiusShape, sizeRadiusShape);
        for (int i = 1; i < 180; i++) {
            double originX = pointsList.get(pointsList.size() - 1).getX();
            double originY = pointsList.get(pointsList.size() - 1).getY();
            double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
            double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
            pointsList.add(new Point2D(X, Y));
        }

        //CREATE POLYGON
        polygon = new Polygon();
        for (Point2D p : pointsList) {
            polygon.getPoints().add(new Double(p.getX()));
            polygon.getPoints().add(new Double(p.getY()));
        }

//        if(imageForFill == null){
//            polygon.setFill(shapeColor);
//            System.err.println("CANT FILL RECTANGLE SHAPE");
//        }else {
//            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0, polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
//            polygon.setFill(image_pattern);
//        }
        unSelectShape();

        polygon.setStroke(Color.BLACK);
        polygon.setStrokeType(StrokeType.INSIDE);
        polygon.setLayoutX(connectAreaWidth);
        polygon.setLayoutY(connectAreaWidth);

        setPrefHeight(sizeRadiusShape + connectAreaWidth * 2);
        setPrefWidth(sizeRadiusShape * 2 + connectAreaWidth * 2);
        //setStyle("-fx-background-color: Blue");

        getChildren().add(polygon);
        //getChildren().add(allDimensions);

        updateShapeNumber();


        if (elementType == ElementTypes.TABLETOP || elementType == ElementTypes.WINDOWSILL) {
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


        if (sideAEdge == null) sideAEdge = new SketchEdge();
        sideAEdge.getPoints().clear();
        sideAEdge.getPoints().addAll(
                0.0, sizeRadiusShape,
                sizeRadiusShape * 2, sizeRadiusShape,
                sizeRadiusShape * 2, sizeRadiusShape - widthEdge,
                0.0, sizeRadiusShape - widthEdge
        );
        getChildren().remove(sideAEdge);
        getChildren().add(sideAEdge);
        sideAEdge.setOnMouseClicked(event -> edgeManagerShow(sideAEdge));


        if (sideRadiusEdge == null) sideRadiusEdge = new SketchEdge();
        sideRadiusEdge.getPoints().clear();
        // CREATE POINTS
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(new Point2D(0.0, sizeRadiusShape));
        points.add(new Point2D(widthEdge, sizeRadiusShape));

        //ROTATE CORNER
        Point2D pivot = new Point2D(sizeRadiusShape, sizeRadiusShape);
        for (int i = 1; i < 180; i++) {
            double originX = points.get(points.size() - 1).getX();
            double originY = points.get(points.size() - 1).getY();
            double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(1)) + pivot.getX();
            double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(1)) + pivot.getY();
            points.add(new Point2D(X, Y));
        }
        points.add(new Point2D(sizeRadiusShape * 2 - widthEdge, sizeRadiusShape));
        points.add(new Point2D(sizeRadiusShape * 2, sizeRadiusShape));

        for (int i = 1; i < 180; i++) {
            double originX = points.get(points.size() - 1).getX();
            double originY = points.get(points.size() - 1).getY();
            double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
            double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
            points.add(new Point2D(X, Y));
        }

        //CREATE POLYGON

        for (Point2D p : points) {
            sideRadiusEdge.getPoints().add(new Double(p.getX()));
            sideRadiusEdge.getPoints().add(new Double(p.getY()));
        }
        getChildren().remove(sideRadiusEdge);
        getChildren().add(sideRadiusEdge);
        sideRadiusEdge.setOnMouseClicked(event -> edgeManagerShow(sideRadiusEdge));


        sideAEdge.setSketchEdgeOwner(this);
        sideRadiusEdge.setSketchEdgeOwner(this);
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

        } else if (sideRadiusEdge == edge) {

            if (!sideRadiusEdge.getName().equals(newEdge.getName())) {
                if (sideRadiusEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideRadiusEdge);
                } else if (sideRadiusEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideRadiusEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }
            getChildren().remove(sideRadiusEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideRadiusEdge.getPoints());
            sideRadiusEdge = newEdge;
            getChildren().add(sideRadiusEdge);
            sideRadiusEdge.setOnMouseClicked(event -> edgeManagerShow(sideRadiusEdge));

        }


        //initEdgesZones();
        refreshEdgeView();
        //refreshShapeSettings();
        //rotateEdgesIcons(rotateAngle);
    }

    @Override
    public ArrayList<SketchEdge> getEdges() {
        return new ArrayList<SketchEdge>(Arrays.asList(sideAEdge, sideRadiusEdge));
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
        getChildren().remove(triangleIconSideRadiusEdge);

        getChildren().remove(lineGrooveAEdge);
        getChildren().remove(arcGrooveRadiusEdge);

        double translateX = 0;
        double translateY = 0;


        //sideAEdge
        {
            if (sideAEdge instanceof Edge) {
                triangleIconSideAEdge = new Polygon(
                        0.0, 0.0,
                        -2.5, -5.0,
                        2.5, -5.0);
                translateX = sizeRadiusShape;
                translateY = sizeRadiusShape;
            } else {
                triangleIconSideAEdge = new Polygon(
                        -2.5, 0.0,
                        2.5, -5.0,
                        0.0, -2.5,
                        -2.5, -5.0,
                        2.5, 0.0,
                        0.0, -2.5);
                translateX = sizeRadiusShape;
                translateY = sizeRadiusShape - 2.5;
                triangleIconSideAEdge.setStroke(Color.BLACK);
            }
            triangleIconSideAEdge.setFill(Color.BLACK);
            for (int i = 0; i < triangleIconSideAEdge.getPoints().size(); i += 2) {
                double newX = triangleIconSideAEdge.getPoints().get(i).doubleValue() + translateX;
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
                    lineGrooveAEdge = new Line(0, sizeRadiusShape - 5, sizeRadiusShape * 2, sizeRadiusShape - 5);
                    lineGrooveAEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                    lineGrooveAEdge.getStrokeDashArray().addAll(5.0, 3.0);
                    getChildren().add(lineGrooveAEdge);
                    lineGrooveAEdge.setVisible((true));
                }

            } else {
                triangleIconSideAEdge.setVisible(false);
            }
        }


        double angle = 45.0;
        double originX = pointsList.get(2).getX();
        double originY = pointsList.get(2).getY();
        translateX = sizeRadiusShape;
        translateY = 0;
        //System.out.println("translateX = " + translateX);
        //System.out.println("translateY = " + translateY);
        if (sideRadiusEdge instanceof Edge) {
            triangleIconSideRadiusEdge = new Polygon(
                    0.0, 0.0,
                    -2.5, 5.0,
                    2.5, 5.0);
            translateX = sizeRadiusShape;
            translateY = 0;
        } else {
            triangleIconSideRadiusEdge = new Polygon(
                    0.0, 0.0,
                    5.0, 5.0,
                    2.5, 2.5,
                    5.0, 0.0,
                    0.0, 5.0,
                    2.5, 2.5);
            translateX = sizeRadiusShape - 2.5;
            translateY = 2.5;
            triangleIconSideRadiusEdge.setStroke(Color.BLACK);
        }
        //triangleIconSideRadiusEdge.setRotate(90-angle);
        triangleIconSideRadiusEdge.setFill(Color.BLACK);
        for (int i = 0; i < triangleIconSideRadiusEdge.getPoints().size(); i += 2) {
            double newX = triangleIconSideRadiusEdge.getPoints().get(i).doubleValue() + translateX;
            double newY = triangleIconSideRadiusEdge.getPoints().get(i + 1).doubleValue() + translateY;
            triangleIconSideRadiusEdge.getPoints().set(i, newX);
            triangleIconSideRadiusEdge.getPoints().set(i + 1, newY);
        }
        getChildren().add(triangleIconSideRadiusEdge);
        Tooltip.install(triangleIconSideRadiusEdge, sideRadiusEdge.getTooltip());
        SketchObject.rotatePolygon(triangleIconSideRadiusEdge, getRotationPivot(), rotateAngle);
        if (sideRadiusEdge.isDefined()) {
            triangleIconSideRadiusEdge.setVisible(true);

            if (sideRadiusEdge instanceof Edge && ((Edge) sideRadiusEdge).isStoneHemOrLeakGroove()) {
                arcGrooveRadiusEdge = new Arc(sizeRadiusShape, sizeRadiusShape, sizeRadiusShape - 5, sizeRadiusShape - 5, 0, 180);
                arcGrooveRadiusEdge.setType(ArcType.OPEN);
                arcGrooveRadiusEdge.setStroke(Color.BLACK);
                arcGrooveRadiusEdge.setStrokeWidth(1);
                arcGrooveRadiusEdge.setFill(Color.TRANSPARENT);
                arcGrooveRadiusEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                arcGrooveRadiusEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(arcGrooveRadiusEdge);
                arcGrooveRadiusEdge.setVisible((true));
            }

        } else {
            triangleIconSideRadiusEdge.setVisible(false);
        }

    }

    @Override
    public void updateMaterialList() {
        choiceBoxMaterial.getItems().clear();
        for (Material material : ProjectHandler.getMaterialsListInProject()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        if (!choiceBoxMaterial.getItems().contains(shapeMaterial.getReceiptName())) {
            shapeMaterial = ProjectHandler.getDefaultMaterial();
        }
        choiceBoxMaterial.getSelectionModel().select(shapeMaterial.getReceiptName());
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
        else if (edge == sideRadiusEdge) return (sizeRadiusReal * 2.0 * Math.PI) / 2.0;

        return 0;
    }

    @Override
    public double getEdgesLength() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Edge)) {
            length += sizeAReal;
        }

        if (sideRadiusEdge != null && (sideRadiusEdge instanceof Edge)) {
            length += (2.0 * Math.PI * sizeRadiusReal) / 2.0;
        }
        return length;

    }

    @Override
    public double getBordersType1Length() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Border) && sideAEdge.getName().indexOf(1) != -1)
            length += sizeAReal;
        if (sideRadiusEdge != null && (sideRadiusEdge instanceof Border) && sideRadiusEdge.getName().indexOf(1) != -1) {
            length += (2.0 * Math.PI * sizeRadiusReal) / 2.0;
        }
        return length;

    }

    @Override
    public double getBordersType2Length() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Border) && sideAEdge.getName().indexOf(2) != -1)
            length += sizeAReal;
        if (sideRadiusEdge != null && (sideRadiusEdge instanceof Border) && sideRadiusEdge.getName().indexOf(2) != -1) {
            length += (2.0 * Math.PI * sizeRadiusReal) / 2.0;
        }
        return length;

    }


    public void rotateShapeInSettings(double angle) {

        Point2D pivot = new Point2D(paneShapeView.getPrefWidth() / 2, paneShapeView.getPrefHeight() / 2);

        SketchObject.rotatePolygon(polygonSettingsShape, pivot, angle);

        double newAX = ((labelA.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelA.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newAY = ((labelA.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelA.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelA.setTranslateX(newAX - 15);
        labelA.setTranslateY(newAY - 15);

        double newRadiusX = ((labelRadius.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelRadius.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newRadiusY = ((labelRadius.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelRadius.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelRadius.setTranslateX(newRadiusX - 15);
        labelRadius.setTranslateY(newRadiusY - 15);

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
    }

    @Override
    public ArrayList<Line> getLineForJoints() {
        refreshLinesForJoints();
        return new ArrayList<Line>(Arrays.asList(lineAJoint));
    }

    @Override
    public void addJoint(Line lineForJointSide, Joint newJoint) {
        if (lineForJointSide.equals(lineAJoint)) {
            sideAJointsList.add(newJoint);
        }
    }

    @Override
    public ArrayList<Joint> getJoints() {
        ArrayList<Joint> list = new ArrayList<>();

        list.addAll(sideAJointsList);

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
            if (sideRadiusEdge.getType() != 1) {
                changeElementEdge(sideRadiusEdge, new SketchEdge());
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
            if (sideRadiusEdge.getType() != 2) {
                changeElementEdge(sideRadiusEdge, new SketchEdge());
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
                if (sideRadiusEdge instanceof Edge && ((Edge) sideRadiusEdge).getSubType() > 7)
                    changeElementEdge(sideRadiusEdge, new SketchEdge());

            } else if (this.edgeHeight > shapeDepth) {
                if (sideAEdge instanceof Edge && ((Edge) sideAEdge).getSubType() <= 7)
                    changeElementEdge(sideAEdge, new SketchEdge());
                if (sideRadiusEdge instanceof Edge && ((Edge) sideRadiusEdge).getSubType() <= 7)
                    changeElementEdge(sideRadiusEdge, new SketchEdge());
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
        if (sizeAShape > sizeRadiusShape) {
            newScale = 30.0 / sizeAShape;
        } else {
            newScale = 30.0 / (sizeRadiusShape);
        }


        // CREATE POINTS
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(new Point2D(0.0, sizeRadiusShape * newScale));
        points.add(new Point2D(sizeRadiusShape * 2 * newScale, sizeRadiusShape * newScale));

        //ROTATE CORNER
        Point2D pivot = new Point2D((sizeRadiusShape) * newScale, (sizeRadiusShape) * newScale);
        for (int i = 1; i < 180; i++) {
            double originX = points.get(points.size() - 1).getX();
            double originY = points.get(points.size() - 1).getY();
            double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
            double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
            points.add(new Point2D(X, Y));
        }

        //CREATE POLYGON
        Polygon polygon = new Polygon();
        for (Point2D p : points) {
            polygon.getPoints().add(new Double(p.getX()));
            polygon.getPoints().add(new Double(p.getY()));
        }

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

        // CREATE POINTS
        ArrayList<Point2D> pointsList = new ArrayList<>();
        pointsList.add(new Point2D(0.0, 50.0));
        pointsList.add(new Point2D(100.0, 50.0));

        //ROTATE CORNER
        Point2D pivot = new Point2D(50.0, 50.0);
        for (int i = 1; i < 180; i++) {
            double originX = pointsList.get(pointsList.size() - 1).getX();
            double originY = pointsList.get(pointsList.size() - 1).getY();
            double X = (originX - pivot.getX()) * Math.cos(Math.toRadians(-1)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(-1)) + pivot.getX();
            double Y = (originX - pivot.getX()) * Math.sin(Math.toRadians(-1)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(-1)) + pivot.getY();
            pointsList.add(new Point2D(X, Y));
        }

        //CREATE POLYGON
        Polygon polygon = new Polygon();
        for (Point2D p : pointsList) {
            polygon.getPoints().add(new Double(p.getX()));
            polygon.getPoints().add(new Double(p.getY()));
        }


        pane.getChildren().add(polygon);
        if (elementType == ElementTypes.TABLETOP) polygon.setFill(TABLE_TOP_COLOR);
        else if (elementType == ElementTypes.WALL_PANEL) polygon.setFill(WALL_PANEL_COLOR);
        else if (elementType == ElementTypes.WINDOWSILL) polygon.setFill(WINDOWSILL_COLOR);
        else if (elementType == ElementTypes.WALL_PANEL) polygon.setFill(FOOT_COLOR);
        return pane;
    }

    public static Tooltip getStaticTooltipForListCell() {
        return new Tooltip("Фигура");
    }

    @Override
    public void unSelectShape() {
        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            //System.err.println("CANT FILL RECTANGLE SHAPE");
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
        object.put("sizeRadiusReal", sizeRadiusReal);
        object.put("material", shapeMaterial.getName());
        object.put("shapeDepth", shapeDepth);
        object.put("edgesHeightsDefault", edgesHeightsDefault);
        object.put("saveMaterialImageOnEdges", saveMaterialImageOnEdges);
        object.put("edgeHeight", edgeHeight);
        object.put("borderHeight", borderHeight);
        object.put("rotateAngle", rotateTransform.getAngle());
        object.put("opacity", this.getOpacity());

        JSONObject sideAEdgeObject = new JSONObject();
        JSONObject sideRadiusEdgeObject = new JSONObject();

        sideAEdgeObject.put("edgeType", (sideAEdge instanceof Edge) ? "edge" : "border");
        sideAEdgeObject.put("name", sideAEdge.getName());
        if (sideAEdge instanceof Border) {
            sideAEdgeObject.put("topCutType", ((Border) sideAEdge).getBorderCutType());
            sideAEdgeObject.put("sideCutType", ((Border) sideAEdge).getBorderSideCutType());
            sideAEdgeObject.put("anglesCutType", ((Border) sideAEdge).getBorderAnglesCutType());
        } else if (sideAEdge instanceof Edge) {
            sideAEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideAEdge).isStoneHemOrLeakGroove());
        }

        sideRadiusEdgeObject.put("edgeType", (sideRadiusEdge instanceof Edge) ? "edge" : "border");
        sideRadiusEdgeObject.put("name", sideRadiusEdge.getName());
        if (sideRadiusEdge instanceof Border) {
            sideRadiusEdgeObject.put("topCutType", ((Border) sideRadiusEdge).getBorderCutType());
            sideRadiusEdgeObject.put("sideCutType", ((Border) sideRadiusEdge).getBorderSideCutType());
            sideRadiusEdgeObject.put("anglesCutType", ((Border) sideRadiusEdge).getBorderAnglesCutType());
        } else if (sideRadiusEdge instanceof Edge) {
            sideRadiusEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideRadiusEdge).isStoneHemOrLeakGroove());
        }

        object.put("sideAEdge", sideAEdgeObject);
        object.put("sideRadiusEdge", sideRadiusEdgeObject);

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
        sizeRadiusReal = ((Double) jsonObject.get("sizeRadiusReal")).doubleValue();

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
        JSONObject sideRadiusEdgeObject = ((JSONObject) jsonObject.get("sideRadiusEdge"));

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


        String sideRadiusEdgeType = (String) sideRadiusEdgeObject.get("edgeType");
        String sideRadiusEdgeName = (String) sideRadiusEdgeObject.get("name");
        if (sideRadiusEdgeType.equals("edge")) {
            changeElementEdge(sideRadiusEdge, new Edge(sideRadiusEdgeName, edgeType));
            ((Edge) sideRadiusEdge).setStoneHemOrLeakGroove(((Boolean) sideRadiusEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideRadiusEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideRadiusEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideRadiusEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideRadiusEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideRadiusEdge, border);
        }


        JSONArray sketchDesignerXY = (JSONArray) (jsonObject.get("sketchDesignerXY"));
        this.setTranslateX(((Double) sketchDesignerXY.get(0)).doubleValue());
        this.setTranslateY(((Double) sketchDesignerXY.get(1)).doubleValue());


        if (shapeMaterial.getName().equals(ProjectHandler.getDefaultMaterial().getName())) {
            materialDefault = true;
        } else {
            materialDefault = false;
        }
        sizeAShape = sizeAReal * commonShapeScale;

        sizeRadiusShape = sizeRadiusReal * commonShapeScale;
        //sizeDShape = sizeDReal*commonShapeScale;

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
