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

//class FeedDetail (
//        val id: String,
//        val author: String,
//        val title: String,
//        val num_comments: Int,
//        val created: Long,
//        val thumbnail: String?,
//        val score: Long,
//        val preview: Preview? = null,
//        var largeImage: String? = null,
//        val self_text_html: String?,
//        @SerializedName("subreddit_name_prefixed") val subredditName : String) : Parcelable {
//
//    constructor(parcel: Parcel) : this(
//            id = parcel.readString(),
//            author =  parcel.readString(),
//            title = parcel.readString(),
//            num_comments = parcel.readInt(),
//            created = parcel.readLong(),
//            thumbnail = parcel.readString(),
//            score = parcel.readLong(),
//            largeImage = parcel.readString(),
//            self_text_html = parcel.readString(),
//            subredditName = parcel.readString()) {
//    }
//
//
//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeString(id)
//        dest.writeString(author)
//        dest.writeString(title)
//        dest.writeInt(num_comments)
//        dest.writeLong(created)
//        dest.writeString(thumbnail)
//        dest.writeLong(score)
//        val largeImage: String? = if (preview == null) "" else preview.images[0].source.url
//        dest.writeString(largeImage)
//        dest.writeString(self_text_html)
//        dest.writeString(subredditName)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<FeedDetail> {
//        override fun createFromParcel(parcel: Parcel): FeedDetail {
//            return FeedDetail(parcel)
//        }
//
//        override fun newArray(size: Int): Array<FeedDetail?> {
//            return arrayOfNulls(size)
//        }
//    }
//
//}

data class FeedDetail(
        val _id: Long,
        val id: String,
        val author: String,
        val title: String,
        val num_comments: Long,
        @SerializedName("created") val created_formatted_time: String,
        val score: Long?,
        var preview: Preview? = null,
        val thumbnail: String?,
        var large_image: String? = null,
        val self_text_html: String?,
        val subreddit_name_prefixed: String) : FeedsModel, Parcelable {

    constructor(parcel: Parcel) : this(
            _id = parcel.readLong(),
            id = parcel.readString(),
            author = parcel.readString(),
            title = parcel.readString(),
            num_comments=  parcel.readLong(),
            created_formatted_time = parcel.readString(),
            score = parcel.readLong(),
            thumbnail = parcel.readString(),
            large_image = parcel.readString(),
            self_text_html = parcel.readString(),
            subreddit_name_prefixed = parcel.readString())

    override fun _id(): Long {
        return _id
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

    override fun score(): Long? {
        return score
    }

    override fun large_image(): String? {
        return large_image
    }

    override fun self_text_html(): String? {
        return self_text_html
    }

    override fun subreddit_name_prefixed(): String {
        return subreddit_name_prefixed
    }

    override fun thumbnail(): String? {
        return thumbnail
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(_id)
        parcel.writeString(id)
        parcel.writeString(author)
        parcel.writeString(title)
        parcel.writeLong(num_comments)
        parcel.writeString(created_formatted_time)
        parcel.writeValue(score)
        parcel.writeValue(thumbnail)
        val largeImage: String? = if (preview == null) "" else preview!!.images[0].source.url
        parcel.writeString(largeImage)
        parcel.writeString(self_text_html)
        parcel.writeString(subreddit_name_prefixed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedDetail> {
        val FACTORY = FeedsModel.Factory<FeedDetail>(object : FeedsModel.Creator<FeedDetail> {
            override fun create(_id: Long, id: String, author: String, title: String, num_comments: Long,
                                created_formatted_time: String, score: Long?, thumbnail: String?, large_image: String?,
                                self_text_html: String?, subreddit_name_prefixed: String): FeedDetail {
                return FeedDetail(_id, id, author, title, num_comments, created_formatted_time,
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


