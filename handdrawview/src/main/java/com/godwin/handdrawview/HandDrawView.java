package com.godwin.handdrawview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import com.godwin.handdrawview.view.ViewCompat;
import com.godwin.handdrawview.view.ViewTreeObserverCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Godwin on 13-11-2017 15:21 for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class HandDrawView extends View {

    private static final String TAG = HandDrawView.class.getSimpleName();

    private static final int DEFAULT_STROKE_WIDTH = 10;
    private static final int DEFAULT_STROKE_WIDTH_FOR_ERASER = 10;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_FADE_SPEED = 50;

    private float mLastTouchX;
    private float mLastTouchY;

    private TimePoint mPoint;

    private static final int DOUBLE_CLICK_DELAY_MS = 200;
    private boolean mClearOnDoubleClick;
    private long mFirstClick;
    private int mCountClick;

    private Paint mPaint;
    private Paint mDummyPaint;

    private RectF mDirtyRect;

    private int mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private int mStrokeWidthForEraser = DEFAULT_STROKE_WIDTH_FOR_ERASER;

    private int mStrokeColor = DEFAULT_COLOR;

    @DrawableRes
    private int mPointerRes = 0;

    private Bitmap mPointerBitmap = null;
    private Bitmap mOverlayBitmap = null;
    private Bitmap mDrawingBitmap = null;
    private Canvas mDrawingBitmapCanvas = null;

    private List<TimePoint> mPoints = new ArrayList<>();
    private List<TimePoint> mPointsCache = new ArrayList<>();
    private List<TimePoint> mExtractedPoints = new ArrayList<>();

    private ControlTimedPoints mControlPointCached = null;
    private Bezier mBezierCached = new Bezier();

    private OnDrawListener mOnDrawListener;

    private boolean isCapturing;
    private boolean isFadeEffect;
    private boolean isFadeEffectSet;
    private Path mPath;

    private Mode mMode = Mode.MARKER;

    private List<LinePath> mFadePath = new ArrayList<>();
    private Timer mTimer;
    private int mFadeSpeed = DEFAULT_FADE_SPEED;
    private int mFadeStep = 25;
    private ValueAnimator animator = null;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can                access the current theme, resources, etc.
     */
    public HandDrawView(Context context) {
        super(context);
        init(null, 0, 0);
    }

    /**
     * Instantiates a new Hand draw view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public HandDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    /**
     * Instantiates a new Hand draw view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public HandDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    /**
     * Instantiates a new Hand draw view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     * @param defStyleRes  the def style res
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HandDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle state = new Bundle();
        state.putParcelable("PARENT", superState);
        state.putInt("mStrokeWidth", mStrokeWidth);
        state.putInt("mStrokeColor", mStrokeColor);
        state.putInt("mPointerRes", mPointerRes);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;

        Parcelable superState = savedState.getParcelable("PARENT");
        super.onRestoreInstanceState(superState);

        mStrokeColor = savedState.getInt("mStrokeColor");
        mStrokeWidth = savedState.getInt("mStrokeWidth");
        mPointerRes = savedState.getInt("mPointerRes");

        initPaint();
    }

    private void init(AttributeSet attrs, int defStyle, int defStyleRes) {
        final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.HandDrawView, defStyle, defStyleRes);
        initAttributes(attrArray);

        mDirtyRect = new RectF();
        initPaint();
        initPath();
        startTimer();
//        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mStrokeColor);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setShadowLayer(mStrokeWidth + .2f, 0, 0, mStrokeColor);

        mDummyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initPath() {
        mPath = new Path();
    }

    private void initAttributes(TypedArray attrArray) {
        this.mStrokeColor = attrArray.getInt(R.styleable.HandDrawView_strokeColor, mStrokeColor);
        this.mStrokeWidth = attrArray.getInt(R.styleable.HandDrawView_strokeWidth, mStrokeWidth);
        this.mPointerRes = attrArray.getInt(R.styleable.HandDrawView_pointerDrawable, mPointerRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (LinePath path : mFadePath) {
            canvas.drawPath(path.path, path.paint);
        }
        if (!isFadeEffect && mDrawingBitmap != null) {
            canvas.drawBitmap(mDrawingBitmap, 0, 0, mPaint);
        }
        if (mPointerBitmap != null && mPoint != null) {
            drawPointerBitmap(canvas, mPoint);
        }
        if (mOverlayBitmap != null) {
            drawOverlayBitmap(canvas);
        }
    }

    private void drawPointerBitmap(Canvas canvas, TimePoint point) {
        if (mPointerBitmap != null) {
            canvas.drawBitmap(mPointerBitmap,
                    point.getX(),
                    point.getY() - mPointerBitmap.getHeight(),
                    mDummyPaint);
        }
    }

    private void drawOverlayBitmap(Canvas canvas) {
        canvas.drawBitmap(mOverlayBitmap,
                getWidth() / 2 - mOverlayBitmap.getWidth() / 2,
                getHeight() / 2 - mOverlayBitmap.getHeight() / 2,
                mDummyPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled())
            return false;
        isFadeEffect = isFadeEffectSet;
        stopAnimation();
        TimePoint p = TimePoint.from(event.getX(), event.getY());
        mPoint = p.clone();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mPoints.clear();

                clearPath();
                if (isDoubleClick()) {
                    clear();
                    return false;
                }
                this.mLastTouchX = event.getX();
                this.mLastTouchY = event.getY();
                moveTo(mPoint);
                addPoint(p);
                if (null != mOnDrawListener) {
                    mOnDrawListener.onStartDraw(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                resetDirtyRect(p);
                lineTo(mPoint);
                addPoint(p);
                if (null != mOnDrawListener) {
                    mOnDrawListener.onDrawing(this, getBitmap());
                }
                break;
            case MotionEvent.ACTION_UP:
                resetDirtyRect(p);
                addPoint(p);
                getParent().requestDisallowInterceptTouchEvent(true);
                if (null != mOnDrawListener) {
                    mOnDrawListener.onStopDrawing(this, getBitmap());
                }
                break;
            default:
                return false;
        }
        //invalidate();
        invalidate(
                (int) (mDirtyRect.left),
                (int) (mDirtyRect.top),
                (int) (mDirtyRect.right),
                (int) (mDirtyRect.bottom));
        return true;
    }

    private void moveTo(TimePoint point) {
        if (isFadeEffect) {
            mPath = new Path();
            mPath.moveTo(point.x, point.y);

            LinePath path = new LinePath();
            path.paint = paintFromColor(mPaint.getColor());
            path.path = mPath;

            mFadePath.add(path);
        }
    }

    private void lineTo(TimePoint point) {
        if (isFadeEffect) {
            mPath.lineTo(point.x, point.y);
            moveTo(point);
        }
    }

    public double getDistance(TimePoint point1, TimePoint point2) {
        double d = Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2));
//        Log.i(TAG, String.format("x2: %s---y2:%s--x1:%s--y1:%s", point2.x, point2.y, point1.x, point1.y));
        Log.i(TAG, "getDistance: " + d);
        return d;
    }

    private void clearPath() {
        if (mPath != null)
            mPath.reset();
        mPath = null;
//        mPoint = null;
    }

    private void resetDirtyRect(TimePoint point) {
// The mLastTouchX and mLastTouchY were set when the ACTION_DOWN motion event occurred.
        mDirtyRect.left = Math.min(mLastTouchX, point.x);
        mDirtyRect.right = Math.max(mLastTouchX, point.x);
        mDirtyRect.top = Math.min(mLastTouchY, point.y);
        mDirtyRect.bottom = Math.max(mLastTouchY, point.y);
    }

    private void addPoint(TimePoint timePoint) {
        if (isCapturing) {
            mExtractedPoints.add(timePoint.clone());
        }
        if (isFadeEffect) {
            return;
        }
        timePoint = getNewPoint(timePoint);
        capture(timePoint);
        mPoints.add(timePoint.clone());
        int pointsCount = mPoints.size();
        if (pointsCount > 3) {

            ControlTimedPoints tmp = calculateCurveControlPoints(mPoints.get(0), mPoints.get(1), mPoints.get(2));
            TimePoint t2 = tmp.t2;
            recyclePoint(tmp.t1);

            tmp = calculateCurveControlPoints(mPoints.get(1), mPoints.get(2), mPoints.get(3));
            TimePoint t3 = tmp.t1;
            recyclePoint(tmp.t2);

            Bezier curve = mBezierCached.set(mPoints.get(1), t2, t3, mPoints.get(2));
            addBezier(curve);

            // Remove the first element from the list,
            // so that we always have no more than 4 mPoints in mPoints array.
            recyclePoint(mPoints.remove(0));

            recyclePoint(t2);
            recyclePoint(t3);
        } else if (pointsCount == 1) {
            // To reduce the initial lag make it work with 3 mPoints
            // by duplicating the first point
            mPoints.add(getNewPoint(mPoints.get(0)));
        }
    }

    private void capture(TimePoint timePoint) {
        if (isCapturing)
            mExtractedPoints.add(timePoint.clone());
    }

    //region Setter and getter

    /**
     * Start capturing.
     */
    public void startCapturing() {
        this.isCapturing = true;
        mExtractedPoints.clear();
    }

    /**
     * Stop capaturing.
     */
    public void stopCapturing() {
        this.isCapturing = false;
    }

    /**
     * Is capturing boolean.
     *
     * @return the boolean
     */
    public boolean isCapturing() {
        return isCapturing;
    }

    /**
     * Is fade effect boolean.
     *
     * @return the boolean
     */
    public boolean isFadeEffect() {
        return isFadeEffect;
    }

    /**
     * Sets fade effect.
     *
     * @param fadeEffect the fade effect
     */
    public void setFadeEffect(boolean fadeEffect) {
        isFadeEffect = fadeEffect;
    }

    /**
     * Gets stroke width.
     *
     * @return the stroke width
     */
    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    /**
     * Sets stroke width.
     *
     * @param mStrokeWidth the m stroke width
     */
    public void setStrokeWidth(int mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
        ensureDrawingBitmap();
    }

    /**
     * Gets stroke width for eraser.
     *
     * @return the stroke width for eraser
     */
    public int getStrokeWidthForEraser() {
        return mStrokeWidthForEraser;
    }

    /**
     * Sets stroke width for eraser.
     *
     * @param mStrokeWidthForEraser the m stroke width for eraser
     */
    public void setStrokeWidthForEraser(int mStrokeWidthForEraser) {
        this.mStrokeWidthForEraser = mStrokeWidthForEraser;
    }

    /**
     * Gets stroke color.
     *
     * @return the stroke color
     */
    public int getStrokeColor() {
        return mStrokeColor;
    }

    /**
     * Sets stroke color.
     *
     * @param mStrokeColor the m stroke color
     */
    public void setStrokeColor(int mStrokeColor) {
        this.mStrokeColor = mStrokeColor;
        if (null == mPaint) {
            initPaint();
        } else {
            mPaint.setColor(mStrokeColor);
        }
    }

    /**
     * Sets clear on double click.
     *
     * @param clearOnDoubleClick the clear on double click
     */
    public void setClearOnDoubleClick(boolean clearOnDoubleClick) {
        mClearOnDoubleClick = clearOnDoubleClick;
    }

    /**
     * Gets pointer res.
     *
     * @return the pointer res
     */
    public int getPointerRes() {
        return mPointerRes;
    }

    /**
     * Sets pointer res.
     *
     * @param mPointerRes the m pointer res
     */
    public void setPointerRes(int mPointerRes) {
        this.mPointerRes = mPointerRes;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        this.mPointerBitmap = BitmapFactory.decodeResource(getResources(), mPointerRes, options);
        invalidate();
    }

    /**
     * Gets pointer bitmap.
     *
     * @return the pointer bitmap
     */
    public Bitmap getPointerBitmap() {
        return mPointerBitmap;
    }

    /**
     * Sets pointer bitmap.
     *
     * @param mPointerBitmap the m pointer bitmap
     */
    public void setPointerBitmap(Bitmap mPointerBitmap) {
        this.mPointerBitmap = mPointerBitmap;
        invalidate();
    }

    /**
     * Gets on draw listener.
     *
     * @return the on draw listener
     */
    public OnDrawListener getOnDrawListener() {
        return mOnDrawListener;
    }

    /**
     * Sets on draw listener.
     *
     * @param mOnDrawListener the m on draw listener
     */
    public void setOnDrawListener(OnDrawListener mOnDrawListener) {
        this.mOnDrawListener = mOnDrawListener;
    }

    /**
     * Sets fade speed.
     *
     * @param fadeSpeed the fade speed
     */
    public void setFadeSpeed(int fadeSpeed) {
        this.mFadeSpeed = fadeSpeed;
    }

    /**
     * Gets fade speed.
     *
     * @return the fade speed
     */
    public int getFadeSpeed() {
        return this.mFadeSpeed;
    }

    /**
     * Gets overlay bitmap.
     *
     * @return the overlay bitmap
     */
    public Bitmap getOverlayBitmap() {
        return mOverlayBitmap;
    }

    /**
     * Sets overlay bitmap.
     *
     * @param mOverlayBitmap the m overlay bitmap
     */
    public void setOverlayBitmap(Bitmap mOverlayBitmap) {
        this.mOverlayBitmap = mOverlayBitmap;
        invalidate();
    }
    //endregion

    /**
     * Start animation.
     */
    public void startAnimation() {
        if (mExtractedPoints != null && mExtractedPoints.size() > 0)
            animation(mExtractedPoints);
    }

    /**
     * Start animation.
     *
     * @param points the points
     */
    public void startAnimation(List<TimePoint> points) {
        animation(points);
    }

    private void animation(final List<TimePoint> points) {
        if (points != null && points.size() > 0) {
            animator = ValueAnimator.ofInt(0, points.size() - 1);
            isFadeEffectSet = isFadeEffect;
            isFadeEffect = true;
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int i = (int) animation.getAnimatedValue();
                    try {
                        if (isFadeEffect && points.size() > 0 && i >= 0 && i < points.size()) {
                            mPoint = points.get(i);
                            if (i == 0)
                                moveTo(mPoint);
                            else
                                lineTo(points.get(i + 1));
                            invalidate();
                            if (i == points.size() - 1)
                                isFadeEffect = isFadeEffectSet;
                        } else {
                            isFadeEffect = isFadeEffectSet;
                        }
                    } catch (Exception e) {
                        Log.v(TAG, e.getLocalizedMessage());
                        isFadeEffect = isFadeEffectSet;
                    }
                }
            });

            int sec = mPoints.size() <= 100 ? 5 * 1000 : (mPoints.size() * 30);
            animator.setDuration(sec);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }
    }

    private void stopAnimation() {
        if (null != animator) {
            animator.cancel();
            animator = null;
        }
    }

    /**
     * Gets normalized path points.
     *
     * @return the normalized path points
     */
    public List<TimePoint> getNormalizedPathPoints() {
        List<TimePoint> normalizedPoints = new ArrayList<>();
        float xNormalizedValue = (getWidth() - getOverlayBitmap().getWidth()) / 2;
        float yNormalizedValue = (getHeight() - getOverlayBitmap().getHeight()) / 2;
        Log.d(TAG, String.format("X norm: %f  , Y norm: %f", xNormalizedValue, yNormalizedValue));
        for (TimePoint p : mExtractedPoints) {
            TimePoint point = TimePoint.from(p.getX() - xNormalizedValue, p.getY() - yNormalizedValue);
            normalizedPoints.add(point);
        }
        return normalizedPoints;
    }

    /**
     * Sets normalized path points.
     *
     * @param points the points
     */
    public void setNormalizedPathPoints(List<TimePoint> points) {
        if (points != null && points.size() > 0 && getOverlayBitmap() != null) {
            float xNormalizedValue = (getWidth() - getOverlayBitmap().getWidth()) / 2;
            float yNormalizedValue = (getHeight() - getOverlayBitmap().getHeight()) / 2;
            mExtractedPoints.clear();
            for (TimePoint p : points) {
                TimePoint point = TimePoint.from(p.getX() + xNormalizedValue, p.getY() + yNormalizedValue);
                mExtractedPoints.add(point);
            }
        }
    }

    private ControlTimedPoints calculateCurveControlPoints(TimePoint s1, TimePoint s2, TimePoint s3) {
        float dx1 = s1.x - s2.x;
        float dy1 = s1.y - s2.y;
        float dx2 = s2.x - s3.x;
        float dy2 = s2.y - s3.y;

        float m1X = (s1.x + s2.x) / 2.0f;
        float m1Y = (s1.y + s2.y) / 2.0f;
        float m2X = (s2.x + s3.x) / 2.0f;
        float m2Y = (s2.y + s3.y) / 2.0f;

        float l1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
        float l2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);

        float dxm = (m1X - m2X);
        float dym = (m1Y - m2Y);
        float k = l2 / (l1 + l2);
        if (Float.isNaN(k)) k = 0.0f;
        float cmX = m2X + dxm * k;
        float cmY = m2Y + dym * k;

        float tx = s2.x - cmX;
        float ty = s2.y - cmY;

        TimePoint t1 = getNewPoint(TimePoint.from(m1X + tx, m1Y + ty));
        TimePoint t2 = getNewPoint(TimePoint.from(m2X + tx, m2Y + ty));
        if (null == mControlPointCached)
            mControlPointCached = new ControlTimedPoints();

        return mControlPointCached.set(t1, t2);
    }

    private void ensureDrawingBitmap() {
        if (mDrawingBitmap == null && getWidth() > 0 && getHeight() > 0) {
            mDrawingBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            mDrawingBitmapCanvas = new Canvas(mDrawingBitmap);
        }
    }

    private void addBezier(Bezier curve) {
        ensureDrawingBitmap();
        float drawSteps = (float) Math.floor(curve.length());

        for (int i = 0; i < drawSteps; i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            float t = ((float) i) / drawSteps;
            float tt = t * t;
            float ttt = tt * t;
            float u = 1 - t;
            float uu = u * u;
            float uuu = uu * u;

            float x = uuu * curve.startPoint.x;
            x += 3 * uu * t * curve.control1.x;
            x += 3 * u * tt * curve.control2.x;
            x += ttt * curve.endPoint.x;

            float y = uuu * curve.startPoint.y;
            y += 3 * uu * t * curve.control1.y;
            y += 3 * u * tt * curve.control2.y;
            y += ttt * curve.endPoint.y;

            // Set the incremental stroke width and draw.
            mDrawingBitmapCanvas.drawPoint(x, y, mPaint);

            expandDirtyRect(x, y);
        }
    }

    /**
     * Clear the canvas.
     */
    public void clear() {
        if (mPoints != null) {
            mPoints.clear();
        }
        if (mExtractedPoints != null)
            mExtractedPoints.clear();

        if (mDrawingBitmap != null) {
            mDrawingBitmap = null;
            ensureDrawingBitmap();
        }
        clearPath();
        stopAnimation();
        if (null != mOnDrawListener) {
            mOnDrawListener.onClearCanvas(this);
        }
        invalidate();
    }

    /**
     * Gets mode.
     *
     * @return the mode
     */
    public Mode getMode() {
        return mMode;
    }

    /**
     * Sets mode.
     *
     * @param mMode the m mode
     */
    public void setMode(Mode mMode) {
        this.mMode = mMode;
    }

    private TimePoint getNewPoint(TimePoint timePoint) {
        int mCacheSize = mPointsCache.size();
        TimePoint temp;
        if (mCacheSize == 0) {
            // Cache is empty, create a new point
            temp = new TimePoint();
        } else {
            // Get point from cache
            temp = mPointsCache.remove(mCacheSize - 1);
        }

        return temp.set(timePoint);
    }

    private boolean isDoubleClick() {
        if (mClearOnDoubleClick) {
            if (mFirstClick != 0 && System.currentTimeMillis() - mFirstClick > DOUBLE_CLICK_DELAY_MS) {
                mCountClick = 0;
            }
            mCountClick++;
            if (mCountClick == 1) {
                mFirstClick = System.currentTimeMillis();
            } else if (mCountClick == 2) {
                long lastClick = System.currentTimeMillis();
                if (lastClick - mFirstClick < DOUBLE_CLICK_DELAY_MS) {
                    clear();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * mPoints.
     *
     * @param historicalX the previous x coordinate.
     * @param historicalY the previous y coordinate.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }

    private void recyclePoint(TimePoint point) {
        mPointsCache.add(point);
    }

    /**
     * Gets bitmap.
     *
     * @return the bitmap
     */
    public Bitmap getBitmap() {
        Bitmap originalBitmap = getTransparentSignatureBitmap();
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        return whiteBgBitmap;
    }

    /**
     * Sets bitmap.
     *
     * @param signature the signature
     */
    public void setBitmap(final Bitmap signature) {
        // View was laid out...
        if (ViewCompat.isLaidOut(this)) {
            clear();
            ensureDrawingBitmap();

            RectF tempSrc = new RectF();
            RectF tempDst = new RectF();

            int dWidth = signature.getWidth();
            int dHeight = signature.getHeight();
            int vWidth = getWidth();
            int vHeight = getHeight();

            // Generate the required transform.
            tempSrc.set(0, 0, dWidth, dHeight);
            tempDst.set(0, 0, vWidth, vHeight);

            Matrix drawMatrix = new Matrix();
            drawMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);

            Canvas canvas = new Canvas(mDrawingBitmap);
            canvas.drawBitmap(signature, drawMatrix, null);

            invalidate();
        }
        // View not laid out yet e.g. called from onCreate(), onRestoreInstanceState()...
        else {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Remove layout listener...
                    ViewTreeObserverCompat.removeOnGlobalLayoutListener(getViewTreeObserver(), this);

                    // Signature bitmap...
                    setBitmap(signature);
                }
            });
        }
    }

    /**
     * Gets transparent signature bitmap.
     *
     * @return the transparent signature bitmap
     */
    public Bitmap getTransparentSignatureBitmap() {
        ensureDrawingBitmap();
        return mDrawingBitmap;
    }

    /**
     * Gets transparent bitmap.
     *
     * @param trimBlankSpace the trim blank space
     * @return the transparent bitmap
     */
    public Bitmap getTransparentBitmap(boolean trimBlankSpace) {
        if (!trimBlankSpace) {
            return getTransparentSignatureBitmap();
        }
        ensureDrawingBitmap();
        int imgHeight = mDrawingBitmap.getHeight();
        int imgWidth = mDrawingBitmap.getWidth();

        int backgroundColor = Color.TRANSPARENT;

        int xMin = Integer.MAX_VALUE,
                xMax = Integer.MIN_VALUE,
                yMin = Integer.MAX_VALUE,
                yMax = Integer.MIN_VALUE;

        boolean foundPixel = false;

        // Find xMin
        for (int x = 0; x < imgWidth; x++) {
            boolean stop = false;
            for (int y = 0; y < imgHeight; y++) {
                if (mDrawingBitmap.getPixel(x, y) != backgroundColor) {
                    xMin = x;
                    stop = true;
                    foundPixel = true;
                    break;
                }
            }
            if (stop)
                break;
        }

        // Image is empty...
        if (!foundPixel)
            return null;

        // Find yMin
        for (int y = 0; y < imgHeight; y++) {
            boolean stop = false;
            for (int x = xMin; x < imgWidth; x++) {
                if (mDrawingBitmap.getPixel(x, y) != backgroundColor) {
                    yMin = y;
                    stop = true;
                    break;
                }
            }
            if (stop)
                break;
        }

        // Find xMax
        for (int x = imgWidth - 1; x >= xMin; x--) {
            boolean stop = false;
            for (int y = yMin; y < imgHeight; y++) {
                if (mDrawingBitmap.getPixel(x, y) != backgroundColor) {
                    xMax = x;
                    stop = true;
                    break;
                }
            }
            if (stop)
                break;
        }

        // Find yMax
        for (int y = imgHeight - 1; y >= yMin; y--) {
            boolean stop = false;
            for (int x = xMin; x <= xMax; x++) {
                if (mDrawingBitmap.getPixel(x, y) != backgroundColor) {
                    yMax = y;
                    stop = true;
                    break;
                }
            }
            if (stop)
                break;
        }

        return Bitmap.createBitmap(mDrawingBitmap, xMin, yMin, xMax - xMin, yMax - yMin);
    }

    /**
     * The Mutex.
     */
    final Object mutex = new Object();

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {

            Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    synchronized (mutex) {
                        Iterator<LinePath> itr = mFadePath.listIterator();
                        while (itr.hasNext()) {
                            LinePath path = itr.next();
                            int currentAlpha = path.paint.getAlpha();
                            float strokeWidth = path.paint.getStrokeWidth();
                            strokeWidth -= .6f;
                            path.paint.setStrokeWidth(strokeWidth);

                            currentAlpha -= mFadeStep;
                            path.paint.setAlpha(currentAlpha);
                            //remove the linePath from the queue if the alpha value is 0
                            if (currentAlpha <= 0 || strokeWidth <= 0) {
                                itr.remove();
                            }
                        }
                        invalidate();
                    }
                    return false;
                }
            });

            @Override
            public void run() {
                if (mFadePath.size() > 0) {
                    handler.sendEmptyMessage(0);
                }
            }
        }, 0, mFadeSpeed);
    }

    private Paint paintFromColor(int color) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(color);
        p.setStrokeWidth(mStrokeWidth);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);
//        p.setStrokeCap(Paint.Cap.ROUND);
//        p.setStrokeMiter(5);
        p.setShadowLayer(mStrokeWidth + 1, 0, 0, color);
        return p;
    }

    /**
     * The interface On draw listener.
     */
    public interface OnDrawListener {
        /**
         * On start draw.
         *
         * @param drawView the draw view
         */
        void onStartDraw(HandDrawView drawView);

        /**
         * On drawing.
         *
         * @param drawView the draw view
         * @param bitmap   the bitmap
         */
        void onDrawing(HandDrawView drawView, Bitmap bitmap);

        /**
         * On stop drawing.
         *
         * @param drawView the draw view
         * @param bitmap   the bitmap
         */
        void onStopDrawing(HandDrawView drawView, Bitmap bitmap);

        /**
         * On clear canvas.
         *
         * @param drawView the draw view
         */
        void onClearCanvas(HandDrawView drawView);
    }
}
