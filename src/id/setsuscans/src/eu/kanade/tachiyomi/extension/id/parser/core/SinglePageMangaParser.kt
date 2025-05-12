package eu.kanade.tachiyomi.extension.id.parser.core

import eu.kanade.tachiyomi.extension.id.parser.InternalParsersApi
import eu.kanade.tachiyomi.extension.id.parser.MangaLoaderContext
import eu.kanade.tachiyomi.extension.id.parser.model.Manga
import eu.kanade.tachiyomi.extension.id.parser.model.MangaParserSource
import eu.kanade.tachiyomi.extension.id.parser.model.search.MangaSearchQuery

@InternalParsersApi
public abstract class SinglePageMangaParser(
	context: MangaLoaderContext,
	source: MangaParserSource,
) : AbstractMangaParser(context, source) {

	final override suspend fun getList(query: MangaSearchQuery): List<Manga> {
		if (query.offset > 0) {
			return emptyList()
		}
		return getSinglePageList(query)
	}

	public abstract suspend fun getSinglePageList(searchQuery: MangaSearchQuery): List<Manga>
}
