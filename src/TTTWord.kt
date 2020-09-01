internal class TTTWord(private val word: Word) {
    private val baseScore: Int
    private var bestPermutation: TTTPermutation? = null
    val bestScore by lazy { bestScore() }
    val sideOfCrossWords: WordPosition
    val crossWords: List<Word>

    // Initialize baseScore and sideOfCrossWords
    init {
        var score = word.score * 27

        val bestStartWords = bestCrossWords(WordData.startWordsByScore)
        val bestEndWords = bestCrossWords(WordData.endWordsByScore)

        val startScore = bestStartWords.score()
        val endScore = bestEndWords.score()

        if (startScore > endScore) {
            sideOfCrossWords = WordPosition.START
            crossWords = bestStartWords
            score += startScore
        } else {
            sideOfCrossWords = WordPosition.END
            crossWords = bestEndWords
            score += endScore
        }
        baseScore = score
    }

    fun scoreUpperLimit(): Int {
        var score = word.score

        val startScoreUpperLimit = crossWordScoreUpperLimit(WordData.startWordsByScore)
        val endScoreUpperLimit = crossWordScoreUpperLimit(WordData.endWordsByScore)

        score += maxOf(startScoreUpperLimit, endScoreUpperLimit)

        score += letterScore(word.string[3]) * 27
        score += letterScore(word.string[11]) * 27
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

    private fun crossWordScoreUpperLimit(wordsByScore: Map<Char, List<Word>>): Int = wordsByScore.getValue(word.string[0]).score() +
            wordsByScore.getValue(word.string[7]).score() +
            wordsByScore.getValue(word.string[14]).score()


    private fun bestCrossWords(wordsByScore: Map<Char, List<Word>>): List<Word> {
        val letterCounts = mutableMapOf<Char, Int>()
        for (letter in word.string) {
            letterCounts[letter] = letterCounts.getOrDefault(letter, 0) + 1
        }

        var bestScore = 0
        var bestWords = listOf<Word>()

        val words1 = wordsByScore.getValue(word.string[0]).getAllValid()

        for (word1 in words1) {
            val words2 = wordsByScore.getValue(word.string[7]).getAllValidHavingUsed(word1)

            for (word2 in words2) {
                val words3 = wordsByScore.getValue(word.string[14]).getAllValidHavingUsed(word1, word2)
                if (words3.isEmpty()) continue

                val word3 = words3[0]

                val words = listOf(word1, word2, word3)
                val score = words.sumBy { it.score }
                if (score > bestScore) {
                    bestScore = score
                    bestWords = words
                }
            }
        }

        return bestWords
    }

    override fun toString() = bestPermutation?.toString() ?: word.string
}

enum class WordPosition { START, END }
