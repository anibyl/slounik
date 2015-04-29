package org.anibyl.slounik;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.anibyl.slounik.dialogs.AboutDialog;
import org.anibyl.slounik.dialogs.ArticleDialog;
import org.anibyl.slounik.network.ArticlesInfo;
import org.anibyl.slounik.network.SlounikOrg;

import java.util.ArrayList;

/**
 * The main activity.
 *
 * Created by Usievaład Čorny on 21.02.2015 11:00.
 */
public class SlounikActivity extends Activity {
    private EditText searchBox;
    private ImageButton searchButton;
    private ImageButton settingsButton;
    private ProgressBar spinner;
    private ListView listView;
    private AboutDialog aboutDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.initialize(this);

        setContentView(R.layout.main);

        searchBox = (EditText) findViewById(R.id.search_box);
        searchButton = (ImageButton) findViewById(R.id.search_button);
        settingsButton = (ImageButton) findViewById(R.id.settings_button);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.listView);

        aboutDialog = new AboutDialog(SlounikActivity.this, getString(R.string.about_title));

        spinner.setVisibility(View.INVISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String wordToSearch = searchBox.getText().toString();

                if (wordToSearch == null || wordToSearch.equals("")) {
                    // TODO Make it visible for everyone.
                    Notifier.toast(SlounikActivity.this, "Nothing to search.");
                } else {
                    spinner.setVisibility(View.VISIBLE);
                    searchButton.setEnabled(false);

                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

                    SlounikOrg.loadArticles(wordToSearch, SlounikActivity.this, new SlounikOrg.ArticlesCallback() {
                        @Override
                        public void invoke(final ArticlesInfo info) {
                            resetControls();

                            final ArrayList<Article> list = info.getArticles();

                            if (list != null) {
                                SlounikAdapter<String> adapter = new SlounikAdapter<String>(SlounikActivity.this,
                                        R.layout.list_item, R.id.description, list);

                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        new ArticleDialog(SlounikActivity.this, list.get(position)).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                aboutDialog.show();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void resetControls() {
        spinner.setVisibility(View.INVISIBLE);
        searchButton.setEnabled(true);
    }
}
