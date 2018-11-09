package com.example.admin.test.list;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.admin.test.R;
import com.example.admin.test.baseView.CashBackView;
import com.example.admin.test.baseView.LevelProgressView;
import com.example.admin.test.baseView.RulerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 说明：
 * Created by jjs on 2018/8/16.
 */

public class RecyActivity extends AppCompatActivity {
    RecyclerView mRv;
    BaseQuickAdapter mAdapter;

    String ttttt;
    List<String> strings;
    int count;
    private static final String TAG = "RecyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recy);
        findViewById(R.id.btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapter.setNewData(strings);
                    }
                });
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinLayoutManager());
        mRv.setNestedScrollingEnabled(false);
        strings = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            strings.add(i + "测试" + (i % 3 == 0 ? new Random().nextInt(100000) : i));
        }
        mAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.itemsss, strings) {

            @Override
            protected void convert(final BaseViewHolder helper, final String item) {
                //Log.e("update", helper.getLayoutPosition() + "==" + ttttt);
                helper.setText(R.id.tv1, item);
                helper.setBackgroundColor(R.id.tv1, item.equals(ttttt) ? Color.MAGENTA : Color.WHITE);
                helper.itemView
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ttttt = item;
                                notifyDataSetChanged();
                            }
                        });
            }

            @Override
            public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                count++;
                Log.e("BaseRecyclerView", "onCreateViewHolder:"+count);
                return super.onCreateViewHolder(parent, viewType);
            }
        };
        mRv.setAdapter(mAdapter);
        RulerView view = findViewById(R.id.v);
        view.setSelectListener(new RulerView.OnSelectListener() {
            @Override
            public void onSelect(int position) {
                //Log.e(TAG, "onSelect: " + position);
            }
        });

        List<LevelProgressView.PoiEntity> mPoiList = new ArrayList<>();
        mPoiList.add(new LevelProgressView.PoiEntity(0, "等级0"));
        mPoiList.add(new LevelProgressView.PoiEntity(40, "等级1"));
        mPoiList.add(new LevelProgressView.PoiEntity(100, "等级2"));
        mPoiList.add(new LevelProgressView.PoiEntity(300, "等级3"));
        mPoiList.add(new LevelProgressView.PoiEntity(600, "等级4"));
/*        mPoiList.add(new LevelProgressView.PoiEntity(1000, "等级5"));
        mPoiList.add(new LevelProgressView.PoiEntity(1000, "等级5"));
        mPoiList.add(new LevelProgressView.PoiEntity(1000, "等级5"));
        mPoiList.add(new LevelProgressView.PoiEntity(1000, "等级5"));
        mPoiList.add(new LevelProgressView.PoiEntity(1000, "等级5"));
        mPoiList.add(new LevelProgressView.PoiEntity(1000, "等级5"));
        mPoiList.add(new LevelProgressView.PoiEntity(1000, "等级5"));*/

        LevelProgressView mLv = findViewById(R.id.lv);
        mLv.setMaxProgress(1000);
        mLv.setProgress(280);
        mLv.setPoiList(mPoiList);

      /*  final ListPopupWindow pop = new ListPopWindow(this);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setVerticalOffset(0);
        pop.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50000000")));//设置背景色
        pop.setModal(true);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.itemsss, R.id.tv_text, mPoiList);
        pop.setAdapter(adapter);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] a = new int[2];
                v.getLocationInWindow(a);
                pop.setHeight(getResources().getDisplayMetrics().heightPixels- a[1] - v.getHeight());
                pop.setAnchorView(v);
                pop.show();
            }
        });*/

        final CashBackView cv = findViewById(R.id.cv);
        cv.setMax(10000);

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.setValue(cv.getValue() + 1000);
            }
        });
    }

}
