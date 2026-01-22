package ba.sum.fsre.sportska_grupa.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.activities.TrainingPlayersActivity;
import ba.sum.fsre.sportska_grupa.models.Training;

import java.util.List;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder> {

    public interface OnTrainingDeleteListener {
        void onDelete(Training training);
    }

    public interface OnTrainingEditListener {
        void onEdit(Training training);
    }

    private List<Training> trainingList;
    private OnTrainingDeleteListener deleteListener;
    private OnTrainingEditListener editListener;

    private boolean isTrainer;


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

        holder.title.setText(training.getTitle());
        holder.date.setText("Datum: " + training.getTrainingDate());

        if (training.getDescription() != null && !training.getDescription().isEmpty()) {
            holder.description.setText(training.getDescription());
            holder.description.setVisibility(View.VISIBLE);
        } else {
            holder.description.setVisibility(View.GONE);
        }


        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(training);
            }
        });


        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(training);
            }
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TrainingPlayersActivity.class);
            intent.putExtra("training_id", training.getId());
            intent.putExtra("training_title", training.getTitle());
            v.getContext().startActivity(intent);
        });

        if (!isTrainer) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return trainingList == null ? 0 : trainingList.size();
    }

    public static class TrainingViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, description;
        ImageButton btnDelete, btnEdit;

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trainingTitle);
            date = itemView.findViewById(R.id.trainingDate);
            description = itemView.findViewById(R.id.trainingDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
