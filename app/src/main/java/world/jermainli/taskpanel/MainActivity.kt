package world.jermainli.taskpanel

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.HttpAuthHandler
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * 狗蛋任务面板的安卓外壳。
 * 就是一个 WebView 加载面板地址：面板本身是 PWA，这里只是让它看起来/用起来像原生 App。
 * 面板有 HTTP Basic Auth，WebView 不会自动弹框，所以由 onReceivedHttpAuthRequest 自动填写。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var errorView: TextView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserRequest = 1001

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        web = findViewById(R.id.web)
        swipe = findViewById(R.id.swipe)
        errorView = findViewById(R.id.error)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            mediaPlaybackRequiresUserGesture = true
        }

        web.webViewClient = object : WebViewClient() {
            override fun onReceivedHttpAuthRequest(
                view: WebView?,
                handler: HttpAuthHandler?,
                host: String?,
                realm: String?
            ) {
                // 面板用 Basic Auth；这里自动填凭据（构建时注入，见 build.gradle.kts）
                android.util.Log.d("TaskPanel", "auth 请求 host=$host realm=$realm user=${BuildConfig.PANEL_USER}")
                handler?.proceed(BuildConfig.PANEL_USER, BuildConfig.PANEL_PASS)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                if (request?.isForMainFrame == true) {
                    errorView.visibility = View.VISIBLE
                    errorView.text = "连不上了：${error?.description ?: "网络错误"}\n下拉可重试"
                }
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.cancel()   // 证书有问题就拒绝，别放行
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                errorView.visibility = View.VISIBLE
                errorView.text = "正在连接…"
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                swipe.isRefreshing = false
                errorView.visibility = View.GONE
            }
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(m: android.webkit.ConsoleMessage?): Boolean {
                android.util.Log.d("TaskPanel", "JS: ${m?.message()} @${m?.lineNumber()}")
                return true
            }

            // WebView 默认不支持 <input type="file">，必须由原生接管，否则点了没反应
            override fun onShowFileChooser(
                webView: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: FileChooserParams?
            ): Boolean {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = callback
                return try {
                    val intent = params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }
                    startActivityForResult(intent, fileChooserRequest)
                    true
                } catch (e: Exception) {
                    android.util.Log.e("TaskPanel", "文件选择器启动失败", e)
                    filePathCallback = null
                    false
                }
            }
        }

        swipe.setColorSchemeColors(0xFF4D8DF7.toInt())
        swipe.setOnRefreshListener { web.reload() }

        web.loadUrl(BuildConfig.PANEL_URL)
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == fileChooserRequest) {
            filePathCallback?.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            )
            filePathCallback = null
            return
        }
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (::web.isInitialized && web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
