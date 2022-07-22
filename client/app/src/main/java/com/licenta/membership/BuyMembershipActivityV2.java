package com.licenta.membership;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClient;
import com.licenta.apis.ApiCallClientMembership;
import com.licenta.apis.ApiCallInvoice;
import com.licenta.apis.ApiCallMembership;
import com.licenta.models.Client;
import com.licenta.club.ClubActivity;
import com.licenta.models.CheckoutProduct;
import com.licenta.models.ClientMembership;
import com.licenta.models.ClientSecret;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.Invoice;
import com.licenta.models.Membership;
import com.licenta.models.Review;
import com.licenta.models.User;
import com.licenta.trainer.TrainerProfileActivity;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuyMembershipActivityV2 extends AppCompatActivity {
    private ApiCallMembership apiCallMembership;
    private ApiCallUser apiCallUser;
    private ApiCallClient apiCallClient;
    private ApiCallInvoice apiCallInvoice;
    private ApiCallClientMembership apiCallClientMembership;

    private RadioButton radioButtonInfo, radioButtonTerms;

    private Integer membershipId;
    private String membershipName;
    private Integer membershipDuration;
    private String membershipStartDate;
    private Double membershipPrice;

    private User user;
    private Membership membership;

    private final String pk = "pk_test_51L5SIKGu5a3fHJbNvuAcKKWTdrC5DFiACL5ubh2ia2P1a1Xlfn8m8kdg26sLdbh3Ij5uVXKDSSSllco4bAqfWvKI00XCq05L8n";
    private PaymentSheet paymentSheet;
    private ProgressDialog progressDialog;

    private TextView textViewTitle, textViewPrice;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static SecureRandom rnd = new SecureRandom();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_membership_v2);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);
        apiCallMembership = retrofit.create(ApiCallMembership.class);
        apiCallClient = retrofit.create(ApiCallClient.class);
        apiCallInvoice = retrofit.create(ApiCallInvoice.class);
        apiCallClientMembership = retrofit.create(ApiCallClientMembership.class);

        Bundle extras = getIntent().getExtras();
        membershipId = extras.getInt("membership_id");
        membershipName = extras.getString("membership_name");
        membershipDuration = extras.getInt("membership_duration");
        membershipStartDate = extras.getString("membership_start_date");
        membershipPrice = extras.getDouble("membership_price");

        textViewTitle = findViewById(R.id.textViewCheckoutTitle);
        textViewTitle.setText(membershipName);

        textViewPrice = findViewById(R.id.textViewCheckoutPrice);
        textViewPrice.setText(String.format("%s RON", membershipPrice));

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        Button checkoutButton = findViewById(R.id.buttonCheckoutBuy);
        checkoutButton.setOnClickListener(view -> startCheckout());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Preparing transaction...");
        progressDialog.setCancelable(false);

        radioButtonInfo = findViewById(R.id.radioButtonCheckoutValid);
        radioButtonTerms = findViewById(R.id.radioButtonCheckoutButton);

        requestClient(Integer.parseInt(SaveSharedPreference.getId(this)));
        requestMembership(membershipId);
    }

    private void requestClient(int id) {
        Call<User> call = apiCallUser.getUser(id);

        EditText firstName = findViewById(R.id.editTextCheckoutFirstName);
        EditText lastName = findViewById(R.id.editTextCheckoutLastName);
        EditText email = findViewById(R.id.editTextCheckoutEmail);
        EditText phone = findViewById(R.id.editTextCheckoutPhone);
        EditText address = findViewById(R.id.editTextCheckoutAddress);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    firstName.setText(user.getClient().getFirstName());
                    lastName.setText(user.getClient().getLastName());
                    email.setText(user.getEmail());
                    phone.setText(user.getClient().getPhoneNumber());
                    address.setText(user.getClient().getAddress());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void startCheckout() {
        if (radioButtonInfo.isChecked() && radioButtonTerms.isChecked()) {
            progressDialog.show();

            CheckoutProduct checkoutProduct = new CheckoutProduct(membershipName, membershipPrice);

            Call<ClientSecret> call = apiCallMembership.createPaymentIntent(checkoutProduct);
            call.enqueue(new Callback<ClientSecret>() {
                @Override
                public void onResponse(@NonNull Call<ClientSecret> call, @NonNull Response<ClientSecret> response) {
                    if (response.isSuccessful()) {
                        String clientSecret = response.body().getClientSecret();

                        PaymentConfiguration.init(getApplicationContext(), pk);
                        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Gym Land").allowsDelayedPaymentMethods(true).build();
                        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ClientSecret> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(BuyMembershipActivityV2.this, "Error StartCheckout", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(BuyMembershipActivityV2.this, "You must agree with the Terms and confirm that the data is valid", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(BuyMembershipActivityV2.this, "Cancelled", Toast.LENGTH_LONG).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(BuyMembershipActivityV2.this, "Error", Toast.LENGTH_LONG).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(BuyMembershipActivityV2.this, "Your Membership has been purchased successfully!", Toast.LENGTH_LONG).show();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime startDate = LocalDateTime.parse(membershipStartDate + " 00:00:00", formatter);
            LocalDateTime endDate = startDate.plusMonths(membershipDuration);

            Client client = user.getClient();
            ClientMembership clientMembership = new ClientMembership();
            clientMembership.setMembership(membership);
            clientMembership.setClient(client);
            clientMembership.setMembershipStartDate(startDate.format(formatterOutput));
            clientMembership.setMembershipEndDate(endDate.format(formatterOutput));

            createClientMembership(clientMembership);
        }
    }

    private void requestMembership(int id) {
        Call<Membership> call = apiCallMembership.getMembership(id);

        call.enqueue(new Callback<Membership>() {
            @Override
            public void onResponse(@NonNull Call<Membership> call, @NonNull Response<Membership> response) {
                if(response.isSuccessful()){
                    membership = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Membership> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void createInvoice(Invoice invoice) {
        Call<Invoice> call = apiCallInvoice.createInvoice(invoice);
        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(@NonNull Call<Invoice> call, @NonNull Response<Invoice> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();

                    finishAffinity();
                    Intent intent = new Intent(BuyMembershipActivityV2.this, ClubActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Invoice> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void createClientMembership(ClientMembership clientMembership) {
        Call<ClientMembership> call = apiCallClientMembership.createClientMembership(clientMembership);
        call.enqueue(new Callback<ClientMembership>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ClientMembership> call, Response<ClientMembership> response) {
                if(response.isSuccessful()){
                    ClientMembership clientMembership1 = response.body();

                    Random rand = new Random();

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date today = Calendar.getInstance().getTime();
                    String todayDate = df.format(today);

                    Invoice invoice = new Invoice();
                    invoice.setClient(user.getClient());
                    invoice.setMembership(clientMembership1.getMembership());
                    invoice.setClub(user.getClubs().stream().filter(club -> club.getId() == Integer.parseInt(SaveSharedPreference.getClubId(BuyMembershipActivityV2.this))).collect(Collectors.toList()).get(0));
                    invoice.setTrainer(null);
                    invoice.setDate(todayDate);
                    invoice.setSeries(randomString(5));
                    invoice.setNumber(rand.nextInt(1000));

                    createInvoice(invoice);
                }
            }

            @Override
            public void onFailure(Call<ClientMembership> call, Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}