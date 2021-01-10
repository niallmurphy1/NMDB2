package com.niall.nmdb.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.niall.nmdb.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Looper looper = null;
    Geocoder geocoder = new Geocoder(getContext());

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:
                if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation(getView());
                }
                else{
                    Toast.makeText(getContext(), "Feature unavailable due to permsissions not granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation(view);
            }
        });




    }

    public void getLocation(View view) {
        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());



        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(), "Request Permission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }



        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        //code to process location result
                        double lat = locationResult.getLastLocation().getLatitude();
                        double lng = locationResult.getLastLocation().getLongitude();
                        TextView textView = view.findViewById(R.id.locationView);
                        textView.setText("Coordinate: " + String.valueOf(lat) + ", " + String.valueOf(lng));


                    }
                }
                ,null);

    }

    public ArrayList<String> addressToLocation(String address){

        ArrayList<String> latLong = new ArrayList<>();
        try {
            List<Address> locations =geocoder.getFromLocationName(address, 2);

            if(locations != null){
                double latitude = locations.get(0).getLatitude();
                double longitude = locations.get(0).getLongitude();
                Toast.makeText(getActivity(), String.valueOf(latitude) + "' " +
                        String.valueOf(longitude), Toast.LENGTH_LONG).show();
                latLong.add(String.valueOf(latitude));
                latLong.add(String.valueOf(longitude));

            }
            else{
                Toast.makeText(getActivity(), "Not found location", Toast.LENGTH_LONG).show();
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return latLong;
    }

    public String locationtoAddress(double longi, double lati) throws IOException {

        List<Address> addressList = geocoder.getFromLocation(longi, lati, 1);

        String address = addressList.get(0).getAddressLine(0);

        return address;

    }


}