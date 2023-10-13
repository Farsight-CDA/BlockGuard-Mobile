package com.farsight.cda.blockguard;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

@CapacitorPlugin(name = "MTLS")
public class MTLSPlugin extends Plugin {

    public class MTLSFetchResponse {
        public boolean success;
        public int statusCode;
        public String body;
        public MTLSFetchResponse(boolean success, int statusCode, String body) {
            this.success = success;
            this.statusCode = statusCode;
            this.body = body;
        }
    }

    @PluginMethod
    public void mtlsFetch(PluginCall call) {
        String method = "GET";//call.getString("method"); // GET
        String inputUrl = "https://provider.akashmining.com:8443/lease/43856657/1/1/status";//call.getString("url"); //https://provider.akashmining.com:8443/lease/43856657/1/1/status
        String body = "" ;//call.getString("body"); // ""
        String csr = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBmDCCAT2gAwIBAgIGAYshHmvIMAoGCCqGSM49BAMCMDcxNTAzBgNVBAMTLGFrYXNoMW1rbGdtbHQzMGN4ZGo4dzl0YWU4M3A5aGM0dnc1a2pkdGZzOHV3MB4XDTIzMTAxMTIyMDAwMFoXDTI0MTAxMDIyMDAwMFowNzE1MDMGA1UEAxMsYWthc2gxbWtsZ21sdDMwY3hkajh3OXRhZTgzcDloYzR2dzVramR0ZnM4dXcwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASr6Oix9WwlzeUPi0Sinr8wIKgbs+hczW/EpqElD/1lbfdBX/InLkv9C3rdTYexd+QBfLOKSRt6LkeCn4+9leNtozUwMzAOBgNVHQ8BAf8EBAMCADAwEwYDVR0lBAwwCgYIKwYBBQUHAwIwDAYDVR0TAQH/BAIwADAKBggqhkjOPQQDAgNJADBGAiEAoMSxk1C6mb3aluPGxcO7T7YVUgPpNLio8vcGvWMAnTkCIQCnT55Q u2H/QvLnbfKLI4paz0jdDOyxI0C0FcQmvteq/g==\n" +
                "-----END CERTIFICATE-----";

        String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgR5CAU8LEd41n6J/0lhJcT60rOPplPshf90P4ji9y/WKhRANCAASr6Oix9WwlzeUPi0Sinr8wIKgbs+hczW/EpqElD/1lbfdBX/InLkv9C3rdTYexd+QBfLOKSRt6LkeCn4+9leNt\n" +
                "-----END PRIVATE KEY-----";



        try {
            URL url  = new URL(inputUrl);
            assert csr != null;
            assert privateKey != null;
            // Load the client certificate and private key
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream inStream = new ByteArrayInputStream(csr.getBytes());
            Certificate certificate = cf.generateCertificate(inStream);

            KeyStore keyStore = KeyStore.getInstance("PKCS12","BC");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("cert", certificate);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(privateKey.getBytes());
            byte[] encodedKey = readAllBytes(inputStream);

            KeyFactory keyFactory = KeyFactory.getInstance("EC");



            keyStore.setKeyEntry("huuuuuan", encodedKey, new Certificate[]{});

            privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] privateKeyBytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                privateKeyBytes = Base64.getDecoder().decode(privateKey);
            }


            // Set up the SSL context
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, "".toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create the HttpURLConnection

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            // Get the socket factory from your SSL context and cast it to SSLSocketFactory
            if (sslContext.getSocketFactory() != null) {
                SSLSocketFactory socketFactory = (SSLSocketFactory) sslContext.getSocketFactory();
                ((HttpsURLConnection) connection).setSSLSocketFactory(socketFactory);
            }

            // Send the request body
            if (body != null && body.length() > 0) {
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(body.getBytes());
                outputStream.flush();
            }

            // Get the response
            int responseCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // Prepare the response to send back to JavaScript
            JSObject result = new JSObject();
            result.put("status", responseCode);
            result.put("data", response.toString());
            call.resolve(result);
        } catch (Exception e) {
            // Handle any exceptions and return an error response
            JSObject error = new JSObject();
            error.put("message", e.getMessage());
            call.reject(error.toString());
        }
    }
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }
}

