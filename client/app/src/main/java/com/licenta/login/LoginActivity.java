package com.licenta.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.launchscreen.FirstScreenActivity;
import com.licenta.apis.ApiCallUser;
import com.licenta.user.UserLogin;
import com.licenta.user.requests.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button buttonLogin;
    private ApiCallUser apiCallUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLoginLogin);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);

        buttonLogin.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        LoginRequest loginRequest = new LoginRequest(email.getText().toString(), password.getText().toString());
        Call<UserLogin> call = apiCallUser.login(loginRequest);

        call.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(@NonNull Call<UserLogin> call, @NonNull Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    String id = response.body().getId();
                    String email = response.body().getEmail();
                    String token = response.body().getToken();

                    SaveSharedPreference.setId(LoginActivity.this, id);
                    SaveSharedPreference.setEmail(LoginActivity.this, email);
                    SaveSharedPreference.setToken(LoginActivity.this, token);
                    SaveSharedPreference.setIsAdmin(LoginActivity.this, response.body().getRole() == 2);

                    Intent intent = new Intent(LoginActivity.this, FirstScreenActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "The email and/or password don't match. Try again!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserLogin> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }
}