package com.licenta.clientschedule;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.R;
import com.licenta.models.WeekDayAppointments;
import com.licenta.trainerschedule.AppointmentInterface;
import com.licenta.trainerschedule.TrainerScheduleAdapter;

import java.util.List;

public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.AppointmentItemViewHolder> {

    private final Activity activity;
    private final List<WeekDayAppointments> weekDayList;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private final AppointmentInterface appointmentInterface;

    public WeekDayAdapter(Activity activity, List<WeekDayAppointments> weekDayList, AppointmentInterface appointmentInterface) {
        this.activity = activity;
        this.weekDayList = weekDayList;
        this.appointmentInterface = appointmentInterface;
    }

    @NonNull
    @Override
    public AppointmentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainer_schedule_item, parent, false);
        return new AppointmentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentItemViewHolder holder, int position) {
        WeekDayAppointments weekDay = weekDayList.get(position);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setInitialPrefetchItemCount(weekDay.getAppointmentsList().size());

        ClientScheduleAdapter clientScheduleAdapter = new ClientScheduleAdapter(weekDay.getAppointmentsList(), appointmentInterface);
        holder.recycleViewTrainerScheduleSubItem.setLayoutManager(linearLayoutManager);
        holder.recycleViewTrainerScheduleSubItem.setAdapter(clientScheduleAdapter);
        holder.recycleViewTrainerScheduleSubItem.setRecycledViewPool(viewPool);

        holder.setData(weekDay.getDisplayDate());
    }

    @Override
    public int getItemCount() {
        return weekDayList.size();
    }

    public static class AppointmentItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewTrainerScheduleItemName;
        private final RecyclerView recycleViewTrainerScheduleSubItem;

        public AppointmentItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTrainerScheduleItemName = itemView.findViewById(R.id.textViewTrainerScheduleItemName);
            recycleViewTrainerScheduleSubItem = itemView.findViewById(R.id.recyclerViewTrainerScheduleSubItem);
        }

        public void setData(String mDate) {
            textViewTrainerScheduleItemName.setText(mDate);
        }
    }
}
