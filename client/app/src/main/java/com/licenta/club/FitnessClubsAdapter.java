package com.licenta.club;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.R;
import com.licenta.models.Club;

import java.util.List;

public class FitnessClubsAdapter extends RecyclerView.Adapter<FitnessClubsAdapter.FitnessClubsViewHolder> {
    private final FitnessClubsRecyclerViewInterface fitnessClubsRecyclerViewInterface;
    private List<Club> clubList;

    public FitnessClubsAdapter(List<Club> clubList, FitnessClubsRecyclerViewInterface fitnessClubsRecyclerViewInterface) {
        this.clubList = clubList;
        this.fitnessClubsRecyclerViewInterface = fitnessClubsRecyclerViewInterface;
    }

    @NonNull
    @Override
    public FitnessClubsAdapter.FitnessClubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fitnessclub_item, parent, false);
        return new FitnessClubsViewHolder(view, fitnessClubsRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessClubsAdapter.FitnessClubsViewHolder holder, int position) {
        Club currentClub = clubList.get(position);
        String name = currentClub.getName();
        holder.setData(name);
    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

    public class FitnessClubsViewHolder extends RecyclerView.ViewHolder{
        private TextView mName;

        public FitnessClubsViewHolder(@NonNull View itemView, FitnessClubsRecyclerViewInterface fitnessClubsRecyclerViewInterface) {
            super(itemView);

            mName = itemView.findViewById(R.id.textViewFitnessClubsClubName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fitnessClubsRecyclerViewInterface != null){
                        int position = getAbsoluteAdapterPosition();

                        if(position != RecyclerView.NO_POSITION){
                            fitnessClubsRecyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void setData(String name) {
            mName.setText(String.valueOf(name));
        }
    }
}
