fun main() {
    fun part1(input: List<String>): Int {
        var increases = 0
        for (i in 1 until input.size) {
            if (input[i].toInt() > input[i-1].toInt()) increases++
        }
        return increases
    }

    fun part2(input: List<String>): Int {
        val list = mutableListOf<List<Int>>()
        for (i in 0..input.size-3) {
            list.add(
                listOf(
                    input[i].toInt(),
                    input[i+1].toInt(),
                    input[i+2].toInt(),
                )
            )
        }
        val sums = list.map { it.sum().toString() }
        return part1(sums)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
