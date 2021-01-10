package com.niall.nmdb.entities;

import com.google.android.gms.maps.model.LatLng;

public class Cinema {

    private String name;
    private LatLng location;
    private double rating;

    public Cinema(){

    }

    public Cinema(String name, LatLng location, double rating) {
        this.name = name;
        this.location = location;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Cinema{" +
                "name='" + name + '\'' +
                ", location=" + location +
                ", rating=" + rating +
                '}';
    }
}
