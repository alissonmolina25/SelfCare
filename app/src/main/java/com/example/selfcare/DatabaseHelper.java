
package com.example.selfcare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos.
    private static final String DATABASE_NAME = "selfcare.db";

    // Versión de la base de datos.
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla donde guardaremos los usuarios.
    private static final String TABLE_USUARIOS = "usuarios";

    // Columnas de la tabla.
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_CORREO = "correo";
    private static final String COLUMN_CONTRASENA = "contrasena";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Crea la tabla de usuarios la primera vez que se abre la app.
        String crearTablaUsuarios =
                "CREATE TABLE " + TABLE_USUARIOS + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NOMBRE + " TEXT NOT NULL, " +
                        COLUMN_CORREO + " TEXT NOT NULL UNIQUE, " +
                        COLUMN_CONTRASENA + " TEXT NOT NULL" +
                        ")";

        db.execSQL(crearTablaUsuarios);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // Guarda un nuevo usuario.
    public boolean registrarUsuario(
            String nombre,
            String correo,
            String contrasena
    ) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put(COLUMN_NOMBRE, nombre);
        valores.put(COLUMN_CORREO, correo);
        valores.put(COLUMN_CONTRASENA, contrasena);

        long resultado = db.insert(
                TABLE_USUARIOS,
                null,
                valores
        );

        db.close();

        return resultado != -1;
    }

    // Comprueba si un correo ya está registrado.
    public boolean existeCorreo(String correo) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USUARIOS,
                new String[]{COLUMN_ID},
                COLUMN_CORREO + " = ?",
                new String[]{correo},
                null,
                null,
                null
        );

        boolean existe = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return existe;
    }

    // Comprueba correo y contraseña para iniciar sesión.
    public boolean verificarUsuario(
            String correo,
            String contrasena
    ) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USUARIOS,
                new String[]{COLUMN_ID},
                COLUMN_CORREO + " = ? AND " +
                        COLUMN_CONTRASENA + " = ?",
                new String[]{correo, contrasena},
                null,
                null,
                null
        );

        boolean usuarioCorrecto = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return usuarioCorrecto;
    }
}