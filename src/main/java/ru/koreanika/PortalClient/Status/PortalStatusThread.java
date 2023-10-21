package ru.koreanika.PortalClient.Status;

import ru.koreanika.preferences.UserPreferences;
import javafx.beans.property.BooleanProperty;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PortalStatusThread extends Thread {

    BooleanProperty portalAvailable;
    String host;

    int portalUnavailableCounter = 0;
    protected PortalStatusThread(BooleanProperty portalAvailable, String host){

        this.portalAvailable = portalAvailable;
        this.host = host;
    }

    @Override
    public void run() {

        try{
            while (true){


                try{
                    sendSimpleTest();

                    sendAppPing();
                    portalAvailable.set(true);

                    portalUnavailableCounter = 0;
                }catch (IOException | ExecutionException e){

                    portalUnavailableCounter++;
                    if(portalUnavailableCounter >=3){
                        System.out.println("PORTAL UNAVAILABLE");
                        portalAvailable.set(false);
                    }

                }
                sleep(10000);
            }

        }catch (InterruptedException e){
            System.out.println("PortalStatusThread INTERRUPTED");
        }

        System.out.println("PortalStatusThread STOPPED");
    }

    private void sendSimpleTest() throws IOException {

        HttpResponse response = Request.get(host).execute().returnResponse();

    }

    private void sendAppPing() throws IOException, ExecutionException, InterruptedException {

        String accessToken = UserPreferences.getInstance().getAccessToken();

        if(accessToken == null){ accessToken = "default";}

        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5)).build();

        final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig).build();

        client.start();

        final SimpleHttpRequest request = SimpleRequestBuilder.get()
                .setHeader("Authorization", "Bearer " + accessToken)
                .setUri(host + "/api/pingAlive")
                .build();

        final Future<SimpleHttpResponse> future = client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                new FutureCallback<SimpleHttpResponse>() {

                    @Override
                    public void completed(final SimpleHttpResponse response) {

                        if(response.getCode() == 200){
                            System.out.println("PING sent");
                        }else{
                            System.out.println("PING did not send, response code: " + response.getCode());

                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        System.out.println(request + "->" + ex);
                    }

                    @Override
                    public void cancelled() {
                        System.out.println(request + " cancelled");
                    }

                });
        future.get();
    }
}
