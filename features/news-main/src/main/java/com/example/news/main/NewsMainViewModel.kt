package com.example.news.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.data.ArticlesRepository
import com.example.news.data.RequestResult
import com.example.news.data.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class NewsMainViewModel(
    getAllArticlesUseCase: GetAllArticlesUseCase,
    private val repository: ArticlesRepository,
) : ViewModel() {

    val state: StateFlow<State> = getAllArticlesUseCase()
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)

    private fun RequestResult<List<Article>>.toState(): State {
        return when(this) {
            is RequestResult.Error -> State.Error(this.data)
            is RequestResult.InProgress -> State.Loading(this.data)
            is RequestResult.Success -> State.Success(this.data)
        }
    }
}

sealed class State {
    object None : State()
    class Loading(val articles: List<Article>?) : State()
    class Error(val articles: List<Article>?) : State()
    class Success(val articles: List<Article>) : State()
}