package com.farsight.cda.blockguard;

import android.content.Context;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import java.io.IOException;
import java.net.InetAddress;

@CapacitorPlugin(name = "VpnService")
public class VpnPlugin extends Plugin {

    @PluginMethod()
    public void vpnStart(PluginCall call) throws IOException {
        Context context = this.getContext();
        VpnSer vpnSer = new VpnSer(context);
        vpnSer.prepare();
        vpnSer.protect();
        vpnSer.connect(InetAddress.getByName("192.168.2.2"), 24);
        vpnSer.build();
        vpnSer.start();
    }

    @PluginMethod()
    public void vpnStop(PluginCall call) throws IOException {
        Context context = this.getContext();

    }
}
