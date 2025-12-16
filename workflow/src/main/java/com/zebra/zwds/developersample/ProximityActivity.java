package com.zebra.zwds.developersample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class ProximityActivity extends AppCompatActivity implements DevServiceResponseListener {

    private static final String TAG = "ProximityActivity";
    private SwitchCompat connectSwitch;
    EditText edt_connect_range;
    EditText edt_disconnect_range;

    TextView txt_connection_status;
    String availableDisplayDetails;
    Button btn_save_Changes;

    private Toolbar toolbar;
    final Handler handler = new Handler(Looper.getMainLooper());
    int connectThreshold;
    int disconnectThreshold;
    String connectDisconnectState;
    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);
        initView();
        setOnClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyGlobalDevServiceResponseListener.getInstance().registerListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister this activity to prevent memory leaks and duplicate callbacks
        MyGlobalDevServiceResponseListener.getInstance().unregisterListener(this);
        Log.d(TAG, "HomeActivity unregistered from callbacks");
    }

    private void setOnClickListener() {
        // Set an OnCheckedChangeListener on the Connect Switch
        connectSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                connectDisconnectState = "ON";
            }else {
                connectDisconnectState = "OFF";
            }
        });

        btn_save_Changes.setOnClickListener(v -> {
            Utils.saveProximityState(ProximityActivity.this, connectSwitch.isChecked());

            String connectRange = edt_connect_range.getText().toString();
            String disconnectRange = edt_disconnect_range.getText().toString();

            connectThreshold = (!connectRange.trim().isEmpty())
                    ? Integer.parseInt(connectRange.trim())
                    : 4;
            disconnectThreshold = (!disconnectRange.trim().isEmpty())
                    ? Integer.parseInt(disconnectRange.trim())
                    : 8;
            // Update proximity settings via the DeveloperService
            boolean success = DeveloperService.setProximitySettings(
                    ProximityActivity.this,
                    getsavedState(),
                    getsavedState(),
                    connectThreshold,
                    disconnectThreshold
            );

            if (success) {
                // Log the switch state
                Log.i(TAG, "Connect switch: " + connectDisconnectState);
                // Save the state locally if the operation is successful
            } else {
                // Handle failure case with appropriate message
                String errorMessage = "Failed to set proximity settings to " + connectDisconnectState + " in developer service";
                Log.e(TAG, errorMessage);
                Toast.makeText(ProximityActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getsavedState() {
        connectDisconnectState = Utils.getSavedProximityState(this) ? "ON" : "OFF";
        return connectDisconnectState;
    }


    /*private void handleProximitySwitch(boolean isSwitchOn) {
        connectDisconnectState = isSwitchOn ? "ON" : "OFF";
        connectSwitchState = isSwitchOn;
        // Retrieve the input values from EditText

    }*/

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
        connectSwitch = findViewById(R.id.connect_switch);
        edt_connect_range = findViewById(R.id.edt_connect_range);
        edt_disconnect_range = findViewById(R.id.edt_disconnect_range);
        txt_connection_status = findViewById(R.id.txt_connection_status);
        btn_save_Changes = findViewById(R.id.btn_save_changes);
        boolean isProximityEnabled = Utils.getSavedProximityState(this);
        connectSwitch.setChecked(isProximityEnabled);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear global listener to prevent memory leaks
        //DevResponseReceiver.setGlobalListener(null);
    }

    @Override
    public void onDevServiceResponseReceived(int reqID, int resultCode, String message, String reqType) {
        switch (reqType) {
            case "PROXIMITY CONNECT":
                Log.i(TAG, reqType + " - " + message);
                if (resultCode == 0) {
                    Toast.makeText(this, "Proximity Connect Failed", Toast.LENGTH_SHORT).show();
                    Utils.saveProximityState(this, false);
                } else {
                    btn_save_Changes.setText("Saving Changes...");
                }
                break;
            case "BT PROXIMITY STATE":
                Log.i(TAG, "In Proximity received: " + message);
                DevServiceUtils.handleBtProximityState(this, message, handler);
                break;
            case "DISCONNECT WIRELESS DISPLAY":
                Log.i(TAG, "Disconnect response received: " + message);
                break;
            case "STOP DISPLAY SCAN":
                if (!isConnected) {
                    DeveloperService.enableWirelessDisplay(ProximityActivity.this);
                }
                break;
            case "ENABLE WIRELESS":
                DeveloperService.startDisplayScan(ProximityActivity.this);
                break;
            case "START DISPLAY SCAN":
                DevServiceUtils.callAvailDisplay(this, handler);
                break;
            case "AVAILABLE DISPLAYS":
                availableDisplayDetails = message;
                DevServiceUtils.handleAvailableDisplays(this, availableDisplayDetails, handler);
                break;
            case "UPDATE UI":
                if (message != null && !message.isEmpty()) {
                    Log.i(TAG, "#MVK-Dev-Sample# Update UI: " + message);
                    availableDisplayDetails = message;
                }
                break;

            case "CONNECT WIRELESS DISPLAY":
                Log.i(TAG, reqType + " - " + message);
                break;

            case "CONNECTION STATE":
                if (Objects.equals(message, "Maverick CONNECTED")) {
                    isConnected = true;
                    Log.i(TAG, reqType + " - " + message);
                    Utils.saveProximityState(this, true);
                    txt_connection_status.setText("Device Connected!!");
                    btn_save_Changes.setVisibility(View.GONE);
                    boolean success = DeveloperService.stopDisplayScan(ProximityActivity.this);
                    if (!success) {
                        Log.e(TAG, "Failed to stop display scan developer service");
                    }

                } else {
                    isConnected = false;
                    btn_save_Changes.setVisibility(View.VISIBLE);
                    txt_connection_status.setText("Device Disconnected!!");
                    Log.i(TAG, reqType + " - " + message);
                }
                break;
            default:
                Log.w(TAG, "Unknown request type: " + reqType);
                break;
        }
    }

    @Override
    public void dismissDialogCallBack() {

    }

}
