import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.PriorityQueue
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

data class Coordinate(val x: Int, val y: Int)
data class Coordinate3D(val x: Int, val y: Int, val z: Int)

data class MatrixPosition(val row: Int, val col: Int) {
    var weight = 0

    fun north() = MatrixPosition(row - 1, col)
    fun south() = MatrixPosition(row + 1, col)
    fun west() = MatrixPosition(row, col - 1)
    fun east() = MatrixPosition(row, col + 1)

    fun northeast() = MatrixPosition(row - 1, col + 1)
    fun southeast() = MatrixPosition(row + 1, col + 1)
    fun southwest() = MatrixPosition(row + 1, col - 1)
    fun northwest() = MatrixPosition(row - 1, col - 1)
}

data class BinaryNode<Int>(
    var parent: BinaryNode<Int>? = null,
    var value: Int? = null,
    var leftChild: BinaryNode<Int>? = null,
    var rightChild: BinaryNode<Int>? = null,
) {
    override fun toString(): String {
        return if (value != null) value.toString() else "[${leftChild?.toString() ?: ""}${rightChild?.let { ", $it" } ?: ""}]"
    }
}

data class Range(val from: Int, val to: Int) {
    init {
        require(from <= to)
    }
    val size = (to - from) + 1L

    fun intersect(other: Range): Range? {
        if (to < other.from || other.to < from) return null
        val sortedPoints = listOf(from, to, other.from, other.to).sorted()
        return Range(sortedPoints[1], sortedPoints[2])
    }
}
data class Cuboid(val x: Range, val y: Range, val z: Range) {
    val size = x.size * y.size * z.size

    fun intersect(other: Cuboid): Cuboid? {
        val xIntersection = x.intersect(other.x)
        val yIntersection = y.intersect(other.y)
        val zIntersection = z.intersect(other.z)
        if (listOf(xIntersection, yIntersection, zIntersection).any { it == null }) return null
        return Cuboid(xIntersection!!, yIntersection!!, zIntersection!!)
    }
}

fun manhattanDistance(position: MatrixPosition, goal: MatrixPosition) =
    abs(position.col - goal.col) + abs(position.row - goal.row)

fun AStarSearch(
    graph: Map<MatrixPosition, Set<MatrixPosition>>,
    start: MatrixPosition,
    goal: MatrixPosition,
    h: (MatrixPosition, MatrixPosition) -> Int
): Int {
    val gScore = mutableMapOf(
        start to 0
    ).withDefault { Int.MAX_VALUE }

    val fScore = mutableMapOf(
        start to h(start, goal)
    ).withDefault { Int.MAX_VALUE }

    val openSet = PriorityQueue<MatrixPosition>(compareBy { fScore[it]!! })
    openSet.add(start)

    while (openSet.isNotEmpty()) {
        val current = openSet.poll()
        if (current == goal) return gScore.getValue(current)

        for (neighbour in graph[current]!!) {
            val tentativeGScore = gScore.getValue(current) + neighbour.weight
            if (tentativeGScore < gScore.getValue(neighbour)) {
                gScore[neighbour] = tentativeGScore
                fScore[neighbour] = tentativeGScore + h(neighbour, goal)
                if (!openSet.contains(neighbour)) openSet.add(neighbour)
            }
        }
    }

    return -1
}