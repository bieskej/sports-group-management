package ba.sum.fsre.sportska_grupa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.adapters.TrainingAdapter;
import ba.sum.fsre.sportska_grupa.api.ApiCallback;
import ba.sum.fsre.sportska_grupa.api.RetrofitClient;
import ba.sum.fsre.sportska_grupa.models.Training;
import ba.sum.fsre.sportska_grupa.models.TrainingUpdateRequest;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;
import retrofit2.Call;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrainingAdapter adapter;
    private FloatingActionButton fabAddTraining;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        authManager = new AuthManager(this);

        initViews();
        applyRolePermissions();
        setupRecyclerView();
        setupListeners();
        loadTrainings();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAddTraining = findViewById(R.id.fabAddTraining);
        progressBar = findViewById(R.id.dashboardProgressBar);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void applyRolePermissions() {
        if (!authManager.isTrainer()) {
            fabAddTraining.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Promijenjen konstruktor: dodali smo edit listener
        adapter = new TrainingAdapter(
                new ArrayList<>(),
                this::showDeleteConfirmationDialog,
                this::showEditTrainingDialog
        );

        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAddTraining.setOnClickListener(v -> showCreateTrainingDialog());
    }

    private void loadTrainings() {
        setLoading(true);
        RetrofitClient.getInstance(this).getApi().getTrainings().enqueue(new ApiCallback<List<Training>>() {
            @Override
            public void onSuccess(List<Training> result) {
                setLoading(false);
                if (result != null) {
                    adapter.updateData(result);
                }
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                handleApiError(errorMessage);
            }
        });
    }

    private void showCreateTrainingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_training, null);
        builder.setView(dialogView);

        android.widget.TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("Novi Trening");

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDate = dialogView.findViewById(R.id.etDate);

        builder.setPositiveButton("Kreiraj", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (title.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Naslov i datum su obavezni", Toast.LENGTH_SHORT).show();
                return;
            }

            createTraining(title, description, date);
        });

        builder.setNegativeButton("Odustani", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private void createTraining(String title, String description, String date) {
        setLoading(true);
        String userId = authManager.getUserId();

        Training training = new Training(date, userId, title, description);

        RetrofitClient.getInstance(this).getApi().createTraining(training).enqueue(new ApiCallback<List<Training>>() {
            @Override
            public void onSuccess(List<Training> result) {
                setLoading(false);
                Toast.makeText(DashboardActivity.this, "Trening kreiran!", Toast.LENGTH_SHORT).show();
                loadTrainings();
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                handleApiError(errorMessage);
            }
        });
    }

    //  NOVO: Edit dialog
    private void showEditTrainingDialog(Training training) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_training, null);
        builder.setView(dialogView);

        // OVO DODAJEŠ (NASLOV)
        android.widget.TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("Uredi Trening");

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDate = dialogView.findViewById(R.id.etDate);

        // Prefill podataka
        etTitle.setText(training.getTitle());
        etDescription.setText(training.getDescription());
        etDate.setText(training.getTrainingDate());

        builder.setPositiveButton("Spremi", (dialog, which) -> {
            String newTitle = etTitle.getText().toString().trim();
            String newDescription = etDescription.getText().toString().trim();
            String newDate = etDate.getText().toString().trim();

            if (newTitle.isEmpty() || newDate.isEmpty()) {
                Toast.makeText(this, "Naslov i datum su obavezni", Toast.LENGTH_SHORT).show();
                return;
            }

            updateTraining(training, newTitle, newDescription, newDate);
        });

        builder.setNegativeButton("Odustani", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    //  NOVO: Update API call (PATCH)
    private void updateTraining(Training training, String title, String description, String date) {
        setLoading(true);

        String idQuery = "eq." + training.getId();

        TrainingUpdateRequest request = new TrainingUpdateRequest(date, title, description);

        Call<List<Training>> call = RetrofitClient.getInstance(this).getApi().updateTraining(idQuery, request);

        call.enqueue(new ApiCallback<List<Training>>() {
            @Override
            public void onSuccess(List<Training> result) {
                setLoading(false);
                Toast.makeText(DashboardActivity.this, "Trening ažuriran!", Toast.LENGTH_SHORT).show();
                loadTrainings();
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                handleApiError(errorMessage);
            }
        });
    }

    private void showDeleteConfirmationDialog(Training training) {
        if (!authManager.isTrainer()) {
            Toast.makeText(this, "Nemate ovlasti za brisanje treninga", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Brisanje treninga")
                .setMessage("Jeste li sigurni da želite obrisati trening: " + training.getTitle() + "?")
                .setPositiveButton("Obriši", (dialog, which) -> deleteTraining(training))
                .setNegativeButton("Odustani", null)
                .show();
    }

    private void deleteTraining(Training training) {
        setLoading(true);
        String idQuery = "eq." + training.getId();
        RetrofitClient.getInstance(this).getApi().deleteTraining(idQuery).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(DashboardActivity.this, "Trening obrisan", Toast.LENGTH_SHORT).show();
                    loadTrainings();
                } else {
                    handleApiError("Greška: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                setLoading(false);
                Toast.makeText(DashboardActivity.this, "Greška mreže: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiError(String errorMessage) {
        if (errorMessage.contains("401")) {
            Toast.makeText(this, "Sesija je istekla. Molimo prijavite se ponovno.", Toast.LENGTH_LONG).show();
            authManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
