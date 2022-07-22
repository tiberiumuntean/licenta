package com.licenta.apis;

import com.licenta.models.Invoice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiCallInvoice {
    @GET("invoices")
    Call<List<Invoice>> getInvoices();

    @GET("invoices/client/{id}")
    Call<List<Invoice>> getInvoicesByClientId(@Path("id") int id);

    @GET("invoices/trainer/{id}")
    Call<List<Invoice>> getInvoicesByTrainerId(@Path("id") int id);

    @POST("invoices")
    Call<Invoice> createInvoice(@Body Invoice invoice);
}
