package xiaoxi.tv;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.Auth;
import xiaoxi.tv.bean.LogoBg;
import xiaoxi.tv.bean.WelcomeAd;
import xiaoxi.tv.service.TVService;
import xiaoxi.tv.tools.Ap;
import xiaoxi.tv.tools.Contants;
import xiaoxi.tv.tools.FULL;
import xiaoxi.tv.tools.PermissionRequestUtil;
import xiaoxi.tv.ui.ad.ResType;

public class WelcomeActivity extends BaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    String tag = "WelcomeActivity";

    @Override
    protected void onStart() {
        super.onStart();
    }

    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        app = (App) getApplication();

//        App.isInstall(WelcomeActivity.this, "tufer.com.menutest"); //设置


        find();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netreceiver, intentFilter);

//        requestAlertWindowPermission();

//        PermissionRequestUtil.judgePermissionOver23(this,
//                new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW,
//                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION
//                },
//                Contants.PermissRequest);

    }

    private void check() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                Boolean s = PermissionRequestUtil.judgePermissionOver23(this,
                        new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                        Contants.PermissRequest);
            } else {
                Intent intent = new Intent(WelcomeActivity.this, TVService.class);
                startService(intent);
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println("2222222222");
//        if (requestCode == Contants.PermissRequest) {
//            if (Build.VERSION.SDK_INT >= 23) {
//                if (!Settings.canDrawOverlays(this)) {
////                    ToastUtils.showLongToast(mContext, "未允许");
//                    PermissionRequestUtil.showSuspeWindow(WelcomeActivity.this);
//                } else {
////                    ToastUtils.showLongToast(mContext, "允许");
//                }
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void exitmusic() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver netreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager
                            .getActiveNetworkInfo();
                    int network_type = activeNetworkInfo.getType();
                    Log.e("网络类型&Net Type：", network_type + "");
                    if (network_type > -1) {
                        ad_tips.setText("");
                        checkAuth();
                    } else {
                        ad_tips.setText(getString(R.string.NetWorkError));
                    }
                    if (network_type == 9) {
                        new Ap(getApplicationContext()).startWifiAp();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void checkAuth() {
        String url = App.requrl("checkAuth", "");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {
                    AJson<Auth> data = App.gson.fromJson(json, new TypeToken<AJson<Auth>>() {
                    }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        Log.e(tag, json);
                        getad();
                        getLogo();
                    } else {
                        warn("温馨提示", data.getMsg());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(WelcomeActivity.this, R.string.Error, Toast.LENGTH_SHORT).show();
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


    private ImageView ad_image;
    private VideoView ad_video;
    private TextView ad_time;
    private TextView ad_tips;
    private MediaPlayer mediaPlayer;
    private WebView ad_web;

    private void find() {
        ad_image = findViewById(R.id.ad_image);
        ad_video = findViewById(R.id.ad_video);
        FULL.star(ad_video);
        ad_video.setOnPreparedListener(this);
        ad_video.setOnErrorListener(this);
        ad_video.setOnCompletionListener(this);
        ad_web = findViewById(R.id.ad_web);
        WebSettings websettings = ad_web.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(true);
        ad_web.setBackgroundColor(Color.TRANSPARENT);
        ad_web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        ad_web.getSettings().setDefaultTextEncodingName("GBK");


        ad_time = findViewById(R.id.ad_time);
        ad_tips = findViewById(R.id.ad_tips);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playmusic();
            }
        });
    }

    private void getLogo() {
        String url = App.requrl("getLogo", "&type=3");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {
                    Log.e(tag, json);
                    AJson<LogoBg> data = App.gson.fromJson(
                            json, new TypeToken<AJson<LogoBg>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        app.setLogoBg(data.getData());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), R.string.Error, Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                5 * 1000,//链接超时时间
                0,//重新尝试连接次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        App.queue.add(request);
    }

    private int playtime;
    private List<WelcomeAd> welcomeAds = new ArrayList<WelcomeAd>();

    private void getad() {
        String url = App.requrl("getWelComeAd", "&adType=1");
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
                        welcomeAds = data.getData();
                        Log.e(tag, welcomeAds.size() + "");
                        if (!welcomeAds.isEmpty()) {
                            for (WelcomeAd ad : data.getData()) {
                                playtime += ad.getInter();
                            }
//                            handler.sendEmptyMessage(UPDATETIME);
                            CountDownTimer countDownTimer = new CountDownTimer(playtime * 1000, 1000) {
                                @Override
                                public void onTick(long t) {
                                    ad_time.setText(t / 1000 + "");
                                }

                                @Override
                                public void onFinish() {
                                    cancel();
                                    ToMain();
                                }
                            }.start();
                            handler.sendEmptyMessage(UPDATEAD);
                        } else {
                            ToMain();
                        }
                    } else {
                        warn("温馨提示", data.getMsg());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(WelcomeActivity.this, R.string.Error, Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                5 * 1000,//链接超时时间
                1,//重新尝试连接次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        App.queue.add(request);
    }


    private void ToMain() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }


    private int currentad;
    private WelcomeAd ad;
    private final int UPDATEAD = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ViewGone();
            switch (msg.what) {
                case UPDATEAD:
                    if (currentad < welcomeAds.size()) {
                        stopmusic();
                        ad = welcomeAds.get(currentad);
                        switch (ad.getType()) {
                            case 1:
                                playimg();
                                playmusic();
                                break;
                            case 2:
                                playvideo();
                                break;
                            case 3:
                                try {
                                    String resurl = ad.getFilePath();
                                    String temp = resurl.substring(resurl.lastIndexOf(".")).toLowerCase();
                                    Log.e(resurl, temp);
                                    int type = ResType.type.get(temp);
                                    switch (type) {
                                        case 1://ResImage
                                            playimg();
                                            break;
                                        case 2://ResAudio
                                        case 3://ResVideo
                                            playvideo();
                                            break;
                                        case 4://ResTxt
                                        case 5://ResOffice
                                            playweb();
                                            break;
                                        default:
                                            break;
                                    }
                                } catch (Exception e) {
                                    playweb();
                                }
                                break;

                        }
                        handler.sendEmptyMessageDelayed(UPDATEAD, ad.getInter() * 1000);
                        currentad++;
                    }
                    break;
            }
        }
    };

    private void ViewGone() {
        ad_image.setVisibility(View.GONE);
        ad_video.setVisibility(View.GONE);
        ad_web.setVisibility(View.GONE);

    }

    private void playimg() {
        ad_image.setVisibility(View.VISIBLE);
        Log.e(tag + " playimg()", ad.getFilePath());
        Picasso.with(WelcomeActivity.this).load(ad.getFilePath()).into(ad_image);
    }

    private void playvideo() {
        ad_video.setVisibility(View.VISIBLE);
        Log.e(tag + " playvideo()", ad.getFilePath());
        ad_video.setVideoURI(Uri.parse(ad.getFilePath()));
    }

    private void playweb() {
        ad_web.setVisibility(View.VISIBLE);
        Log.e(tag + " playweb()", ad.getFilePath());
        ad_web.loadUrl(ad.getFilePath());
    }


    private void playmusic() {
        try {
            Log.e(tag + " playmusic()", ad.getBgFile());
            mediaPlayer.setDataSource(WelcomeActivity.this,
                    Uri.parse(ad.getBgFile()));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopmusic() {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        playvideo();
    }


    private AlertDialog.Builder builder;

    private void warn(String title, String msg) {
        try {
            builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(msg + "\n注册码：" + App.mac)
                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkAuth();
                        }
                    });
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            exitmusic();
            unregisterReceiver(netreceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
