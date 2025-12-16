package com.zebra.zwds.developersample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class DesktopModeActivity extends AppCompatActivity implements DevServiceResponseListener {
    private static final String TAG = "#MVK-Dev-Sample#";
    Button btn_desktop_mode;
    Button btn_mirror_mode;
    boolean isModeCheck = false;

    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop_mode);
        initView();
        onClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register this activity to receive callbacks from the global listener
        MyGlobalDevServiceResponseListener.getInstance().registerListener(this);
        Log.d(TAG, "DesktopModeActivity registered for callbacks");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister this activity to prevent memory leaks and duplicate callbacks
        MyGlobalDevServiceResponseListener.getInstance().unregisterListener(this);
        Log.d(TAG, "DesktopModeActivity unregistered from callbacks");
    }

    private void onClickListener() {
        btn_desktop_mode.setOnClickListener(v -> {
            boolean success = DeveloperService.desktopModeCheck(DesktopModeActivity.this, "ON");
            if (!success) {
                Log.e(TAG, "Failed to desktop mode check to ON developer service");
                Toast.makeText(DesktopModeActivity.this, "Failed to start display scan developer service", Toast.LENGTH_SHORT).show();
            }else {
                isModeCheck = true;
            }
        });

        btn_mirror_mode.setOnClickListener(v -> {
            boolean success = DeveloperService.desktopModeCheck(DesktopModeActivity.this, "OFF");
            if (!success) {
                Log.e(TAG, "Failed to desktop mode check to OFF developer service");
                Toast.makeText(DesktopModeActivity.this, "Failed to start display scan developer service", Toast.LENGTH_SHORT).show();
            }else {
                isModeCheck = true;
            }
        });
    }

    private void initView() {
        btn_desktop_mode = findViewById(R.id.btn_desktop_mode);
        btn_mirror_mode = findViewById(R.id.btn_mirror_mode);
    }

    @Override
    public void onDevServiceResponseReceived(int reqID, int resultCode, String message, String reqType) {
        Log.d(TAG, "Received response - Type: " + reqType + ", Result: " + resultCode + ", Message: " + message);

        switch (reqType.toUpperCase()) {
            case "DESKTOP MODE":
                Log.i(TAG, "Desktop mode response received, initiating disconnect");
                handler.postDelayed(() -> {
                    boolean success = DeveloperService.disconnectDevice(DesktopModeActivity.this);
                    if (!success) {
                        Log.e(TAG, "Failed to disconnect device");
                        Toast.makeText(DesktopModeActivity.this, "Failed to disconnect device", Toast.LENGTH_SHORT).show();
                    }
                }, 500);
                break;

            case "DISCONNECT WIRELESS DISPLAY":
                Log.i(TAG, "Disconnect response received: " + message);
                break;

            case "START DISPLAY SCAN":
                Log.i(TAG, "Display scan started successfully");
                handler.postDelayed(this::callAvailableDisplays, 2000);
                break;

            case "CONNECTION STATE":
                if (Objects.equals(message, "Maverick CONNECTED")) {
                    Log.i(TAG, "Device successfully connected");
                } else if (message.contains("Maverick DISCONNECTED")) {
                    Log.d(TAG, "Device disconnected, preparing for reconnection");
                    if (isModeCheck) {
                        isModeCheck = false;
                        handler.postDelayed(() -> {
                            Log.i(TAG, "Starting display scan after disconnect");
                            boolean success = DeveloperService.startDisplayScan(this);
                            if (!success) {
                                Log.e(TAG, "Failed to start display scan after disconnect");
                            }
                        }, 3000);
                    }
                }
                break;

            case "CONNECT WIRELESS DISPLAY":
                if (resultCode == 0) {
                    Log.e(TAG, "Connection initiation failed");
                } else {
                    Log.i(TAG, "Connection establishment in progress");
                }
                break;

            case "STOP DISPLAY SCAN":
                Log.d(TAG, "Display scan stopped successfully");
                break;

            case "AVAILABLE DISPLAYS":
                if (message != null && !message.isEmpty()) {
                    Toast.makeText(this, "Available Devices", Toast.LENGTH_SHORT).show();
                    String availConnect = Utils.getDisplayAddress(Utils.getSavedTargetDockName(this), message);
                    if (!"ERROR".equals(availConnect)) {
                        handler.postDelayed(() -> {
                            boolean success = DeveloperService.connectDevice(this, Utils.getSavedTargetDockName(this));
                            if (!success) {
                                Log.e(TAG, "Failed to initiate connection attempt");
                            } else {
                                Log.i(TAG, "Connection initiation successful");
                            }
                        }, 3000);

                    } else {
                        Log.i(TAG, "Device not available to connect");
                        Toast.makeText(this, "Device not available to connect ", Toast.LENGTH_SHORT).show();
                        handler.postDelayed(() -> {
                            boolean success = DeveloperService.startDisplayScan(DesktopModeActivity.this);
                            if (!success) {
                                Log.e(TAG, "Retry failed to get available displays developer service");
                                Toast.makeText(DesktopModeActivity.this, "Retry failed to get available displays", Toast.LENGTH_SHORT).show();
                            }
                        }, 2000); // Retry after 2 seconds
                    }
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


    private void callAvailableDisplays() {
        boolean success = DeveloperService.getAvailableDevices(DesktopModeActivity.this);
        if (!success) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to get available displays developer service");
            Toast.makeText(DesktopModeActivity.this, "Failed to get available displays developer service", Toast.LENGTH_SHORT).show();
        }
    }

}
