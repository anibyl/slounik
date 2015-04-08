package org.anibyl.slounik;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Application notifier.
 *
 * Created by Usievaład Čorny on 05.04.2015 5:14.
 */
public class Notifier {
    public static void toast(Context context, String text) {
        toast(context, text, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String text, int length) {
        if (Util.isTestDevice()) {
            Toast.makeText(context, text, length).show();
        }
    }

    public static void log(String message) {
        if (Util.isTestDevice()) {
            Log.d("Slounik", message);
        }
    }
}
