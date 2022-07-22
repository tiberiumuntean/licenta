package com.licenta.membership;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.admin.AdminMembershipActivity;
import com.licenta.apis.ApiCallMembership;
import com.licenta.models.Membership;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MembershipsActivity extends AppCompatActivity implements MembershipRecyclerViewInterface {
    private List<Membership> memberships = new ArrayList<>();

    private ApiCallMembership apiCallMembership;
    
    private Button buttonCreateMembership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberships);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallMembership = retrofit.create(ApiCallMembership.class);

        buttonCreateMembership = findViewById(R.id.buttonMembershipsCreate);
        
        if(SaveSharedPreference.getIsAdmin(this)){
            buttonCreateMembership.setVisibility(View.VISIBLE);
        }
        
        buttonCreateMembership.setOnClickListener(view -> {
            Intent i = new Intent(MembershipsActivity.this, AdminMembershipActivity.class);
            startActivity(i);
        });
    }

    private void requestMemberships() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMemberships);

        Call<List<Membership>> call = apiCallMembership.getMemberships();
        call.enqueue(new Callback<List<Membership>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<List<Membership>> call, @NonNull Response<List<Membership>> response) {
                if (response.isSuccessful()) {
                    memberships = response.body().stream().filter(membership -> membership.getClub().getId() == Integer.parseInt(SaveSharedPreference.getClubId(MembershipsActivity.this))).collect(Collectors.toList());

                    LinearLayoutManager layoutManager = new LinearLayoutManager(MembershipsActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    MembershipAdapter membershipAdapter = new MembershipAdapter(memberships, MembershipsActivity.this);
                    recyclerView.setAdapter(membershipAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Membership>> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Membership clickedMembership = memberships.get(position);

        if(SaveSharedPreference.getIsAdmin(this)){
            Intent intent = new Intent(this, AdminMembershipActivity.class);
            intent.putExtra("id", clickedMembership.getId());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, BuyMembershipActivityV1.class);
            intent.putExtra("membership_name", clickedMembership.getName());
            intent.putExtra("membership_id", clickedMembership.getId());
            intent.putExtra("membership_duration", clickedMembership.getDuration());
            intent.putExtra("membership_price", clickedMembership.getPrice());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestMemberships();
    }
}