package it.polito.ai.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Il filtro verifica se l'utente e' già loggato,
 * altrimenti lo indirizza verso il loginServlet
 */
@WebFilter("/*")
public class LoginFilter implements Filter {
    private String homePage;

    @Override
    public void init(FilterConfig config) throws ServletException {
        //recupero parametro dal web.xml
//        homePage = config.getInitParameter("HomePage");
//        if(homePage == null)
//            throw new ServletException("Init Parameter mancante: modificare web.xml");
//    }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        HttpSession session = request.getSession(false);    // non creare una sessione se non esiste
        String loginURI = request.getContextPath() + "/login";

        boolean loggedIn = session != null && session.getAttribute("user") != null;
        boolean loginRequest = request.getRequestURI().equals(loginURI);

        /* se l'utente è già loggato o è una richiesta di login, vai avanti nel servlet */
        if(loggedIn || loginRequest){
            chain.doFilter(request, response);
        }
        else{
           // PrintWriter out = response.getWriter();
           // out.println("Redirect alla pagina di login..");
            response.sendRedirect(loginURI);
        }

    }


}
