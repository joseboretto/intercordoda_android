package com.example.inter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
 
public class BaseDatos extends SQLiteOpenHelper {
 
    private static String DB_PATH = "/data/data/com.jose.intercordoba/databases/";
    private static String DB_NAME = "DBexterna3.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
 
    public BaseDatos(Context contexto, String nombre, CursorFactory factory,
            int version) {
 
        super(contexto, nombre, factory, version);
        this.myContext = contexto;
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // No hacemos nada aqui
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cuando haya cambios en la estructura deberemos
    }
 
    public void createDataBase() throws IOException {
 
        
            // Llamando a este método se crea la base de datos vacía en la ruta
            // por defecto del sistema de nuestra aplicación por lo que
            // podremos sobreescribirla con nuestra base de datos.
            this.getReadableDatabase();
 
            try {
 
                copyDataBase();
 
            } catch (IOException e) {
 
                throw new Error("Error copiando database");
            }
        }
    
 
   
 
    public void openDataBase() throws SQLException {
 
        // Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
 
    @Override
    public synchronized void close() {
 
        if (myDataBase != null)
            myDataBase.close();
 
        super.close();
    }
 
    private void copyDataBase() throws IOException {
 
        OutputStream databaseOutputStream = new FileOutputStream("" + DB_PATH + DB_NAME);
        InputStream databaseInputStream;
 
        byte[] buffer = new byte[1024];
        int length;
 
        databaseInputStream = myContext.getAssets().open("DBexterna3.db");
        while ((length = databaseInputStream.read(buffer)) > 0) {
            databaseOutputStream.write(buffer);
        }
 
        databaseInputStream.close();
        databaseOutputStream.flush();
        databaseOutputStream.close();
    }
 
}