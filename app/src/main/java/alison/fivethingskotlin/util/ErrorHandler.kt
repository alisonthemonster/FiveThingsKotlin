package alison.fivethingskotlin.util

import alison.fivethingskotlin.PromoActivity
import alison.fivethingskotlin.R
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.Status
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat
import org.json.JSONObject
import retrofit2.Response
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.SpannableString



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

    dialogBuilder.apply {
        val title = SpannableString("Oh no! Something went wrong!")  //TODO move to resource
        title.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        setTitle(title)
        if (message.contains("Log in failed") || message.contains("Unable to resolve host")) {
            setNegativeButton("Log in again", openLogInScreen(context))
            setCancelable(false)
        } else {
            setNegativeButton(buttonText, buttonAction)
        }
        setMessage(message)
    }
    val alert = dialogBuilder.create()
    alert.show()
    val button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
    button.setTextColor(ContextCompat.getColor(context, R.color.bluegreen))
}

fun openLogInScreen(context: Context): DialogInterface.OnClickListener {
    return DialogInterface.OnClickListener { _, _ ->
        clearAuthState(context) //log user out

        val intent = Intent(context, PromoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}