package com.licenta.apis;

import com.licenta.models.User;
import com.licenta.user.UserLogin;
import com.licenta.user.requests.LoginRequest;
import com.licenta.user.requests.PasswordResetRequest;
import com.licenta.user.responses.PasswordResetResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiCallUser {
    @GET("users/{id}")
    Call<User> getUser(@Path("id") int id);

    @POST("users/signup")
    Call<User> createUser(@Body User user);

    @POST("users/signin")
    Call<UserLogin> login(@Body LoginRequest loginRequest);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int id);

    @POST("users/password-reset")
    Call<PasswordResetResponse> updateUserPassword(@Body PasswordResetRequest request);
}