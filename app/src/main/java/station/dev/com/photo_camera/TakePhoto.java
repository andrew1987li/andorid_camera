package station.dev.com.photo_camera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Develop on 5/15/2016.
 */
public class TakePhoto {
    public Timer timer;
    public TimerTask timerTask;
    public int count;

    public Camera mCamera=null;
    public String file_path;

    public TakingFourPhotos takingFourPhotos;

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void getCameraParam(){
        if (mCamera != null) {
            try {
                mCamera.release();
            }catch (Exception e){

            }
        }
        mCamera = getCameraInstance();
        //Get parameters

        try{
            SettingsValue.cam_autofocus = true;
            SettingsValue.shutter_speed = 4;
            SettingsValue.exposure = 0;
            SettingsValue.aperture = 1;
            Camera.Parameters parameters = mCamera.getParameters();

            SettingsValue.expmax = parameters.getMaxExposureCompensation();
            SettingsValue.expmin = parameters.getMinExposureCompensation();
            SettingsValue.exposure = parameters.getExposureCompensation();

            String cam_focus = parameters.getFocusMode();
            SettingsValue.cam_autofocus = cam_focus.equals("auto")?true:false;
            SettingsValue.shutter_speed = 4;
            SettingsValue.shuttmax =9 ; SettingsValue.shuttmin =1;
            SettingsValue.aperture = 1;
            SettingsValue.apermax = 10; SettingsValue.apermin = 1;

            SettingsValue.sizes = parameters.getSupportedPictureSizes();



            //SettingsValue.aperture = parameters
/*
            params.set("mode", "m");

            params.set("aperture", "28"); //can be 28 32 35 40 45 50 56 63 71 80 on default zoom

            params.set("shutter-speed", 9); // depends on camera, eg. 1 means longest

            params.set("iso", 200);
            */
            //mCamera.release();

        }
        catch (Exception e){
            e.printStackTrace();
        }
      /*  try {
           if(mCamera!= null)
            mCamera.release();
        }catch (Exception e){

        }
        */
    }


    /** Called when the activity is first created. */
    public void InitCamera() {
        //
        if (mCamera != null) {
            try {
                mCamera.release();
                mCamera = null;
            }catch (Exception e){

            }
        }
        mCamera = getCameraInstance();

        // mCamera = Camera.open();
        //        mCamera.takePicture(null, null, mPicture);
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }



    public TakePhoto(){
        takingFourPhotos = new TakingFourPhotos();
    }


    public class TakingFourPhotos{
        Timer timer;
        TimerTask timerTask;

        int count = 0;


        public void startTakingPhoto(){
            if(SettingsValue.now_taking_photo == true) return;
            SettingsValue.now_taking_photo = true;
            SettingsValue.photo_count = 0;
            SettingsValue.photo_start = true;
            MainThread.getInstance().taking_photo_group = new PhotoGroup();
        }


    }

    public void startTakePhoto(int delay) {
        stopTakePhoto();
        if(delay > 4) delay -=4;
        SettingsValue.delay_time = delay;


        if(delay == 0){
            takingFourPhotos.startTakingPhoto();
        }else {

            count = 0;

            if(timer != null){
                try{
                    timer.cancel();
                }catch (Exception e){

                }
            }

            timer = new Timer();

            timerTask = new TimerTask() {
                @Override
                public void run() {

                    if (count == SettingsValue.delay_time) {
                        takingFourPhotos.startTakingPhoto();
                        try {
                            timer.cancel();
                        } catch (Exception e) {

                        }
                    }
                    count++;
                    MainThread.getInstance().UpdateUI();
                }
            };

            timer.schedule(timerTask, 0, 1000);
        }
    }

    public  void stopTakePhoto(){
        if(timer != null){
            timer.cancel();
            timer = null;
            timerTask = null;
        }
    }
}
