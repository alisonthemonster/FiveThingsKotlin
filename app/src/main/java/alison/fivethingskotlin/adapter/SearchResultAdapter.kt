package alison.fivethingskotlin.adapter

import alison.fivethingskotlin.ContainerActivity
import alison.fivethingskotlin.Fragments.FiveThingsFragment
import alison.fivethingskotlin.Models.SearchResult
import alison.fivethingskotlin.R
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.search_result.view.*
import java.util.*

class SearchResultAdapter(var searchResults: List<SearchResult>):  RecyclerView.Adapter<ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)
        return ResultViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bindViews(searchResults[position])
    }

    fun setResults(newResults: List<SearchResult>) {
        searchResults = newResults
        notifyDataSetChanged()
    }
}

class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindViews(searchResult: SearchResult) {
        itemView.content.text = searchResult.content
        itemView.date.text = searchResult.date
        itemView.setOnClickListener {
            val activity = it.context as ContainerActivity
            activity.onDateSelected(searchResult.date) }
    }
}