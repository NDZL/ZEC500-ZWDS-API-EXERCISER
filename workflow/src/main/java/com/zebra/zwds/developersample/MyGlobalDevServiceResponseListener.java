package com.zebra.zwds.developersample;

import android.util.Log;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Global listener that manages all DevServiceResponse callbacks
 * and distributes them to registered activities/listeners
 */
public class MyGlobalDevServiceResponseListener implements DevServiceResponseListener {
    private static final String TAG = "GlobalDevListener";
    private static final MyGlobalDevServiceResponseListener instance = new MyGlobalDevServiceResponseListener();
    private final Set<DevServiceResponseListener> listeners = new CopyOnWriteArraySet<>();

    // Track processed requests to prevent duplicates
    private String lastProcessedRequest = "";

    private MyGlobalDevServiceResponseListener() {}

    public static MyGlobalDevServiceResponseListener getInstance() {
        return instance;
    }

    /**
     * Register a listener to receive callbacks
     */
    public void registerListener(DevServiceResponseListener listener) {
        listeners.add(listener);
        Log.d(TAG, "#MVK-Dev-Sample# Registered listener: " + listener.getClass().getSimpleName() + ", Total: " + listeners.size());
    }

    /**
     * Unregister a listener
     */
    public void unregisterListener(DevServiceResponseListener listener) {
        listeners.remove(listener);
        Log.d(TAG, "#MVK-Dev-Sample# Unregistered listener: " + listener.getClass().getSimpleName() + ", Total: " + listeners.size());
    }

    /**
     * Clear all listeners
     */
    public void clearAllListeners() {
        listeners.clear();
        Log.d(TAG, "#MVK-Dev-Sample# Cleared all listeners");
    }

    @Override
    public void onDevServiceResponseReceived(int reqID, int resultCode, String message, String reqType) {
        // Create a unique key for this request to prevent duplicates
        String requestKey = reqType + "_" + reqID + "_" + resultCode + "_" + message;

        // Check if we already processed this exact request
        if (lastProcessedRequest.equals(requestKey) && !reqType.equals("START DISPLAY SCAN") && !reqType.equals("AVAILABLE DISPLAYS") && !reqType.equals("UPDATE UI")) {
            Log.d(TAG, "#MVK-Dev-Sample# Duplicate request ignored: " + reqType);
            return;
        }

        lastProcessedRequest = requestKey;
        Log.d(TAG, "#MVK-Dev-Sample# Processing callback: " + reqType + " for " + listeners.size() + " listeners");

        // Distribute callback to all registered listeners
        for (DevServiceResponseListener listener : listeners) {
            try {
                listener.onDevServiceResponseReceived(reqID, resultCode, message, reqType);
            } catch (Exception e) {
                Log.e(TAG, "#MVK-Dev-Sample# Error in listener callback: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void dismissDialogCallBack() {

    }
}


