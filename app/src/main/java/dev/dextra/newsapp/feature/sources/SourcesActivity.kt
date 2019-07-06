package dev.dextra.newsapp.feature.sources

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.model.enums.Category
import dev.dextra.newsapp.api.model.enums.Country
import dev.dextra.newsapp.base.BaseListActivity
import dev.dextra.newsapp.feature.news.NEWS_ACTIVITY_SOURCE
import dev.dextra.newsapp.feature.news.NewsActivity
import dev.dextra.newsapp.feature.sources.adapter.CustomArrayAdapter
import dev.dextra.newsapp.feature.sources.adapter.SourcesListAdapter
import kotlinx.android.synthetic.main.activity_sources.app_bar
import kotlinx.android.synthetic.main.activity_sources.category_select
import kotlinx.android.synthetic.main.activity_sources.country_select
import kotlinx.android.synthetic.main.activity_sources.sources_list
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * [BaseListActivity] to be launched when the application starts.
 * This class is responsible to present the [Source] content as a list, using the [SourcesViewModel]
 * to have access to the model.
 */
class SourcesActivity : BaseListActivity(), SourcesListAdapter.SourceListAdapterItemListener {

    private val sourcesViewModel: SourcesViewModel by viewModel()

    override val emptyStateTitle: Int = R.string.empty_state_title_source
    override val emptyStateSubTitle: Int = R.string.empty_state_subtitle_source
    override val errorStateTitle: Int = R.string.error_state_title_source
    override val errorStateSubTitle: Int = R.string.error_state_subtitle_source
    override val mainList: View
        get() = sources_list

    private var viewAdapter: SourcesListAdapter = SourcesListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_sources)
        super.onCreate(savedInstanceState)
        setupView()
        setupData()
    }

    override fun onClick(source: Source) {
        val intent = Intent(this, NewsActivity::class.java)
        intent.putExtra(NEWS_ACTIVITY_SOURCE, source)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    override fun executeRetry() {
        setupData()
    }

    private fun setupData() {
        observeSource()
        observeNetworkState()
        sourcesViewModel.loadSources()
    }

    private fun observeSource() {
        sourcesViewModel.sources.observe(this, Observer {
            viewAdapter.apply {
                clear()
                notifyDataSetChanged()
                add(it)
                notifyDataSetChanged()
                sources_list.scrollToPosition(0)
                app_bar.setExpanded(true)
            }
        })
    }

    private fun observeNetworkState() {
        sourcesViewModel.networkState.observe(this, networkStateObserver)
    }

    private fun setupView() {
        setupCountrySelector()
        setupCategorySelector()
        setupList()
    }

    private fun setupCategorySelector() {
        category_select.also {
            it.setAdapter(CustomArrayAdapter(this, R.layout.select_item, Category.values().toMutableList()))
            it.keyListener = null
            it.setOnItemClickListener { parent, _, position, _ ->
                sourcesViewModel.selectedCategory = parent.getItemAtPosition(position) as? Category ?: Category.ALL
            }
        }
    }

    private fun setupCountrySelector() {
        country_select.also {
            it.setAdapter(CustomArrayAdapter(this, R.layout.select_item, Country.values().toMutableList()))
            it.keyListener = null
            it.setOnItemClickListener { parent, _, position, _ ->
                sourcesViewModel.selectedCountry = parent.getItemAtPosition(position) as? Country ?: Country.ALL
            }
        }
    }

    private fun setupList() {
        sources_list.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
        }
    }
}
