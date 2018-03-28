package com.example.arkady.touchy.gesture

import android.content.Context
import android.graphics.PointF
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

abstract class AdvancedTouchListener(context: Context) : View.OnTouchListener {
    private val mTapGestureDetector: GestureDetectorCompat
    private val mScaleDetector: ScaleGestureDetector
    private val mRotateDetector: RotateGestureDetector
    private val mMoveDetector: MoveGestureDetector
    private var mScaleFactor = 1.0f
    private var mRotationDegrees: Float = 0.toFloat()
    private var mTouchStartTime: Long = 0

    init {
        mTapGestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                onTap(PointF(e.x, e.y))
                return true
            }
        })
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mRotateDetector = RotateGestureDetector(RotateListener())
        mMoveDetector = MoveGestureDetector(MoveListener())
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mScaleFactor = 1.0f
                mTouchStartTime = System.currentTimeMillis()
            }
        }
        mTapGestureDetector.onTouchEvent(event)
        mScaleDetector.onTouchEvent(event)
        mRotateDetector.onTouchEvent(event)
        mMoveDetector.onTouchEvent(event)

        return true
    }


    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor // scale change since previous event

            val point = PointF(detector.focusX, detector.focusY)
            this@AdvancedTouchListener.onScale(detector.scaleFactor, mScaleFactor, point)
            return true
        }
    }

    private inner class RotateListener : RotateGestureDetector.OnRotationGestureListener {
        override fun onRotation(detector: RotateGestureDetector) {
            mRotationDegrees = detector.rotationDegreesDelta
            val point = PointF(detector.focusX, detector.focusY)
            this@AdvancedTouchListener.onRotate(mRotationDegrees, point)
        }

    }

    private inner class MoveListener : MoveGestureDetector.OnMoveGestureListener {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            if (System.currentTimeMillis() - mTouchStartTime > TIME_RESERVED_FOR_TAP) {
                val adjustment = detector.focusDelta
                this@AdvancedTouchListener.onMove(adjustment)
            }
            return true
        }
    }


    abstract fun onRotate(rotationDegrees: Float, point: PointF)

    abstract fun onScale(scaleFactor: Float, sessionScaleFactor: Float, focus: PointF)

    abstract fun onMove(point: PointF)

    abstract fun onTap(point: PointF)

    companion object {
        private val TIME_RESERVED_FOR_TAP: Long = 80
    }
}
