package alison.fivethingskotlin.Models

enum class NetworkStatus {
    RUNNING,
    SUCCESS,
    FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: NetworkStatus,
        val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(NetworkStatus.SUCCESS)
        val LOADING = NetworkState(NetworkStatus.RUNNING)
        fun error(msg: String?) = NetworkState(NetworkStatus.FAILED, msg)
    }
}