package com.lobsterai.zhongruan_tuner.model

/**
 * 调音器状态
 */
enum class TunerStatus {
    TOO_LOW,    // 太低 - 红色
    IN_TUNE,    // 准 - 绿色
    TOO_HIGH    // 太高 - 橙色
}

data class TunerState(
    val selectedString: RuanString = RuanString.STRING_1,
    val detectedFrequency: Float? = null,
    val detectedPitch: String? = null,
    val deviation: Float = 0f, // 音分偏差
    val status: TunerStatus = TunerStatus.IN_TUNE,
    val isListening: Boolean = false,
    val error: String? = null
) {
    /**
     * 计算当前频率与目标频率的偏差 (单位：音分)
     * 公式：cents = 1200 * log2(f1 / f2)
     */
    fun calculateCents(frequency: Float, targetFrequency: Float): Float {
        val ratio = frequency.toDouble() / targetFrequency.toDouble()
        return (1200 * (kotlin.math.ln(ratio) / kotlin.math.ln(2.0))).toFloat()
    }

    /**
     * 根据偏差判断状态
     */
    fun getStatusFromCents(cents: Float): TunerStatus {
        return when {
            cents > 5 -> TunerStatus.TOO_LOW    // 频率低，需要调紧
            cents < -5 -> TunerStatus.TOO_HIGH  // 频率高，需要调松
            else -> TunerStatus.IN_TUNE
        }
    }
}
