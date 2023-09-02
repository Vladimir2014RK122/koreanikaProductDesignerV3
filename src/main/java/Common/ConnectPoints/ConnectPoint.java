package Common.ConnectPoints;

import Common.CommonShape;
import Common.Connectible;
import cutDesigner.CutDesigner;
import cutDesigner.Shapes.CutShape;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import sketchDesigner.Shapes.SketchObject;
import sketchDesigner.Shapes.SketchShape;
import sketchDesigner.Shapes.SketchShapeUnion;
import sketchDesigner.SketchDesigner;

public abstract class ConnectPoint extends Polygon {

    public static ConnectPoint draggablePoint = null;

    boolean pointAnchor = false;

    Connectible pointOwner;

    Point2D setPoint = new Point2D(0.0, 0.0);
    Point2D setPointShift = new Point2D(0.0, 0.0);

    private boolean selectionMode = false;


    public ConnectPoint(Connectible pointOwner) {
        this.pointOwner = pointOwner;

        hide();

        this.setOnMouseClicked(event -> onMouseClicked(event));
        this.setOnMouseEntered(event -> onMouseEntered(event));
        this.setOnMouseExited(event -> onMouseExited(event));
        this.setOnMousePressed(event -> onMousePressed(event));
        this.setOnMouseReleased(event -> onMouseReleased(event));
        this.setOnMouseDragEntered(event -> onMouseDragEntered(event));
        this.setOnMouseDragExited(event -> onMouseDragExited(event));
        this.setOnMouseDragReleased(event -> onMouseDragReleased(event));
    }

    public ConnectPoint() {
        //this.pointOwner = pointOwner;

        hide();

        this.setOnMouseClicked(event -> onMouseClicked(event));
        this.setOnMouseEntered(event -> onMouseEntered(event));
        this.setOnMouseExited(event -> onMouseExited(event));
        this.setOnMousePressed(event -> onMousePressed(event));
        this.setOnMouseReleased(event -> onMouseReleased(event));
        this.setOnMouseDragEntered(event -> onMouseDragEntered(event));
        this.setOnMouseDragExited(event -> onMouseDragExited(event));
        this.setOnMouseDragReleased(event -> onMouseDragReleased(event));
    }

    public void setPointOwner(Connectible pointOwner) {
        this.pointOwner = pointOwner;
    }

    private void onMouseEntered(MouseEvent event) {
        setStroke(Color.GREEN);
        //setVisible(true);
        event.consume();
    }

    private void onMouseExited(MouseEvent event) {
        //setVisible(false);
        if (!selectionMode) {
            setStroke(Color.TRANSPARENT);
        } else {
            event.consume();
        }

    }

    private void onMousePressed(MouseEvent event) {
        System.out.println("press");
        if (!selectionMode) {
            pointAnchor = true;
            setFill(Color.GREEN);
            this.setOnMouseExited(null);
            setMouseTransparent(true);
            draggablePoint = this;
        } else {
            setFill(Color.GREEN);

            event.consume();
        }


    }

    private void onMouseReleased(MouseEvent e) {
        System.out.println("release");
        if (!selectionMode) {
            pointAnchor = true;
            setFill(Color.TRANSPARENT);

            this.setOnMouseExited(event -> onMouseExited(event));
            setMouseTransparent(false);
            draggablePoint = null;
        } else {
            if (this.getParent() instanceof SketchObject)
                SketchDesigner.addPointForDimension((CornerConnectPoint) this);
            else if (this.getParent() instanceof CutShape) CutDesigner.getInstance().addPointForDimension((CornerConnectPoint) this);
            e.consume();
        }

    }

    private void onMouseClicked(MouseEvent event) {
        System.out.println("Clicked");

        event.consume();
    }

    private void onMouseDragEntered(MouseDragEvent event) {
        setFill(Color.GREEN);
        event.consume();
    }

    private void onMouseDragExited(MouseDragEvent event) {
        setFill(Color.TRANSPARENT);
        event.consume();
    }

    private void onMouseDragReleased(MouseDragEvent event) {
        System.out.println("connect !");
        if (draggablePoint == null) return;
//        if(draggablePoint.getPointOwner() instanceof SketchShape){
//            SketchDesigner.connectShapeToShape(ConnectPoint.draggablePoint, (ConnectPoint) event.getSource());
//
//        }else if(draggablePoint.getPointOwner() instanceof CutShape){
//            CutDesigner.connectShapeToShape(ConnectPoint.draggablePoint, (ConnectPoint) event.getSource());
//        }
        draggablePoint.getPointOwner().connectShapeToShape(ConnectPoint.draggablePoint, (ConnectPoint) event.getSource());


        System.out.println("ConnectPoint.draggablePoint =" + ConnectPoint.draggablePoint.getTranslateX());
        //System.out.println("(ConnectPoint) event.getSource() =" + ((ConnectPoint) event.getSource()).getTranslateX());


    }

    public void show() {
        setStroke(Color.GREEN);
    }

    public void hide() {
        setStroke(Color.TRANSPARENT);
        setFill(Color.TRANSPARENT);
    }

    public Connectible getPointOwner() {
        return pointOwner;
    }


    public abstract void changeSetPoint(Point2D setPoint);

    public abstract void changeSetPointShift(Point2D setPoint);

    public abstract void changeSide(Double side);

    public Point2D getSetPoint() {
        return setPoint;
    }

    public Point2D getSetPointShift() {
        return setPointShift;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        if (selectionMode) {
            show();
        } else {
            hide();
        }
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }
}
