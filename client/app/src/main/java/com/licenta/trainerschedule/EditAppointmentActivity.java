package com.licenta.trainerschedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.admin.AdminClubActivity;
import com.licenta.apis.ApiCallAppointment;
import com.licenta.fitnessclubs.ChooseFitnessClubActivity;
import com.licenta.models.Appointment;
import com.licenta.models.Client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditAppointmentActivity extends AppCompatActivity {

    private final String ENTITY_NAME = "Appointment";

    private ApiCallAppointment apiCallAppointment;

    private List<Client> clientsList = new ArrayList<>();

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private EditText description;
    private Spinner selectedClient;
    private TextView selectDate, selectTime;
    private Button buttonSaveAppointment, buttonDeleteAppointment;
    private SpinnerAdapter spinnerAdapter;
    private Integer appointmentId = 0;
    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        initDatePicker();
        initTimePicker();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appointmentId = extras.getInt("id");
        }

        selectedClient = findViewById(R.id.spinnerEditAppointmentSelectClient);
        description = findViewById(R.id.editTextEditAppointmentDescription);
        buttonSaveAppointment = findViewById(R.id.buttonEditAppointmentSaveAppointment);
        buttonDeleteAppointment = findViewById(R.id.buttonEditAppointmentDeleteAppointment);
        buttonDeleteAppointment.setOnClickListener(view -> deleteAppointment(appointmentId));
        buttonSaveAppointment.setOnClickListener(view -> saveAppointment());

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallAppointment = retrofit.create(ApiCallAppointment.class);
        getAppointment(appointmentId);
    }

    private void deleteAppointment(Integer id) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(String.format("Delete this %s?", ENTITY_NAME));
        builder.setMessage(String.format("Are you sure you want to delete this %s? This action cannot be undone.", ENTITY_NAME));
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Call<Void> call = apiCallAppointment.deleteAppointment(id);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditAppointmentActivity.this, String.format("The %s has been deleted successfully!", ENTITY_NAME), Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        Toast.makeText(EditAppointmentActivity.this, String.format("Cannot delete %s because it's linked to an existing user", ENTITY_NAME), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("Failure", t.getLocalizedMessage());
                }
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getAppointment(Integer id) {
        Call<Appointment> call = apiCallAppointment.getAppointment(id);
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(@NonNull Call<Appointment> call, @NonNull Response<Appointment> response) {
                if (response.isSuccessful()) {
                    appointment = response.body();

                    clientsList.add(appointment.getClient());

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

                    description.setText(appointment.getName());
                    selectDate.setText(GlobalData.getDisplayDateRomanian(appointment.getDate()));
                    selectTime.setText(GlobalData.getDisplayTime(appointment.getDate()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Appointment> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void saveAppointment() {
        Client client = (Client) selectedClient.getSelectedItem();
        appointment.setClient(client);
        appointment.setDate(GlobalData.parseDate(String.format("%s %s:00", selectDate.getText().toString(), selectTime.getText().toString())));
        appointment.setName(description.getText().toString());

        Call<Appointment> call = apiCallAppointment.updateAppointment(appointmentId, appointment);
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(@NonNull Call<Appointment> call, @NonNull Response<Appointment> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditAppointmentActivity.this, "Appointment has been updated successfully!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Appointment> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void initTimePicker() {
        selectTime = findViewById(R.id.textViewEditAppointmentSelectTime);
        selectTime.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            int style = AlertDialog.THEME_HOLO_LIGHT;

            mTimePicker = new TimePickerDialog(EditAppointmentActivity.this, style, (timePicker, selectedHour, selectedMinute) -> selectTime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)), hour, minute, true); //Yes 24 hour time

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });
    }

    private void initDatePicker() {
        selectDate = findViewById(R.id.textViewEditAppointmentSelectDate);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        selectDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditAppointmentActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);

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
        };
    }
}