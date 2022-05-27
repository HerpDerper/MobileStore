package com.example.mobilestore.Models;

public class User {
    private String userSurname, userName, email, login, password, roleName, address, dateOfBirth, avatar;

    public User() {
    }

    public User(String userSurname, String userName, String email, String login, String password, String roleName, String address, String dateOfBirth, String avatar) {
        this.userSurname = userSurname;
        this.userName = userName;
        this.email = email;
        this.login = login;
        this.password = password;
        this.roleName = roleName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.avatar = avatar;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getAddress() {
        return address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAvatar() {
        return avatar;
    }
}