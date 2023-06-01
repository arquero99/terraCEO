package com.example.terraceoapp;

public class Location {
    private String Name;
    private String Description;
    private double Latitude;
    private double Longitude;
    public Location(String name, String description, double latitude, double longitude) {
        Name = name;
        Description = description;
        Latitude = latitude;
        Longitude = longitude;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
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
}
