package it.polito.ai.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/logout")
public class LogoutServlet  extends HttpServlet {

   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
       response.setContentType("text/html");

       //PrintWriter pw = response.getWriter();
       HttpSession session = request.getSession();
       session.invalidate();

//       pw.println("Logout effettuato!");
//       pw.close();
   }
}
