package com.licenta.trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.apis.ApiCallTrainer;
import com.licenta.models.Trainer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrainersActivity extends AppCompatActivity implements TrainersRecyclerViewInterface {
    private List<Trainer> trainers = new ArrayList<>();
    private ApiCallTrainer apiCallTrainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallTrainer = retrofit.create(ApiCallTrainer.class);
    }

    private void queryTrainers() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTrainers);

        Call<List<Trainer>> call = apiCallTrainer.getTrainers();
        call.enqueue(new Callback<List<Trainer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Trainer>> call, @NonNull Response<List<Trainer>> response) {
                if (response.isSuccessful()) {
                    trainers = response.body();

                    LinearLayoutManager layoutManager = new LinearLayoutManager(TrainersActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    TrainerAdapter trainerAdapter = new TrainerAdapter(trainers, TrainersActivity.this);
                    recyclerView.setAdapter(trainerAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Trainer>> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        int trainerId = trainers.get(position).getId();

        Intent intent = new Intent(this, TrainerProfileActivity.class);
        intent.putExtra("trainer_id", trainerId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        queryTrainers();
    }
}