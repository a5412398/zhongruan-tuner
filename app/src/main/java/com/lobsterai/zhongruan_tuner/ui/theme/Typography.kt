package com.lobsterai.zhongruan_tuner.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = androidx.compose.ui.text.TextStyle(
    fontFamily = FontFamily.Default,
    color = WhiteText
)

val AppTitle = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Medium,
    color = WhiteText
)

val StringName = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold,
    color = WhiteText
)

val StringNumber = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    color = SecondaryText
)

val FrequencyDisplay = TextStyle(
    fontSize = 48.sp,
    fontWeight = FontWeight.Bold,
    color = WhiteText
)

val PitchName = TextStyle(
    fontSize = 36.sp,
    fontWeight = FontWeight.Bold,
    color = WhiteText
)

val StatusText = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.Bold
)

val PointerMark = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    color = DisabledText
)
