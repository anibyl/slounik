package org.anibyl.slounik;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import org.anibyl.slounik.core.Preferences;

import java.util.Locale;

/**
 * Represents language switching logic.
 * <p/>
 * Created by Usievaład Čorny on 3.11.2015.
 */
public class LanguageSwitcher {
    public static Language[] languages;

    public static void initialize(Activity activity) {
        String[] languageNames = activity.getResources().getStringArray(R.array.languages);

        languages = new Language[]{
                new Language("be_BY", languageNames[0]),
                new Language("ru_RU", languageNames[1]),
                new Language("en_US", languageNames[2])
        };

        String preferredLanguage = Preferences.getLanguage();
        if (preferredLanguage != null) {
//            for (Language language : languages) {
//                if (language.getId().equals(preferredLanguage)) {
//                    set(activity, language.getId());
//                }
//            }
            Locale locale = new Locale(preferredLanguage);
            Locale.setDefault(locale);
        }
    }

    public static int getPreferredNo() {
        String preferredLanguage = Preferences.getLanguage();
        if (preferredLanguage != null) {
            for (int i = 0; i <= languages.length; i++) {
                if (languages[i].getId().equals(preferredLanguage)) {
                    return i;
                }
            }
        }

        return 0;
    }

    public static void set(Activity activity, int position) {
        String language = languages[position].getId();
        set(activity, language);
    }

    public static void set(Activity activity, String language) {
        Locale defaultLocale = Locale.getDefault();
        if (!Locale.getDefault().getLanguage().toLowerCase().equals(language.toLowerCase())) {
            setLanguage(activity, language);
        }
    }

    private static void setLanguage(Activity activity, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Locale defaultLocale = Locale.getDefault();
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
