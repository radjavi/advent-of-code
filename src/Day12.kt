private enum class PathRule {
    PART1,
    PART2
}

fun main() {
    fun buildGraph(input: List<String>): MutableMap<String, MutableSet<String>> {
        val graph = mutableMapOf<String, MutableSet<String>>()
        input.map { row ->
            row.split("-").let {
                val start = it[0]
                val end = it[1]
                graph.getOrPut(start) { mutableSetOf() }.add(end)
                graph.getOrPut(end) { mutableSetOf() }.add(start)
            }
        }
        return graph
    }

    fun traverse(
        graph: MutableMap<String, MutableSet<String>>,
        rule: PathRule,
        path: List<String>,
        smallCavesCount: MutableMap<String, Int>,
        current: String
    ): Int {
        val newSmallCavesCount = smallCavesCount.toMutableMap()
        if (current.any { it.isLowerCase() }) {
            newSmallCavesCount[current] = newSmallCavesCount.getOrPut(current) { 0 } + 1
            when (rule) {
                PathRule.PART1 -> if (newSmallCavesCount[current]!! > 1) return 0
                PathRule.PART2 -> {
                    if (newSmallCavesCount.values.count { it > 1 } > 1) return 0
                    else if (newSmallCavesCount.values.maxOrNull()!! > 2) return 0
                }
            }
        }
        if (current == "end") return 1
        return graph[current]!!.filterNot { it == "start" }
            .sumOf {
                traverse(
                    graph = graph,
                    rule = rule,
                    path = path + current,
                    smallCavesCount = newSmallCavesCount,
                    current = it
                )
            }
    }

    fun countDistinctPaths(graph: MutableMap<String, MutableSet<String>>, rule: PathRule): Int {
        return graph["start"]!!.sumOf {
            traverse(
                graph = graph,
                rule = rule,
                path = listOf("start"),
                smallCavesCount = mutableMapOf(),
                current = it
            )
        }
    }

    fun part1(input: List<String>): Int {
        val graph = buildGraph(input)
        return countDistinctPaths(graph, PathRule.PART1)
    }

    fun part2(input: List<String>): Int {
        val graph = buildGraph(input)
        return countDistinctPaths(graph, PathRule.PART2)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)
    check(part2(testInput) == 36)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
