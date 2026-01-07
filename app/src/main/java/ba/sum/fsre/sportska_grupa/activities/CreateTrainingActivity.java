package ba.sum.fsre.sportska_grupa.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.api.ApiCallback;
import ba.sum.fsre.sportska_grupa.api.RetrofitClient;
import ba.sum.fsre.sportska_grupa.models.Training;
import ba.sum.fsre.sportska_grupa.models.request.CreateTrainingRequest;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class CreateTrainingActivity extends AppCompatActivity {

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText dateInput;
    private Button createBtn;
    private Button cancelBtn;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training);

        authManager = new AuthManager(this);

        // Provjeri da li korisnik ima token
        if (!authManager.isLoggedIn()) {
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        titleInput = findViewById(R.id.trainingTitleInput);
        descriptionInput = findViewById(R.id.trainingDescriptionInput);
        dateInput = findViewById(R.id.trainingDateInput);
        createBtn = findViewById(R.id.createTrainingBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        progressBar = findViewById(R.id.createTrainingProgressBar);
    }

    private void setupListeners() {
        createBtn.setOnClickListener(v -> createTraining());
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void createTraining() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        if (!validateInput(title, date)) {
            return;
        }

        setLoading(true);

        // Dobij user ID iz AuthManager-a
        String userId = authManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            setLoading(false);
            Toast.makeText(this, "Greška: Korisnički ID nije dostupan. Molimo prijavite se ponovo.", Toast.LENGTH_LONG).show();
            return;
        }

        CreateTrainingRequest request = new CreateTrainingRequest(title, description, date, userId);

        RetrofitClient.getInstance(this)
                .getApi()
                .createTraining(request)
                .enqueue(new ApiCallback<List<Training>>() {
                    @Override
                    public void onSuccess(List<Training> response) {
                        setLoading(false);
                        if (response != null && !response.isEmpty()) {
                            Toast.makeText(CreateTrainingActivity.this, 
                                    "Trening uspješno kreiran!", 
                                    Toast.LENGTH_SHORT).show();
                            
                            // Vrati rezultat da Dashboard osvježi listu
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(CreateTrainingActivity.this, 
                                    "Greška: Trening je kreiran, ali nema podataka u odgovoru.", 
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        
                        if (errorMessage.contains("401")) {
                            Toast.makeText(CreateTrainingActivity.this, 
                                    "Sesija je istekla. Molimo prijavite se ponovo.", 
                                    Toast.LENGTH_LONG).show();
                            authManager.logout();
                            finish();
                        } else {
                            Toast.makeText(CreateTrainingActivity.this, 
                                    "Greška pri kreiranju treninga: " + errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateInput(String title, String date) {
        boolean isValid = true;

        if (TextUtils.isEmpty(title)) {
            titleInput.setError("Unesite naziv treninga");
            titleInput.requestFocus();
            isValid = false;
        }

        if (TextUtils.isEmpty(date)) {
            dateInput.setError("Unesite datum treninga");
            dateInput.requestFocus();
            isValid = false;
        } else if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            dateInput.setError("Datum mora biti u formatu YYYY-MM-DD (npr. 2026-01-15)");
            dateInput.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        createBtn.setEnabled(!isLoading);
        cancelBtn.setEnabled(!isLoading);
        titleInput.setEnabled(!isLoading);
        descriptionInput.setEnabled(!isLoading);
        dateInput.setEnabled(!isLoading);
    }
}
