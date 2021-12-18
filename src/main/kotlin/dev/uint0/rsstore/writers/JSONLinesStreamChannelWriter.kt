package dev.uint0.rsstore.writers

import dev.uint0.rsstore.RssFeed
import dev.uint0.rsstore.feeds.BaseItem
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

class JSONLinesStreamChannelWriter<T : BaseItem>(prefix: String) : ChannelWriter<T>(prefix) {
    override val dataFile: Path = Paths.get(prefix, "data.jsonlines")

    override fun setupPrefix() {
        super.setupPrefix()
        if (dataFile.exists()) {
            dataFile.toFile().createNewFile()
        }
    }

    override fun writeItems(items: List<T>, serializer: KSerializer<T>) {
        val latestHash = getLatestHash()
        val toWrite = items.takeWhile { hashItem(it) != latestHash }
        val writer = dataFile.toFile()

        toWrite.forEach {
            writer.appendText(
                // Shitty hack who cares
                "${Json.encodeToString(serializer, it).removeSurrounding("[", "]")}\n"
            )
        }
    }
}

@OptIn(InternalSerializationApi::class)
inline fun <reified T : BaseItem> channelToJsonlinesStream(artifactLocation: String, feed: RssFeed<T>) {
    JSONLinesStreamChannelWriter<T>(artifactLocation).writeChannel(feed, T::class.serializer())
}
