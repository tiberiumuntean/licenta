package com.licenta.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClub;
import com.licenta.apis.ApiCallUser;
import com.licenta.client.ClientsActivity;
import com.licenta.club.ClubActivity;
import com.licenta.club.ClubInfoActivity;
import com.licenta.fitnessclass.FitnessClassesActivity;
import com.licenta.fitnessclubs.FitnessClubsActivity;
import com.licenta.launchscreen.FirstScreenActivity;
import com.licenta.membercard.MemberCardActivity;
import com.licenta.membership.MembershipsActivity;
import com.licenta.membership.YourMembershipActivity;
import com.licenta.models.Club;
import com.licenta.models.User;
import com.licenta.personalinfo.PersonalInfoActivity;
import com.licenta.trainer.TrainerProfileActivity;
import com.licenta.trainer.TrainersActivity;
import com.licenta.trainerschedule.TrainerScheduleActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminDashboardActivity extends AppCompatActivity {

    public CardView cardViewClub, cardViewMemberships, cardViewClasses, cardViewLogout;
    private TextView clubName;
    private ApiCallClub apiCallClub;
    private ApiCallUser apiCallUser;
    private User user;
    private ImageView clubArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        clubName = findViewById(R.id.textViewAdminClub);
        clubArrow = findViewById(R.id.imageViewAdminClubChange);
        cardViewMemberships = findViewById(R.id.card_view_admin_membership);
        cardViewClasses = findViewById(R.id.card_view_admin_class_schedule);
        cardViewLogout = findViewById(R.id.card_view_admin_logout);
        cardViewClub = findViewById(R.id.card_view_admin_club);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallClub = retrofit.create(ApiCallClub.class);
        apiCallUser = retrofit.create(ApiCallUser.class);

        clubArrow.setOnClickListener(this::changeClub);
        clubName.setOnClickListener(this::changeClub);

        cardViewMemberships.setOnClickListener(this::onClick);
        cardViewLogout.setOnClickListener(this::onClick);
        cardViewClasses.setOnClickListener(this::onClick);
        cardViewClub.setOnClickListener(this::onClick);
    }

    private void changeClub(View view) {
        Intent i = new Intent(this, FitnessClubsActivity.class);
        startActivity(i);
    }

    private void getClub(Integer id) {
        Call<Club> callClub = apiCallClub.getClub(id);
        callClub.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    clubName.setText(response.body().getName());
                    getUser(Integer.valueOf(SaveSharedPreference.getId(AdminDashboardActivity.this)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void getUser(Integer id) {
        Call<User> callUser = apiCallUser.getUser(id);
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
            case R.id.card_view_admin_club:
                i = new Intent(this, AdminClubActivity.class);
                i.putExtra("id", Integer.parseInt(SaveSharedPreference.getClubId(this)));
                startActivity(i);
                break;
            case R.id.card_view_admin_membership:
                i = new Intent(this, MembershipsActivity.class);
                startActivity(i);
                break;
            case R.id.card_view_admin_class_schedule:
                i = new Intent(this, FitnessClassesActivity.class);
                startActivity(i);
                break;
            case R.id.card_view_admin_logout:
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

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SaveSharedPreference.getClubId(this).isEmpty()) {
            getClub(Integer.valueOf(SaveSharedPreference.getClubId(this)));
        }
    }
}