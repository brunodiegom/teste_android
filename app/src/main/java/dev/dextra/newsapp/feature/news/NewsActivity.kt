package dev.dextra.newsapp.feature.news

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.feature.news.adapter.ArticleListAdapter
import kotlinx.android.synthetic.main.activity_news.news_list
import kotlinx.android.synthetic.main.error_state.error_state
import kotlinx.android.synthetic.main.error_state.error_state_retry
import kotlinx.android.synthetic.main.error_state.error_state_subtitle
import kotlinx.android.synthetic.main.error_state.error_state_title
import org.koin.android.viewmodel.ext.android.viewModel

const val NEWS_ACTIVITY_SOURCE = "NEWS_ACTIVITY_SOURCE"

/**
 * [AppCompatActivity] to be launched when a [Source] is selected on a list.
 * This class is responsible to present the [Article] content as a list, using the [NewsViewModel]
 * to have access to the model.
 */
class NewsActivity : AppCompatActivity(), ArticleListAdapter.ArticleListAdapterItemListener {

    private val newsViewModel: NewsViewModel by viewModel()

    private var viewAdapter: ArticleListAdapter = ArticleListAdapter(this)

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
        setContentView(R.layout.activity_news)

        (intent?.extras?.getSerializable(NEWS_ACTIVITY_SOURCE) as Source).let {
            title = it.name
            setupData(it)
        }
        setupView()
    }

    override fun onClick(article: Article) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(article.url)
        startActivity(intent)
    }

    private fun setupView() {
        setupLoading()
        setupList()
        setupErrorMessage()
    }

    private fun setupData(source: Source) {
        newsViewModel.also {
            it.articles.observe(this, Observer {
                viewAdapter.apply {
                    clear()
                    add(it)
                    news_list.scrollToPosition(0)
                }
            })
            it.configureSource(source)
            it.loadNews()
        }
    }

    private fun setupErrorMessage() {
        setupErrorVisibility()
        setupErrorTitle()
        setupErrorSubtitle()
        setupRetryButton()
    }

    private fun setupErrorVisibility() {
        newsViewModel.hasMessage.observe(this, Observer<Boolean> {
            error_state.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun setupErrorTitle() {
        newsViewModel.errorMessageTitle.observe(this, Observer { title ->
            title?.let { error_state_title.setText(it) }
        })
    }

    private fun setupErrorSubtitle() {
        newsViewModel.errorMessageSubtitle.observe(this, Observer { subtitle ->
            subtitle?.let { error_state_subtitle.setText(it) }
        })
    }

    private fun setupRetryButton() {
        newsViewModel.isRetryAllowed.observe(this, Observer<Boolean> {
            error_state_retry.visibility = if (it) View.VISIBLE else View.GONE
        })
        error_state_retry.setOnClickListener { executeRetry() }
    }

    private fun setupLoading() {
        newsViewModel.isLoading.observe(this, Observer<Boolean> { isLoading ->
            loadingDialog.run { if (isLoading) show() else dismiss() }
        })
    }

    private fun setupList() {
        newsViewModel.shouldShowList.observe(this, Observer<Boolean> {
            news_list.visibility = if (it) View.VISIBLE else View.GONE
        })
        news_list.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
        }
    }

    private fun executeRetry() {
        newsViewModel.loadNews()
    }
}
