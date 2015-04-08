package org.anibyl.slounik;

import android.content.Context;
import android.provider.Settings;
import org.anibyl.slounik.network.Server;

/**
 * General application utility class.
 *
 * Created by Usievaład Čorny on 05.04.2015 4:13.
 */
public class Util {
    private static Boolean testDevice;

    public static void initialize(Context context) {
        getTestDevice(context);
    }

    public static Boolean isTestDevice() {
        return testDevice;
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static void getTestDevice(Context context) {
        Server.getTestDevice(getAndroidId(context),
                new Server.BooleanCallback() {
                    @Override
                    public void invoke(Boolean bool) {
                        testDevice = bool;
                    }
                },
                context);
    }
}
