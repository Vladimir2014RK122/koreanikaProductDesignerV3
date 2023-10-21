package ru.koreanika.sketchDesigner.Features;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.project.ProjectHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Rods extends AdditionalFeature {

    public static final int RODS_TYPE_1 = 1;
    public static final int RODS_TYPE_2 = 2;

    private int rods_type;

    String iconPath = "";
    String imgInfoPath = "";
    String imgInfoSchemePath = "";
    String imgTooltipPath = "";
    String shapeSchemePath = "";
    String briefPath = "";

    private static String modelsInfoPath = "features_resources/rods/brief/rods_models.json";

    private LinkedHashMap<String, ArrayList<Integer>> models = new LinkedHashMap<>();

    public Rods(int rods_type, String rods_model, SketchShape sketchShapeOwner) {
        super(sketchShapeOwner);
        this.featureType = FeatureType.RODS;
        this.rods_type = rods_type;
        this.model = rods_model;
        if (rods_type < RODS_TYPE_1 && rods_type > RODS_TYPE_2) {
            this.rods_type = RODS_TYPE_1;
        }

        initModel();
        createRodsResources();
        createTooltip();
        updateFeatureView();

        Tooltip.install(this, tooltip);
        this.setPickOnBounds(true);
    }

    public int getRods_type() {
        return rods_type;
    }

    private void initModel() {

        JSONObject modelsJson;
        JSONParser jsonParser = new JSONParser();
        try {
            modelsJson = (JSONObject) jsonParser.parse(new FileReader(modelsInfoPath));
            JSONArray jsonArray = (JSONArray) modelsJson.get("rods_" + rods_type);

            modelsList.clear();
            for (Object obj : jsonArray) {
                JSONObject jsonObjectModel = (JSONObject) obj;
                String model = (String) jsonObjectModel.get("model");
                modelsList.add(model + " " + (Long) jsonObjectModel.get("width") +
                        "x" + (Long) jsonObjectModel.get("height"));

            }

            JSONObject jsonObjectModel = null;
            boolean foundModel = false;
            for (Object obj : jsonArray) {
                jsonObjectModel = (JSONObject) obj;

                if (((String) jsonObjectModel.get("model")).equals(this.model)) {
                    featureWidth = (Long) jsonObjectModel.get("width");
                    featureHeight = (Long) jsonObjectModel.get("height");
                    foundModel = true;
                    break;
                }
            }
            if (foundModel == false) {
                System.err.println("NO RODS MODEL in models.json");

                jsonObjectModel = (JSONObject) jsonArray.get(0);
                model = (String) jsonObjectModel.get("model");
                featureWidth = (Long) jsonObjectModel.get("width");
                featureHeight = (Long) jsonObjectModel.get("height");
            }

        } catch (ParseException | IOException exio) {
            System.err.println("cant Parse rods_models.json");
        }
    }

    public static ArrayList<String> getAvailableModels(int sink_type) {
        ArrayList<String> availableModels = new ArrayList<String>();

        JSONObject modelsJson;
        JSONParser jsonParser = new JSONParser();
        try {
            modelsJson = (JSONObject) jsonParser.parse(new FileReader(modelsInfoPath));
            JSONArray jsonArray = (JSONArray) modelsJson.get("rods_" + sink_type);

            availableModels.clear();
            for (Object obj : jsonArray) {
                JSONObject jsonObjectModel = (JSONObject) obj;
                String model = (String) jsonObjectModel.get("model");
                availableModels.add(model + " " + (Long) jsonObjectModel.get("width") +
                        "x" + (Long) jsonObjectModel.get("height"));

            }

        } catch (ParseException | IOException exio) {
            System.err.println("cant Parse rods_models.json");
        }

        return availableModels;
    }

    private void createRodsResources() {
        iconPath = "features_resources/rods/icons/rods_" + rods_type + "_icon.png";
        imgInfoPath = "features_resources/rods/infoImages/rods_" + rods_type + "_info_img.png";
        imgInfoSchemePath = "features_resources/rods/infoSchemes/rods_" + rods_type + "_info_scheme_img.png";
        imgTooltipPath = "features_resources/rods/tooltipImages/rods_" + rods_type + "_tooltip_img.png";
        shapeSchemePath = "features_resources/rods/shapeSchemes/rods_" + rods_type + "_shape_scheme.png";
        briefPath = "features_resources/rods/brief/rods_" + rods_type + ".txt";


        try {
            FileInputStream input = new FileInputStream(iconPath);
            icon.setImage(new Image(input));
            icon.setFitWidth(32.0);
            icon.setFitHeight(32.0);
            icon.setPreserveRatio(true);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get icon for Rods");
        } catch (IOException ex) {
            System.err.println("Getting icon for Rods cant close inputStream");
        }

        try {
            FileInputStream input = new FileInputStream(imgInfoPath);
            infoImage.setImage(new Image(input));
            infoImage.setFitWidth(200.0);
            infoImage.setFitHeight(200.0);
            infoImage.setPreserveRatio(true);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get infoImage for Rods");
        } catch (IOException ex) {
            System.err.println("Getting infoImage for Rods cant close inputStream");
        }

        try {
            FileInputStream input = new FileInputStream(imgInfoSchemePath);
            infoSchemeImage.setImage(new Image(input));
            infoSchemeImage.setFitWidth(200.0);
            infoSchemeImage.setFitHeight(100.0);
            infoSchemeImage.setPreserveRatio(true);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get infoSchemeImage for Rods");
        } catch (IOException ex) {
            System.err.println("Getting infoSchemeImage for Rods cant close inputStream");
        }

        try {
            FileInputStream input = new FileInputStream(imgTooltipPath);
            tooltipImage.setImage(new Image(input));
            tooltipImage.setFitWidth(200.0);
            tooltipImage.setFitHeight(270.0);
            tooltipImage.setPreserveRatio(true);
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Cant get tooltipImage for Rods");
        } catch (IOException ex) {
            System.err.println("Getting tooltipImage for Rods cant close inputStream");
        }
    }

    private void updateFeatureView() {

        shapeSchemePath = "features_resources/rods/shapeSchemes/rods_" + rods_type + "_shape_scheme.png";

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
            System.err.println("Cant get shapeScheme for Rods");
        } catch (IOException ex) {
            System.err.println("Getting shapeScheme for Rods cant close inputStream");
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
            System.err.println("Cant get tooltip brief for Rods");
        } catch (IOException ex) {
            System.err.println("Cant get tooltip brief for Rods io exception");
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
    public static ImageView getIconImageForList(int rods_type) {
        String iconPath = "features_resources/rods/icons/rods_" + rods_type + "_icon.png";

        try {
            FileInputStream input = new FileInputStream(iconPath);
            ImageView imageViewIcon;
            imageViewIcon = new ImageView(new Image(input));
            imageViewIcon.setFitWidth(100.0);
            imageViewIcon.setFitHeight(100.0);

            input.close();

            return imageViewIcon;

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get image for Rods");
        } catch (IOException ex) {
            System.err.println("Cant get image for Rods cant close inputStream");
        }
        return null;
    }

    public static ImageView getImageForReceipt(int rods_type) {
        String imagePath = "features_resources/rods/infoImages/rods_" + rods_type + "_info_img.png";

        try {
            FileInputStream input = new FileInputStream(imagePath);
            ImageView imageView;
            imageView = new ImageView(new Image(input));
            imageView.setFitWidth(90.0);
            imageView.setFitHeight(90.0);

            input.close();

            return imageView;

        } catch (FileNotFoundException ex) {
            System.err.println("Cant get image for Rods");
        } catch (IOException ex) {
            System.err.println("Cant get image for Rods cant close inputStream");
        }
        return null;
    }

    public static Tooltip getTooltipForList(int rods_type) {
        String imgTooltipPath = "features_resources/rods/tooltipImages/rods_" + rods_type + "_tooltip_img.png";
        String briefPath = "features_resources/rods/brief/rods_" + rods_type + ".txt";

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
        return rods_type;
    }

    @Override
    public JSONObject getJsonView() {
        JSONObject object = new JSONObject();

        object.put("featureType", FeatureType.RODS.toString());
        object.put("type", rods_type);
        object.put("model", model);
        object.put("sketchShapeOwnerNumber", sketchShapeOwner.getShapeNumber());
        object.put("featureNumber", featureNumber);

        object.put("translateX", getTranslateX());
        object.put("translateY", getTranslateY());
        object.put("rotateAngle", shapeScheme.getRotate());

        return object;
    }
}
