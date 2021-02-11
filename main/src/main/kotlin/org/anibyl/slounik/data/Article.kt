package org.anibyl.slounik.data

import android.text.Spanned
import org.anibyl.slounik.core.fromHtml
import java.io.Serializable

/**
 * Article.
 *
 * @author Sieva Kimajeŭ
 * @created 26.02.2015
 */
class Article(@Transient val fullDescriptionLoader: FullDescriptionLoader? = null) : Serializable {
	var title: String? = null
		internal set
	var description: String? = null
		internal set
	var dictionary: String? = null
		internal set
	var linkToFullDescription: String? = null
		internal set
	var fullDescription: String? = null
		internal set

	val spannedDescription: Spanned?
		get() = description?.fromHtml()
	val spannedFullDescription: Spanned?
		get() = fullDescription?.fromHtml()

	fun loadArticleDescription(callback: (ArticlesInfo) -> Unit) {
		fullDescriptionLoader?.loadArticleDescription(this, object : ArticlesCallback {
			override fun invoke(info: ArticlesInfo) = callback(info)
		})
	}
}
