package xiaoxi.tv.tools;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

public class Ap {


    private final String TAG = "Ap";
    private WifiManager wifiManager;
    private String WIFI_HOST_SSID = "XiaoXiTV";
    private String WIFI_HOST_PRESHARED_KEY = "88888888";// 密码必须大于8位数

    public Ap(Context context) {
        super();
        //获取wifi管理服务
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 判断热点开启状态
     */
    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    private WIFI_AP_STATE getWifiApState() {
        int tmp;
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            tmp = ((Integer) method.invoke(wifiManager));
            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    public enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }

    /**
     * wifi热点开关
     *
     * @param enabled true：打开	 false：关闭
     * @return true：成功	 false：失败
     */
    public boolean setWifiApEnabled(boolean enabled) {
        System.out.println(TAG + ":开启热点");
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
            System.out.println(TAG + ":关闭wifi");
        } else {
            wifiManager.setWifiEnabled(true);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = WIFI_HOST_SSID;
            //配置热点的密码
            apConfig.preSharedKey = WIFI_HOST_PRESHARED_KEY;
            //安全：WPA2_PSK
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }


    public void startWifiAp() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }

        Method method = null;
        try {
            method = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            method.setAccessible(true);
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = "tv";
            netConfig.preSharedKey = "88888888";
            netConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);

            method.invoke(wifiManager, netConfig, true);

        } catch (Exception e) {
            Log.i(TAG, "startWifiAp: " + e.getMessage());
        }
    }
}
