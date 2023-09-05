package ru.koreanika.sketchDesigner.Edge;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;

import java.io.File;
import java.net.MalformedURLException;

public class Border extends SketchEdge {

    public static int DEFAULT_HEIGHT = 30;
    public static int MIN_HEIGHT = 12;
    public static int MAX_HEIGHT = 100;

    public static final int BORDER_CUT_TYPE_A = 1;
    public static final int BORDER_CUT_TYPE_B = 2;
    public static final int BORDER_CUT_TYPE_C = 3;
    public static final int BORDER_CUT_TYPE_D = 4;

    public static final int BORDER_SIDE_CUT_TYPE_A = 1;
    public static final int BORDER_SIDE_CUT_TYPE_B = 2;

    public static final int BORDER_ANGLE_CUT_NONE = 1;
    public static final int BORDER_ANGLE_CUT_ONE_ANGLE = 2;
    public static final int BORDER_ANGLE_CUT_TWO_ANGLES = 3;

    private int borderCutType = 2;
    private int borderSideCutType = 1;
    private int borderAnglesCutType = 1;

    public Border(String name, int type) {
        super(name, type);
    }

    @Override
    public void getImage() throws MalformedURLException {
        File file = new File("borders_resources/" + name);
        image = new ImageView(new Image(file.toURI().toURL().toString()));
        ((ImageView) image).setFitWidth(100);
        ((ImageView) image).setFitHeight(100);

    }

    public ImageView getImageView() {
        try {
            File file = new File("borders_resources/" + name);
            return new ImageView(new Image(file.toURI().toURL().toString()));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        }


    }

    @Override
    public double getPrice() {
        double price = 0;
        if (sketchEdgeOwner.getMaterial().getMainType().indexOf("Акриловый камень") != -1 ||
                sketchEdgeOwner.getMaterial().getMainType().indexOf("Полиэфирный камень") != -1) {
            if (((SketchShape) sketchEdgeOwner).getBorderHeight() <= 50 && edgeNumber == 1) {
                price = sketchEdgeOwner.getMaterial().getBorderTypesAndPrices().get(new Integer(0));
            } else if (((SketchShape) sketchEdgeOwner).getBorderHeight() > 50 && edgeNumber == 1) {
                price = sketchEdgeOwner.getMaterial().getBorderTypesAndPrices().get(new Integer(1));
            } else if (((SketchShape) sketchEdgeOwner).getBorderHeight() <= 50 && edgeNumber == 2) {
                price = sketchEdgeOwner.getMaterial().getBorderTypesAndPrices().get(new Integer(2));
            } else if (((SketchShape) sketchEdgeOwner).getBorderHeight() > 50 && edgeNumber == 2) {
                price = sketchEdgeOwner.getMaterial().getBorderTypesAndPrices().get(new Integer(3));
            }
        } else {
            price = sketchEdgeOwner.getMaterial().getBorderTypesAndPrices().get(new Integer(0));
        }
        return price / 100;
    }

    @Override
    public String getCurrency() {

        //System.out.println("Border Currency = " + getSketchEdgeOwner().getMaterial().getBorderCurrency());
        return getSketchEdgeOwner().getMaterial().getBorderCurrency();
    }

    public void setBorderCut(int borderCutType) {

        System.out.println("CHANGE BORDER CUT TYPE " + borderCutType);
        this.borderCutType = borderCutType;
    }

    public void setBorderSideCutType(int borderSideCutType) {
        this.borderSideCutType = borderSideCutType;
    }

    public void setBorderAnglesCutType(int borderAnglesCutType) {
        this.borderAnglesCutType = borderAnglesCutType;
    }

    public int getBorderCutType() {
        return borderCutType;
    }

    public int getBorderSideCutType() {
        return borderSideCutType;
    }

    public int getBorderAnglesCutType() {
        return borderAnglesCutType;
    }
}
