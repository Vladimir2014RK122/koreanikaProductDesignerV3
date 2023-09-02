package cutDesigner.ListStatistics;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class StatisticCellItem {

    String materialName;
    double depth;
    double percentUses;

    AnchorPane anchorPaneRoot = new AnchorPane();
    AnchorPane anchorPaneMaximized;

    Label labelMaterialShortName;

    Label labelMaterialName;
    Label labelDepth;
    Label labelPercentUses;

    public StatisticCellItem(String materialName, double depth, double percentUses){
        this.materialName = materialName;
        this.depth = depth;
        this.percentUses = percentUses;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/CutDesigner/cutStatisticCellItem.fxml"));
        try {
            anchorPaneMaximized = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        initControls();
    }

    private void initControls(){
        String name = materialName.split("\\$")[1] + ", " + materialName.split("\\$")[3] + " + " + String.format("%.1f", percentUses) + "%";

        labelMaterialShortName = new Label(name);
        labelMaterialShortName.setMinWidth(0);
        labelMaterialShortName.getStyleClass().add("labelMaterialShortName");
        AnchorPane.setTopAnchor(labelMaterialShortName, 0.0);
        AnchorPane.setBottomAnchor(labelMaterialShortName, 0.0);
        AnchorPane.setLeftAnchor(labelMaterialShortName, 0.0);
        AnchorPane.setRightAnchor(labelMaterialShortName, 0.0);


        AnchorPane.setTopAnchor(anchorPaneMaximized, 0.0);
        AnchorPane.setBottomAnchor(anchorPaneMaximized, 0.0);
        AnchorPane.setLeftAnchor(anchorPaneMaximized, 0.0);
        AnchorPane.setRightAnchor(anchorPaneMaximized, 0.0);

        anchorPaneRoot.setPrefWidth(1);
        anchorPaneRoot.setMinWidth(0);
        anchorPaneRoot.setMaxWidth(Double.MAX_VALUE);
        anchorPaneRoot.getChildren().add(labelMaterialShortName);
        anchorPaneRoot.setPrefHeight(labelMaterialShortName.getPrefHeight());


        labelMaterialName = (Label) anchorPaneMaximized.lookup("#labelMaterialName");
        labelDepth = (Label) anchorPaneMaximized.lookup("#labelDepth");
        labelPercentUses = (Label) anchorPaneMaximized.lookup("#labelPercentUses");

        labelMaterialName.setText(name);
        labelDepth.setText(depth + "мм");
        labelPercentUses.setText(String.format("%.1f", percentUses) + "%");
    }

    public void changeMaximize(boolean maximize){

        anchorPaneRoot.getChildren().clear();
        if(maximize){
            anchorPaneRoot.getChildren().add(anchorPaneMaximized);
            anchorPaneRoot.setPrefHeight(anchorPaneMaximized.getPrefHeight());
        }else{
            anchorPaneRoot.getChildren().add(labelMaterialShortName);
            anchorPaneRoot.setPrefHeight(labelMaterialShortName.getPrefHeight());
        }

    }

    public AnchorPane getView(){
        return anchorPaneRoot;
    }


}
