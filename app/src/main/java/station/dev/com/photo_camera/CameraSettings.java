package station.dev.com.photo_camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class CameraSettings extends Activity {

    Button bt_back, bt_shutterplus, bt_shuttermin, bt_expplus, bt_expmin, bt_aperplus, bt_apermin, bt_save, bt_cancel;
    Button bt_complus, bt_commin, bt_sizeplus, bt_sizemin;
    TextView txt_shutter, txt_exposure, txt_aperture, txt_compress, txt_size;

    boolean auto_focus = true;
    int shutter_speed ,aperature;
    int exposuer ,compress, size_index;

    CheckBox chk_autofocus;

    Button bt_preview, bt_apply;

    List<Camera.Size>  camera_size;

    public void initParameter(){
        shutter_speed = SettingsValue.shutter_speed;
        exposuer = SettingsValue.exposure;
        aperature = SettingsValue.aperture;
        auto_focus = SettingsValue.cam_autofocus;
        compress = SettingsValue.image_compress;
        size_index = SettingsValue.camera_size_index;
        camera_size = SettingsValue.sizes;
    }

    public  void saveParameter(){
        SettingsValue.cam_autofocus = auto_focus;
        SettingsValue.shutter_speed = shutter_speed;
        SettingsValue.exposure = exposuer;
        SettingsValue.aperture = aperature;
        SettingsValue.image_compress = compress;
        SettingsValue.camera_size_index = size_index;

        MainThread.getInstance().SetCameraSettings();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SettingsValue.REQUEST_PREVIEW) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings);

        initParameter();

        setTitle("CameraSetting");


        bt_save = (Button)findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParameter();
                finish();
            }
        });

        bt_cancel =(Button)findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chk_autofocus = (CheckBox)findViewById(R.id.chk_autofocus);
        chk_autofocus.setChecked(auto_focus);

        txt_shutter = (TextView)findViewById(R.id.txt_shutterspeed);
        txt_shutter.setText("" + shutter_speed);

        txt_aperture = (TextView)findViewById(R.id.txt_aperture);
        txt_aperture.setText(SettingsValue.apertureString[aperature - 1]);

        txt_exposure = (TextView)findViewById(R.id.txt_exposure);
        txt_exposure.setText(""+exposuer);

        txt_compress = (TextView)findViewById(R.id.txt_compress);
        txt_compress.setText(""+compress);

        txt_size = (TextView)findViewById(R.id.txt_size);
        try {
            txt_size.setText(String.format("%d * %d", camera_size.get(size_index).width, camera_size.get(size_index).height));
        }
        catch (Exception e){

        }

        bt_shutterplus = (Button)findViewById(R.id.bt_shutterplus);
        bt_shutterplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shutter_speed < SettingsValue.shuttmax) shutter_speed++;
                txt_shutter.setText("" + shutter_speed);
            }
        });

        bt_shuttermin = (Button)findViewById(R.id.bt_shutterminus);
        bt_shuttermin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shutter_speed >SettingsValue.shuttmin) shutter_speed--;
                txt_shutter.setText(""+shutter_speed);
            }
        });

        bt_expplus  = (Button)findViewById(R.id.bt_exposureplus);
        bt_expplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exposuer < SettingsValue.expmax) exposuer++;
                txt_exposure.setText(""+exposuer);
            }
        });

        bt_expmin = (Button)findViewById(R.id.bt_exposureminus);
        bt_expmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exposuer> SettingsValue.expmin) exposuer--;
                txt_exposure.setText(""+exposuer);
            }
        });

        bt_aperplus = (Button)findViewById(R.id.bt_apertureplus);
        bt_aperplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aperature < SettingsValue.apermax) aperature++;
                txt_aperture.setText(SettingsValue.apertureString[aperature-1]);
            }
        });

        bt_apermin = (Button)findViewById(R.id.bt_apertureminus);
        bt_apermin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aperature>SettingsValue.apermin) aperature --;
                txt_aperture.setText(SettingsValue.apertureString[aperature-1]);
            }
        });

        bt_complus = (Button)findViewById(R.id.bt_complus);
        bt_complus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(compress<SettingsValue.max_com) compress++;
                txt_compress.setText(""+compress);
            }
        });

        bt_commin = (Button)findViewById(R.id.bt_comminus);
        bt_commin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(compress>SettingsValue.min_com) compress--;
                txt_compress.setText(""+compress);
            }
        });

        bt_sizeplus = (Button)findViewById(R.id.bt_sizeplus);
        bt_sizeplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(size_index < (camera_size.size() -1)) size_index++;
                txt_size.setText(String.format("%d * %d", camera_size.get(size_index).width, camera_size.get(size_index).height));
            }
        });

        bt_sizemin = (Button)findViewById(R.id.bt_sizeminus);
        bt_sizemin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(size_index>0) size_index --;
                txt_size.setText(String.format("%d * %d", camera_size.get(size_index).width, camera_size.get(size_index).height));
            }
        });

        bt_preview = (Button)findViewById(R.id.bt_preview);
        bt_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prev_activity = new Intent(getApplicationContext(), CameraPreviewActivity.class);
                startActivityForResult(prev_activity, SettingsValue.REQUEST_PREVIEW);
            }
        });

        bt_apply =(Button)findViewById(R.id.bt_apply);
        bt_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveParameter();
            }
        });
    }

    public void confirmBack(){
/*
        new AlertDialog.Builder(CameraSettings.this)
                .setTitle("Warning")
                .setMessage("You are goint to back. The value is not saved.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // process stop

                        finish();


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
                */
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


        Log.i(MUtils.TAG, "Back button pressed in Timer Screen");

        confirmBack();


    }
}
