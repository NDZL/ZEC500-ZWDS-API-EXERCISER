package com.zebra.zwds.developersample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
    private static final String TAG = "ZebraWirelessService.Utils";

    // SharedPreferences constants
    private static final String PREFS_NAME = "ScanConnectPrefs";
    private static final String KEY_TARGET_DOCK_NAME = "target_dock_name";

    static final Uri AUTHORITY_URI = Uri.parse("content://com.zebra.devicemanager.zdmcontentprovider");
    static Uri ACQUIRE_TOKEN_URI = Uri.withAppendedPath(AUTHORITY_URI, "AcquireToken");

    public static final String COLUMN_QUERY_RESULT = "query_result";

    private static Dialog progressDialogsDetails;
    private static DevServiceResponseListener dialogInterface;
    private static CountDownTimer tm;
    public static String secureToken = "";

    public static String getDisplayAddress(String targetDockName, String availableDisplayDetails) {
        // Parse JSON and get deviceAddress
        String deviceAddress;
        try {
            JSONArray jsonArray = new JSONArray(availableDisplayDetails);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject deviceObj = jsonArray.getJSONObject(i);
                String deviceName = deviceObj.getString("deviceName");
                String targetAddress = deviceObj.getString("deviceAddress");
                if (deviceName.equalsIgnoreCase(targetDockName) ||targetAddress.equalsIgnoreCase(targetDockName)) {
                    deviceAddress = deviceObj.getString("deviceAddress");
                    boolean isAvailable = deviceObj.getBoolean("isAvailable");
                    boolean canConnect = deviceObj.getBoolean("canConnect");

                    if (isAvailable && canConnect) {
                        return deviceAddress;
                    } else {
                        return "ERROR";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Not found
    }

    /**
     * Static method to retrieve the saved target dock name from any context
     *
     * @param context The context to use for accessing SharedPreferences
     * @return The saved dock name, or null if not found
     */
    public static String getSavedTargetDockName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedName = prefs.getString(KEY_TARGET_DOCK_NAME, null);
        Log.d(TAG, "Retrieved target dock name (static): " + savedName);
        return savedName;
    }

    /**
     * Static method to save the target dock name from any context
     *
     * @param context  The context to use for accessing SharedPreferences
     * @param dockName The dock name to save
     */
    public static void saveTargetDockName(Context context, String dockName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TARGET_DOCK_NAME, dockName);
        editor.apply();
        Log.d(TAG, "Target dock name saved (static): " + dockName);
    }



    public static void saveProximityState(Context context, boolean isChecked) {
        SharedPreferences prefs = context.getSharedPreferences("ProximityPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("connectSwitchState", isChecked);
        editor.apply();
    }

    public static boolean getSavedProximityState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ProximityPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("connectSwitchState", false); // Default value is false
    }


    @SuppressLint("Range")
    public static String acquireToken(String delegation_scope, Context mContext) {
        String token = "";
        try {
            Cursor cursor = mContext.getContentResolver().query(ACQUIRE_TOKEN_URI, (String[])null,
                    "delegation_scope=?", new String[]{delegation_scope}, (String)null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                token = cursor.getString(cursor.getColumnIndex(COLUMN_QUERY_RESULT));
                setSecuredToken(token);
                Log.e(TAG, token);
                cursor.close();
            }
        } catch (Exception var3) {
            if (var3 instanceof SecurityException) {
                Log.e(TAG, "Invalid Token/Caller" + var3.getMessage());
            } else {
                Log.e(TAG, "Unknown Caller to acquire token");
            }
        }

        return token;
    }

    public static void showNewProgressDialog(Context context, String deviceName, DevServiceResponseListener callback, long timer) {
        dialogInterface = callback;
        if (progressDialogsDetails != null && progressDialogsDetails.isShowing()) {
            progressDialogsDetails.dismiss();
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            // Now you can use activity-specific methods
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                progressDialogsDetails = new Dialog(context);
                progressDialogsDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialogsDetails.setCancelable(false);
                progressDialogsDetails.setCanceledOnTouchOutside(false);

                View view = LayoutInflater.from(context).inflate(R.layout.circular_progress_dialog, null);
                TextView tv = view.findViewById(R.id.txtTitle);
                TextView timerText = view.findViewById(R.id.txtTimer);

                tm = new CountDownTimer(timer, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        final CharSequence negativeButtonText = "Elapse Time ";
                    }

                    @Override
                    public void onFinish() {
                        dialogInterface.dismissDialogCallBack();
                    }
                }.start();

                timerText.setVisibility(View.INVISIBLE);
                String title = "Connecting to ( " + deviceName + " )" + "please_wait";
                tv.setText(title);
                progressDialogsDetails.setContentView(view);

                progressDialogsDetails.show();
            }
        }
    }

    public static void dismissNewProgressDialog() {
        if (progressDialogsDetails != null && progressDialogsDetails.isShowing()) {
            if (tm != null) {
                tm.cancel();
            }
            progressDialogsDetails.dismiss();
        }
    }

    public static void setSecuredToken(String value) {
        secureToken = value;
    }

    public static String getSecuredToken() {
        return secureToken;
    }


}
