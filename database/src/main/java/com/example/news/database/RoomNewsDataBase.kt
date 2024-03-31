package com.example.news.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.news.database.dao.ArticleDao
import com.example.news.database.models.ArticleDBO
import com.example.news.database.utils.DateTimeTypeConverter

class NewsDataBase internal constructor(private val dataBase: RoomNewsDataBase) {
    val articlesDao: ArticleDao
        get() = dataBase.articleDao()
}

@Database(entities = [ArticleDBO::class], version = 1, exportSchema = false)
@TypeConverters(DateTimeTypeConverter::class)
abstract class RoomNewsDataBase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

fun NewsDataBase(applicationContext: Context): NewsDataBase {
    val roomDataBase = Room.databaseBuilder(
        applicationContext.applicationContext,
        RoomNewsDataBase::class.java,
        "News"
    ).build()

    return NewsDataBase(roomDataBase)
}
