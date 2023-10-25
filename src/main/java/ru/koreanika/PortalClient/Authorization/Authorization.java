package ru.koreanika.PortalClient.Authorization;

import ru.koreanika.project.ProjectHandler;
import ru.koreanika.utils.UserPreferences;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.utils.InfoMessage;
import ru.koreanika.utils.Main;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Authorization {

    private final EventBus eventBus;
    private final ProjectHandler projectHandler;

    LoginWindow loginWindow = LoginWindow.getInstance();

    String serverHost = Main.getProperty("server.host");

    private BooleanProperty accessPermitted = new SimpleBooleanProperty(false);

    private static Authorization instance;

    private User user = null;

    private Set<AppType> availableAppTypes = new LinkedHashSet<>();


    private Authorization() {
        eventBus = ServiceLocator.getService("EventBus", EventBus.class);
        projectHandler = ServiceLocator.getService("ProjectHandler", ProjectHandler.class);

        loginWindow.setOnLoginClicked(actionEvent -> {
            //accessPermitted.set(true);
            System.out.println(loginWindow.getLoginValues());
            try {
                sendLoginRequest(loginWindow.getLoginValues().login(), loginWindow.getLoginValues().password());
            } catch (InterruptedException e) {
                System.err.println("Login request CRASHES/ INTERRUPT EXCEPTION");
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "При авторизации произошла ошибка"));
            } catch (ExecutionException e) {
                System.err.println("Login request CRASHES/ SERVER UNAVAILABLE");
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер Авторизации не отвечает"));
            }
        });

        loginWindow.setOnCloseClicked(actionEvent -> {

            if (!accessPermitted.get()) {
//                ((Stage)(Main.getMainScene().getWindow())).getOnCloseRequest().handle();
//                ((Stage)(Main.getMainScene().getWindow())).close();


                InfoMessage.stopMessage();


                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.initOwner(Main.getMainScene().getWindow());
                alert.initModality(Modality.WINDOW_MODAL);

                alert.setTitle("Выйти из приложения?");
                alert.setHeaderText("Вы уверены, что хотите выйти из приложения?");
                alert.setContentText("Сохранить перед выходом?");

                ButtonType buttonTypeNo = new ButtonType("Не сохранять");
                ButtonType buttonTypeYes = new ButtonType("Сохранить");
                ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeNo, buttonTypeYes, buttonTypeCancel);

                if (!projectHandler.projectSelected()) {
                    loginWindow.close();
                    ((Stage) (Main.getMainScene().getWindow())).close();
                    //event.consume();
                    return;
                }
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeNo) {
                    // ... user chose "NO"
                    loginWindow.close();
                    ((Stage) (Main.getMainScene().getWindow())).close();
                } else if (result.get() == buttonTypeYes) {
                    // ... user chose "YES"
                    projectHandler.saveProject();
                    loginWindow.close();
                    ((Stage) (Main.getMainScene().getWindow())).close();
                } else if (result.get() == buttonTypeCancel) {
                    // ... user chose "Three"
                    System.out.println("CANCEL");
                }
            } else {
                loginWindow.close();
            }

        });

    }


    private void setAvailableAppTypes(Set<AppType> newSet) {
        availableAppTypes.clear();
        availableAppTypes.addAll(newSet);
        System.out.println("Availables Apps = " + availableAppTypes);

        if (!availableAppTypes.contains(UserPreferences.getInstance().getSelectedApp())) {
            UserPreferences.getInstance().saveSelectedApp(availableAppTypes.stream().toList().get(0));
        }
        System.out.println("SELECTED APP = " + UserPreferences.getInstance().getSelectedApp());
    }


    public List<AppType> getAvailableAppTypes() {
        return availableAppTypes.stream().toList();
    }

    public static Authorization getInstance() {
        if (instance == null) {
            instance = new Authorization();
        }
        return instance;
    }

    public void startApp() {

        //send request and if token wrong:

        CheckAuthThread checkAuthThread = new CheckAuthThread();
        checkAuthThread.setDaemon(true);
        checkAuthThread.start();
    }


    private void sendLoginRequest(String login, String password) throws InterruptedException, ExecutionException {

        JSONObject obj = new JSONObject();
        obj.put("login", login);
        obj.put("password", password);

        String payload = obj.toString();

        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5)).build();

        final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig).build();

        client.start();

        final SimpleHttpRequest request = SimpleRequestBuilder.post()
                .setUri(Main.getProperty("server.host") + "/auth")
                .setBody(payload, ContentType.APPLICATION_JSON)
                .build();

        System.out.println("Executing request " + request);

        final Future<SimpleHttpResponse> future = client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                new FutureCallback<SimpleHttpResponse>() {

                    @Override
                    public void completed(final SimpleHttpResponse response) {

                        if (response.getCode() == 200) {

                            try {
                                JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.getBody().getBodyText());
                                String accessToken = (String) jsonObject.get("accessToken");
                                String refreshToken = (String) jsonObject.get("refreshToken");
                                System.out.println("accessToken = " + accessToken);
                                System.out.println("refreshToken = " + refreshToken);
                                UserPreferences.getInstance().saveAccessToken(accessToken);
                                UserPreferences.getInstance().saveRefreshToken(refreshToken);

//                                    String userLogin = (String)jsonObject.get("login");
//                                    String userRole = (String)jsonObject.get("roleName");
//                                    setUser(userLogin, userRole);

                                Authorization.getInstance().sendAccessRequest();

                                accessPermitted.set(true);
                                Platform.runLater(() -> {
                                    closeLoginWindow();
                                });
                            } catch (ParseException e) {
                                System.err.println("Cant parse Auth response");
                                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер авторизации ответил некорректно"));
                            }
                        } else {
                            System.out.println(request + "->" + new StatusLine(response));
                            System.err.println("Response body: " + response.getBody().getBodyText());
                            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Неверный логин или пароль"));
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


    private void sendAccessRequest(String uri, String accessToken) throws InterruptedException, ExecutionException {

//        if(accessToken == null){
//            accessPermitted.set(false);
//            showLoginWindow();
//            return;
//        }
        if (accessToken == null) {
            accessToken = "default";
        }


        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5)).build();

        final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig).build();

        client.start();

        final SimpleHttpRequest request = SimpleRequestBuilder.get()
                .setHeader("Authorization", "Bearer " + accessToken)
                .setUri(uri)
                .build();

        System.out.println("Executing request " + request);

        final Future<SimpleHttpResponse> future = client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                new FutureCallback<SimpleHttpResponse>() {

                    @Override
                    public void completed(final SimpleHttpResponse response) {

                        if (response.getCode() == 200) {

                            try {
                                JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.getBody().getBodyText());
                                System.out.println("payload = " + jsonObject);

                                String userLogin = (String) jsonObject.get("login");
                                String userRole = (String) jsonObject.get("roleName");
                                setUser(userLogin, userRole);


                                System.out.println("\r\n ********************************************************");
                                System.out.println("-=ACCESS PERMITTED=-");

                                Set apps = new LinkedHashSet();
                                for (String appShortName : ((String) jsonObject.get("appAccess")).split(",")) {
                                    apps.add(AppType.getByShortName(appShortName));
                                }

                                setAvailableAppTypes(apps);


                                System.out.println("********************************************************\r\n");

                                accessPermitted.set(true);
                                Platform.runLater(() -> {
                                    closeLoginWindow();
                                });
                            } catch (ParseException e) {
                                System.err.println("Cant parse " + uri + " response");
                                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер авторизации ответил некорректно"));
                            }
                        } else {
                            System.out.println(request + "->" + new StatusLine(response));
                            System.err.println("Response body: " + response.getBody().getBodyText());
                            sendUpdateAccessTokenRequest();
//                            accessPermitted.set(false);
//
//                            System.out.println("-=ACCESS DENIED=-");
//                            showLoginWindow();
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

    private void sendUpdateAccessTokenRequest(String uri, String accessToken, String refreshToken) throws InterruptedException, ExecutionException {

        //if(refreshToken == null) return;
        if (accessToken == null) {
            accessToken = "default";
        }
        if (refreshToken == null) {
            refreshToken = "default";
        }

        JSONObject obj = new JSONObject();
        obj.put("refreshToken", refreshToken);

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

                        if (response.getCode() == 200) {

                            try {
                                JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.getBody().getBodyText());
                                String accessToken = (String) jsonObject.get("accessToken");
                                String refreshToken = (String) jsonObject.get("refreshToken");

                                if (accessToken == null) {
                                    accessToken = "default";
                                }
                                if (refreshToken == null) {
                                    refreshToken = "default";
                                }

                                UserPreferences.getInstance().saveAccessToken(accessToken);

                                System.out.println("accessToken = " + accessToken);
                                System.out.println("refreshToken = " + refreshToken);

                                if (accessToken == null || accessToken.equals("null") || accessToken.equals("default")) {
                                    accessPermitted.set(false);
                                    System.out.println("-=ACCESS DENIED=-");
                                    showLoginWindow();

                                    System.out.println("-=FAILED REFRESH ACCESS TOKEN=-");
                                } else {
                                    System.out.println("-=SUCCESS REFRESH ACCESS TOKEN=-");
                                    accessPermitted.set(true);
                                    sendAccessRequest();//for update user name

                                }

                            } catch (ParseException e) {
                                System.err.println("Cant parse " + uri + " response");
                                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер авторизации ответил некорректно"));
                            }

                        } else {
                            System.out.println("-=FAILED REFRESH ACCESS TOKEN=-");
                            System.out.println(request + "->" + new StatusLine(response));
                            //System.out.println("refreshToken = " + refreshToken);
                            System.out.println(request.getBody().getBodyText());
                            System.err.println("Response body: " + response.getBody().getBodyText());

                            accessPermitted.set(false);
                            System.out.println("-=ACCESS DENIED=-");
                            showLoginWindow();
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

    private void sendUpdateRefreshTokenRequest(String uri, String accessToken, String refreshToken) throws InterruptedException, ExecutionException {

        if (accessToken == null) {
            accessToken = "default";
        }
        if (refreshToken == null) {
            refreshToken = "default";
        }

        JSONObject obj = new JSONObject();
        obj.put("refreshToken", refreshToken);

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

                        if (response.getCode() == 200 && response.getContentType() == ContentType.APPLICATION_JSON) {

                            try {
                                JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.getBody().getBodyText());
                                String accessToken = (String) jsonObject.get("accessToken");
                                String refreshToken = (String) jsonObject.get("refreshToken");
                                System.out.println("accessToken = " + accessToken);
                                System.out.println("refreshToken = " + refreshToken);
                                UserPreferences.getInstance().saveAccessToken(accessToken);
                                UserPreferences.getInstance().saveRefreshToken(refreshToken);

                                System.out.println("-=SUCCESS REFRESH REFRESH TOKEN=-");

                            } catch (ParseException e) {
                                System.err.println("Cant parse " + uri + " response");
                                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер авторизации ответил некорректно"));
                            }

                        } else {
                            System.out.println("-=FAILED REFRESH REFRESH TOKEN=-");
                            System.out.println(request + "->" + new StatusLine(response));
                            //System.out.println("refreshToken = " + refreshToken);
                            System.out.println(request.getBody().getBodyText());
                            System.err.println("Response body: " + response.getBody().getBodyText());
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


    int accessFailCounter = 0;

    public void sendAccessRequest() {

        try {
            if (UserPreferences.getInstance().getAccessToken() != null) {
                sendAccessRequest(
                        Main.getProperty("server.host") + "/me",
                        UserPreferences.getInstance().getAccessToken());

                accessFailCounter = 0;
            } else {

                accessFailCounter++;


                if (accessFailCounter >= 3) {
                    accessPermitted.set(false);
                    showLoginWindow();
                }
            }


        } catch (InterruptedException e) {
            System.err.println("check auth request CRASHES/ INTERRUPT EXCEPTION");
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "При авторизации произошла ошибка"));
        } catch (ExecutionException e) {
            System.err.println("check auth request CRASHES/ SERVER UNAVAILABLE");
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер Авторизации не отвечает"));
        }
    }

    public void sendUpdateAccessTokenRequest() {
        try {
            sendUpdateAccessTokenRequest(
                    Main.getProperty("server.host") + "/token",
                    UserPreferences.getInstance().getAccessToken(),
                    UserPreferences.getInstance().getRefreshToken()
            );
        } catch (InterruptedException e) {
            System.err.println("check refresh token request CRASHES/ INTERRUPT EXCEPTION");
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "При обновлении токена произошла ошибка"));
        } catch (ExecutionException e) {
            System.err.println("check refresh token request CRASHES/ SERVER UNAVAILABLE");
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер Авторизации не отвечает"));
        }
    }

    public void sendUpdateRefreshTokenRequest() {
        try {
            if (UserPreferences.getInstance().getAccessToken() != null) {
                sendUpdateRefreshTokenRequest(
                        Main.getProperty("server.host") + "/api/refreshToken",
                        UserPreferences.getInstance().getAccessToken(),
                        UserPreferences.getInstance().getRefreshToken()
                );
            }
        } catch (InterruptedException e) {
            System.err.println("check refresh token request CRASHES/ INTERRUPT EXCEPTION");
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "При обновлении токена произошла ошибка"));
        } catch (ExecutionException e) {
            System.err.println("check refresh token request CRASHES/ SERVER UNAVAILABLE");
            eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Сервер Авторизации не отвечает"));
        }
    }

    public void logout() {
        UserPreferences.getInstance().removeAccessToken();
        UserPreferences.getInstance().removeRefreshToken();
        accessPermitted.set(false);
        showLoginWindow();

        clearUser();
    }


    public void showLoginWindow() {
        clearUser();

        Platform.runLater(() -> LoginWindow.getInstance().show(Main.getMainScene()));
    }

    public void closeLoginWindow() {
        LoginWindow.getInstance().close();
    }

    public boolean isAccessPermitted() {
        return accessPermitted.get();
    }

    public BooleanProperty accessPermittedProperty() {
        return accessPermitted;
    }

    public void setUser(String login, String role) {
        System.out.println("SET USER = " + login);

        boolean sendEvent = false;
        if (user == null) {
            sendEvent = true;
        }

        user = new User(login, role);

        Platform.runLater(() -> {
            //MainWindow.setUser(user);
            Main.getMainWindowDecorator().setUserName(user.getLogin() + "/" + UserPreferences.getInstance().getSelectedApp().getShortName());
        });


        if (sendEvent) {
            //JSONObject jsonObject = new JSONObject();
            //jsonObject.put("type", "app started");
            //UserEventService.getInstance().sendEventRequest(jsonObject);
        }

    }

    public void clearUser() {
        user = null;

        Platform.runLater(() -> {
            Main.getMainWindowDecorator().setUserName("none");
        });


        //JSONObject jsonObject = new JSONObject();
        //jsonObject.put("type", "app stopped");
        //UserEventService.getInstance().sendEventRequest(jsonObject);
    }

    public User getUser() {
        return user;
    }
}

