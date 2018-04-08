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
import java.io.IOException;
import java.io.PrintWriter;
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
    /* quando l'utente scrive localhost:8080/login
     * gli ritorno la pagina di login */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // recupera la mappa dall'application context
        users = (ConcurrentHashMap<String, User>) this.getServletConfig().getServletContext().getAttribute("users");
        String md5_password = get_MD5_Password(password);

        if(users.get(username) != null
                && users.get(username).getUsername().equals(username)
                && users.get(username).getPassword().equals(md5_password)){

            session = request.getSession();
            session.setAttribute("user", username);

            // response.sendRedirect(request.getContextPath() + "/position");
            RequestDispatcher dispatcher;
            dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/home.jsp");
            dispatcher.forward(request, response);
        }
        else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
        // se arrivo qua significa che non mi sono loggato
        // PrintWriter out = response.getWriter();
        // out.print(messages);
        // request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
}

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

}



        /*
        Map<String, String> messages = new HashMap<String, String>();

        if(username == null || username.isEmpty()){
            messages.put("username", "Please enter username");
        }

        if(password == null || password.isEmpty()){
            messages.put("password", "Please enter password");
        }

        if(messages.isEmpty()){
        */



