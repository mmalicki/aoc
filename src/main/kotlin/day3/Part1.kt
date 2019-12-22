package day3

import java.io.File
import kotlin.math.abs

data class Point(val x: Int, val y: Int)

const val FIRST_WIRE = "F"
const val SECOND_WIRE = "S"

fun main() {
    val (firstWirePath, secondWirePath) = File("src/main/resources/day3.txt")
        .readText()
        .split("\n")
        .map { it.split(",") }

    val visitedPoints = mutableMapOf<Point, MutableSet<String>>()

    fun markIntersectedPointsVertically(currentPosX: Int, currentPosY: Int, targetPosY: Int, wire: String) {
        (currentPosY..targetPosY).forEach { subsequentPosY ->
            val p = Point(currentPosX, subsequentPosY)
            val wires = visitedPoints[p]?.apply { add(wire) } ?: mutableSetOf(wire)
            visitedPoints.putIfAbsent(p, wires)
        }
    }

    fun markIntersectedPointsHorizontally(currentPosX: Int, currentPosY: Int, targetPosX: Int, wire: String) {
        (currentPosX..targetPosX).forEach { subsequentPosX ->
            val p = Point(subsequentPosX, currentPosY)
            val wires = visitedPoints[p]?.apply { add(wire) } ?: mutableSetOf(wire)
            visitedPoints.putIfAbsent(p, wires)
        }
    }

    fun applyWire(wirePaths: List<String>, wire: String) {
        var currentPosX = 0
        var currentPosY = 0
        wirePaths.forEach { wirePath ->
            val offset = wirePath.trim().substring(1).toInt()
            when (wirePath[0]) {
                'U' -> {
                    val targetPosY = currentPosY + offset
                    markIntersectedPointsVertically(currentPosX, currentPosY, targetPosY, wire)
                    currentPosY += offset
                }
                'D' -> {
                    val targetPosY = currentPosY - offset
                    markIntersectedPointsVertically(currentPosX, currentPosY, targetPosY, wire)
                    currentPosY -= offset
                }
                'L' -> {
                    val targetPosX = currentPosX - offset
                    markIntersectedPointsHorizontally(currentPosX, currentPosY, targetPosX, wire)
                    currentPosX -= offset
                }
                'R' -> {
                    val targetPosX = currentPosX + offset
                    markIntersectedPointsHorizontally(currentPosX, currentPosY, targetPosX, wire)
                    currentPosX += offset
                }
            }
        }
    }

    applyWire(firstWirePath, FIRST_WIRE)
    applyWire(secondWirePath, SECOND_WIRE)

    val intersectionPoints = visitedPoints.filterValues { it.size > 1 }.keys

    val closestDistanceFromCentral = intersectionPoints.minBy { abs(it.x) + abs(it.y) }!!

    println(closestDistanceFromCentral)

    closestDistanceFromCentral.let { println(abs(it.x) + abs(it.y)) }
}

