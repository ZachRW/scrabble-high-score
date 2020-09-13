class TTTWord(private val word: Word) {
    // the score excluding cross words
    private val baseScore: Int
    private val bestPermutation = TTTPermutation(word)
    var sideOfCrossWords: WordPosition? = null
    var bestCrossScore: Int = 0
    var crossWords: List<Word>? = null

    // Initialize baseScore and sideOfCrossWords
    init {
        baseScore = word.score * 27 + bestPermutation.score
    }

    fun usable(): Boolean = bestPermutation.score != -1

    /**
     * Find the best score for this TTT word. Aborts if there can't be a solution better than
     * [bestTTTWord]. Updates [sideOfCrossWords], [bestCrossScore], and [crossWords] if a new best for this
     * word is found. Updates [bestTTTWord] if solution is better than current [bestTTTWord].
     * Assumes the ttt word can be played with 7 tiles, meaning a call to [usable] must return true.
     */
    fun findBestScore() {
        if (scoreUpperLimit() < bestTTTWord?.bestCrossScore ?: 0) {
            return
        }

        sideOfCrossWords = WordPosition.START
        println("Start words:")
        findBestCrossWords(WordData.START_WORDS_BY_SCORE)

        println("\nEnd words:")
        val prevBestScore = bestCrossScore
        findBestCrossWords(WordData.END_WORDS_BY_SCORE)
        if (bestCrossScore != prevBestScore) {
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
        val wordLists = arrayOf(
                wordsByScore[word.string[0]],
                wordsByScore[word.string[7]],
                wordsByScore[word.string[14]]
        )
        if (wordLists.sumBy { it.score() } <= bestTTTWord.score()) {
            println("Best case not good enough")
            return
        }

        if (!word.letterCounts.withinLetterDistribution()) {
            println("Can't be played")
            return
        }

        val prevWordIndexes2 = Array(wordLists[1].size) { wordLists[2].size }
        for (word0 in wordLists[0]) {
            val letterCounts0 = word.letterCounts + word0.letterCounts
            if (!letterCounts0.withinLetterDistribution()) {
                continue
            }

            var prevWordIndex2 = wordLists[2].size
            for ((wordIndex1, word1) in wordLists[1].withIndex()) {
                val letterCounts1 = letterCounts0 + word1.letterCounts
                if (!letterCounts1.withinLetterDistribution()) {
                    continue
                }

                for (wordIndex2 in 0 until minOf(prevWordIndex2, prevWordIndexes2[wordIndex1])) {
                    val word2 = wordLists[2][wordIndex2]
                    val letterCounts2 = letterCounts1 + word2.letterCounts
                    if (!letterCounts2.withinLetterDistribution()) {
                        continue
                    }

                    val crossScore = word0.score + word1.score + word2.score
                    if (crossScore + baseScore > bestTTTWord.score()) {
                        bestCrossScore = crossScore
                        bestTTTWord = this
                        println("New best: $word0, $word1, $word2")
                        println("Score: ${score()}")
                    }
                    prevWordIndex2 = wordIndex2
                    break
                }
                if (prevWordIndexes2[wordIndex1] != prevWordIndex2) {
                    for (i in 0..wordIndex1) {
                        prevWordIndexes2[i] = prevWordIndex2
                    }
                }
            }
        }
        println("Search complete")
    }

    override fun toString() = bestPermutation.toString()

    fun score() = baseScore + bestCrossScore
}

fun TTTWord?.score(): Int = this?.score() ?: 0

enum class WordPosition { START, END }
