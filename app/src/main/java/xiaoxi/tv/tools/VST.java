package xiaoxi.tv.tools;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class VST {
    String tag = "VST";
    private Context context;
    private String name;

    public boolean to(Context context, String name) {

        try {
            Intent intent = null;
            if (name.equals("有线电视")) {
                intent = context.getPackageManager().getLaunchIntentForPackage("com.mstar.tv.tvplayer.ui");
            } else if (name.equals("系统设置")) {
                intent = context.getPackageManager().getLaunchIntentForPackage("tufer.com.menutest");
            } else if (name.equals("精选")) {
                intent = new Intent("myvst.intent.action.LancherActivity");
            } else if (name.equals("收藏")) {
                intent = new Intent("myvst.intent.action.RecodeActivity");
            } else if (name.equals("搜索")) {
//                Key = ”search_word" Value =”中⽂字符" For example(搜索影⽚ ⼼花路放):
//                intent.putExtras("search_word”,”⼼花路放");
                intent = new Intent("myvst.intent.action.SearchActivity");
            } else if (name.equals("专题")) {
                intent = new Intent("myvst.intent.action.TopicListActivity");
            } else if (name.equals("轮播")) {
//                I)
                intent = new Intent("myvst.intent.action.LivePlayer");
//                II) intent = new Intent("com.vst.v1.live.ACTION_LIVE_BACK");
//                III)  intent = new Intent("com.VST.V1.ACTION.startLive");
            } else if (name.equals("新闻")) {
                intent = new Intent("myvst.intent.action.CompatiblePlayer");
            } else if (name.equals("电影")) {
                intent = new Intent("myvst.intent.action.VodTypeActivity").putExtra("vodtype", "1");
            } else if (name.equals("电视剧")) {
                intent = new Intent("myvst.intent.action.VodTypeActivity").putExtra("vodtype", "2");
            } else if (name.equals("动漫")) {
                intent = new Intent("myvst.intent.action.VodTypeActivity").putExtra("vodtype", "3");
            } else if (name.equals("综艺")) {
                intent = new Intent("myvst.intent.action.VodTypeActivity").putExtra("vodtype", "4");
            } else if (name.equals("记录⽚")) {
                intent = new Intent("myvst.intent.action.VodTypeActivity").putExtra("vodtype", "5");
            } else if (name.equals("全部应⽤")) {
                intent = new Intent("myvst.intent.action.ApplicationActivity");
            } else if (name.equals("应⽤市场")) {
                intent = new Intent("myvst.intent.action.AppMarketActivity");
            } else if (name.equals("偏好设置")) {
                intent = new Intent("myvst.intent.action.StartupSettingActivity");
            } else if (name.equals("播放设置")) {
                intent = new Intent("myvst.intent.action.VodSettingActivity");
            } else if (name.equals("速度优化")) {
                intent = new Intent("myvst.intent.action.ClearDataActivity");
            } else if (name.equals("壁纸设置")) {
                intent = new Intent("myvst.intent.action.WallpaperSettingActivity");
            } else if (name.equals("天⽓设置")) {
                intent = new Intent("myvst.intent.action.WeatherSettingActivity");
            } else if (name.equals("万花筒")) {
                intent = new Intent("myvst.intent.wemedia.WeMediaActivity");
            } else if (name.equals("体育")) {
                intent = new Intent("myvst.intent.sport.SportHomeActivity");
            } else if (name.equals("全球购")) {
                intent = new Intent("net.myvst.v2.action.ShoppingHomeActivity");
            } else if (name.equals("全球购搜索")) {
                intent = new Intent("com.vst.vstshopping.activity.search");
            } else if (name.equals("⽕热专区")) {
                intent = new Intent("myvst.intent.prefecture.PrefectureHomeActivity");
            } else {
                return false;
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (name.equals("系统设置")) {
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    context.startActivity(intent);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return false;
        }
//        return false;
    }

}
