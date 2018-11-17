package alison.fivethingskotlin.search


import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class SearchViewModelFactory(private val repository: SearchRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(repository) as T
    }
}