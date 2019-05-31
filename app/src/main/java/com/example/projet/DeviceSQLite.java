package com.example.projet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DeviceSQLite extends SQLiteOpenHelper {

    private static final String TABLE_DEVICES = "table_devices";
    private static final String COL_ID = "ID";
    private static final String COL_NAME_LAMP = "NAME_LAMP";
    private static final String COL_NAME_DEVICE = "NAME_DEVICE";
    private static final String COL_ADDRESS_MAC = "ADDRESS__MAC";


    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_DEVICES + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME_LAMP + " TEXT NOT NULL, " + COL_NAME_DEVICE + " TEXT NOT NULL, " + COL_ADDRESS_MAC +" TEXT NOT NULL "+");";

    public DeviceSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super (context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_DEVICES);
        onCreate(db);
    }
}
