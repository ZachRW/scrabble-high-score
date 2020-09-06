internal class TTTPermutation {
    private val word: String
    private val substrings: MutableList<Substring>

    constructor(word: Word) {
        this.word = word.string
        substrings = mutableListOf(
                Substring(1, 6),
                Substring(8, 2)
        )
    }

    constructor(permutation: TTTPermutation) {
        word = permutation.word
        substrings = permutation.substrings.map { Substring(it) }.toMutableList()
    }

    fun findBest() {
        val upperLimit = letterScore(word[3]) * 27 + letterScore(word[11]) * 27

        var bestScore = 0
        while (nextValid()) {
            val score = score()
            if (score == upperLimit) {
                return
            }
            if (score > bestScore) {
                bestScore = score
            }
        }
    }

    fun score(): Int {
        var firstDL = true
        var secondDL = true
        for (substring in substrings) {
            if (substring.containsCharAt(3)) {
                firstDL = false
            }
            if (substring.containsCharAt(11)) {
                secondDL = false
            }
        }

        var score = 0
        if (firstDL) {
            score += letterScore(word[3]) * 27
        }
        if (secondDL) {
            score += letterScore(word[11]) * 27
        }
        return score
    }

    private fun validSubstrings(): Boolean = substrings.all { it.isValid }

    private fun permute(): Boolean {
        var substringIndex = substrings
                .indexOfFirst { !it.isValid }
                .let { firstInvalid ->
                    if (firstInvalid == -1) {
                        substrings.lastIndex
                    } else {
                        firstInvalid
                    }
                }

        while (!permute(substringIndex)) {
            if (substringIndex == 0) {
                return false
            }
            substringIndex--
        }
        return true
    }

    private fun permute(substringIndex: Int): Boolean {
        val substring = substrings[substringIndex]
        if (substring.end == 14) {
            return false
        }
        if (substring.end == 7 && substringIndex != substrings.lastIndex) {
            val nextSubstring = substrings[substringIndex + 1]
            if (nextSubstring.start == 8 && nextSubstring.end == 14) {
                return false
            }
        }


        var followingTiles = 0
        for (i in substrings.lastIndex downTo substringIndex + 1) {
            followingTiles += substrings[i].length
            substrings.removeAt(i)
        }
        substrings.removeAt(substringIndex)

        if (substring.length > 1) {
            substrings.add(Substring(substring.start, substring.length - 1))
        }

        val tilesBeforeMid = 7 - substring.end
        val tilesAfterMid = substring.end + followingTiles + 1 - 7
        when {
            tilesBeforeMid > 0 && tilesAfterMid > 0 -> {
                substrings.add(Substring(substring.end, tilesBeforeMid))
                substrings.add(Substring(8, tilesAfterMid))
            }

            substring.end == 7 ->
                substrings.add(Substring(8, followingTiles + 1))

            else ->
                substrings.add(Substring(substring.end, followingTiles + 1))
        }

        return true
    }

    fun nextValid(): Boolean {
        do {
            if (!permute()) {
                return false
            }
        } while (!validSubstrings())
        return true
    }

    override fun toString(): String {
        val string = StringBuilder(word)
        substrings.reversed().forEach {
            string.insert(it.end, ")")
            string.insert(it.start, "(")
        }
        return string.toString()
    }

    internal inner class Substring {
        var start: Int
        var end: Int
        private var validated = false

        constructor(start: Int, length: Int) {
            this.start = start
            end = start + length
        }

        constructor(substring: Substring) {
            start = substring.start
            end = substring.end
            validated = substring.validated
        }

        val length: Int
            get() = end - start

        val isValid: Boolean
            get() {
                if (validated) {
                    return true
                }
                if (length == 1) {
                    validated = true
                    return true
                }
                if (WordData.words.contains(toString())) {
                    validated = true
                    return true
                }
                return false
            }

        override fun toString() = word.substring(start, end)

        fun containsCharAt(index: Int) = index in start..end
    }
}