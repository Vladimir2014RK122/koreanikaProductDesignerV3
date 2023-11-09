package ru.koreanika.utils;

import ru.koreanika.PortalClient.Authorization.Authorization;
import ru.koreanika.PortalClient.Maintenance.ClimeType;
import ru.koreanika.PortalClient.Maintenance.MaintenanceMessage;
import ru.koreanika.PortalClient.Status.PortalStatus;
import ru.koreanika.PortalClient.Update.UpdateService;
import ru.koreanika.Preferences.UserPreferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.utils.currency.BankCurrency;
import ru.koreanika.utils.currency.UserCurrency;
import ru.koreanika.utils.News.NewsController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class Main extends Application {

    public static String actualAppVersion = "1.0.1 RELEASE";
    private static Scene mainScene;
    private static MainWindow mainWindow;
    private static MainWindowDecorator mainWindowDecorator;

    //private static String MAIN_PROPERTIES_FILENAME = "main.properties";
    private static String UPDATER_PROPERTIES_FILENAME = "updater.properties";
    private static Properties updaterProperties;
    //public static String appOwner;
//    public static AppOwner1 appOwner;
//    public static AppType appType;
    public static String appVersion;

    public static double mainCoefficient;
    public static double materialCoefficient;

    int portalUnavailableCounter = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("ТЕСТ КИРИЛЛИЦЫ");

        try {
            ProjectHandler.projectHandlerInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //int R = 100;
        int v1 = 10;
        int v2 = 60;

        for (int R = 100; R <= 1000; R += 100) {
            int b = (int) (R * Math.pow(2, 0.5) + 2 * v1 * Math.pow(2, 0.5) + 5);
            //length *= count;
            int a = (int) ((R - R * Math.pow(0.5, 0.5)) + v1 + (v2 * Math.pow(2, 0.5)) + 5);
            System.out.println("R= " + R + " a = " + a + " b = " + b);
        }

        initMainProperties();

        appVersion = getActualVersion(".//");
//        primaryStage.setTitle(appType.getName().toUpperCase() + " " + appVersion);

        Font.loadFont("file:resources/fonts/MinionPro-Regular.ttf", 10);
        Font.loadFont("file:resources/fonts/OpenSans-Regular.ttf", 10);
        Font.loadFont("file:resources/fonts/OpenSans-Bold.ttf", 10);

        //System.setProperty("prism.lcdtext", "true");
        //System.setProperty("prism.allowhidpi", "true");
        //-Dglass.win.minHiDPI=1 //vm option for hidpi

        mainWindow = new MainWindow();
        mainWindowDecorator = new MainWindowDecorator(primaryStage, mainWindow);

//        AnchorPane rootAnchorPaneMainWindow = mainWindow.getRootAnchorPaneMainWindow();
//        mainWindowDecorator.setRoot(rootAnchorPaneMainWindow);

        mainScene = new Scene(mainWindowDecorator.getDecorator(), 1350, 800);
        primaryStage.setScene(mainScene);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        mainScene.setFill(Color.TRANSPARENT);

        primaryStage.sizeToScene();
        primaryStage.show();
        //primaryStage.setMinWidth(1100);
        //primaryStage.setMinHeight(800)

        mainScene.getRoot().setOnDragEntered(event -> {
            System.out.println(event.getDragboard().getFiles());
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setContentText(event.getTransferMode().toString());
//            alert.show();
        });
        mainScene.getRoot().setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
        });

        mainScene.getRoot().setOnDragDropped(event -> {
            if (event.getDragboard().hasFiles()) {
                System.out.println("path: " + event.getDragboard().getFiles().get(0).getPath());
                System.out.println("name: " + event.getDragboard().getFiles().get(0).getName());

                ProjectHandler.openProjectFromArguments(event.getDragboard().getFiles().get(0).getPath());

                event.setDropCompleted(true);
            }
            event.consume();
        });

        //mainScene.getWindow().setOnShown(windowEvent ->{

        //});


        primaryStage.getIcons().add(new Image(this.getClass().getResource("/styles/icons/koreanika_icon_3.png").toString()));
        primaryStage.setOnCloseRequest(event -> {

            InfoMessage.stopMessage();


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.initOwner(mainScene.getWindow());
            alert.initModality(Modality.WINDOW_MODAL);

            alert.setTitle("Выйти из приложения?");
            alert.setHeaderText("Вы уверены, что хотите выйти из приложения?");
            alert.setContentText("Сохранить перед выходом?");

            ButtonType buttonTypeNo = new ButtonType("Не сохранять");
            ButtonType buttonTypeYes = new ButtonType("Сохранить");
            ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeNo, buttonTypeYes, buttonTypeCancel);

            if (ProjectHandler.getUserProject() == null) {
                primaryStage.close();
                event.consume();
                return;
            }
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeNo) {
                // ... user chose "NO"
                primaryStage.close();
            } else if (result.get() == buttonTypeYes) {
                // ... user chose "YES"
                ProjectHandler.saveProject(ProjectHandler.getCurProjectPath(), ProjectHandler.getCurProjectName());
                primaryStage.close();
            } else if (result.get() == buttonTypeCancel) {
                // ... user chose "Three"
                System.out.println("CANCEL");
            }

            event.consume();

//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("type", "app stopped");
//            UserEventService.getInstance().sendEventRequest(jsonObject);
        });


        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {

                System.err.println("EXCEPTION !!!!" + e);
                String stackTrace = "";
                for (StackTraceElement s : e.getStackTrace()) {
                    System.err.println(s.toString());
                    stackTrace += s.toString() + "\n";
                }

                String finalStackTrace = stackTrace;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR);

                        alert.setHeaderText("ERROR: " + e);

                        alert.getDialogPane().setContent(new TextArea(finalStackTrace));
                        alert.show();
                    }
                });

                MaintenanceMessage maintenanceMessage = new MaintenanceMessage(finalStackTrace);
                try {
                    maintenanceMessage.sendMessageToPortal(ClimeType.ERROR);
                } catch (ExecutionException ex) {
//                    ex.printStackTrace();
                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
                }
            }
        });

        String accessToken = UserPreferences.getInstance().getAccessToken();
        String refreshToken = UserPreferences.getInstance().getRefreshToken();
        System.out.println("accessToken = " + accessToken);
        System.out.println("refreshToken = " + refreshToken);
        PortalStatus.getInstance().startMonitoring(getProperty("server.host"));

        PortalStatus.getInstance().portalAvailableProperty().addListener((observableValue, aBoolean, newValue) -> {
            if (newValue.booleanValue()) {
                System.out.println("PORTAL AVAILABLE");
                Platform.runLater(() -> Authorization.getInstance().startApp());
            } else {
                //portal offline
                System.out.println("PORTAL UNAVAILABLE");
                Authorization.getInstance().accessPermittedProperty().set(false);
                Platform.runLater(() -> Authorization.getInstance().showLoginWindow());
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (!PortalStatus.getInstance().isPortalAvailable()) {
                        Platform.runLater(() -> Authorization.getInstance().showLoginWindow());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        Authorization.getInstance().accessPermittedProperty().addListener((observableValue, aBoolean, newValue) -> {


            Platform.runLater(() -> {

                if (newValue.booleanValue()) {

                    //start Update check Thread:
                    //UpdateChecker.startCheckUpdates();
                    UpdateService.getInstance().startAvailableVersionThread();

                    //Start currency monitoring:
                    {

                        BankCurrency.getInstance().startMonitor();

                        BankCurrency.getInstance().setFirstCurrencyServerAnswer(() -> {

                            UserCurrency.getInstance().updateCurrencyValue();
                        });
                    }

                    //open project from double click on file:
                    Parameters params = getParameters();
                    if (params.getRaw().size() != 0) {
                        File file = new File(params.getRaw().get(0));
                        MainWindow.projectOpenedLogic(file);
                        mainWindowDecorator.refreshControls();
//                        ProjectHandler.openProjectFromArguments(params.getRaw().get(0));
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (NewsController.getNewsController().isHaveNotSeenCards()) NewsController.show();
                                }
                            });
                        }
                    }).start();
                }

            });


        });
    }

    public static MainWindowDecorator getMainWindowDecorator() {
        return mainWindowDecorator;
    }

    private static String getActualVersion(String appPath) {
        String version = "*dev version";
        try {
            version = new String(Files.readAllBytes(Paths.get(appPath + "/version")));
        } catch (IOException e) {
//            File fileVersion = new File(appPath + "/version");
//            try {
//                fileVersion.createNewFile();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
            System.err.println("file version does not exist");
            //e.printStackTrace();
        }
        actualAppVersion = version;
        return version;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Scene getMainScene() {
        return mainScene;
    }

    public static MainWindow getMainWindow() {
        return mainWindow;
    }

    private static void initMainProperties() {
        FileInputStream fis = null;
        updaterProperties = new Properties();

        boolean needToSave = false;

        try {
            fis = new FileInputStream(UPDATER_PROPERTIES_FILENAME);
        } catch (IOException e) {
            System.err.println("NO updater.properties FILE!!!");
            System.exit(1);
            return;
        }

        try {
            updaterProperties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("CANT LOAD PROPERTIES. main.properties FILE!!!");
            System.exit(1);
            return;
        }


//        /* Owner */
//        {
//            if (updaterProperties.getProperty("owner") == null) {
//                updaterProperties.put("owner", "koreanika");
//                try {
//                    updaterProperties.store(new FileOutputStream(UPDATER_PROPERTIES_FILENAME), null);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            appType = AppType.getByShortName(updaterProperties.getProperty("owner"));
//        }

        /* coefficients */
        {
            if (updaterProperties.getProperty("mainCoefficient") == null) {
                updaterProperties.put("mainCoefficient", "" + PriceCoefficientsWindow.getMinMainCoefficient());
                needToSave = true;
            }

            if (updaterProperties.getProperty("materialCoefficient") == null) {
                updaterProperties.put("materialCoefficient", "" + PriceCoefficientsWindow.getMinMaterialCoefficient());
                needToSave = true;
            }

            try {
                materialCoefficient = Double.valueOf(updaterProperties.getProperty("materialCoefficient"));
            } catch (NumberFormatException ex) {
                System.err.println("PROPERTIES ERROR. materialCoefficient wrong value!");
                updaterProperties.put("materialCoefficient", "" + PriceCoefficientsWindow.getMinMaterialCoefficient());
                needToSave = true;
            }

            try {
                mainCoefficient = Double.valueOf(updaterProperties.getProperty("mainCoefficient"));
            } catch (NumberFormatException ex) {
                System.err.println("PROPERTIES ERROR. mainCoefficient wrong value!");
                updaterProperties.put("mainCoefficient", "" + PriceCoefficientsWindow.getMinMainCoefficient());
                needToSave = true;
            }

//            if(appType == AppType.KOREANIKAMASTER && !updaterProperties.getProperty("host").equals("http://51.250.29.155:8081")){
//                updaterProperties.put("host", "http://51.250.29.155:8081");
//                needToSave = true;
//            }

            System.out.println("mainCoefficient = " + mainCoefficient);
            System.out.println("materialCoefficient = " + materialCoefficient);

            ProjectHandler.setPriceMainCoefficient(mainCoefficient);
            ProjectHandler.setPriceMaterialCoefficient(materialCoefficient);
        }

        //company address:
        {
            if (updaterProperties.getProperty("companyAddress") == null) {
                updaterProperties.put("companyAddress", "\tООО \"Кореаника\" Балашиха, мкр. Гагарина 13а e-mail: info@koreanika.ru +7(495) 665-82-95");
                try {
                    updaterProperties.store(new FileOutputStream(UPDATER_PROPERTIES_FILENAME), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //autoSave
        {
            if (updaterProperties.getProperty("autosave.afterCut") == null) {
                updaterProperties.put("autosave.afterCut", "false");
                needToSave = true;
            }
            if (updaterProperties.getProperty("autosave.afterReceipt") == null) {
                updaterProperties.put("autosave.afterReceipt", "true");
                needToSave = true;
            }
        }

        //portal address
        {
            if (updaterProperties.getProperty("server.host") == null) {
                updaterProperties.put("server.host", "portal.koreanika.ru");
                needToSave = true;
            }
        }

        if (needToSave) {
            saveProperties();
        }
    }

    @Override
    public void stop() throws Exception {
        ExecutorService executor = ServiceLocator.getService("ExecutorService", ExecutorService.class);
        executor.shutdownNow();
    }

    private static synchronized void saveProperties() {
        try {
            updaterProperties.store(new FileOutputStream(UPDATER_PROPERTIES_FILENAME), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return updaterProperties.getProperty(key);
    }

    public static synchronized void updateFieldInProperties(String key, String value) {
        updaterProperties.put(key, value);
        saveProperties();
    }

    public static synchronized void updateCoefficientProperties(double mainCoeff, double materialCoeff) {
        mainCoefficient = mainCoeff;
        materialCoefficient = materialCoeff;
        updaterProperties.put("mainCoefficient", "" + mainCoefficient);
        updaterProperties.put("materialCoefficient", "" + materialCoefficient);

        saveProperties();
    }
}
