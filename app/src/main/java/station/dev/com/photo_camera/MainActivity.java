package station.dev.com.photo_camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {


//For settings screen. Tap 4 times.
    TextView txt_settings ,txt_usercount;
    ImageView img_background;

    Button bt_plus, bt_minus, bt_takephoto;

    private  int touch_count =0 ;
    boolean touch_setting = false;

    Timer timer;
    TimerTask timerTask;

    public boolean is_running = false;

    public class SendImagesToShare extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            if(SettingsValue.now_sending == false) {
                SettingsValue.now_sending = true;
                MainThread.getInstance().sendPhotoGroupToShareApp();

            }
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

    public void ResizeBmp(String path){
        Paint new_paint = new Paint();

        File dir= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Resize");

        if(!dir.exists()) dir.mkdir();

        Bitmap b= BitmapFactory.decodeFile(path);
        Bitmap out = Bitmap.createScaledBitmap(b, 320, 480, false);

        File file = new File(dir, "resize.png");
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SettingsValue.REQUEST_PHOTO) {

            String img_path = data.getStringExtra("file_path");
            //Toast.makeText(this, "Return Photo:" + img_path,Toast.LENGTH_SHORT).show();
            Log.i("DebugPoc", img_path);

            img_path =ProcessingBitmap(img_path, "overlay.png");

            Log.i("DebugPoc", img_path);

            MainThread.getInstance().taking_photo_group.img_path_list.add(img_path);
            SettingsValue.photo_count++;
            if(SettingsValue.photo_count == 4){
                SettingsValue.now_taking_photo = false;
                MainThread.getInstance().addPhotoGroupToQueue();
                new SendImagesToShare().execute();
            }
            else{
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent setting_activity = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivityForResult(setting_activity, SettingsValue.REQUEST_PHOTO);
                    }
                }, SettingsValue.interval_photo * 1000);
            }
        }
        else if(resultCode == RESULT_CANCELED && requestCode == SettingsValue.REQUEST_PHOTO){
            SettingsValue.now_taking_photo = false;
        }

    }


    protected PowerManager.WakeLock mlock;

    @Override
    public void onDestroy(){
        mlock.release();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mlock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
        mlock.acquire();

        MFont.context = this;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (SettingsValue.photo_start) {
                    SettingsValue.photo_start = false;

                    Intent setting_activity = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivityForResult(setting_activity, SettingsValue.REQUEST_PHOTO);

                }
            }
        }, 0, 1000);

        img_background = (ImageView)findViewById(R.id.img_background);
        img_background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int actionPeformed = event.getAction();

                switch (actionPeformed) {
                    case MotionEvent.ACTION_DOWN: {
                        touch_setting = false;
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {

                        break;
                    }
                }

                return true;
            }
        });

        txt_settings = (TextView)findViewById(R.id.txt_setting);

        txt_settings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int actionPeformed = event.getAction();

                switch (actionPeformed) {
                    case MotionEvent.ACTION_DOWN: {
                        if (touch_setting == false) touch_count = 0;

                        if (touch_count == 0) {
                            if (timer != null) {
                                try {
                                    timer.cancel();
                                } catch (Exception e) {
                                    Log.i(MUtils.TAG, "touch timer cancel ex:" + e.getMessage());
                                }
                            }

                            timer = new Timer();
                            timerTask = new TimerTask() {
                                @Override
                                public void run() {

                                    touch_count = 0;
                                }
                            };

                            timer.schedule(timerTask, 2000);
                        }

                        touch_setting = true;

                        touch_count++;
                        //Toast.makeText(getApplicationContext(), "Touch Test"+touch_count, Toast.LENGTH_SHORT).show();

                        if (touch_count == 4) {
                                      /*  mhandler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                            }
                                        });*/
                            touch_setting = false;
                            Intent setting_activity = new Intent(getApplicationContext(), SettingsActiviy.class);
                            startActivity(setting_activity);
                        }

                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {

                        break;
                    }
                }

                return true;

            }
        });

        txt_usercount =(TextView)findViewById(R.id.txt_usercount);

        bt_plus = (Button)findViewById(R.id.bt_plus);
        bt_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsValue.user_count++;
                txt_usercount.setText("" + SettingsValue.user_count);
            }
        });

        bt_minus = (Button)findViewById(R.id.bt_minus);
        bt_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingsValue.user_count == 1) {
                    return;
                }
                SettingsValue.user_count--;
                txt_usercount.setText("" + SettingsValue.user_count);
            }
        });


        bt_takephoto = (Button)findViewById(R.id.bt_take_photo);
        bt_takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainThread.getInstance().take_photo.startTakePhoto(0);
                CyfeInfo cyfeInfo = new CyfeInfo();
                cyfeInfo.user_count = SettingsValue.user_count;
                cyfeInfo.experience = 10;
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyyMMdd");
                Date date= new Date();

                String newDateStr = curFormater.format(date);

                cyfeInfo.date = newDateStr;
                cyfeInfo.time = new SimpleDateFormat("hh:mm:ss").format(date);

                SendUserCyInfo sendUserCyInfo = new SendUserCyInfo();
                sendUserCyInfo.cyfeInfo = cyfeInfo;
                new Thread(sendUserCyInfo).start();


            }
        });

        if(SettingsValue.is_running == false) {
            SettingsValue.is_running =true;
            MainThread.getNewMainThread();

            MainThread.getInstance().context = this;

            MainThread mainThread = MainThread.getInstance();


            mainThread.chk_netstatfirst = (CheckBox) findViewById(R.id.chk_stationfirst);
            mainThread.chk_netstatethird = (CheckBox) findViewById(R.id.chk_stationthird);
            mainThread.chk_netstatestationsec =(CheckBox)findViewById(R.id.chk_stationthirdsec);
            mainThread.chk_internet = (CheckBox)findViewById(R.id.chk_internet);
            mainThread.txt_timestate = (TextView) findViewById(R.id.txt_time);


            mainThread.mhandler = new Handler(Looper.getMainLooper());

            MainThread.getInstance().InitParam();
            MainThread.getInstance().ReadParam();
/*
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
*/
            if (MainThread.getInstance().isAlive() == false) {
                Log.i("DebugPoc", "onCreate MainThread");
                try {
                    MainThread.getInstance().start();
                } catch (Exception e) {
                    Log.i("DebugPoc", "ex:" + e.getMessage());
                }
            }
        }
        else {
            MainThread.getInstance().context = this;

            MainThread mainThread = MainThread.getInstance();


            mainThread.chk_netstatfirst = (CheckBox) findViewById(R.id.chk_stationfirst);
            mainThread.chk_netstatethird = (CheckBox) findViewById(R.id.chk_stationthird);
            mainThread.chk_netstatestationsec =(CheckBox)findViewById(R.id.chk_stationthirdsec);
            mainThread.chk_internet = (CheckBox)findViewById(R.id.chk_internet);
            mainThread.txt_timestate = (TextView) findViewById(R.id.txt_time);
        }
    }

    @Override
    public void onBackPressed(){
        try {
            MainThread.getInstance().interrupt();
            MainThread.getInstance().listen_server.serverSocket.close();
            MainThread.getInstance().take_photo.mCamera.release();
        }
        catch (Exception e){
            Log.i("DebugPoc","ex:" + e.getMessage());
            e.printStackTrace();

        }
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);

        Log.i("DebugPoc", "Kill App");
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        SettingsValue.is_running = true;
        savedInstanceState.putBoolean("MyBoolean", true);
        Log.i("Station", "State Saved in Main");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        Log.i("Station", "State Restored in Main");
        SettingsValue.is_running = true;
        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
        if(myBoolean){
            Toast.makeText(this,"Reloading activity", Toast.LENGTH_SHORT);
        }

    }

    public String ProcessingBitmap(String src, String overlay) {
        //path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PocImage/overlay.png";
        //path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PocImage/IMG_20160523_202358.jpg";
        String overlay_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PocImage/overlay.png";
        String path = MUtils.getNewImagePath();
        Bitmap bm1 = null;
        Bitmap bm2 = null;
        Bitmap new_bmp = null;
        Bitmap tmp;


        try {
          /*  bm1 = BitmapFactory.decodeFile(path2);
            bm2 = BitmapFactory.decodeFile(path1);
            */
            //bm1 = MUtils.decodeSampledBitmapFromFile(src, 1920, 1080);
            //bm2 =BitmapFactory.decodeFile(overlay_path);
            Log.i("DebugPoc", "DecodeFile" + src);

          /*  bm1 = BitmapFactory.decodeFile(src);

            Log.i("DebugPoc", "DecodeFile Scaling");

            tmp = Bitmap.createScaledBitmap(bm1, 1920,1080,false);
            */
            bm1 = MUtils.decodeSampledBitmapFromFile(src, 1920, 1080);

            bm2 = MUtils.decodeSampledBitmapFromFile(overlay_path,1920,1080);
            int w=1920, h=1080;
            //w = (bm1.getWidth() > bm2.getWidth()) ? bm1.getWidth() : bm2.getWidth();
            //h = (bm1.getHeight() > bm2.getHeight()) ? bm1.getHeight() : bm2.getHeight();

            //Bitmap.Config config = tmp.getConfig();
            Bitmap.Config config = bm1.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }

            new_bmp = Bitmap.createBitmap(w, h, config);
            Canvas newCanvas = new Canvas(new_bmp);

                //newCanvas.drawBitmap(tmp, 0, 0, null);
            newCanvas.drawBitmap(bm1, 0, 0, null);


            /*Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

             paint.setXfermode(null);

            Paint paint = new Paint();
            paint.setAlpha(160);
            newCanvas.drawBitmap(bm2, 0, 0, null);
                paint.setFilterBitmap(false);

    // Color
            paint.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));

            int ar = Color.argb(0xFF, 255, 255, 255);
            PorterDuff.Mode mode = android.graphics.PorterDuff.Mode.ADD;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColorFilter(new PorterDuffColorFilter(ar, mode));
            paint.setColor(ar);

            Paint paint= new Paint();
            paint.setFilterBitmap(false);

            // Color
            paint.setColorFilter(new PorterDuffColorFilter(ar, PorterDuff.Mode.MULTIPLY));
             */

            try {
                newCanvas.drawBitmap(bm2, 0, 0, null);
            }catch (Exception e){
                Log.i("DebugPoc", "drawing overlay ex:" + e.getMessage());
            }
           /* Canvas tempCanvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            tempCanvas.drawBitmap(original, 0, 0, null);
            tempCanvas.drawBitmap(mask, 0, 0, paint);
            paint.setXfermode(null);

            //Draw result after performing masking
            canvas.drawBitmap(result, 0, 0, new Paint());
            */
            OutputStream os = null;
            try {
                os = new FileOutputStream(path);
                new_bmp.compress(Bitmap.CompressFormat.JPEG, SettingsValue.image_compress, os);  //SettingsValue.compress
            } catch (IOException e) {
                Log.e("combineImages", "problem combining images", e);
            }

        } catch (Exception e) {
            Log.i("DebugPoc", "Processing overlay ex:" + e.getMessage());
            e.printStackTrace();
        }
        return path;
    }


    public class SendUserCyInfo implements Runnable {

        public CyfeInfo cyfeInfo;

        protected String sendInfo(CyfeInfo param) {

            CyfeInfo tmp_info;

            tmp_info = param;

            String jsonString = String.format("{\"data\":[{\"Date\":\"%s\",\"Users\":%d,\"Experience\":%d}]}", tmp_info.date, tmp_info.user_count, tmp_info.experience);
            sendCyfe(jsonString);

            return null;
        }



        public  String endpoint = "https://app.cyfe.com/api/push/574b1bb4550922251062312233149";

        public HttpResponse sendCyfe(String jsonObject){
            //instantiates httpclient to make request
            DefaultHttpClient httpclient = new DefaultHttpClient();

            //url with the post data
            HttpPost httpost = new HttpPost(endpoint);

            //convert parameters into JSON object
            /// JSONObject holder = getJsonObjectFromMap(params);

            //passes the results to a string builder/entity
            try {
                StringEntity se = new StringEntity(jsonObject.toString());

                //sets the post request as the resulting string
                httpost.setEntity(se);
                //sets a request header so the page receving the request
                //will know what to do with it
                httpost.setHeader("Accept", "application/json");
                httpost.setHeader("Content-type", "application/json");

                //Handles what is returned from the page
                ResponseHandler responseHandler = new BasicResponseHandler();
                HttpResponse response =  httpclient.execute(httpost);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));

                // NEW CODE
                String line;
                while((line =in.readLine())!=null) {

                    Log.i("DebugPoc", line);
                }
            }
            catch (Exception e){
                return null;
            }
            return null;
        }


        @Override
        public void run() {

            sendInfo(cyfeInfo);
        }
    }

}
