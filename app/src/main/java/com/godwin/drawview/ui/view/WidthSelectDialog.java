package com.godwin.drawview.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.godwin.drawview.R;

/**
 * Created by Godwin on 12/19/2017 4:03 PM for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class WidthSelectDialog extends AlertDialog.Builder {
    private Dialog mDialog;
    private SeekBar.OnSeekBarChangeListener mSeekbarChangedListener;
    private int current;
    private int offset;

    public WidthSelectDialog(Context context,
                             int current,
                             int offset,
                             SeekBar.OnSeekBarChangeListener listener) {
        super(context);
        this.mSeekbarChangedListener = listener;
        this.current = current;
        this.offset = offset;

        init(context);
    }

    private void init(Context context) {
        View baseView = LayoutInflater.from(context).inflate(R.layout.layout_seekbar, null);
        AppCompatSeekBar seekBar = baseView.findViewById(R.id.seekbar);
        final AppCompatTextView tv = baseView.findViewById(R.id.tv_seekbar_text);
        final AppCompatTextView tvOk = baseView.findViewById(R.id.tvok);
        tv.setText(String.valueOf((current - offset)));
        seekBar.setProgress(current - offset);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (null != mSeekbarChangedListener) {
                    mSeekbarChangedListener.onProgressChanged(seekBar, i, b);
                }
                tv.setText(String.valueOf((offset + i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (null != mSeekbarChangedListener) {
                    mSeekbarChangedListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (null != mSeekbarChangedListener) {
                    mSeekbarChangedListener.onStopTrackingTouch(seekBar);
                }
            }
        });
        setView(baseView);
        mDialog = create();
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    private void setOffset(int offset) {

    }
}
