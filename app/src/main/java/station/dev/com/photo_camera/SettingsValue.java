package station.dev.com.photo_camera;

import android.hardware.Camera;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Develop on 5/19/2016.
 */
public class SettingsValue {
    public static int interval_photo = 1;
    public static int delay_time = 30;

    public static String int_url = "www.tarzaninfinityjungle.us";
    public static int reply_camera = -1;

    public static boolean photo_start = false;

    public static int photo_count = 0;

    public static final int REQUEST_PHOTO = 3000;
    public static final int REQUEST_PREVIEW=3001;

    public static String Ftp_addr = "208.109.181.73";
    public static String UserName = "fcvendor";
    public static  String Password ="Fc5000!!";
    public static String directoryName = "photos_usa";

    public static  boolean cam_autofocus = false;
    public static int shutter_speed = 1;
    public static  int shuttmin,  shuttmax;
    public static int exposure, expmax, expmin;
    public static int apermax, apermin;
    public static  int aperture;

    public static String[] apertureString = {"28", "32" ,"35", "40", "45", "50", "56" ,"63", "71", "80"};

    public static     ArrayList<String> upload_phtogroup= new ArrayList<String>();

    public static boolean is_running = false;

    public static boolean now_taking_photo = false;

    public static int gruop_id =0;

    public static boolean now_sending = false;

    public static String Dir_Path = null;

    public static  int user_count = 0;

    public static int image_compress = 80;
    public static int max_com =100;
    public  static int min_com=10;
    public static int camera_size_index =0;

    public static List<Camera.Size> sizes;

    public static  boolean internet_state = false;

    public static String photo_get_url = "https://tarzanexperience.com//insertphotoinfo.php";
    public static String health_get_url = "https://tarzanexperience.com/healthinfo.php";

}
