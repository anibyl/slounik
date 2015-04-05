package org.anibyl.slounik;

import android.app.Activity;
import android.provider.Settings;

/**
 * General application utility class.
 *
 * Created by Usievaład Čorny on 05.04.2015 4:13.
 */
public class Util {
    private static Boolean testDevice;

    public static void initialize(Activity activity) {
        getTestDevice(activity);
    }

    public static Boolean isTestDevice() {
        return testDevice;
    }

    private static void getTestDevice(Activity activity) {
        Server.getTestDevice(Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID),
                new Server.BooleanCallback() {
                    @Override
                    public void invoke(Boolean bool) {
                        testDevice = bool;
                    }
                },
                activity);
    }
}
