package com.licenta.client;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClient;
import com.licenta.apis.ApiCallUser;
import com.licenta.club.ClubActivity;
import com.licenta.membership.YourMembershipActivity;
import com.licenta.models.Client;
import com.licenta.models.User;
import com.licenta.trainerschedule.NewAppointmentActivity;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientProfileActivity extends AppCompatActivity {

    private TextView textViewName, textViewMemberSince, textViewAge, textViewMembership;
    private ImageView avatar;
    private Button buttonMessage, buttonSchedule, buttonDismiss;
    private ApiCallClient apiCallClient;
    private Client client;
    private int clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        textViewName = findViewById(R.id.textViewClientProfileName);
        textViewMemberSince = findViewById(R.id.textViewClientProfileMemberSince);
        textViewAge = findViewById(R.id.textViewClientProfileAge);
        textViewMembership = findViewById(R.id.textViewClientProfileMembership);

        buttonMessage = findViewById(R.id.buttonClientProfileSMS);
        buttonSchedule = findViewById(R.id.buttonClientProfileSchedule);
        buttonDismiss = findViewById(R.id.buttonClientProfileDismiss);

        avatar = findViewById(R.id.imageViewClientProfileAvatar);

        buttonMessage.setOnClickListener(view -> sendMessage());
        buttonSchedule.setOnClickListener(view -> makeAppointment());
        buttonDismiss.setOnClickListener(view -> dismissClient());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clientId = extras.getInt("client_id");
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallClient = retrofit.create(ApiCallClient.class);

        getClient(clientId);
    }

    private void makeAppointment() {
        Intent i = new Intent(ClientProfileActivity.this, NewAppointmentActivity.class);
        i.putExtra("client_id", client.getId());
        startActivity(i);
    }

    private void dismissClient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Dismiss this client?");
        builder.setMessage("Are you sure you want to dismiss your client? This action cannot be undone.");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            client.setTrainer(null);
            updateClient(client);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendMessage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
            }
        } else {
            if (client.getPhoneNumber() != null) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + client.getPhoneNumber()));
                intent.putExtra("sms_body", String.format("Hey, %s %s!", client.getFirstName(), client.getLastName()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "This Client doesn't have a Phone Number. Try sending a message on Social Media", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateClient(Client newClient) {
        Call<Client> call = apiCallClient.updateClient(clientId, newClient);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(@NonNull Call<Client> call, @NonNull Response<Client> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ClientProfileActivity.this, String.format("%s %s is no longer your client!", client.getFirstName(), client.getLastName()), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void getClient(Integer id) {
        Call<Client> callClient = apiCallClient.getClient(id);
        callClient.enqueue(new Callback<Client>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful()) {
                    client = response.body();

                    textViewName.setText(String.format("%s %s", client.getFirstName(), client.getLastName()));
                    textViewMemberSince.setText(GlobalData.getDisplayDate(client.getRegistrationDate()));
                    if(client.getBirthday() != null) {
                        textViewAge.setText(String.valueOf(GlobalData.calculateAge(client.getBirthday())));
                    } else {
                        textViewAge.setText("Unknown");
                    }
                    textViewMembership.setText(client.getMembershipByClub(Integer.parseInt(SaveSharedPreference.getClubId(ClientProfileActivity.this))) != null ? client.getMembershipByClub(Integer.parseInt(SaveSharedPreference.getClubId(ClientProfileActivity.this))).getName() : "No active membership");

                    avatar.setClipToOutline(true);

                    Picasso.get().load(String.format("%susers/%d/avatar/", GlobalData.RETROFIT_BASE_URL, client.getUser_id())).placeholder(R.drawable.profile_picture).error(R.drawable.profile_picture).into(avatar);
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission not granted. Try again!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

}