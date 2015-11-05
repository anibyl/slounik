package org.anibyl.slounik;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import org.anibyl.slounik.core.Preferences;

import java.util.Locale;

/**
 * Represents language switching logic.
 * <p>
 * Created by Usievaład Čorny on 3.11.2015.
 */
public class LanguageSwitcher {
    public static Language[] languages;

    /**
     * Initializes language switcher and switch the language if it is necessary.
     *
     * @param activity Current activity.
     * @return If language is switched.
     */
    public static boolean initialize(Activity activity) {
        String[] languageNames = activity.getResources().getStringArray(R.array.languages);

        languages = new Language[]{
                new Language("be_by", languageNames[0]),
                new Language("ru_ru", languageNames[1]),
                new Language("en_us", languageNames[2])
        };

        String preferredLanguage = Preferences.getLanguage();

        return preferredLanguage != null && set(activity, preferredLanguage);

    }

    public static int getPreferredNo() {
        String preferredLanguage = Preferences.getLanguage();
        if (preferredLanguage != null) {
            for (int i = 0; i < languages.length; i++) {
                if (languages[i].getId().equals(preferredLanguage)) {
                    return i;
                }
            }
        } else {
            String defaultLanguage = Locale.getDefault().toString().toLowerCase();
            for (int i = 0; i < languages.length; i++) {
                String language = languages[i].getId();
                if (defaultLanguage.equals(language)) {
                    return i;
                }
            }
        }

        return 0;
    }

    /**
     * Sets language by list position.
     *
     * @param activity Current activity.
     * @param languagePosition Language list position.
     * @return If language is switched.
     */
    public static boolean set(Activity activity, int languagePosition) {
        String language = languages[languagePosition].getId();
        return set(activity, language);
    }

    /**
     * Sets language by ID (like en_us).
     *
     * @param activity Current activity.
     * @param languageId Language ID.
     * @return If language is switched.
     */
    public static boolean set(Activity activity, String languageId) {
        if (!Locale.getDefault().toString().toLowerCase().equals(languageId)) {
            setLanguage(activity, languageId);
            return true;
        }

        return false;
    }

    private static void setLanguage(Activity activity, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getApplicationContext().getResources().updateConfiguration(config, null);

        Preferences.setLanguage(language);

        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    public static class Language {
        private String id;
        private String name;

        public Language(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
