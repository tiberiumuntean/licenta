package com.licenta.clientschedule;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallAppointment;
import com.licenta.fitnessclass.requests.DateRequest;
import com.licenta.models.Appointment;
import com.licenta.models.WeekDayAppointments;
import com.licenta.trainerschedule.AppointmentInterface;
import com.licenta.clientschedule.WeekDayAdapter;

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

public class ClientScheduleActivity extends AppCompatActivity implements AppointmentInterface {
    private List<WeekDayAppointments> weekDayList = new ArrayList<>();

    private ApiCallAppointment apiCallAppointment;
    private RecyclerView recyclerView;
    private WeekDayAdapter weekDayAdapter;

    private LinearLayoutManager layoutManager;

    public Button buttonClassSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_schedule);

        buttonClassSchedule = findViewById(R.id.buttonClientScheduleViewFitnessClasses);
        buttonClassSchedule.setOnClickListener(view -> {
            finish();
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallAppointment = retrofit.create(ApiCallAppointment.class);

        layoutManager = new LinearLayoutManager(ClientScheduleActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
    }

    private void getWeekAppointments() {
        weekDayList = new ArrayList<>();

        Calendar todayCalendar = new GregorianCalendar();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, dd MMMM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfQuery = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");

        String today = sdfQuery.format(todayCalendar.getTime());
        recyclerView = findViewById(R.id.recyclerViewClientSchedule);

        DateRequest dateRequest = new DateRequest(today);
        Call<List<Appointment>> call = apiCallAppointment.getAppointmentsByDateAndClient(dateRequest, Integer.parseInt(SaveSharedPreference.getClientId(this)));
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
                    weekDayAdapter = new WeekDayAdapter(ClientScheduleActivity.this, weekDayList, ClientScheduleActivity.this);
                    recyclerView.setAdapter(weekDayAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Appointment>> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position, Appointment appointment) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        getWeekAppointments();
    }
}