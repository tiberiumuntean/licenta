package com.licenta.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.apis.ApiCallClient;
import com.licenta.apis.ApiCallTrainer;
import com.licenta.models.Client;
import com.licenta.login.LoginActivity;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.Trainer;
import com.licenta.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {
    private ApiCallUser apicallUser;
    private ApiCallClient apiCallClient;
    private ApiCallTrainer apiCallTrainer;
    private Button buttonCreateAccount;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Switch trainerSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = findViewById(R.id.editTextSignupFirstName);
        lastName = findViewById(R.id.editTextSignupLastName);
        email = findViewById(R.id.editTextSignupEmail);
        password = findViewById(R.id.editTextSignupPassword);
        confirmPassword = findViewById(R.id.editTextSignupConfirmPassword);
        trainerSwitch = findViewById(R.id.switchSignUpTrainer);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apicallUser = retrofit.create(ApiCallUser.class);
        apiCallClient = retrofit.create(ApiCallClient.class);
        apiCallTrainer = retrofit.create(ApiCallTrainer.class);

        buttonCreateAccount = findViewById(R.id.buttonSignupCreateAccount);
        buttonCreateAccount.setOnClickListener(view -> saveAccount());
    }

    private void saveAccount() {
        if (!firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty()) {
            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(this, "The passwords don't match. Try again!", Toast.LENGTH_LONG).show();
            } else {
                buttonCreateAccount.setAlpha(.5f);
                buttonCreateAccount.setClickable(false);
                createUser();
            }
        } else {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_LONG).show();
        }
    }

    private void createUser() {
        User user = new User();
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());

        if (trainerSwitch.isChecked()) {
            user.setRole(1);
        } else {
            user.setRole(0);
        }

        Call<User> callUser = apicallUser.createUser(user);
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User createdUser = response.body();

                    if (trainerSwitch.isChecked()) {
                        createTrainer(createdUser);
                    } else {
                        createClient(createdUser);
                    }
                } else {
                    buttonCreateAccount.setAlpha(1);
                    buttonCreateAccount.setClickable(true);
                    try {
                        if (response.errorBody() != null) {
                            JSONObject objError = new JSONObject(response.errorBody().string());
                            Toast.makeText(SignUpActivity.this, objError.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                buttonCreateAccount.setAlpha(1);
                buttonCreateAccount.setClickable(true);
                Toast.makeText(SignUpActivity.this, "User couldn't be created due to a server error", Toast.LENGTH_LONG).show();
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void deleteUser(int id) {
        Call<Void> callUser = apicallUser.deleteUser(id);
        callUser.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void createClient(User createdUser) {
        Client client = new Client();
        client.setFirstName(firstName.getText().toString());
        client.setLastName(lastName.getText().toString());
        client.setUser(createdUser);

        Call<Client> callClient = apiCallClient.createClient(client);
        callClient.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(@NonNull Call<Client> call, @NonNull Response<Client> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "The account has been successfully created!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    buttonCreateAccount.setAlpha(1);
                    buttonCreateAccount.setClickable(true);
                    Toast.makeText(SignUpActivity.this, "Client couldn't be created due to an error", Toast.LENGTH_LONG).show();
                    deleteUser(createdUser.getId());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {
                buttonCreateAccount.setAlpha(1);
                buttonCreateAccount.setClickable(true);
                Toast.makeText(SignUpActivity.this, "Client couldn't be created due to a server error", Toast.LENGTH_LONG).show();
                deleteUser(createdUser.getId());
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void createTrainer(User createdUser) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName.getText().toString());
        trainer.setLastName(lastName.getText().toString());
        trainer.setUser(createdUser);

        Call<Trainer> callTrainer = apiCallTrainer.createTrainer(trainer);
        callTrainer.enqueue(new Callback<Trainer>() {
            @Override
            public void onResponse(@NonNull Call<Trainer> call, @NonNull Response<Trainer> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "The account has been successfully created!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    buttonCreateAccount.setAlpha(1);
                    buttonCreateAccount.setClickable(true);
                    Toast.makeText(SignUpActivity.this, "Trainer couldn't be created due to an error", Toast.LENGTH_LONG).show();
                    deleteUser(createdUser.getId());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Trainer> call, @NonNull Throwable t) {
                buttonCreateAccount.setAlpha(1);
                buttonCreateAccount.setClickable(true);
                Toast.makeText(SignUpActivity.this, "Trainer couldn't be created due to a server error", Toast.LENGTH_LONG).show();
                deleteUser(createdUser.getId());
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }
}