package xiaoxi.tv;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import xiaoxi.tv.adapter.AppAdapter;
import xiaoxi.tv.adapter.ModuleAdapter;
import xiaoxi.tv.app.App;
import xiaoxi.tv.bean.AJson;
import xiaoxi.tv.bean.Game;
import xiaoxi.tv.tools.ContantUtil;
import xiaoxi.tv.ui.diy.test.ModuleLayoutManager;
import xiaoxi.tv.ui.diy.test.TvRecyclerView;

public class ModuleFocusActivity extends AppCompatActivity {

    private TvRecyclerView mTvRecyclerView;
    public int[] mStartIndex = {0, 2, 3, 5, 8, 9, 10, 11, 12, 14, 17, 18, 20};
    public int[] mItemRowSizes = {2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1};  //行
    public int[] mItemColumnSizes = {1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1};

    private class MyModuleLayoutManager extends ModuleLayoutManager {

        MyModuleLayoutManager(int rowCount, int orientation, int baseItemWidth, int baseItemHeight) {
            super(rowCount, orientation, baseItemWidth, baseItemHeight);
        }

        @Override
        protected int getItemStartIndex(int position) {
            if (position < mStartIndex.length) {
                return mStartIndex[position];
            } else {
                return 0;
            }
        }

        @Override
        protected int getItemRowSize(int position) {
            if (position < mItemRowSizes.length) {
                return mItemRowSizes[position];
            } else {
                return 1;
            }
        }

        @Override
        protected int getItemColumnSize(int position) {
            if (position < mItemRowSizes.length) {
                return mItemColumnSizes[position];
            } else {
                return 1;
            }
        }

        @Override
        protected int getColumnSpacing() {
            return getResources().
                    getDimensionPixelSize(R.dimen.recyclerView_item_space);
        }

        @Override
        protected int getRowSpacing() {
            return getResources().
                    getDimensionPixelSize(R.dimen.recyclerView_item_space);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        mTvRecyclerView = (TvRecyclerView) findViewById(R.id.tv_recycler_view);
        init();
//        getapp();
    }

    private void init() {
        ModuleLayoutManager manager = new MyModuleLayoutManager(3, LinearLayoutManager.HORIZONTAL,
                400, 260);
        mTvRecyclerView.setLayoutManager(manager);

        int itemSpace = getResources().
                getDimensionPixelSize(R.dimen.recyclerView_item_space);
        mTvRecyclerView.addItemDecoration(new SpaceItemDecoration(itemSpace));
        ModuleAdapter mAdapter = new ModuleAdapter(ModuleFocusActivity.this, mStartIndex.length);
        mTvRecyclerView.setAdapter(mAdapter);

        mTvRecyclerView.setOnItemStateListener(new TvRecyclerView.OnItemStateListener() {
            @Override
            public void onItemViewClick(View view, int position) {
                Toast.makeText(ModuleFocusActivity.this,
                        ContantUtil.TEST_DATAS[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemViewFocusChanged(boolean gainFocus, View view, int position) {
            }
        });
        mTvRecyclerView.setSelectPadding(35, 34, 35, 38);
    }

    List<Game> games = new ArrayList<>();

    private void getapp() {
        String url = App.requrl("getApp", "");
//        Log.e(tag, url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                try {

                    AJson<List<Game>> data = App.gson.fromJson(
                            json, new TypeToken<AJson<List<Game>>>() {
                            }.getType());
                    if (200 == data.getCode() || 0 == data.getCode()) {
//                        games.clear();
//                        games.addAll(data.getData());
//                        mAdapter.notifyDataSetChanged();
                        games = data.getData();
                        AppAdapter mAdapter = new AppAdapter(ModuleFocusActivity.this, games);
                        mTvRecyclerView.setAdapter(mAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        request.setRetryPolicy(new

                DefaultRetryPolicy(
                5 * 1000,//链接超时时间
                0,//重新尝试连接次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        App.queue.add(request);

    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = space;
            outRect.left = space;
        }
    }


}
