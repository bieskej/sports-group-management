package ba.sum.fsre.sportska_grupa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.adapters.TrainingAdapter;
import ba.sum.fsre.sportska_grupa.api.ApiCallback;
import ba.sum.fsre.sportska_grupa.api.RetrofitClient;
import ba.sum.fsre.sportska_grupa.models.Training;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class DashboardActivity extends AppCompatActivity {

    private static final int CREATE_TRAINING_REQUEST = 1001;

    private RecyclerView trainingsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateTxt;
    private TrainingAdapter adapter;
    private List<Training> trainingsList;
    private AuthManager authManager;

    private ActivityResultLauncher<Intent> createTrainingLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        authManager = new AuthManager(this);

        // Provjeri da li korisnik ima token, ako ne, vrati ga na login
        if (!authManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Setup launcher za CreateTrainingActivity
        createTrainingLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Osvježi listu treninga nakon kreiranja novog
                        loadTrainings();
                    }
                }
        );

        initViews();
        setupRecyclerView();
        setupButtons();
        loadTrainings();
    }

    private void initViews() {
        trainingsRecyclerView = findViewById(R.id.trainingsRecyclerView);
        progressBar = findViewById(R.id.dashboardProgressBar);
        emptyStateTxt = findViewById(R.id.emptyStateTxt);
    }

    private void setupButtons() {
        findViewById(R.id.addTrainingBtn).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateTrainingActivity.class);
            createTrainingLauncher.launch(intent);
        });

        findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            authManager.logout();
            Toast.makeText(this, "Odjavljeni ste", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        });
    }

    private void setupRecyclerView() {
        trainingsList = new ArrayList<>();
        adapter = new TrainingAdapter(trainingsList);
        adapter.setOnDeleteClickListener(training -> deleteTraining(training));
        trainingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainingsRecyclerView.setAdapter(adapter);
    }

    private void deleteTraining(Training training) {
        setLoading(true);
        
        Log.d("DashboardActivity", "Deleting training with ID: " + training.getId());

        RetrofitClient.getInstance(this)
                .getApi()
                .deleteTraining("eq." + training.getId())
                .enqueue(new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        setLoading(false);
                        Log.d("DashboardActivity", "Delete successful, refreshing list...");
                        Toast.makeText(DashboardActivity.this, 
                                "Trening uspješno obrisan!", 
                                Toast.LENGTH_SHORT).show();
                        // Osvježi listu treninga
                        loadTrainings();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Log.e("DashboardActivity", "Error deleting training: " + errorMessage);
                        
                        if (errorMessage.contains("401")) {
                            Toast.makeText(DashboardActivity.this, 
                                    "Sesija je istekla. Molimo prijavite se ponovo.", 
                                    Toast.LENGTH_LONG).show();
                            authManager.logout();
                            redirectToLogin();
                        } else {
                            Toast.makeText(DashboardActivity.this, 
                                    "Greška pri brisanju treninga: " + errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadTrainings() {
        setLoading(true);

        RetrofitClient.getInstance(this)
                .getApi()
                .getTrainings()
                .enqueue(new ApiCallback<List<Training>>() {
                    @Override
                    public void onSuccess(List<Training> response) {
                        setLoading(false);
                        if (response != null && !response.isEmpty()) {
                            trainingsList.clear();
                            trainingsList.addAll(response);
                            adapter.updateTrainings(trainingsList);
                            showEmptyState(false);
                        } else {
                            showEmptyState(true);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        
                        Log.e("DashboardActivity", "Error loading trainings: " + errorMessage);
                        
                        // Ako je 401 (Unauthorized), token je nevažeći - vrati na login
                        if (errorMessage.contains("401")) {
                            Toast.makeText(DashboardActivity.this, 
                                    "Sesija je istekla. Molimo prijavite se ponovo.", 
                                    Toast.LENGTH_LONG).show();
                            authManager.logout();
                            redirectToLogin();
                        } else {
                            Toast.makeText(DashboardActivity.this, 
                                    "Greška pri učitavanju treninga: " + errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                            showEmptyState(true);
                        }
                    }
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        trainingsRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyStateTxt.setVisibility(show ? View.VISIBLE : View.GONE);
        trainingsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
