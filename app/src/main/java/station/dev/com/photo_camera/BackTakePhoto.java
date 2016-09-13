package station.dev.com.photo_camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Develop on 5/16/2016.
 */
public class BackTakePhoto  extends AsyncTask<String, Integer, String> {
    public Camera mcamera;
    public String file_path;

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public String getNewImagePath(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String path =  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+ "PocImage";
        File dir = new File(path);

        if(!dir.exists()){
            dir.mkdir();
        }

        String mediaFile =  path + File.separator+ "IMG_" + timeStamp + ".jpg";

        return mediaFile;

    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Log.i("DebugPoc", "took photo");
            file_path = getNewImagePath();

            File img = new File(file_path);

            try {
                FileOutputStream fos = new FileOutputStream(img);
                fos.write(data);
                fos.close();


                //MainThread.getInstance().sendImageToStationThird(file_path);

            }catch (Exception e){

            }


        }
    };

    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer...progress){

    }

    @Override
    protected void onPostExecute(String result){

    }

    @Override
    protected   void onPreExecute(){

    }
}
