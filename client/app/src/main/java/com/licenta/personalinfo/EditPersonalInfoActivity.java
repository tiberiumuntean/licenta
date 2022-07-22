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
import com.licenta.models.Client;
import com.licenta.apis.ApiCallUser;
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

public class EditPersonalInfoActivity extends AppCompatActivity {
    private User user;
    private ApiCallUser apiCallUser;
    private Button updateButton;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPhone, editTextAddress;
    private TextView selectDate;
    private ImageView avatar;
    private final Integer REQUEST_OK = 1;

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_info);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCallUser = retrofit.create(ApiCallUser.class);

        updateButton = findViewById(R.id.buttonEditPI);
        editTextFirstName = findViewById(R.id.editTextEditPIFirstName);
        editTextLastName = findViewById(R.id.editTextEditPILastName);
        editTextEmail = findViewById(R.id.editTextEditPIEmail);
        editTextPhone = findViewById(R.id.editTextEditPIPhone);
        editTextAddress = findViewById(R.id.editTextEditPIAddress);
        avatar = findViewById(R.id.imageViewPIImage);

        updateButton.setOnClickListener(view -> {
            User newUser = user;
            newUser.setEmail(editTextEmail.getText().toString());

            Client newClient = user.getClient();
            newClient.setFirstName(editTextFirstName.getText().toString());
            newClient.setLastName(editTextLastName.getText().toString());
            newClient.setPhoneNumber(editTextPhone.getText().toString());
            newClient.setAddress(editTextAddress.getText().toString());
            newClient.setBirthday(GlobalData.parseDate(String.format("%s 00:00:00", selectDate.getText().toString())));

            newUser.setClient(newClient);

            if (!editTextFirstName.getText().toString().isEmpty() && !editTextLastName.getText().toString().isEmpty() && !editTextEmail.getText().toString().isEmpty() && !editTextPhone.getText().toString().isEmpty() && !selectDate.getText().toString().isEmpty()) {
                updateUser(view, newUser);
            } else {
                Toast.makeText(this, "All fields are required, except for the Address", Toast.LENGTH_LONG).show();
            }
        });

        avatar.setOnClickListener(view -> uploadFile());

        requestClient(Integer.parseInt(SaveSharedPreference.getId(this)));
        initDatePicker();
    }

    private void requestClient(int id) {
        Call<User> call = apiCallUser.getUser(id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();

                    editTextFirstName.setText(user.getClient().getFirstName());
                    editTextLastName.setText(user.getClient().getLastName());
                    editTextEmail.setText(user.getEmail());
                    editTextPhone.setText(user.getClient().getPhoneNumber());
                    editTextAddress.setText(user.getClient().getAddress());
                    if(user.getClient().getBirthday() != null) {
                        selectDate.setText(GlobalData.getDisplayDateRomanian(user.getClient().getBirthday()));
                    }

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
                    Toast.makeText(EditPersonalInfoActivity.this, "error", Toast.LENGTH_LONG).show();
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

    private void initDatePicker() {
        selectDate = findViewById(R.id.textViewEditTPIExperience);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        selectDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);

            long now = System.currentTimeMillis();
            datePickerDialog.getDatePicker().setMaxDate(now - 1000);
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
            //selectDate.setText(String.format(Locale.getDefault(),"%02d.%02d.%02d", day, month, year));
        };
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
                Toast.makeText(EditPersonalInfoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(EditPersonalInfoActivity.this, "You haven't picked any Image", Toast.LENGTH_LONG).show();
        }
    }
}