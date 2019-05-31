package com.example.projet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice device = null;
    private String nameLamp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnBtActivity = findViewById(R.id.bluetoothActivity);
        final TextView btText = findViewById(R.id.btText);
        final ImageView img = findViewById(R.id.img);
        final TextView lamp1 = findViewById(R.id.lamp1);
        final TextView lamp2 = findViewById(R.id.lamp2);
        final TextView textList1 = findViewById(R.id.textList1);
        final TextView textList2= findViewById(R.id.textList2);
        final Button btnLampActivity = findViewById(R.id.lampActivity);
        final TextView textConnexion = findViewById(R.id.textConnexion);

        ListView mListView = findViewById(R.id.lampList);
        final ArrayList<String> listLamp= new ArrayList<>();
        final ArrayList<String> macAdresses= new ArrayList<>();


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            btText.setText("Cet appareil ne dispose pas de Bluetooth !\nImpossible de contrôler une lampe connectée");
            btnBtActivity.setEnabled(false);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "enableBT: enabling BT.");
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBTIntent);
            }

            //Récupération du device et du nom de la lampe si l'activité de recherche a été bien éxécuté
            if (getIntent().hasExtra("devicePaired")) {
                //Setup de la liste d'affichage des lampes
                listLamp.clear();
                macAdresses.clear();
                readforListView(listLamp,macAdresses);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, listLamp);
                mListView.setAdapter(adapter);

                if (getIntent().getBooleanExtra("devicePaired", false)) {
                    device = getIntent().getExtras().getParcelable("btDevice");
                    nameLamp = getIntent().getStringExtra("nameLamp");

                    //Modifications graphiques pour pouvoir accéder à la gestion de la lampe
                    btText.setText("Lampe connectée : ");
                    img.setVisibility(View.VISIBLE);
                    lamp1.setVisibility(View.VISIBLE);
                    lamp2.setVisibility(View.VISIBLE);
                    btnLampActivity.setVisibility(View.VISIBLE);
                    lamp1.setText(nameLamp);
                    lamp2.setText(device.getName());
                }
                else {
                    btText.setText("Aucune lampe connectée !");
                    img.setVisibility(View.INVISIBLE);
                    lamp1.setVisibility(View.INVISIBLE);
                    lamp2.setVisibility(View.INVISIBLE);
                    btnLampActivity.setVisibility(View.INVISIBLE);
                    textConnexion.setVisibility(View.INVISIBLE);
                }

            } else {
                btText.setText("Aucune lampe connectée !");
                img.setVisibility(View.INVISIBLE);
                lamp1.setVisibility(View.INVISIBLE);
                lamp2.setVisibility(View.INVISIBLE);
                btnLampActivity.setVisibility(View.INVISIBLE);
            }

            if(listLamp.size() > 0)
            {
                mListView.setVisibility(View.VISIBLE);
                textList1.setVisibility(View.VISIBLE);
                textList2.setVisibility(View.VISIBLE);
            }
            else
            {
                mListView.setVisibility(View.INVISIBLE);
                textList1.setVisibility(View.INVISIBLE);
                textList2.setVisibility(View.INVISIBLE);
            }

            //Setup de la liste d'affichage des lampes
            listLamp.clear();
            macAdresses.clear();
            readforListView(listLamp,macAdresses);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, listLamp);
            mListView.setAdapter(adapter);

            //Lancement de l'activité de recherche d'appareils/modules
            btnBtActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent bluetoothActivity = new Intent(MainActivity.this, Bluetooth.class);
                    startActivity(bluetoothActivity);
                }
            });

            //Lancement de l'activité de gestion de la lampe
            btnLampActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent pour envoyer le device vers LampActivity
                    textConnexion.setVisibility(View.VISIBLE);
                    Intent lampActivity = new Intent(MainActivity.this, GestionLamp.class);
                    Log.d(TAG, "onClick: " + nameLamp + "and" + device.getName());
                    lampActivity.putExtra("device", device);
                    lampActivity.putExtra("nameLamp", nameLamp);
                    startActivity(lampActivity);
                }
            });

            //Selectionnement du device par la listview et retour vers la mainActivity
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    Log.d(TAG, "onItemClick: Open " + listLamp.get(position));
                    nameLamp = listLamp.get(position);
                    device = bluetoothAdapter.getRemoteDevice(macAdresses.get(position));
                    Log.d(TAG, "onItemClick: ListView Clicked");
                    //Modifications graphiques pour pouvoir accéder à la gestion de la lampe
                    btnBtActivity.setText("Changer de lampe");
                    btText.setText("Lampe connectée : ");
                    img.setVisibility(View.VISIBLE);
                    lamp1.setVisibility(View.VISIBLE);
                    lamp2.setVisibility(View.VISIBLE);
                    btnLampActivity.setVisibility(View.VISIBLE);
                    lamp1.setText(nameLamp);
                    lamp2.setText(device.getName());
                }
            });

        }

    }
    private void readforListView(ArrayList<String> namesLamp,ArrayList<String> macAdresses)
    {
        DevicesBDD devicesBDD = new DevicesBDD(this);
        devicesBDD.openForRead();
        devicesBDD.getAllDevices(namesLamp,macAdresses);
        devicesBDD.close();
    }
}

