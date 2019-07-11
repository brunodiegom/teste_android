package dev.dextra.newsapp.api.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import dev.dextra.newsapp.api.repository.NewsRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Paged data source factory.
 */
class ArticleDataFactory(
    private val disposable: CompositeDisposable,
    private val newsRepository: NewsRepository,
    private val source: Source
) : DataSource.Factory<Int, Article>() {

    /**
     * Provides the [MutableLiveData] representing the [ArticleDataSource].
     */
    val articles = MutableLiveData<ArticleDataSource>()

    override fun create(): DataSource<Int, Article> {
        val newsDataSource = ArticleDataSource(
            disposable,
            newsRepository,
            source
        )
        articles.postValue(newsDataSource)
        return newsDataSource
    }
}
