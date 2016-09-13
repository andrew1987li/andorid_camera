package station.dev.com.photo_camera;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends Activity {
    public String file_path;

    private Camera mCamera;
    private CameraPreview mPreview;

    public FrameLayout preview;

    Timer timer;
    TimerTask timerTask;

    int count =0;

    public void startTimer(){
        stopTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(count ==4){
                    stopTimer();

                    ReleaseCamera();

                    Intent intent_data = new Intent();
                    intent_data.putExtra("file_path", file_path);
                    setResult(RESULT_CANCELED, intent_data);
                    Log.i("DebugPoc", "canceled taking picture");
                    finish();
                }
                else {
                    count++;
                    takePhoto();
                }
            }
        };
          timer.schedule(timerTask, 0, 1500);
    }

    public void stopTimer(){
        if(timer != null){
            try{
                timer.cancel();
                timer =null;

            }catch (Exception e){

            }
        }
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {



            Log.i("DebugPoc", "------took photo");
            file_path = MUtils.getNewImagePath();

            File img = new File(file_path);

            try {
                FileOutputStream fos = new FileOutputStream(img);
                fos.write(data);
                fos.close();



/*
                if(MainThread.getInstance().netstatethird) {
                    MainThread.getInstance().sendReplyToController(GlobalVar.REPLY_PHOTOTAKEN.getValue());
                    MainThread.getInstance().sendImageToStationThird(file_path);
                }
                else {
                    MainThread.getInstance().sendReplyToController(GlobalVar.REPLY_SHAREAPPOFFLINE.getValue());
                }

*/
            }catch (Exception e){

            }


            stopTimer();
            Intent intent_data = new Intent();
            intent_data.putExtra("file_path", file_path);
            setResult(RESULT_OK, intent_data);
            Log.i("DebugPoc", "Picture Taken");

            ReleaseCamera();
            finish();

        }
    };


    public void ReleaseCamera(){
        Log.i("DebugPoc", "releaseCamera in Camera Activity");
        try{
            if(mCamera!=null)     mCamera.release();

        }catch (Exception e){
            Log.i("DebugPoc","camera:"+ e.getMessage());
        }
        MainThread.getInstance().take_photo.mCamera = null;
    }

    public void InitCamera(){

        if(mCamera != null) {
            mPreview = new CameraPreview(this, mCamera);
            // FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

            preview.removeAllViews();
            preview.addView(mPreview);
            // SettingsValue.photo_ready = true;

        }

    }

    public void takePhoto(){
        Log.i("DebugPoc", "TakePhoto Before");
        try {
            /*
            SurfaceView view = new SurfaceView(MainThread.getInstance().context);
            mCamera.setPreviewDisplay(view.getHolder());
            mCamera.startPreview();
            */

            mCamera.takePicture(null, null, mPicture);
            Log.i("DebugPoc", "takePicture call after");
        } catch (Exception e) {
            Log.i("DebugPoc", "TakePhoto Before ex" + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);


        Log.i("DebugPoc", "Taking Photo oncreate");
       // MainThread.getInstance().take_photo.InitCamera();


        preview =(FrameLayout) findViewById(R.id.camera_preview);


        if(MainThread.getInstance().take_photo.mCamera == null)
            MainThread.getInstance().take_photo.InitCamera();
       // mCamera = MainThread.getInstance().take_photo.mCamera;
        mCamera = MainThread.getInstance().take_photo.mCamera;

        //MainThread.getInstance().SetCameraSettings();

        InitCamera();

        startTimer();
        //takePhoto();
    }
}
