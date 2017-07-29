package com.example.android.littlereaderforreddit

import android.app.Application
import com.facebook.stetho.Stetho

class RedditApplication : Application() {
    companion object {
        lateinit var instance : RedditApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this);
    }
}