package com.vinson.gameoflife

import com.vinson.gameoflife.containSeven.ContainSevens
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ContainSevensUnitTest {
    @Test
    fun testSample1() {
        for (i in 1..100000) {
            println("input = $i")
            assertEquals(ContainSevens.calculate(i), getNumbers(i))
        }
    }

    private fun isDigitPresent(x_input: Int, d : Int = 7): Boolean {
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
    private fun getNumbers(n: Int, d: Int = 7) : Int {
        // Check all numbers one by one
        var ans = 0
        for (i in 0..n) {
            if (i == d || isDigitPresent(i)) ans++
        }
        return ans
    }
}
