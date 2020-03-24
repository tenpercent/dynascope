package com.mobileproj.dynascope

import java.util.ArrayDeque
import kotlin.math.sqrt

// a % b for inner product of 2 vectors
operator fun FloatArray.rem(other: FloatArray) = zip(other).map { it.first * it.second }.sum()

fun FloatArray.autocorr(n: Int): Float {
    // drop first n measurements
    val shifted = copyOfRange(n * 3, this.size)
    val slice = sliceArray(0..shifted.size)
    val innerProduct = slice % shifted
    val shiftedNorm = sqrt(shifted % shifted)
    val sliceNorm = sqrt(slice % slice)
    return innerProduct / shiftedNorm / sliceNorm
}

fun<T> ArrayDeque<T>.addAndTrim(values: Sequence<T>, capacity: Int) {
    synchronized(this) {
        addAll(values)
        while (size > capacity) {
            poll()
        }
    }
}