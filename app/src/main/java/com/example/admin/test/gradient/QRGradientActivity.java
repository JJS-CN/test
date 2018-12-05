package com.example.admin.test.gradient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.admin.test.R;

public class QRGradientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradient);
        HeartbeatView mHear = findViewById(R.id.hear);
        mHear.setData(new int[]{15, 15, 15, 26, 48, 69, 128, 80, 50, 5});
    }
}
