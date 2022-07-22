package com.licenta.apis;

import com.licenta.models.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiCallReview {
    @GET("reviews/")
    Call<List<Review>> getReviews();

    @POST("reviews")
    Call<Review> createReview(@Body Review review);
}
