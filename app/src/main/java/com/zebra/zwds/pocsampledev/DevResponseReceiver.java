package com.zebra.zwds.pocsampledev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

public class DevResponseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // Handle the received intent here
        if (intent != null) {
            String action = intent.getAction();
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
                        // Handle the response from the Dev Service
                        // You can extract data from the intent if needed
                        processDevInitResponse(context , intent);
                        break;
                    case "GET_STATUS":
                        processStatusResponse(context, intent);
                        break;
                    case "GET_AVAILABLE_DISPLAYS":
                        processGetAvailableDisplaysResponse(context, intent);
                        break;
                    // Add more cases for other actions if needed
                    default:
                        // Handle unknown actions or do nothing
                }
            }else if (action != null && action.equals("com.zebra.wirelessdeveloperservice.action.BT_PROXIMITY_STATE_CHANGE")) {
                processBtStateChangeResponse(context, intent);
            }else if(action != null && action.equals("com.zebra.wirelessdeveloperservice.action.CONNECTION_STATE_CHANGE")){
                processConnectionStateChangeResponse(context, intent);
            }
        }
    }

    private void processGetAvailableDisplaysResponse(Context context, Intent intent) {
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

        String resultText = "Request ID: " + requestId +
                "\nResult Code: " + resultCode +
                "\nResult Message: " + resultMessage+
                "\nAvailable Displays: " + displaysJson;

        // Send the data to the Activity
        Intent updateIntent = new Intent("com.zebra.pocsampledev.UPDATE_UI");
        updateIntent.putExtra("result_text", resultText);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
    }

    private void processDevInitResponse(Context context, Intent intent) {
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

        String resultText = "Request ID: " + requestId +
                "\nResult Code: " + resultCode +
                "\nResult Message: " + resultMessage;

        // Send the data to the Activity
        Intent updateIntent = new Intent("com.zebra.pocsampledev.UPDATE_UI");
        updateIntent.putExtra("result_text", resultText);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);

    }

    private void processStatusResponse(Context context, Intent intent) {
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

        String resultText = "Request ID: " + requestId +
                "\nResult Code: " + resultCode +
                "\nResult Message: " + resultMessage+
                "\nResult Message: " + statusJson+
                "\n" + getStatusText(statusJson);

        // Send the data to the Activity
        Intent updateIntent = new Intent("com.zebra.pocsampledev.UPDATE_UI");
        updateIntent.putExtra("result_text", resultText);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);

    }

    private String getStatusText(String statusJson){
        String statusText = "UNKNOWN";
        try {
            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(statusJson);
            // Navigate to the device_status object
            JSONObject deviceStatus = jsonObject.getJSONObject("device_status");
            // Extract device_address and connection_status
            String deviceAddress = deviceStatus.getString("device_address");
            int connectionStatus = deviceStatus.getInt("connection_status");

            if(connectionStatus == 1){
                statusText = "Maverick " + deviceAddress + " is CONNECTED";
            }else if(connectionStatus == 0){
                statusText = "Maverick " + deviceAddress + " is  DISCONNECTED";
            }

        }catch ( Exception ex){
            Log.e("DevResponseReceiver", "#MVK-Dev-Sample# Exception in getStatusText: " + ex.getMessage());
        }
        return  statusText;

    }


    private void processBtStateChangeResponse(Context context, Intent intent) {

        Log.w("DevResponseReceiver", "#MVK-Dev-Sample# Received BT State Change Intent");
        String Change = intent.getStringExtra("STATE_TYPE");
        String btStateChange = intent.getStringExtra("STATE_CHANGE");
        Log.w("DevResponseReceiver", "#MVK-Dev-Sample# BT State Change: " + btStateChange);
        String resultText = Change + ": " + btStateChange;
        String btstateText = getBTStateText(btStateChange);
        resultText += "\nBT Status: " + btstateText;

        // Send the data to the Activity
        Intent updateIntent = new Intent("com.zebra.pocsampledev.UPDATE_UI");
        updateIntent.putExtra("result_text", resultText);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);

    }

    private  String getBTStateText(String btStateChange){
        String stateText = "UNKNOWN";
        try {
            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(btStateChange);

            // Navigate to the desired key
            int connectionStatus = jsonObject.getJSONObject("proximity_monitor_status")
                    .getInt("connection_status");

            if(connectionStatus == 2){
                stateText = "DEVICE IN  CONNECTED PROXIMITY";
            }else if(connectionStatus == 4){
                stateText = "DEVICE IN DISCONNECTED PROXIMITY";
            }

        }catch ( Exception ex){
            Log.e("DevResponseReceiver", "#MVK-Dev-Sample# Exception in getBTStateText: " + ex.getMessage());
        }
        return  stateText;

    }


    private void processConnectionStateChangeResponse(Context context, Intent intent) {

        Log.w("DevResponseReceiver", "#MVK-Dev-Sample# Received Connection Change Intent");
        String Change = intent.getStringExtra("STATE_TYPE");
        String stateChange = intent.getStringExtra("STATE_CHANGE");
        Log.w("DevResponseReceiver", "#MVK-Dev-Sample# Connection state  Change: " + stateChange);
        String resultText = Change + ": " + stateChange;
        String btstateText = getConnectionStateText(stateChange);
        resultText += "\nConnection Status: " + btstateText;

        // Send the data to the Activity
        Intent updateIntent = new Intent("com.zebra.pocsampledev.UPDATE_UI");
        updateIntent.putExtra("result_text", resultText);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);

    }

    private  String getConnectionStateText(String btStateChange){
        String stateText = "UNKNOWN";
        try {
            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(btStateChange);
            // Navigate to the desired key
            int connectionStatus = jsonObject.getJSONObject("p2p_connection_status")
                    .getInt("connection_status");

            if(connectionStatus == 1){
                stateText = "Maverick CONNECTED";
            }else if(connectionStatus == 0){
                stateText = "Maverick DISCONNECTED";
            }

        }catch ( Exception ex){
            Log.e("DevResponseReceiver", "#MVK-Dev-Sample# Exception in getConnectionStateText: " + ex.getMessage());
        }
        return  stateText;
    }


}