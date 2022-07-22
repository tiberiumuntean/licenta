package com.licenta.membership;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClientMembership;
import com.licenta.models.Client;
import com.licenta.club.ClubActivity;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.ClientMembership;
import com.licenta.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YourMembershipActivity extends AppCompatActivity {

    private TextView textViewStartDate, textViewEndDate, title, daysLeft;
    private ProgressBar progressBar;
    private Button buttonCancel;
    private ApiCallUser apiCallUser;
    private ApiCallClientMembership apiCallClientMembership;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_membership);

        textViewStartDate = findViewById(R.id.textViewMyMembershipStartDate);
        textViewEndDate = findViewById(R.id.textViewMyMembershipEndDate);
        title = findViewById(R.id.textViewMyMembershipTitle);
        progressBar = findViewById(R.id.progressBarMyMembership);
        daysLeft = findViewById(R.id.textViewMyMembershipDaysLeft);
        buttonCancel = findViewById(R.id.buttonYourMembershipCancel);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);
        apiCallClientMembership = retrofit.create(ApiCallClientMembership.class);

        buttonCancel.setOnClickListener(view -> cancelMembership());

        requestClient(Integer.parseInt(SaveSharedPreference.getId(this)));
    }

    private void cancelMembership() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Cancel your membership?");
        builder.setMessage("Are you sure you want to cancel this membership? This action cannot be undone.");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteClientMembership(user.getClient().getClientMembershipByClub(Integer.parseInt(SaveSharedPreference.getClubId(this))));
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteClientMembership(ClientMembership clientMembership) {
        Call<Void> call = apiCallClientMembership.deleteClientMembership(clientMembership.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(YourMembershipActivity.this, "The membership has been canceled!", Toast.LENGTH_LONG).show();
                    finishAffinity();
                    Intent intent = new Intent(YourMembershipActivity.this, ClubActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(YourMembershipActivity.this, "Error!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void requestClient(int id) {
        Call<User> call = apiCallUser.getUser(id);

        call.enqueue(new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    Client client = user.getClient();

                    try {
                        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

                        Date startDate = initialFormat.parse(client.getMembershipStartDate(Integer.parseInt(SaveSharedPreference.getClubId(YourMembershipActivity.this))));
                        String startDateString = outputFormat.format(startDate);

                        Date endDate = initialFormat.parse(client.getMembershipEndDate(Integer.parseInt(SaveSharedPreference.getClubId(YourMembershipActivity.this))));
                        String endDateString = outputFormat.format(endDate);

                        long daysBetween = TimeUnit.DAYS.convert(endDate.getTime() - startDate.getTime(), TimeUnit.MILLISECONDS);
                        long daysPassed = TimeUnit.DAYS.convert(new Date().getTime() - startDate.getTime(), TimeUnit.MILLISECONDS);
                        progressBar.setMax((int) daysBetween);
                        progressBar.setProgress((int) daysPassed, true);

                        if(new Date().getTime() >= startDate.getTime()) {
                            daysLeft.setText(String.format("%d days left", (int) daysBetween - daysPassed));
                        }
                        title.setText(client.getMembershipByClub(Integer.parseInt(SaveSharedPreference.getClubId(YourMembershipActivity.this))).getName());
                        textViewStartDate.setText(startDateString);
                        textViewEndDate.setText(endDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }
}