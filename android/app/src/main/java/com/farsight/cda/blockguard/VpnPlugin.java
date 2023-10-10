package com.farsight.cda.blockguard;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;

@CapacitorPlugin(name = "VpnService")
public class VpnPlugin extends Plugin {

    @PluginMethod()
    public void VpnService(PluginCall call) {
        JSObject ret = new JSObject();

        if ( VpnService.prepare(getContext())== null) {
            Intent intent = VpnService.prepare(getContext());
            if (intent != null) {
                getContext().startActivity(intent);
                ret.put("value", "Start VPN service.");
                call.resolve();
            } else {

                ret.put("value", "Failed to prepare VPN service.");
                call.resolve(ret);
            }
        } else {
            startVpnService();
            ret.put("value", "VPN service is already prepared.");
            call.resolve();
        }
    }

    private void startVpnService() {
        Context context = getContext();
        Intent vpnIntent = new Intent(context, MyVpnService.class);
        context.startService(vpnIntent);
    }
}
