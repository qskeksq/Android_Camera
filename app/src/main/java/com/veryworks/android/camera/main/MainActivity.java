package com.veryworks.android.camera.main;

import android.content.Intent;
import android.view.View;

import com.veryworks.android.camera.CameraActivity;
import com.veryworks.android.camera.GalleryActivity;
import com.veryworks.android.camera.R;

public class MainActivity extends BaseActivity {

    @Override
    public void init() {
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }
}
