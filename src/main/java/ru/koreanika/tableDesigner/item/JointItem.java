package ru.koreanika.tableDesigner.item;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.json.simple.JSONObject;
import ru.koreanika.common.material.Material;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.project.Project;
import ru.koreanika.utils.currency.Currency;
import ru.koreanika.tableDesigner.TableDesignerSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class JointItem extends TableDesignerItem implements DependOnMaterial {

    /**
     * Settings part
     */
    private static AnchorPane anchorPaneSettingsView = null;
    private static Button btnAdd;
    private static Button btnApply = new Button("OK"), btnCancel = new Button("Отмена");
    private static ChoiceBox<String> choiceBoxMaterial;
    private static ToggleButton toggleButtonJointType1, toggleButtonJointType2, toggleButtonJointType3, toggleButtonJointType4;
    private static ToggleGroup toggleGroupJointType = new ToggleGroup();
    private static TextField textFieldLength;
    private static Label labelPrice;
    private static boolean lengthOk = true;
    Label labelRowNumber, labelName, labelMaterial, labelNull1, labelLength, labelNull2, labelQuantity, labelRowPrice;
    ImageView imageView;
    Button btnPlus, btnMinus, btnDelete, btnEdit;
    Material material;
    int type;
    int subType;
    double length;
    Image imageMain;

    public JointItem(Material material, int type, int subType, double length, int quantity) {
        this.material = material;
        this.type = type;
        this.length = length;
        this.quantity = quantity;
        this.subType = subType;

        imageMain = new ImageView(Project.class.getResource("/styles/images/TableDesigner/Joint/jointItemType" + subType + "_100px.png").toString()).getImage();

        FXMLLoader fxmlLoader = new FXMLLoader(
                this.getClass().getResource("/fxmls/TableDesigner/TableItems/JointRow.fxml")
        );

        try {
            anchorPaneTableRow = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        rowControlElementsInit();
        rowControlElementLogicInit();

        cardControlElementsInit();
        cardControlElementLogicInit();

        updateItemView();
    }

    private static void updateMaterial(JointItem item) {

        JointItem oldJointItem = item;

        Material newMaterial = null;
        Material defaultMaterial = Project.getDefaultMaterial();

        if (Project.getMaterials().contains(item.getMaterial())) {
            newMaterial = oldJointItem.material;
        } else {

            if (defaultMaterial.getMainType().equals(item.getMaterial().getMainType())) {
                newMaterial = Project.getDefaultMaterial();
            } else {
                boolean foundNewMaterial = false;
                for (Material material : Project.getMaterials()) {

                    if (material.getMainType().equals(item.getMaterial().getMainType())) {
                        newMaterial = material;
                        foundNewMaterial = true;
                        break;
                    }
                }

                if (foundNewMaterial == false) {
                    oldJointItem.removeThisItem();
                    return;
                }
            }

        }


        JointItem newJointItem = new JointItem(newMaterial, oldJointItem.type, oldJointItem.subType, oldJointItem.length, oldJointItem.quantity);

        oldJointItem.removeThisItem();
        TableDesignerSession.getTableDesignerMainWorkItemsList().add(newJointItem);

    }

    public static AnchorPane getAnchorPaneSettingsView() {
        if (anchorPaneSettingsView == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(StoneProductItem.class.getResource("/fxmls/TableDesigner/TableItems/JointSettings.fxml"));

            try {
                anchorPaneSettingsView = fxmlLoader.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            settingsControlElementsInit();
            settingsControlElementsLogicInit();
        }
        settingsControlElementsRefresh();

        return anchorPaneSettingsView;
    }

    private static void settingsControlElementsInit() {
        btnApply.getStyleClass().add("btnBrown");
        btnCancel.getStyleClass().add("btnBrown");

        choiceBoxMaterial = (ChoiceBox<String>) anchorPaneSettingsView.lookup("#choiceBoxMaterial");

        toggleButtonJointType1 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonJointType1");
        toggleButtonJointType2 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonJointType2");
        toggleButtonJointType3 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonJointType3");
        toggleButtonJointType4 = (ToggleButton) anchorPaneSettingsView.lookup("#toggleButtonJointType4");

        textFieldLength = (TextField) anchorPaneSettingsView.lookup("#textFieldLength");

        btnAdd = (Button) anchorPaneSettingsView.lookup("#btnAdd");
        labelPrice = (Label) anchorPaneSettingsView.lookup("#labelPrice");
        labelPrice.setText("Цена: 0 RUB/м.п.");

        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        toggleButtonJointType1.setToggleGroup(toggleGroupJointType);
        toggleButtonJointType2.setToggleGroup(toggleGroupJointType);
        toggleButtonJointType3.setToggleGroup(toggleGroupJointType);
        toggleButtonJointType4.setToggleGroup(toggleGroupJointType);

        textFieldLength.setText("600");
    }

    private static void settingsControlElementsLogicInit() {
        btnAdd.setOnMouseClicked(event -> addItem(TableDesignerSession.getTableDesignerMainWorkItemsList().size(), 1));

        choiceBoxMaterial.setOnAction(event -> {
            updatePriceInSettings();

            toggleButtonJointType1.setSelected(false);
            toggleButtonJointType2.setSelected(false);
            toggleButtonJointType3.setSelected(false);
            toggleButtonJointType4.setSelected(false);
            btnAdd.setDisable(true);
        });

        textFieldLength.textProperty().addListener((observable, oldValue, newValue) -> {

            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException ex) {
                textFieldLength.setStyle("-fx-text-fill:red");
                lengthOk = false;
                return;
            }
            textFieldLength.setStyle("-fx-text-fill:#A8A8A8");
            lengthOk = true;
        });

        toggleGroupJointType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updatePriceInSettings();
            if (toggleGroupJointType.getSelectedToggle() != null) btnAdd.setDisable(false);
        });

    }

    private static void addItem(int index, int quantity) {

        if (!lengthOk) return;

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (m.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {
                material = m;
            }
        }

        int type = 1;
        int subType = 1;
        if (toggleButtonJointType1.isSelected()) {
            type = 1;
            subType = 1;
        } else if (toggleButtonJointType2.isSelected()) {
            type = 1;
            subType = 2;
        } else if (toggleButtonJointType3.isSelected()) {
            type = 2;
            subType = 3;
        } else if (toggleButtonJointType4.isSelected()) {
            type = 2;
            subType = 4;
        }
        double length = 0;

        try {
            length = Double.parseDouble(textFieldLength.getText());
        } catch (NumberFormatException ex) {
            return;
        }

        if (length == 0) return;
        TableDesignerSession.getTableDesignerMainWorkItemsList().add(index, new JointItem(material, type, subType, length, quantity));
    }

    public static void settingsControlElementsRefresh() {

        choiceBoxMaterial.getItems().clear();
        for (Material material : Project.getMaterials()) {
            choiceBoxMaterial.getItems().add(material.getReceiptName());
        }
        choiceBoxMaterial.getSelectionModel().select(Project.getDefaultMaterial().getReceiptName());

        toggleButtonJointType1.setSelected(false);
        toggleButtonJointType2.setSelected(false);
        toggleButtonJointType3.setSelected(false);
        toggleButtonJointType4.setSelected(false);

        btnAdd.setDisable(true);

        textFieldLength.setText("600");

        updatePriceInSettings();
    }

    public static void updatePriceInSettings() {

        if (toggleGroupJointType.getSelectedToggle() == null) return;
        for (Material material : Project.getMaterials()) {
            if (material.getReceiptName().equals(choiceBoxMaterial.getSelectionModel().getSelectedItem())) {

                String currency = material.getJointsCurrency();
                String units = "м.п.";
                double priceForOne = -1.0;


                int type = 1;
                int subType = 1;
                if (toggleButtonJointType1.isSelected()) {
                    type = 1;
                    subType = 1;
                } else if (toggleButtonJointType2.isSelected()) {
                    type = 1;
                    subType = 2;
                } else if (toggleButtonJointType3.isSelected()) {
                    type = 2;
                    subType = 3;
                } else if (toggleButtonJointType4.isSelected()) {
                    type = 2;
                    subType = 4;
                }

                priceForOne = material.getJointsTypesAndPrices().get(type - 1);

                priceForOne /= 100.0;

                priceForOne *= Project.getPriceMainCoefficient().doubleValue();

                labelPrice.setText(String.format(Locale.ENGLISH, "Цена: %.0f" + " " + currency + "/" + units, priceForOne));
                break;
            }
        }
    }

    private static void enterToEditMode(JointItem jointItem) {
        TableDesigner.openSettings(JointItem.class);


        //get row data to settings
        choiceBoxMaterial.getSelectionModel().select(jointItem.material.getReceiptName());

        toggleButtonJointType1.setSelected(false);
        toggleButtonJointType2.setSelected(false);
        toggleButtonJointType3.setSelected(false);
        toggleButtonJointType4.setSelected(false);
        if (jointItem.subType == 1) {
            toggleButtonJointType1.setSelected(true);
        } else if (jointItem.subType == 2) {
            toggleButtonJointType2.setSelected(true);
        } else if (jointItem.subType == 3) {
            toggleButtonJointType3.setSelected(true);
        } else if (jointItem.subType == 4) {
            toggleButtonJointType4.setSelected(true);
        }
        textFieldLength.setText("" + jointItem.length);

        //disable button "add"
        btnAdd.setVisible(false);
        //create buttons "apply" and "cancel"
        anchorPaneSettingsView.getChildren().remove(btnApply);
        anchorPaneSettingsView.getChildren().remove(btnCancel);
        AnchorPane.setBottomAnchor(btnApply, 10.0);
        AnchorPane.setRightAnchor(btnApply, 10.0);
        AnchorPane.setBottomAnchor(btnCancel, 10.0);
        AnchorPane.setRightAnchor(btnCancel, 55.0);
        anchorPaneSettingsView.getChildren().add(btnApply);
        anchorPaneSettingsView.getChildren().add(btnCancel);

        //add listeners to new buttons
        btnApply.setOnAction(event -> {

            int index = TableDesignerSession.getTableDesignerMainWorkItemsList().indexOf(jointItem);
            addItem(index, jointItem.quantity);

            exitFromEditMode(jointItem);
            jointItem.removeThisItem();

        });
        btnCancel.setOnAction(event -> {
            exitFromEditMode(jointItem);
        });
        //in listeners:
        //"apply". delete old row and create new row
        //"cancel". exit from edit mode
    }

    protected static void exitFromEditMode(TableDesignerItem tableDesignerItem) {
        btnAdd.setVisible(true);
        //delete buttons "apply" and "cancel"
        anchorPaneSettingsView.getChildren().remove(btnApply);
        anchorPaneSettingsView.getChildren().remove(btnCancel);
        //unselect row
        tableDesignerItem.setEditModeProperty(false);
        settingsControlElementsRefresh();
    }

    public static JointItem initFromJSON(JSONObject jsonObject) {

        String materialName = (String) jsonObject.get("material");

        Material material = null;
        for (Material m : Project.getMaterials()) {
            if (materialName.equals(m.getName())) {
                material = m;
                break;
            }
        }
        if (material == null) return null;

        int quantity = ((Long) jsonObject.get("quantity")).intValue();

        int type = ((Long) jsonObject.get("type")).intValue();
        int subType = 1;
        if (jsonObject.get("subType") == null) {
            if (type == 1) subType = 1;
            else if (type == 2) subType = 3;
        } else {
            subType = ((Long) jsonObject.get("subType")).intValue();
        }
        double length = ((Double) jsonObject.get("length")).doubleValue();

        JointItem jointItem = new JointItem(material, type, subType, length, quantity);
        //jointItem.quantity = quantity;
        jointItem.labelQuantity.setText("" + quantity);
        jointItem.updateRowPrice();
        return jointItem;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void autoUpdateMaterial() {
        updateMaterial(this);
    }

    public int getType() {
        return type;
    }

    public double getLength() {
        return length;
    }

    @Override
    public Map<String, ImageView> getMainImageView() {
        Map<String, ImageView> imagesList = new LinkedHashMap<>();
        String imgPath = "/styles/images/TableDesigner/Joint/jointItemType" + subType + ".png";
        if (type == 1)
            imagesList.put("Стык прямой#" + imgPath, new ImageView(Project.class.getResource("/styles/images/TableDesigner/Joint/jointItemType" + subType + "_100px.png").toString()));
        else if (type == 2)
            imagesList.put("Стык косой#" + imgPath, new ImageView(Project.class.getResource("/styles/images/TableDesigner/Joint/jointItemType" + subType + "_100px.png").toString()));

        return imagesList;
    }

    @Override
    public void removeThisItem() {
        TableDesignerSession.getTableDesignerMainWorkItemsList().remove(this);
    }

    @Override
    public void exitEditMode() {
        if (this.editModeProperty.get()) {
            JointItem.exitFromEditMode(this);
        }
    }

    /**
     * Table ROW part
     */

    @Override
    public void setRowNumber(int number) {
        labelRowNumber.setText("" + number);
    }

    @Override
    public AnchorPane getTableView() {
        return anchorPaneTableRow;
    }

    private void rowControlElementsInit() {

        HBox hBox = (HBox) anchorPaneTableRow.lookup("#hBox");
        labelRowNumber = (Label) hBox.getChildren().get(0);
        labelName = (Label) hBox.getChildren().get(1);
        AnchorPane anchorPaneImageView = (AnchorPane) hBox.getChildren().get(2);
        imageView = (ImageView) anchorPaneImageView.lookup("#imageView");
        labelMaterial = (Label) hBox.getChildren().get(3);
        labelNull1 = (Label) hBox.getChildren().get(4);
        labelLength = (Label) hBox.getChildren().get(5);
        labelNull2 = (Label) hBox.getChildren().get(6);
        labelQuantity = (Label) hBox.getChildren().get(7);
        labelRowPrice = (Label) hBox.getChildren().get(8);
        AnchorPane anchorPaneButtons = (AnchorPane) hBox.getChildren().get(9);
        btnPlus = (Button) anchorPaneButtons.lookup("#btnPlus");
        btnMinus = (Button) anchorPaneButtons.lookup("#btnMinus");
        btnDelete = (Button) anchorPaneButtons.lookup("#btnDelete");
        btnEdit = (Button) anchorPaneButtons.lookup("#btnEdit");


        HBox.setHgrow(labelRowNumber, Priority.ALWAYS);
        HBox.setHgrow(labelName, Priority.ALWAYS);
        HBox.setHgrow(labelMaterial, Priority.ALWAYS);
        HBox.setHgrow(labelNull1, Priority.ALWAYS);
        HBox.setHgrow(labelLength, Priority.ALWAYS);
        HBox.setHgrow(labelNull2, Priority.ALWAYS);
        HBox.setHgrow(labelQuantity, Priority.ALWAYS);
        HBox.setHgrow(labelRowPrice, Priority.ALWAYS);

    }

    private void rowControlElementLogicInit() {
        btnPlus.setOnAction(event -> btnPlusClicked(event));
        btnMinus.setOnAction(event -> btnMinusClicked(event));
        btnDelete.setOnAction(event -> btnDeleteClicked(event));
        btnEdit.setOnAction(event -> btnEditClicked(event));
    }

    private void cardControlElementLogicInit() {
        btnPlusCard.setOnAction(event -> btnPlusClicked(event));
        btnMinusCard.setOnAction(event -> btnMinusClicked(event));
        btnDeleteCard.setOnAction(event -> btnDeleteClicked(event));
        btnEditCard.setOnAction(event -> btnEditClicked(event));
    }

    private void btnPlusClicked(ActionEvent event) {
        quantity++;
        updateItemView();
    }

    private void btnMinusClicked(ActionEvent event) {
        if (quantity == 1) return;
        quantity--;
        updateItemView();
    }

    private void btnDeleteClicked(ActionEvent event) {
        if (editModeProperty.get()) exitFromEditMode(this);

        TableDesignerSession.getTableDesignerMainWorkItemsList().remove(this);
    }

    private void btnEditClicked(ActionEvent event) {
        //setting change mode to edit
        for (TableDesignerItem item : TableDesigner.getTableDesignerAllItemsList()) {
            item.setEditModeProperty(false);
        }
        editModeProperty.setValue(true);
        enterToEditMode(this);
    }

    public void updateItemView() {

        labelRowNumber.setText("");
        labelName.setText((type == 1) ? "Прямой стык" : "Косой стык");
        imageView.setImage(imageMain);
        labelMaterial.setText(material.getReceiptName());
        labelNull1.setText("");
        labelLength.setText("" + length + " мм.");
        labelNull2.setText("");
        labelQuantity.setText("" + quantity);


        labelHeaderCard.setText("Соединение элементов");
        tooltipNameCard.setText("Соединение элементов");
        imageViewBackCard.setImage(imageMain);
        labelQuantityCard.setText("" + quantity);


        labelName1Card.setText("Материал");
        labelValue1Card.setText(material.getReceiptName());

        labelName2Card.setText("Тип");
        labelValue2Card.setText((type == 1) ? "Прямой стык" : "Косой стык");

        labelName3Card.setText("Длина");
        labelValue3Card.setText("" + (int) length + " мм");

        labelName4Card.setText("Высота");
        labelValue4Card.setText("-");

        updateRowPrice();
    }

    @Override
    public void updateRowPrice() {

        String currency = material.getJointsCurrency();
        String units = "м.п.";
        double priceForOne = -1.0;

        priceForOne = material.getJointsTypesAndPrices().get(type - 1);

        priceForOne /= 100.0;

        double multiplier = 1;
        if (currency.equals("USD")) multiplier = MainWindow.getUSDValue().get();
        else if (currency.equals("EUR")) multiplier = MainWindow.getEURValue().get();
        else if (currency.equals("RUB")) multiplier = 1;

        priceForOne *= multiplier;
        priceForOne *= Project.getPriceMainCoefficient().doubleValue();

        labelRowPrice.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length / 1000) * quantity) + Currency.RUR_SYMBOL);

        labelPriceForOneCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length / 1000)) + Currency.RUR_SYMBOL);
        labelPriceForAllCard.setText(String.format(Locale.ENGLISH, "%.0f", priceForOne * (length / 1000) * quantity) + Currency.RUR_SYMBOL);

    }

    /**
     * JSON SAVING & OPENING PART
     */

    @Override
    public JSONObject getJsonView() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemName", "JointItem");
        jsonObject.put("quantity", quantity);

        jsonObject.put("material", material.getName());
        jsonObject.put("type", type);
        jsonObject.put("subType", subType);
        jsonObject.put("length", length);

        return jsonObject;
    }
}
