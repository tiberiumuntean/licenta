package com.licenta.paymentshistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallInvoice;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.Invoice;
import com.licenta.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentsHistoryActivity extends AppCompatActivity {
    private List<Invoice> invoices = new ArrayList<>();

    private ApiCallInvoice apiCallInvoice;
    private ApiCallUser apiCallUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_history);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallInvoice = retrofit.create(ApiCallInvoice.class);
        apiCallUser = retrofit.create(ApiCallUser.class);

        getUser(Integer.parseInt(SaveSharedPreference.getId(this)));
    }

    private void getUser(Integer id){
        Call<User> callUser = apiCallUser.getUser(id);
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    if(user.getClient() != null) {
                        requestInvoices(user.getClient().getId());
                    } else {
                        requestInvoices(user.getTrainer().getId());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void requestInvoices(int id) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPaymentsHistory);

        Call<List<Invoice>> call;

        if(user.getClient() != null) {
            call = apiCallInvoice.getInvoicesByClientId(id);
        } else {
            call = apiCallInvoice.getInvoicesByTrainerId(id);
        }

        call.enqueue(new Callback<List<Invoice>>() {
            @Override
            public void onResponse(@NonNull Call<List<Invoice>> call, @NonNull Response<List<Invoice>> response) {
                if (response.isSuccessful()) {
                    invoices = response.body();

                    LinearLayoutManager layoutManager = new LinearLayoutManager(PaymentsHistoryActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    PaymentsHistoryAdapter paymentsHistoryAdapter = new PaymentsHistoryAdapter(invoices);
                    recyclerView.setAdapter(paymentsHistoryAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Invoice>> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }
}