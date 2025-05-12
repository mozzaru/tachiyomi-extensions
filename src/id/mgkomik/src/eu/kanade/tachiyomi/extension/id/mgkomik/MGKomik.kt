package eu.kanade.tachiyomi.extension.id.mgkomik

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.interceptor.rateLimit
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import okhttp3.*
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class MGKomik : (
    "MG Komik",
    "https://mgkomik.org",
    "id",
    SimpleDateFormat("dd MMM yy", Locale.US),
) {
    override val useLoadMoreRequest = LoadMoreStrategy.Never
    override val useNewChapterEndpoint = false
    override val mangaSubString = "komik"

    override fun headersBuilder() = super.headersBuilder().apply {
        add("User -Agent", randomUser Agent())
        add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
        add("Accept-Language", "en-US,en;q=0.9,id;q=0.8")
        add("Sec-Fetch-Dest", "document")
        add("Sec-Fetch-Mode", "navigate")
        add("Sec-Fetch-Site", "same-origin")
        add("Sec-Fetch-User ", "?1")
        add("Upgrade-Insecure-Requests", "1")
    }

    override val client = network.cloudflareClient.newBuilder()
        .cookieJar(object : CookieJar {
            private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val host = url.host
                if (cookieStore[host] == null) {
                    cookieStore[host] = mutableListOf()
                }
                cookieStore[host]?.removeAll { cookie -> cookies.any { it.name == cookie.name } }
                cookieStore[host]?.addAll(cookies)
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore[url.host] ?: emptyList()
            }
        })
        .addInterceptor { chain ->
            var request = chain.request()

            // Memastikan tidak ada "X-Requested-With" di header request sebelum dikirim
            val newHeaders = request.headers.newBuilder().apply {
                removeAll("X-Requested-With")
            }.build()
            request = request.newBuilder().headers(newHeaders).build()

            val response = chain.proceed(request)

            // Jika Cloudflare challenge (biasanya 503) ditemukan, lakukan retry dengan delay
            if (response.code == 503 && response.header("Server")?.contains("cloudflare", ignoreCase = true) == true) {
                response.close()
                try {
                    Thread.sleep(5000) // delay 5 detik tunggu Cloudflare selesai challenge
                } catch (e: InterruptedException) {
                    // ignore
                }
                // Retry satu kali kemudian lanjutkan
                return@addInterceptor chain.proceed(request)
            }
            response
        }
        .rateLimit(9, 2)
        .build()

    // ================================== Popular ======================================
    override fun popularMangaNextPageSelector() = ".wp-pagenavi span.current + a"

    // ================================== Latest =======================================
    override fun latestUpdatesRequest(page: Int): Request =
        if (useLoadMoreRequest()) {
            loadMoreRequest(page, popular = false)
        } else {
            GET("$baseUrl/$mangaSubString/${searchPage(page)}", headers)
        }

    // ================================== Search =======================================
    override fun searchRequest(page: Int, query: String, filters: FilterList): Request {
        filters.forEach { filter ->
            when (filter)