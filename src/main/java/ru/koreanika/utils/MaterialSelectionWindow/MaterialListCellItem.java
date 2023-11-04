package ru.koreanika.utils.MaterialSelectionWindow;

import ru.koreanika.Common.Material.Material;
import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.Preferences.UserPreferences;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.currency.Currency;

import java.util.ArrayList;
import java.util.Locale;

public class MaterialListCellItem extends AnchorPane {

    Material material;

    String name;
    Label label = new Label();
    Label labelPrice = new Label();
    ChoiceBox<String> choiceBoxDepth = new ChoiceBox<>();
    Button btnEdit = new Button(), btnRemove = new Button();

    int depth;
    boolean forAnalogs = false;

    public MaterialListCellItem(Material material) {
        this.material = material;

        String price = String.format(Locale.ENGLISH, "%.0f", material.getPrice(ElementTypes.TABLETOP, material.getDefaultDepth()));

        name = material.getSubType() + ", " + material.getCollection() + ", " + material.getColor();
        label.setText(name);

        double RURToUSD = MainWindow.getUSDValue().get();
        double RURToEUR = MainWindow.getEURValue().get();
        double priceForOne = material.getPrice(ElementTypes.TABLETOP, material.getDefaultDepth());
        String symbol = Currency.RUR_SYMBOL;
        if (material.getCurrency().equals("RUB")) {
        } else if (material.getCurrency().equals("EUR")) {
            price = String.format(Locale.ENGLISH, "%.0f", priceForOne * RURToEUR);
        } else if (material.getCurrency().equals("USD")) {
            price = String.format(Locale.ENGLISH, "%.0f", priceForOne * RURToUSD);
        }

        labelPrice.setText(price + symbol);

        for (String s : material.getDepths()) {
            choiceBoxDepth.getItems().add(s);
        }

        depth = material.getDefaultDepth();
        choiceBoxDepth.getSelectionModel().select(String.valueOf(depth));

        this.setPrefHeight(37.0);

        label.setPrefHeight(27.0);
        label.setPrefWidth(270.0);
        label.setWrapText(true);

        labelPrice.setId("labelPriceMaterialInList");
        labelPrice.setPrefHeight(27.0);
        labelPrice.setPrefWidth(70.0);
        labelPrice.setTextAlignment(TextAlignment.RIGHT);

        choiceBoxDepth.setPrefHeight(27.0);
        choiceBoxDepth.setMaxWidth(40.0);

        btnEdit.setId("btnEditProjectMaterial");
        btnRemove.setId("btnRemoveProjectMaterial");

        if (material.getMainType().equalsIgnoreCase("Акриловый камень") ||
                material.getMainType().equalsIgnoreCase("Полиэфирный камень") ||
                material.getMainType().equalsIgnoreCase("Массив") ||
                material.getMainType().equalsIgnoreCase("Массив_шпон")) {
            btnEdit.setVisible(false);
        }

        if (UserPreferences.getInstance().getSelectedApp() != AppType.KOREANIKAMASTER) {
            btnEdit.setVisible(false);
        }

        this.getChildren().add(label);
        this.getChildren().add(labelPrice);
        this.getChildren().add(choiceBoxDepth);
        this.getChildren().add(btnEdit);
        this.getChildren().add(btnRemove);

        AnchorPane.setTopAnchor(label, 5.0);
        AnchorPane.setLeftAnchor(label, 5.0);

        AnchorPane.setTopAnchor(labelPrice, 5.0);
        AnchorPane.setRightAnchor(labelPrice, 120.0);

        AnchorPane.setTopAnchor(choiceBoxDepth, 5.0);
        AnchorPane.setRightAnchor(choiceBoxDepth, 75.0);

        AnchorPane.setTopAnchor(btnEdit, 6.0);
        AnchorPane.setRightAnchor(btnEdit, 35.0);

        AnchorPane.setTopAnchor(btnRemove, 6.0);
        AnchorPane.setRightAnchor(btnRemove, 5.0);

        choiceBoxDepth.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            depth = Integer.parseInt(choiceBoxDepth.getSelectionModel().getSelectedItem());
        });

        btnRemove.setOnAction(event -> {
            ArrayList<Material> materials = new ArrayList<>();
            materials.add(material);
            MaterialSelectionWindow.removeMaterialFromProjectListView(materials);
        });

        btnEdit.setOnAction(event -> {
            MaterialSettings materialSettings = new MaterialSettings(material);
            MaterialSelectionWindow.getInstance().showMaterialSettings(materialSettings);
        });
    }

    public MaterialListCellItem(Material material, boolean forAnalogs) {
        this(material);
        this.forAnalogs = forAnalogs;

        btnEdit.setVisible(false);
        btnRemove.setVisible(false);

        label.prefWidth(150);
    }

    public void disableChoiceBox(boolean disable) {
        if (disable) {
            this.getChildren().remove(choiceBoxDepth);
        }
    }

    public void disableControls(boolean disable) {
        if (disable) {
            this.getChildren().remove(btnRemove);
            this.getChildren().remove(btnEdit);
            AnchorPane.setRightAnchor(labelPrice, 0.0);
            label.setPrefWidth(240);
        }
    }

    public int getDepth() {
        return depth;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }
}
