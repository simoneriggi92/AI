package it.polito.ai.utilities;

import javax.servlet.ServletContext;
import java.io.*;
import java.security.MessageDigest;
import java.util.LinkedList;

public class User implements IUser {
    private String username;
    private String password;
    private LinkedList<Position> positionList;

    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
        positionList = new LinkedList<Position>();
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LinkedList<Position> getPositionList() {
        return positionList;
    }

    public void setPositionList(LinkedList<Position> positionList) {
        this.positionList = positionList;
    }
}

