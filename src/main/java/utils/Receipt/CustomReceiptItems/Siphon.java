package utils.Receipt.CustomReceiptItems;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import utils.MainWindow;
import utils.Receipt.ReceiptItem;
import utils.Receipt.ReceiptManager;

public class Siphon extends CustomReceiptItem {

    private static double priceForOneType1 = 700;
    private static double priceForOneType2 = 1000;
    private static String currency = "RUB";
    private static String units = "шт";
    private static String name = "Сифон";


    AnchorPane anchorPaneSettingsRoot;

    //settings controls:
    TextField textFieldCount;
    boolean countOk = true;
    RadioButton radioBtnType1;
    RadioButton radioBtnType2;
    ToggleGroup toggleGroup = new ToggleGroup();

    Button btnAdd;

    public Siphon(AnchorPane anchorPaneSettingsRoot) {
        this.anchorPaneSettingsRoot = anchorPaneSettingsRoot;


        initControls();
        initControlsLogic();

    }

    private void initControls() {

        textFieldCount = (TextField) anchorPaneSettingsRoot.lookup("#textFieldCount");
        radioBtnType1 = (RadioButton) anchorPaneSettingsRoot.lookup("#radioBtnType1");
        radioBtnType2 = (RadioButton) anchorPaneSettingsRoot.lookup("#radioBtnType2");

        textFieldCount.setText("" + 0);

        radioBtnType1.setToggleGroup(toggleGroup);
        radioBtnType2.setToggleGroup(toggleGroup);

        radioBtnType1.setSelected(true);

        btnAdd = (Button) anchorPaneSettingsRoot.lookup("#btnAdd");

    }

    private void initControlsLogic() {

        btnAdd.setOnMouseClicked(event -> btnAddClicked());

        textFieldCount.textProperty().addListener((observable, oldValue, newValue) -> {
            int value = 0;
            try {
                value = Integer.parseInt(newValue);
                textFieldCount.setStyle("-fx-text-fill: #B3B4B4");
                countOk = true;
            } catch (NumberFormatException ex) {
                textFieldCount.setStyle("-fx-text-fill: red;");
                countOk = false;
            }
        });

    }


    private void btnAddClicked() {

        double count = 0;
        double priceForOne = 0;

        if (radioBtnType1.isSelected()) {
            priceForOne = priceForOneType1;
            name = "Сифон с одним выпуском";

        } else if (radioBtnType2.isSelected()) {
            priceForOne = priceForOneType2;
            name = "Сифон с двумя выпусками";
        }

        if (countOk) {
            count = Integer.parseInt(textFieldCount.getText());


        }

        if (count != 0) {

            ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);

            MainWindow.getReceiptManager().getCustomReceiptItems().add(receiptItem);
            MainWindow.getReceiptManager().updateReceiptTable();

        } else {
            textFieldCount.setText("" + 0);
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
