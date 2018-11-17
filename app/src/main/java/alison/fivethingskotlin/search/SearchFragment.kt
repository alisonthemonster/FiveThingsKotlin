package alison.fivethingskotlin.search

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.model.SearchResult
import alison.fivethingskotlin.R
import alison.fivethingskotlin.util.restoreAuthState
import alison.fivethingskotlin.util.handleErrorState
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.vdurmont.emoji.EmojiParser
import kotlinx.android.synthetic.main.fragment_search.*
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import java.util.*
import java.util.concurrent.Executors


class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private var authState: AuthState? = null
    private lateinit var authorizationService: AuthorizationService
    private lateinit var adapter: PagedSearchResultAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

        context?.let {
            authorizationService = AuthorizationService(it)
            authState = restoreAuthState(it)

            val executor = Executors.newFixedThreadPool(5)

            viewModel = ViewModelProviders.of(this, SearchViewModelFactory(SearchRepositoryImpl(FiveThingsService.create(), executor)))
                    .get(SearchViewModel::class.java)

            viewModel.searchResults.observe(this, Observer<PagedList<SearchResult>> {
                adapter.submitList(it)
            })
            viewModel.networkState.observe(this, Observer {
                adapter.setNetworkState(it)
            })

            search_item.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(search_item.windowToken, 0)
                    getPaginatedResultsWithFreshToken(textView.text.toString())
                }
                false
            }

            adapter = PagedSearchResultAdapter {
                viewModel.retry()
            }
        }

        search_results.layoutManager = LinearLayoutManager(context)
        search_results.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalytics.setCurrentScreen(activity as Activity, "SearchScreen", null)
    }

    private fun getPaginatedResultsWithFreshToken(textView: String) {
        if (authState == null) {
            handleErrorState("Log in failed", context!!)
        }


        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                handleErrorState("Log in failed: ${ex.errorDescription}", context!!)
            } else {
                idToken?.let {
                    adapter.submitList(null)
                    val query = EmojiParser.removeAllEmojis(textView)
                    if (query.length < textView.length)
                        Toast.makeText(activity, "Emojis are not searchable", Toast.LENGTH_SHORT).show()
                    if (query.isNotEmpty())
                        viewModel.getPaginatedSearchResults("Bearer $it", query, 50, 1)
                }
            }
        }
    }


    // Container Activity must implement this interface
    interface OnDateSelectedListener {
        fun selectDate(selectedDate: Date, isASearchResult: Boolean)
    }
}

