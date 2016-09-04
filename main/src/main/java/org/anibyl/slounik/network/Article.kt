package org.anibyl.slounik.network

import android.text.Spanned

import java.io.Serializable

/**
 * Article.
 *
 *
 * Created by Usievaład Čorny on 26.02.2015 14:06.
 */
abstract class Article(@Transient val communicator:DictionarySiteCommunicator):Serializable {
	var title:String? = null
		internal set
	var description:Spanned? = null
		internal set
	var dictionary:String? = null
		internal set
	var linkToFullDescription:String? = null
		internal set
	var fullDescription:Spanned? = null
		internal set

	internal abstract fun fill():Article
}