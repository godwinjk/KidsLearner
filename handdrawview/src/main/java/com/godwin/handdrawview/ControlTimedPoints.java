package com.godwin.handdrawview;

/**
 * Created by Godwin on 12/17/2017 9:02 AM for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class ControlTimedPoints {
    private static final String TAG = ControlTimedPoints.class.getSimpleName();
    TimePoint t1 = null;
    TimePoint t2 = null;

    public static ControlTimedPoints from(TimePoint t1, TimePoint t2) {
        ControlTimedPoints points = new ControlTimedPoints();
        points.t1 = t1;
        points.t2 = t2;
        return points;
    }

    ControlTimedPoints set(TimePoint t1, TimePoint t2) {
        this.t1 = t1;
        this.t2 = t2;
        return this;
    }
}
