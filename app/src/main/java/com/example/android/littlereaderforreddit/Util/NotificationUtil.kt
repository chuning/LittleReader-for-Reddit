package com.example.android.littlereaderforreddit.Util

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.text.format.DateUtils
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.UI.RedditDetailActivity


class NotificationUtil {
    companion object {
        private val FEED_NOTIFICATION_ID = 1001
        private val NOTIFICATION_MIN_INTERVAL_HOUR = 12

        fun notifyUserOfNewFeed(context: Context, feed: FeedDetail) {
            val notificationEnabled = SharedPreferenceUtil.getBoolean(Constant.NOTIFICATION_PREFERENCE, true)

            val lastNotificationTime = SharedPreferenceUtil.getLong(Constant.NOTIFICATION_LAST_TIMESTAMP)
            val elapsedTime = System.currentTimeMillis() - lastNotificationTime

            if (notificationEnabled && elapsedTime >= DateUtils.HOUR_IN_MILLIS * NOTIFICATION_MIN_INTERVAL_HOUR) {
                val builder = NotificationCompat.Builder(context)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.notification_template_icon_bg)
                        .setContentText(feed.title)
                        .setContentTitle(feed.subreddit)
                        .setAutoCancel(true)

                val intent = Intent(context, RedditDetailActivity::class.java)
                intent.putExtra(Constant.EXTRA_FEED_DETAIL, feed)
                val taskStackBuilder = TaskStackBuilder.create(context)
                taskStackBuilder.addNextIntentWithParentStack(intent)
                val resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                builder.setContentIntent(resultPendingIntent)

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(FEED_NOTIFICATION_ID, builder.build())
                SharedPreferenceUtil.saveLong(Constant.NOTIFICATION_LAST_TIMESTAMP, System.currentTimeMillis())
            }
        }
    }
}