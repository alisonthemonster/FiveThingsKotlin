package alison.fivethingskotlin.search

import alison.fivethingskotlin.model.NetworkState
import alison.fivethingskotlin.model.NetworkStatus
import alison.fivethingskotlin.R
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView


/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
class NetworkStateItemViewHolder(view: View,
                                 private val retryCallback: () -> Unit)
    : RecyclerView.ViewHolder(view) {
    private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
    private val retry = view.findViewById<Button>(R.id.retry_button)
    private val errorMsg = view.findViewById<TextView>(R.id.error_msg)
    private val noResultsIcon = view.findViewById<ImageView>(R.id.no_results)

    init {
        retry.setOnClickListener {
            retryCallback()
        }
    }
    fun bindTo(networkState: NetworkState?) {
        progressBar.visibility = toVisibility(networkState?.status == NetworkStatus.RUNNING)
        retry.visibility = toVisibility(networkState?.status == NetworkStatus.FAILED)
        errorMsg.visibility = toVisibility(networkState?.msg != null)
        if (networkState?.msg == "No results found") {
            retry.visibility = View.GONE
            noResultsIcon.visibility = View.VISIBLE
        }  else noResultsIcon.visibility = View.GONE
        errorMsg.text = networkState?.msg

    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_last, parent, false)
            return NetworkStateItemViewHolder(view, retryCallback)
        }

        fun toVisibility(constraint : Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}