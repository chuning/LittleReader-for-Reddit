package com.example.android.littlereaderforreddit.Network

import com.example.android.littlereaderforreddit.Data.*
import com.example.android.littlereaderforreddit.Manager.SubredditsManager
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


class RetrofitClient {
    private val reditApi : RedditApi
    private val BASE_URL = "https://oauth.reddit.com"

    init {
        val networkInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain) : Response {
                val originalRequest = chain.request()
                val token = SharedPreferenceUtil.get(Constant.ACCESS_TOKEN)
                if (token == null) {
                    return chain.proceed(originalRequest)
                } else {
                    val agentRequest = originalRequest.newBuilder()
                            .header("Authorization", String.format("bearer %s", token))
                            .build()
                    return chain.proceed(agentRequest)
                }
            }
        }

        val client = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .addNetworkInterceptor(networkInterceptor)
                .build()

        val gson = GsonBuilder().registerTypeAdapter(CommentDetail::class.java, object : JsonDeserializer<CommentDetail> {
            override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): CommentDetail? {
                val jsonObject = json.asJsonObject
                if (!jsonObject.has("depth") || !jsonObject.has("body")) return null
                val depth = jsonObject.get("depth").asInt
                val replies = jsonObject.get("replies")
                val body = jsonObject.get("body")?.asString
                val author = jsonObject.get("author")?.asString
                val created = jsonObject.get("created_utc")?.asLong
                val score = jsonObject.get("score")?.asLong
                if (replies == null || replies.isJsonNull
                        || (replies.isJsonPrimitive) && replies.asString.isEmpty()) {
                    val count = jsonObject.get("count")?.asInt
                    return CommentDetail(depth, body, author, created, score, null, count)
                }
                val deserializedReplies: Comments =context.deserialize(jsonObject.get("replies"), Comments::class.java)
                return CommentDetail(depth, body, author, created, score, deserializedReplies, null)
            }
        }).create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        reditApi = retrofit.create(RedditApi::class.java)
    }

    companion object {
        private var _instance : RetrofitClient? = null
        val instance : RetrofitClient
            get() {
                if (_instance == null) {
                    _instance = RetrofitClient()
                }
                return _instance ?: throw AssertionError("RetrofitClient set to null by another thread")
            }
    }

    fun getFeeds(link: String?): Call<Feeds> {
        val params = HashMap<String, String>()
        if (link != null) {
            params.put("after", "t3_" + link)
        }
        val excludedSubreddits = SubredditsManager.getExcludedSubredditsList()
        if (excludedSubreddits.isNotEmpty()) {
            val selected = SubredditsManager.getSelectedSubredditsList().joinToString("+")
            return reditApi.getRedditFeedsWithFilter(selected, params)
        } else {
            return reditApi.getRedditFeeds(params)
        }
    }

    fun getComments(id: String): Call<List<Comments>> {
        return reditApi.getComments(id)
    }

    fun getSubreddits(): Call<SubredditResponse> {
        return reditApi.getSubreddits()
    }

}