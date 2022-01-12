import java.util.PriorityQueue

private enum class Amphipod(val energyUsage: Int) {
    A(1),
    B(10),
    C(100),
    D(1000)
}

private data class Burrow(val positions: List<MutableList<Char>>) {
    private val neighboursMap = mutableMapOf<MatrixPosition, Set<MatrixPosition>>()
    private val amphipodPositions = mutableMapOf(
        Amphipod.A to mutableListOf<MatrixPosition>(),
        Amphipod.B to mutableListOf(),
        Amphipod.C to mutableListOf(),
        Amphipod.D to mutableListOf(),
    )
    private val amphipodRooms = mapOf(
        Amphipod.A to listOf(
            MatrixPosition(2, 3),
            MatrixPosition(3, 3),
            MatrixPosition(4, 3),
            MatrixPosition(5, 3)
        ),
        Amphipod.B to listOf(
            MatrixPosition(2, 5),
            MatrixPosition(3, 5),
            MatrixPosition(4, 5),
            MatrixPosition(5, 5),
        ),
        Amphipod.C to listOf(
            MatrixPosition(2, 7),
            MatrixPosition(3, 7),
            MatrixPosition(4, 7),
            MatrixPosition(5, 7),
        ),
        Amphipod.D to listOf(
            MatrixPosition(2, 9),
            MatrixPosition(3, 9),
            MatrixPosition(4, 9),
            MatrixPosition(5, 9),
        ),
    )
    private val intermediatePositions = listOf(
        MatrixPosition(1, 1),
        MatrixPosition(1, 2),
        MatrixPosition(1, 4),
        MatrixPosition(1, 6),
        MatrixPosition(1, 8),
        MatrixPosition(1, 10),
        MatrixPosition(1, 11),
    )

    init {
        for (row in positions.indices) {
            for (col in positions[row].indices) {
                val char = positions[row][col]
                if (char in listOf('A', 'B', 'C', 'D')) {
                    val amphipod = Amphipod.valueOf(char.toString())
                    val position = MatrixPosition(row, col)
                    amphipodPositions[amphipod]!!.add(position)
                }
                if (char in listOf('A', 'B', 'C', 'D', '.')) {
                    val position = MatrixPosition(row, col)
                    val neighbours = mutableSetOf<MatrixPosition>()
                    val up = position.north()
                    if (positions[up.row][up.col] == '.') neighbours.add(up)
                    val down = position.south()
                    if (positions[down.row][down.col] == '.') neighbours.add(down)
                    val left = position.west()
                    if (positions[left.row][left.col] == '.') neighbours.add(left)
                    val right = position.east()
                    if (positions[right.row][right.col] == '.') neighbours.add(right)
                    neighboursMap[position] = neighbours
                }
            }
        }
    }

    fun nextBurrows() =
        Amphipod.values().flatMap { amphipod ->
            val nextBurrows = nextBurrows(amphipod)
            nextBurrows.firstOrNull { it.third }?.let { return listOf(it) }
            nextBurrows
        }

    private fun nextBurrows(amphipodToMove: Amphipod) =
        amphipodPositions[amphipodToMove]!!.flatMap { position ->
            if (isFinalPosition(amphipodToMove, position)) emptyList() else nextBurrows(amphipodToMove, position)
        }

    private fun nextBurrows(amphipodToMove: Amphipod, position: MatrixPosition): List<Triple<Burrow, Long, Boolean>> {
        val neighboursMapForAmphipod = neighboursMap.mapValues {
            it.value.map { neighbour -> neighbour.copy().apply { weight = amphipodToMove.energyUsage } }.toSet()
        }

        val finalPositions = amphipodRooms[amphipodToMove]!!
        finalPositions.reversed().map {
            val costToFinalPosition = AStarSearch(
                graph = neighboursMapForAmphipod,
                start = position,
                goal = it,
                h = ::manhattanDistance
            )
            if (costToFinalPosition > 0 && isFinalPosition(amphipodToMove, it)) return listOf(
                Triple(moveAmphipod(position, it), costToFinalPosition.toLong(), true)
            )
        }

        return if (position in intermediatePositions) emptyList() else intermediatePositions.map {
            it to AStarSearch(graph = neighboursMapForAmphipod, start = position, goal = it, h = ::manhattanDistance)
        }.filter { it.second > 0 }.map { Triple(moveAmphipod(position, it.first), it.second.toLong(), false) }
    }

    fun moveAmphipod(from: MatrixPosition, to: MatrixPosition): Burrow {
        require(positions[from.row][from.col] in listOf('A', 'B', 'C', 'D')) { "$from -> $to | $this" }
        require(positions[to.row][to.col] == '.') { "$from -> $to | $this" }
        val newPositions = positions.map { it.toMutableList() }.toList()
        newPositions[from.row][from.col] = '.'
        newPositions[to.row][to.col] = positions[from.row][from.col]
        return Burrow(newPositions)
    }

    fun isFinal() =
        amphipodPositions.all { (amphipod, positions) -> positions.all { isFinalPosition(amphipod, it) } }

    fun isFinalPosition(amphipod: Amphipod, position: MatrixPosition): Boolean {
        if (position !in amphipodRooms[amphipod]!!) return false
        var south = position.south()
        while (positions[south.row][south.col] != '#') {
            if (positions[south.row][south.col].toString() != amphipod.toString()) return false
            south = south.south()
        }
        return true
    }
}

private data class State(val energyUsed: Long, val isFinal: Boolean, val burrow: Burrow) {
    fun nextStates() =
        if (isFinal) emptyList()
        else burrow.nextBurrows().map {
            State(
                energyUsed = energyUsed + it.second,
                isFinal = it.first.isFinal(),
                burrow = it.first,
            )
        }
}

fun main() {
    fun parse(input: List<String>) =
        State(energyUsed = 0, isFinal = false, burrow = Burrow(input.map { it.toMutableList() }))

    fun State.getFinalStateWithMinimalEnergyUsed(): State {
        val queue = PriorityQueue<State> { s1, s2 -> s1.energyUsed.compareTo(s2.energyUsed) }
        queue.addAll(nextStates())
        val visitedBurrows = mutableSetOf<Burrow>()

        while (queue.isNotEmpty()) {
            val currentState = queue.poll()
            if (visitedBurrows.contains(currentState.burrow)) continue
            visitedBurrows.add(currentState.burrow)
            // println("" + currentState.energyUsed + "\t" + queue.size)
            if (currentState.isFinal) {
                return currentState
            }
            queue.addAll(currentState.nextStates())
        }

        throw IllegalStateException("Did not find final state!")
    }

    fun part1(input: List<String>): Long {
        val initialState = parse(input)
        val finalState = initialState.getFinalStateWithMinimalEnergyUsed()
        return finalState.energyUsed
    }

    fun part2(input: List<String>): Long {
        val initialState = parse(input)
        val finalState = initialState.getFinalStateWithMinimalEnergyUsed()
        return finalState.energyUsed
    }

    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("Day23_test_1")
    check(part1(testInputPart1) == 12521L)
    val testInputPart2 = readInput("Day23_test_2")
    check(part2(testInputPart2) == 44169L)

    val input1 = readInput("Day23_1")
    println(part1(input1))
    val input2 = readInput("Day23_2")
    println(part2(input2))
}
