package dev.dextra.newsapp.feature.news

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.feature.news.adapter.ArticleListAdapter
import kotlinx.android.synthetic.main.activity_news.news_list
import kotlinx.android.synthetic.main.error_state.error_state
import kotlinx.android.synthetic.main.error_state.error_state_retry
import kotlinx.android.synthetic.main.error_state.error_state_subtitle
import kotlinx.android.synthetic.main.error_state.error_state_title
import org.koin.android.ext.android.inject

const val NEWS_ACTIVITY_SOURCE = "NEWS_ACTIVITY_SOURCE"

/**
 * [AppCompatActivity] to be launched when a [Source] is selected on a list.
 * This class is responsible to present the [Article] content as a list, using the [NewsViewModel]
 * to have access to the model.
 */
class NewsActivity : AppCompatActivity(), ArticleListAdapter.ArticleListAdapterItemListener {

    private lateinit var newsViewModel: NewsViewModel

    private var viewAdapter: ArticleListAdapter = ArticleListAdapter(this)

    private val newsRepository: NewsRepository by inject()

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
            newsViewModel =
                ViewModelProviders.of(this, NewsViewModelFactory(newsRepository, it)).get(NewsViewModel::class.java)
        }
        setupData()
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

    private fun setupData() {
        newsViewModel.articles.observe(this, Observer {
            viewAdapter.apply {
                submitList(it)
            }
            news_list.scrollToPosition(0)
        })
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
        error_state_retry.setOnClickListener {
            // TODO: Implement retry
        }
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
        news_list.adapter = viewAdapter
    }
}
