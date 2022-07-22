package com.licenta.fitnessclass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.admin.AdminClassActivity;
import com.licenta.apis.ApiCallFitnessClass;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.Client;
import com.licenta.models.FitnessClass;
import com.licenta.models.User;

import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClassScheduleBookingActivity extends AppCompatActivity {

    private Button buttonBookClass, buttonCancelBooking;
    private TextView textViewTitle, textViewTrainer, textViewDate, textViewTime, textViewLocation, textViewDescription;
    private ApiCallFitnessClass apiCallFitnessClass;
    private ApiCallUser apiCallUser;
    private FitnessClass fitnessClass;
    private Client client = null;
    private int classId;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_schedule_booking);

        buttonBookClass = findViewById(R.id.buttonClassScheduleBook);
        buttonCancelBooking = findViewById(R.id.buttonClassScheduleCancelBooking);
        textViewTitle = findViewById(R.id.textViewClassScheduleTitle);
        textViewTrainer = findViewById(R.id.textViewClassScheduleTrainer);
        textViewDate = findViewById(R.id.textViewClassScheduleDate);
        textViewTime = findViewById(R.id.textViewClassScheduleTime);
        textViewLocation = findViewById(R.id.textViewClassScheduleLocation);
        textViewDescription = findViewById(R.id.textViewClassScheduleDescription);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallFitnessClass = retrofit.create(ApiCallFitnessClass.class);
        apiCallUser = retrofit.create(ApiCallUser.class);

        Bundle extras = getIntent().getExtras();
        classId = extras.getInt("class_id");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void cancelBooking(View view) {
        fitnessClass.setFreeSpots(fitnessClass.getFreeSpots() + 1);
        fitnessClass.removeClient(client.getId());
        Call<FitnessClass> call = apiCallFitnessClass.updateFitnessClass(fitnessClass.getId(), fitnessClass);
        call.enqueue(new Callback<FitnessClass>() {
            @Override
            public void onResponse(@NonNull Call<FitnessClass> call, @NonNull Response<FitnessClass> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ClassScheduleBookingActivity.this, "Your booking has been canceled successfully!", Toast.LENGTH_LONG).show();

                    if (fitnessClass.getFreeSpots() != null) {
                        buttonBookClass.setVisibility(View.VISIBLE);
                        buttonCancelBooking.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FitnessClass> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    public void bookClass(View view) {
        fitnessClass.setFreeSpots(fitnessClass.getFreeSpots() - 1);
        fitnessClass.addClient(client);
        Call<FitnessClass> call = apiCallFitnessClass.updateFitnessClass(fitnessClass.getId(), fitnessClass);
        call.enqueue(new Callback<FitnessClass>() {
            @Override
            public void onResponse(@NonNull Call<FitnessClass> call, @NonNull Response<FitnessClass> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ClassScheduleBookingActivity.this, "Your booking has been completed successfully!", Toast.LENGTH_LONG).show();

                    if (fitnessClass.getFreeSpots() != null) {
                        buttonBookClass.setVisibility(View.INVISIBLE);
                        buttonCancelBooking.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FitnessClass> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    public void getUser(int id) {
        Call<User> call = apiCallUser.getUser(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    client = response.body().getClient();

                    getFitnessClass(classId);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    void getFitnessClass(int id) {
        Call<FitnessClass> call = apiCallFitnessClass.getFitnessClass(id);
        call.enqueue(new Callback<FitnessClass>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<FitnessClass> call, @NonNull Response<FitnessClass> response) {
                if (response.isSuccessful()) {
                    fitnessClass = response.body();

                    textViewTitle.setText(fitnessClass.getName());
                    textViewTrainer.setText(String.format("%s %s", fitnessClass.getTrainer().getFirstName(), fitnessClass.getTrainer().getLastName()));
                    textViewDate.setText(GlobalData.getDisplayDate(fitnessClass.getDate()));
                    textViewTime.setText(GlobalData.getDisplayTime(fitnessClass.getDate()));
                    textViewLocation.setText(fitnessClass.getLocation());
                    textViewDescription.setText(fitnessClass.getDescription());

                    if (!SaveSharedPreference.getIsAdmin(ClassScheduleBookingActivity.this)) {
                        buttonBookClass.setText(String.format("Book Now (%s spots left)", fitnessClass.getFreeSpots() != null ? fitnessClass.getFreeSpots() : 0));

                        if (fitnessClass.getClients().stream().filter(c -> c.getId() == client.getId()).collect(Collectors.toList()).size() != 0) {
                            buttonBookClass.setVisibility(View.INVISIBLE);
                            buttonCancelBooking.setVisibility(View.VISIBLE);
                            buttonBookClass.setText(String.format("Book Now (%s spots left)", fitnessClass.getFreeSpots() != null ? fitnessClass.getFreeSpots() + 1 : 0));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FitnessClass> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SaveSharedPreference.getIsAdmin(this)) {
            buttonBookClass.setText("Edit Fitness Class");
            buttonBookClass.setOnClickListener(view -> {
                Intent i = new Intent(this, AdminClassActivity.class);
                i.putExtra("id", classId);
                startActivity(i);
            });
            getFitnessClass(classId);
        } else {
            buttonBookClass.setOnClickListener(this::bookClass);
            buttonCancelBooking.setOnClickListener(this::cancelBooking);
            getUser(Integer.parseInt(SaveSharedPreference.getId(this)));
        }
    }
}