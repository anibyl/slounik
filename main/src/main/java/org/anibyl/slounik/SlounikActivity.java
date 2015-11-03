package org.anibyl.slounik;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import org.anibyl.slounik.dialogs.AboutDialog;
import org.anibyl.slounik.dialogs.ArticleDialog;
import org.anibyl.slounik.network.ArticlesCallback;
import org.anibyl.slounik.network.ArticlesInfo;
import org.anibyl.slounik.network.SlounikOrg;

import java.util.ArrayList;

/**
 * The main activity.
 *
 * Created by Usievaład Čorny on 21.02.2015 11:00.
 */
public class SlounikActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
//    private EditText searchBox;
//    private ImageButton searchButton;
//    private ImageButton settingsButton;
//    private ProgressBar spinner;
    private ListView listView;
    private AboutDialog aboutDialog;
//    private TextView dicAmountCounter;
    private ArrayList<Article> articles;
    private SlounikAdapter adapter;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private SmoothProgressBar progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.initialize(this);

        setContentView(R.layout.main);

        progress = (SmoothProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
//        searchBox = (EditText) findViewById(R.id.search_box);
//        searchButton = (ImageButton) findViewById(R.id.search_button);
//        settingsButton = (ImageButton) findViewById(R.id.settings_button);
//        spinner = (ProgressBar) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.listView);
//        dicAmountCounter = (TextView) findViewById(R.id.dic_amount_counter);

        aboutDialog = new AboutDialog(SlounikActivity.this, getString(R.string.about_title));

//        spinner.setVisibility(View.INVISIBLE);

        articles = new ArrayList<Article>();
        adapter = new SlounikAdapter(SlounikActivity.this, R.layout.list_item, R.id.description, articles);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ArticleDialog(SlounikActivity.this, articles.get(position)).show();
            }
        });

//        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE
//                    || (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)) {
//                    search();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                search();
//            }
//        });

//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                aboutDialog.show();
//            }
//        });

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
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
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
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
        actionBar.setTitle(mTitle);
    }

    public void search(final String wordToSearch) {
        resetArticles();

//        final String wordToSearch = searchBox.getText().toString();

        if (wordToSearch.equals("")) {
            // TODO Make it visible for everyone.
            Notifier.toast(SlounikActivity.this, "Nothing to search.");
        } else {
            mTitle = wordToSearch;
            restoreActionBar();
            progress.setVisibility(View.VISIBLE);
            progress.progressiveStart();
//            spinner.setVisibility(View.VISIBLE);
//            searchButton.setEnabled(false);

            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

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

//                    dicAmountCounter.setText(String.valueOf(articles.size()));
                }
            });
        }
    }

    private void resetControls() {
        progress.progressiveStop();
        progress.setVisibility(View.INVISIBLE);
//        spinner.setVisibility(View.INVISIBLE);
//        searchButton.setEnabled(true);
    }

    private void resetArticles() {
//        dicAmountCounter.setText("");
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
