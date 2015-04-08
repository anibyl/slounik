package org.anibyl.slounik.network;

import android.content.Context;
import android.os.AsyncTask;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.anibyl.slounik.Article;
import org.anibyl.slounik.Notifier;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * slounik.org website communication.
 *
 * Created by Usievaład Kimajeŭ on 8.4.15 14.17.
 */
public class SlounikOrg {
    private static RequestQueue queue;
    public static abstract class ArticlesCallBack {
        // TODO create callback data object.
        public abstract void invoke(Article[] list);
    }

    public static void loadArticles(String wordToSearch, final Context context, final ArticlesCallBack callBack) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }

        final String requestStr;
        try {
            requestStr = "http://slounik.org/search?search=" + URLEncoder.encode(wordToSearch, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            Notifier.toast(context, "Can not encode.");
            callBack.invoke(null);
            return;
        }

        StringRequest request = new StringRequest(requestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Notifier.toast(context, "Response received.");

                        new AsyncTask<String, Void, Article[]>() {
                            @Override
                            protected Article[] doInBackground(String... params) {
                                Document page = Jsoup.parse(response);
                                Elements bodies = page.select("li#li_poszuk");

                                Article[] list = new Article[bodies.size()];
                                int i = 0;
                                for (Element e : bodies) {
                                    list[i++] = new Article(e);
                                }

                                return list;
                            }

                            @Override
                            protected void onPostExecute(Article[] list) {
                                callBack.invoke(list);
                            }
                        }.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Notifier.toast(context, "Error response.");
                        callBack.invoke(null);
                    }
                });

        queue.add(request);
    }
}
