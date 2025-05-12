package eu.kanade.tachiyomi.extension.id.parser.bitmap

public interface Bitmap {

	public val width: Int
	public val height: Int

	public fun drawBitmap(sourceBitmap: Bitmap, src: Rect, dst: Rect)
}
