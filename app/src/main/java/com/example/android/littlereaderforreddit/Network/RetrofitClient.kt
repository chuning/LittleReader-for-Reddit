package com.example.android.littlereaderforreddit.Network

import android.content.Context
import android.util.Log
import com.example.android.littlereaderforreddit.Data.CommentDetail
import com.example.android.littlereaderforreddit.Data.Comments
import com.example.android.littlereaderforreddit.Data.CommentsOrNull
import com.example.android.littlereaderforreddit.Data.Feeds
import com.example.android.littlereaderforreddit.RedditApplication
import com.example.android.littlereaderforreddit.Util.Constant
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


class RetrofitClient {
    private val reditApi : RedditApi
    private val BASE_URL = "https://oauth.reddit.com"

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val networkInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain) : Response {
                val originalRequest = chain.request()
                val agentRequest = originalRequest.newBuilder()
                        .header("Authorization", String.format("bearer %s", getToken()))
                        .build()
                return chain.proceed(agentRequest)
            }
        }

        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(StethoInterceptor())
                .addNetworkInterceptor(networkInterceptor)
                .build()


        val gson = GsonBuilder().create()
        val newGson = GsonBuilder().registerTypeAdapter(CommentsOrNull::class.java, object : JsonDeserializer<CommentsOrNull> {
            override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): CommentsOrNull? {
                if (json.isJsonPrimitive && json.asString.isEmpty()) {
                    return null
                }
                return gson.fromJson(json, CommentsOrNull::class.java)
            }
        }).create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(newGson))
                .client(client)
                .build()
        reditApi = retrofit.create(RedditApi::class.java)
    }

    companion object {
        private val CLIENT_ID = "17Kzwoo6MsSefg"
        private val RESPONSE_TYPE = "token"


        private val CLIENT_SECRET = ""
        private val REDIRECT_URI = "https://www.reddit.com"
        private val GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
        private val GRANT_TYPE2 = "authorization_code"
        private val TOKEN_URL = "access_token"
        private val OAUTH_URL = "https://www.reddit.com/api/v1/authorize"
        private val OAUTH_SCOPE = "read,mysubreddits"
        private val DURATION = "permanent"

        private var _instance : RetrofitClient? = null
        val instance : RetrofitClient
            get() {
                if (_instance == null) {
                    _instance = RetrofitClient()
                }
                return _instance ?: throw AssertionError("RetrofitClient set to null by another thread")
            }
    }

    fun getToken(): String {
        val pref = RedditApplication.instance.getSharedPreferences("AppPref", Context.MODE_PRIVATE)
        val token = pref.getString(Constant.ACCESS_TOKEN, "")
        Log.d("Chuning token", token)
        return token
    }

    fun getFeeds(): Call<Feeds> {
        return reditApi.getRedditFeeds()
    }

    fun getComments(id: String): Call<List<Comments>> {
        return reditApi.getComments(id)
    }

}