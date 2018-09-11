package alison.fivethingskotlin.model

data class Resource<out T>(
        val status: Status,
        val message: String?,
        val data: T?
)