package com.licenta.membership;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClub;
import com.licenta.models.Club;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuyMembershipActivityV1 extends AppCompatActivity {
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private ApiCallClub apiCallClub;

    private Integer membershipId;
    private String membershipName;
    private Integer membershipDuration;
    private Double membershipPrice;

    private Club club;

    private TextView textViewDuration, textViewClub;
    private TextView selectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_membership_v1);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallClub = retrofit.create(ApiCallClub.class);

        Bundle extras = getIntent().getExtras();
        membershipId = extras.getInt("membership_id");
        membershipName = extras.getString("membership_name");
        membershipDuration = extras.getInt("membership_duration");
        membershipPrice = extras.getDouble("membership_price");

        textViewDuration = findViewById(R.id.textViewMembershipDuration);
        textViewDuration.setText(membershipDuration == 1 ? "1 month" : String.format("%d months", membershipDuration));

        textViewClub = findViewById(R.id.textViewMembershipClub);

        TextView membershipTitle = findViewById(R.id.textViewMembershipTitle);
        membershipTitle.setText(membershipName);

        selectDate = findViewById(R.id.textViewMembershipStartDate);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        selectDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(BuyMembershipActivityV1.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        onDateSetListener = (datePicker, year1, month1, day1) -> {
            month1 = month1 + 1;
            String monthString = String.valueOf(month1);
            String dayString = String.valueOf(day1);

            if (monthString.length() == 1) {
                monthString = "0" + monthString;
            }

            if (dayString.length() == 1) {
                dayString = "0" + dayString;
            }

            selectDate.setText(new StringBuilder().append(dayString).append(".").append(monthString).append(".").append(year1));
        };

        Button buttonGoToPayment = (Button) findViewById(R.id.buttonMembershipCheckout);
        buttonGoToPayment.setOnClickListener(view -> openGoToPaymentActivityV2());

        getClub(Integer.parseInt(SaveSharedPreference.getClubId(this)));
    }

    private void getClub(int id) {
        Call<Club> call = apiCallClub.getClub(id);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    club = response.body();

                    textViewClub.setText(club.getName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    public void openGoToPaymentActivityV2() {
        if(!selectDate.getText().toString().isEmpty()) {
            Intent intent = new Intent(this, BuyMembershipActivityV2.class);
            intent.putExtra("membership_id", membershipId);
            intent.putExtra("membership_name", membershipName);
            intent.putExtra("membership_duration", membershipDuration);
            intent.putExtra("membership_start_date", selectDate.getText().toString());
            intent.putExtra("membership_price", membershipPrice);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You must choose a start date!", Toast.LENGTH_LONG).show();
        }
    }
}