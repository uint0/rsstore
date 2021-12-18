package dev.uint0.rsstore.helpers

import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

suspend fun makeHttpRequest(uri: URI, timeout: Duration = Duration.ofSeconds(5)): HttpResponse<String> {
    val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NEVER)
        .connectTimeout(timeout)
        .build()
    val request = HttpRequest.newBuilder()
        .timeout(timeout)
        .uri(uri)
        .build()

    val resp = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    return resp.await()
}
