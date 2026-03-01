package com.lobsterai.zhongruan_tuner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lobsterai.zhongruan_tuner.ui.TunerScreen
import com.lobsterai.zhongruan_tuner.ui.theme.ZhongruanTunerTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var hasPermission = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "Permission result: $isGranted")
        hasPermission = isGranted
        if (!isGranted) {
            // 权限被拒绝，应用会显示错误提示
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity onCreate")

        requestPermission()

        setContent {
            ZhongruanTunerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TunerScreen(
                        hasPermission = hasPermission
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 检查权限状态是否改变
        val currentPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO)
        val newHasPermission = (currentPermission == PackageManager.PERMISSION_GRANTED)
        if (newHasPermission && !hasPermission) {
            Log.d(TAG, "Permission granted in onResume")
            hasPermission = true
        }
    }

    private fun requestPermission() {
        val currentPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO)
        hasPermission = (currentPermission == PackageManager.PERMISSION_GRANTED)
        Log.d(TAG, "Current permission status: $hasPermission")

        if (!hasPermission) {
            Log.d(TAG, "Requesting RECORD_AUDIO permission")
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}
