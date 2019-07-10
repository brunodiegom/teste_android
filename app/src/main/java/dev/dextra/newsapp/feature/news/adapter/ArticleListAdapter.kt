package dev.dextra.newsapp.feature.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import kotlinx.android.synthetic.main.item_article.view.article_author
import kotlinx.android.synthetic.main.item_article.view.article_date
import kotlinx.android.synthetic.main.item_article.view.article_description
import kotlinx.android.synthetic.main.item_article.view.article_name
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.ArrayList

/**
 * [RecyclerView.Adapter] to apply the [Article] content on the list.
 */
class ArticleListAdapter(
    private val listener: ArticleListAdapterItemListener
) : RecyclerView.Adapter<ArticleListAdapter.ArticleListAdapterViewHolder>() {

    private val dataSet: ArrayList<Article> = ArrayList()
    private val dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
    private val parseFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleListAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ArticleListAdapterViewHolder, position: Int) {
        val article = dataSet[position]
        holder.view.apply {
            setOnClickListener { listener.onClick(article) }
            article_name.text = article.title
            article_description.text = article.description
            article_author.text = article.author
            article_date.text = dateFormat.format(parseFormat.parse(article.publishedAt))
        }
    }

    /**
     * Add the [Article] on adapter.
     */
    fun add(list: List<Article>) {
        dataSet.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Remove all [Article] from adapter.
     */
    fun clear() {
        dataSet.clear()
        notifyDataSetChanged()
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
