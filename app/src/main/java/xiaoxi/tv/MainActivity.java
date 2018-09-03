package xiaoxi.tv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
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
import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.Game;
import xiaoxi.tv.bean.Update;
import xiaoxi.tv.tools.ApkUpdate;
import xiaoxi.tv.tools.IpGetUtil;
import xiaoxi.tv.tools.QRCodeUtil;
import xiaoxi.tv.ui.diy.ScaleRecyclerView;
import xiaoxi.tv.ui.diy.SpaceItemDecoration;

public class MainActivity extends BaseActivity implements Gameadapter.OnItemClickListener, Gameadapter2.OnItemClickListener {
    private final String tag = "MainActivity";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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

    private ScaleRecyclerView mainrecyle, mainrecyle2;
    private StaggeredGridLayoutManager layoutManager, layoutManager2;
    private ImageView qr_code;

    private void find() {
        mainrecyle = findViewById(R.id.mainrecyle);
        mainrecyle2 = findViewById(R.id.mainrecyle2);
        mainrecyle.setHasFixedSize(true);
        mainrecyle2.setHasFixedSize(true);
        qr_code = findViewById(R.id.qr_code);
        layoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL);
        mainrecyle.setLayoutManager(layoutManager);
        layoutManager2 = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
        mainrecyle2.setLayoutManager(layoutManager2);
//        mainrecyle2.addItemDecoration(new SpaceItemDecoration(20));
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
//                Log.e(tag, json);
                final AJson<String> data = App.gson.fromJson(
                        json, new TypeToken<AJson<String>>() {
                        }.getType());
                if (200 == data.getCode() || 0 == data.getCode()) {
                    if (!"".equals(data.getData())) {
                        Log.e(tag, "check update");
                        new ApkUpdate(MainActivity.this, data.getData()).update();
                    } else {
                        Log.e(tag, "no update");
                    }
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
                Log.e(tag, json);
                AJson<List<Game>> data = App.gson.fromJson(
                        json, new TypeToken<AJson<List<Game>>>() {
                        }.getType());
                if (200 == data.getCode() || 0 == data.getCode()) {
                    List<Game> games = data.getData();
                    if (!games.isEmpty()) {
                        for (int i = 0; i < games.size(); i++) {
                            if (i < 3) {
                                games1.add(games.get(i));
                            } else {
                                games2.add(games.get(i));
                            }

                        }
                        adapter = new Gameadapter(MainActivity.this, games1);
                        adapter.setOnItemClickListener(MainActivity.this);
                        mainrecyle.setAdapter(adapter);

                        adapter2 = new Gameadapter2(MainActivity.this, games2);
                        mainrecyle2.setAdapter(adapter2);
                        adapter2.setOnItemClickListener(MainActivity.this);
                    }


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
        System.out.println(view.getId() + "sssssssss");
//        if (!App.isInstall(this, games.get(position).getPackage_name())) {
//            new ApkUpdate(this, games.get(position).getPath())
//                    .downloadAndInstall();
//        }
    }
}
