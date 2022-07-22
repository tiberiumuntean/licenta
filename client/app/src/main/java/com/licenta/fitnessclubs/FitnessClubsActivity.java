package com.licenta.fitnessclubs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.admin.AdminDashboardActivity;
import com.licenta.models.Club;
import com.licenta.club.ClubActivity;
import com.licenta.club.FitnessClubsAdapter;
import com.licenta.club.FitnessClubsRecyclerViewInterface;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FitnessClubsActivity extends AppCompatActivity implements FitnessClubsRecyclerViewInterface {
    private ApiCallUser apiCallUser;
    private Button buttonOpenMap;
    private List<Club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_clubs);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);

        buttonOpenMap = findViewById(R.id.buttonFitnessClubsMap);

        buttonOpenMap.setOnClickListener(view -> {
            Intent i = new Intent(this, ChooseFitnessClubActivity.class);
            startActivity(i);
        });
    }

    private void requestClubs() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFitnessClubs);
        Call<User> call = apiCallUser.getUser(Integer.parseInt(SaveSharedPreference.getId(this)));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    clubList = new ArrayList<>(response.body().getClubs());

                    LinearLayoutManager layoutManager = new LinearLayoutManager(FitnessClubsActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    FitnessClubsAdapter fitnessClubsAdapter = new FitnessClubsAdapter(clubList, FitnessClubsActivity.this);
                    recyclerView.setAdapter(fitnessClubsAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        SaveSharedPreference.setClubId(this, String.valueOf(clubList.get(position).getId()));

        if (SaveSharedPreference.getIsAdmin(this)) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ClubActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestClubs();
    }
}