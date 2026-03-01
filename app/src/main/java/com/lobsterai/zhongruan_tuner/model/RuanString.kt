package com.lobsterai.zhongruan_tuner.model

/**
 * 中阮琴弦定义
 * 标准定弦：G2-D3-G3-C3 (从低到高：4-3-2-1)
 */
enum class RuanString(
    val id: Int,
    val name: String,
    val pitch: String,
    val frequency: Float,
    val displayName: String
) {
    STRING_1(1, "C", "C3", 130.81f, "C"),
    STRING_2(2, "G", "G2", 98.00f, "G"),
    STRING_3(3, "D", "D3", 146.83f, "D"),
    STRING_4(4, "G", "G3", 196.00f, "G");

    companion object {
        fun fromFrequency(frequency: Float): RuanString {
            return entries.minBy { kotlin.math.abs(it.frequency - frequency) }
        }
    }
}
