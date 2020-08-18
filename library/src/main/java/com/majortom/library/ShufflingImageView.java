package com.majortom.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * MIT License
 * <p>
 * Copyright (c) 2020 EdwardZhang AKA MAJOR TOM
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class ShufflingImageView extends View {

    private Bitmap bitmaps;

    private float speed = 2;

    private Rect clipBounds = new Rect();

    private float offset = -200;

    private boolean isStarted;

    private List<String> mPathList = new ArrayList<>();

    public ShufflingImageView(Context context) {
        this(context, null);
    }

    public ShufflingImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShufflingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.ShufflingImageView);
        int speed = typedArray.getInteger(R.styleable.ShufflingImageView_shuffling_speed, 2);
        if (speed > 0) {
            this.speed = speed;
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility != VISIBLE) {
            stop();
        } else {
            start();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            super.onDraw(canvas);
            if (canvas == null || bitmaps == null) {
                return;
            }

            canvas.getClipBounds(clipBounds);

            canvas.rotate(-20);
            float left = offset;
            try {
                for (; left < clipBounds.width() * 3 >> 1; ) {
                    int width = bitmaps.getWidth();
                    canvas.drawBitmap(bitmaps, getBitmapLeft(width, left), 0, null);
                    left += width;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

            if (isStarted && speed != 0) {
                offset -= abs(speed);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    postInvalidateOnAnimation();
                }
            }
        }
    }

    private float getBitmapLeft(float layerWidth, float left) {
        if (speed < 0) {
            return clipBounds.width() - layerWidth - left;
        } else {
            return left;
        }
    }

    private Bitmap add2Bitmap(ArrayList<Bitmap> bitmaps) {
        try {
            if (bitmaps.size() % 2 != 0) {
                bitmaps.add(bitmaps.get(bitmaps.size() >> 1));
            }
            int border = 30;
            int length = bitmaps.get(0).getWidth();
            int halfSize = bitmaps.size() >> 1;
            int width = (length + border) * halfSize;
            int height = length * 2 + border;
            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(result);
            for (int i = 0; i < bitmaps.size(); i++) {
                if (i < halfSize) {
                    canvas.drawBitmap(bitmaps.get(i), (length + border) * i, 0, null);
                } else {
                    canvas.drawBitmap(bitmaps.get(i), (length + border) * (i - halfSize), length + border, null);
                }
            }
            return result;
        } catch (Throwable ignored) {
        }
        return null;
    }

    private boolean needRefresh(final List<String> imagePathList) {
        if (imagePathList != null && imagePathList.size() == mPathList.size()) {
            for (int i = 0; i < mPathList.size(); i++) {
                if (!imagePathList.get(i).equals(mPathList.get(i))) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Start the animation
     */
    private void start() {
        if (!isStarted) {
            isStarted = true;
        }
        postInvalidateOnAnimation();
    }

    /**
     * Stop the animation
     */
    private void stop() {
        if (isStarted) {
            isStarted = false;
            invalidate();
        }
    }

    /**
     * Set speed of flow running rate
     *
     * @param speed is the Image Shuffling flow running speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
        if (isStarted) {
            postInvalidateOnAnimation();
        }
    }

    /**
     * Set a imagePathList as source of Shuffling flow
     * this function would use top 6 paths as defult
     *
     * @param imagePathList a image paths list
     */
    public void setImagePathList(final List<String> imagePathList) {
        if (!needRefresh(imagePathList)) {
            return;
        }
        mPathList.clear();
        mPathList.addAll(imagePathList);
        if (imagePathList != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<Bitmap> bitmapList = new ArrayList<>();
                    for (int i = 0; i < imagePathList.size(); i++) {
                        bitmapList.add(CommonUtils.getScaleBitmap(imagePathList.get(i), CommonUtils.dip2px(getContext(), 120), CommonUtils.dip2px(getContext(), 120)));
                    }
                    Bitmap store = bitmaps;
                    bitmaps = add2Bitmap(bitmapList);
                    if (store != null) {
                        store.recycle();
                    }

                    if (isStarted) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            postInvalidateOnAnimation();
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * Set the defult Image when image not scan finished as placeholder
     *
     * @param id  The resource id of the image data
     * @param num The resurce num of the Shuffling flow loaded
     */
    public void setImagePlaceholder(final int id, final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Bitmap> bitmapList = new ArrayList<>();
                for (int i = 0; i < num; i++) {
                    bitmapList.add(CommonUtils.getScaleBitmapByRes(getResources(), id, CommonUtils.dip2px(getContext(), 120), CommonUtils.dip2px(getContext(), 120)));
                }
                bitmaps = add2Bitmap(bitmapList);
                if (isStarted) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        postInvalidateOnAnimation();
                    }
                }
            }
        }).start();
    }

}
