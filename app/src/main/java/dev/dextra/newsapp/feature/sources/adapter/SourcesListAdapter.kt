package dev.dextra.newsapp.feature.sources.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.dextra.newsapp.ICON_LOCATOR_URL
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Source
import kotlinx.android.synthetic.main.item_source.view.source_description
import kotlinx.android.synthetic.main.item_source.view.source_image
import kotlinx.android.synthetic.main.item_source.view.source_link
import kotlinx.android.synthetic.main.item_source.view.source_name

/**
 * [RecyclerView.Adapter] to apply the [Source] content on the list.
 */
class SourcesListAdapter(private val listener: SourceListAdapterItemListener) :
    RecyclerView.Adapter<SourcesListAdapter.SourcesListAdapterViewHolder>() {

    private val dataSet: ArrayList<Source> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourcesListAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_source, parent, false)
        return SourcesListAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: SourcesListAdapterViewHolder, position: Int) {
        val source = dataSet[position]
        holder.view.apply {
            setOnClickListener { listener.onClick(source) }
            source_name.text = source.name
            source_description.text = source.description
            source_link.text = source.url
            Picasso.get()
                .load(ICON_LOCATOR_URL.format(source.url))
                .fit()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(source_image)
        }
    }

    /**
     * Add the [Source] on adapter.
     */
    fun add(sources: List<Source>) {
        dataSet.addAll(sources)
        notifyDataSetChanged()
    }

    /**
     * Remove all [Source] from adapter.
     */
    fun clear() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    /**
     * [RecyclerView.ViewHolder] to contain the adapter view.
     */
    class SourcesListAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    /**
     * Item selection interface.
     */
    interface SourceListAdapterItemListener {
        /**
         * How implements this method will know when an [Source] was selected, and which one,
         */
        fun onClick(source: Source)
    }
}
