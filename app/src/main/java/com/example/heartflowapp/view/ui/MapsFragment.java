package com.example.heartflowapp.view.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heartflowapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    private Marker marker;
    private TextView locationDisplay;
    private double selectedLat;
    private double selectedLong;
    protected FusedLocationProviderClient client;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // Default location
            LatLng location = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(location).title("Marker"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Handle map click
            mMap.setOnMapClickListener(latLng -> {
                if (marker != null) {
                    marker.remove();
                }
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected"));
                selectedLat = latLng.latitude;
                selectedLong = latLng.longitude;

                // Update location in the text view
                if (locationDisplay != null) {
                    String address = getAddress(latLng);
                    locationDisplay.setText(address);
                }
            });

            client = LocationServices.getFusedLocationProviderClient(requireContext());


        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Innit search bar
        SearchView searchBar = view.findViewById(R.id.map_search);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        MaterialButton getPosBtn = view.findViewById(R.id.get_position);
        getPosBtn.setOnClickListener(this::getPosition);

        return view;
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

    /**
     * Search for a particular location
     *
     * @param location address
     */
    private void search(String location) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            // Get location list based on query
            List<Address> addresses = geocoder.getFromLocationName(location, 5);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                if (marker != null) {
                    marker.remove();
                }
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                selectedLat = latLng.latitude;
                selectedLong = latLng.longitude;

                // Update the text view
                if (locationDisplay != null) {
                    locationDisplay.setText(address.getAddressLine(0));
                }
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get the address string from the geolocation
     *
     * @param latLng lat long
     * @return address of the place
     */
    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Address not found", Toast.LENGTH_SHORT).show();
        }
        return "Unknown Address";
    }

    /**
     * Get the user's current position
     *
     * @param view view
     */
    public void getPosition(View view) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request location permissions if not granted
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100); // Request code for permissions
            return;
        }
        client.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                LatLng userLocation = new LatLng(lat, lng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                // Get address
                String address = getAddress(userLocation);
                locationDisplay = view.findViewById(R.id.address_display);
                locationDisplay.setText(address);
                selectedLat = lat;
                selectedLong = lng;

                // Add marker
                if (marker != null) {
                    marker.remove();
                }
                marker = mMap.addMarker(new MarkerOptions().position(userLocation));
            } else {
                Toast.makeText(requireActivity(), "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireActivity(), "Failed to get location", Toast.LENGTH_SHORT).show();
        });
    }

    public double getSelectedLat() {
        return selectedLat;
    }

    public double getSelectedLong() {
        return selectedLong;
    }

    public void setLocationDisplay(TextView locationDisplay) {
        this.locationDisplay = locationDisplay;
    }
}