package com.example.android.littlereaderforreddit.UI

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.android.littlereaderforreddit.Network.SyncTask
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var authDialog : Dialog
    lateinit var webView : WebView

    companion object {
        private val CLIENT_ID = "17Kzwoo6MsSefg"
        private val REDIRECT_URI = "https://www.reddit.com"
        private val OAUTH_URL = "https://www.reddit.com/api/v1/authorize.compact"
        private val OAUTH_SCOPE = "read,mysubreddits"
        private val DURATION = "permanent"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpLoginDialog()
    }

    fun onLoggedIn() {
        startActivity(Intent(this, RedditListActivity::class.java))
        finish()
    }

    private fun setUpLoginDialog() {
        login.setOnClickListener {
            authDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            authDialog.setContentView(R.layout.dialog_auth)
            webView = authDialog.findViewById(R.id.webv) as WebView
            val url = "${OAUTH_URL}?client_id=${CLIENT_ID}&response_type=code" +
                    "&state=TEST&redirect_uri=${REDIRECT_URI}&duration=${DURATION}&scope=${OAUTH_SCOPE}"
            Log.d("Chuning url", url)
            webView.loadUrl(url)

            webView.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    if (url.contains("?code=") || url.contains("&code=")) {
                        val uri = Uri.parse(url)
                        val authCode = uri.getQueryParameter("code")
                        SharedPreferenceUtil.save(Constant.CODE, authCode)
                        FetchTokenAsyncTask().execute()
                        authDialog.dismiss()

                    } else if (url.contains("error=access_denied")) {
                        Toast.makeText(applicationContext, "Error Occured", Toast.LENGTH_SHORT).show()
                        authDialog.dismiss()
                    }
                }
            })
            authDialog.show()
            authDialog.setCancelable(true)
        }
    }

    inner class FetchTokenAsyncTask: AsyncTask<String, Void, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean {
            return SyncTask.syncAuth(isRefresh = false)
        }

        override fun onPostExecute(hasResult: Boolean) {
            if (hasResult) {
                onLoggedIn()
            }
        }

    }
}

