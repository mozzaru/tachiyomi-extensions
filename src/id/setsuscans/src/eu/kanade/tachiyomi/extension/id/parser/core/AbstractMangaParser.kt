package eu.kanade.tachiyomi.extension.id.parser.core

import androidx.annotation.CallSuper
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import eu.kanade.tachiyomi.extension.id.parser.InternalParsersApi
import eu.kanade.tachiyomi.extension.id.parser.MangaLoaderContext
import eu.kanade.tachiyomi.extension.id.parser.MangaParser
import eu.kanade.tachiyomi.extension.id.parser.config.ConfigKey
import eu.kanade.tachiyomi.extension.id.parser.config.MangaSourceConfig
import eu.kanade.tachiyomi.extension.id.parser.model.*
import eu.kanade.tachiyomi.extension.id.parser.network.OkHttpWebClient
import eu.kanade.tachiyomi.extension.id.parser.network.WebClient
import eu.kanade.tachiyomi.extension.id.parser.util.FaviconParser
import eu.kanade.tachiyomi.extension.id.parser.util.LinkResolver
import eu.kanade.tachiyomi.extension.id.parser.util.RelatedMangaFinder
import eu.kanade.tachiyomi.extension.id.parser.util.toAbsoluteUrl
import java.util.*

@InternalParsersApi
public abstract class AbstractMangaParser @InternalParsersApi constructor(
	@property:InternalParsersApi public val context: MangaLoaderContext,
	public final override val source: MangaParserSource,
) : MangaParser {

	public override val config: MangaSourceConfig by lazy { context.getConfig(source) }

	public open val sourceLocale: Locale
		get() = if (source.locale.isEmpty()) Locale.ROOT else Locale(source.locale)

	protected open val userAgentKey: ConfigKey.UserAgent = ConfigKey.UserAgent(context.getDefaultUserAgent())

	protected val sourceContentRating: ContentRating?
		get() = if (source.contentType == ContentType.HENTAI) {
			ContentRating.ADULT
		} else {
			null
		}

	final override val domain: String
		get() = config[configKeyDomain]

	@Deprecated("Override intercept() instead")
	override fun getRequestHeaders(): Headers = Headers.Builder()
		.add("User-Agent", config[userAgentKey])
		.build()

	/**
	 * Used as fallback if value of `order` passed to [getList] is null
	 */
	public open val defaultSortOrder: SortOrder
		get() {
			val supported = availableSortOrders
			return SortOrder.entries.first { it in supported }
		}

	@JvmField
	protected val webClient: WebClient = OkHttpWebClient(context.httpClient, source)

	/**
	 * Fetch direct link to the page image.
	 */
	public override suspend fun getPageUrl(page: MangaPage): String = page.url.toAbsoluteUrl(domain)

	/**
	 * Parse favicons from the main page of the source`s website
	 */
	public override suspend fun getFavicons(): Favicons {
		return FaviconParser(webClient, domain).parseFavicons()
	}

	@CallSuper
	public override fun onCreateConfig(keys: MutableCollection<ConfigKey<*>>) {
		keys.add(configKeyDomain)
	}

	public override suspend fun getRelatedManga(seed: Manga): List<Manga> {
		return RelatedMangaFinder(listOf(this)).invoke(seed)
	}

	/**
	 * Return [Manga] object by web link to it
	 * @see [Manga.publicUrl]
	 */
	override suspend fun resolveLink(resolver: LinkResolver, link: HttpUrl): Manga? = null

	override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(chain.request())
}
