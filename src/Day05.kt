data class Coordinate(val x: Int, val y: Int)

fun parse(input: List<String>) =
    input.map { it.split(" -> ") }
        .map { it[0].split(",") to it[1].split(",") }
        .map { (p1, p2) -> Coordinate(p1[0].toInt(), p1[1].toInt()) to Coordinate(p2[0].toInt(), p2[1].toInt()) }

fun main() {
    fun part1(input: List<String>): Int {
        val coordinatePairs = parse(input)
        val coordinatesCovered = mutableMapOf<Coordinate, Int>()

        for ((p1, p2) in coordinatePairs) {
            if (p1.x == p2.x) {
                val startY = if (p1.y <= p2.y) p1.y else p2.y
                val endY = if (p1.y <= p2.y) p2.y else p1.y
                for (y in startY..endY) {
                    val current = Coordinate(p1.x, y)
                    coordinatesCovered[current] = coordinatesCovered.getOrPut(current) { 0 } + 1
                }
            }
            else if (p1.y == p2.y) {
                val startX = if (p1.x <= p2.x) p1.x else p2.x
                val endX = if (p1.x <= p2.x) p2.x else p1.x
                for (x in startX..endX) {
                    val current = Coordinate(x, p1.y)
                    coordinatesCovered[current] = coordinatesCovered.getOrPut(current) { 0 } + 1
                }
            }
        }

        return coordinatesCovered.values.count { it > 1 }
    }

    fun part2(input: List<String>): Int {
        val coordinatePairs = parse(input)
        val coordinatesCovered = mutableMapOf<Coordinate, Int>()

        for ((p1, p2) in coordinatePairs) {
            if (p1.x == p2.x) {
                val startY = if (p1.y <= p2.y) p1.y else p2.y
                val endY = if (p1.y <= p2.y) p2.y else p1.y
                for (y in startY..endY) {
                    val current = Coordinate(p1.x, y)
                    coordinatesCovered[current] = coordinatesCovered.getOrPut(current) { 0 } + 1
                }
            }
            else if (p1.y == p2.y) {
                val startX = if (p1.x <= p2.x) p1.x else p2.x
                val endX = if (p1.x <= p2.x) p2.x else p1.x
                for (x in startX..endX) {
                    val current = Coordinate(x, p1.y)
                    coordinatesCovered[current] = coordinatesCovered.getOrPut(current) { 0 } + 1
                }
            }
            else {
                val start = if (p1.x <= p2.x) p1 else p2
                val end = if (p1.x <= p2.x) p2 else p1
                val xDir = if (end.x - start.x < 0) -1 else 1
                val yDir = if (end.y - start.y < 0) -1 else 1
                for (i in 0..(end.x - start.x)) {
                    val current = Coordinate(start.x + i * xDir, start.y + i * yDir)
                    coordinatesCovered[current] = coordinatesCovered.getOrPut(current) { 0 } + 1
                }
            }
        }

        return coordinatesCovered.values.count { it > 1 }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
