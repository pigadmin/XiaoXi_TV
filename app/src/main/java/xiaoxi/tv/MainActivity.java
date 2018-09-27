package xiaoxi.tv;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xiaoxi.tv.adapter.AppAdapter;
import xiaoxi.tv.adapter.Gameadapter;
import xiaoxi.tv.adapter.Gameadapter2;
import xiaoxi.tv.adapter.ModuleAdapter;
import xiaoxi.tv.adapter.MyRecyclerAdapter;
import xiaoxi.tv.adapter.MyRecyclerAdapter2;
import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.Game;
import xiaoxi.tv.bean.RollTitles;
import xiaoxi.tv.bean.Update;
import xiaoxi.tv.bean.WelcomeAd;
import xiaoxi.tv.service.TVService;
import xiaoxi.tv.service.msg.MarqueeToast;
import xiaoxi.tv.service.msg.TextSurfaceView;
import xiaoxi.tv.tools.ApkUpdate;
import xiaoxi.tv.tools.ContantUtil;
import xiaoxi.tv.tools.IpGetUtil;
import xiaoxi.tv.tools.QRCodeUtil;
import xiaoxi.tv.tools.VST;
import xiaoxi.tv.ui.ad.MainAd;
import xiaoxi.tv.ui.ad.SleepActivity;
import xiaoxi.tv.ui.ad.SleepActivity2;
import xiaoxi.tv.ui.ad.bean.Command;
import xiaoxi.tv.ui.diy.HorizontalRecyclerView;
import xiaoxi.tv.ui.diy.ScaleRecyclerView;
import xiaoxi.tv.ui.diy.SpaceItemDecoration;
import xiaoxi.tv.ui.diy.test.ModuleLayoutManager;
import xiaoxi.tv.ui.diy.test.TvRecyclerView;

public class MainActivity extends BaseActivity implements Gameadapter.OnItemClickListener, Gameadapter2.OnItemClickListener {
    private String tag = "MainActivity";
    private final int UPDATEMSG = 0;
    private final int RUNMSG = 1;
    private final int SLEEP = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATEMSG:
                    getSubTitle();
                    handler.sendEmptyMessageDelayed(UPDATEMSG, App.createRn());
                    break;
                case RUNMSG:
                    try {
                        if (rollTitles != null && !rollTitles.isEmpty()) {
                            Log.e(tag, "开始跑马灯");
                            if (rollTitles.size() <= currentmsg)
                                currentmsg = 0;
                            webView.loadDataWithBaseURL(null, test(rollTitles.get(currentmsg).getContent()), "text/html", "utf-8", null);
                            currentmsg++;
                            handler.sendEmptyMessageDelayed(RUNMSG, 30 * 1000);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    break;
                case SLEEP:
                    sleep();
                    break;
            }
        }
    };
    private App app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (App) getApplication();
        find();
        init();

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sendBroadcast(new Intent(App.SLEEP_BUTTON));
//            }
//        }, 2000);

//        reqper();

        handler.sendEmptyMessage(UPDATEMSG);


        regad();
        handler.sendEmptyMessageDelayed(SLEEP, sleep * 1000);
    }

    private final int sleep = 60;

    private void regad() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(App.PALY);
            filter.addAction(App.PAUSE);
            filter.addAction(App.STOP);
            filter.addAction(App.FORWARD);
            filter.addAction(App.REWIND);
            filter.addAction(App.Cancle);
            registerReceiver(receiver, filter);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

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
                            if (!webView.isShown()) {
                                webView.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessage(RUNMSG);
                            }
                        } else {
                            if (webView.isShown()) {
                                webView.setVisibility(View.GONE);
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
//                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                5 * 1000,//链接超时时间
                0,//重新尝试连接次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        App.queue.add(request);
    }

    public int currentmsg;


    private WebView webView;

    private void initweb() {
        webView = findViewById(R.id.msg);
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

    private String test(String s) {
        Log.e(tag + "@@@@@", s);
        return "<marquee direction=\"left\"   behavior=\"scroll\" scrollamount=\"4\" scrolldelay=\"0\" loop=\"-1\"   hspace=\"10\" vspace=\"10\" >" + s +
                "</marquee>";
    }


    private void reqper() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                System.out.println(hasWriteContactsPermission);
                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                            2007);
                    return;
                }
            }
//            if (Build.VERSION.SDK_INT >= 23) {
//                if (!Settings.canDrawOverlays(this)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivityForResult(intent, 1);
//                } else {
//                    //TODO do something you need
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sleep() {//休眠
        Log.e(tag, "待机待机");
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
                            Log.e(tag, "待机待机" + data.getData().size());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("key", (Serializable) data.getData());
                            Intent intent = new Intent(getApplicationContext(),
                                    SleepActivity2.class);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.e(tag, getCurrentFocus().getId() + "");
        checkSleep();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(SLEEP);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkSleep();
    }

    private void checkSleep() {
        handler.removeMessages(SLEEP);
        handler.sendEmptyMessageDelayed(SLEEP, 60 * 1000);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
            final String ip = IpGetUtil.getIPAddress(MainActivity.this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap success = QRCodeUtil.createQRImage("{'code':'200','data':" +
                                    "{'mac':'" + App.mac + "','ip':'" + ip + "','time':" + System.currentTimeMillis() + "}" +
                                    ",'msg':null,'errorInfo':null}", 240, 240, null,
                            null);
                    app.setTest(success);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            qr_code.setImageBitmap(success);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ImageView qr_code;
    private HorizontalRecyclerView horizontalRecyclerView1, horizontalRecyclerView2;
    private TvRecyclerView mTvRecyclerView;
    private ModuleLayoutManager manager;
    private AppAdapter mAdapter;


    private void find() {
        initweb();
        mTvRecyclerView = findViewById(R.id.tv_recycler_view);
        qr_code = findViewById(R.id.qr_code);

        manager = new MainActivity.MyModuleLayoutManager(3, LinearLayoutManager.HORIZONTAL,
                400, 260);
        mTvRecyclerView.setLayoutManager(manager);

        int itemSpace = getResources().
                getDimensionPixelSize(R.dimen.recyclerView_item_space);
        mTvRecyclerView.addItemDecoration(new MainActivity.SpaceItemDecoration(itemSpace));


        mTvRecyclerView.setOnItemStateListener(new TvRecyclerView.OnItemStateListener() {
            @Override
            public void onItemViewClick(View view, int position) {
                //点击事件
                try {
                    Game game = games.get(position);
                    if (position == 3 || position == 6) {
                        String url = game.getIcon();
                        startActivity(new Intent(MainActivity.this, MainAd.class).putExtra("key", url));
                    } else {
                        String name = game.getName().trim();
                        String pkgname = game.getPackage_name();
                        if (game.getEnable() != 0) {
                            boolean tmp = new VST().to(MainActivity.this, name);
                            if (!tmp) {
                                if (!App.isInstall(MainActivity.this, pkgname)) {
                                    new ApkUpdate(MainActivity.this, game.getPath())
                                            .downloadAndInstall();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.stopapp, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemViewFocusChanged(boolean gainFocus, View view, int position) {
            }
        });
        mTvRecyclerView.setSelectPadding(35, 34, 35, 38);
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = space;
            outRect.left = space;
        }
    }

    public int[] mStartIndex = {0, 1, 2, 3, 5, 8, 9, 11, 14, 15, 17, 20};

    private class MyModuleLayoutManager extends ModuleLayoutManager {

        MyModuleLayoutManager(int rowCount, int orientation, int baseItemWidth, int baseItemHeight) {
            super(rowCount, orientation, baseItemWidth, baseItemHeight);
        }

        @Override
        protected int getItemStartIndex(int position) {
            if (position > 3) {
                if (position % 3 == 0 || position % 3 == 1) {
                    return 2 * position - 3;
                } else if (position % 3 == 2) {
                    return 2 * position - 2;
                }
            }


//            if (position < mStartIndex.length) {
//                return mStartIndex[position];
//            } else {
            return position;
//            }
        }

        @Override
        protected int getItemRowSize(int position) {
            if (position > 0 && position % 3 == 0) {
                return 2;
            } else {
                return 1;
            }
        }

        @Override
        protected int getItemColumnSize(int position) {
            if (position > 0 && position % 3 == 0) {
                return 2;
            } else {
                return 1;
            }
        }

        @Override
        protected int getColumnSpacing() {
            return getResources().
                    getDimensionPixelSize(R.dimen.recyclerView_item_space);
        }

        @Override
        protected int getRowSpacing() {
            return getResources().
                    getDimensionPixelSize(R.dimen.recyclerView_item_space);
        }
    }

    private void init() {
        getapp();
        getUpgrade();
    }

    private void getUpgrade() {
        String url = App.requrl("getUpgrade", "&version=" + App.version);
//        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {
                    Log.e(tag, json);
                    final AJson<String> data = App.gson.fromJson(
                            json, new TypeToken<AJson<String>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        if (!"".equals(data.getData()) && null != data.getData()) {
                            Log.e(tag, "check update");
                            new ApkUpdate(MainActivity.this, data.getData()).update();
                        } else {
                            Log.e(tag, "no update");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                5 * 1000,//链接超时时间
                0,//重新尝试连接次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        App.queue.add(request);

    }

    List<Game> games = new ArrayList<>();

    private void getapp() {
        String url = App.requrl("getApp", "");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {
                    Log.e(tag, json);
                    AJson<List<Game>> data = App.gson.fromJson(
                            json, new TypeToken<AJson<List<Game>>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
//                        games.clear();
//                        games.addAll(data.getData());
//                        mAdapter.notifyDataSetChanged();
                        games = data.getData();
                        System.out.println(games.size());
                        mAdapter = new AppAdapter(MainActivity.this, games);
                        mTvRecyclerView.setAdapter(mAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new

                DefaultRetryPolicy(
                5 * 1000,//链接超时时间
                0,//重新尝试连接次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        App.queue.add(request);

    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            Game game = games.get(position);
            String name = game.getName();
            if (game.getEnable() != 0) {
//                if ("设置".equals(name) || "热点".equals(name)) {
//                    Intent intent = getPackageManager().getLaunchIntentForPackage("tufer.com.menutest");
//                    startActivity(intent);
//                } else if ("电影".equals(name)) {
//                    Intent intent = new Intent("myvst.intent.action.VodTypeActivity").putExtra("vodtype", "1");
//                    startActivity(intent);
//                } else if (!App.isInstall(this, games.get(position).getPackage_name())) {
//                    new ApkUpdate(this, games.get(position).getPath())
//                            .downloadAndInstall();
//                }

            } else {
                Toast.makeText(MainActivity.this, R.string.stopapp, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String com = intent.getAction();
            Log.e(tag + "---BroadcastReceiver", com);
            if (App.PALY.equals(com)) {
                try {
                    Command command = (Command) intent.getExtras().get("key");
                    mAdapter.ad(command);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else if (App.PAUSE.equals(com)) {
                try {

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else if (App.STOP.equals(com)) {

            } else if (App.FORWARD.equals(com)) {
                try {

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            } else if (App.REWIND.equals(com)) {
                try {

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else if (com.equals(App.Cancle)) {

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
