package dev.uint0.rsstore.writers

import dev.uint0.rsstore.RssFeed
import dev.uint0.rsstore.feeds.BaseItem
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.serializer

class JSONChannelWriter<T : BaseItem>(prefix: String) : ChannelWriter<T>(prefix) {
    override fun writeItems(items: List<T>, serializer: KSerializer<T>) {
        val latestHash = getLatestHash()
        val toWrite = items.takeWhile { hashItem(it) != latestHash }
        val itemSerializer = ListSerializer(serializer)

        if (toWrite.isNotEmpty()) {
            val outputFile = dataFile.toFile()
            outputFile.createNewFile()
            Json.encodeToStream(
                itemSerializer,
                items.takeWhile { hashItem(it) != latestHash },
                outputFile.outputStream()
            )
        }
    }
}

@OptIn(InternalSerializationApi::class)
inline fun <reified T : BaseItem> channelToJson(artifactLocation: String, feed: RssFeed<T>) {
    JSONChannelWriter<T>(artifactLocation).writeChannel(feed, T::class.serializer())
}
