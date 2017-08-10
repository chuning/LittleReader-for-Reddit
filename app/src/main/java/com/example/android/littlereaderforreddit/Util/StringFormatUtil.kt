package com.example.android.littlereaderforreddit.Util

import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView


class StringFormatUtil {
    companion object {
        fun formatHtml(text: String, textView: TextView) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY))
            } else {
                textView.text = Html.fromHtml(Html.fromHtml(text).toString())
            }
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}