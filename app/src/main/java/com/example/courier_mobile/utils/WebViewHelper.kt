package com.example.courier_mobile.utils

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.util.Log

class WebViewHelper(
    private val context: Context,
    private val onPageLoaded: () -> Unit
) {
    companion object { private const val TAG = "WebViewHelper" }

    lateinit var webView: WebView
        private set

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    fun initWebView(): WebView {
        webView = WebView(context)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.allowFileAccess = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(TAG, "onPageFinished: $url")
                onPageLoaded()
            }
        }

//        webView.loadUrl("file:///android_asset/turf.html")
        return webView
    }
}

