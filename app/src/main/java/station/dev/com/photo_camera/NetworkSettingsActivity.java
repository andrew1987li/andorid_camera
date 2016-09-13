package station.dev.com.photo_camera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NetworkSettingsActivity extends AppCompatActivity {
    Button bt_back,bt_save, bt_cancel;

    TextView txt_ftpaddr, txt_user, txt_pwd;
    EditText edit_ftpaddr, edit_user, edit_pwd, edit_directory;

    public  String Ftp_addr;
    public  String UserName;
    public   String Password;
    public String ftp_directory;

    public void InitParam(){
        Ftp_addr = SettingsValue.Ftp_addr;
        UserName = SettingsValue.UserName;
        Password = SettingsValue.Password;
        ftp_directory = SettingsValue.directoryName;
    }

    public void SaveParam(){
        SettingsValue.Ftp_addr = edit_ftpaddr.getText().toString();
        SettingsValue.UserName = edit_user.getText().toString();
        SettingsValue.Password = edit_pwd.getText().toString();
        SettingsValue.directoryName = edit_directory.getText().toString();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_settings);

        InitParam();

        bt_back = (Button)findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        edit_ftpaddr = (EditText)findViewById(R.id.edit_ftpaddress);
        edit_ftpaddr.setText(Ftp_addr);
        edit_user =(EditText)findViewById(R.id.edit_username);
        edit_user.setText(UserName);
        edit_pwd =(EditText)findViewById(R.id.edit_password);
        edit_pwd.setText(Password);

        edit_directory = (EditText)findViewById(R.id.edit_ftp_directory);
        edit_directory.setText(ftp_directory);

    }
}
