import java.util.Stack

fun main() {
    fun String.containsSyntaxError(): Char? {
        val stack = Stack<Char>()
        for (char in this) {
            if (char in listOf('(', '[', '{', '<')) stack.push(char)
            else {
                val popped = stack.pop()
                when (char) {
                    ')' -> if (popped == '(') continue
                    ']' -> if (popped == '[') continue
                    '}' -> if (popped == '{') continue
                    '>' -> if (popped == '<') continue
                }
                return char
            }
        }
        return null
    }

    fun part1(input: List<String>): Int {
        var points = mapOf(
            ')' to 3,
            ']' to 57,
            '}' to 1197,
            '>' to 25137
        )
        var score = 0

        for (line in input) {
            line.containsSyntaxError()?.let {
                score += points[it]!!
            }
        }

        return score
    }

    fun part2(input: List<String>): Long {
        val points = mapOf(
            '(' to 1,
            '[' to 2,
            '{' to 3,
            '<' to 4
        )
        val scores = mutableListOf<Long>()

        for (line in input) {
            if (line.containsSyntaxError() != null) continue
            val stack = Stack<Char>()
            var lineScore = 0L
            for (char in line) {
                if (char in listOf('(', '[', '{', '<')) stack.push(char)
                else stack.pop()
            }
            while (stack.isNotEmpty()) {
                val popped = stack.pop()
                lineScore = lineScore * 5 + points[popped]!!
            }
            scores.add(lineScore)
        }

        return scores.sorted()[scores.size / 2]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
