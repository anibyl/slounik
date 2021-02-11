package org.anibyl.slounik

import android.app.Application
import org.anibyl.slounik.di.ApplicationComponent
import org.anibyl.slounik.di.ApplicationModule
import org.anibyl.slounik.di.DaggerApplicationComponent

/**
 * @author Sieva Kimajeŭ
 * @created 26.12.2016
 */
class SlounikApplication : Application() {
	companion object {
		@JvmStatic lateinit var graph: ApplicationComponent
	}

	override fun onCreate() {
		super.onCreate()

		graph = DaggerApplicationComponent.builder()
				.applicationModule(ApplicationModule(this))
				.build()
		graph.inject(this)
	}
}
