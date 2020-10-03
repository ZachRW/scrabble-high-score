import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileWriter

fun main() {
    findBestTTTWord()
}

var bestTTTWord: TTTWord? = null

fun findBestTTTWord() {
    runBlocking {
        for (word in WordData.wordsByLength.getValue(15)) {
            launch(Dispatchers.Default) {
                println("$word: Start")
                val tttWord = TTTWord(word)
                if (tttWord.usable()) {
                    tttWord.findBestScore()
                } else {
                    println("$word: Not usable")
                }
                println("$word: End")
            }
        }
        println("All coroutines launched")
    }

    println("$bestTTTWord: ${bestTTTWord?.bestCrossScore?.plus(50)}")
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
