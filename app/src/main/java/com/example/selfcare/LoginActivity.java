package com.example.selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carga la pantalla de inicio de sesión.
        setContentView(R.layout.activity_login);

        // Conecta los elementos del XML con Java.
        TextView btnVolver = findViewById(R.id.btnVolver);
        TextView txtRegistrarse = findViewById(R.id.txtRegistrarse);

        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etContrasena = findViewById(R.id.etContrasena);

        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        // Regresa a la pantalla anterior.
        btnVolver.setOnClickListener(v -> finish());

        // Abre la pantalla de registro.
        txtRegistrarse.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        // Iniciar sesión.
        btnIniciarSesion.setOnClickListener(v -> {

            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            // Verifica que se haya ingresado un correo.
            if (correo.isEmpty()) {
                etCorreo.setError("Ingresa tu correo electrónico");
                etCorreo.requestFocus();
                return;
            }

            // Verifica que se haya ingresado una contraseña.
            if (contrasena.isEmpty()) {
                etContrasena.setError("Ingresa tu contraseña");
                etContrasena.requestFocus();
                return;
            }

            // Confirmación temporal.
            Toast.makeText(
                    LoginActivity.this,
                    "Datos ingresados correctamente",
                    Toast.LENGTH_SHORT
            ).show();

            // Más adelante verificaremos el usuario
            // y la contraseña utilizando SQLite.
        });
    }
}