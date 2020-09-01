import java.io.FileReader
import java.io.IOException
import java.io.Reader

private const val DICT_PATH = "scrabble_dict.txt"
private const val START_WORDS_PATH = "start_words.txt"
private const val END_WORDS_PATH = "end_words.txt"

data class Word(val string: String) {
    val score = wordScore(string)
    val letterCounts = LetterCounts(string)

    override fun toString() = string
}

fun List<Word>.score() = sumBy { it.score }

object WordData {
    val words: Set<String>
    val wordsByLength: Map<Int, List<Word>>
    val startWordsByScore: Map<Char, List<Word>>
    val endWordsByScore: Map<Char, List<Word>>

    init {
        val words = mutableSetOf<String>()
        val wordsByLength = mutableMapOf<Int, MutableList<Word>>()
        val startWordsByScore = mutableMapOf<Char, List<Word>>()
        val endWordsByScore = mutableMapOf<Char, List<Word>>()

        val dictFileReader: Reader
        val startWordsReader: Reader
        val endWordsReader: Reader
        try {
            dictFileReader = FileReader(DICT_PATH).buffered()
            startWordsReader = FileReader(START_WORDS_PATH).buffered()
            endWordsReader = FileReader(END_WORDS_PATH).buffered()
        } catch (e: IOException) {
            e.printStackTrace()
            error("File not found")
        }

        dictFileReader.forEachLine { words.add(it) }
        startWordsReader.forEachLine { line ->
            val values = line.split(',')
            val letter = values[0][0]
            val wordStrings = values.subList(1, values.size)
            startWordsByScore[letter] = wordStrings.map { Word(it) }
        }
        endWordsReader.forEachLine { line ->
            val values = line.split(',')
            val letter = values[0][0]
            val wordStrings = values.subList(1, values.size)
            endWordsByScore[letter] = wordStrings.map { Word(it) }
        }


        words.forEach {
            val lengthList = wordsByLength.getOrPut(it.length) { mutableListOf() }
            lengthList.add(Word(it))
        }

        this.words = words
        this.wordsByLength = wordsByLength
        this.startWordsByScore = startWordsByScore
        this.endWordsByScore = endWordsByScore
    }
}

fun letterScore(letter: Char): Int = when (letter) {
    'e', 'a', 'i', 'o', 'n', 'r', 't', 'l', 's', 'u'
    -> 1

    'd', 'g'
    -> 2

    'b', 'c', 'm', 'p'
    -> 3

    'f', 'h', 'v', 'w', 'y'
    -> 4

    'k'
    -> 5

    'j', 'x'
    -> 8

    'q', 'z'
    -> 10

    else
    -> 0
}

fun wordScore(word: String): Int = word.sumBy(::letterScore)
