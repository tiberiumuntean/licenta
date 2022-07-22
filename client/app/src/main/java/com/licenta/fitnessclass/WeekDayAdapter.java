package com.licenta.fitnessclass;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.R;
import com.licenta.models.WeekDayClasses;

import java.util.List;

public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.ClassScheduleItemViewHolder> {

    private final Activity activity;
    private final List<WeekDayClasses> weekDayList;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private final FitnessClassInterface fitnessClassInterface;

    public WeekDayAdapter(Activity activity, List<WeekDayClasses> weekDayList, FitnessClassInterface fitnessClassInterface) {
        this.activity = activity;
        this.weekDayList = weekDayList;
        this.fitnessClassInterface = fitnessClassInterface;
    }

    @NonNull
    @Override
    public WeekDayAdapter.ClassScheduleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_schedule_item, parent, false);
        return new ClassScheduleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekDayAdapter.ClassScheduleItemViewHolder holder, int position) {
        WeekDayClasses weekDay = weekDayList.get(position);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setInitialPrefetchItemCount(weekDay.getFitnessClassList().size());

        FitnessClassAdapter fitnessClassAdapter = new FitnessClassAdapter(weekDay.getFitnessClassList(), fitnessClassInterface);
        holder.recycleViewClassScheduleSubItem.setLayoutManager(linearLayoutManager);
        holder.recycleViewClassScheduleSubItem.setAdapter(fitnessClassAdapter);
        holder.recycleViewClassScheduleSubItem.setRecycledViewPool(viewPool);

        holder.setData(weekDay.getDisplayDate());
    }

    @Override
    public int getItemCount() {
        return weekDayList.size();
    }

    public static class ClassScheduleItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewClassScheduleItemName;
        private final RecyclerView recycleViewClassScheduleSubItem;

        public ClassScheduleItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewClassScheduleItemName = itemView.findViewById(R.id.textViewTrainerScheduleItemName);
            recycleViewClassScheduleSubItem = itemView.findViewById(R.id.recyclerViewTrainerScheduleSubItem);
        }

        public void setData(String mDate) {
            textViewClassScheduleItemName.setText(mDate);
        }
    }
}
