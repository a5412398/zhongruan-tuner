package com.lobsterai.zhongruan_tuner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lobsterai.zhongruan_tuner.ui.components.FrequencyDisplay
import com.lobsterai.zhongruan_tuner.ui.components.PitchIndicator
import com.lobsterai.zhongruan_tuner.ui.components.StringSelector
import com.lobsterai.zhongruan_tuner.ui.theme.AppTitle
import com.lobsterai.zhongruan_tuner.ui.theme.DarkBackground
import com.lobsterai.zhongruan_tuner.ui.theme.WhiteText

@Composable
fun TunerScreen(
    viewModel: TunerViewModel = viewModel(
        factory = TunerViewModelFactory(LocalContext.current)
    )
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题
        Text(
            text = "中阮调音器",
            style = AppTitle,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // 弦选择器
        StringSelector(
            selectedString = state.selectedString,
            onStringSelected = { viewModel.selectString(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 频率显示
        FrequencyDisplay(
            frequency = state.detectedFrequency,
            pitchName = state.detectedPitch
        )

        // 调音指示器
        PitchIndicator(
            deviation = state.deviation,
            status = state.status
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
