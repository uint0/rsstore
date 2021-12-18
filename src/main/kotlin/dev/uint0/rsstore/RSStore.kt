package dev.uint0.rsstore

import dev.uint0.rsstore.feeds.BaseItem
import dev.uint0.rsstore.helpers.XMLUtil
import dev.uint0.rsstore.helpers.makeHttpRequest
import org.w3c.dom.Element
import java.net.URI

data class RssFeed<T : BaseItem> (
    val title: String,
    val description: String,
    val link: String,
    val items: List<T>
)

abstract class RSStore<T : BaseItem> {
    suspend fun fetchRssFeed(feedLocation: URI): List<RssFeed<T>> {
        val rssXML = makeHttpRequest(feedLocation)
        val document = XMLUtil(rssXML.body())

        // NOTE: intentionally not spec compliant - I wanna learn kotlin not rss
        val channels = document.getChildren("channel")

        return channels.map {
            RssFeed(
                title = it.getChild("title").text,
                description = it.getChild("description").text,
                link = it.getChild("link").text,
                items = getItems(it)
            )
        }
    }

    fun getItems(channel: XMLUtil): List<T> =
        channel.getChildren("item").map { this.constructItem(it.element) }

    abstract fun constructItem(item: Element): T
}
