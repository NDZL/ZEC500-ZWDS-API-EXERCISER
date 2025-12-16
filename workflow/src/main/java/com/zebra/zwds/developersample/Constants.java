package com.zebra.zwds.developersample;

public class Constants {
    public static final String EXTRA_PROFILENAME = "WirelessConnect";
    public static final String ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION";
    public static final String DATAWEDGE_PACKAGE = "com.symbol.datawedge";
    public static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";
    public static final String ACTION_RESULT = "com.symbol.datawedge.api.RESULT_ACTION";
    public static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    public static final String EXTRA_RESULT = "RESULT";
    public static final String EXTRA_RESULT_INFO = "RESULT_INFO";
    public static final String EXTRA_COMMAND = "COMMAND";
    /*Tap to pair*/
    public static final String SELECT_APDU_HEADER = "00A40400";
    public static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    public static final byte[] SELECT_OK_SW = {(byte) 0x90, (byte) 0x00};
    /*HOME ACTIVITY*/
    public static final String WIRELESS_DEV_SERVICE_PACKAGE = "com.zebra.wirelessdeveloperservice";
    // Constants for action and extra keys to prevent hardcoded string vulnerabilities
    public static final String ACTION_DEV_SERVICE_RESPONSE = "com.zebra.wirelessdeveloperservice.action.DEV_SERVICE_RESPONSE";
    public static final String EXTRA_REQUEST_TYPE = "request_type";
    public static final String EXTRA_REQUEST_ID = "request_id";
    public static final String EXTRA_CALLBACK_RESPONSE = "CALLBACK_RESPONSE";
    public static final String ACTION_INIT_DEV_SERVICE = "com.zebra.wirelessdeveloperservice.action.INIT_DEV_SERVICE";
    public static final String ACTION_START_WIRELESS_DISPLAY_SCAN = "com.zebra.wirelessdeveloperservice.action.START_WIRELESS_DISPLAY_SCAN";

    public static final String ACTION_STOP_WIRELESS_DISPLAY_SCAN = "com.zebra.wirelessdeveloperservice.action.STOP_WIRELESS_DISPLAY_SCAN";
    public static final String ACTION_DEINIT_DEV_SERVICE = "com.zebra.wirelessdeveloperservice.action.DEINIT_DEV_SERVICE";
    public static final String EXTRA_STATE_CHANGE_RCV_ACTION = "STATE_CHANGE_RCV_ACTION";
    public static final String EXTRA_STATE_CHANGE_RCV_PKG = "STATE_CHANGE_RCV_PKG";
    public static final String STATE_CHANGE_ACTION = "com.zebra.zwds.developersample.WIRELESS_STATE_CHANGE";
    public static final String PACKAGE_NAME = "com.zebra.zwds.developersample";

    // Request type constants
    public static final String REQUEST_TYPE_INIT = "NIT_DEV_SERVICE";
    public static final String REQUEST_TYPE_START_SCAN = "START_WIRELESS_DISPLAY_SCAN";
    public static final String REQUEST_TYPE_STOP_SCAN = "STOP_WIRELESS_DISPLAY_SCAN";
    public static final String REQUEST_TYPE_DEINIT = "DEINIT_DEV_SERVICE";
    public static final String REQUEST_TYPE_PROXIMITY_CONNECT = "SET_PROXIMITY_CONNECT";

    // Request ID constants
    public static final int REQUEST_ID_INIT = 12345;
    public static final int REQUEST_ID_START_SCAN = 1010;
    public static final int REQUEST_ID_STOP_SCAN = 1020;
    public static final int REQUEST_ID_DEINIT = 1000;
    public static final int REQUEST_ID_CONNECT = 1001;
    public static final int REQUEST_ID_DISCONNECT = 1002;
    public static final int REQUEST_ID_PROXIMITY_CONNECT = 2001;

    /*SCAN CONNECT ACTIVITY*/
    public static final String ACTION_CONNECT_WIRELESS_DISPLAY = "com.zebra.wirelessdeveloperservice.action.CONNECT_WIRELESS_DISPLAY";
    public static final String ACTION_DISCONNECT_WIRELESS_DISPLAY = "com.zebra.wirelessdeveloperservice.action.DISCONNECT_WIRELESS_DISPLAY";
    public static final String EXTRA_DEVICE_ID = "DEVICE_ID";
    public static final String REQUEST_TYPE_CONNECT = "CONNECT_WIRELESS_DISPLAY";
    public static final String REQUEST_TYPE_DISCONNECT = "DISCONNECT_WIRELESS_DISPLAY";

    /*PROXIMITY SETTINGS*/
    public static final String ACTION_SET_PROXIMITY_CONNECT = "com.zebra.wirelessdeveloperservice.action.SET_PROXIMITY_CONNECTION";
    public static final String PROXIMITY_CONNECT = "PROXIMITY_CONNECT";
    public static final String PROXIMITY_DISCONNECT = "PROXIMITY_DISCONNECT";
}
