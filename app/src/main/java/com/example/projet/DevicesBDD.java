package com.example.projet;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DevicesBDD {

    private static final String TAG = "DevicesBDD";

    private static final int VERSION = 1;
    private static final String NOM_BDD = "devices.db";

    private static final String TABLE_DEVICES = "table_devices";
    private static final String COL_NAME_LAMP = "NAME_LAMP";
    private static final String COL_NAME_DEVICE = "NAME_DEVICE";
    private static final String COL_ADDRESS_MAC = "ADDRESS__MAC";

    private SQLiteDatabase bdd;
    private DeviceSQLite devices;

    public DevicesBDD(Context context) {
        devices = new DeviceSQLite(context, NOM_BDD, null, VERSION);
    }

    public void openForWrite() {
        bdd = devices.getWritableDatabase();
    }

    public void openForRead() {
        bdd = devices.getReadableDatabase();
    }

    public void close() {
        bdd.close();
    }

    public SQLiteDatabase getBdd() {
        return bdd;
    }

    public void insertDevice(String nameLamp, BluetoothDevice device) {
        String request = "INSERT INTO " + TABLE_DEVICES + " ( " + COL_NAME_LAMP + "," + COL_NAME_DEVICE+ "," + COL_ADDRESS_MAC + ") VALUES ( '" + nameLamp + "','" + device.getName()+ "','" + device.getAddress() + "')";
        bdd.execSQL(request);
    }

    public void updateName(String oldNameLamp, String newNameLamp) {
        String request = "UPDATE " + TABLE_DEVICES + " SET " + COL_NAME_LAMP + " = '" + newNameLamp + "' WHERE " + COL_NAME_LAMP + " = '" + oldNameLamp +"';";
        bdd.execSQL(request);
    }

    public void deleteDevice(String nameLamp) {
        String request = "DELETE FROM " + TABLE_DEVICES + " WHERE " + COL_NAME_LAMP +" = '" + nameLamp +"' ;";
        System.out.println(request);
        bdd.execSQL(request);
    }



    public void getAllDevices(ArrayList<String> namesLamp,ArrayList<String> macAdresses) {

        String request = "SELECT * FROM " + TABLE_DEVICES;

        Cursor c = bdd.rawQuery(request,null);

        c.moveToFirst();
        Log.d(TAG, "getAllDevices: " + c.getCount());
        if (c.getCount() == 0) {
            c.close();
        }
        else
        {
            do {
                namesLamp.add(c.getString(c.getColumnIndex(COL_NAME_LAMP)));
                macAdresses.add(c.getString(c.getColumnIndex(COL_ADDRESS_MAC)));
            } while (c.moveToNext());
            c.close();
        }
    }

}
