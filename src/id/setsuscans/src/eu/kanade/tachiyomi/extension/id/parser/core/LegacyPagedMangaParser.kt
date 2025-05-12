package eu.kanade.tachiyomi.extension.id.parser.core

import androidx.annotation.VisibleForTesting
import eu.kanade.tachiyomi.extension.id.parser.InternalParsersApi
import eu.kanade.tachiyomi.extension.id.parser.MangaLoaderContext
import eu.kanade.tachiyomi.extension.id.parser.model.Manga
import eu.kanade.tachiyomi.extension.id.parser.model.MangaListFilter
import eu.kanade.tachiyomi.extension.id.parser.model.MangaParserSource
import eu.kanade.tachiyomi.extension.id.parser.model.SortOrder
import eu.kanade.tachiyomi.extension.id.parser.util.Paginator

@InternalParsersApi
public abstract class LegacyPagedMangaParser(
	context: MangaLoaderContext,
	source: MangaParserSource,
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED) @JvmField public val pageSize: Int,
	searchPageSize: Int = pageSize,
) : LegacyMangaParser(context, source) {

	@JvmField
	protected val paginator: Paginator = Paginator(pageSize)

	@JvmField
	protected val searchPaginator: Paginator = Paginator(searchPageSize)

	final override suspend fun getList(offset: Int, order: SortOrder, filter: MangaListFilter): List<Manga> {
		return getList(
			paginator = if (filter.query.isNullOrEmpty()) {
				paginator
			} else {
				searchPaginator
			},
			offset = offset,
			order = order,
			filter = filter,
		)
	}

	public abstract suspend fun getListPage(page: Int, order: SortOrder, filter: MangaListFilter): List<Manga>

	protected fun setFirstPage(firstPage: Int, firstPageForSearch: Int = firstPage) {
		paginator.firstPage = firstPage
		searchPaginator.firstPage = firstPageForSearch
	}

	private suspend fun getList(
		paginator: Paginator,
		offset: Int,
		order: SortOrder,
		filter: MangaListFilter,
	): List<Manga> {
		val page = paginator.getPage(offset)
		val list = getListPage(page, order, filter)
		paginator.onListReceived(offset, page, list.size)
		return list
	}
}
