package com.licenta.trainerschedule;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.licenta.models.Client;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Client> {
    private Context context;
    private List<Client> clients;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Client> clients) {
        super(context, resource, clients);

        this.context = context;
        this.clients = clients;
    }

    @Override
    public int getCount() {
        return clients.size();
    }

    @Nullable
    @Override
    public Client getItem(int position) {
        return clients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ((TextView) v).setGravity(Gravity.CENTER_VERTICAL);
        ((TextView) v).setText(String.format("%s %s", clients.get(position).getFirstName(), clients.get(position).getLastName()));
        return v;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        ((TextView) v).setGravity(Gravity.CENTER_VERTICAL);
        ((TextView) v).setText(String.format("%s %s", clients.get(position).getFirstName(), clients.get(position).getLastName()));
        return v;
    }
}
