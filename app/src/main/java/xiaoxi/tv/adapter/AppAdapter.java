package xiaoxi.tv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.List;

import xiaoxi.tv.R;
import xiaoxi.tv.bean.Game;
import xiaoxi.tv.tools.ContantUtil;
import xiaoxi.tv.tools.FULL;
import xiaoxi.tv.ui.ad.ResType;
import xiaoxi.tv.ui.ad.bean.Command;
import xiaoxi.tv.ui.ad.bean.Play;


public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private Context mContext;
    private int mItemCount;
    private List<Game> games;

    public AppAdapter(Context context, List<Game> games) {
        mContext = context;
//        mItemCount = itemCount;
        this.games = games;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(View.inflate(mContext, R.layout.adapter_app, null));
    }

    RecyclerViewHolder viewHolder;


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        viewHolder = (RecyclerViewHolder) holder;
//        viewHolder.mName.setText(position + "." + games.get(position).getName());
        try {


//            GradientDrawable drawable = (GradientDrawable) viewHolder.mFrameLayout.getBackground();
//            drawable.setColor(ContextCompat.getColor(mContext, ContantUtil.getRandColor()));
//            viewHolder.mName.setText(games.get(position).getName());
            ViewGone();
            String icon = games.get(position).getIcon();
            String temp = icon.substring(icon.lastIndexOf(".")).toLowerCase();
            int type = ResType.type.get(temp);
            switch (type) {
                case 1://ResImage
                    viewHolder.icon.setVisibility(View.VISIBLE);
                    if ("gif".equals(temp)) {
                        Glide.with(mContext).load(icon).asGif().into(viewHolder.icon);
                    } else {
                        Glide.with(mContext).load(icon).into(viewHolder.icon);
                    }
                    break;
                case 2://ResAudio
                case 3://ResVideo
//                    playvideo();
                    viewHolder.video.setVisibility(View.VISIBLE);
                    viewHolder.video.setVideoPath(icon);
                    break;

                default:
                    break;
            }



//            if (position == 3 || position == 6) {
//                viewHolder.ad_fr.setVisibility(View.GONE);
//                viewHolder.ad_re.setVisibility(View.VISIBLE);
//                viewHolder.msgimg.setVisibility(View.VISIBLE);
//                Picasso.with(mContext).load(games.get(position).getIcon()).into(viewHolder.msgimg);
//            } else {
//                viewHolder.ad_fr.setVisibility(View.VISIBLE);
//                viewHolder.ad_re.setVisibility(View.GONE);
//
//
//                Picasso.with(mContext).load(games.get(position).getIcon()).into(viewHolder.icon);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

//        return 12;
        return games.size();
    }


    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        FrameLayout mFrameLayout;
        TextView mName;
        ImageView icon;
        VideoView video;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            System.out.println("");
            icon = itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.tv_item_tip);
            mFrameLayout = itemView.findViewById(R.id.fl_main_layout);
            video = itemView.findViewById(R.id.video);
            FULL.star(video);
            video.setOnPreparedListener(AppAdapter.this);
            video.setOnCompletionListener(AppAdapter.this);
            video.setOnErrorListener(AppAdapter.this);
        }
    }


    public void ad(Command command) {

    }


    private void playweb() {
//        viewHolder.msgweb.setVisibility(View.VISIBLE);
//        viewHolder.msgweb.loadUrl(play.getSurl());
    }


    private void playvideo() {
//        viewHolder.video.setVisibility(View.VISIBLE);
//        viewHolder.video.setVideoPath(icon);
    }


    private void playimg() {
//        viewHolder.msgimg.setVisibility(View.VISIBLE);
//        Picasso.with(mContext).load(play.getSurl()).into(viewHolder.msgimg);
    }


    private void ViewGone() {
        viewHolder.icon.setVisibility(View.GONE);
        viewHolder.video.setVisibility(View.GONE);

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    @Override
    public boolean onError(MediaPlayer mp, int i, int i1) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setLooping(true);
        mp.start();
        mp.setVolume(0, 0);

    }
}
