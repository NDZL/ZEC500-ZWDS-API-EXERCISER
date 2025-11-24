package com.zebra.zwds.pocsampledev;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.zebra.zwds.pocsampledev.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String WIRELESS_DEV_SERVICE_PACKAGE = "com.zebra.wirelessdeveloperservice";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button btEnable = findViewById(R.id.btl_enale);
        Button btDisable = findViewById(R.id.btl_desable);
        Button startScan = findViewById(R.id.startScan);
        Button stopScan = findViewById(R.id.stopScan);
        Button getStatus = findViewById(R.id.btngetStatus);
        Button switchToDesktop = findViewById(R.id.btnDesktopMode);
        Button switchToMirror = findViewById(R.id.btnMirrorMode);
        Button enableDisplay = findViewById(R.id.enableDisplay);
        Button getDisplays = findViewById(R.id.btngetDisplay);
        EditText deviceAddressEdiText = findViewById(R.id.deviceAddressEditText);

        TextView resultTextView = findViewById(R.id.resultTextView);

        TextView connectLimit  = findViewById(R.id.connectTextBox);
        TextView disconnectLimit  = findViewById(R.id.disconnectTextBox);

        // Register a local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String resultText = intent.getStringExtra("result_text");
                resultTextView.setText(resultText);
            }
        }, new IntentFilter("com.zebra.pocsampledev.UPDATE_UI"));



        button1.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.INIT_DEV_SERVICE");

            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "NIT_DEV_SERVICE");
            resultIntent.putExtra("request_id", 12345);
            //send a pending intent to the service
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            //set the receiver action and pkg name to get broadcast from the service
          //  intent.putExtra("STATE_CHANGE_RCV_ACTION", "com.zebra.pocsampledev.WIRELESS_STATE_CHANGE");
            intent.putExtra("STATE_CHANGE_RCV_PKG", "com.zebra.pocsampledev");

            //set the package name to the wireless developer service package
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);
            startService(intent);
        });

        startScan.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.START_WIRELESS_DISPLAY_SCAN");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "START_WIRELESS_DISPLAY_SCAN");
            resultIntent.putExtra("request_id", 1010);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            sendBroadcast(intent);
        });

        stopScan.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.STOP_WIRELESS_DISPLAY_SCAN");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "STOP_WIRELESS_DISPLAY_SCAN");
            resultIntent.putExtra("request_id", 1020);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            sendBroadcast(intent);
        });

        button2.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.CONNECT_WIRELESS_DISPLAY");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "CONNECT_WIRELESS_DISPLAY");
            resultIntent.putExtra("request_id", 1001);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            String deviceAddress = deviceAddressEdiText.getText().toString(); // Get the value from EditText
            intent.putExtra("DEVICE_ID", deviceAddress);
            // Show a Toast message
            Toast.makeText(this, "Going to connect to device: " + deviceAddress, Toast.LENGTH_SHORT).show();
            //ser maverick device address
          //  intent.putExtra("DEVICE_ID", "8a:e0:12:5e:0f:88");
          //  intent.putExtra("DEVICE_ID", "AKKR");

            sendBroadcast(intent);
        });

        button3.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.DISCONNECT_WIRELESS_DISPLAY");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "DISCONNECT_WIRELESS_DISPLAY");
            resultIntent.putExtra("request_id", 1002);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            sendBroadcast(intent);
        });

        button4.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.DEINIT_DEV_SERVICE");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);
            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "DEINIT_DEV_SERVICE");
            resultIntent.putExtra("request_id", 1000);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            sendBroadcast(intent);
        });


        btEnable.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.SET_PROXIMITY_CONNECTION");

            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "SET_PROXIMITY_CONNECT");
            resultIntent.putExtra("request_id", 2001);
            //send a pending intent to the service
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            //set the package name to the wireless developer service package
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set proximity connect to true
            intent.putExtra("PROXIMITY_CONNECT", "ON");
            //set proximity disconnect to true
            intent.putExtra("PROXIMITY_DISCONNECT", "ON");

            String connectText = connectLimit.getText().toString();
            int connectValue = Integer.parseInt(connectText);
            //set connect threshold
           // intent.putExtra("CONNECT_THRESHOLD", 4);
            intent.putExtra("CONNECT_THRESHOLD", connectValue);
            //set disconnect threshold
            String diconnectText = disconnectLimit.getText().toString();
            int disconnectValue = Integer.parseInt(diconnectText);
           // intent.putExtra("DISCONNECT_THRESHOLD", 8);
            intent.putExtra("DISCONNECT_THRESHOLD", disconnectValue);

            sendBroadcast(intent);
        });

        btDisable.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.SET_PROXIMITY_CONNECTION");
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "SET_PROXIMITY_CONNECT");
            resultIntent.putExtra("request_id", 2002);
            //send a pending intent to the service
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            //set the package name to the wireless developer service package
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set proximity connect to true
            intent.putExtra("PROXIMITY_CONNECT", "OFF");
            //set proximity disconnect to true
            intent.putExtra("PROXIMITY_DISCONNECT", "OFF");

         //   intent.putExtra("CONNECT_THRESHOLD", 4);
            //set disconnect threshold
          //  intent.putExtra("DISCONNECT_THRESHOLD", 8);
            String connectText = connectLimit.getText().toString();
            int connectValue = Integer.parseInt(connectText);
            //set connect threshold
            // intent.putExtra("CONNECT_THRESHOLD", 4);
            intent.putExtra("CONNECT_THRESHOLD", connectValue);
            //set disconnect threshold
            String diconnectText = disconnectLimit.getText().toString();
            int disconnectValue = Integer.parseInt(diconnectText);
            // intent.putExtra("DISCONNECT_THRESHOLD", 8);
            intent.putExtra("DISCONNECT_THRESHOLD", disconnectValue);

            sendBroadcast(intent);
        });

        getStatus.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.GET_STATUS");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "GET_STATUS");
            resultIntent.putExtra("request_id", 3000);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            sendBroadcast(intent);
        });

        switchToDesktop.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.SWITCH_DESKTOP_MODE");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "SWITCH_DESKTOP_MODE");
            resultIntent.putExtra("request_id", 4001);//
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            //set the package name to the wireless developer service package
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set proximity connect to true
            intent.putExtra("DESKTOP_MODE", "ON");

            sendBroadcast(intent);
        });

        enableDisplay.setOnClickListener( View -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.ENABLE_WIRELESS_DISPLAY");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "ENABLE_WIRELESS_DISPLAY");
            resultIntent.putExtra("request_id", 6000);//
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            //set the package name to the wireless developer service package
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set proximity connect to true
            intent.putExtra("ENABLE_DISPLAY", "ON");

            sendBroadcast(intent);
        });

        switchToMirror.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.SWITCH_DESKTOP_MODE");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "SWITCH_DESKTOP_MODE");
            resultIntent.putExtra("request_id", 4002);//
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            //set the package name to the wireless developer service package
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set proximity connect to true
            intent.putExtra("DESKTOP_MODE", "OFF");

            sendBroadcast(intent);
        });



        getDisplays.setOnClickListener(view -> {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.GET_AVAILABLE_DISPLAYS");
            intent.setPackage(WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(this, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "GET_AVAILABLE_DISPLAYS");
            resultIntent.putExtra("request_id", 5000);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(this, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            sendBroadcast(intent);
        });


    }

    //method initDeveService

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}