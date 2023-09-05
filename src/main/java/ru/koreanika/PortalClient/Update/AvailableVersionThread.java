package ru.koreanika.PortalClient.Update;

import ru.koreanika.PortalClient.Authorization.Authorization;
import ru.koreanika.PortalClient.PortalURI;
import ru.koreanika.PortalClient.Status.PortalStatus;
import ru.koreanika.Preferences.UserPreferences;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import ru.koreanika.utils.Main;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AvailableVersionThread extends Thread{

    PortalResponseHandler portalResponseHandler;
    public AvailableVersionThread( PortalResponseHandler portalResponseHandler){
        this.portalResponseHandler = portalResponseHandler;
    }

    @Override
    public void run() {
        System.out.println("AvailableVersionThread STARTED");

        while(true){


            try {

                if(PortalStatus.getInstance().isPortalAvailable()) getAvailableAppVersion(portalResponseHandler);



            } catch (ExecutionException | InterruptedException e) {
                System.err.println("AvailableVersionThread  EXCEPTION!!! CANT GET AVAILABLE VERSION!!!");
//                e.printStackTrace();
            }

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("AvailableVersionThread INTERRUPTED");
                break;
            }


        }
        System.out.println("AvailableVersionThread STOPPED");
    }



    private void getAvailableAppVersion(PortalResponseHandler portalResponseHandler) throws ExecutionException, InterruptedException {

        if(!PortalStatus.getInstance().isPortalAvailable()) return;
        if(!Authorization.getInstance().isAccessPermitted()) return;

        String accessToken = UserPreferences.getInstance().getAccessToken();
        String host = Main.getProperty("server.host");
//        String uri = PORTAL_URI_LAST_UPDATE;


        if(accessToken == null){ accessToken = "default";}


        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5)).build();

        final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig).build();

        client.start();

        final SimpleHttpRequest request = SimpleRequestBuilder.get()
                .setHeader("Authorization", "Bearer " + accessToken)
//                .setUri(host + PortalURI.PORTAL_URI_LAST_UPDATE)
                .setUri(host + PortalURI.PORTAL_URI_LAST_UPDATE_FOR_CLIENT + UserPreferences.getInstance().getSelectedApp().getShortName())
                .build();

//        System.out.println("Executing request " + request);

        final Future<SimpleHttpResponse> future = client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                new FutureCallback<SimpleHttpResponse>() {

                    @Override
                    public void completed(final SimpleHttpResponse response) {

                        if(response.getCode() == 200){
                            portalResponseHandler.ResponseSuccess(request, response);
                        }else{

                            portalResponseHandler.ResponseFail(request, response);
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
