package com.niall.nmdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.icu.number.IntegerWidth;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.niall.nmdb.fragments.GoogleMapsFragment;
import com.niall.nmdb.fragments.MapsFragment;
import com.niall.nmdb.fragments.MovieSwipeFragment;
import com.niall.nmdb.fragments.MyMoviesFragment;

public class Navigation extends AppCompatActivity {

    private static String movieID;

    final SparseArray<Fragment> fragments = new SparseArray<>();

    private MyViewModel myViewModel;

    private Intent changeUnameIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);


        BottomNavigationView bNV = findViewById(R.id.bottom_nav_view);

        bNV.setSelectedItemId(R.id.movie_swiper);

        changeUnameIntent = new Intent(this, ChangeUsernameActivity.class);

        if(savedInstanceState == null){
            setInitialFrag();
        }

        bNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment frag = fragments.get(item.getItemId());

                switch(item.getItemId()) {
                    case R.id.map:
                        if (frag == null) frag = new GoogleMapsFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, frag)
                                .commit();
                        fragments.put(item.getItemId(), frag);

                        return true;

                    case R.id.movie_swiper:
                        if (frag == null) frag = new MovieSwipeFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, frag)
                                .commit();
                        fragments.put(item.getItemId(), frag);


                        return true;

                    case R.id.my_movies_list:
                        //Toast.makeText(Navigation.this, "My movies/TV selected", Toast.LENGTH_SHORT).show();
                        if (frag == null) frag = new MyMoviesFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, frag)
                                .commit();
                        fragments.put(item.getItemId(), frag);

                        return true;
                }
                return false;

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1:
                this.finish();
                return  true;

            case R.id.item2:
                startActivity(changeUnameIntent);
                return  true;

            case R.id.item3:
                //startActivity(logoutIntent);
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setInitialFrag(){
        Fragment frag = new MovieSwipeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, frag)
                .commit();
        fragments.put(R.id.movie_swiper, frag);

    }
}