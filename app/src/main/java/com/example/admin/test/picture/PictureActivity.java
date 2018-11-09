package com.example.admin.test.picture;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.admin.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * Created by jjs on 2018/9/7.
 */

public class PictureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        final RecyclerView mRvLeft = findViewById(R.id.rv_left);
        RecyclerView mRvTop = findViewById(R.id.rv_top);
        final RecyclerView mRvInfo = findViewById(R.id.rv_info);

        List<String> titles = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            titles.add("title" + i);
        }
        List<InfoEntity> infos = new ArrayList<>();
        infos.add(new InfoEntity("info" + "1-0", 0.3));
        infos.add(new InfoEntity("info" + "1-1", 0.2));
        infos.add(new InfoEntity("info" + "1-2", 0.1));
        infos.add(new InfoEntity("info" + "1-3", 0.3));
        infos.add(new InfoEntity("info" + "1-4", 0.1));
        infos.add(new InfoEntity("info" + "2-0", 0.1));
        infos.add(new InfoEntity("info" + "2-1", 0.3));
        infos.add(new InfoEntity("info" + "2-2", 0.3));
        infos.add(new InfoEntity("info" + "2-3", 0.1));
        infos.add(new InfoEntity("info" + "2-4", 0.2));
        infos.add(new InfoEntity("info" + "3-0", 0.2));
        infos.add(new InfoEntity("info" + "3-1", 0.3));
        infos.add(new InfoEntity("info" + "3-2", 0.2));
        infos.add(new InfoEntity("info" + "3-3", 0.2));
        infos.add(new InfoEntity("info" + "3-4", 0.1));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "4-0", 0.005));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.45));
        infos.add(new InfoEntity("info" + "4-4", 0.4));


        BaseQuickAdapter mLeftAdpt = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_top, titles) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_title, item);
            }
        };
        mRvLeft.setLayoutManager(new LinearLayoutManager(this));
        mRvLeft.setAdapter(mLeftAdpt);

        BaseQuickAdapter mTopAdpt = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_top, titles) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_title, item);
            }
        };
        mRvTop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvTop.setAdapter(mTopAdpt);

        BaseQuickAdapter mInfoAdpt = new BaseQuickAdapter<InfoEntity, BaseViewHolder>(R.layout.item_top, infos) {
            @Override
            protected void convert(BaseViewHolder helper, InfoEntity item) {
                ConstraintLayout mCl = helper.getView(R.id.ll);
                helper.setText(R.id.tv_title, item.title);
                ViewGroup.LayoutParams lp = mCl.getLayoutParams();
                lp.width = (int) (dp2px(2400) * item.per);
                mCl.setLayoutParams(lp);

            }
        };

        mRvLeft.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    mRvInfo.scrollBy(dx, dy);
                }

            }
        });
        mRvInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    mRvLeft.scrollBy(dx, dy);
                }

            }
        });
        new LinearSnapHelper().attachToRecyclerView(mRvLeft);


       /* CoordinatorLayout col = findViewById(R.id.col);
        Snackbar snackbar =
                Snackbar.make(col, "过年了，过年了", Snackbar.LENGTH_LONG)
                       .setDuration(10000)
                        .setAction("去过年", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(PictureActivity.this, "你点击了右边的按钮", Toast.LENGTH_LONG).show();
                            }
                        });

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        View child = LayoutInflater.from(layout.getContext()).inflate(R.layout.itemsss, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        layout.addView(child, 0, lp);
        snackbar.show();
*/
    }

    private float dp2px(float dp) {
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dp * dm.density + 0.5f;
    }

    private class InfoEntity {
        public String title;
        public double per;

        public InfoEntity(String title, double per) {
            this.title = title;
            this.per = per;
        }
    }
}
