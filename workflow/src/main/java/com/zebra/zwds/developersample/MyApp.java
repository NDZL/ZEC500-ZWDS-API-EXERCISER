package com.zebra.zwds.developersample;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Application class to initialize global components
 */
public class MyApp extends Application {

    private static final String TAG = "MyApp";

    private final Handler handler = new Handler(Looper.getMainLooper());

    boolean canCall = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate - Setting up global listener");
        // Set the global listener once for the entire application
        DevResponseReceiver.setGlobalListener(MyGlobalDevServiceResponseListener.getInstance());

      /*  // Example usage of BT PROXIMITY STATE handling
        MyGlobalDevServiceResponseListener.getInstance().registerListener(new DevServiceResponseListener() {
            @Override
            public void onDevServiceResponseReceived(int reqID, int resultCode, String message, String reqType) {
                if ("BT PROXIMITY STATE".equals(reqType)) {
                    DevServiceUtils.handleBtProximityState(MyApp.this, message, handler);
                    canCall = true;
                }
                if (canCall) {
                    if ("START DISPLAY SCAN".equals(reqType)) {
                        DevServiceUtils.callAvailDisplay(MyApp.this, handler);
                    } else if ("AVAILABLE DISPLAYS".equals(reqType)) {
                        DevServiceUtils.handleAvailableDisplays(MyApp.this, message, handler);
                    }
                }
            }
            @Override
            public void dismissDialogCallBack() {

            }
        });*/
    }
}
