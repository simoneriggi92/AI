package it.polito.ai.utilities;

import java.util.Date;

public class Position {
    private double latitude;
    private double longitude;
    private Date timeStamp;

    public Position(double latitude, double longitude, Date timeStamp) {
        setLatitude(latitude);
        setLongitude(longitude);
        setTimeStamp(timeStamp);
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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
