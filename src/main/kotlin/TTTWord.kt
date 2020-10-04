class TTTWord(private val word: Word) {
    // the score excluding cross words
    private val baseScore: Int
    private val bestPermutation = TTTPermutation(word)
    var sideOfCrossWords: WordPosition? = null
    var bestCrossScore: Int = 0
    var crossWords: List<Word>? = null

    // Initialize baseScore and sideOfCrossWords
    init {
        baseScore = (word.score + bestPermutation.score) * 27
    }

    fun usable(): Boolean = bestPermutation.score != -1

    /**
     * Find the best score for this TTT word. Aborts if there can't be a solution better than
     * [bestTTTWord]. Updates [sideOfCrossWords], [bestCrossScore], and [crossWords] if a new best for this
     * word is found. Updates [bestTTTWord] if solution is better than current [bestTTTWord].
     * Assumes the ttt word can be played with 7 tiles, meaning a call to [usable] must return true.
     */
    fun findBestScore() {
        sideOfCrossWords = WordPosition.START
        println("Start words:")
        findBestCrossWords(WordData.START_WORDS_BY_SCORE)

        val prevBestScore = bestCrossScore
        println("\nEnd words:")
        findBestCrossWords(WordData.END_WORDS_BY_SCORE)
        if (bestCrossScore != prevBestScore) {
            sideOfCrossWords = WordPosition.END
        }
    }

    private fun findBestCrossWords(wordsByScore: LetterToWordList) {
        val wordLists = arrayOf(
                wordsByScore[word.string[0]],
                wordsByScore[word.string[7]],
                wordsByScore[word.string[14]]
        )

        if (wordLists.any { it.isEmpty() }) {
            println("Not all TWs can have a cross word")
            return
        }
        if (3 * wordLists.sumBy { it[0].score } + baseScore <= bestTTTWord.score()) {
            println("Best case not good enough")
            return
        }
        if (!word.letterCounts.withinLetterDistribution()) {
            println("Can't be played")
            return
        }

        for (word0 in wordLists[0]) {
            val letterCounts0 = word.letterCounts + word0.letterCounts
            if (!letterCounts0.withinLetterDistribution()) {
                continue
            }

            for (word1 in wordLists[1]) {
                val letterCounts1 = letterCounts0 + word1.letterCounts
                if (!letterCounts1.withinLetterDistribution()) {
                    continue
                }

                for (word2 in wordLists[2]) {
                    val letterCounts2 = letterCounts1 + word2.letterCounts
                    if (!letterCounts2.withinLetterDistribution()) {
                        continue
                    }

                    val crossScore = 3 * (word0.score + word1.score + word2.score)
                    if (crossScore + baseScore > bestTTTWord.score()) {
                        bestCrossScore = crossScore
                        crossWords = listOf(word0, word1, word2)
                        bestTTTWord = this
                        println("New best: $word0, $word1, $word2")
                        println("Score: ${score() + 50}")
                    }
                    break
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
