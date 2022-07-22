package com.licenta.apis;

import com.licenta.models.Client;
import com.licenta.models.Membership;
import com.licenta.models.CheckoutProduct;
import com.licenta.models.ClientSecret;
import com.licenta.models.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiCallMembership {
    @GET("memberships")
    Call<List<Membership>> getMemberships();

    @GET("memberships/{id}")
    Call<Membership> getMembership(@Path("id") int id);

    @POST("memberships")
    Call<Membership> createMembership(@Body Membership membership);

    @PUT("memberships/{id}")
    Call<Membership> updateMembership(@Path("id") int id, @Body Membership membership);

    @DELETE("memberships/{id}")
    Call<Void> deleteMembership(@Path("id") int id);

    @POST("memberships/create-payment-intent")
    Call<ClientSecret> createPaymentIntent(@Body CheckoutProduct checkoutProduct);
}
