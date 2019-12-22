package day4

import java.io.File
import kotlin.system.measureTimeMillis
object StrictAdjacentDigits : PassRule {
    override fun verify(password: Int): Boolean {
        val passString = password.toString()
        var lastChar = passString.first()
        var adjacentChars = 0

        passString.forEachIndexed { index, passwordChar ->
            if (passwordChar == lastChar) {
                adjacentChars++
            } else {
                if (adjacentChars == 2) {
                    return true
                }
                lastChar = passwordChar
                adjacentChars = 1
            }
            if (index == passString.lastIndex && adjacentChars == 2) {
                return true
            }
        }

        return false
    }
}

fun main() {
    val (start, end) = File("src/main/resources/day4.txt")
        .readText()
        .split("-")
        .map { it.toInt() }

    val passwordRules = listOf(NonDecreasingDigits, StrictAdjacentDigits)

    val t = measureTimeMillis {
       val validPasswords = (start..end).filter { password -> passwordRules.all { it.verify(password) } }
        println(validPasswords.size)
    }
    println(t)
}