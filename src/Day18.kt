import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    fun parse(input: List<String>) =
        input.map { Json.parseToJsonElement(it) }

    fun JsonElement.toBinaryNode(parent: BinaryNode<Int>? = null): BinaryNode<Int> =
        try {
            BinaryNode(parent = parent, value = this.jsonPrimitive.intOrNull)
        } catch (e: IllegalArgumentException) {
            val jsonArray = this.jsonArray
            BinaryNode(parent = parent).apply {
                leftChild = jsonArray.first().toBinaryNode(parent = this)
                rightChild = jsonArray.last().toBinaryNode(parent = this)
            }
        }

    fun BinaryNode<Int>.addToLeft() {
        var currentParent: BinaryNode<Int>? = parent
        var currentNode: BinaryNode<Int>? = this
        while (currentParent != null) {
            if (currentParent.leftChild !== currentNode) break
            currentNode = currentParent
            currentParent = currentParent.parent
        }
        if (currentParent?.leftChild != null) {
            val leftSibling = currentParent.leftChild
            if (leftSibling?.value != null) {
                leftSibling.value = leftSibling.value!!.plus(
                    this.leftChild!!.value!!
                )
            } else {
                var rightChildOfLeftSibling = leftSibling?.rightChild
                while (rightChildOfLeftSibling != null) {
                    if (rightChildOfLeftSibling.value != null) break
                    rightChildOfLeftSibling = rightChildOfLeftSibling.rightChild
                }
                rightChildOfLeftSibling!!.value = rightChildOfLeftSibling.value!!.plus(
                    this.leftChild!!.value!!
                )
            }
        }
    }

    fun BinaryNode<Int>.addToRight() {
        var currentParent: BinaryNode<Int>? = parent
        var currentNode: BinaryNode<Int>? = this
        while (currentParent != null) {
            if (currentParent!!.rightChild !== currentNode) break
            currentNode = currentParent
            currentParent = currentParent!!.parent
        }
        if (currentParent?.rightChild != null) {
            val rightSibling = currentParent!!.rightChild
            if (rightSibling?.value != null) {
                rightSibling.value = rightSibling.value!!.plus(
                    this.rightChild!!.value!!
                )
            } else {
                var leftChildOfRightSibling = rightSibling?.leftChild
                while (leftChildOfRightSibling != null) {
                    if (leftChildOfRightSibling.value != null) break
                    leftChildOfRightSibling = leftChildOfRightSibling.leftChild
                }
                leftChildOfRightSibling!!.value = leftChildOfRightSibling.value!!.plus(
                    this.rightChild!!.value!!
                )
            }
        }
    }

    fun BinaryNode<Int>.explode() {
        this.addToLeft()
        this.addToRight()

        this.leftChild = null
        this.rightChild = null
        this.value = 0
    }

    fun BinaryNode<Int>.split() {
        this.leftChild = BinaryNode(
            parent = this,
            value = floor(this.value!! / 2.0).toInt(),
        )
        this.rightChild = BinaryNode(
            parent = this,
            value = ceil(this.value!! / 2.0).toInt(),
        )
        this.value = null
    }

    fun BinaryNode<Int>.nestedPair(): BinaryNode<Int>? {
        if (this.parent?.parent?.parent?.parent != null && this.value == null) return this
        leftChild?.nestedPair()?.let { return it }
        rightChild?.nestedPair()?.let { return it }
        return null
    }

    fun BinaryNode<Int>.largeValue(): BinaryNode<Int>? {
        this.value?.let { if (it >= 10) return this }
        leftChild?.largeValue()?.let { return it }
        rightChild?.largeValue()?.let { return it }
        return null
    }

    fun BinaryNode<Int>.reduce() {
        this.nestedPair()?.let {
            it.explode()
            this.reduce()
        }

        this.largeValue()?.let {
            it.split()
            this.reduce()
        }
    }

    fun BinaryNode<Int>.magnitude(): Int {
        if (value != null) return value!!
        return 3 * (leftChild?.magnitude() ?: 0) + 2 * (rightChild?.magnitude() ?: 0)
    }

    fun part1(input: List<String>): Int {
        val inputRows = parse(input)
        var result = BinaryNode<Int>()

        for ((i, inputRow) in inputRows.withIndex()) {
            if (i == 0) continue
            val newParent = BinaryNode<Int>().apply {
                leftChild = if (i == 1) inputRows.first().toBinaryNode(parent = this) else {
                    result.parent = this
                    result
                }
                rightChild = inputRow.toBinaryNode(parent = this)
            }
            newParent.reduce()
            result = newParent
        }

        return result.magnitude()
    }

    fun part2(input: List<String>): Int {
        val inputRows = parse(input)
        var maxMagnitude = -1

        for (inputRow1 in inputRows) {
            for (inputRow2 in inputRows) {
                val root = BinaryNode<Int>().apply {
                    leftChild = inputRow1.toBinaryNode(parent = this)
                    rightChild = inputRow2.toBinaryNode(parent = this)
                }
                root.reduce()
                val magnitude = root.magnitude()
                if (magnitude > maxMagnitude) maxMagnitude = magnitude
            }
        }

        return maxMagnitude
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
