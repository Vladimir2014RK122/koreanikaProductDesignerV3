package ru.koreanika.sketchDesigner.Features;

import ru.koreanika.Common.ConnectPoints.ConnectPoint;
import ru.koreanika.Common.ConnectPoints.CornerConnectPoint;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShapeAdditionalFeature;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.utils.ProjectHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Sink extends AdditionalFeature {

    public static final int SINK_TYPE_1 = 1;
    public static final int SINK_TYPE_2 = 2;
    public static final int SINK_TYPE_3 = 3;
    public static final int SINK_TYPE_4 = 4;
    public static final int SINK_TYPE_5 = 5;
    public static final int SINK_TYPE_6 = 6;
    public static final int SINK_TYPE_7 = 7;
    public static final int SINK_TYPE_8 = 8;
    public static final int SINK_TYPE_9 = 9;
    public static final int SINK_TYPE_10 = 10;
    public static final int SINK_TYPE_11 = 11;
    public static final int SINK_TYPE_12 = 12;
    public static final int SINK_TYPE_13 = 13;
    public static final int SINK_TYPE_14 = 14;
    public static final int SINK_TYPE_15 = 15;
    public static final int SINK_TYPE_16 = 16;
    public static final int SINK_TYPE_17 = 17;
    public static final int SINK_TYPE_18 = 18;
    public static final int SINK_TYPE_19 = 19;
    public static final int SINK_TYPE_20 = 20;
    public static final int SINK_TYPE_21 = 21;

    public static final int SINK_TYPE_22 = 22;

    public static final int SINK_INSTALL_TYPE_1 = 0;
    public static final int SINK_INSTALL_TYPE_2 = 1;

    public static final int SINK_CUTOUT_RECTANGLE_FORM = 0;
    public static final int SINK_CUTOUT_CIRCLE_FORM = 1;

    public static final int SINK_EDGE_TYPE_1 = 0;
    public static final int SINK_EDGE_TYPE_2 = 1;

    private int sink_type;

    private int installType = SINK_INSTALL_TYPE_1;
    private int edgeType = SINK_EDGE_TYPE_1;
    private int cutForm = SINK_CUTOUT_RECTANGLE_FORM;


    String iconPath = "";
    String imgInfoPath = "";
    String imgInfoSchemePath = "";
    String imgTooltipPath = "";
    String shapeSchemePath = "";
    String briefPath = "";

    boolean cuttable = false;
    CutShapeAdditionalFeature topPartCutShape = new CutShapeAdditionalFeature(this);
    CutShapeAdditionalFeature bottomPartCutShape = new CutShapeAdditionalFeature(this);
    CutShapeAdditionalFeature leftPartCutShape = new CutShapeAdditionalFeature(this);
    CutShapeAdditionalFeature rightPartCutShape = new CutShapeAdditionalFeature(this);
    CutShapeAdditionalFeature centerPartCutShape = new CutShapeAdditionalFeature(this);

    private static String modelsInfoPath = "features_resources/sink/brief/sink_models.json";

    private LinkedHashMap<String, ArrayList<Integer>> models = new LinkedHashMap<>();

    public Sink(int sink_type, String sink_model, SketchShape sketchShapeOwner) {

        super(sketchShapeOwner);
        this.featureType = FeatureType.SINK;
        this.sink_type = sink_type;
        this.model = sink_model;
        if (sink_type < SINK_TYPE_1 && sink_type > SINK_TYPE_21) {
            this.sink_type = SINK_TYPE_1;
        }

        if (sink_type == 1 || (sink_type >= 4 && sink_type <= 10) || sink_type == 20) {
            cutForm = SINK_CUTOUT_CIRCLE_FORM;
        } else {
            cutForm = SINK_CUTOUT_RECTANGLE_FORM;
        }

        initModel();
        createSinkResources();
        createTooltip();
        updateFeatureView();

        Tooltip.install(this, tooltip);
        this.setPickOnBounds(true);
    }

    public int getSink_type() {
        return sink_type;
    }

    public void initModel() {
        //System.out.println("SINK_MODEL = " + this.model);
        JSONObject modelsJson;
        JSONParser jsonParser = new JSONParser();
        try {
            modelsJson = (JSONObject) jsonParser.parse(new FileReader(modelsInfoPath));
            JSONArray jsonArray = (JSONArray) modelsJson.get("sink_" + sink_type);

            modelsList.clear();
            for (Object obj : jsonArray) {
                JSONObject jsonObjectModel = (JSONObject) obj;
                String model = (String) jsonObjectModel.get("model");
                modelsList.add(model + " " + (Long) jsonObjectModel.get("width") +
                        "x" + (Long) jsonObjectModel.get("height") + "x" +
                        (Long) jsonObjectModel.get("depth"));

            }

            JSONObject jsonObjectModel = null;
            boolean foundModel = false;
            for (Object obj : jsonArray) {
                jsonObjectModel = (JSONObject) obj;

                if (((String) jsonObjectModel.get("model")).equals(this.model)) {
                    featureWidth = (Long) jsonObjectModel.get("width");
                    featureHeight = (Long) jsonObjectModel.get("height");
                    foundModel = true;

                    cuttable = ((Boolean) jsonObjectModel.get("cuttable")).booleanValue();

                    if (cuttable) {

                        createCutShapes(jsonObjectModel);
                    }

                    break;
                }
            }
            if (foundModel == false) {
                System.err.println("NO SINK MODEL in models.json");

                jsonObjectModel = (JSONObject) jsonArray.get(0);
                model = (String) jsonObjectModel.get("model");
                featureWidth = (Long) jsonObjectModel.get("width");
                featureHeight = (Long) jsonObjectModel.get("height");

                cuttable = ((Boolean) jsonObjectModel.get("cuttable")).booleanValue();

                if (cuttable) {

                    createCutShapes(jsonObjectModel);
                }

            }
        } catch (ParseException | IOException exio) {
            System.err.println("cant Parse sink_models.json");
        }
    }

    public static ArrayList<String> getAvailableModels(int sink_type) {
        ArrayList<String> availableModels = new ArrayList<String>();

        JSONObject modelsJson;
        JSONParser jsonParser = new JSONParser();
        try {
            modelsJson = (JSONObject) jsonParser.parse(new FileReader(modelsInfoPath));
            JSONArray jsonArray = (JSONArray) modelsJson.get("sink_" + sink_type);

            availableModels.clear();
            for (Object obj : jsonArray) {
                JSONObject jsonObjectModel = (JSONObject) obj;
                String model = (String) jsonObjectModel.get("model");
                availableModels.add(model + " " + (Long) jsonObjectModel.get("width") +
                        "x" + (Long) jsonObjectModel.get("height") + "x" +
                        (Long) jsonObjectModel.get("depth"));

            }


        } catch (ParseException | IOException exio) {
            System.err.println("cant Parse sink_models.json");
        }

        return availableModels;
    }

    public static boolean isCuttable(int sink_type, String mod, ArrayList<ArrayList<Double>> sizeList) {

        ArrayList<String> availableModels = new ArrayList<String>();

        JSONObject modelsJson;
        JSONParser jsonParser = new JSONParser();
        try {
            modelsJson = (JSONObject) jsonParser.parse(new FileReader(modelsInfoPath));
            JSONArray jsonArray = (JSONArray) modelsJson.get("sink_" + sink_type);

            availableModels.clear();
            for (Object obj : jsonArray) {
                JSONObject jsonObjectModel = (JSONObject) obj;
                String model = (String) jsonObjectModel.get("model");
                availableModels.add(model + " " + (Long) jsonObjectModel.get("width") +
                        "x" + (Long) jsonObjectModel.get("height") + "x" +
                        (Long) jsonObjectModel.get("depth"));

            }

            JSONObject jsonObjectModel = null;
            boolean foundModel = false;
            for (Object obj : jsonArray) {
                jsonObjectModel = (JSONObject) obj;

                if (((String) jsonObjectModel.get("model")).equals(mod)) {

                    if (((Boolean) jsonObjectModel.get("cuttable")).booleanValue()) {

                        if(jsonObjectModel.get("parts") != null){

                            JSONArray jsonArr = (JSONArray) jsonObjectModel.get("parts");

                            sizeList.clear();
                            for(Object o : jsonArr){
                                String partStr = (String)o;

                                ArrayList<Double> part = new ArrayList<>(2);
                                Arrays.stream(partStr.split("x")).toList().forEach(s -> {part.add(Double.valueOf(s));});
                                sizeList.add(part);
                            }

                            return true;
                        }

                        double topWidth = ((Long) (((JSONArray) jsonObjectModel.get("topPart")).get(0))).intValue();
                        double topHeight = ((Long) (((JSONArray) jsonObjectModel.get("topPart")).get(1))).intValue();
                        double bottomWidth = ((Long) (((JSONArray) jsonObjectModel.get("bottomPart")).get(0))).intValue();
                        double bottomHeight = ((Long) (((JSONArray) jsonObjectModel.get("bottomPart")).get(1))).intValue();
                        double leftWidth = ((Long) (((JSONArray) jsonObjectModel.get("leftPart")).get(0))).intValue();
                        double leftHeight = ((Long) (((JSONArray) jsonObjectModel.get("leftPart")).get(1))).intValue();
                        double rightWidth = ((Long) (((JSONArray) jsonObjectModel.get("rightPart")).get(0))).intValue();
                        double rightHeight = ((Long) (((JSONArray) jsonObjectModel.get("rightPart")).get(1))).intValue();
                        double centerWidth = ((Long) (((JSONArray) jsonObjectModel.get("centerPart")).get(0))).intValue();
                        double centerHeight = ((Long) (((JSONArray) jsonObjectModel.get("centerPart")).get(1))).intValue();

                        sizeList.clear();
                        ArrayList<Double> size1 = new ArrayList<>(2);
                        size1.add(Double.valueOf(topWidth));
                        size1.add(Double.valueOf(topHeight));
                        sizeList.add(size1);

                        ArrayList<Double> size2 = new ArrayList<>(2);
                        size2.add(Double.valueOf(bottomWidth));
                        size2.add(Double.valueOf(bottomHeight));
                        sizeList.add(size2);

                        ArrayList<Double> size3 = new ArrayList<>(2);
                        size3.add(Double.valueOf(leftWidth));
                        size3.add(Double.valueOf(leftHeight));
                        sizeList.add(size3);

                        ArrayList<Double> size4 = new ArrayList<>(2);
                        size4.add(Double.valueOf(rightWidth));
                        size4.add(Double.valueOf(rightHeight));
                        sizeList.add(size4);

                        ArrayList<Double> size5 = new ArrayList<>(2);
                        size5.add(Double.valueOf(centerWidth));
                        size5.add(Double.valueOf(centerHeight));
                        sizeList.add(size5);

                        return true;
                    }


                    //break;
                }
            }

        } catch (ParseException | IOException exio) {
            System.err.println("cant Parse sink_models.json");
        }

        sizeList = null;
        return false;
    }

    public boolean isCuttable() {
        return cuttable;
    }

    private void createCutShapes(JSONObject jsonObj) {

        //System.out.println("CREATE CUT SHAPES FEATURE");

        double topWidth = ((Long) (((JSONArray) jsonObj.get("topPart")).get(0))).intValue();
        double topHeight = ((Long) (((JSONArray) jsonObj.get("topPart")).get(1))).intValue();
        double bottomWidth = ((Long) (((JSONArray) jsonObj.get("bottomPart")).get(0))).intValue();
        double bottomHeight = ((Long) (((JSONArray) jsonObj.get("bottomPart")).get(1))).intValue();
        double leftWidth = ((Long) (((JSONArray) jsonObj.get("leftPart")).get(0))).intValue();
        double leftHeight = ((Long) (((JSONArray) jsonObj.get("leftPart")).get(1))).intValue();
        double rightWidth = ((Long) (((JSONArray) jsonObj.get("rightPart")).get(0))).intValue();
        double rightHeight = ((Long) (((JSONArray) jsonObj.get("rightPart")).get(1))).intValue();
        double centerWidth = ((Long) (((JSONArray) jsonObj.get("centerPart")).get(0))).intValue();
        double centerHeight = ((Long) (((JSONArray) jsonObj.get("centerPart")).get(1))).intValue();


        double scale = ProjectHandler.getCommonShapeScale();

        createCutShapePart(topPartCutShape, topWidth, topHeight, 0.0, -topHeight * scale);
        createCutShapePart(bottomPartCutShape, bottomWidth, bottomHeight, 0.0, centerHeight * scale);
        createCutShapePart(leftPartCutShape, leftWidth, leftHeight, -leftWidth * scale, 0.0);
        createCutShapePart(rightPartCutShape, rightWidth, rightHeight, centerWidth * scale, 0.0);
        createCutShapePart(centerPartCutShape, centerWidth, centerHeight, 0.0, 0.0);
        //add label with shape number
        //centerPartCutShape.refreshLabelNumber();
        //System.out.println("centerPartCutShape = " + centerPartCutShape);
    }


    private void createCutShapePart(CutShapeAdditionalFeature cutShapeAdditionalFeature, double width, double height, double shiftX, double shiftY) {

        Material material = sketchShapeOwner.getMaterial();
        int depth = sketchShapeOwner.getShapeDepth();


        cutShapeAdditionalFeature.setMaterial(material);
        cutShapeAdditionalFeature.setDepth(depth);
        //return polygon, areas around, connect points
        cutShapeAdditionalFeature.getChildren().clear();

        double scale = ProjectHandler.getCommonShapeScale();
        //create main poligon:
        Point2D[] polygonPoints = new Point2D[]{
                new Point2D(0.0, 0.0),
                new Point2D(width * scale, 0.0),
                new Point2D(width * scale, height * scale),
                new Point2D(0.0, height * scale),
        };

        Polygon cutShapePolygon = new Polygon(
                polygonPoints[0].getX(), polygonPoints[0].getY(),
                polygonPoints[1].getX(), polygonPoints[1].getY(),
                polygonPoints[2].getX(), polygonPoints[2].getY(),
                polygonPoints[3].getX(), polygonPoints[3].getY()
        );

        cutShapePolygon.setTranslateX(0.0);
        cutShapePolygon.setTranslateY(0.0);
        cutShapePolygon.setFill(Color.GREENYELLOW);

        cutShapePolygon.setStroke(Color.GRAY);

        cutShapePolygon.setStrokeType(StrokeType.INSIDE);

        cutShapeAdditionalFeature.setPrefWidth(cutShapePolygon.getBoundsInLocal().getWidth());
        cutShapeAdditionalFeature.setPrefHeight(cutShapePolygon.getBoundsInLocal().getHeight());

        cutShapeAdditionalFeature.setPolygon(cutShapePolygon);
        cutShapeAdditionalFeature.getChildren().add(cutShapePolygon);

        cutShapeAdditionalFeature.setShiftCoordinate(new Point2D(shiftX, shiftY));


        //create connect points:
        ArrayList<ConnectPoint> cutShapeConnectPoints = new ArrayList<>();

        cutShapeConnectPoints.clear();
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShapeAdditionalFeature));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShapeAdditionalFeature));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShapeAdditionalFeature));
        cutShapeConnectPoints.add(new CornerConnectPoint(cutShapeAdditionalFeature));

        cutShapeConnectPoints.get(0).changeSetPoint(polygonPoints[0]);
        cutShapeConnectPoints.get(1).changeSetPoint(polygonPoints[1]);
        cutShapeConnectPoints.get(2).changeSetPoint(polygonPoints[2]);
        cutShapeConnectPoints.get(3).changeSetPoint(polygonPoints[3]);

        cutShapeAdditionalFeature.setConnectPoints(cutShapeConnectPoints);
        for (ConnectPoint connectPoint : cutShapeConnectPoints) {
            cutShapeAdditionalFeature.getChildren().add(connectPoint);
        }
        cutShapeAdditionalFeature.hideConnectionPoints();

    }


    public ArrayList<CutShapeAdditionalFeature> getCutShapes() {
        ArrayList<CutShapeAdditionalFeature> cutShapesList = new ArrayList<>();
        cutShapesList.clear();
        cutShapesList.addAll(Arrays.asList(topPartCutShape, bottomPartCutShape, leftPartCutShape, rightPartCutShape, centerPartCutShape));


        return cutShapesList;
    }


    public Node getViewForListCell() {
        return getIconImageForList(sink_type);
    }

    public Tooltip getTooltipForListCell() {
        return new Tooltip("Раковина" + featureNumber + ". " + model + " " + featureWidth + "x" + featureHeight);
    }

    private void createSinkResources() {
        iconPath = "features_resources/sink/icons/sink_" + sink_type + "_icon.png";
        imgInfoPath = "features_resources/sink/infoImages/sink_" + sink_type + "_info_img.png";
        imgInfoSchemePath = "features_resources/sink/infoSchemes/sink_" + sink_type + "_info_scheme_img.png";
        imgTooltipPath = "features_resources/sink/tooltipImages/sink_" + sink_type + "_tooltip_img.png";
        shapeSchemePath = "features_resources/sink/shapeSchemes/sink_" + sink_type + "_" + model + "_shape_scheme.png";
        briefPath = "features_resources/sink/brief/sink_" + sink_type + ".txt";


        try {
            FileInputStream input = new FileInputStream(iconPath);
            icon.setImage(new Image(input));
            icon.setFitWidth(32.0);
            icon.setFitHeight(32.0);
            icon.setPreserveRatio(true);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get icon for Sink");
        } catch (IOException ex) {
            System.err.println("Getting icon for Sink cant close inputStream");
        }

        try {
            FileInputStream input = new FileInputStream(imgInfoPath);
            infoImage.setImage(new Image(input));
            infoImage.setFitWidth(200.0);
            infoImage.setFitHeight(200.0);
            infoImage.setPreserveRatio(true);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get infoImage for Sink");
        } catch (IOException ex) {
            System.err.println("Getting infoImage for Sink cant close inputStream");
        }

        try {
            FileInputStream input = new FileInputStream(imgInfoSchemePath);
            infoSchemeImage.setImage(new Image(input));
            infoSchemeImage.setFitWidth(200.0);
            infoSchemeImage.setFitHeight(100.0);
            infoSchemeImage.setPreserveRatio(true);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get infoSchemeImage for Sink");
        } catch (IOException ex) {
            System.err.println("Getting infoSchemeImage for Sink cant close inputStream");
        }

        try {
            FileInputStream input = new FileInputStream(imgTooltipPath);
            tooltipImage.setImage(new Image(input));
            tooltipImage.setFitWidth(200.0);
            tooltipImage.setFitHeight(270.0);
            tooltipImage.setPreserveRatio(true);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltipImage for Sink");
        } catch (IOException ex) {
            System.err.println("Getting tooltipImage for Sink cant close inputStream");
        }
    }

    public void setInstallType(int installType) {
        this.installType = installType;
    }

    public int getInstallType() {
        return installType;
    }

    public void setEdgeType(int edgeType) {
        this.edgeType = edgeType;
    }

    public int getEdgeType() {
        return edgeType;
    }

    public int getCutForm() {
        return cutForm;
    }

    private void updateFeatureView() {

        shapeSchemePath = "features_resources/sink/shapeSchemes/sink_" + sink_type + "_" + model + "_shape_scheme.png";

        try {
            this.getChildren().remove(shapeScheme);
            FileInputStream input = new FileInputStream(shapeSchemePath);
            shapeScheme.setImage(new Image(input));
            shapeScheme.setFitWidth(featureWidth * ProjectHandler.getCommonShapeScale());
            shapeScheme.setFitHeight(featureHeight * ProjectHandler.getCommonShapeScale());
            //shapeScheme.setPreserveRatio(true);

            this.setPrefSize(featureWidth * ProjectHandler.getCommonShapeScale(), featureHeight * ProjectHandler.getCommonShapeScale());
            this.getChildren().add(shapeScheme);
            //this.setCenter(shapeScheme);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get shapeScheme for Sink");
        } catch (IOException ex) {
            System.err.println("Getting shapeScheme for Sink cant close inputStream");
        }
    }


    private void createTooltip() {
        tooltip = new Tooltip();

        AnchorPane tooltipPane = new AnchorPane();
        Text briefText = new Text();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(briefPath), "UTF8"));
            String brief = "";
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                brief += line + "\n";
            }
            briefText.setText(brief);
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip brief for Sink");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Sink io exception");
        }

        tooltipPane.setPrefSize(200, 400);
        tooltipPane.setTopAnchor(tooltipImage, 0.0);
        tooltipPane.setLeftAnchor(tooltipImage, 0.0);
        //tooltipPane.setRightAnchor(tooltipImage, 0.0);

        tooltipPane.setTopAnchor(briefText, 275.0);
        tooltipPane.setLeftAnchor(briefText, 5.0);
        //tooltipPane.setRightAnchor(briefText, 5.0);

        //briefText.setWrappingWidth(200);
        briefText.setTextAlignment(TextAlignment.LEFT);

        briefText.setFont(Font.font(12));
        briefText.setFill(Color.WHITE);

        tooltipPane.getChildren().addAll(tooltipImage, briefText);
        tooltipPane.setPrefHeight(briefText.getBoundsInParent().getMaxY());

        tooltip.setGraphic(tooltipPane);
    }

    /* DATA FOR LIST */
    public static ImageView getIconImageForList(int sink_type) {
        String iconPath = "features_resources/sink/icons/sink_" + sink_type + "_icon.png";

        try {
            FileInputStream input = new FileInputStream(iconPath);
            ImageView imageViewIcon;
            imageViewIcon = new ImageView(new Image(input));
            imageViewIcon.setFitWidth(100.0);
            imageViewIcon.setFitHeight(100.0);

            input.close();

            return imageViewIcon;

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get image for Sink");
        } catch (IOException ex) {
            System.err.println("Cant get image for Sink cant close inputStream");
        }
        return null;
    }

    public static Tooltip getTooltipForList(int sink_type) {
        String imgTooltipPath = "features_resources/sink/tooltipImages/sink_" + sink_type + "_tooltip_img.png";
        String briefPath = "features_resources/sink/brief/sink_" + sink_type + ".txt";

        Tooltip tooltip = new Tooltip();

        AnchorPane tooltipPane = new AnchorPane();

        ImageView tooltipImage = null;
        Text briefText = new Text();
        try {
            FileInputStream input = new FileInputStream(imgTooltipPath);
            tooltipImage = new ImageView(new Image(input));
            tooltipImage.setFitWidth(200.0);
            tooltipImage.setFitHeight(270.0);

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip image for Sink");
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(briefPath), "UTF8"));
            String brief = "";
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                brief += line + "\n";
            }
            briefText.setText(brief);
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltip brief for Sink");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Sink io exception");
        }

        tooltipPane.setPrefSize(200, 400);
        tooltipPane.setTopAnchor(tooltipImage, 0.0);
        tooltipPane.setLeftAnchor(tooltipImage, 0.0);
        //tooltipPane.setRightAnchor(tooltipImage, 0.0);

        tooltipPane.setTopAnchor(briefText, 275.0);
        tooltipPane.setLeftAnchor(briefText, 5.0);
        //tooltipPane.setRightAnchor(briefText, 5.0);

        //briefText.setWrappingWidth(200);
        briefText.setTextAlignment(TextAlignment.LEFT);

        briefText.setFont(Font.font(12));
        briefText.setFill(Color.WHITE);

        tooltipPane.getChildren().addAll(tooltipImage, briefText);
        tooltipPane.setPrefHeight(briefText.getBoundsInParent().getMaxY());

        tooltip.setGraphic(tooltipPane);

        return tooltip;
    }

    public static ImageView getImageForReceipt(int sink_type) {
        String imagePath = "features_resources/sink/infoImages/sink_" + sink_type + "_info_img.png";

        try {
            FileInputStream input = new FileInputStream(imagePath);
            ImageView imageView;
            imageView = new ImageView(new Image(input));
            imageView.setFitWidth(90.0);
            imageView.setFitHeight(90.0);

            input.close();

            return imageView;

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get image for Sink");
        } catch (IOException ex) {
            System.err.println("Cant get image for Sink cant close inputStream");
        }
        return null;
    }

    public static ImageView getImageInstallType(int installType) {
        String imagePath = "features_resources/sink/installTypesImages/sink_install_type_" + (installType + 1) + ".png";

        try {
            FileInputStream input = new FileInputStream(imagePath);
            ImageView imageView;
            imageView = new ImageView(new Image(input));
            imageView.setFitWidth(60.0);
            imageView.setFitHeight(60.0);

            input.close();

            return imageView;

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get image for install type Sink");
        } catch (IOException ex) {
            System.err.println("Cant get image for install type Sink cant close inputStream");
        }
        return null;
    }

    public static ImageView getImageEdgeType(int edgeType) {
        String imagePath = "features_resources/sink/installTypesImages/sink_edge_type_" + (edgeType + 1) + ".png";

        try {
            FileInputStream input = new FileInputStream(imagePath);
            ImageView imageView;
            imageView = new ImageView(new Image(input));
            imageView.setFitWidth(60.0);
            imageView.setFitHeight(60.0);

            input.close();

            return imageView;

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get image for install type Sink");
        } catch (IOException ex) {
            System.err.println("Cant get image for install type Sink cant close inputStream");
        }
        return null;
    }

    public LinkedHashMap<String, ArrayList<Integer>> getModels() {
        return models;
    }

    @Override
    public boolean changeModel(String newModel) {
        this.model = newModel;
        initModel();
        updateFeatureView();
        rotate(0.0);
        return true;
    }

    @Override
    public int getSubType() {
        return sink_type;
    }

    @Override
    public JSONObject getJsonView() {
        JSONObject object = new JSONObject();
        System.out.println("SINK = " + this);
        System.out.println("CUTTABLE = " + cuttable);

        object.put("featureType", FeatureType.SINK.toString());
        object.put("type", sink_type);
        object.put("model", model);
        object.put("sketchShapeOwnerNumber", sketchShapeOwner.getShapeNumber());
        object.put("featureNumber", featureNumber);
        object.put("installType", installType);
        object.put("edgeType", edgeType);

        object.put("translateX", getTranslateX());
        object.put("translateY", getTranslateY());
        object.put("rotateAngle", shapeScheme.getRotate());

        object.put("cuttable", isCuttable());

        if (isCuttable()) {
            object.put("onCutPane", CutDesigner.getInstance().getCutShapeAdditionalFeaturesList().contains(topPartCutShape));

            //topPartCutShape
            JSONObject topPartCutShapeObjectJSON = new JSONObject();
            topPartCutShapeObjectJSON.put("translateX", topPartCutShape.getTranslateX());
            topPartCutShapeObjectJSON.put("translateY", topPartCutShape.getTranslateY());
            topPartCutShapeObjectJSON.put("rotateAngle", topPartCutShape.getRotateTransform().getAngle());

            //bottomPartCutShape
            JSONObject bottomPartCutShapeObjectJSON = new JSONObject();
            bottomPartCutShapeObjectJSON.put("translateX", bottomPartCutShape.getTranslateX());
            bottomPartCutShapeObjectJSON.put("translateY", bottomPartCutShape.getTranslateY());
            bottomPartCutShapeObjectJSON.put("rotateAngle", bottomPartCutShape.getRotateTransform().getAngle());

            //leftPartCutShape
            JSONObject leftPartCutShapeObjectJSON = new JSONObject();
            leftPartCutShapeObjectJSON.put("translateX", leftPartCutShape.getTranslateX());
            leftPartCutShapeObjectJSON.put("translateY", leftPartCutShape.getTranslateY());
            leftPartCutShapeObjectJSON.put("rotateAngle", leftPartCutShape.getRotateTransform().getAngle());

            //rightPartCutShape
            JSONObject rightPartCutShapeObjectJSON = new JSONObject();
            rightPartCutShapeObjectJSON.put("translateX", rightPartCutShape.getTranslateX());
            rightPartCutShapeObjectJSON.put("translateY", rightPartCutShape.getTranslateY());
            rightPartCutShapeObjectJSON.put("rotateAngle", rightPartCutShape.getRotateTransform().getAngle());

            //centerPartCutShape
            JSONObject centerPartCutShapeObjectJSON = new JSONObject();
            centerPartCutShapeObjectJSON.put("translateX", centerPartCutShape.getTranslateX());
            centerPartCutShapeObjectJSON.put("translateY", centerPartCutShape.getTranslateY());
            centerPartCutShapeObjectJSON.put("rotateAngle", centerPartCutShape.getRotateTransform().getAngle());

            object.put("topPartCutShape", topPartCutShapeObjectJSON);
            object.put("bottomPartCutShape", bottomPartCutShapeObjectJSON);
            object.put("leftPartCutShape", leftPartCutShapeObjectJSON);
            object.put("rightPartCutShape", rightPartCutShapeObjectJSON);
            object.put("centerPartCutShape", centerPartCutShapeObjectJSON);

        }

        return object;
    }

}
