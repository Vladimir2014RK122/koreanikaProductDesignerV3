package PortalClient.Update;

import PortalClient.Authorization.Authorization;
import PortalClient.PortalURI;
import PortalClient.Status.PortalStatus;
import Preferences.UserPreferences;
import javafx.application.Platform;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.concurrent.CallbackContribution;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.concurrent.FutureContribution;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.nio.AsyncEntityConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.http.nio.support.BasicResponseConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.support.BasicRequestBuilder;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.InfoMessage;
import utils.Main;
import utils.MainWindow;
import utils.Updater.UpdateChecker;
import utils.Updater.UpdateManager;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UpdateService {

    private static UpdateService updateService;

    private AvailableVersionThread availableVersionThread;

    private String availableVersion = "";
    private String availableFile = "";

    private UpdateService(){}

    public synchronized static UpdateService getInstance(){
        if(updateService == null){
            updateService = new UpdateService();
        }
        return updateService;
    }


    public String getAvailableVersion() {
        return availableVersion;
    }

    public boolean downloadUpdateFile() throws FileNotFoundException {

        Platform.runLater(()->{UpdateManager.getUpdateManager().disableControls(true);});

        if(!PortalStatus.getInstance().isPortalAvailable()) return false;
        if(!Authorization.getInstance().isAccessPermitted()) return false;

        String accessToken = UserPreferences.getInstance().getAccessToken();
        String host = Main.getProperty("server.host");
        if(accessToken == null){ accessToken = "default";}

        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5)).build();

        final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig).build();


        client.start();

        final SimpleHttpRequest request = SimpleRequestBuilder.get()
                .setHeader("Authorization", "Bearer " + accessToken)
                .setUri(host + PortalURI.PORTAL_URI_DOWNLOAD_UPDATE + availableFile)
                .build();

        System.out.println("Executing request " + request);
        Platform.runLater(()->{
//            UpdateManager.getUpdateManager().getLabelProgress().setText("Загрузка обновления...");
//            UpdateManager.getUpdateManager().getProgressBar().setProgress(0.3);
            UpdateManager.getUpdateManager().getProgressIndicator().setVisible(true);
            UpdateManager.getUpdateManager().getProgressIndicator().setProgress(0.3);
        });

        File myFile = new File("newUpdate.zip");

        final Future<SimpleHttpResponse> future = client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                new FutureCallback<SimpleHttpResponse>() {

                    @Override
                    public void completed(final SimpleHttpResponse response) {

                        try {
                            System.out.println(response.getHeader("Content-Length"));

                            FileOutputStream outstream = new FileOutputStream(myFile);
                            outstream.write(response.getBodyBytes());
                            outstream.close();
                            System.out.println("receive FILE-----------");

                            startUpdateProcedure();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        }

//                        if(response.getCode() == 200){
//                            portalResponseHandler.ResponseSuccess(request, response);
//                        }else{
//
//                            portalResponseHandler.ResponseFail(request, response);
//                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        System.out.println("error while downloading file");
                        System.out.println(request + "->" + ex);
                    }

                    @Override
                    public void cancelled() {
                        System.out.println(request + " cancelled");
                    }

                }
                );

        try {
            future.get();
            System.out.println("Shutting down");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "В процессе загрузки обновления связь с сервером потеряна.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void startUpdateProcedure() throws IOException {

        Platform.runLater(()->{
//            UpdateManager.getUpdateManager().getLabelProgress().setText("Подготовка обновления...");
//            UpdateManager.getUpdateManager().getProgressBar().setProgress(0.6);
            UpdateManager.getUpdateManager().getProgressIndicator().setProgress(0.6);
        });

        unzip("newUpdate.zip", "newUpdate");

        System.out.println("***********Unzip FINISHED");

        Platform.runLater(()->{
//            UpdateManager.getUpdateManager().getLabelProgress().setText("Завершено");
//            UpdateManager.getUpdateManager().getProgressBar().setProgress(1);
            UpdateManager.getUpdateManager().getProgressIndicator().setProgress(1);
            UpdateManager.getUpdateManager().disableControls(false);
        });

        Runtime runtime = Runtime.getRuntime();
        //runtime.exec("del newUpdate.zip");
        new File("newUpdate.zip").delete();

        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File("newUpdate/update.exe"));

        Platform.runLater(()->{
            UpdateManager.getUpdateManager().disableControls(false);
            ((Stage)(Main.getMainScene().getWindow())).close();
        });

    }

    private  void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
//                String fileName = ze.getName();
                String fileName = "update.exe";
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("***********Unzipping to " + newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startAvailableVersionThread(){

        if(availableVersionThread != null && availableVersionThread.isAlive()){
            availableVersionThread.interrupt();
        }

        availableVersionThread = new AvailableVersionThread(new PortalResponseHandler() {
            @Override
            public void ResponseSuccess(SimpleHttpRequest request, SimpleHttpResponse response) {

                availableUpdateResponseSuccess(request, response);


            }

            @Override
            public void ResponseFail(SimpleHttpRequest request, SimpleHttpResponse response) {

                System.out.println(request + "->" + new StatusLine(response));
                System.err.println("Response body: " + response.getBody().getBodyText());
            }
        });


        availableVersionThread.setDaemon(true);
        availableVersionThread.start();

    }

    private void availableUpdateResponseSuccess(SimpleHttpRequest request, SimpleHttpResponse response){

        try {
//            System.out.println(" response.getBody().getBodyText() = " + response.getBody().getBodyText());
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.getBody().getBodyText());


            availableVersion = ((String)jsonObject.get("name")).split("KPD_")[1].split("\\.zip")[0];
            availableFile = (String)jsonObject.get("name");
//            System.out.println(" Last available file = " + availableFile);
//            System.out.println(" Last available Version = " + availableVersion);

            Platform.runLater(()->{
                UpdateManager.getUpdateManager().setAvailableVersion(availableVersion);

                if(availableVersion.equals(Main.appVersion)){
                    UpdateChecker.updateProperty().set(1);
//                    MainWindow.getLabelUpdateServerStatus().setText("В сети");
//                    MainWindow.getLabelUpdateServerStatus().setStyle("-fx-text-fill: green;");
                    //imageActiveCloud = imageCloudAvailable;
                }else{
                    UpdateChecker.updateProperty().set(2);
//                    MainWindow.getLabelUpdateServerStatus().setText("Доступно обновление");
//                    MainWindow.getLabelUpdateServerStatus().setStyle("-fx-text-fill: orange;");
                    //imageActiveCloud = imageCloudAvailable;


                    if(!UpdateManager.isShowedAlertForUpdate()){
                        UpdateManager.show(Main.getMainScene());
                        UpdateManager.setShowedAlertForUpdate(true);
                    }
                }
            });


        } catch (ParseException e) {
            System.err.println("Cant parse "+ request.getRequestUri() + " response");
//            Platform.runLater(()->{
//                MainWindow.showInfoMessage(
//                        InfoMessage.MessageType.ERROR,
//                        "Портал ответил некорректно");
//            });
        }
    }

}
