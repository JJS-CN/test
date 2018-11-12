package com.example.admin.test.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.admin.test.BaseActivity;
import com.example.admin.test.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 说明：类似excel，可上下和左右拖动的布局
 * 通过监听scrollview滚动来完成上下滚动的联动。
 * 通过HorizontalScrollView，实现左右的联动
 * 注意：此demo只考虑当前需求，所以Left和info的item需要高度一致，Top和info的item总宽度需要一致。不一致情况请自行测试
 * Created by jjs on 2018/10/27.
 */

public class ChannelGuideActivity extends BaseActivity {

    @BindView(R.id.tv_day)
    TextView mTvDay;
    @BindView(R.id.rv_week)
    RecyclerView mRvWeek;
    @BindView(R.id.rv_left)
    RecyclerView mRvLeft;
    @BindView(R.id.rv_top)
    RecyclerView mRvTop;
    @BindView(R.id.rv_info)
    RecyclerView mRvInfo;

    @BindView(R.id.cl_toast)
    ConstraintLayout mClToast;//底部框
    @BindView(R.id.tv_time)
    TextView mTvTime;//时间点
    @BindView(R.id.tv_name)
    TextView mTvName;//频道号与频道名称
    @BindView(R.id.tv_info)
    TextView mTvInfo;//节目名称
    @BindView(R.id.tv_details)
    TextView mTvDetails;//详细说明


    private BaseQuickAdapter mWeekAdapter;
    private List<String> mWeekList;
    private String mWeekCheck;

    private InfoEntity mChooseInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_guide);
        ButterKnife.bind(this);
        initialView();
    }


    private void showSnake() {
        if (mChooseInfo == null) {
            mClToast.setVisibility(View.GONE);
        } else {
            //todo mChooseInfo详情获取
            mClToast.setVisibility(View.VISIBLE);
            mTvTime.setText("08:00");
            mTvName.setText("07" + " " + "AUGS");
            mTvInfo.setText("当前播放" + mChooseInfo.title);
            mTvDetails.setText("xxxxx节目详情");
        }
    }

    protected void initialView() {
        mWeekList = new ArrayList<>();
        mWeekList.add("MONDAY");
        mWeekList.add("TUESDAY");
        mWeekList.add("WEDNESDAY");
        mWeekList.add("THURSDAY");
        mWeekList.add("FRIDAY");
        mWeekList.add("SATURDAY");
        mWeekList.add("SUNDAY");
        //todo 判断星期 日期
        mWeekCheck = "MONDAY";
        mTvDay.setText("WEDNEDAY 15 AUG");
        mWeekAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_week) {
            @Override
            protected void convert(BaseViewHolder helper, final String item) {
                helper.setText(R.id.tv_title, item)
                        .setBackgroundColor(R.id.tv_title, item.equals(mWeekCheck) ? Color.parseColor("#99a1a1a1") : Color.TRANSPARENT)
                        .itemView
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mWeekCheck = item;
                                notifyDataSetChanged();
                            }
                        });
            }
        };
        mRvWeek.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvWeek.setAdapter(mWeekAdapter);
        mWeekAdapter.setNewData(mWeekList);

        //设置内容，per为整行的长度百分比，此demo总长为2400dp；因为每个item为100dp。有24个
        List<String> timers = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            timers.add(i / 2 + ":" + (i % 2 == 0 ? "00" : "30"));
        }
        final List<InfoEntity> infos = new ArrayList<>();
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
        infos.add(new InfoEntity("info" + "4-0", 0.105));
        infos.add(new InfoEntity("info" + "4-1", 0.1));
        infos.add(new InfoEntity("info" + "4-2", 0.045));
        infos.add(new InfoEntity("info" + "4-3", 0.35));
        infos.add(new InfoEntity("info" + "4-4", 0.4));
        infos.add(new InfoEntity("info" + "5-0", 0.105));
        infos.add(new InfoEntity("info" + "5-1", 0.1));
        infos.add(new InfoEntity("info" + "5-2", 0.045));
        infos.add(new InfoEntity("info" + "5-3", 0.35));
        infos.add(new InfoEntity("info" + "5-4", 0.4));
        infos.add(new InfoEntity("info" + "6-0", 0.105));
        infos.add(new InfoEntity("info" + "6-1", 0.1));
        infos.add(new InfoEntity("info" + "6-2", 0.045));
        infos.add(new InfoEntity("info" + "6-3", 0.35));
        infos.add(new InfoEntity("info" + "6-4", 0.4));
        infos.add(new InfoEntity("info" + "7-0", 0.105));
        infos.add(new InfoEntity("info" + "7-1", 0.1));
        infos.add(new InfoEntity("info" + "7-2", 0.045));
        infos.add(new InfoEntity("info" + "7-3", 0.35));
        infos.add(new InfoEntity("info" + "7-4", 0.4));

        List<String> mChnnelsList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            mChnnelsList.add("XXGS" + i);
        }


        BaseQuickAdapter mLeftAdpt = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_channel, mChnnelsList) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item)
                        .setText(R.id.tv_code, "01");
            }
        };
        mRvLeft.setLayoutManager(new LinearLayoutManager(this));
        mRvLeft.setAdapter(mLeftAdpt);

        BaseQuickAdapter mTopAdpt = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_timer, timers) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_title, item);
            }
        };
        mRvTop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvTop.setAdapter(mTopAdpt);

        BaseQuickAdapter mInfoAdpt = new BaseQuickAdapter<InfoEntity, BaseViewHolder>(R.layout.item_info, infos) {
            @Override
            protected void convert(BaseViewHolder helper, final InfoEntity item) {
                LinearLayout mCl = helper.getView(R.id.ll);
                ViewGroup.LayoutParams lp = mCl.getLayoutParams();
                lp.width = (int) (dp2px(2400) * item.per);
                mCl.setLayoutParams(lp);

                helper.setText(R.id.tv_title, item.title)
                        .setBackgroundColor(R.id.tv_title, mChooseInfo == null || !mChooseInfo.equals(item) ? Color.parseColor("#8C155683") : Color.parseColor("#B3000000"))
                        .itemView
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mChooseInfo = item;
                                notifyDataSetChanged();
                                showSnake();
                            }
                        });
            }
        };
        mRvInfo.setLayoutManager(new FlowLayoutManager());
        mRvInfo.setAdapter(mInfoAdpt);
        //此处返回的是实时滚动值，所以要不用值直接保存，要不就这样直接赋值使用
        mRvLeft.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
        //内容区域和左侧区域进行联动
        new LinearSnapHelper().attachToRecyclerView(mRvLeft);
        new LinearSnapHelper().attachToRecyclerView(mRvInfo);
        showSnake();
    }


    private float dp2px(float dp) {
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dp * dm.density;
    }


    @OnClick({R.id.iv_arrow_up, R.id.iv_arrow_down})
    public void onViewClicked(View view) {
        //todo 点击按钮滚动一格recyclerview，拿到第一个可见item的顶部位置，做对应数值的滚动。
        //这样每次点击都是先归位，之后的点击滚动将很正常
        LinearLayoutManager l = (LinearLayoutManager) mRvLeft.getLayoutManager();
        int position = l.findFirstCompletelyVisibleItemPosition();
        switch (view.getId()) {
            case R.id.iv_arrow_up:
                if (position > 0) {

                    View v = l.getChildAt(0);
                    mRvInfo.scrollBy(0, v.getTop() == 0 ? -v.getHeight() : v.getTop());
                    l.scrollToPositionWithOffset(--position, 0);

                }
                break;
            case R.id.iv_arrow_down:
                if (position < mRvLeft.getAdapter().getItemCount() - 1) {
                    View v = l.getChildAt(0);
                    mRvInfo.scrollBy(0, v.getTop() == 0 ? v.getHeight() : Math.abs(v.getTop()));
                    l.scrollToPositionWithOffset(v.getTop() == 0 ? ++position : position, 0);
                }
                break;
        }
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
