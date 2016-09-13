package station.dev.com.photo_camera;

/**
 * Created by Develop on 5/14/2016.
 */
public enum GlobalVar {
    SHARE_FIRST(3),
    SHARE_SECOND(4),

    //COMMAND
    UDP_VERIFY(10),
    USER_CONNECT(11),
    TAKE_PHOTO(12),
    SEND_IMG(13),
    IMG_NAME(14),
    STOP_PHOTO(15),
    REPLY_PHOTOTAKEN(16),
    REPLY_SHAREAPPOFFLINE(17),
    SEND_GROUPPHOTO(18),

    CONTROLLER_ONLINE(40),
    CONTROLLER_OFFLIEN(41),

    COMMAND_PING(50),

    //STATION
    STATION1(30),
    STATION2(31),


    //Network config
    SERVER_PORT(50010);




    private GlobalVar(int val){
        value = val;
    }

    private int value;

    public int getValue(){
        return value;
    }



}
