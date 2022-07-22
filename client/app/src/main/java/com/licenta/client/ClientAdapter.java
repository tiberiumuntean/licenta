package com.licenta.client;

import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.models.Client;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private final ClientsRecyclerViewInterface clientsRecyclerViewInterface;
    private List<Client> clientsList;

    public ClientAdapter(List<Client> clientsList, ClientsRecyclerViewInterface clientsRecyclerViewInterface) {
        this.clientsList = clientsList;
        this.clientsRecyclerViewInterface = clientsRecyclerViewInterface;
    }

    @NonNull
    @Override
    public ClientAdapter.ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clients_item, parent, false);
        return new ClientViewHolder(view, clientsRecyclerViewInterface);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ClientAdapter.ClientViewHolder holder, int position) {
        Client currentClient = clientsList.get(position);

        int clientAge = 0;
        if(currentClient.getBirthday() != null) {
            clientAge = GlobalData.calculateAge(currentClient.getBirthday());
        }
        String firstName = currentClient.getFirstName();
        String lastName = currentClient.getLastName();

        holder.setData(firstName, lastName, clientAge, currentClient);
    }

    @Override
    public int getItemCount() {
        return clientsList.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView name;
        private final TextView age;

        public ClientViewHolder(@NonNull View itemView, ClientsRecyclerViewInterface clientsRecyclerViewInterface) {
            super(itemView);

            image = itemView.findViewById(R.id.imageViewClient);
            name = itemView.findViewById(R.id.textViewClientName);
            age = itemView.findViewById(R.id.textViewClientAge);

            image.setClipToOutline(true);

            itemView.setOnClickListener(view -> {
                if (clientsRecyclerViewInterface != null) {
                    int position = getAbsoluteAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        clientsRecyclerViewInterface.onItemClick(position);
                    }
                }
            });
        }

        public void setData(String firstName, String lastName, int clientAge, Client currentClient) {
            Picasso.get().load(String.format("%susers/%d/avatar/", GlobalData.RETROFIT_BASE_URL, currentClient.getUser_id())).placeholder(R.drawable.profile_picture).error(R.drawable.profile_picture).into(image);
            name.setText(String.format("%s %s", firstName, lastName));
            if(clientAge == 0){
                age.setText(String.format("", clientAge));
            } else {
                age.setText(String.format("%s years", clientAge));
            }
        }
    }
}
