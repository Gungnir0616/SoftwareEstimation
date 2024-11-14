package com.example.back.entity;

public class User {
    private String password;
    private String userName;
    private String phone;
    private String mailbox;
    private boolean gender;
    private String role;//所属角色 超级管理员？
    private String department;
    public User() {}
    public User(String userName, String phone, String password) {
        this.userName = userName;
        this.phone = phone;
        this.password = password;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public String getRole() {
        return role;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getMailbox() {
        return mailbox;
    }

    public boolean isGender() {
        return gender;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserName() {
        return userName;
    }
}
