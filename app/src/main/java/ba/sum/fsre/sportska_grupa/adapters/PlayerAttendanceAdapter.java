package ba.sum.fsre.sportska_grupa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.models.Player;

public class PlayerAttendanceAdapter extends RecyclerView.Adapter<PlayerAttendanceAdapter.ViewHolder> {

    public interface OnAttendanceChangeListener {
        void onAttendanceChanged(Player player, boolean isPresent);
    }

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    private List<Player> players;
    private boolean isTrainer;
    private OnAttendanceChangeListener attendanceListener;
    private OnPlayerClickListener playerClickListener;

    public PlayerAttendanceAdapter(
            List<Player> players,
            boolean isTrainer,
            OnAttendanceChangeListener attendanceListener,
            OnPlayerClickListener playerClickListener
    ) {
        this.players = players;
        this.isTrainer = isTrainer;
        this.attendanceListener = attendanceListener;
        this.playerClickListener = playerClickListener;
    }

    public void updateData(List<Player> newPlayers) {
        this.players = newPlayers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = players.get(position);

        holder.tvPlayerName.setText(player.getUsername());

        // === CHECKBOX LOGIKA ===
        if (isTrainer) {
            holder.cbPresent.setVisibility(View.VISIBLE);
            holder.cbPresent.setOnCheckedChangeListener(null);
            holder.cbPresent.setChecked(player.isPresent());
            holder.cbPresent.setEnabled(true);

            holder.cbPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
                player.setPresent(isChecked);
                if (attendanceListener != null) {
                    attendanceListener.onAttendanceChanged(player, isChecked);
                }
            });
        } else {
            // USER → sakrij checkbox potpuno
            holder.cbPresent.setVisibility(View.GONE);
        }

        // === KLIK NA IGRACA (STATISTIKA) ===
        holder.itemView.setOnClickListener(v -> {
            if (playerClickListener != null) {
                playerClickListener.onPlayerClick(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return players != null ? players.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayerName;
        CheckBox cbPresent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            cbPresent = itemView.findViewById(R.id.cbPresent);
        }
    }
}
