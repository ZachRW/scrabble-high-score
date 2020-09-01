/**
 * An immutable map with lowercase letters as keys and generic values. Backed by an array.
 */
@Suppress("UNCHECKED_CAST")
open class LetterArrayMap<V> {
    val size = ARRAY_SIZE

    constructor(map: Map<Char, V>, default: V) {
        array = Array(ARRAY_SIZE) { map.getOrDefault(it.toCharKey(), default) as Any }
    }

    protected constructor(array: Array<V>) {
        this.array = array as Array<Any>
    }

    protected val array: Array<Any>

    operator fun get(key: Char): V = array[key.toIndex()] as V

    operator fun get(index: Int): V = array[index] as V

    operator fun set(key: Char, value: V) {
        array[key.toIndex()] = value as Any
    }

    operator fun set(index: Int, value: V) {
        array[index] = value as Any
    }
}

//class LetterToWordList(map: Map<Char, List<Word>>) : LetterArrayMap<List<Word>>(map, emptyList())

class LetterToInt : LetterArrayMap<Int> {
    constructor(map: Map<Char, Int>) : super(map, 0)
    constructor() : super(Array(ARRAY_SIZE) { 0 })

    operator fun plus(other: LetterToInt): LetterToInt {
        val result = LetterToInt()

        for (i in 0 until ARRAY_SIZE) {
            result[i] = this[i] + other[i]
        }

        return result
    }
}

private const val ARRAY_SIZE = 'z'.toInt() - 'a'.toInt()
private const val CHAR_OFFSET = 'a'.toInt()
private fun Char.toIndex() = toInt() - CHAR_OFFSET
private fun Int.toCharKey() = toChar() + CHAR_OFFSET
