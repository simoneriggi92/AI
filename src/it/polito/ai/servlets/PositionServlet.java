package it.polito.ai.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.utilities.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Il PositionServlet salva la posizione dell'utente.
 */

@WebServlet(urlPatterns = "/position")
public class PositionServlet extends HttpServlet {
    JSONArray jsonArray = new JSONArray();
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
            System.out.println("Il server ha ricevuto una richiesta di caricamento posizioni da parte dell'utente: " + session.getAttribute("user"));
            // verifico se la richiesta è di tipo json
            if(request.getContentType().equals("application/json")) {
                Position position = null;

                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();

                // scorro il body della request
                while(line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }

                // ritorna la lista delle posioni candidate
                candidatePositionsList = getCandidatePositionsList(sb.toString());

                // aggiunge una posizione se valida e ritorna vero se TUTTE le posizioni sono valide
                allPositionsValid = addCandidatePositions(candidatePositionsList, session.getAttribute("user").toString());

                if(allPositionsValid) {
                    // tutte le posizioni sono valide
                    response.setStatus(HttpServletResponse.SC_OK);
                    System.out.println("Tutte le posizioni ricevute sono valide! Invio risposta al client... (" + response.getStatus() + ")");
                }
                else {
                    System.out.println("Alcune posizioni ricevute non sono valide! Invio risposta al client... (" + response.getStatus() + ")");
                    response.setStatus(HttpServletResponse.SC_ACCEPTED);
                    response.setContentType("application/json");
                    PrintWriter pw = response.getWriter();
                   // pw.println("#position_uploads_rejected: " + jsonArray.length());
                    pw.println(jsonArray);
                    pw.flush();
                    // clear della lista
                    jsonArray = new JSONArray();
                }
            }
            else {
                // la richiesta non è di tipo json
                System.out.println("La richiesta non è di tipo json! Invio risposta al client...");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            // this.getServletContext().setAttribute("users", users);
        }
    }

    // trasformo oggetto json in oggetto Position
    // salvo positions in lista di appoggio candidatePositionList
    private ArrayList<Position> getCandidatePositionsList(String sb) {
        ArrayList<Position> candidatePositionsList = new ArrayList<>();
/*
        // definisci array positions, dove ogni elemento contiene un oggetto json
        sb = sb.substring(1);
        String[] positions = sb.toString().split("},");

        for(int i = 0; i < positions.length; i++) {
            if(i != positions.length - 1) {
                positions[i] = positions[i] + "}";
            }
            else {
                String[] s = positions[i].split("]");
                positions[i] = s[0];
            }
        }

        // converti ogni oggetto json in oggetto Position e aggiungilo a candidatePositionList
        Gson gson = new Gson();
        for(int i= 0; i < positions.length; i++) {
            String a = positions[i].toString();
            Position pos = gson.fromJson(a, Position.class);
            candidatePositionsList.add(pos);
        }
*/
        ObjectMapper mapper = new ObjectMapper();
        try {
            candidatePositionsList = mapper.readValue(sb, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Position.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return candidatePositionsList;
    }

    private boolean addCandidatePositions(ArrayList<Position> candidatePositionsList, String username) {
        boolean positionValid = false;
        boolean allPositionValid = true;

        Iterator<Position> iter = candidatePositionsList.iterator();
        try {
            while (iter.hasNext()) {
                Position position = iter.next();
                positionValid = validateCandidatePosition(position, username);
                if (positionValid) {
                    // salva posizione nella mappa positionList dell'utente
                    users.get(username).getPositionList().add(position);
                } else {
                    // rimuovi la posizione nella mappa positionList dell'utente
                    allPositionValid = false;
                    iter.remove();
                }
            }
        } catch(ConcurrentModificationException ex){
            throw new ConcurrentModificationException(ex);
        }
        return allPositionValid;
    }

    private boolean validateCandidatePosition(Position position, String username) {
        PositionErrors positionErrors = new PositionErrors();
        try {
            // acquisisco latitudine e faccio verifica
            if (position.getLatitude() < -90L || position.getLatitude() > 90L) {
                positionErrors.setError(true);
                positionErrors.setLatitude(true);
            }
            // acquisisco longitudine e faccio verifica
            if (position.getLongitude() < -180L || position.getLongitude() > 180L) {
                positionErrors.setError(true);
                positionErrors.setLongitude(true);
            }
            // acquisisco data e faccio verifica
            if (users.get(username).getPositionList().isEmpty()) {
                // la lista delle posizioni dell'utente è al momento vuota,
                // quindi ritorna true
            } else {
                // la lista delle posizioni dell'utente non è vuota
                // prendi l'ultima posizione dalla lista
                Position lastPosition = users.get(username).getPositionList().getLast();
                long lastTimeStamp = lastPosition.getTimestamp();
                // verifica di coerenza cronologica
                if (lastTimeStamp > position.getTimestamp()) {
                    positionErrors.setError(true);
                    positionErrors.setTimeStamp(true);
                }
                // verifico che la velocità sia < di 100 m/s
                Double distance = GeoFunction.distance(position.getLatitude(), position.getLongitude(), lastPosition.getLatitude(), lastPosition.getLongitude())*1000;
                Double intervalTime = (double) (position.getTimestamp() - lastTimeStamp);
                Double speed = distance / intervalTime;
                if (speed >= 100D) {
                    positionErrors.setError(true);
                    positionErrors.setSpeed(true);
                }
            }
            if(positionErrors.isError()) {
                throw new PositionNotValidException();
            }
        } catch (PositionNotValidException e) {
            createResponse(position, positionErrors.getErrors());
            return false;
        }
        return true;
    }

    /*
    * Metodo per creare oggetti json
    * Riempire array di errori da ritornare al client
    * */
    public void createResponse(Position pos, String reason){

        JSONObject obj = new JSONObject();
        try {
            obj.put("latitude",pos.getLatitude());
            obj.put("longitude", pos.getLongitude());
            obj.put("timestamp", pos.getTimestamp());
            obj.put("description", reason);
            jsonArray.put(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }
}



