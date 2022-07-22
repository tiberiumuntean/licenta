package com.licenta.launchscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.VideoView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.admin.AdminDashboardActivity;
import com.licenta.club.ClubActivity;
import com.licenta.fitnessclubs.ChooseFitnessClubActivity;
import com.licenta.fitnessclubs.FitnessClubsActivity;
import com.licenta.login.LoginActivity;
import com.licenta.signup.SignUpActivity;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstScreenActivity extends AppCompatActivity {

    private Button buttonSignup;
    private Button buttonLogin;
    private ApiCallUser apiCallUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);

        if (SaveSharedPreference.getId(this).length() > 0) {
            String id = SaveSharedPreference.getId(this);
            Call<User> call = apiCallUser.getUser(Integer.parseInt(id));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()) {
                        User user = response.body();

                        if (user.getRole() == 2) {
                            if (user.getClubs().size() > 0) {
                                if (SaveSharedPreference.getClubId(FirstScreenActivity.this).isEmpty()) {
                                    Intent intent1 = new Intent(FirstScreenActivity.this, FitnessClubsActivity.class);
                                    startActivity(intent1);
                                } else {
                                    Intent intent1 = new Intent(FirstScreenActivity.this, AdminDashboardActivity.class);
                                    startActivity(intent1);
                                }
                            } else {
                                Intent intent2 = new Intent(FirstScreenActivity.this, ChooseFitnessClubActivity.class);
                                startActivity(intent2);
                            }
                        } else {
                            if (user.getClubs().size() > 0) {
                                if (SaveSharedPreference.getClubId(FirstScreenActivity.this).isEmpty()) {
                                    Intent intent1 = new Intent(FirstScreenActivity.this, FitnessClubsActivity.class);
                                    startActivity(intent1);
                                } else {
                                    Intent intent1 = new Intent(FirstScreenActivity.this, ClubActivity.class);
                                    startActivity(intent1);
                                }
                            } else {
                                Intent intent2 = new Intent(FirstScreenActivity.this, ChooseFitnessClubActivity.class);
                                startActivity(intent2);
                            }
                        }

                        finish();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Log.e("Failure", t.getLocalizedMessage());
                }
            });
        } else {
            setContentView(R.layout.activity_first_screen);

            buttonLogin = findViewById(R.id.buttonFirstScreenLogin);
            buttonSignup = findViewById(R.id.buttonFirstScreenSignup);

            buttonSignup.setOnClickListener(view -> openSignUpActivity());
            buttonLogin.setOnClickListener(view -> openLoginActivity());
        }
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SaveSharedPreference.getId(this).length() == 0) {
            VideoView videoview = findViewById(R.id.videoView);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video2);
            videoview.setVideoURI(uri);
            videoview.start();
        }
    }
}