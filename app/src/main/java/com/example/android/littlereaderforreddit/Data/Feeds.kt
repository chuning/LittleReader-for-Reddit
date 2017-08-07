package com.example.android.littlereaderforreddit.Data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import android.os.Parcel
import com.example.android.littlereaderforreddit.FeedsModel
import com.squareup.sqldelight.RowMapper

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

data class FeedDetail(
        val id: String,
        val author: String,
        val title: String,
        val num_comments: Long,
        @SerializedName("created") val created_formatted_time: String,
        val score: Long,
        var preview: Preview? = null,
        val thumbnail: String? = null,
        var large_image: String? = null,
        val selftext_html: String?,
        val subreddit: String) : FeedsModel, Parcelable {

    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            author = parcel.readString(),
            title = parcel.readString(),
            num_comments=  parcel.readLong(),
            created_formatted_time = parcel.readString(),
            score = parcel.readLong(),
            thumbnail = parcel.readString(),
            large_image = parcel.readString(),
            selftext_html = parcel.readString(),
            subreddit = parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(author)
        parcel.writeString(title)
        parcel.writeLong(num_comments)
        parcel.writeString(created_formatted_time)
        parcel.writeLong(score)
        parcel.writeString(thumbnail ?: "")
        parcel.writeString(large_image)
        parcel.writeString(selftext_html?: "")
        parcel.writeString(subreddit)
    }

    override fun id(): String {
        return id
    }

    override fun author(): String {
        return author
    }

    override fun title(): String {
        return title
    }

    override fun num_comments(): Long {
        return num_comments
    }

    override fun created_formatted_time(): String {
        return created_formatted_time
    }

    override fun score(): Long {
        return score
    }

    override fun large_image(): String? {
        return large_image
    }

    override fun self_text_html(): String? {
        return selftext_html
    }

    override fun subreddit_name_prefixed(): String {
        return subreddit
    }

    override fun thumbnail(): String? {
        return thumbnail
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedDetail> {
        val FACTORY = FeedsModel.Factory<FeedDetail>(object : FeedsModel.Creator<FeedDetail> {
            override fun create(id: String, author: String, title: String, num_comments: Long,
                                created_formatted_time: String, score: Long, thumbnail: String?, large_image: String?,
                                self_text_html: String?, subreddit_name_prefixed: String): FeedDetail {
                return FeedDetail(id, author, title, num_comments, created_formatted_time,
                        score, null, thumbnail, large_image, self_text_html, subreddit_name_prefixed)
            }
        })
        val SELECT_ALL_MAPPER: RowMapper<FeedDetail> = FACTORY.selectAllMapper()

        override fun createFromParcel(parcel: Parcel): FeedDetail {
            return FeedDetail(parcel)
        }

        override fun newArray(size: Int): Array<FeedDetail?> {
            return arrayOfNulls(size)
        }
    }
}


