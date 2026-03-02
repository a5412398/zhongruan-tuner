package com.lobsterai.zhongruan_tuner.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lobsterai.zhongruan_tuner.model.RuanString
import com.lobsterai.zhongruan_tuner.model.TunerStatus
import com.lobsterai.zhongruan_tuner.ui.theme.*

@Composable
fun TunerScreen(
    viewModel: TunerViewModel = viewModel(
        factory = TunerViewModelFactory(LocalContext.current)
    )
) {
    val state by viewModel.state.collectAsState()

    // 启动时开始监听音频
    LaunchedEffect(Unit) {
        viewModel.startListening()
    }

    // 动画效果
    val pulseScale = animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    ).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D1A),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部标题区域
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "中阮调音器",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp
            )

            Text(
                text = "ZHONGRUAN TUNER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = Color.Gray,
                letterSpacing = 8.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 弦选择器 - 改进设计
            StringSelectorModern(
                selectedString = state.selectedString,
                onStringSelected = { viewModel.selectString(it) }
            )

            Spacer(modifier = Modifier.height(50.dp))

            // 中央调音显示区域
            TunerDisplayModern(
                frequency = state.detectedFrequency,
                pitchName = state.detectedPitch,
                status = state.status,
                deviation = state.deviation,
                pulseScale = pulseScale
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 状态提示
            StatusTextModern(
                status = state.status,
                deviation = state.deviation
            )

            // 底部提示
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "弹响琴弦开始调音",
                fontSize = 14.sp,
                color = Color.Gray.copy(alpha = 0.7F),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // 错误提示
        state.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85F))
                    .clickable { viewModel.clearError() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = error,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    Text(
                        text = "点击重试",
                        fontSize = 14.sp,
                        color = TunerYellow,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StringSelectorModern(
    selectedString: RuanString,
    onStringSelected: (RuanString) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.05F),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 20.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RuanString.entries.forEach { ruanString ->
            StringButtonModern(
                ruanString = ruanString,
                isSelected = ruanString == selectedString,
                onClick = { onStringSelected(ruanString) }
            )
        }
    }
}

@Composable
fun StringButtonModern(
    ruanString: RuanString,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(TunerYellow, TunerYellow.copy(alpha = 0.7F))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.1F),
                Color.White.copy(alpha = 0.05F)
            )
        )
    }

    Column(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = ruanString.stringName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) DarkBackground else Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${ruanString.id}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) DarkBackground.copy(alpha = 0.7f) else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TunerDisplayModern(
    frequency: Float?,
    pitchName: String?,
    status: TunerStatus,
    deviation: Float,
    pulseScale: Float
) {
    // 外圈光环
    val ringColor = when (status) {
        TunerStatus.IN_TUNE -> TunerGreen
        TunerStatus.TOO_LOW -> TunerRed
        TunerStatus.TOO_HIGH -> TunerOrange
    }

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // 外圈动画
        Canvas(modifier = Modifier.size(280.dp * pulseScale)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ringColor.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                radius = size.minDimension / 2
            )
        }

        // 外圈
        Canvas(modifier = Modifier.size(260.dp)) {
            drawCircle(
                color = ringColor.copy(alpha = 0.3f),
                radius = size.minDimension / 2,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // 内圈
        Canvas(modifier = Modifier.size(220.dp)) {
            drawCircle(
                color = ringColor.copy(alpha = 0.5f),
                radius = size.minDimension / 2,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // 中央内容
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 频率显示
            Text(
                text = frequency?.let { "%.1f".format(it) } ?: "--.-",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Hz",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 音名显示
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(ringColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pitchName ?: "--",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = ringColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // 指针刻度
    Spacer(modifier = Modifier.height(24.dp))

    PointerModern(
        deviation = deviation,
        status = status
    )
}

@Composable
fun PointerModern(
    deviation: Float,
    status: TunerStatus
) {
    val pointerColor = when (status) {
        TunerStatus.IN_TUNE -> TunerGreen
        TunerStatus.TOO_LOW -> TunerRed
        TunerStatus.TOO_HIGH -> TunerOrange
    }

    val animatedDeviation by animateFloatAsState(
        targetValue = deviation.coerceIn(-50f, 50f),
        animationSpec = tween(150, easing = EaseOutQuad),
        label = "pointer"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // 刻度线
        Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // 中心线
            drawLine(
                color = TunerGreen,
                start = Offset(centerX, centerY - 25),
                end = Offset(centerX, centerY + 25),
                strokeWidth = 3.dp.toPx()
            )

            // 其他刻度
            for (i in -5..5) {
                if (i != 0) {
                    val x = centerX + (i * 20)
                    val isMark = i % 5 == 0
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5F),
                        start = Offset(x, centerY - if (isMark) 20f else 10f),
                        end = Offset(x, centerY + if (isMark) 20f else 10f),
                        strokeWidth = if (isMark) 6f else 3f
                    )
                }
            }
        }

        // 指针
        val pointerX = (animatedDeviation / 50f) * 100f
        Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // 指针
            drawCircle(
                color = pointerColor,
                radius = 12f,
                center = Offset(centerX + pointerX, centerY)
            )
        }
    }
}

@Composable
fun StatusTextModern(
    status: TunerStatus,
    deviation: Float
) {
    val (text, color) = when (status) {
        TunerStatus.TOO_LOW -> "太低 → 调紧" to TunerRed
        TunerStatus.IN_TUNE -> "✓ 准！" to TunerGreen
        TunerStatus.TOO_HIGH -> "太高 ← 调松" to TunerOrange
    }

    val alpha by animateFloatAsState(
        targetValue = if (status == TunerStatus.IN_TUNE) 1f else 0.7f,
        animationSpec = tween(200),
        label = "alpha"
    )

    Text(
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = color.copy(alpha = alpha),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}
