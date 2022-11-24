package com.example.webviewtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    private val editText by lazy { findViewById<EditText>(R.id.input) }
    private val webViewContainer by lazy { findViewById<FrameLayout>(R.id.webviewContainer) }
    private val recreateWebviewButton by lazy { findViewById<Button>(R.id.recreateWebview)}
    private val progress by lazy { findViewById<TextView>(R.id.progress) }

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WebView.setWebContentsDebuggingEnabled(true)
        webView = createWebview()
        webViewContainer.addView(webView)
        initEditText()
        initRecreateWebviewButton()
    }

    private fun initRecreateWebviewButton() {
        recreateWebviewButton.setOnClickListener {
            val oldUrl = webView.url
            webViewContainer.removeAllViews()
            this@MainActivity.webView = createWebview()
            webViewContainer.addView(webView)
            webView.loadUrl(oldUrl ?: "about:blank")
        }
    }

    private fun initEditText() {
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
            progress.isVisible = true
            progress.text = newProgress.toString()
        } else {
            progress.isVisible = false
        }
    }

    private companion object {
        private const val MAX_PROGRESS = 100
    }
}
