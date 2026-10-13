package com.example.selfcarenuevo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etContrasena;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);

        Button btnIniciarSesion =
                findViewById(R.id.btnIniciarSesion);

        Button btnCrearCuenta =
                findViewById(R.id.btnCrearCuenta);

        String correoRegistrado = getIntent()
                .getStringExtra("correoRegistrado");

        if (correoRegistrado != null) {
            etCorreo.setText(correoRegistrado);
        }

        btnIniciarSesion.setOnClickListener(v -> {

            String correo = etCorreo.getText()
                    .toString().trim();

            String contrasena = etContrasena.getText()
                    .toString();

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.setError("Ingresa un correo válido");
                etCorreo.requestFocus();
                return;
            }

            if (contrasena.isEmpty()) {
                etContrasena.setError("Ingresa tu contraseña");
                etContrasena.requestFocus();
                return;
            }

            long idUsuario = databaseHelper.iniciarSesion(
                    correo,
                    contrasena
            );

            if (idUsuario != -1) {

                SharedPreferences sesion =
                        getSharedPreferences(
                                "SesionSelfCare",
                                MODE_PRIVATE
                        );

                sesion.edit()
                        .putLong("idUsuario", idUsuario)
                        .apply();

                Toast.makeText(
                        this,
                        "Inicio de sesión correcto",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(
                        LoginActivity.this,
                        InicioActivity.class
                );

                startActivity(intent);
                finish();

            } else {
                Toast.makeText(
                        this,
                        "Correo o contraseña incorrectos",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        btnCrearCuenta.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegistroActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
