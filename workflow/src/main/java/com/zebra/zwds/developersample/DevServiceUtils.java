package com.zebra.zwds.developersample;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class DevServiceUtils {

    private static final String TAG = DevServiceUtils.class.getSimpleName();

    // Message Constants
    private static final String DEVICE_CONNECTED = "DEVICE CONNECTED";
    private static final String DEVICE_DISCONNECTED = "DEVICE DISCONNECTED";
    private static final String ERROR_RESPONSE = "ERROR";

    // Delay Constants
    private static final long DELAY_START_SCAN_MS = 1000;
    private static final long DELAY_DISCONNECT_MS = 500;
    private static final long DELAY_GET_DEVICES_MS = 500;
    private static final long DELAY_RETRY_SCAN_MS = 2000; // Delay for retry
    private static int retryCount = 0;
    private static final int MAX_RETRIES = 3;

    private DevServiceUtils() {
        // Prevent instantiation of this utility class
    }

    public static void handleBtProximityState(Context context, String message, Handler handler) {
        if (DEVICE_CONNECTED.equals(message)) {
            Log.i(TAG, "Device connected: " + message);
            handler.postDelayed(() -> {
                Log.i(TAG, "Starting display scan after connect.");
                if (!DeveloperService.stopDisplayScan(context)) {
                    Log.e(TAG, "Failed to start display scan.");
                }
            }, DELAY_START_SCAN_MS);
        } else if (DEVICE_DISCONNECTED.equals(message)) {
            handler.postDelayed(() -> {
                if (!DeveloperService.disconnectDevice(context)) {
                    Log.e(TAG, "Failed to disconnect device.");
                    Toast.makeText(context, "Failed to disconnect device", Toast.LENGTH_SHORT).show();
                }
            }, DELAY_DISCONNECT_MS);
        }
    }

    public static void callAvailDisplay(Context context, Handler handler) {
        Log.i(TAG, "Requesting available displays...");
        handler.postDelayed(() -> {
            if (!DeveloperService.getAvailableDevices(context)) {
                Log.e(TAG, "Failed to get available displays.");
                Toast.makeText(context, "Failed to get available displays", Toast.LENGTH_SHORT).show();
            }
        }, DELAY_GET_DEVICES_MS);
    }

    public static void handleAvailableDisplays(Context context, String message, Handler handler) {
        if (message != null && !message.isEmpty()) {
            String savedDockName = Utils.getSavedTargetDockName(context);
            String displayAddress = Utils.getDisplayAddress(savedDockName, message);

            if (!ERROR_RESPONSE.equals(displayAddress)) {
                retryCount = 0; // Reset on success
                if (!DeveloperService.connectDevice(context, savedDockName)) {
                    Log.e(TAG, "Failed to initiate connection attempt.");
                } else {
                    Log.i(TAG, "Connection initiation successful.");
                }
            } else {
                if (retryCount < MAX_RETRIES) {
                    retryCount++;
                    Log.i(TAG, "Device not found. Retrying... Attempt " + retryCount);
                    Toast.makeText(context, "Device not found. Retrying... (" + retryCount + "/" + MAX_RETRIES + ")", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(() -> {
                        if (!DeveloperService.getAvailableDevices(context)) {
                            Log.e(TAG, "Failed to retry display scan.");
                            Toast.makeText(context, "Retry failed", Toast.LENGTH_SHORT).show();
                        }
                    }, DELAY_RETRY_SCAN_MS);
                } else {
                    retryCount = 0; // Reset after max retries
                    Log.e(TAG, "Max retries reached. Could not find device.");
                    Toast.makeText(context, "Could not find device after " + MAX_RETRIES + " attempts.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


/*    public static void handleAvailableDisplays(Context context, String message, Handler handler) {
        if (message != null && !message.isEmpty()) {
            String savedDockName = Utils.getSavedTargetDockName(context);
            String displayAddress = Utils.getDisplayAddress(savedDockName, message);

            if (!ERROR_RESPONSE.equals(displayAddress)) {
                if (!DeveloperService.connectDevice(context, savedDockName)) {
                    Log.e(TAG, "Failed to initiate connection attempt.");
                } else {
                    Log.i(TAG, "Connection initiation successful.");
                }
            }else{
                Toast.makeText(context,"Can Connect & Available - False",Toast.LENGTH_SHORT).show();
                handler.postDelayed(() -> {
                    if (!DeveloperService.connectDevice(context)) {
                        Log.e(TAG, "Failed to retry display scan.");
                        Toast.makeText(context, "Retry failed", Toast.LENGTH_SHORT).show();
                    }
                }, DELAY_RETRY_SCAN_MS);
            }
        }
    }*/






    /*private static final String TAG = "#MVK-Dev-Sample#";

    public static void handleBtProximityState(Context context, String message, Handler handler) {
        if ("DEVICE CONNECTED".equals(message)) {
            Log.i(TAG, "Device connected: " + message);
            handler.postDelayed(() -> {
                Log.i(TAG, "Starting display scan after disconnect");
                boolean success = DeveloperService.startDisplayScan(context);
                if (!success) {
                    Log.e(TAG, "Failed to start display scan after disconnect");
                }
            }, 1000);
        } else if ("DEVICE DISCONNECTED".equals(message)) {
            handler.postDelayed(() -> {
                boolean success = DeveloperService.disconnectDevice(context);
                if (!success) {
                    Log.e(TAG, "Failed to disconnect device");
                    Toast.makeText(context, "Failed to disconnect device", Toast.LENGTH_SHORT).show();
                }
            }, 500);
        }
    }

    public static void callAvailDisplay(Context context, Handler handler) {
        Log.i(TAG, "Starting display scan...");
        handler.postDelayed(() -> {
            boolean success = DeveloperService.getAvailableDevices(context);
            if (!success) {
                Log.e(TAG, "#MVK-Dev-Sample# Failed to get available displays developer service");
                Toast.makeText(context, "Failed to get available displays developer service", Toast.LENGTH_SHORT).show();
            }
        }, 500);
    }

    public static void handleAvailableDisplays(Context context, String message, Handler handler) {
        if (message != null && !message.isEmpty()) {
            String availConnect = Utils.getDisplayAddress(Utils.getSavedTargetDockName(context), message);
            if (!"ERROR".equals(availConnect)) {
                boolean success = DeveloperService.connectDevice(context, Utils.getSavedTargetDockName(context));
                if (!success) {
                    Log.e(TAG, "Failed to initiate connection attempt");
                } else {
                    Log.i(TAG, "Connection initiation successful");
                }
            } *//*else {
                Log.i(TAG, "Device not available to connect");
                Toast.makeText(context, "Device not available to connect", Toast.LENGTH_SHORT).show();
                handler.postDelayed(() -> {
                    boolean success = DeveloperService.startDisplayScan(context);
                    if (!success) {
                        Log.e(TAG, "Retry failed to get available displays");
                        Toast.makeText(context, "Retry failed to get available displays", Toast.LENGTH_SHORT).show();
                    }
                }, 2000); // Retry after 2 seconds
            }*//*
        }
    }*/
}
