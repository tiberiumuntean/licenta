package com.licenta.apis;

import com.licenta.fitnessclass.requests.DateRequest;
import com.licenta.models.Club;
import com.licenta.models.FitnessClass;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiCallFitnessClass {
    @GET("classes/{id}")
    Call<FitnessClass> getFitnessClass(@Path("id") int id);

    @POST("classes")
    Call<FitnessClass> createFitnessClass(@Body FitnessClass fitnessClass);

    @PUT("classes/{id}")
    Call<FitnessClass> updateFitnessClass(@Path("id") int id, @Body FitnessClass fitnessClass);

    @DELETE("classes/{id}")
    Call<Void> deleteFitnessClass(@Path("id") int id);

    @POST("classes/date")
    Call<List<FitnessClass>> getClassesByDate(@Body DateRequest dateRequest);
}
