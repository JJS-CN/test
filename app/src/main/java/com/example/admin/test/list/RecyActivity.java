package com.example.admin.test.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.admin.test.R;
import com.example.admin.test.baseView.CashBackView;
import com.example.admin.test.baseView.LevelProgressView;
import com.example.admin.test.baseView.RulerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * Created by jjs on 2018/8/16.
 */

public class RecyActivity extends AppCompatActivity {
    RecyclerView mRv;
    BaseQuickAdapter mAdapter;

    int createCount;

    private static final String TAG = "RecyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recy);
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new PileUpLayoutManager());

        final List<String> strings = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if (i % 2 == 0) {
                strings.add("测试" + i);
            } else {
                strings.add("测\n\n试" + i);
            }
        }
        mAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.itemsss, strings) {

            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_text, item);
            }
        };
        mRv.setAdapter(mAdapter);

        RulerView view = findViewById(R.id.v);
        view.setSelectListener(new RulerView.OnSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.e(TAG, "onSelect: " + position);
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
