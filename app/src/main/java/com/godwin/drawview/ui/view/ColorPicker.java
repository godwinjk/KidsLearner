package com.godwin.drawview.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Godwin on 12/19/2017 11:40 AM for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ColorPicker extends View {
    private final int paramOuterPadding = 0; // outer padding of the whole color picker view
    private final int paramInnerPadding = 0; // distance between value slider wheel and inner color wheel
    private final int paramValueSliderWidth = 0; // width of the value slider
    private final int paramArrowPointerSize = 0; // size of the arrow pointer; set to 0 to hide the pointer
    ColorPickerListener listener;
    private Paint colorWheelPaint;
    private Paint valueSliderPaint;
    private Paint colorViewPaint;
    private Paint colorPointerPaint;
    private RectF colorPointerCoords;
    private Paint valuePointerPaint;
    private RectF outerWheelRect;
    private RectF innerWheelRect;
    private Path colorViewPath;
    private Path valueSliderPath;
    private Bitmap colorWheelBitmap;
    private int valueSliderWidth;
    private int innerPadding;
    private int outerPadding;
    private int arrowPointerSize;
    private int outerWheelRadius;
    private int innerWheelRadius;
    private int colorWheelRadius;
    private Matrix gradientRotationMatrix;
    /**
     * Currently selected color
     */
    private float[] colorHSV = new float[]{0f, 0f, 1f};

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPicker(Context context) {
        super(context);
        init();
    }

    public void setListener(ColorPickerListener listener) {
        this.listener = listener;
    }

    private void init() {

        colorPointerPaint = new Paint();
        colorPointerPaint.setStyle(Paint.Style.STROKE);
        colorPointerPaint.setStrokeWidth(2f);
        colorPointerPaint.setARGB(255, 255, 255, 255);

        valuePointerPaint = new Paint();
        valuePointerPaint.setStyle(Paint.Style.STROKE);
        valuePointerPaint.setStrokeWidth(5f);

        colorWheelPaint = new Paint();
        colorWheelPaint.setAntiAlias(true);
        colorWheelPaint.setDither(true);

        valueSliderPaint = new Paint();
        valueSliderPaint.setAntiAlias(true);
        valueSliderPaint.setDither(true);

        colorViewPaint = new Paint();
        colorViewPaint.setAntiAlias(true);

        colorViewPath = new Path();
        valueSliderPath = new Path();

        outerWheelRect = new RectF();
        innerWheelRect = new RectF();

        colorPointerCoords = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // drawing color wheel

        canvas.drawBitmap(colorWheelBitmap, centerX - colorWheelRadius, centerY - colorWheelRadius, null);

        // drawing color view

//        colorViewPaint.setColor(Color.HSVToColor(colorHSV));
//        canvas.drawPath(colorViewPath, colorViewPaint);

        // drawing value slider

        float[] hsv = new float[]{colorHSV[0], colorHSV[1], 1f};

        SweepGradient sweepGradient = new SweepGradient(centerX, centerY, new int[]{Color.BLACK, Color.HSVToColor(hsv), Color.WHITE}, null);
        sweepGradient.setLocalMatrix(gradientRotationMatrix);
        valueSliderPaint.setShader(sweepGradient);

        //canvas.drawPath(valueSliderPath, valueSliderPaint);

        // drawing color wheel pointer

        float hueAngle = (float) Math.toRadians(colorHSV[0]);
        int colorPointX = (int) (-Math.cos(hueAngle) * colorHSV[1] * colorWheelRadius) + centerX;
        int colorPointY = (int) (-Math.sin(hueAngle) * colorHSV[1] * colorWheelRadius) + centerY;

        float pointerRadius = 0.15f * colorWheelRadius;
        int pointerX = (int) (colorPointX - pointerRadius / 2);
        int pointerY = (int) (colorPointY - pointerRadius / 2);

        colorPointerCoords.set(pointerX, pointerY, pointerX + pointerRadius, pointerY + pointerRadius);
        canvas.drawOval(colorPointerCoords, getColorPointerPaint());

        float x1 = pointerX + 5;
        float x2 = pointerX + pointerRadius - 5;
        float y1 = pointerY + 5;
        float y2 = pointerY + pointerRadius - 5;

        canvas.drawLine((x1 + x2) / 2, y2, (x1 + x2) / 2, y1, getColorPointerPaint());
        canvas.drawLine(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2, getColorPointerPaint());
        // drawing value pointer

        valuePointerPaint.setColor(Color.HSVToColor(new float[]{0f, 0f, 1f - colorHSV[2]}));
    }

    private Paint getColorPointerPaint() {
        colorPointerPaint.setColor(inverse(getColor()));
        return colorPointerPaint;
    }

    private int inverse(int color) {
        return Color.rgb(255 - Color.red(color),
                255 - Color.green(color),
                255 - Color.blue(color));
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {

        int centerX = width / 2;
        int centerY = height / 2;

        innerPadding = paramInnerPadding * width / 100;
        outerPadding = paramOuterPadding * width / 100;
        arrowPointerSize = paramArrowPointerSize * width / 100;
        valueSliderWidth = paramValueSliderWidth * width / 100;

        outerWheelRadius = width / 2 - outerPadding - arrowPointerSize;
        innerWheelRadius = outerWheelRadius - valueSliderWidth;
        colorWheelRadius = innerWheelRadius - innerPadding;

        outerWheelRect.set(centerX - outerWheelRadius, centerY - outerWheelRadius, centerX + outerWheelRadius, centerY + outerWheelRadius);
        innerWheelRect.set(centerX - innerWheelRadius, centerY - innerWheelRadius, centerX + innerWheelRadius, centerY + innerWheelRadius);

        colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);

        gradientRotationMatrix = new Matrix();
        gradientRotationMatrix.preRotate(270, width / 2, height / 2);

        colorViewPath.arcTo(outerWheelRect, 270, -180);
        colorViewPath.arcTo(innerWheelRect, 90, 180);

        valueSliderPath.arcTo(outerWheelRect, 270, 180);
        valueSliderPath.arcTo(innerWheelRect, 90, -180);

    }

    private Bitmap createColorWheelBitmap(int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = (i * colorAngleStep + 180) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];

        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

        colorWheelPaint.setShader(composeShader);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, colorWheelRadius, colorWheelPaint);

        return bitmap;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                int x = (int) event.getX();
                int y = (int) event.getY();
                int cx = x - getWidth() / 2;
                int cy = y - getHeight() / 2;
                double d = Math.sqrt(cx * cx + cy * cy);

                if (d <= colorWheelRadius) {

                    colorHSV[0] = (float) (Math.toDegrees(Math.atan2(cy, cx)) + 180f);
                    colorHSV[1] = Math.max(0f, Math.min(1f, (float) (d / colorWheelRadius)));

//                    if (listener != null)
//                        listener.onColorSelected(Color.HSVToColor(colorHSV));
                    invalidate();

                } /*else if (x >= getWidth() / 2 && d >= innerWheelRadius) {

                    colorHSV[2] = (float) Math.max(0, Math.min(1, Math.atan2(cy, cx) / Math.PI + 0.5f));

                    invalidate();
                }*/
                return true;

            case MotionEvent.ACTION_UP:
                int x1 = (int) event.getX();
                int y1 = (int) event.getY();
                int cx1 = x1 - getWidth() / 2;
                int cy1 = y1 - getHeight() / 2;
                double d1 = Math.sqrt(cx1 * cx1 + cy1 * cy1);

                if (d1 <= colorWheelRadius) {

                    colorHSV[0] = (float) (Math.toDegrees(Math.atan2(cy1, cx1)) + 180f);
                    colorHSV[1] = Math.max(0f, Math.min(1f, (float) (d1 / colorWheelRadius)));

                    if (listener != null) {
                        Log.e("Color ", Color.HSVToColor(colorHSV) + "");
                        listener.onSelectColor(Color.HSVToColor(colorHSV));
                    }
                    invalidate();

                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                Log.e("Button release", "Button release");
                return true;

        }
        return super.onTouchEvent(event);
    }

    public int getColor() {
        return Color.HSVToColor(colorHSV);
    }

    public void setColor(int color) {
        Color.colorToHSV(color, colorHSV);
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putFloatArray("color", colorHSV);
        state.putParcelable("super", super.onSaveInstanceState());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            colorHSV = bundle.getFloatArray("color");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
