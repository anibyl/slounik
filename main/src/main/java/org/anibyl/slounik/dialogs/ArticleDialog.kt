package org.anibyl.slounik.dialogs

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.network.Article
import org.anibyl.slounik.network.ArticlesCallback
import org.anibyl.slounik.network.ArticlesInfo
import org.anibyl.slounik.ui.ProgressBar

/**
 * Dialog for the text of an article.
 *
 * Created by Usievaład Čorny on 01.03.2015 10:54.
 */
class ArticleDialog:DialogFragment() {
	private var article:Article? = null

	override fun onCreate(savedInstanceState:Bundle?) {
		super.onCreate(savedInstanceState)

		article = arguments.getSerializable("article") as Article?
				?: savedInstanceState?.getSerializable("article") as Article?
	}

	override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?,
			savedInstanceState:Bundle?):View? {
		val view = inflater!!.inflate(R.layout.article, container, false)

		val isLoadable = article!!.linkToFullDescription != null

		val dictionary = view.findViewById(R.id.dictionary) as TextView
		val description = view.findViewById(R.id.list_item_description) as TextView
		val closeButton = view.findViewById(R.id.article_button_close) as Button
		val loadButton = view.findViewById(R.id.article_button_load) as Button
		val progressBar = view.findViewById(R.id.article_progress) as ProgressBar

		dictionary.text = article!!.dictionary
		description.text = article!!.description

		description.movementMethod = ScrollingMovementMethod()

		description.setOnLongClickListener {
			val text = description.text
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
				val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
				clipboard.text = text
			} else {
				val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
				val clip = ClipData.newPlainText(article!!.description, text)
				clipboard.primaryClip = clip
			}
			Notifier.toast(context, R.string.toast_text_copied)
			true
		}

		closeButton.setOnClickListener { dismiss() }

		if (isLoadable) {
			loadButton.setOnClickListener {
				loadButton.isEnabled = false

				if (article!!.fullDescription == null) {
					progressBar.progressiveStart()
					article!!.communicator.loadArticleDescription(article!!, context, object:ArticlesCallback {
						override fun invoke(info:ArticlesInfo) {
							when (info.status) {
								ArticlesInfo.Status.SUCCESS -> description.text = article!!.fullDescription

								else -> loadButton.isEnabled = true
							}
							progressBar.progressiveStop()
						}
					})
				} else {
					description.text = article!!.fullDescription
				}
			}
		} else {
			loadButton.visibility = View.GONE
			progressBar.visibility = View.GONE
		}

		isCancelable = true

		return view
	}

	override fun onCreateDialog(savedInstanceState:Bundle?):Dialog {
		val dialog = super.onCreateDialog(savedInstanceState)

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

		return dialog
	}
}

fun newArticleDialog(article:Article):ArticleDialog {
	val instance = ArticleDialog()

	val args = Bundle()
	args.putSerializable("article", article)
	instance.arguments = args

	return instance
}