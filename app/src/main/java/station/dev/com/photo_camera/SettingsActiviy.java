package station.dev.com.photo_camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActiviy extends Activity {

    Button bt_back, bt_camerasettings, bt_connections, bt_plus, bt_minus, bt_save, bt_cancel;

    TextView edit_interval;

    int interval_photoval=0;

    Button bt_networksettings;
    public void InitParam(){
        interval_photoval = SettingsValue.interval_photo;
    }
    public void SaveParam(){
        SettingsValue.interval_photo = interval_photoval;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        setTitle("Settings");
        InitParam();

        edit_interval = (TextView)findViewById(R.id.edit_interval);
        edit_interval.setText(String.format("%02d:%02d", SettingsValue.interval_photo / 60, SettingsValue.interval_photo % 60));

        bt_save = (Button)findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveParam();
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

        bt_camerasettings = (Button)findViewById(R.id.bt_camerasettings);
        bt_camerasettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings_activity= new Intent(getApplicationContext(), CameraSettings.class);
                startActivity(settings_activity);
            }
        });

        bt_connections = (Button)findViewById(R.id.bt_connections);
        bt_connections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent connections_activity= new Intent(getApplicationContext(), ConnectionsActivity.class);
                startActivity(connections_activity);
            }
        });

        bt_plus = (Button)findViewById(R.id.bt_plus);
        bt_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interval_photoval++;
                edit_interval.setText(String.format("%02d:%02d", interval_photoval / 60, interval_photoval % 60));
            }
        });

        bt_minus = (Button)findViewById(R.id.bt_minus);
        bt_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(interval_photoval ==1 ){
                    new AlertDialog.Builder(SettingsActiviy.this)
                            .setTitle("Warning")
                            .setMessage("The minial time is 1s.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                interval_photoval--;
                edit_interval.setText(String.format("%02d:%02d", interval_photoval / 60, interval_photoval % 60));
            }
        });

        bt_networksettings = (Button)findViewById(R.id.bt_netsettings);
        bt_networksettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent net_set = new Intent(getApplicationContext(), NetworkSettingsActivity.class);
                startActivity(net_set);
            }
        });
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
