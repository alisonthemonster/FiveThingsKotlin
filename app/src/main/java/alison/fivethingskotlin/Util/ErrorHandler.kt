package alison.fivethingskotlin.Util

import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Models.Status
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import org.json.JSONObject
import retrofit2.Response

fun <T> buildErrorResource(response: Response<T>): Resource<FiveThings>? {
    response.errorBody()?.string()?.let {
        return if (it.contains("\n")) {
            val json = JSONObject(it)
            val messageString = json.getString("detail")
            Resource(Status.ERROR, messageString, null)
        } else {
            Resource(Status.ERROR, it, null)
        }
    }
    return Resource(Status.ERROR, response.message(), null)
}

val closeButtonListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
    dialog.cancel()
}

fun showErrorDialog(message: String, context: Context,
                    buttonText: String = "Ok",
                    buttonAction: DialogInterface.OnClickListener = closeButtonListener) {

    val dialogBuilder = AlertDialog.Builder(context)
    dialogBuilder
            .setTitle("Oh no! Something went wrong!") //TODO move to resource
            .setNegativeButton(buttonText, buttonAction)
            .setMessage(message)
            .show()
}