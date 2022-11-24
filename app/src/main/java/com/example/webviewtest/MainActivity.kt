package com.example.webviewtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val editText by lazy { findViewById<EditText>(R.id.input) }
    private val webViewContainer by lazy { findViewById<FrameLayout>(R.id.webviewContainer) }
    private lateinit var webView: WebView

    private val progress by lazy { findViewById<TextView>(R.id.progress) }

    private var fixWebviewJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WebView.setWebContentsDebuggingEnabled(true)
        webView = createWebview()
        webViewContainer.addView(webView)
        editText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_GO) {
                webView.loadUrl(editText.text.toString())
                true
            } else {
                false
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebview(): WebView {
        val webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                onProgressChange(newProgress)
            }

        }
        return webView
    }

    private fun onProgressChange(newProgress: Int) {
        if (newProgress < MAX_PROGRESS) {
            scheduleFixJob()
            updateProgressView(newProgress)
        } else {
            progress.isVisible = false
            cancelFixJob()
        }
    }

    private fun updateProgressView(newProgress: Int) {
        progress.isVisible = true
        progress.text = newProgress.toString()
    }

    private fun scheduleFixJob() {
        fixWebviewJob?.cancel()
        fixWebviewJob = GlobalScope.launch {
            delay(MAX_TIME_BETWEEN_PROGRESS_UPDATES_MS)
            fixWebviewJob = null
            runOnUiThread {
                webViewContainer.removeAllViews()
                this@MainActivity.webView = createWebview()
                webViewContainer.addView(webView)
            }
        }
    }

    private fun cancelFixJob() {
        fixWebviewJob?.cancel()
        fixWebviewJob = null
    }

    private companion object {
        private const val MAX_PROGRESS = 100
        private const val MAX_TIME_BETWEEN_PROGRESS_UPDATES_MS = 1000L
    }
}
