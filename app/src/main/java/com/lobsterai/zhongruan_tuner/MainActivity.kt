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

    private var permissionGranted = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        Log.i(TAG, "Permission result: $isGranted")

        // 权限授予后重新设置内容
        if (isGranted) {
            Log.i(TAG, "Permission granted, setting up TunerScreen")
            setupContent()
        } else {
            Log.w(TAG, "Permission denied, showing error UI")
            setupErrorContent()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "=== Activity onCreate ===")

        // 检查权限状态
        val currentPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )

        if (currentPermission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission already granted")
            permissionGranted = true
            setupContent()
        } else {
            Log.i(TAG, "Requesting permission...")
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun setupContent() {
        Log.i(TAG, "Setting up TunerScreen content")
        setContent {
            ZhongruanTunerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.i(TAG, "TunerScreen composed")
                    TunerScreen()
                }
            }
        }
        Log.i(TAG, "Content setup successful")
    }

    private fun setupErrorContent() {
        Log.i(TAG, "Setting up error content")
        setContent {
            ZhongruanTunerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                            text = "请在系统设置中允许麦克风访问",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        CircularProgressIndicator(
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        Log.i(TAG, "onResume permission: $currentPermission")
    }
}
