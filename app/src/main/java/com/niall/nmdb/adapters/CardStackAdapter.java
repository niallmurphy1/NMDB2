package com.niall.nmdb.adapters;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.nmdb.R;
import com.niall.nmdb.entities.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<Movie> movies;

    public CardStackAdapter(){
        movies = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_card, parent, false);
        return new ViewHolder(view);
                }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setData(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, rating, language;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.movie_image);
            title = itemView.findViewById(R.id.movie_title);
            rating = itemView.findViewById(R.id.movie_rating);
            language = itemView.findViewById(R.id.movie_language);
        }

        public void setData(Movie data) {

            Picasso.get()
                    .load(data.getImage())
                    .fit()
                    .centerCrop()
                    .into(image);
            title.setText(data.getTitle());
            rating.setText(data.getRating());
            language.setText(data.getLanguage());

        }
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
    }
}
