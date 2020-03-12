package com.mobileproj.dynascope

import kotlin.math.absoluteValue


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

fun FloatArray.autocorr(n: Long): Float {
    // drop first n measurements
    val copy = this.copyOfRange((n * 3).toInt(), this.size)
    return this.zip(copy).map { it.first * it.second }.average().toFloat()
}