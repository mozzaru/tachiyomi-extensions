@file:JvmName("MangaParsersUtils")

package eu.kanade.tachiyomi.extension.id.parser.util

import eu.kanade.tachiyomi.extension.id.parser.model.MangaChapter
import eu.kanade.tachiyomi.extension.id.parser.model.MangaListFilter
import kotlin.contracts.contract

public fun MangaListFilter?.isNullOrEmpty(): Boolean {
	contract {
		returns(false) implies (this@isNullOrEmpty != null)
	}
	return this == null || this.isEmpty()
}

public fun Collection<MangaChapter>.findById(chapterId: Long): MangaChapter? = find { x ->
	x.id == chapterId
}
