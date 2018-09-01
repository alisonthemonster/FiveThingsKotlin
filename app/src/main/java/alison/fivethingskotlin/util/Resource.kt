package alison.fivethingskotlin.util

import alison.fivethingskotlin.model.Status

data class Resource<out T>(
        val status: Status,
        val message: String?,
        val data: T?
)