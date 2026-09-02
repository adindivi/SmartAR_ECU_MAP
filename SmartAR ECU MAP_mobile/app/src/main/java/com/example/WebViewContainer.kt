package com.example

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Composable that hosts a WebView rendering the local SmartAR_ECU_MAP_Mobile.html asset.
 * Configured with full camera support, JavaScript execution, DOM storage, and bridge integration.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    modifier: Modifier = Modifier,
    assetUrl: String = "file:///android_asset/SmartAR_ECU_MAP_Mobile.html",
    javascriptInterface: Any? = null,
    interfaceName: String = "AndroidBridge",
    onPermissionRequestCallback: ((PermissionRequest) -> Unit)? = null,
    onWebViewCreated: ((WebView) -> Unit)? = null
) {
    val context = LocalContext.current

    val webView = remember(context) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.TRANSPARENT)

            settings.apply {
                // 1. JavaScript & DOM Storage
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true

                // 2. Camera & Media Support (Mind-AR / WebRTC)
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                // 3. Local file & asset access
                allowFileAccess = true
                allowContentAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true

                // 4. Viewport & display optimization
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(false)
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            // WebChromeClient to handle HTML5 Camera/Microphone WebRTC permission requests
            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest?) {
                    if (request != null) {
                        if (onPermissionRequestCallback != null) {
                            onPermissionRequestCallback(request)
                        } else {
                            // UI 스레드에서 권한 부여를 실행해야 WebRTC 카메라가 정상 작동함
                            post {
                                request.grant(request.resources)
                            }
                        }
                    }
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: android.webkit.WebResourceRequest?
                ): android.webkit.WebResourceResponse? {
                    val url = request?.url?.toString() ?: ""
                    if (url.startsWith("https://appassets.androidplatform.net/assets/")) {
                        val assetPath = url.replace("https://appassets.androidplatform.net/assets/", "")
                        try {
                            val inputStream = context.assets.open(assetPath)
                            var mimeType = "text/html"
                            if (assetPath.endsWith(".mind")) mimeType = "application/octet-stream"
                            else if (assetPath.endsWith(".png")) mimeType = "image/png"
                            else if (assetPath.endsWith(".jpg") || assetPath.endsWith(".jpeg")) mimeType = "image/jpeg"
                            else if (assetPath.endsWith(".js")) mimeType = "application/javascript"
                            else if (assetPath.endsWith(".css")) mimeType = "text/css"
                            
                            val response = android.webkit.WebResourceResponse(mimeType, "UTF-8", inputStream)
                            response.responseHeaders = mutableMapOf(
                                "Access-Control-Allow-Origin" to "*"
                            )
                            return response
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                @SuppressLint("WebViewClientOnReceivedSslError")
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: android.webkit.SslErrorHandler?,
                    error: android.net.http.SslError?
                ) {
                    handler?.proceed()
                }
            }

            // Attach JavaScript interface if provided
            if (javascriptInterface != null) {
                addJavascriptInterface(javascriptInterface, interfaceName)
            }

            loadUrl(assetUrl)
            onWebViewCreated?.invoke(this)
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}
