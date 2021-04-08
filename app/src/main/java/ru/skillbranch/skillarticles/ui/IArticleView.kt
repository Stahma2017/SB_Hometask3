package ru.skillbranch.skillarticles.ui

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData

interface IArticleView {
    fun renderSearchResult(searchResult: List<Pair<Int, Int>>)
    fun renderSearchPosition(searchPosition: Int)
    fun clearSearchResult()
    fun showSearchBar()
    fun hideSearchBar()
}