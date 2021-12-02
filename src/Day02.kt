fun main() {
    fun part1(input: List<String>): Int {
        var horizontal = 0
        var depth = 0
        for (i in input) {
            val splitted = i.split(" ")
            if (splitted[0] == "forward") horizontal += splitted[1].toInt()
            else if (splitted[0] == "down") depth += splitted[1].toInt()
            else depth -= splitted[1].toInt()
        }
        return horizontal * depth
    }

    fun part2(input: List<String>): Int {
        var horizontal = 0
        var depth = 0
        var aim = 0
        for (i in input) {
            val splitted = i.split(" ")
            if (splitted[0] == "forward") {
                horizontal += splitted[1].toInt()
                depth += aim * splitted[1].toInt()
            }
            else if (splitted[0] == "down") aim += splitted[1].toInt()
            else aim -= splitted[1].toInt()
        }
        return horizontal * depth
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
