package ru.koreanika.utils.Receipt.CustomReceiptItems;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ru.koreanika.utils.MainWindow;
import ru.koreanika.utils.Receipt.ReceiptItem;

public class PlyWood extends CustomReceiptItem {

    private static double priceForOneType1 = 1500;
    private static double priceForOneType2 = 2000;
    private static String currency = "RUB";
    private static String units = "м.кв.";
    private static String name = "Подложка из фанеры";

    AnchorPane anchorPaneSettingsRoot;

    //settings controls:
    TextField textFieldWidth;
    TextField textFieldHeight;
    boolean sizeOk = true;
    RadioButton radioBtnType1;
    RadioButton radioBtnType2;
    ToggleGroup toggleGroup = new ToggleGroup();

    Button btnAdd;

    public PlyWood(AnchorPane anchorPaneSettingsRoot) {
        this.anchorPaneSettingsRoot = anchorPaneSettingsRoot;


        initControls();
        initControlsLogic();

    }

    private void initControls() {

        textFieldWidth = (TextField) anchorPaneSettingsRoot.lookup("#textFieldWidth");
        textFieldHeight = (TextField) anchorPaneSettingsRoot.lookup("#textFieldHeight");
        radioBtnType1 = (RadioButton) anchorPaneSettingsRoot.lookup("#radioBtnType1");
        radioBtnType2 = (RadioButton) anchorPaneSettingsRoot.lookup("#radioBtnType2");

        textFieldWidth.setText("" + 0);
        textFieldHeight.setText("" + 0);

        radioBtnType1.setToggleGroup(toggleGroup);
        radioBtnType2.setToggleGroup(toggleGroup);

        radioBtnType1.setSelected(true);

        btnAdd = (Button) anchorPaneSettingsRoot.lookup("#btnAdd");

    }

    private void initControlsLogic() {

        btnAdd.setOnMouseClicked(event -> btnAddClicked());

        textFieldWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                value = Double.parseDouble(newValue);
                textFieldWidth.setStyle("-fx-text-fill: #B3B4B4");
                sizeOk = true;
            } catch (NumberFormatException ex) {
                textFieldWidth.setStyle("-fx-text-fill: red;");
                sizeOk = false;
            }
        });

        textFieldHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                value = Double.parseDouble(newValue);
                textFieldHeight.setStyle("-fx-text-fill: #B3B4B4");
                sizeOk = true;
            } catch (NumberFormatException ex) {
                textFieldHeight.setStyle("-fx-text-fill: red;");
                sizeOk = false;
            }
        });

    }


    private void btnAddClicked() {

        double count = 0;
        double priceForOne = 0;

        if (radioBtnType1.isSelected()) {
            priceForOne = priceForOneType1;
            name = "Подложка из фанеры. Без покраски.";

        } else if (radioBtnType2.isSelected()) {
            priceForOne = priceForOneType2;
            name = "Подложка из фанеры. С покраской.";
        }

        if (sizeOk) {
            double width = Double.parseDouble(textFieldWidth.getText());
            double height = Double.parseDouble(textFieldHeight.getText());

            count = width * height;
        }

        if (count != 0) {

            ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);

            MainWindow.getReceiptManager().getCustomReceiptItems().add(receiptItem);
            MainWindow.getReceiptManager().updateReceiptTable();

        } else {
            textFieldWidth.setText("" + 0);
            textFieldHeight.setText("" + 0);
        }

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ImageView getImageView() {
        return null;
    }

    @Override
    public Tooltip getTooltip() {
        return null;
    }

    @Override
    public AnchorPane getRootAnchorPane() {
        return anchorPaneSettingsRoot;
    }


}
