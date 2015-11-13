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

    private static SharedPreferences sharedPreferences;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences("org.anibyl.slounik", Context.MODE_PRIVATE);
    }

    public static void setLanguage(String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sharedPreferences.edit().putString(LANGUAGE, language).apply();
        } else {
            sharedPreferences.edit().putString(LANGUAGE, language).commit();
        }
    }

    public static String getLanguage() {
        return sharedPreferences.getString(LANGUAGE, null);
    }
}
