package com.licenta.trainer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.GlobalData;
import com.licenta.R;
import com.licenta.models.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.TrainerReviewsViewHolder> {
    private List<Review> trainerReviewsList;

    public ReviewsAdapter(List<Review> trainerReviewsList) {
        this.trainerReviewsList = trainerReviewsList;
    }

    @NonNull
    @Override
    public ReviewsAdapter.TrainerReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainer_reviews_item, parent, false);
        return new TrainerReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.TrainerReviewsViewHolder holder, int position) {
        Review currentReview = trainerReviewsList.get(position);

        float clientRatingBarVal = currentReview.getRating();
        String clientReviewInitials = String.format("%s%s", currentReview.getClient().getFirstName().charAt(0), currentReview.getClient().getLastName().charAt(0));
        String clientReviewName = String.format("%s %s", currentReview.getClient().getFirstName(), currentReview.getClient().getLastName());
        String clientReviewDate = currentReview.getCreationDate();
        String clientReview = currentReview.getReview();

        holder.setData(clientReviewInitials, clientReviewName, clientReviewDate, clientReview, clientRatingBarVal);
    }

    @Override
    public int getItemCount() {
        return trainerReviewsList.size();
    }

    public static class TrainerReviewsViewHolder extends RecyclerView.ViewHolder {
        private final TextView initials;
        private final TextView name;
        private final TextView date;
        private final TextView review;
        private final RatingBar rating;

        public TrainerReviewsViewHolder(@NonNull View itemView) {
            super(itemView);

            initials = itemView.findViewById(R.id.textViewClientInitialsReview);
            name = itemView.findViewById(R.id.textViewClientNameReview);
            date = itemView.findViewById(R.id.textViewReviewDate);
            review = itemView.findViewById(R.id.textViewClientReview);
            rating = itemView.findViewById(R.id.ratingBarTrainerReview);
        }

        public void setData(String clientReviewInitials, String clientReviewName, String clientReviewDate, String clientReview, float clientRatingBarVal) {
            initials.setText(clientReviewInitials);
            name.setText(clientReviewName);
            date.setText(GlobalData.getDisplayDate(clientReviewDate));
            review.setText(clientReview);
            rating.setRating(clientRatingBarVal);
        }
    }
}
