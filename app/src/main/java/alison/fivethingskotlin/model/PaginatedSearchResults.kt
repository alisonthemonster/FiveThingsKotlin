package alison.fivethingskotlin.model

data class PaginatedSearchResults(val count: Int,
                                  val next: String? = null,
                                  val previous: String? = null,
                                  val results: List<SearchResult> )