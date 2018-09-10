package xiaoxi.tv.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import xiaoxi.tv.R;
import xiaoxi.tv.WelcomeActivity;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.Auth;
import xiaoxi.tv.bean.LogoBg;
import xiaoxi.tv.bean.WelcomeAd;
import xiaoxi.tv.service.CTService;
import xiaoxi.tv.service.TVService;
import xiaoxi.tv.tools.Ap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class App extends Application {
    // 縮放比率
    public static final float SCALE_RATE = 1.25f;
    public static final String SLEEP_BUTTON = "com.mstar.android.intent.action.SLEEP_BUTTON";
    public static final String STANDBY_DIALOG = "com.ada.android.intent.action.STANDBY_DIALOG";
    public static final String CONTROL_BACKLIGHT = "com.ada.android.intent.action.CONTROL_BACKLIGHT";
    public static final String SHOWNAME = "SHOWNAME";
    public static final String HIDENAME = "HIDENAME";
    public static final String PALY = "PALY";
    public static final String PAUSE = "PAUSE";
    public static final String STOP = "STOP";
    public static final String FORWARD = "FORWARD";
    public static final String REWIND = "REWIND";
    public static final String Cancle = "Cancle";

    private static final String tag = "App";
    public static Gson gson;
    public static RequestQueue queue;
    public static Ap ap;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        gson = new GsonBuilder().setDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss").create();
        mac();
        version();

        startService(new Intent(this, TVService.class));
        startService(new Intent(this, CTService.class));

//        hookWebView();
    }

    public static int createRn() {
        int r = (int) (Math.random() * 60 + 60);
        Log.e(tag, "random:" + r);
        return r * 1000;
    }

    public static String version;

    public void version() {
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String url = App.requrl("setUpgrade", "&version=" + version);
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String josn) {
                    AJson<String> data = gson.fromJson(
                            josn, new TypeToken<AJson<String>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
                        Log.e(tag, "上报版本成功: " + version);
                    } else {
                        Log.e(tag, "上报版本失败!");
                    }
                }
            }, null);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60 * 1000,//链接超时时间
                    10,//重新尝试连接次数
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startap() {
        try {
            ap = new Ap(this);
            boolean aps = ap.setWifiApEnabled(true);
            Log.e(tag, "热点状态：" + aps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    http://182.61.11.174:8080/tv/
    public static String headurl = "http://182.61.11.174:8080/tv/remote/";
    public static String socketurl = "http://182.61.11.174:8000/tv";

    //public static String socketurl = "http://192.168.2.12:8000/tv";
    public static String requrl(String api, String parm) {
        String url = headurl + api + "?mac=" + App.mac + parm;

        return url;
    }

    public static <T> T jsonToObject(String json, TypeToken<T> typeToken) {
        //  new TypeToken<AJson<Object>>() {}.getType()   对象参数
        // new TypeToken<AJson<List<Object>>>() {}.getType() 集合参数
        if (TextUtils.isEmpty(json) || json.equals("null"))
            return null;
        try {
            return gson.fromJson(json, typeToken.getType());
        } catch (Exception e) {
            return null;
        }
    }

    public static String mac = "xiaoxitv";

//    public static String mac = "00:15:18:17:84:8f";

    private void mac() {
        try {
            Process pro = Runtime.getRuntime().exec(
                    "cat /sys/class/net/eth0/address");
            InputStreamReader inReader = new InputStreamReader(
                    pro.getInputStream());
            BufferedReader bReader = new BufferedReader(inReader);
            String line = null;
            while ((line = bReader.readLine()) != null) {
                mac = line.trim().replace(":", "").toLowerCase();
            }
            Log.e(tag, "---mac---\n" + mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isInstall(Context context, String packageName) {
        try {
            PackageInfo pin = context.getPackageManager().getPackageInfo(
                    packageName, 0);
            if (pin != null) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Bitmap getTest() {
        return test;
    }

    public void setTest(Bitmap test) {
        this.test = test;
    }

    Bitmap test;

    public boolean isMsg() {
        return msg;
    }

    public void setMsg(boolean msg) {
        this.msg = msg;
    }

    private boolean msg;
    private LogoBg logoBg;

    public LogoBg getLogoBg() {
        return logoBg;
    }

    public void setLogoBg(LogoBg logoBg) {
        this.logoBg = logoBg;
    }

    private void hookWebView() {
        Class<?> factoryClass = null;
        try {
            factoryClass = Class.forName("android.webkit.WebViewFactory");
            Method getProviderClassMethod = null;
            Object sProviderInstance = null;

            if (Build.VERSION.SDK_INT == 23) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> constructor = providerClass.getConstructor(delegateClass);
                if (constructor != null) {
                    constructor.setAccessible(true);
                    Constructor<?> constructor2 = delegateClass.getDeclaredConstructor();
                    constructor2.setAccessible(true);
                    sProviderInstance = constructor.newInstance(constructor2.newInstance());
                }
            } else if (Build.VERSION.SDK_INT == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> constructor = providerClass.getConstructor(delegateClass);
                if (constructor != null) {
                    constructor.setAccessible(true);
                    Constructor<?> constructor2 = delegateClass.getDeclaredConstructor();
                    constructor2.setAccessible(true);
                    sProviderInstance = constructor.newInstance(constructor2.newInstance());
                }
            } else if (Build.VERSION.SDK_INT == 21) {// Android 21无WebView安全限制
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                sProviderInstance = providerClass.newInstance();
            }
            if (sProviderInstance != null) {
                Field field = factoryClass.getDeclaredField("sProviderInstance");
                field.setAccessible(true);
                field.set("sProviderInstance", sProviderInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
