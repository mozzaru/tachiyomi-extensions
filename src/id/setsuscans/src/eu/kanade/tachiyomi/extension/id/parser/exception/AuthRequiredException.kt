package eu.kanade.tachiyomi.extension.id.parser.exception

import okio.IOException
import eu.kanade.tachiyomi.extension.id.parser.InternalParsersApi
import eu.kanade.tachiyomi.extension.id.parser.model.MangaSource

/**
 * Authorization is required for access to the requested content
 */
public class AuthRequiredException @InternalParsersApi @JvmOverloads constructor(
	public val source: MangaSource,
	cause: Throwable? = null,
) : IOException("Authorization required", cause)
