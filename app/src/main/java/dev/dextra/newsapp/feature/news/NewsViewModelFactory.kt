package dev.dextra.newsapp.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository

/**
 * [ViewModelProvider.Factory] used to send [Source] parameter on View Model initialization.
 */
class NewsViewModelFactory(
    private val newsRepository: NewsRepository,
    private val source: Source
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, source) as T
    }
}
