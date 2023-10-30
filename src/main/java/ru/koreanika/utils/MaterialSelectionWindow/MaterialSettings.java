package ru.koreanika.utils.MaterialSelectionWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.Common.Material.MaterialSheet;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.project.MaterialFactory;
import ru.koreanika.sketchDesigner.Shapes.ElementTypes;

import java.io.IOException;
import java.util.*;

public class MaterialSettings {

    Material materialTemplate;
    Material materialForAdd;

    AnchorPane rootAnchorPane;

    Label labelMainType, labelSubType, labelCollection, labelCurrency;
    TextField textFieldColor, textFieldPrice, textFieldWidth, textFieldHeight;
    ChoiceBox<String> choiceBoxSheetDepth;
    CheckBox checkBoxUseMainSheets, checkBoxUseAdditionalSheets;
    Button btnAddPrice, btnAddSheet, btnCancel, btnSave;
    ListView<AdditionalSheetsListCell> listViewAdditionalSheets;

    Map<Integer, Integer> depthsAndPrices = new LinkedHashMap<>();
    List<MaterialSheet> materialSheets = new ArrayList<>();

    public MaterialSettings(Material materialTemplate) {
        this.materialTemplate = materialTemplate;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/MaterialManager/materialSettings.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        labelMainType = (Label) rootAnchorPane.lookup("#labelMainType");
        labelSubType = (Label) rootAnchorPane.lookup("#labelSubType");
        labelCollection = (Label) rootAnchorPane.lookup("#labelCollection");
        labelCurrency = (Label) rootAnchorPane.lookup("#labelCurrency");
        textFieldColor = (TextField) rootAnchorPane.lookup("#textFieldColor");
        textFieldPrice = (TextField) rootAnchorPane.lookup("#textFieldPrice");
        textFieldWidth = (TextField) rootAnchorPane.lookup("#textFieldWidth");
        textFieldHeight = (TextField) rootAnchorPane.lookup("#textFieldHeight");
        choiceBoxSheetDepth = (ChoiceBox<String>) rootAnchorPane.lookup("#choiceBoxSheetDepth");
        checkBoxUseMainSheets = (CheckBox) rootAnchorPane.lookup("#checkBoxUseMainSheets");
        checkBoxUseAdditionalSheets = (CheckBox) rootAnchorPane.lookup("#checkBoxUseAdditionalSheets");
        btnAddPrice = (Button) rootAnchorPane.lookup("#btnAddPrice");
        btnAddSheet = (Button) rootAnchorPane.lookup("#btnAddSheet");
        btnCancel = (Button) rootAnchorPane.lookup("#btnCancel");
        btnSave = (Button) rootAnchorPane.lookup("#btnSave");
        listViewAdditionalSheets = (ListView<AdditionalSheetsListCell>) rootAnchorPane.lookup("#listViewAdditionalSheets");

        refreshView();
        initControlElementsLogic();
    }

    private void refreshView() {
        labelMainType.setText(materialTemplate.getMainType());
        labelSubType.setText(materialTemplate.getSubType());
        labelCollection.setText(materialTemplate.getCollection());
        textFieldColor.setText(materialTemplate.getColor());

        checkBoxUseMainSheets.setSelected(materialTemplate.isUseMainSheets());
        checkBoxUseAdditionalSheets.setSelected(materialTemplate.isUseAdditionalSheets());

        listViewAdditionalSheets.getItems().clear();

        materialSheets = materialTemplate.getAvailableAdditionalSheets();
        depthsAndPrices = materialTemplate.getTableTopDepthsAndPrices();

        if (materialTemplate.getAvailableMainSheetsCount() == 0) {
            choiceBoxSheetDepth.getItems().addAll("12", "20", "30");
        } else {
            choiceBoxSheetDepth.getItems().addAll(materialTemplate.getDepths());
        }
        choiceBoxSheetDepth.getSelectionModel().select(0);

        for (MaterialSheet materialSheet : materialSheets) {
            double sheetS = materialSheet.getSheetHeight() * materialSheet.getSheetWidth() / 1000000;
            listViewAdditionalSheets.getItems().add(new AdditionalSheetsListCell(
                    materialSheet.getSheetWidth(), materialSheet.getSheetHeight(), materialSheet.getSheetDepth(),
                    sheetS * materialSheet.getPriceRaw(ElementTypes.TABLETOP, materialSheet.getDepth())
            ));
        }

        textFieldColor.setDisable(!materialTemplate.isTemplate());

        if (materialTemplate.getAvailableMainSheetsCount() == 0) {
            checkBoxUseMainSheets.setSelected(false);
            checkBoxUseMainSheets.setDisable(true);

            checkBoxUseAdditionalSheets.setSelected(true);
            checkBoxUseAdditionalSheets.setDisable(true);
        } else {
            checkBoxUseMainSheets.setDisable(false);
        }
    }

    private void initControlElementsLogic() {
        btnAddSheet.setOnAction(event -> {
            try {
                double width = Double.parseDouble(textFieldWidth.getText());
                double height = Double.parseDouble(textFieldHeight.getText());
                int depth = Integer.parseInt(choiceBoxSheetDepth.getSelectionModel().getSelectedItem());
                double price = Double.parseDouble(textFieldPrice.getText());

                listViewAdditionalSheets.getItems().add(new AdditionalSheetsListCell(width, height, depth, price));
            } catch (Exception e) {
                System.out.println("ERROR TO ADD SHEET");
            }
        });

        btnSave.setOnAction(event -> {
            if (!isFieldsCorrect()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Неверный ввод");
                alert.setHeaderText("Неверный ввод");
                alert.setContentText("Проверьте корректность ввода данных.");
                alert.show();
                return;
            }

            List<Material> materialsForDelete = new ArrayList<>();
            materialsForDelete.add(materialTemplate);
            MaterialSelectionWindow.removeMaterialFromProjectListView(materialsForDelete);

            save();

            List<Material> materialsForAdd = new ArrayList<>();
            materialsForAdd.add(materialForAdd);

            MaterialSelectionWindow.addMaterialToProjectListView(materialsForAdd);

            MaterialSelectionWindow.getInstance().hideMaterialSettings();

            if (CutDesigner.getInstance().getCutPane() != null) {
                CutDesigner.getInstance().getCutPane().refreshCutPaneView();
            }
        });

        btnCancel.setOnAction(event -> {
            MaterialSelectionWindow.getInstance().hideMaterialSettings();
        });
    }

    public AnchorPane getView() {
        return rootAnchorPane;
    }

    private void save() {
        String color = textFieldColor.getText();
        double width = 1000;
        double height = 1000;
        String imgPath = materialTemplate.getImgPath();
        ArrayList<String> depthsList = new ArrayList<>();

        if (materialTemplate.getAvailableMainSheetsCount() == 0) {
            for (AdditionalSheetsListCell additionalSheetsListCell : listViewAdditionalSheets.getItems()) {
                if (!depthsList.contains("" + additionalSheetsListCell.sheetDepth))
                    depthsList.add("" + additionalSheetsListCell.sheetDepth);
            }

            this.materialForAdd = MaterialFactory.deriveFrom(materialTemplate, color, width, height, imgPath, depthsList);
            this.materialForAdd.setTemplate(false);

            materialForAdd.setUseMainSheets(false);
            materialForAdd.setAvailableMainSheetsCount(0);
        } else {
            materialForAdd = materialTemplate;
        }

        materialForAdd.setUseMainSheets(checkBoxUseMainSheets.isSelected());
        materialForAdd.setUseAdditionalSheets(checkBoxUseAdditionalSheets.isSelected());

        //set main list prices:
        if (materialForAdd.getAvailableMainSheetsCount() == 0) {

            materialForAdd.getTableTopDepthsAndPrices().clear();
            materialForAdd.getWallPanelDepthsAndPrices().clear();
            materialForAdd.getWindowSillDepthsAndPrices().clear();
            materialForAdd.getFootDepthsAndPrices().clear();

            for (AdditionalSheetsListCell additionalSheetsListCell : listViewAdditionalSheets.getItems()) {
                materialForAdd.getTableTopDepthsAndPrices().put(additionalSheetsListCell.sheetDepth, (int) (additionalSheetsListCell.sheetPriceForMeter * 100));
                materialForAdd.getWallPanelDepthsAndPrices().put(additionalSheetsListCell.sheetDepth, (int) (additionalSheetsListCell.sheetPriceForMeter * 100));
                materialForAdd.getWindowSillDepthsAndPrices().put(additionalSheetsListCell.sheetDepth, (int) (additionalSheetsListCell.sheetPriceForMeter * 100));
                materialForAdd.getFootDepthsAndPrices().put(additionalSheetsListCell.sheetDepth, (int) (additionalSheetsListCell.sheetPriceForMeter * 100));
            }
        }

        materialForAdd.getAvailableAdditionalSheets().clear();
        for (AdditionalSheetsListCell additionalSheetsListCell : listViewAdditionalSheets.getItems()) {
            materialForAdd.createAdditionalMaterialSheet(
                    additionalSheetsListCell.sheetDepth,
                    additionalSheetsListCell.sheetWidth,
                    additionalSheetsListCell.sheetHeight,
                    additionalSheetsListCell.sheetWidth,
                    additionalSheetsListCell.sheetHeight,
                    additionalSheetsListCell.sheetPriceForMeter,
                    additionalSheetsListCell.currency
            );
        }

        boolean addPostfix = false;
        String postfix = " (fragment)";
        String newColor = textFieldColor.getText();

        materialForAdd.setColor(newColor);

        String newCondName = materialForAdd.getMainType() + materialForAdd.getSubType() +
                materialForAdd.getCollection() + materialForAdd.getColor();

        for (MaterialListCellItem materialListCellItem : MaterialSelectionWindow.getListViewInProject().getItems()) {
            String condName = materialListCellItem.getMaterial().getMainType() +
                    materialListCellItem.getMaterial().getSubType() +
                    materialListCellItem.getMaterial().getCollection() +
                    materialListCellItem.getMaterial().getColor();
            if (newCondName.equals(condName)) {
                addPostfix = true;
            }

        }
        System.out.println("addPostfix = " + addPostfix);

        if (addPostfix) newColor += postfix;
        materialForAdd.setColor(newColor);
    }

    private boolean isFieldsCorrect() {
        boolean result = true;

        if (textFieldColor.getText().isEmpty()) {
            result = false;
        }

        if (!checkBoxUseMainSheets.isSelected() && !checkBoxUseAdditionalSheets.isSelected()) {
            result = false;
        }

        if (listViewAdditionalSheets.getItems().isEmpty() && checkBoxUseAdditionalSheets.isSelected()) {
            result = false;
        }

        boolean haveNameWithPostfix = false;
        boolean errorName = false;
        boolean haveTheSameName = false;

        String newCondName = materialTemplate.getMainType() + materialTemplate.getSubType() +
                materialTemplate.getCollection() + textFieldColor.getText();

        for (MaterialListCellItem materialListCellItem : MaterialSelectionWindow.getListViewInProject().getItems()) {
            String condName = materialListCellItem.getMaterial().getMainType() +
                    materialListCellItem.getMaterial().getSubType() +
                    materialListCellItem.getMaterial().getCollection() +
                    materialListCellItem.getMaterial().getColor();

            if (condName.equals(newCondName + " (fragment)")) {
                haveNameWithPostfix = true;
            }
            if (newCondName.equals(condName)) {
                haveTheSameName = true;
            }
        }

        System.out.println("haveNameWithPostfix = " + haveNameWithPostfix);
        System.out.println("haveTheSameName = " + haveTheSameName);

        if (textFieldColor.getText().endsWith(" (fragment)") && !textFieldColor.isDisable()) {
            errorName = true;
        }

        if ((haveNameWithPostfix && haveTheSameName) || errorName) {
            result = false;
        }

        return result;
    }

    private class AdditionalSheetsListCell extends AnchorPane {
        Label label = new Label();
        Button btnRemove = new Button("X");
        private double sheetWidth;
        private double sheetHeight;
        private double sheetPriceForMeter;
        private int sheetDepth;
        private String currency = "RUB";

        public AdditionalSheetsListCell(double sheetWidth, double sheetHeight, int sheetDepth, double price) {
            this.sheetWidth = sheetWidth;
            this.sheetHeight = sheetHeight;
            this.sheetDepth = sheetDepth;
            this.sheetPriceForMeter = price / ((sheetWidth * sheetHeight) / 1000000);

            this.getChildren().add(label);
            this.getChildren().add(btnRemove);

            this.setMinWidth(0);
            this.setPrefWidth(1);

            label.setText(String.format(Locale.ENGLISH, "Д х Ш %.0f Х %.0fмм, толщина = %dмм, цена листа = %.0f, цена за м2 = %.0f руб.", sheetWidth, sheetHeight, sheetDepth, price, sheetPriceForMeter));

            label.setPrefWidth(USE_COMPUTED_SIZE);
            label.setMaxWidth(USE_COMPUTED_SIZE);
            label.setMinWidth(USE_COMPUTED_SIZE);
            AnchorPane.setLeftAnchor(label, 5.0);
            AnchorPane.setRightAnchor(label, 60.0);

            AnchorPane.setRightAnchor(btnRemove, 10.0);

            label.getStyleClass().add("labelAddSheetName");
            btnRemove.getStyleClass().add("btnRemove");
            btnRemove.setOnAction(event -> {
                listViewAdditionalSheets.getItems().remove(this);
            });
        }
    }

}
