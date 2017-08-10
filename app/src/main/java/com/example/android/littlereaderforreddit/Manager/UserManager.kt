package com.example.android.littlereaderforreddit.Manager

import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil


class UserManager {
    companion object {
        @JvmStatic fun isLoggedIn() : Boolean {
            val expiresIn = SharedPreferenceUtil.getLong(Constant.EXPIRE_TIME)
            if (expiresIn.compareTo(System.currentTimeMillis()) > 0) {
                return true
            }
            return false
        }
    }
}
