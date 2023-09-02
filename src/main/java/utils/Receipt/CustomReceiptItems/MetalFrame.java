package utils.Receipt.CustomReceiptItems;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import utils.MainWindow;
import utils.Receipt.ReceiptItem;
import utils.Receipt.ReceiptManager;

import javax.swing.*;

public class MetalFrame extends CustomReceiptItem {
    private static double priceForOneType1 = 9200;
    private static double priceForOneType2 = 6350;
    private static String currency = "RUB";
    private static String units = "м.п.";
    private static String name = "Металлокаркас";


    AnchorPane anchorPaneSettingsRoot;

    //settings controls:
    TextField textFieldSize;
    boolean countOk = true;
    RadioButton radioBtnColor1;
    RadioButton radioBtnColor2;
    ToggleGroup toggleGroup = new ToggleGroup();

    Button btnAdd;

    public MetalFrame(AnchorPane anchorPaneSettingsRoot) {
        this.anchorPaneSettingsRoot = anchorPaneSettingsRoot;


        initControls();
        initControlsLogic();

    }

    private void initControls() {


        textFieldSize = (TextField) anchorPaneSettingsRoot.lookup("#textFieldSize");
        radioBtnColor1 = (RadioButton) anchorPaneSettingsRoot.lookup("#radioBtnColor1");
        radioBtnColor2 = (RadioButton) anchorPaneSettingsRoot.lookup("#radioBtnColor2");

        textFieldSize.setText("" + 0);

        radioBtnColor1.setToggleGroup(toggleGroup);
        radioBtnColor2.setToggleGroup(toggleGroup);

        radioBtnColor1.setSelected(true);

        btnAdd = (Button) anchorPaneSettingsRoot.lookup("#btnAdd");

    }

    private void initControlsLogic() {


        btnAdd.setOnMouseClicked(event -> btnAddClicked());

        textFieldSize.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                value = Double.parseDouble(newValue);
                textFieldSize.setStyle("-fx-text-fill: #B3B4B4");
                countOk = true;
            } catch (NumberFormatException ex) {
                textFieldSize.setStyle("-fx-text-fill: red;");
                countOk = false;
            }
        });

    }

    private void btnAddClicked() {

        double count = 0;
        double priceForOne = 0;

        if (radioBtnColor1.isSelected()) {
            priceForOne = priceForOneType1;
            name = "Металлокаркас. Порошковая покраска.";

        } else if (radioBtnColor2.isSelected()) {
            priceForOne = priceForOneType2;
            name = "Металлокаркас. Покраска эмалью.";
        }

        if (countOk) {
            count = Double.parseDouble(textFieldSize.getText());
        }

        if (count != 0) {

            ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);

            MainWindow.getReceiptManager().getCustomReceiptItems().add(receiptItem);
            MainWindow.getReceiptManager().updateReceiptTable();

        } else {
            textFieldSize.setText("" + 0);
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
