package it.polito.ai.servlets;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    ConcurrentHashMap<String, User> users;
    boolean positionNotValid = false;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la mappa dall'application context
        users = (ConcurrentHashMap<String, User>) this.getServletConfig().getServletContext().getAttribute("users");
        HttpSession session = request.getSession(false);
        if(session != null) {
            // verifico se la richiesta è di tipo json
            if(request.getContentType().equals("application/json")) {
                Position position = null;
                PrintWriter out = response.getWriter();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    // leggi l'oggetto json e prova a convertirlo in oggetto position
                    position = mapper.readValue(request.getReader(), Position.class);
                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.close();
                try {
                    // acquisisco latitudine e faccio verifica
                    if (position.getLatitude() < -90 || position.getLatitude() > 90) {
                        throw new PositionNotValidException();
                    }
                    // acquisisco longitudine e faccio verifica
                    if (position.getLongitude() < -180 || position.getLongitude() > 180) {
                        throw new PositionNotValidException();
                    }
                    // acquisisco data e faccio verifica
                    if (users.get(session.getAttribute("user")).getPositionList().isEmpty()) {
                        // salta prossime validazioni e vai direttamente al salvataggio nella mappa
                    } else {
                        Position lastPosition = users.get(session.getAttribute("user")).getPositionList().getLast();
                        Date lastTimeStamp = lastPosition.getTimeStamp();

                        if (lastTimeStamp.after(position.getTimeStamp())) {
                            throw new PositionNotValidException();
                        }
                        // verifico che la velocità sia < di 100 m/s
                        Double distance = Math.sqrt(Math.pow(position.getLatitude()- lastPosition.getLatitude(), 2) + Math.pow(position.getLongitude() - lastPosition.getLongitude(), 2));
                        Double intervalTime = (double) (position.getTimeStamp().getTime()- lastTimeStamp.getTime());
                        Double speed = distance / intervalTime;

                        if (speed >= 100) {
                            throw new PositionNotValidException();
                        }
                    }
                } catch (PositionNotValidException e) {
                    e.printStackTrace();
                    //response.sendRedirect(request.getContextPath() + "/position");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("<html><body><p> Error inserting position data </p></body></html>" + HttpServletResponse.SC_BAD_REQUEST);
                    positionNotValid = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!positionNotValid) {
                    // salva nella mappa
                    users.get(session.getAttribute("user")).getPositionList().add(position);
                    // salva la mappa nell'application context
                    //this.getServletConfig().getServletContext().setAttribute("users", users);

                    PrintWriter pw = response.getWriter();
                    pw.println("<html><head></head><body>");
                    pw.println(session.getAttribute("user") + ", la tua posizione e' stata aggiunta correttamente!");
                    pw.println(users.get("wolly").getPositionList().getLast().getTimeStamp().toString());
                    pw.println("</body></html>");
                }
            }
            else {
                // la richiesta non è di tipo json
                try {
                    // acquisisco latitudine e faccio verifica
                    latitude = Double.parseDouble(request.getParameter("latitude"));
                    if (latitude < -90 || latitude > 90) {
                        throw new PositionNotValidException();
                    }
                    // acquisisco longitudine e faccio verifica
                    longitude = Double.parseDouble(request.getParameter("longitude"));
                    if (longitude < -180 || longitude > 180) {
                        throw new PositionNotValidException();
                    }
                    // acquisisco data e faccio verifica
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    timeStamp = sdf.parse(request.getParameter("date"));
                    if (users.get(session.getAttribute("user")).getPositionList().isEmpty()) {
                        // salta prossime validazioni e vai direttamente al salvataggio nella mappa
                    } else {
                        Position lastPosition = users.get(session.getAttribute("user")).getPositionList().getLast();
                        Date lastTimeStamp = lastPosition.getTimeStamp();

                        if (lastTimeStamp.after(timeStamp)) {
                            throw new PositionNotValidException();
                        }
                        // verifico che la velocità sia < di 100 m/s
                        Double distance = Math.sqrt(Math.pow(latitude - lastPosition.getLatitude(), 2) + Math.pow(longitude - lastPosition.getLongitude(), 2));
                        Double intervalTime = (double) (timeStamp.getTime() - lastTimeStamp.getTime());
                        Double speed = distance / intervalTime;

                        if (speed >= 100) {
                            throw new PositionNotValidException();
                        }
                    }
                } catch (PositionNotValidException e) {
                    e.printStackTrace();
                    //response.sendRedirect(request.getContextPath() + "/position");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("<html><body><p> Error inserting position data </p></body></html>" + HttpServletResponse.SC_BAD_REQUEST);
                    positionNotValid = true;
                } catch (ParseException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!positionNotValid) {
                    Position position = new Position(latitude, longitude, timeStamp);

                    // salva nella mappa
                    users.get(session.getAttribute("user")).getPositionList().add(position);
                    // salva la mappa nell'application context
                    //this.getServletConfig().getServletContext().setAttribute("users", users);

                    PrintWriter pw = response.getWriter();
                    pw.println("<html><head></head><body>");
                    pw.println(session.getAttribute("user") + ", la tua posizione e' stata aggiunta correttamente!");
                    pw.println(users.get("wolly").getPositionList().getLast().getTimeStamp().toString());
                    pw.println("</body></html>");
                }
            }
        }
    }


}
