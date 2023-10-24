package ru.koreanika.utils;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.cutDesigner.CutDesigner;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.project.ProjectType;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.*;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.sketchDesigner.SketchDesigner;
import ru.koreanika.tableDesigner.TableDesigner;
import ru.koreanika.utils.Currency.UserCurrency;
import ru.koreanika.utils.MaterialSelectionWindow.FirstStart;
import ru.koreanika.utils.MaterialSelectionWindow.MaterialSelectionWindow;
import ru.koreanika.utils.News.NewsController;
import ru.koreanika.utils.Receipt.Koreanika.ReceiptManagerKoreanika;
import ru.koreanika.utils.Receipt.Promebel.ReceiptManagerPromebel;
import ru.koreanika.utils.Receipt.ReceiptManager;
import ru.koreanika.utils.Receipt.Zetta.ReceiptManagerZetta;

import java.io.File;

public class MainWindow implements NotificationEventHandler, ApplicationTypeChangeEventHandler {

    static AnchorPane rootAnchorPaneMainWindow;

    ChangeViewListener changeView;

    private static DoubleProperty USDValue = new SimpleDoubleProperty();
    private static DoubleProperty EURValue = new SimpleDoubleProperty();

    static TableDesigner tableDesigner;
    static SketchDesigner sketchDesigner;
    static CutDesigner cutDesigner;
    static ReceiptManager receiptManager;

    NewsController newsController;

    public MainWindow() {
        EventBus eventBus = ServiceLocator.getService("EventBus", EventBus.class);
        eventBus.addHandler(NotificationEvent.TYPE, this);
        eventBus.addHandler(ApplicationTypeChangeEvent.TYPE, this);

        rootAnchorPaneMainWindow = new AnchorPane();
        rootAnchorPaneMainWindow.setId("rootAnchorPaneMainWindow");

        // letting know globally which node was clicked, see https://stackoverflow.com/a/38139005
        rootAnchorPaneMainWindow.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            Node source = e.getPickResult().getIntersectedNode();
            eventBus.fireEvent(new MouseClickedEvent(source));
        });

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

    protected void createProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Some Files");
        File file = fileChooser.showSaveDialog(rootAnchorPaneMainWindow.getScene().getWindow());

        if (file != null) {
            //save current project
            ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());

            ProjectHandler.closeProject();
            rootAnchorPaneMainWindow.getChildren().clear();

            String path = file.getPath();
            String projName = file.getName();
            ProjectHandler.createProject(projName, path, ProjectType.TABLE_TYPE);

            MaterialSelectionWindow.getInstance().setFirstStartFlag(true);
            MaterialSelectionWindow.clear();

            MaterialSelectionWindow.getInstance().setFirstStart(new FirstStart() {
                @Override
                public void firstMaterialSelected() {
                    System.out.println("firstMaterialSelected()");

                    sketchDesigner = new SketchDesigner();
                    cutDesigner = CutDesigner.getInstance().createNewCutDesigner();
                    tableDesigner = new TableDesigner();

                    AppType appType = UserPreferences.getInstance().getSelectedApp();
                    if (appType == AppType.KOREANIKA || appType == AppType.KOREANIKAMASTER) {
                        receiptManager = new ReceiptManagerKoreanika();
                    } else if (appType == AppType.ZETTA) {
                        receiptManager = new ReceiptManagerZetta();
                    } else if (appType == AppType.PROMEBEL) {
                        receiptManager = new ReceiptManagerPromebel();
                    }

                    MaterialSelectionWindow.getInstance().setFirstStartFlag(false);
                    showTableView();
                }
            });

            showMaterialSelection();
            UserCurrency.getInstance().updateCurrencyValue();
        }
    }

    protected void openProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Some Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Koreanika projects", "*.krnkproj", "*.kproj")
        );

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
    }

    protected void saveProject() {
        ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());
    }

    protected void saveAsProject() {
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
        if (file != null) {
            String path = file.getPath();
            String projName = file.getName();
            ProjectHandler.saveProject(path, projName); //save new name project
            ((Stage) rootAnchorPaneMainWindow.getScene().getWindow()).setTitle("Koreanika: " + ProjectHandler.getCurProjectPath());
        }

    }

    protected void showCutView() {
        rootAnchorPaneMainWindow.getChildren().clear();
        rootAnchorPaneMainWindow.getChildren().add(cutDesigner.getRootAnchorPaneCutDesigner());
        AnchorPane.setTopAnchor(cutDesigner.getRootAnchorPaneCutDesigner(), 0.0);
        AnchorPane.setBottomAnchor(cutDesigner.getRootAnchorPaneCutDesigner(), 0.0);
        AnchorPane.setLeftAnchor(cutDesigner.getRootAnchorPaneCutDesigner(), 0.0);
        AnchorPane.setRightAnchor(cutDesigner.getRootAnchorPaneCutDesigner(), 0.0);

        changeView.viewChanged(ViewType.CUT_VIEW);

        cutDesigner.getRootAnchorPaneCutDesigner().requestFocus();
        cutDesigner.refreshCutView();
    }

    protected void showTableView() {
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

    protected void showMaterialSelection() {
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

    protected void showReceiptWithCutting() {
        if (Boolean.parseBoolean(Main.getProperty("autosave.afterReceipt"))) {
            ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());//save current project
        }
        System.out.println("ShowReceipt");
        CutDesigner.getInstance().autoCutting(true);
    }

    public void showReceipt() {
        AnchorPane receiptRoot = receiptManager.getView();

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

    protected void toggleNews() {
        if (rootAnchorPaneMainWindow.getChildren().contains(newsController.getView())) {
            newsController.hide();
        } else {
            newsController.show();
        }
    }

    @Override
    public void onEvent(ApplicationTypeChangeEvent e) {
        AppType appType = e.getApplicationType();
        if (appType == AppType.KOREANIKA || appType == AppType.KOREANIKAMASTER) {
            receiptManager = new ReceiptManagerKoreanika();
        } else if (appType == AppType.ZETTA) {
            receiptManager = new ReceiptManagerZetta();
        } else if (appType == AppType.PROMEBEL) {
            receiptManager = new ReceiptManagerPromebel();
        }
    }

    public static void closeProject() {
        ProjectHandler.closeProject();
        rootAnchorPaneMainWindow.getChildren().clear();
        ((Stage) rootAnchorPaneMainWindow.getScene().getWindow()).setTitle("Koreanika " + Main.actualAppVersion);
    }

    public static void projectOpenedLogic(File file) {
        if (file != null) {
            String path = file.getPath();
            String projName = file.getName();
            ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());//save current project
            ProjectHandler.closeProject();

            sketchDesigner = new SketchDesigner();
            System.out.println(cutDesigner);

            cutDesigner = CutDesigner.getInstance().createNewCutDesigner();

            System.out.println("OPEN pro");

            AppType appType = UserPreferences.getInstance().getSelectedApp();
            if (appType == AppType.KOREANIKA || appType == AppType.KOREANIKAMASTER) {
                receiptManager = new ReceiptManagerKoreanika();
            } else if (appType == AppType.ZETTA) {
                receiptManager = new ReceiptManagerZetta();
            } else if (appType == AppType.PROMEBEL) {
                receiptManager = new ReceiptManagerPromebel();
            }

            if (!ProjectHandler.openProject(path, projName)) {
                return;
            }

            rootAnchorPaneMainWindow.getChildren().clear();

            if (ProjectHandler.getProjectType() == ProjectType.SKETCH_TYPE) {
                rootAnchorPaneMainWindow.getChildren().add(sketchDesigner.getRootAnchorPaneSketchDesigner());
                AnchorPane.setTopAnchor(sketchDesigner.getRootAnchorPaneSketchDesigner(), 0.0);
                AnchorPane.setBottomAnchor(sketchDesigner.getRootAnchorPaneSketchDesigner(), 0.0);
                AnchorPane.setLeftAnchor(sketchDesigner.getRootAnchorPaneSketchDesigner(), 0.0);
                AnchorPane.setRightAnchor(sketchDesigner.getRootAnchorPaneSketchDesigner(), 0.0);
                sketchDesigner.getRootAnchorPaneSketchDesigner().requestFocus();
            } else {
                AnchorPane tableRoot = tableDesigner.getAnchorPaneRoot();
                rootAnchorPaneMainWindow.getChildren().add(tableRoot);
                AnchorPane.setTopAnchor(tableRoot, 0.0);
                AnchorPane.setBottomAnchor(tableRoot, 0.0);
                AnchorPane.setLeftAnchor(tableRoot, 0.0);
                AnchorPane.setRightAnchor(tableRoot, 0.0);

                tableRoot.requestFocus();

                TableDesigner.updatePriceInRows();
            }
        }
    }

    @Deprecated
    /**
     * этот метод ещё используется в классах, наследующих sketchDesigner.Shapes
     */
    public static void showInfoMessage(InfoMessage.MessageType msgType, String message) {
        Platform.runLater(() -> InfoMessage.showMessage(msgType, message, null));
    }

    @Override
    public void onEvent(NotificationEvent e) {
        Platform.runLater(() -> InfoMessage.showMessage(e.getMessageType(), e.getMessage(), null));
    }

    public enum ViewType {
        TABLE_VIEW,
        CUT_VIEW,
        MATERIAL_VIEW,
        RECEIPT_VIEW
    }

    public interface ChangeViewListener {
        void viewChanged(ViewType newViewType);
    }

}
