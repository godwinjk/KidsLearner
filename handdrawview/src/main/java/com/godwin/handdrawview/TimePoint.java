package com.godwin.handdrawview;

/**
 * Created by Godwin on 25-11-2017 20:18 for AdminKids.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class TimePoint implements Cloneable {
    /**
     * The X.
     */
    float x = 0.0f;
    /**
     * The Y.
     */
    float y = 0.0f;
    /**
     * The Time in millis.
     */
    long timeInMillis = 0;

    /**
     * From time point.
     *
     * @param x the x
     * @param y the y
     * @return the time point
     */
    public static TimePoint from(float x, float y) {
        TimePoint point = new TimePoint();
        point.x = x;
        point.y = y;
        point.timeInMillis = System.currentTimeMillis();
        return point;
    }

    /**
     * Set time point.
     *
     * @param x the x
     * @param y the y
     * @return the time point
     */
    TimePoint set(float x, float y) {
        this.x = x;
        this.y = y;
        this.timeInMillis = System.currentTimeMillis();

        return this;
    }

    /**
     * Set time point.
     *
     * @param point the point
     * @return the time point
     */
    TimePoint set(TimePoint point) {
        this.x = point.getX();
        this.y = point.getY();
        this.timeInMillis = point.getTimeInMillis();
        return this;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets time in millis.
     *
     * @return the time in millis
     */
    public long getTimeInMillis() {
        return timeInMillis;
    }

    /**
     * Sets time in millis.
     *
     * @param timeInMillis the time in millis
     */
    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    /**
     * Finds velocity from two time points with help of 2 points.
     *
     * @param start the start
     * @return the float
     */
    public float velocityFrom(TimePoint start) {
        long diff = this.timeInMillis - start.timeInMillis;
        if (diff <= 0) {
            diff = 1;
        }
        float velocity = distanceTo(start) / diff;
        if (Float.isInfinite(velocity) || Float.isNaN(velocity)) {
            velocity = 0;
        }
        return velocity;
    }

    /**
     * Distance to float.
     *
     * @param point the point
     * @return the float
     */
    public float distanceTo(TimePoint point) {
        return (float) Math.sqrt(Math.pow(point.x - this.x, 2) + Math.pow(point.y - this.y, 2));
    }

    public float[] getPoints() {
        return new float[]{x, y};
    }

    @Override
    protected TimePoint clone() {
        try {
            return (TimePoint) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new TimePoint();
    }
}
