package com.example.courier_mobile.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewHelper(
    private val context: Context,
) {

    companion object { private const val TAG = "WebViewHelper" }

    lateinit var webView: WebView
        private set

    lateinit var bridge: JsBridge
        private set

    private var loadedOnce = false

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView(): WebView {
        webView = WebView(context).apply {
            setBackgroundColor(Color.TRANSPARENT)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("WEBVIEW_JS", "JS Console: ${consoleMessage.message()} (line ${consoleMessage.lineNumber()})")
                return true
            }
        }

        bridge = JsBridge(webView, "Tegar")
//        bridge.isJsReady = {
//            Log.d(TAG, "JS READY callback fired")
//        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                Log.d(TAG, "REQUEST: ${request?.url}")
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(TAG, "HTML LOADED (onPageFinished) url=$url")

                if (!loadedOnce) {
                    loadedOnce = true
                    Log.d(TAG, "JS Bridge ready from WebViewHelper")
                    bridge.isJsReady?.invoke()
                }
            }

        }

        webView.addJavascriptInterface(bridge, "Android")
        webView.loadUrl("file:///android_asset/geofencing.html")
        return webView
    }
}
