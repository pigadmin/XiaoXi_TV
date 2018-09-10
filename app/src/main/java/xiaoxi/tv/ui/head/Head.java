package xiaoxi.tv.ui.head;

import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import xiaoxi.tv.R;
import xiaoxi.tv.app.App;
import xiaoxi.tv.tools.LtoDate;

public class Head extends LinearLayout {

    private View view;
    private Context context;
    private App app;

    public Head(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.head, this);
        app = (App) context.getApplicationContext();
        find();
        init();
    }


    private void init() {

        week.setText(LtoDate.E(System.currentTimeMillis()));
        date.setText(LtoDate.yMd(System.currentTimeMillis()));

        handler.sendEmptyMessage(UPDATETIME);
        handler.sendEmptyMessage(LOGO);
    }

    TextView time, week, date;
    ImageView logo;

    private void find() {
        logo = view.findViewById(R.id.logo);
        time = view.findViewById(R.id.time);
        week = view.findViewById(R.id.week);
        date = view.findViewById(R.id.date);
    }

    private final int UPDATETIME = 0;
    private final int LOGO = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case UPDATETIME:
                        time.setText(LtoDate.Hm(System.currentTimeMillis()));
                        handler.sendEmptyMessageDelayed(UPDATETIME, 60 * 1000);
                        break;
                    case LOGO:
                        Log.e("head", app.getLogoBg().getLogo().getLogoPath());
                        Picasso.with(context).load(app.getLogoBg().getLogo().getLogoPath()).into(logo);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
