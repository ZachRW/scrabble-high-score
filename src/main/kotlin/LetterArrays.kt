data class LetterCounts(private val counts: Array<Int>) {
    operator fun get(key: Char): Int = counts[key.toIndex()]

    operator fun plus(other: LetterCounts): LetterCounts {
        val result = counts.copyOf()

        for (i in counts.indices) {
            result[i] += other.counts[i]
        }

        return LetterCounts(result)
    }

    fun withinLetterDistribution(): Boolean {
        for (i in counts.indices) {
            if (counts[i] > letterDistribution.counts[i]) {
                return false
            }
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LetterCounts

        if (!counts.contentEquals(other.counts)) return false

        return true
    }

    override fun hashCode(): Int {
        return counts.contentHashCode()
    }
}

private fun Map<Char, Int>.toLetterCounts(): LetterCounts {
    val array = Array(ARRAY_SIZE) { 0 }

    for ((key, value) in entries) {
        array[key.toIndex()] = value
    }

    return LetterCounts(array)
}

fun String.toLetterCounts(): LetterCounts {
    val array = Array(ARRAY_SIZE) { 0 }

    for (letter in this) {
        array[letter.toIndex()]++
    }

    return LetterCounts(array)
}

data class LetterToWordList(private val array: Array<List<Word>>) {
    operator fun get(key: Char): List<Word> = array[key.toIndex()]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LetterToWordList

        if (!array.contentEquals(other.array)) return false

        return true
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }
}

fun Map<Char, List<Word>>.toLetterToWordList(): LetterToWordList {
    val array = Array<List<Word>>(ARRAY_SIZE) { listOf() }

    for ((key, value) in entries) {
        array[key.toIndex()] = value
    }

    return LetterToWordList(array)
}

private val letterDistribution = mapOf(
        'e' to 12,
        'a' to 9,
        'i' to 9,
        'o' to 8,
        'n' to 6,
        'r' to 6,
        't' to 6,
        'l' to 4,
        's' to 4,
        'u' to 4,
        'd' to 4,
        'g' to 3,
        'b' to 2,
        'c' to 2,
        'm' to 2,
        'p' to 2,
        'f' to 2,
        'h' to 2,
        'v' to 2,
        'w' to 2,
        'y' to 2,
        'k' to 1,
        'j' to 1,
        'x' to 1,
        'q' to 1,
        'z' to 1
).toLetterCounts()

private const val ARRAY_SIZE = 'z'.toInt() - 'a'.toInt() + 1
private const val CHAR_OFFSET = 'a'.toInt()
private fun Char.toIndex() = toInt() - CHAR_OFFSET
