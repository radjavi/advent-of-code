import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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