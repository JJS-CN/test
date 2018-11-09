package com.example.admin.test.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.admin.test.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 说明：
 * Created by jjs on 2018/8/30.
 */

public class ChartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        SportsChart mSpChart = findViewById(R.id.c);
        List<SportsChart.ChartEntity> mSpList = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            mSpList.add(new SportsChart.ChartEntity(new Random().nextInt(10), "测" + i));
        }
        mSpChart.setListener(new SportsChart.OnLeftTextChangeListener() {
            @Override
            public String onChange(int value) {
                return value + " km";
            }
        });
        mSpChart.setEntities(mSpList);

        RunBarChart mBarChart = findViewById(R.id.bar);
        List<RunBarChart.ChartEntity> mbarList = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            mbarList.add(new RunBarChart.ChartEntity(new Random().nextInt(10), new Random().nextInt(20), "测" + i));
        }
        mBarChart.setListener(new RunBarChart.OnLeftTextChangeListener() {
            @Override
            public String onChange(int value) {
                return value + " km";
            }
        });
        mBarChart.setEntities(mbarList);

        DurableChart durableChart = findViewById(R.id.durable);

        List<DurableChart.ChartEntity> mDurableList = new ArrayList<>();
        mDurableList.add(new DurableChart.ChartEntity(0.6f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.3f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.5f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.8f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.7f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.3f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.6f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.2f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.7f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.9f, "测"));
        mDurableList.add(new DurableChart.ChartEntity(0.5f, "测"));
        durableChart.setEntities(mDurableList);
    }
}
