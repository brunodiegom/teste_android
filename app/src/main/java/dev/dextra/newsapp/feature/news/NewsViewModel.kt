package dev.dextra.newsapp.feature.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.ArticleDataFactory
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.NetworkState
import dev.dextra.newsapp.base.NetworkState.EMPTY
import dev.dextra.newsapp.base.NetworkState.ERROR
import dev.dextra.newsapp.base.NetworkState.RUNNING
import dev.dextra.newsapp.base.NetworkState.SUCCESS
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executors

/**
 * [ViewModel] that provides the [Article] content to be presented on View layer.
 * As well as, the current status of the [NetworkState]
 */
class NewsViewModel(
    newsRepository: NewsRepository,
    source: Source
) : ViewModel() {

    private val disposable = CompositeDisposable()

    private val executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

    private var dataFactory = ArticleDataFactory(disposable, newsRepository, source)

    private val networkState = Transformations.switchMap(dataFactory.articles) { it.networkState }

    private val config: PagedList.Config = PagedList.Config.Builder()
        .setPageSize(PAGE_SIZE)
        .setInitialLoadSizeHint(PAGE_SIZE_HINT)
        .setEnablePlaceholders(false)
        .build()

    /**
     * Provides a [Article] list to be presented at the View.
     */
    val articles: LiveData<PagedList<Article>> = LivePagedListBuilder(dataFactory, config)
        .setFetchExecutor(executor)
        .build()

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
    var shouldShowList: LiveData<Boolean> = Transformations.map(networkState, this::shouldShowList)

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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    private fun isLoading(networkState: NetworkState) = networkState == RUNNING

    private fun hasError(networkState: NetworkState) = networkState == ERROR

    private fun hasEmptyList(networkState: NetworkState) = networkState == EMPTY

    private fun hasMessage(networkState: NetworkState) = hasError(networkState) || hasEmptyList(networkState)

    private fun shouldShowList(networkState: NetworkState) = networkState == SUCCESS || networkState == RUNNING

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

    companion object {
        private const val PAGE_SIZE = 5
        private const val PAGE_SIZE_HINT = 9
        private const val THREAD_POOL_SIZE = 5
    }
}
