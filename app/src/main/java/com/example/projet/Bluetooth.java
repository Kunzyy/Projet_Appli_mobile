package com.example.projet;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class Bluetooth extends AppCompatActivity {

    private final static String TAG = "Bluetooth";
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice device = null;
    private String nameLamp = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        TextView etat = findViewById(R.id.etat);
        final ListView mListView = findViewById(R.id.btList);
        final ArrayList<String> deviceList= new ArrayList<>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null || bluetoothAdapter.isEnabled() == false) {
            if(bluetoothAdapter == null) {
                Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
                etat.setText("Cet appareil ne dispose pas de Bluetooth !");
            }
            if(!bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "enableDisableBT: Bluetooth désactivé.");
                etat.setText("Bluetooth désactivé !");
            }
        }
        else {

            Set<BluetoothDevice> setpairedDevices;
            setpairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice blueDevice : setpairedDevices) {
                Log.d(TAG, "setupDevice: Device = " + blueDevice.getName());
            }

            final BluetoothDevice[] pairedDevices = setpairedDevices.toArray(new BluetoothDevice[setpairedDevices.size()]);
            for (int i = 0; i < pairedDevices.length; i++) {
                deviceList.add(pairedDevices[i].getName());
            }

            //Setup de la liste d'affichage des devices
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, deviceList);
            mListView.setAdapter(adapter);

            //Selectionnement du device par la listview et retour vers la mainActivity
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    Log.d(TAG, "onItemClick: Open " + pairedDevices[position].getName() + " device");

                    //Pop-up pour demander le nom de la lampe souhaité
                    AlertDialog.Builder builder = new AlertDialog.Builder(Bluetooth.this);
                    builder.setTitle("Nom de la lampe");
                    builder.setMessage("Exemple : Salon 1, Cuisine 2");

                    // Set up the input
                    final EditText input = new EditText(Bluetooth.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            nameLamp = input.getText().toString();
                            device = pairedDevices[position];
                            insertDevice(nameLamp,device);

                            Log.d(TAG, "onItemClick: NameDevice - End activity");
                            Intent intent = new Intent(Bluetooth.this, MainActivity.class);
                            intent.putExtra("devicePaired", true);
                            intent.putExtra("btDevice", device);
                            intent.putExtra("nameLamp", nameLamp);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
    }

    private void insertDevice(String nameLamp, BluetoothDevice device)
    {
        DevicesBDD devicesBDD = new DevicesBDD(this);
        devicesBDD.openForWrite();
        devicesBDD.insertDevice(nameLamp,device);
        devicesBDD.close();
    }

}
