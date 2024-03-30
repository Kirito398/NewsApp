package com.example.newsapp

import android.content.Context
import com.example.news.database.NewsDataBase
import com.example.newsapi.NewsApi
import com.sir.news.common.AndroidLogcatLogger
import com.sir.news.common.AppDispatchers
import com.sir.news.common.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideNewsApi(okHttpClient: OkHttpClient?): NewsApi {
        return NewsApi(
            baseUrl = BuildConfig.NEWS_BASE_URL,
            apiKey = BuildConfig.NEWS_API_KEY,
            okHttpClient = okHttpClient
        )
    }

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDataBase {
        return NewsDataBase(context)
    }

    @Provides
    @Singleton
    fun provideAppCoroutineDispatcher(): AppDispatchers {
        return AppDispatchers()
    }

    @Provides
    fun provideLogger(): Logger {
        return AndroidLogcatLogger()
    }
}
