package dev.dextra.newsapp.feature.sources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.model.enums.Category
import dev.dextra.newsapp.api.model.enums.Country
import dev.dextra.newsapp.api.model.extension.getSourceValue
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState
import dev.dextra.newsapp.base.NetworkState.EMPTY
import dev.dextra.newsapp.base.NetworkState.ERROR
import dev.dextra.newsapp.base.NetworkState.RUNNING
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
     * Indicates when the server is being accessed.
     */
    var isLoading: LiveData<Boolean> = Transformations.map(networkState, this::isLoading)

    /**
     * Indicates when the [Source] list must be shown.
     */
    var shouldShowList: LiveData<Boolean> = Transformations.map(sources, this::shouldShowList)

    /**
     * Indicates when the retry button must be shown.
     */
    var isRetryAllowed: LiveData<Boolean> = Transformations.map(networkState, this::hasError)

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

    /**
     * Request to server to load the [Source] data, updating the [NetworkState].
     */
    fun loadSources() {
        setState(RUNNING)
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

    private fun isLoading(networkState: NetworkState) = networkState == RUNNING

    private fun hasError(networkState: NetworkState) = networkState == ERROR

    private fun hasEmptyList(networkState: NetworkState) = networkState == EMPTY

    private fun hasMessage(networkState: NetworkState) = hasError(networkState) || hasEmptyList(networkState)

    private fun shouldShowList(sourceList: List<Source>) = sourceList.isNotEmpty()

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

    private fun getResponseState(sources: List<Source>) = if (sources.isEmpty()) EMPTY else SUCCESS

    private fun setState(state: NetworkState) {
        _networkState.postValue(state)
    }
}
