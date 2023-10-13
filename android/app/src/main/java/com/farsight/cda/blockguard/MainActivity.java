package com.farsight.cda.blockguard;

import android.os.Bundle;
import android.webkit.WebView;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WebView.setWebContentsDebuggingEnabled(true);
        registerPlugin(com.farsight.cda.blockguard.MTLSPlugin.class);
        super.onCreate(savedInstanceState);
    }
}