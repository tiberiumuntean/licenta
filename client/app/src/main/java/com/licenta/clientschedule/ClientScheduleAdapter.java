package com.licenta.clientschedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.models.Appointment;
import com.licenta.trainerschedule.AppointmentInterface;

import java.util.List;

public class ClientScheduleAdapter extends RecyclerView.Adapter<ClientScheduleAdapter.ClientScheduleSubItemViewHolder> {
    private final AppointmentInterface appointmentInterface;
    private final List<Appointment> appointmentsList;

    public ClientScheduleAdapter(List<Appointment> appointmentsList, AppointmentInterface appointmentInterface) {
        this.appointmentInterface = appointmentInterface;
        this.appointmentsList = appointmentsList;
    }

    @NonNull
    @Override
    public ClientScheduleSubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainer_schedule_subitem, parent, false);
        return new ClientScheduleSubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientScheduleSubItemViewHolder holder, int position) {
        Appointment currentAppointment = appointmentsList.get(position);

        String mTime = currentAppointment.getDate();
        String mName = currentAppointment.getName();
        String mTrainer = String.format("%s %s", currentAppointment.getTrainer().getFirstName(), currentAppointment.getTrainer().getLastName());

        holder.setData(mTime, mName, mTrainer);
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }

    public static class ClientScheduleSubItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewClientScheduleSubItemTime;
        private final TextView textViewClientScheduleSubItemName;
        private final TextView textViewClientScheduleSubItemTrainer;

        public ClientScheduleSubItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewClientScheduleSubItemName = itemView.findViewById(R.id.textViewTrainerScheduleSubItemName);
            textViewClientScheduleSubItemTime = itemView.findViewById(R.id.textViewTrainerScheduleSubItemTime);
            textViewClientScheduleSubItemTrainer = itemView.findViewById(R.id.textViewTrainerScheduleSubItemClientName);
        }

        public void setData(String mTime, String mName, String mTrainer) {
            textViewClientScheduleSubItemName.setText(mName);
            textViewClientScheduleSubItemTime.setText(GlobalData.getDisplayTime(mTime));
            textViewClientScheduleSubItemTrainer.setText(mTrainer);
        }
    }
}
