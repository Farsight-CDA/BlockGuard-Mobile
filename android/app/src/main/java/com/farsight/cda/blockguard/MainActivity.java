package com.farsight.cda.blockguard;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerPlugin(com.farsight.cda.blockguard.BlockguardPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
