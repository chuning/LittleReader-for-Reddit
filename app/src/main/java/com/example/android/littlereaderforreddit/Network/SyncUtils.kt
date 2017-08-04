package com.example.android.littlereaderforreddit.Network

import android.content.Context
import android.content.Intent

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
            context.startService(intentService)
        }
    }

}