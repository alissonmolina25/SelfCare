package com.example.selfcarenuevo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HabitosActivity extends AppCompatActivity {

    private EditText etNuevoHabito;
    private LinearLayout contenedorHabitos;
    private DatabaseHelper databaseHelper;
    private long idUsuario;

    private final int verdeOscuro =
            Color.rgb(41, 62, 51);

    private final int verdeBoton =
            Color.rgb(73, 102, 83);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitos);

        SharedPreferences sesion = getSharedPreferences(
                "SesionSelfCare",
                MODE_PRIVATE
        );

        idUsuario = sesion.getLong("idUsuario", -1);

        if (idUsuario < 1) {
            startActivity(new Intent(
                    this,
                    LoginActivity.class
            ));
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);

        etNuevoHabito = findViewById(R.id.etNuevoHabito);
        contenedorHabitos = findViewById(R.id.contenedorHabitos);

        Button btnAgregarHabito =
                findViewById(R.id.btnAgregarHabito);

        Button btnVolver =
                findViewById(R.id.btnVolver);

        mostrarHabitos();

        btnAgregarHabito.setOnClickListener(v -> {

            String nombre = etNuevoHabito.getText()
                    .toString().trim();

            if (nombre.isEmpty()) {
                etNuevoHabito.setError(
                        "Escribe un hábito"
                );
                return;
            }

            boolean guardado =
                    databaseHelper.agregarHabito(
                            idUsuario,
                            nombre
                    );

            if (guardado) {

                etNuevoHabito.setText("");
                mostrarHabitos();

                Toast.makeText(
                        this,
                        "Hábito agregado correctamente",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "No se pudo guardar el hábito",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    private String obtenerFechaHoy() {

        SimpleDateFormat formato =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.ROOT
                );

        return formato.format(new Date());
    }

    private void mostrarHabitos() {

        contenedorHabitos.removeAllViews();

        String fechaHoy = obtenerFechaHoy();

        try (Cursor cursor =
                     databaseHelper.obtenerHabitos(idUsuario)) {

            if (cursor.getCount() == 0) {

                TextView mensaje = new TextView(this);
                mensaje.setText(
                        "Todavía no tienes hábitos.\n" +
                                "¡Agrega el primero!"
                );
                mensaje.setTextSize(16);
                mensaje.setTextColor(verdeOscuro);
                mensaje.setPadding(8, 20, 8, 20);

                contenedorHabitos.addView(mensaje);
                return;
            }

            while (cursor.moveToNext()) {

                long habitoId = cursor.getLong(
                        cursor.getColumnIndexOrThrow("id")
                );

                String nombre = cursor.getString(
                        cursor.getColumnIndexOrThrow("nombre")
                );

                String ultimaFecha = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "ultima_fecha_completado"
                        )
                );

                boolean completadoHoy =
                        fechaHoy.equals(ultimaFecha);

                agregarHabitoEnPantalla(
                        habitoId,
                        nombre,
                        completadoHoy,
                        fechaHoy
                );
            }
        }
    }

    private void agregarHabitoEnPantalla(
            long habitoId,
            String nombre,
            boolean completadoHoy,
            String fechaHoy
    ) {

        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );

        fila.setPadding(0, 10, 0, 10);

        CheckBox checkBox = new CheckBox(this);

        checkBox.setText(nombre);
        checkBox.setTextSize(16);
        checkBox.setTextColor(verdeOscuro);

        checkBox.setButtonTintList(
                ColorStateList.valueOf(verdeBoton)
        );

        checkBox.setChecked(completadoHoy);

        LinearLayout.LayoutParams parametrosCheck =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        fila.addView(checkBox, parametrosCheck);

        Button btnEliminar = new Button(this);

        btnEliminar.setText("Eliminar");
        btnEliminar.setAllCaps(false);
        btnEliminar.setTextSize(12);
        btnEliminar.setTextColor(Color.WHITE);
        btnEliminar.setBackgroundTintList(
                ColorStateList.valueOf(verdeBoton)
        );

        fila.addView(btnEliminar);

        checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    String fecha = isChecked
                            ? fechaHoy
                            : null;

                    boolean actualizado =
                            databaseHelper.actualizarHabito(
                                    idUsuario,
                                    habitoId,
                                    fecha
                            );

                    if (!actualizado) {
                        Toast.makeText(
                                this,
                                "No se pudo actualizar",
                                Toast.LENGTH_SHORT
                        ).show();

                        buttonView.setOnCheckedChangeListener(null);
                        buttonView.setChecked(!isChecked);
                        buttonView.setOnCheckedChangeListener(
                                (boton, marcado) -> mostrarHabitos()
                        );
                    }
                }
        );

        btnEliminar.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar hábito")
                    .setMessage(
                            "¿Quieres eliminar \"" +
                                    nombre + "\"?"
                    )
                    .setNegativeButton(
                            "Cancelar",
                            (dialog, which) -> dialog.dismiss()
                    )
                    .setPositiveButton(
                            "Eliminar",
                            (dialog, which) -> {

                                boolean eliminado =
                                        databaseHelper.eliminarHabito(
                                                idUsuario,
                                                habitoId
                                        );

                                if (eliminado) {

                                    mostrarHabitos();

                                    Toast.makeText(
                                            this,
                                            "Hábito eliminado",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "No se pudo eliminar",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    )
                    .show();
        });

        contenedorHabitos.addView(fila);

        TextView separador = new TextView(this);

        separador.setBackgroundColor(
                Color.rgb(129, 154, 136)
        );

        LinearLayout.LayoutParams parametrosSeparador =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );

        parametrosSeparador.setMargins(0, 8, 0, 8);

        contenedorHabitos.addView(
                separador,
                parametrosSeparador
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            mostrarHabitos();
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
