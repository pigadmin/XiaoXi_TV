package xiaoxi.tv.ui.ad;

import android.app.Activity;
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
import xiaoxi.tv.ui.ad.bean.Command;
import xiaoxi.tv.ui.ad.bean.Play;

public class NowinsActivity extends BaseActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private String tag = "NowinsActivity";
    private Handler handle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    try {
                        if (msgvideo.isShown()) {
                            msgvideo.seekTo((int) (msgvideo.getCurrentPosition() + 5000));
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case 2:

                    try {
                        if (msgvideo.isShown()) {
                            msgvideo.seekTo((int) (msgvideo.getCurrentPosition() - 5000));
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private Command command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowins);
        initview();
        setvalue();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        try {
            IntentFilter filter = new IntentFilter();
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
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    Timer timer;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String com = intent.getAction();
            Log.e(tag + "---BroadcastReceiver", com);
            if (App.PAUSE.equals(com)) {
                try {
                    if (msgvideo.isShown()) {
                        if (timer != null) {
                            timer.cancel();
                        }
                        if (msgvideo != null) {
                            if (msgvideo.isPlaying()) {
                                msgvideo.pause();
                            } else {
                                msgvideo.start();
                            }
                        }
                    }


                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else if (App.STOP.equals(com)) {
                finish();
            } else if (App.FORWARD.equals(com)) {

                try {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (msgvideo.isShown()) {
                                handle.sendEmptyMessage(1);
                            }
                        }
                    }, 0, 1 * 1000);

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            } else if (App.REWIND.equals(com)) {
                try {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (msgvideo.isShown()) {
                                handle.sendEmptyMessage(2);
                            }

                        }
                    }, 0, 1 * 1000);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else if (com.equals(App.Cancle)) {
                if (timer != null) {
                    timer.cancel();
                }

            }
        }
    };
    private int type = 0;
    private String resurl;
    private Play play;

    private void setvalue() {
        try {
            command = (Command) getIntent().getExtras().get("key");
            Log.e(tag + "---setvalue", command.getCommand() + "");
            if (command != null) {
                play = command.getPlay();
                type = play.getStype();
                switch (type) {
                    case 2://直播
                        ViewGone();
                        msgvideo.setVisibility(View.VISIBLE);
                        msgname.setText(play.getSname());
                        resurl = play.getSurl();
                        msgvideo.setVideoPath(resurl);
                        System.out.println(resurl + "*************直播");
                        break;
                    case 3://点播
                    case 4://临时上传文件
                        try {
                            ViewGone();
                            String resurl = command.getPlay().getSurl();
                            String temp = resurl.substring(resurl.lastIndexOf(".")).toLowerCase();
                            int type = ResType.type.get(temp);
                            Log.e(resurl, type + "");
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
                    //                case 4://临时文件
                    //                    msgname.setText(play.getSname());
                    //                    resurl = play.getSurl();
                    //                    OtherRes();
                    //                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void playweb() {
        msgweb.setVisibility(View.VISIBLE);
        msgweb.loadUrl(play.getSurl());
    }


    private void playvideo() {
        msgvideo.setVisibility(View.VISIBLE);
        msgvideo.setVideoPath(play.getSurl());
    }


    private void playimg() {
        msgimg.setVisibility(View.VISIBLE);
        Picasso.with(this).load(play.getSurl()).into(msgimg);
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
