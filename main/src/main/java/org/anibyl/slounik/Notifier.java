package org.anibyl.slounik;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.anibyl.slounik.network.Server;

/**
 * Application notifier.
 * <p/>
 * Created by Usievaład Čorny on 05.04.2015 5:14.
 */
public class Notifier {
    public static void toast(Context context, String text) {
        toast(context, text, false);
    }

    public static void toast(Context context, int id) {
        toast(context, context.getResources().getString(id), false);
    }

    public static void toast(Context context, String text, boolean developerMode) {
        toast(context, text, developerMode, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String text, boolean developerMode, int length) {
        if (!developerMode || Server.isTestDevice()) {
            Toast.makeText(context, text, length).show();
        }
    }

    public static void log(String message) {
        if (Server.isTestDevice()) {
            Log.d("Slounik", message);
        }
    }
}