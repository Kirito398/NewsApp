package com.example.news.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.data.RequestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
internal class NewsMainViewModel @Inject constructor(
    getAllArticlesUseCase: Provider<GetAllArticlesUseCase>,
) : ViewModel() {

    val state: StateFlow<State> = getAllArticlesUseCase.get().invoke()
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