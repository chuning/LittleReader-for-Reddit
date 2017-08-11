package com.example.android.littlereaderforreddit.widget

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Binder
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.android.littlereaderforreddit.Data.FeedDbHelper
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.FeedsModel
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.DateTimeUtil


class FeedWidgetRemoteService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return FeedWidgetRemoteViewsFactory(applicationContext, intent)
    }

}

class FeedWidgetRemoteViewsFactory(val context: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {
    var cursor: Cursor? = null
    override fun onCreate() {
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.feed_widget_list_item)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        if (cursor != null) {
            cursor!!.close()
        }
        val identityToken = Binder.clearCallingIdentity()
        val selectAll = FeedDetail.FACTORY.SelectAll()
        val db = FeedDbHelper.getInstance(context).readableDatabase
        cursor = db.rawQuery(selectAll.statement, selectAll.args)
        Binder.restoreCallingIdentity(identityToken)
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor!!.moveToPosition(position)) {
            return null
        }
        val subreddit = cursor!!.getString(cursor!!.getColumnIndex(FeedsModel.SUBREDDIT_NAME_PREFIXED))
        val created_time = cursor!!.getLong(cursor!!.getColumnIndex(FeedsModel.CREATED_TIME))
        val title = cursor!!.getString(cursor!!.getColumnIndex(FeedsModel.TITLE))
        val score = cursor!!.getLong(cursor!!.getColumnIndex(FeedsModel.SCORE))
        val comments = cursor!!.getLong(cursor!!.getColumnIndex(FeedsModel.NUM_COMMENTS))
        val views = RemoteViews(context.packageName, R.layout.feed_widget_list_item)
        views.setTextViewText(R.id.subreddit_name, subreddit)
        views.setTextViewText(R.id.created_time, String.format("·%s", DateTimeUtil.deltaTime(created_time)))
        views.setTextViewText(R.id.feed_title, title)
        views.setTextViewText(R.id.score, String.format("%spts",score))
        views.setTextViewText(R.id.num_comments, String.format("%scomments",comments))

        val fillInIntent = Intent()
        views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent)
        return views
    }

    override fun getCount(): Int {
        return cursor?.count?:0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        if (cursor != null) {
            cursor!!.close()
            cursor = null
        }
    }

}
