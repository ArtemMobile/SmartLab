package com.example.smartlab.utils

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

enum class SwipeDirection {
    ALL, // swipe allowed in left and right both directions
    LEFT, // swipe allowed in only Left direction
    RIGHT, // only right
    NONE, // swipe is disabled completely
}

class SwipeControlTouchListener: RecyclerView.OnItemTouchListener  {
    private var initialXValue = 0f
    private var direction: SwipeDirection = SwipeDirection.ALL

    fun setSwipeDirection(direction: SwipeDirection) {
        this.direction = direction
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
        return !isSwipeAllowed(event)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    private fun isSwipeAllowed(event: MotionEvent): Boolean {
        if (direction === SwipeDirection.ALL) return true
        if (direction == SwipeDirection.NONE) //disable any swipe
            return false
        if (event.action == MotionEvent.ACTION_DOWN) {
            initialXValue = event.x
            return true
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            try {
                val diffX: Float = event.x - initialXValue
                if (diffX > 0 && direction == SwipeDirection.RIGHT) {
                    // swipe from left to right detected
                    return false
                } else if (diffX < 0 && direction == SwipeDirection.LEFT) {
                    // swipe from right to left detected
                    return false
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return true
    }

}