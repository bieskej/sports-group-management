package ba.sum.fsre.sportska_grupa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import ba.sum.fsre.sportska_grupa.models.Profile;
import ba.sum.fsre.sportska_grupa.models.request.LoginRequest;
import ba.sum.fsre.sportska_grupa.models.response.AuthResponse;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginBtn, goToRegisterBtn;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.loginEmailTxt);
        passwordInput = findViewById(R.id.loginPasswordTxt);
        loginBtn = findViewById(R.id.loginBtn);
        goToRegisterBtn = findViewById(R.id.goToRegisterBtn);
        progressBar = findViewById(R.id.loginProgressBar);
    }

    private void setupListeners() {
        loginBtn.setOnClickListener(v -> loginUser());

        goToRegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        setLoading(true);

        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getInstance(this)
                .getApi()
                .login(request)
                .enqueue(new ApiCallback<AuthResponse>() {
                    @Override
                    public void onSuccess(AuthResponse response) {
                        setLoading(false);
                        handleLoginSuccess(response);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        if (errorMessage.contains("400") || errorMessage.contains("401")) {
                            Toast.makeText(LoginActivity.this,
                                    "Neispravan email ili lozinka",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Unesite email");
            emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Unesite ispravan email");
            emailInput.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Unesite lozinku");
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }

    // ✅ ISPRAVLJENA LOGIKA
    private void handleLoginSuccess(AuthResponse response) {
        if (response == null || response.getAccessToken() == null) {
            Toast.makeText(this, "Greška pri prijavi", Toast.LENGTH_SHORT).show();
            return;
        }

        authManager.saveToken(response.getAccessToken());

        if (response.getUser() != null) {
            String userId = response.getUser().getId();
            authManager.saveUserId(userId);
            authManager.saveEmail(response.getUser().getEmail());

            // ✅ TEK NAKON ROLE IDEMO NA DASHBOARD
            fetchUserRole(userId);
        } else {
            Toast.makeText(this, "Greška pri prijavi", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserRole(String userId) {
        RetrofitClient.getInstance(this)
                .getApi()
                .getMyProfile("eq." + userId, "role")
                .enqueue(new ApiCallback<List<Profile>>() {
                    @Override
                    public void onSuccess(List<Profile> profiles) {
                        if (profiles != null && !profiles.isEmpty()) {
                            authManager.saveRole(profiles.get(0).getRole());
                        }
                        goToDashboard();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(LoginActivity.this,
                                "Greška pri dohvaćanju uloge",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToDashboard() {
        Toast.makeText(this, "Prijava uspješna!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginBtn.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
    }
}
