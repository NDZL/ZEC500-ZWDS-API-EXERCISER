package com.zebra.zwds.developersample;


public interface DevServiceResponseListener {

    void onDevServiceResponseReceived(int reqID, int resultCode,String message,String reqType);

    void dismissDialogCallBack();

}
