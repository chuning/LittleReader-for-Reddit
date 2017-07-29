package com.example.android.littlereaderforreddit.UI

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.*
import android.content.SharedPreferences
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.android.littlereaderforreddit.R


class LoginActivity : AppCompatActivity() {

    lateinit var loginButton : Button
    lateinit var authDialog : Dialog
    lateinit var webView : WebView
    var DEVICE_ID = UUID.randomUUID().toString()
    var token: String? = null
    var authComplete = false
    val resultIntent = Intent()
    lateinit var pref: SharedPreferences

    companion object {
        private val CLIENT_ID = "17Kzwoo6MsSefg"
        private val CLIENT_SECRET = ""
        private val REDIRECT_URI = "https://www.reddit.com"
        private val GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
        private val GRANT_TYPE2 = "authorization_code"
        private val TOKEN_URL = "access_token"
        private val OAUTH_URL = "https://www.reddit.com/api/v1/authorize"
        private val OAUTH_SCOPE = "read,mysubreddits"
        private val DURATION = "permanent"
        private val ACCESS_TOKEN = "access_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);

        loginButton = findViewById(R.id.login) as Button
        setUpLoginDialog()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("Chuning onresul", resultCode.toString())
        if (resultCode == Activity.RESULT_OK) {
            startActivity(Intent(this, RedditListActivity::class.java))
        }
    }

    fun onLoggedIn() {
        startActivity(Intent(this, RedditListActivity::class.java))
        finish()
    }

    private fun setUpLoginDialog() {
        loginButton.setOnClickListener {
            authDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            authDialog.setContentView(R.layout.dialog_auth)
            webView = authDialog.findViewById(R.id.webv) as WebView
            val url = "${OAUTH_URL}?client_id=${CLIENT_ID}&response_type=token&state=TEST&redirect_uri=${REDIRECT_URI}&scope=${OAUTH_SCOPE}"
            webView.loadUrl(url)

            webView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    if (url.contains(ACCESS_TOKEN)) {
                        token = url.substringAfter("access_token=").substringBefore("&")
                        authComplete = true
                        resultIntent.putExtra(ACCESS_TOKEN, token)

                        setResult(Activity.RESULT_OK, resultIntent)
                        val edit = pref.edit()
                        edit.putString(ACCESS_TOKEN, token)
                        edit.commit()
                        authDialog.dismiss()
                        onLoggedIn()

                    } else if (url.contains("error=access_denied")) {
                        resultIntent.putExtra(ACCESS_TOKEN, token)
                        authComplete = true
                        setResult(Activity.RESULT_CANCELED, resultIntent)
                        Toast.makeText(applicationContext, "Error Occured", Toast.LENGTH_SHORT).show()
                        authDialog.dismiss()
                    }
                }
            })
            authDialog.show()
            authDialog.setTitle("Authorize")
            authDialog.setCancelable(true)
        }
    }

}
