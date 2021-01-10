package com.niall.nmdb.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.nmdb.MyViewModel;
import com.niall.nmdb.R;
import com.niall.nmdb.adapters.MovieCardAdapter;
import com.niall.nmdb.entities.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public class MyMoviesFragment extends Fragment implements MovieCardAdapter.ViewHolder.onMovieListener{


   // private MyViewModel myViewModel = new MyViewModel();
    private ArrayList<Integer> movieIDsint = new ArrayList<>();

    MovieCardAdapter adapter;

    public RecyclerView movieRCV;

   // RecyclerView.Adapter rCVadapter;

    private ArrayList<Movie> movies = new ArrayList<>();

   // private ArrayList<Movie> movieData = new ArrayList<>();

    private boolean userIDsFetched;


    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference userFavMovies;

    public DatabaseReference dataRef;
    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    public String userId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // myViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(MyViewModel.class);

        userId = fUser.getUid();
        dataRef = FirebaseDatabase.getInstance().getReference("User");



        //get user-favmovieids  from firebase
        userFavMovies = database.getReference("User").child(userId).child("user-likedMovies");

        //needs to be done synchronous

        //TODO: Get API to be called so that RCV is populated with movies



    }

//    public interface DataStatus{
//         static void dataIsLoaded(ArrayList<Movie> movies){
//
//         }
//    }


    private void loadMoviesIntRCV() {

        System.out.println("LOADMOVIEINTORCV STARTED");

        System.out.println("In loadMoviesIntoRCV(), movieIDs: " + movieIDsint.toString());

        for (int i = 0; i < movieIDsint.size(); i++){
        AndroidNetworking.get("https://api.themoviedb.org/3/movie/" + movieIDsint.get(i).toString() + "?api_key=57c10a851406809ce5be1ba20e3d3430&language=en-US")
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {

                System.out.println("Movie Array " +   response);

                try {

                    JSONObject obj = new JSONObject(response);



                        int id =  obj.getInt("id");
                        String name = obj.getString("title");
                        String image = obj.getString("poster_path");
                        String rating = obj.getString("vote_average");
                        String language = obj.getString("original_language");


                        String imageUrl = "https://image.tmdb.org/t/p/w1280" + image;

                        Movie movie = new Movie(imageUrl, name, rating, language);

                        movie.setId(id);

                        movies.add(movie);

                        System.out.println(movie.toMap().toString());







                    System.out.println(movies);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //DataStatus.dataIsLoaded(movies);
                adapter.addMovies(movies);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(ANError anError) {

            }
        });



        }


    }


    public void getlikedMoviesFromFirebase(){
        userFavMovies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieIDsint.clear();
                List<String> movieKeys = new ArrayList<>();

                for(DataSnapshot keyNode: snapshot.getChildren()){

                    movieKeys.add(keyNode.getKey());

                    int movieId = keyNode.getValue(Integer.class);

                    movieIDsint.add(movieId);

                    //System.out.println( "Movie IDs" + movieIDsint.toString());
                }

                System.out.println( "Movie IDs" + movieIDsint.toString());
                System.out.println("List of user-likedMovie IDs without duplicates" + removeDuplicates(movieIDsint));

                userIDsFetched = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                System.out.println("There was an error : " + error);
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        return inflater.inflate(R.layout.activity_my_movies_fragment, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        getlikedMoviesFromFirebase();

        loadMoviesIntRCV();



//        Movie movie = new Movie("/mMWLGu9pFymqipN8yvISHsAaj72.jpg", "boob", "sket", "Loose");
//        Movie movie1 = new Movie("/mMWLGu9pFymqipN8yvISHsAaj72.jpg", "boob", "sket", "Loose");
//
//        Movie movie2 = new Movie("/mMWLGu9pFymqipN8yvISHsAaj72.jpg", "boob", "sket", "Loose");
//
//        Movie movie3 = new Movie("/mMWLGu9pFymqipN8yvISHsAaj72.jpg", "boob", "sket", "Loose");
//
//        movies.add(movie);
//        movies.add(movie1);
//        movies.add(movie2);
//        movies.add(movie3);


        System.out.println("Movies size: " + movies.size());

        System.out.println("Liked movies: " + movies.toString());

        movieRCV = view.findViewById(R.id.movie_RCV);
        movieRCV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MovieCardAdapter(getContext(), movies, this);
        movieRCV.setAdapter(adapter);



        adapter.notifyDataSetChanged();


        //fetch liked movie IDs
        //System.out.println("Movie Ids with duplicates:  " + myViewModel.getMovieIDs());

//        movieIDs = myViewModel.getMovieIDs();
//
//        if (movieIDs != null) {
//            Set<String> set = new HashSet<>(movieIDs);
//            movieIDs.clear();
//            movieIDs.addAll(set);
//
//            System.out.println("Movie Ids without duplicates:  " + movieIDs);
//        }


    }

    @Override
    public void onMovieClick(int position) {
        //TODO: handle movie click
        System.out.println("Movie " + movies.get(position).getTitle() + " selected");
    }

    public ArrayList<Integer> removeDuplicates(ArrayList<Integer> movieIDs){

        if (movieIDs != null) {
            Set<Integer> set = new HashSet<>(movieIDs);
            movieIDs.clear();
            movieIDs.addAll(set);
        }

        return movieIDs;

    }


}