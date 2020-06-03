internal class TTTWord(private val word: String) {
    private val baseScore: Int
    private var bestPermutation: TTTPermutation? = null
    val bestScore by lazy { bestScore() }
    val sideOfCrossWords: WordPosition

    fun scoreUpperLimit(): Int {
        var score = baseScore
        score += letterScore(word[3]) * 27
        score += letterScore(word[11]) * 27
        return score
    }

    private fun bestScore(): Int {
        val upperLimit = scoreUpperLimit()
        val permutation = TTTPermutation(word)
        var bestScore = 0
        while (permutation.nextValid()) {
            val score = permutation.score(baseScore)
            if (score == upperLimit) {
                bestPermutation = TTTPermutation(permutation)
                return score
            }
            if (score > bestScore) {
                bestScore = score
                bestPermutation = TTTPermutation(permutation)
            }
        }
        return bestScore
    }

    override fun toString(): String {
        return bestPermutation?.toString() ?: word
    }

    // Initialize baseScore and sideOfCrossWords
    init {
        var score = wordScore(word) * 27
        var startScore = 0
        var endScore = 0

        with(WordData.bestStartCross) {
            startScore += getValue(word[0]).score
            startScore += getValue(word[7]).score
            startScore += getValue(word[14]).score
        }
        with(WordData.bestEndCross) {
            endScore += getValue(word[0]).score
            endScore += getValue(word[7]).score
            endScore += getValue(word[14]).score
        }

        if (startScore > endScore) {
            sideOfCrossWords = WordPosition.START
            score += startScore
        } else {
            sideOfCrossWords = WordPosition.END
            score += endScore
        }
        baseScore = score
    }
}

enum class WordPosition { START, END }
