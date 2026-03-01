package com.lobsterai.zhongruan_tuner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lobsterai.zhongruan_tuner.ui.theme.FrequencyDisplay
import com.lobsterai.zhongruan_tuner.ui.theme.PitchName
import com.lobsterai.zhongruan_tuner.ui.theme.WhiteText

@Composable
fun FrequencyDisplay(
    frequency: Float?,
    pitchName: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(160.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = frequency?.let { "%.1f Hz".format(it) } ?: "--.- Hz",
            style = FrequencyDisplay,
            color = WhiteText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pitchName ?: "--",
            style = PitchName,
            color = WhiteText,
            textAlign = TextAlign.Center
        )
    }
}
