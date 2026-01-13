package ba.sum.fsre.sportska_grupa.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrainingAdapter adapter;
    private FloatingActionButton fabAddTraining;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        authManager = new AuthManager(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadTrainings();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAddTraining = findViewById(R.id.fabAddTraining);
        progressBar = findViewById(R.id.dashboardProgressBar);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrainingAdapter(new ArrayList<>());
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
                Toast.makeText(DashboardActivity.this, "Greška pri učitavanju: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateTrainingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_training, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDate = dialogView.findViewById(R.id.etDate);

        builder.setPositiveButton("Kreiraj", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (title.isEmpty() || date.isEmpty()) {
                Toast.makeText(DashboardActivity.this, "Naslov i datum su obavezni", Toast.LENGTH_SHORT).show();
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
        
        // Ensure date is in correct format or accept string for now. 
        // Supabase expects YYYY-MM-DD for date type.
        
        Training training = new Training(date, userId, title, description);
        
        // Note: The structure requires sending a list or object depending on API. 
        // Supabase creates return the created object(s).
        
        RetrofitClient.getInstance(this).getApi().createTraining(training).enqueue(new ApiCallback<List<Training>>() {
            @Override
            public void onSuccess(List<Training> result) {
                setLoading(false);
                Toast.makeText(DashboardActivity.this, "Trening kreiran!", Toast.LENGTH_SHORT).show();
                loadTrainings(); // Refresh list
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(DashboardActivity.this, "Greška pri kreiranju: " + errorMessage, Toast.LENGTH_SHORT).show();
            }git commit -m "Training: create training (CRUD - CREATE)"

        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
