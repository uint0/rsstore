package dev.uint0.rsstore.writers

import dev.uint0.rsstore.RssFeed
import dev.uint0.rsstore.feeds.BaseItem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import java.io.File
import java.nio.file.Paths
import java.security.MessageDigest
import java.time.Instant

@OptIn(ExperimentalSerializationApi::class)
abstract class ChannelWriter<T : BaseItem>(private val prefix: String) {
    val writeTime = Instant.now().toEpochMilli()
    val metadataFile = Paths.get(prefix, "metadata.cfg")
    open val dataFile = Paths.get(prefix, "$writeTime.json")

    open fun writeChannel(feed: RssFeed<T>, serializer: KSerializer<T>) {
        setupPrefix()
        writeItems(feed.items, serializer)
        writeMetadata(feed)
    }

    open fun setupPrefix() {
        val directory = File(prefix)
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    open fun writeMetadata(feed: RssFeed<T>) {
        val latestHash = if (feed.items.isEmpty()) "" else hashItem(feed.items.first())
        metadataFile.toFile().writeText(
            listOf(
                "title=${feed.title}",
                "description=${feed.description}",
                "link=${feed.link}",
                "writtenAt=$writeTime",
                "latestHash=$latestHash",
            ).joinToString("\n")
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    abstract fun writeItems(items: List<T>, serializer: KSerializer<T>)

    fun hashItem(item: T): String =
        MessageDigest.getInstance("sha-256").digest(
            item.id.toByteArray()
        ).joinToString(separator = "") { "%02x".format(it) }

    fun getLatestHash(): String {
        val file = metadataFile.toFile()
        if (!file.exists()) { return ""; }
        val metadata = file.readText().split('\n')
        val lastHashEntry = metadata.find { it.startsWith("latestHash=") }
        return lastHashEntry?.split('=', limit = 2)?.last() ?: ""
    }
}
