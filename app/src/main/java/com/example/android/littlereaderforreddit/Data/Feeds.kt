package com.example.android.littlereaderforreddit.Data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import android.os.Parcel

data class Feeds(
        val data: FeedsData
)

data class FeedsData(
        val children: List<FeedChildren>
)

data class FeedChildren(
        val data: FeedDetail
)

data class Preview(
        val images: List<Image>
)

data class Image(
        val source: ImageView
)

data class ImageView(
        val url: String
)

class FeedDetail (
        val id: String,
        val author: String,
        val title: String,
        val num_comments: Int,
        val created: Long,
        val thumbnail: String?,
        val score: Long,
        val preview: Preview? = null,
        var largeImage: String? = null,
        @SerializedName("subreddit_name_prefixed") val subredditName : String) : Parcelable {

    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            author =  parcel.readString(),
            title = parcel.readString(),
            num_comments = parcel.readInt(),
            created = parcel.readLong(),
            thumbnail = parcel.readString(),
            score = parcel.readLong(),
            largeImage = parcel.readString(),
            subredditName = parcel.readString()) {
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(author)
        dest.writeString(title)
        dest.writeInt(num_comments)
        dest.writeLong(created)
        dest.writeString(thumbnail)
        dest.writeLong(score)
        val largeImage: String? = if (preview == null) "" else preview.images[0].source.url
        dest.writeString(largeImage)
        dest.writeString(subredditName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedDetail> {
        override fun createFromParcel(parcel: Parcel): FeedDetail {
            return FeedDetail(parcel)
        }

        override fun newArray(size: Int): Array<FeedDetail?> {
            return arrayOfNulls(size)
        }
    }

}

