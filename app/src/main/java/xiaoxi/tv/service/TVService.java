package xiaoxi.tv.service;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.LongDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import xiaoxi.tv.MainActivity;
import xiaoxi.tv.R;
import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.AdList;
import xiaoxi.tv.bean.RollTitles;
import xiaoxi.tv.bean.WelcomeAd;
import xiaoxi.tv.service.msg.IScrollState;
import xiaoxi.tv.service.msg.MarqueeToast;
import xiaoxi.tv.service.msg.TextSurfaceView;
import xiaoxi.tv.tools.LtoDate;
import xiaoxi.tv.ui.ad.MsgInsActivity;
import xiaoxi.tv.ui.ad.NowinsActivity;
import xiaoxi.tv.ui.ad.SleepActivity;
import xiaoxi.tv.ui.ad.bean.Command;
import xiaoxi.tv.ui.ad.bean.Msg;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class TVService extends Service implements Runnable, IScrollState {
    private final int UPDATEMSG = 0;
    private final int UPDATEAD = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATEMSG:
                    getSubTitle();
                    handler.sendEmptyMessageDelayed(UPDATEMSG, App.createRn());
                    break;
                case UPDATEAD:
                    msgins();
//                    handler.sendEmptyMessageDelayed(UPDATEAD, createRn());
                    break;
            }
        }
    };

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
        initweb();

        websocket();
        regPower();

//        handler.sendEmptyMessage(UPDATEMSG);
//        handler.sendEmptyMessage(UPDATEAD);


    }


//    com.mstar.android.intent.action.SLEEP_BUTTON 睡眠键
//    com.ada.android.intent.action.STANDBY_DIALOG 待机键
//    com.ada.android.intent.action.CONTROL_BACKLIGHT  intent.putExtra("BLStatus","off");关背光 	 intent.putExtra("BLStatus","on");开背光

    private void regPower() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(App.SLEEP_BUTTON);
        filter.addAction(App.STANDBY_DIALOG);
        filter.addAction(App.STANDBY);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);

        registerReceiver(receiver, filter);
    }

    boolean issleep;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (App.STANDBY.equals(intent.getAction())) {//待机
//                    Log.e(tag, "关机关机关机");
//                    sendBroadcast(new Intent(App.STANDBY));
                } else if (App.SLEEP_BUTTON.equals(intent.getAction())) {
                    if (!issleep) {
                        sleep();//休眠
                    } else {
                        standby();//唤醒
                    }
                    issleep = !issleep;
                } else if (App.STANDBY_DIALOG.equals(intent.getAction())) {
//                    sendBroadcast(new Intent(App.STANDBY));
                    off();//关机
                } else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    handler.sendEmptyMessage(UPDATEAD);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            else if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
//
//            }

        }
    };
    private Msg msg = null;

    private void msgins() {//任务计划插播
        String url = App.requrl("msgins", "");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Log.e(tag, "msgins" + json);
                try {
                    AJson<Msg> data = App.gson.fromJson(
                            json, new TypeToken<AJson<Msg>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        if (data.getData() != null) {
                            msg = data.getData();
                            if (msg != null) {
                                long cur = System.currentTimeMillis();
                                //计划
                                long begin = msg.getBeginTime();

                                long end = msg.getEndTime();
                                Log.e(tag + "计划" + LtoDate.yMdHmE(begin) + "---" + LtoDate.yMdHmE(end), msg.getName());
                                if (cur > begin && cur < end && !app.isMsg()) {
                                    app.setMsg(true);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("key", msg);
                                    Intent week = new Intent(getApplicationContext(),
                                            MsgInsActivity.class);
                                    week.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    week.putExtras(bundle);
                                    startActivity(week);
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void standby() {//唤醒
        sendBroadcast(new Intent(App.CONTROL_BACKLIGHT).putExtra("BLStatus", "on"));
        Log.e(tag, "唤醒唤醒唤醒");
        String url = App.requrl("getWelComeAd", "&adType=2");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                Log.e(tag, json);
                try {
                    AJson<List<WelcomeAd>> data = App.gson.fromJson(
                            json, new TypeToken<AJson<List<WelcomeAd>>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        if (!data.getData().isEmpty()) {
                            Log.e(tag, "喚醒" + data.getData().size());
                            sendBroadcast(new Intent(App.STOP));
                            Bundle bundle = new Bundle();
                            bundle.putString("type", App.STANDBY_DIALOG);
                            bundle.putSerializable("key", (Serializable) data.getData());
                            Intent intent = new Intent(getApplicationContext(),
                                    SleepActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void off() {//关机
        Log.e(tag, "关机关机关机");
        String url = App.requrl("getWelComeAd", "&adType=3");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {
                    Log.e(tag, json);
                    AJson<List<WelcomeAd>> data = App.gson.fromJson(
                            json, new TypeToken<AJson<List<WelcomeAd>>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        if (!data.getData().isEmpty()) {
                            Log.e(tag, "关机关机关机" + data.getData().size());
                            sendBroadcast(new Intent(App.STOP));
                            Bundle bundle = new Bundle();
                            bundle.putString("type", App.STANDBY);
                            bundle.putSerializable("key", (Serializable) data.getData());
                            Intent intent = new Intent(getApplicationContext(),
                                    SleepActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            sendBroadcast(new Intent(App.STANDBY));
                        }
                    }
                } catch (Exception e) {
                    sendBroadcast(new Intent(App.STANDBY));
                    e.printStackTrace();
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

    private void sleep() {//休眠
        Log.e(tag, "休眠休眠休眠休眠");
        String url = App.requrl("getWelComeAd", "&adType=3");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {
                    Log.e(tag, json);
                    AJson<List<WelcomeAd>> data = App.gson.fromJson(
                            json, new TypeToken<AJson<List<WelcomeAd>>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        if (!data.getData().isEmpty()) {
                            Log.e(tag, "休眠" + data.getData().size());
                            sendBroadcast(new Intent(App.STOP));
                            Bundle bundle = new Bundle();
                            bundle.putString("type", App.SLEEP_BUTTON);
                            bundle.putSerializable("key", (Serializable) data.getData());
                            Intent intent = new Intent(getApplicationContext(),
                                    SleepActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            sendBroadcast(new Intent(App.CONTROL_BACKLIGHT).putExtra("BLStatus", "off"));
                        }
                    }
                } catch (Exception e) {
                    sendBroadcast(new Intent(App.CONTROL_BACKLIGHT).putExtra("BLStatus", "off"));
                    e.printStackTrace();
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
        handler.removeMessages(UPDATEMSG);
    }

    private Socket socket;


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

                        Command cmmond = App.gson.fromJson(
                                json, Command.class);
                        Log.e(tag + "in_play" + "---" + cmmond.getCommand(), json);
                        switch (cmmond.getCommand()) {
                            case 1:

                                sendBroadcast(new Intent(App.STOP));
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("key", cmmond);
                                Intent intent = new Intent(getApplicationContext(),
                                        NowinsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                sendBroadcast(new Intent(App.PALY).putExtras(bundle));
                                break;
                            case 2:
                                sendBroadcast(new Intent(App.FORWARD));
                                break;
                            case 3:
                                sendBroadcast(new Intent(App.REWIND));
                                break;
                            case 4:
                                sendBroadcast(new Intent(App.Cancle));
                                break;
                            case 5:
                                sendBroadcast(new Intent(App.PAUSE));
                                break;
                            case 6:
                                sendBroadcast(new Intent(App.STOP));
                                break;
                            case 7:
                                sendBroadcast(new Intent(App.PAUSE));
                                break;
                        }
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
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    跑马灯：rollTitles
    //            返回数据类型
//    List<RollTitlesEntity> rollTitles
    public int currentmsg;
    private TextSurfaceView Text;
    private MarqueeToast toast;
    private List<RollTitles> rollTitles = new ArrayList<>();

    public void getSubTitle() {//跑马灯
        String url = App.requrl("getSubTitle", "");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {
                    Log.e(tag, json);
                    AJson<List<RollTitles>> data = App.gson.fromJson(
                            json, new TypeToken<AJson<List<RollTitles>>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        if (!data.getData().isEmpty()) {
                            rollTitles = data.getData();
                            if (toast == null) {
                                showmessage();
                            } else {
                                Log.e(tag, "toast != null");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void initweb() {
        WebView webView = new WebView(this);
        webView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings websettings = webView.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setDefaultTextEncodingName("gbk");
    }

    private void showmessage() {
        try {
            WebView webView = new WebView(this);
            webView.setBackgroundColor(Color.TRANSPARENT);

            if (rollTitles != null && !rollTitles.isEmpty()) {
                Log.e(tag, "开始跑马灯");
                if (rollTitles.size() <= currentmsg)
                    currentmsg = 0;
//                if (System.currentTimeMillis() > rollTitles.get(currentmsg).getEndtime()) {
//                    rollTitles.remove(currentmsg);
//                    currentmsg = 0;
//                    handler.post(this);
//                    return;
//                }
                if (toast != null) {
                    toast.hid();
                    toast = null;
                }
                toast = new MarqueeToast(getApplicationContext());
                Text = new TextSurfaceView(getApplicationContext(), this);
                Text.setOrientation(1);

                Text.setContent(rollTitles.get(currentmsg).getContent());
//                if (!rollTitles.get(currentmsg).getContent().equals("")
//                        && rollTitles.get(currentmsg).getContent() != null) {
//                    Text.setContent(rollTitles.get(currentmsg).getContent());
//                }
//                if (app.getTest() != null) {
//                    toast.setHeight(app.getTest().getHeight());
//                }
//                Text.setTest(app.getTest());


//                webView.loadData("<marquee>" + rollTitles.get(currentmsg).getContent() + "</marquee>", "text/html", "utf-8");
                webView.loadDataWithBaseURL(null, test(rollTitles.get(currentmsg).getContent()), "text/html", "utf-8", null);
//                toast.setView(Text);
                toast.setHeight(720);
                toast.setView(webView);
                toast.setGravity(Gravity.TOP | Gravity.LEFT, 1920, 0, 0);
                toast.show();
                currentmsg++;
            } else {
                Log.e(tag, "跑马灯没了");
                if (toast != null) {
                    toast.hid();
                    toast = null;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private String test(String s) {
        Log.e(tag + "@@@@@", s);
        return "<marquee direction=\"left\"   behavior=\"scroll\" scrollamount=\"4\" scrolldelay=\"0\" loop=\"-1\"   hspace=\"10\" vspace=\"10\" >" + s +
                "</marquee>";
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
//        rollTitles();
    }

}
