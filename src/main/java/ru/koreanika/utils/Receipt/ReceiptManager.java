package ru.koreanika.utils.Receipt;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.PortalClient.UserEventHandler.UserEventService;
import ru.koreanika.Preferences.UserPreferences;
import ru.koreanika.cutDesigner.CutDesigner;
import ru.koreanika.cutDesigner.Shapes.CutShape;
import ru.koreanika.utils.Currency.UserCurrency;
import ru.koreanika.utils.*;
import ru.koreanika.utils.PrinterHandler.PdfSaver;
import ru.koreanika.utils.PrinterHandler.PrinterDialog;

import java.io.IOException;
import java.util.*;

public abstract class ReceiptManager {

    protected Scene sceneReceiptManager;

    //CONTROL ELEMENTS:
    protected AnchorPane rootAnchorPane;

    //menu zone:
    protected AnchorPane anchorPaneMenu;
    protected TextField textFieldUSD;
    protected TextField textFieldEUR;
    protected Button btnPrint;
    protected Button btnPrintQuickly;
    protected Button btnCurrencyUpdate, btnCurrencyApply;
    protected ToggleButton toggleButtonFullReceipt;
    protected ToggleButton toggleButtonShortReceipt;
    protected Button btnReceiptLog;
    protected AnchorPane anchorPaneReceiptRoot;

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

        //properties zone:
        textFieldUSD.setText(String.format(Locale.ENGLISH, "%.2f", MainWindow.getUSDValue().doubleValue()));
        textFieldEUR.setText(String.format(Locale.ENGLISH, "%.2f", MainWindow.getEURValue().doubleValue()));

        textFieldUSD.setVisible(false);
        textFieldEUR.setVisible(false);
        btnCurrencyUpdate.setVisible(false);
        btnCurrencyApply.setVisible(false);
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
                labelUSD.setText(Currency.USD_SYMBOL + value);
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
                labelEUR.setText(Currency.USD_SYMBOL + value);
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

        List<Node> nodeList = new ArrayList<>();
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
        labelUSD.setText(Currency.USD_SYMBOL + String.format("%.2f", MainWindow.getUSDValue().get()));
        labelEUR.setText(Currency.EUR_SYMBOL + String.format("%.2f", MainWindow.getEURValue().get()));

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

        TableReceiptNodeBuilder receiptNodeBuilder = new TableReceiptNodeBuilder(this);

        receiptNodeBuilder.createTopPartGridPane();
        receiptNodeBuilder.createMaterialsPartGridPane();
        receiptNodeBuilder.createImagesPartGridPaneTD();
        receiptNodeBuilder.createHeaderForAdditionalWorks();

        receiptNodeBuilder.createSinkQuartzPartGridPaneTD();
        receiptNodeBuilder.createEdgesAndBordersPartGridPaneTD();
        receiptNodeBuilder.createLeakGroovePartGridPaneTD();
        receiptNodeBuilder.createStoneHemPartGridPaneTD();

        receiptNodeBuilder.createSinkAcrylPartGridPaneTD();
        receiptNodeBuilder.createSinkInstallTypesPartGridPaneTD();
        receiptNodeBuilder.createJointsPartGridPaneTD();
        receiptNodeBuilder.createRadiusElementsPartGridPaneTD();

        receiptNodeBuilder.createCutoutPartGridPaneTD();
        receiptNodeBuilder.createPlumbingAlveusPartGridPaneTD();
        receiptNodeBuilder.createPlumbingPartGridPaneTD();
        receiptNodeBuilder.createPalletPartGridPaneTD();
        receiptNodeBuilder.createGroovesPartGridPaneTD();
        receiptNodeBuilder.createRodsPartGridPaneTD();

        receiptNodeBuilder.createMetalFootingPartGridPaneTD();
        receiptNodeBuilder.createPlywoodPartGridPaneTD();
        receiptNodeBuilder.createStonePolishingPartGridPaneTD();
        receiptNodeBuilder.createSiphonPartGridPaneTD();
        receiptNodeBuilder.createCustomPartGridPaneTD();
        receiptNodeBuilder.createMeasuringPartGridPaneTD();
        receiptNodeBuilder.createDeliveryPartGridPaneTD();

        receiptNodeBuilder.createResultPart();

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

        TableReceiptNodeBuilder receiptNodeBuilder = new TableReceiptNodeBuilder(this);

        receiptNodeBuilder.createTopPartGridPane();
        receiptNodeBuilder.createMaterialsPartGridPaneShort();
        receiptNodeBuilder.createImagesPartGridPaneTD();
        receiptNodeBuilder.createAdditionalRowShort();
        receiptNodeBuilder.createMeasuringPartGridPaneTD();
        receiptNodeBuilder.createDeliveryPartGridPaneTD();
        receiptNodeBuilder.createResultPart();

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

        if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
            SketchReceiptNodeBuilder receiptNodeBuilder = new SketchReceiptNodeBuilder(this);

            receiptNodeBuilder.createTopPartGridPane();
            receiptNodeBuilder.createMaterialsPartGridPane();

            receiptNodeBuilder.createImagesPartGridPane();
            receiptNodeBuilder.createEdgesAndBordersPartGridPane();
            receiptNodeBuilder.createLeakGroovePartGridPane();
            receiptNodeBuilder.createStoneHemPartGridPane();

            receiptNodeBuilder.createSinkPartGridPane();
            receiptNodeBuilder.createSinkInstallTypesPartGridPane();
            receiptNodeBuilder.createJointsPartGridPane();
            receiptNodeBuilder.createRadiusElementsPartGridPane();

            receiptNodeBuilder.createAdditionalPartTop();
            receiptNodeBuilder.createCutoutPartGridPane();
            receiptNodeBuilder.createGroovesPartGridPane();
            receiptNodeBuilder.createRodsPartGridPane();

            receiptNodeBuilder.createResultPart();
        } else {
            TableReceiptNodeBuilder receiptNodeBuilder = new TableReceiptNodeBuilder(this);

            receiptNodeBuilder.createTopPartGridPane();
            receiptNodeBuilder.createMaterialsPartGridPane();

            receiptNodeBuilder.createSinkQuartzPartGridPaneTD();
            receiptNodeBuilder.createImagesPartGridPaneTD();
            receiptNodeBuilder.createEdgesAndBordersPartGridPaneTD();
            receiptNodeBuilder.createLeakGroovePartGridPaneTD();
            receiptNodeBuilder.createStoneHemPartGridPaneTD();

            receiptNodeBuilder.createSinkAcrylPartGridPaneTD();
            receiptNodeBuilder.createSinkInstallTypesPartGridPaneTD();
            receiptNodeBuilder.createJointsPartGridPaneTD();
            receiptNodeBuilder.createRadiusElementsPartGridPaneTD();

            receiptNodeBuilder.createCutoutPartGridPaneTD();
            receiptNodeBuilder.createPlumbingAlveusPartGridPaneTD();
            receiptNodeBuilder.createPlumbingPartGridPaneTD();
            receiptNodeBuilder.createPalletPartGridPaneTD();
            receiptNodeBuilder.createGroovesPartGridPaneTD();
            receiptNodeBuilder.createRodsPartGridPaneTD();

            receiptNodeBuilder.createResultPart();
        }

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
