package ru.skillbranch.skillarticles.viewmodels.article

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.ui.custom.markdown.MarkdownElement
import ru.skillbranch.skillarticles.ui.custom.markdown.clearContent
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class ArticleViewModel(
    handle: SavedStateHandle,
    private val articleId: String
) : BaseViewModel<ArticleState>(handle, ArticleState()),
    IArticleViewModel {
    private val repository = ArticleRepository
    private var clearContent: String? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
    }

    private val listData: LiveData<PagedList<CommentItemData>> = Transformations.switchMap(getArticleData()) {
        buildPagedList(repository.allComments(articleId, it?.commentCount? : 0))
    }

    fun handleCommentFocus(hasFocus: Boolean) {
        updateState { it.copy(showBottomBar = !hasFocus) }
    }

    fun handleClearComment() {
        updateState { it.copy(answerTo = null, answewrToSlug = null) }
    }

    fun handleReplyTo(slug: String, name: String) {
        updateState { it.copy(answewrToSlug = slug, answerTo = "Reply to $name") }
    }

    init {
        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format(),
                author = article.author
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            Log.d("TAG11", "subscribeOnDataSource(settings)")
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }

        subscribeOnDataSource(repository.isAuth()) { auth, state ->
            state.copy(isAuth = auth)
        }
    }

    override fun handleSendComment(comment: String) {
        if (!currentState.isAuth) navigate(NavigationCommand.StartLogin())
        else {
            viewModelScope.launch {
                repository.sendComment(articleId, comment, currentState.answerToSlug)
                withContext(Dispatchers.Main) {
                    updateState { it.copy(answerTo = null, answerToSlug = null) }
                }
            }
        }
    }

    fun observeList(
        owner: LifecycleOwner,
        onChanged: (list: PagedList<CommentItemData>) -> Unit
    ) {
        listData.observe(woner, Observer { onChanged(it) })
    }

    private fun buildPagedList(
        dataFactory : CommentsDataFactory
    ) : LiveData<PagedList<CommentItemData>> {
        return LivePagedListBuilder<String, CommentItemData>(dataFactory, listConfig)
            .setFetchExecutor(Executors.newSingleTHreadExecutor())
            .build()
    }



    override fun getArticleContent(): LiveData<List<MarkdownElement>?> {
        return repository.loadArticleContent(articleId)
    }

    override fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleLike() {
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if (currentState.isLike) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don't like it anymore",
                "No, still like it",
                toggleLike
            )
        }

        notify(msg)
    }

    override fun handleBookmark() {
        val toggleBookmark = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))
        }
        toggleBookmark()

        val msg = if (currentState.isBookmark) Notify.TextMessage("Add to bookmarks")
        else Notify.TextMessage("Remove from bookmarks")
        notify(msg)
    }

    override fun handleShare() {
        val msg = "Share is not implemented"
       notify(Notify.ErrorMessage(msg, "OK", null))
    }

    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }

    override fun handleSearchMode(isSearchMode: Boolean) {
        updateState { it.copy(isSearch = isSearchMode, isShowMenu = false, searchPosition = 0) }
    }

    override fun handleSearch(query: String?) {
        query ?: return
        if (clearContent == null && currentState.content.isNotEmpty()) clearContent =
            currentState.content.clearContent()
        val result = clearContent
            .indexesOf(query)
            .map { it to it + query.length }
        updateState { it.copy(searchQuery = query, searchResults = result, searchPosition = 0) }
    }

    fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    fun handleCopyCode() {
        notify(Notify.TextMessage("Code copy to clipboard"))
    }

    fun handleSendComment() {
       if (!currentState.isAuth) navigate(NavigationCommand.StartLogin())
        // send comment
    }
}

data class ArticleState(
    val isAuth: Boolean = false, //пользователь авторизован
    val isLoadingContent: Boolean = true, //контент загружается
    val isLoadingReviews: Boolean = true, //отзывы загружаются
    val isLike: Boolean = false, //отмечено как Like
    val isBookmark: Boolean = false, //в закладках
    val isShowMenu: Boolean = false, //отображается меню
    val isBigText: Boolean = false, //шрифт увеличен
    val isDarkMode: Boolean = false, //темный режим
    val isSearch: Boolean = false, //режим поиска
    val searchQuery: String? = null, //поисковый запрос
    val searchResults: List<Pair<Int, Int>> = emptyList(), //результаты поиска (стартовая и конечная позиции)
    val searchPosition: Int = 0, //текущая позиция найденного результата
    val shareLink: String? = null, //ссылка share
    val title: String? = null, //заголовок статьи
    val category: String? = null, //категория
    val categoryIcon: Any? = null, //иконка категории
    val date: String? = null, //дату публикации
    val author: Any? = null, //автор статьи
    val poster: String? = null, //обложка статьи
    val content: List<MarkdownElement> = emptyList(), //контент
    val reviews: List<Any> = emptyList(), //комментарии
    val commentsCount: Int = 0,
    val answerTo: String? = null,
    val answerToSlug: String? = null,
    val showBottomBar: Boolean = true
) : IViewModelState {

    override fun save(outState: SavedStateHandle) {
        //TODO save state
        outState.set("isSearch", isSearch)
        outState.set("searchQuery", searchQuery)
        outState.set("searchResults", searchResults)
        outState.set("searchPosition", searchPosition)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        //TODO restore state
        return copy(
            isSearch = savedState["isSearch"] ?: false,
            searchQuery = savedState["searchQuery"],
            searchResults = savedState["searchResults"] ?: emptyList(),
            searchPosition = savedState["searchPosition"] ?: 0
        )
    }
}