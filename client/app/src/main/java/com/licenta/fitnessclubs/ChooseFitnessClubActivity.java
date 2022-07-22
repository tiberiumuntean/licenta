package com.licenta.fitnessclubs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.admin.AdminClubActivity;
import com.licenta.apis.ApiCallClub;
import com.licenta.models.Club;

import com.licenta.apis.ApiCallUser;
import com.licenta.models.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChooseFitnessClubActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {
    private SearchView searchView;
    private ListView listView;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient fusedLocationClient;

    private ApiCallClub apiCallClub;
    private Location myLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private List<Club> clubList = new ArrayList<>();
    private HashMap<String, Club> hashMap = new HashMap<>();

    private ArrayAdapter<Club> arrayAdapter;
    private Button buttonStartYourClubsActivity, buttonCreateClub;

    private ApiCallUser apiCallUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_fitness_club);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallClub = retrofit.create(ApiCallClub.class);
        apiCallUser = retrofit.create(ApiCallUser.class);

        if (ActivityCompat.checkSelfPermission(ChooseFitnessClubActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChooseFitnessClubActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        buttonStartYourClubsActivity = findViewById(R.id.buttonChooseClubSelect);
        buttonCreateClub = findViewById(R.id.buttonChooseClubCreate);

        buttonStartYourClubsActivity.setOnClickListener(view -> startYourClubsActivity());
        buttonCreateClub.setOnClickListener(view -> startCreateClubActivity());

        requestUser(Integer.parseInt(SaveSharedPreference.getId(this)));
        getCurrentLocation();

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    private void requestUser(int id) {
        Call<User> call = apiCallUser.getUser(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    if (user.getRole() == 2) {
                        buttonCreateClub.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void updateUser(User newUser) {
        Call<User> call = apiCallUser.updateUser(newUser.getId(), newUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void startYourClubsActivity() {
        if (ActivityCompat.checkSelfPermission(ChooseFitnessClubActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChooseFitnessClubActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        } else {
            if (!searchView.getQuery().toString().isEmpty()) {
                Intent intent = new Intent(this, FitnessClubsActivity.class);
                for (int i = 0; i < clubList.size(); i++) {
                    if (clubList.get(i).getName().contentEquals(searchView.getQuery())) {
                        user.addClub(clubList.get(i));
                        SaveSharedPreference.setClubId(this, String.valueOf(clubList.get(i).getId()));
                    }
                }
                updateUser(user);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please choose a Club!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCreateClubActivity() {
        if (ActivityCompat.checkSelfPermission(ChooseFitnessClubActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChooseFitnessClubActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        } else {
            Intent i = new Intent(this, AdminClubActivity.class);
            startActivity(i);
        }
    }

    private void queryClubs() {
        Call<List<Club>> call = apiCallClub.getClubs();

        call.enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(@NonNull Call<List<Club>> call, @NonNull Response<List<Club>> response) {
                if (response.isSuccessful()) {
                    clubList = response.body();

                    getListOfClubs();
                    getCurrentLocation();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Club>> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void getListOfClubs() {
        searchView = findViewById(R.id.searchViewChooseClub);
        listView = findViewById(R.id.listViewChooseClub);

        arrayAdapter = new ArrayAdapter<Club>(this, android.R.layout.simple_list_item_1, clubList);
        listView.setAdapter(arrayAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ChooseFitnessClubActivity.this.arrayAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ChooseFitnessClubActivity.this.arrayAdapter.getFilter().filter(newText);
                listView.setVisibility(View.VISIBLE);
                return false;
            }

        });

        searchView.setOnClickListener(view -> listView.setVisibility(View.VISIBLE));

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            searchView.setIconified(false);
            searchView.setQuery(clubList.get(i).getName(), true);
            listView.setVisibility(View.INVISIBLE);
        });

    }

    private void addMapMarkers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();

        task.addOnSuccessListener(location -> {
            if (location != null) {
                supportMapFragment.getMapAsync(googleMap -> {

                    if (myLocation != null) {
                        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        MarkerOptions options = new MarkerOptions().position(latLng).title("Your location");
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        googleMap.addMarker(options);
                    }

                    for (int i = 0; i < clubList.size(); i++) {
                        LatLng clubsLatLng = new LatLng(clubList.get(i).getLatitude(), clubList.get(i).getLongitude());
                        MarkerOptions optionsClub = new MarkerOptions().position(clubsLatLng).title(clubList.get(i).getName());
                        hashMap.put(optionsClub.getTitle(), clubList.get(i));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clubsLatLng, 15));
                        googleMap.addMarker(optionsClub);
                    }

                    googleMap.setOnMarkerClickListener(ChooseFitnessClubActivity.this);
                });
            }
        });
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2 * 5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        myLocation = location;
                    }
                }
            }
        };
    }

    ///////////////////////////////
    // Location Fetching Methods //
    ///////////////////////////////

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        addMapMarkers();
        queryClubs();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            addMapMarkers();
            queryClubs();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Location permission is required to use this application. Please enable it!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Club club = hashMap.get(marker.getTitle());
        searchView.setIconified(false);
        searchView.setQuery(club.getName(), false);

        return true;
    }
}