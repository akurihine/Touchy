package com.example.arkady.touchy.view

import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.example.arkady.touchy.gesture.AdvancedTouchListener


class TouchyLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    private var mViewSelectedDuringTouchEvent: Boolean = false
    private var mFocusSize: Int = 0
    private var mCurrentView: View? = null

    var firstContactView: View? = null
        private set

    var currentView: View?
        get() = mCurrentView
        set(currentView) {
            if (mCurrentView !== currentView) {
                mCurrentView = currentView
                if (currentView != null) {
                    //bring current view to the top of the child stack
                    removeView(currentView)
                    addView(currentView, childCount)
                }
            }
        }

    init {
        mFocusSize = (resources.displayMetrics.density * FOCUS_SIZE).toInt()
        setOnTouchListener(TouchyTouchListener(context))
    }

    override fun onViewAdded(view: View) {
        super.onViewAdded(view)

        if (view is TextView) {
            view.fitsSystemWindows = false
        }

        view.setOnTouchListener { view, event ->
            if (firstContactView == null) {
                firstContactView = view
            }
            false
        }

        val layoutParams = FrameLayout.LayoutParams(view.layoutParams)
        layoutParams.gravity = Gravity.CENTER
        view.layoutParams = layoutParams
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            firstContactView = null
            mViewSelectedDuringTouchEvent = false
            mCurrentView = null
        }

        return super.dispatchTouchEvent(ev)
    }

    private inner class TouchyTouchListener internal constructor(context: Context) : AdvancedTouchListener(context) {
        override fun onRotate(rotationDegrees: Float, focus: PointF) {
            if (mCurrentView != null) {
                mCurrentView!!.rotation = mCurrentView!!.rotation - rotationDegrees
            } else {
                val view = findViewAroundFocus(focus)
                if (view != null) {
                    currentView = view
                    mViewSelectedDuringTouchEvent = true
                }
            }
            firstContactView = null
        }

        override fun onScale(scaleFactor: Float, sessionScaleFactor: Float, focus: PointF) {
            if (mCurrentView != null) {
                mCurrentView!!.scaleX = Math.min(Math.max(mCurrentView!!.scaleX * scaleFactor, MIN_SCALE), MAX_SCALE)
                mCurrentView!!.scaleY = Math.min(Math.max(mCurrentView!!.scaleY * scaleFactor, MIN_SCALE), MAX_SCALE)
            } else {
                val view = findViewAroundFocus(focus)
                if (view != null) {
                    currentView = view
                    mViewSelectedDuringTouchEvent = true
                }
            }
            firstContactView = null
        }

        override fun onMove(point: PointF) {
            if (firstContactView != null && mCurrentView !== firstContactView) {
                currentView = firstContactView
                mViewSelectedDuringTouchEvent = true
            }

            if (mCurrentView != null) {
                mCurrentView!!.translationX = mCurrentView!!.translationX + point.x
                mCurrentView!!.translationY = mCurrentView!!.translationY + point.y
            }
        }

        override fun onTap(point: PointF) {
            if (mCurrentView == null || mCurrentView != firstContactView || mViewSelectedDuringTouchEvent) {
                currentView = firstContactView
            }
        }

        private fun findViewAroundFocus(focus: PointF): View? {
            var view: View? = null
            for (i in childCount - 1 downTo 0) {
                val childView = getChildAt(i)
                val rect = Rect()
                childView.getHitRect(rect)
                val left = focus.x.toInt() - mFocusSize
                val top = focus.y.toInt() - mFocusSize
                val right = focus.x.toInt() + mFocusSize
                val bottom = focus.y.toInt() + mFocusSize

                val intersect = rect.intersect(left, top, right, bottom)

                if (intersect) {
                    view = childView
                    break
                }
            }
            return view
        }
    }

    companion object {
        private val MIN_SCALE = 0.3f
        private val MAX_SCALE = 20.0f
        private val FOCUS_SIZE = 50f
    }
}
