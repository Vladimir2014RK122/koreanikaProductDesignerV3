package ru.koreanika.cutDesigner.Shapes;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.Material.Material;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;

import java.util.ArrayList;

public class CutShapeAdditionalFeature extends CutObject {

    Color DISCONNECT_COLOR = Color.DARKOLIVEGREEN;

    AdditionalFeature featureOwner = null;
    Material material = null;
    int depth;

    Point2D shiftCoordinate = new Point2D(0, 0);


    // connect points
    private ArrayList<ConnectPoint> connectPoints = new ArrayList<>();

    public CutShapeAdditionalFeature(AdditionalFeature featureOwner) {
        shapeColor = DISCONNECT_COLOR;
        this.featureOwner = featureOwner;
    }

    @Override
    public Color getShapeColor() {
        return DISCONNECT_COLOR;
    }

    @Override
    public void setShapeColor(Color color) {
        shapeColor = color;
    }

    public Point2D getRotationPivot() {

        Point2D pivot = new Point2D(polygon.getBoundsInParent().getWidth() / 2, polygon.getBoundsInParent().getHeight() / 2);
        return pivot;
    }


    @Override
    public void rotateShapeLocal(double angle) {

        rotateTransform.setPivotX(getRotationPivot().getX());
        rotateTransform.setPivotY(getRotationPivot().getY());
        rotateTransform.setAngle(rotateTransform.getAngle() + angle);
        for (ConnectPoint cp : connectPoints) {
            cp.setRotate((-1) * rotateTransform.getAngle());
        }
//        labelShapeNumber.setRotate((-1) * rotateTransform.getAngle());
//        if(isSaveMaterialImage()){
//            for(CutShapeEdge edge : cutShapeEdgesList){
//                edge.rotateShapeGlobal(angle, getGlobalCenter());
//            }
//        }
    }

    @Override
    public void rotateShapeGlobal(double angle, Point2D pivot) {

    }

    @Override
    public void showConnectionPoints() {
        for (ConnectPoint cp : connectPoints) {
            cp.show();
        }
    }

    @Override
    public void hideConnectionPoints() {
        for (ConnectPoint cp : connectPoints) {
            cp.hide();
        }
    }

    public double getShapeSquare() {
        double shapeSquare = getPolygonSquare(polygon);
        return shapeSquare;
    }

    public AdditionalFeature getFeatureOwner() {
        return featureOwner;
    }

    public void setConnectPoints(ArrayList<ConnectPoint> connectPoints) {
        this.connectPoints = connectPoints;
    }

    public void setShiftCoordinate(Point2D shiftCoordinate) {
        this.shiftCoordinate = shiftCoordinate;
    }

    public Point2D getShiftCoordinate() {
        return shiftCoordinate;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public int getDepth() {
        return depth;
    }


//    public JSONObject getJsonView() {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("shapeNumber", featureOwner.getSketchShapeOwner().getShapeNumber());
//        jsonObject.put("featureNumber", featureOwner.getFeatureNumber());
//        jsonObject.put("translateX", getTranslateX());
//        jsonObject.put("translateY", getTranslateY());
//        jsonObject.put("rotateAngle", rotateTransform.getAngle());
//
//        return jsonObject;
//    }
//
//
//    public static ArrayList<CutShapeAdditionalFeature> initFromJson(JSONObject jsonObject) {
//
//        SketchShape sketchShape = SketchDesigner.getSketchShape(((Long)jsonObject.get("shapeNumber")).intValue());
//        AdditionalFeature additionalFeature = sketchShape.getFeatureByNumber(((Long)jsonObject.get("featureNumber")).intValue());
//
//
//
//    }
}
