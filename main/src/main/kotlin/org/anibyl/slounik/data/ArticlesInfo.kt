package org.anibyl.slounik.data

import org.anibyl.slounik.data.ArticlesInfo.Status.FINISHED

/**
 * Represents article information.

 * Contains articles and connected information.

 * @author Sieva Kimajeŭ
 * @created 2015-04-29
 */
class ArticlesInfo {
	enum class Status {
		FINISHED,
		IN_PROCESS
	}

	val articles: List<Article>
	val status: Status

	constructor(articles: List<Article>, status: Status) {
		this.articles = articles
		this.status = status
	}

	constructor(articles: List<Article>) {
		this.articles = articles
		this.status = FINISHED
	}

	constructor(article: Article) {
		this.articles = listOf(article)
		this.status = FINISHED
	}

	constructor(article: Article?, status: Status) {
		this.articles = if (article == null) emptyList() else listOf(article)
		this.status = status
	}

	constructor(status: Status) {
		this.articles = emptyList()
		this.status = status
	}
}
