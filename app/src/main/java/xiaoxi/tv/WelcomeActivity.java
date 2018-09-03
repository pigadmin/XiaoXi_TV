package xiaoxi.tv;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.Auth;
import xiaoxi.tv.bean.WelcomeAd;
import xiaoxi.tv.tools.FULL;

public class WelcomeActivity extends BaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    String tag = "WelcomeActivity";

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


//        App.isInstall(WelcomeActivity.this, "tufer.com.menutest"); //设置


        find();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netreceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void exitmusic() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
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
                    System.out.println("网络类型&Net Type：" + network_type);
                    if (network_type > -1) {
                        checkAuth();
//
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
            public void onResponse(String josn) {
                try {
                    AJson<Auth> data = App.gson.fromJson(josn, new TypeToken<AJson<Auth>>() {
                    }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        Log.e(tag, josn);
                        getad();
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

    private void find() {
        ad_image = findViewById(R.id.ad_image);
        ad_video = findViewById(R.id.ad_video);
        FULL.star(ad_video);
        ad_video.setOnPreparedListener(this);
        ad_video.setOnErrorListener(this);
        ad_video.setOnCompletionListener(this);


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

    private int playtime;
    private List<WelcomeAd> welcomeAds = new ArrayList<WelcomeAd>();

    private void getad() {
        String url = App.requrl("getWelComeAd", "&adType=1");
        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String josn) {
                try {
                    Log.e(tag, josn);
                    AJson<List<WelcomeAd>> data = App.gson.fromJson(
                            josn, new TypeToken<AJson<List<WelcomeAd>>>() {
                            }.getType());

                    if (200 == data.getCode() || 0 == data.getCode()) {
                        welcomeAds = data.getData();
                        Log.e(tag, welcomeAds.size() + "");
                        if (!welcomeAds.isEmpty()) {
                            for (WelcomeAd ad : data.getData()) {
                                playtime += ad.getInter();
                            }
                            handler.sendEmptyMessage(0);
                            handler.sendEmptyMessage(1);
                        } else {
                            toClass();
                        }
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


    private void toClass() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }


    private int currentad;
    private WelcomeAd ad;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (playtime > 0) {
//                        if (!ad_tips.isShown()) {
//                            ad_tips.setVisibility(View.VISIBLE);
//                        }
                        ad_time.setText(playtime + "");
                        playtime--;
                        handler.sendEmptyMessageDelayed(0, 1 * 1000);
                    } else {
                        toClass();
                    }
                    break;
                case 1:

                    if (currentad < welcomeAds.size()) {
                        ad = welcomeAds.get(currentad);
                        switch (ad.getType()) {
                            case 2:
                                if (ad_image.isShown()) {
                                    ad_image.setVisibility(View.GONE);
                                }
                                if (!ad_video.isShown()) {
                                    ad_video.setVisibility(View.VISIBLE);
                                }
                                videourl = ad.getFilePath();
//                                if (mediaPlayer.isPlaying()) {
//                                    mediaPlayer.reset();
//                                    mediaPlayer.stop();
//                                }
                                playvideo();
                                break;
                            case 1:
                                if (!ad_image.isShown()) {
                                    ad_image.setVisibility(View.VISIBLE);
                                }
                                if (ad_video.isShown()) {
                                    ad_video.setVisibility(View.GONE);
                                }
                                videourl = ad.getBgFile();
                                Picasso.with(WelcomeActivity.this).load(ad.getFilePath()).into(ad_image);
                                playmusic();
                                break;
                        }
                        handler.sendEmptyMessageDelayed(1, ad.getInter() * 1000);
                        currentad++;
                    }
//                    else {//循环取消注释
//                        currentad = 0;
//                        handler.sendEmptyMessage(1);
//                    }

                    break;
            }
        }
    };


//    private boolean isto;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE) {
//            if (!isto) {
//                isto = !isto;
//                handler.removeMessages(0);
//                handler.removeMessages(1);
//                toClass();
//            }
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (!isto) {
//                isto = !isto;
//                handler.removeMessages(0);
//                handler.removeMessages(1);
//                toClass();
//            }
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playvideo();
    }

    private String videourl;

    private void playvideo() {

        if (!videourl.equals("")) {
            System.out.println(videourl);
            ad_video.setVideoURI(Uri.parse(videourl));
        }
    }

    private void playmusic() {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(WelcomeActivity.this,
                    Uri.parse(videourl));
            mediaPlayer.prepareAsync();
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

    private AlertDialog.Builder builder;

    private void warn(String title, String msg) {
        try {
            builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(title)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(msg + "\n注册码：" + App.mac)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getad();
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
}
