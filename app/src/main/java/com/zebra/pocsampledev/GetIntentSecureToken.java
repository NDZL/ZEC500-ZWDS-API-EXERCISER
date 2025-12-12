package com.zebra.pocsampledev;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class GetIntentSecureToken {
    static final Uri AUTHORITY_URI = Uri.parse("content://com.zebra.devicemanager.zdmcontentprovider");
   static Uri ACQUIRE_TOKEN_URI = Uri.withAppendedPath(AUTHORITY_URI, "AcquireToken");

    public static final String COLUMN_QUERY_RESULT = "query_result";
    private static final String TAG = "GetIntentSecureToken";

    @SuppressLint("Range")
    public static String acquireToken(String delegation_scope, Context mContext) {
        String token = "";
        try {
            Cursor cursor = mContext.getContentResolver().query(ACQUIRE_TOKEN_URI, (String[])null,
                    "delegation_scope=?", new String[]{delegation_scope}, (String)null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                token = cursor.getString(cursor.getColumnIndex(COLUMN_QUERY_RESULT));
                Log.e(TAG, token);
                cursor.close();
            }
        } catch (Exception var3) {
            if (var3 instanceof SecurityException) {
                Log.e(TAG, "Invalid Token/Caller");
            } else {
                Log.e(TAG, "Unknown Caller to acquire token");
            }
        }

        return token;


    }
}
