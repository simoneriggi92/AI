package it.polito.ai.utilities;

import java.util.ArrayList;
import java.util.Random;

public class GeoFunction {

    public GeoFunction(){

    }

    private static final double EARTH_RADIUS = 6371; // Raggio approssimativo della Terra
    private static Random random = new Random();
    /*
     * Per il calcolo della distanza tra due punti su una sfera (Terra) si ricorre alla
     * formula di haversine:
     *      hav(d/r) = hav(L2-L1) + cos(L1)cos(L2)hav(Long2-Long1)
     */

    public static double distance(double startLat, double startLong,
                                  double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d
    }

    public static double haversin(double val) {
        /*
         *  La formula di Haversine è la seguente:
         *      hav(a) = sin^2(a/2) = (1-cos(a))/2
         */
        return Math.pow(Math.sin(val / 2), 2);
    }



}

