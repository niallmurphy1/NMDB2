package com.niall.nmdb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.nmdb.R;
import com.niall.nmdb.entities.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.ViewHolder> implements Filterable {

    private LayoutInflater layoutInflater;
    private ArrayList<Movie> movieData = new ArrayList<>();

    private ArrayList<Movie> filteredMovies = new ArrayList<>();

    private ViewHolder.onMovieListener onMovieListener;

    public MovieCardAdapter(Context context, ViewHolder.onMovieListener onMovieListener){
        this.layoutInflater = LayoutInflater.from(context);

        this.onMovieListener = onMovieListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                ArrayList<Movie> filteredMovies = new ArrayList<>();

                for(Movie movie : movieData){
                    if(movie.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredMovies.add(movie);
                    }
                }
                FilterResults filterResults = new FilterResults();

                if(constraint.toString().equals("")){
                    filteredMovies.addAll(movieData);
                    filterResults.values = movieData;
                    filterResults.count = movieData.size();

                }
                else{
                    filterResults.values=filteredMovies;
                    filterResults.count=filteredMovies.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                MovieCardAdapter.this.filteredMovies = (ArrayList<Movie>) results.values;
                notifyDataSetChanged();
            }
        }
        ;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView movieImage;
        public TextView movieTitleText, movieRatingText, movieLangText;
        onMovieListener onMovieListener;

        public ViewHolder(@NonNull View itemView, onMovieListener onMovieListener) {
            super(itemView);
            movieImage = itemView.findViewById(R.id.movie_image_rcv);
            movieTitleText = itemView.findViewById(R.id.movie_title_rcv);
            movieRatingText = itemView.findViewById(R.id.movie_rating_rcv);
            movieLangText = itemView.findViewById(R.id.movie_lang_rcv);
            this.onMovieListener = onMovieListener;

            itemView.setOnClickListener(this);
        }

        public void setData(Movie data) {

            //TODO: add rating to movie_rcv if !null so user can see their rating in recycler view


            Picasso.get()
                    .load(data.getImage())
                    .fit()
                    .centerCrop()
                    .into(movieImage);
            movieTitleText.setText(data.getTitle());
            movieRatingText.setText(data.getRating());
            movieLangText.setText(data.getLanguage());

        }

        @Override
        public void onClick(View v) {

            onMovieListener.onMovieClick(getAdapterPosition());
        }

        public interface onMovieListener{

            public void onMovieClick(int position);
        }
    }


    @NonNull
    @Override
    public MovieCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.movie_card_rcv, parent, false);
        ViewHolder vh = new ViewHolder(view, onMovieListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCardAdapter.ViewHolder holder, int position) {
        holder.setData(filteredMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredMovies.size();

    }

    public ArrayList<Movie> getMovieData() {
        return movieData;
    }

    public void setMovieData(ArrayList<Movie> movieData) {
        filteredMovies = movieData;
        this.movieData = movieData;
    }

    public void addMovies(ArrayList<Movie> movies) {
        this.movieData.addAll(movies);
    }
}
