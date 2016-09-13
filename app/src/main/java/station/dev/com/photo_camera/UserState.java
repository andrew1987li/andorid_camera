package station.dev.com.photo_camera;

import java.net.Socket;

/**
 * Created by Develop on 5/14/2016.
 */
public class UserState {
    public String userName;
    public String password;
    public String company;
    public Boolean networkstate;
    public  CBuffer userBuffer;
    public  int count;
    public int station;
    public Socket socket;

    public UserState(){
        userName = "PocUser";
        password = "PocPassword";
        networkstate = false;
        company = "Poc.co.hk";
        userBuffer = new CBuffer();
        count = 0;
    }


}
