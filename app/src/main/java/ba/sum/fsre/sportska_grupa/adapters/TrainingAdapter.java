// TrainingAdapter.java
package ba.sum.fsre.sportska_grupa.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.activities.TrainingPlayersActivity;
import ba.sum.fsre.sportska_grupa.models.Training;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder> {

    public interface OnTrainingDeleteListener {
        void onDelete(Training training);
    }

    public interface OnTrainingEditListener {
        void onEdit(Training training);
    }

    private List<Training> trainingList;
    private final OnTrainingDeleteListener deleteListener;
    private final OnTrainingEditListener editListener;
    private final boolean isTrainer;

    public TrainingAdapter(List<Training> trainingList,
                           boolean isTrainer,
                           OnTrainingDeleteListener deleteListener,
                           OnTrainingEditListener editListener) {
        this.trainingList = trainingList;
        this.isTrainer = isTrainer;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    public void updateData(List<Training> newTrainings) {
        this.trainingList = newTrainings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training, parent, false);
        return new TrainingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingViewHolder holder, int position) {
        Training training = trainingList.get(position);

        // TITLE
        holder.title.setText(training.getTitle() != null ? training.getTitle() : "Training");

        // DATE (u XML-u ti je već 📅 + datum, zato ovdje samo datum)
        String date = training.getTrainingDate();
        holder.date.setText((date != null && !date.trim().isEmpty()) ? date : "-");

        // TIME (u XML-u ti je već ⏰ + vrijeme, zato ovdje samo vrijeme)
        String time = training.getTrainingTime();
        holder.time.setText((time != null && !time.trim().isEmpty()) ? time : "-");

        // DESCRIPTION
        String desc = training.getDescription();
        if (desc != null && !desc.trim().isEmpty()) {
            holder.description.setText(desc);
            holder.description.setVisibility(View.VISIBLE);
        } else {
            holder.description.setVisibility(View.GONE);
        }

        // COACH (ovo ti je falilo)
        String coach = training.getCoach();
        if (coach != null && !coach.trim().isEmpty()) {
            holder.coach.setText("Trener: " + coach);
        } else {
            holder.coach.setText("Trener: -");
        }

        // Edit / Delete
        if (!isTrainer) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> {
                if (editListener != null) editListener.onEdit(training);
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) deleteListener.onDelete(training);
            });
        }

        // Click on item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TrainingPlayersActivity.class);
            intent.putExtra("training_id", training.getId());
            intent.putExtra("training_title", training.getTitle());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return trainingList == null ? 0 : trainingList.size();
    }

    public static class TrainingViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time, coach, description;
        ImageButton btnDelete, btnEdit;

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trainingTitle);
            date = itemView.findViewById(R.id.trainingDate);
            time = itemView.findViewById(R.id.trainingTime);
            coach = itemView.findViewById(R.id.trainingCoach);
            description = itemView.findViewById(R.id.trainingDescription);

            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
