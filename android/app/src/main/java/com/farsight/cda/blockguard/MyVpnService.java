package com.farsight.cda.blockguard;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MyVpnService extends VpnService {
    private DatagramSocket tunnelSocket;
    private ParcelFileDescriptor vpnInterface;

    //ToDo: Make sure everything is try-caught to avoid side effects
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Step 1: Call VpnService.prepare() to ask for permission (when needed).
        Context context = getApplicationContext();
        if (prepare(context) != null) {
            //Caller should make sure that it is prepared
            stopService(intent);
            return START_NOT_STICKY;
        }

        // Step 2: Call VpnService.protect() to keep your app's tunnel socket outside of the system VPN and avoid a circular connection.
        tunnelSocket = createGatewaySocket();
        protect(tunnelSocket);

        // Step 3: Call DatagramSocket.connect() to connect your app's tunnel socket to the VPN gateway.

        //ToDo: Connect your tunnel socket to the VPN gateway

        // Step 4: Call VpnService.Builder methods to configure a new local TUN interface on the device for VPN traffic.
        //ToDo: Obtain those IPs from the gateway
        Builder builder = new Builder();
        builder.setSession("MyVPNService")
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0);

        // Step 5: Call VpnService.Builder.establish() so that the system establishes the local TUN interface and begins routing traffic through the interface.
        vpnInterface = builder.establish();

        //ToDo: Notify plugin of service status change
        return START_STICKY;
    }

    private DatagramSocket createGatewaySocket() {
        DatagramSocket tunnel = null;
        try {
            tunnel = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        return tunnel;
    }

    @Override
    public void onRevoke() {
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (Exception e) {
                //ToDo: Handle exception
            } finally {
                vpnInterface = null;
            }
        }
        if (tunnelSocket != null) {
            try {
                tunnelSocket.close();
            } catch (Exception ex) {
                //ToDo: Handle exception
            } finally {
                tunnelSocket = null;
            }
        }

        //ToDo: Notify plugin of service status change
        super.onRevoke();
    }
}
