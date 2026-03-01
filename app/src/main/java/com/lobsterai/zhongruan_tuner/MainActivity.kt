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

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "Permission result: $isGranted")
        // 权限请求完成，应用会继续显示
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity onCreate")

        // 请求权限
        val currentPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO)
        if (currentPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting RECORD_AUDIO permission")
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            Log.d(TAG, "Permission already granted")
        }

        setContent {
            ZhongruanTunerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TunerScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 检查权限状态
        val currentPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO)
        Log.d(TAG, "onResume permission status: $currentPermission")
    }
}
