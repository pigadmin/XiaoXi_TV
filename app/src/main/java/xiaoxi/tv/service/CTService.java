package xiaoxi.tv.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import xiaoxi.tv.app.App;
import xiaoxi.tv.tools.adb;

public class CTService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private App app;
    private String tag = "CTService";

    @Override
    public void onCreate() {
        super.onCreate();
        app = (App) getApplication();
        createsocket();

    }

    private static final int PORT = 9999;
    private ServerSocket server = null;
    private Socket socket = null;

    private void createsocket() {
        // TODO Auto-generated method stub

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    if (server != null) {
                        server = null;
                    }
                    server = new ServerSocket(PORT);
                    Log.e(tag, "遥控服已启动");
                    while (true) {
                        socket = server.accept();
                        Log.e(tag, "检测到用户连接...IP:" + socket.getInetAddress().getHostAddress());
                        new SocketThread(socket).start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class SocketThread extends Thread {

        private Socket socket;

        public SocketThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            String msg;
            DataInputStream in = null;
            try {
                in = new DataInputStream(socket.getInputStream());

                while (true) {
                    if ((msg = in.readUTF()) != null) {
                        event(msg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void event(String msg) {
        // TODO Auto-generated method stub
        try {
            int keycode = Integer.parseInt(msg);
            adb.InputEvent(keycode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
