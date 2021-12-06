import kotlin.math.pow

fun main() {
    fun parse(input: List<String>) =
        input[0].trim().split(",").map { it.toInt() }

    fun produceFishLantern(input: List<Int>, days: Int): MutableList<Int> {
        var fishTimers = input.toMutableList()
        for (day in 1..days) {
            val newFishTimers = mutableListOf<Int>()
            for (fishTimer in fishTimers) {
                when (fishTimer) {
                    0 -> newFishTimers.addAll(listOf(6, 8))
                    else -> newFishTimers.add(fishTimer - 1)
                }
            }

            fishTimers = newFishTimers
        }
        return fishTimers
    }

    fun produceFishLantern2(input: List<Int>, days: Int): MutableMap<Int, Long> {
        var fishTimerMap = mutableMapOf<Int, Long>()

        for (fishTimer in input) {
            fishTimerMap[fishTimer] = fishTimerMap.getOrPut(fishTimer) { 0 } + 1
        }

        for (day in 1..days) {
            val newFishTimerMap = mutableMapOf<Int, Long>()
            newFishTimerMap[8] = fishTimerMap.getOrPut(0) { 0 }
            newFishTimerMap[6] = fishTimerMap.getOrPut(0) { 0 }
            for (t in 1..8) {
                newFishTimerMap[t - 1] = fishTimerMap.getOrPut(t) { 0 } + newFishTimerMap.getOrPut(t - 1) { 0 }
            }
            fishTimerMap = newFishTimerMap
        }

        return fishTimerMap
    }

    fun part1(input: List<String>): Int {
        return produceFishLantern(parse(input), 80).size
    }

    fun part2(input: List<String>): Long {
        return produceFishLantern2(parse(input), 256).values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934)
    check(part2(testInput) == 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
