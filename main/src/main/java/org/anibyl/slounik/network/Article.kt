package org.anibyl.slounik.network

import android.text.Spanned

import java.io.Serializable

/**
 * Article.
 *
 * @author Usievaład Kimajeŭ
 * @created 26.02.2015
 */
class Article(@Transient val communicator: DictionarySiteCommunicator) : Serializable {
	var title: String? = null
		internal set
	var description: Spanned? = null
		internal set
	var dictionary: String? = null
		internal set
	var linkToFullDescription: String? = null
		internal set
	var fullDescription: Spanned? = null
		internal set
}

fun Article.loadArticleDescription(callback: (ArticlesInfo) -> Unit) {
	this.communicator.loadArticleDescription(this, object : ArticlesCallback {
		override fun invoke(info: ArticlesInfo) = callback(info)
	})
}
