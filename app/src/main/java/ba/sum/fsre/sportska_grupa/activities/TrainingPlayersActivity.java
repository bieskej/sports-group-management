package ba.sum.fsre.sportska_grupa.activities;

import android.content.Intent;
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
    private boolean isTrainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_players);

        authManager = new AuthManager(this);
        isTrainer = authManager.isTrainer();

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

        // OVO JE FALILO: postavi toolbar kao ActionBar
        setSupportActionBar(toolbar);

        // Uključi strelicu nazad
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Prisutnost"); // ili prazno "" ako želiš bez teksta
        }

        // Bijela strelica (ako je rozi toolbar)
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(android.graphics.Color.WHITE);
        }

        // Klik na strelicu
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tvTrainingTitle);
        recyclerView = findViewById(R.id.recyclerViewPlayers);
        progressBar = findViewById(R.id.progressBar);

        if (isTrainer) {
            tvTitle.setText("Prisutnost – " + trainingTitle);
        } else {
            tvTitle.setText("Moja prisutnost – " + trainingTitle);
        }
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PlayerAttendanceAdapter(
                new ArrayList<>(),
                isTrainer,
                this::onAttendanceChanged,
                player -> {
                    Intent intent = new Intent(TrainingPlayersActivity.this, StatisticsActivity.class);
                    intent.putExtra("playerId", player.getId());
                    intent.putExtra("trainingId", trainingId);
                    startActivity(intent);
                }
        );

        recyclerView.setAdapter(adapter);
    }

    private void loadPlayers() {
        setLoading(true);

        if (isTrainer) {
            loadAllPlayers();
        } else {
            loadMyAttendance();
        }
    }

    private void loadAllPlayers() {
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

    private void loadMyAttendance() {
        String userId = authManager.getUserId();

        RetrofitClient.getInstance(this)
                .getApi()
                .getPlayerById("eq." + userId)
                .enqueue(new ApiCallback<List<Player>>() {
                    @Override
                    public void onSuccess(List<Player> result) {
                        if (result != null && !result.isEmpty()) {
                            loadAttendanceForPlayers(result);
                        } else {
                            setLoading(false);
                            Toast.makeText(TrainingPlayersActivity.this,
                                    "Greška pri učitavanju podataka", Toast.LENGTH_SHORT).show();
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
        RetrofitClient.getInstance(this)
                .getApi()
                .getAttendanceForTraining("eq." + trainingId)
                .enqueue(new ApiCallback<List<ba.sum.fsre.sportska_grupa.models.Attendance>>() {
                    @Override
                    public void onSuccess(List<ba.sum.fsre.sportska_grupa.models.Attendance> attendances) {
                        setLoading(false);

                        for (Player player : players) {
                            player.setPresent(false);
                            player.setAttendanceId(null);

                            if (attendances != null) {
                                for (ba.sum.fsre.sportska_grupa.models.Attendance att : attendances) {
                                    if (att.getPlayerId().equals(player.getId())) {
                                        // Uzimamo ZADNJI zapis (najnoviji)
                                        player.setPresent(att.isPresent());
                                        player.setAttendanceId(att.getId());
                                    }
                                }
                            }
                        }

                        adapter.updateData(players);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        adapter.updateData(players);
                    }
                });
    }

    private void onAttendanceChanged(Player player, boolean isPresent) {
        if (!isTrainer) {
            Toast.makeText(this, "Samo trener može ažurirati prisutnost", Toast.LENGTH_SHORT).show();
            return;
        }

        if (player.getAttendanceId() == null) {
            createAttendance(player, isPresent);
        } else {
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
