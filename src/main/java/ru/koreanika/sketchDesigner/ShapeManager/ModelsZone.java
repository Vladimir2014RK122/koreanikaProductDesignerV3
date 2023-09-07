package ru.koreanika.sketchDesigner.ShapeManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ru.koreanika.sketchDesigner.Features.AdditionalFeature;
import ru.koreanika.sketchDesigner.Features.Sink;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ModelsZone {


    AdditionalFeature selectedFeature;
    String selectedModel;
    ArrayList<String> modelsList = new ArrayList<>();

    //view elements:
    AnchorPane rootAnchorPane;
    ImageView imageViewItemInfo;
    RadioButton radioBtnModel1, radioBtnModel2, radioBtnModel3, radioBtnModel4, radioBtnModel5, radioBtnModel6;
    RadioButton radioBtnModel7, radioBtnModel8, radioBtnModel9, radioBtnModel10;
    RadioButton radioBtnSinkInstallType1, radioBtnSinkInstallType2;
    RadioButton radioBtnSinkEdgeType1, radioBtnSinkEdgeType2;
    ToggleGroup toggleGroup = new ToggleGroup();
    ToggleGroup toggleGroupSinkInstallType = new ToggleGroup();
    ToggleGroup toggleGroupSinkEdgeType = new ToggleGroup();


    public ModelsZone() {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/ShapeManager/ItemModelsZone.fxml"));

        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        initView();
    }

    public AnchorPane getRootAnchorPane() {
        return rootAnchorPane;
    }

    void initView() {
        imageViewItemInfo = (ImageView) rootAnchorPane.lookup("#imageViewItemInfo");
        radioBtnModel1 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel1");
        radioBtnModel2 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel2");
        radioBtnModel3 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel3");
        radioBtnModel4 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel4");
        radioBtnModel5 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel5");
        radioBtnModel6 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel6");
        radioBtnModel7 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel7");
        radioBtnModel8 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel8");
        radioBtnModel9 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel9");
        radioBtnModel10 = (RadioButton) rootAnchorPane.lookup("#radioBtnModel10");

        radioBtnSinkInstallType1 = (RadioButton) rootAnchorPane.lookup("#radioBtnSinkInstallType1");
        radioBtnSinkInstallType2 = (RadioButton) rootAnchorPane.lookup("#radioBtnSinkInstallType2");

        radioBtnSinkEdgeType1 = (RadioButton) rootAnchorPane.lookup("#radioBtnSinkEdgeType1");
        radioBtnSinkEdgeType2 = (RadioButton) rootAnchorPane.lookup("#radioBtnSinkEdgeType2");

        radioBtnSinkInstallType1.setText("");
        radioBtnSinkInstallType2.setText("");
        radioBtnSinkInstallType1.getStyleClass().remove("radio-button");
        radioBtnSinkInstallType2.getStyleClass().remove("radio-button");
        radioBtnSinkInstallType1.getStyleClass().add("toggle-button");
        radioBtnSinkInstallType2.getStyleClass().add("toggle-button");

        radioBtnSinkInstallType1.setGraphic(Sink.getImageInstallType(Sink.SINK_INSTALL_TYPE_1));
        radioBtnSinkInstallType2.setGraphic(Sink.getImageInstallType(Sink.SINK_INSTALL_TYPE_2));

        radioBtnSinkEdgeType1.setText("");
        radioBtnSinkEdgeType2.setText("");
        radioBtnSinkEdgeType1.getStyleClass().remove("radio-button");
        radioBtnSinkEdgeType2.getStyleClass().remove("radio-button");
        radioBtnSinkEdgeType1.getStyleClass().add("toggle-button");
        radioBtnSinkEdgeType2.getStyleClass().add("toggle-button");

        radioBtnSinkEdgeType1.setGraphic(Sink.getImageEdgeType(Sink.SINK_EDGE_TYPE_1));
        radioBtnSinkEdgeType2.setGraphic(Sink.getImageEdgeType(Sink.SINK_EDGE_TYPE_2));

        radioBtnModel1.setToggleGroup(toggleGroup);
        radioBtnModel2.setToggleGroup(toggleGroup);
        radioBtnModel3.setToggleGroup(toggleGroup);
        radioBtnModel4.setToggleGroup(toggleGroup);
        radioBtnModel5.setToggleGroup(toggleGroup);
        radioBtnModel6.setToggleGroup(toggleGroup);
        radioBtnModel7.setToggleGroup(toggleGroup);
        radioBtnModel8.setToggleGroup(toggleGroup);
        radioBtnModel9.setToggleGroup(toggleGroup);
        radioBtnModel10.setToggleGroup(toggleGroup);

        radioBtnSinkInstallType1.setToggleGroup(toggleGroupSinkInstallType);
        radioBtnSinkInstallType2.setToggleGroup(toggleGroupSinkInstallType);

        radioBtnSinkEdgeType1.setToggleGroup(toggleGroupSinkEdgeType);
        radioBtnSinkEdgeType2.setToggleGroup(toggleGroupSinkEdgeType);


        imageViewItemInfo.setVisible(false);
        radioBtnModel1.setVisible(false);
        radioBtnModel2.setVisible(false);
        radioBtnModel3.setVisible(false);
        radioBtnModel4.setVisible(false);
        radioBtnModel5.setVisible(false);
        radioBtnModel6.setVisible(false);
        radioBtnModel7.setVisible(false);
        radioBtnModel8.setVisible(false);
        radioBtnModel9.setVisible(false);
        radioBtnModel10.setVisible(false);

        radioBtnSinkInstallType1.setVisible(false);
        radioBtnSinkInstallType2.setVisible(false);

        radioBtnSinkEdgeType1.setVisible(false);
        radioBtnSinkEdgeType2.setVisible(false);


        radioBtnModel1.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel1.getText().split(" ")[0]);
        });
        radioBtnModel2.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel2.getText().split(" ")[0]);
        });
        radioBtnModel3.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel3.getText().split(" ")[0]);
        });
        radioBtnModel4.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel4.getText().split(" ")[0]);
        });
        radioBtnModel5.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel5.getText().split(" ")[0]);
        });
        radioBtnModel6.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel6.getText().split(" ")[0]);
        });
        radioBtnModel7.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel7.getText().split(" ")[0]);
        });
        radioBtnModel8.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel8.getText().split(" ")[0]);
        });
        radioBtnModel9.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel9.getText().split(" ")[0]);
        });
        radioBtnModel10.setOnMouseClicked(event -> {
            ChangeFeatureModel(radioBtnModel10.getText().split(" ")[0]);
        });


        radioBtnSinkInstallType1.setOnMouseClicked(event -> {
            ChangeSinkInstallType();
        });
        radioBtnSinkInstallType2.setOnMouseClicked(event -> {
            ChangeSinkInstallType();
        });

        radioBtnSinkEdgeType1.setOnMouseClicked(event -> {
            ChangeSinkEdgeType();
        });
        radioBtnSinkEdgeType2.setOnMouseClicked(event -> {
            ChangeSinkEdgeType();
        });

    }

    public RadioButton getRadioBtnSinkInstallType1() {
        return radioBtnSinkInstallType1;
    }

    public RadioButton getRadioBtnSinkInstallType2() {
        return radioBtnSinkInstallType2;
    }

    private void ChangeSinkInstallType() {

        if (selectedFeature instanceof Sink) {
            Sink sink = (Sink) selectedFeature;


            if (radioBtnSinkInstallType1.isSelected()) sink.setInstallType(Sink.SINK_INSTALL_TYPE_1);
            else if (radioBtnSinkInstallType2.isSelected()) sink.setInstallType(Sink.SINK_INSTALL_TYPE_2);

            if (sink.getSubType() == Sink.SINK_TYPE_16) {
                if (radioBtnSinkInstallType1.isSelected()) {
                    radioBtnSinkEdgeType1.setVisible(false);
                    radioBtnSinkEdgeType2.setVisible(false);
                }
                if (radioBtnSinkInstallType2.isSelected()) {
                    radioBtnSinkEdgeType1.setVisible(true);
                    radioBtnSinkEdgeType2.setVisible(true);
                }
            }

        }
    }

    private void ChangeSinkEdgeType() {

        if (selectedFeature instanceof Sink) {
            Sink sink = (Sink) selectedFeature;

            if (radioBtnSinkEdgeType1.isSelected()) sink.setEdgeType(Sink.SINK_EDGE_TYPE_1);
            else if (radioBtnSinkEdgeType2.isSelected()) sink.setEdgeType(Sink.SINK_EDGE_TYPE_2);
        }
    }

    private void ChangeFeatureModel(String newModel) {
        this.selectedModel = newModel;
        selectedFeature.changeModel(newModel);
    }

    public void changeFeature(AdditionalFeature feature) {
        this.selectedFeature = feature;

        if (feature instanceof Sink) {
            Sink sink = (Sink) feature;
            if (sink.getSubType() == Sink.SINK_TYPE_16) {

                String imagePath = "features_resources/sink/installTypesImages/sink_install_type_0.png";
                ImageView imageView = null;
                try {
                    FileInputStream input = new FileInputStream(imagePath);

                    imageView = new ImageView(new Image(input));
                    imageView.setFitWidth(60.0);
                    imageView.setFitHeight(60.0);

                    input.close();

                } catch (FileNotFoundException ex) {
                    System.err.println("Cant get image for install type Sink");
                } catch (IOException ex) {
                    System.err.println("Cant get image for install type Sink cant close inputStream");
                }

                radioBtnSinkInstallType1.setGraphic(imageView);
                radioBtnSinkInstallType2.setGraphic(Sink.getImageInstallType(Sink.SINK_INSTALL_TYPE_1));

                radioBtnSinkInstallType1.setVisible(true);
                radioBtnSinkInstallType2.setVisible(true);

                if (sink.getInstallType() == Sink.SINK_INSTALL_TYPE_1) {
                    radioBtnSinkEdgeType1.setVisible(false);
                    radioBtnSinkEdgeType2.setVisible(false);
                }
                if (sink.getInstallType() == Sink.SINK_INSTALL_TYPE_2) {
                    radioBtnSinkEdgeType1.setVisible(true);
                    radioBtnSinkEdgeType2.setVisible(true);
                }


            } else {
                radioBtnSinkInstallType1.setGraphic(Sink.getImageInstallType(Sink.SINK_INSTALL_TYPE_1));
                radioBtnSinkInstallType2.setGraphic(Sink.getImageInstallType(Sink.SINK_INSTALL_TYPE_2));

                radioBtnSinkInstallType1.setVisible(true);
                radioBtnSinkInstallType2.setVisible(true);

                radioBtnSinkEdgeType1.setVisible(true);
                radioBtnSinkEdgeType2.setVisible(true);
            }


            if (sink.getInstallType() == Sink.SINK_INSTALL_TYPE_1) radioBtnSinkInstallType1.setSelected(true);
            else if (sink.getInstallType() == Sink.SINK_INSTALL_TYPE_2) radioBtnSinkInstallType2.setSelected(true);

            if (sink.getEdgeType() == Sink.SINK_EDGE_TYPE_1) radioBtnSinkEdgeType1.setSelected(true);
            else if (sink.getEdgeType() == Sink.SINK_EDGE_TYPE_2) radioBtnSinkEdgeType2.setSelected(true);
        } else {
            radioBtnSinkInstallType1.setVisible(false);
            radioBtnSinkInstallType2.setVisible(false);

            radioBtnSinkEdgeType1.setVisible(false);
            radioBtnSinkEdgeType2.setVisible(false);
        }

        if (this.selectedFeature == null) {
            imageViewItemInfo.setVisible(false);
            for (int i = 1; i <= 10; i++) {
                rootAnchorPane.lookup("#radioBtnModel" + i).setVisible(false);
            }
            //modelsList.clear();
            return;
        }


        imageViewItemInfo.setImage(feature.getTooltipImage().getImage());
        imageViewItemInfo.setVisible(true);

        modelsList = feature.getModelsList();
        System.out.println("modelsList =" + modelsList.toString());

        for (int i = 1; i <= 10; i++) {
            rootAnchorPane.lookup("#radioBtnModel" + i).setVisible(false);
        }

        for (int i = 1; i <= modelsList.size(); i++) {
            if (rootAnchorPane.lookup("#radioBtnModel" + i) == null) break;
            rootAnchorPane.lookup("#radioBtnModel" + i).setVisible(true);
            ((RadioButton) rootAnchorPane.lookup("#radioBtnModel" + i)).setText(modelsList.get(i - 1));
        }

        for (int i = 1; i <= modelsList.size(); i++) {
            if (rootAnchorPane.lookup("#radioBtnModel" + i) == null) break;
            String modelBtn = ((RadioButton) rootAnchorPane.lookup("#radioBtnModel" + i)).getText();
            if (modelBtn.indexOf(feature.getModel() + " ") != -1) {
                ((RadioButton) rootAnchorPane.lookup("#radioBtnModel" + i)).setSelected(true);
            }
        }

        rootAnchorPane.setMinHeight(240 + modelsList.size() * 37 + 200);
    }
}
