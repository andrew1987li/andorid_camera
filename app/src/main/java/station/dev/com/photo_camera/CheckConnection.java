package station.dev.com.photo_camera;

import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Develop on 5/14/2016.
 */
public class CheckConnection implements Runnable {


    @Override
    public void run() {
        while(true) {
            InetAddress addr = null;
            try {
                addr = InetAddress.getByName(SettingsValue.int_url);
                SettingsValue.internet_state = true;

            } catch (Exception e) {
                SettingsValue.internet_state = false;
                e.printStackTrace();

            }
            try{
                MainThread.getInstance().UpdateUI();
                Thread.sleep(10000);
            }catch (Exception e){

            }
            //addr.getHostAddress()

        }
    }
}
