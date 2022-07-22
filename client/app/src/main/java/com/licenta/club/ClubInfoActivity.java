package com.licenta.club;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClub;
import com.licenta.models.Club;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClubInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Club club;
    private MapView mMapView;
    private ApiCallClub apiCallClub;
    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyBRBIgCEIK_SEOX2Q9h1zH_lqMV7jBsgW4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_info);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallClub = retrofit.create(ApiCallClub.class);

        mMapView = findViewById(R.id.mapViewClubInfoLocation);

        getClub(Integer.parseInt(SaveSharedPreference.getClubId(this)));
        initMap(savedInstanceState);
    }

    private void initMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map != null) {
            LatLng revo = new LatLng(46.77725130790893, 23.611923342329828);
            map.addMarker(new MarkerOptions().position(revo).title("Revo Fitness Club"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(revo, 15));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void getClub(int id) {
        Call<Club> call = apiCallClub.getClub(id);
        TextView name = findViewById(R.id.textViewClubInfoClubName);
        TextView phone = findViewById(R.id.textViewClubInfoPhone);
        TextView email = findViewById(R.id.textViewClubInfoEmail);
        TextView address = findViewById(R.id.textViewClubInfoAddress);
        // MapView location = findViewById(R.id.mapViewClubInfoLocation);
        TextView schedule = findViewById(R.id.textViewClubInfoScheduleHours);

        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    club = response.body();

                    name.setText(String.valueOf(club.getName()));
                    phone.setText(String.valueOf(club.getPhoneNumber()));
                    email.setText(String.valueOf(club.getEmail()));
                    address.setText(String.valueOf(club.getAddress()));
                    schedule.setText(String.valueOf(club.getSchedule()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());

            }
        });

    }

}