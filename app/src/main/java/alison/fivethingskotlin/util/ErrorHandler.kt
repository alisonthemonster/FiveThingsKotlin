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

//TODO make dialog not dismissable by clicking outside it

fun showErrorDialog(message: String,
                    context: Context,
                    buttonText: String = "Ok",
                    buttonAction: DialogInterface.OnClickListener = closeButtonListener) {

    val dialogBuilder = AlertDialog.Builder(context)

    dialogBuilder.apply {
        setTitle("Oh no! Something went wrong!") //TODO move to resource
        if (message.contains("Log in failed")) {
            setNegativeButton("Log in again", openLogInScreen(context))
        } else {
            setNegativeButton(buttonText, buttonAction)
        }
        setMessage(message)
        setCancelable(false)
        show()
    }
}

fun openLogInScreen(context: Context): DialogInterface.OnClickListener {
    return DialogInterface.OnClickListener { _, _ ->
        clearAuthState(context) //log user out

        val intent = Intent(context, PromoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}