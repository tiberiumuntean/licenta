package com.licenta.personalinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallUser;
import com.licenta.models.Trainer;
import com.licenta.models.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditTrainerPersonalInfoActivity extends AppCompatActivity {

    private User user;
    private Trainer trainer;
    private ApiCallUser apiCallUser;
    private Button updateButton;
    private EditText editTextExperience, editTextFirstName, editTextLastName, editTextEmail, editTextPhone, editTextAddress, editTextDescription, editTextFacebook, editTextInstagram, editTextFreeSpots, editTextPrice;
    private ImageView avatar;
    private final Integer REQUEST_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trainer_personal_info);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);

        updateButton = findViewById(R.id.buttonEditTPI);
        editTextFirstName = findViewById(R.id.editTextEditTPIFirstName);
        editTextLastName = findViewById(R.id.editTextEditTPILastName);
        editTextEmail = findViewById(R.id.editTextEditTPIEmail);
        editTextPhone = findViewById(R.id.editTextEditTPIPhone);
        editTextAddress = findViewById(R.id.editTextEditTPIAddress);
        editTextDescription = findViewById(R.id.editTextEditTPIDescription);
        editTextFacebook = findViewById(R.id.editTextEditTPIFacebook);
        editTextInstagram = findViewById(R.id.editTextEditTPIInstagram);
        editTextFreeSpots = findViewById(R.id.editTextEditTPIFreeSpots);
        editTextPrice = findViewById(R.id.editTextEditTPIPrice);
        editTextExperience = findViewById(R.id.textViewEditTPIExperience);

        avatar = findViewById(R.id.imageViewTPIImage);

        updateButton.setOnClickListener(view -> {
            user.setEmail(editTextEmail.getText().toString());

            Trainer newTrainer = user.getTrainer();
            newTrainer.setFirstName(editTextFirstName.getText().toString());
            newTrainer.setLastName(editTextLastName.getText().toString());
            newTrainer.setPhoneNumber(editTextPhone.getText().toString());
            newTrainer.setAddress(editTextAddress.getText().toString());
            newTrainer.setDescription(editTextDescription.getText().toString());
            newTrainer.setFacebook(editTextFacebook.getText().toString());
            newTrainer.setInstagram(editTextInstagram.getText().toString());
            newTrainer.setFreeSpots(Integer.parseInt(editTextFreeSpots.getText().toString()));
            newTrainer.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
            newTrainer.setWorkExperience(Integer.parseInt(editTextExperience.getText().toString()));
            newTrainer.setClients(null);
            newTrainer.setReviews(null);

            user.setTrainer(newTrainer);

            if (Integer.parseInt(editTextExperience.getText().toString()) < 1900 || Integer.parseInt(editTextExperience.getText().toString()) > 2022) {
                Toast.makeText(this, "The Work Experience must be a valid year between 1900 and 2022", Toast.LENGTH_LONG).show();
            } else {
                if (!editTextFirstName.getText().toString().isEmpty() &&
                        !editTextLastName.getText().toString().isEmpty() &&
                        !editTextEmail.getText().toString().isEmpty() &&
                        !editTextPhone.getText().toString().isEmpty() &&
                        !editTextExperience.getText().toString().isEmpty() &&
                        !editTextDescription.getText().toString().isEmpty() &&
                        !editTextFreeSpots.getText().toString().isEmpty() &&
                        !editTextPrice.getText().toString().isEmpty()) {
                    updateUser(view, user);
                } else {
                    Toast.makeText(this, "All fields are required, except for the Address and the Social Media Profiles", Toast.LENGTH_LONG).show();
                }
            }
        });

        avatar.setOnClickListener(view -> uploadFile());

        requestTrainer(Integer.parseInt(SaveSharedPreference.getId(this)));
    }

    private void requestTrainer(int id) {
        Call<User> call = apiCallUser.getUser(id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    editTextFirstName.setText(user.getTrainer().getFirstName());
                    editTextLastName.setText(user.getTrainer().getLastName());
                    editTextEmail.setText(user.getEmail());
                    editTextPhone.setText(user.getTrainer().getPhoneNumber());
                    editTextAddress.setText(user.getTrainer().getAddress());
                    editTextDescription.setText(user.getTrainer().getDescription());
                    editTextFacebook.setText(user.getTrainer().getFacebook());
                    editTextInstagram.setText(user.getTrainer().getInstagram());
                    editTextFreeSpots.setText(String.valueOf(user.getTrainer().getFreeSpots()));
                    editTextPrice.setText(String.valueOf(user.getTrainer().getPrice()));
                    editTextExperience.setText(String.valueOf(user.getTrainer().getWorkExperience()));

                    avatar.setClipToOutline(true);

                    Picasso.get().load(String.format("%susers/%d/avatar/", GlobalData.RETROFIT_BASE_URL, id)).placeholder(R.drawable.profile_picture).error(R.drawable.profile_picture).into(avatar);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void updateUser(View view, User newUser) {
        Call<User> call = apiCallUser.updateUser(newUser.getId(), newUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    Snackbar.make(view, "User Updated!", Snackbar.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditTrainerPersonalInfoActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void uploadFile() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_OK);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == -1) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] b = baos.toByteArray();
                String base64Image = Base64.encodeToString(b, Base64.DEFAULT);
                user.setImage(base64Image);
                this.updateUser(findViewById(android.R.id.content).getRootView(), user);

                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                avatar.setImageBitmap(decodedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(EditTrainerPersonalInfoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(EditTrainerPersonalInfoActivity.this, "You haven't picked any Image", Toast.LENGTH_LONG).show();
        }
    }
}