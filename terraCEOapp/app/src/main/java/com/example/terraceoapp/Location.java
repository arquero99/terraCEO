package com.example.terraceoapp;

public class Location {
    private double Latitude;
    private double Longitude;
    private String name;
    public Location(double latitude, double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }
    public double getLatitude() {
        return Latitude;
    }
    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
    public double getLongitude() {
        return Longitude;
    }
    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
