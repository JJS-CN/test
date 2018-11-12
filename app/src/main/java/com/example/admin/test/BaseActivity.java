package com.example.admin.test;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * 说明：
 * Created by jjs on 2018/11/12.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            if (getIntent() != null && getIntent().getStringExtra("title") != null) {
                getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
            } else {
                getSupportActionBar().setTitle("返回");
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
