package org.anibyl.slounik.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Shared preferences of the application.
 * <p/>
 * Created by Usievaład Čorny on 3.11.2015 18:10.
 */
public class Preferences {
    private static final String LANGUAGE = "language";
    private static final String USE_SLOUNIK_ORG = "use_slounik_org";
    private static final String USE_SKARNIK = "use_skarnik";
    private static final String SEARCH_IN_TITLES = "search_in_titles";

    private static PreferencesManager manager;

    public static void initialize(Context context) {
        manager = new PreferencesManager(context.getSharedPreferences("org.anibyl.slounik", Context.MODE_PRIVATE));
    }

    public static void setLanguage(String language) {
        manager.save(LANGUAGE, language);
    }

    public static String getLanguage() {
        return manager.getString(LANGUAGE);
    }

    public static void setUseSlounikOrg(boolean useSlounikOrg) {
        manager.save(USE_SLOUNIK_ORG, useSlounikOrg);
    }

    public static boolean getUseSlounikOrg() {
        return manager.getBoolean(USE_SLOUNIK_ORG, true);
    }

    public static void setUseSkarnik(boolean useSkarnik) {
        manager.save(USE_SKARNIK, useSkarnik);
    }

    public static boolean getUseSkarnik() {
        return manager.getBoolean(USE_SKARNIK, true);
    }

    public static void setSearchInTitles(boolean searchInTitles) {
        manager.save(SEARCH_IN_TITLES, searchInTitles);
    }

    public static boolean getSearchInTitles() {
        return manager.getBoolean(SEARCH_IN_TITLES);
    }

    private static class PreferencesManager {
        private SharedPreferences sharedPreferences;

        public PreferencesManager(SharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
        }

        private void save(String key, boolean value) {
            apply(edit().putBoolean(key, value));
        }

        private void save(String key, String value) {
            apply(edit().putString(key, value));
        }

        private boolean getBoolean(String key) {
            return sharedPreferences.getBoolean(key, false);
        }

        private boolean getBoolean(String key, boolean defaultValue) {
            return sharedPreferences.getBoolean(key, defaultValue);
        }

        private String getString(String key) {
            return sharedPreferences.getString(key, null);
        }

        private SharedPreferences.Editor edit() {
            return sharedPreferences.edit();
        }

        private static void apply(SharedPreferences.Editor editor) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }
}
