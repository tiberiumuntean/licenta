package com.licenta.membership;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.R;
import com.licenta.models.Membership;

import java.util.List;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.MembershipViewHolder> {
    private final MembershipRecyclerViewInterface membershipRecyclerViewInterface;

    private final List<Membership> membershipList;

    public MembershipAdapter (List<Membership> membershipList, MembershipRecyclerViewInterface membershipRecyclerViewInterface){
        this.membershipList = membershipList;
        this.membershipRecyclerViewInterface = membershipRecyclerViewInterface;
    }

    @NonNull
    @Override
    public MembershipAdapter.MembershipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memberships_item, parent, false);
        return new MembershipViewHolder(view, membershipRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipAdapter.MembershipViewHolder holder, int position) {
        Membership currentMembership = membershipList.get(position);

        String membershipName = currentMembership.getName();
        Double membershipPrice = currentMembership.getPrice();
        int membershipDuration = currentMembership.getDuration();
        
        holder.setData(membershipName, membershipPrice, membershipDuration);
    }

    @Override
    public int getItemCount() {
        return membershipList.size();
    }

    public static class MembershipViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView standardPrice;
        private final TextView fullYearPrice;

        public MembershipViewHolder(@NonNull View itemView, MembershipRecyclerViewInterface membershipRecyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.textViewMembershipName);
            standardPrice = itemView.findViewById(R.id.textViewMembershipStandardPrice);
            fullYearPrice = itemView.findViewById(R.id.textViewMembershipFullYearPrice);

            itemView.setOnClickListener(view -> {
                if(membershipRecyclerViewInterface != null){
                    int position = getAbsoluteAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        membershipRecyclerViewInterface.onItemClick(position);
                    }
                }
            });
        }

        public void setData(String membershipName, Double membershipPrice, int membershipDuration) {
            name.setText(membershipName);
            standardPrice.setText(String.format("%s RON", membershipPrice));
            fullYearPrice.setText(String.format(membershipDuration == 1 ? "Price for %s month" : "Price for %s months", membershipDuration));
        }
    }
}

