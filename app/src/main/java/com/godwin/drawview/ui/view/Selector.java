package com.godwin.drawview.ui.view;

import android.widget.ImageView;

/**
 * Created by Godwin on 12/19/2017 10:53 AM for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class Selector {
    private ImageView selector;
    private ColorPickerListener colorPickerListener;

    public Selector(ImageView selector, ColorPickerListener colorPickerListener) {
        this.selector = selector;
        this.colorPickerListener = colorPickerListener;
    }

    public ImageView getSelector() {
        return selector;
    }

    public ColorPickerListener getColorPickerListener() {
        return colorPickerListener;
    }
}
