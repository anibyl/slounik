package org.anibyl.slounik.network

import android.text.Spanned

import java.io.Serializable

/**
 * Article.
 *
 * @author Usievaład Kimajeŭ
 * @created 26.02.2015
 */
abstract class Article(@Transient val communicator: DictionarySiteCommunicator<ArticlesCallback>) : Serializable {
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

	internal abstract fun fill(): Article
}
