package org.anibyl.slounik.di

import dagger.Component
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.activities.NavigationDrawerFragment
import org.anibyl.slounik.activities.SlounikActivity
import org.anibyl.slounik.activities.SlounikActivityPresenter
import org.anibyl.slounik.data.db.engbel.EngBel
import org.anibyl.slounik.data.network.RodnyjaVobrazy
import org.anibyl.slounik.data.network.Skarnik
import org.anibyl.slounik.data.network.SlounikOrg
import org.anibyl.slounik.dialogs.ArticleDialog
import javax.inject.Singleton

/**
 * @author Usievaład Kimajeŭ
 * @created 26.12.2016
 */
@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
	fun inject(application: SlounikApplication)
	fun inject(activity: SlounikActivity)
	fun inject(slounikActivityPresenter: SlounikActivityPresenter)
	fun inject(navigationDrawerFragment: NavigationDrawerFragment)
	fun inject(slounikOrg: SlounikOrg)
	fun inject(skarnik: Skarnik)
	fun inject(notifier: Notifier)
	fun inject(articleDialog: ArticleDialog)
	fun inject(rodnyjaVobrazy: RodnyjaVobrazy)
	fun inject(engBel: EngBel)
}
