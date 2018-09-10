package xiaoxi.tv.ui.ad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import java.util.Timer;
import java.util.TimerTask;

import xiaoxi.tv.BaseActivity;
import xiaoxi.tv.R;
import xiaoxi.tv.app.App;
import xiaoxi.tv.tools.FULL;
import xiaoxi.tv.ui.ad.ResType;
import xiaoxi.tv.ui.ad.bean.Command;
import xiaoxi.tv.ui.ad.bean.Msg;
import xiaoxi.tv.ui.ad.bean.Play;

public class MsgInsActivity extends BaseActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private String tag = "MsgInsActivity";
    private Handler handler = new Handler();

    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowins);
        app = (App) getApplication();
        initview();
        setvalue();
    }


    @Override
    protected void onDestroy() {
        app.setMsg(false);
        super.onDestroy();
    }


    private int type = 0;
    private String resurl;
    private Msg msg;

    private void setvalue() {
        try {
            msg = (Msg) getIntent().getExtras().get("key");
            if (msg != null) {
                Log.e(tag + "---setvalue()", msg.getSourceType() + "");
                stop();
                type = msg.getSourceType();
                switch (type) {
                    case 1://外网
                    case 2://上传
                        try {
                            ViewGone();
                            resurl = msg.getSurl();
                            String temp = resurl.substring(resurl.lastIndexOf(".")).toLowerCase();
                            Log.e(tag, resurl);
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
            } else {
                stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        long end = msg.getEndTime();
        long cur = System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, end - cur);

    }

    private void playweb() {
        msgweb.setVisibility(View.VISIBLE);
        msgweb.loadUrl(resurl);
    }


    private void playvideo() {
        msgvideo.setVisibility(View.VISIBLE);
        msgvideo.setVideoPath(resurl);
    }


    private void playimg() {
        msgimg.setVisibility(View.VISIBLE);
        Picasso.with(this).load(resurl).into(msgimg);
    }


    private void ViewGone() {
        msgimg.setVisibility(View.GONE);
        msgvideo.setVisibility(View.GONE);
        msgweb.setVisibility(View.GONE);

    }


    private VideoView msgvideo;
    private ImageView msgimg;
    private TextView msgname;
    private WebView msgweb;

    private void initview() {
        // TODO Auto-generated method stub
        msgvideo = (VideoView) findViewById(R.id.msgvideo);
        FULL.star(msgvideo);
        msgname = (TextView) findViewById(R.id.msgname);
        MediaController controller = new MediaController(this);
        msgvideo.setMediaController(controller);
        msgimg = (ImageView) findViewById(R.id.msgimg);
        msgweb = (WebView) findViewById(R.id.msgweb);
        WebSettings websettings = msgweb.getSettings();
        websettings.setJavaScriptEnabled(true);
        websettings.setBuiltInZoomControls(true);
        msgweb.setBackgroundColor(Color.TRANSPARENT);
        msgweb.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        msgweb.getSettings().setDefaultTextEncodingName("GBK");


        msgvideo.setOnPreparedListener(this);
        msgvideo.setOnCompletionListener(this);
        msgvideo.setOnErrorListener(this);
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
