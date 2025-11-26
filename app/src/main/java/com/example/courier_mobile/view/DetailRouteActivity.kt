package com.example.courier_mobile.view

import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.courier_mobile.utils.JsBridge
import com.example.courier_mobile.utils.Route
import com.example.courier_mobile.utils.WebViewHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailRouteActivity : AppCompatActivity() {

    private lateinit var webHelper: WebViewHelper
    private lateinit var jsBridge: JsBridge

    private var pageLoaded = false
    private val TAG = "DETAIL_ROUTE"

    private val startLat = -6.953306544784432
    private val startLng = 107.58220827953811

    private val destLat = -6.927428122342222
    private val destLng = 107.55418914267061

    private val courierLat = -6.959637309382047
    private val courierLng = 107.58054819510916

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webHelper = WebViewHelper(this) { onPageLoaded() }

        val web = webHelper.initWebView()

        // ONLY ONCE
        jsBridge = JsBridge(web, courierLng, courierLat)
        web.addJavascriptInterface(jsBridge, "Android")

        // Load HTML FIRST
        web.loadUrl("file:///android_asset/turf.html")

        setContentView(web)

        webHelper.webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.d("WEBVIEW_JS", "JS Console: ${consoleMessage?.message()} (line ${consoleMessage?.lineNumber()})")
                return true
            }
        }
    }

    private fun onPageLoaded() {
        pageLoaded = true
        Log.d(TAG, "Page fully loaded.")

        // Now safe to call JS
        val js = "setCourierPosition($courierLng, $courierLat);"
        webHelper.webView.evaluateJavascript(js, null)

        // Load OSRM route
        lifecycleScope.launch {
            val route = Route.fetchOsrmRouteGeoJson(
                startLng, startLat,
                destLng, destLat
            )

            if (route != null) {
                withContext(Dispatchers.Main) {
                    sendRouteToJs(route)
                }
            }
        }
    }

    private fun sendRouteToJs(lineJson: String) {
        if (!pageLoaded) return

        val js = """
            console.log("Android -> makeBuffer called");
            window.__line = $lineJson;
            makeBuffer(window.__line, 0.1);
        """

        webHelper.webView.evaluateJavascript(js, null)
    }
}
