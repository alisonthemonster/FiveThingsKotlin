package alison.fivethingskotlin.adapter

import alison.fivethingskotlin.ContainerActivity
import alison.fivethingskotlin.model.NetworkState
import alison.fivethingskotlin.model.SearchResult
import alison.fivethingskotlin.R
import alison.fivethingskotlin.util.getDateFromFullDateFormat
import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_search_result.view.*
import java.util.*

class PagedSearchResultAdapter(private val retryCallback: () -> Unit) : PagedListAdapter<SearchResult, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<SearchResult>() {
            override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean =
                    oldItem.id == newItem.id

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_search_result -> PagedResultViewHolder.create(parent)
            R.layout.item_last -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            (holder as PagedResultViewHolder).bindViews(item!!)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_search_result -> (holder as PagedResultViewHolder).bindViews(getItem(position)!!)
            R.layout.item_last -> (holder as NetworkStateItemViewHolder).bindTo(networkState)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_last
        } else {
            R.layout.item_search_result
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}

class PagedResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindViews(searchResult: SearchResult) {
        itemView.content.text = searchResult.content
        itemView.date.text = searchResult.date
        itemView.setOnClickListener {
            val activity = it.context as ContainerActivity
            activity.onDateSelected(Date(), getDateFromFullDateFormat(searchResult.date))
        }
    }

    companion object {
        fun create(parent: ViewGroup): PagedResultViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_search_result, parent, false)
            return PagedResultViewHolder(view)
        }
    }
}