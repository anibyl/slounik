package org.anibyl.slounik;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.anibyl.slounik.core.Preferences;
import org.anibyl.slounik.dialogs.ArticleDialog;
import org.anibyl.slounik.network.ArticlesCallback;
import org.anibyl.slounik.network.ArticlesInfo;
import org.anibyl.slounik.network.Server;
import org.anibyl.slounik.network.SlounikOrg;
import org.anibyl.slounik.ui.ProgressBar;

import java.util.ArrayList;

/**
 * The main activity.
 * <p>
 * Created by Usievaład Čorny on 21.02.2015 11:00.
 */
public class SlounikActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private ListView listView;
    private TextView articlesAmount;
    private ArrayList<Article> articles;
    private SlounikAdapter adapter;
    private NavigationDrawerFragment navigationDrawerFragment;
    private CharSequence title;
    private ProgressBar progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.initialize(this);
        Server.loadConfig(this, new Server.Callback() {
            @Override
            public void invoke() {
                SlounikOrg.setMainUrl(Server.getMainUrl());
            }
        });
        if (LanguageSwitcher.initialize(this)) {
            return;
        }

        setContentView(R.layout.main);

        progress = (ProgressBar) findViewById(R.id.progress);

        listView = (ListView) findViewById(R.id.listView);
        articlesAmount = (TextView) findViewById(R.id.articles_amount);

        articles = new ArrayList<Article>();
        adapter = new SlounikAdapter(SlounikActivity.this, R.layout.list_item, R.id.description, articles);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ArticleDialog(SlounikActivity.this, articles.get(position)).show();
            }
        });

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        setTitle(R.string.app_name);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    public void search(final String wordToSearch) {
        resetArticles();

        if (wordToSearch.equals("")) {
            // TODO Make it visible for everyone.
            Notifier.toast(SlounikActivity.this, "Nothing to search.", true);
        } else {
            title = wordToSearch;
            restoreActionBar();
            progress.progressiveStart();
            navigationDrawerFragment.setSearchEnabled(false);

            SlounikOrg.loadArticles(wordToSearch, SlounikActivity.this, new ArticlesCallback() {
                @Override
                public void invoke(final ArticlesInfo info) {
                    ArrayList<Article> loadedArticles = info.getArticles();
                    if (loadedArticles != null) {
                        articles.addAll(loadedArticles);
                    }

                    switch (info.getStatus()) {
                        case SUCCESS:
                        case FAILURE:
                            resetControls();
                            break;
                    }

                    adapter.notifyDataSetChanged();

                    articlesAmount.setText(String.valueOf(articles.size()));
                }
            });
        }
    }

    private void resetControls() {
        progress.progressiveStop();
        navigationDrawerFragment.setSearchEnabled(true);
    }

    private void resetArticles() {
        articlesAmount.setText("");
        articles.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Do smth with selected getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }
}