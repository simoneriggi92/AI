package it.polito.ai.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polito.ai.utilities.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import sun.awt.image.ImageWatched;

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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Il PositionServlet salva la posizione dell'utente.
 */

@WebServlet(urlPatterns = "/position")
public class PositionServlet extends HttpServlet {
    private Double latitude;
    private Double longitude;
    JSONArray jsonArray = new JSONArray();
    private long timeStamp;
    ConcurrentHashMap<String, User> users;
    ArrayList<Position> candidatePositionsList = new ArrayList<>();
    boolean allPositionsValid = false;
    boolean valid = true;
public static int o = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // recupera la mappa degli utenti dall'application context
        users = (ConcurrentHashMap<String, User>) this.getServletConfig().getServletContext().getAttribute("users");


        // recupera la sessione corrente
        HttpSession session = request.getSession(false);
        if(session != null) {
            // System.out.println("POSITION USERS 2 VOLTA INIO" + users.get(session.getAttribute("user")).getPositionList().size());
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
                System.out.println(candidatePositionsList.get(0).getTimeStamp());
                for(Position p : candidatePositionsList) {
                    System.out.println("positioncandite" + p.getTimeStamp());
                }
                // aggiunge una posizione se valida e ritorna vero se TUTTE le posizioni sono valide
                allPositionsValid = addCandidatePositions(candidatePositionsList, session.getAttribute("user").toString());
                // System.out.println("POSITION USERS 2 VOLTA fine" + users.get(session.getAttribute("user")).getPositionList().size());



                if(allPositionsValid) {
                    // tutte le posizioni sono valide
                    response.setStatus(HttpServletResponse.SC_OK);
//                    PrintWriter pw = response.getWriter();
//                    pw.println("<html><head></head><body>");
//                    pw.println(session.getAttribute("user") + ", la tua posizione e' stata aggiunta correttamente! " + HttpServletResponse.SC_OK);
//                    pw.println("</body></html>");
                }
                else {
                    // almeno una posizione non è valida
//                    try {
//                        for(int i =0; i < jsonArray.length(); i++) {
//                            System.out.println("RISPOSTA" + i + ":" + jsonArray.get(i).toString());
//                        }

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    response.setStatus(HttpServletResponse.SC_ACCEPTED);
                    response.setContentType("application/json");
                    PrintWriter pw = response.getWriter();
                    pw.println("#rejected:"+jsonArray.length());
                    pw.println(jsonArray);
                    pw.flush();
                    /*clear della lista*/
                    for(int i = 0; i<jsonArray.length();i++){
                        jsonArray.remove(i);
                    }



                    //response.getWriter().println("<html><body><p>" + session.getAttribute("user") + ", one or more position are not valid! </p></body></html>" + HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            else {
                // la richiesta non è di tipo json
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                //response.getWriter().println("<html><body><p>" + session.getAttribute("user") + ", your request is not in json format! Retry </p></body></html>" + HttpServletResponse.SC_BAD_REQUEST);
            }
            this.getServletContext().setAttribute("users", users);
//            System.out.println("POSITION USERS 2 VOLTA fine" + users.get(session.getAttribute("user")).getPositionList().size());
        }

    }

    // trasformo json in oggetto Position
    // salvo positions in lista di appoggio candidatePositionList
    private ArrayList<Position> getCandidatePositionsList(String sb) {
        ArrayList<Position> candidatePositionsList = new ArrayList<>();

        // definisci array positions, dove ogni elemento contiene un oggetto json
        sb = sb.substring(1);
        // System.out.println("flusso: " + sb.toString());
        String[] positions = sb.toString().split("},");
//        for(String s : positions) {
//            System.out.println(s + "---");
//        }

        for(int i = 0; i < positions.length; i++) {
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
        for(int i= 0; i < positions.length; i++) {
            String a = positions[i].toString();
            Position pos = gson.fromJson(a, Position.class);
            candidatePositionsList.add(pos);
        }
//        System.out.println("CANDIDATESLIST" + candidatePositionsList.size());
//        for(Position p : candidatePositionsList) {
//           System.out.println(p.getLatitude() + " " + p.getLongitude() + " " + p.getTimeStamp());
//        }

        return candidatePositionsList;
    }

    private boolean addCandidatePositions(ArrayList<Position> candidatePositionsList, String username) {
        boolean positionValid = false;
        boolean allPositionValid = true;

        Iterator<Position> iter = candidatePositionsList.iterator();
        try {

            while (iter.hasNext()) {
                //for (Position position : candidatePositionsList) {
                Position position = iter.next();
                positionValid = validateCandidatePosition(position, username);
                if (positionValid) {
                    // salva posizione nella mappa positionList dell'utente
                    users.get(username).getPositionList().add(position);
                    // allPositionValid = true;
                    // System.out.println("utenti: "+users.get(username).getPositionList().size());

                } else {
                    allPositionValid = false;
                    // rimuovi posizione dalla mappa candidatePositionsList
                    // System.out.println("PRIMA" + candidatePositionsList.size());
                    // System.out.println("utenti: "+users.get(username).getPositionList().size());
                    iter.remove();

                    // System.out.println("DOPO:" + candidatePositionsList.size());

                }
            }
            // System.out.println("SIZE(POST): "+users.get(username).getPositionList().size());

        }catch(ConcurrentModificationException ex){
            // System.out.println("ECCCCCCC");
            throw new ConcurrentModificationException(ex);

        }
            //}
        //}
//        for( int i = 0; i< users.get(username).getPositionList().size();i++) {
//
//            Position pos = users.get(username).getPositionList().getLast();
//            System.out.println("ULTIMO: "+pos.getTimeStamp());
//            users.get(username).getPositionList().remove(pos);
//        }
//        System.out.println("POSITION USERS 2 VOLTA fine" + users.get(username).getPositionList().size());
//        System.out.println(("AllPositionValid: "+allPositionValid));
        return allPositionValid;
    }

    private boolean validateCandidatePosition(Position position, String username) {
        PositionErrors positionErrors = new PositionErrors();
        try {
            // acquisisco latitudine e faccio verifica
            if (position.getLatitude() < -90L || position.getLatitude() > 90L) {
                positionErrors.setError(true);
                positionErrors.setLatitude(true);
                // createResponse(position,"latitude");
                // throw new PositionNotValidException();
            }
            // acquisisco longitudine e faccio verifica
            if (position.getLongitude() < -180L || position.getLongitude() > 180L) {
                positionErrors.setError(true);
                positionErrors.setLongitude(true);
                // createResponse(position,"longitude");
                // throw new PositionNotValidException();
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
                // System.out.println("AH" + "timestampdellaposizionecorrente"+position.getTimeStamp() + " ultimo timestamp della lista utenti" + lastTimeStamp);
                // verifica di coerenza cronologica
                if (lastTimeStamp > position.getTimeStamp()) {
                    positionErrors.setError(true);
                    positionErrors.setTimeStamp(true);
                    // createResponse(position, "timestamp");
                    // System.out.println("AZ" + position.getTimeStamp() + " " + lastTimeStamp);
                    // throw new PositionNotValidException();
                }
                // verifico che la velocità sia < di 100 m/s

                Double distance = GeoFunction.distance(position.getLatitude(), position.getLongitude(), lastPosition.getLatitude(), lastPosition.getLongitude())*1000;
                //Double distance = Math.sqrt(Math.pow(position.getLatitude() - lastPosition.getLatitude(), 2) + Math.pow(position.getLongitude() - lastPosition.getLongitude(), 2));
                Double intervalTime = (double) (position.getTimeStamp() - lastTimeStamp);
                Double speed = distance / intervalTime;
                // verifica coerenza spazio-temporale
                if (speed >= 100D) {
                    positionErrors.setError(true);
                    positionErrors.setSpeed(true);
                    //createResponse(position, "speed");
                    //throw new PositionNotValidException();

                }
            }
            if(positionErrors.isError()) {
                throw new PositionNotValidException();
            }
            //users.get(username).getPositionList().add(position);
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
            //jsonArray = new JSONArray();
            obj.put("latitude",pos.getLatitude());
            obj.put("longitude", pos.getLongitude());
            obj.put("timeStamp", pos.getTimeStamp());
            obj.put("description", reason);
            jsonArray.put(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}



