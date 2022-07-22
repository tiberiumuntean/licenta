package com.licenta.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClub;
import com.licenta.apis.ApiCallMembership;
import com.licenta.fitnessclubs.ChooseFitnessClubActivity;
import com.licenta.models.Club;
import com.licenta.models.Membership;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminClubActivity extends AppCompatActivity {

    private final String ENTITY_NAME = "Club";

    private TextView textViewTitle;
    private Button buttonCreate, buttonDelete;
    private EditText editTextName, editTextEmail, editTextAddress, editTextLat, editTextLng, editTextPhone, editTextSchedule;

    private ApiCallClub apiCall;

    private Club entity = new Club();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_club);

        initElements();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCall = retrofit.create(ApiCallClub.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            getEntity(id);

            buttonDelete.setVisibility(View.VISIBLE);
            buttonDelete.setOnClickListener(view -> deleteEntity(id));

            buttonCreate.setText("Save Changes");
            buttonCreate.setOnClickListener(view -> updateEntity());
        } else {
            buttonCreate.setText(String.format("Create %s", ENTITY_NAME));
            buttonCreate.setOnClickListener(view -> createEntity());
        }
    }

    private void getEntity(int id) {
        Call<Club> call = apiCall.getClub(id);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    entity = response.body();

                    updateElements(entity);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void createEntity() {
        buildEntity();

        Call<Club> call = apiCall.createClub(entity);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminClubActivity.this, String.format("The %s has been successfully created!", ENTITY_NAME), Toast.LENGTH_SHORT).show();

                    goBackToMainScreen();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void updateEntity() {
        buildEntity();

        Call<Club> call = apiCall.updateClub(entity.getId(), entity);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminClubActivity.this, String.format("The %s has been updated successfully!", ENTITY_NAME), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void deleteEntity(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(String.format("Delete this %s?", ENTITY_NAME));
        builder.setMessage(String.format("Are you sure you want to delete this %s? This action cannot be undone.", ENTITY_NAME));
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Call<Void> call = apiCall.deleteClub(id);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminClubActivity.this, String.format("The %s has been deleted successfully!", ENTITY_NAME), Toast.LENGTH_SHORT).show();

                        finishAffinity();

                        Intent i = new Intent(AdminClubActivity.this, ChooseFitnessClubActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(AdminClubActivity.this, String.format("Cannot delete %s because it's linked to an existing user", ENTITY_NAME), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("Failure", t.getLocalizedMessage());
                }
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void buildEntity() {
        entity.setName(editTextName.getText().toString());
        entity.setAddress(editTextAddress.getText().toString());
        entity.setEmail(editTextEmail.getText().toString());
        entity.setLatitude(Double.parseDouble(editTextLat.getText().toString()));
        entity.setLongitude(Double.parseDouble(editTextLng.getText().toString()));
        entity.setPhoneNumber(editTextPhone.getText().toString());
        entity.setSchedule(editTextSchedule.getText().toString());
    }

    private void initElements() {
        textViewTitle = findViewById(R.id.textViewAdminClubTitle);

        buttonCreate = findViewById(R.id.buttonAdminClubCreate);
        buttonDelete = findViewById(R.id.buttonAdminClubDelete);

        editTextName = findViewById(R.id.editTextAdminClubName);
        editTextEmail = findViewById(R.id.editTextAdminClubEmail);
        editTextAddress = findViewById(R.id.editTextAdminClubAddress);
        editTextLat = findViewById(R.id.editTextAdminClubLatitude);
        editTextLng = findViewById(R.id.editTextAdminClubLongitude);
        editTextPhone = findViewById(R.id.editTextAdminClubPhone);
        editTextSchedule = findViewById(R.id.editTextAdminClubSchedule);
    }

    private void updateElements(Club entity) {
        textViewTitle.setText(entity.getName());

        editTextName.setText(entity.getName());
        editTextEmail.setText(entity.getEmail());
        editTextAddress.setText(entity.getAddress());
        editTextLat.setText(String.valueOf(entity.getLatitude()));
        editTextLng.setText(String.valueOf(entity.getLongitude()));
        editTextPhone.setText(entity.getPhoneNumber());
        editTextSchedule.setText(entity.getSchedule());
    }

    private void goBackToMainScreen(){
        finish();
    }
}