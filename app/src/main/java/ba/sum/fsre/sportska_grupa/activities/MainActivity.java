package ba.sum.fsre.sportska_grupa.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ba.sum.fsre.sportska_grupa.utils.AuthManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Provjera postoji li već spremljeni token (korisnik je već prijavljen)
        AuthManager authManager = new AuthManager(this);

        Intent intent;
        if (authManager.isLoggedIn()) {
            intent = new Intent(this, DashboardActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        // Očisti back stack kako bi se preskočio login kod već prijavljenog korisnika
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

