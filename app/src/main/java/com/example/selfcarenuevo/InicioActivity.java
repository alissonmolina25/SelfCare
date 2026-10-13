package com.example.selfcarenuevo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InicioActivity extends AppCompatActivity {

    private TextView txtUltimoRegistro;
    private DatabaseHelper databaseHelper;
    private long idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        SharedPreferences sesion = getSharedPreferences(
                "SesionSelfCare",
                MODE_PRIVATE
        );

        idUsuario = sesion.getLong("idUsuario", -1);

        if (idUsuario < 1) {
            startActivity(new Intent(
                    InicioActivity.this,
                    LoginActivity.class
            ));
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);

        Button btnEstado = findViewById(R.id.btnEstadoAnimo);
        Button btnHistorial = findViewById(R.id.btnHistorial);
        Button btnHabitos = findViewById(R.id.btnHabitos);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        txtUltimoRegistro = findViewById(R.id.txtUltimoRegistro);

        // ABRIR ESTADO DE ÁNIMO
        btnEstado.setOnClickListener(v -> {
            Intent intent = new Intent(
                    InicioActivity.this,
                    EstadoAnimoActivity.class
            );
            startActivity(intent);
        });

        // ABRIR MI DIARIO
        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(
                    InicioActivity.this,
                    HistorialActivity.class
            );
            startActivity(intent);
        });

        // ABRIR MIS HÁBITOS
        btnHabitos.setOnClickListener(v -> {
            Intent intent = new Intent(
                    InicioActivity.this,
                    HabitosActivity.class
            );
            startActivity(intent);
        });

        // CERRAR SESIÓN
        btnCerrarSesion.setOnClickListener(v -> {

            sesion.edit()
                    .remove("idUsuario")
                    .apply();

            Intent intent = new Intent(
                    InicioActivity.this,
                    LoginActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper == null || txtUltimoRegistro == null) {
            return;
        }

        try (Cursor cursor =
                     databaseHelper.obtenerEstadosAnimo(idUsuario)) {

            if (cursor.moveToFirst()) {

                String emocion = cursor.getString(
                        cursor.getColumnIndexOrThrow("emocion")
                );

                txtUltimoRegistro.setText(emocion);

            } else {

                txtUltimoRegistro.setText(
                        "Sin registros todavía"
                );
            }
        }
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}
