package com.zebra.zwds.developersample;

import android.content.Context;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class TapConnectActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback, NfcOffCallback, DevServiceResponseListener {
    private static final String TAG = "TapConnectActivity";
    NfcAdapter nfcAdapter;
    private boolean connectedFlag; //Flag to control the observer of device list
    private boolean isDialogueUp = false; //Flag to prevent creating multiple dialogues
    private boolean isHandlerUp = false; // Flag to prevent running multiple handlers until current one finishes
    private NfcReceiver nfcCheckReceiver;
    private String targetDockName;
    String availableDisplayDetails;
    ImageView logoIconViewMobile;
    TextView txt_welcome;
    TextView txt_instruction;
    String deviceAddress = null;
    private String targetDockAddress;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap);
        registerNfcState();
        initView();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // Register this activity to receive callbacks from the global listener
        MyGlobalDevServiceResponseListener.getInstance().registerListener(this);

        boolean success = DeveloperService.startDisplayScan(TapConnectActivity.this);
        if (!success) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to start display scan developer service");
            Toast.makeText(TapConnectActivity.this, "Failed to start display scan developer service", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        logoIconViewMobile = findViewById(R.id.logoIconViewMobile);
        txt_welcome = findViewById(R.id.welcomeTextView);
        txt_instruction = findViewById(R.id.instructionTextView);
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Unregister this activity to prevent memory leaks and duplicate callbacks
        MyGlobalDevServiceResponseListener.getInstance().unregisterListener(this);
        Log.d(TAG, "#MVK-Dev-Sample# TapConnectActivity unregistered from callbacks");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(this);
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        try {
            isoDep.connect();
            byte[] command = BuildSelectApdu(Constants.SAMPLE_LOYALTY_CARD_AID);
            byte[] response = isoDep.transceive(command);
            int resultLength = response.length;
            byte[] statusWord = {response[resultLength - 2], response[resultLength - 1]};
            byte[] payload = Arrays.copyOf(response, resultLength - 2);
            if (Arrays.equals(Constants.SELECT_OK_SW, statusWord)) {
                // The remote NFC device will immediately respond with its stored account number
                targetDockName = new String(payload, StandardCharsets.UTF_8);
            }
            runOnUiThread(() -> onTagRead(targetDockName));
        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            try {
                isoDep.close();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    private void onTagRead(String targetDockName) {
        if (!connectedFlag) {
            getAvailable(targetDockName);
        }
    }

    private void getAvailable(String targetDockName) {
        //callAvailableDisplays();
        if (!isDialogueUp) {
            Utils.showNewProgressDialog(this, targetDockName, this, 25000);
            txt_instruction.setVisibility(View.GONE);
            txt_welcome.setVisibility(View.GONE);
            logoIconViewMobile.setVisibility(View.GONE);
            isDialogueUp = true;
        }
        Log.d(TAG, "getAvailable method called");
        try {
            if (!isHandlerUp) {
                isHandlerUp = true;
                mHandler.postDelayed(() -> {
                    if (!connectedFlag) {
                        isDialogueUp = false;
                        isHandlerUp = false;
                    }
                }, 15000);
            }
            mHandler.postDelayed(() -> {
                if (availableDisplayDetails != null && !availableDisplayDetails.isEmpty()) {
                    deviceAddress = Utils.getDisplayAddress(targetDockName, availableDisplayDetails);
                    if (deviceAddress != null && !deviceAddress.equals("ERROR")) {
                        targetDockAddress = deviceAddress;
                        Utils.saveTargetDockName(TapConnectActivity.this, targetDockAddress);
                        try {
                            DeveloperService.connectDevice(TapConnectActivity.this, targetDockAddress);
                            connectedFlag = true;
                        } catch (Exception e) {
                            e.fillInStackTrace();
                            isDialogueUp = false;
                            connectedFlag = false;
                        }
                    } else {
                        Toast.makeText(TapConnectActivity.this, "Device not available to connect", Toast.LENGTH_SHORT).show();
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

    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(Constants.SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
    }

    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public void nfcState(boolean is) {

    }

    private void registerNfcState() {
        nfcCheckReceiver = new NfcReceiver(is -> {
            if (!is) {
                if (!isFinishing()) {
                    Log.d(TAG, "#MVK-Dev-Sample# nfcState: broadcast received");
                    nfcDisable();
                }
            }
        });
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        // Fix API level compatibility for RECEIVER_EXPORTED
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(nfcCheckReceiver, filter2, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(nfcCheckReceiver, filter2);
        }
    }

    private void nfcDisable() {
        Toast.makeText(this, "NFC is disabled. Closing Tap to Connect.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDevServiceResponseReceived(int reqID, int resultCode, String message, String reqType) {
        Log.d(TAG, "#MVK-Dev-Sample# broadcast received :  " + reqType);
        switch (reqType) {
            case "START DISPLAY SCAN":
                mHandler.postDelayed(this::callAvailableDisplays, 500); // Delay for 5 seconds
                break;
            case "CONNECT WIRELESS DISPLAY":
                if (resultCode == 0) { // Failed to initiate connection
                    Toast.makeText(this, "Connection initiated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Establishing the connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case "STOP DISPLAY SCAN":
                Log.d(TAG, "#MVK-Dev-Sample# Display scan stopped");
                break;
            case "CONNECTION STATE":
                if (Objects.equals(message, "Maverick CONNECTED")) {
                    // Connection successful - stop scanning immediately
                    dismissDialogCallBack();
                    logoIconViewMobile.setVisibility(View.GONE);
                    txt_instruction.setVisibility(View.GONE);
                    txt_welcome.setVisibility(View.VISIBLE);
                    txt_welcome.setText("Connected Successfully to " + targetDockName + "!");
                    DeveloperService.stopDisplayScan(this);
                } else {
                    // Show minimal feedback during connection
                    Log.d(TAG, "#MVK-Dev-Sample# Device connecting...");
                    logoIconViewMobile.setVisibility(View.VISIBLE);
                    txt_instruction.setVisibility(View.VISIBLE);
                    txt_welcome.setText("DisConnected " + targetDockName + "!");
                }
                break;
            case "AVAILABLE DISPLAYS":
                if (message != null && !message.isEmpty()) {
                    Log.i(TAG, "#MVK-Dev-Sample# Avail Displays received: " + message);
                    availableDisplayDetails = message;
                }
                break;
            case "UPDATE UI":
                if (message != null && !message.isEmpty()) {
                    Log.i(TAG, "#MVK-Dev-Sample# update Displays received: " + message);
                    availableDisplayDetails = message;
                }
                break;
        }
    }

    private void callAvailableDisplays() {
        boolean success = DeveloperService.getAvailableDevices(TapConnectActivity.this);
        if (!success) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to get available displays developer service");
            Toast.makeText(TapConnectActivity.this, "Failed to get available displays developer service", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void dismissDialogCallBack() {
        if (!isFinishing()) {
            Utils.dismissNewProgressDialog();
            txt_instruction.setVisibility(View.VISIBLE);
            txt_welcome.setVisibility(View.VISIBLE);
            logoIconViewMobile.setVisibility(View.VISIBLE);
            isDialogueUp = false;
        }
    }
}
