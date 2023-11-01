package ru.koreanika.sketchDesigner.Shapes;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.Connectible;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.project.Project;

import java.util.ArrayList;

public abstract class SketchObject extends Pane implements Connectible {


    Polygon polygon = new Polygon();
    ArrayList<ConnectPoint> connectionPoints = new ArrayList<>();

    //move on work pane:
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;

    //Colors:
    protected Color SELECTED_COLOR = Color.ORANGE;
    protected Color CONNECT_COLOR = Color.RED;
    protected Color UNION_COLOR = Color.GREEN;

    protected double opacity = 1.0;

    //material:
    Material shapeMaterial;                //default
    int shapeDepth = 20;

    //dimensions:
    boolean addDimensionMode = false;

    //context menu
    ContextMenu shapeContextMenu = new ContextMenu();


    double commonShapeScale = Project.getCommonShapeScale();

    public abstract void refreshCutShapeView();

    public abstract void selectShape();

    public abstract void unSelectShape();

    public abstract void showConnectionPoints();

    public abstract void hideConnectionPoints();

    public abstract Polygon getPolygon();

    public abstract void edgesDisable(boolean edgesDisable);

    public abstract int getShapeNumber();

    public Material getMaterial() {
        return shapeMaterial;
    }

    public int getShapeDepth() {
        return shapeDepth;
    }


    protected boolean overOtherShape(SketchObject sketchObject1, ArrayList<SketchObject> otherSketchObjects) throws IndexOutOfBoundsException {


        boolean result = false;
        //get cutObject1 poligon:
        ArrayList<Point2D> thisPolygonPoints = new ArrayList<>();
        for (int i = 0; i < sketchObject1.getPolygon().getPoints().size(); i += 2) {
            double x = sketchObject1.getPolygon().getPoints().get(i);
            double y = sketchObject1.getPolygon().getPoints().get(i + 1);
            Point2D pointOnCutPane = CutDesigner.getInstance().getCutPane().sceneToLocal(sketchObject1.localToScene(x, y));
            thisPolygonPoints.add(pointOnCutPane);
        }
        Polygon thisPolygon = new Polygon();
        for (Point2D p : thisPolygonPoints) {
            thisPolygon.getPoints().add(p.getX());
            thisPolygon.getPoints().add(p.getY());
        }

        //remove cutObject1 from otherCutObjectsList:
        otherSketchObjects.remove(sketchObject1);

        //remove edges for cutObject1 if saveImage == true form otherCutObjectsList:
//        if(sketchObject1 instanceof SketchShape && ((SketchShape)sketchObject1).isSaveMaterialImage()){
//            otherSketchObjects.removeAll(((SketchShape)sketchObject1).getCutShapeEdgesList());
//        }
//
//        if(cutObject1 instanceof CutShapeEdge && ((CutShapeEdge)cutObject1).getOwner().isSaveMaterialImage()){
//            otherCutObjects.removeAll(((CutShapeEdge)cutObject1).getOwner().getCutShapeEdgesList());
//            otherCutObjects.remove(((CutShapeEdge) cutObject1).getOwner());
//        }


        for (SketchObject otherSketchObject : otherSketchObjects) {

            ArrayList<Point2D> otherPolygonPoints = new ArrayList<>();
            for (int i = 0; i < otherSketchObject.getPolygon().getPoints().size(); i += 2) {
                double x = otherSketchObject.getPolygon().getPoints().get(i);
                double y = otherSketchObject.getPolygon().getPoints().get(i + 1);
                Point2D pointOnCutPane = CutDesigner.getInstance().getCutPane().sceneToLocal(otherSketchObject.localToScene(x, y));
                otherPolygonPoints.add(pointOnCutPane);
            }
            Polygon otherSketchObjectPolygon = new Polygon();
            for (Point2D p : otherPolygonPoints) {
                otherSketchObjectPolygon.getPoints().add(p.getX());
                otherSketchObjectPolygon.getPoints().add(p.getY());
            }
//          Shape shape = Shape.intersect(thisPolygon, otherCutObjectPolygon);
//          shape.setScaleX(10);
//          shape.setScaleY(10);
//          CutDesigner.getCutPane().getChildren().add(shape);
//          shape.setFill(Color.BLACK);
            if (((Path) Shape.intersect(thisPolygon, otherSketchObjectPolygon)).getElements().size() != 0) {
                //System.out.println("OVER!");
                if (Math.abs(pathSquare(((Path) Shape.intersect(thisPolygon, otherSketchObjectPolygon)))) > 0.5) {
                    //System.out.println("OVER! but < 0.5 = " + Math.abs(pathSquare(((Path)Shape.intersect(thisPolygon, otherCutObjectPolygon)))));
                    //result =  true;
                    otherSketchObject.setOpacity(0.5);
                    return true;
                } else {
                    otherSketchObject.setOpacity(1.0);
                }
                //result =  false;

            } else {
                otherSketchObject.setOpacity(1.0);
            }
        }
        return false;
    }

    public static double pathSquare(Path path) {
        double square = 0.0;
        ArrayList<Polygon> polygonsList = new ArrayList<>();
        Polygon polygon = new Polygon();
        ;
        for (PathElement element : path.getElements()) {
            if (element instanceof MoveTo) {
                polygon = new Polygon();
                polygon.getPoints().add(new Double(((MoveTo) element).getX()));
                polygon.getPoints().add(new Double(((MoveTo) element).getY()));

            } else if (element instanceof LineTo) {
                polygon.getPoints().add(new Double(((LineTo) element).getX()));
                polygon.getPoints().add(new Double(((LineTo) element).getY()));

            } else if (element instanceof ClosePath) {
                polygonsList.add(polygon);
            }
        }

        for (Polygon pol : polygonsList) {
            square = square + getPolygonSquare(pol);
        }

        return square;
    }

    public static double getPolygonSquare(Polygon polygon) {
        //Gaus theorem
        double S = 0;
        double firstPart = 0;
        double secondPart = 0;
        for (int i = 0; i < polygon.getPoints().size() - 3; i++) {
            if (i % 2 == 0) {
                //X
                double x = polygon.getPoints().get(i).doubleValue();
                double y = polygon.getPoints().get(i + 3).doubleValue();
                firstPart += x * y;
            }
        }
        for (int i = 0; i < polygon.getPoints().size() - 1; i++) {
            if (i % 2 != 0) {
                //Y
                double x = polygon.getPoints().get(i + 1).doubleValue();
                double y = polygon.getPoints().get(i).doubleValue();
                secondPart -= x * y;
            }
        }

        S = (firstPart + secondPart) / 2;

        return S;
    }


    public abstract void rotateShape(double angle);

    public void connectShapeToShape(ConnectPoint draggablePoint, ConnectPoint staticPoint) {

        SketchObject draggableShape = (SketchObject) draggablePoint.getPointOwner();
        SketchObject staticShape = (SketchObject) staticPoint.getPointOwner();

        draggableShape.hideConnectionPoints();
        staticShape.hideConnectionPoints();

//      setStyle("-fx-background-color: Blue");
        Bounds staticBounds = SketchDesigner.getSketchPane().sceneToLocal(staticShape.localToScene(staticPoint.getBoundsInParent()));
        Bounds draggableBounds = SketchDesigner.getSketchPane().sceneToLocal(draggableShape.localToScene(draggablePoint.getBoundsInParent()));

        Point2D staticPointOnWorkPane = new Point2D(staticBounds.getMinX() + staticBounds.getWidth() / 2, staticBounds.getMinY() + staticBounds.getHeight() / 2);
        Point2D draggablePointOnWorkPane = new Point2D(draggableBounds.getMinX() + draggableBounds.getWidth() / 2, draggableBounds.getMinY() + draggableBounds.getHeight() / 2);

        double deltaX = staticPointOnWorkPane.getX() - draggablePointOnWorkPane.getX();
        double deltaY = staticPointOnWorkPane.getY() - draggablePointOnWorkPane.getY();

//      Circle staticCircle = new Circle(staticBounds.getMinX() + staticBounds.getWidth()/2, staticBounds.getMinY() + staticBounds.getHeight()/2,3, Color.RED);
//      CutDesigner.getCutPane().getChildren().add(staticCircle);

//      Circle draggableCircle = new Circle(draggablePointOnWorkPane.getX(), draggablePointOnWorkPane.getY(),5, Color.YELLOW);
//      CutDesigner.getCutPane().getChildren().add(draggableCircle);

        draggableShape.setTranslateX(draggableShape.getTranslateX() + deltaX);
        draggableShape.setTranslateY(draggableShape.getTranslateY() + deltaY);


        if (((SketchShape) draggableShape).isContainInUnion()) {

            for (SketchShape sketchShape : ((SketchShape) draggableShape).sketchShapeUnionOwner.getSketchShapesInUnion()) {
                if (this == sketchShape) continue;
                sketchShape.setTranslateX(sketchShape.getTranslateX() + deltaX);
                sketchShape.setTranslateY(sketchShape.getTranslateY() + deltaY);
            }

        }



    }

    public void calculateMyJoints() {
    }

    ;

    public void calculateAllJoints() {
    }

    ;


    public static Point2D rotatePoint(Point2D point, Point2D pivotPoint, double angle) {
        double pivotX = pivotPoint.getX();
        double pivotY = pivotPoint.getY();

        double x = point.getX();
        double y = point.getY();

        double newX = (x - pivotX) * Math.cos(Math.toRadians(angle)) - (y - pivotY) * Math.sin(Math.toRadians(angle)) + pivotX;
        double newY = (x - pivotX) * Math.sin(Math.toRadians(angle)) + (y - pivotY) * Math.cos(Math.toRadians(angle)) + pivotY;

        return new Point2D(newX, newY);
    }

    public static void rotatePolygon(Polygon polygon, Point2D pivotPoint, double angle) {
        for (int i = 0; i < polygon.getPoints().size(); i += 2) {
            double originX = polygon.getPoints().get(i).doubleValue();
            double originY = polygon.getPoints().get(i + 1).doubleValue();
            double newX = (originX - pivotPoint.getX()) * Math.cos(Math.toRadians(angle)) - (originY - pivotPoint.getY()) * Math.sin(Math.toRadians(angle)) + pivotPoint.getX();
            double newY = (originX - pivotPoint.getX()) * Math.sin(Math.toRadians(angle)) + (originY - pivotPoint.getY()) * Math.cos(Math.toRadians(angle)) + pivotPoint.getY();
            polygon.getPoints().set(i, new Double(newX));
            polygon.getPoints().set(i + 1, new Double(newY));
        }
    }


}
