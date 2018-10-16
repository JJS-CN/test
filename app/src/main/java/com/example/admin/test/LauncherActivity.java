package com.example.admin.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.admin.test.picture.PictureActivity;

/**
 * 说明：
 * Created by jjs on 2018/9/7.
 */

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, PictureActivity.class));
        finish();
    }
}
