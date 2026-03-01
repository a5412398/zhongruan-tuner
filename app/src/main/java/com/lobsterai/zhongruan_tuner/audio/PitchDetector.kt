package com.lobsterai.zhongruan_tuner.audio

import kotlin.math.*

/**
 * 使用 YIN 算法进行音高检测
 * 参考：A. de Cheveigné & H. Kawahara, "YIN, a fundamental frequency estimator for speech and music", J. Acoust. Soc. Am. 111, 1917 (2002)
 */
class PitchDetector {

    companion object {
        private const val SAMPLE_RATE = 44100

        // 中阮频率范围：80Hz - 400Hz
        private const val MIN_FREQUENCY = 80f
        private const val MAX_FREQUENCY = 400f

        // YIN 算法参数
        private const val MAX_PERIOD = 44100 / 80  // 最低频率对应的最大周期
        private const val MIN_PERIOD = 44100 / 400 // 最高频率对应的最小周期
        private const val THRESHOLD = 0.15f // YIN 阈值
    }

    /**
     * 从音频数据中检测频率（使用 YIN 算法）
     */
    fun detectFrequency(audioData: ShortArray): Float? {
        if (audioData.size < MAX_PERIOD * 2) return null

        // 转换为浮点数并归一化
        val floatData = FloatArray(audioData.size) { i ->
            audioData[i] / 32768.0f
        }

        // 运行 YIN 算法
        val period = yin(floatData, MIN_PERIOD, MAX_PERIOD, THRESHOLD)

        if (period < 0) return null

        // 将周期转换为频率
        val frequency = SAMPLE_RATE / period

        // 过滤无效频率
        return if (frequency in MIN_FREQUENCY..MAX_FREQUENCY) {
            frequency
        } else {
            null
        }
    }

    /**
     * YIN 算法实现
     * @param data 音频数据（归一化到 -1..1）
     * @param minPeriod 最小周期
     * @param maxPeriod 最大周期
     * @param threshold 阈值
     * @return 检测到的周期（样本数），如果未检测到返回 -1
     */
    private fun yin(data: FloatArray, minPeriod: Int, maxPeriod: Int, threshold: Float): Float {
        val difference = computeDifferenceFunction(data, maxPeriod)
        val cumulativeMeanNormalized = computeCumulativeMeanNormalizedDifference(difference, minPeriod, maxPeriod)

        // 找到第一个低于阈值的谷值
        var bestPeriod = -1
        for (tau in minPeriod..maxPeriod) {
            if (cumulativeMeanNormalized[tau] < threshold) {
                // 确认这是局部最小值
                if (tau + 1 < cumulativeMeanNormalized.size &&
                    cumulativeMeanNormalized[tau] <= cumulativeMeanNormalized[tau + 1]) {
                    bestPeriod = tau
                    break
                }
            }
        }

        if (bestPeriod < 0) {
            // 如果没有找到低于阈值的，返回最小值
            var minValue = Float.MAX_VALUE
            for (tau in minPeriod..maxPeriod) {
                if (cumulativeMeanNormalized[tau] < minValue) {
                    minValue = cumulativeMeanNormalized[tau]
                    bestPeriod = tau
                }
            }
        }

        // 抛物线插值提高精度
        return parabolicInterpolation(data, bestPeriod)
    }

    /**
     * 计算差异函数
     */
    private fun computeDifferenceFunction(data: FloatArray, maxPeriod: Int): FloatArray {
        val difference = FloatArray(maxPeriod + 1)

        for (tau in 1..maxPeriod) {
            var sum = 0f
            for (i in 0 until data.size - tau) {
                val diff = data[i] - data[i + tau]
                sum += diff * diff
            }
            difference[tau] = sum
        }

        return difference
    }

    /**
     * 计算累积平均归一化差异函数
     */
    private fun computeCumulativeMeanNormalizedDifference(
        difference: FloatArray,
        minPeriod: Int,
        maxPeriod: Int
    ): FloatArray {
        val cmnd = FloatArray(difference.size)
        var runningSum = 0f

        cmnd[0] = 1f
        for (tau in 1..maxPeriod) {
            runningSum += difference[tau]
            cmnd[tau] = if (tau > 0) {
                difference[tau] / (runningSum / tau)
            } else {
                1f
            }
        }

        return cmnd
    }

    /**
     * 抛物线插值，提高周期检测精度
     */
    private fun parabolicInterpolation(data: FloatArray, period: Int): Float {
        if (period <= 1 || period >= data.size - 1) return period.toFloat()

        val x0 = period - 1
        val x1 = period
        val x2 = period + 1

        // 使用自相关函数的值进行插值
        val y0 = computeAutocorrelation(data, x0)
        val y1 = computeAutocorrelation(data, x1)
        val y2 = computeAutocorrelation(data, x2)

        // 抛物线顶点公式
        val denominator = y0 - 2 * y1 + y2
        if (abs(denominator) < 1e-10) return x1.toFloat()

        val offset = 0.5f * (y0 - y2) / denominator
        return (x1 + offset).toFloat()
    }

    /**
     * 计算指定周期的自相关值
     */
    private fun computeAutocorrelation(data: FloatArray, period: Int): Float {
        var sum = 0f
        for (i in 0 until data.size - period) {
            sum += data[i] * data[i + period]
        }
        return sum / (data.size - period)
    }

    /**
     * 将频率转换为音名
     */
    fun frequencyToPitchName(frequency: Float): String {
        val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        // A4 = 440Hz 作为基准
        val a4 = 440.0
        val semitonesFromA4 = 12 * (ln(frequency / a4) / ln(2.0))
        val noteIndex = ((semitonesFromA4 + 69) % 12).toInt()
        val octave = ((semitonesFromA4 + 69) / 12).toInt()

        val safeIndex = ((noteIndex % 12) + 12) % 12
        return "${noteNames[safeIndex]}$octave"
    }
}
