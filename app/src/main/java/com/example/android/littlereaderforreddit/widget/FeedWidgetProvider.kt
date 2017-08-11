package com.example.android.littlereaderforreddit.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import android.widget.RemoteViews
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.UI.RedditListActivity
import com.example.android.littlereaderforreddit.Util.Constant

class FeedWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val N = appWidgetIds.size
        for (i in 0..N-1) {
            val remoteViews = RemoteViews(context.packageName, R.layout.feed_widget)
            val intent = Intent(context, RedditListActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent)

            remoteViews.setRemoteAdapter(R.id.widget_list, Intent(context, FeedWidgetRemoteService::class.java))

            val pendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(Intent(context, RedditListActivity::class.java))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list, pendingIntentTemplate)
            remoteViews.setEmptyView(R.id.widget_list, R.id.widget_empty)
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == Constant.FEED_SYNC_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, javaClass))
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        }
    }

}