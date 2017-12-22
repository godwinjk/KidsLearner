package com.godwin.drawview.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.godwin.drawview.R;


/**
 * Created by Godwin on 12/19/2017 11:07 AM for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ColorPickerDialog extends AlertDialog.Builder {
    private ColorPicker mColorPicker;
    private ColorPickerListener mListener;
    private Dialog mDialog;

    public ColorPickerDialog(Context context, ColorPickerListener listener) {
        super(context);
        this.mListener = listener;
        init(context);
    }

    private void init(Context context) {
        View baseView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_color, null);

        mColorPicker = baseView.findViewById(R.id.color_picker);
        mColorPicker.setListener(mListener);

        setView(baseView);
        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onSelectColor(mColorPicker.getColor());
                dialogInterface.dismiss();
            }
        });
        mDialog = create();
    }

    public void setColor(int color) {
        mColorPicker.setColor(color);
    }
}
