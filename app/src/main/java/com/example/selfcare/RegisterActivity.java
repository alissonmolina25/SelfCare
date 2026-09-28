package com.example.selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Muestra la pantalla de registro.
        setContentView(R.layout.activity_register);

        // Conecta los elementos del XML con Java.
        TextView btnVolver = findViewById(R.id.btnVolver);
        TextView txtIniciarSesion = findViewById(R.id.txtIniciarSesion);
        Button btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        // Regresa a la pantalla anterior.
        btnVolver.setOnClickListener(v -> finish());

        // Regresa al inicio de sesión.
        txtIniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });

        // Más adelante este botón guardará al usuario en SQLite.
        btnCrearCuenta.setOnClickListener(v -> {
            // Próximamente: validar los datos y crear la cuenta.
        });
    }
}


