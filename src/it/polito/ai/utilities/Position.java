package it.polito.ai.utilities;

public class Position {
    private double latitude;
    private double longitude;
    private long timestamp;

    public Position(){

    }
    public Position(double latitude, double longitude, long timestamp) {
        setLatitude(latitude);
        setLongitude(longitude);
        setTimestamp(timestamp);
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
