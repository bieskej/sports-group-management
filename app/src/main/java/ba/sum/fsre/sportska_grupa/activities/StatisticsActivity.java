package ba.sum.fsre.sportska_grupa.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import java.util.List;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.api.ApiCallback;
import ba.sum.fsre.sportska_grupa.api.RetrofitClient;
import ba.sum.fsre.sportska_grupa.models.Attendance;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class StatisticsActivity extends AppCompatActivity {

    private CheckBox cbPresent;
    private TextView tvTotalTrainings;
    private TextView tvPresent;
    private TextView tvAbsent;
    private TextView tvAttendanceRate;
    private ProgressBar progressBar;
    private CardView cardCurrentTraining;
    private CardView cardOverallStats;

    private String playerId;
    private String trainingId;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_statistics);

        authManager = new AuthManager(this);

        initViews();

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statisticsCoordinatorLayout), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            View appBar = findViewById(R.id.toolbar).getParent() instanceof View ? (View) findViewById(R.id.toolbar).getParent() : findViewById(R.id.toolbar);
            appBar.setPadding(appBar.getPaddingLeft(), systemBars.top, appBar.getPaddingRight(), appBar.getPaddingBottom());
            return insets;
        });

        // Dohvati podatke iz Intent-a
        playerId = getIntent().getStringExtra("playerId");
        trainingId = getIntent().getStringExtra("trainingId");

        boolean isTrainer = authManager.isTrainer();

        // Ako je pozvan iz TrainingPlayersActivity (ima playerId i trainingId)
        if (playerId != null && trainingId != null) {
            // Prikaži samo trenutni trening
            cardCurrentTraining.setVisibility(View.VISIBLE);
            cardOverallStats.setVisibility(View.GONE);

            if (isTrainer) {
                cbPresent.setEnabled(true);
            } else {
                cbPresent.setEnabled(false);
            }

            loadAttendance(trainingId, playerId);
        }
        // Ako je pozvan iz Dashboard-a (bez parametara) - prikaži ukupnu statistiku
        else {
            cardCurrentTraining.setVisibility(View.GONE);
            cardOverallStats.setVisibility(View.VISIBLE);

            // Koristi trenutnog korisnika
            playerId = authManager.getUserId();
            loadOverallStatistics();
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        cbPresent = findViewById(R.id.cbPresent);
        tvTotalTrainings = findViewById(R.id.tvTotalTrainings);
        tvPresent = findViewById(R.id.tvPresent);
        tvAbsent = findViewById(R.id.tvAbsent);
        tvAttendanceRate = findViewById(R.id.tvAttendanceRate);
        progressBar = findViewById(R.id.progressBar);
        cardCurrentTraining = findViewById(R.id.cardCurrentTraining);
        cardOverallStats = findViewById(R.id.cardOverallStats);
    }

    private void loadAttendance(String trainingId, String playerId) {
        RetrofitClient.getInstance(this)
                .getApi()
                .getAttendanceForTraining("eq." + trainingId)
                .enqueue(new ApiCallback<List<Attendance>>() {
                    @Override
                    public void onSuccess(List<Attendance> result) {
                        if (result != null) {
                            for (Attendance att : result) {
                                if (att.getPlayerId() != null &&
                                        att.getPlayerId().equals(playerId)) {
                                    cbPresent.setChecked(att.isPresent());
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // ako nema zapisa, ostaje unchecked
                    }
                });
    }

    private void loadOverallStatistics() {
        setLoading(true);

        // Dohvati sve zapise prisutnosti za ovog igrača
        RetrofitClient.getInstance(this)
                .getApi()
                .getPlayerAttendance("eq." + playerId)
                .enqueue(new ApiCallback<List<Attendance>>() {
                    @Override
                    public void onSuccess(List<Attendance> result) {
                        setLoading(false);
                        calculateAndDisplayStats(result);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(StatisticsActivity.this,
                                "Greška: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void calculateAndDisplayStats(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            tvTotalTrainings.setText("0");
            tvPresent.setText("0");
            tvAbsent.setText("0");
            tvAttendanceRate.setText("0%");
            return;
        }

        int total = attendances.size();
        int present = 0;
        int absent = 0;

        for (Attendance att : attendances) {
            if (att.isPresent()) {
                present++;
            } else {
                absent++;
            }
        }

        // Izračunaj postotak prisutnosti
        double attendanceRate = (total > 0) ? ((double) present / total * 100) : 0;

        // Prikaži rezultate
        tvTotalTrainings.setText(String.valueOf(total));
        tvPresent.setText(String.valueOf(present));
        tvAbsent.setText(String.valueOf(absent));
        tvAttendanceRate.setText(String.format("%.1f%%", attendanceRate));

        // Animiraj karticu (opcionalno)
        cardOverallStats.setAlpha(0f);
        cardOverallStats.animate().alpha(1f).setDuration(500).start();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        cardOverallStats.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}


