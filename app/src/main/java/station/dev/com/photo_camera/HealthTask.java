package station.dev.com.photo_camera;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Develop on 5/26/2016.
 */
public class HealthTask implements  Runnable{

    @Override
    public void run() {
        while(true) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Linux; Android 4.4.2; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
                httpclient.getConnectionManager().getSchemeRegistry().register(getMockedScheme());

                HttpGet request = new HttpGet();

                String url = String.format("%s?id=%d", SettingsValue.health_get_url, 2);

                URI website = new URI(url);
                request.setURI(website);
                HttpResponse response = httpclient.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));

                // NEW CODE
                String line = in.readLine();
            /*textv.append(" First line: " + line);
            // END OF NEW CODE

            textv.append(" Connected ");
            */
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            try{
                Thread.sleep(20000);
            }catch (Exception e){

            }
        }
    }

    public Scheme getMockedScheme() throws Exception {
        MySSLSocketFactory mySSLSocketFactory = new MySSLSocketFactory();
        return new Scheme("https", mySSLSocketFactory, 443);
    }

    class MySSLSocketFactory extends SSLSocketFactory {
        javax.net.ssl.SSLSocketFactory socketFactory = null;

        public MySSLSocketFactory(KeyStore truststore) throws Exception {
            super(truststore);
            socketFactory = getSSLSocketFactory();
        }

        public MySSLSocketFactory() throws Exception {
            this(null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
                UnknownHostException {
            return socketFactory.createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return socketFactory.createSocket();
        }

        javax.net.ssl.SSLSocketFactory getSSLSocketFactory() throws Exception {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext.init(null, new TrustManager[] { tm }, null);
            return sslContext.getSocketFactory();
        }
    }
}
