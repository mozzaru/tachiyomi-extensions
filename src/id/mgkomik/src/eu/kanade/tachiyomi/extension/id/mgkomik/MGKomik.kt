package eu.kanade.tachiyomi.extension.id.mgkomik

import eu.kanade.tachiyomi.multisrc.madara.Madara
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.interceptor.rateLimit
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import okhttp3.Headers
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class MGKomik : Madara(
    "MG Komik",
    "https://mgkomik.org",
    "id",
    SimpleDateFormat("dd MMM yy", Locale.US),
) {
    override val tagPrefix = "genres/"
    override val listUrl = "komik/"
    override val datePattern = "dd MMM yy"
    override val stylePage = ""
    override val sourceLocale: Locale = Locale.ENGLISH

    private fun generateRandomString(length: Int): String {
        val charset = "HALOGaES.BCDFHIJKMNPQRTUVWXYZ.bcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    override fun getRequestHeaders(): Headers {
        val randomLength = Random.nextInt(13, 21)
        val randomString = generateRandomString(randomLength)
        return Headers.Builder()
            .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .add("Accept-Language", "en-US,en;q=0.9,id;q=0.8")
            .add("Sec-Fetch-Dest", "document")
            .add("Sec-Fetch-Mode", "navigate")
            .add("Sec-Fetch-Site", "same-origin")
            .add("Sec-Fetch-User ", "?1")
            .add("Upgrade-Insecure-Requests", "1")
            .add("X-Requested-With", randomString)
            .build()
    }
}
