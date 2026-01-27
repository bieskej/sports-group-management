package ba.sum.fsre.sportska_grupa.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.adapters.TrainingAdapter;
import ba.sum.fsre.sportska_grupa.api.ApiCallback;
import ba.sum.fsre.sportska_grupa.api.RetrofitClient;
import ba.sum.fsre.sportska_grupa.models.Training;
import ba.sum.fsre.sportska_grupa.models.request.TrainingUpdateRequest;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;
import retrofit2.Call;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrainingAdapter adapter;
    private FloatingActionButton fabAddTraining;
    private FloatingActionButton fabStats;
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
        fabStats = findViewById(R.id.fabStats);
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
            fabStats.setVisibility(View.VISIBLE);
        } else {
            fabAddTraining.setVisibility(View.VISIBLE);
            fabStats.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TrainingAdapter(
                new ArrayList<>(),
                authManager.isTrainer(),
                this::showDeleteConfirmationDialog,
                this::showEditTrainingDialog
        );

        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAddTraining.setOnClickListener(v -> showCreateTrainingDialog());
        fabStats.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            // NE šalji playerId i trainingId - statistika će koristiti trenutnog korisnika
            startActivity(intent);
        });
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
        EditText etCoach = dialogView.findViewById(R.id.etCoach); // ✅ trener
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);

        setupDatePicker(etDate);
        setupTimePicker(etTime);

        builder.setPositiveButton("Kreiraj", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String coach = etCoach.getText().toString().trim(); // ✅ trener
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Naslov, datum i vrijeme su obavezni", Toast.LENGTH_SHORT).show();
                return;
            }

            createTraining(title, description, coach, date, time);
        });

        builder.setNegativeButton("Odustani", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void createTraining(String title, String description, String coach, String date, String time) {
        setLoading(true);

        String userId = authManager.getUserId();

        String timeForDb = (time.length() == 5) ? (time + ":00") : time;

        Training training = new Training(date, timeForDb, userId, title, description);

        training.setCoach(coach);

        RetrofitClient.getInstance(this).getApi().createTraining(training)
                .enqueue(new ApiCallback<List<Training>>() {
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


    private void showEditTrainingDialog(Training training) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_training, null);
        builder.setView(dialogView);

        android.widget.TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("Uredi Trening");

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etCoach = dialogView.findViewById(R.id.etCoach); // ✅ trener
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);

        // Prefill
        etTitle.setText(training.getTitle());
        etDescription.setText(training.getDescription());
        etDate.setText(training.getTrainingDate());

        // Prefill coach ako postoji getter
        try {
            if (training.getCoach() != null) etCoach.setText(training.getCoach());
        } catch (Exception ignored) {
            // ako trenutno nema coach u modelu, samo preskoči
        }

        // Ako je u bazi "HH:mm:ss", prikaži "HH:mm"
        String t = training.getTrainingTime();
        if (t != null && t.length() >= 5) {
            t = t.substring(0, 5);
        } else {
            t = "";
        }
        etTime.setText(t);

        setupDatePicker(etDate);
        setupTimePicker(etTime);

        builder.setPositiveButton("Spremi", (dialog, which) -> {
            String newTitle = etTitle.getText().toString().trim();
            String newDescription = etDescription.getText().toString().trim();
            String newCoach = etCoach.getText().toString().trim(); // ✅ trener
            String newDate = etDate.getText().toString().trim();
            String newTime = etTime.getText().toString().trim();

            if (newTitle.isEmpty() || newDate.isEmpty() || newTime.isEmpty()) {
                Toast.makeText(this, "Naslov, datum i vrijeme su obavezni", Toast.LENGTH_SHORT).show();
                return;
            }

            updateTraining(training, newTitle, newDescription, newCoach, newDate, newTime);
        });

        builder.setNegativeButton("Odustani", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void updateTraining(Training training, String title, String description, String coach, String date, String time) {
        setLoading(true);

        String idQuery = "eq." + training.getId();
        String timeForDb = (time.length() == 5) ? (time + ":00") : time;

        //  request sada šalje i coach
        TrainingUpdateRequest request = new TrainingUpdateRequest(date, timeForDb, title, description, coach);

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

    private void setupDatePicker(EditText etDate) {
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dp = new DatePickerDialog(
                    DashboardActivity.this,
                    (view, y, m, d) -> {
                        String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, (m + 1), d);
                        etDate.setText(formatted);
                    },
                    year, month, day
            );
            dp.show();
        });
    }

    private void setupTimePicker(EditText etTime) {
        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog tp = new TimePickerDialog(
                    DashboardActivity.this,
                    (view, h, min) -> {
                        String formatted = String.format(Locale.getDefault(), "%02d:%02d", h, min);
                        etTime.setText(formatted);
                    },
                    hour, minute, true
            );
            tp.show();
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
