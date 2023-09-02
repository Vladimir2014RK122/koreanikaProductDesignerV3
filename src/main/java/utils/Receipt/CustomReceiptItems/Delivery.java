package utils.Receipt.CustomReceiptItems;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import utils.MainWindow;
import utils.Receipt.ReceiptItem;
import utils.Receipt.ReceiptManager;

public class Delivery extends CustomReceiptItem {

    private static double priceForOneType1 = 3000;
    private static double priceForOneType2 = 60;
    private static double priceForOneType3 = 500;
    private static String currency = "RUB";
    private static String units = "";
    private static String name = "Доставка";


    AnchorPane anchorPaneSettingsRoot;

    //settings controls:

    TextField textFieldDistance1;
    TextField textFieldDistance2;
    TextField textFieldElementCount;
    boolean distance1Ok = true;
    boolean distance2Ok = true;
    boolean elementCountOk = true;

    Button btnAdd;

    public Delivery(AnchorPane anchorPaneSettingsRoot) {
        this.anchorPaneSettingsRoot = anchorPaneSettingsRoot;


        initControls();
        initControlsLogic();

    }

    private void initControls() {


        textFieldDistance1 = (TextField) anchorPaneSettingsRoot.lookup("#textFieldDistance1");
        textFieldDistance2 = (TextField) anchorPaneSettingsRoot.lookup("#textFieldDistance2");
        textFieldElementCount = (TextField) anchorPaneSettingsRoot.lookup("#textFieldElementCount");

        textFieldDistance1.setText("" + 0);
        textFieldDistance2.setText("" + 0);
        textFieldElementCount.setText("" + 0);

        btnAdd = (Button) anchorPaneSettingsRoot.lookup("#btnAdd");

    }

    private void initControlsLogic() {


        btnAdd.setOnMouseClicked(event -> btnAddClicked());

        textFieldDistance1.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                value = Double.parseDouble(newValue);
                textFieldDistance1.setStyle("-fx-text-fill: #B3B4B4");
                distance1Ok = true;
            } catch (NumberFormatException ex) {
                textFieldDistance1.setStyle("-fx-text-fill: red;");
                distance1Ok = false;
            }
        });

        textFieldDistance2.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                value = Double.parseDouble(newValue);
                textFieldDistance2.setStyle("-fx-text-fill: #B3B4B4");
                distance2Ok = true;
            } catch (NumberFormatException ex) {
                textFieldDistance2.setStyle("-fx-text-fill: red;");
                distance2Ok = false;
            }
        });

        textFieldElementCount.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                value = Double.parseDouble(newValue);
                textFieldElementCount.setStyle("-fx-text-fill: #B3B4B4");
                elementCountOk = true;
            } catch (NumberFormatException ex) {
                textFieldElementCount.setStyle("-fx-text-fill: red;");
                elementCountOk = false;
            }
        });

    }

    private void btnAddClicked() {

        double count1 = 0;
        double count2 = 0;
        double count3 = 0;


        if (distance1Ok) {
            count1 = Double.parseDouble(textFieldDistance1.getText());
        }

        if (count1 != 0) {

            name = "Доставка в пределах МКАД";
            units = "шт.";

            ReceiptItem receiptItem = new ReceiptItem(name, units, count1, currency, priceForOneType1);

            MainWindow.getReceiptManager().getCustomReceiptItems().add(receiptItem);
            MainWindow.getReceiptManager().updateReceiptTable();

        } else {
            textFieldDistance1.setText("" + 0);
        }


        if (distance2Ok) {
            count2 = Double.parseDouble(textFieldDistance2.getText());
        }

        if (count2 != 0) {

            name = "Доставка за пределы МКАД";
            units = "км.";

            ReceiptItem receiptItem = new ReceiptItem(name, units, count2, currency, priceForOneType2);

            MainWindow.getReceiptManager().getCustomReceiptItems().add(receiptItem);
            MainWindow.getReceiptManager().updateReceiptTable();

        } else {
            textFieldDistance2.setText("" + 0);
        }

        if (elementCountOk) {
            count3 = Double.parseDouble(textFieldElementCount.getText());
        }

        if (count3 != 0) {

            name = "Ручной подъем";
            units = "1 эл/1 эт.";

            ReceiptItem receiptItem = new ReceiptItem(name, units, count3, currency, priceForOneType3);

            MainWindow.getReceiptManager().getCustomReceiptItems().add(receiptItem);
            MainWindow.getReceiptManager().updateReceiptTable();

        } else {
            textFieldDistance2.setText("" + 0);
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
