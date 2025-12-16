package com.zebra.zwds.developersample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class DevResponseReceiver extends BroadcastReceiver {

    private static DevServiceResponseListener globalListener;

    public static void setGlobalListener(DevServiceResponseListener listener) {
        globalListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("#MVK-Dev-Sample#", "Intent received: ");
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // Handle the received intent here
        if (intent != null) {
            String action = intent.getAction();

            Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Received Intent with action: " + action +" - " +intent.getStringExtra("request_type") );
            if (action != null && action.equals("com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE")) {

                String requestType = intent.getStringExtra("request_type");
                // Process the response data as needed
                switch (requestType) {
                    case "NIT_DEV_SERVICE":
                    case "CONNECT_WIRELESS_DISPLAY":
                    case "DISCONNECT_WIRELESS_DISPLAY":
                    case "DEINIT_DEV_SERVICE":
                    case "START_WIRELESS_DISPLAY_SCAN":
                    case "STOP_WIRELESS_DISPLAY_SCAN":
                    case "SET_PROXIMITY_CONNECT":
                    case "SWITCH_DESKTOP_MODE":
                    case "ENABLE_WIRELESS_DISPLAY":
                    case "WIRELESS_DISPLAY_CALLBACK_ON":
                    case "WIRELESS_DISPLAY_CALLBACK_OFF":
                        // Handle the response from the Dev Service
                        // You can extract data from the intent if needed
                        processDevInitResponse(context, intent, requestType);
                        break;
                    case "GET_AVAILABLE_DISPLAYS":
                        processGetAvailableDisplaysResponse(context, intent, requestType);
                        break;
                    case "GET_STATUS":
                        processConnectionStatusResponse(context, intent,requestType);
                        break;
                    // Add more cases for other actions if needed
                    default:
                        // Handle unknown actions or do nothing
                }
            } else if (action != null && action.equals("com.zebra.wirelessdeveloperservice.action.BT_PROXIMITY_STATE_CHANGE")) {
                processBtStateChangeResponse(context, intent, "BT_PROXIMITY_STATE_CHANGE");
            } else if (action != null && action.equals("com.zebra.wirelessdeveloperservice.action.CONNECTION_STATE_CHANGE")) {
                processConnectionStateChangeResponse(context, intent, "CONNECTION_STATE_CHANGE");
            } else if (action != null && action.equals("com.zebra.wirelessdeveloperservice.action.DISPLAY_DETAILS_CHANGE")) {
                processDisplayDetailsChangeResponse(context, intent);
            }

        }
    }

    private void processConnectionStatusResponse(Context context, Intent intent,String requestType) {
        //get the id request_id
        int requestId = intent.getIntExtra("request_id", -1);
        //get the response status
        int resultCode = intent.getIntExtra("RESULT_CODE", -1);
        String resultMessage = intent.getStringExtra("RESULT_MESSAGE");
        String statusJson = intent.getStringExtra("STATUS");
        // Process the response based on requestId and responseStatus
        //log aall th outpu
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Request ID: " + requestId);
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Result Code: " + resultCode);
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Result Message: " + resultMessage);

        if (globalListener != null) {
            String responseType = getResponseType(requestType);
            globalListener.onDevServiceResponseReceived(requestId, resultCode, getStatusText(statusJson), responseType);
        } else {
            Toast.makeText(context, "DevServiceResponseListener is null", Toast.LENGTH_SHORT).show();
        }
    }


    private String getStatusText(String statusJson) {
        String statusText = "UNKNOWN";
        try {
            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(statusJson);
            // Navigate to the device_status object
            JSONObject deviceStatus = jsonObject.getJSONObject("device_status");
            // Extract device_address and connection_status
            String deviceAddress = deviceStatus.getString("device_address");
            int connectionStatus = deviceStatus.getInt("connection_status");

            if (connectionStatus == 1) {
                statusText = "Maverick " + deviceAddress + " is CONNECTED";
            } else if (connectionStatus == 0) {
                statusText = "Maverick " + deviceAddress + " is  DISCONNECTED";
            }

        } catch (Exception ex) {
            Log.e("DevResponseReceiver", "#MVK-Dev-Sample# Exception in getStatusText: " + ex.getMessage());
        }
        return statusText;

    }


    private void processDevInitResponse(Context context, Intent intent, String requestType) {
        //get the id request_id
        int requestId = intent.getIntExtra("request_id", -1);
        //get the response status
        int resultCode = intent.getIntExtra("RESULT_CODE", -1);
        String resultMessage = intent.getStringExtra("RESULT_MESSAGE");
        // Process the response based on requestId and responseStatus
        //log aall th outpu
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Request ID: " + requestId);
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Result Code: " + resultCode);
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Result Message: " + resultMessage);
        Log.i("DevResponseReceiver", "proximitysettings: " + requestType + " - " + resultMessage);
        if (globalListener != null) {
            String responseType = getResponseType(requestType);
            globalListener.onDevServiceResponseReceived(requestId, resultCode, resultMessage, responseType);
        } else {
            Toast.makeText(context, "DevServiceResponseListener is null", Toast.LENGTH_SHORT).show();
        }

    }

    private String getResponseType(String reqType) {
        switch (reqType) {
            case "NIT_DEV_SERVICE":
                return "INIT SERVICE";
            case "CONNECT_WIRELESS_DISPLAY":
                return "CONNECT WIRELESS DISPLAY";
            case "DISCONNECT_WIRELESS_DISPLAY":
                return "DISCONNECT WIRELESS DISPLAY";
            case "DEINIT_DEV_SERVICE":
                return "DEINIT SERVICE";
            case "START_WIRELESS_DISPLAY_SCAN":
                return "START DISPLAY SCAN";
            case "STOP_WIRELESS_DISPLAY_SCAN":
                return "STOP DISPLAY SCAN";
            case "CONNECTION_STATE_CHANGE":
                return "CONNECTION STATE";
            case "SET_PROXIMITY_CONNECT":
                return "PROXIMITY CONNECT";
            case "BT_PROXIMITY_STATE_CHANGE":
                return "BT PROXIMITY STATE";
            case "SWITCH_DESKTOP_MODE":
                return "DESKTOP MODE";
            case "GET_AVAILABLE_DISPLAYS":
                return "AVAILABLE DISPLAYS";
            case "GET_STATUS":
                return "GET STATUS";
            case "ENABLE_WIRELESS_DISPLAY":
                return "ENABLE WIRELESS";
            case "WIRELESS_DISPLAY_CALLBACK_ON":
                return "CALLBACK ON";
            case "WIRELESS_DISPLAY_CALLBACK_OFF":
                return "CALLBACK OFF";

            default:
                return reqType;
        }
    }

    private void processBtStateChangeResponse(Context context, Intent intent, String requestType) {

        Log.w("DevResponseReceiver", "#MVK-Dev-Sample# Received BT State Change Intent");
        String btStateChange = intent.getStringExtra("STATE_CHANGE");
        Log.w("DevResponseReceiver", "#MVK-Dev-Sample# BT State Change: " + btStateChange);
        String btstateText = getBTStateText(btStateChange);

        if (globalListener != null) {
            String responseType = getResponseType(requestType);
            globalListener.onDevServiceResponseReceived(0, 0, btstateText, responseType);
        } else {
            Toast.makeText(context, "DevServiceResponseListener is null", Toast.LENGTH_SHORT).show();
        }

    }

    private String getBTStateText(String btStateChange) {
        String stateText = "UNKNOWN";
        try {
            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(btStateChange);

            // Navigate to the desired key
            int connectionStatus = jsonObject.getJSONObject("proximity_monitor_status")
                    .getInt("connection_status");

            if (connectionStatus == 2) {
                stateText = "DEVICE CONNECTED";
            } else if (connectionStatus == 4) {
                stateText = "DEVICE DISCONNECTED";
            }

        } catch (Exception ex) {
            Log.e("DevResponseReceiver", "#MVK-Dev-Sample# Exception in getBTStateText: " + ex.getMessage());
        }
        return stateText;

    }


    private void processConnectionStateChangeResponse(Context context, Intent intent, String requestType) {

        Log.w("DevResponseReceiver", "#MVK-Dev-Sample# Received Connection Change Intent");
        String stateChange = intent.getStringExtra("STATE_CHANGE");
        String btstateText = getConnectionStateText(stateChange);
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Connection state  Change: " + btstateText +" - "+stateChange);
        if (globalListener != null) {
            String responseType = getResponseType(requestType);
            globalListener.onDevServiceResponseReceived(1, 0, btstateText, responseType);
        } else {
            Toast.makeText(context, "DevServiceResponseListener is null", Toast.LENGTH_SHORT).show();
        }

    }

    private String getConnectionStateText(String btStateChange) {
        String stateText = "UNKNOWN";
        try {
            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(btStateChange);
            // Navigate to the desired key
            int connectionStatus = jsonObject.getJSONObject("p2p_connection_status")
                    .getInt("connection_status");

            if (connectionStatus == 1) {
                stateText = "Maverick CONNECTED";
            } else if (connectionStatus == 0) {
                stateText = "Maverick DISCONNECTED";
            }

        } catch (Exception ex) {
            Log.e("DevResponseReceiver", "#MVK-Dev-Sample# Exception in getConnectionStateText: " + ex.getMessage());
        }
        return stateText;
    }

    private void processGetAvailableDisplaysResponse(Context context, Intent intent, String requestType) {
        //get the id request_id
        int requestId = intent.getIntExtra("request_id", -1);
        //get the response status
        int resultCode = intent.getIntExtra("RESULT_CODE", -1);
        String resultMessage = intent.getStringExtra("RESULT_MESSAGE");
        String displaysJson = intent.getStringExtra("AVAILABLE_DISPLAYS");
        // Process the response based on requestId and responseStatus
        //log aall th outpu
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Request ID: " + requestId);
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Result Code: " + resultCode);
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Result Message: " + resultMessage);
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Result dispalyJson: " + displaysJson);

        if (globalListener != null) {
            String responseType = getResponseType(requestType);
            globalListener.onDevServiceResponseReceived(requestId, resultCode, displaysJson, responseType);
        } else {
            Toast.makeText(context, "DevServiceResponseListener is null", Toast.LENGTH_SHORT).show();
        }

    }

    private void processDisplayDetailsChangeResponse(Context context, Intent intent) {
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Received Display Details Change Intent");
        String displayChange = intent.getStringExtra("STATE_CHANGE");
        Log.i("DevResponseReceiver", "#MVK-Dev-Sample# Display Details Change: " + displayChange);

        if (globalListener != null) {
            globalListener.onDevServiceResponseReceived(0, 0, displayChange, "UPDATE UI");
        } else {
            Toast.makeText(context, "DevServiceResponseListener is null", Toast.LENGTH_SHORT).show();
        }
    }


}