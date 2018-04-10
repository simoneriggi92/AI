package it.polito.ai.servlets;


import com.google.gson.Gson;
import it.polito.ai.utilities.GeoFunction;
import it.polito.ai.utilities.Position;
import it.polito.ai.utilities.PositionNotValidException;
import it.polito.ai.utilities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Il PositionServlet salva la posizione dell'utente.
 */

@WebServlet(urlPatterns = "/position")
public class PositionServlet extends HttpServlet {
    private Double latitude;
    private Double longitude;
    private long timeStamp;
    ConcurrentHashMap<String, User> users;
    ArrayList<Position> candidatePositionsList = new ArrayList<>();
    boolean allPositionsValid = false;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la mappa degli utenti dall'application context
        users = (ConcurrentHashMap<String, User>) this.getServletConfig().getServletContext().getAttribute("users");
        // recupera la sessione corrente
        HttpSession session = request.getSession(false);
        if(session != null) {
            // verifico se la richiesta è di tipo json
            if(request.getContentType().equals("application/json")) {
                Position position = null;

                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();

                // scorro il body della request
                while(line != null) {
                    sb.append(line + "\n");
                    line = reader.readLine();
                }
                // ritorna la lista delle posioni candidate
                candidatePositionsList = getCandidatePositionsList(sb.toString());

                // aggiunge una posizione se valida e ritorna vero se TUTTE le posizioni sono valide
                allPositionsValid = addCandidatePositions(candidatePositionsList, (String) session.getAttribute("user"));

                if(allPositionsValid) {
                    // tutte le posizioni sono valide
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter pw = response.getWriter();
                    pw.println("<html><head></head><body>");
                    pw.println(session.getAttribute("user") + ", la tua posizione e' stata aggiunta correttamente! " + HttpServletResponse.SC_OK);
                    pw.println("</body></html>");
                }
                else {
                    // almeno una posizione non è valida
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().println("<html><body><p>" + session.getAttribute("user") + ", one or more position are not valid! </p></body></html>" + HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            else {
                // la richiesta non è di tipo json
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("<html><body><p>" + session.getAttribute("user") + ", your request is not in json format! Retry </p></body></html>" + HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    // trasformo json in oggetto Position
    // salvo positions in lista di appoggio candidatePositionList
    private ArrayList<Position> getCandidatePositionsList(String sb) {
        ArrayList<Position> candidatePositionsList = new ArrayList<>();

        // definisci array positions, dove ogni elemento contiene un oggetto json
        String[] positions = sb.toString().split("},");
        for(int i = 1; i < positions.length; i++) {
            if(i != positions.length - 1) {
                positions[i] = positions[i] + "}";
            }
            else {
                String[] s = positions[i].split("]");
                positions[i] = s[0];
            }
        }

        // converti ogni oggetto json in oggetto Position e mandalo a checkPosition()
        Gson gson = new Gson();
        for(int i = 1; i < positions.length; i++) {
            String a = positions[i].toString();
            Position pos = gson.fromJson(a, Position.class);
            candidatePositionsList.add(pos);
        }
        return candidatePositionsList;
    }

    private boolean addCandidatePositions(ArrayList<Position> candidatePositionsList, String username) {
        boolean positionValid = false;
        boolean allPositionValid = false;

        Iterator<Position> iter = candidatePositionsList.iterator();
        try {
            while (iter.hasNext()) {
                //for (Position position : candidatePositionsList) {
                Position position = iter.next();
                positionValid = validateCandidatePosition(position, username);
                if (positionValid) {
                    // salva posizione nella mappa positionList dell'utente
                    users.get(username).getPositionList().add(position);
                    allPositionValid = true;
                } else {
                    // rimuovi posizione dalla mappa candidatePositionsList
                    iter.remove();
                    allPositionValid = false;
                }
            }
            System.out.println("SIZE(POST): "+users.get(username).getPositionList().size());

        }catch(ConcurrentModificationException ex){
            throw new ConcurrentModificationException(ex);
        }
            //}
        //}
        return allPositionValid;
    }

    private boolean validateCandidatePosition(Position position, String username) {
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
            if (users.get(username).getPositionList().isEmpty()) {
                // la lista delle posizioni dell'utente è al momento vuota, quindi
                // salva la prima posizione ricevuta e vai alla prossima iterazione del for
            } else {
                // la lista delle posizioni dell'utente non è vuota
                // prendi l'ultima posizione dalla lista
                Position lastPosition = users.get(username).getPositionList().getLast();
                long lastTimeStamp = lastPosition.getTimeStamp();
                // verifica di coerenza cronologica
                if (lastTimeStamp > (position.getTimeStamp())) {
                    throw new PositionNotValidException();
                }
                // verifico che la velocità sia < di 100 m/s

                Double distance = GeoFunction.distance(position.getLatitude(), position.getLatitude(), lastPosition.getLatitude(), lastPosition.getLongitude());
//                Double distance = Math.sqrt(Math.pow(position.getLatitude() - lastPosition.getLatitude(), 2) + Math.pow(position.getLongitude() - lastPosition.getLongitude(), 2));
                Double intervalTime = (double) (position.getTimeStamp() - lastTimeStamp);
                Double speed = distance / intervalTime;
                // verifica coerenza spazio-temporale
                if (speed >= 100) {
                    throw new PositionNotValidException();
                }
            }
            //users.get(username).getPositionList().add(position);
        } catch (PositionNotValidException e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }
}



