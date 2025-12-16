package com.zebra.zwds.developersample;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SecureIntentActivity extends AppCompatActivity {

    public static boolean hasSecureModeEnabled = false;
    public static final String DEVELOPER_SERVICE_IDENTIFIER = "delegation-zebra-zwds-api-intent-secure";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.acquireToken(DEVELOPER_SERVICE_IDENTIFIER, this);
        new AlertDialog.Builder(this)
                .setTitle("Secure Intent")
                .setMessage("Would you like to enable Secure Intent?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    setSecureModeEnabled(true);
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    setSecureModeEnabled(false);
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    public static void setSecureModeEnabled(boolean value) {
        hasSecureModeEnabled = value;
    }

    public static boolean isSecureModeEnabled() {
        return hasSecureModeEnabled;
    }
}
