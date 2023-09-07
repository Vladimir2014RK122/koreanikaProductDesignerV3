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
import ru.koreanika.utils.ProjectHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class SketchShapeTrapeze extends SketchShape {

    Pane sketchPane;

    Point2D[] points;
    double sizeAShape = 60;
    double sizeBShape = 40;
    double sizeCShape = 0;
    double sizeDShape = 0;
    double sizeEShape = 0;
    double sizeAlphaAngleShape = 75;
    double sizeBettaAngleShape = 75;
    double connectAreaWidth = 0;

    //private Image imageForFill = null;

    //connect points:
    //ArrayList<CornerConnectPoint> connectionPoints = new ArrayList<>();
    //ArrayList<ConnectPoint> cutShapeConnectPoints = new ArrayList<>(Arrays.asList(new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint(), new CornerConnectPoint()));

    CutShapeEdge cutShapeEdgeA;

    CutShapeEdge cutShapeEdgeC;
    CutShapeEdge cutShapeEdgeD;
    CutShapeEdge cutShapeEdgeE;

    //Element edges
    double widthEdge = 5;

    SketchEdge sideAEdge = null;

    SketchEdge sideCEdge = null;
    SketchEdge sideDEdge = null;
    SketchEdge sideEEdge = null;

    Polygon triangleIconSideAEdge;
    Polygon triangleIconSideBEdge;
    Polygon triangleIconSideCEdge;
    Polygon triangleIconSideDEdge;
    Polygon triangleIconSideEEdge;

    Line lineGrooveAEdge;

    Line lineGrooveCEdge;
    Line lineGrooveDEdge;
    Line lineGrooveEEdge;

    //Joints:
    Line lineAJoint = null;

    Line lineCJoint = null;
    Line lineDJoint = null;
    Line lineEJoint = null;

    ArrayList<Joint> sideAJointsList = new ArrayList<>();
    ArrayList<Joint> sideCJointsList = new ArrayList<>();
    ArrayList<Joint> sideDJointsList = new ArrayList<>();
    ArrayList<Joint> sideEJointsList = new ArrayList<>();

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
    double sizeAlphaAngleReal = sizeAlphaAngleShape;
    double sizeBettaAngleReal = sizeBettaAngleShape;

    CheckBox checkBoxMaterialDefault, checkBoxDefaultHeights, checkBoxSaveImage;
    ChoiceBox<String> choiceBoxMaterial;
    ChoiceBox<String> choiceBoxMaterialDepth;
    TextField textFieldASize, textFieldBSize, textFieldCSize, textFieldDSize, textFieldESize;
    TextField textFieldAlphaAngleSize, textFieldBettaAngleSize;
    TextField textFieldX, textFieldY;
    TextField textFieldEdgeHeight, textFieldBorderHeight;

    Group groupEdges;
    Button btnRotateRight, btnRotateLeft;

    Pane paneShapeView;
    Polygon polygonSettingsShape;
    Label labelA, labelB, labelC, labelD, labelE, labelAlpha, labelBetta;

    boolean correctEdgeHeight = true, correctBorderHeight = true, correctX = true, correctY = true;
    boolean correctASize = true, correctBSize = true, correctAlphaAngleSize = true, correctBettaAngleSize = true;


    public SketchShapeTrapeze(double layoutX, double layoutY, ElementTypes elementType, Pane sketchPane) {
        setChildShape(this);

        shapeType = ShapeType.TRAPEZE;
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
                new Point2D(0.0, sizeBShape),
                new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), 0.0),
                new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), 0.0),
                new Point2D(sizeAShape, sizeBShape)
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
        );


        // create ImagePattern
        try {
            FileInputStream input = new FileInputStream(imagePath);
            imageForFill = new Image(input);
        } catch (FileNotFoundException ex) {
            System.err.println("CANT FILL TRAPEZE SHAPE");
        }

        if (USE_IMAGES_FOR_FILL_SHAPES == false || imageForFill == null) {
            polygon.setFill(shapeColor);
            System.err.println("CANT FILL TRAPEZE SHAPE");
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

    public SketchShapeTrapeze(ElementTypes elementType, Material material, int depth, double sizeA, double sizeB, double sizeAlpha, double sizeBetta) {

        this.sizeAlphaAngleReal = sizeAlpha;
        this.sizeBettaAngleReal = sizeBetta;
        this.sizeAlphaAngleShape = sizeAlpha;
        this.sizeBettaAngleShape = sizeBetta;

        this.sizeAReal = sizeA;
        this.sizeBReal = sizeB;
        this.sizeCReal = Math.sqrt((sizeBReal / Math.tan(Math.toRadians(sizeAlpha))) + Math.pow(sizeBReal, 2));
        this.sizeDReal = sizeBReal -
                ((sizeBReal / Math.tan(Math.toRadians(sizeAlpha))) + (sizeBReal / Math.tan(Math.toRadians(sizeBetta))));
        this.sizeEReal = Math.sqrt((sizeBReal / Math.tan(Math.toRadians(sizeAlpha))) + Math.pow(sizeBReal, 2));


        sizeAShape = sizeAReal * commonShapeScale;
        sizeBShape = sizeBReal * commonShapeScale;
        sizeCShape = sizeCReal * commonShapeScale;
        sizeDShape = sizeDReal * commonShapeScale;
        sizeEShape = sizeEReal * commonShapeScale;

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
                new Point2D(0.0, sizeBShape),
                new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), 0.0),
                new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), 0.0),
                new Point2D(sizeAShape, sizeBShape)
        };

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
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
//            System.err.println("CANT FILL RECTANGLE SHAPE");
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
            for (int i = 0; i < 4; i++) {
                connectionPoints.add(new CornerConnectPoint(this));
            }
        }


        connectionPoints.get(0).setTranslateX(-(widthConnectPoint / 2));
        connectionPoints.get(0).setTranslateY(sizeBShape - (widthConnectPoint / 2));
        connectionPoints.get(0).hide();

        connectionPoints.get(1).setTranslateX(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))) -
                (widthConnectPoint / 2));
        connectionPoints.get(1).setTranslateY(-(widthConnectPoint / 2));
        connectionPoints.get(1).hide();

        connectionPoints.get(2).setTranslateX(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))) -
                (widthConnectPoint / 2));
        connectionPoints.get(2).setTranslateY(-(widthConnectPoint / 2));
        connectionPoints.get(2).hide();

        connectionPoints.get(3).setTranslateX(sizeAShape - (widthConnectPoint / 2));
        connectionPoints.get(3).setTranslateY(sizeBShape - (widthConnectPoint / 2));
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

        sideCEdge.setDisable(edgesDisable);
        sideDEdge.setDisable(edgesDisable);
        sideEEdge.setDisable(edgesDisable);
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
            //System.out.println("CREATE CUT SHAPE TRAPEZE " + thisShapeNumber);
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

        System.out.println("sizeAlphaAngleShape = " + sizeAlphaAngleShape);
        System.out.println("sizeBettaAngleShape = " + sizeBettaAngleShape);
        //create Cut zone polygon
        ArrayList<Point2D> cutZonePolygonPoints = null;
        Polygon cutZonePolygon = null;
        {
            Point2D p1 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);
            Point2D p2 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape);

            Point2D pivot = new Point2D(0.0, sizeBShape);
            Point2D p3 = SketchShape.rotatePoint(p2,pivot, 90 - sizeAlphaAngleShape);

            Point2D p5 = new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);
            pivot = new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), 0.0);
            Point2D p4 = SketchShape.rotatePoint(p5,pivot, -sizeAlphaAngleShape);

            Point2D p6 = new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);
            pivot = new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), 0.0);
            Point2D p7 = SketchShape.rotatePoint(p6,pivot, sizeBettaAngleShape);

            Point2D p9 = new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape);
            pivot = new Point2D(sizeAShape, sizeBShape);
            Point2D p8 = SketchShape.rotatePoint(p9,pivot, -90+sizeBettaAngleShape);

            Point2D p10 = new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);

            cutZonePolygonPoints = new ArrayList<>();
            cutZonePolygonPoints.addAll(Arrays.asList(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10));

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
            polygonPoints.add(new Point2D(0.0, sizeBShape));
            polygonPoints.add(new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), 0.0));
            polygonPoints.add(new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), 0.0));
            polygonPoints.add(new Point2D(sizeAShape, sizeBShape));

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

        cutShapeConnectPoints.get(0).changeSetPoint(polygonPoints.get(0));
        cutShapeConnectPoints.get(1).changeSetPoint(polygonPoints.get(1));
        cutShapeConnectPoints.get(2).changeSetPoint(polygonPoints.get(2));
        cutShapeConnectPoints.get(3).changeSetPoint(polygonPoints.get(3));

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(cutZonePolygonPoints.get(0));
            cutShapeConnectPoints.get(1).changeSetPointShift(cutZonePolygonPoints.get(4));
            cutShapeConnectPoints.get(2).changeSetPointShift(cutZonePolygonPoints.get(5));
            cutShapeConnectPoints.get(3).changeSetPointShift(cutZonePolygonPoints.get(9));

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
        dimensionVLabel.setTranslateX(polygonPoints.get(1).getX());
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

        dimensionHLabel.setAlignment(Pos.CENTER);
        dimensionHLabel.setPrefWidth(sizeH);
        dimensionHLabel.setTranslateX(0.0);
        dimensionHLabel.setTranslateY(sizeBShape - 11);
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
        createCCutEdge();
        createDCutEdge();
        createECutEdge();

        cutShapesEdgesList.clear();
        cutShapesEdgesList.addAll(Arrays.asList(cutShapeEdgeA, cutShapeEdgeC, cutShapeEdgeD, cutShapeEdgeE));
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

    private void createCCutEdge() {

        cutShapeEdgeC = new CutShapeEdge(cutShape);

        ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
        Polygon edgePolygon = new Polygon();
        double h = edgeHeight * commonShapeScale;
        if (sideCEdge instanceof Border) {
            h = borderHeight * commonShapeScale;
        }


        pointsForEdge.add(new Point2D(0.0, sizeBShape));
        pointsForEdge.add(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), 0.0));
        pointsForEdge.add(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)) - h, 0.0));
        pointsForEdge.add(new Point2D(-h, sizeBShape));

        // ROTATE TWO POINTS:
        double angle = 90 - sizeAlphaAngleShape;
        Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
        Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

        pointsForEdge.set(2, p2);
        pointsForEdge.set(3, p3);

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

        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(sizeAShape - sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)) -
                sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), 0.0));
        pointsForEdge.add(new Point2D(sizeAShape - sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)) -
                sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), h));
        pointsForEdge.add(new Point2D(0.0, h));

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
            cutShapeEdgeD.setStartCoordinate(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), -h));
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
        pointsForEdge.add(new Point2D(0.0, 0.0));
        pointsForEdge.add(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)), sizeBShape));
        pointsForEdge.add(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)) + h, sizeBShape));
        pointsForEdge.add(new Point2D(h, 0.0));

        //ROTATE TWO POINTS
        double angle = sizeBettaAngleShape;
        Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), -90 + angle);
        Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), -90 + angle);

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
            cutShapeEdgeE.setStartCoordinate(new Point2D(sizeAShape -
                    sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), 0.0));
        } else {
            cutShapeEdgeE.setStartCoordinate(null);
        }
    }

    private void updateCutShapeView() {

        cutShape.setSizesInfo("ШхВ "+ (int)(sizeAShape/ProjectHandler.getCommonShapeScale()) +
                "x" + (int)(sizeBShape/ProjectHandler.getCommonShapeScale()));

        //create Cut zone polygon
        ArrayList<Point2D> cutZonePolygonPoints = null;
        Polygon cutZonePolygon = null;
        {
            Point2D p1 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);
            Point2D p2 = new Point2D(-1 * CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape);

            Point2D pivot = new Point2D(0.0, sizeBShape);
            Point2D p3 = SketchShape.rotatePoint(p2,pivot, 90 - sizeAlphaAngleShape);

            Point2D p5 = new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);
            pivot = new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), 0.0);
            Point2D p4 = SketchShape.rotatePoint(p5,pivot, -sizeAlphaAngleShape);

            Point2D p6 = new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), -1 * CutDesigner.CUT_SHAPES_CUTSHIFT);
            pivot = new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), 0.0);
            Point2D p7 = SketchShape.rotatePoint(p6,pivot, sizeBettaAngleShape);

            Point2D p9 = new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape);
            pivot = new Point2D(sizeAShape, sizeBShape);
            Point2D p8 = SketchShape.rotatePoint(p9,pivot, -90+sizeBettaAngleShape);

            Point2D p10 = new Point2D(sizeAShape + CutDesigner.CUT_SHAPES_CUTSHIFT, sizeBShape + CutDesigner.CUT_SHAPES_CUTSHIFT);

            cutZonePolygonPoints = new ArrayList<>();
            cutZonePolygonPoints.addAll(Arrays.asList(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10));

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
            polygonPoints.add(new Point2D(0.0, sizeBShape));
            polygonPoints.add(new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), 0.0));
            polygonPoints.add(new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), 0.0));
            polygonPoints.add(new Point2D(sizeAShape, sizeBShape));

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

        //setCutZone points:
        {
            cutShapeConnectPoints.get(0).changeSetPointShift(cutZonePolygonPoints.get(0));
            cutShapeConnectPoints.get(1).changeSetPointShift(cutZonePolygonPoints.get(4));
            cutShapeConnectPoints.get(2).changeSetPointShift(cutZonePolygonPoints.get(5));
            cutShapeConnectPoints.get(3).changeSetPointShift(cutZonePolygonPoints.get(9));

        }

        //Update dimensions labels:
        double sizeH = sizeAShape;
        double sizeY = sizeBShape;

        //cutShape.getDimensionVLabel().setRotate(0);
        cutShape.getDimensionVLabel().setPrefWidth(sizeY);
        cutShape.getDimensionVLabel().setTranslateX(polygonPoints.get(1).getX());
        cutShape.getDimensionVLabel().setTranslateY(sizeY);
        cutShape.getDimensionVLabel().setText(String.format("%.0f", sizeY / ProjectHandler.getCommonShapeScale()));
        cutShape.getDimensionVLabel().toFront();


        double shiftX = 2;
        double shiftY = sizeY - 5;

        //cutShape.getDimensionHLabel().setRotate(0);
        cutShape.getDimensionHLabel().setPrefWidth(sizeH);
        cutShape.getDimensionHLabel().setTranslateX(0.0);
        cutShape.getDimensionHLabel().setTranslateY(sizeBShape - 11);
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

        } else {
            updateACutEdge();
            updateCCutEdge();
            updateDCutEdge();
            updateECutEdge();
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

    private void updateCCutEdge() {

        if (sideCEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideCEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, sizeBShape));
            pointsForEdge.add(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), 0.0));
            pointsForEdge.add(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)) - h, 0.0));
            pointsForEdge.add(new Point2D(-h, sizeBShape));

            // ROTATE TWO POINTS:
            double angle = 90 - sizeAlphaAngleShape;
            Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), angle);
            Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), angle);

            pointsForEdge.set(2, p2);
            pointsForEdge.set(3, p3);

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

    private void updateDCutEdge() {

        if (sideDEdge.isDefined()) {

            double h = edgeHeight * commonShapeScale;
            if (sideDEdge instanceof Border) {
                h = borderHeight * commonShapeScale;
            }

            ArrayList<Point2D> pointsForEdge = new ArrayList<>(4);
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(sizeAShape - sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)) -
                    sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), 0.0));
            pointsForEdge.add(new Point2D(sizeAShape - sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)) -
                    sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), h));
            pointsForEdge.add(new Point2D(0.0, h));

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

            cutShapeEdgeD.setStartCoordinate(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), -h));
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
            pointsForEdge.add(new Point2D(0.0, 0.0));
            pointsForEdge.add(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)), sizeBShape));
            pointsForEdge.add(new Point2D(sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)) + h, sizeBShape));
            pointsForEdge.add(new Point2D(h, 0.0));

            //ROTATE TWO POINTS
            double angle = sizeBettaAngleShape;
            Point2D p2 = SketchShape.rotatePoint(pointsForEdge.get(2), pointsForEdge.get(1), -90 + angle);
            Point2D p3 = SketchShape.rotatePoint(pointsForEdge.get(3), pointsForEdge.get(0), -90 + angle);

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

            cutShapeEdgeE.setStartCoordinate(new Point2D(sizeAShape -
                    sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), 0.0));
            if (saveMaterialImageOnEdges) {
                cutShapeEdgeE.setTranslateX(cutShape.getTranslateX() + cutShapeEdgeE.getStartCoordinate().getX());
                cutShapeEdgeE.setTranslateY(cutShape.getTranslateY() + cutShapeEdgeE.getStartCoordinate().getY());
            }

        } else {
            cutShapeEdgeE.setStartCoordinate(null);
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
        fxmlLoader.setLocation(getClass().getResource("/fxmls/sketchShapeTrapezeSettings.fxml"));
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
        textFieldESize = (TextField) settingsRootAnchorPane.lookup("#textFieldESize");
        textFieldAlphaAngleSize = (TextField) settingsRootAnchorPane.lookup("#textFieldAlphaAngleSize");
        textFieldBettaAngleSize = (TextField) settingsRootAnchorPane.lookup("#textFieldBettaAngleSize");
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


        double b = 60;
        double a = 90;
        double alpha = 75;
        double betta = 75;

        double v = b / (Math.tan(Math.toRadians(alpha)));

        Point2D[] points = new Point2D[]{
                new Point2D(0.0, b),
                new Point2D(v, 0.0),
                new Point2D(a - b / (Math.tan(Math.toRadians(betta))), 0.0),
                new Point2D(a, b)
        };
        polygonSettingsShape = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
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
        labelA.setTranslateY(90);

        labelB = new Label("B");
        labelB.setAlignment(Pos.CENTER);
        labelB.setPrefSize(30.0, 30.0);
        labelB.setTranslateX(100);
        labelB.setTranslateY(50);

        labelC = new Label("C");
        labelC.setAlignment(Pos.CENTER);
        labelC.setPrefSize(30.0, 30.0);
        labelC.setTranslateX(55);
        labelC.setTranslateY(45);

        labelD = new Label("D");
        labelD.setAlignment(Pos.CENTER);
        labelD.setPrefSize(30.0, 30.0);
        labelD.setTranslateX(100);
        labelD.setTranslateY(10);

        labelE = new Label("E");
        labelE.setAlignment(Pos.CENTER);
        labelE.setPrefSize(30.0, 30.0);
        labelE.setTranslateX(145);
        labelE.setTranslateY(45);

        labelAlpha = new Label("α");
        labelAlpha.setAlignment(Pos.CENTER);
        labelAlpha.setPrefSize(30.0, 30.0);
        labelAlpha.setTranslateX(65);
        labelAlpha.setTranslateY(69);

        labelBetta = new Label("β");
        labelBetta.setAlignment(Pos.CENTER);
        labelBetta.setPrefSize(30.0, 30.0);
        labelBetta.setTranslateX(135);
        labelBetta.setTranslateY(69);

        paneShapeView.getChildren().addAll(labelA, labelB, labelC, labelD, labelE, labelAlpha, labelBetta);

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
        textFieldAlphaAngleSize.setText(String.format("%.0f", sizeAlphaAngleReal));
        textFieldBettaAngleSize.setText(String.format("%.0f", sizeBettaAngleReal));
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
            double a, b, c, d, e, alpha, betta;
            try {
                a = Double.parseDouble(textFieldASize.getText());
                b = Double.parseDouble(textFieldBSize.getText());
                alpha = Double.parseDouble(textFieldAlphaAngleSize.getText());
                betta = Double.parseDouble(textFieldBettaAngleSize.getText());

                double a1 = b / Math.tan(Math.toRadians(alpha));
                double a2 = b / Math.tan(Math.toRadians(betta));
                c = Math.sqrt(Math.pow(b, 2) + Math.pow(a1, 2));
                e = Math.sqrt(Math.pow(b, 2) + Math.pow(a2, 2));
                d = a - a1 - a2;


                textFieldCSize.setText(String.format(Locale.ENGLISH, "%.0f", c));
                textFieldESize.setText(String.format(Locale.ENGLISH, "%.0f", e));
                textFieldDSize.setText(String.format(Locale.ENGLISH, "%.0f", d));

                correctASize = true;
                textFieldASize.setStyle("-fx-text-fill: #B3B4B4");
                if (a1 + a2 > a) {
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
            double a, b, c, d, e, alpha, betta;
            try {
                a = Double.parseDouble(textFieldASize.getText());
                b = Double.parseDouble(textFieldBSize.getText());
                alpha = Double.parseDouble(textFieldAlphaAngleSize.getText());
                betta = Double.parseDouble(textFieldBettaAngleSize.getText());

                double a1 = b / Math.tan(Math.toRadians(alpha));
                double a2 = b / Math.tan(Math.toRadians(betta));
                c = Math.sqrt(Math.pow(b, 2) + Math.pow(a1, 2));
                e = Math.sqrt(Math.pow(b, 2) + Math.pow(a2, 2));
                d = a - a1 - a2;


                textFieldCSize.setText(String.format(Locale.ENGLISH, "%.0f", c));
                textFieldESize.setText(String.format(Locale.ENGLISH, "%.0f", e));
                textFieldDSize.setText(String.format(Locale.ENGLISH, "%.0f", d));

                correctBSize = true;
                textFieldBSize.setStyle("-fx-text-fill: #B3B4B4");
                if (a1 + a2 > a) {
                    correctBSize = false;
                    textFieldBSize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctBSize = false;
                textFieldBSize.setStyle("-fx-text-fill: red");
            }
        });

        textFieldAlphaAngleSize.textProperty().addListener(observable -> {
            //check that it correct
            double a, b, c, d, e, alpha, betta;
            try {
                a = Double.parseDouble(textFieldASize.getText());
                b = Double.parseDouble(textFieldBSize.getText());
                alpha = Double.parseDouble(textFieldAlphaAngleSize.getText());
                betta = Double.parseDouble(textFieldBettaAngleSize.getText());

                double a1 = b / Math.tan(Math.toRadians(alpha));
                double a2 = b / Math.tan(Math.toRadians(betta));
                c = Math.sqrt(Math.pow(b, 2) + Math.pow(a1, 2));
                e = Math.sqrt(Math.pow(b, 2) + Math.pow(a2, 2));
                d = a - a1 - a2;

                textFieldCSize.setText(String.format(Locale.ENGLISH, "%.0f", c));
                textFieldESize.setText(String.format(Locale.ENGLISH, "%.0f", e));
                textFieldDSize.setText(String.format(Locale.ENGLISH, "%.0f", d));

                correctAlphaAngleSize = true;
                textFieldAlphaAngleSize.setStyle("-fx-text-fill: #B3B4B4");
                if (a1 + a2 > a || alpha > 170 || betta > 170) {
                    correctAlphaAngleSize = false;
                    textFieldAlphaAngleSize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctAlphaAngleSize = false;
                textFieldAlphaAngleSize.setStyle("-fx-text-fill: red");
            }
        });

        textFieldBettaAngleSize.textProperty().addListener(observable -> {
            //check that it correct
            double a, b, c, d, e, alpha, betta;
            try {
                a = Double.parseDouble(textFieldASize.getText());
                b = Double.parseDouble(textFieldBSize.getText());
                alpha = Double.parseDouble(textFieldAlphaAngleSize.getText());
                betta = Double.parseDouble(textFieldBettaAngleSize.getText());

                double a1 = b / Math.tan(Math.toRadians(alpha));
                double a2 = b / Math.tan(Math.toRadians(betta));
                c = Math.sqrt(Math.pow(b, 2) + Math.pow(a1, 2));
                e = Math.sqrt(Math.pow(b, 2) + Math.pow(a2, 2));
                d = a - a1 - a2;

                textFieldCSize.setText(String.format(Locale.ENGLISH, "%.0f", c));
                textFieldESize.setText(String.format(Locale.ENGLISH, "%.0f", e));
                textFieldDSize.setText(String.format(Locale.ENGLISH, "%.0f", d));

                correctBettaAngleSize = true;
                textFieldBettaAngleSize.setStyle("-fx-text-fill: #B3B4B4");
                if (a1 + a2 > a || alpha > 170 || betta > 170) {
                    correctBettaAngleSize = false;
                    textFieldBettaAngleSize.setStyle("-fx-text-fill: red");
                }
            } catch (NumberFormatException ex) {
                correctBettaAngleSize = false;
                textFieldBettaAngleSize.setStyle("-fx-text-fill: red");
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
        textFieldBSize.setText(String.format("%.0f", sizeBReal));
        textFieldCSize.setText(String.format("%.0f", sizeCReal));
        textFieldDSize.setText(String.format("%.0f", sizeDReal));
        textFieldAlphaAngleSize.setText(String.format("%.0f", sizeAlphaAngleReal));
        textFieldBettaAngleSize.setText(String.format("%.0f", sizeBettaAngleReal));

        textFieldX.setText(String.format("%.0f", getTranslateX()));
        textFieldY.setText(String.format("%.0f", getTranslateY()));

        textFieldEdgeHeight.setText(String.valueOf(edgeHeight));
        textFieldBorderHeight.setText(String.valueOf(borderHeight));

    }

    @Override
    public void shapeSettingsSaveBtnClicked() {
        System.out.println("SAVE");
        if ((!correctBorderHeight) || (!correctEdgeHeight) || (!correctASize) || (!correctBSize) ||
                (!correctAlphaAngleSize) || (!correctBettaAngleSize) || (!correctX) || (!correctY)) {
            InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Проверьте введенные данные!", null);
            return;
        }

        saveMaterialImageOnEdges = checkBoxSaveImage.isSelected();

        sizeAReal = Integer.parseInt(textFieldASize.getText());
        sizeBReal = Integer.parseInt(textFieldBSize.getText());
//        sizeCReal = Integer.parseInt(textFieldCSize.getText());
//        sizeDReal = Integer.parseInt(textFieldDSize.getText());
        sizeAlphaAngleReal = Integer.parseInt(textFieldAlphaAngleSize.getText());
        sizeBettaAngleReal = Integer.parseInt(textFieldBettaAngleSize.getText());


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
            return;
        } else {
            sizeAShape = sizeAReal * commonShapeScale;
            sizeBShape = sizeBReal * commonShapeScale;
            //System.out.println("WIDTH 3 = " + sizeAReal);
        }

        sizeAShape = sizeAReal * commonShapeScale;
        sizeBShape = sizeBReal * commonShapeScale;
//        sizeCShape = sizeCReal*commonShapeScale;
//        sizeDShape = sizeDReal*commonShapeScale;
        sizeAlphaAngleShape = sizeAlphaAngleReal;
        sizeBettaAngleShape = sizeBettaAngleReal;


        textFieldASize.setText(String.format("%.0f", sizeAReal));
        textFieldBSize.setText(String.format("%.0f", sizeBReal));
        textFieldCSize.setText(String.format("%.0f", sizeCReal));
        textFieldDSize.setText(String.format("%.0f", sizeDReal));
        textFieldAlphaAngleSize.setText(String.format("%.0f", sizeAlphaAngleReal));
        textFieldBettaAngleSize.setText(String.format("%.0f", sizeBettaAngleReal));


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
                new Point2D(0.0, sizeBShape),
                new Point2D(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))), 0.0),
                new Point2D(sizeAShape - sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))), 0.0),
                new Point2D(sizeAShape, sizeBShape)
        };

        System.out.println(sizeAlphaAngleShape);
        System.out.println(sizeBettaAngleShape);

        System.out.println(sizeBShape / (Math.tan(Math.toRadians(sizeAlphaAngleShape))));
        System.out.println(sizeBShape / (Math.tan(Math.toRadians(sizeBettaAngleShape))));

        polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
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
                0.0, sizeBShape,
                sizeAShape, sizeBShape,
                sizeAShape, sizeBShape - widthEdge,
                0.0, sizeBShape - widthEdge
        );
        getChildren().remove(sideAEdge);
        getChildren().add(sideAEdge);
        sideAEdge.setOnMouseClicked(event -> edgeManagerShow(sideAEdge));

        if (sideCEdge == null) sideCEdge = new SketchEdge();
        sideCEdge.getPoints().clear();
        sideCEdge.getPoints().addAll(
                0.0, sizeBShape,
                sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)), 0.0,
                sizeBShape / Math.tan(Math.toRadians(sizeAlphaAngleShape)) + widthEdge, 0.0,
                widthEdge, sizeBShape
        );
        getChildren().remove(sideCEdge);
        getChildren().add(sideCEdge);
        sideCEdge.setOnMouseClicked(event -> edgeManagerShow(sideCEdge));

        if (sideDEdge == null) sideDEdge = new SketchEdge();
        sideDEdge.getPoints().clear();
        sideDEdge.getPoints().addAll(
                sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)), 0.0,
                sizeAShape - sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)), 0.0,
                sizeAShape - sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)), widthEdge,
                sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)), widthEdge
        );
        getChildren().remove(sideDEdge);
        getChildren().add(sideDEdge);
        sideDEdge.setOnMouseClicked(event -> edgeManagerShow(sideDEdge));

        if (sideEEdge == null) sideEEdge = new SketchEdge();
        sideEEdge.getPoints().clear();
        sideEEdge.getPoints().addAll(
                sizeAShape - sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)), 0.0,
                sizeAShape, sizeBShape,
                sizeAShape - widthEdge, sizeBShape,
                sizeAShape - sizeBShape / Math.tan(Math.toRadians(sizeBettaAngleShape)) - widthEdge, 0.0
        );
        getChildren().remove(sideEEdge);
        getChildren().add(sideEEdge);
        sideEEdge.setOnMouseClicked(event -> edgeManagerShow(sideEEdge));


        sideAEdge.setSketchEdgeOwner(this);
        sideCEdge.setSketchEdgeOwner(this);
        sideDEdge.setSketchEdgeOwner(this);
        sideEEdge.setSketchEdgeOwner(this);
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

        }

        //initEdgesZones();
        refreshEdgeView();
        //refreshShapeSettings();
        //rotateEdgesIcons(rotateAngle);
    }

    @Override
    public ArrayList<SketchEdge> getEdges() {
        return new ArrayList<SketchEdge>(Arrays.asList(sideAEdge, sideCEdge, sideDEdge, sideEEdge));
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
        getChildren().remove(triangleIconSideCEdge);
        getChildren().remove(triangleIconSideDEdge);
        getChildren().remove(triangleIconSideEEdge);

        getChildren().remove(lineGrooveAEdge);
        getChildren().remove(lineGrooveCEdge);
        getChildren().remove(lineGrooveDEdge);
        getChildren().remove(lineGrooveEEdge);

        double translateX = 0;
        double translateY = 0;


        if (sideAEdge instanceof Edge) {
            triangleIconSideAEdge = new Polygon(
                    0.0, 0.0,
                    2.5, -5.0,
                    -2.5, -5.0);
            translateX = sizeAShape / 2;
            translateY = sizeBShape;
        } else {
            triangleIconSideAEdge = new Polygon(
                    -2.5, 0.0,
                    2.5, -5.0,
                    -2.5, 0.0,
                    -2.5, -5.0,
                    2.5, 0.0,
                    0.0, -2.5);
            translateX = sizeAShape / 2;
            translateY = sizeBShape;
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
                lineGrooveAEdge = new Line(0, sizeBShape - 5, sizeAShape, sizeBShape - 5);
                lineGrooveAEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveAEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveAEdge);
                lineGrooveAEdge.setVisible((true));
            }

        } else {
            triangleIconSideAEdge.setVisible(false);
        }


        if (sideCEdge instanceof Edge) {
            triangleIconSideCEdge = new Polygon(
                    0.0, 0.0,
                    2.5, 5.0,
                    -2.5, 5.0);
            translateX = (points[0].getX() + points[1].getX()) / 2;
            translateY = (points[0].getY() + points[1].getY()) / 2;
        } else {
            triangleIconSideCEdge = new Polygon(
                    -2.5, 0.0,
                    2.5, 5.0,
                    0.0, 2.5,
                    2.5, 0.0,
                    -2.5, 5.0,
                    0.0, 2.5);
            translateX = (points[0].getX() + points[1].getX()) / 2;
            translateY = (points[0].getY() + points[1].getY()) / 2;

            triangleIconSideCEdge.setStroke(Color.BLACK);
        }
        SketchObject.rotatePolygon(triangleIconSideCEdge, new Point2D(0.0, 0.0), -sizeAlphaAngleShape);
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
                lineGrooveCEdge = new Line(5, sizeBShape, points[1].getX() + 5, 0.0);
                lineGrooveCEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveCEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveCEdge);
                lineGrooveCEdge.setVisible((true));
            }

        } else {
            triangleIconSideCEdge.setVisible(false);
        }


        if (sideDEdge instanceof Edge) {
            triangleIconSideDEdge = new Polygon(
                    0.0, 0.0,
                    2.5, 5.0,
                    -2.5, 5.0);
            translateX = (points[2].getX() + points[1].getX()) / 2;
            translateY = 0.0;
        } else {
            triangleIconSideDEdge = new Polygon(
                    -2.5, 0.0,
                    2.5, 5.0,
                    0.0, 2.5,
                    2.5, 0.0,
                    -2.5, 5.0,
                    0.0, 2.5);
            translateX = (points[2].getX() - points[1].getX()) / 2;
            translateY = 0.0;
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
                lineGrooveDEdge = new Line(points[1].getX(), 5, points[2].getX(), 5);
                lineGrooveDEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveDEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveDEdge);
                lineGrooveDEdge.setVisible((true));
            }

        } else {
            triangleIconSideDEdge.setVisible(false);
        }


        double angle = Math.toDegrees(Math.atan((sizeBShape - sizeCShape) / (sizeAShape - sizeDShape)));
        if (sideEEdge instanceof Edge) {
            triangleIconSideEEdge = new Polygon(
                    0.0, 0.0,
                    2.5, 5.0,
                    -2.5, 5.0);
            translateX = (points[2].getX() + points[3].getX()) / 2;
            translateY = (points[2].getY() + points[3].getY()) / 2;
        } else {
            triangleIconSideEEdge = new Polygon(
                    -2.5, 0.0,
                    2.5, 5.0,
                    0.0, 2.5,
                    2.5, 0.0,
                    -2.5, 5.0,
                    0.0, 2.5);
            translateX = (points[2].getX() + points[3].getX()) / 2;
            translateY = (points[2].getY() + points[3].getY()) / 2;
            triangleIconSideEEdge.setStroke(Color.BLACK);
        }
        SketchObject.rotatePolygon(triangleIconSideEEdge, new Point2D(0.0, 0.0), sizeBettaAngleShape);
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
                lineGrooveEEdge = new Line(points[2].getX() - 5, 0.0, points[3].getX() - 5, sizeBShape);
                lineGrooveEEdge.setStrokeLineCap(StrokeLineCap.BUTT);
                lineGrooveEEdge.getStrokeDashArray().addAll(5.0, 3.0);
                getChildren().add(lineGrooveEEdge);
                lineGrooveEEdge.setVisible((true));
            }

        } else {
            triangleIconSideEEdge.setVisible(false);
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
        else if (edge == sideCEdge) {
            sizeCReal = Math.sqrt(Math.pow(sizeBReal / (Math.tan(Math.toRadians(sizeAlphaAngleReal))), 2) +
                    Math.pow(sizeBReal, 2));
            return sizeCReal;
        } else if (edge == sideDEdge) {
            sizeDReal = sizeAReal - sizeBReal / (Math.tan(Math.toRadians(sizeAlphaAngleReal))) -
                    sizeBReal / (Math.tan(Math.toRadians(sizeBettaAngleReal)));
            return sizeDReal;
        } else if (edge == sideEEdge) {
            sizeEReal = Math.sqrt(Math.pow(sizeBReal / (Math.tan(Math.toRadians(sizeBettaAngleReal))), 2) +
                    Math.pow(sizeBReal, 2));
            return sizeEReal;
        }

        return 0;
    }

    @Override
    public double getEdgesLength() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Edge)) length += sizeAReal;
        if (sideCEdge != null && (sideCEdge instanceof Edge)) {
            sizeCReal = Math.sqrt(Math.pow(sizeBReal / (Math.tan(Math.toRadians(sizeAlphaAngleReal))), 2) +
                    Math.pow(sizeBReal, 2));
            length += sizeCReal;
        }
        if (sideDEdge != null && (sideDEdge instanceof Edge)) {
            sizeDReal = sizeAReal - sizeBReal / (Math.tan(Math.toRadians(sizeAlphaAngleReal))) -
                    sizeBReal / (Math.tan(Math.toRadians(sizeBettaAngleReal)));
            length += sizeDReal;
        }
        if (sideEEdge != null && (sideEEdge instanceof Edge)) {
            sizeEReal = Math.sqrt(Math.pow(sizeBReal / (Math.tan(Math.toRadians(sizeBettaAngleReal))), 2) +
                    Math.pow(sizeBReal, 2));
            length += sizeEReal;
        }
        return length;

    }

    @Override
    public double getBordersType1Length() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Border) && sideAEdge.getName().indexOf("1") != -1)
            length += sizeAReal;
        if (sideCEdge != null && (sideCEdge instanceof Border) && sideCEdge.getName().indexOf("1") != -1) {
            sizeCReal = Math.sqrt(Math.pow(sizeBReal / (Math.tan(Math.toRadians(sizeAlphaAngleReal))), 2) +
                    Math.pow(sizeBReal, 2));
            length += sizeCReal;
        }
        if (sideDEdge != null && (sideDEdge instanceof Border) && sideDEdge.getName().indexOf("1") != -1) {
            sizeDReal = sizeAReal - sizeBReal / (Math.tan(Math.toRadians(sizeAlphaAngleReal))) -
                    sizeBReal / (Math.tan(Math.toRadians(sizeBettaAngleReal)));
            length += sizeDReal;
        }
        if (sideEEdge != null && (sideEEdge instanceof Border) && sideEEdge.getName().indexOf("1") != -1) {
            sizeEReal = Math.sqrt(Math.pow(sizeBReal / (Math.tan(Math.toRadians(sizeBettaAngleReal))), 2) +
                    Math.pow(sizeBReal, 2));
            length += sizeEReal;
        }
        return length;
    }

    @Override
    public double getBordersType2Length() {

        double length = 0;
        if (sideAEdge != null && (sideAEdge instanceof Border) && sideAEdge.getName().indexOf("2") != -1) {
            length += sizeAReal;
        }
        if (sideCEdge != null && (sideCEdge instanceof Border) && sideCEdge.getName().indexOf("2") != -1) {
            sizeCReal = Math.sqrt(Math.pow(sizeBReal / (Math.tan(Math.toRadians(sizeAlphaAngleReal))), 2) +
                    Math.pow(sizeBReal, 2));
            length += sizeCReal;
        }
        if (sideDEdge != null && (sideDEdge instanceof Border) && sideDEdge.getName().indexOf("2") != -1) {
            sizeDReal = sizeAReal - sizeBReal / (Math.tan(Math.toRadians(sizeAlphaAngleReal))) -
                    sizeBReal / (Math.tan(Math.toRadians(sizeBettaAngleReal)));
            length += sizeDReal;
        }
        if (sideEEdge != null && (sideEEdge instanceof Border) && sideEEdge.getName().indexOf("2") != -1) {
            sizeEReal = Math.sqrt(Math.pow(sizeBReal / (Math.tan(Math.toRadians(sizeBettaAngleReal))), 2) +
                    Math.pow(sizeBReal, 2));
            length += sizeEReal;
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
        edgesIcons.add(triangleIconSideAEdge);
        edgesIcons.add(triangleIconSideCEdge);
        edgesIcons.add(triangleIconSideDEdge);
        edgesIcons.add(triangleIconSideEEdge);
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

        double newAlphaX = ((labelAlpha.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelAlpha.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newAlphaY = ((labelAlpha.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelAlpha.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelAlpha.setTranslateX(newAlphaX - 15);
        labelAlpha.setTranslateY(newAlphaY - 15);

        double newBettaX = ((labelBetta.getTranslateX() + 15) - pivot.getX()) * Math.cos(Math.toRadians(angle)) - ((labelBetta.getTranslateY() + 15) - pivot.getY()) * Math.sin(Math.toRadians(angle)) + pivot.getX();
        double newBettaY = ((labelBetta.getTranslateX() + 15) - pivot.getX()) * Math.sin(Math.toRadians(angle)) + ((labelBetta.getTranslateY() + 15) - pivot.getY()) * Math.cos(Math.toRadians(angle)) + pivot.getY();
        labelBetta.setTranslateX(newBettaX - 15);
        labelBetta.setTranslateY(newBettaY - 15);

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
        sideCJointsList.clear();
        sideDJointsList.clear();
        sideEJointsList.clear();
    }

    @Override
    public void refreshLinesForJoints() {
        Point2D point1;
        Point2D point2;

        point1 = new Point2D(points[0].getX(), points[0].getY());
        point2 = new Point2D(points[1].getX(), points[1].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineAJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());


        point1 = new Point2D(points[1].getX(), points[1].getY());
        point2 = new Point2D(points[2].getX(), points[2].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineCJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(points[2].getX(), points[2].getY());
        point2 = new Point2D(points[3].getX(), points[3].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineDJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());

        point1 = new Point2D(points[3].getX(), points[3].getY());
        point2 = new Point2D(points[0].getX(), points[0].getY());
        point1 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point1));
        point2 = SketchDesigner.getSketchPane().sceneToLocal(this.localToScene(point2));
        lineEJoint = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    @Override
    public ArrayList<Line> getLineForJoints() {
        refreshLinesForJoints();
        return new ArrayList<Line>(Arrays.asList(lineAJoint, lineCJoint, lineDJoint, lineEJoint));
    }

    @Override
    public void addJoint(Line lineForJointSide, Joint newJoint) {
        if (lineForJointSide.equals(lineAJoint)) {
            sideAJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineCJoint)) {
            sideCJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineDJoint)) {
            sideDJointsList.add(newJoint);
        } else if (lineForJointSide.equals(lineEJoint)) {
            sideEJointsList.add(newJoint);
        }
    }

    @Override
    public ArrayList<Joint> getJoints() {
        ArrayList<Joint> list = new ArrayList<>();

        list.addAll(sideAJointsList);
        list.addAll(sideCJointsList);
        list.addAll(sideDJointsList);
        list.addAll(sideEJointsList);

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
            if (sideCEdge.getType() != 1) {
                changeElementEdge(sideCEdge, new SketchEdge());
            }
            if (sideDEdge.getType() != 1) {
                changeElementEdge(sideDEdge, new SketchEdge());
            }
            if (sideEEdge.getType() != 1) {
                changeElementEdge(sideEEdge, new SketchEdge());
            }
        } else if (material.getName().indexOf("Кварцевый агломерат") != -1 || material.getName().indexOf("Кварцевый агломерат") != -1) {

            checkBoxSaveImage.setDisable(false);

            if (sideAEdge.getType() != 2) {
                changeElementEdge(sideAEdge, new SketchEdge());
            }
            if (sideCEdge.getType() != 2) {
                changeElementEdge(sideCEdge, new SketchEdge());
            }
            if (sideDEdge.getType() != 2) {
                changeElementEdge(sideDEdge, new SketchEdge());
            }
            if (sideEEdge.getType() != 2) {
                changeElementEdge(sideEEdge, new SketchEdge());
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
                if (sideCEdge instanceof Edge && ((Edge) sideCEdge).getSubType() > 7)
                    changeElementEdge(sideCEdge, new SketchEdge());
                if (sideDEdge instanceof Edge && ((Edge) sideDEdge).getSubType() > 7)
                    changeElementEdge(sideDEdge, new SketchEdge());
                if (sideEEdge instanceof Edge && ((Edge) sideEEdge).getSubType() > 7)
                    changeElementEdge(sideEEdge, new SketchEdge());

            } else if (this.edgeHeight > shapeDepth) {
                if (sideAEdge instanceof Edge && ((Edge) sideAEdge).getSubType() <= 7)
                    changeElementEdge(sideAEdge, new SketchEdge());
                if (sideCEdge instanceof Edge && ((Edge) sideCEdge).getSubType() <= 7)
                    changeElementEdge(sideCEdge, new SketchEdge());
                if (sideDEdge instanceof Edge && ((Edge) sideDEdge).getSubType() <= 7)
                    changeElementEdge(sideDEdge, new SketchEdge());
                if (sideEEdge instanceof Edge && ((Edge) sideEEdge).getSubType() <= 7)
                    changeElementEdge(sideEEdge, new SketchEdge());
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
                new Point2D(this.points[3].getX() * newScale, this.points[3].getY() * newScale)
        };

        Polygon polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
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
                new Point2D(0.0, 60),
                new Point2D(20, 0.0),
                new Point2D(80, 0.0),
                new Point2D(100, 60)
        };
        Polygon polygon = new Polygon(
                points[0].getX(), points[0].getY(),
                points[1].getX(), points[1].getY(),
                points[2].getX(), points[2].getY(),
                points[3].getX(), points[3].getY()
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
        object.put("sizeAlphaAngleReal", sizeAlphaAngleReal);
        object.put("sizeBettaAngleReal", sizeBettaAngleReal);
        object.put("material", shapeMaterial.getName());
        object.put("shapeDepth", shapeDepth);
        object.put("edgesHeightsDefault", edgesHeightsDefault);
        object.put("saveMaterialImageOnEdges", saveMaterialImageOnEdges);
        object.put("edgeHeight", edgeHeight);
        object.put("borderHeight", borderHeight);
        object.put("rotateAngle", rotateTransform.getAngle());
        object.put("opacity", this.getOpacity());

        JSONObject sideAEdgeObject = new JSONObject();
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

        object.put("sideAEdge", sideAEdgeObject);
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
        sizeDReal = ((Double) jsonObject.get("sizeDReal")).doubleValue();
        sizeAlphaAngleReal = ((Double) jsonObject.get("sizeAlphaAngleReal")).doubleValue();
        sizeBettaAngleReal = ((Double) jsonObject.get("sizeBettaAngleReal")).doubleValue();


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
        JSONObject sideCEdgeObject = ((JSONObject) jsonObject.get("sideCEdge"));
        JSONObject sideDEdgeObject = ((JSONObject) jsonObject.get("sideDEdge"));
        JSONObject sideEEdgeObject = ((JSONObject) jsonObject.get("sideEEdge"));

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
        sizeAlphaAngleShape = sizeAlphaAngleReal;
        sizeBettaAngleShape = sizeBettaAngleReal;

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
