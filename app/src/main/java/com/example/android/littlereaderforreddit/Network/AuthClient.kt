package com.example.android.littlereaderforreddit.Network

import com.example.android.littlereaderforreddit.Data.*
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthClient {
    private val authApi: AuthApi
    private val BASE_URL = "https://www.reddit.com/api/"

    init {
        val auth = Credentials.basic("17Kzwoo6MsSefg", "")
        val authInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain) : Response {
                val originalRequest = chain.request()
                val agentRequest = originalRequest.newBuilder()
                        .header("Authorization", auth)
                        .build()
                return chain.proceed(agentRequest)
            }
        }

        val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addNetworkInterceptor(StethoInterceptor())
                .build()


        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        authApi = retrofit.create(AuthApi::class.java)
    }

    companion object {
        private var _instance : AuthClient? = null
        val instance : AuthClient
            get() {
                if (_instance == null) {
                    _instance = AuthClient()
                }
                return _instance ?: throw AssertionError("AuthClient set to null by another thread")
            }
    }

    fun getToken(redirectUri: String): Call<Token> {
        val params = HashMap<String, String>()
        val code = SharedPreferenceUtil.get(Constant.CODE)
        params.put("code", code!!)
        params.put("grant_type", "authorization_code")
        params.put("redirect_uri", redirectUri)
        return authApi.getToken(params)
    }

    fun refreshToken(): Call<Token> {
        val params = HashMap<String, String>()
        val refreshToken = SharedPreferenceUtil.get(Constant.REFRESH_TOKEN)
        params.put("grant_type", "refresh_token")
        params.put("refresh_token", refreshToken!!)
        return authApi.getToken(params)
    }

    fun logout(): Call<Token> {
        val params = HashMap<String, String>()
        val token = SharedPreferenceUtil.get(Constant.ACCESS_TOKEN)
        params.put("token", token!!)
        params.put("token_type_hint", "access_token")
        return authApi.revokeToken(params)
    }
}