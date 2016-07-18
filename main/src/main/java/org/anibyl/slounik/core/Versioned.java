package org.anibyl.slounik.core;

import android.os.Build;

/**
 * Contains Android version related stuff.
 * <p/>
 * Created by Usievaład Čorny on 17.07.2016.
 */
public class Versioned {
    public static final String UTF_8 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            ? java.nio.charset.StandardCharsets.UTF_8.name()
            : "UTF-8";
}
