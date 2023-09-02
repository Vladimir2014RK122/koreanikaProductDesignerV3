package PortalClient.Authorization;

public class User {

    String login;
    String Company;
    String role;
    String email;

    public User(String login, String role) {
        this.login = login;
        this.role = role;
    }

    public User(String login, String company, String role, String email) {
        this.login = login;
        Company = company;
        this.role = role;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public String getCompany() {
        return Company;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }
}
