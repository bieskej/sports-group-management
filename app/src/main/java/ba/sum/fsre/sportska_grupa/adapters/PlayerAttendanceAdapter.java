/*package ba.sum.fsre.sportska_grupa.adapters;

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

    private List<Player> players;
    private OnAttendanceChangeListener listener;

    public PlayerAttendanceAdapter(List<Player> players, OnAttendanceChangeListener listener) {
        this.players = players;
        this.listener = listener;
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

        // Privremeno ukloni listener da izbjegneš neželjene pozive
        holder.cbPresent.setOnCheckedChangeListener(null);
        holder.cbPresent.setChecked(player.isPresent());

        // Ponovo postavi listener
        holder.cbPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            player.setPresent(isChecked);
            if (listener != null) {
                listener.onAttendanceChanged(player, isChecked);
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
*/
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

    private List<Player> players;
    private boolean isTrainer;
    private OnAttendanceChangeListener listener;

    public PlayerAttendanceAdapter(List<Player> players, boolean isTrainer, OnAttendanceChangeListener listener) {
        this.players = players;
        this.isTrainer = isTrainer;
        this.listener = listener;
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

        // Privremeno ukloni listener da izbjegneš neželjene pozive
        holder.cbPresent.setOnCheckedChangeListener(null);
        holder.cbPresent.setChecked(player.isPresent());

        // Samo trener može mijenjati checkbox
        holder.cbPresent.setEnabled(isTrainer);

        // Ponovo postavi listener
        holder.cbPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isTrainer) {
                player.setPresent(isChecked);
                if (listener != null) {
                    listener.onAttendanceChanged(player, isChecked);
                }
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