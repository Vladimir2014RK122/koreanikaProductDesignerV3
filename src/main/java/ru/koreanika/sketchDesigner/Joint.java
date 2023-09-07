package ru.koreanika.sketchDesigner;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;

public class Joint {

    SketchShape shapeOwner;
    SketchShape anotherShape;
    Line lineForJoint;

    JointType type;

    double len;
    Point2D startPoint;
    Point2D endPoint;

    public Joint(SketchShape shapeOwner, SketchShape anotherShape, Line lineForJoint, JointType type) {
        this.shapeOwner = shapeOwner;
        this.anotherShape = anotherShape;
        this.lineForJoint = lineForJoint;
        this.type = type;
    }

    public Line getLineForJoint() {
        return lineForJoint;
    }

    public void setLineForJoint(Line lineForJoint) {
        this.lineForJoint = lineForJoint;
    }

    public Point2D getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point2D startPoint) {
        this.startPoint = startPoint;
    }

    public double getLen() {
        return len;
    }

    public SketchShape getAnotherShape() {
        return anotherShape;
    }

    public void setLen(double len) {
        this.len = len;
    }

    public Point2D getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point2D endPoint) {
        this.endPoint = endPoint;
    }

    public JointType getType() {
        return type;
    }

    public void setType(JointType type) {
        this.type = type;
    }

    public SketchShape getShapeOwner() {
        return shapeOwner;
    }

    @Override
    public String toString() {
        return "Joint{" +
                "shapeOwner=" + shapeOwner.getShapeNumber() +
                ", type=" + type +
                ", len=" + len +
                "}";
    }

    public enum JointType {
        STRAIGHT,
        OBLIQUE
    }
}
