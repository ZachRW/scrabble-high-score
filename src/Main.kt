import java.io.FileWriter

fun main() {
}

fun findBestTTTWord() {
    val bestTTTWords = mutableListOf<TTTWord>()
    var bestScore = 0

    for (word in WordData.wordsByLength.getValue(15)) {
        val tttWord = TTTWord(word)
        if (tttWord.scoreUpperLimit() > bestScore) {
            val score = tttWord.bestScore
            if (score > bestScore) {
                bestScore = score
                bestTTTWords.clear()
                bestTTTWords.add(tttWord)
                println("$tttWord: $bestScore")
            } else if (score == bestScore) {
                bestTTTWords.add(tttWord)
            }
        }
    }
    println()

    for (tttWord in bestTTTWords) {
        println("$tttWord: $bestScore")
        println(tttWord.sideOfCrossWords)
    }
}

fun findCrossWords() {
    val startWords = mutableMapOf<Char, MutableSet<String>>().apply {
        for (letter in 'a'..'z') {
            put(letter, mutableSetOf())
        }
    }
    val endWords = mutableMapOf<Char, MutableSet<String>>().apply {
        for (letter in 'a'..'z') {
            put(letter, mutableSetOf())
        }
    }

    for (word in WordData.words) {
        if (WordData.words.contains(word.substring(1))) {
            startWords.getValue(word.first()).add(word)
        }
        if (WordData.words.contains(word.substring(0, word.lastIndex))) {
            endWords.getValue(word.last()).add(word)
        }
    }

    writeSortedCrossWordsToFile(startWords, "start_words.txt")
    writeSortedCrossWordsToFile(endWords, "end_words.txt")
}

fun writeSortedCrossWordsToFile(wordMap: Map<Char, MutableSet<String>>, fileName: String) {
    FileWriter(fileName).buffered().use { writer ->
        for ((letter, words) in wordMap) {
            val sortedWords: List<String> = words.sortedBy { wordScore(it) }

            writer.write("$letter")
            for (word in sortedWords) {
                writer.write(",$word")
            }
            writer.write("\n")
        }
    }
}
