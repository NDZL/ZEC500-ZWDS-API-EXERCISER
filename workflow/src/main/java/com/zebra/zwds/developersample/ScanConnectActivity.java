package com.zebra.zwds.developersample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.Set;

public class ScanConnectActivity extends AppCompatActivity implements DevServiceResponseListener {
    private static final String TAG = "#MVK-Dev-Sample#";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private String targetDockName;
    private ImageView logoIconViewMobile;
    private TextView txtWelcome;
    private TextView txtInstruction;
    private String availableDisplayDetails;

    private boolean connectedFlag; //Flag to control the observer of device list
    private boolean isDialogueUp = false; //Flag to prevent creating multiple dialogues
    private boolean isHandlerUp = false; // Flag to prevent running multiple handlers until current one finishes

    String deviceAddress = null;
    private String targetDockAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        registerDWReceivers();
        createProfile();
        initView();

        boolean success = DeveloperService.startDisplayScan(ScanConnectActivity.this);
        if (!success) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to start display scan developer service");
            Toast.makeText(ScanConnectActivity.this, "Failed to start display scan developer service", Toast.LENGTH_SHORT).show();
        }

    }

    private void initView() {
        logoIconViewMobile = findViewById(R.id.logoIconViewMobile);
        txtWelcome = findViewById(R.id.welcomeTextView);
        txtInstruction = findViewById(R.id.instructionTextView);
    }

    private final BroadcastReceiver dwBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
                    targetDockName = intent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
                    runOnUiThread(() -> onScanRead(targetDockName));
                } catch (Exception e) {
                    Log.e(TAG, "Error in scanning data: " + e.getMessage(), e);
                    Toast.makeText(ScanConnectActivity.this, R.string.error_in_scanning_data, Toast.LENGTH_SHORT).show();
                }
            } else if (action.equals(Constants.ACTION_RESULT)) {
                // Register to receive the result code
                if ((intent.hasExtra(Constants.EXTRA_RESULT)) && (intent.hasExtra(Constants.EXTRA_COMMAND))) {
                    String command = intent.getStringExtra(Constants.EXTRA_COMMAND);
                    String result = intent.getStringExtra(Constants.EXTRA_RESULT);
                    String info = "";

                    if (intent.hasExtra(Constants.EXTRA_RESULT_INFO)) {
                        Bundle result_info = intent.getBundleExtra(Constants.EXTRA_RESULT_INFO);
                        assert result_info != null;
                        Set<String> keys = result_info.keySet();
                        for (String key : keys) {
                            Object object = result_info.get(key);
                            if (object instanceof String) {
                                info += key + ": " + object + "\n";
                            } else if (object instanceof String[]) {
                                String[] codes = (String[]) object;
                                for (String code : codes) {
                                    info += key + ": " + code + "\n";
                                }
                            }
                        }
                        Toast.makeText(ScanConnectActivity.this, "Error. Command:" + command + "\nResult: " + result + "\nResult Info: " + info, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    private void createProfile() {
        // Configure created profile to apply to this app
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", Constants.EXTRA_PROFILENAME);
        profileConfig.putString("PROFILE_ENABLED", "true");
        profileConfig.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");  // Create profile if it does not exist

        // Associate profile with this app
        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", getPackageName());
        appConfig.putStringArray("ACTIVITY_LIST", new String[]{"*"});
        profileConfig.putParcelableArray("APP_LIST", new Bundle[]{appConfig});
        profileConfig.remove("PLUGIN_CONFIG");

        // Configure intent output for captured data to be sent to this app
        Bundle intentConfig = new Bundle();
        intentConfig.putString("PLUGIN_NAME", "INTENT");
        intentConfig.putString("RESET_CONFIG", "true");
        Bundle intentProps = new Bundle();
        intentProps.putString("intent_output_enabled", "true");
        intentProps.putString("intent_action", "com.zebra.WirelessLumos.SCAN_ACTION");
        intentProps.putString("intent_delivery", "2");
        intentConfig.putBundle("PARAM_LIST", intentProps);
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig);

        // Send DataWedge intent with extra to create profile
        Intent iSetConfig = new Intent();
        iSetConfig.setAction(Constants.ACTION_DATAWEDGE);
        iSetConfig.setPackage(Constants.DATAWEDGE_PACKAGE);
        iSetConfig.putExtra(Constants.EXTRA_SET_CONFIG, profileConfig);
        iSetConfig.putExtra(Constants.EXTRA_RESULT, "true");
        iSetConfig.putExtra(Constants.EXTRA_COMMAND, Constants.EXTRA_CREATE_PROFILE);
        this.sendBroadcast(iSetConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyGlobalDevServiceResponseListener.getInstance().registerListener(this);
        Log.d(TAG, "TapConnectActivity registered for callbacks");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister this activity to prevent memory leaks and duplicate callbacks
        MyGlobalDevServiceResponseListener.getInstance().unregisterListener(this);
        Log.d(TAG, "ScanConnectActivity unregistered from callbacks in onStop");
    }

    // Create filter for the broadcast intent
    private void registerDWReceivers() {
        // register to received broadcasts via DataWedge scanning
        IntentFilter profileFilter = new IntentFilter();
        profileFilter.addAction(Constants.ACTION_RESULT);   // for error code result
        profileFilter.addCategory(Intent.CATEGORY_DEFAULT);    // needed to get version info
        // Use RECEIVER_NOT_EXPORTED for security (DataWedge is internal)
        registerReceiver(dwBroadcastReceiver, profileFilter, Context.RECEIVER_EXPORTED);
        // register to received broadcasts via DataWedge scanning
        IntentFilter scanIntentFilter = new IntentFilter();
        scanIntentFilter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        scanIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Use RECEIVER_NOT_EXPORTED for security (internal app communication)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dwBroadcastReceiver, scanIntentFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(dwBroadcastReceiver, scanIntentFilter);
        }
    }

    @Override
    public void onDevServiceResponseReceived(int reqID, int resultCode, String message, String reqType) {
        Log.d(TAG, "onDevServiceResponseReceived: " + reqType + " - " + message + " - " + resultCode);
        switch (reqType) {
            case "START DISPLAY SCAN":
                mHandler.postDelayed(this::callAvailableDisplays, 500); // Delay for 5 seconds
                break;

            case "CONNECT WIRELESS DISPLAY":
                if (resultCode == 0) { // Failed to initiate connection
                    Log.i(TAG, "Connection initiated failed");
                    Toast.makeText(this, "Connection initiated", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Connection initiated Establishing");
                    Toast.makeText(this, "Establishing the connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case "STOP DISPLAY SCAN":
                Log.d(TAG, "#MVK-Dev-Sample# Display scan stopped");
                break;

            case "CONNECTION STATE":
                if (Objects.equals(message, "Maverick CONNECTED")) {
                    Log.i(TAG, "Connection Established");
                    // Connection successful - stop scanning immediately
                    dismissDialogCallBack();
                    Toast.makeText(this, "Connected successfully!", Toast.LENGTH_SHORT).show();
                    logoIconViewMobile.setVisibility(View.GONE);
                    txtInstruction.setVisibility(View.GONE);
                    txtWelcome.setText("Connected Successfully !");
                    DeveloperService.stopDisplayScan(this);

                    // Optional: Close activity or navigate to next screen
                    // finish();
                } else if (message.contains("Maverick DISCONNECTED")) {
                    // Show minimal feedback during connection
                    Log.d(TAG, "Device connecting...");
                    logoIconViewMobile.setVisibility(View.VISIBLE);
                    txtInstruction.setVisibility(View.VISIBLE);
                    txtWelcome.setText("DisConnected " + targetDockName + "!");
                }
                break;

            case "AVAILABLE DISPLAYS":
                // Cache the display details for faster lookups
                if (message != null && !message.isEmpty()) {
                    availableDisplayDetails = message;
                    Log.d(TAG, "Cached available displays for faster connections");
                }
                break;

            case "UPDATE UI":
                if (message != null && !message.isEmpty()) {
                    Log.i(TAG, "#MVK-Dev-Sample# Update Displays received: " + message);
                    availableDisplayDetails = message;
                }
                break;
            default:
                Log.w(TAG, "Unhandled response type: " + reqType);
                break;
        }
    }

    private void callAvailableDisplays() {
        boolean success = DeveloperService.getAvailableDevices(this);
        if (!success) {
            Log.e(TAG, "Failed to get available displays developer service");
            Toast.makeText(this, "Failed to get available displays developer service", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver
        try {
            unregisterReceiver(dwBroadcastReceiver);
        } catch (Exception e) {
            Log.w(TAG, "Error unregistering receiver: " + e.getMessage());
        }

        // Clear the global listener to prevent memory leaks and duplicate callbacks
        MyGlobalDevServiceResponseListener.getInstance().unregisterListener(this);
        Log.d(TAG, "Global listener cleared for ScanConnectActivity");
    }

    private void onScanRead(String targetDockName) {
        if (!connectedFlag) {
            getAvailable(targetDockName);
        }
    }

    private void getAvailable(String targetDockName) {
        if (!isDialogueUp) {
            Utils.showNewProgressDialog(this, targetDockName, ScanConnectActivity.this, 25000);
            isDialogueUp = true;
        }
        Log.d(TAG, "getAvailable method called");
        try {
            if (!isHandlerUp) {
                isHandlerUp = true;
                mHandler.postDelayed(() -> {
                    if (!connectedFlag) {
                        isDialogueUp = false;
                        showRetryDialog();
                        isHandlerUp = false;
                    }
                }, 15000);
            }

           // callAvailableDisplays();

            mHandler.postDelayed(() -> {
                if (availableDisplayDetails != null && !availableDisplayDetails.isEmpty()) {
                    deviceAddress = Utils.getDisplayAddress(targetDockName, availableDisplayDetails);
                    if (deviceAddress != null && !deviceAddress.equals("ERROR")) {
                        targetDockAddress = deviceAddress;
                        if (targetDockAddress == null) {
                            targetDockAddress = Utils.getSavedTargetDockName(ScanConnectActivity.this);
                            Log.d(TAG, "#MVK-Dev-Sample# Found device address from SharedPreferences: " + deviceAddress);
                        }
                        Utils.saveTargetDockName(ScanConnectActivity.this, targetDockAddress);
                        try {
                            DeveloperService.connectDevice(ScanConnectActivity.this, targetDockAddress);
                            connectedFlag = true;
                        } catch (Exception e) {
                            e.fillInStackTrace();
                            isDialogueUp = false;
                            connectedFlag = false;
                        }
                    } else {
                        connectedFlag = false;
                    }
                } else {
                    connectedFlag = false;
                    Log.d(TAG, "#MVK-Dev-Sample# Not Found device address");
                }
            }, 1000);


        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void dismissDialogCallBack() {
        if (!isFinishing()) {
            Utils.dismissNewProgressDialog();
            isDialogueUp = false;
            if (!connectedFlag) {
                showRetryDialog();
            }
        }
    }

    private void showRetryDialog() {
        if (isDialogueUp) {
            Utils.dismissNewProgressDialog();
            isDialogueUp = false;
        }
    }

}

