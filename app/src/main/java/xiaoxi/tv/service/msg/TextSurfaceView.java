package xiaoxi.tv.service.msg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import xiaoxi.tv.R;


public class TextSurfaceView extends SurfaceView implements Callback, Runnable {

    private boolean isMove = true;

    private int orientation = 0;

    public final static int MOVE_LEFT = 1;

    public final static int MOVE_RIGHT = 0;

    private long speed = 10;

    private String content;

    //    private String bgColor = "#55000000";
    private String bgColor = "#00000000";

    private int bgalpha = 0;

    private String fontColor = "#FFFFFF";

    private int fontAlpha = 255;

    private float fontSize = 22f;

    private SurfaceHolder mSurfaceHolder;

    private boolean loop = true;

    private float x = 0;

    IScrollState scroolState;


    public TextSurfaceView(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setBackgroundColor(Color.parseColor(bgColor));
//        getBackground().setAlpha(bgalpha);
    }

    public TextSurfaceView(Context context, IScrollState scroolState) {
        super(context);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setBackgroundColor(Color.parseColor(bgColor));
//        getBackground().setAlpha(bgalpha);
        this.scroolState = scroolState;

    }


    public TextSurfaceView(Context context, boolean move) {
        this(context);
        this.isMove = move;
        setLoop(isMove());
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {


        if (isMove) {
            if (orientation == MOVE_LEFT) {
                x = getWidth();
            } else {
                x = -(content.length() * 10);
            }
            new Thread(this).start();
        } else {
            draw();
        }


    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        loop = false;
        getBackground().setAlpha(bgalpha);
    }

    private void draw() {

        try {

            if (mSurfaceHolder == null) {
                return;
            }

            Canvas canvas = mSurfaceHolder.lockCanvas();
            if (null != canvas) {

                Paint paint = new Paint();
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                paint.setAntiAlias(true);
                paint.setTypeface(Typeface.SANS_SERIF);
                paint.setTextSize(fontSize);
                paint.setColor(Color.parseColor(fontColor));
                paint.setAlpha(fontAlpha);
                Rect bounds = new Rect();
                paint.getTextBounds(content, 0, content.length(), bounds);
                Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
                canvas.drawText(content, x, baseline, paint);
                Resources res = getResources();
//                Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
//                Log.e("----", x + "");

//                if (test != null) {
//                    Log.e("----", x + test.getWidth() + "");
//                    canvas.drawBitmap(test, x + test.getWidth(), 0, paint);
//                }


                mSurfaceHolder.unlockCanvasAndPost(canvas);
                if (isMove) {
                    float conlen = paint.measureText(content);
                    int w = getWidth();
                    if (orientation == MOVE_LEFT) {
                        if (x < -conlen) {
                            x = w;
                            if (this.scroolState != null)
                                this.scroolState.stop();
                        } else {
                            x -= 2;
                        }
                    } else if (orientation == MOVE_RIGHT) {
                        if (x >= w) {
                            x = -conlen;
                            if (this.scroolState != null)
                                this.scroolState.stop();
                        } else {
                            x += 2;
                        }
                    }
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void run() {
        while (loop) {
            synchronized (mSurfaceHolder) {
                draw();
            }
            try {
                Thread.sleep(speed);
            } catch (Exception e) {

            }
        }
        content = null;
    }


    private int getOrientation() {
        return orientation;
    }


    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    private long getSpeed() {
        return speed;
    }


    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public boolean isMove() {
        return isMove;
    }


    public void setMove(boolean isMove) {
        this.isMove = isMove;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setBgalpha(int bgalpha) {
        this.bgalpha = bgalpha;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public void setFontAlpha(int fontAlpha) {
        this.fontAlpha = fontAlpha;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public Bitmap getTest() {
        return test;
    }

    public void setTest(Bitmap test) {
        this.test = test;
    }

    Bitmap test;

}
