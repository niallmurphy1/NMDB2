package com.niall.nmdb.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.ICUUncheckedIOException;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.niall.nmdb.R;
import com.niall.nmdb.entities.Cinema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class GoogleMapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener{

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    // Geocoder geocoder = new Geocoder(getContext());
    GoogleMap gMap;
    double currentLat = 0, currentLong = 0;
    LatLng currentLocation;
    private boolean locationSet;
    private int count = 0;
    private final GoogleMap.OnMarkerClickListener onMarkerClickListener = this;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            gMap = googleMap;
            getLocation();
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            gMap.setOnMarkerClickListener(onMarkerClickListener);
            gMap.getUiSettings().setMapToolbarEnabled(true);

           // Toast.makeText(getContext(), "The circle represents your location", Toast.LENGTH_LONG).show();


        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationSet = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // getLocation();
        return inflater.inflate(R.layout.fragment_google_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void getLocation() {


        //  fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        //code to process location result

                        currentLong = locationResult.getLastLocation().getLongitude();
                        currentLat = locationResult.getLastLocation().getLatitude();


                        currentLocation = new LatLng(currentLat, currentLong);

                        if (!locationSet) {
                            count++;


                            gMap.setMyLocationEnabled(true);
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                            gMap.moveCamera((CameraUpdateFactory.newLatLngZoom(currentLocation, 15)));

                            System.out.println("current latitude: " + currentLat);
                            System.out.println("current longitude: " + currentLong);

                            AndroidNetworking.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                                    currentLat +","+ currentLong + "&radius=2000&keyword=cinema&type=movie_theater&key=AIzaSyBLhb0IsHjqGyumwEn0oIL3FMgRDc6dgNg")

                                    .build().getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {

                                    Log.w("API", response);
                                    String jsonString = response;

                                    try {
                                        JSONObject obj = new JSONObject(jsonString);
                                        JSONArray arr = obj.getJSONArray("results");

                                        for(int i = 0; i < arr.length(); i++){

                                            String cinemaName = arr.getJSONObject(i).getString("name");

                                            double rating = arr.getJSONObject(i).getDouble("rating");

                                            double cinemaLat = arr.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                            double cinemaLng = arr.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                            LatLng cinemaLatLng = new LatLng(cinemaLat, cinemaLng);

                                           Cinema cinema = new Cinema(cinemaName, cinemaLatLng, rating);

                                            Marker marker = (gMap.addMarker(new MarkerOptions().position(cinemaLatLng).title(cinema.getName())));

                                            marker.setTag(cinema);







                                        }

                                       // Toast.makeText(getActivity(), "Showing Cinemas within a 2km range", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                                @Override
                                public void onError(ANError anError) {

                                    Log.d(TAG, "onError: " + anError);
                                    System.out.println("Error" + anError);
                                }
                            });







                        }

                        locationSet =true;
                    }
                }
                , null);







    }



    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //Integer clickCount = (Integer) marker.getTag();

        Cinema cinema = (Cinema) marker.getTag();


        assert cinema != null;

        if(cinema.getRating() == 0){
            Toast.makeText(getActivity(), "No rating yet", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getActivity(), "Cinema rating:  " + cinema.getRating(), Toast.LENGTH_SHORT).show();

        }
//        if (clickCount != null) {
//            clickCount = clickCount + 1;
//            marker.setTag(clickCount);
//            Toast.makeText(getActivity(),
//                    marker.getTitle() +
//                            " has been clicked " + clickCount + " times.",
//                    Toast.LENGTH_SHORT).show();
//        }

        return false;
        }
    }
