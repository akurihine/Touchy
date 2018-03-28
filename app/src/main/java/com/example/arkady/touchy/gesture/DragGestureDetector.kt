package com.example.arkady.touchy.gesture

import android.graphics.PointF
import android.view.MotionEvent

class DragGestureDetector(private val mListener: OnMoveGestureListener) {

    private var mCurrFocusInternal: PointF? = null
    private var mPrevFocusInternal: PointF? = null

    private var mCurrPressure: Float = 0.toFloat()
    private var mPrevPressure: Float = 0.toFloat()
    private val mFocusExternal = PointF()
    var mPrevEvent: MotionEvent? = null

    var focusDelta = PointF()
        private set

    interface OnMoveGestureListener {
        fun onMove(detector: DragGestureDetector): Boolean
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        mCurrPressure = event.getPressure(event.actionIndex)
        if (mPrevEvent != null) {
            mPrevPressure = mPrevEvent!!.getPressure(mPrevEvent!!.actionIndex)
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mPrevEvent = MotionEvent.obtain(event)
                updateStateByEvent(event)
            }

            MotionEvent.ACTION_MOVE -> {
                updateStateByEvent(event)
                if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
                    val updatePrevious = mListener.onMove(this)
                    if (updatePrevious) {
                        mPrevEvent!!.recycle()
                        mPrevEvent = MotionEvent.obtain(event)
                    }
                }
            }
        }
       return true
    }

    private fun updateStateByEvent(curr: MotionEvent) {
        mCurrPressure = curr.getPressure(curr.actionIndex)
        mPrevPressure = mPrevEvent!!.getPressure(mPrevEvent!!.actionIndex)

        val prev = mPrevEvent

        mCurrFocusInternal = getFocalPoint(curr)
        mPrevFocusInternal = getFocalPoint(prev)

        val mSkipNextMoveEvent = prev!!.pointerCount != curr.pointerCount
        focusDelta = if (mSkipNextMoveEvent) FOCUS_DELTA_ZERO else PointF(mCurrFocusInternal!!.x - mPrevFocusInternal!!.x, mCurrFocusInternal!!.y - mPrevFocusInternal!!.y)

        mFocusExternal.x += focusDelta.x
        mFocusExternal.y += focusDelta.y
    }

    private fun getFocalPoint(e: MotionEvent?): PointF {
        val pCount = e!!.pointerCount
        var x = 0f
        var y = 0f

        for (i in 0 until pCount) {
            x += e.getX(i)
            y += e.getY(i)
        }

        return PointF(x / pCount, y / pCount)
    }

    companion object {
        private val FOCUS_DELTA_ZERO = PointF()
        const val PRESSURE_THRESHOLD = 0.65f
    }

}
