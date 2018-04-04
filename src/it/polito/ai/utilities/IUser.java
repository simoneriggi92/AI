package it.polito.ai.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface IUser {
    public boolean findUser(String username, String password) throws IOException;
    public String get_MD5_Password(String passwordToHash) throws IOException;
}

