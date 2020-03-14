package com.mobileproj.dynascope

import kotlin.math.absoluteValue
import kotlin.math.sqrt


fun FloatArray.stats(): String {
    var min = Float.MAX_VALUE
    var argmin = -1
    var absmin = Float.MAX_VALUE
    var argabsmin = -1
    var max = Float.MIN_VALUE
    var argmax = -1
    var absmax = 0F
    var argabsmax = -1

    this.forEachIndexed { index, fl ->
        if (fl > max) {
            max = fl
            argmax = index
        }
        if (fl < min) {
            min = fl
            argmin = index
        }
        if (fl.absoluteValue > absmax) {
            absmax = fl.absoluteValue
            argabsmax = index
        }
        if (fl.absoluteValue < absmin) {
            absmin = fl.absoluteValue
            argabsmin = index
        }
    }

    return "min: $min, argmin: $argmin, absmin: $absmin, argabsmin: $argabsmin, max: $max, argmax: $argmax, absmax: $absmax, argabsmax: $argabsmax"
}

fun FloatArray.innerproduct(other: FloatArray) = zip(other).map { it.first * it.second }.sum()

fun FloatArray.autocorr(n: Int): Float {
    // drop first n measurements
    val shifted = this.copyOfRange(n * 3, this.size)
    val slice = this.sliceArray(0..shifted.size)
    val innerProduct = slice.innerproduct(shifted)
    val shiftedNorm = sqrt(shifted.innerproduct(shifted))
    val sliceNorm = sqrt(slice.innerproduct(slice))
    return innerProduct / shiftedNorm / sliceNorm
}