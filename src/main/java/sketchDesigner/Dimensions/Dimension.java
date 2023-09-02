package sketchDesigner.Dimensions;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import sketchDesigner.Shapes.SketchShape;
import sketchDesigner.SketchDesigner;

public abstract class Dimension extends Pane {

    public static final Color NORMAL_COLOR = Color.LINEN;
    public static final Color SELECTED_COLOR = Color.ORANGE;

    SketchShape shape;

    Polygon dimension = new Polygon();
    Label dimensionLabel = new Label();
    Color dimensionColor;

    boolean selected = false;


    Dimension(SketchShape shape) {
        this.shape = shape;

        //this.translateXProperty().bind(shape.translateXProperty());
        //this.translateYProperty().bind(shape.translateYProperty());


        show();


    }

    public Dimension() {

        dimensionColor = NORMAL_COLOR;


    }

    public abstract void moveDimension(double value);

    public abstract void setDimensionOffset(double offset);

    public abstract double getDimensionOffset();

    public void show() {
        dimension.setVisible(true);
        dimensionLabel.setVisible(true);
        this.setMouseTransparent(false);
    }

    public void hide() {
        dimension.setVisible(false);
        dimensionLabel.setVisible(false);
        this.setMouseTransparent(true);
    }

    public void delete() {
        dimension.setVisible(false);
        dimensionLabel.setVisible(false);
        this.setMouseTransparent(true);
        SketchDesigner.getSketchPane().getChildren().remove(dimension);
        SketchDesigner.getSketchPane().getChildren().remove(dimensionLabel);
    }

    public abstract void refreshView();

    public void selectDimension(boolean selected) {
        this.selected = selected;

        if (selected) {
            dimension.setStroke(SELECTED_COLOR);
            dimensionLabel.setTextFill(SELECTED_COLOR);
            //dimensionLabel.setFont(new Font(10));

        } else {
            dimension.setStroke(dimensionColor);
            dimensionLabel.setTextFill(dimensionColor);
            //dimensionLabel.setFont(new Font(10));
        }
    }

    public void changeColor(Color color) {
        dimension.setStroke(color);
        dimensionLabel.setTextFill(color);

        getChildren().forEach(node -> {
            if (node instanceof Line) {
                Line line = (Line) node;
                line.setStroke(color);
            }
        });
    }

    public boolean isSelected() {
        return selected;
    }
}
