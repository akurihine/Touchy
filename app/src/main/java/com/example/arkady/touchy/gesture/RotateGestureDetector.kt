package com.example.arkady.touchy.gesture

import android.view.MotionEvent
import android.graphics.PointF


class RotateGestureDetector(private val mListener: OnRotationGestureListener?) {
    private val mStartPointOfPointer2 = PointF()
    private val mStartPointOfPointer1 = PointF()
    private var mIndexOfPointer1: Int = 0
    private var mIndexOfPointer2: Int = 0

    var focusX = 0F
    var focusY = 0F

    private var previousAngle: Float = 0.toFloat()
    private var angle: Float = 0.toFloat()

    val rotationDegreesDelta: Float
        get() {
            return  previousAngle - angle
        }

    init {
        mIndexOfPointer1 = INVALID_POINTER_ID
        mIndexOfPointer2 = INVALID_POINTER_ID
    }


    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                angle = 0F
                previousAngle = 0F
                mIndexOfPointer1 = event.getPointerId(event.actionIndex)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount > 1) {
                    mIndexOfPointer1 = event.getPointerId(0)
                    mIndexOfPointer2 = event.getPointerId(1)
                } else {
                    mIndexOfPointer1 = event.getPointerId(0)
                }

                val pointer1StartX = event.getX(event.findPointerIndex(mIndexOfPointer1))
                val pointer1StartY = event.getY(event.findPointerIndex(mIndexOfPointer1))

                val pointer2StartX = event.getX(event.findPointerIndex(mIndexOfPointer2))
                val pointer2StartY = event.getY(event.findPointerIndex(mIndexOfPointer2))

                mStartPointOfPointer1.set(pointer1StartX, pointer1StartY)
                mStartPointOfPointer2.set(pointer2StartX, pointer2StartY)
                angle = 0F
                previousAngle = 0F
            }

            MotionEvent.ACTION_MOVE -> if (mIndexOfPointer1 != INVALID_POINTER_ID && mIndexOfPointer2 != INVALID_POINTER_ID) {
                val sumX = event.getX(0) + event.getX(1)
                val sumY = event.getY(0) + event.getY(1)

                focusX = sumX / 2
                focusY = sumY / 2

                val xOfPointer1  = event.getX(event.findPointerIndex(mIndexOfPointer1))
                val yOfPointer1  = event.getY(event.findPointerIndex(mIndexOfPointer1))
                val xOfPointer2 = event.getX(event.findPointerIndex(mIndexOfPointer2))
                val yOfPointer2 = event.getY(event.findPointerIndex(mIndexOfPointer2))

                val currentPointOfPointer1 = PointF(xOfPointer1, yOfPointer1)
                val currentPointOfPointer2 = PointF(xOfPointer2, yOfPointer2)

                previousAngle = angle
                angle = angleBetweenLines(mStartPointOfPointer1, mStartPointOfPointer2, currentPointOfPointer1, currentPointOfPointer2)

                mListener?.onRotation(this)
            }

            MotionEvent.ACTION_UP -> mIndexOfPointer1 = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> mIndexOfPointer2 = INVALID_POINTER_ID
            MotionEvent.ACTION_CANCEL -> {
                mIndexOfPointer1 = INVALID_POINTER_ID
                mIndexOfPointer2 = INVALID_POINTER_ID
            }
        }
        return true
    }

    private fun angleBetweenLines(startPoint1: PointF, finishPoint1: PointF, startPoint2: PointF, finishPoint2: PointF): Float {
        val angle1 = Math.atan2((finishPoint1.y - startPoint1.y).toDouble(), (finishPoint1.x - startPoint1.x).toDouble()).toFloat()
        val angle2 = Math.atan2((finishPoint2.y - startPoint2.y).toDouble(), (finishPoint2.x - startPoint2.x).toDouble()).toFloat()

        var angle = Math.toDegrees((angle1 - angle2).toDouble()).toFloat() % 360
        if (angle < -180f) angle += 360.0f
        if (angle > 180f) angle -= 360.0f
        return -angle
    }

    interface OnRotationGestureListener {
        fun onRotation(rotationDetector: RotateGestureDetector)
    }

    companion object {
        private val INVALID_POINTER_ID = -1
    }
}