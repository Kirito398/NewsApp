package com.example.news.main

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
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

    val state: StateFlow<State> = getAllArticlesUseCase.get().invoke(query = "android")
        .map { it.toState() }
        .stateIn(viewModelScope, SharingStarted.Lazily, State.None)

    private fun RequestResult<List<ArticleUI>>.toState(): State {
        return when (this) {
            is RequestResult.Error -> State.Error(this.data)
            is RequestResult.InProgress -> State.Loading(this.data)
            is RequestResult.Success -> State.Success(this.data)
        }
    }
}

@Stable
internal sealed class State(open val articles: List<ArticleUI>?) {
    @Immutable
    data object None : State(articles = null)
    @Stable
    class Loading(articles: List<ArticleUI>? = null) : State(articles)
    @Stable
    class Error(articles: List<ArticleUI>? = null) : State(articles)
    @Stable
    class Success(override val articles: List<ArticleUI>) : State(articles)
}
