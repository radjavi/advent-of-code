fun main() {
    fun getTemplate(input: List<String>) =
        input[0]

    fun getMapping(input: List<String>) =
        input.subList(2, input.size).map { it.split(" -> ").let { (left, right) -> left to right } }.toMap()

    fun pairInsertion(pair: String, mapping: Map<String, String>) =
        "" + pair[0] + mapping[pair]!! + pair[1]

    fun recursivePairInsertion(
        subTemplate: String,
        mapping: Map<String, String>,
        steps: Int,
        cache: MutableMap<String, MutableMap<Int, Map<Char, Long>>>
    ): Map<Char, Long> {
        if (steps == 0) return emptyMap()
        if (cache[subTemplate]?.get(steps) != null) return cache[subTemplate]!![steps]!!
        val occurrences = mutableMapOf<Char, Long>()
        val newSubTemplate = pairInsertion(subTemplate, mapping)
        val middle = newSubTemplate[1]
        occurrences[middle] = 1L
        val leftOccurrences = recursivePairInsertion(newSubTemplate.substring(0..1), mapping, steps - 1, cache)
        val rightOccurrences = recursivePairInsertion(newSubTemplate.substring(1..2), mapping, steps - 1, cache)
        leftOccurrences.map { occurrences[it.key] = occurrences.getOrPut(it.key) { 0L } + it.value }
        rightOccurrences.map { occurrences[it.key] = occurrences.getOrPut(it.key) { 0L } + it.value }
        cache.getOrPut(subTemplate) { mutableMapOf() }[steps] = occurrences
        return occurrences
    }

    fun getQuantity(input: List<String>, steps: Int): Long {
        val template = getTemplate(input)
        val mapping = getMapping(input)
        val occurrences = mutableMapOf<Char, Long>()
        val cache = mutableMapOf<String, MutableMap<Int, Map<Char, Long>>>()

        for (i in 0 until template.length - 1) {
            val currentPair = "" + template[i] + template[i + 1]
            val subOccurences = recursivePairInsertion(currentPair, mapping, steps, cache)
            subOccurences.map { occurrences[it.key] = occurrences.getOrPut(it.key) { 0L } + it.value }
            occurrences[template[i]] = occurrences.getOrPut(template[i]) { 0L } + 1
        }
        occurrences[template.last()] = occurrences.getOrPut(template.last()) { 0L } + 1

        val mostCommon = occurrences.maxByOrNull { it.value }!!.value
        val leastCommon = occurrences.minByOrNull { it.value }!!.value

        return mostCommon - leastCommon
    }

    fun part1(input: List<String>) =
        getQuantity(input, 10)

    fun part2(input: List<String>) =
        getQuantity(input, 40)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588L)
    check(part2(testInput) == 2188189693529L)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
