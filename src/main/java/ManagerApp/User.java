/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ManagerApp;

import java.util.Arrays;

/**
 *
 * @author AORUS
 */
public class User {
    
    private char[] user = new char[20];
    private char[] password = new char[20];
    
    public User(char[] user, char[] password) {
        for (char u : user) {
            u = Character.toUpperCase(u);
        }
        this.user = user;
        this.password = password;
    }

    public void setUser(char[] user) {
        for (char u : user) {
            u = Character.toUpperCase(u);
        }
        this.user = user;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
    
    public boolean confirmUser(char[] user, char[] password) {
        for (char u : user) {
            u = Character.toUpperCase(u);
        }
        return Arrays.equals(this.user, user) && Arrays.equals(this.password, password);
    }
    
}
