package ru.koreanika.utils;

import ru.koreanika.PortalClient.Maintenance.MaintenanceMessageWindow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ru.koreanika.project.Project;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.ApplicationTypeChangeEvent;
import ru.koreanika.service.event.ApplicationTypeChangeEventHandler;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.utils.Currency.UserCurrency;
import ru.koreanika.utils.MainSettings.MainSettings;
import ru.koreanika.utils.MaterialSelectionWindow.MaterialSelectionEventHandler;
import ru.koreanika.utils.MaterialSelectionWindow.MaterialSelectionWindow;
import ru.koreanika.utils.News.NewsController;
import ru.koreanika.utils.Updater.UpdateChecker;
import ru.koreanika.utils.Updater.UpdateManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

public class MainWindowDecorator implements ApplicationTypeChangeEventHandler {
    private final EventBus eventBus;
    private final ProjectHandler projectHandler;

    private MainWindow mainWindow;

    AnchorPane anchorPaneWindowDecorator;
    AnchorPane anchorPaneHeader;

    private Button btnWindowSubtract, btnWindowMaxMin, btnWindowClose;

    private Label labelUserName, labelProjectName;

    private Button btnCreateProject, btnOpenProject, btnSaveProject, btnSaveAsProject, btnCloseProject;
    private Button btnSettings, btnUpdater, btnNews, btnCurrencyUpdate,btnCurrencyApply, btnPriceCoefficient;
    private Button btnBack, btnMaintance;
    private TextField textFieldUSD, textFieldEUR;
    private ToggleButton btnSketchView, btnTableView, btn3DView, btnCutView, btnSelectMaterial;
    private HBox hBoxBtns;
    private ToggleButton btnShowReceiptBottom, btnShowCutViewBottom;
    private ToggleGroup toggleGroup;

    private final HashMap<Cursor, EventHandler<MouseEvent>> LISTENER = new HashMap<>();
    private final Stage STAGE;



    private final int TR;
    private final int TM;
    private final double SCREEN_WIDTH, SCREEN_HEIGHT;

    private double mPresSceneX, mPresSceneY;
    private double mPresScreeX, mPresScreeY;
    private double mPresStageW, mPresStageH;

    private boolean mIsMaximized = false;
    private double mWidthStore, mHeightStore, mXStore, mYStore;

    private boolean usdValidate = true, eurValidate = true;

    /**
     * Create an FXResizeHelper for undecoreated JavaFX Stages.
     * The only wich is your job is to create an padding for the Stage so the user can resize it.
     *
     * stage - The JavaFX Stage.
     * dt    - The area (in px) where the user can drag the window.
     * rt    - The area (in px) where the user can resize the window.
     */
    public MainWindowDecorator(Stage stage, MainWindow mainWindow) {
        eventBus = ServiceLocator.getService("EventBus", EventBus.class);
        eventBus.addHandler(ApplicationTypeChangeEvent.TYPE, this);

        projectHandler = ServiceLocator.getService("ProjectHandler", ProjectHandler.class);

        this.mainWindow = mainWindow;

        this.TR = 5;
        this.TM = 5 + 5;
        this.STAGE = stage;
//        this.SCENE = stage.getScene();

        STAGE.setMinWidth(1200);
        STAGE.setMinHeight(700);

        this.SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
        this.SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();


        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxmls/MainWidowDecorator.fxml"));
        try {
            anchorPaneWindowDecorator = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        anchorPaneWindowDecorator.getChildren().add(mainWindow.getRootAnchorPaneMainWindow());
        AnchorPane.setTopAnchor(mainWindow.getRootAnchorPaneMainWindow(), 80.0);
        AnchorPane.setRightAnchor(mainWindow.getRootAnchorPaneMainWindow(), 5.0);
        AnchorPane.setBottomAnchor(mainWindow.getRootAnchorPaneMainWindow(), 5.0);
        AnchorPane.setLeftAnchor(mainWindow.getRootAnchorPaneMainWindow(), 5.0);


        initSystemButtons();
        initControls();
        initControlsLogic();
        refreshControls();

        initDraggableStage();

        createListener();
        launch();


        InfoMessage.initInfoMessage(anchorPaneWindowDecorator);
    }


    public AnchorPane getDecorator(){
        return anchorPaneWindowDecorator;
    }

    /**
     * Minimize the stage.
     */
    public void minimize() {
        STAGE.setIconified(true);
    }

    /**
     * If the stage is maximized, it will be restored to the last postition
     * with heigth and width. Otherwise it will be maximized to fullscreen.
     */
    public void switchWindowedMode() {

        if (mIsMaximized) {
            STAGE.setY(mYStore);
            STAGE.setX(mXStore);
            STAGE.setWidth(mWidthStore);
            STAGE.setHeight(mHeightStore);
        } else {
            mXStore = STAGE.getX();
            mYStore = STAGE.getY();
            mWidthStore = STAGE.getWidth();
            mHeightStore = STAGE.getHeight();

            STAGE.setY(0);
            STAGE.setX(0);
            STAGE.setWidth(SCREEN_WIDTH);
            STAGE.setHeight(SCREEN_HEIGHT);
        }
        mIsMaximized = !mIsMaximized;
    }

    public void initSystemButtons(){
        btnWindowSubtract = (Button) anchorPaneWindowDecorator.lookup("#btnWindowSubtract");
        btnWindowMaxMin = (Button) anchorPaneWindowDecorator.lookup("#btnWindowMaxMin");
        btnWindowClose = (Button) anchorPaneWindowDecorator.lookup("#btnWindowClose");

        btnWindowClose.setOnAction(actionEvent -> {

            {

                InfoMessage.stopMessage();


                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.initOwner(anchorPaneWindowDecorator.getScene().getWindow());
                alert.initModality(Modality.WINDOW_MODAL);

                alert.setTitle("Выйти из приложения?");
                alert.setHeaderText("Вы уверены, что хотите выйти из приложения?");
                alert.setContentText("Сохранить перед выходом?");

                ButtonType buttonTypeNo = new ButtonType("Не сохранять");
                ButtonType buttonTypeYes = new ButtonType("Сохранить");
                ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeNo, buttonTypeYes, buttonTypeCancel);

                if(!projectHandler.projectSelected()){
                    ((Stage)(anchorPaneWindowDecorator.getScene().getWindow())).close();
                    actionEvent.consume();
                    return;
                }
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeNo) {
                    // ... user chose "NO"
                    ((Stage)(anchorPaneWindowDecorator.getScene().getWindow())).close();
                } else if (result.get() == buttonTypeYes) {
                    // ... user chose "YES"
                    projectHandler.saveProject();
                    ((Stage)(anchorPaneWindowDecorator.getScene().getWindow())).close();
                } else if (result.get() == buttonTypeCancel) {
                    // ... user chose "Three"
                    System.out.println("CANCEL");
                }

                actionEvent.consume();

//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("type", "app stopped");
//            UserEventService.getInstance().sendEventRequest(jsonObject);
            }
        });
        btnWindowMaxMin.setOnAction(actionEvent -> {

            if(STAGE.isMaximized()){
                this.setMaximize(false);
                //STAGE.setMaximized(false);
                AnchorPane.setRightAnchor(mainWindow.getRootAnchorPaneMainWindow(), 5.0);
                AnchorPane.setBottomAnchor(mainWindow.getRootAnchorPaneMainWindow(), 5.0);
                AnchorPane.setLeftAnchor(mainWindow.getRootAnchorPaneMainWindow(), 5.0);

                AnchorPane.setRightAnchor(anchorPaneHeader, 5.0);
                AnchorPane.setLeftAnchor(anchorPaneHeader, 5.0);
            }else{
                this.setMaximize(true);
                //STAGE.setMaximized(true);
                AnchorPane.setRightAnchor(mainWindow.getRootAnchorPaneMainWindow(), 0.0);
                AnchorPane.setBottomAnchor(mainWindow.getRootAnchorPaneMainWindow(), 0.0);
                AnchorPane.setLeftAnchor(mainWindow.getRootAnchorPaneMainWindow(), 0.0);

                AnchorPane.setRightAnchor(anchorPaneHeader, 0.0);
                AnchorPane.setLeftAnchor(anchorPaneHeader, 0.0);
            }
        });
        btnWindowSubtract.setOnAction(actionEvent -> {
            STAGE.setIconified(true);


        });
        btnWindowClose.setOnMouseEntered(mouseEvent -> STAGE.getScene().setCursor(Cursor.DEFAULT));
        btnWindowMaxMin.setOnMouseEntered(mouseEvent -> STAGE.getScene().setCursor(Cursor.DEFAULT));
        btnWindowSubtract.setOnMouseEntered(mouseEvent -> STAGE.getScene().setCursor(Cursor.DEFAULT));

        STAGE.iconifiedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1.booleanValue() == false){

                Rectangle2D appBounds = new Rectangle2D(STAGE.getX(), STAGE.getY(), STAGE.getWidth(), STAGE.getHeight());
                ObservableList<Screen> screens = Screen.getScreensForRectangle(appBounds);
                Rectangle2D visualBounds = screens.get(0).getVisualBounds();
                STAGE.setMaxWidth(visualBounds.getWidth());
                STAGE.setMaxHeight(visualBounds.getHeight());

                if(STAGE.isMaximized()){
                    this.setMaximize(true);
                }
            }
        });
    }

    double minimizedWidth, minimizedHeight;
    private void setMaximize(boolean value){
        Rectangle2D appBounds = new Rectangle2D(STAGE.getX(), STAGE.getY(), STAGE.getWidth(), STAGE.getHeight());
        ObservableList<Screen> screens = Screen.getScreensForRectangle(appBounds);
        System.out.println("screens = " + screens);
        Rectangle2D visualBounds = screens.get(0).getVisualBounds();
        System.out.println("visualBounds = " + visualBounds);
        System.out.println("minimizedWidth = " + minimizedWidth);
        System.out.println("minimizedHeight = " + minimizedHeight);
        if(value){
            STAGE.setMaximized(true);
            minimizedWidth = STAGE.getWidth();
            minimizedHeight = STAGE.getHeight();

//            STAGE.setX(visualBounds.getMinX());
//            STAGE.setY(visualBounds.getMinY());
            STAGE.setWidth(visualBounds.getWidth());
            STAGE.setHeight(visualBounds.getHeight());
        }else{
            STAGE.setMaximized(false);
//            STAGE.centerOnScreen();
//            STAGE.setWidth(minimizedWidth);
//            STAGE.setHeight(minimizedHeight);
        }
//        STAGE.getScene().getSc



    }

    private void initControls(){
        anchorPaneHeader = (AnchorPane) anchorPaneWindowDecorator.lookup("#anchorPaneHeader");

        labelUserName = (Label) anchorPaneWindowDecorator.lookup("#labelUserName");
        labelProjectName = (Label) anchorPaneWindowDecorator.lookup("#labelProjectName");

        btnCreateProject = (Button) anchorPaneWindowDecorator.lookup("#btnCreateProject");
        btnOpenProject = (Button) anchorPaneWindowDecorator.lookup("#btnOpenProject");
        btnSaveProject = (Button) anchorPaneWindowDecorator.lookup("#btnSaveProject");
        btnSaveAsProject = (Button) anchorPaneWindowDecorator.lookup("#btnSaveAsProject");
        btnCloseProject = (Button) anchorPaneWindowDecorator.lookup("#btnCloseProject");

        btnUpdater = (Button) anchorPaneWindowDecorator.lookup("#btnUpdater");
        btnNews = (Button) anchorPaneWindowDecorator.lookup("#btnNews");

        btnSelectMaterial = (ToggleButton) anchorPaneWindowDecorator.lookup("#btnSelectMaterial");
        btnCutView = (ToggleButton) anchorPaneWindowDecorator.lookup("#btnCutView");
        btnSketchView = (ToggleButton) anchorPaneWindowDecorator.lookup("#btnSketchView");
        btnTableView = (ToggleButton) anchorPaneWindowDecorator.lookup("#btnTableView");
        btn3DView = (ToggleButton) anchorPaneWindowDecorator.lookup("#btn3DView");
        hBoxBtns = (HBox) anchorPaneWindowDecorator.lookup("#hBoxBtns");
        btnShowReceiptBottom = (ToggleButton) anchorPaneWindowDecorator.lookup("#btnShowReceiptBottom");
        btnShowCutViewBottom = (ToggleButton) anchorPaneWindowDecorator.lookup("#btnShowCutViewBottom");
        //btnAutoCut = (Button) anchorPaneWindowDecorator.lookup("#btnAutoCut");
        //btnShowReceipt = (Button) anchorPaneWindowDecorator.lookup("#btnShowReceipt");

        textFieldEUR = (TextField) anchorPaneWindowDecorator.lookup("#textFieldEUR");
        textFieldUSD = (TextField) anchorPaneWindowDecorator.lookup("#textFieldUSD");
        btnCurrencyUpdate = (Button) anchorPaneWindowDecorator.lookup("#btnCurrencyUpdate");
        btnCurrencyApply = (Button) anchorPaneWindowDecorator.lookup("#btnCurrencyApply");
        btnPriceCoefficient = (Button) anchorPaneWindowDecorator.lookup("#btnPriceCoefficient");
        btnSettings = (Button) anchorPaneWindowDecorator.lookup("#btnSettings");
        btnBack = (Button) anchorPaneWindowDecorator.lookup("#btnBack");
        btnMaintance = (Button) anchorPaneWindowDecorator.lookup("#btnMaintance");


        toggleGroup = new ToggleGroup();
        btnTableView.setToggleGroup(toggleGroup);
        btnSketchView.setToggleGroup(toggleGroup);
        btn3DView.setToggleGroup(toggleGroup);
        btnCutView.setToggleGroup(toggleGroup);
        btnSelectMaterial.setToggleGroup(toggleGroup);
        btnShowReceiptBottom.setToggleGroup(toggleGroup);
        btnShowCutViewBottom.setToggleGroup(toggleGroup);

        btn3DView.setSelected(false);
        btnTableView.setSelected(false);
        btnSketchView.setSelected(false);



    }


    private void initControlsLogic(){

        MaterialSelectionWindow.getInstance().setMaterialSelectionEventHandler(new MaterialSelectionEventHandler() {
            @Override
            public void apply() {
                toggleGroup.selectToggle(btnTableView);
            }

            @Override
            public void cancel() {

            }
        });

        mainWindow.setChangeView((MainWindow.ViewType viewType) -> {
            refreshControls();
            System.out.println("CHANGE VIEW");
        });

        btnCreateProject.setOnMouseClicked(mouseEvent -> {
            mainWindow.createProject();
            refreshControls();
        });

        btnOpenProject.setOnMouseClicked(mouseEvent -> {
            mainWindow.openProject();
            refreshControls();
        });

        btnSaveProject.setOnMouseClicked(mouseEvent -> projectHandler.saveProject());

        btnSaveAsProject.setOnMouseClicked(mouseEvent -> mainWindow.saveAsProject());

        btnBack.setOnAction(actionEvent -> {
            btnTableView.fire();
        });

        toggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null){
                oldValue.setSelected(true);
                return;
            }

            if(newValue == btnTableView){
                mainWindow.showTableView();

                hideBtnCutViewBottom(false);
                hideBtnReceiptViewBottom(false);
            }else if(newValue == btnCutView){
                mainWindow.showCutView();

                hideBtnCutViewBottom(true);
                hideBtnReceiptViewBottom(false);
            }else if(newValue == btnSelectMaterial){
                mainWindow.showMaterialSelection();

                hideBtnCutViewBottom(true);
                hideBtnReceiptViewBottom(true);
            }else if(newValue == btnShowCutViewBottom){
                btnCutView.fire();

            }else if(newValue == btnShowReceiptBottom){
                mainWindow.showReceiptWithCutting();

                hideBtnCutViewBottom(true);
                hideBtnReceiptViewBottom(true);
            }
        });
//        btnCutView.setOnMouseClicked(mouseEvent -> mainWindow.showCutView());
//        btnTableView.setOnMouseClicked(mouseEvent -> mainWindow.showTableView());
//        btnSelectMaterial.setOnMouseClicked(mouseEvent -> mainWindow.showMaterialSelection());

        btnPriceCoefficient.setOnMouseClicked(mouseEvent -> PriceCoefficientsWindow.show(STAGE.getScene()));
        btnSettings.setOnMouseClicked(mouseEvent -> MainSettings.showWindow(STAGE.getScene()));

        btnUpdater.setOnMouseClicked(mouseEvent -> UpdateManager.show(STAGE.getScene()));
        btnNews.setOnMouseClicked(mouseEvent -> mainWindow.toggleNews());
        btnMaintance.setOnMouseClicked(mouseEvent -> MaintenanceMessageWindow.getInstance().show(STAGE.getScene()));


        //News button

        NewsController.newsBtnProperty().addListener((observableValue, number, t1) -> {
            if(t1.intValue() == 0){
                btnNews.setStyle("-fx-background-image: url(/styles/icons/menu/notification_white.png);-fx-background-size: 22px 22px;");
            }else if(t1.intValue() == 1){
                btnNews.setStyle("-fx-background-image: url(/styles/icons/menu/notification_orange.png);-fx-background-size: 22px 27px;");
            }
        });

        //updateBtn:

        UpdateChecker.updateProperty().addListener((observableValue, number, t1) -> {
            if(t1.intValue() == 0){
                btnUpdater.setStyle("-fx-background-image: url(/styles/icons/menu/updateOk.png);");
            }else if(t1.intValue() == 1){
                btnUpdater.setStyle("-fx-background-image: url(/styles/icons/menu/updateOk.png);");
            }else if(t1.intValue() == 2){
                btnUpdater.setStyle("-fx-background-image: url(/styles/icons/menu/updateAvailable.png);");
            }
        });



        // Currency:
        btnCurrencyUpdate.setOnMouseClicked(event -> {
            UserCurrency.getInstance().updateCurrencyValue();
        });

        btnCurrencyApply.setOnAction(actionEvent -> {
            if(usdValidate && eurValidate){
                double usd = Double.parseDouble(textFieldUSD.getText());
                double eur = Double.parseDouble(textFieldEUR.getText());
                MainWindow.getReceiptManager().updateReceiptTable();
            }
        });

        ChangeListener USDValueChangeListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("USD VALUE CHANGED" + newValue);
                textFieldUSD.setText(String.format(Locale.ENGLISH, "%.2f", ((Double) newValue).doubleValue()));

            }
        };

        ChangeListener EURValueChangeListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                textFieldEUR.setText(String.format(Locale.ENGLISH, "%.2f", ((Double) newValue).doubleValue()));

//                MainWindow.getReceiptManager().updateReceiptTable();
            }
        };

        MainWindow.EURValueProperty().addListener(EURValueChangeListener);
        MainWindow.USDValueProperty().addListener(USDValueChangeListener);

        textFieldUSD.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                MainWindow.USDValueProperty().removeListener(USDValueChangeListener);

                value = Double.parseDouble(newValue);

                if (value == 0) {
                    textFieldUSD.setStyle("-fx-text-fill: red");
                    usdValidate = false;
                    return;
                }
                MainWindow.setUSDValue(value);
                textFieldUSD.setStyle("-fx-text-fill: #B3B4B4");
                usdValidate = true;
            } catch (NumberFormatException ex) {
                textFieldUSD.setStyle("-fx-text-fill: red;");
                usdValidate = false;
            }

            MainWindow.USDValueProperty().addListener(USDValueChangeListener);
        });

        textFieldEUR.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                MainWindow.EURValueProperty().removeListener(EURValueChangeListener);
                value = Double.parseDouble(newValue);
                if (value == 0) {
                    textFieldEUR.setStyle("-fx-text-fill: red");
                    eurValidate = false;
                    return;
                }
                MainWindow.setEURValue(value);
                textFieldEUR.setStyle("-fx-text-fill: #B3B4B4");
                eurValidate = true;
            } catch (NumberFormatException ex) {
                textFieldEUR.setStyle("-fx-text-fill: red;");
                eurValidate = false;
            }

            MainWindow.EURValueProperty().addListener(EURValueChangeListener);
        });

        textFieldUSD.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.booleanValue()){
                UserCurrency.getInstance().checkCurrencyLvl(textFieldUSD,"USD");
            }
        });

        textFieldEUR.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.booleanValue()){
                UserCurrency.getInstance().checkCurrencyLvl(textFieldEUR, "EUR");
            }
        });

    }

    @Override
    public void onEvent(ApplicationTypeChangeEvent e) {
        Platform.runLater(()->{
            btnTableView.fire();
            Project.setPriceMainCoefficient(Project.getPriceMainCoefficient().doubleValue());
            Project.setPriceMaterialCoefficient(Project.getPriceMaterialCoefficient().doubleValue());
        });
    }

    private void hideBtnCutViewBottom(boolean val){
        if (val){
            hBoxBtns.getChildren().remove(btnShowCutViewBottom);
        }else{
            if(!hBoxBtns.getChildren().contains(btnShowCutViewBottom)){
                hBoxBtns.getChildren().add(hBoxBtns.getChildren().size(), btnShowCutViewBottom);
            }
        }
    }

    private void hideBtnReceiptViewBottom(boolean val){
        if (val){
            hBoxBtns.getChildren().remove(btnShowReceiptBottom);
        }else{
            if(!hBoxBtns.getChildren().contains(btnShowReceiptBottom)) {
                hBoxBtns.getChildren().add(0, btnShowReceiptBottom);
            }
        }
    }

    public void refreshControls(){

        hBoxBtns.toFront();
//        btnShowCutViewBottom.toFront();
//        btnShowReceiptBottom.toFront();

        if (!projectHandler.projectSelected() || MaterialSelectionWindow.getInstance().isFirstStartFlag()) {
            btnBack.setDisable(true);
            btnSaveProject.setDisable(true);
            btnSaveAsProject.setDisable(true);
            btnTableView.setDisable(true);
            btnSketchView.setDisable(true);
            btn3DView.setDisable(true);

            btnCutView.setDisable(true);
            btnSelectMaterial.setDisable(true);

            btnShowCutViewBottom.setVisible(false);
            btnShowReceiptBottom.setVisible(false);
        }else{
            labelProjectName.setText(projectHandler.getCurrentProjectName());
            btnBack.setDisable(false);
            btnSaveProject.setDisable(false);
            btnSaveAsProject.setDisable(false);
            btnTableView.setDisable(false);
//            btnSketchView.setDisable(false);
//            btn3DView.setDisable(false);

            btnCutView.setDisable(false);
            btnSelectMaterial.setDisable(false);

            btnShowCutViewBottom.setVisible(true);
            btnShowReceiptBottom.setVisible(true);
        }
    }

    public Button getBtnCurrencyUpdate() {
        return btnCurrencyUpdate;
    }

    public Button getBtnPriceCoefficient() {
        return btnPriceCoefficient;
    }

    public TextField getTextFieldUSD() {
        return textFieldUSD;
    }

    public TextField getTextFieldEUR() {
        return textFieldEUR;
    }

    public void setUserName(String userName){
        labelUserName.setText(userName);
    }

    private void createListener() {
        LISTENER.put(Cursor.NW_RESIZE, event -> {


            double newWidth = mPresStageW - (event.getScreenX() - mPresScreeX);
            double newHeight = mPresStageH - (event.getScreenY() - mPresScreeY);
            if (newHeight > STAGE.getMinHeight()) {
                STAGE.setY(event.getScreenY() - mPresSceneY);
                STAGE.setHeight(newHeight);
            }
            if (newWidth > STAGE.getMinWidth()) {
                STAGE.setX(event.getScreenX() - mPresSceneX);
                STAGE.setWidth(newWidth);
            }
        });

        LISTENER.put(Cursor.NE_RESIZE, event -> {

            double newWidth = mPresStageW - (event.getScreenX() - mPresScreeX);
            double newHeight = mPresStageH + (event.getScreenY() - mPresScreeY);
            if (newHeight > STAGE.getMinHeight()) STAGE.setHeight(newHeight);
            if (newWidth > STAGE.getMinWidth()) {
                STAGE.setX(event.getScreenX() - mPresSceneX);
                STAGE.setWidth(newWidth);
            }
        });

        LISTENER.put(Cursor.SW_RESIZE, event -> {

            double newWidth = mPresStageW + (event.getScreenX() - mPresScreeX);
            double newHeight = mPresStageH - (event.getScreenY() - mPresScreeY);
            if (newHeight > STAGE.getMinHeight()) {
                STAGE.setHeight(newHeight);
                STAGE.setY(event.getScreenY() - mPresSceneY);
            }
            if (newWidth > STAGE.getMinWidth()) STAGE.setWidth(newWidth);
        });

        LISTENER.put(Cursor.SE_RESIZE, event -> {
            double newWidth = mPresStageW + (event.getScreenX() - mPresScreeX);
            double newHeight = mPresStageH + (event.getScreenY() - mPresScreeY);
            if (newHeight > STAGE.getMinHeight()) STAGE.setHeight(newHeight);
            if (newWidth > STAGE.getMinWidth()) STAGE.setWidth(newWidth);
        });

        LISTENER.put(Cursor.E_RESIZE, event -> {

            double newWidth = mPresStageW - (event.getScreenX() - mPresScreeX);
            if (newWidth > STAGE.getMinWidth()) {
                STAGE.setX(event.getScreenX() - mPresSceneX);
                STAGE.setWidth(newWidth);
            }
        });

        LISTENER.put(Cursor.W_RESIZE, event -> {
            double newWidth = mPresStageW + (event.getScreenX() - mPresScreeX);
            if (newWidth > STAGE.getMinWidth()) STAGE.setWidth(newWidth);
        });

        LISTENER.put(Cursor.N_RESIZE, event -> {


            double newHeight = mPresStageH - (event.getScreenY() - mPresScreeY);
            if (newHeight > STAGE.getMinHeight()) {
                STAGE.setY(event.getScreenY() - mPresSceneY);
                STAGE.setHeight(newHeight);
            }
        });

        LISTENER.put(Cursor.S_RESIZE, event -> {
            double newHeight = mPresStageH + (event.getScreenY() - mPresScreeY);
            if (newHeight > STAGE.getMinHeight()) STAGE.setHeight(newHeight);
        });

//        LISTENER.put(Cursor.OPEN_HAND, event -> {
//            STAGE.setX(event.getScreenX() - mPresSceneX);
//            STAGE.setY(event.getScreenY() - mPresSceneY);
//        });
    }

    public void launch() {

        anchorPaneWindowDecorator.setOnMousePressed(event -> {
            if(STAGE.isMaximized()) return;

            mPresSceneX = event.getSceneX();
            mPresSceneY = event.getSceneY();

            mPresScreeX = event.getScreenX();
            mPresScreeY = event.getScreenY();

            mPresStageW = STAGE.getWidth();
            mPresStageH = STAGE.getHeight();
        });

        anchorPaneWindowDecorator.setOnMouseMoved(event -> {
            if(STAGE.isMaximized()) return;

            Scene SCENE = STAGE.getScene();

            double sx = event.getSceneX();
            double sy = event.getSceneY();

            boolean l_trigger = sx > 0 && sx < TR;
            boolean r_trigger = sx < SCENE.getWidth() && sx > SCENE.getWidth() - TR;

            boolean u_trigger = sy < SCENE.getHeight() && sy > SCENE.getHeight() - TR;
            boolean d_trigger = sy > 0 && sy < TR/*  && (sx < SCENE.getWidth() - 300 || sx > SCENE.getWidth()- TR)*/;

            if (l_trigger && d_trigger) fireAction(Cursor.NW_RESIZE);
            else if (l_trigger && u_trigger) fireAction(Cursor.NE_RESIZE);
            else if (r_trigger && d_trigger) fireAction(Cursor.SW_RESIZE);
            else if (r_trigger && u_trigger) fireAction(Cursor.SE_RESIZE);
            else if (l_trigger) fireAction(Cursor.E_RESIZE);
            else if (r_trigger) fireAction(Cursor.W_RESIZE);
            else if (d_trigger) fireAction(Cursor.N_RESIZE);
//            else if (sy < TM && !u_trigger) fireAction(Cursor.OPEN_HAND);
            else if (u_trigger) fireAction(Cursor.S_RESIZE);
            else fireAction(Cursor.DEFAULT);

        });

    }

    private void fireAction(Cursor c) {

        Scene SCENE = STAGE.getScene();

        SCENE.setCursor(c);
        if (c != Cursor.DEFAULT) SCENE.setOnMouseDragged(LISTENER.get(c));
        else SCENE.setOnMouseDragged(null);
    }


    private double xOffset = 0;
    private double yOffset = 0;
    private void initDraggableStage(){

        anchorPaneHeader.setOnMousePressed((MouseEvent event) -> {
            if(STAGE.isMaximized()) return;
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        anchorPaneHeader.setOnMouseDragged((MouseEvent event) -> {
            if(STAGE.isMaximized()) return;
            if(STAGE.getScene().getCursor() != Cursor.DEFAULT) return;
            STAGE.getScene().getWindow().setX(event.getScreenX() - xOffset);
            STAGE.getScene().getWindow().setY(event.getScreenY() - yOffset);
        });
    }

    private void initResizableStage(){
        anchorPaneWindowDecorator.setOnMouseMoved(event -> {

        });

        anchorPaneHeader.setOnMousePressed((MouseEvent event) -> {
            if(STAGE.isMaximized()) return;
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        anchorPaneHeader.setOnMouseDragged((MouseEvent event) -> {
            if(STAGE.isMaximized()) return;


//            STAGE.getScene().getWindow().setX(event.getScreenX() - xOffset);
//            STAGE.getScene().getWindow().setY(event.getScreenY() - yOffset);
        });
    }

}
