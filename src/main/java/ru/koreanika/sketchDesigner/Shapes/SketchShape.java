package ru.koreanika.sketchDesigner.Shapes;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.RepresentToJson;
//import ru.koreanika.cutDesigner.CutSheet;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.cutDesigner.Shapes.CutShapeEdge;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import org.json.simple.JSONObject;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.FeatureType;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.Joint;
import ru.koreanika.sketchDesigner.ShapeManager.ShapeManager;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.utils.InfoMessage;

import java.util.ArrayList;

public abstract class SketchShape extends SketchObject implements RepresentToJson {


    SketchShape childShape = null;

    private double workCoefficient = 0;//only fo quartz
    private String productName = "";


    protected Image imageForFill = null;

    Pane dimensionsPane;
    AnchorPane shapeSettings;
    AnchorPane settingsRootAnchorPane;


    protected Rotate unionRotateTransform = null;
    protected Rotate rotateTransform = new Rotate();
    double rotateAngle = 0.0;


    public static int shapeCounter = 0;
    public static ArrayList<Integer> shapesNumbersList = new ArrayList<>();
    //shape number:
    protected int thisShapeNumber = 0;

    boolean connected = false;

    double widthConnectPoint = 10;
    ArrayList<ConnectPoint> connectionPoints = new ArrayList<>();

    protected static final double MIN_EDGE_HEIGHT_FOR_CUTSHAPE = 50;
    CutShape cutShape = null;
    ArrayList<ConnectPoint> cutShapeConnectPoints = new ArrayList<>();
    ArrayList<CutShapeEdge> cutShapesEdgesList = new ArrayList<>(4);

    protected Label labelShapeNumber;

    protected ShapeType shapeType;
    protected ElementTypes elementType;

    protected BoundingBox boundingBox;
    //public DataFormat dragShapeFormat;

    public static final DataFormat DRAG_DATA_FORMAT_ELEMENT_TYPE = new DataFormat("ELEMENT_TYPE");
    public static final DataFormat DRAG_DATA_FORMAT_SHAPE_TYPE = new DataFormat("SHAPE_TYPE");

    //Sketch shape Union
    SketchShapeUnion sketchShapeUnionOwner;
    boolean containInUnion = false;

    boolean saveMaterialImageOnEdges = false;

    public static final boolean USE_IMAGES_FOR_FILL_SHAPES = false;
    public static final Color TABLE_TOP_COLOR = Color.valueOf("#565656");
    public static final Color WALL_PANEL_COLOR = Color.valueOf("#8E8E8E");
    public static final Color WINDOWSILL_COLOR = Color.valueOf("#D9D9D9");
    public static final Color FOOT_COLOR = Color.DARKSLATEGREY;

    public static final String TABLE_TOP_IMAGE_PATH = "fill_shapes/fill_shape_tabletop.png";
    public static final String WALL_PANEL_IMAGE_PATH = "fill_shapes/fill_shape_wallpanel.png";
    public static final String WINDOWSILL_IMAGE_PATH = "fill_shapes/fill_shape_windowsill.png";
    public static final String FOOT_IMAGE_PATH = "fill_shapes/fill_shape_foot.png";

    public static Color SELECTED_COLOR = Color.web("0x4287f5");

    //Color DISCONNECT_COLOR = Color.BLUEVIOLET;


    protected Color shapeColor;
    String imagePath = "";

    ArrayList<AdditionalFeature> featuresList = new ArrayList<>(2);
    ArrayList<AdditionalFeature> featuresListForCutShape = new ArrayList<>(2);

    public SketchShape() {

        int counter = 0;
        while (true) {
            counter++;
            boolean contain = false;
            for (SketchShape sh : SketchDesigner.getSketchShapesList()) {
                if (sh.getShapeNumber() == counter) {
                    contain = true;
                    break;
                }
            }
            if (contain == false) {
                thisShapeNumber = counter;
                break;
            }
        }


        //shapeCounter++;
        //thisShapeNumber = shapeCounter;
        this.setOnMouseClicked(event -> onMouseClickedCenterArea(event));

        this.setOnMousePressed(event -> onMousePressedCenterArea(event));
        this.setOnMouseReleased(event -> onMouseReleasedCenterArea(event));
        this.setOnDragDetected(event -> onDragDetectedCenterArea(event));
        this.setOnMouseDragged(event -> onMouseDraggedCenterArea(event));

        //this.setOnDragOver(event -> dragOverShape(event));
        this.setOnDragEntered(event -> dragEnteredToShape(event));
        this.setOnDragExited(event -> dragExitedToShape(event));
        this.setOnDragDropped(event -> dropToShape(event));

        this.setPickOnBounds(false);


        this.getTransforms().add(rotateTransform);

//        this.translateXProperty().addListener((observable, oldValue, newValue) -> {
//            calculateJoints();
//        });
//        this.translateYProperty().addListener((observable, oldValue, newValue) -> {
//            calculateJoints();
//        });
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setWorkCoefficient(double workCoefficient) {
        this.workCoefficient = workCoefficient;
    }

    public double getWorkCoefficient() {
        return workCoefficient;
    }

    protected void onMousePressedCenterArea(MouseEvent event) {

        if (addDimensionMode) return;
        //System.out.println("pressed" + consistInUnion + (consistInSketchShapeUnion != null?  consistInSketchShapeUnion.isConnected() : consistInSketchShapeUnion));

        double scale = SketchDesigner.getSketchPaneScale();
        orgSceneX = event.getSceneX() / scale;
        orgSceneY = event.getSceneY() / scale;
        orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
        orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
        ((Pane) (event.getSource())).toFront();
        //show connectors area for all shapes on Pane
        shapeContextMenu.hide();
//        this.setOpacity(1.0);
        event.consume();

    }

    protected void onMouseClickedCenterArea(MouseEvent event) {
        if (addDimensionMode) return;
        //System.out.println("clicked");

        if (event.getButton() == MouseButton.PRIMARY) {
            if (SketchDesigner.getSelectionModeForEdges()) {
                event.consume();
                return;
            }
            if (SketchDesigner.multipleSelectionMode == true) {
                SketchDesigner.selectedSketchObjects.add(this);
                SketchDesigner.hideShapeSettings();
            } else {
                if (SketchDesigner.selectedSketchObjects.size() != 0)
                    SketchDesigner.selectedSketchObjects.get(0).unSelectShape();
                //SketchDesigner.selectedShapes.clear();
                SketchDesigner.unSelectAllShapes();
                SketchDesigner.selectedSketchObjects.add(this);
                SketchDesigner.showShapeSettings();
            }
            this.selectShape();

            if (containInUnion) {
                for (SketchShape sketchShape : sketchShapeUnionOwner.getSketchShapesInUnion()) {
                    if (this == sketchShape) continue;
                    sketchShape.selectShape();
                    SketchDesigner.selectedSketchObjects.add(sketchShape);

                }
                SketchDesigner.hideShapeSettings();
            }

        } else if (event.getButton() == MouseButton.SECONDARY) {
            //shapeContextMenu.show(childShape.getScene().getWindow(), event.getScreenX(), event.getScreenY());
        }

        event.consume();
    }


    protected void onDragDetectedCenterArea(MouseEvent event) {
        if (addDimensionMode) return;

        for (SketchShape shape : SketchDesigner.getSketchShapesList()) {
            if (!shape.equals(event.getSource()))
                //((SketchShape) shape).showConnectorsArea((SketchShape) event.getSource());
                if (ConnectPoint.draggablePoint != null) {
                    shape.showConnectionPoints();
                    ((Pane) (event.getSource())).setMouseTransparent(true);
                }
        }
        for (SketchShapeUnion shapeUnion : SketchDesigner.getSketchShapeUnionsList()) {
            if (!shapeUnion.equals(event.getSource()))
                //((SketchShape) shape).showConnectorsArea((SketchShape) event.getSource());
                if (ConnectPoint.draggablePoint != null) {
                    shapeUnion.showConnectionPoints();
                    ((Pane) (event.getSource())).setMouseTransparent(true);
                }
        }
        SketchDesigner.setDraggedShape((SketchShape) event.getSource());
        startFullDrag();
        event.consume();
    }

    protected void onMouseReleasedCenterArea(MouseEvent event) {
        if (addDimensionMode) return;

        for (SketchShape shape : SketchDesigner.getSketchShapesList()) {
            if (!shape.equals(event.getSource()))
                shape.hideConnectionPoints();
        }
        for (SketchShapeUnion shapeUnion : SketchDesigner.getSketchShapeUnionsList()) {
            if (!shapeUnion.equals(event.getSource()))
                shapeUnion.hideConnectionPoints();
        }
        SketchDesigner.setDraggedShape(null);

        ((Pane) (event.getSource())).setMouseTransparent(false);
        SketchDesigner.allDimensionsToFront();

//        ArrayList<SketchObject> otherSketchObjects = new ArrayList<>();
//        otherSketchObjects.addAll(SketchDesigner.getSketchShapesList());
//        if(overOtherShape(this, otherSketchObjects)){
//            this.setOpacity(0.5);
//        }else{
//            this.setOpacity(1.0);
//        }

        calculateAllJoints();

        event.consume();
    }

    protected void onMouseDraggedCenterArea(MouseEvent event) {
        if (addDimensionMode) return;

        double scale = SketchDesigner.getSketchPaneScale();

        double offsetX = event.getSceneX() / scale - orgSceneX;
        double offsetY = event.getSceneY() / scale - orgSceneY;
        double newTranslateX = orgTranslateX + offsetX;
        double newTranslateY = orgTranslateY + offsetY;

        double deltaX = getTranslateX() - newTranslateX;
        double deltaY = getTranslateY() - newTranslateY;

        setTranslateX(newTranslateX);
        setTranslateY(newTranslateY);

        ArrayList<SketchObject> otherSketchObjects = new ArrayList<>();
        otherSketchObjects.addAll(SketchDesigner.getSketchShapesList());
        if (overOtherShape(this, otherSketchObjects)) {
            this.setOpacity(0.5);
        } else {
            this.setOpacity(1.0);
        }


        if (containInUnion) {
            for (SketchShape sketchShape : sketchShapeUnionOwner.getSketchShapesInUnion()) {
                if (this == sketchShape) continue;
                sketchShape.setTranslateX(sketchShape.getTranslateX() - deltaX);
                sketchShape.setTranslateY(sketchShape.getTranslateY() - deltaY);
            }
        }

        //calculateJoints();
        event.consume();
        //hideDimensions();


    }


    public void setChildShape(SketchShape childShape) {
        this.childShape = childShape;


    }

    public boolean isSaveMaterialImageOnEdges() {
        return saveMaterialImageOnEdges;
    }

    public void setSaveMaterialImageOnEdges(boolean saveMaterialImageOnEdges) {
        this.saveMaterialImageOnEdges = saveMaterialImageOnEdges;
    }


    public void setSketchShapeUnionOwner(boolean containInUnion, SketchShapeUnion sketchShapeUnionOwner) {
        this.containInUnion = containInUnion;
        this.sketchShapeUnionOwner = sketchShapeUnionOwner;
        //this.setMouseTransparent(containInUnion);
        System.out.println("containInUnion = " + containInUnion);
        if (sketchShapeUnionOwner != null) {
            polygon.setStroke(UNION_COLOR);
        } else {
            polygon.setStroke(Color.BLACK);
        }

    }


    public boolean isContainInUnion() {
        return containInUnion;
    }

    public SketchShapeUnion getSketchShapeUnionOwner() {
        return sketchShapeUnionOwner;
    }

    public int getShapeNumber() {
        return thisShapeNumber;
    }


    public ShapeType getShapeType() {
        return shapeType;
    }

    public ElementTypes getElementType() {
        return elementType;
    }


    public ArrayList<AdditionalFeature> getFeaturesList() {
        return featuresList;
    }

    public void addFeature(AdditionalFeature feature) {
        featuresList.add(feature);
        getChildren().add(feature);
//        feature.setTranslateX(xPos);
//        feature.setTranslateY(yPos);
        feature.toFront();

//        rotateFeature(feature, rotateAngle);
    }

    public AdditionalFeature getFeatureByNumber(int featureNumber) {

        for (AdditionalFeature feature : featuresList) {
            if (feature.getFeatureNumber() == featureNumber) {
                return feature;
            }
        }
        return null;
    }

    private void rotateFeature(AdditionalFeature feature, double angle) {
        feature.setRotate(rotateAngle);

        double x = feature.getTranslateX() + feature.getBoundsInLocal().getWidth() / 2;
        double y = feature.getTranslateY() + feature.getBoundsInLocal().getHeight() / 2;
        Point2D newPoint = SketchObject.rotatePoint(new Point2D(x, y), new Point2D(getRotationPivot().getX(), getRotationPivot().getY()), angle);
        feature.setTranslateX(newPoint.getX() - feature.getBoundsInLocal().getWidth() / 2);
        feature.setTranslateY(newPoint.getY() - feature.getBoundsInLocal().getHeight() / 2);
    }

    private void dragOverShape(DragEvent event) {
        System.out.println("drag over");
    }

    private void dragEnteredToShape(DragEvent event) {
        System.out.println("drag entered");
        this.selectShape();
    }

    private void dragExitedToShape(DragEvent event) {
        System.out.println("drag exited");
        this.unSelectShape();
    }

    private void dropToShape(DragEvent event) {


        Dragboard db = event.getDragboard();
        if (db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_TYPE) == null) return;
        if (db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_SUBTYPE) == null) return;


        FeatureType featureType = (FeatureType) db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_TYPE);
        int featureSubtype = (int) db.getContent(AdditionalFeature.DRAG_DATA_FORMAT_FEATURE_SUBTYPE);


        if (this.elementType == ElementTypes.WALL_PANEL) {
            if (featureType == FeatureType.SINK || featureType == FeatureType.GROOVES || featureType == FeatureType.RODS) {
                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Нельзя добавить на стеновую панель", null);
                return;
            }
        } else if (this.elementType == ElementTypes.FOOT) {
            if (featureType == FeatureType.SINK || featureType == FeatureType.GROOVES || featureType == FeatureType.RODS) {
                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Нельзя добавить на опору", null);
                return;
            }

        }

        if (featureType == FeatureType.SINK) {
            if (featureSubtype != Sink.SINK_TYPE_16 && featureSubtype != Sink.SINK_TYPE_17) {
                if (!getMaterial().getAvailableSinkTypes().contains(Integer.valueOf(featureSubtype))) {
                    InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Нельзя добавить для этого материала", null);
                    return;
                }
            }

        } else if (featureType == FeatureType.GROOVES) {
            if (!getMaterial().getAvailableGroovesTypes().contains(Integer.valueOf(featureSubtype))) {
                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Нельзя добавить для этого материала", null);
                return;
            }
        } else if (featureType == FeatureType.RODS) {
            if (!getMaterial().getAvailableRodsTypes().contains(Integer.valueOf(featureSubtype))) {
                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Нельзя добавить для этого материала", null);
                return;
            }
        }


        AdditionalFeature feature = AdditionalFeature.createFeature(featureType, featureSubtype, this);
        ShapeManager.show(getScene(), this, feature);
//        if(feature instanceof Sink){
//            //SetupSinkFeatureWindow.show(getScene(), (Sink)feature, this);
//
//            ShapeManager.show(getScene(), this, feature);
//
//        }else if(feature instanceof Rods){
//            //SetupSinkFeatureWindow.show(getScene(), (Sink)feature, this);
//
//            ShapeManager.show(getScene(), this, feature);
//
//        }

    }


    public Point2D getRotationPivot() {

        Point2D pivot = new Point2D(polygon.getBoundsInParent().getWidth() / 2, polygon.getBoundsInParent().getHeight() / 2);
        return pivot;
    }

    public void rotateShape(double angle) {
        if (containInUnion) {
            sketchShapeUnionOwner.rotate(angle);
            return;
        }

        rotateTransform.setPivotX(getRotationPivot().getX());
        rotateTransform.setPivotY(getRotationPivot().getY());
        rotateTransform.setAngle(rotateTransform.getAngle() + angle);
        for (ConnectPoint cp : connectionPoints) {
            cp.setRotate((-1) * rotateTransform.getAngle());
        }
        labelShapeNumber.setRotate((-1) * rotateTransform.getAngle());
    }

    public Rotate getRotateTransform() {
        return rotateTransform;
    }

    public void setUnionRotateTransform(Rotate unionRotateTransform) {
        this.getTransforms().add(unionRotateTransform);
    }

    public Rotate getUnionRotateTransform() {
        return unionRotateTransform;
    }


    public abstract void initConnectionPoints();

    public abstract void setWidthConnectPoint(double widthConnectPoint);

    public abstract ArrayList<ConnectPoint> getConnectPoints();

    //public abstract void getConnectionPointShiftX();
    //public abstract void getConnectionPointShiftY();
    public abstract boolean isConnectedToShapeOutOfUnion();

    public abstract double getVerticalSize();

    public abstract double getHorizontalSize();

    public abstract void updateShapeNumber();


    //public abstract void updateCutShapeDimensions(CutShape cutShape);
    public abstract CutShape getCutShapeFromJSON(JSONObject obj);


    public CutShape getCutShape() {
        refreshCutShapeView();
        return cutShape;
    }

    public CutShape getCutShapeWithoutRefresh() {
        return cutShape;
    }

    public ArrayList<CutShapeEdge> getCutShapeEdgesList() {
        return cutShapesEdgesList;
    }

    public void deleteCutShape() {
        cutShape = null;
    }

    public abstract AnchorPane getShapeSettings();

    public abstract ArrayList<Point2D[]> getShapeEdges();

    public abstract int getEdgeHeight();

    public abstract int getBorderHeight();

    public abstract double getShapeWidth();

    public abstract double getShapeHeight();

    public abstract void initShapeSettings();

    public abstract void initShapeSettingsControlLogic();

    public abstract void refreshShapeSettings();

    public abstract void shapeSettingsSaveBtnClicked();

    public abstract void shapeSettingsCancelBtnClicked();

    public abstract void rebuildShapeView();

    public abstract void deleteShape();

    public abstract void disconnectFromShape(SketchShape connectedShape);

    public abstract void createContextMenu();


    public abstract void setEdgesZoneWidth(double widthEdge);

    public abstract void initEdgesZones();

    public abstract void changeElementEdge(SketchEdge edge, SketchEdge newEdge);

    public abstract ArrayList<SketchEdge> getEdges();

    public abstract void selectEdge(SketchEdge edge);

    public abstract void deSelectEdge(SketchEdge edge);

    public abstract void deSelectAllEdges();

    public abstract void updateMaterialList();

    public abstract void updateEdgesHeight();

    public abstract double getEdgeOrBorderLength(SketchEdge edge);

    public abstract double getEdgesLength();

    public abstract double getBordersType1Length();

    public abstract double getBordersType2Length();

    //public abstract void rotateShape(double angle);

    public abstract void addDimensionsMode(boolean mode);

    /**
     * JOINTS
     */

    public abstract void clearJointsLists();

    public abstract void refreshLinesForJoints();

    public abstract ArrayList<Line> getLineForJoints();

    public abstract void addJoint(Line lineForJointSide, Joint newJoint);

    public abstract ArrayList<Joint> getJoints();

    @Override
    public void calculateMyJoints() {

        clearJointsLists();

        ArrayList<SketchObject> otherSketchObjects = new ArrayList<>();
        otherSketchObjects.addAll(SketchDesigner.getSketchShapesList());
        boolean correctPlace = !overOtherShape(this, otherSketchObjects);
        //System.out.println("correctPlace = " + correctPlace );


        for (SketchShape sketchShape : SketchDesigner.getSketchShapesList()) {
            if (!sketchShape.equals(this)) {
                //System.out.println("sketchShape.getShapeNumber() = " + sketchShape.getShapeNumber());

                for (Line lineThis : this.getLineForJoints()) {
                    for (Line lineThat : sketchShape.getLineForJoints()) {

//                        System.out.println("sketchShape.Number = " + sketchShape.getShapeNumber() + " Line = " + linesOnOneStraight(lineThis, lineThat));
                        if (linesOnOneStraight(lineThis, lineThat)) {
                            //System.out.println("true");
                            Line intersect = intersectLines(lineThis, lineThat);
                            if (intersect != null) {
                                //System.out.println(intersect);
                                double len = new Point2D(intersect.getStartX(), intersect.getStartY()).distance(intersect.getEndX(), intersect.getEndY());
                                System.out.println("len = " + len);


                                if (len > 0.001) {
                                    //create and add Joint:
                                    Joint.JointType jointType;
                                    if ((this.getElementType() == ElementTypes.FOOT && sketchShape.getElementType() != ElementTypes.FOOT) ||
                                            (sketchShape.getElementType() == ElementTypes.FOOT && this.getElementType() != ElementTypes.FOOT)) {
                                        jointType = Joint.JointType.OBLIQUE;
                                    } else {
                                        jointType = Joint.JointType.STRAIGHT;
                                    }
                                    Joint newJoint = new Joint(this, sketchShape, lineThis, jointType);
                                    newJoint.setLen(len);
                                    addJoint(lineThis, newJoint);
                                }
                            }
                        }

                    }
                }
                //SketchDesigner.getSketchPane().getChildren().addAll(shapeRectangle.getJoints());
            }
        }

        //SketchDesigner.getSketchPane().getChildren().addAll(getJoints());
    }

    @Override
    public void calculateAllJoints() {

        ArrayList<Joint> allJoints = new ArrayList<>();

        for (SketchShape sketchShape : SketchDesigner.getSketchShapesList()) {
            sketchShape.calculateMyJoints();
            allJoints.addAll(sketchShape.getJoints());
        }

        for (Joint joint : allJoints) {
            System.out.println(joint);
        }


    }


    public static boolean linesOnOneStraight(Line line1, Line line2) {
        double x = 0;
        double y = 0;

        double line1X1 = line1.getStartX();
        double line1X2 = line1.getEndX();
        double line1Y1 = line1.getStartY();
        double line1Y2 = line1.getEndY();

        double leftPart = 0.0;
        double rightPart = 0.0;

        x = line2.getStartX();
        y = line2.getStartY();
        boolean point1Result = false;
        leftPart = (x - line1X1) * (line1Y2 - line1Y1);
        rightPart = (y - line1Y1) * (line1X2 - line1X1);
        point1Result = Math.abs(leftPart - rightPart) <= 0.01;
//        System.out.println("leftPart = " + leftPart + "rightPart = " + rightPart);

        x = line2.getEndX();
        y = line2.getEndY();
        boolean point2Result = false;
        leftPart = (x - line1X1) * (line1Y2 - line1Y1);
        rightPart = (y - line1Y1) * (line1X2 - line1X1);
        point2Result = Math.abs(leftPart - rightPart) <= 0.01;
//        System.out.println("leftPart = " + leftPart + "rightPart = " + rightPart);

//        System.out.println("point1Result = " + point1Result + "point2Result = " + point2Result);
        return point1Result && point2Result;
    }

    public static Line intersectLines(Line line1, Line line2) {
        Line resultLine = null;
        Point2D p1 = null;
        Point2D p2 = null;

        Point2D A = new Point2D(line1.getStartX(), line1.getStartY());
        Point2D B = new Point2D(line1.getEndX(), line1.getEndY());

        Point2D C = new Point2D(line2.getStartX(), line2.getStartY());
        Point2D D = new Point2D(line2.getEndX(), line2.getEndY());

        if (line2.contains(A) && line2.contains(B)) {
            p1 = A;
            p2 = B;
        } else if (line1.contains(C) && line1.contains(D)) {
            p1 = C;
            p2 = D;
        } else if (line1.contains(C) && !line1.contains(D) && line2.contains(B) && !line2.contains(A) && !B.equals(C)) {
            p1 = C;
            p2 = B;
        } else if (line1.contains(D) && !line1.contains(C) && line2.contains(A) && !line2.contains(B) && !A.equals(D)) {
            p1 = A;
            p2 = D;
        }

        if (p1 != null && p2 != null) {
            resultLine = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }

        return resultLine;
    }

    /**
     * JOINTS
     */


    public abstract Node getViewForListCell();

    public abstract Tooltip getTooltipForListCell();

    public Polygon getPolygon() {
        return polygon;
    }

    public static Node getStaticViewForListCell() {
        return null;
    }

    public double getRotateAngle() {
        return rotateAngle;
    }

    public Label getLabelShapeNumber() {
        return labelShapeNumber;
    }

    public void setShapeDepth(int shapeDepth) {
        this.shapeDepth = shapeDepth;
    }

    @Override
    public void selectShape() {
        polygon.setFill(SketchShape.SELECTED_COLOR);

    }

    @Override
    public void unSelectShape() {
        polygon.setFill(shapeColor);
    }
}
