import java.lang.Math.abs

fun main() {
    fun parse(input: List<String>): List<Int> =
        input[0].trim().split(",").map { it.toInt() }

    fun part1(input: List<String>): Int {
        val positions = parse(input)
        val min = positions.minOrNull()!!
        val max = positions.maxOrNull()!!

        var minTotalFuelCost = Int.MAX_VALUE
        for (outcome in min..max) {
            var totalFuelCost = 0
            for (position in positions) {
                totalFuelCost += kotlin.math.abs(outcome - position)
            }
            if (totalFuelCost < minTotalFuelCost) {
                minTotalFuelCost = totalFuelCost
            }
        }

        return minTotalFuelCost
    }

    fun part2(input: List<String>): Int {
        val positions = parse(input)
        val min = positions.minOrNull()!!
        val max = positions.maxOrNull()!!

        var minTotalFuelCost = Int.MAX_VALUE
        for (outcome in min..max) {
            var totalFuelCost = 0
            for (position in positions) {
                val n = kotlin.math.abs(outcome - position)
                totalFuelCost += (n * (n+1)) / 2
            }
            if (totalFuelCost < minTotalFuelCost) {
                minTotalFuelCost = totalFuelCost
            }
        }

        return minTotalFuelCost
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
