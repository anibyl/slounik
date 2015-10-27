package org.anibyl.slounik.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import org.anibyl.slounik.R;
import org.anibyl.slounik.Util;

/**
 * About dialog.
 *
 * Created by Usievaład Čorny on 05.04.2015 9:36.
 */
public class AboutDialog extends Dialog {
    private final String title;
    private final Context context;

    public AboutDialog(Context context, String title) {
        super(context);

        this.title = title;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        setTitle(title);
        TextView androidIdField = (TextView) findViewById(R.id.about_android_id);
        androidIdField.setText(androidIdField.getText().toString().replace("%s", Util.getAndroidId(context)));
    }
}
