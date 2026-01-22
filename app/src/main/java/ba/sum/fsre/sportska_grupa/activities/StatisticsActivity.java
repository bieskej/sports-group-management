package ba.sum.fsre.sportska_grupa.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.api.ApiCallback;
import ba.sum.fsre.sportska_grupa.api.RetrofitClient;
import ba.sum.fsre.sportska_grupa.models.Attendance;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class StatisticsActivity extends AppCompatActivity {

    private CheckBox cbPresent;

    private String playerId;
    private String trainingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        cbPresent = findViewById(R.id.cbPresent);

        AuthManager authManager = new AuthManager(this);
        boolean isTrainer = authManager.isTrainer();

        if (isTrainer) {
            cbPresent.setVisibility(View.VISIBLE);
            cbPresent.setEnabled(true);
        } else {
            cbPresent.setVisibility(View.GONE); // USER → potpuno sakrij checkbox
        }

        playerId = getIntent().getStringExtra("playerId");
        trainingId = getIntent().getStringExtra("trainingId");

        if (playerId != null && trainingId != null) {
            loadAttendance(trainingId, playerId);
        }
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
}
