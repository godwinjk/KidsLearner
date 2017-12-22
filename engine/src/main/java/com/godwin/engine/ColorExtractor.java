package com.godwin.engine;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Arrays;

/**
 * Created by Godwin on 21-11-2017 11:26 for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class ColorExtractor {
    public static Bitmap getPixelWithColor(Bitmap bitmap, int color) {
        int[] allPixels = new int[bitmap.getHeight() * bitmap.getWidth()];

        bitmap.getPixels(allPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < allPixels.length; i++) {
            if (allPixels[i] != color) {
                allPixels[i] = Color.TRANSPARENT;
            }
        }
        return Bitmap.createBitmap(allPixels, bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    public static int[] getPixels(Bitmap bitmap) {
        int[] allPixels = new int[bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(allPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return allPixels;
    }

    public static int[][] getPixels2d(Bitmap bitmap) {
        int[] allPixels = new int[bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(allPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int[][] pixels2d = new int[bitmap.getHeight()][bitmap.getWidth()];
        for (int i = 0; i < bitmap.getHeight(); i += bitmap.getWidth()) {
            pixels2d[i] = Arrays.copyOfRange(allPixels, i, i + bitmap.getWidth());
        }
        return pixels2d;
    }
}
