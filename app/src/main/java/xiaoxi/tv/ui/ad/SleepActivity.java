package xiaoxi.tv.ui.ad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xiaoxi.tv.BaseActivity;
import xiaoxi.tv.R;
import xiaoxi.tv.WelcomeActivity;
import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.WelcomeAd;
import xiaoxi.tv.tools.FULL;
import xiaoxi.tv.ui.ad.bean.Command;
import xiaoxi.tv.ui.ad.bean.Play;

public class SleepActivity extends BaseActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private String tag = "SleepActivity";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initview();
        setvalue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private int playtime;
    private List<WelcomeAd> welcomeAds;

    private void setvalue() {
        try {
            welcomeAds = (List<WelcomeAd>) getIntent().getExtras().get("key");
            Log.e(tag, welcomeAds.size() + "");
            if (!welcomeAds.isEmpty()) {
                for (WelcomeAd ad : welcomeAds) {
                    playtime += ad.getInter();
                }
                CountDownTimer countDownTimer = new CountDownTimer(playtime * 1000, 1000) {
                    @Override
                    public void onTick(long t) {
                        ad_time.setText(t / 1000 + "");
                    }

                    @Override
                    public void onFinish() {
                        cancel();
                        finish();
                        String type = getIntent().getStringExtra("type");
                        Log.e(tag, type);
                        if (App.SLEEP_BUTTON.equals(type)) {
                            sendBroadcast(new Intent(App.CONTROL_BACKLIGHT).putExtra("BLStatus", "off"));
                        } else if (App.STANDBY_DIALOG.equals(type)) {
                            sendBroadcast(new Intent(App.CONTROL_BACKLIGHT).putExtra("BLStatus", "on"));
                        }
                    }
                }.start();
                handler.sendEmptyMessage(UPDATEAD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void ViewGone() {
        ad_image.setVisibility(View.GONE);
        ad_video.setVisibility(View.GONE);
        ad_web.setVisibility(View.GONE);

    }

    private void playimg() {
        ad_image.setVisibility(View.VISIBLE);
        Log.e(tag + " playimg()", ad.getFilePath());
        Picasso.with(SleepActivity.this).load(ad.getFilePath()).into(ad_image);
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
            mediaPlayer.stop();
            mediaPlayer.reset();
            Log.e(tag + " playmusic()", ad.getBgFile());
            mediaPlayer.setDataSource(SleepActivity.this,
                    Uri.parse(ad.getBgFile()));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageView ad_image;
    private VideoView ad_video;
    private TextView ad_time;
    private TextView ad_tips;
    private MediaPlayer mediaPlayer;
    private WebView ad_web;

    private void initview() {
        // TODO Auto-generated method stub
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


    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        setvalue();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub
        mp.start();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }


}
