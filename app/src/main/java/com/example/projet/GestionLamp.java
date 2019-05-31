package com.example.projet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class GestionLamp extends AppCompatActivity {

    private static final String TAG = "GestionLamp";
    private BluetoothDevice device = null;
    private String nameLamp = null;
    private BluetoothSocket socket = null;
    private OutputStream sendStream = null;
    private String lastChoiced = null;

    private int connected = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_lamp);


        Log.d(TAG, "onCreate: Gestion");

        final TextView textNameDevice = findViewById(R.id.nameDevice);
        final TextView textDevice = findViewById(R.id.device);
        final TextView textEtat = findViewById(R.id.textEtat);

        Switch onOff = findViewById(R.id.ONOFF);


        final Button btnWhite = findViewById(R.id.btnWhite);
        final Button btnRed = findViewById(R.id.btnRed);
        final Button btnGreen = findViewById(R.id.btnGreen);
        final Button btnBlue = findViewById(R.id.btnBlue);
        final Button btnName = findViewById(R.id.btnName);
        final Button btnSupp = findViewById(R.id.btnSupprimer);
        final Button btnDeco = findViewById(R.id.btnDeconnexion);

        final ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(btnBlue);
        buttons.add(btnRed);
        buttons.add(btnGreen);
        buttons.add(btnWhite);


        onOff.setChecked(false);
        for(Button i : buttons)
        {
            i.setEnabled(false);
        }

        //Récupération du device et initialisation du socket
        if(getIntent().hasExtra("device")) {
            Log.d(TAG, "onCreate: Noms bien setup");
            device = getIntent().getExtras().getParcelable("device");
            nameLamp = getIntent().getStringExtra("nameLamp");
            Log.d(TAG, "Names : "+ nameLamp + "and" + device.getName());
            textNameDevice.setText(nameLamp);
            textDevice.setText(device.getName());

            try {
                // On récupère le socket de notre périphérique
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            connectToDevice(textEtat,buttons,onOff);
        }
        else
        {
            Log.d(TAG, "onCreate: Error Intent Device");
            textEtat.setText("Erreur de connexion !");
        }

        while(connected == 0)
        {
            Log.d(TAG, "onCreate: Tentative de connexion");
            if(connected == 1) {
                textEtat.setText("La lampe est connectée.");
                onOff.setEnabled(true);
                sendData("o");
                for(Button i : buttons)
                {
                    i.setEnabled(false);
                }
            }
            if(connected == -1) {
                textEtat.setText("Erreur de connexion !");
                for (Button i : buttons) {
                    i.setEnabled(false);
                }
                onOff.setEnabled(false);
            }
        }

        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GestionLamp.this);
                builder.setTitle("Nom de la lampe");
                builder.setMessage("Exemple : Salon 1, Cuisine 2");

                // Set up the input
                final EditText input = new EditText(GestionLamp.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldNameLamp = nameLamp;
                        nameLamp = input.getText().toString();
                        changeName(oldNameLamp,nameLamp);
                        textNameDevice.setText(nameLamp);
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

        btnSupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connected == 1) {
                    sendData("o");
                }
                deleteDevice(nameLamp);
                close();
                Log.d(TAG, "onItemClick: Suppresion Lampe - End activity");
                Intent intent = new Intent(GestionLamp.this, MainActivity.class);
                intent.putExtra("devicePaired", false);
                startActivity(intent);
            }
        });

        btnDeco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connected == 1) {
                    sendData("o");
                }
                close();
                Log.d(TAG, "onItemClick: Deconnexion - End activity");
                Intent intent = new Intent(GestionLamp.this, MainActivity.class);
                intent.putExtra("devicePaired", false);
                startActivity(intent);
            }
        });

        btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("a");
                lastChoiced = "a";
                btnRed.setBackgroundResource(R.color.gray);
                btnGreen.setBackgroundResource(R.color.gray);
                btnBlue.setBackgroundResource(R.color.gray);
            }
        });

        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("r");
                lastChoiced = "r";
                btnRed.setBackgroundResource(R.color.red);
                btnGreen.setBackgroundResource(R.color.gray);
                btnBlue.setBackgroundResource(R.color.gray);
            }
        });

        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("g");
                lastChoiced = "g";
                btnRed.setBackgroundResource(R.color.gray);
                btnGreen.setBackgroundResource(R.color.green);
                btnBlue.setBackgroundResource(R.color.gray);
            }
        });

        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("b");
                lastChoiced = "b";

                btnRed.setBackgroundResource(R.color.gray);
                btnGreen.setBackgroundResource(R.color.gray);
                btnBlue.setBackgroundResource(R.color.blue);
            }
        });

        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(lastChoiced != null) {
                        sendData(lastChoiced);
                    }
                    else
                    {
                        sendData("a");
                    }

                    for(Button i : buttons)
                    {
                        i.setEnabled(true);
                    }

                }
                else
                {
                    sendData("o");
                    for(Button i : buttons)
                    {
                        i.setEnabled(false);
                    }
                }
            }
        });

    }

    private void connectToDevice(final TextView textEtat, final ArrayList<Button> buttons, final Switch onOff){

        new Thread() {
            @Override
            public void run() {
                try {
                    socket.connect();// Tentative de connexion
                    Log.d(TAG, "run: Connecté au device");
                    connected = 1;

                    // Connexion réussie
                } catch (IOException e) {
                    Log.d(TAG, "run: Erreur de connexion");
                    connected = -1;
                    // Echec de la connexion
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void sendData(String data) {
        try
        {
            sendStream = socket.getOutputStream();
        } catch (IOException e)
        {
            // Erreur on arrive pas à déclarer la sortie donc l'envoi
            Log.d(TAG, "sendData: Impossible de déclarer la sortie");
        }

        Log.d(TAG, "...Sending data: " + data + "...");
        try {
            // On écrit les données dans le buffer d'envoi
            sendStream.write(data.getBytes());
            // On s'assure qu'elles soient bien envoyées
            sendStream.flush();
            Log.d(TAG, "sendData: Ok");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void close() {
        if(socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeName(String oldNameLamp, String newNameLamp)
    {
        DevicesBDD devicesBDD = new DevicesBDD(this);
        devicesBDD.openForWrite();
        devicesBDD.updateName(oldNameLamp,newNameLamp);
        devicesBDD.close();
    }

    private void deleteDevice(String nameLamp)
    {
        DevicesBDD devicesBDD = new DevicesBDD(this);
        devicesBDD.openForWrite();
        devicesBDD.deleteDevice(nameLamp);
        devicesBDD.close();
    }

}
