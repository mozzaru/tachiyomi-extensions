package eu.kanade.tachiyomi.extension.id.parser.config

public interface MangaSourceConfig {

	public operator fun <T> get(key: ConfigKey<T>): T
}
