package com.licenta.fitnessclass;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.SaveSharedPreference;
import com.licenta.models.Client;
import com.licenta.models.FitnessClass;
import com.licenta.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class FitnessClassAdapter extends RecyclerView.Adapter<FitnessClassAdapter.ClassScheduleSubItemViewHolder> {
    private final FitnessClassInterface fitnessClassInterface;
    private final List<FitnessClass> fitnessClassList;

    public FitnessClassAdapter(List<FitnessClass> fitnessClassList, FitnessClassInterface fitnessClassInterface) {
        this.fitnessClassList = fitnessClassList;
        this.fitnessClassInterface = fitnessClassInterface;
    }

    @NonNull
    @Override
    public FitnessClassAdapter.ClassScheduleSubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_schedule_subitem, parent, false);
        return new ClassScheduleSubItemViewHolder(view, fitnessClassInterface);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ClassScheduleSubItemViewHolder holder, int position) {
        FitnessClass currentFitnessClass = fitnessClassList.get(position);

        String trainer = String.format("%s %s", currentFitnessClass.getTrainer().getFirstName(), currentFitnessClass.getTrainer().getLastName());
        String date = currentFitnessClass.getDate();
        String name = currentFitnessClass.getName();
        @SuppressLint("DefaultLocale") String spotsLeft = String.format("%d spots left", currentFitnessClass.getFreeSpots());
        holder.setData(trainer, date, name, spotsLeft, currentFitnessClass);
    }

    @Override
    public int getItemCount() {
        return fitnessClassList.size();
    }

    public static class ClassScheduleSubItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName;
        private final TextView mDate;
        private final TextView mTrainer;
        private final TextView mSpotsLeft;
        private FitnessClass fitnessClass;

        public ClassScheduleSubItemViewHolder(@NonNull View itemView, FitnessClassInterface fitnessClassInterface) {
            super(itemView);

            mDate = itemView.findViewById(R.id.textViewTrainerScheduleSubItemTime);
            mName = itemView.findViewById(R.id.textViewTrainerScheduleSubItemName);
            mTrainer = itemView.findViewById(R.id.textViewTrainerScheduleSubItemClientName);
            mSpotsLeft = itemView.findViewById(R.id.textViewTrainerScheduleSubItemSpotsLeft);

            itemView.setOnClickListener(view -> {
                if (fitnessClassInterface != null) {
                    int position = getAbsoluteAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        fitnessClassInterface.onItemClick(position, fitnessClass);
                    }
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void setData(String trainer, String date, String name, String spotsLeft, FitnessClass fitnessClass) {
            List<Integer> clientsIds = fitnessClass.getClients().stream().map(Client::getId).collect(Collectors.toList());

            String title = fitnessClass.getName();
            if(!SaveSharedPreference.getClientId(this.mDate.getContext()).isEmpty()){
                title = clientsIds.contains(Integer.parseInt(SaveSharedPreference.getClientId(this.mDate.getContext()))) ? String.format("%s (Booked)", fitnessClass.getName()) : name;
            }
            mDate.setText(GlobalData.getDisplayTime(date));
            mName.setText(title);
            mTrainer.setText(trainer);
            mSpotsLeft.setText(spotsLeft);
            this.fitnessClass = fitnessClass;
        }
    }
}
