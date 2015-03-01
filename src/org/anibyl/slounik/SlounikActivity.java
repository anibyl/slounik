package org.anibyl.slounik;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * The main activity.
 *
 * Created by Usievaład Čorny on 21.02.2015 11:00.
 */
public class SlounikActivity extends Activity {
    private EditText searchBox;
    private ImageButton searchButton;
    private ProgressBar spinner;
    private ListView listView;
    private volatile Article[] list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        searchBox = (EditText) findViewById(R.id.search_box);
        searchButton = (ImageButton) findViewById(R.id.search_button);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.listView);

        spinner.setVisibility(View.INVISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String wordToSearch = searchBox.getText().toString();

                if (wordToSearch == null || wordToSearch.equals("")) {
                    Toast.makeText(SlounikActivity.this, "Nothing to search.", Toast.LENGTH_SHORT).show();
                } else {
                    spinner.setVisibility(View.VISIBLE);
                    searchButton.setEnabled(false);

                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

                    getInfo(wordToSearch);
                }
            }
        });
    }

    private void getInfo(String wordToSearch) {
        RequestQueue queue = Volley.newRequestQueue(SlounikActivity.this);
        final String requestStr;
        try {
            requestStr = "http://slounik.org/search?search=" + URLEncoder.encode(wordToSearch, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {resetControls();
            Toast.makeText(SlounikActivity.this, "Can not encode.", Toast.LENGTH_SHORT).show();
            return;
        }
        StringRequest request = new StringRequest(requestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Toast.makeText(SlounikActivity.this, "Response received.", Toast.LENGTH_SHORT).show();

                        new AsyncTask<String, SlounikAdapter<String>, SlounikAdapter<String>>() {
                            @Override
                            protected SlounikAdapter<String> doInBackground(String... params) {
                                Document page = Jsoup.parse(response);
                                Elements bodies = page.select("li#li_poszuk");

                                Article[] list = new Article[bodies.size()];
                                int i = 0;
                                for (Element e : bodies) {
                                    list[i++] = new Article(e);
                                }

                                setList(list);

                                return new SlounikAdapter<String>(SlounikActivity.this, R.layout.list_item, R.id.description, list);
                            }

                            @Override
                            protected void onPostExecute(SlounikAdapter<String> adapter) {
                                super.onPostExecute(adapter);
                                resetControls();
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        new ArticleDialog(SlounikActivity.this, list[position]).show();
                                    }
                                });
                            }
                        }.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SlounikActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                        resetControls();
                    }
                });

        queue.add(request);
    }

    private void resetControls() {
        spinner.setVisibility(View.INVISIBLE);
        searchButton.setEnabled(true);
    }

    private synchronized void setList(Article[] list) {
        this.list = list;
    }
}