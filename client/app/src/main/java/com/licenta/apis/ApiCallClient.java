package com.licenta.apis;

import com.licenta.models.Client;
import com.licenta.models.Trainer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiCallClient {
    @GET("clients")
    Call<List<Client>> getClients();

    @GET("clients/{id}")
    Call<Client> getClient(@Path("id") int id);

    @POST("clients")
    Call<Client> createClient(@Body Client client);

    @PUT("clients/{id}")
    Call<Client> updateClient(@Path("id") int id, @Body Client client);
}
