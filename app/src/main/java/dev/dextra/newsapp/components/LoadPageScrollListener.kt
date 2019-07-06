package dev.dextra.newsapp.components

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LoadPageScrollListener(private val loadPageScrollLoadMoreListener: LoadPageScrollLoadMoreListener) :
    RecyclerView.OnScrollListener() {

    private var currentPage = 1
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            val totalItemCount = layoutManager.getItemCount()

            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex
                this.previousTotalItemCount = totalItemCount
                if (totalItemCount == 0) {
                    this.loading = true
                }
            }

            if (loading && totalItemCount > previousTotalItemCount) {
                loading = false
                previousTotalItemCount = totalItemCount
            }

            if (!loading && lastVisibleItemPosition + VISIBLE_THRESHOLD > totalItemCount) {
                currentPage++
                loadPageScrollLoadMoreListener.onLoadMore(currentPage, totalItemCount, recyclerView)
                loading = true
            }
        }
    }

    interface LoadPageScrollLoadMoreListener {

        fun onLoadMore(
            currentPage: Int,
            totalItemCount: Int,
            recyclerView: RecyclerView
        )
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }
}
