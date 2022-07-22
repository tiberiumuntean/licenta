package com.licenta.fitnessclass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.admin.AdminClassActivity;
import com.licenta.admin.AdminClubActivity;
import com.licenta.apis.ApiCallFitnessClass;
import com.licenta.clientschedule.ClientScheduleActivity;
import com.licenta.fitnessclass.requests.DateRequest;
import com.licenta.models.FitnessClass;
import com.licenta.models.WeekDayClasses;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FitnessClassesActivity extends AppCompatActivity implements FitnessClassInterface {
    private List<WeekDayClasses> weekDayList = new ArrayList<>();

    private ApiCallFitnessClass apiCallFitnessClass;
    private RecyclerView recyclerView;
    private WeekDayAdapter weekDayAdapter;

    private LinearLayoutManager layoutManager;

    private List<FitnessClass> fitnessClassList;

    private Button mySchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_schedule);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallFitnessClass = retrofit.create(ApiCallFitnessClass.class);

        mySchedule = findViewById(R.id.buttonClassScheduleViewMySchedule);

        if(SaveSharedPreference.getIsAdmin(this)){
            mySchedule.setText("Create a new Fitness Class");

            mySchedule.setOnClickListener(view -> {
                Intent i = new Intent(this, AdminClassActivity.class);
                startActivity(i);
            });
            
        } else {
            mySchedule.setOnClickListener(view -> {
                Intent i = new Intent(this, ClientScheduleActivity.class);
                startActivity(i);
            });
        }
    }

    @Override
    public void onItemClick(int position, FitnessClass fitnessClass) {
        Intent intent = new Intent(this, ClassScheduleBookingActivity.class);
        intent.putExtra("class_id", fitnessClass.getId());
        startActivity(intent);
    }

    private void getWeekClasses() {
        Calendar todayCalendar = new GregorianCalendar();

        weekDayList = new ArrayList<>();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, dd MMMM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfQuery = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");

        String today = sdfQuery.format(todayCalendar.getTime());
        recyclerView = findViewById(R.id.recyclerViewClassScheduleItem);

        layoutManager = new LinearLayoutManager(FitnessClassesActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        DateRequest dateRequest = new DateRequest(today);
        Call<List<FitnessClass>> call = apiCallFitnessClass.getClassesByDate(dateRequest);

        call.enqueue(new Callback<List<FitnessClass>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<List<FitnessClass>> call, @NonNull Response<List<FitnessClass>> response) {
                if (response.isSuccessful()) {
                    fitnessClassList = response.body().stream().filter(fitnessClass -> fitnessClass.getClub_id() == Integer.parseInt(SaveSharedPreference.getClubId(FitnessClassesActivity.this))).collect(Collectors.toList());

                    // Parcurgem toate zilele din saptamana
                    for (int i = 0; i < 7; i++) {
                        List<FitnessClass> fitnessClassListToday = new ArrayList<>(); // Pentru fiecare zi, declaram un nou array cu clasele care se vor tine in ziua respectiva

                        Calendar calendar = new GregorianCalendar();
                        calendar.add(Calendar.DATE, i);

                        String displayValue = sdfDisplay.format(calendar.getTime());
                        String queryValue = sdfQuery.format(calendar.getTime());
                        String dateOnlyValue = sdfDateOnly.format(calendar.getTime());

                        // Parcurgem lista cu toate clasele din saptamana respectiva (primita de la request) si le filtram pe cele care sunt in ziua i
                        for (FitnessClass fc : fitnessClassList) {
                            String classDate = fc.getDate().split(" ")[0]; // Luam doar data din clasa (nu ne intereseaza ora)

                            if (classDate.contentEquals(dateOnlyValue)) { // Comparam sa vedem daca ziua i este egala cu data clasei (adica, clasa este in ziua i)
                                fitnessClassListToday.add(fc);          // Daca clasa respectiva se tine in ziua i, o adaugam in lista declarata la inceputul for-ului
                            }
                        }

                        // La sfarsit, cream un nou obiect weekDay care contine lista cu clasele din ziua respectiva
                        WeekDayClasses weekDay = new WeekDayClasses(displayValue, queryValue, fitnessClassListToday);

                        // Adaugam ziua respectiva la array-ul cu zile care va fi trimis spre adapter pentru afisare
                        weekDayList.add(weekDay);
                    }

                    recyclerView.setLayoutManager(layoutManager);
                    weekDayAdapter = new WeekDayAdapter(FitnessClassesActivity.this, weekDayList, FitnessClassesActivity.this);
                    recyclerView.setAdapter(weekDayAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FitnessClass>> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWeekClasses();
    }
}
