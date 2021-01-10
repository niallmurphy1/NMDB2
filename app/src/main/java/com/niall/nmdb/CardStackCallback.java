package com.niall.nmdb;

import androidx.recyclerview.widget.DiffUtil;

import com.niall.nmdb.entities.Movie;

import java.util.List;

public class CardStackCallback extends DiffUtil.Callback {

   private List<Movie> oldMovies, newMovies;

   public CardStackCallback(List<Movie> oldMovies, List<Movie> newMovies){
       this.oldMovies = oldMovies;
       this.newMovies = newMovies;
   }


    @Override
    public int getOldListSize() {
        return oldMovies.size();
    }

    @Override
    public int getNewListSize() {
        return newMovies.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMovies.get(oldItemPosition).getImage() == newMovies.get(newItemPosition).getImage();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMovies.get(oldItemPosition) == newMovies.get(newItemPosition);
    }
}
