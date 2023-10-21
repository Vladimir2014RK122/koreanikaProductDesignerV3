package ru.koreanika.PortalClient.Maintenance;

import ru.koreanika.preferences.UserPreferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import org.json.simple.JSONObject;
import ru.koreanika.utils.Main;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MaintenanceMessage {

    private static final long TIMEOUT_BETWEEN_MESSAGES = 15000;

    private static Date lastMessageDate;

    String message = "";
//    POST /api/claims/saveClaim

    private static BooleanProperty readyToSend = new SimpleBooleanProperty(true);

    private ResultCallBacks resultCallBacks;

    public MaintenanceMessage(String message){
        this.message = message;
    }

    public void setResultCallBacks(ResultCallBacks resultCallBacks) {
        this.resultCallBacks = resultCallBacks;
    }


    public static BooleanProperty readyToSendProperty() {
        return readyToSend;
    }

    public void sendMessageToPortal(ClimeType climeType) throws ExecutionException, InterruptedException {

        //if it will be a lot of errors it will be crash server, so it possible send one message in 15 sec:
        if(!readyToSend.get()) return;

        String uri = Main.getProperty("server.host") + "/api/claims/saveClaim";
        String accessToken = UserPreferences.getInstance().getAccessToken();

        if(accessToken == null){ accessToken = "default";}

        JSONObject obj = new JSONObject();
        obj.put("claimType", climeType.getName());
        obj.put("claimText", this.message);
        String payload = obj.toString();

        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5)).build();

        final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig).build();

        client.start();

        final SimpleHttpRequest request = SimpleRequestBuilder.post()
                .setHeader("Authorization", "Bearer " + accessToken)
                .setUri(uri)
                .setBody(payload, ContentType.APPLICATION_JSON)
                .build();

        System.out.println("Executing request " + request);

        final Future<SimpleHttpResponse> future = client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                new FutureCallback<SimpleHttpResponse>() {

                    @Override
                    public void completed(final SimpleHttpResponse response) {

                        if(response.getCode() == 200){
                            System.out.println("MAINTANCE Message sent: ");
                            System.out.println("Response content: " + response.getBodyText());
                            if(resultCallBacks != null) resultCallBacks.success();
//                            MainWindow.showInfoMessage(InfoMessage.MessageType.SUCCESS, "Ваше сообщение отправлено!");
                        }else{

                            response.getCode();
                            System.out.println("MAINTANCE Message did not send: ");
                            System.out.println("Response code: " + response.getCode());
                            System.out.println("Response content: " + response.getBodyText());
                            if(resultCallBacks != null) resultCallBacks.failed();
//                            MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Ваше сообщение НЕ отправлено!");

                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        System.out.println(request + "->" + ex);
                        if(resultCallBacks != null) resultCallBacks.failed();
//                        MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Ваше сообщение НЕ отправлено!");
                    }

                    @Override
                    public void cancelled() {
                        System.out.println(request + " cancelled");
                        if(resultCallBacks != null) resultCallBacks.failed();
//                        MainWindow.showInfoMessage(InfoMessage.MessageType.ERROR, "Ваше сообщение НЕ отправлено!");
                    }

                });
        future.get();

        Thread thread = new Thread(()->{
            readyToSend.set(false);

            try {
                Thread.sleep(TIMEOUT_BETWEEN_MESSAGES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            readyToSend.set(true);

        });
        thread.setDaemon(true);
        thread.start();
    }

    public interface ResultCallBacks{

        void success();
        void failed();
    }
}
