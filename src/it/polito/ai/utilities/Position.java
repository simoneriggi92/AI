package it.polito.ai.utilities;

import java.util.Date;

public class Position {
    private double latitude;
    private double longitude;
    private Date temporalStamp;

    public Position(double latitude, double longitude, Date temporalStamp) {
        setLatitude(latitude);
        setLongitude(longitude);
        setTemporalStamp(temporalStamp);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTemporalStamp() {
        return temporalStamp;
    }

    public void setTemporalStamp(Date temporalStamp) {
        this.temporalStamp = temporalStamp;
    }
}
