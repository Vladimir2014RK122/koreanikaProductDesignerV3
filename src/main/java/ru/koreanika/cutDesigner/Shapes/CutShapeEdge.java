package ru.koreanika.cutDesigner.Shapes;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.CutPane;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class CutShapeEdge extends CutObject {

    private CutShape cutShapeOwner = null;
    Color DISCONNECT_COLOR = Color.BLUEVIOLET;
    private ArrayList<ConnectPoint> connectPoints = new ArrayList<>();

    Point2D startCoordinate = null;


    public CutShapeEdge(CutShape cutShapeOwner) {

        this.cutShapeOwner = cutShapeOwner;

    }

    @Override
    public Color getShapeColor() {
        return DISCONNECT_COLOR;
    }

    @Override
    public void setShapeColor(Color color) {
        shapeColor = color;
    }

    @Override
    protected void onMousePressedCenterArea(MouseEvent event) {
        double scale = CutPane.getCutPaneScale();//SketchDesigner.getSketchPane().getScaleX();
        orgSceneX = event.getSceneX() / scale;
        orgSceneY = event.getSceneY() / scale;
        orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
        orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
        ((Pane) (event.getSource())).toFront();
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

        if (cutShapeOwner.isSaveMaterialImage()) {

            for (CutShapeEdge cutShapeEdge : cutShapeOwner.getCutShapeEdgesList()) {
                if (cutShapeEdge == this) continue;
                cutShapeEdge.setTranslateX(cutShapeEdge.getTranslateX() - oldNewTranslateX);
                cutShapeEdge.setTranslateY(cutShapeEdge.getTranslateY() - oldNewTranslateY);
                cutShapeEdge.toFront();
            }
            cutShapeOwner.setTranslateX(cutShapeOwner.getTranslateX() - oldNewTranslateX);
            cutShapeOwner.setTranslateY(cutShapeOwner.getTranslateY() - oldNewTranslateY);
        }


        if (cutShapeOwner.isContainInUnion()) {
            ArrayList<CutShape> cutShapesInUnion = new ArrayList<>();
            for (CutShape cutShape : cutShapeOwner.getCutShapeUnionOwner().getCutShapesInUnionList()) {
                int number = cutShape.getShapeNumber();
                if (number == cutShapeOwner.getShapeNumber()) continue;
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
        setTranslateX(newTranslateX);
        setTranslateY(newTranslateY);
        event.consume();
    }


    public CutShape getOwner() {
        return cutShapeOwner;
    }

    public void setConnectPoints(ArrayList<ConnectPoint> connectPoints) {
        this.connectPoints = connectPoints;
    }

    public ArrayList<ConnectPoint> getConnectPoints() {
        return connectPoints;
    }

    public void setStartCoordinate(Point2D startCoordinate) {
        this.startCoordinate = startCoordinate;
    }

    public Point2D getStartCoordinate() {
        return startCoordinate;
    }

    public double getEdgeSquare() {
        return getPolygonSquare(polygon);
    }

    @Override
    public void showConnectionPoints() {
        //if(this instanceof CutShapeEdge)System.out.println(connectPoints.size());
        for (ConnectPoint p : connectPoints) {
            p.show();
        }
    }

    public Point2D getRotationPivot() {
        Point2D pivot = new Point2D(polygon.getBoundsInParent().getWidth() / 2, polygon.getBoundsInParent().getHeight() / 2);
        //Point2D pivot = new Point2D(getBoundsInLocal().getWidth()/2, getBoundsInLocal().getHeight()/2);

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
        //showConnectionPoints();
    }

    public void setGlobalPivot(Point2D pivot) {

    }

    @Override
    public void rotateShapeGlobal(double angle, Point2D pivot) {

        Point2D rotationPoint = this.sceneToLocal(CutDesigner.getInstance().getCutPane().localToScene(pivot));
        rotateTransformGlobal.setPivotX(rotationPoint.getX());
        rotateTransformGlobal.setPivotY(rotationPoint.getY());
        rotateTransformGlobal.setAngle(rotateTransformGlobal.getAngle() + angle);

        for (ConnectPoint cp : connectPoints) {
            cp.setRotate((-1) * rotateTransformGlobal.getAngle());
        }
    }

    @Override
    public void hideConnectionPoints() {
        for (ConnectPoint p : connectPoints) {
            p.hide();
        }
    }

    @Override
    public Material getMaterial() {
        return getOwner().getMaterial();
    }

    @Override
    public int getDepth() {
        return getOwner().getDepth();
    }


}
