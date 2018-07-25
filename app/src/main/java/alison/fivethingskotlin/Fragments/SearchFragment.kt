package alison.fivethingskotlin.Fragments

import alison.fivethingskotlin.API.repository.SearchRepositoryImpl
import alison.fivethingskotlin.R
import alison.fivethingskotlin.Util.restoreAuthState
import alison.fivethingskotlin.ViewModels.SearchViewModel
import alison.fivethingskotlin.ViewModels.SearchViewModelFactory
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.search_fragment.*
import net.openid.appauth.AuthorizationService

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO onViewCreated right place?
        context?.let {
            val authorizationService = AuthorizationService(it)
            val authState = restoreAuthState(it)

            authState?.let {
                viewModel = ViewModelProviders.of(this, SearchViewModelFactory(authState,
                                                                                authorizationService,
                                                                                SearchRepositoryImpl()))
                            .get(SearchViewModel::class.java)

                search_item.setOnEditorActionListener { textView, actionId, keyEvent ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        viewModel.getSearchResults(textView.text.toString())
                    }
                    false
                }
            }

        }
    }

}
