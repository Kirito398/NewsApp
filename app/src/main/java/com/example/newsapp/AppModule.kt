package com.example.newsapp

import android.content.Context
import com.example.news.database.NewsDataBase
import com.example.newsapi.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi {
        return NewsApi(
            baseUrl = BuildConfig.NEWS_BASE_URL,
            apiKey = BuildConfig.NEWS_API_KEY,
        )
    }

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDataBase {
        return NewsDataBase(context)
    }

//    @Provides
//    fun provideArticlesRepository(api: NewsApi, dataBase: NewsDataBase): ArticlesRepository {
//        return ArticlesRepository(dataBase, api)
//    }
}
