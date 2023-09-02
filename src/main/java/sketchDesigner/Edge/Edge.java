package sketchDesigner.Edge;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;

public class Edge extends SketchEdge {

    public static int DEFAULT_HEIGHT = 30;
    public static int MIN_HEIGHT = 12;
    public static int MAX_HEIGHT = 400;

    int subType = 0;

    boolean stoneHemOrLeakGroove = false;

    public Edge(String name, int type) {

        super(name, type);

        int len = (name.split("\\.")[0]).split("_").length;
        subType = Integer.parseInt((name.split("\\.")[0]).split("_")[len - 1]);

    }

    @Override
    public void getImage() throws MalformedURLException {
        File file = new File("edges_resources/" + name);
        image = new ImageView(new Image(file.toURI().toURL().toString()));
        ((ImageView) image).setFitWidth(100);
        ((ImageView) image).setFitHeight(100);

    }

    public ImageView getImageView() {
        try {
            File file = new File("edges_resources/" + name);
            return new ImageView(new Image(file.toURI().toURL().toString()));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        }


    }

    public void setStoneHemOrLeakGroove(boolean stoneHemOrLeakGroove) {
        this.stoneHemOrLeakGroove = stoneHemOrLeakGroove;
    }

    public boolean isStoneHemOrLeakGroove() {
        return stoneHemOrLeakGroove;
    }

    @Override
    public double getPrice() {
        return getSketchEdgeOwner().getMaterial().getEdgesAndPrices().get(new Integer(getEdgeNumber())).doubleValue();
    }

    @Override
    public String getCurrency() {
        return getSketchEdgeOwner().getMaterial().getEdgesCurrency();
    }

    public int getSubType() {
        return subType;
    }
}
