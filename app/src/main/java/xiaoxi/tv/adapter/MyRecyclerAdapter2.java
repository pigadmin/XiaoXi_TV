package xiaoxi.tv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xiaoxi.tv.R;
import xiaoxi.tv.bean.Game;

public class MyRecyclerAdapter2 extends RecyclerView.Adapter<MyRecyclerAdapter2.ViewHolder> implements View.OnClickListener {

    private Context context;
    List<Game> games;

    public MyRecyclerAdapter2(Context context, List<Game> games) {
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
        holder.itemTV.setText(games.get(position).getName());
        Picasso.with(context).load(games.get(position).getIcon()).into(holder.game_bg);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(200, 200);
        lp.setMargins(0, 0, 30, 30);
        holder.itemRL.setLayoutParams(lp);

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
            itemTV = itemView.findViewById(R.id.itemTV);
            game_bg = itemView.findViewById(R.id.game_bg);
            itemRL = itemView.findViewById(R.id.itemRL);
            itemTV.setOnClickListener(MyRecyclerAdapter2.this);
        }
    }
}
