package xiaoxi.tv.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import xiaoxi.tv.MainActivity;
import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.AdList;
import xiaoxi.tv.bean.RollTitles;
import xiaoxi.tv.bean.Update;
import xiaoxi.tv.service.msg.IScrollState;
import xiaoxi.tv.service.msg.MarqueeToast;
import xiaoxi.tv.service.msg.TextSurfaceView;
import xiaoxi.tv.tools.ApkUpdate;

public class TVService extends Service implements Runnable, IScrollState {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private App app;
    private String tag = "TVService";

    @Override
    public void onCreate() {
        super.onCreate();
        app = (App) getApplication();
        websocket();

        regPower();

//        rollTitles();
    }
//    com.mstar.android.intent.action.SLEEP_BUTTON 睡眠键
//    com.ada.android.intent.action.STANDBY_DIALOG 待机键
//    com.ada.android.intent.action.CONTROL_BACKLIGHT  intent.putExtra("BLStatus","off");关背光 	 intent.putExtra("BLStatus","on");开背光

    private void regPower() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(App.SLEEP_BUTTON);
        filter.addAction(App.STANDBY_DIALOG);
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (App.SLEEP_BUTTON.equals(intent.getAction())) {
                sleep();
            } else if (App.STANDBY_DIALOG.equals(intent.getAction())) {
                standby();
            }

        }
    };

    private void standby() {
        String url = App.requrl("getWelComeAd", "&adType=2");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Log.e(tag, json);
                final AJson<Update> data = App.gson.fromJson(
                        json, new TypeToken<AJson<Update>>() {
                        }.getType());
                if (200 == data.getCode() || 0 == data.getCode()) {
                    if (null != data.getData()) {

                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TVService.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                5 * 1000,//链接超时时间
                0,//重新尝试连接次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        App.queue.add(request);

    }

    private void sleep() {
        String url = App.requrl("getWelComeAd", "&adType=2");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Log.e(tag, json);
                final AJson<Update> data = App.gson.fromJson(
                        json, new TypeToken<AJson<Update>>() {
                        }.getType());
                if (200 == data.getCode() || 0 == data.getCode()) {
                    if (null != data.getData()) {

                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TVService.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                5 * 1000,//链接超时时间
                0,//重新尝试连接次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        App.queue.add(request);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private Socket socket;

    private List<RollTitles> rollTitles = new ArrayList<>();
    private AdList adLists;

    private void websocket() {

        try {
            socket = IO.socket(App.socketurl);
            socket.on("weather", new Emitter.Listener() {

                public void call(Object... arg0) {//天气更新
                    // TODO Auto-generated method stub
                    try {
                        final String json = arg0[0].toString();
                        Log.e(tag + "---" + "weather", json);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            socket.on("in_play", new Emitter.Listener() {

                public void call(Object... arg0) {//即时插播
                    // TODO Auto-generated method stub
                    try {
                        String json = arg0[0].toString();
                        Log.e(tag + "---" + "rollTitles", json);
//                        rollTitles = new ArrayList<>(Arrays.asList(App.gson.fromJson(json, RollTitles[].class)));
//                        currentmsg = 0;
//                        handler.post(TVService.this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                public void call(Object... arg0) {
                    // TODO Auto-generated method stub
                    try {
                        Log.e(tag, "Socket连接成功----online");
                        register();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                public void call(Object... arg0) {
                    // TODO Auto-generated method stub
                    Log.e(tag, "Socket断开连接-----online");
                }

            });
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                public void call(Object... arg0) {
                    // TODO Auto-generated method stub
                    Log.e(tag, "Socket连接失败-----online");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.connect();
    }

    //    跑马灯：rollTitles
    //            返回数据类型
//    List<RollTitlesEntity> rollTitles
    public int currentmsg;
    private TextSurfaceView Text;
    private MarqueeToast toast;

    public void rollTitles() {
//        try {
//            if (rollTitles != null && !rollTitles.isEmpty()) {
//                System.out.println("开始跑马灯");
//                if (rollTitles.size() <= currentmsg)
//                    currentmsg = 0;
//                if (System.currentTimeMillis() > rollTitles.get(currentmsg).getEndtime()) {
//                    rollTitles.remove(currentmsg);
//                    currentmsg = 0;
//                    handler.post(this);
//                    return;
//                }


        if (toast != null)
            toast.hid();
        toast = new MarqueeToast(getApplicationContext());
        Text = new TextSurfaceView(getApplicationContext(), this);
        Text.setOrientation(1);
//                Text.setFontSize(rollTitles.get(currentmsg).getSize());
//                Text.setSpeed((int) ((101 - rollTitles.get(currentmsg).getSpeed()) * 0.3));
//                Text.setSpeed(30);
//                Text.setBackgroundColor(Color.parseColor(rollTitles.get(currentmsg).getColor().replace("#", "#99")));
//                toast.setHeight(rollTitles.get(currentmsg).getSize());
//                Text.setFontColor(rollTitles.get(currentmsg).getColor());

//                if (rollTitles.get(currentmsg).getContent().equals("")
//                        && rollTitles.get(currentmsg).getContent() == null) {
//                    Text.setBackgroundColor(Color.TRANSPARENT);
//                    Text.setContent("");
//                } else {
//                    Text.setContent(rollTitles.get(currentmsg).getContent());
////                    Text.setContent("《" + rollTitles.get(currentmsg).getName() + "》\t" + rollTitles.get(currentmsg).getContent());
//                }
        if (app.getTest() != null) {
            toast.setHeight(app.getTest().getHeight());
        }

        Text.setContent("666");
        Text.setTest(app.getTest());
        toast.setView(Text);
        toast.setGravity(Gravity.TOP | Gravity.LEFT, 1920, 0, 0);
        toast.show();
//        currentmsg++;
//            } else {
//                System.out.println("跑马灯没了");
//                if (toast != null) {
//                    toast.hid();
//                    toast = null;
//                }
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }

    }

    //    register
//    参数：String mac
    private void register() {
        socket.emit("register", App.mac);
    }

    //    提醒续费：
//    发送数据：
//    warning  String类型（直接显示出来，并且给个确认键让用户确认）
    private AlertDialog.Builder builder;
    AlertDialog ad;

    private void warning(String json) {
//        Toast.makeText(MyService.this, json, Toast.LENGTH_SHORT).show();
        builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(json)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        ad = builder.create();
        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        ad.show();
    }

    @Override
    public void start() {

    }

    private static final long SHOW_MSG_PERIOD = 1L * 10L * 1000L;
    private Handler handler = new Handler();

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        Text.setLoop(false);
        Looper.prepare();
        handler.postDelayed(this, SHOW_MSG_PERIOD);
        Looper.loop();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        rollTitles();
    }

}
