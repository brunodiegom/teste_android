package dev.dextra.newsapp.feature.sources

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.model.enums.Category
import dev.dextra.newsapp.api.model.enums.Country
import dev.dextra.newsapp.feature.news.NEWS_ACTIVITY_SOURCE
import dev.dextra.newsapp.feature.news.NewsActivity
import dev.dextra.newsapp.feature.sources.adapter.CustomArrayAdapter
import dev.dextra.newsapp.feature.sources.adapter.SourcesListAdapter
import kotlinx.android.synthetic.main.activity_sources.app_bar
import kotlinx.android.synthetic.main.activity_sources.category_select
import kotlinx.android.synthetic.main.activity_sources.country_select
import kotlinx.android.synthetic.main.activity_sources.sources_list
import kotlinx.android.synthetic.main.error_state.error_state
import kotlinx.android.synthetic.main.error_state.error_state_retry
import kotlinx.android.synthetic.main.error_state.error_state_subtitle
import kotlinx.android.synthetic.main.error_state.error_state_title
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * [AppCompatActivity] to be launched when the application starts.
 * This class is responsible to present the [Source] content as a list, using the [SourcesViewModel]
 * to have access to the model.
 */
class SourcesActivity : AppCompatActivity(), SourcesListAdapter.SourceListAdapterItemListener {

    private val sourcesViewModel: SourcesViewModel by viewModel()

    private var viewAdapter: SourcesListAdapter = SourcesListAdapter(this)

    private val loadingDialog by lazy {
        Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setContentView(R.layout.dialog_loading)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)
        setupView()
        setupData()
    }

    override fun onClick(source: Source) {
        val intent = Intent(this, NewsActivity::class.java)
        intent.putExtra(NEWS_ACTIVITY_SOURCE, source)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun executeRetry() {
        sourcesViewModel.loadSources()
    }

    private fun setupLoading() {
        sourcesViewModel.isLoading.observe(this, Observer<Boolean> { isLoading ->
            loadingDialog.run { if (isLoading) show() else dismiss() }
        })
    }

    private fun setupData() {
        sourcesViewModel.also {
            it.sources.observe(this, Observer {
                viewAdapter.apply {
                    clear()
                    notifyDataSetChanged()
                    add(it)
                    notifyDataSetChanged()
                    sources_list.scrollToPosition(0)
                    app_bar.setExpanded(true)
                }
            })
            it.loadSources()
        }
    }

    private fun setupView() {
        setupCountrySelector()
        setupCategorySelector()
        setupLoading()
        setupList()
        setupErrorMessage()
    }

    private fun setupErrorMessage() {
        setupErrorVisibility()
        setupErrorTitle()
        setupErrorSubtitle()
        setupRetryButton()
    }

    private fun setupErrorVisibility() {
        sourcesViewModel.hasMessage.observe(this, Observer<Boolean> {
            error_state.visibility = if (it) VISIBLE else GONE
        })
    }

    private fun setupErrorTitle() {
        sourcesViewModel.errorMessageTitle.observe(this, Observer { title ->
            title?.let { error_state_title.setText(it) }
        })
    }

    private fun setupErrorSubtitle() {
        sourcesViewModel.errorMessageSubtitle.observe(this, Observer { subtitle ->
            subtitle?.let { error_state_subtitle.setText(it) }
        })
    }

    private fun setupRetryButton() {
        sourcesViewModel.isRetryAllowed.observe(this, Observer<Boolean> {
            error_state_retry.visibility = if (it) VISIBLE else GONE
        })
        error_state_retry.setOnClickListener { executeRetry() }
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
        sourcesViewModel.shouldShowList.observe(this, Observer<Boolean> {
            sources_list.visibility = if (it) VISIBLE else GONE
        })
        sources_list.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
        }
    }
}
