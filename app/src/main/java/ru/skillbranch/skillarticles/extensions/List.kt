package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> {
    val list: MutableList<List<Pair<Int, Int>>> = mutableListOf()
    var indexPointer = 0

    bounds.forEach { bound ->
        var boundFirst: Int?
        var boundSecond: Int?
        val boundList = mutableListOf<Pair<Int, Int>>()


        while (indexPointer < size && (bound.second in get(indexPointer).first..get(indexPointer).second || get(indexPointer).second in bound.first..bound.second)) {

            if (get(indexPointer).first in bound.first..bound.second) {
                boundFirst = get(indexPointer).first
            } else {
                boundFirst = bound.first
            }

            if (get(indexPointer).second in bound.first..bound.second) {
                boundSecond = get(indexPointer).second
                indexPointer++
            } else {
                boundSecond = bound.second
                boundList.add(Pair(boundFirst, boundSecond))
                break
            }

            boundList.add(Pair(boundFirst, boundSecond))
        }

        list.add(boundList.toList())
    }
    return list
}

