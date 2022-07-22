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
import com.licenta.membership.MembershipsActivity;
import com.licenta.models.Club;
import com.licenta.models.Membership;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminMembershipActivity extends AppCompatActivity {

    private final String ENTITY_NAME = "Membership";

    private TextView textViewTitle;
    private Button buttonCreate, buttonDelete;
    private EditText editTextName, editTextPrice, editTextDuration;

    private ApiCallMembership apiCall;
    private ApiCallClub apiCallClub;

    private Membership entity = new Membership();
    private Club secondaryEntity = new Club();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_membership);

        initElements();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCall = retrofit.create(ApiCallMembership.class);
        apiCallClub = retrofit.create(ApiCallClub.class);

        getSecondaryEntity(Integer.parseInt(SaveSharedPreference.getClubId(this)));

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
        Call<Membership> call = apiCall.getMembership(id);
        call.enqueue(new Callback<Membership>() {
            @Override
            public void onResponse(@NonNull Call<Membership> call, @NonNull Response<Membership> response) {
                if (response.isSuccessful()) {
                    entity = response.body();

                    updateElements(entity);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Membership> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void createEntity() {
        buildEntity();

        Call<Membership> call = apiCall.createMembership(entity);
        call.enqueue(new Callback<Membership>() {
            @Override
            public void onResponse(@NonNull Call<Membership> call, @NonNull Response<Membership> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminMembershipActivity.this, String.format("The %s has been successfully created!", ENTITY_NAME), Toast.LENGTH_SHORT).show();

                    goBackToMainScreen();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Membership> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void updateEntity() {
        buildEntity();

        Call<Membership> call = apiCall.updateMembership(entity.getId(), entity);
        call.enqueue(new Callback<Membership>() {
            @Override
            public void onResponse(@NonNull Call<Membership> call, @NonNull Response<Membership> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminMembershipActivity.this, String.format("The %s has been updated successfully!", ENTITY_NAME), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Membership> call, @NonNull Throwable t) {
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
            Call<Void> call = apiCall.deleteMembership(id);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminMembershipActivity.this, String.format("The %s has been deleted successfully!", ENTITY_NAME), Toast.LENGTH_SHORT).show();

                        goBackToMainScreen();
                    } else {
                        Toast.makeText(AdminMembershipActivity.this, String.format("Cannot delete %s because it's linked to an existing user", ENTITY_NAME), Toast.LENGTH_SHORT).show();
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
        entity.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
        entity.setDuration(Integer.parseInt(editTextDuration.getText().toString()));

        entity.setClub(secondaryEntity);
    }

    private void getSecondaryEntity(int id) {
        Call<Club> call = apiCallClub.getClub(id);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    secondaryEntity = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void initElements() {
        textViewTitle = findViewById(R.id.textViewAdminMembershipTitle);

        buttonCreate = findViewById(R.id.buttonAdminMembershipCreate);
        buttonDelete = findViewById(R.id.buttonAdminMembershipDelete);

        editTextName = findViewById(R.id.editTextAdminMembershipName);
        editTextDuration = findViewById(R.id.editTextAdminMembershipDuration);
        editTextPrice = findViewById(R.id.editTextAdminMembershipPrice);
    }

    private void updateElements(Membership entity) {
        textViewTitle.setText(entity.getName());

        editTextName.setText(entity.getName());
        editTextPrice.setText(String.valueOf(entity.getPrice()));
        editTextDuration.setText(String.valueOf(entity.getDuration()));
    }

    private void goBackToMainScreen(){
        finish();
    }
}