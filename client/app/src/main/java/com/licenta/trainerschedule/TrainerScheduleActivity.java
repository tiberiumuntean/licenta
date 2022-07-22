package com.licenta.trainerschedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallAppointment;
import com.licenta.apis.ApiCallClub;
import com.licenta.client.ClientsActivity;
import com.licenta.fitnessclass.requests.DateRequest;
import com.licenta.models.Appointment;
import com.licenta.models.Club;
import com.licenta.models.WeekDayAppointments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrainerScheduleActivity extends AppCompatActivity implements AppointmentInterface {
    private List<WeekDayAppointments> weekDayList = new ArrayList<>();

    private ApiCallAppointment apiCallAppointment;
    private ApiCallClub apiCallClub;
    private WeekDayAdapter weekDayAdapter;

    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;

    public Button buttonMakeAnAppointment;

    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_schedule);

        buttonMakeAnAppointment = findViewById(R.id.buttonTrainerScheduleMakeAnAppointment);
        buttonMakeAnAppointment.setOnClickListener(view -> openNewAppointmentActivity());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallAppointment = retrofit.create(ApiCallAppointment.class);
        apiCallClub = retrofit.create(ApiCallClub.class);
    }

    private void getClub(Integer id) {
        Call<Club> call = apiCallClub.getClub(id);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    club = response.body();

                    getWeekAppointments();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position, Appointment appointment) {
        Intent i = new Intent(this, EditAppointmentActivity.class);
        i.putExtra("id", appointment.getId());
        startActivity(i);
    }

    private void getWeekAppointments() {

        recyclerView = findViewById(R.id.recyclerViewTrainerSchedule);

        layoutManager = new LinearLayoutManager(TrainerScheduleActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        weekDayList = new ArrayList<>();

        Calendar todayCalendar = new GregorianCalendar();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, dd MMMM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfQuery = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");

        String today = sdfQuery.format(todayCalendar.getTime());

        DateRequest dateRequest = new DateRequest(today);
        Call<List<Appointment>> call = apiCallAppointment.getAppointmentsByDateAndTrainer(dateRequest, Integer.parseInt(SaveSharedPreference.getTrainerId(this)));
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Appointment>> call, @NonNull Response<List<Appointment>> response) {
                if (response.isSuccessful()) {
                    List<Appointment> appointmentsList = response.body();

                    // Parcurgem toate zilele din saptamana
                    for (int i = 0; i < 7; i++) {
                        List<Appointment> appointmentsListToday = new ArrayList<>(); // Pentru fiecare zi, declaram un nou array cu programarile din ziua respectiva

                        Calendar calendar = new GregorianCalendar();
                        calendar.add(Calendar.DATE, i);

                        String displayValue = sdfDisplay.format(calendar.getTime());
                        String queryValue = sdfQuery.format(calendar.getTime());
                        String dateOnlyValue = sdfDateOnly.format(calendar.getTime());

                        // Parcurgem lista cu toate programarile din saptamana respectiva (primita de la request) si le filtram pe cele care sunt in ziua i
                        for (Appointment ap : appointmentsList) {
                            String appointmentDate = ap.getDate().split(" ")[0]; // Luam doar data din clasa (nu ne intereseaza ora)

                            if (appointmentDate.contentEquals(dateOnlyValue)) { // Comparam sa vedem daca ziua i este egala cu data programarii (adica, clasa este in ziua i)
                                appointmentsListToday.add(ap);                  // Daca programarea respectiva este in ziua i, o adaugam in lista declarata la inceputul for-ului
                            }
                        }

                        // La sfarsit, cream un nou obiect weekDay care contine lista cu clasele din ziua respectiva
                        WeekDayAppointments weekDay = new WeekDayAppointments(displayValue, queryValue, appointmentsListToday);

                        // Adaugam ziua respectiva la array-ul cu zile care va fi trimis spre adapter pentru afisare
                        weekDayList.add(weekDay);
                    }

                    recyclerView.setLayoutManager(layoutManager);
                    weekDayAdapter = new WeekDayAdapter(TrainerScheduleActivity.this, weekDayList, TrainerScheduleActivity.this, club);
                    recyclerView.setAdapter(weekDayAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Appointment>> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void openNewAppointmentActivity() {
        Intent intent = new Intent(this, NewAppointmentActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getClub(Integer.parseInt(SaveSharedPreference.getClubId(this)));
    }
}