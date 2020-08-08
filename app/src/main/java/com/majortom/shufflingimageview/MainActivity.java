package com.majortom.shufflingimageview;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.majortom.library.ShufflingImageView;
import com.majortom.shufflingimageview.imagescan.ImageScan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ShufflingImageView mShuffingImageView;
    private ShufflingImageView mShuffingImageView1;
    private ShufflingImageView mShuffingImageView2;
    private ShufflingImageView mShuffingImageView3;

    private List<File> imagePathList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShuffingImageView = findViewById(R.id.shuffling_image_view);
        mShuffingImageView1 = findViewById(R.id.shuffling_image_view1);
        mShuffingImageView2 = findViewById(R.id.shuffling_image_view2);
        mShuffingImageView3 = findViewById(R.id.shuffling_image_view3);
        mShuffingImageView.setSpeed(1);
        mShuffingImageView1.setSpeed(2);
        mShuffingImageView2.setSpeed(3);
        mShuffingImageView3.setSpeed(4);
        mShuffingImageView.setImagePlaceholder(R.drawable.placeholder1, 6);
        mShuffingImageView1.setImagePlaceholder(R.drawable.placeholder2, 6);
        mShuffingImageView2.setImagePlaceholder(R.drawable.placeholder3, 6);
        mShuffingImageView3.setImagePlaceholder(R.drawable.placeholder4, 6);

        requestStoragePermission();

        new ImageScan(ImageScan.getMediaImageDir(), new ImageScan.ScanCallBack() {
            @Override
            public void scanFinish(List<File> imageList) {
                imagePathList.clear();
                imagePathList.addAll(imageList);
                List<String> thumbList = new ArrayList<>(6);

                for (int i = 0; i < 6 && i < imagePathList.size(); i++) {
                    thumbList.add(imagePathList.get(i).getPath());
                }
                mShuffingImageView.setImagePathList(thumbList);
            }
        }).scan();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }
}