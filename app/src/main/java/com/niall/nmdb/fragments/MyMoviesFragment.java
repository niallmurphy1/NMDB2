package com.niall.nmdb.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.LinkedHashSet;
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

    private List<String> movieKeys;

    private EditText searchBarEdit;


    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference userFavMovies;

    private DatabaseReference userMovies;

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

        userMovies = database.getReference("Movie");




    }



    public void retrieveMoviesFromFB(){


        userMovies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movies.clear();


                for(DataSnapshot keyNode: snapshot.getChildren()){


                    for(int i = 0; i< movieKeys.size(); i ++){


                        if(movieKeys.get(i).equals(keyNode.getKey())){

                            Movie movie = keyNode.getValue(Movie.class);
                            movies.add(movie);
                        }
                    }
                }


                System.out.println("Movies w/ duplicates: " + movies.toString());
                System.out.println("Movies w/ duplicates size : " + movies.size());


               getRidOfDuplicates(movies);

                System.out.println("Movies w/out duplicates: " + movies.toString());
                System.out.println("Movies w/out duplicates size : " + movies.size());

                adapter.setMovieData(movies);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRidOfDuplicates(ArrayList<Movie> movies) {


        if(movies != null) {
            LinkedHashSet<Movie> set = new LinkedHashSet<>(movies);
            movies.clear();
            movies.addAll(set);
        }


    }

    public void getlikedMoviesFromFirebase(){
        userFavMovies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                movieIDsint.clear();
               movieKeys = new ArrayList<>();

                for(DataSnapshot keyNode: snapshot.getChildren()){

                    movieKeys.add(keyNode.getKey());

                    int movieId = keyNode.getValue(Integer.class);

                    movieIDsint.add(movieId);

                    //System.out.println( "Movie IDs" + movieIDsint.toString());
                }

                System.out.println( "Movie IDs" + movieIDsint.toString());
                System.out.println("List of user-likedMovie IDs without duplicates" + removeDuplicates(movieIDsint));

                userIDsFetched = true;

                retrieveMoviesFromFB();

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


        searchBarEdit = view.findViewById(R.id.searchBar);

        setUpFilter();

        getlikedMoviesFromFirebase();


        System.out.println("Movies size: " + movies.size());

        System.out.println("Liked movies: " + movies.toString());

        movieRCV = view.findViewById(R.id.movie_RCV);
        movieRCV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MovieCardAdapter(getContext(), this);
        movieRCV.setAdapter(adapter);




    }

    private void setUpFilter() {

        searchBarEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                adapter.getFilter().filter(s.toString());
            }
        });
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