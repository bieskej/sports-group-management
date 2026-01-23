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
    private OnPlayerClickListener clickListener;

    public PlayerAttendanceAdapter(List<Player> players,
                                   boolean isTrainer,
                                   OnAttendanceChangeListener attendanceListener,
                                   OnPlayerClickListener clickListener) {
        this.players = players;
        this.isTrainer = isTrainer;
        this.attendanceListener = attendanceListener;
        this.clickListener = clickListener;
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

        // Postavi ime
        holder.tvPlayerName.setText(player.getUsername());
        holder.tvPlayerRole.setText("Igrač");

        // Postavi inicijal
        if (player.getUsername() != null && !player.getUsername().isEmpty()) {
            String initial = player.getUsername().substring(0, 1).toUpperCase();
            holder.tvPlayerInitial.setText(initial);
        }

        if (isTrainer) {
            // Trener vidi checkbox koji može klikati
            holder.cbPresent.setVisibility(View.VISIBLE);
            holder.tvPresenceStatus.setVisibility(View.GONE);

            // Privremeno ukloni listener
            holder.cbPresent.setOnCheckedChangeListener(null);
            holder.cbPresent.setChecked(player.isPresent());
            holder.cbPresent.setEnabled(true);

            // Postavi listener
            holder.cbPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
                player.setPresent(isChecked);
                if (attendanceListener != null) {
                    attendanceListener.onAttendanceChanged(player, isChecked);
                }
            });
        } else {
            // Igrač vidi samo status ikonu (ne može klikati)
            holder.cbPresent.setVisibility(View.GONE);
            holder.tvPresenceStatus.setVisibility(View.VISIBLE);

            // Prikaži odgovarajući status
            if (player.isPresent()) {
                holder.tvPresenceStatus.setText("✓");
                holder.tvPresenceStatus.setTextColor(0xFF10B981);
            } else {
                holder.tvPresenceStatus.setText("✗");
                holder.tvPresenceStatus.setTextColor(0xFFEF4444);
            }
        }

        // Klik na cijelu karticu (opcionalno)
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPlayerClick(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return players != null ? players.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayerName;
        TextView tvPlayerRole;
        TextView tvPlayerInitial;
        CheckBox cbPresent;
        TextView tvPresenceStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerRole = itemView.findViewById(R.id.tvPlayerRole);
            tvPlayerInitial = itemView.findViewById(R.id.tvPlayerInitial);
            cbPresent = itemView.findViewById(R.id.cbPresent);
            tvPresenceStatus = itemView.findViewById(R.id.tvPresenceStatus);
        }
    }
}