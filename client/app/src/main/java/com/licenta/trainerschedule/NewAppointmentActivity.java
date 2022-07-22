package com.licenta.trainerschedule;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallAppointment;
import com.licenta.apis.ApiCallClient;
import com.licenta.apis.ApiCallClub;
import com.licenta.apis.ApiCallUser;
import com.licenta.client.ClientAdapter;
import com.licenta.client.ClientsActivity;
import com.licenta.models.Appointment;
import com.licenta.models.Client;
import com.licenta.models.Club;
import com.licenta.models.User;
import com.licenta.signup.SignUpActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewAppointmentActivity extends AppCompatActivity {
    private ApiCallUser apiCallUser;
    private ApiCallAppointment apiCallAppointment;
    private ApiCallClub apiCallClub;

    private List<Client> clientsList;

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private EditText description;
    private Spinner selectedClient;
    private TextView selectDate, selectTime;
    private Button saveAppointment;
    private SpinnerAdapter spinnerAdapter;
    private Integer clientId = 0;
    private User user;
    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        initDatePicker();
        initTimePicker();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clientId = extras.getInt("client_id");
        }

        selectedClient = findViewById(R.id.spinnerNewAppointmentSelectClient);
        description = findViewById(R.id.editTextNewAppointmentDescription);
        saveAppointment = findViewById(R.id.buttonNewAppointmentSaveAppointment);
        saveAppointment.setOnClickListener(view -> createAppointment());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);
        apiCallAppointment = retrofit.create(ApiCallAppointment.class);
        apiCallClub = retrofit.create(ApiCallClub.class);

        getClub(Integer.parseInt(SaveSharedPreference.getClubId(this)));
    }

    private void getClub(Integer id) {
        Call<Club> call = apiCallClub.getClub(id);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if(response.isSuccessful()){
                    club = response.body();

                    getUser(Integer.parseInt(SaveSharedPreference.getId(NewAppointmentActivity.this)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void getUser(Integer id) {
        Call<User> callUser = apiCallUser.getUser(id);
        callUser.enqueue(new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    if (clientId == 0) {
                        List<Integer> clubUsersIds = club.getUsers().stream().map(User::getId).collect(Collectors.toList());
                        clientsList = new ArrayList<>(user.getTrainer().getClients()).stream().filter(client -> clubUsersIds.contains(client.getUser_id())).collect(Collectors.toList());
                    } else {
                        clientsList = new ArrayList<>(user.getTrainer().getClients()).stream().filter(client -> client.getId() == clientId).collect(Collectors.toList());
                    }
                    spinnerAdapter = new SpinnerAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, clientsList);
                    selectedClient.setAdapter(spinnerAdapter);

                    selectedClient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            Client client = spinnerAdapter.getItem(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void createAppointment() {
        Appointment appointment = new Appointment();
        Client client = (Client) selectedClient.getSelectedItem();
        appointment.setClient(client);
        appointment.setDate(GlobalData.parseDate(String.format("%s %s:00", selectDate.getText().toString(), selectTime.getText().toString())));
        appointment.setName(description.getText().toString());
        appointment.setTrainer(user.getTrainer());

        Call<Appointment> call = apiCallAppointment.createAppointment(appointment);
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(@NonNull Call<Appointment> call, @NonNull Response<Appointment> response) {
                if(response.isSuccessful()){
                    Toast.makeText(NewAppointmentActivity.this, "Appointment has been created!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Appointment> call, @NonNull Throwable t) {

            }
        });
    }

    private void initTimePicker() {
        selectTime = findViewById(R.id.textViewNewAppointmentSelectTime);
        selectTime.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            int style = AlertDialog.THEME_HOLO_LIGHT;

            mTimePicker = new TimePickerDialog(NewAppointmentActivity.this, style, (timePicker, selectedHour, selectedMinute) -> selectTime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)), hour, minute, true); //Yes 24 hour time

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });
    }

    private void initDatePicker() {
        selectDate = findViewById(R.id.textViewNewAppointmentSelectDate);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        selectDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewAppointmentActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);

            long now = System.currentTimeMillis();
            datePickerDialog.getDatePicker().setMinDate(now - 1000);
            datePickerDialog.getDatePicker().setMaxDate(now + (1000 * 60 * 60 * 24 * 7) - 1000);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        onDateSetListener = (datePicker, year1, month1, day1) -> {
            month1 = month1 + 1;
            String monthString = String.valueOf(month1);
            String dayString = String.valueOf(day1);

            if (monthString.length() == 1) {
                monthString = "0" + monthString;
            }
            if (dayString.length() == 1) {
                dayString = "0" + dayString;
            }

            selectDate.setText(new StringBuilder().append(dayString).append(".").append(monthString).append(".").append(year1));
            //selectDate.setText(String.format(Locale.getDefault(),"%02d.%02d.%02d", day, month, year));
        };
    }
}


