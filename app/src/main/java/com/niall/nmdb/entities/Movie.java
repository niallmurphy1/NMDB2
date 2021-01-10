package com.niall.nmdb.entities;

import java.util.HashMap;
import java.util.Map;

public class Movie {

    private String title;
    private int id;
    private String image;
    private String rating;
    private String language;

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("id", id);
        result.put("image", image);
        result.put("rating", rating);
        result.put("language", language);

        return result;

    }


    public Movie(String image,String title, String rating, String language) {

        this.image = image;
        this.title = title;
        this.rating = rating;
        this.language = language;
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", image='" + image + '\'' +
                ", rating='" + rating + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
