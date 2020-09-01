internal class TTTWord(private val word: Word) {
    private val baseScore: Int
    private var bestPermutation: TTTPermutation? = null
    val bestScore by lazy { bestScore() }
    val sideOfCrossWords: WordPosition
    val crossWords: List<Word>

    // Initialize baseScore and sideOfCrossWords
    init {
        var score = word.score * 27

        val bestStartWords = bestCrossWords(WordData.START_WORDS_BY_SCORE)
        val bestEndWords = bestCrossWords(WordData.END_WORDS_BY_SCORE)

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

        val startScoreUpperLimit = crossWordScoreUpperLimit(WordData.START_WORDS_BY_SCORE)
        val endScoreUpperLimit = crossWordScoreUpperLimit(WordData.END_WORDS_BY_SCORE)

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

    private fun crossWordScoreUpperLimit(wordsByScore: LetterArrayMap<List<Word>>): Int =
            wordsByScore[word.string[0]].score() +
                    wordsByScore[word.string[7]].score() +
                    wordsByScore[word.string[14]].score()


    private fun bestCrossWords(wordsByScore: LetterArrayMap<List<Word>>): List<Word> {
        val letterCounts = mutableMapOf<Char, Int>()
        for (letter in word.string) {
            letterCounts[letter] = letterCounts.getOrDefault(letter, 0) + 1
        }

        var bestScore = 0
        var bestWords = listOf<Word>()

        val words1 = wordsByScore[word.string[0]].getAllValid()

        for (word1 in words1) {
            println(word1)

            val words2 = wordsByScore[word.string[7]].getAllValidHavingUsed(word1)

            for (word2 in words2) {
                val words3 = wordsByScore[word.string[14]].getAllValidHavingUsed(word1, word2)
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
