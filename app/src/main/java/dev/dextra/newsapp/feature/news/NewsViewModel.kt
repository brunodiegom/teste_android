package dev.dextra.newsapp.feature.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState
import dev.dextra.newsapp.base.NetworkState.EMPTY
import dev.dextra.newsapp.base.NetworkState.ERROR
import dev.dextra.newsapp.base.NetworkState.RUNNING
import dev.dextra.newsapp.base.NetworkState.SUCCESS

/**
 * [BaseViewModel] that provides the [Article] content to be presented on View layer.
 * As well as, the current status of the [NetworkState]
 */
class NewsViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    private val _articles = MutableLiveData<List<Article>>()
    private val _networkState = MutableLiveData<NetworkState>()

    /**
     * Provides a [Source] list to be presented at the View.
     */
    val articles: LiveData<List<Article>> = _articles

    /**
     * Provides the current status of [NetworkState].
     */
    val networkState: LiveData<NetworkState> = _networkState

    /**
     * Indicates when the server is being accessed.
     */
    var isLoading: LiveData<Boolean> = Transformations.map(networkState, this::isLoading)

    /**
     * Indicates when the retry button must be shown.
     */
    var isRetryAllowed: LiveData<Boolean> = Transformations.map(networkState, this::hasError)

    /**
     * Indicates when the [Source] list must be shown.
     */
    var shouldShowList: LiveData<Boolean> = Transformations.map(articles, this::shouldShowList)

    /**
     * Indicates when the there's a message to be shown.
     * It should happen when the [NetworkState] is an [ERROR] or [EMPTY].
     */
    var hasMessage: LiveData<Boolean> = Transformations.map(networkState, this::hasMessage)

    /**
     * Text to be set as a title.
     */
    var errorMessageTitle: LiveData<Int?> = Transformations.map(networkState, this::getErrorTitle)

    /**
     * Text to be set as a subtitle.
     */
    var errorMessageSubtitle: LiveData<Int?> = Transformations.map(networkState, this::getErrorSubtitle)

    private var source: Source? = null

    /**
     * Set the [Source] that was selected, using as parameter to load the articles related.
     */
    fun configureSource(source: Source) {
        this.source = source
    }

    /**
     * Load the articles from selected [Source].
     */
    fun loadNews() {
        setState(RUNNING)
        addDisposable(
            newsRepository.getEverything(
                source?.id
            ).subscribe({
                _articles.postValue(it.articles)
                setState(getResponseState(it.articles))
            }, { setState(ERROR) })
        )
    }

    private fun isLoading(networkState: NetworkState) = networkState == RUNNING

    private fun hasError(networkState: NetworkState) = networkState == ERROR

    private fun hasEmptyList(networkState: NetworkState) = networkState == EMPTY

    private fun hasMessage(networkState: NetworkState) = hasError(networkState) || hasEmptyList(networkState)

    private fun shouldShowList(list: List<Article>) = list.isNotEmpty()

    private fun getErrorTitle(networkState: NetworkState): Int? = when (networkState) {
        ERROR -> R.string.error_state_title_source
        EMPTY -> R.string.empty_state_title_source
        else -> null
    }

    private fun getErrorSubtitle(networkState: NetworkState): Int? = when (networkState) {
        ERROR -> R.string.error_state_subtitle_source
        EMPTY -> R.string.empty_state_subtitle_source
        else -> null
    }

    private fun getResponseState(list: List<Article>) = if (list.isEmpty()) EMPTY else SUCCESS

    private fun setState(state: NetworkState) {
        _networkState.postValue(state)
    }
}
