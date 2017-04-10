package org.anibyl.slounik.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.activities.SlounikActivityPresenter
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.network.BatchArticlesLoader
import org.anibyl.slounik.network.RodnyjaVobrazy
import org.anibyl.slounik.network.Server
import org.anibyl.slounik.network.Skarnik
import org.anibyl.slounik.network.SlounikOrg
import javax.inject.Singleton

/**
 * @author Usievaład Kimajeŭ
 * @created 26.12.2016
 */
@Module
class ApplicationModule(val application: Application) {
	@Provides
	@Singleton
	fun provideApplicationContext(): Context {
		return application.applicationContext
	}

	@Provides
	@Singleton
	fun provideConfig(): Server.Config {
		return Server().loadConfig(application)
	}

	@Provides
	@Singleton
	fun provideNotifier(): Notifier {
		return Notifier()
	}

	@Provides
	@Singleton
	fun provideBatchArticlesLoader(): BatchArticlesLoader {
		return BatchArticlesLoader(SlounikOrg(), Skarnik(), RodnyjaVobrazy())
	}

	@Provides
	@Singleton
	fun providePreferences(): Preferences {
		return Preferences(application)
	}

	@Provides
	@Singleton
	fun provideSlounikActivityPresenter(): SlounikActivityPresenter {
		return SlounikActivityPresenter()
	}
}
