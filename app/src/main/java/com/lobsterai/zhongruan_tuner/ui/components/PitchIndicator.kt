package com.lobsterai.zhongruan_tuner.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lobsterai.zhongruan_tuner.model.TunerStatus
import com.lobsterai.zhongruan_tuner.ui.theme.*

@Composable
fun PitchIndicator(
    deviation: Float, // 音分偏差，范围 -50 到 +50
    status: TunerStatus,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 状态文字
        val statusText = when (status) {
            TunerStatus.TOO_LOW -> "太低 → 调紧"
            TunerStatus.IN_TUNE -> "准！"
            TunerStatus.TOO_HIGH -> "太高 ← 调松"
        }

        val statusColor = when (status) {
            TunerStatus.TOO_LOW -> TunerRed
            TunerStatus.IN_TUNE -> TunerGreen
            TunerStatus.TOO_HIGH -> TunerOrange
        }

        Text(
            text = statusText,
            style = StatusText.copy(color = statusColor),
            textAlign = TextAlign.Center,
            modifier = Modifier.height(80.dp)
        )

        // 指针
        val animatedDeviation by animateFloatAsState(
            targetValue = deviation.coerceIn(-50f, 50f),
            label = "deviation"
        )

        PointerScale(
            deviation = animatedDeviation,
            status = status
        )
    }
}

@Composable
private fun PointerScale(
    deviation: Float,
    status: TunerStatus
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2

            // 绘制刻度线
            val markPositions = listOf(-20, -10, 0, 10, 20)
            markPositions.forEach { mark ->
                val x = width / 2 + (mark / 50f) * (width / 2)
                val isCenter = mark == 0
                drawLine(
                    color = if (isCenter) TunerGreen else DisabledText,
                    start = Offset(x, centerY - 20),
                    end = Offset(x, centerY + 20),
                    strokeWidth = if (isCenter) 4f else 2f
                )
            }

            // 绘制指针
            val pointerX = width / 2 + (deviation / 50f) * (width / 2)
            val pointerColor = when (status) {
                TunerStatus.TOO_LOW -> TunerRed
                TunerStatus.IN_TUNE -> TunerGreen
                TunerStatus.TOO_HIGH -> TunerOrange
            }

            drawCircle(
                color = pointerColor,
                radius = 12f,
                center = Offset(pointerX, centerY)
            )
        }

        // 刻度标签
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "←", style = PointerMark)
            Text(text = "-20", style = PointerMark)
            Text(text = "-10", style = PointerMark)
            Text(text = "0", style = PointerMark.copy(color = TunerGreen))
            Text(text = "+10", style = PointerMark)
            Text(text = "+20", style = PointerMark)
            Text(text = "→", style = PointerMark)
        }
    }
}
