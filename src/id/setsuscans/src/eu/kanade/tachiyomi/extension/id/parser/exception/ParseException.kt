package eu.kanade.tachiyomi.extension.id.parser.exception

import eu.kanade.tachiyomi.extension.id.parser.InternalParsersApi

public class ParseException @InternalParsersApi @JvmOverloads constructor(
	public val shortMessage: String?,
	public val url: String,
	cause: Throwable? = null,
) : RuntimeException("$shortMessage at $url", cause)
