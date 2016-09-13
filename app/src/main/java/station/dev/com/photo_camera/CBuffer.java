package station.dev.com.photo_camera;

/**
 * Created by Develop on 5/14/2016.
 */

public class CBuffer {
    int len;
    int data_len;
    byte[] cdata;


    public void FormatBuffer(){
        data_len = 0;
        len = 4096;

        cdata = new byte[len];

    }

    public CBuffer(){
        FormatBuffer();
    }

    public void addData(byte[]data, int dlen){

        int new_len =  data_len + dlen;

        byte[] tmp_data;
        if(new_len > len){
            len *= 2;
            tmp_data = new byte[len];
            System.arraycopy(cdata, 0, tmp_data, 0, len/2);
            cdata = tmp_data;
        }

        System.arraycopy(data, 0, cdata, data_len, dlen);
        data_len = new_len;

    }

    public int getPacketLen(){

        if(data_len<4) return -1;
        else return MUtils.byteArrayToInt(cdata);
    }

    public byte[] getPacket(){
        int pac_len = getPacketLen();
        if(pac_len == -1) return  null;

        if(pac_len > data_len) return  null;

        byte[] packet = new byte[pac_len];

        System.arraycopy(cdata, 0, packet, 0, pac_len);


        data_len -= pac_len;

        int new_len = (data_len < len/2 && len>4096)? len/2: len;

        byte[] tmp_data = new byte[new_len];
        System.arraycopy(cdata, pac_len, tmp_data,0, data_len);
        cdata = tmp_data;
        len = new_len;

        return  packet;

    }

}
