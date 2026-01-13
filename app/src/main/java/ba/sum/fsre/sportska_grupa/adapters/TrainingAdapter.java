package ba.sum.fsre.sportska_grupa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.models.Training;
import java.util.List;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder> {

    private List<Training> trainingList;

    public TrainingAdapter(List<Training> trainingList) {
        this.trainingList = trainingList;
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
    }

    @Override
    public int getItemCount() {
        return trainingList.size();
    }

    public static class TrainingViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, description;

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trainingTitle);
            date = itemView.findViewById(R.id.trainingDate);
            description = itemView.findViewById(R.id.trainingDescription);
        }
    }
}
