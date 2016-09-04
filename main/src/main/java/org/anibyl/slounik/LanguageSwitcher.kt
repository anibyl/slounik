package org.anibyl.slounik

import android.app.Activity
import android.content.res.Configuration
import org.anibyl.slounik.core.Preferences
import java.util.Locale

/**
 * Represents language switching logic.
 *
 * Created by Usievaład Čorny on 3.11.2015.
 */
object LanguageSwitcher {
	var languages:Array<Language> = arrayOf()

	/**
	 * Initializes language switcher and switch the language if it is necessary.
	 *
	 * @param activity Current activity.
	 * @return If language is switched.
	 */
	fun initialize(activity:Activity):Boolean {
		val languageNames = activity.resources.getStringArray(R.array.languages)

		languages = arrayOf(Language("be_by", languageNames[0]),
				Language("ru_ru", languageNames[1]),
				Language("en_us", languageNames[2]))

		val preferredLanguage = Preferences.language

		return preferredLanguage != null && set(activity, preferredLanguage)

	}

	val preferredNo:Int
		get() {
			val preferredLanguage = Preferences.language
			if (preferredLanguage != null) {
				for (i in languages.indices) {
					if (languages[i].id == preferredLanguage) {
						return i
					}
				}
			} else {
				val defaultLanguage = Locale.getDefault().toString().toLowerCase()
				for (i in languages.indices) {
					val language = languages[i].id
					if (defaultLanguage == language) {
						return i
					}
				}
			}

			return 0
		}

	/**
	 * Sets language by list position.
	 *
	 * @param activity Current activity.
	 * @param languagePosition Language list position.
	 * @return If language is switched.
	 */
	operator fun set(activity:Activity, languagePosition:Int):Boolean {
		val language = languages[languagePosition].id
		return set(activity, language)
	}

	/**
	 * Sets language by ID (like en_us).
	 *
	 * @param activity Current activity.
	 * @param languageId Language ID.
	 * @return If language is switched.
	 */
	operator fun set(activity:Activity, languageId:String):Boolean {
		if (Locale.getDefault().toString().toLowerCase() != languageId) {
			setLanguage(activity, languageId)
			return true
		}

		return false
	}

	private fun setLanguage(activity:Activity, language:String) {
		val locale = Locale(language)
		Locale.setDefault(locale)
		val config = Configuration()
		config.locale = locale
		activity.applicationContext.resources.updateConfiguration(config, null)

		Preferences.language = language

		val intent = activity.intent
		activity.finish()
		activity.startActivity(intent)
	}

	class Language(val id:String, val name:String)
}
