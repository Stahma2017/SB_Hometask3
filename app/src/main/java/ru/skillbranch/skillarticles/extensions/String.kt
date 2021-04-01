package ru.skillbranch.skillarticles.extensions

/*fun String?.indexesOf(query: String): List<Int> {
    if (this.isNullOrEmpty()) return emptyList()
    var removingString = this
    var cuttedStringLength = 0
    val resultList: MutableList<Int> = mutableListOf()

    while (removingString?.contains(query) == true) {
        val entryStartIndex = removingString.indexOf(query)
        resultList.add(cuttedStringLength + entryStartIndex)
        removingString = removingString.removeRange(0, entryStartIndex + query.length)
        cuttedStringLength = this.indexOf(removingString)
    }
    return resultList
}*/

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true) =
    when {
        this == null || substr.isEmpty() -> listOf()
        else -> substr
            .run { if (ignoreCase) toRegex(RegexOption.IGNORE_CASE) else toRegex() }
            .findAll(this)
            .map { it.range.first }
            .toList()
    }