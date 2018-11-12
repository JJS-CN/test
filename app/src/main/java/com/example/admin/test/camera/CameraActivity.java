package com.example.admin.test.camera;

import android.os.Bundle;

import com.example.admin.test.BaseActivity;
import com.example.admin.test.R;

/**
 * 说明：相机
 * Created by jjs on 2018/9/6.
 */

public class CameraActivity extends BaseActivity {

    private CameraSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mSurfaceView = findViewById(R.id.camera);
        //设置视频名称，用于生成视频输出地址，注意唯一性
        mSurfaceView.setVideoName("orderId");
        //设置摄像头选择
        mSurfaceView.setDefaultCamera(true);
        //获取视频输出地址
        String path = mSurfaceView.getVideoOutputPath();
    }

    private void open() {
        //注意申请权限：Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
        //有时会出现camera未准备完成的情况，所以此时延迟一段时间进行启动
        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //开启录制
                boolean isStartRecord = mSurfaceView.startRecord();
                //此时将添加数据到greenDao
                if (isStartRecord) {//开始录制后存入数据库中

                }
            }
        }, 500);

    }

    @Override
    protected void onDestroy() {
        //关闭时注意释放camera，否则其他程序将无法再获取camera
        if (mSurfaceView != null) {
            if (mSurfaceView.isRecording()) {
                mSurfaceView.stopRecord();
            }
            mSurfaceView.closeCamera();
        }
        super.onDestroy();
    }

}

