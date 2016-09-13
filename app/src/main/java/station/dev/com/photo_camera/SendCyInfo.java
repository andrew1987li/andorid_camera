package station.dev.com.photo_camera;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
 * Created by Develop on 5/25/2016.
 */
public class SendCyInfo implements  Runnable
{
    public  CyfeInfo cyfeInfo;
    protected String SendInfo(CyfeInfo params) {
        CyfeInfo tmp_info = params;

/*
        for(int i=0; i<count; i++){
            tmp_info = params[i];
            JSONObject jsonObject= new JSONObject();
            JSONArray jsonDataArr  = new JSONArray();

            try {
                JSONObject jsonData = new JSONObject();
                jsonData.put("Date", tmp_info.date);
                jsonData.put("NumberOfPhotos", tmp_info.count);
                //jsonData.put("TimeStamp", tmp_info.time);
              //  jsonData.put("Users", tmp_info.count);

                jsonDataArr.put(jsonData);



                jsonObject.put("data", jsonDataArr);

                sendCyfe(jsonObject);



            }catch (Exception e){

            }

        }

        */

        try{
            HttpClient httpclient = new DefaultHttpClient();

            httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Linux; Android 4.4.2; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
            httpclient.getConnectionManager().getSchemeRegistry().register(getMockedScheme());

            String url = String.format("%s?count=%d&date=%s&time=%s&img=%s", SettingsValue.photo_get_url,tmp_info.count, tmp_info.date, tmp_info.time, tmp_info.img_path);

            URI website = new URI(url);

            HttpGet request = new HttpGet(website);
            //request.setURI(url);
            HttpResponse response = httpclient.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            // NEW CODE
            String line;
            while((line =in.readLine())!=null) {

                        Log.i("DebugPoc", line);
            }
            /*textv.append(" First line: " + line);
            // END OF NEW CODE

            textv.append(" Connected ");
            */
        }catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        return null;
    }

    public  String endpoint = "https://app.cyfe.com/api/push/5745130a340cc6184698542220301";

    public HttpResponse sendCyfe(JSONObject jsonObject){
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //url with the post data
        HttpPost httpost = new HttpPost(endpoint);

        //convert parameters into JSON object
        /// JSONObject holder = getJsonObjectFromMap(params);

        //passes the results to a string builder/entity
        try {
            StringEntity se = new StringEntity(jsonObject.toString());

            //sets the post request as the resulting string
            httpost.setEntity(se);
            //sets a request header so the page receving the request
            //will know what to do with it
            httpost.setHeader("Accept", "application/json");
            httpost.setHeader("Content-type", "application/json");

            //Handles what is returned from the page
            ResponseHandler responseHandler = new BasicResponseHandler();
            return (HttpResponse) httpclient.execute(httpost, responseHandler);
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public void run() {
        SendInfo(cyfeInfo);
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
