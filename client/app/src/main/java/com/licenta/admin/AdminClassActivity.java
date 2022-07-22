package com.licenta.admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.apis.ApiCallClub;
import com.licenta.apis.ApiCallFitnessClass;
import com.licenta.apis.ApiCallMembership;
import com.licenta.apis.ApiCallTrainer;
import com.licenta.fitnessclubs.ChooseFitnessClubActivity;
import com.licenta.models.Club;
import com.licenta.models.FitnessClass;
import com.licenta.models.Membership;
import com.licenta.models.Trainer;
import com.licenta.models.User;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminClassActivity extends AppCompatActivity {

    private final String ENTITY_NAME = "Fitness Class";

    private TextView textViewTitle, textViewDate;
    private Button buttonCreate, buttonDelete;
    private EditText editTextName, editTextDescription, editTextLocation, editTextFreeSpots;
    private SearchView searchViewTrainer;
    private ListView listViewTrainer;

    private ApiCallFitnessClass apiCall;
    private ApiCallClub apiCallClub;
    private ApiCallTrainer apiCallTrainer;

    private ArrayAdapter<Trainer> arrayAdapter;

    private FitnessClass entity = new FitnessClass();
    private Club secondaryEntity = new Club();
    private List<Trainer> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_class);

        initElements();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(GlobalData.RETROFIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCall = retrofit.create(ApiCallFitnessClass.class);
        apiCallClub = retrofit.create(ApiCallClub.class);
        apiCallTrainer = retrofit.create(ApiCallTrainer.class);

        getSecondaryEntity(Integer.parseInt(SaveSharedPreference.getClubId(this)));
        getList();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            getEntity(id);

            buttonDelete.setVisibility(View.VISIBLE);
            buttonDelete.setOnClickListener(view -> deleteEntity(id));

            buttonCreate.setText("Save Changes");
            buttonCreate.setOnClickListener(view -> updateEntity());
        } else {
            buttonCreate.setText(String.format("Create %s", ENTITY_NAME));
            buttonCreate.setOnClickListener(view -> createEntity());
        }

        initDatePicker();
    }

    private void getEntity(int id) {
        Call<FitnessClass> call = apiCall.getFitnessClass(id);
        call.enqueue(new Callback<FitnessClass>() {
            @Override
            public void onResponse(@NonNull Call<FitnessClass> call, @NonNull Response<FitnessClass> response) {
                if (response.isSuccessful()) {
                    entity = response.body();

                    updateElements(entity);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FitnessClass> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void createEntity() {
        buildEntity();

        Call<FitnessClass> call = apiCall.createFitnessClass(entity);
        call.enqueue(new Callback<FitnessClass>() {
            @Override
            public void onResponse(@NonNull Call<FitnessClass> call, @NonNull Response<FitnessClass> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminClassActivity.this, String.format("The %s has been successfully created!", ENTITY_NAME), Toast.LENGTH_SHORT).show();

                    goBackToMainScreen();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FitnessClass> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void updateEntity() {
        buildEntity();

        Call<FitnessClass> call = apiCall.updateFitnessClass(entity.getId(), entity);
        call.enqueue(new Callback<FitnessClass>() {
            @Override
            public void onResponse(@NonNull Call<FitnessClass> call, @NonNull Response<FitnessClass> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminClassActivity.this, String.format("The %s has been updated successfully!", ENTITY_NAME), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FitnessClass> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void deleteEntity(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(String.format("Delete this %s?", ENTITY_NAME));
        builder.setMessage(String.format("Are you sure you want to delete this %s? This action cannot be undone.", ENTITY_NAME));
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Call<Void> call = apiCall.deleteFitnessClass(id);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminClassActivity.this, String.format("The %s has been deleted successfully!", ENTITY_NAME), Toast.LENGTH_SHORT).show();

                        goBackToMainScreen();
                    } else {
                        Toast.makeText(AdminClassActivity.this, String.format("Cannot delete %s because it's linked to an existing user", ENTITY_NAME), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("Failure", t.getLocalizedMessage());
                }
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void buildEntity() {
        entity.setName(editTextName.getText().toString());
        entity.setDescription(editTextDescription.getText().toString());
        entity.setLocation(editTextLocation.getText().toString());
        entity.setFreeSpots(Integer.parseInt(editTextFreeSpots.getText().toString()));
        entity.setDate(GlobalData.parseDate(textViewDate.getText().toString()));

        for (int i = 0; i < list.size(); i++) {
            if (String.format("%s %s", list.get(i).getFirstName(), list.get(i).getLastName()).contentEquals(searchViewTrainer.getQuery())) {
                entity.setTrainer(list.get(i));
                break;
            }
        }

        entity.setClub_id(secondaryEntity.getId());
    }

    private void getSecondaryEntity(int id) {
        Call<Club> call = apiCallClub.getClub(id);
        call.enqueue(new Callback<Club>() {
            @Override
            public void onResponse(@NonNull Call<Club> call, @NonNull Response<Club> response) {
                if (response.isSuccessful()) {
                    secondaryEntity = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Club> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void getList() {
        Call<List<Trainer>> call = apiCallTrainer.getTrainers();
        call.enqueue(new Callback<List<Trainer>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<List<Trainer>> call, @NonNull Response<List<Trainer>> response) {
                if (response.isSuccessful()) {
                    list = response.body().stream().filter(trainer -> secondaryEntity.getUsers().stream().map(User::getId).collect(Collectors.toList()).contains(trainer.getUser_id())).collect(Collectors.toList());

                    arrayAdapter = new ArrayAdapter<Trainer>(AdminClassActivity.this, android.R.layout.simple_list_item_1, list);
                    listViewTrainer.setAdapter(arrayAdapter);

                    searchViewTrainer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            AdminClassActivity.this.arrayAdapter.getFilter().filter(query);

                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            AdminClassActivity.this.arrayAdapter.getFilter().filter(newText);
                            listViewTrainer.setVisibility(View.VISIBLE);
                            return false;
                        }

                    });

                    searchViewTrainer.setOnClickListener(view -> listViewTrainer.setVisibility(View.VISIBLE));

                    listViewTrainer.setOnItemClickListener((adapterView, view, i, l) -> {
                        searchViewTrainer.setIconified(false);
                        searchViewTrainer.setQuery(String.format("%s %s", list.get(i).getFirstName(), list.get(i).getLastName()), true);
                        listViewTrainer.setVisibility(View.INVISIBLE);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Trainer>> call, @NonNull Throwable t) {
                Log.e("Failure", t.getLocalizedMessage());
            }
        });
    }

    private void initElements() {
        textViewTitle = findViewById(R.id.textViewAdminClassTitle);
        textViewDate = findViewById(R.id.textViewAdminClassDate);

        buttonCreate = findViewById(R.id.buttonAdminClassCreate);
        buttonDelete = findViewById(R.id.buttonAdminClassDelete);

        editTextName = findViewById(R.id.editTextAdminClassName);
        editTextDescription = findViewById(R.id.editTextAdminClassDescription);
        editTextLocation = findViewById(R.id.editTextAdminClassLocation);
        editTextFreeSpots = findViewById(R.id.editTextAdminClassFreeSpots);

        searchViewTrainer = findViewById(R.id.searchViewAdminClassTrainer);
        listViewTrainer = findViewById(R.id.listViewAdminClassTrainer);
    }

    private void updateElements(FitnessClass entity) {
        textViewTitle.setText(entity.getName());
        textViewDate.setText(GlobalData.getDisplayDateTimeRomanian(entity.getDate()));

        editTextName.setText(entity.getName());
        editTextDescription.setText(entity.getDescription());
        editTextLocation.setText(entity.getLocation());
        editTextFreeSpots.setText(String.valueOf(entity.getFreeSpots()));

        searchViewTrainer.setQuery(String.format("%s %s", entity.getTrainer().getFirstName(), entity.getTrainer().getLastName()), false);
        searchViewTrainer.setIconified(false);
    }

    private void goBackToMainScreen() {
        finish();
    }

    private void initDatePicker() {
        textViewDate.setOnClickListener(view -> {
            final Calendar currentDate = Calendar.getInstance();
            Calendar date = Calendar.getInstance();
            new DatePickerDialog(this, (view12, year, monthOfYear, dayOfMonth) -> {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(AdminClassActivity.this, (TimePickerDialog.OnTimeSetListener) (view1, hourOfDay, minute) -> {
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);

                    String monthString = String.valueOf(monthOfYear + 1);
                    String dayString = String.valueOf(dayOfMonth);

                    if (monthString.length() == 1) {
                        monthString = "0" + monthString;
                    }
                    if (dayString.length() == 1) {
                        dayString = "0" + dayString;
                    }

                    textViewDate.setText(new StringBuilder().append(dayString).append(".").append(monthString).append(".").append(year).append(" ").append(pad(hourOfDay)).append(":").append(pad(minute)).append(":00"));
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
        });
    }

    private String pad(int input) {
        String str = "";
        if (input >= 10) {
            str = Integer.toString(input);
        } else {
            str = "0" + input;
        }

        return str;
    }
}