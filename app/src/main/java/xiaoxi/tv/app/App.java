package xiaoxi.tv.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
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

import xiaoxi.tv.WelcomeActivity;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.Auth;
import xiaoxi.tv.service.CTService;
import xiaoxi.tv.service.TVService;
import xiaoxi.tv.tools.Ap;

public class App extends Application {
    public static final String SLEEP_BUTTON = "com.mstar.android.intent.action.SLEEP_BUTTON";
    public static final String STANDBY_DIALOG = "com.ada.android.intent.action.STANDBY_DIALOG";
    public static final String CONTROL_BACKLIGHT = "com.ada.android.intent.action.CONTROL_BACKLIGHT";

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
//        startap();
    }


    public static String version;

    public  void version() {
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
                        Log.e(tag, "上报版本失败!" );
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
        ap = new Ap(this);
        boolean aps = ap.setWifiApEnabled(true);
        Log.e(tag, aps + "");
    }

    //    http://182.61.11.174:8080/tv/
    public static String headurl = "http://182.61.11.174:8080/tv/remote/";
    public static String socketurl = "http://182.61.11.174:8000/tv";

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

//    public static String mac = "zhu";

    public static String mac = "00:15:18:17:84:8f";

    private void mac() {
        try {
            Process pro = Runtime.getRuntime().exec(
                    "cat /sys/class/net/eth0/address");
            InputStreamReader inReader = new InputStreamReader(
                    pro.getInputStream());
            BufferedReader bReader = new BufferedReader(inReader);
            String line = null;
            while ((line = bReader.readLine()) != null) {
//                mac = line.trim().replace(":", "").toLowerCase();
            }
            System.out.println("---mac---\n" + mac);
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

}
