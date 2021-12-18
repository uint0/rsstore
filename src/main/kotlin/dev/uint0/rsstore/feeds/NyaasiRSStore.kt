package dev.uint0.rsstore.feeds

import dev.uint0.rsstore.RSStore
import dev.uint0.rsstore.helpers.XMLUtil
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class NyaaItem(
    override val id: String,
    val title: String,
    val link: String,
    val description: String,
    val guid: String,
    val pubDate: Long,
    val seeders: Int,
    val leechers: Int,
    val downloads: Int,
    val infoHash: String,
    val categoryId: String,
    val category: String,
    val size: Long,
    val comments: Int,
    val trusted: Boolean,
    val remake: Boolean,
) : BaseItem

class NyaaRSStore : RSStore<NyaaItem>() {
    override fun constructItem(item: Element): NyaaItem {
        val itemWrapper = XMLUtil(item)
        return NyaaItem(
            id = itemWrapper.getChild("guid").text.split('/').last(),
            title = itemWrapper.getChild("title").text,
            link = itemWrapper.getChild("link").text,
            description = itemWrapper.getChild("description").text,
            guid = itemWrapper.getChild("guid").text,
            pubDate = parsePubDate(itemWrapper.getChild("pubDate").text),
            seeders = itemWrapper.getChild("nyaa:seeders").text.toInt(),
            leechers = itemWrapper.getChild("nyaa:leechers").text.toInt(),
            downloads = itemWrapper.getChild("nyaa:downloads").text.toInt(),
            infoHash = itemWrapper.getChild("nyaa:infoHash").text,
            categoryId = itemWrapper.getChild("nyaa:categoryId").text,
            category = itemWrapper.getChild("nyaa:category").text,
            size = parseFileSize(itemWrapper.getChild("nyaa:size").text),
            comments = itemWrapper.getChild("nyaa:comments").text.toInt(),
            trusted = parseBool(itemWrapper.getChild("nyaa:trusted").text),
            remake = parseBool(itemWrapper.getChild("nyaa:remake").text),
        )
    }

    private fun parsePubDate(pubDateStr: String) = OffsetDateTime.parse(
        pubDateStr,
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z")
    ).toEpochSecond()

    private fun parseFileSize(fileSizeStr: String): Long {
        val num = fileSizeStr.takeWhile { it.isDigit() || it == '.' }.toFloat()
        val suffix = fileSizeStr.takeLastWhile { it.isLetter() }

        return when (suffix) {
            "B" -> num
            "KB" -> 1000L * num
            "KiB" -> 1024L * num
            "MB" -> 1000L * 1000L * num
            "MiB" -> 1024L * 1024L * num
            "GB" -> 1000L * 1000L * 1000L * num
            "GiB" -> 1024L * 1024L * 1024L * num
            "TB" -> 1000L * 1000L * 1000L * 1000L * num
            "TiB" -> 1024L * 1024L * 1024L * 1024L * num
            else -> throw IllegalArgumentException("Unknown suffix $suffix for $fileSizeStr")
        }.toLong()
    }

    private fun parseBool(boolStr: String): Boolean = boolStr.lowercase() == "yes"
}
