package day4

import java.io.File

object StrictAdjacentDigits : PassRule {
    override fun verify(password: Int): Boolean {
        var lastChar = password.toString().first()
        var lastGroup = mutableListOf<Char>()
        val groups = mutableListOf<List<Char>>(lastGroup)
        password.toString().forEach { passwordChar ->
            if (passwordChar == lastChar) {
                lastGroup.add(passwordChar)
            } else {
                lastChar = passwordChar
                lastGroup = mutableListOf(lastChar)
                groups.add(lastGroup)
            }
        }

        return groups.any { it.size == 2 }
    }
}

fun main() {
    val (start, end) = File("src/main/resources/day4.txt")
        .readText()
        .split("-")
        .map { it.toInt() }

    val passwordRules = listOf(StrictAdjacentDigits, NonDecreasingDigits)

    val validPasswords = (start..end).filter { password -> passwordRules.all { it.verify(password) } }

    println(validPasswords.size)
}