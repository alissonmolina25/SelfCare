package com.example.selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Muestra el diseño de la pantalla principal.
        setContentView(R.layout.activity_main);

        // Conecta el botón de activity_main.xml con Java.
        Button btnComenzar = findViewById(R.id.btnComenzar);

        // Al presionar "Comenzar", abre la pantalla de inicio de sesión.
        btnComenzar.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
        });
    }
}
