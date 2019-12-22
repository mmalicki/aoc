package day4

import java.io.File

interface PassRule {
    fun verify(password: Int): Boolean
}
object AdjacentDigits : PassRule {
    override fun verify(password: Int): Boolean {
        return password.toString().windowed(size = 2).any { digitsPair -> digitsPair[0] == digitsPair[1] }
    }
}

object NonDecreasingDigits : PassRule {
    override fun verify(password: Int): Boolean {
        var passwordMut = password

        while (passwordMut > 0) {
            val lastDigit = passwordMut % 10
            passwordMut /= 10
            val currentLastDigit = passwordMut % 10
            if (currentLastDigit > lastDigit) {
                return false
            }
        }

        return true
    }
}

fun main() {
    val (start, end) = File("src/main/resources/day4.txt")
        .readText()
        .split("-")
        .map { it.toInt() }

    val filters = listOf(AdjacentDigits, NonDecreasingDigits)

    val validPasswords = (start..end).filter { password -> filters.all { it.verify(password) } }

    println(validPasswords.size)
}