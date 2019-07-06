package dev.dextra.newsapp.feature.sources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.model.enums.Category
import dev.dextra.newsapp.api.model.enums.Country
import dev.dextra.newsapp.api.model.extension.getSourceValue
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState
import dev.dextra.newsapp.base.NetworkState.ERROR
import dev.dextra.newsapp.base.NetworkState.SUCCESS
import kotlin.properties.Delegates.observable

/**
 * [BaseViewModel] that provides the [Source] content to be presented on View layer.
 * As the current status of the [NetworkState]
 */
class SourcesViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    private val _sources = MutableLiveData<List<Source>>()
    private val _networkState = MutableLiveData<NetworkState>()

    /**
     * Provides a [Source] list to be presented at the View.
     */
    val sources: LiveData<List<Source>> = _sources

    /**
     * Provides the current status of [NetworkState].
     */
    val networkState: LiveData<NetworkState> = _networkState

    /**
     * Represents the selected [Country] to be used on server request.
     */
    var selectedCountry: Country by observable(Country.ALL, { _, oldValue, newValue ->
        if (oldValue != newValue) loadSources()
    })

    /**
     * Represents the selected [Category] to be used on server request.
     */
    var selectedCategory: Category by observable(Category.ALL, { _, oldValue, newValue ->
        if (oldValue != newValue) loadSources()
    })

    /**
     * Request to server to load the [Source] data, updating the [NetworkState].
     */
    fun loadSources() {
        setState(NetworkState.RUNNING)
        addDisposable(
            newsRepository.getSources(
                selectedCountry.getSourceValue(),
                selectedCategory.getSourceValue()
            ).subscribe({
                _sources.postValue(it.sources)
                setState(getResponseState(it.sources))
            }, {
                setState(ERROR)
            })
        )
    }

    private fun getResponseState(sources: List<Source>) = if (sources.isEmpty()) ERROR else SUCCESS

    private fun setState(state: NetworkState) {
        _networkState.postValue(state)
    }
}
