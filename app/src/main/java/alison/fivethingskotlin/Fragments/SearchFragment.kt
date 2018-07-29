package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.API.repository.SearchRepositoryImpl
import alison.fivethingskotlin.Models.SearchResult
import alison.fivethingskotlin.Models.Status
import alison.fivethingskotlin.PromoActivity
import alison.fivethingskotlin.R
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.Util.restoreAuthState
import alison.fivethingskotlin.Util.showErrorDialog
import alison.fivethingskotlin.ViewModels.SearchViewModel
import alison.fivethingskotlin.ViewModels.SearchViewModelFactory
import alison.fivethingskotlin.adapter.SearchResultAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.search_fragment.*
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService


class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var authState: AuthState
    private lateinit var authorizationService: AuthorizationService
    private lateinit var adapter: SearchResultAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            authorizationService = AuthorizationService(it)
            authState = restoreAuthState(it)!!

            viewModel = ViewModelProviders.of(this, SearchViewModelFactory(SearchRepositoryImpl()))
                        .get(SearchViewModel::class.java)

            search_item.setOnEditorActionListener { textView, actionId, keyEvent ->
                Log.d("blerg", "they pressed $actionId")
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.d("blerg", "they pressed da button")
                    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(search_item.windowToken, 0)
                    getResultsWithFreshToken(textView.text.toString())
                } else {
                    Log.d("blerg", "idk what button they pressed")
                }
                false
            }
        }

        search_results.layoutManager = LinearLayoutManager(context)
        adapter = SearchResultAdapter(emptyList())
        search_results.adapter = adapter
    }

    private fun getResultsWithFreshToken(text: String) {
        authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                Log.e("blerg", "Negotiation for fresh tokens failed: $ex")
                showErrorDialog(ex.localizedMessage, context!!, "Log in again", openLogInScreen())
            } else {
                idToken?.let {

                    viewModel.getSearchResults("Bearer $it", text).observe(this, Observer<Resource<List<SearchResult>>> { results ->
                        results?.let {
                            Log.d("blerg", "we got da results")
                            when (it.status) {
                                Status.SUCCESS -> {
                                    no_results.visibility = if (results.data!!.isEmpty()) View.VISIBLE else View.GONE
                                    addResultsToAdapter(it.data!!)
                                }
                                Status.ERROR -> showErrorDialog(it.message!!.capitalize(), context!!)
                            }
                        }
                    })
                }
            }
        }
    }

    private fun addResultsToAdapter(data: List<SearchResult>) {
        Log.d("blerg", "data: $data")
        adapter.setResults(data)
    }

    private fun openLogInScreen(): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { _, _ ->
            val intent = Intent(context, PromoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
