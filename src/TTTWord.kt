class TTTWord(private val word: Word) {
    // the score excluding cross words
    private val baseScore: Int
    private val bestPermutation = TTTPermutation(word)
    var sideOfCrossWords: WordPosition? = null
    var bestScore: Int = 0
    var crossWords: List<Word>? = null

    // Initialize baseScore and sideOfCrossWords
    init {
        bestPermutation.findBest()
        baseScore = word.score * 27 + bestPermutation.score()
    }

    /**
     * Find the best score for this TTT word. Aborts if there can't be a solution better than
     * [bestTTTWord]. Updates [sideOfCrossWords], [bestScore], and [crossWords] if a new best for this
     * word is found. Updates [bestTTTWord] if solution is better than current [bestTTTWord].
     */
    fun findBestScore() {
        if (scoreUpperLimit() < bestTTTWord?.bestScore ?: 0) {
            return
        }

        sideOfCrossWords = WordPosition.START
        findBestCrossWords(WordData.START_WORDS_BY_SCORE)

        val prevBestScore = bestScore
        findBestCrossWords(WordData.END_WORDS_BY_SCORE)
        if (bestScore != prevBestScore) {
            sideOfCrossWords = WordPosition.END
        }
    }

    private fun scoreUpperLimit(): Int {
        var score = baseScore

        val startScoreUpperLimit = crossWordScoreUpperLimit(WordData.START_WORDS_BY_SCORE)
        val endScoreUpperLimit = crossWordScoreUpperLimit(WordData.END_WORDS_BY_SCORE)

        score += maxOf(startScoreUpperLimit, endScoreUpperLimit)

        return score
    }

    private fun crossWordScoreUpperLimit(wordsByScore: LetterArrayMap<List<Word>>): Int =
            wordsByScore[word.string[0]].score() +
                    wordsByScore[word.string[7]].score() +
                    wordsByScore[word.string[14]].score()


    private fun findBestCrossWords(wordsByScore: LetterArrayMap<List<Word>>) {
        val letterCounts = mutableMapOf<Char, Int>()
        for (letter in word.string) {
            letterCounts[letter] = letterCounts.getOrDefault(letter, 0) + 1
        }

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
                    crossWords = words
                }
            }
        }
    }

    override fun toString() = bestPermutation.toString()
}

enum class WordPosition { START, END }
