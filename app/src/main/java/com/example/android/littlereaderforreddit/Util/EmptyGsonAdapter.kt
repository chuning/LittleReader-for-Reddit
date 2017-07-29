package com.example.android.littlereaderforreddit.Util

import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.Gson
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException


/**
 * Created by chuningluo on 17/8/1.
 */

class EmptyGsonAdapter<T>(private val delegate: TypeAdapter<T>,
                               private val elementAdapter: TypeAdapter<JsonElement>) : TypeAdapter<T>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: T) {
        this.delegate.write(out, value)
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): T? {
        val asJsonObject = elementAdapter.read(`in`).asJsonObject
        if (asJsonObject.entrySet().isEmpty()) return null
        return this.delegate.fromJsonTree(asJsonObject)
    }
}

