package com.example.selfcarenuevo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EstadoAnimoActivity extends AppCompatActivity {

    private RadioGroup grupoEmociones;
    private EditText etDescripcion;
    private DatabaseHelper databaseHelper;
    private long idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_animo);

        SharedPreferences sesion = getSharedPreferences(
                "SesionSelfCare",
                MODE_PRIVATE
        );

        idUsuario = sesion.getLong("idUsuario", -1);

        if (idUsuario == -1) {
            startActivity(new Intent(
                    this,
                    LoginActivity.class
            ));
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);

        grupoEmociones = findViewById(R.id.grupoEmociones);
        etDescripcion = findViewById(R.id.etDescripcion);

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnVolver = findViewById(R.id.btnVolver);

        btnGuardar.setOnClickListener(v -> {

            int idSeleccionado =
                    grupoEmociones.getCheckedRadioButtonId();

            if (idSeleccionado == -1) {
                Toast.makeText(
                        this,
                        "Selecciona cómo te sientes",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            RadioButton radioSeleccionado =
                    findViewById(idSeleccionado);

            String emocion = radioSeleccionado
                    .getText().toString();

            String descripcion = etDescripcion
                    .getText().toString().trim();

            if (descripcion.isEmpty()) {
                etDescripcion.setError(
                        "Escribe cómo estuvo tu día"
                );
                etDescripcion.requestFocus();
                return;
            }

            boolean guardado = databaseHelper.guardarEstadoAnimo(
                    idUsuario,
                    emocion,
                    descripcion
            );

            if (guardado) {
                Toast.makeText(
                        this,
                        "Estado de ánimo guardado correctamente",
                        Toast.LENGTH_LONG
                ).show();

                grupoEmociones.clearCheck();
                etDescripcion.setText("");

            } else {
                Toast.makeText(
                        this,
                        "No se pudo guardar el registro",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnVolver.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
