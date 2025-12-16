package com.zebra.zwds.developersample;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Developer Service utility class for managing wireless developer service operations.
 * This class provides static methods to interact with the wireless developer service.
 */
public class DeveloperService {

    private static final String TAG = "DeveloperService";
    static String secureTokenKey = "SECURE_TOKEN";
    static String secureToken = "";

    // Private constructor to prevent instantiation
    private DeveloperService() {
        // Utility class - no instantiation needed
    }

    public static boolean isSecureEnabled() {
        return SecureIntentActivity.isSecureModeEnabled();
    }

    /**
     * Initialize the developer service with the provided context.
     *
     * @param context The application context to use for service operations
     * @return true if initialization was successful, false otherwise
     */
    public static boolean initializeService(Context context) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Log.d(TAG, "#MVK-Dev-Sample# Developer service initialization Start - " + isSecureEnabled());
            Intent intent = new Intent(Constants.ACTION_INIT_DEV_SERVICE);
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            // Create result intent for callback
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction(Constants.ACTION_DEV_SERVICE_RESPONSE);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_TYPE, Constants.REQUEST_TYPE_INIT);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, Constants.REQUEST_ID_INIT);

            intent.putExtra(Constants.EXTRA_CALLBACK_RESPONSE, PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            // Set the receiver action and pkg name to get broadcast from the service
            intent.putExtra(Constants.EXTRA_STATE_CHANGE_RCV_ACTION, Constants.STATE_CHANGE_ACTION);
            intent.putExtra(Constants.EXTRA_STATE_CHANGE_RCV_PKG, Constants.PACKAGE_NAME);
            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            // Start the service
            context.startService(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service initialization initiated");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to initialize developer service: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Start wireless display scanning to discover available devices.
     *
     * @param context The application context to use for service operations
     * @return true if display scan was initiated successfully, false otherwise
     */
    public static boolean startDisplayScan(Context context) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Intent intent = new Intent(Constants.ACTION_START_WIRELESS_DISPLAY_SCAN);
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            // Set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction(Constants.ACTION_DEV_SERVICE_RESPONSE);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_TYPE, Constants.REQUEST_TYPE_START_SCAN);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, Constants.REQUEST_ID_START_SCAN);
            intent.putExtra(Constants.EXTRA_CALLBACK_RESPONSE, PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            // Start the service
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service start display scan initiated");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to start display scan developer service: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Connect to a wireless display device using the specified device ID.
     *
     * @param context  The application context to use for service operations
     * @param deviceId The unique identifier of the device to connect to
     * @return true if device connection was initiated successfully, false otherwise
     */
    public static boolean connectDevice(Context context, String deviceId) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Log.i(TAG, "#MVK-Dev-Sample# connectDevice initiated for deviceId: " + deviceId);
            Intent intent = new Intent(Constants.ACTION_CONNECT_WIRELESS_DISPLAY);
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);
            //set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction(Constants.ACTION_DEV_SERVICE_RESPONSE);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_TYPE, Constants.REQUEST_TYPE_CONNECT);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, Constants.REQUEST_ID_CONNECT);

            intent.putExtra(Constants.EXTRA_CALLBACK_RESPONSE, PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            intent.putExtra(Constants.EXTRA_DEVICE_ID, deviceId);
            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            //set maverick device address
            // Start the service
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service Connect Device initiated");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to Connect Device service: " + e.getMessage(), e);
            return false;
        }

    }

    /**
     * Stop wireless display scanning to end device discovery.
     *
     * @param context The application context to use for service operations
     * @return true if display scan was stopped successfully, false otherwise
     */
    public static boolean stopDisplayScan(Context context) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }

        try {
            Intent intent = new Intent(Constants.ACTION_STOP_WIRELESS_DISPLAY_SCAN);
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction(Constants.ACTION_DEV_SERVICE_RESPONSE);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_TYPE, Constants.REQUEST_TYPE_STOP_SCAN);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, Constants.REQUEST_ID_STOP_SCAN);
            intent.putExtra(Constants.EXTRA_CALLBACK_RESPONSE, PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service stop display scan initiated");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to stop display scan service: " + e.getMessage(), e);
            return false;
        }
    }


    /**
     * Disconnect from wireless display device.
     *
     * @param context The application context
     * @return true if disconnect was initiated successfully, false otherwise
     */
    public static boolean disconnectDevice(Context context) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }

        try {
            Intent intent = new Intent(Constants.ACTION_DISCONNECT_WIRELESS_DISPLAY);
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            // Set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction(Constants.ACTION_DEV_SERVICE_RESPONSE);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_TYPE, Constants.REQUEST_TYPE_DISCONNECT);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, Constants.REQUEST_ID_DISCONNECT);

            intent.putExtra(Constants.EXTRA_CALLBACK_RESPONSE, PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Device disconnect initiated");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to disconnect device: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Configure proximity-based connection and disconnection settings.
     *
     * @param context         The application context to use for service operations
     * @param connectState    The state for proximity-based connection ("ON" or "OFF")
     * @param disconnectState The state for proximity-based disconnection ("ON" or "OFF")
     * @return true if proximity settings were configured successfully, false otherwise
     */
    public static boolean setProximitySettings(Context context, String connectState, String disconnectState, int connectThreshold, int disconnectThreshold) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Log.i(TAG, "#MVK-Dev-Sample# setProximitySettings: " + connectState + " - " + disconnectState);
            Intent intent = new Intent(Constants.ACTION_SET_PROXIMITY_CONNECT);
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction(Constants.ACTION_DEV_SERVICE_RESPONSE);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_TYPE, Constants.REQUEST_TYPE_PROXIMITY_CONNECT);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, Constants.REQUEST_ID_PROXIMITY_CONNECT);

            intent.putExtra(Constants.EXTRA_CALLBACK_RESPONSE, PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);
            // Set proximity settings
            intent.putExtra(Constants.PROXIMITY_CONNECT, connectState);
            intent.putExtra(Constants.PROXIMITY_DISCONNECT, disconnectState);
            intent.putExtra("CONNECT_THRESHOLD", connectThreshold);
            intent.putExtra("DISCONNECT_THRESHOLD", disconnectThreshold);
            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Proximity Settings initiated");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to proximity settings device: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Deinitialize the developer service.
     *
     * @param context The application context
     * @return true if deinitialization was initiated successfully, false otherwise
     */
    public static boolean deinitializeService(Context context) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }

        try {
            Intent intent = new Intent(Constants.ACTION_DEINIT_DEV_SERVICE);
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            // Set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction(Constants.ACTION_DEV_SERVICE_RESPONSE);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_TYPE, Constants.REQUEST_TYPE_DEINIT);
            resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, Constants.REQUEST_ID_DEINIT);

            intent.putExtra(Constants.EXTRA_CALLBACK_RESPONSE, PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));
            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service deinitialization initiated");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to deinitialize developer service: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Switch or check the desktop mode state for the wireless display.
     *
     * @param context     The application context to use for service operations
     * @param desktopMode The desired desktop mode state to set or check
     * @return true if desktop mode operation was initiated successfully, false otherwise
     */
    public static boolean desktopModeCheck(Context context, String desktopMode) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.SWITCH_DESKTOP_MODE");
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "SWITCH_DESKTOP_MODE");
            resultIntent.putExtra("request_id", 4001);//
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            //set the package name to the wireless developer service package
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            //set proximity connect to true
            intent.putExtra("DESKTOP_MODE", desktopMode);

            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service desktop mode check initiated");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to desktop mode check developer service: " + e.getMessage(), e);
            return false;
        }

    }

    public static boolean getAvailableDevices(Context context) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.GET_AVAILABLE_DISPLAYS");
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "GET_AVAILABLE_DISPLAYS");
            resultIntent.putExtra("request_id", 5000);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service get available devices initiated");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to get available devices developer service: " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean getConnectionStatus(Context context) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.GET_STATUS");
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "GET_STATUS");
            resultIntent.putExtra("request_id", 3000);
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service get available devices initiated");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to get available devices developer service: " + e.getMessage(), e);
            return false;
        }
    }


    public static boolean getWirelessDisplayCallback(Context context, String callbackState) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.WIRELESS_DISPLAY_CALLBACK");
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "WIRELESS_DISPLAY_CALLBACK_ON");
            resultIntent.putExtra("request_id", 7001);//
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            //set the package name to the wireless developer service package
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);
            //set proximity connect to true
            intent.putExtra("REGISTER_CALLBACK", callbackState);

            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service get Wireless Display Callback initiated");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to get get Wireless Display Callback developer service: " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean enableWirelessDisplay(Context context) {
        if (context == null) {
            Log.e(TAG, "#MVK-Dev-Sample# Context cannot be null");
            return false;
        }
        try {
            Intent intent = new Intent("com.zebra.wirelessdeveloperservice.action.ENABLE_WIRELESS_DISPLAY");
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);

            //set pending intent for response
            Intent resultIntent = new Intent(context, DevResponseReceiver.class);
            resultIntent.setAction("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE");
            resultIntent.putExtra("request_type", "ENABLE_WIRELESS_DISPLAY");
            resultIntent.putExtra("request_id", 6000);//
            intent.putExtra("CALLBACK_RESPONSE", PendingIntent.getBroadcast(context, 0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE));

            //set the package name to the wireless developer service package
            intent.setPackage(Constants.WIRELESS_DEV_SERVICE_PACKAGE);
            //set proximity connect to true
            intent.putExtra("ENABLE_DISPLAY", "ON");

            if (isSecureEnabled()) {
                intent.putExtra(secureTokenKey, Utils.getSecuredToken());
            }
            context.sendBroadcast(intent);
            Log.d(TAG, "#MVK-Dev-Sample# Developer service enableWirelessDisplay initiated");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "#MVK-Dev-Sample# Failed to enableWirelessDisplay developer service: " + e.getMessage(), e);
            return false;
        }
    }

}
