package com.licenta.apis;

import com.licenta.models.ClientMembership;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiCallClientMembership {
    @POST("client-memberships")
    Call<ClientMembership> createClientMembership(@Body ClientMembership clientMembership);

    @DELETE("client-memberships/{id}")
    Call<Void> deleteClientMembership(@Path("id") int id);
}
