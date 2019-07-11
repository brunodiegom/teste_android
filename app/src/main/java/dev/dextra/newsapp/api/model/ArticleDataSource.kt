package dev.dextra.newsapp.api.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.NetworkState
import dev.dextra.newsapp.base.NetworkState.EMPTY
import dev.dextra.newsapp.base.NetworkState.ERROR
import dev.dextra.newsapp.base.NetworkState.RUNNING
import dev.dextra.newsapp.base.NetworkState.SUCCESS
import io.reactivex.disposables.CompositeDisposable

/**
 * [PageKeyedDataSource] responsible to loading the [Article] paged.
 */
class ArticleDataSource(
    private val disposable: CompositeDisposable,
    private val newsRepository: NewsRepository,
    private val source: Source
) : PageKeyedDataSource<Int, Article>() {

    val networkState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Article>) {
        networkState.postValue(RUNNING)
        disposable.add(
            newsRepository.getEverything(
                sources = source.id,
                pageSize = params.requestedLoadSize
            ).subscribe({
                if (it.articles.isNotEmpty()) {
                    callback.onResult(it.articles, null, params.requestedLoadSize + 1)
                    networkState.postValue(SUCCESS)
                } else {
                    networkState.postValue(EMPTY)
                }
            }, {
                networkState.postValue(ERROR)
            })
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Article>) {
        networkState.postValue(RUNNING)
        disposable.add(
            newsRepository.getEverything(
                sources = source.id,
                page = params.key,
                pageSize = params.requestedLoadSize
            ).subscribe({
                if (it.articles.isNotEmpty()) {
                    val next = if (params.key < PAGE_LIMIT) params.key + 1 else null
                    callback.onResult(it.articles, next)
                    networkState.postValue(SUCCESS)
                } else {
                    networkState.postValue(EMPTY)
                }
            }, {
                networkState.postValue(ERROR)
            })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Article>) {
        // Nothing to be done.
    }

    companion object {
        private const val PAGE_LIMIT = 20
    }
}
