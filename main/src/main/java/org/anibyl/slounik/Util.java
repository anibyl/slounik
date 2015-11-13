package org.anibyl.slounik;

import android.content.Context;
import android.provider.Settings;

/**
 * General application utility class.
 *
 * Created by Usievaład Čorny on 05.04.2015 4:13.
 */
public class Util {
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
