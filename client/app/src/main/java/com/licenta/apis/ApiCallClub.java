package com.licenta.apis;

import com.licenta.models.Club;
import com.licenta.models.Membership;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiCallClub {
    @GET("clubs")
    Call<List<Club>> getClubs();

    @GET("clubs/{id}")
    Call<Club> getClub(@Path("id") int id);

    @POST("clubs")
    Call<Club> createClub(@Body Club club);

    @PUT("clubs/{id}")
    Call<Club> updateClub(@Path("id") int id, @Body Club club);

    @DELETE("clubs/{id}")
    Call<Void> deleteClub(@Path("id") int id);
}
