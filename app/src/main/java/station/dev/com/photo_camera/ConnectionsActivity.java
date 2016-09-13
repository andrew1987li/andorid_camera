package station.dev.com.photo_camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionsActivity extends Activity {
    TextView lbl_connections, lbl_camera, lbl_wifi,lbl_internet, lbl_sharefisrt, lbl_sharesecond;
    CheckBox chk_camera, chk_wifi, chk_internet, chk_sharefirst, chk_sharesecond;

    public void CheckWifi(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
/*
        if (mWifi.isConnected()) {
            // Do whatever
        }
*/

        chk_wifi.setChecked(mWifi.isConnected());
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        lbl_wifi.setText(String.format("Wifi Info:\n\n SSID: %s\n\n MacAddress: %s \n\n BSSID: %s \n\n LineSpeed : %d ", wifiInfo.getSSID().toString(), wifiInfo.getMacAddress().toString(), wifiInfo.getBSSID().toString(), wifiInfo.getLinkSpeed()));
    /*    Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        */

    }

    private boolean isNetworkAvailable() {

/*
        String netAddress = null;
        try
        {
            netAddress = new NetTask().execute(SettingsValue.int_url).get();
            if(netAddress == null) return  false;
            /*Socket socket = new Socket(netAddress, 80);
            if(socket != null){
                socket.close();
                return true;
            }
            else return false;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return  false;
        }

        return  true;


*/
        return  true;
    }

    public void CheckInternet(){
        if(chk_wifi.isChecked()){
            new NetTask().execute(SettingsValue.int_url);
        }
        else
            chk_internet.setChecked(false);


    }

    public void CheckCameraApp(){
        chk_camera.setChecked(MainThread.getInstance().netstatefirst);
    }

    public class NetTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            InetAddress addr = null;
            try
            {
                addr = InetAddress.getByName(params[0]);
            }

            catch (UnknownHostException e)
            {
                e.printStackTrace();
                publishProgress(0);
                return  null;
            }
            publishProgress(1);
            return addr.getHostAddress();
        }
        @Override
        protected void onProgressUpdate(Integer...progress){
            if(progress[0] == 1){
                chk_internet.setChecked(true);
            }
            else chk_internet.setChecked(false);
        }

        @Override
        protected void onPostExecute(String result){

        }

        @Override
        protected   void onPreExecute(){

        }
    }

    public void CheckShareApp(){
        int share_count = MainThread.getInstance().con_third;
        if(share_count == 0){
            chk_sharefirst.setChecked(false);
            chk_sharesecond.setChecked(false);
        }else if(share_count ==1) {
            chk_sharefirst.setChecked(true);
        }
        else {
            chk_sharefirst.setChecked(true);
            chk_sharesecond.setChecked(true);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_connections);
        setTitle("Connections");

        Typeface face = MFont.getBoldfont();

        lbl_connections = (TextView)findViewById(R.id.lbl_connections);
        lbl_connections.setTypeface(face);

        lbl_camera = (TextView)findViewById(R.id.lbl_camera);
        lbl_camera.setTypeface(face);

        lbl_internet = (TextView)findViewById(R.id.lbl_internet);
        lbl_internet.setTypeface(face);

        lbl_wifi = (TextView)findViewById(R.id.lbl_wifi);
        lbl_wifi.setTypeface(face);

        lbl_sharefisrt = (TextView)findViewById(R.id.lbl_sharefirst);
        lbl_sharefisrt.setTypeface(face);

        lbl_sharesecond = (TextView)findViewById(R.id.lbl_sharesecond);
        lbl_sharesecond.setTypeface(face);

        chk_camera = (CheckBox)findViewById(R.id.chk_camera);
        chk_internet = ( CheckBox)findViewById(R.id.chk_internet);
        chk_wifi =(CheckBox)findViewById(R.id.chk_wifi);
        chk_sharefirst =(CheckBox)findViewById(R.id.chk_sharefirst);
        chk_sharesecond = (CheckBox)findViewById(R.id.chk_sharesecond);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                MainThread.getInstance().mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CheckCameraApp();
                        CheckShareApp();
                        CheckWifi();
                        CheckInternet();
                    }
                });

            }
        },0,2000);



    }
}
