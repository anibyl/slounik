package org.anibyl.slounik.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import org.anibyl.slounik.R


/**
 * About information dialog.
 *
 * @author Usievaład Kimajeŭ
 * @created 26.12.2016
 */
class AboutDialog : DialogFragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.about, container, false)

		val homepageButton = view.findViewById(R.id.about_dialog_homepage_button) as Button
		val closeButton = view.findViewById(R.id.about_dialog_close_button) as Button

		homepageButton.setOnClickListener {
			startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.homepage))))
		}

		closeButton.setOnClickListener {
			dismiss()
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