package com.licenta.paymentshistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.R;
import com.licenta.models.Invoice;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentsHistoryAdapter extends RecyclerView.Adapter<PaymentsHistoryAdapter.PaymentsHistoryViewHolder> {

    private final List<Invoice> paymentsHistoryList;

    public PaymentsHistoryAdapter(List<Invoice> paymentsHistoryList) {
        this.paymentsHistoryList = paymentsHistoryList;
    }

    @NonNull
    @Override
    public PaymentsHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payments_history_item, parent, false);
        return new PaymentsHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentsHistoryViewHolder holder, int position) {
        Invoice currentInvoice = paymentsHistoryList.get(position);

        String paymentsHistorySeries = currentInvoice.getSeries();
        int paymentsHistoryNumber = currentInvoice.getNumber();
        Double paymentsHistoryPrice = currentInvoice.getMembership() != null ? currentInvoice.getMembership().getPrice() : currentInvoice.getTrainer().getPrice();
        String paymentsHistoryFitnessClub = currentInvoice.getClub().getName();
        String paymentsHistoryDate = currentInvoice.getDate();
        String paymentsHistoryMembership = currentInvoice.getMembership() != null ? currentInvoice.getMembership().getName() : String.format("Training by %s %s", currentInvoice.getTrainer().getFirstName(), currentInvoice.getTrainer().getLastName());
        String paymentsHistoryClient = String.format("%s %s", currentInvoice.getClient().getFirstName(), currentInvoice.getClient().getLastName());

        holder.setData(paymentsHistorySeries, paymentsHistoryNumber, paymentsHistoryPrice, paymentsHistoryDate, paymentsHistoryFitnessClub, paymentsHistoryMembership, paymentsHistoryClient);
    }

    @Override
    public int getItemCount() {
        return paymentsHistoryList.size();
    }

    public static class PaymentsHistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView series;
        private final TextView number;
        private final TextView price;
        private final TextView date;
        private final TextView club;
        private final TextView client;

        public PaymentsHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            series = itemView.findViewById(R.id.textViewPaymentsHistorySeries);
            number = itemView.findViewById(R.id.textViewPaymentsHistoryNumber);
            price = itemView.findViewById(R.id.textViewPaymentsHistoryPrice);
            date = itemView.findViewById(R.id.textViewPaymentsHistoryDate);
            club = itemView.findViewById(R.id.textViewPaymentsHistoryFitnessClub);
            client = itemView.findViewById(R.id.textViewPaymentsHistoryClient);
        }

        public void setData(String paymentsHistorySeries, int paymentsHistoryNumber, Double paymentsHistoryPrice, String paymentsHistoryDate, String paymentsHistoryFitnessClub, String paymentsHistoryMembership, String paymentsHistoryClient) {
            SimpleDateFormat formatInitial = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatFinal = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date tempDate = formatInitial.parse(paymentsHistoryDate);
                String finalDate = formatFinal.format(tempDate);

                date.setText(finalDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            series.setText(String.valueOf(paymentsHistorySeries));
            number.setText(String.format("%s / %s", paymentsHistoryNumber, paymentsHistoryMembership));
            price.setText(String.format("%s RON", paymentsHistoryPrice));
            club.setText(String.valueOf(paymentsHistoryFitnessClub));
            client.setText(paymentsHistoryClient);
        }
    }
}
