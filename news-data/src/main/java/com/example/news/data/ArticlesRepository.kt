package com.example.news.data

import com.example.news.data.model.Article
import com.example.news.database.NewsDataBase
import com.example.news.database.models.ArticleDBO
import com.example.newsapi.NewsApi
import com.example.newsapi.models.ArticleDTO
import com.example.newsapi.models.ResponseDTO
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

class ArticlesRepository @Inject constructor(
    private val dataBase: NewsDataBase,
    private val api: NewsApi,
) {

    fun getAll(
        mergeStrategy: MergeStrategy<RequestResult<List<Article>>> = RequestResponseMergeStrategy<List<Article>>()
    ): Flow<RequestResult<List<Article>>> {
        val cachedAllArticles: Flow<RequestResult<List<Article>>> = getAllFromDataBase()
            .map { result: RequestResult<List<ArticleDBO>> ->
                result.map { articleDBOs: List<ArticleDBO> ->
                    articleDBOs.map {
                        it.toArticle()
                    }
                }
            }
        val remoteArticles: Flow<RequestResult<List<Article>>> = getAllFromServer()
            .map { result: RequestResult<ResponseDTO<ArticleDTO>> ->
                result.map { response: ResponseDTO<ArticleDTO> ->
                    response.articles.map {
                        it.toArticle()
                    }
                }
            }

        return cachedAllArticles.combine(remoteArticles, mergeStrategy::merge)
            .flatMapLatest { result ->
                if (result is RequestResult.Success) {
                    dataBase.articlesDao.observeAll()
                        .map { dbos -> dbos.map { it.toArticle() } }
                        .map { RequestResult.Success(it) }
                } else {
                    flowOf(result)
                }
            }
    }

    private fun getAllFromServer(): Flow<RequestResult<ResponseDTO<ArticleDTO>>> {
        val apiRequest = flow {
            emit(api.everything())
        }.onEach { result ->
            if (result.isSuccess) {
                saveNetResponseToCache(result.getOrThrow().articles)
            }
        }.map {
            it.toRequestResult()
        }

        val start = flowOf<RequestResult<ResponseDTO<ArticleDTO>>>(RequestResult.InProgress())

        return merge(start, apiRequest)
    }

    private suspend fun saveNetResponseToCache(data: List<ArticleDTO>) {
        val dbos = data.map { articleDto -> articleDto.toArticleDbo() }
        dataBase.articlesDao.insert(dbos)
    }

    private fun getAllFromDataBase(): Flow<RequestResult<List<ArticleDBO>>> {
        val dbRequest = dataBase.articlesDao::getAll.asFlow()
            .map { RequestResult.Success(it) }

        val start = flowOf<RequestResult<List<ArticleDBO>>>(RequestResult.InProgress())

        return merge(start, dbRequest)
    }
}