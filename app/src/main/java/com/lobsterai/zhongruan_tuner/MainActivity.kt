package com.lobsterai.zhongruan_tuner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.lobsterai.zhongruan_tuner.ui.TunerScreen
import com.lobsterai.zhongruan_tuner.ui.theme.ZhongruanTunerTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var hasPermission by mutableStateOf(false)
    private var permissionRequested by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        Log.i(TAG, "=== Permission result: $isGranted ===")
        if (isGranted) {
            Log.i(TAG, "Permission granted!")
        } else {
            Log.w(TAG, "Permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "=== Activity onCreate ===")
        Log.i(TAG, "Package: $packageName")

        // 先检查权限状态
        val currentPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        hasPermission = (currentPermission == PackageManager.PERMISSION_GRANTED)
        Log.i(TAG, "Initial permission status: $hasPermission")

        setContent {
            ZhongruanTunerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 使用 LaunchedEffect 在 UI 渲染后请求权限
                    if (!hasPermission && !permissionRequested) {
                        Log.i(TAG, "Requesting permission from Composable")
                        permissionRequested = true
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }

                    if (hasPermission) {
                        Log.i(TAG, "Showing TunerScreen")
                        TunerScreen()
                    } else {
                        Log.i(TAG, "Showing permission request UI")
                        PermissionRequestUI()
                    }
                }
            }
        }

        Log.i(TAG, "=== Activity onCreate completed ===")
    }

    override fun onResume() {
        super.onResume()
        val currentPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        val newHasPermission = (currentPermission == PackageManager.PERMISSION_GRANTED)
        if (newHasPermission && !hasPermission) {
            Log.i(TAG, "Permission granted in onResume")
            hasPermission = true
        }
    }
}

@androidx.compose.runtime.Composable
fun PermissionRequestUI() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "需要麦克风权限",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = "请在弹出窗口中点击\"允许\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp)
        )
        CircularProgressIndicator(
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}
