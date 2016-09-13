package station.dev.com.photo_camera;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Develop on 5/14/2016.
 */
public class CommunicationThread implements Runnable {

        private Socket clientSocket;

        public UserState user_info;

        private InputStream data_input = null;
        private OutputStream data_output = null;

    Timer ping_timer;
    TimerTask ping_task;


    public void startTimer(){
        stopTimer();

        ping_timer = new Timer();
        ping_task = new TimerTask() {
            @Override
            public void run() {
                if(SettingsValue.now_sending)return;
                SettingsValue.now_sending =true;
                byte[] data = new byte[5];
                byte[]sndData = MUtils.MakePacket(data,(byte)GlobalVar.COMMAND_PING.getValue(), 0);

                try{
                    Log.i("DebugPoc", "Ping send");
                    data_output.write(sndData);
                }catch (Exception e){
                    Log.i("DebugPoc", "Ping error");
                    stopTimer();
                    try {
                        clientSocket.close();
                    }catch (Exception ee){

                    }
                }
                Log.i("DebugPoc", "Ping sent");
                SettingsValue.now_sending = false;
            }
        };

        ping_timer.schedule(ping_task,0,60000);
    }

    public void stopTimer(){
        if(ping_timer != null){
            try {
                ping_timer.cancel();
            }catch ( Exception e){

            }
            ping_timer =null;
        }

    }


    public CommunicationThread(Socket csocket){

            clientSocket = csocket;

            try{
                clientSocket.setSoTimeout(720000);
            }catch (Exception e){
                Log.i("DebugPoc","Try so timeout set");
            }

            user_info = new UserState();

            user_info.userBuffer.FormatBuffer();
            user_info.networkstate = true;

            try{
                data_input = clientSocket.getInputStream();
                data_output = clientSocket.getOutputStream();
            }catch (Exception e){

            }

            startTimer();
        }

        public void processUserPacket(byte[]data){
            user_info.station = data[0];
            user_info.socket = clientSocket;
            if(user_info.station == GlobalVar.STATION1.getValue()){
                MainThread.getInstance().con_first ++;
                MainThread.getInstance().sock_controller = clientSocket;
                MainThread.getInstance().netstatefirst = true;
                MainThread.getInstance().sendControllerState(true);
            }else if(user_info.station == GlobalVar.SHARE_FIRST.getValue()){
                MainThread.getInstance().con_third ++;
                MainThread.getInstance().sock_shareapp = clientSocket;
                MainThread.getInstance().netstatethird = true;
                MainThread.getInstance().share_app_list.add(user_info);
                MainThread.getInstance().sendPhotoGroupToShareApp();
            }
            else if(user_info.station == GlobalVar.SHARE_SECOND.getValue()){
                MainThread.getInstance().con_third_sec ++;
                MainThread.getInstance().sock_shareapp = clientSocket;
                MainThread.getInstance().netstatethirdsec = true;
                MainThread.getInstance().share_app_list.add(user_info);
                MainThread.getInstance().sendPhotoGroupToShareApp();
            }
            MainThread.getInstance().UpdateUI();
        }

        public void processTakePhotoPacket(byte[] data){
            int delay = MUtils.byteArrayToInt(data);
            MainThread.getInstance().take_photo.startTakePhoto(delay);
        }

        public void processPacket(byte[] packet){
            int data_len = MUtils.byteArrayToInt(packet) -5;
            int command = packet[4];


            byte[] data = new byte[data_len];
            System.arraycopy(packet,5, data, 0, data_len);

            if(command == GlobalVar.USER_CONNECT.getValue()){
                processUserPacket(data);
            }
            else if (command == GlobalVar.TAKE_PHOTO.getValue()){
                //For start timer command.
                processTakePhotoPacket(data);
             }
            else if(command == GlobalVar.STOP_PHOTO.getValue()){
                MainThread.getInstance().take_photo.stopTakePhoto();
            }

        }

        @Override
        public void run() {
            MainThread.getInstance().UpdateUI();





            byte[] buffer = new byte[2048];

            while(true){
                try{
                   int read_byte =  data_input.read(buffer);

                    if (read_byte < 1){

                        user_info.networkstate = false;
                        break;
                    }

                    user_info.userBuffer.addData(buffer, read_byte);

                    byte[] packet;
                    while(true){
                        packet = user_info.userBuffer.getPacket();
                        if(packet == null) break;
                        processPacket(packet);
                    }
                }
                catch (Exception e){
                    user_info.networkstate = false;
                    try {
                        clientSocket.close();
                    }catch (Exception ee){

                    }
                    Log.i("DebugPoc", "reading msg : ex" + e.getMessage());
                    break;
                }

            }
            stopTimer();

            if(user_info.station == GlobalVar.STATION1.getValue()){
                int con = --MainThread.getInstance().con_first;
                MainThread.getInstance().netstatefirst = (con> 0)?true:false;
                if(con ==0 )MainThread.getInstance().sendControllerState(false);
              //  MainThread.getInstance().take_photo.stopTakePhoto();
            }else if(user_info.station == GlobalVar.SHARE_FIRST.getValue()){
                int con = --MainThread.getInstance().con_third;
                //MainThread.getInstance().data_out = null;
                MainThread.getInstance().netstatethird = (con> 0)?true:false;
                MainThread.getInstance().UpdateShareAppList();
            }else if(user_info.station == GlobalVar.SHARE_SECOND.getValue()){
                int con = --MainThread.getInstance().con_third_sec;
                //MainThread.getInstance().data_out = null;
                MainThread.getInstance().netstatethirdsec = (con> 0)?true:false;
                MainThread.getInstance().UpdateShareAppList();
            }
            MainThread.getInstance().UpdateUI();
        }

}
