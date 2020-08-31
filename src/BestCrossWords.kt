fun bestCrossWords(tttWord: String): List<Word> {
    val letterCounts = mutableMapOf<Char, Int>()
    for (letter in tttWord) {
        letterCounts[letter] = letterCounts.getOrDefault(letter, 0) + 1
    }

    val words1 = WordData.startWordsByScore[tttWord[0]].getAllValidHavingUsed()

    for (word1 in words1) {
    }
}
