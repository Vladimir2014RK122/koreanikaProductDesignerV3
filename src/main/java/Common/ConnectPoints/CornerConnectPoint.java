package Common.ConnectPoints;

import Common.CommonShape;
import Common.Connectible;
//import com.sun.javafx.geom.Shape;
import javafx.geometry.Point2D;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class CornerConnectPoint extends ConnectPoint {

    private double side = 10;


    public CornerConnectPoint(Connectible pointOwner) {
        super(pointOwner);

        this.getPoints().addAll(
                0.0, 0.0,
                side, 0.0,
                side, side,
                0.0, side);

        /*this.getPoints().addAll(
                -WIDTH/2,-HEIGHT/2,
                WIDTH/2, -HEIGHT/2,
                WIDTH/2, HEIGHT/2,
                -WIDTH/2, HEIGHT/2);*/

        this.setFill(Color.TRANSPARENT);
        this.setStrokeWidth(1.0);
        this.setStroke(Color.GREEN);
        this.setStrokeType(StrokeType.INSIDE);

    }

    public CornerConnectPoint() {
        super();

        this.getPoints().addAll(
                0.0, 0.0,
                side, 0.0,
                side, side,
                0.0, side);

        /*this.getPoints().addAll(
                -WIDTH/2,-HEIGHT/2,
                WIDTH/2, -HEIGHT/2,
                WIDTH/2, HEIGHT/2,
                -WIDTH/2, HEIGHT/2);*/

        this.setFill(Color.TRANSPARENT);
        this.setStrokeWidth(1.0);
        this.setStroke(Color.GREEN);

    }

    @Override
    public void changeSetPoint(Point2D setPoint) {
        this.setPoint = new Point2D(setPoint.getX(), setPoint.getY());
        this.setTranslateX(setPoint.getX() - side / 2);
        this.setTranslateY(setPoint.getY() - side / 2);
    }

    @Override
    public void changeSetPointShift(Point2D setPointShift) {
        this.setPointShift = new Point2D(setPointShift.getX(), setPointShift.getY());
    }

    @Override
    public void changeSide(Double side) {
        this.side = side;
        this.getPoints().clear();
        this.getPoints().addAll(
                0.0, 0.0,
                side, 0.0,
                side, side,
                0.0, side);

        this.setStrokeWidth(1.0 * (side / 10.0));

        changeSetPoint(setPoint);

    }

}
