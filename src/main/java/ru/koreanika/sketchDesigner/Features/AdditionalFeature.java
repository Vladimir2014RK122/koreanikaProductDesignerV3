package ru.koreanika.sketchDesigner.Features;


import ru.koreanika.cutDesigner.CutDesigner;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import org.json.simple.JSONObject;
import ru.koreanika.sketchDesigner.ShapeManager.ShapeManager;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;

import java.util.ArrayList;

public abstract class AdditionalFeature extends Pane {

    public static ArrayList<Integer> createdFeaturesNumbersList = new ArrayList<>(10);

    public static final DataFormat DRAG_DATA_FORMAT_FEATURE_TYPE = new DataFormat("FEATURE_FORMAT_TYPE");
    public static final DataFormat DRAG_DATA_FORMAT_FEATURE_SUBTYPE = new DataFormat("FEATURE_FORMAT_SUBTYPE");

    //move on work pane:
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    boolean movable = true;


    protected Polygon polygon;
    protected double featureWidth;
    protected double featureHeight;
    protected ImageView icon = new ImageView();
    protected ImageView infoImage = new ImageView();
    protected ImageView infoSchemeImage = new ImageView();
    protected ImageView tooltipImage = new ImageView();
    protected ImageView shapeScheme = new ImageView();

    protected Tooltip tooltip;
    protected FeatureType featureType;
    ArrayList<String> modelsList = new ArrayList<>();
    String model;

    SketchShape sketchShapeOwner;
    int featureNumber = 0;

    public AdditionalFeature(SketchShape sketchShapeOwner) {
        this.sketchShapeOwner = sketchShapeOwner;
        //createFeatureNumber();
        initMovable();
    }

    public int createFeatureNumber() {

        int counter = 0;
        while (true) {
            counter++;
            boolean contain = false;
            for (Integer number : createdFeaturesNumbersList) {
                if (number.intValue() == counter) {
                    contain = true;
                    break;
                }
            }
            if (contain == false) {
                featureNumber = counter;
                createdFeaturesNumbersList.add(new Integer(featureNumber));
                break;
            }
        }

        return featureNumber;
    }

    public int getFeatureNumber() {
        return featureNumber;
    }

    public void setFeatureNumber(int featureNumber) {
        this.featureNumber = featureNumber;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setSketchShapeOwner(SketchShape sketchShapeOwner) {
        this.sketchShapeOwner = sketchShapeOwner;
    }

    public SketchShape getSketchShapeOwner() {
        return sketchShapeOwner;
    }

    public ImageView getIcon() {
        return icon;
    }

    public ImageView getInfoImage() {
        return infoImage;
    }

    public ImageView getInfoScheme() {
        return infoSchemeImage;
    }

    public ImageView getTooltipImage() {
        return tooltipImage;
    }

    public ImageView getShapeScheme() {
        return shapeScheme;
    }

    public double getFeatureWidth() {
        return featureWidth;
    }

    public double getFeatureHeight() {
        return featureHeight;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public ArrayList<String> getModelsList() {
        return modelsList;
    }

    public String getModel() {
        return model;
    }

    public static AdditionalFeature createFeature(FeatureType type, int subType, SketchShape sketchShapeOwner) {
        String model;
        //open selecting model window
        model = "unknown";
        AdditionalFeature feature = null;
        if (type == FeatureType.SINK) {
            Sink sink = new Sink(subType, model, sketchShapeOwner);
            sink.createFeatureNumber();
            feature = sink;
        } else if (type == FeatureType.GROOVES) {
            Grooves grooves = new Grooves(subType, model, sketchShapeOwner);
            grooves.createFeatureNumber();
            feature = grooves;
        } else if (type == FeatureType.RODS) {
            Rods rods = new Rods(subType, model, sketchShapeOwner);
            rods.createFeatureNumber();
            feature = rods;
        } else if (type == FeatureType.CUTOUTS) {
            Cutout cutout = new Cutout(subType, model, sketchShapeOwner);
            cutout.createFeatureNumber();
            feature = cutout;
        }

        return feature;
    }

    public abstract boolean changeModel(String newModel);

    public abstract int getSubType();

    public Tooltip getTooltip() {
        return tooltip;
    }

    public void initMovable() {

        setOnMouseClicked(event -> {
            if (movable == false) return;
            //select
            select();
            event.consume();
        });

        setOnMousePressed(event -> {

            if (movable == false) return;
            double scale = this.getParent().getParent().getScaleX();
            System.out.println(this.getParent().getParent());
            System.out.println(scale);
            orgSceneX = event.getSceneX() / scale;
            orgSceneY = event.getSceneY() / scale;
            orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
            orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
            ((Pane) (event.getSource())).toFront();
            event.consume();

        });

        setOnMouseDragged(event -> {

            if (movable == false) return;
            double scale = this.getParent().getParent().getScaleX();
            double offsetX = event.getSceneX() / scale - orgSceneX;
            double offsetY = event.getSceneY() / scale - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            setTranslateX(newTranslateX);
            setTranslateY(newTranslateY);
            event.consume();

        });

        setOnMouseReleased(event -> {

        });
    }

    public void select() {
        this.setStyle("-fx-background-color: Gray");
        ShapeManager.selectElement(this);
    }

    public void rotate(double angle) {
//        System.out.println("rotate feature to angle - " + angle);

        shapeScheme.setRotate(shapeScheme.getRotate() + angle);
        this.setPrefWidth(shapeScheme.getBoundsInParent().getWidth());
        this.setPrefHeight(shapeScheme.getBoundsInParent().getHeight());

        //this.setWidth(shapeScheme.getBoundsInParent().getWidth());
        //this.computeMinWidth(shapeScheme.getBoundsInParent().getWidth());
//        System.out.println("shapeScheme.getBoundsInParent().getMinX()" + shapeScheme.getBoundsInParent().getMinX());
//        System.out.println("shapeScheme.getBoundsInParent().getMinY()" + shapeScheme.getBoundsInParent().getMinY());
        shapeScheme.setTranslateX(shapeScheme.getTranslateX() - shapeScheme.getBoundsInParent().getMinX());
        shapeScheme.setTranslateY(shapeScheme.getTranslateY() - shapeScheme.getBoundsInParent().getMinY());
//        System.out.println("this.getWidth()" + this.getWidth());
//        System.out.println("this.getPrefWidth()" + this.getPrefWidth());


    }

    public void deselect() {
        this.setStyle("-fx-background-color: transparent");
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public static AdditionalFeature getDuplicateFeature(AdditionalFeature feature) {

        //System.out.println(feature.getSketchShapeOwner());
        AdditionalFeature returnFeature = createFeature(feature.getFeatureType(), feature.getSubType(), feature.getSketchShapeOwner());
        returnFeature.changeModel(feature.getModel());

//        returnFeature.rotate(feature.getShapeScheme().getRotate());
//        returnFeature.setTranslateX(feature.getTranslateX());
//        returnFeature.setTranslateY(feature.getTranslateY());
//        returnFeature.rotate(-feature.getShapeScheme().getRotate());

        return returnFeature;
    }

    //save to Json:
    public abstract JSONObject getJsonView();

    public static AdditionalFeature initFromJson(JSONObject featureJsonObject, SketchShape sketchShapeOwner) {

        FeatureType featureType = FeatureType.valueOf(((String) featureJsonObject.get("featureType")));

        if (featureType == FeatureType.SINK) {
            int sinkType = ((Long) featureJsonObject.get("type")).intValue();
            String model = ((String) featureJsonObject.get("model"));
            double rotateAngle = ((Double) featureJsonObject.get("rotateAngle")).doubleValue();
            double translateX = ((Double) featureJsonObject.get("translateX")).doubleValue();
            double translateY = ((Double) featureJsonObject.get("translateY")).doubleValue();

            Sink sink = new Sink(sinkType, model, sketchShapeOwner);
            sink.setFeatureNumber(((Long) featureJsonObject.get("featureNumber")).intValue());
            createdFeaturesNumbersList.add(new Integer(sink.getFeatureNumber()));
            sink.rotate(rotateAngle);
            sink.setTranslateX(translateX);
            sink.setTranslateY(translateY);
            sink.setMouseTransparent(true);
            //System.out.println("FROM JSON FEATURE = " + " " + sketchShapeNumber +" "+ SketchDesigner.getSketchShape(sketchShapeNumber));
            //System.out.println(SketchDesigner.getSketchShapesList());
            int installType = ((Long) featureJsonObject.get("installType")).intValue();
            sink.setInstallType(installType);

            int edgeType = ((Long) featureJsonObject.get("edgeType")).intValue();
            sink.setEdgeType(edgeType);
            //(topPartCutShape, bottomPartCutShape, leftPartCutShape, rightPartCutShape, centerPartCutShape)
            if (sink.isCuttable()) {
                JSONObject topPartCutShapeObjectJSON = (JSONObject) featureJsonObject.get("topPartCutShape");
                sink.getCutShapes().get(0).setTranslateX(((Double) topPartCutShapeObjectJSON.get("translateX")).doubleValue());
                sink.getCutShapes().get(0).setTranslateY(((Double) topPartCutShapeObjectJSON.get("translateY")).doubleValue());
                sink.getCutShapes().get(0).rotateShapeLocal(((Double) topPartCutShapeObjectJSON.get("rotateAngle")).doubleValue());


                JSONObject bottomPartCutShapeObjectJSON = (JSONObject) featureJsonObject.get("bottomPartCutShape");
                sink.getCutShapes().get(1).setTranslateX(((Double) bottomPartCutShapeObjectJSON.get("translateX")).doubleValue());
                sink.getCutShapes().get(1).setTranslateY(((Double) bottomPartCutShapeObjectJSON.get("translateY")).doubleValue());
                sink.getCutShapes().get(1).rotateShapeLocal(((Double) bottomPartCutShapeObjectJSON.get("rotateAngle")).doubleValue());

                JSONObject leftPartCutShapeObjectJSON = (JSONObject) featureJsonObject.get("leftPartCutShape");
                sink.getCutShapes().get(2).setTranslateX(((Double) leftPartCutShapeObjectJSON.get("translateX")).doubleValue());
                sink.getCutShapes().get(2).setTranslateY(((Double) leftPartCutShapeObjectJSON.get("translateY")).doubleValue());
                sink.getCutShapes().get(2).rotateShapeLocal(((Double) leftPartCutShapeObjectJSON.get("rotateAngle")).doubleValue());

                JSONObject rightPartCutShapeObjectJSON = (JSONObject) featureJsonObject.get("rightPartCutShape");
                sink.getCutShapes().get(3).setTranslateX(((Double) rightPartCutShapeObjectJSON.get("translateX")).doubleValue());
                sink.getCutShapes().get(3).setTranslateY(((Double) rightPartCutShapeObjectJSON.get("translateY")).doubleValue());
                sink.getCutShapes().get(3).rotateShapeLocal(((Double) rightPartCutShapeObjectJSON.get("rotateAngle")).doubleValue());

                JSONObject centerPartCutShapeObjectJSON = (JSONObject) featureJsonObject.get("centerPartCutShape");
                sink.getCutShapes().get(4).setTranslateX(((Double) centerPartCutShapeObjectJSON.get("translateX")).doubleValue());
                sink.getCutShapes().get(4).setTranslateY(((Double) centerPartCutShapeObjectJSON.get("translateY")).doubleValue());
                sink.getCutShapes().get(4).rotateShapeLocal(((Double) centerPartCutShapeObjectJSON.get("rotateAngle")).doubleValue());

                if (((Boolean) featureJsonObject.get("onCutPane")).booleanValue()) {
                    CutDesigner.getInstance().getCutShapeAdditionalFeaturesList().addAll(sink.getCutShapes());
                    CutDesigner.getInstance().getCutPane().getCutObjectsGroup().getChildren().addAll(sink.getCutShapes());


                }
            }

            return sink;
        } else if (featureType == FeatureType.RODS) {
            int rodsType = ((Long) featureJsonObject.get("type")).intValue();
            String model = ((String) featureJsonObject.get("model"));
            double rotateAngle = ((Double) featureJsonObject.get("rotateAngle")).doubleValue();
            double translateX = ((Double) featureJsonObject.get("translateX")).doubleValue();
            double translateY = ((Double) featureJsonObject.get("translateY")).doubleValue();

            Rods rods = new Rods(rodsType, model, sketchShapeOwner);
            rods.setFeatureNumber(((Long) featureJsonObject.get("featureNumber")).intValue());
            createdFeaturesNumbersList.add(new Integer(rods.getFeatureNumber()));
            rods.rotate(rotateAngle);
            rods.setTranslateX(translateX);
            rods.setTranslateY(translateY);
            rods.setMouseTransparent(true);
            return rods;
        } else if (featureType == FeatureType.GROOVES) {
            int groovesType = ((Long) featureJsonObject.get("type")).intValue();
            String model = ((String) featureJsonObject.get("model"));
            double rotateAngle = ((Double) featureJsonObject.get("rotateAngle")).doubleValue();
            double translateX = ((Double) featureJsonObject.get("translateX")).doubleValue();
            double translateY = ((Double) featureJsonObject.get("translateY")).doubleValue();

            Grooves grooves = new Grooves(groovesType, model, sketchShapeOwner);
            grooves.setFeatureNumber(((Long) featureJsonObject.get("featureNumber")).intValue());
            createdFeaturesNumbersList.add(new Integer(grooves.getFeatureNumber()));
            grooves.rotate(rotateAngle);
            grooves.setTranslateX(translateX);
            grooves.setTranslateY(translateY);
            grooves.setMouseTransparent(true);
            return grooves;
        } else if (featureType == FeatureType.CUTOUTS) {
            int cutoutType = ((Long) featureJsonObject.get("type")).intValue();
            String model = ((String) featureJsonObject.get("model"));
            double rotateAngle = ((Double) featureJsonObject.get("rotateAngle")).doubleValue();
            double translateX = ((Double) featureJsonObject.get("translateX")).doubleValue();
            double translateY = ((Double) featureJsonObject.get("translateY")).doubleValue();

            Cutout cutout = new Cutout(cutoutType, model, sketchShapeOwner);
            cutout.setFeatureNumber(((Long) featureJsonObject.get("featureNumber")).intValue());
            createdFeaturesNumbersList.add(new Integer(cutout.getFeatureNumber()));
            cutout.rotate(rotateAngle);
            cutout.setTranslateX(translateX);
            cutout.setTranslateY(translateY);
            cutout.setMouseTransparent(true);

            return cutout;
        } else {
            return null;
        }


    }
}
