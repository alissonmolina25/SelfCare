package com.example.selfcarenuevo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistorialActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView txtHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        SharedPreferences sesion = getSharedPreferences(
                "SesionSelfCare",
                MODE_PRIVATE
        );

        long idUsuario = sesion.getLong("idUsuario", -1);

        if (idUsuario == -1) {
            startActivity(new Intent(
                    this,
                    LoginActivity.class
            ));
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);

        txtHistorial = findViewById(R.id.txtHistorial);
        Button btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> finish());

        mostrarHistorial(idUsuario);
    }

    private void mostrarHistorial(long idUsuario) {

        StringBuilder historial = new StringBuilder();

        SimpleDateFormat formato = new SimpleDateFormat(
                "dd/MM/yyyy - hh:mm a",
                new Locale("es", "SV")
        );

        try (Cursor cursor =
                     databaseHelper.obtenerEstadosAnimo(idUsuario)) {

            if (cursor.getCount() == 0) {
                txtHistorial.setText(
                        "Todavía no tienes registros."
                );
                return;
            }

            while (cursor.moveToNext()) {

                String emocion = cursor.getString(
                        cursor.getColumnIndexOrThrow("emocion")
                );

                String descripcion = cursor.getString(
                        cursor.getColumnIndexOrThrow("descripcion")
                );

                long fecha = cursor.getLong(
                        cursor.getColumnIndexOrThrow("fecha")
                );

                historial.append(formato.format(new Date(fecha)))
                        .append("\n\n")
                        .append("Estado: ")
                        .append(emocion)
                        .append("\n")
                        .append(descripcion)
                        .append("\n\n")
                        .append("──────────────────")
                        .append("\n\n");
            }
        }

        txtHistorial.setText(historial.toString());
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
