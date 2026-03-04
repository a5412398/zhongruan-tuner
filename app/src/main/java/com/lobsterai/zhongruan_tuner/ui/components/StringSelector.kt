package com.lobsterai.zhongruan_tuner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lobsterai.zhongruan_tuner.model.RuanString
import com.lobsterai.zhongruan_tuner.ui.theme.*

@Composable
fun StringSelector(
    selectedString: RuanString,
    onStringSelected: (RuanString) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(120.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RuanString.entries.forEach { ruanString ->
            StringButton(
                ruanString = ruanString,
                isSelected = ruanString == selectedString,
                onClick = { onStringSelected(ruanString) }
            )
        }
    }
}

@Composable
private fun StringButton(
    ruanString: RuanString,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) TunerYellow else StringUnselected
    val nameColor = if (isSelected) DarkBackground else SecondaryText
    val numberColor = if (isSelected) DarkBackground.copy(alpha = 0.7f) else SecondaryText.copy(alpha = 0.7f)
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
            text = ruanString.stringName,
            style = StringName.copy(color = nameColor),
            textAlign = TextAlign.Center
        )
        Text(
            text = ruanString.id.toString(),
            style = StringNumber.copy(color = numberColor),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
