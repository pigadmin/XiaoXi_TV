package xiaoxi.tv.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import xiaoxi.tv.R;
import xiaoxi.tv.bean.Game;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    List<Game> games;

    public MyRecyclerAdapter(Context context, List<Game> games) {
        this.context = context;
        this.games = games;
    }

    private Gameadapter.OnItemClickListener mOnItemClickListener = null;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(Gameadapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_recycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.itemTV.setText("item" + position);
        holder.itemView.setTag(position);
        if (position % 3 == 0) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(400, 400);
            lp.setMargins(0, 0, 30, 30);
            holder.itemRL.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(200, 200);
            lp.setMargins(0, 0, 30, 30);
            holder.itemRL.setLayoutParams(lp);
        }
        holder.itemTV.setText(games.get(position).getName());
        Picasso.with(context).load(games.get(position).getIcon()).into(holder.game_bg);
//        holder.itemRL.setBackgroundColor(Color.RED);
//        Picasso.with(context).load(games.get(position).getIcon()).into(new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
//                holder.game_bg.setBackground(new BitmapDrawable(bitmap));
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable drawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable drawable) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTV;
        public RelativeLayout itemRL;
        public ImageView game_bg;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTV = (TextView) itemView.findViewById(R.id.itemTV);
            game_bg = itemView.findViewById(R.id.game_bg);
            itemRL = (RelativeLayout) itemView.findViewById(R.id.itemRL);
            itemView.setOnClickListener(MyRecyclerAdapter.this);
        }
    }
}
