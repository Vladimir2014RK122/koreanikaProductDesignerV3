package ru.koreanika.PortalClient.UserEventHandler;

import ru.koreanika.PortalClient.Authorization.Authorization;
import ru.koreanika.preferences.UserPreferences;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import org.json.simple.JSONObject;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.Main;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UserEventService {

    private static UserEventService userEventService;
    private final EventBus eventBus;

    public UserEventService(){
        eventBus = ServiceLocator.getService("EventBus", EventBus.class);
    }

    synchronized static public UserEventService getInstance() {
        if (userEventService == null) {
            userEventService = new UserEventService();
        }
        return userEventService;
    }

    public void sendEventRequest(JSONObject jsonObject){

        if(Authorization.getInstance().getUser() == null) return;

        String message = jsonObject.toString();

        UserEvent event = new UserEvent(Authorization.getInstance().getUser().getLogin(), message);
        try {
            sendEventRequest(event, Main.getProperty("server.host") + "/api/app/saveCalcActivity", UserPreferences.getInstance().getAccessToken());
        } catch (InterruptedException e) {
            System.err.println("check auth request CRASHES/ INTERRUPT EXCEPTION");
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "При авторизации произошла ошибка"));
        } catch (ExecutionException e) {
            System.err.println("check auth request CRASHES/ SERVER UNAVAILABLE");
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер Авторизации не отвечает"));
        }

    }

    public void sendEventRequest(UserEvent event, String uri,  String accessToken) throws InterruptedException, ExecutionException {

        //if(refreshToken == null) return;
        if(accessToken == null){ accessToken = "default";}


//        JSONObject obj = new JSONObject();
//        obj.put("message", event.getMessage());

        String payload = event.getMessage();

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
                            System.out.println("EVENT Message sent: " + event.getMessage());
                            System.out.println("Response content: " + response.getBodyText());
                        }else{

                            response.getCode();
                            System.out.println("EVENT Message did not send: " + event.message);
                            System.out.println("Response code: " + response.getCode());
                            System.out.println("Response content: " + response.getBodyText());
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
