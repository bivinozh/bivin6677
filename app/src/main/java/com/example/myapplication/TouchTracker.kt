package com.example.myapplication

import android.view.MotionEvent

class TouchTracker {
    @Volatile var lastRawX: Float = -1f
    @Volatile var lastRawY: Float = -1f

    fun updateFrom(event: MotionEvent) {
        lastRawX = event.rawX
        lastRawY = event.rawY
    }
}


