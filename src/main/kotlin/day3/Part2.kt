package day3

import java.io.File


data class WireSteps(val wire: String, val steps: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WireSteps

        if (wire != other.wire) return false

        return true
    }

    override fun hashCode(): Int {
        return wire.hashCode()
    }
}

fun main() {
    val (firstWirePath, secondWirePath) = File("src/main/resources/day3.txt")
        .readText()
        .split("\n")
        .map { it.split(",") }

    val visitedPoints = mutableMapOf<Point, MutableSet<WireSteps>>()

    fun visitWirePath(currentPos: Int, targetPos: Int, wire: String, steps: Int, createPoint: (Int) -> Point) {
        val range = if (targetPos > currentPos) (currentPos..targetPos) else (currentPos downTo targetPos)
        range.forEachIndexed { index, subsequentPos ->
            val p = createPoint(subsequentPos)
            val currSteps = steps + index
            val wireSteps = WireSteps(wire, currSteps)
            val wires = visitedPoints[p]?.apply { add(wireSteps) } ?: mutableSetOf(wireSteps)
            visitedPoints.putIfAbsent(p, wires)
        }
    }

    fun applyWire(wirePaths: List<String>, wire: String) {
        var currentPosX = 0
        var currentPosY = 0
        var steps = 0
        wirePaths.forEach { wirePath ->
            val offset = wirePath.trim().substring(1).toInt()
            when (wirePath[0]) {
                'U' -> {
                    val targetPosY = currentPosY + offset
                    visitWirePath(currentPosY, targetPosY, wire, steps) { subsequentPosY ->
                        Point(currentPosX, subsequentPosY)
                    }
                    currentPosY += offset
                }
                'D' -> {
                    val targetPosY = currentPosY - offset
                    visitWirePath(currentPosY, targetPosY, wire, steps) { subsequentPosY ->
                        Point(currentPosX, subsequentPosY)
                    }
                    currentPosY -= offset
                }
                'L' -> {
                    val targetPosX = currentPosX - offset
                    visitWirePath(currentPosX, targetPosX, wire, steps) { subsequentPosX ->
                        Point(subsequentPosX, currentPosY)
                    }
                    currentPosX -= offset
                }
                'R' -> {
                    val targetPosX = currentPosX + offset
                    visitWirePath(currentPosX, targetPosX, wire, steps) { subsequentPosX ->
                        Point(subsequentPosX, currentPosY)
                    }
                    currentPosX += offset
                }
            }
            steps += offset
        }
    }

    applyWire(firstWirePath, FIRST_WIRE)
    applyWire(secondWirePath, SECOND_WIRE)

    val intersectionPoints = visitedPoints
        .filterKeys { it.x != 0 || it.y != 0 }
        .filterValues { it.size > 1 }.values

    val lowestStepsIntersection = intersectionPoints.minBy { it.sumBy { it.steps } }!!

    println(lowestStepsIntersection.sumBy { it.steps })

}