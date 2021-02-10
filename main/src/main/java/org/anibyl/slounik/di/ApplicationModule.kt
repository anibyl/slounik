package org.anibyl.slounik.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import dagger.Module
import dagger.Provides
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.activities.SlounikActivityPresenter
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.data.BatchArticlesLoader
import org.anibyl.slounik.data.db.SlounikDb
import org.anibyl.slounik.data.db.engbel.EngBel
import org.anibyl.slounik.data.network.RodnyjaVobrazy
import org.anibyl.slounik.data.network.Server
import org.anibyl.slounik.data.network.Skarnik
import org.anibyl.slounik.data.network.SlounikOrg
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
	fun provideResources(): Resources {
		return application.resources
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
		return BatchArticlesLoader(EngBel(), SlounikOrg(), Skarnik(), RodnyjaVobrazy())
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

	@Provides
	@Singleton
	fun provideDb(): SlounikDb {
		return Room.databaseBuilder(application.applicationContext, SlounikDb::class.java, "slounik_db").build()
	}
}
