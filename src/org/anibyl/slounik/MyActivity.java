package org.anibyl.slounik;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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

public class MyActivity extends Activity {
    private EditText searchBox;
    private Button searchButton;
    private ProgressBar spinner;
    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        searchBox = (EditText) findViewById(R.id.searchBox);
        searchButton = (Button) findViewById(R.id.searchButton);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.listView);

        spinner.setVisibility(View.INVISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String wordToSearch = searchBox.getText().toString();

                if (wordToSearch == null || wordToSearch.equals("")) {
                    Toast.makeText(MyActivity.this, "Nothing to search.", Toast.LENGTH_SHORT).show();
                } else {
                    spinner.setVisibility(View.VISIBLE);

                    RequestQueue queue = Volley.newRequestQueue(MyActivity.this);
                    final String requestStr;
                    try {
                        requestStr = "http://slounik.org/search?search=" + URLEncoder.encode(wordToSearch, HTTP.UTF_8);
                    } catch (UnsupportedEncodingException e) {
                        spinner.setVisibility(View.INVISIBLE);
                        Toast.makeText(MyActivity.this, "Can not encode.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringRequest request = new StringRequest(requestStr,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    spinner.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MyActivity.this, "Response received.", Toast.LENGTH_SHORT).show();

                                    Document page = Jsoup.parse(response);
                                    Elements headers = page.select("a.tsb");
                                    Elements bodies = page.select("a.ts");

                                    String[] headersArr = new String[headers.size()], bodiesArr = new String[bodies.size()];
                                    int i = 0;
                                    for (Element e : headers) {
                                        headersArr[i++] = e.text();
                                    }
                                    i = 0;
                                    for (Element e : bodies) {
                                        bodiesArr[i++] = e.text();
                                    }

                                    final ArrayAdapter<String> headersAdapter = new ArrayAdapter<String>(MyActivity.this, R.layout.list_item, R.id.firstLine, headersArr);
                                    final ArrayAdapter<String> bodiesAdapter = new ArrayAdapter<String>(MyActivity.this, R.layout.list_item, R.id.secondLine, bodiesArr);
                                    listView.setAdapter(headersAdapter);
                                    listView.setAdapter(bodiesAdapter);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    spinner.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MyActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    queue.add(request);
                }
            }
        });
    }
}
