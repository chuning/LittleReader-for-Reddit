package com.example.android.littlereaderforreddit.Network

import android.content.Context
import android.content.Intent
import com.example.android.littlereaderforreddit.Util.Constant

/**
 * Created by chuningluo on 17/8/4.
 */

class SyncUtils {
    companion object {
        private var sInitialized: Boolean = false

        fun initialize(context: Context) {
            if (sInitialized) return
            sInitialized = true
            startImmediateSync(context)
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
    }

}