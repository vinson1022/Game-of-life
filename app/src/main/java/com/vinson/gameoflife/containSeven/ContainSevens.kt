package com.vinson.gameoflife.containSeven

import java.util.*
import kotlin.collections.HashMap
import kotlin.math.log10
import kotlin.math.pow

object ContainSevens {

    private const val CONTAIN_TARGET = 7

    private val logAnsRecord : HashMap<Int, Int> = HashMap()

    private fun Int.pow(n: Int) : Int = toDouble().pow(n).toInt()

    fun calculate(num: Int): Int {
        if (num < 0) throw InputMismatchException()

        return if (num < 1000) getAnsByLoop(num)
        else getAnsByMath(num)
    }

    private fun getAnsByMath(num: Int) : Int {
        val logNum = log10(num.toDouble()).toInt()
        if (logNum == 0 || num == 0) {
            return if (num >= CONTAIN_TARGET) 1
            else 0
        }

        val r = num / 10.pow(logNum)
        if (r == CONTAIN_TARGET) {
            return r * recursiveLogAns(logNum) + (num - r * 10.pow(logNum) + 1)
        }

        return calculate(num - r * 10.pow(logNum)) +
                if (r > CONTAIN_TARGET) {
                    (r - 1) * recursiveLogAns(logNum) + 10.pow(logNum)
                } else {
                    r * recursiveLogAns(logNum)
                }
    }

    private fun recursiveLogAns(log: Int): Int {
        if (log == 1) return 1
        if (logAnsRecord.containsKey(log)) return logAnsRecord[log]!!
        var ans = recursiveLogAns(log - 1) * 10 + 10.pow(log - 1)
        for (i in 0 until log - 1) {
            ans -= 9.pow(i) * combination(log - 1, log - 1 - i)
        }
        logAnsRecord[log] = ans
        return ans
    }

    private fun combination(m: Int, n: Int) : Int {
        if (m < 0 || n < 0) return 1
        return factorial(m) / (factorial(n) * factorial(m - n))
    }

    private fun factorial(v: Int): Int {
        return if (v <= 0) 1
         else v * factorial(v - 1)
    }

    private fun isDigitPresent(x_input: Int, d : Int = CONTAIN_TARGET): Boolean {
        var x = x_input
        // Break loop if d is present as digit
        while (x > 0) {
            if (x % 10 == d)
                break
            x /= 10
        }

        // If loop broke
        return x > 0
    }

    // function to display the values
    private fun getAnsByLoop(n: Int, d: Int = CONTAIN_TARGET) : Int {
        // Check all numbers one by one
        var ans = 0
        for (i in 0..n) {
            if (i == d || isDigitPresent(i)) ans++
        }
        return ans
    }
}