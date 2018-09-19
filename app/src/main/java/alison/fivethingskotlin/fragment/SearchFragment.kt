package alison.fivethingskotlin.fragment

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.api.repository.SearchRepositoryImpl
import alison.fivethingskotlin.model.SearchResult
import alison.fivethingskotlin.R
import alison.fivethingskotlin.util.restoreAuthState
import alison.fivethingskotlin.util.showErrorDialog
import alison.fivethingskotlin.viewmodel.SearchViewModel
import alison.fivethingskotlin.viewmodel.SearchViewModelFactory
import alison.fivethingskotlin.adapter.PagedSearchResultAdapter
import alison.fivethingskotlin.util.openLogInScreen
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
import kotlinx.android.synthetic.main.fragment_search.*
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import java.util.*
import java.util.concurrent.Executors


class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var authState: AuthState
    private lateinit var authorizationService: AuthorizationService
    private lateinit var adapter: PagedSearchResultAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            authorizationService = AuthorizationService(it)
            authState = restoreAuthState(it)!!

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
                    if (textView.text.isNotBlank())
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

    private fun getPaginatedResultsWithFreshToken(text: String) {
        authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                showErrorDialog("Unable to log in: ${ex.errorDescription}", context!!, "Log in again", openLogInScreen(context!!))
            } else {
                idToken?.let {
                    adapter.submitList(null)
                    viewModel.getPaginatedSearchResults("Bearer $it", text, 50, 1)
                }
            }
        }
    }



    // Container Activity must implement this interface
    interface OnDateSelectedListener {
        fun selectDate(selectedDate: Date, isASearchResult: Boolean)
    }
}
