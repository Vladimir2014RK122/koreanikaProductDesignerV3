package ru.koreanika.sketchDesigner.Dimensions;

import ru.koreanika.Common.ConnectPoints.CornerConnectPoint;
//import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import org.json.simple.JSONObject;
import ru.koreanika.sketchDesigner.Shapes.*;
import ru.koreanika.sketchDesigner.SketchDesigner;

public class LinearDimension extends Dimension {

    public static int VERTICAL_TYPE = 0;
    public static int HORIZONTAL_TYPE = 1;

    public static int SHAPE = 0;
    public static int UNION = 1;

    public static int SKETCH_DESIGNER_DIMENSION = 0;
    public static int CUT_DESIGNER_DIMENSION = 1;

    int typeDimension = VERTICAL_TYPE;
    int dimensionDesigner = SKETCH_DESIGNER_DIMENSION;
    int parent1Class = SHAPE;
    int parent2Class = SHAPE;
    int parent1Number;
    int parent2Number;
    //    ElementTypes parent1ElementType;
//    ElementTypes parent2ElementType;
    int connectPoint1Index;
    int connectPoint2Index;

    double x1, y1;
    double x2, y2;

    double hDimOrgSceneY;
    double hDimOrgTranslateY;
    double offsetYHDim = 50;
    double vDimOrgSceneX;
    double vDimOrgTranslateX;
    double offsetXVDim = 50;


    double vDimOffset;
    double hDimOffset;


    CornerConnectPoint connectPoint1, connectPoint2;
    Pane workPane;
    Node parent1;
    Node parent2;

    MenuItem delMenuItem = new MenuItem("delete");
    ContextMenu contextMenu = new ContextMenu(delMenuItem);

    public LinearDimension(JSONObject object) {

        initFromJson(object);

        if (dimensionDesigner == SKETCH_DESIGNER_DIMENSION) {

            this.parent2 = SketchDesigner.getSketchShape(parent2Number);
            if (parent1Class == SHAPE) {
                this.parent1 = SketchDesigner.getSketchShape(parent1Number);
                this.connectPoint1 = (CornerConnectPoint) ((SketchShape) parent1).getConnectPoints().get(connectPoint1Index);
            } else {
//                this.parent1 = SketchDesigner.getSketchShapeUnion(parent1Number);
//                this.connectPoint1 = (CornerConnectPoint) ((SketchShapeUnion)parent1).getConnectPoints().get(connectPoint1Index);
            }

            if (parent2Class == SHAPE) {
                this.parent2 = SketchDesigner.getSketchShape(parent2Number);
                System.out.println("this.parent2 = " + this.parent2);
                System.out.println("connectPoint2Index = " + connectPoint2Index);
                this.connectPoint2 = (CornerConnectPoint) ((SketchShape) parent2).getConnectPoints().get(connectPoint2Index);
            } else {
//                this.parent2 = SketchDesigner.getSketchShapeUnion(parent2Number);
//                this.connectPoint2 = (CornerConnectPoint) ((SketchShapeUnion)parent2).getConnectPoints().get(connectPoint2Index);
            }

            this.workPane = (Pane) connectPoint1.getParent().getParent();
        } else if (dimensionDesigner == CUT_DESIGNER_DIMENSION) {
//            this.parent1 = CutDesigner.getCutShape(parent1Number, parent1ElementType);
//            this.parent2 = CutDesigner.getCutShape(parent2Number, parent2ElementType);
            this.connectPoint1 = (CornerConnectPoint) ((CutShape) parent1).getConnectPoints().get(connectPoint1Index);
            this.connectPoint2 = (CornerConnectPoint) ((CutShape) parent2).getConnectPoints().get(connectPoint2Index);
            this.workPane = (Pane) connectPoint1.getParent().getParent();
            this.dimensionColor = Color.BLACK;
        }


        //setStyle("-fx-background-color: Blue");
        if (typeDimension == VERTICAL_TYPE) {
            createVerticalDimension();
        } else {
            createHorizontalDimension();
        }

        Node shape1 = connectPoint1.getParent();
        Node shape2 = connectPoint2.getParent();

        shape1.translateXProperty().addListener((observable, oldValue, newValue) -> refreshView());
        shape1.translateYProperty().addListener((observable, oldValue, newValue) -> refreshView());
        if (!shape1.equals(shape2)) {
            shape2.translateXProperty().addListener((observable, oldValue, newValue) -> refreshView());
            shape2.translateYProperty().addListener((observable, oldValue, newValue) -> refreshView());
        }

        connectPoint1.translateXProperty().addListener((observable, oldValue, newValue) -> refreshView());
        connectPoint1.translateYProperty().addListener((observable, oldValue, newValue) -> refreshView());
        connectPoint2.translateXProperty().addListener((observable, oldValue, newValue) -> refreshView());
        connectPoint2.translateYProperty().addListener((observable, oldValue, newValue) -> refreshView());


        LinearDimension thisDim = this;
        dimensionLabel.setContextMenu(contextMenu);
        delMenuItem.setOnAction(event -> {
            workPane.getChildren().remove(thisDim);
            SketchDesigner.getAllDimensions().remove(thisDim);
        });

    }


    public LinearDimension(CornerConnectPoint connectPoint1, CornerConnectPoint connectPoint2, int typeDimension) {
        super();

        this.typeDimension = typeDimension;
        this.connectPoint1 = connectPoint1;
        this.connectPoint2 = connectPoint2;
        this.workPane = (Pane) connectPoint1.getParent().getParent();
        this.parent1 = connectPoint1.getParent();
        this.parent2 = connectPoint2.getParent();

        if (this.parent1 instanceof SketchShape) {
            parent1Class = SHAPE;
            dimensionDesigner = SKETCH_DESIGNER_DIMENSION;
            parent1Number = ((SketchShape) parent1).getShapeNumber();
//            parent1ElementType = ((SketchShape)parent1).getElementType();
            connectPoint1Index = ((SketchShape) parent1).getConnectPoints().indexOf(connectPoint1);

        } else if (this.parent1 instanceof CutShape) {
            parent1Class = SHAPE;
            dimensionDesigner = CUT_DESIGNER_DIMENSION;
            parent1Number = ((CutShape) parent1).getShapeNumber();
            connectPoint1Index = ((CutShape) parent1).getConnectPoints().indexOf(connectPoint1);

            this.dimensionColor = Color.BLACK;
        }

        if (this.parent2 instanceof SketchShape) {
            parent2Class = SHAPE;
            parent2Number = ((SketchShape) parent2).getShapeNumber();
//            parent2ElementType = ((SketchShape)parent2).getElementType();
            connectPoint2Index = ((SketchShape) parent2).getConnectPoints().indexOf(connectPoint2);
        } else if (this.parent2 instanceof CutShape) {
            parent2Class = SHAPE;
            parent2Number = ((CutShape) parent2).getShapeNumber();
            connectPoint2Index = ((CutShape) parent2).getConnectPoints().indexOf(connectPoint2);
        }

        //setStyle("-fx-background-color: Blue");
        if (typeDimension == VERTICAL_TYPE) {
            createVerticalDimension();
        } else {
            createHorizontalDimension();
        }

        Node shape1 = connectPoint1.getParent();
        Node shape2 = connectPoint2.getParent();

        shape1.translateXProperty().addListener((observable, oldValue, newValue) -> refreshView());
        shape1.translateYProperty().addListener((observable, oldValue, newValue) -> refreshView());
        if (!shape1.equals(shape2)) {
            shape2.translateXProperty().addListener((observable, oldValue, newValue) -> refreshView());
            shape2.translateYProperty().addListener((observable, oldValue, newValue) -> refreshView());
        }

        connectPoint1.translateXProperty().addListener((observable, oldValue, newValue) -> refreshView());
        connectPoint1.translateYProperty().addListener((observable, oldValue, newValue) -> refreshView());
        connectPoint2.translateXProperty().addListener((observable, oldValue, newValue) -> refreshView());
        connectPoint2.translateYProperty().addListener((observable, oldValue, newValue) -> refreshView());


        LinearDimension thisDim = this;
        dimensionLabel.setContextMenu(contextMenu);
        delMenuItem.setOnAction(event -> {
            workPane.getChildren().remove(thisDim);
            SketchDesigner.getAllDimensions().remove(thisDim);
        });
    }

    private void createVerticalDimension() {

        double pointWidth = connectPoint1.getBoundsInLocal().getWidth();


        //Bounds b = SketchDesigner.getSketchPane().sceneToLocal(connectPoint1.localToScene(connectPoint1.getBoundsInLocal()));
        x1 = connectPoint1.getTranslateX() + pointWidth / 2 + connectPoint1.getParent().getTranslateX();
        y1 = connectPoint1.getTranslateY() + pointWidth / 2 + connectPoint1.getParent().getTranslateY();

        x2 = connectPoint2.getTranslateX() + pointWidth / 2 + connectPoint2.getParent().getTranslateX();
        y2 = connectPoint2.getTranslateY() + pointWidth / 2 + connectPoint2.getParent().getTranslateY();


        double size = Math.abs(y2 - y1);

//        Line line1 = new Line(-10,(y1<y2)?(-2):(size-2), ((x1>x2)? (x1-x2 + offsetXVDim-3):(offsetXVDim-3)), (y1<y2)?(-2):(size-2));
//        Line line2 = new Line(-10,(y2<y1)?(-2):(size-2), ((x2>x1)? (x2-x1 + offsetXVDim-3):(offsetXVDim-3)), (y2<y1)?(-2):(size-2));

        dimensionLabel = new Label(String.format("%.0f", size * 10.0));

        //vDimLabel.setRotate(90);
        dimensionLabel.setAlignment(Pos.CENTER);
        dimensionLabel.setFont(Font.font(10));
        dimensionLabel.setTextFill(dimensionColor);

        Text text = new Text(dimensionLabel.getText());
        text.setFont(dimensionLabel.getFont());
        double labelWidth = text.getBoundsInLocal().getWidth();
        double labelHeight = text.getBoundsInLocal().getHeight();


        Line line1 = new Line(-5, (y1 < y2) ? (0) : (size), ((x1 > x2) ? (x1 - x2 + offsetXVDim) : (offsetXVDim)), (y1 < y2) ? (0) : (size));
        Line line2 = new Line(-5, (y2 < y1) ? (0) : (size), ((x2 > x1) ? (x2 - x1 + offsetXVDim) : (offsetXVDim)), (y2 < y1) ? (0) : (size));
        line1.setStroke(dimensionColor);
        line1.setStrokeType(StrokeType.CENTERED);
        line2.setStroke(dimensionColor);
        line2.setStrokeType(StrokeType.CENTERED);
        line1.setStrokeWidth(0.2);
        line2.setStrokeWidth(0.2);

        line1.setMouseTransparent(true);
        line2.setMouseTransparent(true);

        Point2D[] pointsVDimNormal = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(-1.0, 5.0),
                new Point2D(0.0, 0.0),
                new Point2D(1.0, 5.0),
                new Point2D(0.0, 0.0),
                new Point2D(0.0, size - 0),
                new Point2D(-1.0, size - 5.0),
                new Point2D(0.0, size - 0),
                new Point2D(1.0, size - 5.0),
                new Point2D(0.0, size - 0)
        };

        Point2D[] pointsVDimSmall = new Point2D[]{
                new Point2D(0.0, size + 10.0),
                new Point2D(0.0, size),
                new Point2D(-1.0, size + 5),
                new Point2D(0.0, size),
                new Point2D(1.0, size + 5),
                new Point2D(0.0, size),
                new Point2D(0.0, 0.0),
                new Point2D(0.0, -(labelWidth + 10)),
                new Point2D(0.0, 0.0),
                new Point2D(-1.0, -5.0),
                new Point2D(0.0, 0.0),
                new Point2D(1.0, -5.0),
                new Point2D(0.0, 0.0),
        };

        dimension = new Polygon();
        System.out.println("size + 20 = " + (size + 20));
        System.out.println("labelWidth = " + labelWidth);
        if (size - 20 > labelWidth) {
            for (Point2D p : pointsVDimNormal) {
                dimension.getPoints().add(p.getX());
                dimension.getPoints().add(p.getY());

            }

            dimensionLabel.setTranslateY(size / 2 - labelHeight / 2);
            dimensionLabel.setTranslateX(-(labelWidth / 2 + labelHeight / 2));
            Rotate rotate = new Rotate(-90, labelWidth / 2, labelHeight / 2);
            dimensionLabel.getTransforms().add(rotate);


        } else {
            for (Point2D p : pointsVDimSmall) {
                dimension.getPoints().add(p.getX());
                dimension.getPoints().add(p.getY());

            }

            dimensionLabel.setTranslateY(-(labelWidth + 8));
            dimensionLabel.setTranslateX(-(labelWidth / 2 + labelHeight / 2));
            Rotate rotate = new Rotate(-90, labelWidth / 2, labelHeight / 2);
            dimensionLabel.getTransforms().add(rotate);
        }

        dimension.setFill(Color.RED);
        dimension.setStroke(dimensionColor);
        dimension.setStrokeWidth(0.2);
        dimension.setStrokeType(StrokeType.CENTERED);

        this.getChildren().clear();
        this.setTranslateX(((x1 > x2) ? x2 : x1) - offsetXVDim);
        this.setTranslateY(((y1 > y2) ? y2 : y1) - 0);


        if (this.getTranslateX() > x1) {
            line1.setStartX(5);
        } else {
            line1.setStartX(-5);
        }

        if (this.getTranslateX() > x2) {
            line2.setStartX(5);
        } else {
            line2.setStartX(-5);
        }

        /*
        if(this.getTranslateX() > ((x1>x2)? x1:x2)){ dimensionLabel.setTranslateX(-40.0); }
        else{ dimensionLabel.setTranslateX(-20.0);}*/

        this.getChildren().addAll(dimension, dimensionLabel, line1, line2);

        this.setPickOnBounds(false);

        this.setPrefWidth(0);

        dimensionLabel.setOnMouseClicked(event -> {

            if (this.getParent() == SketchDesigner.getSketchPane()) {
                for (LinearDimension ld : SketchDesigner.getAllDimensions()) {
                    ld.selectDimension(false);
                }
            } else if (this.getParent() == CutDesigner.getInstance().getCutPane()) {
                for (LinearDimension ld : CutDesigner.getInstance().getAllDimensions()) {
                    ld.selectDimension(false);
                }
            }
            selectDimension(true);
            if (event.getButton() == MouseButton.PRIMARY) event.consume();
        });
        dimensionLabel.setOnMouseEntered(event -> {

            dimension.setStroke(Color.ORANGE);
            dimensionLabel.setTextFill(Color.ORANGE);
            event.consume();
        });
        dimensionLabel.setOnMouseExited(event -> {
            if (!selected) {
                dimension.setStroke(dimensionColor);
                dimensionLabel.setTextFill(dimensionColor);
            } else {
                dimension.setStroke(Color.ORANGE);
                dimensionLabel.setTextFill(Color.ORANGE);
            }
            event.consume();
        });
        dimensionLabel.setOnMousePressed(event -> {
            double scale = SketchDesigner.getSketchPaneScale();

            vDimOrgSceneX = event.getSceneX() / scale;
            vDimOrgTranslateX = ((Dimension) (((Label) (event.getSource())).getParent())).getTranslateX();

            event.consume();
            this.toFront();
        });
        dimensionLabel.setOnMouseReleased(event -> {
            event.consume();
            System.out.println("offsetXVDim = " + offsetXVDim);
        });
        dimensionLabel.setOnDragDetected(event -> {
            event.consume();
        });
        dimensionLabel.setOnMouseDragged(event -> {
            double scale = SketchDesigner.getSketchPaneScale();
            double offsetX = event.getSceneX() / scale - vDimOrgSceneX;
            double newTranslateX = vDimOrgTranslateX + offsetX;

            x1 = connectPoint1.getBoundsInParent().getMinX() + pointWidth / 2 + connectPoint1.getParent().getTranslateX() + 0;
            y1 = connectPoint1.getBoundsInParent().getMinY() + pointWidth / 2 + connectPoint1.getParent().getTranslateY() + 0;

            x2 = connectPoint2.getBoundsInParent().getMinX() + pointWidth / 2 + connectPoint2.getParent().getTranslateX() + 0;
            y2 = connectPoint2.getBoundsInParent().getMinY() + pointWidth / 2 + connectPoint2.getParent().getTranslateY() + 0;


            offsetXVDim = ((x1 > x2) ? x2 : x1) - newTranslateX;

            this.setTranslateX(newTranslateX);

            line1.setEndX(((x1 > x2) ? (x1 - x2 + offsetXVDim) : (offsetXVDim)));
            line2.setEndX(((x2 > x1) ? (x2 - x1 + offsetXVDim) : (offsetXVDim)));


            if (this.getTranslateX() > x1) {
                line1.setStartX(5);
            } else {
                line1.setStartX(-5);
            }

            if (this.getTranslateX() > x2) {
                line2.setStartX(5);
            } else {
                line2.setStartX(-5);
            }

//            if(newTranslateX > ((x1>x2)? x1:x2)){ dimensionLabel.setTranslateX(-40.0); }
//            else{ dimensionLabel.setTranslateX(-20.0);}

            dimensionLabel.setTranslateX(-(labelWidth / 2 + labelHeight / 2));

            event.consume();
        });
    }

    private void createHorizontalDimension() {

        double pointWidth = connectPoint1.getBoundsInLocal().getWidth();

        x1 = connectPoint1.getBoundsInParent().getMinX() + pointWidth / 2 + parent1.getTranslateX() + 0;
        y1 = connectPoint1.getBoundsInParent().getMinY() + pointWidth / 2 + parent1.getTranslateY() + 0;

        x2 = connectPoint2.getBoundsInParent().getMinX() + pointWidth / 2 + parent2.getTranslateX() + 0;
        y2 = connectPoint2.getBoundsInParent().getMinY() + pointWidth / 2 + parent2.getTranslateY() + 0;

        double size = Math.abs(x2 - x1);

        Line line1 = new Line((x1 < x2) ? (-0) : (size - 0), -5, (x1 < x2) ? (0) : (size - 0), ((y1 > y2) ? (y1 - y2 + offsetYHDim - 0) : (offsetYHDim - 0)));
        Line line2 = new Line((x2 < x1) ? (-0) : (size - 0), -5, (x2 < x1) ? (0) : (size - 0), ((y2 > y1) ? (y2 - y1 + offsetYHDim - 0) : (offsetYHDim - 0)));
        line1.setFill(Color.RED);
        line2.setFill(Color.RED);
        line1.setStroke(dimensionColor);
        line2.setStroke(dimensionColor);
        line1.setStrokeType(StrokeType.CENTERED);
        line2.setStrokeType(StrokeType.CENTERED);
        line1.setStrokeWidth(0.2);
        line2.setStrokeWidth(0.2);

        line1.setMouseTransparent(true);
        line2.setMouseTransparent(true);

        dimensionLabel = new Label(String.format("%.0f", size * 10.0));

        //vDimLabel.setRotate(90);
        dimensionLabel.setAlignment(Pos.CENTER);
        dimensionLabel.setFont(Font.font(10));
        dimensionLabel.setTextFill(dimensionColor);

        Text text = new Text(dimensionLabel.getText());
        text.setFont(dimensionLabel.getFont());
        double labelWidth = text.getBoundsInLocal().getWidth();
        double labelHeight = text.getBoundsInLocal().getHeight();


        Point2D[] pointsHDimNormal = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(5.0, 1.0),
                new Point2D(0.0, 0.0),
                new Point2D(5.0, -1.0),
                new Point2D(0.0, 0.0),
                new Point2D(size - 0, 0.0),
                new Point2D(size - 5.0, -1.0),
                new Point2D(size - 0, 0.0),
                new Point2D(size - 5.0, 1.0),
                new Point2D(size - 0, 0.0)
        };

        Point2D[] pointsHDimSmall = new Point2D[]{
                new Point2D(-10.0, 0.0),
                new Point2D(0.0, 0.0),
                new Point2D(-5.0, -1.0),
                new Point2D(0.0, 0.0),
                new Point2D(-5.0, 1.0),
                new Point2D(0.0, 0.0),
                new Point2D(size + (labelWidth + 10), 0.0),
                new Point2D(size, 0.0),
                new Point2D(size + 5.0, -1.0),
                new Point2D(size, 0.0),
                new Point2D(size + 5.0, 1.0),
                new Point2D(size, 0.0),
                new Point2D(0.0, 0.0)
        };

        dimension = new Polygon();
        if (size - 20 > labelWidth) {
            for (Point2D p : pointsHDimNormal) {
                dimension.getPoints().add(p.getX());
                dimension.getPoints().add(p.getY());
            }

            dimensionLabel.setTranslateY(-labelHeight);
            dimensionLabel.setTranslateX(size / 2 - labelWidth / 2);

        } else {
            for (Point2D p : pointsHDimSmall) {
                dimension.getPoints().add(p.getX());
                dimension.getPoints().add(p.getY());
            }

            dimensionLabel.setTranslateY(-labelHeight);
            dimensionLabel.setTranslateX(size + 8);
        }

        dimension.setStrokeWidth(0.2);
        dimension.setStrokeType(StrokeType.CENTERED);


        //dimension.setFill(Color.RED);
        dimension.setStroke(dimensionColor);


        this.getChildren().clear();
        this.setTranslateY(((y1 > y2) ? y2 : y1) - offsetYHDim);
        this.setTranslateX(((x1 > x2) ? x2 : x1) - 0);


        if (this.getTranslateY() > y1) {
            line1.setStartY(5);
        } else {
            line1.setStartY(-5);
        }

        if (this.getTranslateY() > y2) {
            line2.setStartY(5);
        } else {
            line2.setStartY(-5);
        }


        this.getChildren().addAll(dimension, dimensionLabel, line1, line2);
        this.setPrefHeight(0);

        this.setPickOnBounds(false);

        dimensionLabel.setOnMouseClicked(event -> {

            if (this.getParent() == SketchDesigner.getSketchPane()) {
                for (LinearDimension ld : SketchDesigner.getAllDimensions()) {
                    ld.selectDimension(false);
                }
            } else if (this.getParent() == CutDesigner.getInstance().getCutPane()) {
                for (LinearDimension ld : CutDesigner.getInstance().getAllDimensions()) {
                    ld.selectDimension(false);
                }
            }
            selectDimension(true);
            if (event.getButton() == MouseButton.PRIMARY) event.consume();
        });
        dimensionLabel.setOnMouseEntered(event -> {

            dimension.setStroke(Color.ORANGE);
            dimensionLabel.setTextFill(Color.ORANGE);
            event.consume();
        });
        dimensionLabel.setOnMouseExited(event -> {
            if (!selected) {
                dimension.setStroke(dimensionColor);
                dimensionLabel.setTextFill(dimensionColor);
            } else {
                dimension.setStroke(Color.ORANGE);
                dimensionLabel.setTextFill(Color.ORANGE);
            }
            event.consume();
        });
        dimensionLabel.setOnMousePressed(event -> {
            double scale = SketchDesigner.getSketchPaneScale();

            hDimOrgSceneY = event.getSceneY() / scale;
            hDimOrgTranslateY = ((Dimension) (((Label) (event.getSource())).getParent())).getTranslateY();


            event.consume();
            this.toFront();
        });
        dimensionLabel.setOnMouseReleased(event -> {

            event.consume();
        });
        dimensionLabel.setOnDragDetected(event -> {

            event.consume();
        });
        dimensionLabel.setOnMouseDragged(event -> {
            double scale = SketchDesigner.getSketchPaneScale();
            double offsetY = event.getSceneY() / scale - hDimOrgSceneY;
            double newTranslateY = hDimOrgTranslateY + offsetY;


            x1 = connectPoint1.getBoundsInParent().getMinX() + pointWidth / 2 + parent1.getTranslateX() + 0;
            y1 = connectPoint1.getBoundsInParent().getMinY() + pointWidth / 2 + parent1.getTranslateY() + 0;

            x2 = connectPoint2.getBoundsInParent().getMinX() + pointWidth / 2 + parent2.getTranslateX() + 0;
            y2 = connectPoint2.getBoundsInParent().getMinY() + pointWidth / 2 + parent2.getTranslateY() + 0;


            offsetYHDim = ((y1 > y2) ? y2 : y1) - newTranslateY;

            this.setTranslateY(newTranslateY);


            line1.setEndY(((y1 > y2) ? (y1 - y2 + offsetYHDim - 0) : (offsetYHDim - 0)));
            line2.setEndY(((y2 > y1) ? (y2 - y1 + offsetYHDim - 0) : (offsetYHDim - 0)));

            if (this.getTranslateY() > y1) {
                line1.setStartY(5);
            } else {
                line1.setStartY(-5);
            }

            if (this.getTranslateY() > y2) {
                line2.setStartY(5);
            } else {
                line2.setStartY(-5);
            }

            dimensionLabel.setTranslateY(-labelHeight);

            event.consume();
        });
    }


    public CornerConnectPoint getConnectPoint1() {
        return connectPoint1;
    }

    public CornerConnectPoint getConnectPoint2() {
        return connectPoint2;
    }

    public void setDimensionColor(Color color) {
        this.dimensionColor = color;
        dimension.setStroke(dimensionColor);
        dimensionLabel.setTextFill(dimensionColor);
    }


    @Override
    public void moveDimension(double value) {

    }


    @Override
    public void setDimensionOffset(double offset) {
        if (typeDimension == VERTICAL_TYPE) {
            this.offsetXVDim = offset;
            this.setTranslateX(shape.getTranslateX() - offsetXVDim);
        } else if (typeDimension == HORIZONTAL_TYPE) {
            this.offsetYHDim = offset;
            this.setTranslateY(shape.getTranslateY() - offsetYHDim);
        }
    }

    @Override
    public double getDimensionOffset() {

        if (typeDimension == VERTICAL_TYPE) {
            return this.offsetXVDim;
        } else if (typeDimension == HORIZONTAL_TYPE) {
            return this.offsetYHDim;
        }
        return 0;
    }

    @Override
    public void refreshView() {
        this.getChildren().clear();
        if (this.typeDimension == VERTICAL_TYPE) createVerticalDimension();
        else if (this.typeDimension == HORIZONTAL_TYPE) createHorizontalDimension();

        System.out.println("REFRESH DIMENSION VIEW()  = " + dimensionLabel.getFont().getSize());
    }


    public JSONObject getJsonView() {
        JSONObject object = new JSONObject();

        object.put("parent1Number", parent1Number);
        object.put("parent2Number", parent2Number);
        object.put("parent1Class", parent1Class);
        object.put("parent2Class", parent2Class);

        object.put("typeDimension", typeDimension);
        object.put("dimensionDesigner", dimensionDesigner);
        object.put("connectPoint1Index", connectPoint1Index);
        object.put("connectPoint2Index", connectPoint2Index);
        object.put("offsetXVDim", offsetXVDim);
        object.put("offsetYHDim", offsetYHDim);

        return object;
    }

    public void initFromJson(JSONObject jsonObject) {
        parent1Number = ((Long) jsonObject.get("parent1Number")).intValue();
        parent2Number = ((Long) jsonObject.get("parent2Number")).intValue();
        parent1Class = ((Long) jsonObject.get("parent1Class")).intValue();
        parent2Class = ((Long) jsonObject.get("parent2Class")).intValue();
//        parent1ElementType = ElementTypes.valueOf(((String)jsonObject.get("parent1ElementType")));
//        parent2ElementType = ElementTypes.valueOf(((String)jsonObject.get("parent2ElementType")));
        typeDimension = ((Long) jsonObject.get("typeDimension")).intValue();
        dimensionDesigner = ((Long) jsonObject.get("dimensionDesigner")).intValue();
        connectPoint1Index = ((Long) jsonObject.get("connectPoint1Index")).intValue();
        connectPoint2Index = ((Long) jsonObject.get("connectPoint2Index")).intValue();
        offsetXVDim = ((Double) jsonObject.get("offsetXVDim")).intValue();
        offsetYHDim = ((Double) jsonObject.get("offsetYHDim")).intValue();

    }
}
