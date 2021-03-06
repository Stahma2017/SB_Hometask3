package ru.skillbranch.skillarticles.viewmodels

interface IArticleViewModel {
    fun handleNightMode()
    fun handleUpText()
    fun handleDownText()
    fun handleBookmark()
    fun handleLike()
    fun handleShare()
    fun handleToggleMenu()
    fun handleSearchMode(isSearchMode: Boolean)
    fun handleSearchQuery(query: String?)
}