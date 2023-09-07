package ru.koreanika.PortalClient.Authorization;

public record LoginValues(String login, String password) {

    @Override
    public String toString() {
        return "LoginValues{" +
                "login=" + login +
                ", password=" + password +
                '}';
    }

}
