package com.lobsterai.zhongruan_tuner.audio

import be.tarsos.dsp.pitch.PitchDetectionModel
import be.tarsos.dsp.pitch.PitchProcessor

class PitchDetector {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val BUFFER_SIZE = 2048

        // 中阮频率范围：80Hz - 400Hz
        private const val MIN_FREQUENCY = 80f
        private const val MAX_FREQUENCY = 400f
    }

    private val pitchProcessor = PitchProcessor(
        PitchDetectionModel.YIN,
        SAMPLE_RATE,
        BUFFER_SIZE
    )

    /**
     * 从音频数据中检测频率
     */
    fun detectFrequency(audioData: ShortArray): Float? {
        try {
            val pitchResult = pitchProcessor.detectPitch(audioData, audioData.size)
            val frequency = pitchResult.pitch

            // 过滤无效频率
            return if (frequency in MIN_FREQUENCY..MAX_FREQUENCY) {
                frequency
            } else {
                null
            }
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 将频率转换为音名
     */
    fun frequencyToPitchName(frequency: Float): String {
        val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        // A4 = 440Hz 作为基准
        val a4 = 440.0
        val semitonesFromA4 = 12 * (kotlin.math.log(frequency / a4) / kotlin.math.ln(2))
        val noteIndex = ((semitonesFromA4 + 69) % 12).toInt()
        val octave = ((semitonesFromA4 + 69) / 12).toInt()

        return "${noteNames[noteIndex]}$octave"
    }
}
