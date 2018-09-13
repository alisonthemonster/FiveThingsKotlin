package alison.fivethingskotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    companion object {
        const val WEBVIEW_URL = "WEBVIEW_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webviewUrl = intent.getStringExtra(WEBVIEW_URL)
        webview.loadUrl(webviewUrl)

    }
}
