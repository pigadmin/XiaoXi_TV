package xiaoxi.tv.service.msg;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MarqueeToast {

    WindowManager mWindowManager;
    private final LayoutParams mParams = new LayoutParams();
    private int mGravity = Gravity.BOTTOM;
    private int mX, mY;
    float mHorizontalMargin;
    float mVerticalMargin;
    private Context context;
    View mView;
    int defaultw = 1280;
    int defaulth = 40;

    public MarqueeToast(Context context) {
        this.context = context;
        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        final LayoutParams params = mParams;
        params.height = defaulth;
        params.width = defaultw;

//        params.type = LayoutParams.TYPE_SYSTEM_ALERT
////                | LayoutParams.TYPE_SYSTEM_OVERLAY;

        params.type = LayoutParams.TYPE_SYSTEM_ALERT | LayoutParams.TYPE_SYSTEM_OVERLAY;
        ;

        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;

        params.format = PixelFormat.TRANSLUCENT;

        params.setTitle("Marquee");
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        mHorizontalMargin = horizontalMargin;
        mVerticalMargin = verticalMargin;
    }

    public void setView(View mView) {
        this.mView = mView;
    }

    public void show() {
        try {
            if (mView != null) {
                final int gravity = mGravity;
                mParams.gravity = gravity;
                if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                    mParams.horizontalWeight = 0.0f;
                }
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                    mParams.verticalWeight = 0.0f;
                }
                mParams.x = mX;
                mParams.y = mY;
                mParams.verticalMargin = mVerticalMargin;
                mParams.horizontalMargin = mHorizontalMargin;
                if (mView.getParent() != null) {
                    mWindowManager.removeView(mView);
                }
                mWindowManager.addView(mView, mParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hid() {
        if (mView != null && mView.getParent() != null) {
            mWindowManager.removeView(mView);
        }
    }

    public void setGravity(int gravity, int with, int xOffset, int yOffset) {
        mGravity = gravity;
        mX = xOffset;
        mY = yOffset;
        mParams.width = with;
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        mGravity = gravity;
        mX = xOffset;
        mY = yOffset;
    }

    public void setHeight(int height) {
        mParams.height = height;
    }

    public void setWidth(int width) {
        mParams.width = width;
    }

}
