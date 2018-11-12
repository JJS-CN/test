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
import com.example.admin.test.camera.CameraActivity;
import com.example.admin.test.chart.ChartActivity;
import com.example.admin.test.expandable.MultisActivity;
import com.example.admin.test.other.OtherActivity;
import com.example.admin.test.recyclerview.ChannelGuideActivity;
import com.example.admin.test.recyclerview.RecyActivity;

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
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setTitle("启动页");

        mRv = findViewById(R.id.rv);
        mTabList = new ArrayList<>();
        mTabList.add("类似QQ的三级分组adapter");
        mTabList.add("SvgPath绘制复杂图形");
        mTabList.add("自定义图表");
        mTabList.add("类似Excel组合");
        mTabList.add("Camera拍照录像功能");
        mTabList.add("Recycler高级功能");
        mTabList.add("其他");
        mAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_label, mTabList) {

            @Override
            protected void convert(BaseViewHolder helper, final String item) {
                helper.setText(R.id.tv_name, item)
                        .itemView
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                title = item;
                                switch (item) {
                                    case "类似QQ的三级分组adapter":
                                        toGo(MultisActivity.class);
                                        break;
                                    case "SvgPath绘制复杂图形":
                                        toGo(OtherActivity.class);
                                        break;
                                    case "自定义图表":
                                        toGo(ChartActivity.class);
                                        break;
                                    case "类似Excel组合":
                                        toGo(ChannelGuideActivity.class);
                                        break;
                                    case "自定义LayoutManager":
                                        toGo(RecyActivity.class);
                                        break;
                                    case "Camera拍照录像功能":
                                        toGo(CameraActivity.class);
                                        break;
                                    case "其他":
                                        toGo(OtherActivity.class);
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
        Intent intent = new Intent(this, clazz);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
