package org.anibyl.slounik.util

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable

/**
 * Created by anibyl on 18.8.16.
 */
class StubDrawable:Drawable() {
	override fun draw(canvas:Canvas?) {
	}

	override fun setAlpha(alpha:Int) {
	}

	override fun getOpacity():Int {
		return 0;
	}

	override fun setColorFilter(colorFilter:ColorFilter?) {
	}
}