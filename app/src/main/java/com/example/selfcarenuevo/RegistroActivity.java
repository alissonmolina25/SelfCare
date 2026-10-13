package com.example.selfcarenuevo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etCorreoRegistro;
    private EditText etContrasenaRegistro;
    private EditText etConfirmarContrasena;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        databaseHelper = new DatabaseHelper(this);

        etNombre = findViewById(R.id.etNombre);
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        etContrasenaRegistro =
                findViewById(R.id.etContrasenaRegistro);
        etConfirmarContrasena =
                findViewById(R.id.etConfirmarContrasena);

        Button btnRegistrarme =
                findViewById(R.id.btnRegistrarme);

        Button btnVolverLogin =
                findViewById(R.id.btnVolverLogin);

        btnRegistrarme.setOnClickListener(v -> {

            String nombre = etNombre.getText()
                    .toString().trim();

            String correo = etCorreoRegistro.getText()
                    .toString().trim();

            String contrasena = etContrasenaRegistro
                    .getText().toString();

            String confirmar = etConfirmarContrasena
                    .getText().toString();

            if (nombre.isEmpty()) {
                etNombre.setError("Ingresa tu nombre");
                etNombre.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreoRegistro.setError(
                        "Ingresa un correo válido");
                etCorreoRegistro.requestFocus();
                return;
            }

            if (contrasena.length() < 8) {
                etContrasenaRegistro.setError(
                        "La contraseña debe tener al menos 8 caracteres");
                etContrasenaRegistro.requestFocus();
                return;
            }

            if (!contrasena.equals(confirmar)) {
                etConfirmarContrasena.setError(
                        "Las contraseñas no coinciden");
                etConfirmarContrasena.requestFocus();
                return;
            }

            boolean registrado = databaseHelper.registrarUsuario(
                    nombre,
                    correo,
                    contrasena
            );

            if (registrado) {
                Toast.makeText(
                        this,
                        "Cuenta creada correctamente",
                        Toast.LENGTH_LONG
                ).show();

                Intent intent = new Intent(
                        RegistroActivity.this,
                        LoginActivity.class
                );

                intent.putExtra("correoRegistrado", correo);

                startActivity(intent);
                finish();

            } else {
                Toast.makeText(
                        this,
                        "No se pudo registrar. Verifica si el correo ya existe.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        btnVolverLogin.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
