package station.dev.com.photo_camera;

import android.app.Activity;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CameraPreviewActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;

    public FrameLayout preview;

    public void InitCamera(){
        MainThread.getInstance().take_photo.InitCamera();
        mCamera = MainThread.getInstance().take_photo.mCamera;

        if(mCamera != null) {
            mPreview = new CameraPreview(this, mCamera);
            // FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.removeAllViews();
            preview.addView(mPreview);
            // SettingsValue.photo_ready = true;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_preview);

        preview =(FrameLayout) findViewById(R.id.camera_preview);

        if(MainThread.getInstance().take_photo.mCamera == null)
            MainThread.getInstance().take_photo.InitCamera();

        // mCamera = MainThread.getInstance().take_photo.mCamera;
        mCamera = MainThread.getInstance().take_photo.mCamera;

        MainThread.getInstance().SetCameraSettings();

        InitCamera();
    }

    public void ReleaseCamera(){
        try{
            if(mCamera!=null)     mCamera.release();

        }catch (Exception e){
            Log.i("DebugPoc","camera:"+ e.getMessage());
        }
        MainThread.getInstance().take_photo.mCamera = null;
    }

    public void confirmBack(){
        /*
        new AlertDialog.Builder(SettingsActiviy.this)
                .setTitle("Warning")
                .setMessage("You are goint to back to the Start screen. The settings is saved automatically.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();   */
        ReleaseCamera();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed(){


        Log.i(MUtils.TAG, "Back button pressed in Setting Screen");

        confirmBack();


    }
}
