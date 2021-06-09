package ru.skillbranch.skillarticles.viewmodels.article

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.ui.custom.markdown.MarkdownElement

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

    fun getArticleContent(): LiveData<List<MarkdownElement>?>
    fun getArticleData(): LiveData<ArticleData?>
    fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>
    fun handleSendComment(comment: String)
}