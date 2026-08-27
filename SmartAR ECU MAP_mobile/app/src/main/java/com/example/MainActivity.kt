package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.io.BufferedReader
import java.io.InputStreamReader

import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {

    private var webView: WebView? = null
    private val PREFS_NAME = "SmartAR_ECU_Prefs"
    private val KEY_ECU_DATA = "ar_ecu_positions_v2"
    
    private val hasCameraPermission = mutableStateOf(false)

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission.value = isGranted
        if (isGranted) {
            Toast.makeText(this, "카메라 권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "AR 기능을 사용하려면 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    private var pendingCsvDataToExport: String? = null

    private val createDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            pendingCsvDataToExport?.let { csvData ->
                try {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(csvData.toByteArray(Charsets.UTF_8))
                    }
                    Toast.makeText(this, "CSV 파일이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "CSV 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        pendingCsvDataToExport = null
    }

    private val csvPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { fileUri ->
            try {
                contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                    val csvContent = reader.readText()
                    // Escape string for JavaScript function evaluation
                    val escapedCsv = csvContent
                        .replace("\\", "\\\\")
                        .replace("`", "\\`")
                        .replace("\r", "")

                    val jsCode = "window.onCsvDataReceived && window.onCsvDataReceived(`$escapedCsv`);"
                    runOnUiThread {
                        webView?.evaluateJavascript(jsCode, null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "CSV 파일을 읽는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카메라 권한 초기 확인 및 요청
        hasCameraPermission.value = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!hasCameraPermission.value) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // 뒤로가기 처리를 위한 OnBackPressedCallback 등록 (Deprecated onBackPressed 대체)
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentWebView = webView
                if (currentWebView != null && currentWebView.canGoBack()) {
                    currentWebView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

        setContent {
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
                // [아키텍처 수정] 네이티브 CameraX(CameraPreview) 중단.
                // 웹뷰 내부의 Mind-AR (WebRTC)가 단독으로 카메라 제어권을 획득하여 타이어 이미지를 트래킹하도록 함.
                WebViewContainer(
                    assetUrl = "file:///android_asset/SmartAR_ECU_MAP_Mobile.html",
                    javascriptInterface = AndroidBridge(),
                    interfaceName = "AndroidBridge",
                    onWebViewCreated = { instance ->
                        webView = instance
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        webView = null // 메모리 누수 방지
        super.onDestroy()
    }

    inner class AndroidBridge {

        @JavascriptInterface
        fun saveData(jsonStr: String) {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_ECU_DATA, jsonStr).apply()
        }

        @JavascriptInterface
        fun loadData(): String {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_ECU_DATA, "") ?: ""
        }

        @JavascriptInterface
        fun onNodeSelected(nodeId: String) {
            // 특정 노드 선택 시 네이티브 로깅 또는 커스텀 처리
        }

        @JavascriptInterface
        fun onDtcChanged(nodeId: String, jsonStr: String) {
            // DTC 고장 코드 변경 시 네이티브 처리
        }

        @JavascriptInterface
        fun vibrate(ms: Long) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    val vibrator = vibratorManager.defaultVibrator
                    vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(ms)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JavascriptInterface
        fun setLandscapeLock(lock: Boolean) {
            runOnUiThread {
                requestedOrientation = if (lock) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                }
            }
        }

        @JavascriptInterface
        fun showToast(message: String) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun triggerCsvPicker() {
            runOnUiThread {
                csvPickerLauncher.launch("*/*")
            }
        }

        @JavascriptInterface
        fun exportCsvData(csvStr: String, defaultFilename: String) {
            pendingCsvDataToExport = csvStr
            runOnUiThread {
                createDocumentLauncher.launch(defaultFilename)
            }
        }
    }

}
