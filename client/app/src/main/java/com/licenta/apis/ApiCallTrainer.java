package com.licenta.apis;

import com.licenta.models.Trainer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiCallTrainer {
    @GET("trainers/")
    Call<List<Trainer>> getTrainers();

    @GET("trainers/{id}")
    Call<Trainer> getTrainer(@Path("id") int id);

    @POST("trainers")
    Call<Trainer> createTrainer(@Body Trainer trainer);
}
