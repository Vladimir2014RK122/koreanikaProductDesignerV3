package utils;

import PortalClient.Authorization.AppType;
import PortalClient.Authorization.Authorization;
import Preferences.UserPreferences;
import cutDesigner.CutDesigner;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.SetChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sketchDesigner.SketchDesigner;
import tableDesigner.TableDesigner;
import utils.Currency.UserCurrency;
import utils.MaterialSelectionWindow.FirstStart;
import utils.MaterialSelectionWindow.MaterialSelectionWindow;
import utils.News.NewsController;
import utils.Receipt.Koreanika.ReceiptManagerKoreanika;
import utils.Receipt.Promebel.ReceiptManagerPromebel;
import utils.Receipt.ReceiptManager;
import utils.Receipt.Zetta.ReceiptManagerZetta;

import java.io.File;

public class MainWindow {

    /* System buttons: */


    static AnchorPane rootAnchorPaneMainWindow;//, anchorPaneChildrenRoot;
//    static AnchorPane anchorPaneHeader;
    static SplitPane splitPaneHorizontal;
    //static ListView<ReceiptCellItem> listViewInReceipt;
    //static Button btnCreateProject, btnOpenProject, btnSaveProject, btnSaveAsProject, btnCloseProject;
//    static Button btnSelectMaterial, btnSketchView, btnCutView, btn3DView, btnAutoCut, btnShowReceipt;
    //static Button btnSettings;
    //static Button btnCutView, btnShowReceipt;

//    static Scene mainScene = null;

    //private static Button btnUpdater, btnNews;
    //private static Button btnLogout;
    //private static Label labelUpdateServerStatus;


    ChangeViewListener changeView;


    //static TextField textFieldUSD, textFieldEUR;
    //static Button btnCurrencyUpdate, btnPriceCoefficient;

    private static DoubleProperty USDValue = new SimpleDoubleProperty();
    private static DoubleProperty EURValue = new SimpleDoubleProperty();


    static TableDesigner tableDesigner;
    static SketchDesigner sketchDesigner;
    static CutDesigner cutDesigner;
    static ReceiptManager receiptManager;
    //static MaterialSelectionWindow materialSelectionWindow;

    private static boolean enableReceiptListeners = true;

    NewsController newsController;

    public MainWindow() {

//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("/fxmls/mainWindow.fxml"));
//        try {
//            rootAnchorPaneMainWindow = fxmlLoader.load();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        rootAnchorPaneMainWindow = new AnchorPane();

        rootAnchorPaneMainWindow.setId("rootAnchorPaneMainWindow");

//        rootAnchorPaneMainWindow.getStylesheets().clear();
//
//        if(Main.appType == AppType.KOREANIKA || Main.appType == AppType.KOREANIKAMASTER){
//            System.out.println("OWNER = KOREANIKA");
//            rootAnchorPaneMainWindow.getStylesheets().add(getClass().getResource("/styles/colorsKoreanika.css").toExternalForm());
//        }else if(Main.appType == AppType.ZETTA){
//            rootAnchorPaneMainWindow.getStylesheets().add(getClass().getResource("/styles/colorsZetta.css").toExternalForm());
//        }else if(Main.appType == AppType.PROMEBEL){
//            rootAnchorPaneMainWindow.getStylesheets().add(getClass().getResource("/styles/colorsPromebel.css").toExternalForm());
//        }
//        rootAnchorPaneMainWindow.getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());
//        rootAnchorPaneMainWindow.getStylesheets().add(getClass().getResource("/styles/mainWindow.css").toExternalForm());


        initControlElements();
//        initMaterialSelectionWindow();
        initControlElementsLogic();

//        initSystemButtons();
//        initDraggableStage();



        newsController = NewsController.createNewsController(rootAnchorPaneMainWindow);


    }

    public void setChangeView(ChangeViewListener changeView) {
        this.changeView = changeView;
    }

    public static DoubleProperty getUSDValue() {
        return USDValue;
    }

    public static DoubleProperty getEURValue() {
        return EURValue;
    }

    public static void setUSDValue(double USDValue) {
        MainWindow.USDValue.set(USDValue);
    }

    public static void setEURValue(double EURValue) {
        MainWindow.EURValue.set(EURValue);
    }

    public static DoubleProperty EURValueProperty() {
        return EURValue;
    }

    public static DoubleProperty USDValueProperty() {
        return USDValue;
    }

    public static TableDesigner getTableDesigner() {
        return tableDesigner;
    }

    public static void setTableDesigner(TableDesigner tableDesigner) {
        MainWindow.tableDesigner = tableDesigner;
    }

    public static CutDesigner getCutDesigner() {
        return cutDesigner;
    }

    public static ReceiptManager getReceiptManager() {
        return receiptManager;
    }

    public static void setReceiptManager(ReceiptManager receiptManager) {
        MainWindow.receiptManager = receiptManager;
    }

    public static void setCutDesigner(CutDesigner cutDesigner) {
        MainWindow.cutDesigner = cutDesigner;
    }

    public static SketchDesigner getSketchDesigner() {
        return sketchDesigner;
    }

    public static void setSketchDesigner(SketchDesigner sketchDesigner) {
        MainWindow.sketchDesigner = sketchDesigner;
    }

    public AnchorPane getRootAnchorPaneMainWindow() {
        return rootAnchorPaneMainWindow;
    }

//    public AnchorPane getAnchorPaneChildrenRoot() {
//        return anchorPaneChildrenRoot;
//    }

    public void initControlElements() {
    }


    protected void createProject(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Some Files");
        File file = fileChooser.showSaveDialog(rootAnchorPaneMainWindow.getScene().getWindow());

        String path = "null";
        String projName = "null";

        if (file != null) {
            path = file.getPath();
            projName = file.getName();

            ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());//save current project

            ProjectHandler.closeProject();
            rootAnchorPaneMainWindow.getChildren().clear();


            String finalProjName = projName;
            String finalPath = path;


            ProjectHandler.createProject(finalProjName, finalPath, ProjectType.TABLE_TYPE);

            //anchorPaneChildrenRoot.getChildren().clear();


//            ((Stage) rootAnchorPaneMainWindow.getScene().getWindow()).setTitle(
//                    Main.appType.getName().toUpperCase() + " " + Main.actualAppVersion + ": " + ProjectHandler.getCurProjectPath()
//            );

//            btnSaveProject.setDisable(false);
//            btnSaveAsProject.setDisable(false);
//            btnCloseProject.setDisable(false);
//            btnPriceCoefficient.setDisable(false);
//
//            btnSelectMaterial.setDisable(false);
//            btnSketchView.setDisable(false);
            //btnCutView.setDisable(false);
//            btn3DView.setDisable(false);
            //btnShowReceipt.setDisable(false);
            //btnAutoCut.setDisable(false);
            //menuBtnCloseProj.setDisable(false);

            MaterialSelectionWindow.getInstance().setFirstStartFlag(true);
            MaterialSelectionWindow.clear();


            MaterialSelectionWindow.getInstance().setFirstStart(new FirstStart() {
                @Override
                public void firstMaterialSelected() {

                    System.out.println("firstMaterialSelected()");

                    sketchDesigner = new SketchDesigner();
                    cutDesigner = CutDesigner.getInstance().createNewCutDesigner();
                    tableDesigner = new TableDesigner();

//                              if(Main.appOwner.toUpperCase().equals("KOREANIKA")){
                    AppType appType = UserPreferences.getInstance().getSelectedApp();
                    if(appType == AppType.KOREANIKA || appType == AppType.KOREANIKAMASTER){
                        receiptManager = new ReceiptManagerKoreanika();
//                              }else if(Main.appOwner.toUpperCase().equals("ZETTA")){
                    }else if(appType == AppType.ZETTA){
                        receiptManager = new ReceiptManagerZetta();
                    }else if(appType == AppType.PROMEBEL){
                        receiptManager = new ReceiptManagerPromebel();
                    }

                    MaterialSelectionWindow.getInstance().setFirstStartFlag(false);
                    showTableView();
                }
            });

//            materialSelectionWindow.show(rootAnchorPaneMainWindow.getScene());


            showMaterialSelection();
            UserCurrency.getInstance().updateCurrencyValue();

        }

    }

    protected void openProject(){



        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Some Files");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Koreanika projects", "*.krnkproj", "*.kproj"));


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


        File file = fileChooser.showOpenDialog(rootAnchorPaneMainWindow.getScene().getWindow());


        projectOpenedLogic(file);
        UserCurrency.getInstance().updateCurrencyValueWithRequest();
        //checkCurrencyLvl(textFieldUSD, "USD");
        //checkCurrencyLvl(textFieldEUR,"EUR");

    }

    protected void saveProject(){

        ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());

//        ((Stage) rootAnchorPaneMainWindow.getScene().getWindow()).setTitle(
//                Main.appType.getName().toUpperCase() + " " + Main.actualAppVersion + ": " + ProjectHandler.getCurProjectPath()
//        );

    }

    protected void saveAsProject(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Some Files");

        if (ProjectHandler.getCurProjectPath() != null) {

            String[] pathArr = ProjectHandler.getCurProjectPath().split("\\\\");
            String path1 = "";
            for (int i = 0; i < pathArr.length - 1; i++) {
                path1 += "/" + pathArr[i];
            }
            System.out.println(ProjectHandler.getCurProjectPath());
            System.out.println(path1);

            fileChooser.setInitialDirectory(new File(path1));
            fileChooser.setInitialFileName(ProjectHandler.getCurProjectName());
        }

        File file = fileChooser.showSaveDialog(rootAnchorPaneMainWindow.getScene().getWindow());

        String path = "null";
        String projName = "null";

        if (file != null) {
            path = file.getPath();
            projName = file.getName();

            //ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());//save current project
            //ProjectHandler.setCurProjectName(projName);
            //ProjectHandler.setCurProjectPath(path);
            ProjectHandler.saveProject(path, projName);//save new name project

            //deviceViewZone.rebuildTree();

            ((Stage) rootAnchorPaneMainWindow.getScene().getWindow()).setTitle("Koreanika: " + ProjectHandler.getCurProjectPath());
        }

    }

    protected void showCutView(){

        rootAnchorPaneMainWindow.getChildren().clear();
        rootAnchorPaneMainWindow.getChildren().add(cutDesigner.getRootAnchorPaneCutDesigner());
        AnchorPane.setTopAnchor(cutDesigner.getRootAnchorPaneCutDesigner(), 0.0);
        AnchorPane.setBottomAnchor(cutDesigner.getRootAnchorPaneCutDesigner(), 0.0);
        AnchorPane.setLeftAnchor(cutDesigner.getRootAnchorPaneCutDesigner(), 0.0);
        AnchorPane.setRightAnchor(cutDesigner.getRootAnchorPaneCutDesigner(), 0.0);

//        btnAutoCut.setVisible(true);

        changeView.viewChanged(ViewType.CUT_VIEW);

        cutDesigner.getRootAnchorPaneCutDesigner().requestFocus();
        cutDesigner.refreshCutView();


    }

    protected void showTableView(){
        rootAnchorPaneMainWindow.getChildren().clear();

        AnchorPane tableRoot = tableDesigner.getAnchorPaneRoot();
        rootAnchorPaneMainWindow.getChildren().add(tableRoot);
        AnchorPane.setTopAnchor(tableRoot, 0.0);
        AnchorPane.setBottomAnchor(tableRoot, 0.0);
        AnchorPane.setLeftAnchor(tableRoot, 0.0);
        AnchorPane.setRightAnchor(tableRoot, 0.0);

        changeView.viewChanged(ViewType.TABLE_VIEW);

        tableRoot.requestFocus();
    }

    protected void showMaterialSelection(){

        AnchorPane materialsWindowRoot = MaterialSelectionWindow.getInstance().getView();
        materialsWindowRoot.getStyleClass().add("materialWindowRoot");

        rootAnchorPaneMainWindow.getChildren().clear();
        rootAnchorPaneMainWindow.getChildren().add(materialsWindowRoot);

        AnchorPane.setTopAnchor(materialsWindowRoot, 0.0);
        AnchorPane.setBottomAnchor(materialsWindowRoot, 0.0);
        AnchorPane.setLeftAnchor(materialsWindowRoot, 0.0);
        AnchorPane.setRightAnchor(materialsWindowRoot, 0.0);

        changeView.viewChanged(ViewType.MATERIAL_VIEW);

        materialsWindowRoot.requestFocus();
    }

    protected void showReceiptWithCutting(){
        if(Boolean.parseBoolean(Main.getProperty("autosave.afterReceipt"))){
            ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());//save current project
        }
        System.out.println("ShowReceipt");
        //btnCutView.getOnMouseClicked().handle(null);
        //CutDesigner.refreshCutView();
        //ReceiptManager.show(rootAnchorPaneMainWindow.getScene());
        CutDesigner.getInstance().autoCutting(true);

//        changeView.viewChanged(ViewType.RECEIPT_VIEW);
    }

    public void showReceipt(){
        AnchorPane receiptRoot = receiptManager.getView();
//        materialsWindowRoot.getStyleClass().add("materialWindowRoot");

        rootAnchorPaneMainWindow.getChildren().clear();
        rootAnchorPaneMainWindow.getChildren().add(receiptRoot);
        rootAnchorPaneMainWindow.getChildren().clear();
        rootAnchorPaneMainWindow.getChildren().add(receiptRoot);

        AnchorPane.setTopAnchor(receiptRoot, 0.0);
        AnchorPane.setBottomAnchor(receiptRoot, 0.0);
        AnchorPane.setLeftAnchor(receiptRoot, 0.0);
        AnchorPane.setRightAnchor(receiptRoot, 0.0);

        changeView.viewChanged(ViewType.MATERIAL_VIEW);

        receiptRoot.requestFocus();
    }

    protected void toggleNews(){
        if(rootAnchorPaneMainWindow.getChildren().contains(newsController.getView())){
            newsController.hide();
        }else{
            newsController.show();
        }
    }
    public void initControlElementsLogic() {

        UserPreferences.getInstance().addAppTypeChangeListener(change -> {
//            System.out.println("MainWindow.class - APP CHANGED: " + change);

            AppType appType = UserPreferences.getInstance().getSelectedApp();
            if(appType == AppType.KOREANIKA || appType == AppType.KOREANIKAMASTER){
                receiptManager = new ReceiptManagerKoreanika();
            }else if(appType == AppType.ZETTA){
                receiptManager = new ReceiptManagerZetta();
            }else if(appType == AppType.PROMEBEL){
                receiptManager = new ReceiptManagerPromebel();
            }

        });
    }


    public static void closeProject(){
        ProjectHandler.closeProject();
        rootAnchorPaneMainWindow.getChildren().clear();

        ((Stage) rootAnchorPaneMainWindow.getScene().getWindow()).setTitle("Koreanika " + Main.actualAppVersion);

    }

    public static void projectOpenedLogic(File file){
        String path = "null";
        String projName = "null";

        if (file != null) {
            path = file.getPath();
            projName = file.getName();



            ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());//save current project


            ProjectHandler.closeProject();


            sketchDesigner = new SketchDesigner();
            System.out.println(cutDesigner);

            cutDesigner = CutDesigner.getInstance().createNewCutDesigner();

            System.out.println("OPEN pro");


            AppType appType = UserPreferences.getInstance().getSelectedApp();
            if(appType == AppType.KOREANIKA || appType == AppType.KOREANIKAMASTER){
                receiptManager = new ReceiptManagerKoreanika();
            }else if(appType == AppType.ZETTA){
                receiptManager = new ReceiptManagerZetta();
            }else if(appType == AppType.PROMEBEL){
                receiptManager = new ReceiptManagerPromebel();
            }


            if (!ProjectHandler.openProject(path, projName)) {
                return;
            }


//            ((Stage) rootAnchorPaneMainWindow.getScene().getWindow()).setTitle(
//                    Main.appType.getName().toUpperCase() + " " + Main.actualAppVersion + ": " + ProjectHandler.getCurProjectPath()
//            );

            rootAnchorPaneMainWindow.getChildren().clear();

            if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
//                btnSketchView.setText("Эскиз");
                rootAnchorPaneMainWindow.getChildren().add(sketchDesigner.getRootAnchorPaneSketchDesigner());
                AnchorPane.setTopAnchor(sketchDesigner.getRootAnchorPaneSketchDesigner(), 0.0);
                AnchorPane.setBottomAnchor(sketchDesigner.getRootAnchorPaneSketchDesigner(), 0.0);
                AnchorPane.setLeftAnchor(sketchDesigner.getRootAnchorPaneSketchDesigner(), 0.0);
                AnchorPane.setRightAnchor(sketchDesigner.getRootAnchorPaneSketchDesigner(), 0.0);

                sketchDesigner.getRootAnchorPaneSketchDesigner().requestFocus();

//                MainWindow.hideReceiptImagesItemsList(false);
            } else {
//                btnSketchView.setText("Таблица");

                AnchorPane tableRoot = tableDesigner.getAnchorPaneRoot();
                rootAnchorPaneMainWindow.getChildren().add(tableRoot);
                AnchorPane.setTopAnchor(tableRoot, 0.0);
                AnchorPane.setBottomAnchor(tableRoot, 0.0);
                AnchorPane.setLeftAnchor(tableRoot, 0.0);
                AnchorPane.setRightAnchor(tableRoot, 0.0);

                tableRoot.requestFocus();

                TableDesigner.updatePriceInRows();

//                MainWindow.hideReceiptImagesItemsList(true);

            }
        }

    }


//    public static Label getLabelUpdateServerStatus() {
//        return labelUpdateServerStatus;
//    }
//
//    public TextField getTextFieldEUR() {
//        return textFieldEUR;
//    }
//
//    public TextField getTextFieldUSD() {
//        return textFieldUSD;
//    }

//    public void initMaterialSelectionWindow() {
//        materialSelectionWindow = MaterialSelectionWindow.getInstance();
//    }

    public static void showInfoMessage(InfoMessage.MessageType msgType, String message) {
        Platform.runLater(()-> {
            InfoMessage.showMessage(msgType, message, null);
        });

    }

//    public static Button getBtnNews() {
//        return btnNews;
//    }


//    public static void setUser(User user){
//
//        if(user == null){
//            Main.getMainWindowDecorator().setUserName("none");
//
//        }else{
//            Main.getMainWindowDecorator().setUserName(user.getLogin());
//        }
//
//    }


    public enum ViewType{
        TABLE_VIEW,
        CUT_VIEW,
        MATERIAL_VIEW,
        RECEIPT_VIEW
    }
    public interface ChangeViewListener {

        void viewChanged(ViewType newViewType);
    }

}
