package ru.skillbranch.skillarticles.markdown

import java.lang.StringBuilder
import java.util.regex.Pattern

object MarkdownParser2 {
    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    // group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?[^~]?~{2}(?!~))"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"
    private const val BLOCK_CODE_GROUP = "(^```[\\s\\S]+?```$)"
    private const val ORDER_LIST_GROUP = "(^\\d{1,2}\\.\\s.+?$)"

    //result regex
    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
            "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP|$BLOCK_CODE_GROUP|$ORDER_LIST_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }


    /**
     * parse markdown text to elements
     */
    fun parse(string: String): MarkdownText2 {
        val elements = mutableListOf<Element2>()
        elements.addAll(findElements(string))
        return MarkdownText2(elements)
    }

    /**
     * clear markdown text to string without markdown characters
     */
    fun clear(string: String?): String? {
        string ?: return null
        val mt = parse(string)
        val element = Element2.Text("", mt.elements)
        return getInnerText(element)
    }

    private fun getInnerText(element: Element2): String {
        if (element.elements.size <= 1) return element.text.toString()

        val stringBuilder = StringBuilder()
        element.elements.forEach {
            stringBuilder.append(getInnerText(it))
        }
        return stringBuilder.toString()
    }

    /**
     * find markdown elements in markdown text
     */
     fun findElements(string: CharSequence): List<Element2> {
        val parents = mutableListOf<Element2>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            // if something is found then everything before - TEXT
            if (lastStartIndex < startIndex) {
                parents.add((Element2.Text(string.subSequence(lastStartIndex, startIndex))))
            }

            // found text
            var text: CharSequence

            // groups range for iterate by groups
            val groups = 1..11
            var group = -1
            // цикл чтобы итереироваться по группам
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {              // 01:03:50
                // NOT FOUND -> BREAK
                -1 -> break@loop

                //UNORDERED LIST
                1 -> {

                    // text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element2.UnorderedListItem(text, subs)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex

                }

                //HEADER
                2 -> {                  // 01:09
                    val reg = "^#{1,6}".toRegex()
                        .find(string.subSequence(startIndex, string.length))
                    val level = reg!!.value.length

                    // text without "{#} "
                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    val element = Element2.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //QUOTE
                3 -> {                  // 01:11
                    //text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subelements = findElements(text)
                    val element = Element2.Quote(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //ITALIC
                4 -> {                  // 01:13
                    //text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements = findElements(text)
                    val element = Element2.Italic(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //BOLD
                5 -> {                  // 01:16
                    //text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subelements = findElements(text)
                    val element = Element2.Bold(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //STRIKE
                6 -> {
                    //text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subelements = findElements(text)
                    val element = Element2.Strike(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //RULE
                7 -> {                    // 01:21
                    //text without "***" insert empty character
                    val element = Element2.Rule()
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //INLINE_CODE
                8 -> {
                    //text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val element = Element2.InlineCode(text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //LINK
                9 -> {                      // 01:24
                    //full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title: kotlin.String, link: kotlin.String) = "\\[(.*)]\\((.*)\\)".toRegex()
                        .find(text)!!.destructured
                    val element = Element2.Link(link, title)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //10 -> BLOCK CODE - optionally
                10 -> {
                    text = string.subSequence(startIndex.plus(3), endIndex.minus(3)).toString()

                    if (text.contains(LINE_SEPARATOR)) {
                        for ((index, line) in text.lines().withIndex()) {
                            when (index) {
                                text.lines().lastIndex -> parents.add(
                                    Element2.BlockCode(
                                        Element2.BlockCode.Type.END,
                                        line
                                    )
                                )
                                0 -> parents.add(
                                    Element2.BlockCode(
                                        Element2.BlockCode.Type.START,
                                        line + LINE_SEPARATOR
                                    )
                                )
                                else -> parents.add(
                                    Element2.BlockCode(
                                        Element2.BlockCode.Type.MIDDLE,
                                        line + LINE_SEPARATOR
                                    )
                                )
                            }
                        }

                    } else parents.add(Element2.BlockCode(Element2.BlockCode.Type.SINGLE, text))
                    lastStartIndex = endIndex
                }

                //11 -> NUMERIC LIST
                11 -> {
                    val reg = "(^\\d{1,2}.)".toRegex().find(string.substring(startIndex, endIndex))
                    val order = reg!!.value
                    text =
                        string.subSequence(startIndex.plus(order.length.inc()), endIndex).toString()

                    val subs = findElements(text)
                    val element = Element2.OrderedListItem(order, text.toString(), subs)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

            }
        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element2.Text(text))
        }

        return parents
    }
}


data class MarkdownText2(val elements: List<Element2>)

sealed class Element2() {
    abstract val text: CharSequence
    abstract val elements: List<Element2>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class Rule(
        override val text: CharSequence = " ", //for insert span
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class InlineCode(
        override val text: CharSequence, //for insert span
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class Link(
        val link: String,
        override val text: CharSequence, //for insert span
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element2> = emptyList()
    ) : Element2() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }
}