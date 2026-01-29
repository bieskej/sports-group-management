package ba.sum.fsre.sportska_grupa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ba.sum.fsre.sportska_grupa.R;
import ba.sum.fsre.sportska_grupa.api.ApiCallback;
import ba.sum.fsre.sportska_grupa.api.RetrofitClient;
import ba.sum.fsre.sportska_grupa.models.request.RegisterRequest;
import ba.sum.fsre.sportska_grupa.models.response.AuthResponse;
import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerBtn, backToLoginBtn;
    private ProgressBar progressBar;
    private AuthManager authManager;

    private CheckBox cbAcceptPolicy;
    private TextView tvPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = new AuthManager(this);

        initViews();
        setupPrivacyPolicy();
        setupListeners();
    }

    private void initViews() {
        usernameInput = findViewById(R.id.usernameTxt);
        emailInput = findViewById(R.id.emailTxt);
        passwordInput = findViewById(R.id.passwordTxt);
        confirmPasswordInput = findViewById(R.id.passwordCnfTxt);

        registerBtn = findViewById(R.id.registerBtn);
        backToLoginBtn = findViewById(R.id.backToLoginBtn);
        progressBar = findViewById(R.id.registerProgressBar);

        cbAcceptPolicy = findViewById(R.id.cbAcceptPolicy);
        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy);

        // gumb je klikabilan, ali vizualno "disabled"
        registerBtn.setEnabled(true);
        registerBtn.setAlpha(0.5f);
    }

    private void setupPrivacyPolicy() {

        // klik na Privacy Policy → GitHub
        tvPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(
                    "https://matijevicmila.github.io/SportGroup-policies/privacy-policy"
            ));
            startActivity(intent);
        });

        // checkbox upravlja izgledom gumba
        cbAcceptPolicy.setOnCheckedChangeListener((cbView, isChecked) -> {
            registerBtn.setAlpha(isChecked ? 1f : 0.5f);
        });
    }

    private void setupListeners() {
        registerBtn.setOnClickListener(v -> registerUser());
        backToLoginBtn.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        if (!cbAcceptPolicy.isChecked()) {
            Toast.makeText(this,
                    "Morate prihvatiti pravila privatnosti",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (!validateInput(username, email, password, confirmPassword)) {
            return;
        }

        setLoading(true);

        RegisterRequest request = new RegisterRequest(email, password, username);

        RetrofitClient.getInstance(this)
                .getApi()
                .signup(request)
                .enqueue(new ApiCallback<AuthResponse>() {
                    @Override
                    public void onSuccess(AuthResponse response) {
                        setLoading(false);
                        handleRegisterSuccess(response, email);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this,
                                errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String username, String email,
                                  String password, String confirmPassword) {

        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Korisničko ime mora imati najmanje 3 znaka");
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Unesite ispravan email");
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Lozinka mora imati najmanje 6 znakova");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Lozinke se ne podudaraju");
            return false;
        }

        return true;
    }

    private void handleRegisterSuccess(AuthResponse response, String email) {
        authManager.saveToken(response.getAccessToken());
        authManager.saveEmail(response.getUser().getEmail());

        showVerificationDialog(email);
    }

    private void showVerificationDialog(String email) {
        new AlertDialog.Builder(this)
                .setTitle("Registracija Uspješna! ✅")
                .setMessage("Verifikacijski email je poslan na:\n\n" + email +
                        "\n\nMolimo provjerite svoju email poštu i kliknite na link za verifikaciju računa.")
                .setIcon(android.R.drawable.ic_dialog_email)
                .setPositiveButton("U redu", (dialog, which) -> {
                    // Prebaci na login ekran
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        registerBtn.setEnabled(!isLoading);
    }
}
