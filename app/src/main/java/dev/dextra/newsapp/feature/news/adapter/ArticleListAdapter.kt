package dev.dextra.newsapp.feature.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Article.Companion.DIFF_CALLBACK
import kotlinx.android.synthetic.main.item_article.view.article_author
import kotlinx.android.synthetic.main.item_article.view.article_date
import kotlinx.android.synthetic.main.item_article.view.article_description
import kotlinx.android.synthetic.main.item_article.view.article_name
import java.text.DateFormat
import java.text.DateFormat.SHORT
import java.text.SimpleDateFormat
import java.text.SimpleDateFormat.getDateTimeInstance
import java.util.Locale

/**
 * [RecyclerView.Adapter] to apply the [Article] content on the list.
 */
class ArticleListAdapter(
    private val listener: ArticleListAdapterItemListener
) : PagedListAdapter<Article, ArticleListAdapter.ArticleListAdapterViewHolder>(DIFF_CALLBACK) {

    private val dateFormat = getDateTimeInstance(DateFormat.DEFAULT, SHORT)
    private val parseFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleListAdapterViewHolder, position: Int) {
        getItem(position)?.let { article ->
            holder.view.apply {
                setOnClickListener { listener.onClick(article) }
                article_name.text = article.title
                article_description.text = article.description
                article_author.text = article.author
                article_date.text = dateFormat.format(parseFormat.parse(article.publishedAt))
            }
        }
    }

    /**
     * [RecyclerView.ViewHolder] to contain the adapter view.
     */
    class ArticleListAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    /**
     * Item selection interface.
     */
    interface ArticleListAdapterItemListener {
        /**
         * How implements this method will know when an [Article] was selected, and which one,
         */
        fun onClick(article: Article)
    }
}
