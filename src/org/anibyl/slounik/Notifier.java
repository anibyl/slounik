package org.anibyl.slounik;

import android.app.Activity;
import android.widget.Toast;

/**
 * Application notifier.
 *
 * Created by Usievaład Čorny on 05.04.2015 5:14.
 */
public class Notifier {
    public static void toast(Activity activity, String text) {
        toast(activity, text, Toast.LENGTH_SHORT);
    }

    public static void toast(Activity activity, String text, int length) {
        if (Util.isTestDevice()) {
            Toast.makeText(activity, text, length).show();
        }
    }
}
