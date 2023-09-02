package PortalClient.Authorization;

public class LoginValues{

    private String login;
    private String password;

    public LoginValues(String login, String password){
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginValues{" +
                "login=" + login +
                ", password=" + password +
                '}';
    }
}
