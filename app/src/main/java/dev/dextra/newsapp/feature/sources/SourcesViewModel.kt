package dev.dextra.newsapp.feature.sources

import androidx.lifecycle.MutableLiveData
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.model.enums.Category
import dev.dextra.newsapp.api.model.enums.Country
import dev.dextra.newsapp.api.model.extension.getValue
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState
import kotlin.properties.Delegates.observable

class SourcesViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    val sources = MutableLiveData<List<Source>>()
    val networkState = MutableLiveData<NetworkState>()

    var selectedCountry: Country by observable(Country.ALL, { _, oldValue, newValue ->
        if (oldValue != newValue) loadSources()
    })

    var selectedCategory: Category by observable(Category.ALL, { _, oldValue, newValue ->
        if (oldValue != newValue) loadSources()
    })

    fun loadSources() {
        networkState.postValue(NetworkState.RUNNING)
        addDisposable(
            newsRepository.getSources(
                selectedCountry.getValue(),
                selectedCategory.getValue()
            ).subscribe({
                sources.postValue(it.sources)
                if (it.sources.isEmpty()) {
                    networkState.postValue(NetworkState.ERROR)
                } else {
                    networkState.postValue(NetworkState.SUCCESS)
                }
            }, {
                networkState.postValue(NetworkState.ERROR)
            })
        )
    }
}
