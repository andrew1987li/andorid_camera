package station.dev.com.photo_camera;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;


/**
 * Created by Develop on 5/15/2016.
 */
public class UploadImage implements Runnable {

    ArrayList<String>photoGroupsList;

    @Override
    public void run() {

        photoGroupsList = SettingsValue.upload_phtogroup;

        int i,j;

        while(true) {

            int image_count = photoGroupsList.size();

            if(image_count>0){
                String image_path;
                int del_count =0;

                for (i = 0; i < image_count; i++) {
                    try {
                        image_path = photoGroupsList.get(i - del_count);

                        File file = new File(image_path);
                        try {
                            uploadFile(file);

                            photoGroupsList.remove(i - del_count);
                            del_count++;
                        } catch (Exception e) {
                            Log.i("DebugPoc", "FTP upload ex:" + e.getMessage());

                        }

                    }
                    catch (Exception e){

                    }
                }


            }

            try{
                Thread.sleep(6000);
            }catch (Exception e){

            }

        }
    }
/*
    public void upload(String img_path){


        FTPClient con = null;

        try
        {
            con = new FTPClient();
            con.connect("208.109.181.73");


            if (con.)
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                String data = "/sdcard/vivekm4a.m4a";

                FileInputStream in = new FileInputStream(new File(data));
                boolean result = con.storeFile("/vivekm4a.m4a", in);
                in.close();
                if (result) Log.v("upload result", "succeeded");
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }






    }
*/
    public void download(){
       /* FTPClient con = null;

        try
        {
            con = new FTPClient();
            con.connect("192.168.2.57");

            if (con.login("Administrator", "KUjWbk"))
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                String data = "/sdcard/vivekm4a.m4a";

                OutputStream out = new FileOutputStream(new File(data));
                boolean result = con.retrieveFile("vivekm4a.m4a", out);
                out.close();
                if (result) Log.v("download result", "succeeded");
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            Log.v("download result","failed");
            e.printStackTrace();
        }


            */
    }

    public static final String FTP_USER="fcvendor";
    public static final String FTP_PASS ="Fc5000!!";

        public void uploadFile(File fileName) throws  Exception{


            FTPClient client = new FTPClient();

            try {

                client.connect("208.109.181.73", 21);
                client.login(FTP_USER, FTP_PASS);
                client.setType(FTPClient.TYPE_BINARY);

                Log.i("DebugPoc", SettingsValue.directoryName);
                String dir = "/Tarzan/" + SettingsValue.directoryName;

                try{
                    client.createDirectory(dir);
                }catch (Exception e){

                }

/*



 */

                client.changeDirectory(dir);

                client.upload(fileName, new MyTransferListener());

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                throw e;
            }

        }

        /*******  Used to file upload and show progress  **********/

        public class MyTransferListener implements FTPDataTransferListener {

            public void started() {
/*
                btn.setVisibility(View.GONE);
                // Transfer started
                Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                //System.out.println(" Upload Started ...");
                */
            }

            public void transferred(int length) {
/*
                // Yet other length bytes has been transferred since the last time this
                // method was called
                Toast.makeText(getBaseContext(), " transferred ..." + length, Toast.LENGTH_SHORT).show();
                //System.out.println(" transferred ..." + length);
                */
            }

            public void completed() {
/*
                btn.setVisibility(View.VISIBLE);
                // Transfer completed

                Toast.makeText(getBaseContext(), " completed ...", Toast.LENGTH_SHORT).show();
                //System.out.println(" completed ..." );
                */
            }

            public void aborted() {
/*
                btn.setVisibility(View.VISIBLE);
                // Transfer aborted
                Toast.makeText(getBaseContext()," transfer aborted ,
                        please try again...", Toast.LENGTH_SHORT).show();
                //System.out.println(" aborted ..." );
                */
            }

            public void failed() {
/*
                btn.setVisibility(View.VISIBLE);
                // Transfer failed
                System.out.println(" failed ..." );
                */
            }

        }
}
