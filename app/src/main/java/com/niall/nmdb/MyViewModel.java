package com.niall.nmdb;

import java.util.ArrayList;

public class MyViewModel extends androidx.lifecycle.ViewModel {


    private String movieID;
    private ArrayList<String> movieIDs;

    public MyViewModel(){

    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public ArrayList<String> getMovieIDs() {
        return movieIDs;
    }

    public void setMovieIDs(ArrayList<String> movieIDs) {
        this.movieIDs = movieIDs;
    }
}
