package com.example.selfcarenuevo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "selfcare.db";
    private static final int DB_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE usuarios (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT NOT NULL, " +
                        "correo TEXT NOT NULL UNIQUE, " +
                        "salt TEXT NOT NULL, " +
                        "password_hash TEXT NOT NULL)"
        );

        crearTablaEstadosAnimo(db);
        crearTablaHabitos(db);
    }

    private void crearTablaEstadosAnimo(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS estados_animo (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "usuario_id INTEGER NOT NULL, " +
                        "emocion TEXT NOT NULL, " +
                        "descripcion TEXT NOT NULL, " +
                        "fecha INTEGER NOT NULL, " +
                        "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))"
        );
    }

    private void crearTablaHabitos(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS habitos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "usuario_id INTEGER NOT NULL, " +
                        "nombre TEXT NOT NULL, " +
                        "ultima_fecha_completado TEXT, " +
                        "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        if (oldVersion < 2) {
            crearTablaEstadosAnimo(db);
        }

        if (oldVersion < 3) {
            crearTablaHabitos(db);
        }
    }

    // REGISTRAR USUARIOS

    public boolean registrarUsuario(
            String nombre,
            String correo,
            String contrasena
    ) {
        try {
            String correoNormalizado =
                    correo.trim().toLowerCase(Locale.ROOT);

            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            byte[] hash = generarHash(contrasena, salt);

            ContentValues valores = new ContentValues();
            valores.put("nombre", nombre.trim());
            valores.put("correo", correoNormalizado);
            valores.put("salt", convertirHex(salt));
            valores.put("password_hash", convertirHex(hash));

            long resultado = getWritableDatabase().insert(
                    "usuarios",
                    null,
                    valores
            );

            return resultado != -1;

        } catch (Exception e) {
            return false;
        }
    }

    // INICIAR SESIÓN

    public long iniciarSesion(
            String correo,
            String contrasena
    ) {
        String correoNormalizado =
                correo.trim().toLowerCase(Locale.ROOT);

        try (Cursor cursor = getReadableDatabase().query(
                "usuarios",
                new String[]{"id", "salt", "password_hash"},
                "correo = ?",
                new String[]{correoNormalizado},
                null,
                null,
                null
        )) {

            if (!cursor.moveToFirst()) {
                return -1;
            }

            long id = cursor.getLong(0);
            byte[] salt = leerHex(cursor.getString(1));
            byte[] hashGuardado = leerHex(cursor.getString(2));
            byte[] hashIngresado =
                    generarHash(contrasena, salt);

            if (MessageDigest.isEqual(
                    hashGuardado,
                    hashIngresado
            )) {
                return id;
            }

        } catch (Exception e) {
            return -1;
        }

        return -1;
    }

    // GUARDAR ESTADO DE ÁNIMO

    public boolean guardarEstadoAnimo(
            long usuarioId,
            String emocion,
            String descripcion
    ) {
        if (usuarioId < 1) {
            return false;
        }

        ContentValues valores = new ContentValues();
        valores.put("usuario_id", usuarioId);
        valores.put("emocion", emocion);
        valores.put("descripcion", descripcion);
        valores.put("fecha", System.currentTimeMillis());

        long resultado = getWritableDatabase().insert(
                "estados_animo",
                null,
                valores
        );

        return resultado != -1;
    }

    // OBTENER ESTADOS DE ÁNIMO

    public Cursor obtenerEstadosAnimo(long usuarioId) {
        return getReadableDatabase().query(
                "estados_animo",
                new String[]{"id", "emocion", "descripcion", "fecha"},
                "usuario_id = ?",
                new String[]{String.valueOf(usuarioId)},
                null,
                null,
                "fecha DESC, id DESC"
        );
    }

    // AGREGAR HÁBITO

    public boolean agregarHabito(
            long usuarioId,
            String nombre
    ) {
        if (usuarioId < 1 || nombre.trim().isEmpty()) {
            return false;
        }

        ContentValues valores = new ContentValues();
        valores.put("usuario_id", usuarioId);
        valores.put("nombre", nombre.trim());

        long resultado = getWritableDatabase().insert(
                "habitos",
                null,
                valores
        );

        return resultado != -1;
    }

    // OBTENER HÁBITOS DEL USUARIO

    public Cursor obtenerHabitos(long usuarioId) {
        return getReadableDatabase().query(
                "habitos",
                new String[]{
                        "id",
                        "nombre",
                        "ultima_fecha_completado"
                },
                "usuario_id = ?",
                new String[]{String.valueOf(usuarioId)},
                null,
                null,
                "id DESC"
        );
    }

    // MARCAR O DESMARCAR HÁBITO

    public boolean actualizarHabito(
            long usuarioId,
            long habitoId,
            String fechaCompletado
    ) {
        ContentValues valores = new ContentValues();

        if (fechaCompletado == null) {
            valores.putNull("ultima_fecha_completado");
        } else {
            valores.put(
                    "ultima_fecha_completado",
                    fechaCompletado
            );
        }

        int filas = getWritableDatabase().update(
                "habitos",
                valores,
                "id = ? AND usuario_id = ?",
                new String[]{
                        String.valueOf(habitoId),
                        String.valueOf(usuarioId)
                }
        );

        return filas > 0;
    }

    // ELIMINAR HÁBITO

    public boolean eliminarHabito(
            long usuarioId,
            long habitoId
    ) {
        int filas = getWritableDatabase().delete(
                "habitos",
                "id = ? AND usuario_id = ?",
                new String[]{
                        String.valueOf(habitoId),
                        String.valueOf(usuarioId)
                }
        );

        return filas > 0;
    }

    // SEGURIDAD DE CONTRASEÑAS

    private byte[] generarHash(
            String contrasena,
            byte[] salt
    ) throws Exception {

        PBEKeySpec spec = new PBEKeySpec(
                contrasena.toCharArray(),
                salt,
                120000,
                256
        );

        try {
            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return fabrica.generateSecret(spec).getEncoded();

        } finally {
            spec.clearPassword();
        }
    }

    private String convertirHex(byte[] datos) {
        StringBuilder resultado = new StringBuilder();

        for (byte dato : datos) {
            resultado.append(
                    String.format(
                            Locale.ROOT,
                            "%02x",
                            dato & 0xff
                    )
            );
        }

        return resultado.toString();
    }

    private byte[] leerHex(String texto) {
        byte[] datos = new byte[texto.length() / 2];

        for (int i = 0; i < datos.length; i++) {
            int posicion = i * 2;

            datos[i] = (byte) Integer.parseInt(
                    texto.substring(posicion, posicion + 2),
                    16
            );
        }

        return datos;
    }
}
