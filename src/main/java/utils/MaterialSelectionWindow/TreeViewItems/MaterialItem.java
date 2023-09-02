package utils.MaterialSelectionWindow.TreeViewItems;

import Common.Material.Material;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MaterialItem extends MaterialTreeCellItem {

    Material material;
    String name;
    String fullName;
    Label label = new Label();
    ChoiceBox<String> choiceBoxDepth = new ChoiceBox<>();

    int depth;

    public MaterialItem(Material material) {
        this.material = material;
        name = material.getColor();
        fullName = material.getName();
        label.setText(material.getName());

        for (String s : material.getDepths()) {
            choiceBoxDepth.getItems().add(s);
        }

        depth = material.getDefaultDepth();
        choiceBoxDepth.getSelectionModel().select(String.valueOf(depth));
        //depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());

        //this.setPrefHeight(50.0);
        label.setPrefHeight(30.0);
        label.setMaxWidth(150.0);

        choiceBoxDepth.setPrefHeight(30.0);
        choiceBoxDepth.setMaxWidth(40.0);

        //this.getChildren().add(label);
        //this.getChildren().add(choiceBoxDepth);

        AnchorPane.setTopAnchor(label, 10.0);
        AnchorPane.setBottomAnchor(label, 10.0);
        AnchorPane.setLeftAnchor(label, 10.0);
        //AnchorPane.setRightAnchor(label, 50.0);

        AnchorPane.setTopAnchor(choiceBoxDepth, 10.0);
        AnchorPane.setBottomAnchor(choiceBoxDepth, 10.0);
        //AnchorPane.setLeftAnchor(choiceBoxDepth,150.0);
        AnchorPane.setRightAnchor(choiceBoxDepth, 10.0);


        choiceBoxDepth.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());
        });
    }

    public int getDepth() {
        return depth;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public Material getMaterial() {
        return material;
    }
}
