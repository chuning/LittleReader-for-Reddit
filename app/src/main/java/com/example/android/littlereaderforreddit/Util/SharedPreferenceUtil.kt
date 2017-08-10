package com.example.android.littlereaderforreddit.Util

import android.content.Context
import android.content.SharedPreferences
import com.example.android.littlereaderforreddit.RedditApplication
import java.security.Timestamp

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

        fun saveLong(key: String, value: Long) {
            val editor = prefs.edit()
            editor.putLong(key, value)
            editor.apply()
        }

        fun getLong(key: String): Long {
            return prefs.getLong(key, -1)
        }

    }
}
