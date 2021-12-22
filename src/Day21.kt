class DeterministicDice {
    private var lastValue = 0

    fun roll(n: Int) = sequence {
        for (i in 1..n) {
            val newValue = (lastValue % 100) + 1
            lastValue = newValue
            yield(newValue)
        }
    }
}

fun main() {
    fun parse(input: List<String>) =
        input.map { it.split(Regex("Player \\d* starting position: "))[1].toInt() }

    fun play(playerPositions: MutableList<Int>): Pair<List<Int>, Int> {
        var playerScores = mutableListOf(0, 0)
        var rolledCount = 0
        val dice = DeterministicDice()

        outer@ while (true) {
            for (player in playerPositions.indices) {
                val currentPosition = playerPositions[player]
                val foldedValue = dice.roll(3).fold(currentPosition) { acc, rolled -> acc + rolled } % 10
                val newPosition = if (foldedValue == 0) 10 else foldedValue
                playerScores[player] += newPosition
                playerPositions[player] = newPosition
                rolledCount += 3
                if (playerScores[player] >= 1000) break@outer
            }
        }

        return playerScores to rolledCount
    }

    data class State(
        val playerPositions: List<Int>,
        val playerScores: List<Int>,
        val player: Int,
    )

    val cache = mutableMapOf<State, Pair<Long, Long>>()
    fun play2(playerPositions: List<Int>, playerScores: List<Int>, player: Int): Pair<Long, Long> {
        if (playerScores[0] >= 21) return 1L to 0L
        if (playerScores[1] >= 21) return 0L to 1L
        var universesWon = 0L to 0L
        val currentPosition = playerPositions[player]
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    val newPlayerScores = playerScores.toMutableList()
                    val newPlayerPositions = playerPositions.toMutableList()
                    val foldedValue = (currentPosition + i + j + k) % 10
                    val newPosition = if (foldedValue == 0) 10 else foldedValue
                    newPlayerScores[player] += newPosition
                    newPlayerPositions[player] = newPosition
                    val state = State(newPlayerPositions, newPlayerScores, player)
                    val newUniversesWon =
                        cache.getOrPut(state) { play2(newPlayerPositions, newPlayerScores, if (player == 0) 1 else 0) }
                    universesWon =
                        universesWon.first + newUniversesWon.first to universesWon.second + newUniversesWon.second
                }
            }
        }
        return universesWon
    }

    fun part1(input: List<String>): Int {
        val playerPositions = parse(input).toMutableList()
        val (playerScores, rolledCount) = play(playerPositions)
        return playerScores.minOrNull()!! * rolledCount
    }

    fun part2(input: List<String>): Long {
        val playerPositions = parse(input)
        val playerScores = listOf(0, 0)
        val universesWon = play2(playerPositions, playerScores, 0)
        return if (universesWon.first > universesWon.second) universesWon.first else universesWon.second
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
