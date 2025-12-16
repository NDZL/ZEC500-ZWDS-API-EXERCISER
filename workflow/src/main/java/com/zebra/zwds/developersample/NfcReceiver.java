package com.zebra.zwds.developersample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;

public class NfcReceiver extends BroadcastReceiver {
    private NfcOffCallback callback;
    public NfcReceiver(NfcOffCallback callback){
        this.callback=callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int nfcState = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF);
        if (nfcState == NfcAdapter.STATE_ON) {
            callback.nfcState(true);
        } else if (nfcState == NfcAdapter.STATE_OFF) {
            callback.nfcState(false);
        }

    }
}