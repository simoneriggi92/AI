package it.polito.ai.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.utilities.Position;
import it.polito.ai.utilities.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Servlet che restituisce un sottoinsieme di posizioni in un dato range temporale
 */

@WebServlet(urlPatterns = "/rangePositions")   //???? url
public class RangeServlet extends HttpServlet{

    ConcurrentHashMap<String, User> users;
    User currentUser;
    String currentUsername;
    LinkedList<Position> rangeList = new LinkedList<>();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // recupera la mappa degli users
        users = (ConcurrentHashMap<String, User>) this.getServletConfig()
                .getServletContext()
                .getAttribute("users");

        // recupera la sessione corrente, se esiste (dovrebbe sempre esistere perchè se no agirebbe il filtro)
        HttpSession session = req.getSession(false);
        String currentUsername = (String)session.getAttribute("user");

        // verifica dei parametri passato dal client nella richiesta
        if(req.getParameter("startTimestamp") == null || req.getParameter("endTimestmap")== null) {
            // la richiesta del client non è valida
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.reset();
        }

        // recupera i parametri della richiesta del client
        long startTimestamp = Long.parseLong(req.getParameter("startTimestamp"));
        long endTimestamp = Long.parseLong(req.getParameter("endTimestamp"));


        if (currentUsername != null
                && !users.get(currentUsername).getPositionList().isEmpty()) {
            currentUser = users.get(currentUsername);
            // la lista delle posizioni dell'utente non è vuota

            // se endTimestamp > ultimo timestamp della lista, allora aggiorna endTimeStamp all'ultimo timestamp della lista
            if (endTimestamp > currentUser.getPositionList().getLast().getTimeStamp()) {
                endTimestamp = currentUser.getPositionList().getLast().getTimeStamp();
            }

            // se starTimestamp < primo timestamp della lista, allora aggiorna startTimestamp al primo timestamp della lista
            if (startTimestamp < currentUser.getPositionList().getFirst().getTimeStamp()) {
                startTimestamp = currentUser.getPositionList().getFirst().getTimeStamp();
            }

            // non è ammesso il caso starTimeStamp > endTimestamp
            if (startTimestamp > endTimestamp) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.reset();
            }

            // riempi la lista rangeList con le posizioni che rientrano nel range richiesto dal client
            for (Position p : currentUser.getPositionList()) {
                if (p.getTimeStamp() >= startTimestamp && p.getTimeStamp() <= endTimestamp) {
                    rangeList.add(p);
                }
            }
            // crea e manda oggetto json di risposta
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getWriter(), rangeList);      // scrivi rangeList nella response come oggetto json
            resp.setContentType("application/json");             // la risposta e' di tipo application/json
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            // la lista dell'utente corrente è vuota!
            // crea e manda oggetto json di risposta
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> emptyResponse = new HashMap<>();
            emptyResponse.put("response", "empty");
            mapper.writeValue(resp.getWriter(), emptyResponse);  // scrivi emptyResponse nella response come oggetto json
            resp.setContentType("application/json");             // la risposta e' di tipo application/json
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
