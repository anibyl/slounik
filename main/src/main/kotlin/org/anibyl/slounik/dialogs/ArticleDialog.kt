package org.anibyl.slounik.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.core.copyToClipboard
import org.anibyl.slounik.data.Article
import org.anibyl.slounik.data.ArticlesInfo
import org.anibyl.slounik.ui.ProgressBar
import javax.inject.Inject

/**
 * Dialog for the text of an article.
 *
 * @author Sieva Kimajeŭ
 * @created 01.03.2015
 */
class ArticleDialog : DialogFragment() {
	@Inject lateinit var notifier: Notifier

	private lateinit var article: Article

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		SlounikApplication.graph.inject(this)

		article = arguments?.getSerializable("article") as Article?
				?: savedInstanceState?.getSerializable("article") as Article
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.article, container, false)

		val isLoadable = article.linkToFullDescription != null

		val title = view.findViewById(R.id.article_title) as TextView
		val dictionary = view.findViewById(R.id.article_dictionary) as TextView
		val description = view.findViewById(R.id.article_description) as TextView
		val closeButton = view.findViewById(R.id.article_button_close) as Button
		val loadButton = view.findViewById(R.id.article_button_load) as Button
		val progressBar = view.findViewById(R.id.article_progress) as ProgressBar

		title.text = article.title
		dictionary.text = article.dictionary
		description.text = article.spannedDescription

		description.movementMethod = ScrollingMovementMethod()

		description.setOnLongClickListener {
			context?.copyToClipboard(description.text)

			notifier.toast(R.string.toast_text_copied)
			true
		}

		closeButton.setOnClickListener { dismiss() }

		if (isLoadable) {
			loadButton.setOnClickListener {
				loadButton.isEnabled = false

				if (article.fullDescription == null) {
					progressBar.progressiveStart()
					article.loadArticleDescription { info: ArticlesInfo ->
						when (info.status) {
							ArticlesInfo.Status.SUCCESS -> description.text = article.spannedFullDescription

							else -> loadButton.isEnabled = true
						}
						progressBar.progressiveStop()
					}
				} else {
					description.text = article.spannedFullDescription
				}
			}
		} else {
			loadButton.visibility = View.GONE
			progressBar.visibility = View.GONE
		}

		isCancelable = true

		return view
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val dialog = super.onCreateDialog(savedInstanceState)

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

		return dialog
	}
}

fun newArticleDialog(article: Article): ArticleDialog {
	val instance = ArticleDialog()

	val args = Bundle()
	args.putSerializable("article", article)
	instance.arguments = args

	return instance
}
