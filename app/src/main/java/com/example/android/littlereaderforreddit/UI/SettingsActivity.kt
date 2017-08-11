package com.example.android.littlereaderforreddit.UI

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.android.littlereaderforreddit.Data.Db
import com.example.android.littlereaderforreddit.Data.Subreddit
import com.example.android.littlereaderforreddit.FeedsModel
import com.example.android.littlereaderforreddit.Network.AuthClient
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        notification_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SharedPreferenceUtil.saveBoolean(Constant.NOTIFICATION_PREFERENCE, true)
            } else {
                SharedPreferenceUtil.saveBoolean(Constant.NOTIFICATION_PREFERENCE, false)
            }
        }

        log_out_button.setOnClickListener {
            LogOutAsyncTask().execute()
        }

    }

    inner class LogOutAsyncTask: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val response = AuthClient.instance.logout().execute()
                if (response.isSuccessful) {
                    logout()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    private fun logout() {
        SharedPreferenceUtil.clearAll()
        Db.getInstance(this).writableDatabase.execSQL(FeedsModel.DELETEALL)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
