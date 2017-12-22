package com.godwin.drawview.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

/**
 * Created by Godwin on 28-11-2017 11:57 for DrawView.
 * @author : Godwin Joseph Kurinjikattu
 */
class ParallaxAnimationView : View {

    private var width: Int? = 0
    private var height: Int? = 0
    private var xPosition: Int? = 0

    private var mBitmap: Bitmap? = null
    private var mSmallBitmap: Bitmap? = null

    private var isAnimating: Boolean? = false

    private var yPositions: IntArray? = null
    private var yPositionsSmall: IntArray? = null

    private var xOffset: IntArray? = null
    private var xOffsetSmall: IntArray? = null
    private var mPaint: Paint? = null

    private var numberOfImage: Int? = 10
    private var numberOfImageSmall: Int? = 15

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int) {
        mPaint = Paint()
//        setupValues()
    }

    private fun setupValues() {
        calculateNumberOfIcon()
        calculateYPositions()
        calculateXOffset()

        calculateNumberOfIconForSmall()
        calculateYPositionsSmall()
        calculateXOffsetSamll()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        height = measuredHeight
        width = measuredWidth

        setupValues()

        startAnimation()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (null == getBitmap())
            return

        for (i in 0 until numberOfImage!!) {
            val y = yPositions!![i]
            var x = xPosition

            val xo = xOffset!![i]

            var diff = x!!.minus(xo)/*.plus(0 - getBitmap()!!.width)*/
            x = if (diff <= 0 - getBitmap()!!.width) {
                width!!.plus(diff)
            } else {
                diff
            }
            canvas!!.drawBitmap(getBitmap(), x.toFloat(), y.toFloat(), mPaint)
        }
        for (i in 0 until numberOfImageSmall!!) {
            val y = yPositionsSmall!![i]
            var x = xPosition

            val xo = xOffsetSmall!![i]

            var diff = x!!.minus(xo)/*.plus(0 - getBitmap()!!.width)*/
            x = if (diff <= 0 - getSmallBitmap()!!.width) {
                width!!.plus(diff)
            } else {
                diff
            }
            canvas!!.drawBitmap(getSmallBitmap(), x.toFloat(), y.toFloat(), mPaint)
        }
    }

    public fun setBitmap(bitmap: Bitmap) {
        this.mBitmap = bitmap
//        setupValues()
//        startSmoothAnimation()
    }

    public fun getBitmap(): Bitmap? {
        return this.mBitmap
    }

    public fun getSmallBitmap(): Bitmap? {
        return this.mSmallBitmap
    }

    public fun setSmallBitmap(bitmap: Bitmap) {
        this.mSmallBitmap = bitmap
    }

    public fun startAnimation() {
        if (isAnimating!!) {
            return
        }
        isAnimating = true
        startSmoothAnimation()
    }

    private fun startSmoothAnimation() {
        val startValue = width
        val endValue = 0 - getBitmap()!!.width

        val valueAnimator = ValueAnimator.ofInt(startValue!!, endValue)
        valueAnimator.addUpdateListener { valueAnimator1 ->
            xPosition = valueAnimator1.animatedValue as Int
            invalidate()
        }
        valueAnimator.duration = 30 * 1000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.start()
    }

    private fun calculateYPositions() {
        if (null != yPositions)
            return

        yPositions = IntArray(numberOfImage!!)
        for (i in 0 until numberOfImage!!) {
            yPositions!![i] = calculateYPosition(i)
        }
    }

    private fun calculateXOffset() {
        if (null != xOffset)
            return

        xOffset = IntArray(numberOfImage!!)
        for (i in 0 until numberOfImage!!) {
            xOffset!![i] = calculateRandomXOffset()
        }
    }

    private fun calculateNumberOfIcon() {
        if (null == getBitmap())
            return
        val bitmapHeight = getBitmap()!!.height
        numberOfImage = height!!.div(bitmapHeight)
    }

    private fun calculateNumberOfIconForSmall() {
        if (null == getSmallBitmap())
            return
        val bitmapHeight = getSmallBitmap()!!.height
        numberOfImageSmall = height!!.div(bitmapHeight)
    }
    private fun calculateYPositionsSmall() {
        if (null != yPositionsSmall)
            return

        yPositionsSmall = IntArray(numberOfImageSmall!!)
        for (i in 0 until numberOfImageSmall!!) {
            yPositionsSmall!![i] = calculateYPosition(i)
        }
    }
    private fun calculateXOffsetSamll() {
        if (null != xOffsetSmall)
            return

        xOffsetSmall = IntArray(numberOfImageSmall!!)
        for (i in 0 until numberOfImageSmall!!) {
            xOffsetSmall!![i] = calculateRandomXOffset()
        }
    }

    private fun calculateYPosition(position: Int): Int {
        if (position == 0)
            return 0
        val bitmapHeight = getBitmap()!!.height
        return (bitmapHeight.plus((position.minus(1)).times(bitmapHeight)).plus(20))
    }

    private var randomXOffset: Int? = 0
    private fun calculateRandomXOffset(): Int {
        val random = Random()
        val randomValue = random.nextInt(width!!)
        if (randomValue < width!! / 4
                || Math.abs(randomXOffset!!.minus(randomValue)) < width!! / 4) {
            return calculateRandomXOffset()
        }
        randomXOffset = randomValue
        return randomValue
    }
}