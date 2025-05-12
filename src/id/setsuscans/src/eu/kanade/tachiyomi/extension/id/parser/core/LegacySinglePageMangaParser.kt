package eu.kanade.tachiyomi.extension.id.parser.core

import eu.kanade.tachiyomi.extension.id.parser.InternalParsersApi
import eu.kanade.tachiyomi.extension.id.parser.MangaLoaderContext
import eu.kanade.tachiyomi.extension.id.parser.model.Manga
import eu.kanade.tachiyomi.extension.id.parser.model.MangaListFilter
import eu.kanade.tachiyomi.extension.id.parser.model.MangaParserSource
import eu.kanade.tachiyomi.extension.id.parser.model.SortOrder

@InternalParsersApi
public abstract class LegacySinglePageMangaParser(
	context: MangaLoaderContext,
	source: MangaParserSource,
) : LegacyMangaParser(context, source) {

	final override suspend fun getList(offset: Int, order: SortOrder, filter: MangaListFilter): List<Manga> {
		if (offset > 0) {
			return emptyList()
		}
		return getList(order, filter)
	}

	public abstract suspend fun getList(order: SortOrder, filter: MangaListFilter): List<Manga>
}
