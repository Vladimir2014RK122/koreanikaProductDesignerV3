package ru.koreanika.sketchDesigner.Shapes;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.ConnectPoints.CornerConnectPoint;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.cutDesigner.Shapes.CutShapeEdge;
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
import ru.koreanika.sketchDesigner.Dimensions.LinearDimension;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.Edge;
import ru.koreanika.sketchDesigner.Edge.EdgeManager;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.Joint;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.ProjectHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class SketchShapeRhombus extends SketchShape {

    Pane sketchPane;

    Point2D[] points;
    double sizeAShape = 100;
    double sizeBShape = 60;
    double sizeCShape = 40;
    double sizeDShape = 40;
    double sizeEShape = 0;
    double sizeFShape = 0;
    double sizeGShape = 0;
    double sizeKShape = 0;
    double connectAreaWidth = 0;

    //private Image imageForFill = null;

    //connect points:
    //ArrayList<CornerConnectPoint> connectionPoints = new ArrayList<>();
    //ArrayList<ConnectPoint> cutShapeConnectPoints = new ArrayList<>(Arrays.asList(new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint()));

    CutShapeEdge cutShapeEdgeC;
    CutShapeEdge cutShapeEdgeD;
    CutShapeEdge cutShapeEdgeE;
    CutShapeEdge cutShapeEdgeF;
    CutShapeEdge cutShapeEdgeG;
    CutShapeEdge cutShapeEdgeK;

    //Element edges
    double widthEdge = 5;

    SketchEdge sideCEdge = null;
    SketchEdge sideDEdge = null;
    SketchEdge sideEEdge = null;
    SketchEdge sideFEdge = null;
    SketchEdge sideGEdge = null;
    SketchEdge sideKEdge = null;


    Polygon triangleIconSideCEdge;
    Polygon triangleIconSideDEdge;
    Polygon triangleIconSideEEdge;
    Polygon triangleIconSideFEdge;
    Polygon triangleIconSideGEdge;
    Polygon triangleIconSideKEdge;


    Line lineGrooveCEdge;
    Line lineGrooveDEdge;
    Line lineGrooveEEdge;
    Line lineGrooveFEdge;
    Line lineGrooveGEdge;
    Line lineGrooveKEdge;

    //Joints:
    Line lineCJoint = null;
    Line lineDJoint = null;
    Line lineEJoint = null;
    Line lineFJoint = null;
    Line lineGJoint = null;
    Line lineKJoint = null;

    ArrayList<Joint> sideCJointsList = new ArrayList<>();
    ArrayList<Joint> sideDJointsList = new ArrayList<>();
    ArrayList<Joint> sideEJointsList = new ArrayList<>();
    ArrayList<Joint> sideFJointsList = new ArrayList<>();
    ArrayList<Joint> sideGJointsList = new ArrayList<>();
    ArrayList<Joint> sideKJointsList = new ArrayList<>();

    //Shape settings:
    boolean materialDefault = true; //default = true;
    boolean edgesHeightsDefault = true; //default = true;


    int edgeHeight = 0;
    int borderHeight = 0;

    double sizeAReal = sizeAShape / commonShapeScale;
    double sizeBReal = sizeBShape / commonShapeScale;
    double sizeCReal = sizeCShape / commonShapeScale;
    double sizeDReal = sizeDShape / commonShapeScale;
    double sizeEReal = sizeEShape / commonShapeScale;
    double sizeFReal = sizeFShape / commonShapeScale;
    double sizeGReal = sizeGShape / commonShapeScale;
    double sizeKReal = sizeKShape / commonShapeScale;

    CheckBox checkBoxMaterialDefault, checkBoxDefaultHeights, checkBoxSaveImage;
    ChoiceBox<String> choiceBoxMaterial;
    ChoiceBox<String> choiceBoxMaterialDepth;
    TextField textFieldASize, textFieldBSize, textFieldCSize, textFieldDSize, textFieldEFGKSize;
    TextField textFieldX, textFieldY;
    TextField textFieldEdgeHeight, textFieldBorderHeight;

    Group groupEdges;
    Button btnRotateRight, btnRotateLeft;

    Pane paneShapeView;
    Polygon polygonSettingsShape;
    Label labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelK;

    boolean correctEdgeHeight = true, correctBorderHeight = true, correctX = true, correctY = true;
    boolean correctASize = true, correctBSize = true, correctCSize = true, correctDSize = true;
    boolean correctESize = true, correctFSize = true, correctGSize = true, correctKSize = true;


    public SketchShapeRhombus(double layoutX, double layoutY, ElementTypes elementType, Pane sketchPane) {
        setChildShape(this);

        shapeType = ShapeType.RHOMBUS;
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
                new Point2D(0.0, sizeBShape / 2),
                new Point2D((sizeAShape - sizeCShape) / 2, 0.0),
                new Point2D((sizeAShape - sizeCShape) / 2 + sizeCShape, 0.0),
                new Point2D(sizeAShape, sizeBShape / 2),
                new Point2D((sizeAShape - sizeDShape) / 2 + sizeDShape, sizeBShape),
                new Point2D((sizeAShape - sizeDShape) / 2, sizeBShape),
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY(),
                points[4].getX(), points[4].getY(),
                points[5].getX(), points[5].getY()
        );


        // create ImagePattern
        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
            System.err.println("CANT FILL RHOMBUS SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            System.err.println("CANT FILL RHOMBUS SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0,
                    polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
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

    public SketchShapeRhombus(ElementTypes elementType, Material material, int depth, double sizeA, double sizeB, double sizeC, double sizeD) {


        this.sizeAReal = sizeA;
        this.sizeBReal = sizeB;
        this.sizeCReal = sizeC;
        this.sizeDReal = sizeD;
        this.sizeEReal = Math.sqrt(Math.pow(sizeB / 2, 2) * 2);
        this.sizeFReal = sizeEReal;
        this.sizeGReal = sizeEReal;
        this.sizeKReal = sizeEReal;


        sizeAShape = sizeAReal * commonShapeScale;
        sizeBShape = sizeBReal * commonShapeScale;
        sizeCShape = sizeCReal * commonShapeScale;
        sizeDShape = sizeDReal * commonShapeScale;
        sizeEShape = sizeEReal * commonShapeScale;
        sizeFShape = sizeFReal * commonShapeScale;
        sizeGShape = sizeGReal * commonShapeScale;
        sizeKShape = sizeKReal * commonShapeScale;

        initShapeMaterial(material, depth);

        setChildShape(this);

        shapeType = ShapeType.TRAPEZE;
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
                new Point2D(0.0, sizeBShape / 2),
                new Point2D((sizeAShape - sizeCShape) / 2, 0.0),
                new Point2D((sizeAShape - sizeCShape) / 2 + sizeCShape, 0.0),
                new Point2D(sizeAShape, sizeBShape / 2),
                new Point2D((sizeAShape - sizeDShape) / 2 + sizeDShape, sizeBShape),
                new Point2D((sizeAShape - sizeDShape) / 2, sizeBShape),
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY(),
                points[4].getX(), points[4].getY(),
                points[5].getX(), points[5].getY()
        );


        // create ImagePattern
        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
//            System.err.println("CANT FILL RECTANGLE SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            System.err.println("CANT FILL RHOMBUS SHAPE");
        } else {
            ImagePattern image_pattern = new ImagePattern(imageForFill, 0, 0,
                    polygon.getBoundsInParent().getWidth(), polygon.getBoundsInParent().getHeight(), false);
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
            for (int i = 0; i < 6; i++) {
                connectionPoints.add(new CornerConnectPoint(this));
            }
        }


        connectionPoints.get(0).setTranslateX(points[0].getX() - (widthConnectPoint / 2));
        connectionPoints.get(0).setTranslateY(points[0].getY() - (widthConnectPoint / 2));
        connectionPoints.get(0).hide();

        connectionPoints.get(1).setTranslateX(points[1].getX() - (widthConnectPoint / 2));
        connectionPoints.get(1).setTranslateY(points[1].getY() - (widthConnectPoint / 2));
        connectionPoints.get(1).hide();

        connectionPoints.get(2).setTranslateX(points[2].getX() - (widthConnectPoint / 2));
        connectionPoints.get(2).setTranslateY(points[2].getY() - (widthConnectPoint / 2));
        connectionPoints.get(2).hide();

        connectionPoints.get(3).setTranslateX(points[3].getX() - (widthConnectPoint / 2));
        connectionPoints.get(3).setTranslateY(points[3].getY() - (widthConnectPoint / 2));
        connectionPoints.get(3).hide();

        connectionPoints.get(4).setTranslateX(points[4].getX() - (widthConnectPoint / 2));
        connectionPoints.get(4).setTranslateY(points[4].getY() - (widthConnectPoint / 2));
        connectionPoints.get(4).hide();

        connectionPoints.get(5).setTranslateX(points[5].getX() - (widthConnectPoint / 2));
        connectionPoints.get(5).setTranslateY(points[5].getY() - (widthConnectPoint / 2));
        connectionPoints.get(5).hide();

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

        sideCEdge.setDisable(edgesDisable);
        sideDEdge.setDisable(edgesDisable);
        sideEEdge.setDisable(edgesDisable);
        sideFEdge.setDisable(edgesDisable);
        sideGEdge.setDisable(edgesDisable);
        sideKEdge.setDisable(edgesDisable);
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

        labelShapeNumber.setTranslateX(sizeAShape / 2 - 7.5);
        labelShapeNumber.setTranslateY(sizeBShape / 2 - 7.5);
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
            //System.out.println("CREATE CUT SHAPE RHOMBUS " + thisShapeNumber);
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
            Point2D p1 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape / 2);

            double angle = Math.toDegrees(Math.atan(((sizeAShape - sizeCShape)/2) / (sizeBShape/2)));
            Point2D pivot = new Point2D(0.0, sizeBShape / 2);
            Point2D p2 = SketchShape.rotatePoint(p1,pivot, angle);

            Point2D p4 = new Point2D((sizeAShape - sizeCShape) / 2, -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeCShape)/2) / (sizeBShape/2)));
            pivot = new Point2D((sizeAShape - sizeCShape)/2, 0.0);
            Point2D p3 = SketchShape.rotatePoint(p4,pivot, -angle);

            Point2D p5 = new Point2D((sizeAShape - sizeCShape) / 2 + sizeCShape,  -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeCShape)/2) / (sizeBShape/2)));
            pivot = new Point2D((sizeAShape - sizeCShape) / 2 + sizeCShape, 0.0);
            Point2D p6 = SketchShape.rotatePoint(p5,pivot, angle);

            Point2D p8 = new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape / 2);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeCShape)/2) / (sizeBShape/2)));
            pivot = new Point2D(sizeAShape, sizeBShape / 2);
            Point2D p7 = SketchShape.rotatePoint(p8,pivot, -angle);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeDShape)/2) / (sizeBShape/2)));
            pivot = new Point2D(sizeAShape, sizeBShape / 2);
            Point2D p9 = SketchShape.rotatePoint(p8,pivot, angle);

            Point2D p11 = new Point2D((sizeAShape - sizeDShape) / 2 + sizeDShape, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeDShape)/2) / (sizeBShape/2)));
            pivot = new Point2D((sizeAShape - sizeDShape) / 2 + sizeDShape, sizeBShape);
            Point2D p10 = SketchShape.rotatePoint(p11,pivot, -angle);

            Point2D p12 = new Point2D((sizeAShape - sizeDShape) / 2, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeDShape)/2) / (sizeBShape/2)));
            pivot = new Point2D((sizeAShape - sizeDShape) / 2, sizeBShape);
            Point2D p13 = SketchShape.rotatePoint(p12,pivot, angle);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeDShape)/2) / (sizeBShape/2)));
            pivot = new Point2D(0.0, sizeBShape / 2);
            Point2D p14 = SketchShape.rotatePoint(p1,pivot, -angle);

            cutZonePolygonPoints = new ArrayList<>();
            cutZonePolygonPoints.addAll(Arrays.asList(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14));

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
            polygonPoints.add(new Point2D(points[0].getX(), points[0].getY()));
            polygonPoints.add(new Point2D(points[1].getX(), points[1].getY()));
            polygonPoints.add(new Point2D(points[2].getX(), points[2].getY()));
            polygonPoints.add(new Point2D(points[3].getX(), points[3].getY()));
            polygonPoints.add(new Point2D(points[4].getX(), points[4].getY()));
            polygonPoints.add(new Point2D(points[5].getX(), points[5].getY()));

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
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShape));

        cutShapeConnectPoints.get(0).changeSetPoint(polygonPoints.get(0));
        cutShapeConnectPoints.get(1).changeSetPoint(polygonPoints.get(1));
        cutShapeConnectPoints.get(2).changeSetPoint(polygonPoints.get(2));
        cutShapeConnectPoints.get(3).changeSetPoint(polygonPoints.get(3));
        cutShapeConnectPoints.get(4).changeSetPoint(polygonPoints.get(4));
        cutShapeConnectPoints.get(5).changeSetPoint(polygonPoints.get(5));

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(cutZonePolygonPoints.get(0));
            cutShapeConnectPoints.get(1).changeSetPointShift(cutZonePolygonPoints.get(3));
            cutShapeConnectPoints.get(2).changeSetPointShift(cutZonePolygonPoints.get(4));
            cutShapeConnectPoints.get(3).changeSetPointShift(cutZonePolygonPoints.get(7));
            cutShapeConnectPoints.get(4).changeSetPointShift(cutZonePolygonPoints.get(10));
            cutShapeConnectPoints.get(5).changeSetPointShift(cutZonePolygonPoints.get(11));
        }

        cutShape.setConnectPoints(cutShapeConnectPoints);
        for (ConnectPoint connectPoint : cutShapeConnectPoints) {
            cutShape.getChildren().add(connectPoint);
        }
        cutShape.hideConnectionPoints();

        //add label with shape number
        cutShape.refreshLabelNumber();

        //CREATE DIMENSIONS

        double sizeH = sizeAShape;
        double sizeY = sizeBShape;

        Label dimensionVLabel = new Label();
        dimensionVLabel.setId("dimensionVLabel");
        dimensionVLabel.setPickOnBounds(false);

        dimensionVLabel.setAlignment(Pos.CENTER);
        dimensionVLabel.setPrefWidth(sizeY);
        //dimensionVLabel.setPrefWidth(60);
        dimensionVLabel.setPrefHeight(8);
        dimensionVLabel.setTranslateX(0.0);
        dimensionVLabel.setTranslateY(sizeBShape / 2 - 4);
        dimensionVLabel.setText("");
        dimensionVLabel.setFont(Font.font(8));
        //dimensionVLabel.setRotate(-90);
        Rotate rotateV = new Rotate(-90);
        //dimensionVLabel.getTransforms().add(rotateV);

        cutShape.setDimensionV(dimensionVLabel);

        Label dimensionHLabel = new Label();
        dimensionHLabel.setId("dimensionHLabel");
        dimensionHLabel.setPickOnBounds(false);

        dimensionHLabel.setAlignment(Pos.CENTER);
        dimensionHLabel.setPrefWidth(sizeH);
        dimensionHLabel.setTranslateX(0.0);
        dimensionHLabel.setTranslateY(sizeBShape / 2 - 15);
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

        createCCutEdge();
        createDCutEdge();
        createECutEdge();
        createFCutEdge();
        createGCutEdge();
        createKCutEdge();

        cutShapesEdgesList.clear();
        cutShapesEdgesList.addAll(Arrays.asList(cutShapeEdgeC, cutShapeEdgeD, cutShapeEdgeE,
                cutShapeEdgeF, cutShapeEdgeG, cutShapeEdgeK));
        cutShape.setCutShapeEdgesList(cutShapesEdgesList);

    }


    private void createCCutEdge() {

        cutShapeEdgeC = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideCEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }


        pointsForEdge.add(new Point2D(points[1].getX(), points[1].getY()));
        pointsForEdge.add(new Point2D(points[2].getX(), points[2].getY()));
        pointsForEdge.add(new Point2D(points[2].getX(), points[2].getY() - h));
        pointsForEdge.add(new Point2D(points[1].getX(), points[1].getY() - h));


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
        ArrayList<ConnectPoint> leftEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeC);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeC);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeC);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeC);

        leftEdgeConnectPoints.add(point1);
        leftEdgeConnectPoints.add(point2);
        leftEdgeConnectPoints.add(point3);
        leftEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : leftEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeC);
        }
        for (ConnectPoint connectPoint : leftEdgeConnectPoints) {
            cutShapeEdgeC.getChildren().add(connectPoint);
        }

        cutShapeEdgeC.setConnectPoints(leftEdgeConnectPoints);
        cutShapeEdgeC.hideConnectionPoints();

        if (sideCEdge.isDefined()) {
            cutShapeEdgeC.setStartCoordinate(new Point2D(0.0, 0.0));
        } else {
            cutShapeEdgeC.setStartCoordinate(null);
        }
    }

    private void createDCutEdge() {

        cutShapeEdgeD = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideDEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }

        pointsForEdge.add(new Point2D(points[5].getX(), points[5].getY()));
        pointsForEdge.add(new Point2D(points[4].getX(), points[4].getY()));
        pointsForEdge.add(new Point2D(points[4].getX(), points[4].getY() + h));
        pointsForEdge.add(new Point2D(points[5].getX(), points[5].getY() + h));

        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeEdgeD.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeD.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeD.getChildren().remove(cutShapeEdgeD.getPolygon());
        cutShapeEdgeD.setPolygon(edgePolygon);
        cutShapeEdgeD.getChildren().add(edgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> rightEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeD);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeD);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeD);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeD);

        rightEdgeConnectPoints.add(point1);
        rightEdgeConnectPoints.add(point2);
        rightEdgeConnectPoints.add(point3);
        rightEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeD);
        }
        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            cutShapeEdgeD.getChildren().add(connectPoint);
        }

        cutShapeEdgeD.setConnectPoints(rightEdgeConnectPoints);
        cutShapeEdgeD.hideConnectionPoints();

        if (sideDEdge.isDefined()) {
            cutShapeEdgeD.setStartCoordinate(new Point2D(0.0, 0.0));
        } else {
            cutShapeEdgeD.setStartCoordinate(null);
        }
    }

    private void createECutEdge() {

        cutShapeEdgeE = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideEEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }

        // CREATE POINTS
        pointsForEdge.add(new Point2D(points[0].getX(), points[0].getY()));
        pointsForEdge.add(new Point2D(points[1].getX(), points[1].getY()));
        pointsForEdge.add(new Point2D(points[1].getX(), points[1].getY() - h));
        pointsForEdge.add(new Point2D(points[0].getX(), points[0].getY() - h));

        //ROTATE TWO POINTS
        double angle = -45;
        Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
        Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

        pointsForEdge.set(2, p2);
        pointsForEdge.set(3, p3);


        //ADD POINTS TO POLYGON:
        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeEdgeE.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeE.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeE.getChildren().remove(cutShapeEdgeE.getPolygon());
        cutShapeEdgeE.setPolygon(edgePolygon);
        cutShapeEdgeE.getChildren().add(edgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> rightEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeE);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeE);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeE);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeE);

        rightEdgeConnectPoints.add(point1);
        rightEdgeConnectPoints.add(point2);
        rightEdgeConnectPoints.add(point3);
        rightEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeE);
        }
        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            cutShapeEdgeE.getChildren().add(connectPoint);
        }

        cutShapeEdgeE.setConnectPoints(rightEdgeConnectPoints);
        cutShapeEdgeE.hideConnectionPoints();

        if (sideEEdge.isDefined()) {
            cutShapeEdgeE.setStartCoordinate(new Point2D(0.0, 0.0));
        } else {
            cutShapeEdgeE.setStartCoordinate(null);
        }
    }

    private void createFCutEdge() {

        cutShapeEdgeF = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideFEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }

        // CREATE POINTS
        pointsForEdge.add(new Point2D(points[2].getX(), points[2].getY()));
        pointsForEdge.add(new Point2D(points[3].getX(), points[3].getY()));
        pointsForEdge.add(new Point2D(points[3].getX(), points[3].getY() - h));
        pointsForEdge.add(new Point2D(points[2].getX(), points[2].getY() - h));

        //ROTATE TWO POINTS
        double angle = 45;
        Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
        Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

        pointsForEdge.set(2, p2);
        pointsForEdge.set(3, p3);


        //ADD POINTS TO POLYGON:
        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeEdgeF.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeF.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeF.getChildren().remove(cutShapeEdgeF.getPolygon());
        cutShapeEdgeF.setPolygon(edgePolygon);
        cutShapeEdgeF.getChildren().add(edgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> rightEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeF);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeF);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeF);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeF);

        rightEdgeConnectPoints.add(point1);
        rightEdgeConnectPoints.add(point2);
        rightEdgeConnectPoints.add(point3);
        rightEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeF);
        }
        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            cutShapeEdgeF.getChildren().add(connectPoint);
        }

        cutShapeEdgeF.setConnectPoints(rightEdgeConnectPoints);
        cutShapeEdgeF.hideConnectionPoints();

        if (sideFEdge.isDefined()) {
            cutShapeEdgeF.setStartCoordinate(new Point2D(0.0, 0.0));
        } else {
            cutShapeEdgeF.setStartCoordinate(null);
        }
    }

    private void createGCutEdge() {

        cutShapeEdgeG = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideGEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }

        // CREATE POINTS
        pointsForEdge.add(new Point2D(points[0].getX(), points[0].getY()));
        pointsForEdge.add(new Point2D(points[5].getX(), points[5].getY()));
        pointsForEdge.add(new Point2D(points[5].getX(), points[5].getY() + h));
        pointsForEdge.add(new Point2D(points[0].getX(), points[0].getY() + h));

        //ROTATE TWO POINTS
        double angle = 45;
        Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
        Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

        pointsForEdge.set(2, p2);
        pointsForEdge.set(3, p3);


        //ADD POINTS TO POLYGON:
        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeEdgeG.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeG.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeG.getChildren().remove(cutShapeEdgeG.getPolygon());
        cutShapeEdgeG.setPolygon(edgePolygon);
        cutShapeEdgeG.getChildren().add(edgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> rightEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeG);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeG);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeG);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeG);

        rightEdgeConnectPoints.add(point1);
        rightEdgeConnectPoints.add(point2);
        rightEdgeConnectPoints.add(point3);
        rightEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeG);
        }
        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            cutShapeEdgeG.getChildren().add(connectPoint);
        }

        cutShapeEdgeG.setConnectPoints(rightEdgeConnectPoints);
        cutShapeEdgeG.hideConnectionPoints();

        if (sideGEdge.isDefined()) {
            cutShapeEdgeG.setStartCoordinate(new Point2D(0.0, 0.0));
        } else {
            cutShapeEdgeG.setStartCoordinate(null);
        }
    }

    private void createKCutEdge() {

        cutShapeEdgeK = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideKEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }

        // CREATE POINTS
        pointsForEdge.add(new Point2D(points[4].getX(), points[4].getY()));
        pointsForEdge.add(new Point2D(points[3].getX(), points[3].getY()));
        pointsForEdge.add(new Point2D(points[3].getX(), points[3].getY() + h));
        pointsForEdge.add(new Point2D(points[4].getX(), points[4].getY() + h));

        //ROTATE TWO POINTS
        double angle = -45;
        Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
        Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

        pointsForEdge.set(2, p2);
        pointsForEdge.set(3, p3);


        //ADD POINTS TO POLYGON:
        for (Point2D p : pointsForEdge) {
            edgePolygon.getPoints().add(p.getX());
            edgePolygon.getPoints().add(p.getY());
        }

        edgePolygon.setFill(shapeColor);
        edgePolygon.setStroke(Color.BLACK);
        edgePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeEdgeK.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
        cutShapeEdgeK.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

        cutShapeEdgeK.getChildren().remove(cutShapeEdgeK.getPolygon());
        cutShapeEdgeK.setPolygon(edgePolygon);
        cutShapeEdgeK.getChildren().add(edgePolygon);

        //create connect points:
        ArrayList<ConnectPoint> rightEdgeConnectPoints = new ArrayList<>(4);
        CornerConnectPoint point1 = new CornerConnectPoint(cutShapeEdgeK);
        CornerConnectPoint point2 = new CornerConnectPoint(cutShapeEdgeK);
        CornerConnectPoint point3 = new CornerConnectPoint(cutShapeEdgeK);
        CornerConnectPoint point4 = new CornerConnectPoint(cutShapeEdgeK);

        rightEdgeConnectPoints.add(point1);
        rightEdgeConnectPoints.add(point2);
        rightEdgeConnectPoints.add(point3);
        rightEdgeConnectPoints.add(point4);

        point1.changeSetPoint(pointsForEdge.get(0));
        point2.changeSetPoint(pointsForEdge.get(1));
        point3.changeSetPoint(pointsForEdge.get(2));
        point4.changeSetPoint(pointsForEdge.get(3));

        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            connectPoint.setPointOwner(cutShapeEdgeK);
        }
        for (ConnectPoint connectPoint : rightEdgeConnectPoints) {
            cutShapeEdgeK.getChildren().add(connectPoint);
        }

        cutShapeEdgeK.setConnectPoints(rightEdgeConnectPoints);
        cutShapeEdgeK.hideConnectionPoints();

        if (sideKEdge.isDefined()) {
            cutShapeEdgeK.setStartCoordinate(new Point2D(0.0, 0.0));
        } else {
            cutShapeEdgeK.setStartCoordinate(null);
        }
    }

    private void updateCutShapeView() {

        cutShape.setSizesInfo("ШхВ "+ (int)(sizeAShape/ProjectHandler.getCommonShapeScale()) +
                "x" + (int)(sizeBShape/ProjectHandler.getCommonShapeScale()));

        //create Cut zone polygon
        ArrayList<Point2D> cutZonePolygonPoints = null;
        Polygon cutZonePolygon = null;
        {
            Point2D p1 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape / 2);

            double angle = Math.toDegrees(Math.atan(((sizeAShape - sizeCShape)/2) / (sizeBShape/2)));
            Point2D pivot = new Point2D(0.0, sizeBShape / 2);
            Point2D p2 = SketchShape.rotatePoint(p1,pivot, angle);

            Point2D p4 = new Point2D((sizeAShape - sizeCShape) / 2, -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeCShape)/2) / (sizeBShape/2)));
            pivot = new Point2D((sizeAShape - sizeCShape)/2, 0.0);
            Point2D p3 = SketchShape.rotatePoint(p4,pivot, -angle);

            Point2D p5 = new Point2D((sizeAShape - sizeCShape) / 2 + sizeCShape,  -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeCShape)/2) / (sizeBShape/2)));
            pivot = new Point2D((sizeAShape - sizeCShape) / 2 + sizeCShape, 0.0);
            Point2D p6 = SketchShape.rotatePoint(p5,pivot, angle);

            Point2D p8 = new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape / 2);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeCShape)/2) / (sizeBShape/2)));
            pivot = new Point2D(sizeAShape, sizeBShape / 2);
            Point2D p7 = SketchShape.rotatePoint(p8,pivot, -angle);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeDShape)/2) / (sizeBShape/2)));
            pivot = new Point2D(sizeAShape, sizeBShape / 2);
            Point2D p9 = SketchShape.rotatePoint(p8,pivot, angle);

            Point2D p11 = new Point2D((sizeAShape - sizeDShape) / 2 + sizeDShape, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeDShape)/2) / (sizeBShape/2)));
            pivot = new Point2D((sizeAShape - sizeDShape) / 2 + sizeDShape, sizeBShape);
            Point2D p10 = SketchShape.rotatePoint(p11,pivot, -angle);

            Point2D p12 = new Point2D((sizeAShape - sizeDShape) / 2, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeDShape)/2) / (sizeBShape/2)));
            pivot = new Point2D((sizeAShape - sizeDShape) / 2, sizeBShape);
            Point2D p13 = SketchShape.rotatePoint(p12,pivot, angle);

            angle = Math.toDegrees(Math.atan(((sizeAShape - sizeDShape)/2) / (sizeBShape/2)));
            pivot = new Point2D(0.0, sizeBShape / 2);
            Point2D p14 = SketchShape.rotatePoint(p1,pivot, -angle);

            cutZonePolygonPoints = new ArrayList<>();
            cutZonePolygonPoints.addAll(Arrays.asList(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14));

            cutZonePolygon = cutShape.getCutZonePolygon();
            cutZonePolygon.getPoints().clear();
            for (Point2D p : cutZonePolygonPoints) {
                cutZonePolygon.getPoints().add(new Double(p.getX()));
                cutZonePolygon.getPoints().add(new Double(p.getY()));
            }

        }

        //create main polygon
        ArrayList<Point2D> polygonPoints = null;
        Polygon cutShapePolygon = null;
        {
            // CREATE POINTS
            polygonPoints = new ArrayList<>();
            polygonPoints.add(new Point2D(points[0].getX(), points[0].getY()));
            polygonPoints.add(new Point2D(points[1].getX(), points[1].getY()));
            polygonPoints.add(new Point2D(points[2].getX(), points[2].getY()));
            polygonPoints.add(new Point2D(points[3].getX(), points[3].getY()));
            polygonPoints.add(new Point2D(points[4].getX(), points[4].getY()));
            polygonPoints.add(new Point2D(points[5].getX(), points[5].getY()));

            cutShapePolygon = cutShape.getPolygon();
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
        cutShapeConnectPoints.get(3).changeSetPoint(polygonPoints.get(3));
        cutShapeConnectPoints.get(4).changeSetPoint(polygonPoints.get(4));
        cutShapeConnectPoints.get(5).changeSetPoint(polygonPoints.get(5));

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(cutZonePolygonPoints.get(0));
            cutShapeConnectPoints.get(1).changeSetPointShift(cutZonePolygonPoints.get(3));
            cutShapeConnectPoints.get(2).changeSetPointShift(cutZonePolygonPoints.get(4));
            cutShapeConnectPoints.get(3).changeSetPointShift(cutZonePolygonPoints.get(7));
            cutShapeConnectPoints.get(4).changeSetPointShift(cutZonePolygonPoints.get(10));
            cutShapeConnectPoints.get(5).changeSetPointShift(cutZonePolygonPoints.get(11));
        }

        //Update dimensions labels:
        double sizeH = sizeAShape;
        double sizeY = sizeBShape;

        //cutShape.getDimensionVLabel().setRotate(0);
        cutShape.getDimensionVLabel().setPrefWidth(sizeY);
        cutShape.getDimensionVLabel().setTranslateX(polygonPoints.get(1).getX());
        cutShape.getDimensionVLabel().setTranslateY(sizeY);
        cutShape.getDimensionVLabel().setText("");
        cutShape.getDimensionVLabel().toFront();

        double shiftX = 2;
        double shiftY = sizeY - 5;

        //cutShape.getDimensionHLabel().setRotate(0);
        cutShape.getDimensionHLabel().setPrefWidth(sizeH);
        cutShape.getDimensionHLabel().setTranslateX(0.0);
        cutShape.getDimensionHLabel().setTranslateY(sizeBShape / 2 - 15);
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

        if (edgeHeight <= MIN_EDGE_HEIGHT_FOR_CUTSHAPE && (shapeMaterial.getName().indexOf("Акриловый камень") != -1 ||
                shapeMaterial.getName().indexOf("Полиэфирный камень") != -1)) {


            if (sideCEdge instanceof Border) {
                updateCCutEdge();
            } else {
                cutShapeEdgeC.setStartCoordinate(null);
            }

            if (sideDEdge instanceof Border) {
                updateDCutEdge();
            } else {
                cutShapeEdgeD.setStartCoordinate(null);
            }

            if (sideEEdge instanceof Border) {
                updateECutEdge();
            } else {
                cutShapeEdgeE.setStartCoordinate(null);
            }

            if (sideFEdge instanceof Border) {
                updateECutEdge();
            } else {
                cutShapeEdgeE.setStartCoordinate(null);
            }

            if (sideGEdge instanceof Border) {
                updateECutEdge();
            } else {
                cutShapeEdgeE.setStartCoordinate(null);
            }

            if (sideKEdge instanceof Border) {
                updateECutEdge();
            } else {
                cutShapeEdgeE.setStartCoordinate(null);
            }

        } else {

            updateCCutEdge();
            updateDCutEdge();
            updateECutEdge();
            updateFCutEdge();
            updateGCutEdge();
            updateKCutEdge();
        }

    }

    private void updateCCutEdge() {

        if (sideCEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideCEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(points[1].getX(), points[1].getY()));
            pointsForEdge.add(new Point2D(points[2].getX(), points[2].getY()));
            pointsForEdge.add(new Point2D(points[2].getX(), points[2].getY() - h));
            pointsForEdge.add(new Point2D(points[1].getX(), points[1].getY() - h));

            Polygon edgePolygon = cutShapeEdgeC.getPolygon();
            edgePolygon.getPoints().clear();
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeC.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeC.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

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

    private void updateDCutEdge() {

        if (sideDEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideDEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(points[5].getX(), points[5].getY()));
            pointsForEdge.add(new Point2D(points[4].getX(), points[4].getY()));
            pointsForEdge.add(new Point2D(points[4].getX(), points[4].getY() + h));
            pointsForEdge.add(new Point2D(points[5].getX(), points[5].getY() + h));

            Polygon edgePolygon = cutShapeEdgeD.getPolygon();
            edgePolygon.getPoints().clear();
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeD.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeD.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

            //cutShapeEdgeRight.setStartCoordinate(new Point2D(cutShapeEdgeRight.getTranslateX(), cutShapeEdgeRight.getTranslateY()));

            //connect points:
            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeD.getConnectPoints();
            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeD.setStartCoordinate(new Point2D(0.0, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeD.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeD.getStartCoordinate().getX());
                cutShapeEdgeD.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeD.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeD.setStartCoordinate(null);
        }
    }

    private void updateECutEdge() {

        if (sideEEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideEEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);

            Polygon edgePolygon = cutShapeEdgeE.getPolygon();
            edgePolygon.getPoints().clear();
            // CREATE POINTS
            pointsForEdge.add(new Point2D(points[0].getX(), points[0].getY()));
            pointsForEdge.add(new Point2D(points[1].getX(), points[1].getY()));
            pointsForEdge.add(new Point2D(points[1].getX(), points[1].getY() - h));
            pointsForEdge.add(new Point2D(points[0].getX(), points[0].getY() - h));

            //ROTATE TWO POINTS
            double angle = -45;
            Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
            Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

            pointsForEdge.set(2, p2);
            pointsForEdge.set(3, p3);

            //ADD POINTS TO POLYGON:
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeE.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeE.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

            //System.out.println("edgePolygon.getBoundsInLocal().getWidth() = " + edgePolygon.getBoundsInLocal().getWidth());
            //System.out.println("edgePolygon.getBoundsInLocal().getHeight() = " + edgePolygon.getBoundsInLocal().getHeight());
            //System.out.println("sizeRadiusShape = " + sizeRadiusShape);
            //cutShapeEdgeE.setStyle("-fx-background-color: red");

            //cutShapeEdgeRight.setStartCoordinate(new Point2D(cutShapeEdgeRight.getTranslateX(), cutShapeEdgeRight.getTranslateY()));

            //connect points:
            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeE.getConnectPoints();
            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeE.setStartCoordinate(new Point2D(0.0, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeE.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeE.getStartCoordinate().getX());
                cutShapeEdgeE.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeE.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeE.setStartCoordinate(null);
        }

    }

    private void updateFCutEdge() {

        if (sideFEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideFEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);

            Polygon edgePolygon = cutShapeEdgeF.getPolygon();
            edgePolygon.getPoints().clear();
            // CREATE POINTS
            pointsForEdge.add(new Point2D(points[2].getX(), points[2].getY()));
            pointsForEdge.add(new Point2D(points[3].getX(), points[3].getY()));
            pointsForEdge.add(new Point2D(points[3].getX(), points[3].getY() - h));
            pointsForEdge.add(new Point2D(points[2].getX(), points[2].getY() - h));

            //ROTATE TWO POINTS
            double angle = 45;
            Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
            Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

            pointsForEdge.set(2, p2);
            pointsForEdge.set(3, p3);

            //ADD POINTS TO POLYGON:
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeF.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeF.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

            //connect points:
            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeF.getConnectPoints();
            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeF.setStartCoordinate(new Point2D(0.0, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeF.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeF.getStartCoordinate().getX());
                cutShapeEdgeF.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeF.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeF.setStartCoordinate(null);
        }

    }

    private void updateGCutEdge() {

        if (sideGEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideGEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);

            Polygon edgePolygon = cutShapeEdgeK.getPolygon();
            edgePolygon.getPoints().clear();
            // CREATE POINTS
            pointsForEdge.add(new Point2D(points[0].getX(), points[0].getY()));
            pointsForEdge.add(new Point2D(points[5].getX(), points[5].getY()));
            pointsForEdge.add(new Point2D(points[5].getX(), points[5].getY() + h));
            pointsForEdge.add(new Point2D(points[0].getX(), points[0].getY() + h));

            //ROTATE TWO POINTS
            double angle = 45;
            Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
            Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

            pointsForEdge.set(2, p2);
            pointsForEdge.set(3, p3);

            //ADD POINTS TO POLYGON:
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeG.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeG.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

            //connect points:
            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeG.getConnectPoints();
            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeG.setStartCoordinate(new Point2D(0.0, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeG.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeG.getStartCoordinate().getX());
                cutShapeEdgeG.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeG.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeK.setStartCoordinate(null);
        }

    }

    private void updateKCutEdge() {

        if (sideKEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideGEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);

            Polygon edgePolygon = cutShapeEdgeG.getPolygon();
            edgePolygon.getPoints().clear();
            // CREATE POINTS
            pointsForEdge.add(new Point2D(points[4].getX(), points[4].getY()));
            pointsForEdge.add(new Point2D(points[3].getX(), points[3].getY()));
            pointsForEdge.add(new Point2D(points[3].getX(), points[3].getY() + h));
            pointsForEdge.add(new Point2D(points[4].getX(), points[4].getY() + h));

            //ROTATE TWO POINTS
            double angle = -45;
            Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
            Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

            pointsForEdge.set(2, p2);
            pointsForEdge.set(3, p3);

            //ADD POINTS TO POLYGON:
            for (Point2D p : pointsForEdge) {
                edgePolygon.getPoints().add(p.getX());
                edgePolygon.getPoints().add(p.getY());
            }

            cutShapeEdgeG.setPrefWidth(edgePolygon.getBoundsInLocal().getWidth());
            cutShapeEdgeG.setPrefHeight(edgePolygon.getBoundsInLocal().getHeight());

            //connect points:
            ArrayList<ConnectPoint> edgeConnectPoints = cutShapeEdgeG.getConnectPoints();
            edgeConnectPoints.get(0).changeSetPoint(pointsForEdge.get(0));
            edgeConnectPoints.get(1).changeSetPoint(pointsForEdge.get(1));
            edgeConnectPoints.get(2).changeSetPoint(pointsForEdge.get(2));
            edgeConnectPoints.get(3).changeSetPoint(pointsForEdge.get(3));

            cutShapeEdgeG.setStartCoordinate(new Point2D(0.0, 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeG.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeG.getStartCoordinate().getX());
                cutShapeEdgeG.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeG.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeG.setStartCoordinate(null);
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
        return sizeBReal;
    }

    @Override
    public void initShapeSettings() {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/sketchShapeRhombusSettings.fxml"));
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
        textFieldDSize = (TextField) settingsRootAnchorPane.lookup("#textFieldDSize");
        textFieldEFGKSize = (TextField) settingsRootAnchorPane.lookup("#textFieldEFGKSize");

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
                new Point2D(0.0, 30.0),
                new Point2D(30.0, 0.0),
                new Point2D(60.0, 0.0),
                new Point2D(90, 30),
                new Point2D(60, 60),
                new Point2D(30, 60)
        };
        polygonSettingsShape = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY(),
                points[4].getX(), points[4].getY(),
                points[5].getX(), points[5].getY()
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
        labelA.setTranslateX(100);
        labelA.setTranslateY(70);

        labelB = new Label("B");
        labelB.setAlignment(Pos.CENTER);
        labelB.setPrefSize(30.0, 30.0);
        labelB.setTranslateX(100);
        labelB.setTranslateY(50);

        labelC = new Label("C");
        labelC.setAlignment(Pos.CENTER);
        labelC.setPrefSize(30.0, 30.0);
        labelC.setTranslateX(100);
        labelC.setTranslateY(90);

        labelD = new Label("D");
        labelD.setAlignment(Pos.CENTER);
        labelD.setPrefSize(30.0, 30.0);
        labelD.setTranslateX(100);
        labelD.setTranslateY(10);

        labelE = new Label("E");
        labelE.setAlignment(Pos.CENTER);
        labelE.setPrefSize(30.0, 30.0);
        labelE.setTranslateX(60);
        labelE.setTranslateY(30);

        labelF = new Label("F");
        labelF.setAlignment(Pos.CENTER);
        labelF.setPrefSize(30.0, 30.0);
        labelF.setTranslateX(135);
        labelF.setTranslateY(30);

        labelG = new Label("G");
        labelG.setAlignment(Pos.CENTER);
        labelG.setPrefSize(30.0, 30.0);
        labelG.setTranslateX(60);
        labelG.setTranslateY(65);

        labelK = new Label("K");
        labelK.setAlignment(Pos.CENTER);
        labelK.setPrefSize(30.0, 30.0);
        labelK.setTranslateX(135);
        labelK.setTranslateY(65);

        paneShapeView.getChildren().addAll(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelK);

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
        textFieldDSize.setText(String.format("%.0f", sizeDReal));
        textFieldEFGKSize.setText(String.format("%.0f", sizeEReal));
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
            double a, b, c, d, e;
            try {
                a = Double.parseDouble(textFieldASize.getText());
                b = Double.parseDouble(textFieldBSize.getText());
                c = Double.parseDouble(textFieldCSize.getText());
                d = Double.parseDouble(textFieldDSize.getText());

                e = Math.sqrt(Math.pow(b / 2, 2) + Math.pow((a - c) / 2, 2));


                textFieldEFGKSize.setText(String.format(Locale.ENGLISH, "%.0f", e));


                correctASize = true;
                textFieldASize.setStyle("-fx-text-fill: #B3B4B4");
                if (b > a || c < 10 || c > a || d < 10 || d > a) {
                    correctASize = false;
                    textFieldASize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctASize = false;
                textFieldASize.setStyle("-fx-text-fill: red");
            }
            //check that it correct
        });
        textFieldBSize.textProperty().addListener(observable -> {
            //check that it correct
            double a, b, c, d, e;
            try {
                a = Double.parseDouble(textFieldASize.getText());
                b = Double.parseDouble(textFieldBSize.getText());
                c = Double.parseDouble(textFieldCSize.getText());
                d = Double.parseDouble(textFieldDSize.getText());

                e = Math.sqrt(Math.pow(b / 2, 2) + Math.pow((a - c) / 2, 2));


                textFieldEFGKSize.setText(String.format(Locale.ENGLISH, "%.0f", e));

                correctBSize = true;
                textFieldBSize.setStyle("-fx-text-fill: #B3B4B4");
                if (b > a || c < 10 || c > a || d < 10 || d > a) {
                    correctBSize = false;
                    textFieldBSize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctBSize = false;
                textFieldBSize.setStyle("-fx-text-fill: red");
            }
        });

        textFieldCSize.textProperty().addListener(observable -> {
            //check that it correct
            double a, b, c, d, e, alpha, betta;
            try {
                a = Double.parseDouble(textFieldASize.getText());
                b = Double.parseDouble(textFieldBSize.getText());
                c = Double.parseDouble(textFieldCSize.getText());
                d = Double.parseDouble(textFieldDSize.getText());

                e = Math.sqrt(Math.pow(b / 2, 2) + Math.pow((a - c) / 2, 2));

                textFieldEFGKSize.setText(String.format(Locale.ENGLISH, "%.0f", e));

                correctCSize = true;
                textFieldCSize.setStyle("-fx-text-fill: #B3B4B4");
                if (b > a || c < 10 || c > a || d < 10 || d > a) {
                    correctCSize = false;
                    textFieldCSize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctCSize = false;
                textFieldCSize.setStyle("-fx-text-fill: red");
            }
        });

        textFieldDSize.textProperty().addListener(observable -> {
            //check that it correct
            double a, b, c, d, e;
            try {
                a = Double.parseDouble(textFieldASize.getText());
                b = Double.parseDouble(textFieldBSize.getText());
                c = Double.parseDouble(textFieldCSize.getText());
                d = Double.parseDouble(textFieldDSize.getText());

                e = Math.sqrt(Math.pow(b / 2, 2) + Math.pow((a - c) / 2, 2));

                textFieldEFGKSize.setText(String.format(Locale.ENGLISH, "%.0f", e));

                correctDSize = true;
                textFieldDSize.setStyle("-fx-text-fill: #B3B4B4");
                if (b > a || c < 10 || c > a || d < 10 || d > a) {
                    correctDSize = false;
                    textFieldDSize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctDSize = false;
                textFieldDSize.setStyle("-fx-text-fill: red");
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
        //System.out.println("REFRESH SHAPE SETTINGS ");

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
        textFieldDSize.setText(String.format("%.0f", sizeDReal));

        sizeEReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
        textFieldEFGKSize.setText(String.format(Locale.ENGLISH, "%.0f", sizeEReal));

        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));

        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
        textFieldBorderHeight.setText(String.valueOf(borderHeight));

    }

    @Override
    public void shapeSettingsSaveBtnClicked() {
        System.out.println("SAVE");
        if ((!correctBorderHeight) || (!correctEdgeHeight) || (!correctASize) || (!correctBSize) ||
                (!correctCSize) || (!correctDSize) || (!correctX) || (!correctY)) {
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Проверьте введенные данные!", null);
            return;
        }

        saveMaterialImageOnEdges = checkBoxSaveImage.isSelected();

        sizeAReal = Integer.parseInt(textFieldASize.getText());
        sizeBReal = Integer.parseInt(textFieldBSize.getText());
        sizeCReal = Integer.parseInt(textFieldCSize.getText());
        sizeDReal = Integer.parseInt(textFieldDSize.getText());
        sizeEReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) + Math.pow((sizeAReal - sizeCReal) / 2, 2));


        double minMaterialSize = Math.min(shapeMaterial.getMaterialWidth(), shapeMaterial.getMaterialHeight());
        double maxMaterialSize = Math.max(shapeMaterial.getMaterialWidth(), shapeMaterial.getMaterialHeight());

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
//            sizeDShape = sizeDReal*commonShapeScale;
            textFieldASize.setText(String.format("%.0f", sizeAReal));
            textFieldBSize.setText(String.format("%.0f", sizeBReal));
            textFieldCSize.setText(String.format("%.0f", sizeCReal));
            textFieldDSize.setText(String.format("%.0f", sizeDReal));
            textFieldEFGKSize.setText(String.format("%.0f", sizeEReal));
            return;
        } else {
            sizeAShape = sizeAReal * commonShapeScale;
            sizeBShape = sizeBReal * commonShapeScale;
            //System.out.println("WIDTH 3 = " + sizeAReal);
        }

        sizeAShape = sizeAReal * commonShapeScale;
        sizeBShape = sizeBReal * commonShapeScale;
        sizeCShape = sizeCReal * commonShapeScale;
        sizeDShape = sizeDReal * commonShapeScale;
        sizeEShape = sizeEReal * commonShapeScale;


        textFieldASize.setText(String.format("%.0f", sizeAReal));
        textFieldBSize.setText(String.format("%.0f", sizeBReal));
        textFieldCSize.setText(String.format("%.0f", sizeCReal));
        textFieldDSize.setText(String.format("%.0f", sizeDReal));
        textFieldEFGKSize.setText(String.format("%.0f", sizeEReal));


        setTranslateX(Double.parseDouble(textFieldX.getText()));
        setTranslateY(Double.parseDouble(textFieldY.getText()));


        materialDefault = checkBoxMaterialDefault.isSelected();
        edgesHeightsDefault = checkBoxDefaultHeights.isSelected() ? true : false;

        if (checkBoxMaterialDefault.isSelected()) {
            if (shapeDepth != ProjectHandler.getDefaultMaterial().getDefaultDepth() ||
                    (!shapeMaterial.getName().equals(ProjectHandler.getDefaultMaterial()))) {
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
                if (SketchDesigner.getSelectedEdgeMaterial() == null ||
                        SketchDesigner.getSelectedEdgeMaterial().getName().equals(shapeMaterial.getName())) {
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
                new Point2D(0.0, sizeBShape / 2),
                new Point2D((sizeAShape - sizeCShape) / 2, 0.0),
                new Point2D((sizeAShape - sizeCShape) / 2 + sizeCShape, 0.0),
                new Point2D(sizeAShape, sizeBShape / 2),
                new Point2D((sizeAShape - sizeDShape) / 2 + sizeDShape, sizeBShape),
                new Point2D((sizeAShape - sizeDShape) / 2, sizeBShape),
        };


        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY(),
                points[4].getX(), points[4].getY(),
                points[5].getX(), points[5].getY()
        );

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


        if (sideCEdge == null) sideCEdge = new SketchEdge();
        sideCEdge.getPoints().clear();
        sideCEdge.getPoints().addAll(
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[2].getX(), points[2].getY() + widthEdge,
                points[1].getX(), points[1].getY() + widthEdge
        );
        getChildren().remove(sideCEdge);
        getChildren().add(sideCEdge);
        sideCEdge.setOnMouseClicked(event -> edgeManagerShow(sideCEdge));

        if (sideDEdge == null) sideDEdge = new SketchEdge();
        sideDEdge.getPoints().clear();
        sideDEdge.getPoints().addAll(
                points[5].getX(), points[5].getY(),
                points[4].getX(), points[4].getY(),
                points[4].getX(), points[4].getY() - widthEdge,
                points[5].getX(), points[5].getY() - widthEdge
        );
        getChildren().remove(sideDEdge);
        getChildren().add(sideDEdge);
        sideDEdge.setOnMouseClicked(event -> edgeManagerShow(sideDEdge));

        if (sideEEdge == null) sideEEdge = new SketchEdge();
        sideEEdge.getPoints().clear();
        sideEEdge.getPoints().addAll(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[1].getX() + widthEdge, points[1].getY(),
                points[0].getX() + widthEdge, points[0].getY()
        );
        getChildren().remove(sideEEdge);
        getChildren().add(sideEEdge);
        sideEEdge.setOnMouseClicked(event -> edgeManagerShow(sideEEdge));

        if (sideFEdge == null) sideFEdge = new SketchEdge();
        sideFEdge.getPoints().clear();
        sideFEdge.getPoints().addAll(
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY(),
                points[3].getX() - widthEdge, points[3].getY(),
                points[2].getX() - widthEdge, points[2].getY()
        );
        getChildren().remove(sideFEdge);
        getChildren().add(sideFEdge);
        sideFEdge.setOnMouseClicked(event -> edgeManagerShow(sideFEdge));


        if (sideGEdge == null) sideGEdge = new SketchEdge();
        sideGEdge.getPoints().clear();
        sideGEdge.getPoints().addAll(
                points[0].getX(), points[0].getY(),
                points[5].getX(), points[5].getY(),
                points[5].getX() + widthEdge, points[5].getY(),
                points[0].getX() + widthEdge, points[0].getY()
        );
        getChildren().remove(sideGEdge);
        getChildren().add(sideGEdge);
        sideGEdge.setOnMouseClicked(event -> edgeManagerShow(sideGEdge));


        if (sideKEdge == null) sideKEdge = new SketchEdge();
        sideKEdge.getPoints().clear();
        sideKEdge.getPoints().addAll(
                points[4].getX(), points[4].getY(),
                points[3].getX(), points[3].getY(),
                points[3].getX() - widthEdge, points[3].getY(),
                points[4].getX() - widthEdge, points[4].getY()
        );
        getChildren().remove(sideKEdge);
        getChildren().add(sideKEdge);
        sideKEdge.setOnMouseClicked(event -> edgeManagerShow(sideKEdge));

        sideCEdge.setSketchEdgeOwner(this);
        sideDEdge.setSketchEdgeOwner(this);
        sideEEdge.setSketchEdgeOwner(this);
        sideFEdge.setSketchEdgeOwner(this);
        sideGEdge.setSketchEdgeOwner(this);
        sideKEdge.setSketchEdgeOwner(this);
    }

    @Override
    public void changeElementEdge(SketchEdge edge, SketchEdge newEdge) {
        newEdge.setSketchEdgeOwner(this);
        if (sideCEdge == edge) {

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

        } else if (sideDEdge == edge) {

            if (!sideDEdge.getName().equals(newEdge.getName())) {
                if (sideDEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideDEdge);
                } else if (sideDEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideDEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }

            getChildren().remove(sideDEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideDEdge.getPoints());
            sideDEdge = newEdge;
            getChildren().add(sideDEdge);
            sideDEdge.setOnMouseClicked(event -> edgeManagerShow(sideDEdge));

        } else if (sideEEdge == edge) {

            if (!sideEEdge.getName().equals(newEdge.getName())) {
                if (sideEEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideEEdge);
                } else if (sideEEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideEEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }
            getChildren().remove(sideEEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideEEdge.getPoints());
            sideEEdge = newEdge;
            getChildren().add(sideEEdge);
            sideEEdge.setOnMouseClicked(event -> edgeManagerShow(sideEEdge));

        } else if (sideFEdge == edge) {

            if (!sideFEdge.getName().equals(newEdge.getName())) {
                if (sideFEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideFEdge);
                } else if (sideFEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideFEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }
            getChildren().remove(sideFEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideFEdge.getPoints());
            sideFEdge = newEdge;
            getChildren().add(sideFEdge);
            sideFEdge.setOnMouseClicked(event -> edgeManagerShow(sideFEdge));

        } else if (sideGEdge == edge) {

            if (!sideGEdge.getName().equals(newEdge.getName())) {
                if (sideGEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideGEdge);
                } else if (sideGEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideGEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }
            getChildren().remove(sideGEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideGEdge.getPoints());
            sideGEdge = newEdge;
            getChildren().add(sideGEdge);
            sideGEdge.setOnMouseClicked(event -> edgeManagerShow(sideGEdge));

        } else if (sideKEdge == edge) {

            if (!sideKEdge.getName().equals(newEdge.getName())) {
                if (sideKEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().remove(sideKEdge);
                } else if (sideKEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().remove(sideKEdge);
                }

                if (newEdge instanceof Edge) {
                    ProjectHandler.getEdgesUsesInProjectObservable().add((Edge) newEdge);
                } else if (newEdge instanceof Border) {
                    ProjectHandler.getBordersUsesInProjectObservable().add((Border) newEdge);
                }
            }
            getChildren().remove(sideKEdge);
            newEdge.getPoints().clear();
            newEdge.getPoints().addAll(sideKEdge.getPoints());
            sideKEdge = newEdge;
            getChildren().add(sideKEdge);
            sideKEdge.setOnMouseClicked(event -> edgeManagerShow(sideKEdge));

        }

        //initEdgesZones();
        refreshEdgeView();
        //refreshShapeSettings();
        //rotateEdgesIcons(rotateAngle);
    }

    @Override
    public ArrayList<SketchEdge> getEdges() {
        return new ArrayList<SketchEdge>(Arrays.asList(sideCEdge, sideDEdge, sideEEdge, sideFEdge, sideGEdge,
                sideKEdge));
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

        getChildren().remove(triangleIconSideCEdge);
        getChildren().remove(triangleIconSideDEdge);
        getChildren().remove(triangleIconSideEEdge);
        getChildren().remove(triangleIconSideFEdge);
        getChildren().remove(triangleIconSideGEdge);
        getChildren().remove(triangleIconSideKEdge);

        getChildren().remove(lineGrooveCEdge);
        getChildren().remove(lineGrooveDEdge);
        getChildren().remove(lineGrooveEEdge);
        getChildren().remove(lineGrooveFEdge);
        getChildren().remove(lineGrooveGEdge);
        getChildren().remove(lineGrooveKEdge);

        double translateX = 0;
        double translateY = 0;

        //sideCEdge
        {
            if (sideCEdge instanceof Edge) {
                triangleIconSideCEdge = new Polygon(
                        0.0, 0.0,
                        2.5, 5.0,
                        -2.5, 5.0);

                translateX = (points[1].getX() + points[2].getX()) / 2;
                translateY = points[1].getY();
            } else {
                triangleIconSideCEdge = new Polygon(
                        -2.5, 0.0,
                        2.5, 5.0,
                        0.0, 2.5,
                        2.5, 0.0,
                        -2.5, 5.0,
                        0.0, 2.5);
                triangleIconSideCEdge.setStroke(Color.BLACK);
                translateX = (points[1].getX() + points[2].getX()) / 2;
                translateY = points[1].getY() + 1;
            }


            //SketchObject.rotatePolygon(triangleIconSideCEdge, new Point2D(0.0, 0.0), 0.0);
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
                    lineGrooveCEdge = new Line(points[1].getX(), points[1].getY() + 5, points[2].getX(),
                            points[2].getY() + 5);
                    lineGrooveCEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                    lineGrooveCEdge.getStrokeDashArray().addAll(5.0, 3.0);
                    getChildren().add(lineGrooveCEdge);
                    lineGrooveCEdge.setVisible((true));
                }

            } else {
                triangleIconSideCEdge.setVisible(false);
            }
        }

        //sideDEdge
        {
            if (sideDEdge instanceof Edge) {
                triangleIconSideDEdge = new Polygon(
                        0.0, 0.0,
                        2.5, -5.0,
                        -2.5, -5.0);
                translateX = (points[5].getX() + points[4].getX()) / 2;
                translateY = points[5].getY();
            } else {
                triangleIconSideDEdge = new Polygon(
                        -2.5, 0.0,
                        2.5, -5.0,
                        0.0, -2.5,
                        -2.5, -5.0,
                        2.5, 0.0,
                        0.0, -2.5);
                translateX = (points[5].getX() + points[4].getX()) / 2;
                translateY = points[5].getY() - 1;
                triangleIconSideDEdge.setStroke(Color.BLACK);
            }
            triangleIconSideDEdge.setFill(Color.BLACK);
            for (int i = 0; i < triangleIconSideDEdge.getPoints().size(); i += 2) {
                double newX = triangleIconSideDEdge.getPoints().get(i).doubleValue() + translateX;
                double newY = triangleIconSideDEdge.getPoints().get(i + 1).doubleValue() + translateY;
                triangleIconSideDEdge.getPoints().set(i, newX);
                triangleIconSideDEdge.getPoints().set(i + 1, newY);
            }
            getChildren().add(triangleIconSideDEdge);
            Tooltip.install(triangleIconSideDEdge, sideDEdge.getTooltip());
            SketchObject.rotatePolygon(triangleIconSideDEdge, getRotationPivot(), rotateAngle);
            if (sideDEdge.isDefined()) {
                triangleIconSideDEdge.setVisible(true);

                if (sideDEdge instanceof Edge && ((Edge) sideDEdge).isStoneHemOrLeakGroove()) {
                    lineGrooveDEdge = new Line(points[5].getX(), points[5].getY() - 5, points[4].getX(), points[4].getY() - 5);
                    lineGrooveDEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                    lineGrooveDEdge.getStrokeDashArray().addAll(5.0, 3.0);
                    getChildren().add(lineGrooveDEdge);
                    lineGrooveDEdge.setVisible((true));
                }

            } else {
                triangleIconSideDEdge.setVisible(false);
            }
        }


        //sideEEdge
        {
            if (sideEEdge instanceof Edge) {
                triangleIconSideEEdge = new Polygon(
                        0.0, 0.0,
                        2.5, 5.0,
                        -2.5, 5.0);
                translateX = (points[0].getX() + points[1].getX()) / 2;
                translateY = (points[0].getY() + points[1].getY()) / 2;
            } else {
                triangleIconSideEEdge = new Polygon(
                        -2.5, 0.0,
                        2.5, 5.0,
                        0.0, 2.5,
                        2.5, 0.0,
                        -2.5, 5.0,
                        0.0, 2.5);
                translateX = (points[0].getX() + points[1].getX()) / 2;
                translateY = (points[0].getY() + points[1].getY()) / 2;
                triangleIconSideEEdge.setStroke(Color.BLACK);
            }
            SketchObject.rotatePolygon(triangleIconSideEEdge, new Point2D(0.0, 0.0), -45);
            triangleIconSideEEdge.setFill(Color.BLACK);
            for (int i = 0; i < triangleIconSideEEdge.getPoints().size(); i += 2) {
                double newX = triangleIconSideEEdge.getPoints().get(i).doubleValue() + translateX;
                double newY = triangleIconSideEEdge.getPoints().get(i + 1).doubleValue() + translateY;
                triangleIconSideEEdge.getPoints().set(i, newX);
                triangleIconSideEEdge.getPoints().set(i + 1, newY);
            }
            getChildren().add(triangleIconSideEEdge);
            Tooltip.install(triangleIconSideEEdge, sideEEdge.getTooltip());
            SketchObject.rotatePolygon(triangleIconSideEEdge, getRotationPivot(), rotateAngle);
            if (sideEEdge.isDefined()) {
                triangleIconSideEEdge.setVisible(true);

                if (sideEEdge instanceof Edge && ((Edge) sideEEdge).isStoneHemOrLeakGroove()) {
                    lineGrooveEEdge = new Line(points[0].getX() + 5, points[0].getY(),
                            points[1].getX() + 5, points[1].getY());
                    lineGrooveEEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                    lineGrooveEEdge.getStrokeDashArray().addAll(5.0, 3.0);
                    getChildren().add(lineGrooveEEdge);
                    lineGrooveEEdge.setVisible((true));
                }

            } else {
                triangleIconSideEEdge.setVisible(false);
            }
        }

        //sideFEdge
        {
            if (sideFEdge instanceof Edge) {
                triangleIconSideFEdge = new Polygon(
                        0.0, 0.0,
                        2.5, 5.0,
                        -2.5, 5.0);
                translateX = (points[2].getX() + points[3].getX()) / 2;
                translateY = (points[2].getY() + points[3].getY()) / 2;
            } else {
                triangleIconSideFEdge = new Polygon(
                        -2.5, 0.0,
                        2.5, 5.0,
                        0.0, 2.5,
                        2.5, 0.0,
                        -2.5, 5.0,
                        0.0, 2.5);
                translateX = (points[2].getX() + points[3].getX()) / 2;
                translateY = (points[2].getY() + points[3].getY()) / 2;
                triangleIconSideFEdge.setStroke(Color.BLACK);
            }
            SketchObject.rotatePolygon(triangleIconSideFEdge, new Point2D(0.0, 0.0), 45);
            triangleIconSideFEdge.setFill(Color.BLACK);
            for (int i = 0; i < triangleIconSideFEdge.getPoints().size(); i += 2) {
                double newX = triangleIconSideFEdge.getPoints().get(i).doubleValue() + translateX;
                double newY = triangleIconSideFEdge.getPoints().get(i + 1).doubleValue() + translateY;
                triangleIconSideFEdge.getPoints().set(i, newX);
                triangleIconSideFEdge.getPoints().set(i + 1, newY);
            }
            getChildren().add(triangleIconSideFEdge);
            Tooltip.install(triangleIconSideFEdge, sideFEdge.getTooltip());
            SketchObject.rotatePolygon(triangleIconSideFEdge, getRotationPivot(), rotateAngle);
            if (sideFEdge.isDefined()) {
                triangleIconSideFEdge.setVisible(true);

                if (sideFEdge instanceof Edge && ((Edge) sideFEdge).isStoneHemOrLeakGroove()) {
                    lineGrooveFEdge = new Line(points[2].getX() - 5, points[2].getY(),
                            points[3].getX() - 5, points[3].getY());
                    lineGrooveFEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                    lineGrooveFEdge.getStrokeDashArray().addAll(5.0, 3.0);
                    getChildren().add(lineGrooveFEdge);
                    lineGrooveFEdge.setVisible((true));
                }

            } else {
                triangleIconSideFEdge.setVisible(false);
            }
        }

        //sideGEdge
        {
            if (sideGEdge instanceof Edge) {
                triangleIconSideGEdge = new Polygon(
                        0.0, 0.0,
                        2.5, -5.0,
                        -2.5, -5.0);
                translateX = (points[0].getX() + points[5].getX()) / 2;
                translateY = (points[0].getY() + points[5].getY()) / 2;
            } else {
                triangleIconSideGEdge = new Polygon(
                        -2.5, 0.0,
                        2.5, -5.0,
                        0.0, -2.5,
                        -2.5, -5.0,
                        2.5, 0.0,
                        0.0, -2.5);
                translateX = (points[0].getX() + points[5].getX()) / 2;
                translateY = (points[0].getY() + points[5].getY()) / 2;
                triangleIconSideGEdge.setStroke(Color.BLACK);
            }
            SketchObject.rotatePolygon(triangleIconSideGEdge, new Point2D(0.0, 0.0), 45);
            triangleIconSideGEdge.setFill(Color.BLACK);
            for (int i = 0; i < triangleIconSideGEdge.getPoints().size(); i += 2) {
                double newX = triangleIconSideGEdge.getPoints().get(i).doubleValue() + translateX;
                double newY = triangleIconSideGEdge.getPoints().get(i + 1).doubleValue() + translateY;
                triangleIconSideGEdge.getPoints().set(i, newX);
                triangleIconSideGEdge.getPoints().set(i + 1, newY);
            }
            getChildren().add(triangleIconSideGEdge);
            Tooltip.install(triangleIconSideGEdge, sideGEdge.getTooltip());
            SketchObject.rotatePolygon(triangleIconSideGEdge, getRotationPivot(), rotateAngle);
            if (sideGEdge.isDefined()) {
                triangleIconSideGEdge.setVisible(true);

                if (sideGEdge instanceof Edge && ((Edge) sideGEdge).isStoneHemOrLeakGroove()) {
                    lineGrooveGEdge = new Line(points[0].getX() + 5, points[0].getY(),
                            points[5].getX() + 5, points[5].getY());
                    lineGrooveGEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                    lineGrooveGEdge.getStrokeDashArray().addAll(5.0, 3.0);
                    getChildren().add(lineGrooveGEdge);
                    lineGrooveGEdge.setVisible((true));
                }

            } else {
                triangleIconSideGEdge.setVisible(false);
            }
        }

        //sidKEdge
        {
            if (sideKEdge instanceof Edge) {
                triangleIconSideKEdge = new Polygon(
                        0.0, 0.0,
                        2.5, -5.0,
                        -2.5, -5.0);
                translateX = (points[4].getX() + points[3].getX()) / 2;
                translateY = (points[4].getY() + points[3].getY()) / 2;
            } else {
                triangleIconSideKEdge = new Polygon(
                        -2.5, 0.0,
                        2.5, -5.0,
                        0.0, -2.5,
                        -2.5, -5.0,
                        2.5, 0.0,
                        0.0, -2.5);
                translateX = (points[4].getX() + points[3].getX()) / 2;
                translateY = (points[4].getY() + points[3].getY()) / 2;
                triangleIconSideKEdge.setStroke(Color.BLACK);
            }
            SketchObject.rotatePolygon(triangleIconSideKEdge, new Point2D(0.0, 0.0), -45);
            triangleIconSideKEdge.setFill(Color.BLACK);
            for (int i = 0; i < triangleIconSideKEdge.getPoints().size(); i += 2) {
                double newX = triangleIconSideKEdge.getPoints().get(i).doubleValue() + translateX;
                double newY = triangleIconSideKEdge.getPoints().get(i + 1).doubleValue() + translateY;
                triangleIconSideKEdge.getPoints().set(i, newX);
                triangleIconSideKEdge.getPoints().set(i + 1, newY);
            }
            getChildren().add(triangleIconSideKEdge);
            Tooltip.install(triangleIconSideKEdge, sideKEdge.getTooltip());
            SketchObject.rotatePolygon(triangleIconSideKEdge, getRotationPivot(), rotateAngle);
            if (sideKEdge.isDefined()) {
                triangleIconSideKEdge.setVisible(true);

                if (sideKEdge instanceof Edge && ((Edge) sideKEdge).isStoneHemOrLeakGroove()) {
                    lineGrooveKEdge = new Line(points[4].getX() - 5, points[4].getY(),
                            points[3].getX() - 5, points[3].getY());
                    lineGrooveKEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                    lineGrooveKEdge.getStrokeDashArray().addAll(5.0, 3.0);
                    getChildren().add(lineGrooveKEdge);
                    lineGrooveKEdge.setVisible((true));
                }

            } else {
                triangleIconSideKEdge.setVisible(false);
            }
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

        if (edge == sideCEdge) {
            return sizeCReal;
        } else if (edge == sideDEdge) {
            return sizeDReal;
        } else if (edge == sideEEdge) {
            sizeEReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            return sizeEReal;
        } else if (edge == sideFEdge) {
            sizeFReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            return sizeFReal;
        } else if (edge == sideGEdge) {
            sizeGReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            return sizeGReal;
        } else if (edge == sideKEdge) {
            sizeKReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            return sizeKReal;
        }

        return 0;
    }

    @Override
    public double getEdgesLength() {

        double length = 0;
        if (sideCEdge != null && (sideCEdge instanceof Edge)) {
            length += sizeCReal;
        }
        if (sideDEdge != null && (sideDEdge instanceof Edge)) {
            length += sizeDReal;
        }
        if (sideEEdge != null && (sideEEdge instanceof Edge)) {
            sizeEReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeEReal;
        }
        if (sideFEdge != null && (sideFEdge instanceof Edge)) {
            sizeFReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeFReal;
        }
        if (sideGEdge != null && (sideGEdge instanceof Edge)) {
            sizeGReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeGReal;
        }
        if (sideKEdge != null && (sideKEdge instanceof Edge)) {
            sizeKReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeKReal;
        }
        return length;

    }

    @Override
    public double getBordersType1Length() {

        double length = 0;
        if (sideCEdge != null && (sideCEdge instanceof Border) && sideCEdge.getName().indexOf("1") != -1) {
            length += sizeCReal;
        }
        if (sideDEdge != null && (sideDEdge instanceof Border) && sideDEdge.getName().indexOf("1") != -1) {
            length += sizeDReal;
        }
        if (sideEEdge != null && (sideEEdge instanceof Border) && sideEEdge.getName().indexOf("1") != -1) {
            sizeEReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeEReal;
        }
        if (sideFEdge != null && (sideFEdge instanceof Border) && sideFEdge.getName().indexOf("1") != -1) {
            sizeFReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeFReal;
        }
        if (sideGEdge != null && (sideGEdge instanceof Border) && sideGEdge.getName().indexOf("1") != -1) {
            sizeGReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeGReal;
        }
        if (sideKEdge != null && (sideKEdge instanceof Border) && sideKEdge.getName().indexOf("1") != -1) {
            sizeKReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeKReal;
        }
        return length;
    }

    @Override
    public double getBordersType2Length() {

        double length = 0;
        if (sideCEdge != null && (sideCEdge instanceof Border) && sideCEdge.getName().indexOf("2") != -1) {
            length += sizeCReal;
        }
        if (sideDEdge != null && (sideDEdge instanceof Border) && sideDEdge.getName().indexOf("2") != -1) {
            length += sizeDReal;
        }
        if (sideEEdge != null && (sideEEdge instanceof Border) && sideEEdge.getName().indexOf("2") != -1) {
            sizeEReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeEReal;
        }
        if (sideFEdge != null && (sideFEdge instanceof Border) && sideFEdge.getName().indexOf("2") != -1) {
            sizeFReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeFReal;
        }
        if (sideGEdge != null && (sideGEdge instanceof Border) && sideGEdge.getName().indexOf("2") != -1) {
            sizeGReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeGReal;
        }
        if (sideKEdge != null && (sideKEdge instanceof Border) && sideKEdge.getName().indexOf("2") != -1) {
            sizeKReal = Math.sqrt(Math.pow(sizeBReal / 2, 2) * 2);
            length += sizeKReal;
        }

        return length;
    }

    public void rotateEdgesIcons(double angle) {
        //ROTATE IT AFTER UPDATE

        //Point2D pivot = new Point2D(polygon.getBoundsInLocal().getWidth()/2, polygon.getBoundsInLocal().getHeight()/2);
        Point2D pivot = getRotationPivot();

        double polygonMinX = polygon.getBoundsInParent().getMinX();
        double polygonMinY = polygon.getBoundsInParent().getMinY();

        System.out.println("Rotate edges Icons");
        //System.out.println("pivot.getX() = " + pivot.getX());
        //System.out.println("pivot.getY() = " + pivot.getY());
        //ROTATE EDGES ICONS
        ArrayList<Polygon> edgesIcons = new ArrayList<>();
        edgesIcons.add(triangleIconSideCEdge);
        edgesIcons.add(triangleIconSideDEdge);
        edgesIcons.add(triangleIconSideEEdge);
        edgesIcons.add(triangleIconSideFEdge);
        edgesIcons.add(triangleIconSideGEdge);
        edgesIcons.add(triangleIconSideKEdge);
        for (Polygon icon : edgesIcons) {
            if (icon == null) continue;
            for (int i = 0; i < icon.getPoints().size(); i += 2) {
                double originX = icon.getPoints().get(i).doubleValue();
                double originY = icon.getPoints().get(i + 1).doubleValue();
                double newX = (originX - pivot.getX()) * Math.cos(Math.toRadians(angle)) - (originY - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
                double newY = (originX - pivot.getX()) * Math.sin(Math.toRadians(angle)) + (originY - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
                icon.getPoints().set(i, new Double(newX));
                icon.getPoints().set(i + 1, new Double(newY));
            }
        }
        for (Polygon icon : edgesIcons) {
            if (icon == null) continue;
            for (int i = 0; i < icon.getPoints().size(); i += 2) {
                double newX = icon.getPoints().get(i).doubleValue() + polygonMinX;
                double newY = icon.getPoints().get(i + 1).doubleValue() + polygonMinY;
                icon.getPoints().set(i, newX);
                icon.getPoints().set(i + 1, newY);
            }
        }
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

        double newDX = ((labelD.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelD.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newDY = ((labelD.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelD.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelD.setTranslateX(newDX - 15);
        labelD.setTranslateY(newDY - 15);

        double newEX = ((labelE.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelE.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newEY = ((labelE.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelE.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelE.setTranslateX(newEX - 15);
        labelE.setTranslateY(newEY - 15);

        double newFX = ((labelF.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelF.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newFY = ((labelF.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelF.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelF.setTranslateX(newFX - 15);
        labelF.setTranslateY(newFY - 15);

        double newGX = ((labelG.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelG.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newGY = ((labelG.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelG.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelG.setTranslateX(newGX - 15);
        labelG.setTranslateY(newGY - 15);

        double newKX = ((labelK.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelK.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newKY = ((labelK.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelK.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelK.setTranslateX(newKX - 15);
        labelK.setTranslateY(newKY - 15);


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
        sideCJointsList.clear();
        sideDJointsList.clear();
        sideEJointsList.clear();
        sideFJointsList.clear();
        sideGJointsList.clear();
        sideKJointsList.clear();
    }

    @Override
    public void refreshLinesForJoints() {
        Point2D point1;
        Point2D point2;


        point1 = new Point2D(points[1].getX(), points[1].getY());
        point2 = new Point2D(points[2].getX(), points[2].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineCJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(points[5].getX(), points[5].getY());
        point2 = new Point2D(points[4].getX(), points[4].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineDJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(points[0].getX(), points[0].getY());
        point2 = new Point2D(points[1].getX(), points[1].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineEJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(points[2].getX(), points[2].getY());
        point2 = new Point2D(points[3].getX(), points[3].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineFJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(points[0].getX(), points[0].getY());
        point2 = new Point2D(points[5].getX(), points[5].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineGJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(points[4].getX(), points[4].getY());
        point2 = new Point2D(points[3].getX(), points[3].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineKJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    @Override
    public ArrayList<Line> getLineForJoints() {
        refreshLinesForJoints();
        return new ArrayList<Line>(Arrays.asList(lineCJoint, lineDJoint, lineEJoint, lineFJoint, lineGJoint, lineKJoint));
    }

    @Override
    public void addJoint(Line lineForJointSide, Joint newJoint) {
        if (lineForJointSide.equals(lineCJoint)) {
            sideCJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineDJoint)) {
            sideDJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineEJoint)) {
            sideEJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineFJoint)) {
            sideFJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineGJoint)) {
            sideGJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineKJoint)) {
            sideKJointsList.add(newJoint);
        }
    }

    @Override
    public ArrayList<Joint> getJoints() {
        ArrayList<Joint> list = new ArrayList<>();

        list.addAll(sideCJointsList);
        list.addAll(sideDJointsList);
        list.addAll(sideEJointsList);
        list.addAll(sideFJointsList);
        list.addAll(sideGJointsList);
        list.addAll(sideKJointsList);

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

        if (material.getName().indexOf("Акриловый камень") != -1 ||
                material.getName().indexOf("Полиэфирный камень") != -1) {

            checkBoxSaveImage.setDisable(true);
            checkBoxSaveImage.setSelected(false);
            saveMaterialImageOnEdges = false;

            if (sideCEdge.getType() != 1) {
                changeElementEdge(sideCEdge, new SketchEdge());
            }
            if (sideDEdge.getType() != 1) {
                changeElementEdge(sideDEdge, new SketchEdge());
            }
            if (sideEEdge.getType() != 1) {
                changeElementEdge(sideEEdge, new SketchEdge());
            }
            if (sideFEdge.getType() != 1) {
                changeElementEdge(sideFEdge, new SketchEdge());
            }
            if (sideGEdge.getType() != 1) {
                changeElementEdge(sideGEdge, new SketchEdge());
            }
            if (sideKEdge.getType() != 1) {
                changeElementEdge(sideKEdge, new SketchEdge());
            }
        } else if (material.getName().indexOf("Кварцевый агломерат") != -1 ||
                shapeMaterial.getName().indexOf("Натуральный камень") != -1 ||
                material.getName().indexOf("Dektone") != -1 ||
                material.getName().indexOf("Мраморный агломерат") != -1 ||
                material.getName().indexOf("Кварцекерамический камень") != -1) {

            checkBoxSaveImage.setDisable(false);

            if (sideCEdge.getType() != 2) {
                changeElementEdge(sideCEdge, new SketchEdge());
            }
            if (sideDEdge.getType() != 2) {
                changeElementEdge(sideDEdge, new SketchEdge());
            }
            if (sideEEdge.getType() != 2) {
                changeElementEdge(sideEEdge, new SketchEdge());
            }
            if (sideFEdge.getType() != 2) {
                changeElementEdge(sideFEdge, new SketchEdge());
            }
            if (sideGEdge.getType() != 2) {
                changeElementEdge(sideGEdge, new SketchEdge());
            }
            if (sideKEdge.getType() != 2) {
                changeElementEdge(sideKEdge, new SketchEdge());
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
                if (sideCEdge instanceof Edge && ((Edge) sideCEdge).getSubType() > 7)
                    changeElementEdge(sideCEdge, new SketchEdge());
                if (sideDEdge instanceof Edge && ((Edge) sideDEdge).getSubType() > 7)
                    changeElementEdge(sideDEdge, new SketchEdge());
                if (sideEEdge instanceof Edge && ((Edge) sideEEdge).getSubType() > 7)
                    changeElementEdge(sideEEdge, new SketchEdge());
                if (sideFEdge instanceof Edge && ((Edge) sideFEdge).getSubType() > 7)
                    changeElementEdge(sideFEdge, new SketchEdge());
                if (sideGEdge instanceof Edge && ((Edge) sideGEdge).getSubType() > 7)
                    changeElementEdge(sideGEdge, new SketchEdge());
                if (sideKEdge instanceof Edge && ((Edge) sideKEdge).getSubType() > 7)
                    changeElementEdge(sideKEdge, new SketchEdge());

            } else if (this.edgeHeight > shapeDepth) {
                if (sideCEdge instanceof Edge && ((Edge) sideCEdge).getSubType() <= 7)
                    changeElementEdge(sideCEdge, new SketchEdge());
                if (sideDEdge instanceof Edge && ((Edge) sideDEdge).getSubType() <= 7)
                    changeElementEdge(sideDEdge, new SketchEdge());
                if (sideEEdge instanceof Edge && ((Edge) sideEEdge).getSubType() <= 7)
                    changeElementEdge(sideEEdge, new SketchEdge());
                if (sideFEdge instanceof Edge && ((Edge) sideFEdge).getSubType() <= 7)
                    changeElementEdge(sideFEdge, new SketchEdge());
                if (sideGEdge instanceof Edge && ((Edge) sideGEdge).getSubType() <= 7)
                    changeElementEdge(sideGEdge, new SketchEdge());
                if (sideKEdge instanceof Edge && ((Edge) sideKEdge).getSubType() <= 7)
                    changeElementEdge(sideKEdge, new SketchEdge());
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
                new Point2D(this.points[0].getX() * newScale, this.points[0].getY() * newScale),
                new Point2D(this.points[1].getX() * newScale, this.points[1].getY() * newScale),
                new Point2D(this.points[2].getX() * newScale, this.points[2].getY() * newScale),
                new Point2D(this.points[3].getX() * newScale, this.points[3].getY() * newScale),
                new Point2D(this.points[4].getX() * newScale, this.points[4].getY() * newScale),
                new Point2D(this.points[5].getX() * newScale, this.points[5].getY() * newScale)
        };

        Polygon polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY(),
                points[4].getX(), points[4].getY(),
                points[5].getX(), points[5].getY()
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
                new Point2D(0.0, 30),
                new Point2D(30, 0.0),
                new Point2D(70, 0.0),
                new Point2D(100, 30),
                new Point2D(70, 60),
                new Point2D(30, 60)
        };
        Polygon polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY(),
                points[4].getX(), points[4].getY(),
                points[5].getX(), points[5].getY()
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
//            System.err.println("CANT FILL RECTANGLE SHAPE");
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
        object.put("sizeDReal", sizeDReal);
        object.put("sizeEReal", sizeEReal);
        object.put("material", shapeMaterial.getName());
        object.put("shapeDepth", shapeDepth);
        object.put("edgesHeightsDefault", edgesHeightsDefault);
        object.put("saveMaterialImageOnEdges", saveMaterialImageOnEdges);
        object.put("edgeHeight", edgeHeight);
        object.put("borderHeight", borderHeight);
        object.put("rotateAngle", rotateTransform.getAngle());
        object.put("opacity", this.getOpacity());


        JSONObject sideCEdgeObject = new JSONObject();
        JSONObject sideDEdgeObject = new JSONObject();
        JSONObject sideEEdgeObject = new JSONObject();
        JSONObject sideFEdgeObject = new JSONObject();
        JSONObject sideGEdgeObject = new JSONObject();
        JSONObject sideKEdgeObject = new JSONObject();


        sideCEdgeObject.put("edgeType", (sideCEdge instanceof Edge) ? "edge" : "border");
        sideCEdgeObject.put("name", sideCEdge.getName());
        if (sideCEdge instanceof Border) {
            sideCEdgeObject.put("topCutType", ((Border) sideCEdge).getBorderCutType());
            sideCEdgeObject.put("sideCutType", ((Border) sideCEdge).getBorderSideCutType());
            sideCEdgeObject.put("anglesCutType", ((Border) sideCEdge).getBorderAnglesCutType());
        } else if (sideCEdge instanceof Edge) {
            sideCEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideCEdge).isStoneHemOrLeakGroove());
        }

        sideDEdgeObject.put("edgeType", (sideDEdge instanceof Edge) ? "edge" : "border");
        sideDEdgeObject.put("name", sideDEdge.getName());
        if (sideDEdge instanceof Border) {
            sideDEdgeObject.put("topCutType", ((Border) sideDEdge).getBorderCutType());
            sideDEdgeObject.put("sideCutType", ((Border) sideDEdge).getBorderSideCutType());
            sideDEdgeObject.put("anglesCutType", ((Border) sideDEdge).getBorderAnglesCutType());
        } else if (sideDEdge instanceof Edge) {
            sideDEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideDEdge).isStoneHemOrLeakGroove());
        }

        sideEEdgeObject.put("edgeType", (sideEEdge instanceof Edge) ? "edge" : "border");
        sideEEdgeObject.put("name", sideEEdge.getName());
        if (sideEEdge instanceof Border) {
            sideEEdgeObject.put("topCutType", ((Border) sideEEdge).getBorderCutType());
            sideEEdgeObject.put("sideCutType", ((Border) sideEEdge).getBorderSideCutType());
            sideEEdgeObject.put("anglesCutType", ((Border) sideEEdge).getBorderAnglesCutType());
        } else if (sideEEdge instanceof Edge) {
            sideEEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideEEdge).isStoneHemOrLeakGroove());
        }

        sideFEdgeObject.put("edgeType", (sideFEdge instanceof Edge) ? "edge" : "border");
        sideFEdgeObject.put("name", sideFEdge.getName());
        if (sideFEdge instanceof Border) {
            sideFEdgeObject.put("topCutType", ((Border) sideFEdge).getBorderCutType());
            sideFEdgeObject.put("sideCutType", ((Border) sideFEdge).getBorderSideCutType());
            sideFEdgeObject.put("anglesCutType", ((Border) sideFEdge).getBorderAnglesCutType());
        } else if (sideFEdge instanceof Edge) {
            sideFEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideFEdge).isStoneHemOrLeakGroove());
        }

        sideGEdgeObject.put("edgeType", (sideGEdge instanceof Edge) ? "edge" : "border");
        sideGEdgeObject.put("name", sideGEdge.getName());
        if (sideGEdge instanceof Border) {
            sideGEdgeObject.put("topCutType", ((Border) sideGEdge).getBorderCutType());
            sideGEdgeObject.put("sideCutType", ((Border) sideGEdge).getBorderSideCutType());
            sideGEdgeObject.put("anglesCutType", ((Border) sideGEdge).getBorderAnglesCutType());
        } else if (sideGEdge instanceof Edge) {
            sideGEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideGEdge).isStoneHemOrLeakGroove());
        }

        sideKEdgeObject.put("edgeType", (sideKEdge instanceof Edge) ? "edge" : "border");
        sideKEdgeObject.put("name", sideKEdge.getName());
        if (sideKEdge instanceof Border) {
            sideKEdgeObject.put("topCutType", ((Border) sideKEdge).getBorderCutType());
            sideKEdgeObject.put("sideCutType", ((Border) sideKEdge).getBorderSideCutType());
            sideKEdgeObject.put("anglesCutType", ((Border) sideKEdge).getBorderAnglesCutType());
        } else if (sideKEdge instanceof Edge) {
            sideKEdgeObject.put("stoneHemOrLeakGroove", ((Edge) sideKEdge).isStoneHemOrLeakGroove());
        }

        object.put("sideCEdge", sideCEdgeObject);
        object.put("sideDEdge", sideDEdgeObject);
        object.put("sideEEdge", sideEEdgeObject);
        object.put("sideFEdge", sideFEdgeObject);
        object.put("sideGEdge", sideGEdgeObject);
        object.put("sideKEdge", sideKEdgeObject);

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
        sizeDReal = ((Double) jsonObject.get("sizeDReal")).doubleValue();
        sizeEReal = ((Double) jsonObject.get("sizeEReal")).doubleValue();


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
        JSONObject sideCEdgeObject = ((JSONObject) jsonObject.get("sideCEdge"));
        JSONObject sideDEdgeObject = ((JSONObject) jsonObject.get("sideDEdge"));
        JSONObject sideEEdgeObject = ((JSONObject) jsonObject.get("sideEEdge"));
        JSONObject sideFEdgeObject = ((JSONObject) jsonObject.get("sideFEdge"));
        JSONObject sideGEdgeObject = ((JSONObject) jsonObject.get("sideGEdge"));
        JSONObject sideKEdgeObject = ((JSONObject) jsonObject.get("sideKEdge"));

        int edgeType = 0;
        if (shapeMaterial.getName().indexOf("Акриловый камень") != -1 || shapeMaterial.getName().indexOf("Полиэфирный камень") != -1) {
            edgeType = 1;
        } else if (shapeMaterial.getName().indexOf("Кварцевый агломерат") != -1) {
            edgeType = 2;
        }

        //changeElementEdge(sideAEdge, (sideAEdgeType.equals("edge"))? new Edge(sideAEdgeName, edgeType) : new Border(sideAEdgeName, edgeType) );

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

        String sideDEdgeType = (String) sideDEdgeObject.get("edgeType");
        String sideDEdgeName = (String) sideDEdgeObject.get("name");
        if (sideDEdgeType.equals("edge")) {
            changeElementEdge(sideDEdge, new Edge(sideDEdgeName, edgeType));
            ((Edge) sideDEdge).setStoneHemOrLeakGroove(((Boolean) sideDEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideDEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideDEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideDEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideDEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideDEdge, border);
        }
        //changeElementEdge(sideDEdge, (sideDEdgeType.equals("edge"))? new Edge(sideDEdgeName, edgeType) : new Border(sideDEdgeName, edgeType) );

        String sideEEdgeType = (String) sideEEdgeObject.get("edgeType");
        String sideEEdgeName = (String) sideEEdgeObject.get("name");
        if (sideEEdgeType.equals("edge")) {
            changeElementEdge(sideEEdge, new Edge(sideEEdgeName, edgeType));
            ((Edge) sideEEdge).setStoneHemOrLeakGroove(((Boolean) sideEEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideEEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideEEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideEEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideEEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideEEdge, border);
        }

        String sideFEdgeType = (String) sideEEdgeObject.get("edgeType");
        String sideFEdgeName = (String) sideEEdgeObject.get("name");
        if (sideFEdgeType.equals("edge")) {
            changeElementEdge(sideFEdge, new Edge(sideFEdgeName, edgeType));
            ((Edge) sideFEdge).setStoneHemOrLeakGroove(((Boolean) sideFEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideFEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideFEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideFEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideFEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideFEdge, border);
        }

        String sideGEdgeType = (String) sideGEdgeObject.get("edgeType");
        String sideGEdgeName = (String) sideGEdgeObject.get("name");
        if (sideGEdgeType.equals("edge")) {
            changeElementEdge(sideGEdge, new Edge(sideGEdgeName, edgeType));
            ((Edge) sideGEdge).setStoneHemOrLeakGroove(((Boolean) sideGEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideGEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideGEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideGEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideGEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideGEdge, border);
        }

        String sideKEdgeType = (String) sideKEdgeObject.get("edgeType");
        String sideKEdgeName = (String) sideKEdgeObject.get("name");
        if (sideKEdgeType.equals("edge")) {
            changeElementEdge(sideKEdge, new Edge(sideKEdgeName, edgeType));
            ((Edge) sideKEdge).setStoneHemOrLeakGroove(((Boolean) sideKEdgeObject.get("stoneHemOrLeakGroove")).booleanValue());
        } else {
            Border border = new Border(sideKEdgeName, edgeType);
            if (border.isDefined()) {
                int topCutType = ((Long) sideKEdgeObject.get("topCutType")).intValue();
                int sideCutType = ((Long) sideKEdgeObject.get("sideCutType")).intValue();
                int angleCutType = ((Long) sideKEdgeObject.get("anglesCutType")).intValue();
                border.setBorderCut(topCutType);
                border.setBorderSideCutType(sideCutType);
                border.setBorderAnglesCutType(angleCutType);
            }
            changeElementEdge(sideKEdge, border);
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
        sizeBShape = sizeBReal * commonShapeScale;
        sizeCShape = sizeCReal * commonShapeScale;
        sizeDShape = sizeDReal * commonShapeScale;
        sizeEShape = sizeEReal * commonShapeScale;


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
