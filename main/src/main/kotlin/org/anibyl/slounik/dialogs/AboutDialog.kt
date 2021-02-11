package org.anibyl.slounik.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.anibyl.slounik.BuildConfig
import org.anibyl.slounik.R


/**
 * About information dialog.
 *
 * @author Sieva Kimajeŭ
 * @created 2016-12-26
 */
class AboutDialog : DialogFragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view: View = inflater.inflate(R.layout.about, container, false)

		val version: TextView = view.findViewById(R.id.about_dialog_version)
		val homepageButton = view.findViewById(R.id.about_dialog_homepage_button) as Button
		val betaParticipationButton = view.findViewById(R.id.about_dialog_beta_participation_button) as Button
		val closeButton = view.findViewById(R.id.about_dialog_close_button) as Button

		version.text = BuildConfig.VERSION_NAME

		homepageButton.setOnClickListener {
			startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.homepage))))
		}

		betaParticipationButton.setOnClickListener {
			startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.beta_participation_page))))
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
