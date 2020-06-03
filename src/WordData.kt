import java.io.FileReader
import java.io.IOException
import java.io.Reader
import kotlin.system.exitProcess

private const val DICT_PATH = "scrabble_dict.txt"
private const val CROSS_PATH = "cross_words.txt"

data class WordAndScore(val word: String, val score: Int)

object WordData {
    val words: Set<String>
    val wordsByLength: Map<Int, List<String>>
    val bestStartCross: Map<Char, WordAndScore>
    val bestEndCross: Map<Char, WordAndScore>

    init {
        val words = mutableSetOf<String>()
        val wordsByLength = mutableMapOf<Int, MutableList<String>>()
        val bestStartCross = mutableMapOf<Char, WordAndScore>()
        val bestEndCross = mutableMapOf<Char, WordAndScore>()

        val dictFileReader: Reader
        val crossFileReader: Reader
        try {
            dictFileReader = FileReader(DICT_PATH).buffered()
            crossFileReader = FileReader(CROSS_PATH).buffered()
        } catch (e: IOException) {
            e.printStackTrace()
            exitProcess(1)
        }

        dictFileReader.forEachLine { words.add(it) }
        crossFileReader.forEachLine {
            val values = it.split(',')
            val letter = values[0][0]
            bestStartCross[letter] = WordAndScore(values[1], values[3].toInt())
            bestEndCross[letter] = WordAndScore(values[2], values[4].toInt())
        }

        words.forEach {
            val lengthList = wordsByLength.getOrPut(it.length) { mutableListOf() }
            lengthList.add(it)
        }

        this.words = words
        this.wordsByLength = wordsByLength
        this.bestStartCross = bestStartCross
        this.bestEndCross = bestEndCross
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
