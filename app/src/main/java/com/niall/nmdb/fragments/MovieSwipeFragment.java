package com.niall.nmdb.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.nmdb.CardStackCallback;
import com.niall.nmdb.MyViewModel;
import com.niall.nmdb.R;
import com.niall.nmdb.adapters.CardStackAdapter;
import com.niall.nmdb.entities.Movie;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieSwipeFragment extends Fragment {

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();

    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

    private MyViewModel myViewModel;

    private String movieId;

    private ArrayList<String> movieIds = new ArrayList<>();

    public Map newLikedMovie = new HashMap();





    public static final String MOVIE_ID = "movieID";


    private ArrayList<Movie> movies = new ArrayList<>();

    private static final String TAG = "MovieSwipe";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myViewModel = ViewModelProviders.of((FragmentActivity) getActivity()).get(MyViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_movie_swipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        CardStackView cardStackView = view.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);

            }

            @Override
            public void onCardSwiped(Direction direction) {

                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);

                if (direction == Direction.Right){
                   Movie currentMovie = movies.get(manager.getTopPosition()-1);
                    System.out.println(movies.get(manager.getTopPosition()-1).getTitle());
                    movieId = String.valueOf(movies.get(manager.getTopPosition()-1).getId());
                    System.out.println(movieId);


                    myViewModel.setMovieID(movieId);

                    passData(movieId);

                    DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movie");

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

                    String key = movieRef.push().getKey();

                    Map<String, Object> movieValues = currentMovie.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();


                    childUpdates.put(key, movieValues);

                    newLikedMovie.put(key, currentMovie.getId() );



                    userRef.child(userId).child("user-likedMovies").updateChildren(newLikedMovie).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Log.d("MOVIE FAV", " ADDED!! ");
                            Toast.makeText(getContext(), currentMovie.getTitle() + " added to favourites", Toast.LENGTH_SHORT).show();
                        }
                    });

                    movieRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("MOVIE:", " Successfully added to db");
                        }
                    });







                }
                if (direction == Direction.Top){

                    //TODO: allow user to rate movie, save rating to user-ratedMovies in FB with movie key and rating


                }
                if (direction == Direction.Left){
                    //dismiss
                    //Toast.makeText(getContext(), "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
                    //dismiss
                    Toast.makeText(getContext(), "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
                    fetchMovies();
                }


            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());

            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());

            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView textView = view.findViewById(R.id.movie_title);

                Log.d(TAG, "onCardAppeared: " + position + ", name: " + textView.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.movie_title);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter();
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        fetchMovies();

    }

    private void paginate() {

        List<Movie> oldMovies = adapter.getMovies();
        List<Movie> newMovies = new ArrayList<>();
        CardStackCallback callback = new CardStackCallback(oldMovies, newMovies);
        DiffUtil.DiffResult hasil = DiffUtil.calculateDiff(callback);
        adapter.setMovies(newMovies);
        hasil.dispatchUpdatesTo(adapter);
    }

    private int pageNum = 0;
    private void fetchMovies() {

        pageNum++;
        //List<Movie> movies = new ArrayList<>();

        AndroidNetworking.get("https://api.themoviedb.org/3/movie/popular?api_key=57c10a851406809ce5be1ba20e3d3430&language=en-US&page="+pageNum)
//                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: api works...");

                System.out.println(response);


                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray arr = obj.getJSONArray("results");

                    for (int i = 0; i < arr.length(); i++) {


                        int id =  arr.getJSONObject(i).getInt("id");
                        String name = arr.getJSONObject(i).getString("title");
                        String image = arr.getJSONObject(i).getString("poster_path");
                        String rating = arr.getJSONObject(i).getString("vote_average");
                        String language = arr.getJSONObject(i).getString("original_language");

                        String imageUrl = "https://image.tmdb.org/t/p/w1280" + image;

                        Movie movie = new Movie(imageUrl, name, rating, language);

                        movie.setId(id);

                        movies.add(movie);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.addMovies(movies);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(ANError anError) {

            }
        });

    }

    public void passData(String movieId){
        movieIds.add(movieId);
        myViewModel.setMovieIDs(movieIds);
    }

    public Map getNewLikedMovie() {
        return newLikedMovie;
    }

    public void setNewLikedMovie(Map newLikedMovie) {
        this.newLikedMovie = newLikedMovie;
    }
}