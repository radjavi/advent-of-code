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

data class MatrixPosition(val row: Int, val col: Int) {
    fun north() = MatrixPosition(row - 1, col)
    fun south() = MatrixPosition(row + 1, col)
    fun west() = MatrixPosition(row, col - 1)
    fun east() = MatrixPosition(row, col + 1)

    fun northeast() = MatrixPosition(row - 1, col + 1)
    fun southeast() = MatrixPosition(row + 1, col + 1)
    fun southwest() = MatrixPosition(row + 1, col - 1)
    fun northwest() = MatrixPosition(row - 1, col - 1)
}