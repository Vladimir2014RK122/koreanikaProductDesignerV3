package ru.koreanika.utils.Updater;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {


    private static final Image imageCloudAvailable = new Image(UpdateChecker.class.getResource("/styles/icons/icon_cloud_checked.png").toString());
    private static final Image imageCloudUnavailable = new Image(UpdateChecker.class.getResource("/styles/icons/icon_cloud_unavailable.png").toString());
    private static final Image imageCloudUpdateAvailable = new Image(UpdateChecker.class.getResource("/styles/icons/icon_cloud_update_available.png").toString());
    private static Image imageActiveCloud = imageCloudUnavailable;

    private static boolean showedAlertForUpdate = false;

    private static IntegerProperty updateProperty = new SimpleIntegerProperty();


    private static final long UPDATER_DELAY = 5000;


    private static SimpleBooleanProperty serverAvailableProperty = new SimpleBooleanProperty(false);

    private static Thread checkerThread = null;

    private static void createCheckerThread(){
        UpdateManager.getUpdateManager();//create it

        checkerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                int responseCode = 0;
                HttpURLConnection huc = null;
                System.err.println("UpdateChecker.checkerThread started");
                while (true) {

                    try {
                        URL url = new URL(UpdateManager.getProperties().getProperty("host"));

                        huc = (HttpURLConnection) url.openConnection();
                        //huc.setRequestMethod("HEAD");

                        responseCode = huc.getResponseCode();
                        System.err.println("OK");
                        serverAvailableProperty.set(true);

//                        UpdateManager.getBtnUpdate().fire();


                    } catch (RuntimeException ex) {
                        System.err.println("Illegal URI");
                        serverAvailableProperty.set(false);
                    } catch (IOException e) {
                        //e.printStackTrace();
                        System.err.println("NOT OK");
                        serverAvailableProperty.set(false);
                    }



                    try {
                        Thread.sleep(UPDATER_DELAY);
                    } catch (InterruptedException e) {
                        System.err.println("UpdateChecker.checkerThread interrupted");
                    }

                }
            }
        });
    }

    public static void startCheckUpdates() {

        createCheckerThread();
        serverAvailableProperty.addListener((observable, oldValue, newValue) -> {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String cloudIcon = "/styles/icons/icon_cloud_update_unavailable.png";
                    if (newValue.booleanValue()) {
                        if(UpdateManager.needToUpdate()){
                            updateProperty.set(1);
                        }else{
                            updateProperty.set(2);
                        }
                    } else {
                        updateProperty.set(0);
                    }
                }
            });

        });


        checkerThread.setDaemon(true);
        checkerThread.start();

        updateProperty.set(0);
    }

    public static void restartCheckerThread(){

        createCheckerThread();

        checkerThread.interrupt();
        checkerThread.setDaemon(true);
        checkerThread.start();
    }

    public static void setServerAvailableProperty(boolean serverAvailableProperty) {
        UpdateChecker.serverAvailableProperty.set(serverAvailableProperty);
    }

    public static IntegerProperty updateProperty() {
        return updateProperty;
    }
}
