package ru.koreanika.utils.Receipt.Zetta;


import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.*;
import ru.koreanika.utils.Receipt.Receipt;
import ru.koreanika.utils.Receipt.ReceiptItem;
import ru.koreanika.utils.Receipt.ReceiptManager;

import java.util.*;

public class ReceiptManagerZetta extends ReceiptManager {

    //private static ReceiptManagerZetta receiptManagerZetta;


//    Scene sceneReceiptManagerZetta;

    //CONTROL ELEMENTS:
//    AnchorPane rootAnchorPane;
//    SplitPane splitPaneRoot;

    //menu zone:
//    private static AnchorPane anchorPaneMenu;
//    TextField textFieldUSD, textFieldEUR;
//    Button btnPrint, btnPrintQuickly;
//    Button btnCurrencyUpdate, btnCurrencyApply;

    //list Zone:
//    static AnchorPane anchorPaneListRoot;
//    static Accordion accordionCustomItems;

    //receipt zone:
//    static AnchorPane anchorPaneReceiptRoot;
//    AnchorPane anchorPaneResultMenu;
//    ScrollPane scrollPaneResultMenu;
//    AnchorPane anchorPaneIntoScrollPane;

    //    GridPane gridPaneTop;
//    ImageView imageViewLogo;
//    Label labelGeneralName;
//    Label labelDate;
    Label labelVersion;
//    TextField textFieldDocName = new TextField();
//    Label labelManagerName;
//    Label labelNull51;
//    Label labelCostumerName;
//    Label labelCostumerAddress;
//    TextField textFieldCostumerName = new TextField();
//    TextField textFieldCostumerAddress = new TextField();
//    TextField textFieldManagerName = new TextField();
//    Label labelCashDate;
//    Label labelUSD;
//    Label labelEUR;
//    Label labelSpecification;
//    TextField textFieldCoefficient = new TextField();
//    Label labelNull14, labelNull24, labelNull64, labelNull65, labelNull74, labelNull75, labelNull84, labelNull85, labelNull94, labelNull104;
//    Label labelStoneType, labelElementType, labelMaterial, labelCollection, labelColor, labelLength;
//    Label labelWidth, labelInchType, labelPrice, labelCount, labelResultPrice;
//    Label labelAdditionalFeatureName;
//    Label labelAdditionalFeatureInches;
//    Label labelAdditionalFeaturePrice;
//    Label labelAdditionalFeatureCount;
//    Label labelAdditionalFeatureResultPrice;

    //properties zone:
    //AnchorPane anchorPaneItemPropertiesRoot;

//    private static ArrayList<ReceiptItem> customReceiptItems = new ArrayList<>();

//    private int topPartChildrenCount = 0;
    //common:
//    public static double RUBtoUSD = 0.0;
//    public static double RUBtoEUR = 0.0;

//    private static String docName = "";
//    private static String costumerName = "";
//    private static String costumerAddress = "";
//    private static String managerName = "";
//    private static double coefficient = 1;

//    private static double allPriceForRUR = 0.0;
//    private static double allPriceForUSD = 0.0;
//    private static double allPriceForEUR = 0.0;
//
//    private static double allAddPriceForRUR = 0.0;
//    private static double allAddPriceForUSD = 0.0;
//    private static double allAddPriceForEUR = 0.0;

    private double allStoneProductsPriceInUSD = 0.0;

    public ReceiptManagerZetta() {
        super();
        rootAnchorPane.getStylesheets().clear();

        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/colorsZetta.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerCommon.css").toExternalForm());
        rootAnchorPane.getStylesheets().add(getClass().getResource("/styles/TableDesigner/ReceiptManager/receiptManagerZetta.css").toExternalForm());
    }


    @Override
    protected void createTopPartGridPane() {

        super.createTopPartGridPane();


//        labelGeneralName.setText("\t123022, Москва, Расторгуевский пер., д. 1 тел.: (495) 510-19-19 email: sek@zetta.ru www.zetta.ru");

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
//        for (int i = 0; i < 6; i++) {
//            RowConstraints row = new RowConstraints(20);
//            gridPaneTop.getRowConstraints().add(row);
//        }
//        gridPaneTop.getRowConstraints().get(0).setPrefHeight(60);
//        gridPaneTop.getRowConstraints().get(1).setPrefHeight(20);
//        gridPaneTop.getRowConstraints().get(2).setPrefHeight(20);
//        gridPaneTop.getRowConstraints().get(3).setPrefHeight(20);
//        gridPaneTop.getRowConstraints().get(4).setPrefHeight(40);
//        gridPaneTop.getRowConstraints().get(5).setPrefHeight(40);
//        //gridPaneTop.getRowConstraints().get(5).setPrefHeight(20);
//        //gridPaneTop.getRowConstraints().get(6).setPrefHeight(40);
//
////        gridPaneTop.setMaxSize(anchorPaneIntoScrollPane.getPrefWidth(), anchorPaneIntoScrollPane.getPrefHeight());
//
//
//        //ImageView Logo:
//        {
//            imageViewLogo = new ImageView(ProjectHandler.class.getResource("/styles/images/receiptManager/ZettaLogo.jpeg").toString());
//            imageViewLogo.setId("imageViewLogo");
//            imageViewLogo.setFitHeight(60);
//            imageViewLogo.setFitWidth(850);
//            gridPaneTop.add(imageViewLogo, 0, 0, 6, 1);
//        }
//        //label General name:
//        {
//            labelGeneralName = new Label("\tООО \"Кореаника\" Балашиха, мкр. Гагарина 13а e-mail: info@koreanika.ru +7(495) 665-82-95");
//            labelGeneralName.setId("labelGeneralName");
//            labelGeneralName.setMaxWidth(Double.MAX_VALUE);
//            labelGeneralName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelGeneralName, Priority.ALWAYS);
//            GridPane.setVgrow(labelGeneralName, Priority.ALWAYS);
//            //gridPaneTop.add(labelGeneralName, 1, 0, 6, 1);
//        }
//        //labelVersion:
//        {
//            labelVersion = new Label("");
//            labelVersion.setId("labelVersion");
//            Date date = new Date();
//            labelVersion.setText("v." + Main.appVersion);
//            labelVersion.setMaxWidth(Double.MAX_VALUE);
//            labelVersion.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelVersion, Priority.ALWAYS);
//            GridPane.setVgrow(labelVersion, Priority.ALWAYS);
//            gridPaneTop.add(labelVersion, 7, 0, 2, 1);
//        }
//        //label null 51
//        {
//            labelNull51 = new Label("");
//            labelNull51.setId("labelNull51");
//            labelNull51.setMaxWidth(Double.MAX_VALUE);
//            labelNull51.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelNull51, Priority.ALWAYS);
//            GridPane.setVgrow(labelNull51, Priority.ALWAYS);
//            gridPaneTop.add(labelNull51, 0, 1, 1, 1);
//        }
//
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
//            gridPaneTop.add(labelDate, 1, 1, 1, 1);
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
//            gridPaneTop.add(textFieldDocName, 2, 1, 4, 1);
//        }
//        //label Manager name:
//        {
//            labelManagerName = new Label("Менеджер ФИО/Подпись");
//            labelManagerName.setId("labelManagerName");
//            labelManagerName.setMaxWidth(Double.MAX_VALUE);
//            labelManagerName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelManagerName, Priority.ALWAYS);
//            GridPane.setVgrow(labelManagerName, Priority.ALWAYS);
//            gridPaneTop.add(labelManagerName, 6, 1, 3, 1);
//        }
//
//
//        //label costumer name
//        {
//            labelCostumerName = new Label("ФИО заказчика");
//            labelCostumerName.setId("labelCostumerName");
//            labelCostumerName.setMaxWidth(Double.MAX_VALUE);
//            labelCostumerName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelCostumerName, Priority.ALWAYS);
//            GridPane.setVgrow(labelCostumerName, Priority.ALWAYS);
//            gridPaneTop.add(labelCostumerName, 0, 2, 1, 1);
//        }
//        //label costumer address
//        {
//            labelCostumerAddress = new Label("Адрес заказчика");
//            labelCostumerAddress.setId("labelCostumerName");
//            labelCostumerAddress.setMaxWidth(Double.MAX_VALUE);
//            labelCostumerAddress.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelCostumerAddress, Priority.ALWAYS);
//            GridPane.setVgrow(labelCostumerAddress, Priority.ALWAYS);
//            gridPaneTop.add(labelCostumerAddress, 0, 3, 1, 1);
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
//            gridPaneTop.add(textFieldCostumerName, 1, 2, 5, 1);
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
//            gridPaneTop.add(textFieldCostumerAddress, 1, 3, 5, 1);
//
//        }
//        //label textFieldManagerName
//        {
//            //textFieldManagerName = new TextField(managerName);
//
//            textFieldManagerName.setText(managerName);
//            textFieldManagerName.setId("textFieldManagerName");
//            textFieldManagerName.setMaxWidth(Double.MAX_VALUE);
//            textFieldManagerName.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(textFieldManagerName, Priority.ALWAYS);
//            GridPane.setVgrow(textFieldManagerName, Priority.ALWAYS);
//            gridPaneTop.add(textFieldManagerName, 6, 2, 3, 3);
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
//            gridPaneTop.add(labelUSD, 0, 4, 1, 1);
//        }
//        //label cash EUR
//        {
//            labelEUR = new Label(EUR_SYMBOL + String.format(Locale.ENGLISH, "%.2f", MainWindow.getEURValue().doubleValue()));
//            labelEUR.setId("labelEUR");
//            labelEUR.setMaxWidth(Double.MAX_VALUE);
//            labelEUR.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelEUR, Priority.ALWAYS);
//            GridPane.setVgrow(labelEUR, Priority.ALWAYS);
//            gridPaneTop.add(labelEUR, 1, 4, 1, 1);
//        }
//        //label specification
//        {
//            labelSpecification = new Label("Спецификация");
//            labelSpecification.setId("labelSpecification");
//            labelSpecification.setMaxWidth(Double.MAX_VALUE);
//            labelSpecification.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelSpecification, Priority.ALWAYS);
//            GridPane.setVgrow(labelSpecification, Priority.ALWAYS);
//            gridPaneTop.add(labelSpecification, 2, 4, 4, 1);
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
//            //gridPaneTop.add(labelNull14, 0, 2, 1,1);
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
//            gridPaneTop.add(labelElementType, 0, 5, 1, 1);
//        }
//        //label labelStoneType
//        {
//            labelStoneType = new Label("Вид камня и Материал");
//            labelStoneType.setId("labelStoneType");
//            labelStoneType.setMaxWidth(Double.MAX_VALUE);
//            labelStoneType.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelStoneType, Priority.ALWAYS);
//            GridPane.setVgrow(labelStoneType, Priority.ALWAYS);
//            gridPaneTop.add(labelStoneType, 1, 5, 1, 1);
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
//            gridPaneTop.add(labelColor, 2, 5, 1, 1);
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
//            gridPaneTop.add(labelLength, 3, 5, 1, 1);
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
//            gridPaneTop.add(labelWidth, 4, 5, 1, 1);
//        }
//        //label labelInchType
//        {
//            labelInchType = new Label("Ед.");
//            labelInchType.setId("labelInchType");
//            labelInchType.setMaxWidth(Double.MAX_VALUE);
//            labelInchType.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelInchType, Priority.ALWAYS);
//            GridPane.setVgrow(labelInchType, Priority.ALWAYS);
//            gridPaneTop.add(labelInchType, 5, 5, 1, 1);
//        }
//        //label labelPrice
//        {
//            labelPrice = new Label("Цена");
//            labelPrice.setId("labelPrice");
//            labelPrice.setMaxWidth(Double.MAX_VALUE);
//            labelPrice.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelPrice, Priority.ALWAYS);
//            GridPane.setVgrow(labelPrice, Priority.ALWAYS);
//            //gridPaneTop.add(labelPrice, 6, 5, 1, 1);
//        }
//        //label labelCount
//        {
//            labelCount = new Label("Кол-во");
//            labelCount.setId("labelCount");
//            labelCount.setMaxWidth(Double.MAX_VALUE);
//            labelCount.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelCount, Priority.ALWAYS);
//            GridPane.setVgrow(labelCount, Priority.ALWAYS);
//            gridPaneTop.add(labelCount, 6, 5, 1, 1);
//        }
//        //label labelResultPrice
//        {
//            labelResultPrice = new Label("Стоимость");
//            labelResultPrice.setId("labelResultPrice");
//            labelResultPrice.setMaxWidth(Double.MAX_VALUE);
//            labelResultPrice.setMaxHeight(Double.MAX_VALUE);
//            GridPane.setHgrow(labelResultPrice, Priority.ALWAYS);
//            GridPane.setVgrow(labelResultPrice, Priority.ALWAYS);
//            gridPaneTop.add(labelResultPrice, 7, 5, 2, 1);
//        }

        topPartChildrenCount = gridPaneTop.getChildren().size();
    }


    /**
     * FOR TABLE ProjectType START KOREANIKA
     */

    @Override
    protected void createMountPartGridPaneTD() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double addPriceUSD = 0;

        for (ReceiptItem receiptItem : TableDesigner.getMountingReceiptList()) {
            RowConstraints row2 = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row2);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelMountName
            {

                Label labelMountName = new Label(receiptItem.getName());
                labelMountName.setAlignment(Pos.CENTER_LEFT);
                labelMountName.getStyleClass().add("labelProduct-right");
                labelMountName.getStyleClass().add("labelProductPrice");
                labelMountName.setMaxWidth(Double.MAX_VALUE);
                labelMountName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountName, Priority.ALWAYS);
                GridPane.setVgrow(labelMountName, Priority.ALWAYS);
                gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
            }

            //label labelMountPercent
            {
                Label labelMountPercent = new Label();
                labelMountPercent.setText(receiptItem.getPriceForOne() + "%");
                labelMountPercent.getStyleClass().add("labelProductPrice");
                labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            }

            //label labelMountPrice
            {
                double price = ((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0);
                if (price * RUBtoUSD < 4000) price = 4000 / RUBtoUSD;

                Label labelMountPrice = new Label();
                //labelMountPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                //labelMountPrice.setText("$" + formatPrice(price));
                labelMountPrice.setText(RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelMountPrice.getStyleClass().add("labelProductPrice");
                labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                addPriceUSD += price;

            }

            //calculate allPrice:
            {
                allPriceForUSD += addPriceUSD;
            }

        }
    }

    /**
     * DISCOUNT should be placed before services works
     */
    @Override
    protected void createDiscountPartGridPaneTD() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();


        double discountPriceInUSD = 0;

        for (ReceiptItem receiptItem : TableDesigner.getDiscountReceiptList()) {
            RowConstraints row2 = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row2);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelDiscountName
            {

                Label labelDiscountName = new Label(receiptItem.getName());
                labelDiscountName.setAlignment(Pos.CENTER_LEFT);
                labelDiscountName.getStyleClass().add("labelProduct");
                labelDiscountName.setMaxWidth(Double.MAX_VALUE);
                labelDiscountName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDiscountName, Priority.ALWAYS);
                GridPane.setVgrow(labelDiscountName, Priority.ALWAYS);
                gridPaneTop.add(labelDiscountName, 0, rowIndex, 6, 1);
            }

            //label labelDiscountPercent
            {
                Label labelDiscountPercent = new Label();
                labelDiscountPercent.setText(receiptItem.getPriceForOne() + "%");
                labelDiscountPercent.getStyleClass().add("labelProduct");
                labelDiscountPercent.setMaxWidth(Double.MAX_VALUE);
                labelDiscountPercent.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDiscountPercent, Priority.ALWAYS);
                GridPane.setVgrow(labelDiscountPercent, Priority.ALWAYS);
                gridPaneTop.add(labelDiscountPercent, 6, rowIndex, 1, 1);
            }

            //label labelDiscountPrice
            {

                System.out.println("price part in RUR = " + allPriceForRUR);
                System.out.println("price part in USD = " + allPriceForUSD);
                System.out.println("price part in EUR = " + allPriceForEUR);
                System.out.println("ALL PRICE ID USD =" + ((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)));

                double price = -1 * (((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0));

                Label labelDiscountPrice = new Label();
                //labelDiscountPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                //labelDiscountPrice.setText("$" + formatPrice(price));
                labelDiscountPrice.setText(RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelDiscountPrice.getStyleClass().add("labelProductPrice");
                labelDiscountPrice.setMaxWidth(Double.MAX_VALUE);
                labelDiscountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDiscountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDiscountPrice, Priority.ALWAYS);
                gridPaneTop.add(labelDiscountPrice, 7, rowIndex, 2, 1);

                //calculate allPrice:
                {
                    //ReceiptItem receiptItem = entry.getValue();
                    discountPriceInUSD = price;

                }

            }


        }

        allPriceForUSD += discountPriceInUSD;


    }


    /* Services works: (START)*/
    double servicesWorksPriceInUSD = 0;
    @Override
    protected void createDeliveryPartGridPaneTD() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        for (ReceiptItem receiptItem : TableDesigner.getDeliveryReceiptList()) {

            int rowIndex;

            //Cutout cutout = entry.getKey();
            //Material material = cutout.getSketchShapeOwner().getMaterial();
            //ReceiptItem receiptItem = entry.getValue();

            Label labelDeliveryValueName = new Label(receiptItem.getName());
            Label labelDeliveryValueSubName = new Label("");
            Label labelDeliveryPrice = new Label(receiptItem.getPriceForOne());
            Label labelDeliveryCount = new Label(receiptItem.getCount());
            Label labelDeliveryResultPrice = new Label(receiptItem.getAllPrice());

            receiptItem.setCoefficient(coefficient);

            String currency = "*";
            if (receiptItem.getCurrency().equals("USD")) currency = USD_SYMBOL;
            else if (receiptItem.getCurrency().equals("EUR")) currency = EUR_SYMBOL;
            if (receiptItem.getCurrency().equals("RUB")) currency = RUR_SYMBOL;


            labelDeliveryValueName.setText(receiptItem.getName());

            labelDeliveryPrice.setText(currency + receiptItem.getPriceForOne());
            labelDeliveryCount.setText(receiptItem.getCount());
            labelDeliveryResultPrice.setText(currency + receiptItem.getAllPrice());

            RowConstraints rowForEdge = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(rowForEdge);

            //labelDeliveryValueName:
            {
                labelDeliveryValueName.getStyleClass().add("labelProduct");
                labelDeliveryValueName.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryValueName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryValueName, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryValueName, Priority.ALWAYS);
                labelDeliveryValueName.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryValueName, 0, rowIndex, 5, 1);
            }
            //labelDeliveryValueSubName:
            {
                labelDeliveryValueSubName.getStyleClass().add("labelProduct");
                labelDeliveryValueSubName.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryValueSubName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryValueSubName, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryValueSubName, Priority.ALWAYS);
                labelDeliveryValueSubName.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelDeliveryValueSubName, 2, rowIndex, 2, 1);
            }

            //labelDeliveryNull1:
            {
                Label labelDeliveryNull1 = new Label();
                labelDeliveryNull1.getStyleClass().add("labelProduct");
                labelDeliveryNull1.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryNull1.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryNull1, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryNull1, Priority.ALWAYS);
                labelDeliveryNull1.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelEdgeNull1, 3, rowIndex, 1, 1);
            }
            //labelDeliveryNull2:
            {
                Label labelDeliveryNull2 = new Label();
                labelDeliveryNull2.getStyleClass().add("labelProduct");
                labelDeliveryNull2.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryNull2.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryNull2, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryNull2, Priority.ALWAYS);
                labelDeliveryNull2.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelDeliveryNull2, 4, rowIndex, 1, 1);
            }
            //labelDeliveryInches:
            {
                Label labelDeliveryInches = new Label(receiptItem.getUnits());
                labelDeliveryInches.getStyleClass().add("labelProduct");
                labelDeliveryInches.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryInches.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryInches, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryInches, Priority.ALWAYS);
                labelDeliveryInches.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryInches, 5, rowIndex, 1, 1);
            }
            //labelDeliveryPrice:
            {
                labelDeliveryPrice.getStyleClass().add("labelProduct");
                labelDeliveryPrice.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryPrice, Priority.ALWAYS);
                labelDeliveryPrice.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryPrice, 6, rowIndex, 1, 1);
            }
            //labelDeliveryCount:
            {
                labelDeliveryCount.getStyleClass().add("labelProduct");
                labelDeliveryCount.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryCount.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryCount, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryCount, Priority.ALWAYS);
                labelDeliveryCount.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryCount, 7, rowIndex, 1, 1);
            }
            //labelDeliveryResultPrice:
            {
                labelDeliveryResultPrice.getStyleClass().add("labelProductPrice");
                labelDeliveryResultPrice.setMaxWidth(Double.MAX_VALUE);
                labelDeliveryResultPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelDeliveryResultPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelDeliveryResultPrice, Priority.ALWAYS);
                labelDeliveryResultPrice.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelDeliveryResultPrice, 8, rowIndex, 1, 1);
            }

            //calculate servicesWorksPriceInUSD
            {
                if (receiptItem.getCurrency().equals("USD")) {
                    servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                } else if (receiptItem.getCurrency().equals("EUR")) {
                    servicesWorksPriceInUSD += (Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) * RUBtoEUR) / RUBtoUSD;
                } else if (receiptItem.getCurrency().equals("RUB")) {
                    servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) / RUBtoUSD;
                }
            }
            //calculate allPrice:
//            {
//                //ReceiptItem receiptItem = entry.getValue();
//                if (receiptItem.getCurrency().equals("USD"))
//                    allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                else if (receiptItem.getCurrency().equals("EUR"))
//                    allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                if (receiptItem.getCurrency().equals("RUB"))
//                    allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//            }

            //calculate allAddPrice:
//            {
//                //ReceiptItem receiptItem = entry.getValue();
//                if (receiptItem.getCurrency().equals("USD"))
//                    allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                else if (receiptItem.getCurrency().equals("EUR"))
//                    allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                if (receiptItem.getCurrency().equals("RUB"))
//                    allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//            }

        }
    }
    @Override
    protected void createMeasuringPartGridPaneTD() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        for (ReceiptItem receiptItem : TableDesigner.getMeasurerReceiptList()) {

            int rowIndex;

            //Cutout cutout = entry.getKey();
            //Material material = cutout.getSketchShapeOwner().getMaterial();
            //ReceiptItem receiptItem = entry.getValue();

            Label labelMeasuringValueName = new Label(receiptItem.getName());
            Label labelMeasuringValueSubName = new Label("");
            Label labelMeasuringPrice = new Label(receiptItem.getPriceForOne());
            Label labelMeasuringCount = new Label(receiptItem.getCount());
            Label labelMeasuringResultPrice = new Label(receiptItem.getAllPrice());

            receiptItem.setCoefficient(coefficient);

            String currency = "*";
            if (receiptItem.getCurrency().equals("USD")) currency = USD_SYMBOL;
            else if (receiptItem.getCurrency().equals("EUR")) currency = EUR_SYMBOL;
            if (receiptItem.getCurrency().equals("RUB")) currency = RUR_SYMBOL;


            labelMeasuringValueName.setText(receiptItem.getName());

            labelMeasuringPrice.setText(currency + receiptItem.getPriceForOne());
            labelMeasuringCount.setText(receiptItem.getCount());
            labelMeasuringResultPrice.setText(currency + receiptItem.getAllPrice());

            RowConstraints rowForEdge = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(rowForEdge);

            //labelMeasuringValueName:
            {
                labelMeasuringValueName.getStyleClass().add("labelProduct");
                labelMeasuringValueName.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringValueName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringValueName, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringValueName, Priority.ALWAYS);
                labelMeasuringValueName.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringValueName, 0, rowIndex, 5, 1);
            }
            //labelMeasuringValueSubName:
            {
                labelMeasuringValueSubName.getStyleClass().add("labelProduct");
                labelMeasuringValueSubName.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringValueSubName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringValueSubName, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringValueSubName, Priority.ALWAYS);
                labelMeasuringValueSubName.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelMeasuringValueSubName, 2, rowIndex, 2, 1);
            }

            //labelMeasuringNull1:
            {
                Label labelMeasuringNull1 = new Label();
                labelMeasuringNull1.getStyleClass().add("labelProduct");
                labelMeasuringNull1.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringNull1.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringNull1, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringNull1, Priority.ALWAYS);
                labelMeasuringNull1.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelEdgeNull1, 3, rowIndex, 1, 1);
            }
            //labelMeasuringNull2:
            {
                Label labelMeasuringNull2 = new Label();
                labelMeasuringNull2.getStyleClass().add("labelProduct");
                labelMeasuringNull2.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringNull2.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringNull2, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringNull2, Priority.ALWAYS);
                labelMeasuringNull2.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //gridPaneTop.add(labelMeasuringNull2, 4, rowIndex, 1, 1);
            }
            //labelMeasuringNull2:
            {
                Label labelMeasuringInches = new Label(receiptItem.getUnits());
                labelMeasuringInches.getStyleClass().add("labelProduct");
                labelMeasuringInches.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringInches.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringInches, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringInches, Priority.ALWAYS);
                labelMeasuringInches.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringInches, 5, rowIndex, 1, 1);
            }
            //labelMeasuringPrice:
            {
                labelMeasuringPrice.getStyleClass().add("labelProduct");
                labelMeasuringPrice.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringPrice, Priority.ALWAYS);
                labelMeasuringPrice.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringPrice, 6, rowIndex, 1, 1);
            }
            //labelMeasuringCount:
            {
                labelMeasuringCount.getStyleClass().add("labelProduct");
                labelMeasuringCount.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringCount.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringCount, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringCount, Priority.ALWAYS);
                labelMeasuringCount.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringCount, 7, rowIndex, 1, 1);
            }
            //labelMeasuringResultPrice:
            {
                labelMeasuringResultPrice.getStyleClass().add("labelProductPrice");
                labelMeasuringResultPrice.setMaxWidth(Double.MAX_VALUE);
                labelMeasuringResultPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMeasuringResultPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMeasuringResultPrice, Priority.ALWAYS);
                labelMeasuringResultPrice.setWrapText(true);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                gridPaneTop.add(labelMeasuringResultPrice, 8, rowIndex, 1, 1);
            }

            //calculate servicesWorksPriceInUSD
            {
                if (receiptItem.getCurrency().equals("USD")) {
                    servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                } else if (receiptItem.getCurrency().equals("EUR")) {
                    servicesWorksPriceInUSD += (Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) * RUBtoEUR) / RUBtoUSD;
                } else if (receiptItem.getCurrency().equals("RUB")) {
                    servicesWorksPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.')) / RUBtoUSD;
                }
            }

            //calculate allPrice:
//            {
//                //ReceiptItem receiptItem = entry.getValue();
//                if (receiptItem.getCurrency().equals("USD"))
//                    allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                else if (receiptItem.getCurrency().equals("EUR"))
//                    allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                if (receiptItem.getCurrency().equals("RUB"))
//                    allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//            }

            //calculate allAddPrice:
//            {
//                //ReceiptItem receiptItem = entry.getValue();
//                if (receiptItem.getCurrency().equals("USD"))
//                    allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                else if (receiptItem.getCurrency().equals("EUR"))
//                    allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//                if (receiptItem.getCurrency().equals("RUB"))
//                    allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
//            }

        }

    }



    /* Services works: (END)*/

//    @Override
//    public void updateReceiptTable() {
//
//        System.out.println("UPDATE RECEIPT TABLE ");
//        allPriceForRUR = 0.0;
//        allPriceForUSD = 0.0;
//        allPriceForEUR = 0.0;
//
//        allAddPriceForRUR = 0.0;
//        allAddPriceForUSD = 0.0;
//        allAddPriceForEUR = 0.0;
//
//
//        createTopPartGridPane();
//        createMaterialsPartGridPane();
//        if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
//
//
//            createImagesPartGridPane();
//            createEdgesAndBordersPartGridPane();
//            createLeakGroovePartGridPane();
//            createStoneHemPartGridPane();
//
//            createSinkPartGridPane();
//            createSinkInstallTypesPartGridPane();
//            createJointsPartGridPane();
//            createRadiusElementsPartGridPane();
//
//            createAdditionalPartTop();
//            createCutoutPartGridPane();
//            createPlumbingAlveusPartGridPaneTD();
//            createGroovesPartGridPane();
//            createRodsPartGridPane();
////            createCustomReceiptItemsPartGridPane();
//        } else {
//            //AdditionalPart
//            //MainWork
//            //AdditionalWork
//            createSinkQuarzPartGridPaneTD();
//            createImagesPartGridPaneTD();
//            createEdgesAndBordersPartGridPaneTD();
//            createLeakGroovePartGridPaneTD();
//            createStoneHemPartGridPaneTD();
//
//            createSinkAcrylPartGridPaneTD();
//            createSinkInstallTypesPartGridPaneTD();
//            createJointsPartGridPaneTD();
//            createRadiusElementsPartGridPaneTD();
//
//            // receiptManager.createAdditionalPartTopTD();
//            createCutoutPartGridPaneTD();
//            createPlumbingAlveusPartGridPaneTD();
//            createGroovesPartGridPaneTD();
//            createRodsPartGridPaneTD();
//            //receiptManager.createCustomReceiptItemsPartGridPaneTD();
//
//            createMetalFootingPartGridPaneTD();
//            createPlywoodPartGridPaneTD();
//            createStonePolishingPartGridPaneTD();
//            createSiphonPartGridPaneTD();
//            createCustomPartGridPaneTD();
//        }
//
//
//        createResultPart();
//
//
//        double gridPaneHeight = 0;
//        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
//            gridPaneHeight += rowConstraints.getPrefHeight();
//        }
////        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
//
//
////        System.out.println("allPriceForRUR = " + allPriceForRUR);
////        System.out.println("allPriceForUSD = " + allPriceForUSD);
////        System.out.println("allPriceForEUR = " + allPriceForEUR);
//    }

    @Override
    protected void createResultPart() {

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();


        createDiscountPartGridPaneTD();

        /** All product price in USD */
        {
            RowConstraints row = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row);

            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelAdditionalFeatureName
            {

                Label labelAllPriceName = new Label("  Итого стоимость изделия в долларах США");
                labelAllPriceName.setAlignment(Pos.CENTER_LEFT);
                labelAllPriceName.getStyleClass().add("labelTableResult");
                labelAllPriceName.setMaxWidth(Double.MAX_VALUE);
                labelAllPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAllPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelAllPriceName, Priority.ALWAYS);
                gridPaneTop.add(labelAllPriceName, 0, rowIndex, 7, 1);
            }

            //label labelAllPrice
            {
                double price = (allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD);
                Label labelAllPrice = new Label();
                //labelAllPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                labelAllPrice.setText(USD_SYMBOL + formatPrice(price));
                //labelAllPrice.setText(RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelAllPrice.getStyleClass().add("labelTableResultPrice");
                labelAllPrice.setMaxWidth(Double.MAX_VALUE);
                labelAllPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAllPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelAllPrice, Priority.ALWAYS);
                gridPaneTop.add(labelAllPrice, 7, rowIndex, 2, 1);
            }
        }

        /** Services works*/
        {
            RowConstraints row = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row);

            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelServicesWorksName
            {

                Label labelServicesWorksName = new Label("Сервисные услуги");
                labelServicesWorksName.setAlignment(Pos.CENTER_LEFT);
                labelServicesWorksName.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksName.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksName, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksName, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksName, 0, rowIndex, 5, 1);
            }

            //label labelServicesWorksInches
            {
                Label labelServicesWorksInches = new Label("Ед.");
                labelServicesWorksInches.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksInches.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksInches.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksInches, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksInches, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksInches, 5, rowIndex, 1, 1);
            }
            //label labelServicesWorksCost
            {
                Label labelServicesWorksCost = new Label("Цена");
                labelServicesWorksCost.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksCost.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksCost.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksCost, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksCost, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksCost, 6, rowIndex, 1, 1);
            }
            //label labelServicesWorksCount
            {
                Label labelServicesWorksCount = new Label("Кол-во");
                labelServicesWorksCount.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksCount.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksCount.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksCount, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksCount, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksCount, 7, rowIndex, 1, 1);
            }
            //label labelServicesWorksPrice
            {
                Label labelServicesWorksPrice = new Label("Стоимость");
                labelServicesWorksPrice.getStyleClass().add("labelTableHeader-2");
                labelServicesWorksPrice.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksPrice, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksPrice, 8, rowIndex, 1, 1);
            }

        }

        servicesWorksPriceInUSD = 0;


        createDeliveryPartGridPaneTD();
        createMeasuringPartGridPaneTD();

        /** Services work price*/
        {
            RowConstraints row = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row);

            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelServicesWorksPriceName
            {

                Label labelServicesWorksPriceName = new Label("  Итого стоимость Сервисных услуг в долларах США");
                labelServicesWorksPriceName.setAlignment(Pos.CENTER_LEFT);
                labelServicesWorksPriceName.getStyleClass().add("labelTableResult");
                labelServicesWorksPriceName.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksPriceName, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksPriceName, 0, rowIndex, 7, 1);
            }

            //label labelServicesWorksPriceValue
            {
                Label labelServicesWorksPriceValue = new Label();
                //labelAdditionalAllPrice.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                labelServicesWorksPriceValue.setText(USD_SYMBOL + formatPrice(servicesWorksPriceInUSD));
                //labelServicesWorksPriceValue.setText(RUR_SYMBOL + formatPrice(servicesWorksPriceInUSD * RUBtoUSD));
                labelServicesWorksPriceValue.getStyleClass().add("labelTableResultPrice");
                labelServicesWorksPriceValue.setMaxWidth(Double.MAX_VALUE);
                labelServicesWorksPriceValue.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelServicesWorksPriceValue, Priority.ALWAYS);
                GridPane.setVgrow(labelServicesWorksPriceValue, Priority.ALWAYS);
                gridPaneTop.add(labelServicesWorksPriceValue, 7, rowIndex, 2, 1);

            }

        }


        System.out.println("all  price in RUR = " + (allPriceForRUR + allPriceForUSD * RUBtoUSD + allPriceForEUR * RUBtoEUR));
        System.out.println("all add price in RUR = " + (allAddPriceForRUR + allAddPriceForUSD * RUBtoUSD + allAddPriceForEUR * RUBtoEUR));

        /** MOUNT */
        {
            createMountPartGridPaneTD();
        }

        allPriceForUSD += servicesWorksPriceInUSD;

        /** result price in USD*/
        {
            RowConstraints row3 = new RowConstraints(40);
            //gridPaneTop.getRowConstraints().add(row3);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelResultPriceUSDName
            {

                Label labelResultPriceUSDName = new Label("  Итог по заказу в долларах США:");
                labelResultPriceUSDName.setAlignment(Pos.CENTER_LEFT);
                labelResultPriceUSDName.setId("labelResultPriceUSDName");
                labelResultPriceUSDName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceUSDName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceUSDName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceUSDName, Priority.ALWAYS);
                //gridPaneTop.add(labelResultPriceUSDName, 0, rowIndex, 7, 1);
            }


            //label labelResultPriceUSD
            {
                double price = (allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD);

                Label labelResultPriceUSD = new Label();
                //labelResultPriceUSD.setText(String.format(Locale.ENGLISH, "$%.2f", price));
                labelResultPriceUSD.setText(USD_SYMBOL + formatPrice(price));
                labelResultPriceUSD.setId("labelResultPriceUSD");
                labelResultPriceUSD.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceUSD.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceUSD, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceUSD, Priority.ALWAYS);
                //gridPaneTop.add(labelResultPriceUSD, 7, rowIndex, 2, 1);
            }
        }

        /** additional price percent for small product */
//        System.out.println("stoneItems = " + stoneItems);

        {

            double coeff = Receipt.getAdditionalPriceCoefficientForAcryl();

            if(stoneItems == 0){
                coeff = 0;
            }

            double price = (allPriceForRUR + allPriceForUSD * RUBtoUSD + allPriceForEUR * RUBtoEUR) * coeff;//in RUR
            if (coeff != 0.0) {

                RowConstraints row2 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row2);
                int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //label labelSmallProductName
                {

                    Label labelSmallProductName = new Label(" Доплата за изделие менее 2 кв.м.");
                    //labelSmallProductName.setText(receiptItem.getName());
                    labelSmallProductName.setAlignment(Pos.CENTER_LEFT);
                    labelSmallProductName.getStyleClass().add("labelProduct-right");
                    labelSmallProductName.getStyleClass().add("labelProduct");
                    labelSmallProductName.setMaxWidth(Double.MAX_VALUE);
                    labelSmallProductName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelSmallProductName, Priority.ALWAYS);
                    GridPane.setVgrow(labelSmallProductName, Priority.ALWAYS);
                    gridPaneTop.add(labelSmallProductName, 0, rowIndex, 6, 1);
                }

                //label labelSmallProductPercent
                {
                    Label labelSmallProductPercent = new Label();
                    //labelMountPercent.setText(Receipt.getAdditionalPricePercent() + "%");
                    labelSmallProductPercent.setText("");
                    labelSmallProductPercent.getStyleClass().add("labelProduct");
                    labelSmallProductPercent.setMaxWidth(Double.MAX_VALUE);
                    labelSmallProductPercent.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelSmallProductPercent, Priority.ALWAYS);
                    GridPane.setVgrow(labelSmallProductPercent, Priority.ALWAYS);
                    gridPaneTop.add(labelSmallProductPercent, 6, rowIndex, 1, 1);
                }

                //label labelSmallProductPrice
                {
                    //double price = receiptItem.getAllPrice();


                    Label labelSmallProductPrice = new Label();
                    //labelSmallProductPrice.setText(String.format(Locale.ENGLISH, USD_SYMBOL + "%.2f", price));
                    labelSmallProductPrice.setText(String.format(Locale.ENGLISH, RUR_SYMBOL + "%.0f", price));
                    //labelMountPrice.setText(USD_SYMBOL + price);
                    labelSmallProductPrice.getStyleClass().add("labelProductPrice");
                    labelSmallProductPrice.setMaxWidth(Double.MAX_VALUE);
                    labelSmallProductPrice.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelSmallProductPrice, Priority.ALWAYS);
                    GridPane.setVgrow(labelSmallProductPrice, Priority.ALWAYS);
                    gridPaneTop.add(labelSmallProductPrice, 7, rowIndex, 2, 1);


                    //calculate allPrice:
                    {
                        //ReceiptItem receiptItem = entry.getValue();
                        allPriceForUSD += price/RUBtoUSD;
                    }

                }
            }
        }

        /** result price in RUR*/
        {
            RowConstraints row4 = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row4);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelResultPriceRURName
            {

                Label labelResultPriceRURName = new Label("  Стоимость в рублях действительна на день просчета:");
                labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);
                labelResultPriceRURName.getStyleClass().add("labelTableResultEnd");
                labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            }


            //label labelResultPriceRUR
            {
                System.out.println("allPriceForRUR = " + allPriceForRUR);
                System.out.println("allPriceForUSD = " + allPriceForUSD);
                System.out.println("allPriceForEUR = " + allPriceForEUR);
                double price = ((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * RUBtoUSD;

                Label labelResultPriceRUR = new Label();
                //labelResultPriceRUR.setText(String.format(Locale.ENGLISH, "₽%.2f", price));
                labelResultPriceRUR.setText(RUR_SYMBOL + formatPrice(price));
                labelResultPriceRUR.getStyleClass().add("labelTableResultEndPrice");
                labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
            }


        }

        /** FOR CLIENT*/
        {


            RowConstraints row = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row);
            int rowIndex = gridPaneTop.getRowConstraints().size() - 1;

            //label labelText
            {
                Label labelText = new Label("С выбранным декором и конфигурацией согласен");
                //labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
                labelText.setMaxWidth(Double.MAX_VALUE);
                labelText.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelText, Priority.ALWAYS);
                GridPane.setVgrow(labelText, Priority.ALWAYS);
                labelText.getStyleClass().add("labelProduct");
                gridPaneTop.add(labelText, 0, rowIndex, 7, 1);
            }
            //label labelField
            {
                Label labelField = new Label("");
                //labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
                labelField.setMaxWidth(Double.MAX_VALUE);
                labelField.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelField, Priority.ALWAYS);
                GridPane.setVgrow(labelField, Priority.ALWAYS);
                labelField.getStyleClass().add("labelProduct");
                gridPaneTop.add(labelField, 7, rowIndex, 2, 1);
            }

        }

        /** NOTIFICATION if have old prices*/
        {

            if (!Receipt.pricesActual) {
                RowConstraints row5 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row5);
                int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                Label labelPricesNotActual = new Label("Внимание! Цены на материал устарели. Необходимо согласование у персонального менеджера.");
                labelPricesNotActual.setStyle("-fx-text-fill: red; -fx-background-color: #bbb6b6;");
                labelPricesNotActual.setMaxWidth(Double.MAX_VALUE);
                labelPricesNotActual.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelPricesNotActual, Priority.ALWAYS);
                GridPane.setVgrow(labelPricesNotActual, Priority.ALWAYS);
                labelPricesNotActual.setId("labelResultPriceRUR");
                gridPaneTop.add(labelPricesNotActual, 0, rowIndex, 9, 1);
            }
        }


    }

    @Override
    public void updateReceiptFull(){

        System.out.println("UPDATE RECEIPT TABLE  FULL");
        allPriceForRUR = 0.0;
        allPriceForUSD = 0.0;
        allPriceForEUR = 0.0;

        allAddPriceForRUR = 0.0;
        allAddPriceForUSD = 0.0;
        allAddPriceForEUR = 0.0;

        Receipt.calculateItemsStocks();

        createTopPartGridPane();

        createMaterialsPartGridPane();


        createImagesPartGridPaneTD();

        createHeaderForAdditionalWorks();

        createSinkQuarzPartGridPaneTD();
        createEdgesAndBordersPartGridPaneTD();
        createLeakGroovePartGridPaneTD();
        createStoneHemPartGridPaneTD();

        createSinkAcrylPartGridPaneTD();
        createSinkInstallTypesPartGridPaneTD();
        createJointsPartGridPaneTD();
        createRadiusElementsPartGridPaneTD();

        // receiptManager.createAdditionalPartTopTD();
        createCutoutPartGridPaneTD();
        createPlumbingAlveusPartGridPaneTD();
        createPlumbingPartGridPaneTD();
        createPalletPartGridPaneTD();
        createGroovesPartGridPaneTD();
        createRodsPartGridPaneTD();
        //receiptManager.createCustomReceiptItemsPartGridPaneTD();

        createMetalFootingPartGridPaneTD();
        createPlywoodPartGridPaneTD();
        createStonePolishingPartGridPaneTD();
        createSiphonPartGridPaneTD();
        createCustomPartGridPaneTD();
//        createMeasuringPartGridPaneTD();
//        createDeliveryPartGridPaneTD();

        createResultPart();


        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
//        stackPaneIntoScrollPane.setPrefHeight(gridPaneHeight);

    }

    @Override
    public void updateReceiptShort(){

        System.out.println("UPDATE RECEIPT TABLE SHORT");
        allPriceForRUR = 0.0;
        allPriceForUSD = 0.0;
        allPriceForEUR = 0.0;

        allAddPriceForRUR = 0.0;
        allAddPriceForUSD = 0.0;
        allAddPriceForEUR = 0.0;

        Receipt.calculateItemsStocks();

        createTopPartGridPane();

        createMaterialsPartGridPaneShort();

        //createSinkQuarzPartGridPaneTD();
        createImagesPartGridPaneTD();


        createAdditionalRowShort();

//        createMeasuringPartGridPaneTD();
//        createDeliveryPartGridPaneTD();

        createResultPart();


        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
//        stackPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
    }



    public TextField getTextFieldUSD() {
        return textFieldUSD;
    }

    public TextField getTextFieldEUR() {
        return textFieldEUR;
    }

}
