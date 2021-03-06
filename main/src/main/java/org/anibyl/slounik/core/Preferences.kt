package org.anibyl.slounik.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Shared preferences of the application.
 *
 * @author Usievaład Kimajeŭ
 * @created 03.11.2015
 */
class Preferences(context: Context) {
	private val manager: PreferencesManager = PreferencesManager(
			context.getSharedPreferences("org.anibyl.slounik", Context.MODE_PRIVATE)
	)

	@Deprecated("Slounik server is deprecated.")
	private val USE_SLOUNIK_SERVER = "use_slounik_server"
	private val USE_SLOUNIK_ORG = "use_slounik_org"
	private val USE_SKARNIK = "use_skarnik"
	private val USE_RODNYJA_VOBRAZY = "use_rodnyja_vobrazy"
	private val USE_ENGBEL = "use_engbel"
	private val SEARCH_IN_TITLES = "search_in_titles"
	private val ENGBEL_INITIALIZED = "engbel_initialized"

	@Deprecated("Slounik server is deprecated.")
	var useSlounikServer: Boolean
		get() = manager.getBoolean(USE_SLOUNIK_SERVER, true)
		set(value) = manager.save(USE_SLOUNIK_SERVER, value)

	var useSlounikOrg: Boolean
		get() = manager.getBoolean(USE_SLOUNIK_ORG, true)
		set(useSlounikOrg) {
			manager.save(USE_SLOUNIK_ORG, useSlounikOrg)
		}

	var useSkarnik: Boolean
		get() = manager.getBoolean(USE_SKARNIK, true)
		set(useSkarnik) {
			manager.save(USE_SKARNIK, useSkarnik)
		}

	var useRodnyjaVobrazy: Boolean
		get() = manager.getBoolean(USE_RODNYJA_VOBRAZY, true)
		set(useRodnyjaVobrazy) {
			manager.save(USE_RODNYJA_VOBRAZY, useRodnyjaVobrazy)
		}

	var useEngBel: Boolean
		get() = manager.getBoolean(USE_ENGBEL, useSlounikServer)
		set(value) = manager.save(USE_ENGBEL, value)

	var searchInTitles: Boolean
		get() = manager.getBoolean(SEARCH_IN_TITLES)
		set(searchInTitles) {
			manager.save(SEARCH_IN_TITLES, searchInTitles)
		}

	var engBelInitialized: Boolean
		get() = manager.getBoolean(ENGBEL_INITIALIZED)
		set(value) {
			manager.save(ENGBEL_INITIALIZED, value)
		}

	private class PreferencesManager(private val sharedPreferences: SharedPreferences) {
		fun save(key: String, value: Boolean) {
			apply(edit().putBoolean(key, value))
		}

		fun save(key: String, value: String?) {
			apply(edit().putString(key, value))
		}

		fun getBoolean(key: String): Boolean {
			return sharedPreferences.getBoolean(key, false)
		}

		fun getBoolean(key: String, defaultValue: Boolean): Boolean {
			return sharedPreferences.getBoolean(key, defaultValue)
		}

		fun getString(key: String): String? {
			return sharedPreferences.getString(key, null)
		}

		fun edit(): SharedPreferences.Editor {
			return sharedPreferences.edit()
		}

		fun apply(editor: SharedPreferences.Editor) {
			editor.apply()
		}
	}
}
