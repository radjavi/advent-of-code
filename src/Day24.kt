import java.util.concurrent.atomic.AtomicInteger

private class ALU {
    var w = AtomicInteger(0)
    var x = AtomicInteger(0)
    var y = AtomicInteger(0)
    var z = AtomicInteger(0)

    private var inputCount = 1

    fun executeInstruction(instruction: String) {
        val splitted = instruction.split(" ")
        when (splitted[0]) {
            "inp" -> {
                print("Enter value for ${splitted[1]}_$inputCount: ")
                stringToVariable(splitted[1]).set(readLine()!!.toInt())
                inputCount++
            }
            "add" -> stringToVariable(splitted[1]).accumulateAndGet(stringToVariable(splitted[2]).get()) { a, b -> a + b }
            "mul" -> stringToVariable(splitted[1]).accumulateAndGet(stringToVariable(splitted[2]).get()) { a, b -> a * b }
            "div" -> stringToVariable(splitted[1]).accumulateAndGet(stringToVariable(splitted[2]).get()) { a, b -> a / b }
            "mod" -> stringToVariable(splitted[1]).accumulateAndGet(stringToVariable(splitted[2]).get()) { a, b -> a % b }
            "eql" -> stringToVariable(splitted[1]).accumulateAndGet(stringToVariable(splitted[2]).get()) { a, b -> if (a == b) 1 else 0 }
            else -> throw IllegalArgumentException("Unsupported instruction: ${splitted[0]}")
        }
        println("[$instruction]: w=$w, x=$x, y=$y, z=$z")
    }

    private fun stringToVariable(string: String) =
        when (string) {
            "w" -> w
            "x" -> x
            "y" -> y
            "z" -> z
            else -> AtomicInteger(string.toInt())
        }
}

fun main() {
    val input = readInput("Day24")

    val alu = ALU()
    input.map { alu.executeInstruction(it) }
    val result = alu.z.get()

    println(result)
}

// z_1 = w_1 + 6
// z_2 = 26*(w_1 + 6) + w_2 + 12
// z_3 = 26^2*(w_1 + 6) + 26*(w_2 + 12) + w_3 + 5
// z_4 = 26^3*(w_1 + 6) + 26^2*(w_2 + 12) + 26*(w_3 + 5) + w_4 + 10
// z_5 = 26^2*(w_1 + 6) + 26*(w_2 + 12) + w_3 + 5
// z_6 = 26^3*(w_1 + 6) + 26^2*(w_2 + 12) + 26*(w_3 + 5) + w_6
// z_7 = 26^4*(w_1 + 6) + 26^3*(w_2 + 12) + 26^2*(w_3 + 5) + 26*w_6 + w_7 + 4
// z_8 = 26^3*(w_1 + 6) + 26^2*(w_2 + 12) + 26^1*(w_3 + 5) + w_6
// z_9 = 26^4*(w_1 + 6) + 26^3*(w_2 + 12) + 26^2*(w_3 + 5) + 26*w_6 + w_9 + 14
// z_10 = 26^3*(w_1 + 6) + 26^2*(w_2 + 12) + 26*(w_3 + 5) + w_6
// z_11 = 26^2*(w_1 + 6) + 26^1*(w_2 + 12) + w_3 + 5
// z_12 = 26*(w_1 + 6) + w_2 + 12
// z_13 = w_1 + 6
// z_14 = 0

// z_1  = w_1 + 6
// z_2  = (z_1 * 26) + (w_2 + 12)
// z_3  = (z_2 * 26) + (w_3 + 5)
// z_4  = (z_3 * 26) + (w_4 + 10)
// z_5  = ((z_4 / 26) * ((25 * x_5) + 1)) + (w_5 + 7) * (((z_4 % 26 - 16) == w_5) == 0)
// z_6  = (z_5 * 26) + w_6
// z_7  = (z_6 * 26) + (w_7 + 4)
// z_8  = ((z_7 / 26) * ((25 * x_8) + 1)) + (w_8 + 12) * (((z_7 % 26 - 4) == w_8) == 0)
// z_9  = (z_8 * 26) + (w_9 + 14)
// z_10 = (z_9 / 26 * ((25 * x_10) + 1)) + (w_10 + 13) * (((z_9 % 26 - 7) == w_10) == 0)
// z_11 = (z_10 / 26 * ((25 * x_11) + 1)) + (w_11 + 10) * (((z_10 % 26 - 8) == w_11) == 0)
// z_12 = (z_11 / 26 * ((25 * x_12) + 1)) + (w_12 + 11) * (((z_11 % 26 - 4) == w_12) == 0)
// z_13 = (z_12 / 26 * ((25 * x_13) + 1)) + (w_13 + 9) * (((z_12 % 26 - 15) == w_13) == 0)
// z_14 = (z_13 / 26 * ((25 * x_14) + 1)) + (w_14 + 9) * (((z_13 % 26 - 8) == w_14) == 0)

// 998..9..291967
// 99893999291967

// 341..9..181211
// 34171911181211