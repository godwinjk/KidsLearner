package com.godwin.handdrawview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by Godwin on 21-11-2017 10:44 for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class BitmapUtility {
    public static Bitmap changePixelColor(Bitmap bitmap, int originalColor, int replaceColor) {
        int[] allPixels = new int[bitmap.getHeight() * bitmap.getWidth()];

        bitmap.getPixels(allPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < allPixels.length; i++) {
            if (allPixels[i] == originalColor) {
                allPixels[i] = replaceColor;
            }
        }
        return Bitmap.createBitmap(allPixels, bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    public static Bitmap combine(Bitmap bm1, Bitmap bm2) {
        bm2 = Bitmap.createBitmap(bm2, bm2.getWidth()/2-bm1.getWidth()/2, bm2.getHeight()/2-bm1.getHeight()/2, bm1.getWidth(), bm1.getHeight());

        Bitmap bmOverlay = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), bm1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bm1, new Matrix(), null);
        canvas.drawBitmap(bm2, 0, 0, null);
        return bmOverlay;
    }

}
