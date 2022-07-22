package com.licenta.personalinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.User;
import com.licenta.user.requests.PasswordResetRequest;
import com.licenta.user.responses.PasswordResetResponse;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PasswordResetActivity extends AppCompatActivity {

    private User user;
    private ApiCallUser apiCallUser;
    private EditText oldPassword, newPassword, newPasswordConfirmation;
    private TextView textViewName, textViewEmail;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);

        Button buttonResetPassword = findViewById(R.id.buttonChangePassword);
        buttonResetPassword.setOnClickListener(this::resetPassword);

        oldPassword = findViewById(R.id.editTextChangePasswordOldPassword);
        newPassword = findViewById(R.id.editTextChangePasswordNewPassword);
        newPasswordConfirmation = findViewById(R.id.editTextChangePasswordNewPassword2);

        avatar = findViewById(R.id.imageViewPasswordResetAvatar);

        requestClient(Integer.parseInt(SaveSharedPreference.getId(this)));
    }

    private void requestClient(int id) {
        Call<User> call = apiCallUser.getUser(id);

        textViewEmail = findViewById(R.id.textViewPasswordResetEmail);
        textViewName = findViewById(R.id.textViewPasswordResetName);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    textViewEmail.setText(user.getEmail());

                    if(user.getClient() != null) {
                        textViewName.setText(String.format("%s %s", user.getClient().getFirstName(), user.getClient().getLastName()));
                    } else {
                        textViewName.setText(String.format("%s %s", user.getTrainer().getFirstName(), user.getTrainer().getLastName()));
                    }

                    avatar.setClipToOutline(true);

                    Picasso.get().load(String.format("%susers/%d/avatar/", GlobalData.RETROFIT_BASE_URL, id)).placeholder(R.drawable.profile_picture).error(R.drawable.profile_picture).into(avatar);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void resetPassword(View view) {
        if (!newPassword.getText().toString().equals(newPasswordConfirmation.getText().toString())) {
            Snackbar.make(view, "The passwords don't match!", Snackbar.LENGTH_LONG).show();
        } else {
            PasswordResetRequest request = new PasswordResetRequest(Integer.parseInt(SaveSharedPreference.getId(this)), oldPassword.getText().toString(), newPassword.getText().toString());
            Call<PasswordResetResponse> call = apiCallUser.updateUserPassword(request);

            call.enqueue(new Callback<PasswordResetResponse>() {
                @Override
                public void onResponse(@NonNull Call<PasswordResetResponse> call, @NonNull Response<PasswordResetResponse> response) {
                    if (response.isSuccessful()) {
                        Snackbar.make(view, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<PasswordResetResponse> call, Throwable t) {
                    Log.e("Failure", t.getLocalizedMessage());
                }
            });
        }
    }
}