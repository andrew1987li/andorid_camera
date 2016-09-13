package station.dev.com.photo_camera;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Develop on 5/14/2016.
 */
public class ListenServer implements Runnable {

    public ServerSocket serverSocket;
    int server_port = GlobalVar.SERVER_PORT.getValue();

    public  void startServerSocket(){
        Socket socket;
        boolean server_state = false;

        while (server_state == false) {

            try {

                serverSocket = new ServerSocket(server_port);

                server_state = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

               Thread.sleep(3000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        while(!Thread.currentThread().isInterrupted()){
            try{
                 socket = serverSocket.accept();

                CommunicationThread commThread = new CommunicationThread(socket);
                new Thread(commThread).start();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        startServerSocket();
    }

}
