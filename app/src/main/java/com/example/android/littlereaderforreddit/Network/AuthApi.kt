package com.example.android.littlereaderforreddit.Network

import com.example.android.littlereaderforreddit.Data.Token
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface AuthApi {
    @POST("v1/access_token")
    fun getToken(@QueryMap paramMap: Map<String, String>): Call<Token>

    @POST("v1/revoke_token")
    fun revokeToken(@QueryMap paramMap: Map<String, String>): Call<Token>
}