package utils.Receipt.Promebel;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import utils.Main;
import utils.MainWindow;
import utils.ProjectHandler;
import utils.Receipt.Receipt;
import utils.Receipt.ReceiptManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptManagerPromebel extends ReceiptManager {


    public ReceiptManagerPromebel() {
        super();
        rootAnchorPane.getStylesheets().clear();

        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/colorsPromebel.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerCommon.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerPromebel.css").toExternalForm());

    }


    @Override
    protected void createTopPartGridPane() {

        super.createTopPartGridPane();

//        labelGeneralName.setText("\tг. Электросталь, ул. Карла Маркса 43/1, e-mail: info@tytpromebel.ru +7(926) 195-20-00");

//        gridPaneTop.getColumnConstraints().clear();
//        gridPaneTop.getRowConstraints().clear();
//        gridPaneTop.getChildren().clear();
//
//
//        for (int i = 0; i < 9; i++) {
//            ColumnConstraints column = new ColumnConstraints(100);
//            gridPaneTop.getColumnConstraints().add(column);
//        }
//        gridPaneTop.getColumnConstraints().get(0).setPrefWidth(200);
//        gridPaneTop.getColumnConstraints().get(1).setPrefWidth(120);
//        gridPaneTop.getColumnConstraints().get(2).setPrefWidth(180);
//        gridPaneTop.getColumnConstraints().get(3).setPrefWidth(100);
//        gridPaneTop.getColumnConstraints().get(4).setPrefWidth(100);
//        gridPaneTop.getColumnConstraints().get(5).setPrefWidth(70);
//        gridPaneTop.getColumnConstraints().get(6).setPrefWidth(80);
//        gridPaneTop.getColumnConstraints().get(7).setPrefWidth(60);
//        gridPaneTop.getColumnConstraints().get(8).setPrefWidth(90);
//
//
//        for (int i = 0; i < 5; i++) {
//            RowConstraints row = new RowConstraints(20);
//            gridPaneTop.getRowConstraints().add(row);
//        }
//        gridPaneTop.getRowConstraints().get(0).setPrefHeight(40);
//        gridPaneTop.getRowConstraints().get(1).setPrefHeight(20);
//        gridPaneTop.getRowConstraints().get(2).setPrefHeight(20);
//        gridPaneTop.getRowConstraints().get(3).setPrefHeight(40);
//        gridPaneTop.getRowConstraints().get(4).setPrefHeight(40);
//        //gridPaneTop.getRowConstraints().get(5).setPrefHeight(20);
//        //gridPaneTop.getRowConstraints().get(6).setPrefHeight(40);
//
////        gridPaneTop.setMaxSize(anchorPaneIntoScrollPane.getPrefWidth(), anchorPaneIntoScrollPane.getPrefHeight());
////        gridPaneTop.setMaxSize(stackPaneIntoScrollPane.getPrefWidth(), stackPaneIntoScrollPane.getPrefHeight());
//
//
//        //ImageView Logo:
//        {
//            imageViewLogo = new ImageView(getClass().getResource("/styles/images/receiptManager/PromebelLogo.png").toString());
//            imageViewLogo.setId("imageViewLogo");
//            imageViewLogo.setFitHeight(60);
//            imageViewLogo.setFitWidth(200);
//            gridPaneTop.add(imageViewLogo, 0, 0, 1, 2);
//        }
//        //label General name:
//        {
//            //labelGeneralName = new Label("\tг. Электросталь, ул. Карла Маркса 43/1, e-mail: info@tytpromebel.ru +7(926) 195-20-00");
//            labelGeneralName = new Label(Main.getProperty("companyAddress"));
//            labelGeneralName.setId("labelGeneralName");
//            labelGeneralName.setMaxWidth(Double.MAX_VALUE);
//            labelGeneralName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelGeneralName, Priority.ALWAYS);
//            GridPane.setVgrow(labelGeneralName, Priority.ALWAYS);
//            gridPaneTop.add(labelGeneralName, 1, 0, 6, 1);
//        }
//        //label date:
//        {
//            labelDate = new Label("");
//            labelDate.setId("labelDate");
//            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
//            Date date = new Date();
//            labelDate.setText(formatter.format(date));
//            labelDate.setMaxWidth(Double.MAX_VALUE);
//            labelDate.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelDate, Priority.ALWAYS);
//            GridPane.setVgrow(labelDate, Priority.ALWAYS);
//            gridPaneTop.add(labelDate, 0, 2);
//        }
//        //label Slab Count and coefficients:
//        {
//            double usesSlabs = Receipt.getUsesSlabs();
//            double coeffMaterial = ProjectHandler.getPriceMaterialCoefficient().doubleValue();
//            double coeffMain = ProjectHandler.getPriceMainCoefficient().doubleValue();
//            double allSquare = Receipt.getAllSquare();
//            labelManagerName = new Label("S:" + usesSlabs + "K:" +
//                    String.format(Locale.ENGLISH, "%.1f", coeffMain) + "P:" +
//                    String.format(Locale.ENGLISH, "%.1f", coeffMaterial) +
//                    "Sq:" + String.format(Locale.ENGLISH, "%.2f", allSquare) + "");
//            labelManagerName.setId("labelManagerName");
//            labelManagerName.setMaxWidth(Double.MAX_VALUE);
//            labelManagerName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelManagerName, Priority.ALWAYS);
//            GridPane.setVgrow(labelManagerName, Priority.ALWAYS);
//            gridPaneTop.add(labelManagerName, 7, 0, 2, 1);
//        }
//        //label Manager name:
//        {
//            labelManagerName = new Label("Менеджер ФИО/Подпись");
//            labelManagerName.setId("labelManagerName");
//            labelManagerName.setMaxWidth(Double.MAX_VALUE);
//            labelManagerName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelManagerName, Priority.ALWAYS);
//            GridPane.setVgrow(labelManagerName, Priority.ALWAYS);
//            gridPaneTop.add(labelManagerName, 7, 1, 2, 1);
//        }
//        //textField document name
//        {
//
//            //textFieldDocName = new TextField(docName);
//            textFieldDocName.setText(docName);
//            if (docName.equals("")) {
//                textFieldDocName.setText("Приложение №1 к заказу №");
//            }
//            textFieldDocName.setId("textFieldDocName");
//            textFieldDocName.setMaxWidth(Double.MAX_VALUE);
//            textFieldDocName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(textFieldDocName, Priority.ALWAYS);
//            GridPane.setVgrow(textFieldDocName, Priority.ALWAYS);
//            gridPaneTop.add(textFieldDocName, 0, 3, 9, 1);
//        }
//        //label null 51
//        {
//            labelNull51 = new Label("");
//            labelNull51.setId("labelNull51");
//            labelNull51.setMaxWidth(Double.MAX_VALUE);
//            labelNull51.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull51, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull51, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull51, 5, 1, 2,1);
//        }
//        //label costumer name
//        {
//            labelCostumerName = new Label("ФИО заказчика");
//            labelCostumerName.setId("labelCostumerName");
//            labelCostumerName.setMaxWidth(Double.MAX_VALUE);
//            labelCostumerName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelCostumerName, Priority.ALWAYS);
//            GridPane.setVgrow(labelCostumerName, Priority.ALWAYS);
//            gridPaneTop.add(labelCostumerName, 1, 1, 1, 1);
//        }
//        //label costumer address
//        {
//            labelCostumerAddress = new Label("Адрес заказчика");
//            labelCostumerAddress.setId("labelCostumerName");
//            labelCostumerAddress.setMaxWidth(Double.MAX_VALUE);
//            labelCostumerAddress.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelCostumerAddress, Priority.ALWAYS);
//            GridPane.setVgrow(labelCostumerAddress, Priority.ALWAYS);
//            gridPaneTop.add(labelCostumerAddress, 1, 2, 1, 1);
//        }
//        //textField costumer name
//        {
//            //textFieldCostumerName = new TextField(costumerName);
//            textFieldCostumerName.setText(costumerName);
//            textFieldCostumerName.setId("textFieldCostumerName");
//            textFieldCostumerName.setMaxWidth(Double.MAX_VALUE);
//            textFieldCostumerName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(textFieldCostumerName, Priority.ALWAYS);
//            GridPane.setVgrow(textFieldCostumerName, Priority.ALWAYS);
//            gridPaneTop.add(textFieldCostumerName, 2, 1, 4, 1);
//        }
//        //textField costumer address
//        {
//            //textFieldCostumerAddress = new TextField(costumerAddress);
//            textFieldCostumerAddress.setText(costumerAddress);
//            textFieldCostumerAddress.setId("textFieldCostumerAddress");
//            textFieldCostumerAddress.setMaxWidth(Double.MAX_VALUE);
//            textFieldCostumerAddress.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(textFieldCostumerAddress, Priority.ALWAYS);
//            GridPane.setVgrow(textFieldCostumerAddress, Priority.ALWAYS);
//            gridPaneTop.add(textFieldCostumerAddress, 2, 2, 4, 1);
//
//        }
//        //label textFieldManagerName
//        {
//            //textFieldManagerName = new TextField(managerName);
//            textFieldManagerName.setText(managerName);
//            textFieldManagerName.setId("textFieldManagerName");
//            textFieldManagerName.setMaxWidth(Double.MAX_VALUE);
//            textFieldManagerName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(textFieldManagerName, Priority.ALWAYS);
//            GridPane.setVgrow(textFieldManagerName, Priority.ALWAYS);
//            gridPaneTop.add(textFieldManagerName, 7, 2, 2, 2);
//        }
//        //label cashDate
//        {
//            labelCashDate = new Label("");
//            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
//            Date date = new Date();
//            //labelCashDate.setText(formatter.format(date));
//            labelCashDate.setId("labelCashDate");
//            labelCashDate.setMaxWidth(Double.MAX_VALUE);
//            labelCashDate.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelCashDate, Priority.ALWAYS);
//            GridPane.setVgrow(labelCashDate, Priority.ALWAYS);
//            //gridPaneTop.add(labelCashDate, 0, 4, 1,2);
//        }
//        //label cash USD
//        {
//            labelUSD = new Label(USD_SYMBOL + String.format(Locale.ENGLISH, "%.2f", MainWindow.getUSDValue().doubleValue()));
//            labelUSD.setId("labelUSD");
//            labelUSD.setMaxWidth(Double.MAX_VALUE);
//            labelUSD.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelUSD, Priority.ALWAYS);
//            GridPane.setVgrow(labelUSD, Priority.ALWAYS);
//            gridPaneTop.add(labelUSD, 6, 1, 1, 1);
//        }
//        //label cash EUR
//        {
//            labelEUR = new Label(EUR_SYMBOL + String.format(Locale.ENGLISH, "%.2f", MainWindow.getEURValue().doubleValue()));
//            labelEUR.setId("labelEUR");
//            labelEUR.setMaxWidth(Double.MAX_VALUE);
//            labelEUR.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelEUR, Priority.ALWAYS);
//            GridPane.setVgrow(labelEUR, Priority.ALWAYS);
//            gridPaneTop.add(labelEUR, 6, 2, 1, 1);
//        }
//        //label specification
//        {
//            labelSpecification = new Label("Спецификация");
//            labelSpecification.setId("labelSpecification");
//            labelSpecification.setMaxWidth(Double.MAX_VALUE);
//            labelSpecification.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelSpecification, Priority.ALWAYS);
//            GridPane.setVgrow(labelSpecification, Priority.ALWAYS);
//            //gridPaneTop.add(labelSpecification, 0, 4, 11,2);
//        }
//
//        //label null14
//        {
//            labelNull14 = new Label("");
//            labelNull14.setId("labelNull14");
//            labelNull14.setMaxWidth(Double.MAX_VALUE);
//            labelNull14.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull14, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull14, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull14, 1, 4, 1,2);
//        }
//        //label null24
//        {
//            labelNull24 = new Label("");
//            labelNull24.setId("labelNull24");
//            labelNull24.setMaxWidth(Double.MAX_VALUE);
//            labelNull24.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull24, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull24, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull24, 2, 4, 1,2);
//        }
//        //label null65
//        {
//            labelNull65 = new Label("");
//            labelNull65.setId("labelNull65");
//            labelNull65.setMaxWidth(Double.MAX_VALUE);
//            labelNull65.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull65, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull65, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull65, 6, 5, 1,1);
//        }
//        //label null74
//        {
//            labelNull74 = new Label("");
//            labelNull74.setId("labelNull74");
//            labelNull74.setMaxWidth(Double.MAX_VALUE);
//            labelNull74.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull74, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull74, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull74, 7, 4, 1,1);
//        }
//        //label null75
//        {
//            labelNull75 = new Label("");
//            labelNull75.setId("labelNull75");
//            labelNull75.setMaxWidth(Double.MAX_VALUE);
//            labelNull75.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull75, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull75, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull75, 7, 5, 1,1);
//        }
//        //label null84
//        {
//            labelNull84 = new Label("");
//            labelNull84.setId("labelNull84");
//            labelNull84.setMaxWidth(Double.MAX_VALUE);
//            labelNull84.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull84, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull84, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull84, 8, 4, 1,1);
//        }
//        //label null85
//        {
//            labelNull85 = new Label("");
//            labelNull85.setId("labelNull85");
//            labelNull85.setMaxWidth(Double.MAX_VALUE);
//            labelNull85.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull85, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull85, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull85, 8, 5, 1,1);
//        }
//        //label null94
//        {
//            labelNull94 = new Label("");
//            labelNull94.setId("labelNull94");
//            labelNull94.setMaxWidth(Double.MAX_VALUE);
//            labelNull94.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull94, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull94, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull94, 9, 4, 1,2);
//        }
//        //label null104
//        {
//            labelNull104 = new Label("");
//            labelNull104.setId("labelNull104");
//            labelNull104.setMaxWidth(Double.MAX_VALUE);
//            labelNull104.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull104, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull104, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull104, 10, 4, 1,2);
//        }
//        //textField coefficient
//        {
//            //System.out.println(gridPaneTop.getChildren().contains(textFieldCoefficient));
//            //textFieldCoefficient = new TextField("" + coefficient);
//            labelNull64 = new Label("");
//            labelNull64.setId("labelNull65");
//            labelNull64.setMaxWidth(Double.MAX_VALUE);
//            labelNull64.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull64, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull64, Priority.ALWAYS);
//            //gridPaneTop.add(labelNull64, 6, 4, 1,1);
//        }
//        //label labelElementType
//        {
//            labelElementType = new Label("Наименование изделия");
//            labelElementType.setId("labelElementType");
//            labelElementType.setMaxWidth(Double.MAX_VALUE);
//            labelElementType.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelElementType, Priority.ALWAYS);
//            GridPane.setVgrow(labelElementType, Priority.ALWAYS);
//            gridPaneTop.add(labelElementType, 0, 4, 1, 1);
//        }
//        //label labelStoneType
//        {
//            labelStoneType = new Label("Вид камня и Материал");
//            labelStoneType.setId("labelStoneType");
//            labelStoneType.setMaxWidth(Double.MAX_VALUE);
//            labelStoneType.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelStoneType, Priority.ALWAYS);
//            GridPane.setVgrow(labelStoneType, Priority.ALWAYS);
//            gridPaneTop.add(labelStoneType, 1, 4, 1, 1);
//        }
//        //label labelMaterial
//        {
//            labelMaterial = new Label("Материал");
//            labelMaterial.setId("labelMaterial");
//            labelMaterial.setMaxWidth(Double.MAX_VALUE);
//            labelMaterial.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelMaterial, Priority.ALWAYS);
//            GridPane.setVgrow(labelMaterial, Priority.ALWAYS);
//            //gridPaneTop.add(labelMaterial, 2, 6, 1,1);
//        }
//        //label labelCollection
//        {
//            labelCollection = new Label("Коллекция");
//            labelCollection.setId("labelCollection");
//            labelCollection.setMaxWidth(Double.MAX_VALUE);
//            labelCollection.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelCollection, Priority.ALWAYS);
//            GridPane.setVgrow(labelCollection, Priority.ALWAYS);
//            //gridPaneTop.add(labelCollection, 3, 6, 1,1);
//        }
//        //label labelColor
//        {
//            labelColor = new Label("Цвет");
//            labelColor.setId("labelColor");
//            labelColor.setMaxWidth(Double.MAX_VALUE);
//            labelColor.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelColor, Priority.ALWAYS);
//            GridPane.setVgrow(labelColor, Priority.ALWAYS);
//            gridPaneTop.add(labelColor, 2, 4, 1, 1);
//        }
//        //label labelLength
//        {
//            labelLength = new Label("Сторона 1, мм.");
//            labelLength.setWrapText(true);
//            labelLength.setId("labelLength");
//            labelLength.setMaxWidth(Double.MAX_VALUE);
//            labelLength.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelLength, Priority.ALWAYS);
//            GridPane.setVgrow(labelLength, Priority.ALWAYS);
//            gridPaneTop.add(labelLength, 3, 4, 1, 1);
//        }
//        //label labelWidth
//        {
//            labelWidth = new Label("Сторона 2, мм.");
//            labelWidth.setWrapText(true);
//            labelWidth.setId("labelWidth");
//            labelWidth.setMaxWidth(Double.MAX_VALUE);
//            labelWidth.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelWidth, Priority.ALWAYS);
//            GridPane.setVgrow(labelWidth, Priority.ALWAYS);
//            gridPaneTop.add(labelWidth, 4, 4, 1, 1);
//        }
//        //label labelInchType
//        {
//            labelInchType = new Label("Ед.");
//            labelInchType.setId("labelInchType");
//            labelInchType.setMaxWidth(Double.MAX_VALUE);
//            labelInchType.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelInchType, Priority.ALWAYS);
//            GridPane.setVgrow(labelInchType, Priority.ALWAYS);
//            gridPaneTop.add(labelInchType, 5, 4, 1, 1);
//        }
//        //label labelPrice
//        {
//            labelPrice = new Label("Цена");
//            labelPrice.setId("labelPrice");
//            labelPrice.setMaxWidth(Double.MAX_VALUE);
//            labelPrice.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelPrice, Priority.ALWAYS);
//            GridPane.setVgrow(labelPrice, Priority.ALWAYS);
//            //gridPaneTop.add(labelPrice, 6, 4, 1, 1);
//        }
//        //label labelCount
//        {
//            labelCount = new Label("Кол-во");
//            labelCount.setId("labelCount");
//            labelCount.setMaxWidth(Double.MAX_VALUE);
//            labelCount.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelCount, Priority.ALWAYS);
//            GridPane.setVgrow(labelCount, Priority.ALWAYS);
//            gridPaneTop.add(labelCount, 6, 4, 1, 1);
//        }
//        //label labelResultPrice
//        {
//            labelResultPrice = new Label("Стоимость");
//            labelResultPrice.setId("labelResultPrice");
//            labelResultPrice.setMaxWidth(Double.MAX_VALUE);
//            labelResultPrice.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelResultPrice, Priority.ALWAYS);
//            GridPane.setVgrow(labelResultPrice, Priority.ALWAYS);
//            gridPaneTop.add(labelResultPrice, 7, 4, 2, 1);
//        }

        topPartChildrenCount = gridPaneTop.getChildren().size();
    }
}
