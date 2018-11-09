package com.example.admin.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.admin.test.Bezier.BezierActivity;
import com.example.admin.test.chart.ChartActivity;
import com.example.admin.test.expandable.MultisActivity;
import com.example.admin.test.list.RecyActivity;
import com.example.admin.test.picture.PictureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * Created by jjs on 2018/9/7.
 */

public class LauncherActivity extends AppCompatActivity {
    RecyclerView mRv;
    BaseQuickAdapter mAdapter;
    List<String> mTabList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mRv = findViewById(R.id.rv);
        mTabList = new ArrayList<>();
        mTabList.add("类似QQ的三级分组adapter");
        mTabList.add("SvgPath绘制复杂图形");
        mTabList.add("自定义图表");
        mTabList.add("类似Excel组合");
        mTabList.add("自定义LayoutManager");
        mAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_label, mTabList) {

            @Override
            protected void convert(BaseViewHolder helper, final String item) {
                helper.setText(R.id.tv_name, item)
                        .itemView
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (item) {
                                    case "类似QQ的三级分组adapter":
                                        toGo(MultisActivity.class);
                                        break;
                                    case "SvgPath绘制复杂图形":
                                        toGo(BezierActivity.class);
                                        break;
                                    case "自定义图表":
                                        toGo(ChartActivity.class);
                                        break;
                                    case "类似Excel组合":
                                        toGo(PictureActivity.class);
                                        break;
                                    case "自定义LayoutManager":
                                        toGo(RecyActivity.class);
                                        break;

                                }
                            }
                        });
            }
        };
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mAdapter);
    }

    private void toGo(Class clazz) {
        startActivity(new Intent(this, clazz));
    }
}
