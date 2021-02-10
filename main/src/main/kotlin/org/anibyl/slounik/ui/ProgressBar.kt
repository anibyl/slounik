package org.anibyl.slounik.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable

/**
 * Slounik's progress bar.
 *
 * @author Usievaład Kimajeŭ
 * @created 21.12.2015
 */
class ProgressBar : SmoothProgressBar {
	private var invisible: Boolean
		get() { return visibility == View.INVISIBLE }
		set(value) { visibility = if (value) View.INVISIBLE else View.VISIBLE }

	constructor(context: Context) : super(context) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init()
	}

	override fun progressiveStart() {
		invisible = false
		super.progressiveStart()
	}

	override fun progressiveStop() {
		invisible = true
		super.progressiveStop()
	}

	private fun init() {
		visibility = View.INVISIBLE

		setSmoothProgressDrawableCallbacks(object : SmoothProgressDrawable.Callbacks {
			override fun onStop() {
				if (invisible) {
					visibility = View.INVISIBLE
				}
			}

			override fun onStart() {
				if (!invisible) {
					visibility = View.VISIBLE
				}
			}
		})
	}
}
