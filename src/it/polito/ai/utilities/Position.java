package it.polito.ai.utilities;

import java.util.Date;

public class Position {
    private double latitude;
    private double longitude;
    private long timeStamp;

    public Position(double latitude, double longitude, long timeStamp) {
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
