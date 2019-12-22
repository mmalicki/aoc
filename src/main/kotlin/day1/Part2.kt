package day1

import java.io.File
import kotlin.math.floor

fun main() {
    val f = File("src/main/resources/day1.txt").readLines()
        .sumBy { line -> return@sumBy calcFuel(line.toInt())}
    println(f)
}

fun calcFuel(massOrFuel: Int): Int {
    val f = (massOrFuel / 3.0).toInt() - 2
    if (f <= 0) return 0
    return f + calcFuel(f)
}