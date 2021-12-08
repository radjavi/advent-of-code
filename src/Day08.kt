fun main() {
    fun parse(input: List<String>) =
        input.map { it.split(" | ") }
            .map { Pair(it.first().split(" "), it.last().split(" ")) }

    fun part1(input: List<String>): Int {
        val secondParts = parse(input).map { it.second }

        var count = 0
        for (secondPart in secondParts) {
            for (digit in secondPart) {
                if (digit.length in listOf(2, 3, 4, 7)) count += 1
            }
        }

        return count
    }

    fun part2(input: List<String>): Int {
        val pairs = parse(input)
        var sum = 0

        val digitToPattern = mutableMapOf<Int, Set<Char>>()
        for (pair in pairs) {
            for (pattern in pair.first.sortedBy { it.length }) {
                var digit = when (pattern.length) {
                    2 -> 1
                    3 -> 7
                    4 -> 4
                    6 -> {
                        if (!pattern.toSet().containsAll(digitToPattern[1]!!)) 6
                        else if (pattern.toSet().containsAll(digitToPattern[4]!!)) 9
                        else 0
                    }
                    7 -> 8
                    else -> -1
                }
                if (digit > -1) digitToPattern[digit] = pattern.toSet()
            }
            for (pattern in pair.first.filter { it.length == 5 }) {
                if (pattern.toSet().containsAll(digitToPattern[1]!!)) digitToPattern[3] = pattern.toSet()
                else if (digitToPattern[6]!!.containsAll(pattern.toSet())) digitToPattern[5] = pattern.toSet()
                else digitToPattern[2] = pattern.toSet()
            }
            var number = ""
            for (output in pair.second) {
                val digit = digitToPattern.firstNotNullOf { (d, p) -> if (output.toSet() == p) d else null }
                number += digit
            }
            sum += number.toInt()
        }

        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
