package com.example.android.littlereaderforreddit.Util

import java.util.*

/**
 * Created by chuningluo on 17/8/9.
 */

class DateTimeUtil {
    companion object {
        fun deltaTime(created: Long): String {
            val deltaSecond = System.currentTimeMillis() / 1000 - created
            val min = (deltaSecond / 60).toInt()
            val hour = (deltaSecond / (60 * 60)).toInt()
            val day = hour / 24
            if (min == 0) {
                return "Now"
            } else if (hour == 0) {
                return String.format("%dm", min)
            } else if (day == 0) {
                return String.format("%dh", hour)
            } else {
                return String.format("%dd", day)
            }
        }
    }
}