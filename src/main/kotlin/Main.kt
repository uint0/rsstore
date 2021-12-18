import dev.uint0.rsstore.feeds.NyaaRSStore
import dev.uint0.rsstore.writers.channelToJson
import dev.uint0.rsstore.writers.channelToJsonlinesStream
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking
import java.net.URI

enum class FeedFormat {
    NyaaSI,
}

enum class OutputFormat {
    JSON,
    JSONLines,
}

data class CLIArgs(
    val feedURL: String,
    val feedFormat: FeedFormat,
    val outputFormat: OutputFormat,
    val outputDir: String,
)

fun getArgs(args: Array<String>): CLIArgs {
    val parser = ArgParser("rsstore")
    val feedURL by parser.option(
        ArgType.String,
        shortName = "u",
        fullName = "feed-url",
        description = "Feed url"
    ).required()
    val feedFormat by parser.option(
        ArgType.Choice<FeedFormat>(),
        shortName = "f",
        fullName = "feed-format",
        description = "Feed format"
    ).required()
    val outputDir by parser.option(
        ArgType.String,
        shortName = "o",
        fullName = "output",
        description = "Output Directory"
    ).required()
    val outputFormat by parser.option(
        ArgType.Choice<OutputFormat>(),
        shortName = "F",
        fullName = "output-format",
        description = "Output Format"
    ).required()

    parser.parse(args)

    return CLIArgs(
        feedURL = feedURL,
        feedFormat = feedFormat,
        outputDir = outputDir,
        outputFormat = outputFormat,
    )
}

fun main(args: Array<String>) {
    val opts = getArgs(args)
    val rsStore = when (opts.feedFormat) {
        FeedFormat.NyaaSI -> NyaaRSStore()
    }
    val feed = runBlocking {
        rsStore.fetchRssFeed(URI(opts.feedURL))
    }
    val firstChannel = feed.firstOrNull() ?: throw IllegalArgumentException("No channels found")
    when (opts.outputFormat) {
        OutputFormat.JSON -> channelToJson(opts.outputDir, firstChannel)
        OutputFormat.JSONLines -> channelToJsonlinesStream(opts.outputDir, firstChannel)
    }
}
