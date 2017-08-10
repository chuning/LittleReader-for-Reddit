package com.example.android.littlereaderforreddit.Network

import android.content.Context
import android.content.Intent
import com.example.android.littlereaderforreddit.Util.Constant
import com.firebase.jobdispatcher.*
import java.util.concurrent.TimeUnit

/**
 * Created by chuningluo on 17/8/4.
 */

class SyncUtils {
    companion object {
        private var sInitialized: Boolean = false
        private val FEED_SYNC_INTERVAL_HOURS = 1
        private val FEED_SYNC_INTERVAL_SECONDS = TimeUnit.HOURS.toSeconds(FEED_SYNC_INTERVAL_HOURS.toLong()).toInt()
        private val FEED_SYNC_FLEXTIME_SECONDS = FEED_SYNC_INTERVAL_SECONDS / 3
        private val TOKEN_SYNC_INTERVAL_MINUTES = 55
        private val TOKEN_SYNC_INTERVAL_SECONDS = TimeUnit.MINUTES.toSeconds(TOKEN_SYNC_INTERVAL_MINUTES.toLong()).toInt()
        private val TOKEN_SYNC_FLEXTIME_SECONDS = TOKEN_SYNC_INTERVAL_SECONDS / 3

        fun initialize(context: Context) {
            if (sInitialized) return
            sInitialized = true
            startImmediateSync(context)
            scheduleJobDispatcherForFeeds(context)
            scheduleRefreshToken(context)
        }

        fun startImmediateSync(context: Context) {
            val intentService = Intent(context, FeedSyncIntentService::class.java)
            intentService.putExtra(Constant.EXTRA_SYNC_FOR_PAGING, false)
            context.startService(intentService)
        }

        fun startSyncForPaging(context: Context, link: String?) {
            val intentService = Intent(context, FeedSyncIntentService::class.java)
            intentService.putExtra(Constant.EXTRA_LAST_LINK_ID, link)
            intentService.putExtra(Constant.EXTRA_SYNC_FOR_PAGING, true)
            context.startService(intentService)
        }

        fun scheduleJobDispatcherForFeeds(context: Context) {
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
            val feedSyncJob = dispatcher.newJobBuilder()
                    .setService(FeedFirebaseJobService::class.java)
                    .setTag(Constant.FEED_SYNC_TAG)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(FEED_SYNC_INTERVAL_SECONDS,
                            FEED_SYNC_INTERVAL_SECONDS + FEED_SYNC_FLEXTIME_SECONDS))
                    .setReplaceCurrent(true)
                    .build()
            dispatcher.schedule(feedSyncJob)
        }

        fun scheduleRefreshToken(context: Context) {
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
            val tokenRefreshJob = dispatcher.newJobBuilder()
                    .setService(AuthenticationJobService::class.java)
                    .setTag(Constant.TOKEN_SYNC_TAG)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(TOKEN_SYNC_INTERVAL_SECONDS,
                            TOKEN_SYNC_INTERVAL_SECONDS + TOKEN_SYNC_FLEXTIME_SECONDS))
                    .setReplaceCurrent(true)
                    .build()
            dispatcher.schedule(tokenRefreshJob)
        }
    }

}