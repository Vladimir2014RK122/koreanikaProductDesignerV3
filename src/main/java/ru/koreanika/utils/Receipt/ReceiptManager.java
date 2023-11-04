package ru.koreanika.utils.Receipt;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.Common.Material.Material;
import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.PortalClient.UserEventHandler.UserEventService;
import ru.koreanika.Preferences.UserPreferences;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutObject;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.sketchDesigner.Edge.Border;
import ru.koreanika.sketchDesigner.Edge.Edge;
import ru.koreanika.sketchDesigner.Edge.SketchEdge;
import ru.koreanika.sketchDesigner.Features.Cutout;
import ru.koreanika.sketchDesigner.Features.Grooves;
import ru.koreanika.sketchDesigner.Features.Rods;
import ru.koreanika.sketchDesigner.Features.Sink;
import ru.koreanika.sketchDesigner.Shapes.SketchShape;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.Currency.UserCurrency;
import ru.koreanika.utils.*;
import ru.koreanika.utils.PrinterHandler.PdfSaver;
import ru.koreanika.utils.PrinterHandler.PrinterDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReceiptManager {

    public static final String USD_SYMBOL = "\u0024"; // $
    public static final String EUR_SYMBOL = "\u20AC"; // €
    public static final String RUR_SYMBOL = "\u20BD"; // ₽

    protected Scene sceneReceiptManager;

    //CONTROL ELEMENTS:
    protected AnchorPane rootAnchorPane;

    //menu zone:
    protected AnchorPane anchorPaneMenu;
    protected TextField textFieldUSD, textFieldEUR;
    protected Button btnPrint, btnPrintQuickly;
    protected Button btnCurrencyUpdate, btnCurrencyApply;
    protected ToggleButton toggleButtonFullReceipt, toggleButtonShortReceipt;
    protected Button btnReceiptLog;
    protected AnchorPane anchorPaneReceiptRoot;

    //list Zone:

    //receipt zone:
    protected AnchorPane anchorPaneResultMenu;
    protected ScrollPane scrollPaneResultMenu;
    protected AnchorPane anchorPaneIntoScrollPane;
    protected GridPane gridPaneTop;
    protected AnchorPane anchorPaneReceiptHeader;
    protected Label labelGeneralName;
    protected Label labelDate;
    protected Label labelCutoutInfo;
    protected TextField textFieldDocName = new TextField();
    protected Label labelManagerName;
    protected TextField textFieldCustomerName = new TextField();
    protected TextField textFieldCustomerAddress = new TextField();
    protected TextField textFieldManagerName = new TextField();
    protected Label labelUSD;
    protected Label labelEUR;
    protected TextField textFieldCoefficient = new TextField();
    protected Label labelAdditionalFeatureName;
    protected Label labelAdditionalFeatureInches;
    protected Label labelAdditionalFeaturePrice;
    protected Label labelAdditionalFeatureCount;
    protected Label labelAdditionalFeatureResultPrice;
    protected ImageView imageViewSketch = null;
    protected List<ReceiptItem> customReceiptItems = new ArrayList<>();
    protected int topPartChildrenCount = 0;

    //common:
    protected String docName = "";

    //properties zone:
    protected String customerName = "";
    protected String customerAddress = "";
    protected String managerName = "";
    protected double coefficient = 1;
    protected double allPriceForRUR = 0.0;
    protected double allPriceForUSD = 0.0;
    protected double allPriceForEUR = 0.0;
    protected double allAddPriceForRUR = 0.0;
    protected double allAddPriceForUSD = 0.0;
    protected double allAddPriceForEUR = 0.0;
    protected double allStoneProductsPriceInRUR = 0.0;
    protected double allStoneProductsPriceInEUR = 0.0;
    protected double allStoneProductsPriceInUSD = 0.0;
    protected int stoneItems = 0;
    ToggleGroup toggleGroupReceiptSize;
    ScrollBar scrollBarVertical;
    ScrollBar scrollBarHorizontal;
    Set<String> materialsForEvent = new LinkedHashSet<>();
    JSONObject jsonObjectLastCalcEvent;

    protected ReceiptManager() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/Receipt/ReceiptManager.fxml"));
        try {
            rootAnchorPane = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        FXMLLoader fxmlLoader1 = new FXMLLoader();
        fxmlLoader1.setLocation(getClass().getResource("/fxmls/Receipt/ReceiptKoreanikaHeader.fxml"));
        try {
            anchorPaneReceiptHeader = fxmlLoader1.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        rootAnchorPane.getStylesheets().clear();
        sceneReceiptManager = new Scene(rootAnchorPane, rootAnchorPane.getPrefWidth(), rootAnchorPane.getPrefHeight());

        initControlElements();
        initLogicForControls();
    }

    public static String formatPrice(double price) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        return formatter.format(price);
    }

    protected void initControlElements() {
        //menu zone:
        anchorPaneMenu = (AnchorPane) rootAnchorPane.lookup("#anchorPaneMenu");
        textFieldUSD = (TextField) anchorPaneMenu.lookup("#textFieldUSD");
        textFieldEUR = (TextField) anchorPaneMenu.lookup("#textFieldEUR");

        btnPrint = (Button) anchorPaneMenu.lookup("#btnPrint");
        btnPrintQuickly = (Button) anchorPaneMenu.lookup("#btnPrintQuickly");
        btnCurrencyUpdate = (Button) anchorPaneMenu.lookup("#btnCurrencyUpdate");
        btnCurrencyApply = (Button) anchorPaneMenu.lookup("#btnCurrencyApply");
        toggleButtonFullReceipt = (ToggleButton) anchorPaneMenu.lookup("#toggleButtonFullReceipt");
        toggleButtonShortReceipt = (ToggleButton) anchorPaneMenu.lookup("#toggleButtonShortReceipt");
        btnReceiptLog = (Button) anchorPaneMenu.lookup("#btnReceiptLog");

        toggleGroupReceiptSize = new ToggleGroup();
        toggleGroupReceiptSize.getToggles().clear();
        toggleGroupReceiptSize.getToggles().addAll(toggleButtonFullReceipt, toggleButtonShortReceipt);
        toggleButtonShortReceipt.setSelected(true);

        anchorPaneReceiptRoot = (AnchorPane) rootAnchorPane.lookup("#anchorPaneReceiptRoot");

        //receipt zone:
        anchorPaneResultMenu = (AnchorPane) anchorPaneReceiptRoot.lookup("#anchorPaneResultMenu");
        scrollPaneResultMenu = (ScrollPane) anchorPaneReceiptRoot.lookup("#scrollPaneResultMenu");

        scrollBarVertical = (ScrollBar) anchorPaneResultMenu.lookup(".scroll-bar:vertical");
        scrollBarHorizontal = (ScrollBar) anchorPaneResultMenu.lookup(".scroll-bar:horizontal");
        anchorPaneIntoScrollPane = (AnchorPane) scrollPaneResultMenu.getContent();

        gridPaneTop = (GridPane) anchorPaneIntoScrollPane.lookup("#gridPaneTop");

        textFieldCustomerName = (TextField) anchorPaneReceiptHeader.lookup("#textFieldCostumerName");
        textFieldCustomerAddress = (TextField) anchorPaneReceiptHeader.lookup("#textFieldCostumerAddress");
        textFieldManagerName = (TextField) anchorPaneReceiptHeader.lookup("#textFieldManagerName");
        textFieldDocName = (TextField) anchorPaneReceiptHeader.lookup("#textFieldDocName");

        textFieldDocName.getStyleClass().remove("textFieldDocName");

        labelUSD = (Label) anchorPaneReceiptHeader.lookup("#labelUSD");
        labelEUR = (Label) anchorPaneReceiptHeader.lookup("#labelEUR");
        labelGeneralName = (Label) anchorPaneReceiptHeader.lookup("#labelGeneralName");
        labelDate = (Label) anchorPaneReceiptHeader.lookup("#labelDate");
        labelCutoutInfo = (Label) anchorPaneReceiptHeader.lookup("#labelCutoutInfo");

        createTopPartGridPane();

        //properties zone:
        textFieldUSD.setText(String.format(Locale.ENGLISH, "%.2f", MainWindow.getUSDValue().doubleValue()));
        textFieldEUR.setText(String.format(Locale.ENGLISH, "%.2f", MainWindow.getEURValue().doubleValue()));

        textFieldUSD.setVisible(false);
        textFieldEUR.setVisible(false);
        btnCurrencyUpdate.setVisible(false);
        btnCurrencyApply.setVisible(false);
    }

    protected void createTopPartGridPane() {
        gridPaneTop.getColumnConstraints().clear();
        gridPaneTop.getRowConstraints().clear();
        gridPaneTop.getChildren().clear();

        for (int i = 0; i < 9; i++) {
            ColumnConstraints column = new ColumnConstraints(100);
            gridPaneTop.getColumnConstraints().add(column);
        }
        gridPaneTop.getColumnConstraints().get(0).setPercentWidth(20);
        gridPaneTop.getColumnConstraints().get(1).setPercentWidth(12);
        gridPaneTop.getColumnConstraints().get(2).setPercentWidth(18);
        gridPaneTop.getColumnConstraints().get(3).setPercentWidth(10);
        gridPaneTop.getColumnConstraints().get(4).setPercentWidth(10);
        gridPaneTop.getColumnConstraints().get(5).setPercentWidth(7);
        gridPaneTop.getColumnConstraints().get(6).setPercentWidth(8);
        gridPaneTop.getColumnConstraints().get(7).setPercentWidth(6);
        gridPaneTop.getColumnConstraints().get(8).setPercentWidth(9);

        for (int i = 0; i < 6; i++) {
            RowConstraints row = new RowConstraints(20);
            gridPaneTop.getRowConstraints().add(row);
        }
        gridPaneTop.getRowConstraints().get(0).setPrefHeight(40);
        gridPaneTop.getRowConstraints().get(1).setPrefHeight(40);
        gridPaneTop.getRowConstraints().get(2).setPrefHeight(40);
        gridPaneTop.getRowConstraints().get(3).setPrefHeight(40);
        gridPaneTop.getRowConstraints().get(4).setPrefHeight(40);
        gridPaneTop.getRowConstraints().get(5).setPrefHeight(40);

        gridPaneTop.add(anchorPaneReceiptHeader, 0, 0, 9, 5);

        labelGeneralName.setText(UserPreferences.getInstance().getCompanyAddress());
        System.out.println(UserPreferences.getInstance().getCompanyAddress());

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        labelDate.setText(formatter.format(date));

        //label Slab Count and coefficients:
        {
            double usesSlabs = Receipt.getUsesSlabs();
            double coeffMaterial = ProjectHandler.getPriceMaterialCoefficient().doubleValue();
            double coeffMain = ProjectHandler.getPriceMainCoefficient().doubleValue();
            double allSquare = Receipt.getAllSquare();
            labelCutoutInfo.setText("S:" + usesSlabs + " K:" +
                    String.format(Locale.ENGLISH, "%.1f", coeffMain) + " P:" +
                    String.format(Locale.ENGLISH, "%.1f", coeffMaterial) +
                    " Sq:" + String.format(Locale.ENGLISH, "%.2f", allSquare));
        }

        labelManagerName = buildLabel("labelManagerName", "Менеджер ФИО/Подпись", null, false);

        textFieldDocName.setText(docName);
        if (docName.isEmpty()) {
            textFieldDocName.setText("Приложение №1 к заказу №");
        }

        textFieldCustomerName.setText(customerName);
        textFieldCustomerAddress.setText(customerAddress);
        textFieldManagerName.setText(managerName);

        labelUSD.setText(USD_SYMBOL + String.format(Locale.ENGLISH, "%.2f", MainWindow.getUSDValue().doubleValue()));
        labelEUR.setText(EUR_SYMBOL + String.format(Locale.ENGLISH, "%.2f", MainWindow.getEURValue().doubleValue()));

        addTableHeaderToGridPaneTop();

        topPartChildrenCount = gridPaneTop.getChildren().size();
    }

    private void addTableHeaderToGridPaneTop() {
        Label labelElementType = buildLabel(null, "Наименование изделия", "labelTableHeader", false);
        Label labelStoneType = buildLabel(null, "Материал", "labelTableHeader", false);
        Label labelColor = buildLabel(null, "Цвет", "labelTableHeader", false);
        Label labelLength = buildLabel(null, "Сторона 1, мм.", "labelTableHeader", true);
        Label labelWidth = buildLabel(null, "Сторона 2, мм.", "labelTableHeader", true);
        Label labelInchType = buildLabel(null, "Ед.", "labelTableHeader", false);
        Label labelCount = buildLabel(null, "Кол-во", "labelTableHeader", false);
        Label labelResultPrice = buildLabel(null, "Стоимость", "labelTableHeader", false);

        gridPaneTop.add(labelElementType, 0, 5, 1, 1);
        gridPaneTop.add(labelStoneType, 1, 5, 1, 1);
        gridPaneTop.add(labelColor, 2, 5, 1, 1);
        gridPaneTop.add(labelLength, 3, 5, 1, 1);
        gridPaneTop.add(labelWidth, 4, 5, 1, 1);
        gridPaneTop.add(labelInchType, 5, 5, 1, 1);
        gridPaneTop.add(labelCount, 6, 5, 1, 1);
        gridPaneTop.add(labelResultPrice, 7, 5, 2, 1);
    }

    protected void createMaterialsPartGridPane() {
        stoneItems = 0;

        allStoneProductsPriceInRUR = 0;
        allStoneProductsPriceInUSD = 0;
        allStoneProductsPriceInEUR = 0;

        /* sorting */
        LinkedHashMap<CutShape, ReceiptItem> receiptItems = new LinkedHashMap<>();

        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Столешница")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Стеновая панель")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Подоконник")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Опора")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Кромка")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Бортик")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
            if (entry.getValue().getName().contains("Раковина")) {
                receiptItems.put(entry.getKey(), entry.getValue());
            }
        }

        if (receiptItems.size() != Receipt.getCutShapesAndReceiptItem().size()) {
            System.out.println("ERROR WITH SORTING STONE PRODUCT!!!");

            for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {
                RowConstraints row = new RowConstraints(20);
                gridPaneTop.getRowConstraints().add(row);
                row.setPrefHeight(40);

                createCutShapeRow(entry.getKey(), entry.getValue());
            }
        } else {
            System.out.println("SUCCESS SORTING STONE PRODUCT!!!");

            for (Map.Entry<CutShape, ReceiptItem> entry : receiptItems.entrySet()) {
                RowConstraints row = new RowConstraints(20);
                gridPaneTop.getRowConstraints().add(row);
                row.setPrefHeight(40);

                createCutShapeRow(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<Sink, ReceiptItem> entry : Receipt.getCuttableSinkAndReceiptItem().entrySet()) {
            RowConstraints row = new RowConstraints(20);
            gridPaneTop.getRowConstraints().add(row);
            row.setPrefHeight(40);

            createCutShapeRow(entry.getKey(), entry.getValue());
        }
        System.out.println("MATERIAL PART ONLY:");
        System.out.println("allStoneProductsPriceInRUR = " + allStoneProductsPriceInRUR);
        System.out.println("allStoneProductsPriceInUSD = " + allStoneProductsPriceInUSD);
        System.out.println("allStoneProductsPriceInEUR = " + allStoneProductsPriceInEUR);

        System.out.println("FOR currency USD:" + MainWindow.getUSDValue().get());
    }

    public void createMaterialsPartGridPaneShort() {
        stoneItems = 0;

        allStoneProductsPriceInRUR = 0;
        allStoneProductsPriceInUSD = 0;
        allStoneProductsPriceInEUR = 0;

        Map<Material, Map<CutShape, ReceiptItem>> cutShapesAndMaterials = new LinkedHashMap<>();
        Map<Material, Map<CutShape, ReceiptItem>> cutShapesForSinkAndMaterials = new LinkedHashMap<>();
        for (Map.Entry<CutShape, ReceiptItem> entry : Receipt.getCutShapesAndReceiptItem().entrySet()) {

            Material material = entry.getKey().getMaterial();

            if (entry.getValue().getName().contains("Раковина")) {
                Map<CutShape, ReceiptItem> map = cutShapesForSinkAndMaterials.get(material);
                if (map == null) {
                    map = new LinkedHashMap<CutShape, ReceiptItem>();
                }
                map.put(entry.getKey(), entry.getValue());
                cutShapesForSinkAndMaterials.put(material, map);
            } else {
                Map<CutShape, ReceiptItem> map = cutShapesAndMaterials.get(material);
                if (map == null) {
                    map = new LinkedHashMap<CutShape, ReceiptItem>();
                }
                map.put(entry.getKey(), entry.getValue());

                cutShapesAndMaterials.put(material, map);
            }
        }

        for (Map.Entry<Material, Map<CutShape, ReceiptItem>> entry : cutShapesAndMaterials.entrySet()) {
            double priceForPartInRUB = 0;

            Material material = entry.getKey();

            String name = "Стоимость изделия с обработкой";
            String materialMainType = material.getMainType();
            String materialSubType = material.getSubType();
            String materialCollectionType = material.getCollection();
            String materialColor = material.getColor();
            String units = "м2";
            String strPrice;
            double square = 0;
            String squareStr = String.format(Locale.ENGLISH, "%.2f", Receipt.getAllSquare());

            for (Map.Entry<CutShape, ReceiptItem> entryInner : entry.getValue().entrySet()) {
                stoneItems++;

                ReceiptItem receiptItem = entryInner.getValue();

                square += receiptItem.getPseudoCountDouble();

                //calculate allPrice:
                {
                    if (receiptItem.getCurrency().equals("USD")) {
                        allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                        allStoneProductsPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                    } else if (receiptItem.getCurrency().equals("EUR")) {
                        allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                        allStoneProductsPriceInEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                    } else if (receiptItem.getCurrency().equals("RUB")) {
                        allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                        allStoneProductsPriceInRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                    }
                }

                priceForPartInRUB += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            }

            squareStr = String.format(Locale.ENGLISH, "%.2f", square);

            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            symbols.setGroupingSeparator(' ');
            DecimalFormat formatter = new DecimalFormat("###,###", symbols);
            strPrice = formatter.format(priceForPartInRUB);

            boolean redColor = true;
            createCutShapeRow(name, materialMainType, materialSubType, materialColor, "-", "-", units, squareStr, strPrice, redColor);
        }

        for (Map.Entry<Material, Map<CutShape, ReceiptItem>> entry : cutShapesForSinkAndMaterials.entrySet()) {
            double priceForPartInRUB = 0;

            Material material = entry.getKey();

            String name = "Стоимость изделия для мойки с изготовлением";
            String materialMainType = material.getMainType();
            String materialSubType = material.getSubType();
            String materialCollectionType = material.getCollection();
            String materialColor = material.getColor();
            String units = "м2";
            String strPrice;
            double square = 0;
            String squareStr = "";

            for (Map.Entry<CutShape, ReceiptItem> entryInner : entry.getValue().entrySet()) {
                stoneItems++;

                ReceiptItem receiptItem = entryInner.getValue();
                square += receiptItem.getPseudoCountDouble();

                //calculate allPrice:
                {
                    if (receiptItem.getCurrency().equals("USD")) {
                        allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                        allStoneProductsPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                    } else if (receiptItem.getCurrency().equals("EUR")) {
                        allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                        allStoneProductsPriceInEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                    } else if (receiptItem.getCurrency().equals("RUB")) {
                        allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                        allStoneProductsPriceInRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                    }
                }

                priceForPartInRUB += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            }

            for (ReceiptItem receiptItemSink : TableDesigner.getSinkQuarzReceiptList()) {
                String nameSink = receiptItemSink.getName().split("#")[0];
                String subNameSink = receiptItemSink.getName().split("#")[1];

                if (subNameSink.equals(materialCollectionType + " " + materialColor)) {
                    priceForPartInRUB += Double.parseDouble(receiptItemSink.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
                }
            }

            squareStr = String.format(Locale.ENGLISH, "%.2f", square);

            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
            symbols.setGroupingSeparator(' ');
            DecimalFormat formatter = new DecimalFormat("###,###", symbols);
            strPrice = formatter.format(priceForPartInRUB);

            boolean redColor = true;
            createCutShapeRow(name, materialMainType, materialSubType, materialColor, "-", "-", units, squareStr, strPrice, redColor);
        }

        for (ReceiptItem receiptItem : TableDesigner.getSinkQuarzReceiptList()) {
            if (receiptItem.getCurrency().equals("USD")) {
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                allStoneProductsPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            } else if (receiptItem.getCurrency().equals("EUR")) {
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                allStoneProductsPriceInEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            } else if (receiptItem.getCurrency().equals("RUB")) {
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                allStoneProductsPriceInRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            }
        }
    }

    protected void createCutShapeRow(String name, String materialMainType, String materialSubType,
                                     String materialColor, String width, String height, String units,
                                     String square, String priceInRUR, boolean redColor) {

        materialsForEvent.add(materialMainType + "-" + materialSubType + "-" + materialColor);

        RowConstraints row = new RowConstraints(20);
        gridPaneTop.getRowConstraints().add(row);
        row.setPrefHeight(40);

        int rowIndex = gridPaneTop.getRowConstraints().size() - 1;

        //label element type type value
        {
            Label labelElementTypeValue = new Label(name);
            labelElementTypeValue.getStyleClass().add("labelStoneProduct");
            labelElementTypeValue.setMaxWidth(Double.MAX_VALUE);
            labelElementTypeValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelElementTypeValue, Priority.ALWAYS);
            GridPane.setVgrow(labelElementTypeValue, Priority.ALWAYS);
            gridPaneTop.add(labelElementTypeValue, 0, rowIndex, 1, 1);
        }
        //label stone type value
        {
            Label labelStoneTypeValue = new Label(materialMainType);
            labelStoneTypeValue.getStyleClass().add("labelStoneProduct");
            labelStoneTypeValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneTypeValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneTypeValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneTypeValue, Priority.ALWAYS);
            gridPaneTop.add(labelStoneTypeValue, 1, rowIndex, 1, 1);
        }

        //label stone color value
        {
            Label labelStoneColorValue = new Label(materialSubType + ", " + materialColor);
            labelStoneColorValue.getStyleClass().add("labelStoneProduct");
            labelStoneColorValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneColorValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneColorValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneColorValue, Priority.ALWAYS);
            gridPaneTop.add(labelStoneColorValue, 2, rowIndex, 1, 1);
        }

        //label stone width value
        {
            Label labelStoneWidthValue = new Label(width);
            labelStoneWidthValue.getStyleClass().add("labelStoneProduct");
            labelStoneWidthValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneWidthValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneWidthValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneWidthValue, Priority.ALWAYS);
            gridPaneTop.add(labelStoneWidthValue, 3, rowIndex, 1, 1);
        }

        //label stone height value
        {
            Label labelStoneHeightValue = new Label(height);
            labelStoneHeightValue.getStyleClass().add("labelStoneProduct");
            labelStoneHeightValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneHeightValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneHeightValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneHeightValue, Priority.ALWAYS);
            gridPaneTop.add(labelStoneHeightValue, 4, rowIndex, 1, 1);
        }

        //label inches type
        {
            Label labelInchesValue = new Label(units);
            labelInchesValue.getStyleClass().add("labelStoneProduct");
            labelInchesValue.setMaxWidth(Double.MAX_VALUE);
            labelInchesValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelInchesValue, Priority.ALWAYS);
            GridPane.setVgrow(labelInchesValue, Priority.ALWAYS);
            gridPaneTop.add(labelInchesValue, 5, rowIndex, 1, 1);
        }

        //label square value
        {
            Label labelSquareValue = new Label(square);
            labelSquareValue.getStyleClass().add("labelStoneProduct");
            labelSquareValue.setMaxWidth(Double.MAX_VALUE);
            labelSquareValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelSquareValue, Priority.ALWAYS);
            GridPane.setVgrow(labelSquareValue, Priority.ALWAYS);
            gridPaneTop.add(labelSquareValue, 6, rowIndex, 1, 1);
        }

        //label result price value
        {
            Label labelResultPriceValue = new Label();
            labelResultPriceValue.setText(RUR_SYMBOL + priceInRUR);
            labelResultPriceValue.getStyleClass().add("labelStoneProductPrice");
            labelResultPriceValue.setMaxWidth(Double.MAX_VALUE);
            labelResultPriceValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelResultPriceValue, Priority.ALWAYS);
            GridPane.setVgrow(labelResultPriceValue, Priority.ALWAYS);
            gridPaneTop.add(labelResultPriceValue, 7, rowIndex, 2, 1);
        }
    }

    protected void createCutShapeRow(Object object, ReceiptItem receiptItem) {
        stoneItems++;

        int rowIndex = gridPaneTop.getRowConstraints().size() - 1;

        Material material;
        double width = 0;
        double height = 0;

        if (object instanceof CutObject) {
            CutObject cutObject = (CutObject) object;
            material = cutObject.getMaterial();
            width = ((SketchShape) ((CutShape) cutObject).getSketchObjectOwner()).getShapeWidth();
            height = ((SketchShape) ((CutShape) cutObject).getSketchObjectOwner()).getShapeHeight();
        } else {
            Sink sink = (Sink) object;
            material = sink.getSketchShapeOwner().getMaterial();
            width = sink.getFeatureWidth();
            height = sink.getFeatureHeight();
        }

        receiptItem.setCoefficient(coefficient);

        //label element type type value
        {
            Label labelElementTypeValue = new Label(receiptItem.getName());
            labelElementTypeValue.getStyleClass().add("labelStoneProduct");

            labelElementTypeValue.setMaxWidth(Double.MAX_VALUE);
            labelElementTypeValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelElementTypeValue, Priority.ALWAYS);
            GridPane.setVgrow(labelElementTypeValue, Priority.ALWAYS);
            gridPaneTop.add(labelElementTypeValue, 0, rowIndex, 1, 1);

            String message = "";
            if (material.getNotification1() == 1 || material.getNotification2() == 1) {
                if (material.getNotification1() == 1) {
                    message += "Выбранный цвет требует обязательного уточнения по наличию.\n\t";
                }
                if (material.getNotification2() == 1) {
                    message += "Количество стыков и их расположение в данной коллекции выполняется по усмотрению производителя.";
                }

                Tooltip tooltip = new Tooltip(message);
                labelElementTypeValue.setTooltip(tooltip);
            }
        }
        //label stone type value
        {
            Label labelStoneTypeValue = new Label(material.getMainType());
            labelStoneTypeValue.getStyleClass().add("labelStoneProduct");
            labelStoneTypeValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneTypeValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneTypeValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneTypeValue, Priority.ALWAYS);
            gridPaneTop.add(labelStoneTypeValue, 1, rowIndex, 1, 1);

            String message = "";
            if (material.getNotification1() == 1 || material.getNotification2() == 1) {
                if (material.getNotification1() == 1) {
                    message += "Выбранный цвет требует обязательного уточнения по наличию.\n\t";
                }
                if (material.getNotification2() == 1) {
                    message += "Количество стыков и их расположение в данной коллекции выполняется по усмотрению производителя.";
                }

                Tooltip tooltip = new Tooltip(message);
                labelStoneTypeValue.setTooltip(tooltip);
            }
        }
        //label stone name value
        {
            Label labelStoneNameValue = new Label(material.getSubType());
            labelStoneNameValue.getStyleClass().add("labelStoneProduct");
            labelStoneNameValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneNameValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneNameValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneNameValue, Priority.ALWAYS);
        }

        //label stone collection value
        {
            Label labelStoneCollectionValue = new Label(material.getCollection());
            labelStoneCollectionValue.getStyleClass().add("labelStoneProduct");
            labelStoneCollectionValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneCollectionValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneCollectionValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneCollectionValue, Priority.ALWAYS);
        }
        //label stone color value
        {
            Label labelStoneColorValue = new Label(material.getCollection() + ", " + material.getColor());
            labelStoneColorValue.getStyleClass().add("labelStoneProduct");
            labelStoneColorValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneColorValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneColorValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneColorValue, Priority.ALWAYS);
            gridPaneTop.add(labelStoneColorValue, 2, rowIndex, 1, 1);

            String message = "";
            if (material.getNotification1() == 1 || material.getNotification2() == 1) {
                if (material.getNotification1() == 1) {
                    message += "Выбранный цвет требует обязательного уточнения по наличию.\n\t";
                }
                if (material.getNotification2() == 1) {
                    message += "Количество стыков и их расположение в данной коллекции выполняется по усмотрению производителя.";
                }

                Tooltip tooltip = new Tooltip(message);
                labelStoneColorValue.setTooltip(tooltip);
            }
        }

        //label stone width value
        {
            Label labelStoneWidthValue = new Label(String.format(Locale.ENGLISH, "%.1f", width));
            labelStoneWidthValue.getStyleClass().add("labelStoneProduct");
            labelStoneWidthValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneWidthValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneWidthValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneWidthValue, Priority.ALWAYS);
            gridPaneTop.add(labelStoneWidthValue, 3, rowIndex, 1, 1);
        }

        //label stone height value
        {
            Label labelStoneHeightValue = new Label(String.format(Locale.ENGLISH, "%.1f", height));
            labelStoneHeightValue.getStyleClass().add("labelStoneProduct");
            labelStoneHeightValue.setMaxWidth(Double.MAX_VALUE);
            labelStoneHeightValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelStoneHeightValue, Priority.ALWAYS);
            GridPane.setVgrow(labelStoneHeightValue, Priority.ALWAYS);
            gridPaneTop.add(labelStoneHeightValue, 4, rowIndex, 1, 1);
        }

        //label inches type
        {
            Label labelInchesValue = new Label(receiptItem.getUnits());
            labelInchesValue.getStyleClass().add("labelStoneProduct");
            labelInchesValue.setMaxWidth(Double.MAX_VALUE);
            labelInchesValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelInchesValue, Priority.ALWAYS);
            GridPane.setVgrow(labelInchesValue, Priority.ALWAYS);
            gridPaneTop.add(labelInchesValue, 5, rowIndex, 1, 1);
        }

        //label price value
        {
            Label labelPriceValue = new Label(RUR_SYMBOL + receiptItem.getPriceForOneInRUR());
            labelPriceValue.setStyle("-fx-text-fill: " + receiptItem.getPriceColor() + ";");
            labelPriceValue.getStyleClass().add("labelStoneProduct");
            labelPriceValue.setMaxWidth(Double.MAX_VALUE);
            labelPriceValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelPriceValue, Priority.ALWAYS);
            GridPane.setVgrow(labelPriceValue, Priority.ALWAYS);
        }

        //label square value
        {
            String count = "";
            if (!(receiptItem.getPseudoCount().equals("-1.00"))) {
                count = receiptItem.getPseudoCount();
            } else {
                count = receiptItem.getCount();
            }
            Label labelSquareValue = new Label(count);
            labelSquareValue.getStyleClass().add("labelStoneProduct");
            labelSquareValue.setMaxWidth(Double.MAX_VALUE);
            labelSquareValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelSquareValue, Priority.ALWAYS);
            GridPane.setVgrow(labelSquareValue, Priority.ALWAYS);
            gridPaneTop.add(labelSquareValue, 6, rowIndex, 1, 1);
        }

        //label result price value
        {
            Label labelResultPriceValue = new Label();
            labelResultPriceValue.setText(RUR_SYMBOL + receiptItem.getAllPriceInRUR());
            labelResultPriceValue.setStyle("-fx-text-fill: " + receiptItem.getPriceColor() + ";");
            labelResultPriceValue.getStyleClass().add("labelStoneProductPrice");
            labelResultPriceValue.setMaxWidth(Double.MAX_VALUE);
            labelResultPriceValue.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelResultPriceValue, Priority.ALWAYS);
            GridPane.setVgrow(labelResultPriceValue, Priority.ALWAYS);
            gridPaneTop.add(labelResultPriceValue, 7, rowIndex, 2, 1);
        }

        //calculate allPrice:
        {
            if (receiptItem.getCurrency().equals("USD")) {
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                allStoneProductsPriceInUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            } else if (receiptItem.getCurrency().equals("EUR")) {
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                allStoneProductsPriceInEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            } else if (receiptItem.getCurrency().equals("RUB")) {
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                allStoneProductsPriceInRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            }
        }

    }

    protected void createImagesPartGridPane() {
        int rowIndex;

        if (Receipt.getReceiptImageItemsList().size() != 0) {

            System.out.println("***************** Receipt.getReceiptImageItemsList().size() = " + Receipt.getReceiptImageItemsList().size());
            FlowPane flowPaneForFeaturesPictures = new FlowPane();
            flowPaneForFeaturesPictures.setId("flowPaneForFeaturesPictures");
            flowPaneForFeaturesPictures.setMinWidth(600);
            flowPaneForFeaturesPictures.setMaxWidth(600);
            flowPaneForFeaturesPictures.setPrefWidth(600);

            for (ReceiptImageItem receiptImageItem : Receipt.getReceiptImageItemsList()) {
                flowPaneForFeaturesPictures.getChildren().add(receiptImageItem);
            }

            double flowPaneHeight = (Receipt.getReceiptImageItemsList().size() / 6) * 140;
            if (Receipt.getReceiptImageItemsList().size() % 6 != 0) {
                flowPaneHeight += 140;
            }
            System.out.println(Receipt.getReceiptImageItemsList().size());
            System.out.println(flowPaneHeight);

            RowConstraints rowForFlowPane = new RowConstraints(flowPaneHeight);
            gridPaneTop.getRowConstraints().add(rowForFlowPane);
            flowPaneForFeaturesPictures.setPrefHeight(flowPaneHeight);
            flowPaneForFeaturesPictures.setMaxHeight(flowPaneHeight);
            flowPaneForFeaturesPictures.setMinHeight(flowPaneHeight);

            GridPane.setHgrow(flowPaneForFeaturesPictures, Priority.ALWAYS);
            GridPane.setVgrow(flowPaneForFeaturesPictures, Priority.ALWAYS);
            rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            gridPaneTop.add(flowPaneForFeaturesPictures, 0, rowIndex, 5, 1);

            gridPaneTop.setHalignment(flowPaneForFeaturesPictures, HPos.LEFT);
        }

        RowConstraints row1 = new RowConstraints(40);
        gridPaneTop.getRowConstraints().add(row1);

        rowIndex = gridPaneTop.getRowConstraints().size() - 1;

        //label labelAdditionalFeatureName
        {
            labelAdditionalFeatureName = new Label("Фиксированые дополнительные работы и опции");
            labelAdditionalFeatureName.getStyleClass().add("labelTableHeader");
            labelAdditionalFeatureName.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureName.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureName, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureName, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureName, 0, rowIndex, 5, 1);
        }

        //label labelAdditionalFeatureInches
        {
            labelAdditionalFeatureInches = new Label("Ед.");
            labelAdditionalFeatureInches.getStyleClass().add("labelTableHeader");
            labelAdditionalFeatureInches.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureInches.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureInches, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureInches, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureInches, 5, rowIndex, 1, 1);
        }

        //label labelAdditionalFeaturePrice
        {
            labelAdditionalFeaturePrice = new Label("цена");
            labelAdditionalFeaturePrice.getStyleClass().add("labelTableHeader");
            labelAdditionalFeaturePrice.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeaturePrice.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeaturePrice, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeaturePrice, Priority.ALWAYS);
        }
        //label labelAdditionalFeaturePrice
        {
            labelAdditionalFeatureCount = new Label("кол-во");
            labelAdditionalFeatureCount.getStyleClass().add("labelTableHeader");
            labelAdditionalFeatureCount.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureCount.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureCount, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureCount, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureCount, 6, rowIndex, 1, 1);
        }
        //label labelAdditionalFeaturePrice
        {
            labelAdditionalFeatureResultPrice = new Label("Стоимость");
            labelAdditionalFeatureResultPrice.getStyleClass().add("labelTableHeader");
            labelAdditionalFeatureResultPrice.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureResultPrice.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureResultPrice, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureResultPrice, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureResultPrice, 7, rowIndex, 2, 1);
        }
    }

    protected void createAdditionalPartTop() {
        int rowIndex = addRowToGridPaneTop();

        //label labelAdditionalFeatureName
        {
            Label labelAdditionalFeatureName = new Label("Дополнительные работы и опции в зависимости от камня");
            labelAdditionalFeatureName.getStyleClass().add("labelTableHeader");
            labelAdditionalFeatureName.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureName.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureName, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureName, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureName, 0, rowIndex, 5, 1);
        }

        //label labelAdditionalFeatureInches
        {
            Label labelAdditionalFeatureInches = new Label("Ед.");
            labelAdditionalFeatureInches.getStyleClass().add("labelTableHeader");
            labelAdditionalFeatureInches.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureInches.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureInches, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureInches, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureInches, 5, rowIndex, 1, 1);
        }

        //label labelAdditionalFeaturePrice
        {
            Label labelAdditionalFeaturePrice = new Label("цена");
            labelAdditionalFeaturePrice.getStyleClass().add("labelTableHeader");
            labelAdditionalFeaturePrice.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeaturePrice.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeaturePrice, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeaturePrice, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeaturePrice, 6, rowIndex, 1, 1);
        }
        //label labelAdditionalFeaturePrice
        {
            Label labelAdditionalFeatureCount = new Label("кол-во");
            labelAdditionalFeatureCount.getStyleClass().add("labelTableHeader");
            labelAdditionalFeatureCount.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureCount.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureCount, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureCount, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureCount, 7, rowIndex, 1, 1);
        }
        //label labelAdditionalFeaturePrice
        {
            Label labelAdditionalFeatureResultPrice = new Label("Стоимость");
            labelAdditionalFeatureResultPrice.getStyleClass().add("labelTableHeader");
            labelAdditionalFeatureResultPrice.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureResultPrice.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureResultPrice, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureResultPrice, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureResultPrice, 8, rowIndex, 1, 1);
        }
    }

    /**
     * FOR SKETCH ProjectType START KOREANIKA
     */
    protected void createEdgesAndBordersPartGridPane() {
        //create rows for edges:
        for (Map.Entry<SketchEdge, ReceiptItem> entry : Receipt.getEdgesAndBordersReceiptItemMap().entrySet()) {
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());

            String labelEdgeValueNameText = null;
            String labelEdgeValueSubNameText = null;
            if (entry.getKey() instanceof Edge) {
                SketchEdge sketchEdge = entry.getKey();
                labelEdgeValueNameText = entry.getValue().getName() + ", Вариант №" + sketchEdge.getEdgeNumber();
                labelEdgeValueSubNameText = sketchEdge.getSketchEdgeOwner().getMaterial().getReceiptName();
            } else if (entry.getKey() instanceof Border) {
                SketchEdge sketchBorder = entry.getKey();
                labelEdgeValueNameText = entry.getValue().getName() + ", Высота - " + ((SketchShape) sketchBorder.getSketchEdgeOwner()).getBorderHeight() + "мм";
                labelEdgeValueSubNameText = sketchBorder.getSketchEdgeOwner().getMaterial().getReceiptName();
            }

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", labelEdgeValueNameText, null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", labelEdgeValueSubNameText, null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + entry.getValue().getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", entry.getValue().getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + entry.getValue().getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD")) {
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            } else if (receiptItem.getCurrency().equals("EUR")) {
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            }
            if (receiptItem.getCurrency().equals("RUB")) {
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            }
        }

        for (Map.Entry<Border, ReceiptItem> entry : Receipt.getBordersTopCutReceiptItemMap().entrySet()) {
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", entry.getValue().getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", "Вариант №" + entry.getKey().getBorderCutType(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + entry.getValue().getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", entry.getValue().getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + entry.getValue().getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            {
                if (receiptItem.getCurrency().equals("USD"))
                    allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                else if (receiptItem.getCurrency().equals("EUR"))
                    allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
                if (receiptItem.getCurrency().equals("RUB"))
                    allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            }
        }

        for (Map.Entry<Border, ReceiptItem> entry : Receipt.getBordersSideCutReceiptItemMap().entrySet()) {
            Border border = entry.getKey();
            if (border.getBorderAnglesCutType() == Border.BORDER_ANGLE_CUT_NONE) {
                continue;
            }

            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", entry.getValue().getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", "Вариант №" + entry.getKey().getBorderSideCutType(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + entry.getValue().getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", entry.getValue().getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + entry.getValue().getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);
            
            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createSinkPartGridPane() {
        if (Receipt.getSinkAndReceiptItem().isEmpty()) {
            return;
        }

        for (Map.Entry<Sink, ReceiptItem> entry : Receipt.getSinkAndReceiptItem().entrySet()) {
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName() + " " + entry.getKey().getModel(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", entry.getKey().getSketchShapeOwner().getMaterial().getReceiptName(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createSinkInstallTypesPartGridPane() {
        if (Receipt.getSinkAndReceiptItem().isEmpty()) {
            return;
        }

        for (Map.Entry<Sink, ReceiptItem> entry : Receipt.getSinkInstallTypesAndReceiptItem().entrySet()) {
            Sink sink = entry.getKey();
            Material material = sink.getSketchShapeOwner().getMaterial();
            
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }

        for (Map.Entry<Sink, ReceiptItem> entry : Receipt.getSinkEdgeTypesAndReceiptItem().entrySet()) {
            Sink sink = entry.getKey();

            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", sink.getSketchShapeOwner().getMaterial().getReceiptName(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    private int addRowToGridPaneTop() {
        RowConstraints rowForEdge = new RowConstraints(40);
        gridPaneTop.getRowConstraints().add(rowForEdge);
        int rowIndex = gridPaneTop.getRowConstraints().size() - 1;
        return rowIndex;
    }

    protected void createJointsPartGridPane() {
        for (ReceiptItem receiptItem : Receipt.getJointReceiptItemsList()) {
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(receiptItem);
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", null, null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createCutoutPartGridPane() {
        for (Map.Entry<Cutout, ReceiptItem> entry : Receipt.getCutoutAndReceiptItem().entrySet()) {
            Cutout cutout = entry.getKey();
            Material material = cutout.getSketchShapeOwner().getMaterial();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createGroovesPartGridPane() {
        for (Map.Entry<Grooves, ReceiptItem> entry : Receipt.getGroovesAndReceiptItem().entrySet()) {
            Grooves grooves = entry.getKey();
            Material material = grooves.getSketchShapeOwner().getMaterial();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createRodsPartGridPane() {
        for (Map.Entry<Rods, ReceiptItem> entry : Receipt.getRodsAndReceiptItem().entrySet()) {
            Rods rods = entry.getKey();
            Material material = rods.getSketchShapeOwner().getMaterial();

            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    private static String getCurrency(ReceiptItem entry) {
        return switch (entry.getCurrency()) {
            case "USD" -> USD_SYMBOL;
            case "EUR" -> EUR_SYMBOL;
            case "RUB" -> RUR_SYMBOL;
            default -> "*";
        };
    }

    protected void createRadiusElementsPartGridPane() {
        for (Map.Entry<Material, ReceiptItem> entry : Receipt.getRadiusElementReceiptItemMap().entrySet()) {
            Material material = entry.getKey();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createLeakGroovePartGridPane() {
        for (Map.Entry<Material, ReceiptItem> entry : Receipt.getLeakGrooveReceiptItemMap().entrySet()) {
            Material material = entry.getKey();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createStoneHemPartGridPane() {
        for (Map.Entry<Material, ReceiptItem> entry : Receipt.getStoneHemReceiptItemMap().entrySet()) {
            Material material = entry.getKey();
            ReceiptItem receiptItem = entry.getValue();
            receiptItem.setCoefficient(coefficient);

            String currency = getCurrency(entry.getValue());
            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel("labelEdgeValueName", receiptItem.getName(), null);
            Label labelEdgeValueSubName = buildLabel("labelEdgeValueSubName", material.getReceiptName(), null);
            Label labelEdgeNull2 = buildLabel("labelEdgeNull2", null, null);
            Label labelEdgeInches = buildLabel("labelEdgeInches", "м.п.", null);
            Label labelEdgePrice = buildLabel("labelEdgePrice", currency + receiptItem.getPriceForOne(), null);
            Label labelEdgeCount = buildLabel("labelEdgeCount", receiptItem.getCount(), null);
            Label labelEdgeResultPrice = buildLabel("labelEdgeResultPrice", currency + receiptItem.getAllPrice(), null);

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgePrice, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 7, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 8, rowIndex, 1, 1);

            //calculate allPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));

            //calculate allAddPrice:
            if (receiptItem.getCurrency().equals("USD"))
                allAddPriceForUSD += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            else if (receiptItem.getCurrency().equals("EUR"))
                allAddPriceForEUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
            if (receiptItem.getCurrency().equals("RUB"))
                allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPrice().replaceAll(" ", "").replace(',', '.'));
        }
    }

    /**
     * FOR TABLE ProjectType START KOREANIKA
     */
    protected void createImagesPartGridPaneTD() {
        double heightFieldForSketch = 40;
        int rowIndex;

        RowConstraints rowForFlowPaneAndSketchImage = new RowConstraints(heightFieldForSketch);
        gridPaneTop.getRowConstraints().add(rowForFlowPaneAndSketchImage);

        rowIndex = gridPaneTop.getRowConstraints().size() - 1;

        //Field for Sketch
        {
            BorderPane sketchPane = new BorderPane();
            sketchPane.setId("sketchPane");

            Button btnAttach = new Button("Attach");
            Button btnDelete = new Button("Delete");
            AnchorPane anchorPaneButtons = new AnchorPane();

            if (imageViewSketch != null) {
                heightFieldForSketch = 280;
            } else if (ProjectHandler.getReceiptManagerSketchImage() != null) {
                heightFieldForSketch = 280;
                imageViewSketch = new ImageView(ProjectHandler.getReceiptManagerSketchImage());
            }

            if (imageViewSketch != null) {
                imageViewSketch.setFitWidth(390);
                imageViewSketch.setFitHeight(240);
                imageViewSketch.setPreserveRatio(true);
            }

            sketchPane.setMinWidth(400);
            sketchPane.setMaxWidth(400);
            sketchPane.setMinHeight(heightFieldForSketch);

            anchorPaneButtons.setId("anchorPaneButtonsSketchImage");
            anchorPaneButtons.setPrefHeight(40);
            anchorPaneButtons.setMaxWidth(400);
            anchorPaneButtons.getChildren().add(btnAttach);
            anchorPaneButtons.getChildren().add(btnDelete);

            AnchorPane.setBottomAnchor(btnDelete, 10.0);
            AnchorPane.setLeftAnchor(btnDelete, 10.0);
            AnchorPane.setBottomAnchor(btnAttach, 10.0);
            AnchorPane.setRightAnchor(btnAttach, 10.0);

            btnAttach.setId("btnAttachSketchImage");
            btnDelete.setId("btnDeleteSketchImage");

            if (imageViewSketch != null) {
                btnDelete.setVisible(true);
            } else {
                btnDelete.setVisible(false);
            }

            sketchPane.setCenter(imageViewSketch);
            sketchPane.setBottom(anchorPaneButtons);

            rowForFlowPaneAndSketchImage.setPrefHeight(heightFieldForSketch);
            gridPaneTop.add(sketchPane, 4, rowIndex, 5, 1);

            gridPaneTop.setHalignment(sketchPane, HPos.LEFT);

            btnAttach.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Some Files");

                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Эскизы", "*.png", "*.jpeg", "*.jpg"));

                if (ProjectHandler.getCurProjectPath() != null) {
                    String[] pathArr = ProjectHandler.getCurProjectPath().split("\\\\");
                    String path1 = "";
                    for (int i = 0; i < pathArr.length - 1; i++) {
                        path1 += "/" + pathArr[i];
                    }
                    System.out.println(ProjectHandler.getCurProjectPath());
                    System.out.println(path1);

                    fileChooser.setInitialDirectory(new File(path1));
                }

                File file = fileChooser.showOpenDialog(sceneReceiptManager.getWindow());

                if (file == null) return;
                try {
                    imageViewSketch = new ImageView(new Image(new FileInputStream(file)));

                    imageViewSketch.setFitWidth(240);
                    imageViewSketch.setFitHeight(240);
                    imageViewSketch.setPreserveRatio(true);

                    sketchPane.setCenter(imageViewSketch);
                    System.out.println("imageViewSketch = " + imageViewSketch);

                    btnDelete.setVisible(true);
                    updateReceiptTable();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });

            btnDelete.setOnAction(event -> {
                imageViewSketch = null;
                ProjectHandler.setReceiptManagerSketchImage(null);
                btnDelete.setVisible(false);
                updateReceiptTable();
            });
        }

        if (TableDesigner.getReceiptImages().size() != 0) {

            FlowPane flowPaneForFeaturesPictures = new FlowPane();
            flowPaneForFeaturesPictures.setId("flowPaneForFeaturesPictures");

            int countInRow = 6;
            if (heightFieldForSketch == 40) {
                flowPaneForFeaturesPictures.setMinWidth(960);
                flowPaneForFeaturesPictures.setMaxWidth(960);
                countInRow = 9;
            } else {
                flowPaneForFeaturesPictures.setMinWidth(600);
                flowPaneForFeaturesPictures.setMaxWidth(600);
                countInRow = 6;
            }

            for (ReceiptImageItem receiptImageItem : TableDesigner.getReceiptImages()) {
                flowPaneForFeaturesPictures.getChildren().add(receiptImageItem);
            }

            double flowPaneHeight = (TableDesigner.getReceiptImages().size() / countInRow) * 140;
            if (TableDesigner.getReceiptImages().size() % countInRow != 0) {
                flowPaneHeight += 140;
            }
            System.out.println("TableDesigner.getReceiptImages().size() = " + TableDesigner.getReceiptImages().size());
            System.out.println("flowPaneHeight = " + flowPaneHeight);

            if (flowPaneHeight < heightFieldForSketch) {
                flowPaneHeight = heightFieldForSketch;
            }

            flowPaneForFeaturesPictures.setPrefHeight(flowPaneHeight);

            flowPaneForFeaturesPictures.setMaxHeight(flowPaneHeight);
            flowPaneForFeaturesPictures.setMinHeight(flowPaneHeight);

            rowForFlowPaneAndSketchImage.setPrefHeight(flowPaneHeight);

            GridPane.setHgrow(flowPaneForFeaturesPictures, Priority.ALWAYS);
            GridPane.setVgrow(flowPaneForFeaturesPictures, Priority.ALWAYS);

            gridPaneTop.add(flowPaneForFeaturesPictures, 0, rowIndex, 4, 1);
            gridPaneTop.setHalignment(flowPaneForFeaturesPictures, HPos.LEFT);
        }

        RowConstraints row = new RowConstraints(40);
        gridPaneTop.getRowConstraints().add(row);

        rowIndex = gridPaneTop.getRowConstraints().size() - 1;

        /** MAIN work price*/
        {
            double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
            double RUBtoEUR = MainWindow.getEURValue().doubleValue();

            {
                Label labelAdditionalAllPriceName = new Label("  Итого стоимость обязательных работ");
                labelAdditionalAllPriceName.setAlignment(Pos.CENTER_LEFT);
                labelAdditionalAllPriceName.getStyleClass().add("labelTableResult");
                labelAdditionalAllPriceName.setMaxWidth(Double.MAX_VALUE);
                labelAdditionalAllPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAdditionalAllPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelAdditionalAllPriceName, Priority.ALWAYS);
                gridPaneTop.add(labelAdditionalAllPriceName, 0, rowIndex, 7, 1);
            }

            {
                double price = allStoneProductsPriceInRUR + (allStoneProductsPriceInUSD * RUBtoUSD) + ((allStoneProductsPriceInEUR * RUBtoEUR));//in RUR
                Label labelAdditionalAllPrice = new Label();
                labelAdditionalAllPrice.setText(RUR_SYMBOL + formatPrice(price));
                labelAdditionalAllPrice.getStyleClass().add("labelTableResultPrice");
                labelAdditionalAllPrice.setMaxWidth(Double.MAX_VALUE);
                labelAdditionalAllPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAdditionalAllPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelAdditionalAllPrice, Priority.ALWAYS);
                gridPaneTop.add(labelAdditionalAllPrice, 7, rowIndex, 2, 1);
            }
        }
    }

    protected void createHeaderForAdditionalWorks() {
        int rowIndex = addRowToGridPaneTop();

        //label labelAdditionalFeatureName
        {
            labelAdditionalFeatureName = new Label("Фиксированые дополнительные работы и опции");
            labelAdditionalFeatureName.getStyleClass().add("labelTableHeader-2");
            labelAdditionalFeatureName.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureName.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureName, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureName, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureName, 0, rowIndex, 5, 1);
        }

        //label labelAdditionalFeatureInches
        {
            labelAdditionalFeatureInches = new Label("Ед.");
            labelAdditionalFeatureInches.getStyleClass().add("labelTableHeader-2");
            labelAdditionalFeatureInches.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureInches.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureInches, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureInches, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureInches, 5, rowIndex, 1, 1);
        }

        //label labelAdditionalFeaturePrice
        {
            labelAdditionalFeaturePrice = new Label("цена");
            labelAdditionalFeaturePrice.getStyleClass().add("labelTableHeader-2");
            labelAdditionalFeaturePrice.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeaturePrice.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeaturePrice, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeaturePrice, Priority.ALWAYS);
        }
        //label labelAdditionalFeaturePrice
        {
            labelAdditionalFeatureCount = new Label("кол-во");
            labelAdditionalFeatureCount.getStyleClass().add("labelTableHeader-2");
            labelAdditionalFeatureCount.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureCount.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureCount, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureCount, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureCount, 6, rowIndex, 1, 1);
        }
        //label labelAdditionalFeaturePrice
        {
            labelAdditionalFeatureResultPrice = new Label("Стоимость");
            labelAdditionalFeatureResultPrice.getStyleClass().add("labelTableHeader-2");
            labelAdditionalFeatureResultPrice.setMaxWidth(Double.MAX_VALUE);
            labelAdditionalFeatureResultPrice.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(labelAdditionalFeatureResultPrice, Priority.ALWAYS);
            GridPane.setVgrow(labelAdditionalFeatureResultPrice, Priority.ALWAYS);
            gridPaneTop.add(labelAdditionalFeatureResultPrice, 7, rowIndex, 2, 1);
        }
    }

    protected void createEdgesAndBordersPartGridPaneTD() {
        int rowIndex;

        for (ReceiptItem receiptItem : TableDesigner.getEdgesReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            RowConstraints rowForEdge = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(rowForEdge);
            rowIndex = gridPaneTop.getRowConstraints().size() - 1;

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }

        for (ReceiptItem receiptItem : TableDesigner.getBordersReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            RowConstraints rowForEdge = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(rowForEdge);
            rowIndex = gridPaneTop.getRowConstraints().size() - 1;

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createSinkAcrylPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getSinkAcrylReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createSinkQuartzPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getSinkQuarzReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createSinkInstallTypesPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getSinkInstallReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createJointsPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getJointsReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createCutoutPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getCutoutsReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createPlumbingAlveusPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getPlumbingAlveusReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createPlumbingPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getPlumbingReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createPalletPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getPalletReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createGroovesPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getGroovesReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createRodsPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getRodsReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, "Нержавеющая сталь", "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "шт.", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createRadiusElementsPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getRadiusReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createLeakGroovePartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getLeakGroovesReceiptItems()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, "м.п.", "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createStoneHemPartGridPaneTD() {
        //not uses, logic inside createLeakGroovePartGridPaneTD()
    }

    protected void createMetalFootingPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getMetalFootingReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createPlywoodPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getPlywoodReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName().split("#")[0], "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, receiptItem.getName().split("#")[1], "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createStonePolishingPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getStonePolishingReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createSiphonPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getSiphonReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    private static Label buildLabel(String id, String text, String className) {
        return buildLabel(id, text, className, true);
    }
    
    private static Label buildLabel(String id, String text, String className, boolean wrapText) {
        Label label = new Label(text);
        if (id != null && !id.isEmpty()) {
            label.setId(id);
        }
        if (className != null && !className.isEmpty()) {
            label.getStyleClass().add(className);
        }
        label.setWrapText(wrapText);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setVgrow(label, Priority.ALWAYS);
        return label;
    }

    protected void createCustomPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getCustomReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createMeasuringPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getMeasurerReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createDeliveryPartGridPaneTD() {
        for (ReceiptItem receiptItem : TableDesigner.getDeliveryReceiptList()) {
            receiptItem.setCoefficient(coefficient);

            int rowIndex = addRowToGridPaneTop();

            Label labelEdgeValueName = buildLabel(null, receiptItem.getName(), "labelProduct");
            Label labelEdgeValueSubName = buildLabel(null, null, "labelProduct");
            Label labelEdgeNull2 = buildLabel(null, null, "labelProduct");
            Label labelEdgeInches = buildLabel(null, receiptItem.getUnits(), "labelProduct");
            Label labelEdgeCount = buildLabel(null, receiptItem.getCount(), "labelProduct");
            Label labelEdgeResultPrice = buildLabel(null, RUR_SYMBOL + receiptItem.getAllPriceInRUR(), "labelProductPrice");

            gridPaneTop.add(labelEdgeValueName, 0, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeValueSubName, 2, rowIndex, 2, 1);
            gridPaneTop.add(labelEdgeNull2, 4, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeInches, 5, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeCount, 6, rowIndex, 1, 1);
            gridPaneTop.add(labelEdgeResultPrice, 7, rowIndex, 2, 1);

            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }
    }

    protected void createMountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double addPriceUSD = 0;

        for (ReceiptItem receiptItem : TableDesigner.getMountingReceiptList()) {
            int rowIndex = addRowToGridPaneTop();
            //label labelMountName
            {
                Label labelMountName = new Label(receiptItem.getName());
                labelMountName.setAlignment(Pos.CENTER_LEFT);
                labelMountName.getStyleClass().add("labelProduct");
                labelMountName.getStyleClass().add("labelProduct-right");
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
                labelMountPercent.getStyleClass().add("labelProduct");
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
                labelMountPrice.setText(RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelMountPrice.getStyleClass().add("labelProductPrice");
                labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                //calculate allPrice:
                addPriceUSD += price;
            }
        }

        allPriceForUSD += addPriceUSD;
    }

    protected void createDiscountPartGridPaneTD() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        double addPriceUSD = 0;

        for (ReceiptItem receiptItem : TableDesigner.getDiscountReceiptList()) {
            int rowIndex = addRowToGridPaneTop();
            //label labelMountName
            {
                Label labelMountName = new Label(receiptItem.getName());
                labelMountName.setAlignment(Pos.CENTER_LEFT);
                labelMountName.getStyleClass().add("labelProduct");
                labelMountName.getStyleClass().add("labelProduct-right");
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
                labelMountPercent.getStyleClass().add("labelProduct");
                labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
            }

            //label labelMountPrice
            {
                double price = -1 * (((allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD)) * ((Double.parseDouble(receiptItem.getPriceForOne())) / 100.0));

                Label labelMountPrice = new Label();
                labelMountPrice.setText(RUR_SYMBOL + formatPrice(price * RUBtoUSD));
                labelMountPrice.getStyleClass().add("labelProductPrice");
                labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                //calculate allPrice:
                addPriceUSD += price;
            }
        }

        allPriceForUSD += addPriceUSD;
    }

    protected void createAdditionalRowShort() {
        List<ReceiptItem> allAdditionalItems = new ArrayList<>();

        allAdditionalItems.addAll(TableDesigner.getEdgesReceiptList());
        allAdditionalItems.addAll(TableDesigner.getBordersReceiptList());
        allAdditionalItems.addAll(TableDesigner.getLeakGroovesReceiptItems());
        allAdditionalItems.addAll(TableDesigner.getSinkAcrylReceiptList());
        allAdditionalItems.addAll(TableDesigner.getSinkInstallReceiptList());
        allAdditionalItems.addAll(TableDesigner.getJointsReceiptList());
        allAdditionalItems.addAll(TableDesigner.getRadiusReceiptList());
        allAdditionalItems.addAll(TableDesigner.getCutoutsReceiptList());
        allAdditionalItems.addAll(TableDesigner.getPlumbingAlveusReceiptList());
        allAdditionalItems.addAll(TableDesigner.getPlumbingReceiptList());
        allAdditionalItems.addAll(TableDesigner.getPalletReceiptList());
        allAdditionalItems.addAll(TableDesigner.getGroovesReceiptList());
        allAdditionalItems.addAll(TableDesigner.getRodsReceiptList());
        allAdditionalItems.addAll(TableDesigner.getMetalFootingReceiptList());
        allAdditionalItems.addAll(TableDesigner.getPlywoodReceiptList());
        allAdditionalItems.addAll(TableDesigner.getStonePolishingReceiptList());
        allAdditionalItems.addAll(TableDesigner.getSiphonReceiptList());
        allAdditionalItems.addAll(TableDesigner.getCustomReceiptList());

        for (ReceiptItem receiptItem : allAdditionalItems) {
            allPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
            allAddPriceForRUR += Double.parseDouble(receiptItem.getAllPriceInRUR().replaceAll(" ", "").replace(',', '.'));
        }

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        int rowIndex = addRowToGridPaneTop();

        /** Additional work price*/
        {
            //label labelAdditionalFeatureName
            {
                Label labelAdditionalAllPriceName = new Label("Стоимость дополнительных работ");
                labelAdditionalAllPriceName.setAlignment(Pos.CENTER_LEFT);
                labelAdditionalAllPriceName.getStyleClass().add("labelTableResult");
                labelAdditionalAllPriceName.getStyleClass().add("labelProduct-right");
                labelAdditionalAllPriceName.setMaxWidth(Double.MAX_VALUE);
                labelAdditionalAllPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAdditionalAllPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelAdditionalAllPriceName, Priority.ALWAYS);
                gridPaneTop.add(labelAdditionalAllPriceName, 0, rowIndex, 7, 1);
            }

            //label labelAdditionalAllPrice
            {
                double price = allAddPriceForRUR + (allAddPriceForUSD * RUBtoUSD) + ((allAddPriceForEUR * RUBtoEUR));//in RUR
                Label labelAdditionalAllPrice = new Label();
                labelAdditionalAllPrice.setText(RUR_SYMBOL + formatPrice(price));
                labelAdditionalAllPrice.getStyleClass().add("labelTableResultPrice");
                labelAdditionalAllPrice.setMaxWidth(Double.MAX_VALUE);
                labelAdditionalAllPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAdditionalAllPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelAdditionalAllPrice, Priority.ALWAYS);
                gridPaneTop.add(labelAdditionalAllPrice, 7, rowIndex, 2, 1);
            }
        }
    }

    /**
     * FOR TABLE ProjectType END  START "KOREANIKA"
     */

    protected void createResultPart() {
        double forEventMaterialCoeff = ProjectHandler.getPriceMaterialCoefficient().doubleValue();
        double forEventMainCoeff = ProjectHandler.getPriceMainCoefficient().doubleValue();
        double forEventStonePriceRUR = 0;
        double forEventAddPriceRUR = 0;
        double forEventResultPriceRUR = 0;

        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        int rowIndex = addRowToGridPaneTop();

        /** Additional work price*/
        {
            //label labelAdditionalFeatureName
            {
                Label labelAdditionalAllPriceName = new Label("  Итого стоимость дополнительных работ");
                labelAdditionalAllPriceName.setAlignment(Pos.CENTER_LEFT);
                labelAdditionalAllPriceName.getStyleClass().add("labelTableResult");
                labelAdditionalAllPriceName.setMaxWidth(Double.MAX_VALUE);
                labelAdditionalAllPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAdditionalAllPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelAdditionalAllPriceName, Priority.ALWAYS);
                gridPaneTop.add(labelAdditionalAllPriceName, 0, rowIndex, 7, 1);
            }

            //label labelAdditionalAllPrice
            {
                double price = allAddPriceForRUR + (allAddPriceForUSD * RUBtoUSD) + ((allAddPriceForEUR * RUBtoEUR));//in RUR
                Label labelAdditionalAllPrice = new Label();
                labelAdditionalAllPrice.setText(RUR_SYMBOL + formatPrice(price));
                labelAdditionalAllPrice.getStyleClass().add("labelTableResultPrice");
                labelAdditionalAllPrice.setMaxWidth(Double.MAX_VALUE);
                labelAdditionalAllPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAdditionalAllPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelAdditionalAllPrice, Priority.ALWAYS);
                gridPaneTop.add(labelAdditionalAllPrice, 7, rowIndex, 2, 1);

                forEventAddPriceRUR = price;
            }
        }

        System.out.println("all  price in RUR = " + (allPriceForRUR + allPriceForUSD * RUBtoUSD + allPriceForEUR * RUBtoEUR));
        System.out.println("all add price in RUR = " + (allAddPriceForRUR + allAddPriceForUSD * RUBtoUSD + allAddPriceForEUR * RUBtoEUR));

        System.out.println("all product prices EUR = " + allPriceForEUR);
        System.out.println("all product prices USD = " + allPriceForUSD);
        System.out.println("all product prices RUR = " + allPriceForRUR);

        /** All product price in USD */
        {
            //label labelAdditionalFeatureName
            {
                Label labelAllPriceName = new Label("  Итого сумма по заказу в долларах США");
                labelAllPriceName.setAlignment(Pos.CENTER_LEFT);
                labelAllPriceName.setId("labelAllPriceName");
                labelAllPriceName.setMaxWidth(Double.MAX_VALUE);
                labelAllPriceName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAllPriceName, Priority.ALWAYS);
                GridPane.setVgrow(labelAllPriceName, Priority.ALWAYS);
            }

            //label labelAllPrice
            {
                double price = (allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD);
                Label labelAllPrice = new Label();
                labelAllPrice.setText(USD_SYMBOL + formatPrice(price));
                labelAllPrice.setId("labelAllPrice");
                labelAllPrice.setMaxWidth(Double.MAX_VALUE);
                labelAllPrice.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelAllPrice, Priority.ALWAYS);
                GridPane.setVgrow(labelAllPrice, Priority.ALWAYS);
            }
        }

        /** MOUNT */
        {
            if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
                RowConstraints row2 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row2);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
                //label labelMountName
                {
                    Label labelMountName = new Label(" Монтаж изделий +10% (но не менее 4000 рублей)");
                    labelMountName.setAlignment(Pos.CENTER_LEFT);
                    labelMountName.getStyleClass().add("labelProduct");
                    labelMountName.setMaxWidth(Double.MAX_VALUE);
                    labelMountName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountName, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountName, Priority.ALWAYS);
                    gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
                }

                //label labelMountPercent
                {
                    Label labelMountPercent = new Label();
                    labelMountPercent.setText("10%");
                    labelMountPercent.getStyleClass().add("labelProduct");
                    labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                    labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                    gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
                }

                //label labelMountPrice
                {
                    double price = (allPriceForRUR + (allPriceForUSD * RUBtoUSD) + (allPriceForEUR * RUBtoEUR)) * 0.1;
                    if (price * RUBtoUSD < 4000) price = 4000;//in RUR

                    Label labelMountPrice = new Label();
                    labelMountPrice.setText(USD_SYMBOL + formatPrice(price));
                    labelMountPrice.getStyleClass().add("labelProductPrice");
                    labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                    labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                    gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                    //calculate allPrice:
                    allPriceForUSD += price;
                }
            } else {
                createMountPartGridPaneTD();
                createDiscountPartGridPaneTD();
            }
        }

        /** result price in USD*/
        {
            //label labelResultPriceUSDName
            {
                Label labelResultPriceUSDName = new Label("  Итог по заказу в долларах США:");
                labelResultPriceUSDName.setAlignment(Pos.CENTER_LEFT);
                labelResultPriceUSDName.setId("labelResultPriceUSDName");
                labelResultPriceUSDName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceUSDName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceUSDName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceUSDName, Priority.ALWAYS);
            }

            //label labelResultPriceUSD
            {
                double price = (allPriceForRUR / RUBtoUSD) + (allPriceForUSD) + ((allPriceForEUR * RUBtoEUR) / RUBtoUSD);

                Label labelResultPriceUSD = new Label();
                labelResultPriceUSD.setText(USD_SYMBOL + formatPrice(price));
                labelResultPriceUSD.setId("labelResultPriceUSD");
                labelResultPriceUSD.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceUSD.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceUSD, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceUSD, Priority.ALWAYS);
            }
        }

        /** additional price percent for small product */
        {
            double coeff = Receipt.getAdditionalPriceCoefficientForAcryl();
            if (stoneItems == 0) {
                coeff = 0;
            }

            double price = (allStoneProductsPriceInRUR + allStoneProductsPriceInUSD * RUBtoUSD + allStoneProductsPriceInEUR * RUBtoEUR) * coeff;//in RUR

            if (coeff != 0.0) {
                RowConstraints row2 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row2);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;

                //label labelMountName
                {
                    Label labelMountName = new Label(" Доплата за изделие менее 2 кв.м.");
                    labelMountName.setAlignment(Pos.CENTER_LEFT);
                    labelMountName.getStyleClass().add("labelProduct");
                    labelMountName.setMaxWidth(Double.MAX_VALUE);
                    labelMountName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountName, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountName, Priority.ALWAYS);
                    gridPaneTop.add(labelMountName, 0, rowIndex, 6, 1);
                }
                //label labelMountPercent
                {
                    Label labelMountPercent = new Label();
                    labelMountPercent.setText("");
                    labelMountPercent.getStyleClass().add("labelProduct");
                    labelMountPercent.setMaxWidth(Double.MAX_VALUE);
                    labelMountPercent.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountPercent, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountPercent, Priority.ALWAYS);
                    gridPaneTop.add(labelMountPercent, 6, rowIndex, 1, 1);
                }
                //label labelMountPrice
                {
                    Label labelMountPrice = new Label();
                    labelMountPrice.setText(String.format(Locale.ENGLISH, RUR_SYMBOL + "%.0f", price));
                    labelMountPrice.getStyleClass().add("labelProductPrice");
                    labelMountPrice.setMaxWidth(Double.MAX_VALUE);
                    labelMountPrice.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelMountPrice, Priority.ALWAYS);
                    GridPane.setVgrow(labelMountPrice, Priority.ALWAYS);
                    gridPaneTop.add(labelMountPrice, 7, rowIndex, 2, 1);

                    //calculate allPrice:
                    allPriceForUSD += price / RUBtoUSD;
                }
            }
        }

        /** STOCKS */
        double stockSizeAll = 0;

        {
            System.out.println("Receipt.getMaterialStocks() = " + Receipt.getMaterialStocks());

            for (Map.Entry<String, Double> entry : Receipt.getMaterialStocks().entrySet()) {
                String stockName = "\"" + entry.getKey() + "\"";
                double stock = entry.getValue();

                RowConstraints row4 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row4);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;

                //label labelResultPriceRURName
                {
                    Label labelResultPriceRURName = new Label("Скидка по акции: " + stockName);
                    labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);
                    labelResultPriceRURName.getStyleClass().add("labelProduct");
                    labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                    labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                    GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                    gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
                }
                //label labelResultPriceRUR
                {
                    Label labelResultPriceRUR = new Label();
                    labelResultPriceRUR.setText("- " + RUR_SYMBOL + formatPrice(stock));
                    labelResultPriceRUR.getStyleClass().add("labelProductPrice");
                    labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                    labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                    GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                    gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
                }

                stockSizeAll += stock; //in RUR
                System.out.println("STOCK SIZE ALL = " + stockSizeAll);
            }

            System.out.println("Receipt.getItemStocks() = " + Receipt.getItemStocks());

            for (Map.Entry<String, Double> entry : Receipt.getItemStocks().entrySet()) {
                String stockName = "\"" + entry.getKey() + "\"";
                double stock = entry.getValue();

                RowConstraints row4 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row4);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;

                //label labelResultPriceRURName
                {
                    Label labelResultPriceRURName = new Label("Скидка по акции: " + stockName);
                    labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);
                    labelResultPriceRURName.getStyleClass().add("labelProduct");
                    labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                    labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                    GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                    gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
                }
                //label labelResultPriceRUR
                {
                    Label labelResultPriceRUR = new Label();
                    labelResultPriceRUR.setText("- " + RUR_SYMBOL + formatPrice(stock));
                    labelResultPriceRUR.getStyleClass().add("labelProductPrice");
                    labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                    labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                    GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                    gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
                }

                stockSizeAll += stock; //in RUR
                System.out.println("STOCK SIZE ALL = " + stockSizeAll);
            }
        }

        /** result price in RUR*/
        {
            RowConstraints row4 = new RowConstraints(40);
            gridPaneTop.getRowConstraints().add(row4);
            rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            {
                Label labelResultPriceRURName = new Label("  Стоимость в рублях действительна на день просчета:");
                labelResultPriceRURName.setAlignment(Pos.CENTER_LEFT);
                labelResultPriceRURName.getStyleClass().add("labelTableResult");
                labelResultPriceRURName.getStyleClass().add("labelTableResultEnd");
                labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            }

            //label labelResultPriceRUR
            {
                double price = allPriceForRUR + (allPriceForUSD * RUBtoUSD) + (allPriceForEUR * RUBtoEUR);
                forEventResultPriceRUR = price;

                price -= stockSizeAll;//in RUR

                Label labelResultPriceRUR = new Label();
                labelResultPriceRUR.setText(RUR_SYMBOL + formatPrice(price));
                labelResultPriceRUR.getStyleClass().add("labelTableResultPrice");
                labelResultPriceRUR.getStyleClass().add("labelTableResultEndPrice");
                labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
            }
        }

        /** EMPTY STR */
        {
            RowConstraints row4 = new RowConstraints(1);
            gridPaneTop.getRowConstraints().add(row4);
            rowIndex = gridPaneTop.getRowConstraints().size() - 1;
            //label labelResultPriceRURName
            {
                Label labelResultPriceRURName = new Label("");
                labelResultPriceRURName.setAlignment(Pos.CENTER_RIGHT);
                labelResultPriceRURName.getStyleClass().add("labelLastRowTransparent");
                labelResultPriceRURName.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRURName.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRURName, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRURName, Priority.ALWAYS);
                gridPaneTop.add(labelResultPriceRURName, 0, rowIndex, 7, 1);
            }

            //label labelResultPriceRUR
            {
                Label labelResultPriceRUR = new Label();
                labelResultPriceRUR.setAlignment(Pos.CENTER_RIGHT);
                labelResultPriceRUR.setText("");
                labelResultPriceRUR.getStyleClass().add("labelLastRowTransparent");
                labelResultPriceRUR.setMaxWidth(Double.MAX_VALUE);
                labelResultPriceRUR.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(labelResultPriceRUR, Priority.ALWAYS);
                GridPane.setVgrow(labelResultPriceRUR, Priority.ALWAYS);
                gridPaneTop.add(labelResultPriceRUR, 7, rowIndex, 2, 1);
            }
        }

        System.out.println("result prices EUR = " + allPriceForEUR);
        System.out.println("result product prices USD = " + allPriceForUSD);
        System.out.println("result product prices RUR = " + allPriceForRUR);

        /* if have old prices*/
        {
            if (!Receipt.pricesActual) {
                RowConstraints row5 = new RowConstraints(40);
                gridPaneTop.getRowConstraints().add(row5);
                rowIndex = gridPaneTop.getRowConstraints().size() - 1;
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

        String materials = "";

        for (String n : materialsForEvent) {
            materials += n + ",";
        }

        double price = (allStoneProductsPriceInRUR + allStoneProductsPriceInUSD * RUBtoUSD +
                allStoneProductsPriceInEUR * RUBtoEUR);//in RUR

        forEventStonePriceRUR = price;

        jsonObjectLastCalcEvent = new JSONObject();
        jsonObjectLastCalcEvent.put("type", "show receipt");
        jsonObjectLastCalcEvent.put("materials", materials);
        jsonObjectLastCalcEvent.put("materialPrice", forEventStonePriceRUR);
        jsonObjectLastCalcEvent.put("addPrice", forEventAddPriceRUR);
        jsonObjectLastCalcEvent.put("allPrice", forEventResultPriceRUR);
        jsonObjectLastCalcEvent.put("mainCoeff", forEventMainCoeff);
        jsonObjectLastCalcEvent.put("materialCoeff", forEventMaterialCoeff);
        jsonObjectLastCalcEvent.put("slabs", Receipt.getUsesSlabs());
        jsonObjectLastCalcEvent.put("productSquare", Receipt.getAllSquare());

        UserEventService.getInstance().sendEventRequest(jsonObjectLastCalcEvent);//materials, material prices, add price, all price
    }

    protected void initLogicForControls() {
        textFieldUSD.textProperty().addListener((observable, oldValue, newValue) -> {
            double value;
            try {
                value = Double.parseDouble(newValue);
                if (value == 0) {
                    textFieldUSD.setStyle("-fx-text-fill: red");
                    return;
                }

                MainWindow.getUSDValue().set(value);
                labelUSD.setText(USD_SYMBOL + value);
                textFieldUSD.setStyle("-fx-text-fill: #B3B4B4");
            } catch (NumberFormatException ex) {
                textFieldUSD.setStyle("-fx-text-fill: red;");
            }
        });

        textFieldEUR.textProperty().addListener((observable, oldValue, newValue) -> {
            double value;
            try {
                value = Double.parseDouble(newValue);
                if (value == 0) {
                    textFieldEUR.setStyle("-fx-text-fill: red");
                    return;
                }

                MainWindow.getEURValue().set(value);
                labelEUR.setText(USD_SYMBOL + value);
                textFieldEUR.setStyle("-fx-text-fill: #B3B4B4");
            } catch (NumberFormatException ex) {
                textFieldEUR.setStyle("-fx-text-fill: red;");
            }
        });

        textFieldUSD.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                UserCurrency.getInstance().checkCurrencyLvl(textFieldUSD, "USD");
                updateReceiptTable();
            }
        });

        textFieldEUR.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                UserCurrency.getInstance().checkCurrencyLvl(textFieldEUR, "EUR");
                updateReceiptTable();
            }
        });

        textFieldUSD.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                rootAnchorPane.requestFocus();
            }
        });
        textFieldEUR.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                rootAnchorPane.requestFocus();
            }
        });

        textFieldCoefficient.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                coefficient = Double.parseDouble(newValue);
                changeCoefficient();
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        });

        textFieldDocName.textProperty().addListener((observable, oldValue, newValue) -> docName = newValue);
        textFieldCustomerName.textProperty().addListener((observable, oldValue, newValue) -> customerName = newValue);
        textFieldCustomerAddress.textProperty().addListener((observable, oldValue, newValue) -> customerAddress = newValue);
        textFieldManagerName.textProperty().addListener((observable, oldValue, newValue) -> managerName = newValue);

        btnPrint.setOnMouseClicked(e -> printReceipt());
        btnPrintQuickly.setOnMouseClicked(e -> printToPdfBox());

        btnCurrencyUpdate.setOnMouseClicked(event -> {
            UserCurrency.getInstance().updateCurrencyValue();
            Platform.runLater(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                textFieldUSD.setText(String.format(Locale.ENGLISH, "%.2f", MainWindow.getUSDValue().doubleValue()));
                textFieldEUR.setText(String.format(Locale.ENGLISH, "%.2f", MainWindow.getEURValue().doubleValue()));
            });
        });

        btnCurrencyApply.setOnAction(event -> {
            UserCurrency.getInstance().checkCurrencyLvl(textFieldEUR, "EUR");
            UserCurrency.getInstance().checkCurrencyLvl(textFieldUSD, "USD");
            updateReceiptTable();
        });

        MainWindow.getUSDValue().addListener((observableValue, number, t1) -> {
            Receipt.calculateMaterials();
            Receipt.calculateItemsStocks();
            updateReceiptTable();
        });

        MainWindow.getEURValue().addListener((observableValue, number, t1) -> {
            Receipt.calculateMaterials();
            Receipt.calculateItemsStocks();
            updateReceiptTable();
        });

        ProjectHandler.getPriceMainCoefficient().addListener((observableValue, number, t1) -> {
            Receipt.calculateMaterials();
            updateReceiptTable();
        });

        ProjectHandler.getPriceMaterialCoefficient().addListener((observableValue, number, t1) -> {
            Receipt.calculateMaterials();
            updateReceiptTable();
        });

        btnReceiptLog.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setResizable(true);
            alert.setHeaderText("Лог раскроя: ");

            alert.getDialogPane().setContent(new TextArea(Receipt.getReceiptLog()));

            alert.show();
        });

        toggleGroupReceiptSize.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (t1 == null) {
                toggle.setSelected(true);
                return;
            }
            if (toggle == null) return;
            if (t1.equals(toggleButtonFullReceipt)) {
                updateReceiptFull();
            } else if (t1.equals(toggleButtonShortReceipt)) {
                updateReceiptShort();
            } else {
            }
        });

        scrollPaneResultMenu.viewportBoundsProperty().addListener((observableValue, bounds, t1) -> {
            System.out.println("BOUNDS = " + t1.getWidth());
            double offset = 0;
            if (t1.getWidth() < scrollPaneResultMenu.getWidth()) {
                offset = 2;
            }
            anchorPaneIntoScrollPane.setPrefWidth(t1.getWidth() - offset);
        });

        scrollPaneResultMenu.widthProperty().addListener((observableValue, number, t1) -> {
            double offset = 0;
            if (scrollPaneResultMenu.getWidth() > scrollPaneResultMenu.getViewportBounds().getWidth()) {
                offset = 2;
            }
            System.out.println("getWidth = " + t1.doubleValue());
            anchorPaneIntoScrollPane.setPrefWidth(scrollPaneResultMenu.getViewportBounds().getWidth() - offset);
        });
    }

    protected void printReceipt() {
        anchorPaneIntoScrollPane.setPrefWidth(1000);
        textFieldDocName.getStyleClass().add("textFieldDocName");

        ArrayList<Node> nodeList = new ArrayList<>();
        nodeList.add(gridPaneTop);

        ChangeListener<Bounds> listener = new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds bounds, Bounds t1) {
                if (gridPaneTop.getBoundsInParent().getWidth() <= 1001) {
                    gridPaneTop.boundsInParentProperty().removeListener(this);
                    PrinterDialog.showPrinterDialog(Main.getMainScene().getWindow(), nodeList, false);
                }
            }
        };
        gridPaneTop.boundsInParentProperty().addListener(listener);

        if (jsonObjectLastCalcEvent != null) {
            jsonObjectLastCalcEvent.put("type", "printing");
            UserEventService.getInstance().sendEventRequest(jsonObjectLastCalcEvent);//materials, material prices, add price, all price
        }
    }

    protected void printToPdfBox() {
        anchorPaneIntoScrollPane.setPrefWidth(1000);
        textFieldDocName.getStyleClass().add("textFieldDocName");

        List<Node> nodeList = new ArrayList<>();
        nodeList.add(gridPaneTop);

        ChangeListener<Bounds> listener = new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds bounds, Bounds t1) {
                if (gridPaneTop.getBoundsInParent().getWidth() <= 1001) {
                    gridPaneTop.boundsInParentProperty().removeListener(this);

                    PdfSaver pdfSaver = new PdfSaver();
                    LoadingProgressDialog loadingProgressDialog = new LoadingProgressDialog(sceneReceiptManager);
                    pdfSaver.printToPdfBox(nodeList, loadingProgressDialog);
                }
            }
        };
        gridPaneTop.boundsInParentProperty().addListener(listener);

        if (jsonObjectLastCalcEvent != null) {
            jsonObjectLastCalcEvent.put("type", "printing");
            UserEventService.getInstance().sendEventRequest(jsonObjectLastCalcEvent);//materials, material prices, add price, all price
        }
    }

    public void updateReceiptTable() {
        labelUSD.setText(USD_SYMBOL + String.format("%.2f", MainWindow.getUSDValue().get()));
        labelEUR.setText(EUR_SYMBOL + String.format("%.2f", MainWindow.getEURValue().get()));

        if (toggleButtonFullReceipt.isSelected()) {
            updateReceiptFull();
        } else if (toggleButtonShortReceipt.isSelected()) {
            updateReceiptShort();
        }
    }

    public void updateReceiptWidth() {
        textFieldDocName.getStyleClass().remove("textFieldDocName");

        double offset = 0;
        if (scrollPaneResultMenu.getWidth() > scrollPaneResultMenu.getViewportBounds().getWidth()) {
            offset = 2;
        }

        System.out.println("UPDATE RECEIPT WIDTH getWidth = " + scrollPaneResultMenu.getViewportBounds().getWidth());
        anchorPaneIntoScrollPane.setPrefWidth(scrollPaneResultMenu.getViewportBounds().getWidth() - offset);
    }

    public void updateReceiptFull() {
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

        createSinkQuartzPartGridPaneTD();
        createEdgesAndBordersPartGridPaneTD();
        createLeakGroovePartGridPaneTD();
        createStoneHemPartGridPaneTD();

        createSinkAcrylPartGridPaneTD();
        createSinkInstallTypesPartGridPaneTD();
        createJointsPartGridPaneTD();
        createRadiusElementsPartGridPaneTD();

        createCutoutPartGridPaneTD();
        createPlumbingAlveusPartGridPaneTD();
        createPlumbingPartGridPaneTD();
        createPalletPartGridPaneTD();
        createGroovesPartGridPaneTD();
        createRodsPartGridPaneTD();

        createMetalFootingPartGridPaneTD();
        createPlywoodPartGridPaneTD();
        createStonePolishingPartGridPaneTD();
        createSiphonPartGridPaneTD();
        createCustomPartGridPaneTD();
        createMeasuringPartGridPaneTD();
        createDeliveryPartGridPaneTD();

        createResultPart();

        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
    }

    public void updateReceiptShort() {
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
        createImagesPartGridPaneTD();
        createAdditionalRowShort();
        createMeasuringPartGridPaneTD();
        createDeliveryPartGridPaneTD();
        createResultPart();

        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
    }

    private void changeCoefficient() {
        allPriceForRUR = 0.0;
        allPriceForUSD = 0.0;
        allPriceForEUR = 0.0;

        allAddPriceForRUR = 0.0;
        allAddPriceForUSD = 0.0;
        allAddPriceForEUR = 0.0;

        gridPaneTop.getRowConstraints().remove(7, gridPaneTop.getRowConstraints().size());
        gridPaneTop.getChildren().remove(topPartChildrenCount, gridPaneTop.getChildren().size());

        createMaterialsPartGridPane();
        if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
            createImagesPartGridPane();
            createEdgesAndBordersPartGridPane();
            createLeakGroovePartGridPane();
            createStoneHemPartGridPane();

            createSinkPartGridPane();
            createSinkInstallTypesPartGridPane();
            createJointsPartGridPane();
            createRadiusElementsPartGridPane();

            createAdditionalPartTop();
            createCutoutPartGridPane();
            createGroovesPartGridPane();
            createRodsPartGridPane();
        } else {
            //AdditionalPart
            //MainWork
            //AdditionalWork
            createSinkQuartzPartGridPaneTD();
            createImagesPartGridPaneTD();
            createEdgesAndBordersPartGridPaneTD();
            createLeakGroovePartGridPaneTD();
            createStoneHemPartGridPaneTD();

            createSinkAcrylPartGridPaneTD();
            createSinkInstallTypesPartGridPaneTD();
            createJointsPartGridPaneTD();
            createRadiusElementsPartGridPaneTD();

            createCutoutPartGridPaneTD();
            createPlumbingAlveusPartGridPaneTD();
            createPlumbingPartGridPaneTD();
            createPalletPartGridPaneTD();
            createGroovesPartGridPaneTD();
            createRodsPartGridPaneTD();
        }

        createResultPart();

        double gridPaneHeight = 0;
        for (RowConstraints rowConstraints : gridPaneTop.getRowConstraints()) {
            gridPaneHeight += rowConstraints.getPrefHeight();
        }
        anchorPaneIntoScrollPane.setPrefHeight(gridPaneHeight);
    }

    public AnchorPane getView() {
        btnReceiptLog.setVisible(UserPreferences.getInstance().getSelectedApp() == AppType.KOREANIKAMASTER);

        Receipt.calculateMaterials();
        Receipt.calculateItemsStocks();

        updateReceiptTable();
        showNotificationAboutCurrency();

        for (CutShape cutShape : CutDesigner.getInstance().getCutShapesList()) {
            if (!cutShape.checkCorrectPlaceOrNot()) {
                System.out.println("NOT CORRECT SHAPE: " + cutShape.getShapeNumber());

                InfoMessage.showMessage(InfoMessage.MessageType.ERROR, "Не все фигуры размещены на раскрое", null);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Добавление листа");
                            alert.setHeaderText("Проверьте количество материала");
                            alert.setContentText("Не все изделия добавлены на раскрой, проверьте количество материала.");
                            alert.show();
                            System.out.println("ALERT");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            }
        }

        return (AnchorPane) getSceneReceiptManager().getRoot();
    }

    public TextField getTextFieldUSD() {
        return textFieldUSD;
    }

    public TextField getTextFieldEUR() {
        return textFieldEUR;
    }

    public Scene getSceneReceiptManager() {
        return sceneReceiptManager;
    }

    public ImageView getImageViewSketch() {
        return imageViewSketch;
    }

    public JSONObject getJsonViewForSaveData() {
        double RUBtoUSD = MainWindow.getUSDValue().doubleValue();
        double RUBtoEUR = MainWindow.getEURValue().doubleValue();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("RUBtoUSD", RUBtoUSD);
        jsonObject.put("RUBtoEUR", RUBtoEUR);
        jsonObject.put("coefficient", coefficient);
        jsonObject.put("costumerAddress", customerAddress);
        jsonObject.put("costumerName", customerName);
        jsonObject.put("managerName", managerName);
        jsonObject.put("documentName", docName);

        //custom receiptItemsMap
        JSONArray jsonCustomItemsArray = new JSONArray();
        for (ReceiptItem receiptItem : customReceiptItems) {
            JSONObject jsonCustomItemObject = new JSONObject();
            jsonCustomItemObject.put("name", receiptItem.getName());
            jsonCustomItemObject.put("units", receiptItem.getUnits());
            jsonCustomItemObject.put("count", Double.parseDouble(receiptItem.getCount()));
            jsonCustomItemObject.put("currency", receiptItem.getCurrency());
            jsonCustomItemObject.put("priceForOne", Double.parseDouble(receiptItem.getPriceForOne().replaceAll(" ", "").replace(',', '.')));
            jsonCustomItemsArray.add(jsonCustomItemObject);

        }
        jsonObject.put("jsonCustomItemsArray", jsonCustomItemsArray);
        return jsonObject;
    }

    public void initFromJsonObject(JSONObject jsonObject) {
        double RUBtoUSD = (Double) jsonObject.get("RUBtoUSD");
        double RUBtoEUR = (Double) jsonObject.get("RUBtoEUR");

        MainWindow.getUSDValue().set(RUBtoUSD);
        MainWindow.getEURValue().set(RUBtoEUR);

        coefficient = (Double) jsonObject.get("coefficient");
        customerAddress = (String) jsonObject.get("costumerAddress");
        customerName = (String) jsonObject.get("costumerName");
        managerName = (String) jsonObject.get("managerName");
        docName = (String) jsonObject.get("documentName");

        JSONArray jsonArray = (JSONArray) jsonObject.get("jsonCustomItemsArray");

        for (Object object : jsonArray) {
            JSONObject jsonObjectCustomItem = (JSONObject) object;

            String name = (String) jsonObjectCustomItem.get("name");
            String units = (String) jsonObjectCustomItem.get("units");
            double count = (Double) jsonObjectCustomItem.get("count");
            String currency = (String) jsonObjectCustomItem.get("currency");
            double priceForOne = (Double) jsonObjectCustomItem.get("priceForOne");

            ReceiptItem receiptItem = new ReceiptItem(name, units, count, currency, priceForOne);
            customReceiptItems.add(receiptItem);
        }
    }

    private void showNotificationAboutCurrency() {
        Label labelNotification = new Label("Обновите курсы валют!!!");
        labelNotification.setPrefHeight(20);
        labelNotification.setId("labelNotification");

        labelNotification.setStyle("-fx-text-fill:red;-fx-font-size:15;");

        anchorPaneMenu.getChildren().add(labelNotification);

        AnchorPane.setRightAnchor(labelNotification, 40.0);
        AnchorPane.setTopAnchor(labelNotification, 2.0);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), labelNotification);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.20);
        fadeTransition.setCycleCount(11);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setOnFinished(event -> anchorPaneMenu.getChildren().remove(labelNotification));
        fadeTransition.play();
    }

}
