package org.anibyl.slounik.data

/**
 * @author Sieva Kimajeŭ
 * @created 2019-08-08
 */
interface FullDescriptionLoader {
	fun loadArticleDescription(article: Article, callback: ArticlesCallback)
}
