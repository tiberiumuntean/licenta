package com.licenta.trainer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClient;
import com.licenta.apis.ApiCallInvoice;
import com.licenta.apis.ApiCallMembership;
import com.licenta.apis.ApiCallReview;
import com.licenta.apis.ApiCallTrainer;
import com.licenta.club.ClubActivity;
import com.licenta.membership.BuyMembershipActivityV2;
import com.licenta.models.CheckoutProduct;
import com.licenta.models.Client;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.ClientSecret;
import com.licenta.models.Invoice;
import com.licenta.models.Review;
import com.licenta.models.Trainer;
import com.licenta.models.User;
import com.squareup.picasso.Picasso;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrainerProfileActivity extends AppCompatActivity {
    private Trainer trainer;
    private List<Review> reviewList = new ArrayList<>();
    private int trainerId;
    private ApiCallTrainer apiCallTrainer;
    private ApiCallReview apiCallReview;
    private ApiCallClient apiCallClient;
    private ApiCallMembership apiCallMembership;
    private ApiCallInvoice apiCallInvoice;
    private ApiCallUser apiCallUser;
    private Button buttonHireTrainer;
    private Button buttonDismissTrainer;
    private Button buttonMessageTrainer;
    private Button buttonReviewTrainer;
    private ImageView avatar;

    private final String pk = "pk_test_51L5SIKGu5a3fHJbNvuAcKKWTdrC5DFiACL5ubh2ia2P1a1Xlfn8m8kdg26sLdbh3Ij5uVXKDSSSllco4bAqfWvKI00XCq05L8n";
    private PaymentSheet paymentSheet;
    private ProgressDialog progressDialog;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static SecureRandom rnd = new SecureRandom();
    private TextView activeClients;

    private User user;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trainerId = extras.getInt("trainer_id");
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        apiCallTrainer = retrofit.create(ApiCallTrainer.class);
        apiCallReview = retrofit.create(ApiCallReview.class);
        apiCallUser = retrofit.create(ApiCallUser.class);
        apiCallClient = retrofit.create(ApiCallClient.class);
        apiCallMembership = retrofit.create(ApiCallMembership.class);
        apiCallInvoice = retrofit.create(ApiCallInvoice.class);

        avatar = findViewById(R.id.imageViewTrainerProfileAvatar);

        buttonHireTrainer = findViewById(R.id.buttonTrainerProfileHireTrainer);
        buttonHireTrainer.setOnClickListener(view -> hireOrDismissTrainer(true));

        buttonDismissTrainer = findViewById(R.id.buttonTrainerProfileDismissTrainer);
        buttonMessageTrainer = findViewById(R.id.buttonTrainerProfileMessageTrainer);

        buttonMessageTrainer.setOnClickListener(view -> messageTrainer());
        buttonDismissTrainer.setOnClickListener(view -> hireOrDismissTrainer(false));

        buttonReviewTrainer = findViewById(R.id.buttonTrainerProfileWriteReview);
        buttonReviewTrainer.setOnClickListener(view -> showReviewPopup());

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Preparing transaction...");
        progressDialog.setCancelable(false);

        queryTrainer();
    }

    private void messageTrainer() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
            }
        } else {
            if (trainer.getPhoneNumber() != null) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + trainer.getPhoneNumber()));
                intent.putExtra("sms_body", String.format("Hey, %s %s!", trainer.getFirstName(), trainer.getLastName()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "This Trainer doesn't have a Phone Number. Try sending a message on Social Media", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void hireOrDismissTrainer(boolean isHireAction) {
        if (isHireAction) {
            user.getClient().setTrainer(trainer);
            startCheckout();
            activeClients.setText(String.valueOf(trainer.getActiveClients() + 1));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Dismiss this trainer?");
            builder.setMessage("Are you sure you want to dismiss your trainer? This action cannot be undone.");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                user.getClient().setTrainer(null);
                user.getClient().setTrainerId(null);
                updateClient(user.getClient(), false);
                activeClients.setText(String.valueOf(trainer.getActiveClients() - 1));
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void getUser() {
        Call<User> call = apiCallUser.getUser(Integer.parseInt(SaveSharedPreference.getId(this)));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    if (user.getClient().getTrainerId() == null) {
                        buttonHireTrainer.setVisibility(View.VISIBLE);
                        buttonDismissTrainer.setVisibility(View.INVISIBLE);
                        buttonMessageTrainer.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void updateClient(Client client, boolean isHireAction) {
        Call<Client> call = apiCallClient.updateClient(client.getId(), client);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(@NonNull Call<Client> call, @NonNull Response<Client> response) {
                if (response.isSuccessful()) {
                    if (isHireAction) {
                        buttonHireTrainer.setVisibility(View.INVISIBLE);
                        buttonDismissTrainer.setVisibility(View.VISIBLE);
                        buttonMessageTrainer.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(TrainerProfileActivity.this, String.format("%s %s is no longer your trainer!", trainer.getFirstName(), trainer.getLastName()), Toast.LENGTH_LONG).show();
                        buttonHireTrainer.setVisibility(View.VISIBLE);
                        buttonDismissTrainer.setVisibility(View.INVISIBLE);
                        buttonMessageTrainer.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void queryTrainer() {
        Call<Trainer> call = apiCallTrainer.getTrainer(trainerId);

        TextView name = findViewById(R.id.textViewTrainerProfileName);
        TextView registrationDate = findViewById(R.id.textViewTrainerProfileRegistrationDate);
        TextView workExperience = findViewById(R.id.textViewTrainerProfileWorkExperience);
        activeClients = findViewById(R.id.textViewTrainerProfileActiveClients);
        TextView description = findViewById(R.id.textViewTrainerProfileDescription);
        TextView facebook = findViewById(R.id.textViewTrainerProfileFacebook);
        TextView instagram = findViewById(R.id.textViewTrainerProfileInstagram);

        RecyclerView recyclerView = findViewById(R.id.recycleViewReviews);

        call.enqueue(new Callback<Trainer>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<Trainer> call, @NonNull Response<Trainer> response) {
                if (response.isSuccessful()) {
                    trainer = response.body();

                    name.setText(String.format("%s %s", trainer.getFirstName(), trainer.getLastName()));
                    registrationDate.setText(String.valueOf(trainer.getRegistrationDate().split("-")[0]));
                    workExperience.setText(String.format("%s years", GlobalData.calculateAge(String.format("%d-01-01", trainer.getWorkExperience()))));
                    activeClients.setText(String.valueOf(trainer.getActiveClients()));
                    description.setText(trainer.getDescription() != null ? String.valueOf(trainer.getDescription()) : "No Description Available");
                    facebook.setText(trainer.getFacebook() != null ? String.valueOf(trainer.getFacebook()) : "No Account Available");
                    instagram.setText(trainer.getInstagram() != null ? String.valueOf(trainer.getInstagram()) : "No Account Available");

                    buttonHireTrainer.setText(String.format("Hire %s %s now\n(%s RON/month)", trainer.getFirstName(), trainer.getLastName(), trainer.getPrice()));

                    reviewList = new ArrayList<>(trainer.getReviews());

                    LinearLayoutManager layoutManager = new LinearLayoutManager(TrainerProfileActivity.this);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviewList);
                    recyclerView.setAdapter(reviewsAdapter);

                    avatar.setClipToOutline(true);
                    Picasso.get().load(String.format("%susers/%d/avatar/", GlobalData.RETROFIT_BASE_URL, trainer.getUser_id())).placeholder(R.drawable.profile_picture).error(R.drawable.profile_picture).into(avatar);

                    getUser();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Trainer> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void showReviewPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Write your review");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_rating, null);
        builder.setView(customLayout);

        EditText input = customLayout.findViewById(R.id.editTextTrainerReview);
        RatingBar ratingBar = customLayout.findViewById(R.id.ratingBarTrainerReview);

        builder.setPositiveButton("Send Review", (dialog, which) -> {
            Review review = new Review();
            review.setReview(input.getText().toString());
            review.setClient(user.getClient());
            review.setRating(ratingBar.getRating());
            review.setTrainer(trainer);
            review.setCreationDate(GlobalData.getTodaysDate());

            if (!input.getText().toString().isEmpty() && ratingBar.getRating() > 0) {
                createReview(review);
            } else {
                Toast.makeText(this, "The Review cannot be empty", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createReview(Review review) {
        Call<Review> call = apiCallReview.createReview(review);
        call.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    Review reviewResponse = response.body();

                    reviewList.add(reviewResponse);

                    Toast.makeText(TrainerProfileActivity.this, "The review has been added!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
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
                    messageTrainer();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission not granted. Try again!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, String.format("The purchase has been completed successfully. You hired %s %s!", trainer.getFirstName(), trainer.getLastName()), Toast.LENGTH_LONG).show();

            updateClient(user.getClient(), true);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date today = Calendar.getInstance().getTime();
            String todayDate = df.format(today);
            Random rand = new Random();

            Invoice invoice = new Invoice();
            invoice.setClient(user.getClient());
            invoice.setMembership(null);
            invoice.setClub(user.getClubs().stream().filter(club -> club.getId() == Integer.parseInt(SaveSharedPreference.getClubId(TrainerProfileActivity.this))).collect(Collectors.toList()).get(0));
            invoice.setTrainer(trainer);
            invoice.setDate(todayDate);
            invoice.setSeries(randomString(5));
            invoice.setNumber(rand.nextInt(1000));
            createInvoice(invoice);

            progressDialog.hide();
        }
    }

    private void startCheckout() {
        progressDialog.show();

        CheckoutProduct checkoutProduct = new CheckoutProduct(String.format("Trainer Membership - %s %s!", trainer.getFirstName(), trainer.getLastName()), trainer.getPrice());

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
                Toast.makeText(TrainerProfileActivity.this, "Error StartCheckout", Toast.LENGTH_LONG).show();
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
                }
            }

            @Override
            public void onFailure(@NonNull Call<Invoice> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}