package cutDesigner.Shapes;

import Common.ConnectPoints.ConnectPoint;
import Common.Material.Material;
import cutDesigner.CutDesigner;
import javafx.geometry.Point2D;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sketchDesigner.Shapes.*;
import sketchDesigner.SketchDesigner;

import java.util.ArrayList;

public class CutShapeUnion {


    ArrayList<Rotate> rotateTransformListForCutShapes = new ArrayList<>();
    ArrayList<Rotate> rotateTransformListForCutShapeEdges = new ArrayList<>();
    double rotateAngle = 0.0;

    ArrayList<CutShape> cutShapesInUnionList = new ArrayList<>();
    ArrayList<CutShapeEdge> cutShapeEdgesInUnion = new ArrayList<>();

    // connect points
    private ArrayList<ConnectPoint> connectPoints = new ArrayList<>();
    SketchShapeUnion sketchShapeUnion = null;

    int unionNumber = 0;

    public CutShapeUnion(SketchShapeUnion sketchShapeUnion) {
        this.sketchShapeUnion = sketchShapeUnion;
        this.unionNumber = sketchShapeUnion.getUnionNumber();

        for (SketchShape sketchShape : sketchShapeUnion.getSketchShapesInUnion()) {
            CutShape cutShape = sketchShape.getCutShape();
            cutShapesInUnionList.add(cutShape);
            for (CutShapeEdge cutShapeEdge : cutShape.getCutShapeEdgesList()) {
                //if(cutShapeEdge != null){
                cutShapeEdgesInUnion.add(cutShapeEdge);
                Rotate rotateTransform = new Rotate();
                rotateTransformListForCutShapeEdges.add(rotateTransform);
                cutShapeEdge.getTransforms().add(rotateTransform);
                //}
            }

            Rotate rotateTransform = new Rotate();
            rotateTransformListForCutShapes.add(rotateTransform);
            cutShape.getTransforms().add(rotateTransform);
        }

        setRotatePivots();
    }


    public Point2D setRotatePivots() {
        Path path = new Path();
        ArrayList<Point2D> polygonsPoints = new ArrayList<>();
        for (int i = 0; i < cutShapesInUnionList.size(); i++) {

            path = (Path) Polygon.union(path, cutShapesInUnionList.get(i).getPolygon());

            for (int j = 0; j < cutShapesInUnionList.get(i).getPolygon().getPoints().size(); j += 2) {
                double x = cutShapesInUnionList.get(i).getPolygon().getPoints().get(j);
                double y = cutShapesInUnionList.get(i).getPolygon().getPoints().get(j + 1);
                Point2D point = new Point2D(x, y);

                point = CutDesigner.getInstance().getCutPane().sceneToLocal(cutShapesInUnionList.get(i).localToScene(point));
                polygonsPoints.add(point);
//                System.out.println(point);
//                Circle circlePivot = new Circle(point.getX(), point.getY(), 2, Color.RED);
//                SketchDesigner.getSketchPane().getChildren().add(circlePivot);
            }

        }

        double minX = 10000, maxX = 0;
        double minY = 10000, maxY = 0;
        for (Point2D p : polygonsPoints) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getX() > maxX) maxX = p.getX();

            if (p.getY() < minY) minY = p.getY();
            if (p.getY() > maxY) maxY = p.getY();
        }
        //System.out.println("minX = " + minX + " maxX = " + maxX);
        //System.out.println("minY = " + minY + " maxY = " + maxY);

        Point2D pivot = new Point2D(minX + ((maxX - minX) / 2.0), minY + ((maxY - minY) / 2.0));
        //Point2D pivot = new Point2D(minX, minY);

        for (int i = 0; i < cutShapesInUnionList.size(); i++) {
            Point2D internalPivot = cutShapesInUnionList.get(i).sceneToLocal(CutDesigner.getInstance().getCutPane().localToScene(pivot));
            rotateTransformListForCutShapes.get(i).setPivotX(internalPivot.getX());
            rotateTransformListForCutShapes.get(i).setPivotY(internalPivot.getY());
            //Circle circlePivot = new Circle(internalPivot.getX(), internalPivot.getY(), 2, Color.RED);
            //cutShapesInUnionList.get(i).getChildren().add(circlePivot);
        }

        for (int i = 0; i < cutShapeEdgesInUnion.size(); i++) {
            Point2D internalPivot = cutShapeEdgesInUnion.get(i).sceneToLocal(CutDesigner.getInstance().getCutPane().localToScene(pivot));
            rotateTransformListForCutShapeEdges.get(i).setPivotX(internalPivot.getX());
            rotateTransformListForCutShapeEdges.get(i).setPivotY(internalPivot.getY());
//            Circle circlePivot = new Circle(internalPivot.getX(), internalPivot.getY(), 2, Color.RED);
//            sketchShapesInUnion.get(i).getChildren().add(circlePivot);
        }

        return pivot;
    }

    public void rotate(double angle) {

        System.out.println("ROTATE UNION  " + angle);

        for (int i = 0; i < cutShapesInUnionList.size(); i++) {

            //Point2D internalPivot = sketchShapesInUnion.get(i).sceneToLocal(SketchDesigner.getSketchPane().localToScene(pivot));
            //rotateTransformList.get(i).setPivotX(internalPivot.getX());
            //rotateTransformList.get(i).setPivotY(internalPivot.getY());
            rotateTransformListForCutShapes.get(i).setAngle(rotateAngle + angle);

            cutShapesInUnionList.get(i).getLabelShapeNumber().setRotate((-1) * (rotateTransformListForCutShapes.get(i).getAngle() + cutShapesInUnionList.get(i).getRotateTransform().getAngle()));
            for (ConnectPoint cp : cutShapesInUnionList.get(i).getConnectPoints()) {
                cp.setRotate((-1) * (rotateTransformListForCutShapes.get(i).getAngle() + cutShapesInUnionList.get(i).getRotateTransform().getAngle()));
            }


            //Circle circlePivot = new Circle(internalPivot.getX(), internalPivot.getY(), 2, Color.RED);
            //sketchShapesInUnion.get(i).getChildren().add(circlePivot);
        }

        for (int i = 0; i < cutShapeEdgesInUnion.size(); i++) {

            //System.out.println("X= " + rotateTransformListForCutShapeEdges.get(i).getPivotX() + " " + "Y= " + rotateTransformListForCutShapeEdges.get(i).getPivotY());

            //Point2D internalPivot = sketchShapesInUnion.get(i).sceneToLocal(SketchDesigner.getSketchPane().localToScene(pivot));
            //rotateTransformList.get(i).setPivotX(internalPivot.getX());
            //rotateTransformList.get(i).setPivotY(internalPivot.getY());
            rotateTransformListForCutShapeEdges.get(i).setAngle(rotateAngle + angle);

            //cutShapeEdgesInUnion.get(i).getLabelShapeNumber().setRotate((-1) * (rotateTransformListForCutShapes.get(i).getAngle() + cutShapesInUnionList.get(i).getRotateTransform().getAngle()));
            for (ConnectPoint cp : cutShapeEdgesInUnion.get(i).getConnectPoints()) {
                double cpAngle = cp.getRotate();
//                System.out.println("cpAngle = " + cpAngle);
//                System.out.println("cpAngle = " + cpAngle%90);
                cp.setRotate((-1) * (cp.getRotate() + rotateTransformListForCutShapeEdges.get(i).getAngle()));
            }
            //System.out.println(cutShapeEdgesInUnion.get(i).getConnectPoints().size());


            //Circle circlePivot = new Circle(internalPivot.getX(), internalPivot.getY(), 2, Color.RED);
            //sketchShapesInUnion.get(i).getChildren().add(circlePivot);
        }

        rotateAngle += angle;
    }

    public double getRotateAngle() {
        return rotateAngle;
    }

    public int getUnionNumber() {
        return unionNumber;
    }

    public SketchShapeUnion getSketchShapeUnion() {
        return sketchShapeUnion;
    }

    public void setConnectPoints(ArrayList<ConnectPoint> connectPoints) {
        this.connectPoints = connectPoints;
    }

    public ArrayList<ConnectPoint> getConnectPoints() {
        return connectPoints;
    }

    public void setCutShapesInUnionList(ArrayList<CutShape> cutShapesInUnionList) {
        this.cutShapesInUnionList = cutShapesInUnionList;
    }

    public ArrayList<CutShape> getCutShapesInUnionList() {
        return cutShapesInUnionList;
    }

    public void setCutShapeEdgesInUnion(ArrayList<CutShapeEdge> cutShapeEdgesInUnion) {
        this.cutShapeEdgesInUnion = cutShapeEdgesInUnion;
    }

    public ArrayList<CutShapeEdge> getCutShapeEdgesInUnion() {
        return cutShapeEdgesInUnion;
    }

    public JSONObject getJsonView() {
        JSONObject object = new JSONObject();

        object.put("unionNumber", unionNumber);
        object.put("rotateAngle", rotateAngle);

        object.put("elementType", cutShapesInUnionList.get(0).getElementType().toString());
        JSONArray shapesNumbersIntoUnion = new JSONArray();
        for (CutShape cutShape : cutShapesInUnionList) {
            shapesNumbersIntoUnion.add(cutShape.getShapeNumber());
        }
        object.put("shapesNumbersIntoUnion", shapesNumbersIntoUnion);

        return object;
    }

    public static CutShapeUnion initFromJson(JSONObject jsonObject) {

        int unionNumber = ((Long) (jsonObject.get("unionNumber"))).intValue();
        SketchShapeUnion sketchShapeUnion = SketchDesigner.getSketchShapeUnion(unionNumber);
        CutShapeUnion cutShapeUnion = new CutShapeUnion(sketchShapeUnion);

        for (CutShape cutShape : cutShapeUnion.getCutShapesInUnionList()) {
            cutShape.setUnionNumber(unionNumber);
            cutShape.setCutShapeUnionOwner(cutShapeUnion);
            cutShape.setContainInUnion(true);
            for (CutShapeEdge cutShapeEdge : cutShape.getCutShapeEdgesList()) {
                cutShapeEdge.setUnionNumber(unionNumber);
                cutShapeEdge.setCutShapeUnionOwner(cutShapeUnion);
                cutShapeEdge.setContainInUnion(true);
            }
        }


        cutShapeUnion.rotate(((Double) (jsonObject.get("rotateAngle"))).doubleValue());


        return cutShapeUnion;
    }

    public Material getMaterial() {
        return cutShapesInUnionList.get(0).getMaterial();
    }

    public int getDepth() {
        return cutShapesInUnionList.get(0).getDepth();
    }
}
