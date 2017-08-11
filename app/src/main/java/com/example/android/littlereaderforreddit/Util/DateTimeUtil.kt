package com.example.android.littlereaderforreddit.Util

/**
 * Created by chuningluo on 17/8/9.
 */

class DateTimeUtil {
    companion object {
        fun deltaTime(createdTime: Long): String {
            val mili = System.currentTimeMillis() - createdTime * 1000
            val min = (mili / (60 * 1000)).toInt()
            val hour = (mili / (60 * 60 * 1000)).toInt()
            val day = hour / 24
            if (min <= 0) {
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