package org.anibyl.slounik.core

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

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

	private val USE_SLOUNIK_ORG = "use_slounik_org"
	private val USE_SKARNIK = "use_skarnik"
	private val USE_RODNYJA_VOBRAZY = "use_rodnyja_vobrazy"
	private val SEARCH_IN_TITLES = "search_in_titles"

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

	var searchInTitles: Boolean
		get() = manager.getBoolean(SEARCH_IN_TITLES)
		set(searchInTitles) {
			manager.save(SEARCH_IN_TITLES, searchInTitles)
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
