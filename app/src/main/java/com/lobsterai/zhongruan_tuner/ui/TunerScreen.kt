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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "涓槷璋冮煶鍣?,
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

            // 娣诲姞閱掔洰鐨勫綋鍓嶅鸡鏄剧ず
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "褰撳墠锛氱${state.selectedString.id}寮?,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TunerYellow,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            StringSelectorModern(
                selectedString = state.selectedString,
                onStringSelected = { viewModel.selectString(it) }
            )

            Spacer(modifier = Modifier.height(50.dp))

            TunerDisplayModern(
                frequency = state.detectedFrequency,
                pitchName = state.detectedPitch,
                status = state.status,
                deviation = state.deviation,
                pulseScale = pulseScale
            )

            Spacer(modifier = Modifier.height(32.dp))

            StatusTextModern(
                status = state.status,
                deviation = state.deviation
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "寮瑰搷鐞村鸡寮€濮嬭皟闊?,
                fontSize = 14.sp,
                color = Color.Gray.copy(alpha = 0.7F),
                modifier = Modifier.padding(bottom = 32.dp)
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
                        text = "鐐瑰嚮閲嶈瘯",
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
                .height(80.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RuanString.entries.forEach { ruanString ->
                StringButtonModern(
                    ruanString = ruanString,
                    isSelected = ruanString == selectedString,
                    onClick = { onStringSelected(ruanString) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "鐐瑰嚮涓嬫柟鎸夐挳閫夋嫨鐞村鸡",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StringButtonModern(
    ruanString: RuanString,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) TunerYellow else StringUnselected
    val nameColor = if (isSelected) DarkBackground else SecondaryText
    val scale = if (isSelected) 1.1f else 1.0f

    Column(
        modifier = Modifier
            .size(width = 72.dp, height = 80.dp)
            .scale(scale)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "绗?{ruanString.id}寮?,
            style = StringName.copy(color = nameColor),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = ruanString.pitch,
            style = StringName.copy(color = nameColor.copy(alpha = 0.8f)),
            fontSize = 12.sp,
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
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
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                frequency?.let {
                    Text(
                        text = String.format("%.1f Hz", it),
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PointerModern(
            deviation = deviation,
            status = status
        )
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
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            drawLine(
                color = TunerGreen,
                start = Offset(centerX, centerY - 25),
                end = Offset(centerX, centerY + 25),
                strokeWidth = 3.dp.toPx()
            )

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

        val pointerX = (animatedDeviation / 50f) * 100f
        Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2

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
        TunerStatus.TOO_LOW -> "澶綆 鈫?璋冪揣" to TunerRed
        TunerStatus.IN_TUNE -> "鉁?鍑嗭紒" to TunerGreen
        TunerStatus.TOO_HIGH -> "澶珮 鈫?璋冩澗" to TunerOrange
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


