package com.licenta.client;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClient;
import com.licenta.apis.ApiCallClub;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.Client;
import com.licenta.models.Club;
import com.licenta.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientsActivity extends AppCompatActivity implements ClientsRecyclerViewInterface {
    private ApiCallUser apiCallUser;
    private ApiCallClub apiCallClub;
    private User user;
    private List<Client> clientsList;
    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);
        apiCallClub = retrofit.create(ApiCallClub.class);

        getClub(Integer.valueOf(SaveSharedPreference.getClubId(this)));
    }

    private void getClub(Integer id) {
        Call<Club> call = apiCallClub.getClub(id);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if(response.isSuccessful()){
                    club = response.body();

                    getUser(Integer.parseInt(SaveSharedPreference.getId(ClientsActivity.this)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void getUser(Integer id) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewClientss);

        Call<User> callUser = apiCallUser.getUser(id);
        callUser.enqueue(new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    List<Integer> clubUsersIds = club.getUsers().stream().map(User::getId).collect(Collectors.toList());
                    clientsList = new ArrayList<>(user.getTrainer().getClients()).stream().filter(client -> clubUsersIds.contains(client.getUser_id())).collect(Collectors.toList());

                    LinearLayoutManager layoutManager = new LinearLayoutManager(ClientsActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    ClientAdapter clientAdapter = new ClientAdapter(clientsList, ClientsActivity.this);
                    recyclerView.setAdapter(clientAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        int clientId = clientsList.get(position).getId();

        Intent intent = new Intent(this, ClientProfileActivity.class);
        intent.putExtra("client_id", clientId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getClub(Integer.parseInt(SaveSharedPreference.getClubId(this)));
    }
}