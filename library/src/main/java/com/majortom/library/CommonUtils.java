package com.majortom.library;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * MIT License
 *
 * Copyright (c) 2020 EdwardZhang AKA MAJOR TOM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

public class CommonUtils {

    public static int dip2px(Context context, float dpValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {
        }
        return 0;
    }

    private static Bitmap cropCenterBitmap(Bitmap bitmap) {
        final int length = Math.min(bitmap.getWidth(), bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, Math.abs(bitmap.getWidth() - length) >> 1, Math.abs(bitmap.getHeight() - length) >> 1, length, length);
    }

    public static Bitmap getScaleBitmap(String file_path, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file_path, options);
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            options.inJustDecodeBounds = false;
            options.inSampleSize = getFitInSampleSize(reqWidth, reqHeight, options);
            final Bitmap bitmap = BitmapFactory.decodeFile(file_path, options);
            if (bitmap == null){
                return null;
            }

            return Bitmap.createScaledBitmap(cropCenterBitmap(bitmap), reqWidth, reqHeight, true);
        } catch (Throwable t) {
        }
        return null;
    }

    public static Bitmap getScaleBitmapByRes(Resources res, int id, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            options.inJustDecodeBounds = false;
            options.inSampleSize = getFitInSampleSize(reqWidth, reqHeight, options);
            final Bitmap bitmap = BitmapFactory.decodeResource(res, id, options);
            if (bitmap == null){
                return null;
            }

            return Bitmap.createScaledBitmap(cropCenterBitmap(bitmap), reqWidth, reqHeight, true);
        } catch (Throwable t) {
        }
        return null;
    }


    public static int getFitInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options) {
        int inSampleSize = 1;
        if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
            int widthRatio = Math.round((float) options.outWidth / (float) reqWidth);
            int heightRatio = Math.round((float) options.outHeight / (float) reqHeight);
            inSampleSize = Math.min(widthRatio, heightRatio);
        }
        return inSampleSize;
    }
}
