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
import androidx.compose.ui.draw.scale
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
        factory = TunerViewModel.Factory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startListening()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopListening()
        }
    }

    val pulseScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

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
                .padding(horizontal = 20.dp)
                .padding(top = 36.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "中阮调音器",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 3.sp
            )

            Text(
                text = "ZHONGRUAN TUNER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                color = Color.Gray,
                letterSpacing = 6.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Text(
                text = "当前：第${state.selectedString.id}弦",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TunerYellow
            )

            Spacer(modifier = Modifier.height(12.dp))

            StringSelectorModern(
                selectedString = state.selectedString,
                onStringSelected = { viewModel.selectString(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TunerDisplayModern(
                frequency = state.detectedFrequency,
                pitchName = state.detectedPitch,
                status = state.status,
                deviation = state.deviation,
                pulseScale = pulseScale
            )

            Spacer(modifier = Modifier.height(12.dp))

            PointerModern(
                deviation = state.deviation,
                status = state.status
            )

            Spacer(modifier = Modifier.height(10.dp))

            StatusTextModern(
                status = state.status,
                deviation = state.deviation
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "弹响琵琶弦开始调音",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

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
                        color = Color.Gray,
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RuanString.entries.forEach { ruanString ->
                StringButtonModern(
                    ruanString = ruanString,
                    isSelected = ruanString == selectedString,
                    onClick = { onStringSelected(ruanString) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Text(
            text = "点击按钮选择要调的弦",
            fontSize = 10.sp,
            color = Color.Gray.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun StringButtonModern(
    ruanString: RuanString,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) TunerYellow else Color.White.copy(alpha = 0.1f)
    val nameColor = if (isSelected) DarkBackground else Color.Gray

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .scale(if (isSelected) 1.05f else 1f)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "第${ruanString.id}弦",
            color = nameColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = ruanString.pitch,
            color = nameColor.copy(alpha = 0.8f),
            fontSize = 10.sp,
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
    val statusColor = when (status) {
        TunerStatus.IN_TUNE -> TunerGreen
        TunerStatus.TOO_LOW -> TunerRed
        TunerStatus.TOO_HIGH -> TunerOrange
    }

    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        statusColor.copy(alpha = 0.3f * pulseScale),
                        statusColor.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            pitchName?.let {
                Text(
                    text = it,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            frequency?.let {
                Text(
                    text = String.format("%.1f Hz", it),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
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
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            drawLine(
                color = TunerGreen,
                start = Offset(centerX, centerY - 20),
                end = Offset(centerX, centerY + 20),
                strokeWidth = 3.dp.toPx()
            )

            for (i in -5..5) {
                if (i != 0) {
                    val x = centerX + (i * 20)
                    val isMark = i % 5 == 0
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5F),
                        start = Offset(x, centerY - if (isMark) 15f else 8f),
                        end = Offset(x, centerY + if (isMark) 15f else 8f),
                        strokeWidth = if (isMark) 4f else 2f
                    )
                }
            }
        }

        val pointerX = (animatedDeviation / 50f) * 100f
        Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            drawCircle(
                color = pointerColor,
                radius = 9f,
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
        TunerStatus.TOO_LOW -> "太低 ← 调紧" to TunerRed
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
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = color.copy(alpha = alpha),
        textAlign = TextAlign.Center
    )
}