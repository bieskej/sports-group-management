package ba.sum.fsre.sportska_grupa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.models.Training;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {

    private List<Training> trainings;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Training training);
    }

    public TrainingAdapter(List<Training> trainings) {
        this.trainings = trainings;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Training training = trainings.get(position);
        holder.bind(training, onDeleteClickListener);
    }

    @Override
    public int getItemCount() {
        return trainings != null ? trainings.size() : 0;
    }

    public void updateTrainings(List<Training> newTrainings) {
        this.trainings = newTrainings;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView dateTextView;
        private TextView createdTextView;
        private ImageButton deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.trainingTitleTxt);
            descriptionTextView = itemView.findViewById(R.id.trainingDescriptionTxt);
            dateTextView = itemView.findViewById(R.id.trainingDateTxt);
            createdTextView = itemView.findViewById(R.id.trainingCreatedTxt);
            deleteButton = itemView.findViewById(R.id.deleteTrainingBtn);
        }

        void bind(Training training, OnDeleteClickListener listener) {
            titleTextView.setText(training.getTitle() != null ? training.getTitle() : "Bez naziva");
            
            if (training.getDescription() != null && !training.getDescription().isEmpty()) {
                descriptionTextView.setText(training.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            // Formatiranje datuma treninga
            if (training.getTrainingDate() != null) {
                dateTextView.setText("📅 " + formatDate(training.getTrainingDate()));
                dateTextView.setVisibility(View.VISIBLE);
            } else {
                dateTextView.setVisibility(View.GONE);
            }

            // Formatiranje vremena kreiranja
            if (training.getCreatedAt() != null) {
                createdTextView.setText("Kreirano: " + formatDateTime(training.getCreatedAt()));
                createdTextView.setVisibility(View.VISIBLE);
            } else {
                createdTextView.setVisibility(View.GONE);
            }

            // Setup delete button
            if (listener != null) {
                deleteButton.setOnClickListener(v -> listener.onDeleteClick(training));
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        }

        private String formatDate(String dateString) {
            try {
                // Supabase vraća date u formatu YYYY-MM-DD
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return date != null ? outputFormat.format(date) : dateString;
            } catch (ParseException e) {
                return dateString;
            }
        }

        private String formatDateTime(String dateTimeString) {
            try {
                // ISO 8601 format timestamp
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                
                // Remove timezone and microseconds if present
                String cleanDateTime = dateTimeString.split("\\.")[0].replace("Z", "");
                
                Date date = inputFormat.parse(cleanDateTime);
                return date != null ? outputFormat.format(date) : dateTimeString;
            } catch (Exception e) {
                return dateTimeString;
            }
        }
    }
}
