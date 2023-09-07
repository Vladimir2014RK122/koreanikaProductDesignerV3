package ru.koreanika.PortalClient.UserEventHandler;

public class UserEvent {

    String userLogin;

    String message;

    public UserEvent(String userLogin, String message) {
        this.userLogin = userLogin;
        this.message = message;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getMessage() {
        return message;
    }
}

/*
* Endpoint: /saveCalculatorActivity
BodyJson:
{
    "activityMessage": "YO! ITS ACTIVITY"
}
* */
