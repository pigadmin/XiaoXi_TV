package xiaoxi.tv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
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
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import xiaoxi.tv.adapter.Gameadapter;
import xiaoxi.tv.adapter.Gameadapter2;
import xiaoxi.tv.adapter.MyRecyclerAdapter;
import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.Game;
import xiaoxi.tv.bean.RollTitles;
import xiaoxi.tv.tools.ApkUpdate;
import xiaoxi.tv.tools.IpGetUtil;
import xiaoxi.tv.tools.QRCodeUtil;
import xiaoxi.tv.ui.diy.HorizontalRecyclerView;
import xiaoxi.tv.ui.diy.ScaleRecyclerView;

public class MainActivity2 extends BaseActivity implements Gameadapter.OnItemClickListener, Gameadapter2.OnItemClickListener {
    private final String tag = "MainActivity2";
    private final int UPDATEMSG = 0;
    private final int RUNMSG = 1;
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
                        } else {
                            webView.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
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
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(MainActivity2.this, "error", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.e(tag, getCurrentFocus().getId() + "");
        return super.onKeyDown(keyCode, event);
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
            final String ip = IpGetUtil.getIPAddress(MainActivity2.this);
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

    private ScaleRecyclerView mainrecyle, mainrecyle2;
    private StaggeredGridLayoutManager layoutManager, layoutManager2;
    private ImageView qr_code;
    HorizontalRecyclerView horizontalRecyclerView1, horizontalRecyclerView2;

    private void find() {
        initweb();
        mainrecyle = findViewById(R.id.mainrecyle);
        mainrecyle2 = findViewById(R.id.mainrecyle2);
        mainrecyle.setHasFixedSize(true);
        mainrecyle2.setHasFixedSize(true);
        qr_code = findViewById(R.id.qr_code);


//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(MainActivity2.this, LinearLayoutManager.VERTICAL, false);
//        mainrecyle.setLayoutManager(layoutManager1);

//        layoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL);
//        mainrecyle.setLayoutManager(layoutManager);
//        layoutManager2 = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
//        mainrecyle2.setLayoutManager(layoutManager2);
//        mainrecyle2.addItemDecoration(new SpaceItemDecoration(20));

//        horizontalRecyclerView1 = (HorizontalRecyclerView) findViewById(R.id.horizontalRecyclerView1);
//        horizontalRecyclerView1.setFocusable(true);
//        horizontalRecyclerView1.setLayoutManager(new StaggeredGridLayoutManager(1, OrientationHelper.VERTICAL));

//        horizontalRecyclerView1.setAdapter(new MyRecyclerAdapter2(this));
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, OrientationHelper.HORIZONTAL);
        manager.setOrientation(0);
        horizontalRecyclerView2 = (HorizontalRecyclerView) findViewById(R.id.horizontalRecyclerView2);
        horizontalRecyclerView2.setFocusable(true);
//        horizontalRecyclerView2.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.HORIZONTAL));
        horizontalRecyclerView2.setLayoutManager(manager);
//        horizontalRecyclerView2.setAdapter(new MyRecyclerAdapter(this));

    }

    private void setSpanCount(GridLayoutManager gridLayoutManager) {
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position > 0 && position % 3 == 2) {
                    return 3;
                } else {
                    return 1;
                }

            }
        });
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
                            new ApkUpdate(MainActivity2.this, data.getData()).update();
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
                Toast.makeText(MainActivity2.this, "error", Toast.LENGTH_SHORT).show();
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
    private List<Game> games1 = new ArrayList<>();
    private List<Game> games2 = new ArrayList<>();
    private Gameadapter adapter;
    private Gameadapter2 adapter2;

    private void getapp() {
        String url = App.requrl("getApp", "");
//        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {
                    Log.e(tag, json);
                    AJson<List<Game>> data = App.gson.fromJson(
                            json, new TypeToken<AJson<List<Game>>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        games = data.getData();
                        if (!games.isEmpty()) {
                            for (int i = 0; i < games.size(); i++) {
                                if (i < 3) {
                                    games1.add(games.get(i));
                                } else {
                                    games2.add(games.get(i));
                                }

                            }
//                            adapter = new Gameadapter(MainActivity2.this, games1);
//                            adapter.setOnItemClickListener(MainActivity2.this);
//                            mainrecyle.setAdapter(adapter);
//
//                            adapter2 = new Gameadapter2(MainActivity2.this, games2);
//                            mainrecyle2.setAdapter(adapter2);
//                            adapter2.setOnItemClickListener(MainActivity2.this);

//                            horizontalRecyclerView1.setAdapter(new MyRecyclerAdapter2(MainActivity2.this, games1));
                            MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter(MainActivity2.this, games);
                            horizontalRecyclerView2.setAdapter(myRecyclerAdapter);
                            myRecyclerAdapter.setOnItemClickListener(MainActivity2.this);
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity2.this, "error", Toast.LENGTH_SHORT).show();
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
            System.out.println();
            if (game.getEnable() != 0) {
                if ("设置".equals(name) || "热点".equals(name)) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("tufer.com.menutest");
                    startActivity(intent);
                } else if ("电影".equals(name)) {
                    Intent intent = new Intent("myvst.intent.action.VodTypeActivity").putExtra("vodtype", "1");
                    startActivity(intent);
                } else if (!App.isInstall(this, games.get(position).getPackage_name())) {
                    new ApkUpdate(this, games.get(position).getPath())
                            .downloadAndInstall();
                }
            } else {
                Toast.makeText(MainActivity2.this, R.string.stopapp, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
