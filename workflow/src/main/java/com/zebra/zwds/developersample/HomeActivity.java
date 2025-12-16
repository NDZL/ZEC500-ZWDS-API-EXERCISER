package com.zebra.zwds.developersample;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements DevServiceResponseListener {

    private static final String TAG = "HomeActivity";
    final Handler handler = new Handler(Looper.getMainLooper());
    private Button btnNfcPair;
    private Button btnScanConnect;
    private Button btnProximity;
    private Button btnDisconnect;
    private Button btnDesktopModeSettings;
    private LinearLayout lin_connect_options;
    private Toolbar toolbar;
    private TextView txt_connected_device_info;
    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "#MVK-Dev-Sample# HomeActivity registered for callbacks");
        initView();
        onClickListener();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "#MVK-Dev-Sample# HomeActivity unregistered from callbacks start");
        super.onStart();
        MyGlobalDevServiceResponseListener.getInstance().registerListener(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        MyGlobalDevServiceResponseListener.getInstance().unregisterListener(this);
        Log.d(TAG, "#MVK-Dev-Sample# HomeActivity unregistered from callbacks stop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "#MVK-Dev-Sample# HomeActivity unregistered from callbacks resume");
    }

    boolean isRestarted = false;

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "#MVK-Dev-Sample# HomeActivity unregistered from callbacks restart");
        isRestarted = true;
        boolean success = DeveloperService.getConnectionStatus(this);
        if (!success) {
            Log.e(TAG, "#MVK-Dev-Sample# HomeActivity failed to initialize developer service");
            Toast.makeText(this, "Failed to initialize developer service", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "#MVK-Dev-Sample# HomeActivity unregistered from callbacks pause");
        MyGlobalDevServiceResponseListener.getInstance().unregisterListener(this);
        Log.d(TAG, "#MVK-Dev-Sample# HomeActivity unregistered from callbacks stop");
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get app title from manifest
        TextView rightView = toolbar.findViewById(R.id.toolbar_right);
        if (SecureIntentActivity.isSecureModeEnabled()) {
            rightView.setText("Service running securely"); // Right side
        } else {
            rightView.setText("Service running insecurely"); // Right side
        }
        btnNfcPair = findViewById(R.id.btn_Nfc_tap);
        btnScanConnect = findViewById(R.id.btn_Scan_QR);
        btnProximity = findViewById(R.id.btn_proximity);
        btnDisconnect = findViewById(R.id.btn_disconnect_device);
        btnDesktopModeSettings = findViewById(R.id.btn_desktop_mode_settings);
        lin_connect_options = findViewById(R.id.lin_connection_options);
        txt_connected_device_info = findViewById(R.id.txt_connection_status);
        initializeView();
    }

    private void initializeView() {
        // Use the improved DeveloperService utility class for initialization
        boolean success = DeveloperService.initializeService(this);
        if (!success) {
            Log.e(TAG, "#MVK-Dev-Sample# HomeActivity failed to initialize developer service");
            Toast.makeText(this, "Failed to initialize developer service", Toast.LENGTH_SHORT).show();
        }
    }

    public static void disconnectDevice(android.content.Context context) {
        // Use the improved DeveloperService utility class for disconnection
        boolean success = DeveloperService.disconnectDevice(context);
        if (!success) {
            Log.e(TAG, "#MVK-Dev-Sample# HomeActivity failed to disconnect device");
            // Note: Can't show Toast here as this is a static method without UI context
        }
    }


    private void deInitializeView() {
        // Use the improved DeveloperService utility class for deinitialization
        boolean success = DeveloperService.deinitializeService(this);
        if (!success) {
            Log.e(TAG, "#MVK-Dev-Sample# HomeActivity failed to deinitialize developer service");
            Toast.makeText(this, "Failed to deinitialize developer service", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickListener() {
        btnNfcPair.setOnClickListener(v -> {
            Intent intent = new Intent(this, TapConnectActivity.class);
            startActivity(intent);
        });

        btnScanConnect.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ScanConnectActivity.class);
            startActivity(intent);
        });

        btnProximity.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ProximityActivity.class);
            startActivity(intent);
        });

        btnDisconnect.setOnClickListener(view -> {
            disconnectDevice(this);
            lin_connect_options.setVisibility(View.VISIBLE);
            txt_connected_device_info.setVisibility(View.GONE);
        });

        btnDesktopModeSettings.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, DesktopModeActivity.class);
            startActivity(intent);
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "#MVK-Dev-Sample# HomeActivity onDestroy: Called");
        UnRegisterDisplayCallback();
        deInitializeView();
    }

    private void UnRegisterDisplayCallback() {
        boolean successs = DeveloperService.getWirelessDisplayCallback(HomeActivity.this, "OFF");
        if (!successs) {
            Log.e(TAG, "#MVK-Dev-Sample# HomeActivity failed to get display callback developer service");
            Toast.makeText(HomeActivity.this, "Failed to get display callback developer service", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDevServiceResponseReceived(int reqID, int resultCode, String message, String reqType) {
        Log.d(TAG, "#MVK-Dev-Sample# HomeActivity received callback: " + reqType + " - " + message + " - " + resultCode);

        switch (reqType) {
            case "INIT SERVICE":
                Toast.makeText(this, reqType + " - " + message + " - " + resultCode, Toast.LENGTH_SHORT).show();
                boolean initSuccess = DeveloperService.getConnectionStatus(this);
                if (!initSuccess) {
                    Log.e(TAG, "#MVK-Dev-Sample# HomeActivity failed to initialize developer service");
                    Toast.makeText(this, "#MVK-Dev-Sample# Failed to initialize developer service", Toast.LENGTH_SHORT).show();
                }
                break;

            case "GET STATUS":
                Toast.makeText(this, reqType + " - " + message + " - " + resultCode, Toast.LENGTH_SHORT).show();
                if (message.contains("DISCONNECTED")) {
                    lin_connect_options.setVisibility(View.VISIBLE);
                    txt_connected_device_info.setVisibility(View.GONE);
                    if (!isRestarted) {
                        boolean successs = DeveloperService.startDisplayScan(HomeActivity.this);
                        if (!successs) {
                            Log.e(TAG, "#MVK-Dev-Sample# HomeActivity failed to get display scan developer service");
                        }
                    }

                } else if (message.contains("CONNECTED")) {
                    lin_connect_options.setVisibility(View.GONE);
                    txt_connected_device_info.setVisibility(View.VISIBLE);
                    txt_connected_device_info.setText("Connected to: " + message);
                }
                break;
            case "BT PROXIMITY STATE":
                Log.i(TAG, "#MVK-Dev-Sample# HomeActivity in Home Screen received: " + message);
                isConnected = true;
                DevServiceUtils.handleBtProximityState(this, message, handler);
                break;
            case "START DISPLAY SCAN":
                if (isConnected) {
                    DevServiceUtils.callAvailDisplay(this, handler);
                } else {
                    boolean success = DeveloperService.getWirelessDisplayCallback(HomeActivity.this, "ON");
                    if (!success) {
                        Log.e(TAG, "#MVK-Dev-Sample# HomeActivity failed to get display callback developer service");
                        Toast.makeText(HomeActivity.this, "Failed to get display callback developer service", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "AVAILABLE DISPLAYS":
                DevServiceUtils.handleAvailableDisplays(this, message, handler);
                break;
            case "CONNECTION STATE":
                Log.i(TAG, reqType + " - " + message);
                isConnected = false;
                if (Objects.equals(message, "Maverick CONNECTED")) {
                    Utils.saveProximityState(this, true);
                    boolean succes = DeveloperService.stopDisplayScan(HomeActivity.this);
                    if (!succes) {
                        Log.e(TAG, "Failed to stop display scan developer service");
                    }
                } else {
                    lin_connect_options.setVisibility(View.VISIBLE);
                    txt_connected_device_info.setVisibility(View.GONE);
                }
                Toast.makeText(this, reqType + " - " + message + " - " + resultCode, Toast.LENGTH_SHORT).show();
                break;
            case "ENABLE WIRELESS":
                DeveloperService.startDisplayScan(HomeActivity.this);
                break;
            case "STOP DISPLAY SCAN":
                if (isConnected) {
                    DeveloperService.enableWirelessDisplay(HomeActivity.this);
                } else {
                    boolean status = DeveloperService.getConnectionStatus(HomeActivity.this);
                    if (!status) {
                        Log.e(TAG, "Failed to stop display scan developer service");
                    }
                }
                break;
            case "CALLBACK OFF":
                if (message != null && !message.isEmpty()) {
                    Log.i(TAG, "#MVK-Dev-Sample# Display callback received: " + reqType + " - " + message);
                }
                break;
            case "CALLBACK ON":
                if (message != null && !message.isEmpty()) {
                    Log.i(TAG, "#MVK-Dev-Sample# Display callback received: " + reqType + " - " + message);
                }
                break;
            case "UPDATE UI":
                if (message != null && !message.isEmpty()) {
                    Log.i(TAG, "#MVK-Dev-Sample# update Displays received: " + message);
                }
                break;
            default:
                Log.w(TAG, "Unhandled request type: " + reqType);
                break;
        }
    }

    @Override
    public void dismissDialogCallBack() {

    }

}
