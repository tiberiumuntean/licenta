package com.licenta.trainer;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.models.Review;
import com.licenta.models.Trainer;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder> {
    private Trainer currentTrainer;

    private final TrainersRecyclerViewInterface trainersRecyclerViewInterface;
    private final List<Trainer> trainerList;

    public TrainerAdapter(List<Trainer> trainerList, TrainersRecyclerViewInterface trainersRecyclerViewInterface) {
        this.trainerList = trainerList;
        this.trainersRecyclerViewInterface = trainersRecyclerViewInterface;
    }

    @NonNull
    @Override
    public TrainerAdapter.TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainers_item, parent, false);
        return new TrainerViewHolder(view, trainersRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerAdapter.TrainerViewHolder holder, int position) {
        currentTrainer = trainerList.get(position);

        int trainerFreeSpots = currentTrainer.getFreeSpots();
        String firstName = currentTrainer.getFirstName();
        String lastName = currentTrainer.getLastName();
        int totalReviewsNumber = currentTrainer.getReviews().size();

        holder.setData(firstName, lastName, trainerFreeSpots, totalReviewsNumber, currentTrainer);
    }

    @Override
    public int getItemCount() {
        return trainerList.size();
    }

    public static class TrainerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView name;
        private final RatingBar ratingBarVal;
        private final TextView ratingScore;
        private final TextView freeSpots;
        private final TextView totalReviews;

        public TrainerViewHolder(@NonNull View itemView, TrainersRecyclerViewInterface trainersRecyclerViewInterface) {
            super(itemView);

            image = itemView.findViewById(R.id.imageViewTrainer);
            name = itemView.findViewById(R.id.textViewTrainerName);
            ratingBarVal = itemView.findViewById(R.id.textViewTrainerRatingBar);
            ratingScore = itemView.findViewById(R.id.textViewTrainerRatingBarScore);
            freeSpots = itemView.findViewById(R.id.textViewFreeSpotsNr);
            totalReviews = itemView.findViewById(R.id.textViewTrainerReviews);

            image.setClipToOutline(true);

            itemView.setOnClickListener(view -> {
                if (trainersRecyclerViewInterface != null) {
                    int position = getAbsoluteAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        trainersRecyclerViewInterface.onItemClick(position);
                    }
                }
            });
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void setData(String firstName, String lastName, int trainerFreeSpots, int totalReviewsNumber, Trainer currentTrainer) {
            Float averageRating = .0f;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                averageRating = currentTrainer.getReviews().stream().map(Review::getRating).reduce((float) 0, Float::sum) / currentTrainer.getReviews().size();

                if(Double.isNaN(averageRating)){
                    averageRating = 0f;
                }
            }

            Picasso.get().load(String.format("%susers/%d/avatar/", GlobalData.RETROFIT_BASE_URL, currentTrainer.getUser_id())).placeholder(R.drawable.profile_picture).error(R.drawable.profile_picture).into(image);
            name.setText(String.format("%s %s", firstName, lastName));
            ratingBarVal.setRating(averageRating);

            ratingScore.setText(String.valueOf(averageRating));
            freeSpots.setText(Integer.toString(trainerFreeSpots));
            totalReviews.setText(String.format("(%d reviews)", totalReviewsNumber));
        }
    }
}
