package com.example.android.littlereaderforreddit.Util

import android.content.Context
import android.content.SharedPreferences
import com.example.android.littlereaderforreddit.RedditApplication

class SharedPreferenceUtil {

    companion object {
        private var _prefs: SharedPreferences? = null
        val prefs: SharedPreferences
            get() {
                if (_prefs == null) {
                    _prefs = RedditApplication.instance.getSharedPreferences("AppPref", Context.MODE_PRIVATE)
                }
                return _prefs!!
            }

        fun save(key: String, value: String) {
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun get(key: String): String? {
            return prefs.getString(key, null)
        }
    }
}
