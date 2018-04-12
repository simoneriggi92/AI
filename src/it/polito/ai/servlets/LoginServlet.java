package it.polito.ai.servlets;

import it.polito.ai.utilities.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Il LoginServlet gestisce il login dell'utente.
 */

@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    HttpSession session;
    ConcurrentHashMap<String, User> users;

    // Quando l'utente digita localhost:8080/login gli ritorno la pagina di login

/*
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
    }
*/

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        /*
         *  Controllo che i parametri passati dal client non siano nulli: se lo sono restituisco
         *  come codice di stato 400 BAD_REQUEST
         */
        if(request.getParameter("username") == null || request.getParameter("password")== null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.reset();
        }

        /*
         *  Recupero dall'application context la mappa contente la lista di utenti registrati.
         *  Questa contiene, per ogni utente le sue credenziali di accesso
         */
        users = (ConcurrentHashMap<String, User>) this.getServletConfig().getServletContext().getAttribute("users");
        String md5_password = password;

        /*
         *  Controllo se quel determinato utente è presente nella lista di utenti registrati
         *  e che i parametri passati dal client siano corretti confrontandoli con quelli
         *  presenti nella lista
         */
        if(users.get(username) != null
                && users.get(username).getUsername().equals(username)
                && users.get(username).getPassword().equals(md5_password)){

            session = request.getSession();
            session.setAttribute("user", username);

            RequestDispatcher dispatcher;
            dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/home.jsp");
            dispatcher.forward(request, response);
        }
        else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    public void init() {

        ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

        // Salvataggio nel contest applicativo della concurrentHashMap contenente la lista di utenti
        this.getServletConfig()
                .getServletContext()
                .setAttribute("users", users);

        // Lettura da file
        String fileName = getServletContext().getRealPath(File.separator + "WEB-INF" + File.separator + "users.txt");
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*
         *  Nel file users.txt sono memorizzati tutti gli utenti registrati (uno per ogni riga)
         *  con le relative credenziali d'accesso nel seguente formato:
         *                      username:password_md5
         *  Pertanto leggo il contenuto del file per righe e splitto ciascuna riga secondo il
         *  separatore ':' in modo da ottenere separatamente username e password_md5
         *  Questi due parametri vengono passati al costruttore per poter creare un oggetto User
         *  per ogni utente: tale oggetto User viene poi inserito nella lista al fine di popolarla
         */
        try {
            String line = br.readLine();
            String[] tokens;
            while (line != null) {
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
}