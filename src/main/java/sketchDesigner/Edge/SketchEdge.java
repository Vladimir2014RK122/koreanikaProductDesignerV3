package sketchDesigner.Edge;

import Common.CommonShape;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import sketchDesigner.Shapes.SketchObject;
import sketchDesigner.Shapes.SketchShape;
import sketchDesigner.SketchDesigner;
import utils.ProjectHandler;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Objects;

public class SketchEdge extends Polygon {

    private int type = 0;

    String name;
    String imgPath;
    Node image;
    Tooltip tooltip;
    ContextMenu contextMenu;

    int edgeNumber = 0;

    private Color DEFAULT_COLOR = Color.TRANSPARENT;
    private Color MOUSE_ENTERED_COLOR = Color.GRAY;
    private Color MOUSE_EXITED_COLOR = Color.TRANSPARENT;
    private Color SELECTED_COLOR = Color.BLUE;

    boolean defined = false;
    private boolean selected;

    private double price = 0;
    private String currency = "RUB";

    SketchObject sketchEdgeOwner;

    public SketchEdge() {

        this.name = "";
        this.type = type;

        if (name != null && (!name.equals(""))) {
            defined = true;

        } else {
            defined = false;
        }

        tooltip = new Tooltip(name);

        //Tooltip.install(this, tooltip);

        try {
            getImage();
            tooltip.setGraphic(image);
        } catch (MalformedURLException ex) {
            System.out.println("ERROR WHEN GET EDGE IMAGE");
        }

        setFill(Color.TRANSPARENT);
        setStroke(Color.TRANSPARENT);


        this.setOnMouseEntered(event -> onMouseEntered());
        this.setOnMouseClicked(event -> onMouseClicked());
        this.setOnMouseExited(event -> onMouseExited());
    }

    public int getEdgeNumber() {
        if (name != null && (!name.equals(""))) {
            String leftPart = name.split("\\.")[0];
            String[] name1 = leftPart.split("_");
            edgeNumber = Integer.parseInt(name1[name1.length - 1]);
        }

        return edgeNumber;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public SketchEdge(String name, int type) {
        this.name = name;
        this.type = type;

        if (name != null && (!name.equals(""))) {
            defined = true;
        } else {
            defined = false;
        }

        tooltip = new Tooltip(name);

        //Tooltip.install(this, tooltip);

        try {
            getImage();
            tooltip.setGraphic(image);
        } catch (MalformedURLException ex) {
            System.out.println("ERROR WHEN GET EDGE IMAGE");
        }

        setFill(Color.TRANSPARENT);
        setStroke(Color.TRANSPARENT);

        this.setOnMouseEntered(event -> onMouseEntered());
        this.setOnMouseClicked(event -> onMouseClicked());
        this.setOnMouseExited(event -> onMouseExited());
    }


    public void setSketchEdgeOwner(SketchObject sketchShapeOwner) {
        this.sketchEdgeOwner = sketchShapeOwner;
    }

    public SketchObject getSketchEdgeOwner() {
        return sketchEdgeOwner;
    }

    public void changeEdge(String name, Node image) {

        this.name = name;
        this.image = image;
        tooltip.setGraphic(image);
        tooltip.setText(name);
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public void getImage() throws MalformedURLException {
        File file = new File("edges_resources/" + name);
        image = new ImageView(new Image(file.toURI().toURL().toString()));
    }

    public ImageView getImageView() {
        return null;
    }

    public void select(boolean select) {
        //System.out.println("select " + select);
        this.selected = select;
        if (this.selected) {
            setFill(SELECTED_COLOR);
            //System.out.println("setFill(SELECTED_COLOR) ");

        } else {
            //System.out.println("setFill(DEFAULT_COLOR) ");
            setFill(DEFAULT_COLOR);
        }

    }

    public boolean isDefined() {
        return defined;
    }

    private void onMouseEntered() {
        //tooltip.show(this.getScene().getWindow());
        if (this.selected) {
            setFill(SELECTED_COLOR);
        } else {
            setFill(MOUSE_ENTERED_COLOR);
        }
        setOpacity(0.2);
        setStroke(Color.BLACK);
        setStrokeType(StrokeType.INSIDE);
    }

    private void onMouseExited() {
        //tooltip.hide();
        if (this.selected) {
            setFill(SELECTED_COLOR);
        } else {
            setFill(MOUSE_EXITED_COLOR);
        }
        setStroke(Color.TRANSPARENT);
    }

    private void onMouseClicked() {
        //showEdgeManager();
    }

    public void deleteEdge() {
        name = null;
        image = null;
    }


    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getImgPath() {

        if (this instanceof Edge) {
            return ProjectHandler.EDGES_IMG_PATH + name;
        } else if (this instanceof Border) {
            return ProjectHandler.BORDERS_IMG_PATH + name;
        }

        return "";
    }

    @Override
    public String toString() {
        return "SketchEdgeOrBorder" + hashCode();
    }
}
