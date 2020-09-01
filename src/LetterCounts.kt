class LetterCounts {
    private val counts: LetterToInt

    constructor(counts: LetterToInt) {
        this.counts = counts
    }

    constructor(word: String) {
        val mutableCounts = mutableMapOf<Char, Int>()
        for (letter in word) {
            mutableCounts[letter] = mutableCounts.getOrDefault(letter, 0) + 1
        }
        counts = LetterToInt(mutableCounts)
    }

    operator fun plus(other: LetterCounts) = LetterCounts(counts + other.counts)

    fun withinLetterDistribution(): Boolean {
        for (i in 0 until counts.size) {
            if (counts[i] > letterDistribution.counts[i]) {
                return false
            }
        }
        return true
    }
}

fun List<Word>.getAllValidHavingUsed(counts: LetterCounts): List<Word> {
    val mutable = toMutableList()
    mutable.removeIf { !(it.letterCounts + counts).withinLetterDistribution() }
    return mutable
}

fun List<Word>.getAllValidHavingUsed(vararg words: Word): List<Word> {
    val countsList = words.map { it.letterCounts }
    var totalCounts = LetterCounts("")
    for (counts in countsList) {
        totalCounts += counts
    }

    return getAllValidHavingUsed(totalCounts)
}

fun List<Word>.getAllValid(): List<Word> = getAllValidHavingUsed()

private val letterDistribution = LetterCounts(LetterToInt(mapOf(
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
)))
