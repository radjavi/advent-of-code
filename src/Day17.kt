import kotlin.math.max

fun main() {
    data class Velocity(val x: Int, val y: Int)

    data class Position(val x: Int, val y: Int) {
        fun next(velocity: Velocity) =
            Position(x + velocity.x, y + velocity.y)
    }

    fun parse(input: List<String>) =
        input[0].split("target area: ")[1]
            .split(", ")
            .map { it.split("=")[1] }
            .let { (xString, yString) ->
                val xs = xString.split("..").map { it.toInt() }
                val ys = yString.split("..").map { it.toInt() }
                val minX = xs.minByOrNull { it }!!
                val maxX = xs.maxByOrNull { it }!!
                val minY = ys.minByOrNull { it }!!
                val maxY = ys.maxByOrNull { it }!!
                val targetArea = mutableSetOf<Position>()
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        targetArea.add(Position(x, y))
                    }
                }
                targetArea
            }

    fun part1(input: List<String>): Int {
        val targetArea = parse(input)
        val maxX = targetArea.maxByOrNull { it.x }!!.x
        val minY = targetArea.minByOrNull { it.y }!!.y

        var highest = -1

        for (x in 1..maxX) {
            for (y in 1..5000) {
                var current = Position(0, 0)
                var velocity = Velocity(x, y)
                var currentHighest = -1
                while (true) {
                    if (current.y > currentHighest) currentHighest = current.y
                    if (current.x > maxX || current.y < minY) break
                    if (targetArea.contains(current)) {
                        if (currentHighest > highest) highest = currentHighest
                        break
                    }
                    current = current.next(velocity)
                    velocity = Velocity(max(velocity.x - 1, 0), velocity.y - 1)
                }
            }
        }

        return highest
    }

    fun part2(input: List<String>): Int {
        val targetArea = parse(input)
        val maxX = targetArea.maxByOrNull { it.x }!!.x
        val minY = targetArea.minByOrNull { it.y }!!.y

        var distinct = mutableSetOf<Velocity>()

        for (x in 1..maxX) {
            for (y in minY..1000) {
                var current = Position(0, 0)
                var velocity = Velocity(x, y)
                var currentHighest = -1
                while (true) {
                    if (current.y > currentHighest) currentHighest = current.y
                    if (current.x > maxX || current.y < minY) break
                    if (targetArea.contains(current)) {
                        distinct.add(Velocity(x, y))
                        break
                    }
                    current = current.next(velocity)
                    velocity = Velocity(max(velocity.x - 1, 0), velocity.y - 1)
                }
            }
        }

        return distinct.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}
