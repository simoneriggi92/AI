package it.polito.ai.filters;

import it.polito.ai.servlets.LoginServlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polito.ai.servlets.StartServlet;
import it.polito.ai.utilities.Position;
import it.polito.ai.utilities.PositionNotValidException;

/**
 * Il filtro verifica se la posizione inserita dall'utente è valida,
 * se è così lo indirizza verso il PositionServlet
 */
//@WebFilter("/position")
public class PositionFilter implements Filter {
    private Double latitude;
    private Double longitude;
    private Date timeStamp;

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        HttpSession session = request.getSession(false);

        //chain.doFilter(request, response);

        /*try {
            // acquisisco latitudine e faccio verifica
            latitude = Double.parseDouble(request.getParameter("latitude"));
            if (latitude < -90 || latitude > 90) {
                throw new PositionNotValidException();
            }
            // acquisisco longitudine e faccio verifica
            longitude = Double.parseDouble(request.getParameter("latitude"));
            if(longitude < -180 || longitude > 180) {
                throw new PositionNotValidException();
            }
            // acquisisco data e faccio verifica
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
            timeStamp = formatter.parse(request.getParameter("date"));

            //if(LoginServlet.users.get(session.getAttribute("user")) == null) ;
            if(StartServlet.users.get(session.getAttribute("user")).getPositionList().isEmpty()) {
                // salta prossime validazioni e vai direttamente al PositionServlet
            }
            else {
                Position lastPosition = StartServlet.users.get(session.getAttribute("user")).getPositionList().getLast();
                Date lastTemporalStamp = lastPosition.getTemporalStamp();

                if(lastTemporalStamp.after(timeStamp)) {
                    throw new PositionNotValidException();
                }
                // verifico che la velocità sia < di 100 m/s
                Double distance = Math.sqrt(Math.pow(latitude - lastPosition.getLatitude(), 2) + Math.pow(longitude - lastPosition.getLongitude(), 2));
                Double intervalTime = (double)(timeStamp.getTime() - lastTemporalStamp.getTime());
                Double speed = distance / intervalTime;

                if(speed >= 100) {
                    throw new PositionNotValidException();
                }
            }
        } catch (PositionNotValidException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/position");
        } catch(ParseException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }*/

        chain.doFilter(request, response);
    }
}
