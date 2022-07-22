package com.licenta.apis;

import com.licenta.fitnessclass.requests.DateRequest;
import com.licenta.models.Appointment;
import com.licenta.models.Club;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiCallAppointment {
    @GET("appointments/{id}")
    Call<Appointment> getAppointment(@Path("id") int id);

    @POST("appointments")
    Call<Appointment> createAppointment(@Body Appointment appointment);

    @PUT("appointments/{id}")
    Call<Appointment> updateAppointment(@Path("id") int id, @Body Appointment appointment);

    @DELETE("appointments/{id}")
    Call<Void> deleteAppointment(@Path("id") int id);


    @POST("appointments/date/client/{id}")
    Call<List<Appointment>> getAppointmentsByDateAndClient(@Body DateRequest dateRequest, @Path("id") int id);

    @POST("appointments/date/trainer/{id}")
    Call<List<Appointment>> getAppointmentsByDateAndTrainer(@Body DateRequest dateRequest, @Path("id") int id);
}
