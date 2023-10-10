package com.farsight.cda.blockguard;


import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class VpnSer extends VpnService {
    DatagramSocket vpnSocket;
    Context context;
    Intent intent;

    public VpnSer(Context context) throws IOException {
        this.vpnSocket = new DatagramSocket();
        this.context = context;
    }

    void prepare() {
        Intent temp = VpnService.prepare(this.context);
        if (temp == null) {
            throw new RuntimeException("VPN is not prepared");
        }
        this.intent = temp;
    }


    void protect() {
        protect(vpnSocket);
    }

    void connect(InetAddress address, int port) {
        vpnSocket.connect(address, port);
    }

    void build() {
        Builder builder = new Builder();
        ParcelFileDescriptor p = builder.setSession("BlockGuard")
                .addAddress("192.168.2.2", 24)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("192.168.1.1")
                .establish();
        System.out.print(p);
    }

    void start() {

        try {
            // Start the VPN service
            stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
