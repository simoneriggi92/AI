package it.polito.ai.servlets;

import it.polito.ai.utilities.Position;
import it.polito.ai.utilities.PositionNotValidException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Il PositionServlet salva la posizione dell'utente.
 */

@WebServlet(urlPatterns = "/position")
public class PositionServlet extends HttpServlet {
    private Double latitude;
    private Double longitude;
    private Date timeStamp;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if(session != null) {

            try {
                // acquisisco latitudine e faccio verifica
                latitude = Double.parseDouble(request.getParameter("latitude"));
                if (latitude < -90 || latitude > 90) {
                    throw new PositionNotValidException();
                }
                // acquisisco longitudine e faccio verifica
                longitude = Double.parseDouble(request.getParameter("latitude"));
                if (longitude < -180 || longitude > 180) {
                    throw new PositionNotValidException();
                }
                // acquisisco data e faccio verifica
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
                timeStamp = formatter.parse(request.getParameter("date"));

                //if(LoginServlet.users.get(session.getAttribute("user")) == null) ;
                if (StartServlet.users.get(session.getAttribute("user")).getPositionList().isEmpty()) {
                    // salta prossime validazioni e vai direttamente al PositionServlet
                } else {
                    Position lastPosition = StartServlet.users.get(session.getAttribute("user")).getPositionList().getLast();
                    Date lastTemporalStamp = lastPosition.getTemporalStamp();

                    if (lastTemporalStamp.after(timeStamp)) {
                        throw new PositionNotValidException();
                    }
                    // verifico che la velocità sia < di 100 m/s
                    Double distance = Math.sqrt(Math.pow(latitude - lastPosition.getLatitude(), 2) + Math.pow(longitude - lastPosition.getLongitude(), 2));
                    Double intervalTime = (double) (timeStamp.getTime() - lastTemporalStamp.getTime());
                    Double speed = distance / intervalTime;

                    if (speed >= 100) {
                        throw new PositionNotValidException();
                    }
                }
            } catch (PositionNotValidException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/position");
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Position position = new Position(latitude, longitude, timeStamp);

            StartServlet.users.get(session.getAttribute("user")).getPositionList().add(position);

            PrintWriter pw = response.getWriter();
            pw.println("<html><head></head><body>");
            pw.println(session.getAttribute("user") + ", la tua posizione e' stata aggiunta correttamente!");
            pw.println("</body></html>");
        }
    }


}
