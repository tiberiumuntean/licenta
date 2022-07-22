package com.licenta.personalinfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.launchscreen.FirstScreenActivity;
import com.licenta.paymentshistory.PaymentsHistoryActivity;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.User;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PersonalInfoActivity extends AppCompatActivity {
    private User user;
    private ApiCallUser apiCallUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);

        Button buttonEditPersonalInfo = findViewById(R.id.buttonPersonalInfoEditPl);
        buttonEditPersonalInfo.setOnClickListener(view -> openEditPersonalInfoActivity());

        Button buttonPaymentsHistory = findViewById(R.id.buttonPersonalInfoPaymentsHistory);
        buttonPaymentsHistory.setOnClickListener(view -> openPaymentsHistoryActivity());

        Button buttonLogout = findViewById(R.id.buttonPersonalInfoLogout);
        buttonLogout.setOnClickListener(view -> openLoginActivity());

        Button buttonResetPassword = findViewById(R.id.buttonPersonalInfoChangePassword);
        buttonResetPassword.setOnClickListener(view -> openResetPasswordActivity());
    }

    private void requestUser(int id) {
        Call<User> call = apiCallUser.getUser(id);

        TextView name = findViewById(R.id.textViewPasswordResetName);
        TextView email = findViewById(R.id.textViewPasswordResetEmail);
        EditText phone = findViewById(R.id.editTextPersonalInfoPhone);
        EditText address = findViewById(R.id.editTextPersonalInfoAddress);
        ImageView avatar = findViewById(R.id.imageViewPasswordResetAvatar);

        call.enqueue(new Callback<User>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    if (user.getClient() != null) {
                        name.setText(String.format("%s %s", user.getClient().getFirstName(), user.getClient().getLastName()));
                        email.setText(SaveSharedPreference.getEmail(PersonalInfoActivity.this));
                        phone.setText(user.getClient().getPhoneNumber());
                        address.setText(user.getClient().getAddress());
                        avatar.setClipToOutline(true);

                        Picasso.get().load(String.format("%susers/%d/avatar/", GlobalData.RETROFIT_BASE_URL, id)).placeholder(R.drawable.profile_picture).error(R.drawable.profile_picture).into(avatar);
                    } else {
                        name.setText(String.format("%s %s", user.getTrainer().getFirstName(), user.getTrainer().getLastName()));
                        email.setText(SaveSharedPreference.getEmail(PersonalInfoActivity.this));
                        phone.setText(user.getTrainer().getPhoneNumber());
                        address.setText(user.getTrainer().getAddress());
                        avatar.setClipToOutline(true);

                        Picasso.get().load(String.format("%susers/%d/avatar/", GlobalData.RETROFIT_BASE_URL, id)).placeholder(R.drawable.profile_picture).error(R.drawable.profile_picture).into(avatar);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void openResetPasswordActivity() {
        Intent intent = new Intent(this, PasswordResetActivity.class);
        startActivity(intent);
    }

    private void openEditPersonalInfoActivity() {
        Intent intent;

        if(user.getClient() == null) {
            intent = new Intent(this, EditTrainerPersonalInfoActivity.class);
        } else {
            intent = new Intent(this, EditPersonalInfoActivity.class);
        }

        startActivity(intent);
    }

    private void openPaymentsHistoryActivity() {
        Intent intent = new Intent(this, PaymentsHistoryActivity.class);
        startActivity(intent);
    }

    private void openLoginActivity() {
        SaveSharedPreference.setEmail(this, "");
        SaveSharedPreference.setId(this, "");
        SaveSharedPreference.setClientId(this, "");
        SaveSharedPreference.setTrainerId(this, "");
        SaveSharedPreference.setToken(this, "");
        SaveSharedPreference.setIsAdmin(this, false);
        SaveSharedPreference.setClubId(this, "");

        finishAffinity();
        Intent intent = new Intent(this, FirstScreenActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestUser(Integer.parseInt(SaveSharedPreference.getId(this)));
    }
}