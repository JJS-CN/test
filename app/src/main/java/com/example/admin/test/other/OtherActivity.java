package com.example.admin.test.other;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import com.example.admin.test.BaseActivity;
import com.example.admin.test.R;
import com.example.admin.test.other.view.FootView;

/**
 * 说明：
 * Created by jjs on 2018/8/31.
 */

public class OtherActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier);

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
