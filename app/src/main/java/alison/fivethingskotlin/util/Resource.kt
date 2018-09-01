package alison.fivethingskotlin.util

import alison.fivethingskotlin.Models.Status

data class Resource<out T>(
        val status: Status,
        val message: String?,
        val data: T?
)