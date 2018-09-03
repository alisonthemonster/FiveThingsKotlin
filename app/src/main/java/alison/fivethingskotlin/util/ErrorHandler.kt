package alison.fivethingskotlin.util

import alison.fivethingskotlin.PromoActivity
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Status
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
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

fun showErrorDialog(message: String,
                    context: Context,
                    buttonText: String = "Ok",
                    buttonAction: DialogInterface.OnClickListener = closeButtonListener) {

    val dialogBuilder = AlertDialog.Builder(context)

    //TODO comment back in when not on a plane lol
//    dialogBuilder.apply {
//        setTitle("Oh no! Something went wrong!") //TODO move to resource
//        if (message == "Unable to get fresh tokens") {
//            setNegativeButton("Log in again", openLogInScreen(context))
//        } else {
//            setNegativeButton(buttonText, buttonAction)
//        }
//        setMessage(message)
//        show()
//    }

    dialogBuilder
            .setTitle("Oh no! Something went wrong!") //TODO move to resource
            .setNegativeButton(buttonText, buttonAction)
            .setMessage(message)
            .show()
}

fun openLogInScreen(context: Context): DialogInterface.OnClickListener {
    return DialogInterface.OnClickListener { _, _ ->
        val intent = Intent(context, PromoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}