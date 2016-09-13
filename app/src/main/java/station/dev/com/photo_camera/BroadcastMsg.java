package station.dev.com.photo_camera;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Develop on 5/14/2016.
 */
public class BroadcastMsg {
    public  Timer timer;
    public TimerTask timerTask;

    public Context context;
    public int server_port;
    public UserState user_info;

    DatagramSocket socket;

    public BroadcastMsg(){
        server_port = GlobalVar.SERVER_PORT.getValue();
    }


    public InetAddress getBroadcastAddress() throws IOException{
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;

        byte[] quads = new byte[4];
        for(int k=0; k < 4 ; k++)
            quads[k] = (byte)((broadcast >> k* 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


    public void sendBroadMessage(){
        String UDP_data = "UDP_STATION2";
        byte[]tmp =  UDP_data.getBytes();
        byte[] snd_Data = MUtils.MakePacket(tmp, (byte)GlobalVar.UDP_VERIFY.getValue(), tmp.length);


        try{
            if(socket == null) {
                 socket = new DatagramSocket(server_port);
                 socket.setBroadcast(true);
            }

            DatagramPacket packet = new DatagramPacket(snd_Data,snd_Data.length, getBroadcastAddress(), server_port);

            socket.send(packet);

            /*
             If you want to listen for a response...
             byte[] buf = new byte[1024];
             DatagramPacket packet = new DatagramPacket(buf, buf.length);
             socket.receive(packet);

             */

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void startBroadCast(){
        try{
            socket = new DatagramSocket(server_port);
            socket.setBroadcast(true);

        }catch (Exception e){
            e.printStackTrace();
        }


        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                sendBroadMessage();
            }
        };

        timer.schedule(timerTask, 1000, 5000);

    }


}
