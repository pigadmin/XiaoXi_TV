package xiaoxi.tv.ui.head;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import xiaoxi.tv.R;
import xiaoxi.tv.tools.LtoDate;

public class Head extends LinearLayout {

    private View view;
    private Context context;

    public Head(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.head, this);
        find();
        init();
    }

    TextView time, week, date;

    private void init() {
        time = view.findViewById(R.id.time);
        week = view.findViewById(R.id.week);
        week.setText(LtoDate.E(System.currentTimeMillis()));
        date = view.findViewById(R.id.date);
        date.setText(LtoDate.yMd(System.currentTimeMillis()));
        handler.sendEmptyMessage(UPDATETIME);
    }

    private void find() {
    }

    private final int UPDATETIME = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATETIME:
                    time.setText(LtoDate.Hm(System.currentTimeMillis()));
                    handler.sendEmptyMessageDelayed(UPDATETIME, 60 * 1000);
                    break;

            }
        }
    };
}
