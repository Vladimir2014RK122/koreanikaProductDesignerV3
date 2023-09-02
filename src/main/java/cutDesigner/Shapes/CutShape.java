package cutDesigner.Shapes;

import Common.ConnectPoints.ConnectPoint;
import Common.Material.Material;
import Common.RepresentToJson;

import cutDesigner.CutDesigner;
import cutDesigner.CutPane;
//import cutDesigner.CutSheet;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sketchDesigner.Features.AdditionalFeature;
import sketchDesigner.Shapes.*;

import java.util.ArrayList;

public class CutShape extends CutObject implements RepresentToJson {

    Pane cutPane = null;
    Label labelShapeNumber;
    Color DISCONNECT_COLOR = Color.web("0x8AB79E");

    //edges:
    private ArrayList<CutShapeEdge> cutShapeEdgesList = new ArrayList<>(4);

    // connect points
    private ArrayList<ConnectPoint> connectPoints = new ArrayList<>();

    //features:
    private ArrayList<AdditionalFeature> featuresList = new ArrayList<>(2);

    Point2D rotationPivot = new Point2D(0, 0);

    SketchShape sketchShapeOwner = null;


    public double getShapeSquare() {
        double shapeSquare = getPolygonSquare(polygon);
        double edgesSquare = 0;


        for (CutShapeEdge edge : cutShapeEdgesList) {
            if (CutDesigner.getInstance().getCutShapeEdgesList().contains(edge)) {
                edgesSquare += edge.getPolygonSquare(edge.getPolygon());
            }
        }
        return shapeSquare + edgesSquare;
    }

    //protected BoundingBox boundingBox;
    //Dimensions:
    Label dimensionHLabel;
    Label dimensionVLabel;

    public CutShape(SketchShape sketchShapeOwner) {

        this.sketchShapeOwner = sketchShapeOwner;

        if (sketchShapeOwner.getElementType() == ElementTypes.TABLETOP) shapeColor = SketchShape.TABLE_TOP_COLOR;
        else if (sketchShapeOwner.getElementType() == ElementTypes.WALL_PANEL)
            shapeColor = SketchShape.WALL_PANEL_COLOR;

//        translateXProperty().addListener((observable, oldValue, newValue) -> {
//            //6 - connect point width/2, 12 - (connect point width/2)*2
//            boundingBox = new BoundingBox(getBoundsInParent().getMinX()+6,
//                    getBoundsInParent().getMinY()+6, getBoundsInParent().getWidth() - 12,
//                    getBoundsInParent().getHeight() - 12);
//
//        });
//        translateYProperty().addListener((observable, oldValue, newValue) -> {
//            //6 - connect point width/2, 12 - (connect point width/2)*2
//            boundingBox = new BoundingBox(getBoundsInParent().getMinX()+6,
//                    getBoundsInParent().getMinY()+6, getBoundsInParent().getWidth() - 12,
//                    getBoundsInParent().getHeight() - 12);
//
//        });

        this.setHeight(0);
        //this.setStyle("-fx-background-color: red;");


    }

    public boolean isSaveMaterialImage() {

        if (sketchShapeOwner instanceof SketchShape) {
            return ((SketchShape) sketchShapeOwner).isSaveMaterialImageOnEdges();
        } else {
            return false;
        }
    }

    @Override
    public Material getMaterial() {
        return sketchShapeOwner.getMaterial();
    }

    @Override
    public int getDepth() {
        return sketchShapeOwner.getShapeDepth();
    }

    public SketchObject getSketchObjectOwner() {
        return sketchShapeOwner;
    }

    @Override
    protected void onMousePressedCenterArea(MouseEvent event) {
        double scale = CutPane.getCutPaneScale();//SketchDesigner.getSketchPane().getScaleX();
        orgSceneX = event.getSceneX() / scale;
        orgSceneY = event.getSceneY() / scale;
        orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
        orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
        ((Pane) (event.getSource())).toFront();

        if (isSaveMaterialImage()) {
            for (CutShapeEdge edge : cutShapeEdgesList) {
                edge.onMousePressedCenterArea(event);
            }
        }
    }

    @Override
    public boolean isCorrectPlaced() {
        if (isSaveMaterialImage()) {
            for (CutShapeEdge edge : cutShapeEdgesList) {
                if (edge == null || edge.getStartCoordinate() == null) continue;
                if (!edge.isCorrectPlaced()) {
                    return false;
                }
            }
        }



        return super.isCorrectPlaced();
    }

    @Override
    public Color getShapeColor() {
        return DISCONNECT_COLOR;
    }

    @Override
    public void setShapeColor(Color color) {
        this.shapeColor = color;
    }

    @Override
    protected void onMouseDraggedCenterArea(MouseEvent event) {

        double scale = CutPane.getCutPaneScale();//SketchDesigner.getSketchPane().getScaleX();

        double offsetX = event.getSceneX() / scale - orgSceneX;
        double offsetY = event.getSceneY() / scale - orgSceneY;
        double newTranslateX = orgTranslateX + offsetX;
        double newTranslateY = orgTranslateY + offsetY;

        double oldNewTranslateX = getTranslateX() - newTranslateX;
        double oldNewTranslateY = getTranslateY() - newTranslateY;

//        if (newTranslateX <= 5 || newTranslateY <= 5) {
//            event.consume();
//            return;
//        }

        setTranslateX(newTranslateX);
        setTranslateY(newTranslateY);

        if (isSaveMaterialImage()) {

            for (CutShapeEdge cutShapeEdge : cutShapeEdgesList) {
                cutShapeEdge.setTranslateX(cutShapeEdge.getTranslateX() - oldNewTranslateX);
                cutShapeEdge.setTranslateY(cutShapeEdge.getTranslateY() - oldNewTranslateY);
                cutShapeEdge.toFront();
            }

        }

        if (containInUnion) {
            ArrayList<CutShape> cutShapesInUnion = new ArrayList<>();
            for (CutShape cutShape : cutShapeUnionOwner.getCutShapesInUnionList()) {
                int number = cutShape.getShapeNumber();
                if (number == this.getShapeNumber()) continue;
                CutShape cutShape1 = CutDesigner.getInstance().getCutPane().getCutShapeByNumber(number);
                cutShapesInUnion.add(cutShape1);
            }
            for (CutShape cutShape : cutShapesInUnion) {
                cutShape.setTranslateX(cutShape.getTranslateX() - oldNewTranslateX);
                cutShape.setTranslateY(cutShape.getTranslateY() - oldNewTranslateY);
                if (cutShape.isSaveMaterialImage()) {
                    for (CutShapeEdge edge : cutShape.getCutShapeEdgesList()) {
                        edge.setTranslateX(edge.getTranslateX() - oldNewTranslateX);
                        edge.setTranslateY(edge.getTranslateY() - oldNewTranslateY);
                    }
                }
            }
        }
        event.consume();
    }

    public void setRotationPivot(Point2D rotationPivot) {
        this.rotationPivot = rotationPivot;
    }


    public void setConnectPoints(ArrayList<ConnectPoint> connectPoints) {
        this.connectPoints = connectPoints;
    }

    public void setDimensionH(Label dimensionHLabel) {

//        if(dimensionH != null)  this.getChildren().remove(this.dimensionH);
        if (dimensionHLabel != null) this.getChildren().remove(this.dimensionHLabel);

//        this.dimensionH = dimensionH;
        this.dimensionHLabel = dimensionHLabel;

//        this.getChildren().add(dimensionH);
        this.getChildren().add(dimensionHLabel);
    }

    public void setDimensionV(Label dimensionVLabel) {

//        if(dimensionV != null)  this.getChildren().remove(this.dimensionV);
        if (dimensionVLabel != null) this.getChildren().remove(this.dimensionVLabel);

//        this.dimensionV = dimensionV;
        this.dimensionVLabel = dimensionVLabel;

//        this.getChildren().add(dimensionV);
        this.getChildren().add(dimensionVLabel);
    }

    public Label getDimensionHLabel() {
        return dimensionHLabel;
    }

    public Label getDimensionVLabel() {
        return dimensionVLabel;
    }

    public Label getLabelShapeNumber() {
        return labelShapeNumber;
    }

    public ArrayList<AdditionalFeature> getFeaturesList() {
        return featuresList;
    }


    public ArrayList<CutShapeEdge> getCutShapeEdgesList() {
        return cutShapeEdgesList;
    }

    public void setCutShapeEdgesList(ArrayList<CutShapeEdge> cutShapeEdgesList) {
        this.cutShapeEdgesList = cutShapeEdgesList;
    }

    public void delete() {
        cutPane.getChildren().remove(this);

        //if(elementType == ElementTypes.TABLETOP) {
        //cutSheet.getShapesList().remove(this);
        //}
    }

//    public double getRotateAngle() {
//        return rotateTransform.getAngle();
//    }

    public void refreshLabelNumber() {


        this.getChildren().remove(labelShapeNumber);
        double width = getBoundsInParent().getWidth();
        double height = getBoundsInParent().getHeight();
        labelShapeNumber = new Label(String.valueOf(sketchShapeOwner.getShapeNumber()));
        labelShapeNumber.setId("labelShapeNumber");

        labelShapeNumber.setPrefSize(30, 30);
        //labelShapeNumber.setStyle("-fx-text-fill:#B3B4B4;");
        labelShapeNumber.setAlignment(Pos.CENTER);
        labelShapeNumber.setTranslateX(polygon.getBoundsInParent().getWidth() / 2 - labelShapeNumber.getPrefWidth() / 2);
        labelShapeNumber.setTranslateY(polygon.getBoundsInParent().getHeight() / 2 - labelShapeNumber.getPrefHeight() / 2);

        polygon.setId("cutShapePolygon");

        if (sketchShapeOwner.getShapeType() == ShapeType.TRIANGLE) {
            labelShapeNumber.setTranslateX(0);
            labelShapeNumber.setTranslateY(polygon.getBoundsInParent().getHeight() - 30);
        }

        Bounds bounds = polygon.getBoundsInParent();
        if(bounds.getWidth() < 30 || bounds.getHeight() < 30){

        }else{
            this.getChildren().add(labelShapeNumber);
        }

        labelShapeNumber.setMouseTransparent(true);
        polygon.setStrokeWidth(0.3);

        refreshDimensions();
    }

    public void refreshDimensions() {
        Bounds bounds = polygon.getBoundsInParent();
        if(bounds.getWidth() < 30 || bounds.getHeight() < 30){
            this.getChildren().remove(dimensionHLabel);
            this.getChildren().remove(dimensionVLabel);
        }else{

        }
    }



    public ArrayList<ConnectPoint> getConnectPoints() {
        return connectPoints;
    }

    public void addDimensionsMode(boolean mode) {

        for (ConnectPoint p : connectPoints) {
            p.setSelectionMode(mode);
        }
//        connectPoints.get(0).setSelectionMode(mode);
//        connectPoints.get(1).setSelectionMode(mode);
//        connectPoints.get(2).setSelectionMode(mode);
//        connectPoints.get(3).setSelectionMode(mode);
        addDimensionMode = mode;
    }

    public ElementTypes getElementType() {
        return sketchShapeOwner.getElementType();
    }

    public ShapeType getShapeType() {
        return sketchShapeOwner.getShapeType();
    }

    public int getShapeNumber() {
        return sketchShapeOwner.getShapeNumber();
    }


    public void refreshShapeView() {
        //System.out.println("Refresh Cut Shape");
        double saveAngle = rotateTransform.getAngle();
        rotateShapeLocal(-saveAngle);
        sketchShapeOwner.refreshCutShapeView();
        rotateShapeLocal(saveAngle);

    }

    /*  ROTATE SHAPE */
    public Point2D getRotationPivot() {

        Point2D pivot = new Point2D(polygon.getBoundsInParent().getWidth() / 2, polygon.getBoundsInParent().getHeight() / 2);
        return pivot;
    }

    public Point2D getGlobalCenter() {

        globalCenter = CutDesigner.getInstance().getCutPane().sceneToLocal(this.localToScene(getRotationPivot()));

//        Circle circle = new Circle(globalCenter.getX() , globalCenter.getY(), 3, Color.PINK);
//        CutDesigner.getCutPane().getChildren().add(circle);
        return globalCenter;
    }

    @Override
    public void rotateShapeLocal(double angle) {


        rotateTransform.setPivotX(getRotationPivot().getX());
        rotateTransform.setPivotY(getRotationPivot().getY());
        rotateTransform.setAngle(rotateTransform.getAngle() + angle);
        for (ConnectPoint cp : connectPoints) {
            cp.setRotate((-1) * rotateTransform.getAngle());
        }
        labelShapeNumber.setRotate((-1) * rotateTransform.getAngle());
        if (isSaveMaterialImage()) {
            for (CutShapeEdge edge : cutShapeEdgesList) {
                edge.rotateShapeGlobal(angle, getGlobalCenter());
            }
        }

    }

    @Override
    public void rotateShapeGlobal(double angle, Point2D pivot) {

        Point2D rotationPoint = this.sceneToLocal(CutDesigner.getInstance().getCutPane().localToScene(pivot));
        rotateTransformGlobal.setPivotX(rotationPoint.getX());
        rotateTransformGlobal.setPivotY(rotationPoint.getY());
        rotateTransformGlobal.setAngle(rotateTransformGlobal.getAngle() + angle);

        if (isSaveMaterialImage()) {
            for (CutShapeEdge edge : cutShapeEdgesList) {
                edge.rotateShapeGlobal(angle, pivot);
            }
        }
        System.out.println("cutShape global rotate");
//        Point2D rotationPoint = this.sceneToLocal(CutDesigner.getCutPane().localToScene(pivot));
//        rotateTransformGlobal.setPivotX(rotationPoint.getX());
//        rotateTransformGlobal.setPivotY(rotationPoint.getY());
//        rotateTransformGlobal.setAngle(rotateTransformGlobal.getAngle() + angle);

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

    private void rotateDimensions(double angle) {
        dimensionHLabel.setRotate(rotateAngle);
        double dimHX = dimensionHLabel.getTranslateX() + dimensionHLabel.getBoundsInLocal().getWidth() / 2;
        double dimHY = dimensionHLabel.getTranslateY() + dimensionHLabel.getBoundsInLocal().getHeight() / 2;
        Point2D newPointDimH = SketchObject.rotatePoint(new Point2D(dimHX, dimHY), new Point2D(getRotationPivot().getX(), getRotationPivot().getY()), angle);
        dimensionHLabel.setTranslateX(newPointDimH.getX() - dimensionHLabel.getBoundsInLocal().getWidth() / 2);
        dimensionHLabel.setTranslateY(newPointDimH.getY() - dimensionHLabel.getBoundsInLocal().getHeight() / 2);


        dimensionVLabel.setRotate(rotateAngle);
        double dimVX = dimensionVLabel.getTranslateX() + dimensionVLabel.getBoundsInLocal().getWidth() / 2;
        double dimVY = dimensionVLabel.getTranslateY() + dimensionVLabel.getBoundsInLocal().getHeight() / 2;
        Point2D newPointDimV = SketchObject.rotatePoint(new Point2D(dimVX, dimVY), new Point2D(getRotationPivot().getX(), getRotationPivot().getY()), angle);
        dimensionVLabel.setTranslateX(newPointDimV.getX() - dimensionVLabel.getBoundsInLocal().getWidth() / 2);
        dimensionVLabel.setTranslateY(newPointDimV.getY() - dimensionVLabel.getBoundsInLocal().getHeight() / 2);
    }

    /*  ROTATE SHAPE */

    public void showConnectionPoints() {
        //if(this instanceof CutShapeEdge)System.out.println(connectPoints.size());
        for (ConnectPoint p : connectPoints) {
            p.show();
        }
    }

    public void hideConnectionPoints() {
        for (ConnectPoint p : connectPoints) {
            p.hide();
        }
    }

    @Override
    public JSONObject getJsonView() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("shapeNumber", getShapeNumber());
        jsonObject.put("elementType", getElementType().toString());
        jsonObject.put("translateX", getTranslateX());
        jsonObject.put("translateY", getTranslateY());
        jsonObject.put("globalPivotX", getGlobalCenter().getX());
        jsonObject.put("globalPivotY", getGlobalCenter().getY());
        jsonObject.put("rotateAngle", rotateTransform.getAngle());
//
//
        JSONArray edgesJsonArray = new JSONArray();
        int i = 1;
        for (CutShapeEdge cutShapeEdge : getCutShapeEdgesList()) {
            JSONObject objectEdge = new JSONObject();
            objectEdge.put("translateX", cutShapeEdge.getTranslateX());
            objectEdge.put("translateY", cutShapeEdge.getTranslateY());
            objectEdge.put("rotateAngle", cutShapeEdge.getRotateAngle());
            edgesJsonArray.add(objectEdge);
        }
        jsonObject.put("edgesArray", edgesJsonArray);

        return jsonObject;
    }


    @Override
    public void initFromJson(JSONObject jsonObject) {

        System.out.println("INIT FROM JSON");

        this.setTranslateX(((Double) jsonObject.get("translateX")).doubleValue());
        this.setTranslateY(((Double) jsonObject.get("translateY")).doubleValue());
        //CutDesigner.getCutPane().getChildren().add(this);
        //rotate:
        //----


        JSONArray edgesJsonArray = (JSONArray) jsonObject.get("edgesArray");
        for (int i = 0; i < edgesJsonArray.size(); i++) {
            JSONObject objectEdge = (JSONObject) edgesJsonArray.get(i);
            getCutShapeEdgesList().get(i).setTranslateX(((Double) objectEdge.get("translateX")).doubleValue());
            getCutShapeEdgesList().get(i).setTranslateY(((Double) objectEdge.get("translateY")).doubleValue());
            //CutDesigner.getCutPane().getChildren().add(getCutShapeEdgesList().get(i));
            //CutDesigner.getCutPane().getChildren().add(getCutShapeEdgesList().get(i));
            double angle = ((Double) objectEdge.get("rotateAngle")).doubleValue();
            if (isSaveMaterialImage() == false) {
                getCutShapeEdgesList().get(i).rotateShapeLocal(angle);
            }
            //rotate:
            //----
        }


        //rotateAngle = angle;

        double shapeAngle = ((Double) jsonObject.get("rotateAngle")).doubleValue();
        rotateShapeLocal(shapeAngle);


    }

    @Override
    public String toString() {
        return super.toString() + " №" + getShapeNumber();
    }
}
