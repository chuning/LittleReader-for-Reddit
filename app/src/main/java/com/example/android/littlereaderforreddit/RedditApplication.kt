package com.example.android.littlereaderforreddit

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.firebase.analytics.FirebaseAnalytics

class RedditApplication : Application() {
    companion object {
        lateinit var instance : RedditApplication
            private set
        private lateinit var sAnalytics: FirebaseAnalytics
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this)
        sAnalytics = FirebaseAnalytics.getInstance(this)
    }
}