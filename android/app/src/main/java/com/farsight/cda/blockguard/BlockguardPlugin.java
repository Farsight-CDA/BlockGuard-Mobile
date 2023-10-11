package com.farsight.cda.blockguard;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.util.Log;

import androidx.activity.result.ActivityResult;

@CapacitorPlugin(name = "Blockguard",
permissions = {
        @Permission(
                alias = "internet",
                strings = {
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE
                }
        )
})
public class BlockguardPlugin extends Plugin {
    private MyVpnService vpnService;

    @Override
    public void load() {
        vpnService = new MyVpnService();
        super.load();
    }

    @PluginMethod()
    public void connectVPN(PluginCall call) {
        Log.d("Blockguard", "connectVPN: Initiated");
        if (getPermissionState("internet") != PermissionState.GRANTED) {
            Log.i("Blockguard", "connectVPN: Requesting internet permissions");
            requestPermissionForAlias("internet", call, "internetPermissionCallback");
            return;
        }

        Log.d("Blockguard", "connectVPN: internet permission available");
        prepareVPNConnection(call);
    }

    @PermissionCallback
    private void internetPermissionCallback(PluginCall call) {
        if (getPermissionState("internet") == PermissionState.GRANTED) {
            Log.w("Blockguard", "internetPermissionCallback: Request granted");
            prepareVPNConnection(call);
            return;
        }

        Log.w("Blockguard", "internetPermissionCallback: Request rejected");
        call.reject("The app needs permissions to access the internet to continue!");
    }

    public void prepareVPNConnection(PluginCall call) {
        var prepareIntent = VpnService.prepare(getContext());

        if (prepareIntent != null) {
            Log.d("Blockguard", "prepareVPNConnection: Preparing Connection");
            startActivityForResult(call, prepareIntent, "prepareVPNCallback");
            getContext().startActivity(prepareIntent);
        } else {
            Log.d("Blockguard", "prepareVPNConnection: Already prepared");
            launchVpnService(call);
        }
    }
    @ActivityCallback
    private void prepareVPNCallback(PluginCall call, ActivityResult result) {
        if (VpnService.prepare(getContext()) != null) {
            Log.w("Blockguard", "Preparation rejected");
            call.reject("The app could not set your active VPN!");
            return;
        }

        launchVpnService(call);
    }

    private void launchVpnService(PluginCall call) {
        Log.i("Blockguard", "launchVpnService: Starting");
        Context context = getContext();

        Intent vpnIntent = new Intent(context, MyVpnService.class);
        context.startService(vpnIntent);

        call.resolve();
    }
}
