package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo

interface IArticleViewModel {
    fun handleNightMode()
    fun handleUpText()
    fun handleDownText()
    fun handleBookmark()
    fun handleLike()
    fun handleShare()
    fun handleToggleMenu()
    fun handleSearchMode(isSearchMode: Boolean)
    fun handleSearch(query: String?)

    fun getArticleContent(): LiveData<List<Any>?>
    fun getArticleData(): LiveData<ArticleData?>
    fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>
}