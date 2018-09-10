package xiaoxi.tv.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xiaoxi.tv.R;
import xiaoxi.tv.bean.Game;
import xiaoxi.tv.ui.diy.FocusRelativeLayout;

/**
 * Created by zhu on 2017/9/26.
 */

public class Gameadapter extends RecyclerView.Adapter<Gameadapter.ViewHolder> implements View.OnClickListener {
    List<Game> menus;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView game_bg;
        TextView game_name;
        FocusRelativeLayout menu_linearlayout;

        public ViewHolder(View v) {
            super(v);
            menu_linearlayout = v.findViewById(R.id.menu_linearlayout);
            game_bg = v.findViewById(R.id.game_bg);
            game_name = v.findViewById(R.id.game_name);
//            v.setOnClickListener(Game2dapter.this);
            menu_linearlayout.setOnClickListener(Gameadapter.this);
        }
    }

    public Gameadapter(Activity activity, List<Game> menus) {
        this.context = activity;
        this.menus = menus;
    }

    private OnItemClickListener mOnItemClickListener = null;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public Gameadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_game, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            holder.itemView.setTag(position);
            holder.game_name.setText(position + "." + menus.get(position).getName());
//            holder.game_name.setText(position + "");


//            StaggeredGridLayoutManager.LayoutParams lp = new StaggeredGridLayoutManager.LayoutParams(230, StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT);
//            holder.menu_linearlayout.setLayoutParams(lp);

            if (position == 0) {
                StaggeredGridLayoutManager.LayoutParams lp = new StaggeredGridLayoutManager.LayoutParams(400, StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT);
                lp.setMargins(0, 0, 30, 30);
                holder.menu_linearlayout.setLayoutParams(lp);
            } else {
                StaggeredGridLayoutManager.LayoutParams lp = new StaggeredGridLayoutManager.LayoutParams(200, StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT);
                lp.setMargins(0, 0, 30, 30);
                holder.menu_linearlayout.setLayoutParams(lp);
            }
            Picasso.with(context).load(menus.get(position).getIcon()).into(holder.game_bg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }
}
