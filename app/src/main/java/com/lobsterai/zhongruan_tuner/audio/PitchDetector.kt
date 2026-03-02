package com.lobsterai.zhongruan_tuner.audio

import kotlin.math.*

class PitchDetector {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val MIN_FREQUENCY = 80f
        private const val MAX_FREQUENCY = 400f
        private const val MAX_PERIOD = 44100 / 80
        private const val MIN_PERIOD = 44100 / 400
        private const val THRESHOLD = 0.15f
    }

    fun detectFrequency(audioData: ShortArray): Float? {
        try {
            if (audioData.size < MAX_PERIOD * 2) return null
            
            val floatData = FloatArray(audioData.size) { i ->
                audioData[i] / 32768.0f
            }

            val period = yin(floatData, MIN_PERIOD, MAX_PERIOD, THRESHOLD)

            if (period < MIN_PERIOD || period > MAX_PERIOD) return null

            val frequency = SAMPLE_RATE / period

            return if (frequency in MIN_FREQUENCY..MAX_FREQUENCY) {
                frequency
            } else {
                null
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun yin(data: FloatArray, minPeriod: Int, maxPeriod: Int, threshold: Float): Float {
        val safeMaxPeriod = minOf(maxPeriod, data.size / 2 - 1)
        val safeMinPeriod = maxOf(minPeriod, 2)
        
        if (safeMaxPeriod <= safeMinPeriod) return -1f
        
        val difference = computeDifferenceFunction(data, safeMaxPeriod)
        val cumulativeMeanNormalized = computeCumulativeMeanNormalizedDifference(difference, safeMinPeriod, safeMaxPeriod)

        var bestPeriod = -1
        for (tau in safeMinPeriod..safeMaxPeriod) {
            if (tau < cumulativeMeanNormalized.size && cumulativeMeanNormalized[tau] < threshold) {
                if (tau + 1 < cumulativeMeanNormalized.size &&
                    cumulativeMeanNormalized[tau] <= cumulativeMeanNormalized[tau + 1]) {
                    bestPeriod = tau
                    break
                }
            }
        }

        if (bestPeriod < 0) {
            var minValue = Float.MAX_VALUE
            for (tau in safeMinPeriod..minOf(safeMaxPeriod, cumulativeMeanNormalized.size - 1)) {
                if (cumulativeMeanNormalized[tau] < minValue) {
                    minValue = cumulativeMeanNormalized[tau]
                    bestPeriod = tau
                }
            }
        }

        if (bestPeriod < safeMinPeriod || bestPeriod > safeMaxPeriod) return -1f

        return parabolicInterpolation(data, bestPeriod)
    }

    private fun computeDifferenceFunction(data: FloatArray, maxPeriod: Int): FloatArray {
        val safeMaxPeriod = minOf(maxPeriod, data.size - 1)
        val difference = FloatArray(safeMaxPeriod + 1)

        for (tau in 1..safeMaxPeriod) {
            var sum = 0f
            val limit = data.size - tau
            for (i in 0 until limit) {
                val diff = data[i] - data[i + tau]
                sum += diff * diff
            }
            difference[tau] = sum
        }

        return difference
    }

    private fun computeCumulativeMeanNormalizedDifference(
        difference: FloatArray,
        minPeriod: Int,
        maxPeriod: Int
    ): FloatArray {
        val cmnd = FloatArray(difference.size)
        var runningSum = 0f

        cmnd[0] = 1f
        val safeMax = minOf(maxPeriod, difference.size - 1)
        for (tau in 1..safeMax) {
            runningSum += difference[tau]
            cmnd[tau] = difference[tau] / (runningSum / tau)
        }

        return cmnd
    }

    private fun parabolicInterpolation(data: FloatArray, period: Int): Float {
        if (period <= 2 || period >= data.size - 2) return period.toFloat()

        return try {
            val y0 = computeAutocorrelation(data, period - 1)
            val y1 = computeAutocorrelation(data, period)
            val y2 = computeAutocorrelation(data, period + 1)

            val denominator = y0 - 2 * y1 + y2
            if (abs(denominator) < 1e-10) return period.toFloat()

            val offset = 0.5f * (y0 - y2) / denominator
            (period + offset).coerceIn(2f, (data.size - 2).toFloat())
        } catch (e: Exception) {
            period.toFloat()
        }
    }

    private fun computeAutocorrelation(data: FloatArray, period: Int): Float {
        if (period <= 0 || period >= data.size) return 0f
        var sum = 0f
        val limit = data.size - period
        for (i in 0 until limit) {
            sum += data[i] * data[i + period]
        }
        return sum / limit
    }

    fun frequencyToPitchName(frequency: Float): String {
        if (frequency <= 0) return "--"
        
        val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val a4 = 440.0
        val semitonesFromA4 = 12 * (ln(frequency / a4) / ln(2.0))
        val noteIndex = ((semitonesFromA4 + 69).roundToInt() % 12 + 12) % 12
        val octave = ((semitonesFromA4 + 69) / 12).roundToInt()
        return "${noteNames[noteIndex]}$octave"
    }
    
    private fun Double.roundToInt(): Int = this.toInt().let { 
        if (this - it >= 0.5) it + 1 else it 
    }
}