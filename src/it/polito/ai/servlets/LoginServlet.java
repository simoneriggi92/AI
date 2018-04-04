package it.polito.ai.servlets;

import it.polito.ai.utilities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private User user = new User();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map<String, String> messages = new HashMap<String, String>();

        if(username == null || username.isEmpty()){
            messages.put("username", "Please enter username");
        }

        if(password == null || password.isEmpty()){
            messages.put("password", "Please enter password");
        }

        if(messages.isEmpty()){

            String md5_password = user.get_MD5_Password(password);
            boolean isPresent = user.findUser(username, md5_password);

            if(isPresent){
                String homeURI = request.getContextPath()+"/home";
//                HttpSession session = request.getSession();
//                session.setAttribute("user", username);
                //response.sendRedirect("home.jsp");
                request.getSession().setAttribute("user", username);
                response.sendRedirect(homeURI);
               // messages.put("ok", ""+request.getSession().getId());
                return;
            }
            else{
                messages.put("login", "Unknown login, please try again");
            }
        }
        //se arrivo qua significa che non mi sono loggato
        request.setAttribute("messages", messages);
//        PrintWriter out = response.getWriter();
//         out.print(messages);
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request,response);
    }
}
