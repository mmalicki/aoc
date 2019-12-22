package day1

import java.io.File
import kotlin.math.floor

fun main() {
    val f = File("src/main/resources/day1.txt").readLines()
        .sumBy { line -> return@sumBy floor(line.toInt() / 3.0).toInt() - 2 }
    println(f)
}
