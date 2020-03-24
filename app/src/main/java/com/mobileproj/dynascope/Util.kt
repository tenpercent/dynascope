package com.mobileproj.dynascope

import java.util.ArrayDeque
import kotlin.math.sqrt

fun FloatArray.innerproduct(other: FloatArray) = zip(other).map { it.first * it.second }.sum()

fun FloatArray.autocorr(n: Int): Float {
    // drop first n measurements
    val shifted = copyOfRange(n * 3, this.size)
    val slice = sliceArray(0..shifted.size)
    val innerProduct = slice.innerproduct(shifted)
    val shiftedNorm = sqrt(shifted.innerproduct(shifted))
    val sliceNorm = sqrt(slice.innerproduct(slice))
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