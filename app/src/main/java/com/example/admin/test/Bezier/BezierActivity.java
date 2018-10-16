package com.example.admin.test.Bezier;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

import com.example.admin.test.R;

/**
 * 说明：
 * Created by jjs on 2018/8/31.
 */

public class BezierActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier);
        TimeRoteView roteView = findViewById(R.id.v);
        roteView.setCheckType(0);
        roteView.setOnCheckedListener(new TimeRoteView.OnCheckedListener() {
            @Override
            public void onSelect(TimeRoteView.RunType runType) {
                Log.e("select", runType == TimeRoteView.RunType.Run ? "跑步" : runType == TimeRoteView.RunType.Basketball ? "篮球" : runType == TimeRoteView.RunType.Weightlifting ? "举重" : "按钮");
            }

            @Override
            public void onCheck(TimeRoteView.RunType runType) {
                Log.e("check", runType == TimeRoteView.RunType.Run ? "跑步" : runType == TimeRoteView.RunType.Basketball ? "篮球" : runType == TimeRoteView.RunType.Weightlifting ? "举重" : "按钮");
            }
        });

        final FootView view = findViewById(R.id.foot);

        SeekBar bar = findViewById(R.id.seek);
        bar.setMax(1000);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int pro, boolean fromUser) {
                float progress = pro / 25f;
                FootView.FootEntity entity = new FootView.FootEntity(progress, progress, progress, progress, progress, progress, progress, progress);
                FootView.FootEntity entity2 = new FootView.FootEntity(progress, progress, progress, progress, progress, progress, progress, progress);
                view.setFootEntity(entity, entity2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
