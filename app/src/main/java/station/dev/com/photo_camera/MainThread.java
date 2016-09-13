package station.dev.com.photo_camera;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.audiofx.BassBoost;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Develop on 5/14/2016.
 */
public class MainThread extends Thread{
    private static MainThread mainthread;

    public Handler mhandler;
    public Context context;
    public UserState user_info;

    public BroadcastMsg  broadcast_msg;

    public FrameLayout preview;
    public CameraPreview mPreview;

//Taking four photos
    public PhotoGroup taking_photo_group;

    public ArrayList<UserState> share_app_list = new ArrayList<UserState>();

    public Queue<PhotoGroup> queue_photogroup = new LinkedList<PhotoGroup>();

    public static void getNewMainThread(){
        mainthread = new MainThread();
    }

    UploadImage img_upload_thread;

    public static MainThread getInstance() {
        return mainthread;
    }




    private MainThread() {
        broadcast_msg = new BroadcastMsg();
        listen_server = new ListenServer();
        user_info = new UserState();
        take_photo = new TakePhoto();

        img_upload_thread = new UploadImage();

        take_photo.getCameraParam();

        netstatefirst = false;
        netstatethird = false;
        netstatethirdsec = false;
    }

    public void SetCameraSettings(){
        Camera mCamera = take_photo.mCamera;
        try {
            Camera.Parameters parameters = mCamera.getParameters();

        //    parameters.setExposureCompensation(SettingsValue.exposure);

        //    parameters.setPictureSize(SettingsValue.sizes.get(SettingsValue.camera_size_index).width, SettingsValue.sizes.get(SettingsValue.camera_size_index).height);

           /* parameters.set("mode", "m");

            parameters.set("aperture", SettingsValue.apertureString[SettingsValue.aperture]); //can be 28 32 35 40 45 50 56 63 71 80 on default zoom

            parameters.set("shutter-speed", SettingsValue.shutter_speed); // depends on camera, eg. 1 means longest

            if (SettingsValue.cam_autofocus) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            } else {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            }
            */


          //  mCamera.setParameters(parameters);
        }
        catch ( Exception e){
            e.printStackTrace();
            Log.i(MUtils.TAG, "camera settings save" + e.getMessage());
        }

    }

    //For the controller app
    Socket sock_controller;
    public void sendReplyToController(int reply){
        OutputStream data_out = null;
        if(netstatefirst){
            try {
                byte[] data = new byte[3];
                byte[] snd_data = MUtils.MakePacket(data, (byte) reply, 0);


                if (data_out == null) data_out = sock_controller.getOutputStream();

                data_out.write(snd_data);

            }
            catch (Exception e){
                e.printStackTrace();
                Log.i(MUtils.TAG, "send Reply to controller ex:" + e.getMessage());
            }


        }



    }



    //For Take the photo
    TakePhoto take_photo;


    //For UI widget
    public CheckBox chk_netstatfirst, chk_netstatethird, chk_netstatestationsec, chk_internet;
    public  TextView txt_timestate;
    public Boolean netstatefirst, netstatethird , netstatethirdsec;
    public int con_first =0, con_third =0, con_third_sec = 0;



    //For send img to the station3
    Socket sock_shareapp;

    public void sendImageToShareApp(PhotoGroup photoGroup, Socket sock){

        ArrayList<String>img_list = photoGroup.img_path_list;

        int count = img_list.size();
        for(int i=0; i< count ;i++) {
            String img_path = img_list.get(i);
            File img = new File(img_path);
            FileInputStream fin;
            OutputStream data_out = null;
            try {

                fin = new FileInputStream(img);
                if (data_out == null) data_out = sock.getOutputStream();
                byte[] img_name = img.getName().getBytes();
                byte[] snd_data = MUtils.MakePacket(img_name, (byte) GlobalVar.IMG_NAME.getValue(), img_name.length);

                data_out.write(snd_data);

                int file_size = (int) img.length();


                byte[] sndData = new byte[5];

                sndData[4] = (byte) GlobalVar.SEND_IMG.getValue();
                System.arraycopy(MUtils.intToByteArray(file_size + 5), 0, sndData, 0, 4);

                data_out.write(sndData);

                int read_byte;
                byte[] buffer = new byte[1024];

                while (file_size > 0) {

                    read_byte = fin.read(buffer);
                    file_size -= read_byte;

                    data_out.write(buffer, 0, read_byte);


                }


                fin.close();
                //data_out.close();


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("DebugPoc", "ImageFileSend  :: readfile exception ex:" + e.getMessage());

                return;
            }


        }

        photoGroup.send_share_state = true;

    }


    public void UpdateUI(){
         mhandler.post(new Runnable() {
             @Override
             public void run() {
                 try {
                     chk_netstatfirst.setChecked(netstatefirst);
                     chk_netstatethird.setChecked(netstatethird);
                     chk_internet.setChecked(SettingsValue.internet_state);
                     txt_timestate.setText("Time:" + take_photo.count + "(s)");
                     chk_netstatestationsec.setChecked(netstatethirdsec);

                 }
                 catch (Exception e){
                     e.printStackTrace();
                 }
             }
         });

    }


    ListenServer listen_server;

    CheckConnection checkConnection;

    public void run(){

        Log.i("DebugPoc", "Mainthread run");

        InitDatabase();

        broadcast_msg.context = context;
        broadcast_msg.startBroadCast();
        Thread server = new Thread(listen_server);
        server.start();

        //For the check.
        //take_photo.startTakePhoto();

        //
        Thread Image_UploadFTp = new Thread(img_upload_thread);
        Image_UploadFTp.start();

        new Thread(new HealthTask()).start();

        checkConnection= new CheckConnection();
        new Thread(checkConnection).start();
    }

    public void InitParam() {
        SettingsValue.Dir_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PocImage";
        File file = new File(SettingsValue.Dir_Path);

        if(!file.exists()){
            file.mkdir();
        }

        File conf_file = new File(file, "conf.ini");

        byte[] data = new byte[5];


        if(!conf_file.exists()){
            System.arraycopy(MUtils.intToByteArray(0),0, data, 0, 4);

            try {
                FileOutputStream fos = new FileOutputStream(conf_file);
                fos.write(data,0,4);
                fos.close();
            }catch (Exception e){

            }

        }
    }






    public void ReadParam(){
        byte[] data = new  byte[5];
        File conf_file = new File(SettingsValue.Dir_Path, "conf.ini");
        try{
            FileInputStream fin = new FileInputStream(conf_file);
            fin.read(data);
            SettingsValue.gruop_id = MUtils.byteArrayToInt(data);
        }catch (Exception e){

        }
    }

    public void SaveParam(){
        File conf_file = new File(SettingsValue.Dir_Path, "conf.ini");

        byte[] data = new byte[5];


        System.arraycopy(MUtils.intToByteArray(SettingsValue.gruop_id), 0, data, 0, 4);

        try {
            FileOutputStream fos = new FileOutputStream(conf_file);
            fos.write(data,0,4);
            fos.close();
        }catch (Exception e){

        }

    }

    public void addPhotoGroupToQueue(){
        taking_photo_group.send_share_state = false;
        taking_photo_group.gruop_id = SettingsValue.gruop_id++;

        SaveParam();

        CyfeInfo cyfeInfo = new CyfeInfo();
        cyfeInfo.count = 4;
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyyMMdd");
        Date date= new Date();

        String newDateStr = curFormater.format(date);

        String tmp_path  = taking_photo_group.img_path_list.get(0);
        File tmpfile = new File(tmp_path);

        cyfeInfo.date = newDateStr;
        cyfeInfo.time = new SimpleDateFormat("hh:mm:ss").format(date);
        cyfeInfo.img_path = "http://" + SettingsValue.int_url +"/" +SettingsValue.directoryName + "/"+tmpfile.getName();

        SaveDatabase(cyfeInfo);

        SendCyInfo sendCyInfo = new SendCyInfo();
        sendCyInfo.cyfeInfo = cyfeInfo;

        new Thread(sendCyInfo).start();

        queue_photogroup.add(taking_photo_group);

        int pic_count = taking_photo_group.img_path_list.size();
        for(int i=0; i<pic_count ;i++){
            SettingsValue.upload_phtogroup.add(taking_photo_group.img_path_list.get(i));

        }

    }

    public void sendControllerState(boolean state){
        int share_app_count =share_app_list.size();

        int command = state? GlobalVar.CONTROLLER_ONLINE.getValue():GlobalVar.CONTROLLER_OFFLIEN.getValue();

        for (int j = 0; j < share_app_count; j++) {
            UserState muser_info = share_app_list.get(j);



            OutputStream data_out = null;
            try {

                if (data_out == null) data_out = muser_info.socket.getOutputStream();

                byte[] mdata = new byte[10];

                byte[] snd_data = MUtils.MakePacket(mdata, (byte) command, 0);

                data_out.write(snd_data);

            }catch (Exception e){
                return;
            }


        }
    }

    public void sendPhotoGroupToShareApp(){
        if(share_app_list.size() == 0) return;
        ArrayList<Socket> error_socket = new ArrayList<Socket>();

        int share_app_count = share_app_list.size();
        //Sending.
        int photo_groups_count = queue_photogroup.size();

        for(int i=0; i<photo_groups_count; i++) {
            PhotoGroup photoGroup = queue_photogroup.peek();
            for (int j = 0; j < share_app_count; j++) {

                OutputStream data_out = null;
                UserState muser_info;
                try {
                    muser_info = share_app_list.get(j);


                    try {
                        if (data_out == null) data_out = muser_info.socket.getOutputStream();

                        byte[] mdata = new byte[200];

                        byte[] path = SettingsValue.directoryName.getBytes();
                        int path_len = path.length;

                        System.arraycopy(MUtils.intToByteArray(photoGroup.gruop_id),0, mdata,0,4);  //Group ID
                        System.arraycopy(MUtils.intToByteArray(path_len),0, mdata,4, 4);
                        System.arraycopy(path,0, mdata,8, path_len);

                        byte[] snd_data = MUtils.MakePacket(mdata, (byte) GlobalVar.SEND_GROUPPHOTO.getValue(), path_len+8);

                        data_out.write(snd_data);

                        sendImageToShareApp(photoGroup, muser_info.socket);
                    }catch (Exception e){
                        error_socket.add(muser_info.socket);
                    }

                }catch (Exception e){

                }


            }
            if(photoGroup.send_share_state){
                try {
                    queue_photogroup.remove();
                }catch (Exception e){

                }
            }
            else break;  //If can't sent the images to share
        }

        int er_count = error_socket.size();
        for(int i=0; i<er_count; i++){
            Socket sock = error_socket.get(i);
            try{
                sock.close();
            }catch (Exception e){

            }
        }
        SettingsValue.now_sending = false;
    }

    public void UpdateShareAppList(){
        int user_count = share_app_list.size();
        int del_count = 0;
        for(int i = 0 ; i < user_count; i++){
            int index = i - del_count;
            UserState userState = share_app_list.get(index);

            if(userState.networkstate == false){
                del_count++;
                share_app_list.remove(index);
            }
        }
    }

    String External_Path;
    DBHelper img_db;

    public void InitDatabase(){
        External_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"PocImage";

        File dir = new File(External_Path);
        if(!dir.exists())
            dir.mkdir();


        img_db = new DBHelper(context);



    }

    public void ReadDatabase(){


    }


    public void SaveDatabase(CyfeInfo cyfeinfo){
        img_db.insertPhotoInfo(cyfeinfo.count, cyfeinfo.date,cyfeinfo.time);

    }
}
