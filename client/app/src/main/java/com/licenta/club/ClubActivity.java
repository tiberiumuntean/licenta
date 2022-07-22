package com.licenta.club;

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
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.licenta.GlobalData;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClub;
import com.licenta.fitnessclubs.ChooseFitnessClubActivity;
import com.licenta.fitnessclubs.FitnessClubsActivity;
import com.licenta.membercard.MemberCardActivity;
import com.licenta.membership.MembershipsActivity;
import com.licenta.membership.YourMembershipActivity;
import com.licenta.models.Club;
import com.licenta.personalinfo.PersonalInfoActivity;
import com.licenta.R;
import com.licenta.trainer.TrainerProfileActivity;
import com.licenta.trainerschedule.TrainerScheduleActivity;
import com.licenta.fitnessclass.FitnessClassesActivity;
import com.licenta.client.ClientsActivity;
import com.licenta.trainer.TrainersActivity;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClubActivity extends AppCompatActivity implements View.OnClickListener {

    public CardView cardViewMemberCard, cardViewPersonalInfo, cardViewTrainers, cardViewYourMembership, cardViewClassSchedule, cardViewClubInfo, cardViewClients, cardViewSchedule;
    private TextView clubName, clubMembership, clubTrainer;
    private ImageView clubArrow;
    private ApiCallClub apiCallClub;
    private ApiCallUser apiCallUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        clubName = findViewById(R.id.textViewClubActivityClubName);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallClub = retrofit.create(ApiCallClub.class);
        apiCallUser = retrofit.create(ApiCallUser.class);

        clubMembership = findViewById(R.id.textViewClubMembership);
        clubTrainer = findViewById(R.id.textViewClubTrainers);

        clubArrow = findViewById(R.id.imageViewClubChange);

        cardViewMemberCard = findViewById(R.id.card_view_member_card);
        cardViewPersonalInfo = findViewById(R.id.card_view_personal_info);
        cardViewTrainers = findViewById(R.id.card_view_trainers);
        cardViewYourMembership = findViewById(R.id.card_view_your_membership);
        cardViewClassSchedule = findViewById(R.id.card_view_class_schedule);
        cardViewClubInfo = findViewById(R.id.card_view_club_info);
        cardViewClients = findViewById(R.id.card_view_clients);
        cardViewSchedule = findViewById(R.id.card_view_schedule);

        cardViewMemberCard.setOnClickListener(this);
        cardViewPersonalInfo.setOnClickListener(this);
        cardViewTrainers.setOnClickListener(this);
        cardViewYourMembership.setOnClickListener(this);
        cardViewClassSchedule.setOnClickListener(this);
        cardViewClubInfo.setOnClickListener(this);
        cardViewClients.setOnClickListener(this);
        cardViewSchedule.setOnClickListener(this);

        clubArrow.setOnClickListener(this::changeClub);
        clubName.setOnClickListener(this::changeClub);
    }

    private void changeClub(View view) {
        Intent i = new Intent(this, FitnessClubsActivity.class);
        startActivity(i);
    }

    private void getClub(Integer id){
        Call<Club> callClub = apiCallClub.getClub(id);
        callClub.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    clubName.setText(response.body().getName());
                    getUser(Integer.valueOf(SaveSharedPreference.getId(ClubActivity.this)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void getUser(Integer id){
        Call<User> callUser = apiCallUser.getUser(id);
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    if(user.getClient() == null && user.getRole() == 1){
                        cardViewClients.setVisibility(View.VISIBLE);
                        cardViewSchedule.setVisibility(View.VISIBLE);
                        cardViewTrainers.setVisibility(View.GONE);
                        cardViewYourMembership.setVisibility(View.GONE);
                        cardViewClassSchedule.setVisibility(View.GONE);

                        SaveSharedPreference.setTrainerId(ClubActivity.this, String.valueOf(user.getTrainer().getId()));
                    } else if(user.getRole() == 0) {
                        SaveSharedPreference.setClientId(ClubActivity.this, String.valueOf(user.getClient().getId()));
                    }

                    if(user.getClient() != null && user.getClient().getMembershipByClub(Integer.parseInt(SaveSharedPreference.getClubId(ClubActivity.this))) != null) {
                        clubMembership.setText("Your Membership");
                    } else {
                        clubMembership.setText("Memberships List");
                    }

                    if(user.getClient() != null && user.getClient().getTrainerId() != null) {
                        clubTrainer.setText("Your Trainer");
                    } else {
                        clubTrainer.setText("Trainers");
                    }
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
            case R.id.card_view_member_card:
                if(user.getClient() != null && user.getClient().getMembershipByClub(Integer.parseInt(SaveSharedPreference.getClubId(ClubActivity.this))) != null) {
                    i = new Intent(this, MemberCardActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(this, "You don't have an active membership!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.card_view_personal_info:
                i = new Intent(this, PersonalInfoActivity.class);
                startActivity(i);
                break;
            case R.id.card_view_trainers:
                if(user.getClient() != null && user.getClient().getTrainerId() != null) {
                    i = new Intent(this, TrainerProfileActivity.class);
                    i.putExtra("trainer_id", user.getClient().getTrainerId());
                } else {
                    i = new Intent(this, TrainersActivity.class);
                }
                startActivity(i);
                break;
            case R.id.card_view_your_membership:
                if(user.getClient() != null && user.getClient().getMembershipByClub(Integer.parseInt(SaveSharedPreference.getClubId(ClubActivity.this))) != null) {
                    i = new Intent(this, YourMembershipActivity.class);
                } else {
                    i = new Intent(this, MembershipsActivity.class);
                }
                startActivity(i);
                break;
            case R.id.card_view_class_schedule:
                i = new Intent(this, FitnessClassesActivity.class);
                startActivity(i);
                break;
            case R.id.card_view_club_info:
                i = new Intent(this, ClubInfoActivity.class);
                startActivity(i);
                break;
            case R.id.card_view_clients:
                i = new Intent(this, ClientsActivity.class);
                startActivity(i);
                break;
            case R.id.card_view_schedule:
                i = new Intent(this, TrainerScheduleActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!SaveSharedPreference.getClubId(this).isEmpty()) {
            getClub(Integer.valueOf(SaveSharedPreference.getClubId(this)));
        }
    }
}