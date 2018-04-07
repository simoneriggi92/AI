package it.polito.ai.servlets;

import it.polito.ai.utilities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lo StartServlet carica in memoria la lista degli utenti, leggendo dal file users.txt
 */
@WebServlet(urlPatterns = "/index")
public class StartServlet extends HttpServlet {
    public static ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();;

    @Override
    public void init() {

        // leggi da file
        String fileName = getServletContext().getRealPath(File.separator + "WEB-INF" + File.separator + "users.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // lanciare internal server error exception
        }
        try {
            String line = br.readLine();
            String[] tokens;
            while(line != null) {
                tokens = line.split(":");
                User user = new User(tokens[0], tokens[1]);
                users.put(user.getUsername(), user);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // forse va messo in init

        PrintWriter pw = response.getWriter();
        pw.println("<html><head></head><body>");
        pw.println("<a href=\"/login\"> Fai login </a>");
        pw.println("CIAO");
        pw.println("</body></html>");
    }
}





