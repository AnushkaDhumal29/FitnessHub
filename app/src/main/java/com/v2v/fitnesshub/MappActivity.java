package com.v2v.fitnesshub;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MappActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Spinner typeSpinner;
    private double curLat = 0, curLng = 0;

    private static final String API_KEY = "AIzaSyBTzcHFv78SYN7x6U9UWv22nnfAV5uvHPk";

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) loadMapAndLocation();
                else Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapp_activity);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        typeSpinner = findViewById(R.id.typeSpinner);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                checkPermissionAndStart();
            });
        }

        // Spinner change
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int pos, long id) {
                if (curLat != 0 && curLng != 0) {
                    String type = parent.getItemAtPosition(pos).toString();
                    fetchNearbyPlaces(curLat, curLng, type);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void checkPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            loadMapAndLocation();
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void loadMapAndLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
            curLat = location.getLatitude();
            curLng = location.getLongitude();

            LatLng me = new LatLng(curLat, curLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 15f));
            mMap.addMarker(new MarkerOptions().position(me).title("You are here"));

            // default type
            String type = typeSpinner.getSelectedItem().toString();
            fetchNearbyPlaces(curLat, curLng, type);
        });
    }

    private void fetchNearbyPlaces(double lat, double lng, String type) {
        new Thread(() -> {
            try {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=" + lat + "," + lng +
                        "&radius=2000" +
                        "&type=" + type +
                        "&key=" + API_KEY;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful() || response.body() == null) return;

                String body = response.body().string();
                JSONObject root = new JSONObject(body);
                JSONArray results = root.getJSONArray("results");

                List<LatLng> markers = new ArrayList<>();
                List<String> names = new ArrayList<>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject p = results.getJSONObject(i);
                    String name = p.optString("name", "Unknown");
                    JSONObject loc = p.getJSONObject("geometry").getJSONObject("location");
                    double plat = loc.getDouble("lat");
                    double plng = loc.getDouble("lng");
                    markers.add(new LatLng(plat, plng));
                    names.add(name);
                }

                runOnUiThread(() -> {
                    mMap.clear();
                    LatLng me = new LatLng(curLat, curLng);
                    mMap.addMarker(new MarkerOptions().position(me).title("You are here"));

                    for (int i = 0; i < markers.size(); i++) {
                        mMap.addMarker(new MarkerOptions()
                                .position(markers.get(i))
                                .title(names.get(i)));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to load places", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
