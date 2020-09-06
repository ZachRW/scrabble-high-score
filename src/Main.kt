import java.io.FileWriter

fun main() {
    findBestTTTWord()
}

var bestTTTWord: TTTWord? = null

fun findBestTTTWord() {
    for (word in WordData.wordsByLength.getValue(15)) {
        TTTWord(word).findBestScore()
    }

    println("$bestTTTWord: ${bestTTTWord?.bestScore}")
    println(bestTTTWord?.sideOfCrossWords)
    println(bestTTTWord?.crossWords)
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
            val sortedWords: List<String> = words.sortedBy { wordScore(it) }.reversed()

            writer.write("$letter")
            for (word in sortedWords) {
                writer.write(",$word")
            }
            writer.write("\n")
        }
    }
}
