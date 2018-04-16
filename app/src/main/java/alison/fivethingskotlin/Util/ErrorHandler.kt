package alison.fivethingskotlin.Util

import alison.fivethingskotlin.Models.Status
import org.json.JSONObject
import retrofit2.Response

fun <T> buildErrorResource(response: Response<T>): Resource<T> {
    response.errorBody()?.string()?.let {
        return if (it.contains("\n")) {
            val json = JSONObject(it)
            val messageString = json.getString("message")
            Resource(Status.ERROR, messageString, null)
        } else {
            Resource(Status.ERROR, it, null)
        }
    }
    return Resource(Status.ERROR, response.message(), null)
}
