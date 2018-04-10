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

//    public static void main(String [] args) throws FileNotFoundException, NoSuchElementException {
//    }
// }
//        it.polito.ai.utilities.User user = new it.polito.ai.utilities.User();
//        boolean isPresent = false;
//        try {
//        isPresent = user.findUser("smone", "1124");
//            System.out.println("Result: "+ isPresent);
//        }catch (FileNotFoundException e){
//            throw new FileNotFoundException("File users.txt not found");
//        } catch (NoSuchElementException e){
//            throw new NoSuchElementException("File terminato");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
