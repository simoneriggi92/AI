package it.polito.ai.servlets;

import com.sun.org.apache.xpath.internal.FoundIndex;
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
 *      Servlet che restituisce un sottoinsieme di posizioni in un dato range temporale
 */

@WebServlet(urlPatterns = "/rangePositions")   //???? url
public class RangeServlet extends HttpServlet{

    ConcurrentHashMap<String, User> users;
    User currentUser;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        users = (ConcurrentHashMap<String, User>) this.getServletConfig()
                .getServletContext()
                .getAttribute("users");

        HttpSession session = req.getSession(false);
        if(req.getParameter("startTimestamp") == null || req.getParameter("endTimestmap")== null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.reset();
        }

        long startTimestamp = Long.parseLong(req.getParameter("startTimestamp"));
        long endTimestamp = Long.parseLong(req.getParameter("endTimestamp"));

        System.out.println("Start: " + startTimestamp);
        System.out.println("End: " + endTimestamp);

        LinkedList<Position> rangeList = new LinkedList<>();

        if (session.getAttribute("user") != null
                && !users.get(session.getAttribute("user")).getPositionList().isEmpty()) {

            currentUser = users.get(session.getAttribute("user"));

            System.out.println("SIZE(GET): " + currentUser.getPositionList().size());
            //System.out.println("ULTIMO: "+currentUser.getPositionList().getLast().getTimeStamp());
            //se endTimeStamp > ultimo TimeStamp della lista-> endTimeStamp aggiornato
            if (endTimestamp > currentUser.getPositionList().getLast().getTimeStamp()) {
                endTimestamp = currentUser.getPositionList().getLast().getTimeStamp();
            }

            if (startTimestamp < currentUser.getPositionList().getFirst().getTimeStamp()) {
                startTimestamp = currentUser.getPositionList().getFirst().getTimeStamp();
            }

            if (startTimestamp > endTimestamp) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.reset();
            }

//            for (Position pos : currentUser.getPositionList()) {
//                System.out.println("val: " + pos.getLatitude() + "-" + pos.getLongitude() + "-" + pos.getTimeStamp());
//                if (pos.getTimeStamp() == startTimestamp) {
//                    found = true;
//                    break;
//                }
//            }
//
//            if (found) {
//                found = false;
//                for (Position pos : currentUser.getPositionList()) {
//                    if (pos.getTimeStamp() == endTimestamp) {
//                        found = true;
//                        break;
//                    }
//                }
//            }

//            if (found) {
                for (Position p : currentUser.getPositionList()) { //currentList){
                    if (p.getTimeStamp() >= startTimestamp && p.getTimeStamp() <= endTimestamp) {
                        System.out.println("Lat: " + p.getLatitude() + " Lon: " + p.getLongitude() + " TS: " + p.getTimeStamp());
                        rangeList.add(p);
                    }
                }

                PrintWriter pw = resp.getWriter();

                int i = 1;
                System.out.println("#Positions: " + rangeList.size());
                for (Position p : rangeList) {
                    pw.println("Position " + i + " --- Latitude: " + p.getLatitude()
                            +  " --- Longitude: " + p.getLongitude() + " --- TimeStamp: " + convertTime(p.getTimeStamp()));
                    i++;
                }
            //}

        }
    }

    public String convertTime(long time){

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss z");
        Date date = new Date(time*1000L);
        return sdf.format(date);
    }
}
