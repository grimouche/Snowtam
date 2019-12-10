package com.nais.test_api;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps1Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    View view_marker;

    double latitude;
    double longitude;
    String airport_name;
    String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("LATI", 0);
        longitude = intent.getDoubleExtra("LONGI", 0);
        airport_name = intent.getStringExtra("AIRPORT_NAME");
        country = intent.getStringExtra("COUNTRY");

        LatLng marker = new LatLng(latitude, longitude);

        mMap.addMarker((new MarkerOptions().position(marker)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                view_marker = getLayoutInflater().inflate(R.layout.infowindow_marker, null);

                TextView showAirport = (TextView) view_marker.findViewById(R.id.nom_aeroport);
                TextView showCountry = (TextView) view_marker.findViewById(R.id.country);
                TextView showLat = (TextView) view_marker.findViewById(R.id.lat);
                TextView showLng = (TextView) view_marker.findViewById(R.id.lng);

                showAirport.setText(airport_name);
                showCountry.setText("Country : " + country);
                showLat.setText("Latitude : " + latitude);
                showLng.setText("Longitude : " + longitude);

                return view_marker;
            }
        });
    }
}

