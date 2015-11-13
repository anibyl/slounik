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
        toast(context, text, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String text, int length) {
        if (Server.isTestDevice()) {
            Toast.makeText(context, text, length).show();
        }
    }

    public static void log(String message) {
        if (Server.isTestDevice()) {
            Log.d("Slounik", message);
        }
    }
}
