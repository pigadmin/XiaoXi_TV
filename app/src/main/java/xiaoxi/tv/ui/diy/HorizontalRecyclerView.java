package xiaoxi.tv.ui.diy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class HorizontalRecyclerView extends RecyclerView {

    public HorizontalRecyclerView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public HorizontalRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public HorizontalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        if (dx > 0) {
            super.scrollBy(dx + 100, dy);
        } else if (dx < 0) {
            super.scrollBy(dx - 100, dy);
        } else {
            super.scrollBy(dx, dy);
        }
    }
}
