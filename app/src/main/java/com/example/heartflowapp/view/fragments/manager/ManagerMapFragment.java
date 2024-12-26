package com.example.heartflowapp.view.fragments.manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.model.Site;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ManagerMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_USER = "USER";
    private static final int LOCATION_REQUEST_CODE = 1001;

    private GoogleMap googleMap;
    private SearchView mapSearch;
    private String user;
    private final List<Site> siteList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        mapSearch = view.findViewById(R.id.map_search_bar);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle search location query
        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearch.getQuery().toString();
                if (!location.isEmpty()) {
                    searchLocation(location);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }


    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                addMarker(latLng, address.getAddressLine(0), false);
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMarker(LatLng latLng, String address, boolean isExistingSite) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(isExistingSite ? "Site Details" : "Create Site")
                .snippet(address)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.marker, isExistingSite))
                );
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(isExistingSite ? findSiteByLatLng(latLng) : null);
    }

    private Bitmap getBitmapFromDrawable(int resId, boolean isExisting) {
        Bitmap bitmap = null;
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), resId, null);
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            // Change color of the marker if the location doesn't exist
            if (!isExisting) {
                drawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#9694FF"), PorterDuff.Mode.SRC_IN));
            }
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

        }
        return bitmap;

    }

    private Site findSiteByLatLng(LatLng latLng) {
        for (Site site : siteList) {
            if (site.getLatitude() == latLng.latitude && site.getLongitude() == latLng.longitude) {
                return site;
            }
        }
        return null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        loadSites();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // Enable zoom control and get current location
        // Check location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            // Enable current location button
            googleMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof Site) {
                openDetails((Site) tag);
            } else {
                openSiteForm(marker.getPosition(), marker.getSnippet());
            }
            return true;
        });
    }

    // Fetch site list from database
    private void loadSites() {
        String userId = getArguments().getString(ARG_USER);
        if (userId != null) {
            user = userId;
        }
        DatabaseManager db = new DatabaseManager();
        db.getRef("site")
                .whereEqualTo("status", "ACTIVE")
                .whereEqualTo("createdBy", user)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    siteList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Site site = document.toObject(Site.class);
                        site.setSiteId(document.getId());
                        siteList.add(site);
                        addMarker(new LatLng(site.getLatitude(), site.getLongitude()), site.getAddress(), true);
                    }
                    // Set camera position after loading sites
                    LatLng initialLatLng;
                    if (!siteList.isEmpty()) {
                        initialLatLng = new LatLng(siteList.get(0).getLatitude(), siteList.get(0).getLongitude());
                    } else {
                        // Default to RMIT location
                        initialLatLng = new LatLng(10.7295137449591, 106.70918280905457);
                    }
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(initialLatLng, 15);
                    googleMap.animateCamera(cameraUpdate);
                });
    }

    private void openSiteForm(LatLng latLng, String address) {
        SiteFormFragment fragment = SiteFormFragment.newInstance(latLng.latitude, latLng.longitude, address);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.manager_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openDetails(Site site) {
        SiteDetailsFragment fragment = SiteDetailsFragment.newInstance(site, user);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.manager_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // Handle permission results
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(requireContext(), "Location permission is required to show your location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
