package ba.sum.fsre.sportska_grupa.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.adapters.PlayerAttendanceAdapter;
import ba.sum.fsre.sportska_grupa.api.ApiCallback;
import ba.sum.fsre.sportska_grupa.api.RetrofitClient;
import ba.sum.fsre.sportska_grupa.models.Player;
import ba.sum.fsre.sportska_grupa.models.request.AttendanceRequest;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class TrainingPlayersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvTitle;
    private PlayerAttendanceAdapter adapter;
    private AuthManager authManager;
    private String trainingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_players);

        authManager = new AuthManager(this);

        trainingId = getIntent().getStringExtra("training_id");
        String trainingTitle = getIntent().getStringExtra("training_title");

        if (trainingId == null) {
            Toast.makeText(this, "ID treninga nije pronađen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews(trainingTitle);
        setupRecyclerView();
        loadPlayers();
    }

    private void initViews(String trainingTitle) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tvTrainingTitle);
        recyclerView = findViewById(R.id.recyclerViewPlayers);
        progressBar = findViewById(R.id.progressBar);

        tvTitle.setText("Prisutnost – " + trainingTitle);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayerAttendanceAdapter(new ArrayList<>(), this::onAttendanceChanged);
        recyclerView.setAdapter(adapter);
    }

    private void loadPlayers() {
        setLoading(true);

        // Dohvati sve igrače
        RetrofitClient.getInstance(this)
                .getApi()
                .getAllPlayers("eq.player")
                .enqueue(new ApiCallback<List<Player>>() {
                    @Override
                    public void onSuccess(List<Player> result) {
                        if (result != null && !result.isEmpty()) {
                            loadAttendanceForPlayers(result);
                        } else {
                            setLoading(false);
                            Toast.makeText(TrainingPlayersActivity.this,
                                    "Nema registriranih igrača", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(TrainingPlayersActivity.this,
                                "Greška: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAttendanceForPlayers(List<Player> players) {
        // Dohvati prisutnost za ovaj trening
        RetrofitClient.getInstance(this)
                .getApi()
                .getAttendanceForTraining("eq." + trainingId)
                .enqueue(new ApiCallback<List<ba.sum.fsre.sportska_grupa.models.Attendance>>() {
                    @Override
                    public void onSuccess(List<ba.sum.fsre.sportska_grupa.models.Attendance> attendances) {
                        setLoading(false);

                        // Spoji podatke
                        for (Player player : players) {
                            player.setPresent(false);
                            player.setAttendanceId(null);

                            if (attendances != null) {
                                for (ba.sum.fsre.sportska_grupa.models.Attendance att : attendances) {
                                    if (att.getPlayerId().equals(player.getId())) {
                                        player.setPresent(att.isPresent());
                                        player.setAttendanceId(att.getId());
                                        break;
                                    }
                                }
                            }
                        }

                        adapter.updateData(players);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        // Ako nema attendance zapisa, prikaži igrače sa present=false
                        adapter.updateData(players);
                    }
                });
    }

    private void onAttendanceChanged(Player player, boolean isPresent) {
        if (player.getAttendanceId() == null) {
            // Kreiraj novi zapis
            createAttendance(player, isPresent);
        } else {
            // Ažuriraj postojeći
            updateAttendance(player, isPresent);
        }
    }

    private void createAttendance(Player player, boolean isPresent) {
        AttendanceRequest request = new AttendanceRequest(trainingId, player.getId(), isPresent);

        RetrofitClient.getInstance(this)
                .getApi()
                .createAttendance(request)
                .enqueue(new ApiCallback<List<ba.sum.fsre.sportska_grupa.models.Attendance>>() {
                    @Override
                    public void onSuccess(List<ba.sum.fsre.sportska_grupa.models.Attendance> result) {
                        if (result != null && !result.isEmpty()) {
                            player.setAttendanceId(result.get(0).getId());
                            Toast.makeText(TrainingPlayersActivity.this,
                                    "Prisutnost zabilježena", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(TrainingPlayersActivity.this,
                                "Greška: " + errorMessage, Toast.LENGTH_SHORT).show();
                        // Vrati checkbox na prethodno stanje
                        loadPlayers();
                    }
                });
    }

    private void updateAttendance(Player player, boolean isPresent) {
        String idFilter = "eq." + player.getAttendanceId();
        AttendanceRequest request = new AttendanceRequest(null, null, isPresent);

        RetrofitClient.getInstance(this)
                .getApi()
                .updateAttendance(idFilter, request)
                .enqueue(new ApiCallback<List<ba.sum.fsre.sportska_grupa.models.Attendance>>() {
                    @Override
                    public void onSuccess(List<ba.sum.fsre.sportska_grupa.models.Attendance> result) {
                        Toast.makeText(TrainingPlayersActivity.this,
                                "Prisutnost ažurirana", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(TrainingPlayersActivity.this,
                                "Greška: " + errorMessage, Toast.LENGTH_SHORT).show();
                        loadPlayers();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}