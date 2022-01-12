fun main() {
    fun parse(input: List<String>) =
        input.map { it.map { it.digitToInt() } }

    fun getIncrementedSubMatrix(subMatrix: List<List<Int>>) =
        subMatrix.map { row -> row.map { if (it == 9) 1 else it + 1 } }

    fun createBiggerInput(original: List<List<Int>>): List<List<Int>> {
        val incrementedRight = (1..4).fold(original) { bigger, _ ->
            val subMatrix = bigger.map { it.subList(bigger[0].lastIndex - original[0].lastIndex, bigger[0].lastIndex + 1) }
                .subList(0, original.lastIndex + 1)
            val incremented = getIncrementedSubMatrix(subMatrix)
            val biggerPlusIncremented = bigger.map { it.toMutableList() }.toMutableList()
            for (row in incremented.indices) {
                for (col in incremented[row].indices) {
                    biggerPlusIncremented[row].add(incremented[row][col])
                }
            }
            biggerPlusIncremented
        }

        val incrementedDown = (1..4).fold(incrementedRight) { bigger, _ ->
            val subMatrix = bigger.map { it.subList(0, bigger[0].size) }
                .subList(bigger.lastIndex - original.lastIndex, bigger.lastIndex + 1)
            val incremented = getIncrementedSubMatrix(subMatrix)
            val biggerPlusIncremented = bigger.toMutableList()
            for (row in incremented.indices) {
                biggerPlusIncremented.add(incremented[row])
            }
            biggerPlusIncremented
        }
        return incrementedDown
    }

    fun buildGraph(input: List<List<Int>>): MutableMap<MatrixPosition, Set<MatrixPosition>> {
        val graph = mutableMapOf<MatrixPosition, Set<MatrixPosition>>()
        for (row in input.indices) {
            for (col in input[row].indices) {
                val current = MatrixPosition(row, col).apply { weight = input[this.row][this.col] }
                val potentialNeighbours = setOf(
                    current.north(),
                    current.south(),
                    current.west(),
                    current.east(),
                )
                val neighbours = potentialNeighbours.mapNotNull {
                    if (it.row in input.indices && it.col in input[it.row].indices) it else null
                }.map { it.apply { weight = input[this.row][this.col] } }.toSet()
                graph[current] = neighbours
            }
        }
        return graph
    }

    fun getLowestRisk(input: List<List<Int>>): Int {
        val graph = buildGraph(input)

        val start = MatrixPosition(0, 0).apply { weight = input[this.row][this.col] }
        val goal = MatrixPosition(input.lastIndex, input[input.lastIndex].lastIndex).apply {
            weight = input[this.row][this.col]
        }

        return AStarSearch(graph, start, goal, ::manhattanDistance)
    }

    fun part1(input: List<String>): Int {
        return getLowestRisk(parse(input))
    }

    fun part2(input: List<String>): Int {
        return getLowestRisk(createBiggerInput(parse(input)))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
