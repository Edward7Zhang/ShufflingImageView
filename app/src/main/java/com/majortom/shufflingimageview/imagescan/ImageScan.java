package com.majortom.shufflingimageview.imagescan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ImageScan {

    private List<String> mScanPath;

    private ScanCallBack mScanCallBack;

    private volatile boolean mStop = false;

    private List<File> mImageFound = new ArrayList<>();

    public ImageScan(List<String> scanPath, ScanCallBack scanCallBack) {
        mScanPath = scanPath;
        mScanCallBack = scanCallBack;
    }

    public static List<String> getMediaImageDir() {
        List<String> dirs = new ArrayList<>(2);
        dirs.add(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera");
        return dirs;
    }

    public void scan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listFiles();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (mScanCallBack != null) {
                            mScanCallBack.scanFinish(new ArrayList<>(mImageFound));
                            mImageFound.clear();
                            if (mScanPath != null) {
                                mScanPath.clear();
                            }

                            mScanCallBack = null;
                        }
                    }
                });
            }
        }, "image_scan").start();
    }

    private void listFiles() {
        for (String path : mScanPath) {
            File fileDir = new File(path);
            if (fileDir.exists() && fileDir.isDirectory()) {
                innerListFiles(0, fileDir);
            }
        }
    }

    private void innerListFiles(int depth, final File directory) {
        if (mStop) {
            return;
        }

        if (depth > 10) {
            return;
        }
        final File[] found = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || isAccept(pathname.getAbsolutePath());
            }
        });

        if (found != null) {
            for (final File file : found) {
                if (mStop) {
                    return;
                }
                if (file.isDirectory()) {
                    innerListFiles(depth + 1, file);
                } else {
                    mImageFound.add(file);
                }
            }
        }
    }

    private boolean isAccept(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        return !isImageDamage(path);
    }

    private boolean isImageDamage(String imageFilePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, options);
        int w = options.outWidth;
        int h = options.outHeight;
        if (bitmap != null) {
            bitmap.recycle();
        }
        boolean isDamage = (w <= 0 || h <= 0);
        return isDamage;
    }

    public interface ScanCallBack {
        void scanFinish(List<File> imageList);
    }
}
