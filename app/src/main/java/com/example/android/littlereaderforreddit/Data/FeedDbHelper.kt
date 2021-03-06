package com.example.android.littlereaderforreddit.Data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.android.littlereaderforreddit.FeedsModel
import com.squareup.sqlbrite2.BriteDatabase
import com.squareup.sqlbrite2.SqlBrite
import io.reactivex.schedulers.Schedulers


class FeedDbHelper private constructor(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(FeedsModel.CREATE_TABLE);
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // upgrade logic
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME)
        onCreate(db)
    }

    companion object {
        val DB_NAME: String = "reddit_feeds_table.db"
        val DB_VERSION = 1
        private var instance: FeedDbHelper? = null
        fun getInstance(context: Context): FeedDbHelper {
            if (null == instance) {
                instance = FeedDbHelper(context)
            }
            return instance!!
        }
    }
}

class SqlBriteDb {
    companion object {
        private var _instance: SqlBrite? = null
        fun getInstance(): SqlBrite {
            if (_instance == null) {
                _instance = SqlBrite.Builder().build()
            }
            return _instance!!
        }
    }
}

class Db {
    companion object {
        private var _instance: BriteDatabase? = null
        fun getInstance(context: Context): BriteDatabase {
            if (_instance == null) {
                _instance = SqlBriteDb.getInstance()
                        .wrapDatabaseHelper(FeedDbHelper.getInstance(context), Schedulers.io())
                _instance!!.setLoggingEnabled(true)
            }
            return _instance!!
        }
    }
}
