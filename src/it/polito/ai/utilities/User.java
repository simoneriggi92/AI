package it.polito.ai.utilities;

import java.io.*;
import java.security.MessageDigest;

public class User implements IUser{


    @Override
    public boolean findUser(String username, String password) throws IOException        {
        String fileName = "/home/simone/IdeaProjects/A1/web/WEB-INF/users.txt";
        BufferedReader br;
        br = new BufferedReader(new FileReader(fileName));
        try {
             String line = br.readLine();
             String[] tokens;
            while (line != null) {
                tokens = line.split(":");
                if(username.equals(tokens[0]) && password.equals(tokens[1]))
                    return true;
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
        return false;
    }

    @Override
    public String get_MD5_Password(String passwordToHash) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(passwordToHash.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

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
}
