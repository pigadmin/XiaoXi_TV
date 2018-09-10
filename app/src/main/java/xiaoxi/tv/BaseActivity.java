package xiaoxi.tv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.Backs;

public class BaseActivity extends Activity {
    private final String tag = "BaseActivity";
    private App app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getApplication();


        try {
            if (!getClass().getSimpleName().equals("WelcomeActivity")) {
                backs = app.getLogoBg().getBacks();
                handler.sendEmptyMessageDelayed(backsmsg, 500);
    //            handler.sendEmptyMessage(backsmsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Backs> backs = new ArrayList<>();
    private final int backsmsg = 0;
    private final int KEY = 1;
    int cutbg = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case backsmsg:
                    try {
                        Log.e(tag, backs.get(cutbg).getPath());

                        Picasso.with(BaseActivity.this).load(backs.get(cutbg).getPath()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                                getWindow().getDecorView().setBackground(
                                        new BitmapDrawable(bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable drawable) {
                            }

                            @Override
                            public void onPrepareLoad(Drawable drawable) {
                            }
                        });
                        if (backs.size() > 1) {
                            Log.e(tag, "cutbg：" + cutbg + "----s：" + backs.get(cutbg).getInter());
                            handler.sendEmptyMessageDelayed(backsmsg, backs.get(cutbg).getInter() * 1000);
                            if (cutbg < backs.size() - 1) {
                                cutbg++;
                            } else {
                                cutbg = 0;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case KEY:
                    if (key_temp.equals("111")) {
//                        new ServerIpDialog(handler, BaseActivity.this).crt();
                    } else if (key_temp.equals("222")) {
                        try {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    key_temp = "";
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(tag + "onKeyDown", keyCode + "");
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            handler.removeMessages(0);
            key_temp += keyCode - 7;
            handler.sendEmptyMessageDelayed(0, 1 * 1000);
        }
        if (keyCode == KeyEvent.KEYCODE_8) {
            sendBroadcast(new Intent(App.SLEEP_BUTTON));
        } else if (keyCode == KeyEvent.KEYCODE_9) {
            sendBroadcast(new Intent(App.STANDBY_DIALOG));
        }

        return super.onKeyDown(keyCode, event);
    }

    private String key_temp = "";


}
