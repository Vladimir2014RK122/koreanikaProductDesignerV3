package utils;

import PortalClient.Authorization.AppType;
import PortalClient.Authorization.Authorization;
import Preferences.UserPreferences;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import utils.Receipt.ReceiptManager;

import java.io.IOException;
import java.util.Locale;

public class PriceCoefficientsWindow {

    public static final double MAIN_COEFFICIENT_MIN_K = 1.0;
    public static final double MAIN_COEFFICIENT_MIN_KM = 0.9;
    public static final double MAIN_COEFFICIENT_MIN_Z = 2.0;
    public static final double MAIN_COEFFICIENT_MIN_PM = 1.0;

    public static final double MAIN_COEFFICIENT_MAX_K = Double.MAX_VALUE;
    public static final double MAIN_COEFFICIENT_MAX_KM = Double.MAX_VALUE;
    public static final double MAIN_COEFFICIENT_MAX_Z = 2.0;
    public static final double MAIN_COEFFICIENT_MAX_PM = Double.MAX_VALUE;

    public static final double MATERIAL_COEFFICIENT_MIN_K = 1.0;
    public static final double MATERIAL_COEFFICIENT_MIN_KM = 0.9;
    public static final double MATERIAL_COEFFICIENT_MIN_Z = 1.0;
    public static final double MATERIAL_COEFFICIENT_MIN_PM = 1.0;

    public static final double MATERIAL_COEFFICIENT_MAX_K = Double.MAX_VALUE;
    public static final double MATERIAL_COEFFICIENT_MAX_KM = Double.MAX_VALUE;
    public static final double MATERIAL_COEFFICIENT_MAX_Z = 1.0;
    public static final double MATERIAL_COEFFICIENT_MAX_PM = Double.MAX_VALUE;

    private static PriceCoefficientsWindow priceCoefficientsWindow = null;

    static Stage priceCoefficientStage;
    Scene priceCoefficientScene = null;

    Button btnWindowClose;
    Button btnWindowMaxMin;
    Button btnWindowSubtract;

    AnchorPane anchorPaneRoot = null;
    Button btnSave = null;
    Button btnCancel = null;
    TextField textFieldMaterialCoefficient;
    TextField textFieldMainCoefficient;

    private double xOffset = 0;
    private double yOffset = 0;

    boolean materialCoefOK = true, mainCoefOK = true;

    private PriceCoefficientsWindow() {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ProjectTypeSelectionWindow.class.getResource("/fxmls/priceCoefficients.fxml"));
        try {
            anchorPaneRoot = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        anchorPaneRoot.getStylesheets().clear();
////        if(Main.appOwner.toUpperCase().equals("KOREANIKA")){
//        if(Main.appType == AppType.KOREANIKA || Main.appType == AppType.KOREANIKAMASTER){
//            anchorPaneRoot.getStylesheets().add(getClass().getResource("/styles/colorsKoreanika.css").toExternalForm());
////        }else if(Main.appOwner.toUpperCase().equals("ZETTA")){
//        }else if(Main.appType == AppType.ZETTA){
//            anchorPaneRoot.getStylesheets().add(getClass().getResource("/styles/colorsZetta.css").toExternalForm());
//        }else if(Main.appType == AppType.PROMEBEL){
//            anchorPaneRoot.getStylesheets().add(getClass().getResource("/styles/colorsPromebel.css").toExternalForm());
//        }

//        anchorPaneRoot.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
//        anchorPaneRoot.getStylesheets().add(getClass().getResource("/styles/priceCoefficients.css").toExternalForm());

        textFieldMaterialCoefficient = (TextField) anchorPaneRoot.lookup("#textFieldMaterialCoefficient");
        textFieldMainCoefficient = (TextField) anchorPaneRoot.lookup("#textFieldMainCoefficient");
        btnSave = (Button) anchorPaneRoot.lookup("#btnSave");
        btnCancel = (Button) anchorPaneRoot.lookup("#btnCancel");

        btnWindowClose = (Button) anchorPaneRoot.lookup("#btnWindowClose");
        btnWindowMaxMin = (Button) anchorPaneRoot.lookup("#btnWindowMaxMin");
        btnWindowSubtract = (Button) anchorPaneRoot.lookup("#btnWindowSubtract");

        btnWindowMaxMin.setVisible(false);
        btnWindowSubtract.setVisible(false);

        priceCoefficientScene = new Scene(anchorPaneRoot);



        refreshView();
        initLogicItems();
    }

    private void initLogicItems() {

        textFieldMainCoefficient.textProperty().addListener((observable, oldValue, newValue) -> {
            double value = 0;
            try {
                value = Double.parseDouble(newValue);

                double minValue = getMinMainCoefficient();
                double maxValue = getMaxMainCoefficient();

                if(value >= minValue && value <= maxValue){
                    textFieldMainCoefficient.setStyle("-fx-text-fill: black");
                    mainCoefOK = true;
                }else{
                    textFieldMainCoefficient.setStyle("-fx-text-fill: red;");
                    mainCoefOK = false;
                }

            } catch (NumberFormatException ex) {
                textFieldMainCoefficient.setStyle("-fx-text-fill: red;");
                mainCoefOK = false;
            }

        });

        textFieldMaterialCoefficient.textProperty().addListener((observable, oldValue, newValue) -> {

            double value = 0;
            try {
                value = Double.parseDouble(newValue);

                double minValue = getMinMaterialCoefficient();
                double maxValue = getMaxMaterialCoefficient();

                if(value >= minValue && value <= maxValue){
                    textFieldMaterialCoefficient.setStyle("-fx-text-fill: black");
                    materialCoefOK = true;
                }else{
                    textFieldMaterialCoefficient.setStyle("-fx-text-fill: red;");
                    materialCoefOK = false;
                }

            } catch (NumberFormatException ex) {
                textFieldMaterialCoefficient.setStyle("-fx-text-fill: red;");
                materialCoefOK = false;
            }

        });

        btnSave.setOnAction(event -> {

            if (mainCoefOK & materialCoefOK) {
                ((Stage) priceCoefficientScene.getWindow()).close();

                double mainD = Double.parseDouble(textFieldMainCoefficient.getText());
                double materialD = Double.parseDouble(textFieldMaterialCoefficient.getText());

                String mainS = String.format(Locale.ENGLISH, "%.2f", mainD);
                String materialS = String.format(Locale.ENGLISH, "%.2f", materialD);

                ProjectHandler.setPriceMainCoefficient(Double.parseDouble(mainS));
                ProjectHandler.setPriceMaterialCoefficient(Double.parseDouble(materialS));

                Main.updateCoefficientProperties(Double.parseDouble(mainS), Double.parseDouble(materialS));

                //                ProjectHandler.getPriceMainCoefficient().set(Double.parseDouble(mainS));
//                ProjectHandler.getPriceMaterialCoefficient().set(Double.parseDouble(materialS));


            }


        });

        btnCancel.setOnAction(event -> {
            ((Stage) priceCoefficientScene.getWindow()).close();
        });


        anchorPaneRoot.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                btnSave.fire();
            }
        });


        anchorPaneRoot.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        anchorPaneRoot.setOnMouseDragged((MouseEvent event) -> {
            priceCoefficientStage.setX(event.getScreenX() - xOffset);
            priceCoefficientStage.setY(event.getScreenY() - yOffset);
        });


        btnWindowClose.setOnAction(actionEvent -> {
            btnCancel.fire();
        });


    }

    private void refreshView(){

        if(UserPreferences.getInstance().getSelectedApp() == AppType.ZETTA){
            textFieldMaterialCoefficient.setDisable(true);
            textFieldMainCoefficient.setDisable(true);
        }else{
            textFieldMaterialCoefficient.setDisable(false);
            textFieldMainCoefficient.setDisable(false);
        }
    }

    public static double getMinMainCoefficient(){
        double minValue = 1.0;
        AppType appType = UserPreferences.getInstance().getSelectedApp();
        if(appType == AppType.KOREANIKAMASTER) {
            minValue = MAIN_COEFFICIENT_MIN_KM;
        }else if(appType == AppType.KOREANIKA) {
            minValue = MAIN_COEFFICIENT_MIN_K;
        }else if(appType == AppType.ZETTA) {
            minValue = MAIN_COEFFICIENT_MIN_Z;
        }else if(appType == AppType.PROMEBEL) {
            minValue = MAIN_COEFFICIENT_MIN_PM;
        }
        return minValue;
    }

    public static double getMaxMainCoefficient(){
        double maxValue = 1.0;
        AppType appType = UserPreferences.getInstance().getSelectedApp();
        if(appType == AppType.KOREANIKAMASTER) {
            maxValue = MAIN_COEFFICIENT_MAX_KM;
        }else if(appType == AppType.KOREANIKA) {
            maxValue = MAIN_COEFFICIENT_MAX_K;
        }else if(appType == AppType.ZETTA) {
            maxValue = MAIN_COEFFICIENT_MAX_Z;
        }else if(appType == AppType.PROMEBEL) {
            maxValue = MAIN_COEFFICIENT_MAX_PM;
        }
        return maxValue;
    }

    public static double getMinMaterialCoefficient(){
        double minValue = 1.0;
        AppType appType = UserPreferences.getInstance().getSelectedApp();
        if(appType == AppType.KOREANIKAMASTER) {
            minValue = MATERIAL_COEFFICIENT_MIN_KM;
        }else if(appType == AppType.KOREANIKA) {
            minValue = MATERIAL_COEFFICIENT_MIN_K;
        }else if(appType == AppType.ZETTA) {
            minValue = MATERIAL_COEFFICIENT_MIN_Z;
        }else if(appType == AppType.PROMEBEL) {
            minValue = MATERIAL_COEFFICIENT_MIN_PM;
        }

        return minValue;
    }

    public static double getMaxMaterialCoefficient(){
        double maxValue = 1.0;
        AppType appType = UserPreferences.getInstance().getSelectedApp();
        if(appType == AppType.KOREANIKAMASTER) {
            maxValue = MATERIAL_COEFFICIENT_MAX_KM;
        }else if(appType == AppType.KOREANIKA) {
            maxValue = MATERIAL_COEFFICIENT_MAX_K;
        }else if(appType == AppType.ZETTA) {
            maxValue = MATERIAL_COEFFICIENT_MAX_Z;
        }else if(appType == AppType.PROMEBEL) {
            maxValue = MATERIAL_COEFFICIENT_MAX_PM;
        }

        return maxValue;
    }




    public static void show(Scene mainScene) {

        if (priceCoefficientsWindow == null) {
            priceCoefficientsWindow = new PriceCoefficientsWindow();
        }

        priceCoefficientsWindow.refreshView();

        priceCoefficientsWindow.textFieldMaterialCoefficient.setText(String.format(Locale.ENGLISH, "%.2f", ProjectHandler.getPriceMaterialCoefficient().get()));
        priceCoefficientsWindow.textFieldMainCoefficient.setText(String.format(Locale.ENGLISH, "%.2f", ProjectHandler.getPriceMainCoefficient().get()));



        Window windowOwner = mainScene.getWindow();


        priceCoefficientStage = new Stage();
        priceCoefficientStage.setTitle("Настройка наценок");
        priceCoefficientStage.initOwner(windowOwner);
        priceCoefficientStage.setScene(priceCoefficientsWindow.priceCoefficientScene);
        priceCoefficientStage.initStyle(StageStyle.TRANSPARENT);
        priceCoefficientsWindow.priceCoefficientScene.setFill(Color.TRANSPARENT);
//        priceCoefficientStage.setX(windowOwner.getX() + windowOwner.getWidth() / 2 - priceCoefficientsWindow.priceCoefficientScene.getWidth() / 2);
//        priceCoefficientStage.setY(windowOwner.getY() + windowOwner.getHeight() / 2 - priceCoefficientsWindow.priceCoefficientScene.getHeight() / 2);
        priceCoefficientStage.initModality(Modality.APPLICATION_MODAL);
//        priceCoefficientStage.initStyle(StageStyle.UNDECORATED);
        priceCoefficientStage.setResizable(false);

        priceCoefficientStage.show();

    }
}
