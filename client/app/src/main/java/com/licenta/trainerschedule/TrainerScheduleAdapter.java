package com.licenta.trainerschedule;

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
import com.licenta.models.Appointment;
import com.licenta.models.Club;
import com.licenta.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class TrainerScheduleAdapter extends RecyclerView.Adapter<TrainerScheduleAdapter.TrainerScheduleSubItemViewHolder> {
    private final AppointmentInterface appointmentInterface;
    private final List<Appointment> appointmentsList;
    private Club club;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TrainerScheduleAdapter(List<Appointment> appointmentsList, AppointmentInterface appointmentInterface, Club club) {
        List<Integer> clubUsersIds = club.getUsers().stream().map(User::getId).collect(Collectors.toList());

        this.appointmentInterface = appointmentInterface;
        this.club = club;
        this.appointmentsList = appointmentsList.stream().filter(appointment -> clubUsersIds.contains(appointment.getClient().getUser_id())).collect(Collectors.toList());
    }

    @NonNull
    @Override
    public TrainerScheduleAdapter.TrainerScheduleSubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainer_schedule_subitem, parent, false);
        return new TrainerScheduleSubItemViewHolder(view, appointmentInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerScheduleAdapter.TrainerScheduleSubItemViewHolder holder, int position) {
        Appointment currentAppointment = appointmentsList.get(position);

        String mTime = currentAppointment.getDate();
        String mName = currentAppointment.getName();
        String mClient = String.format("%s %s", currentAppointment.getClient().getFirstName(), currentAppointment.getClient().getLastName());

        holder.setData(mTime, mName, mClient, currentAppointment);
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }

    public static class TrainerScheduleSubItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTrainerScheduleSubItemTime;
        private final TextView textViewTrainerScheduleSubItemName;
        private final TextView textViewTrainerScheduleSubItemClient;
        private Appointment appointment;

        public TrainerScheduleSubItemViewHolder(@NonNull View itemView, AppointmentInterface appointmentInterface) {
            super(itemView);

            textViewTrainerScheduleSubItemName = itemView.findViewById(R.id.textViewTrainerScheduleSubItemName);
            textViewTrainerScheduleSubItemTime = itemView.findViewById(R.id.textViewTrainerScheduleSubItemTime);
            textViewTrainerScheduleSubItemClient = itemView.findViewById(R.id.textViewTrainerScheduleSubItemClientName);

            itemView.setOnClickListener(view -> {
                if (appointmentInterface != null) {
                    int position = getAbsoluteAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        appointmentInterface.onItemClick(position, appointment);
                    }
                }
            });
        }

        public void setData(String mTime, String mName, String mClient, Appointment appointment) {
            textViewTrainerScheduleSubItemName.setText(mName);
            textViewTrainerScheduleSubItemTime.setText(GlobalData.getDisplayTime(mTime));
            textViewTrainerScheduleSubItemClient.setText(mClient);
            this.appointment = appointment;
        }
    }
}
