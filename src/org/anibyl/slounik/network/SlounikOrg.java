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
import java.util.ArrayList;

/**
 * slounik.org website communication.
 *
 * Created by Usievaład Kimajeŭ on 8.4.15 14.17.
 */
public class SlounikOrg {
    private static final String URL = "http://slounik.org";
    private static RequestQueue queue;

    public static abstract class ArticlesCallback {
        // TODO create callback data object.
        public abstract void invoke(ArrayList<Article> articles);
    }

    public static void loadArticles(String wordToSearch, final Context context, final ArticlesCallback callBack) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }

        final String requestStr;
        try {
            requestStr = URL + "/search?search=" + URLEncoder.encode(wordToSearch, HTTP.UTF_8);
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

                        new AsyncTask<Void, Void, Void>() {
                            private ArrayList<Article> articles = new ArrayList<Article>();
                            private int dicsAmount;

                            @Override
                            protected Void doInBackground(Void... params) {
                                Document page = Jsoup.parse(response);
                                Elements dicsElements = page.select("a.treeSearchDict");
                                dicsAmount = dicsElements.size();

                                for (Element e : dicsElements) {
                                    String dicRequestStr = e.attr("href");
                                    if (dicRequestStr != null) {
                                        try {
                                            dicRequestStr = URL + "/" + URLEncoder.encode(dicRequestStr.substring(1),
                                                    HTTP.UTF_8);
                                        } catch (UnsupportedEncodingException e1) {
                                            Notifier.log("UnsupportedEncodingException");
                                            // TODO handle it.
                                        }
                                        StringRequest eachDicRequest = new StringRequest(dicRequestStr,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(final String response) {
                                                        new AsyncTask<Void, Void, ArrayList<Article>>() {
                                                            @Override
                                                            protected ArrayList<Article> doInBackground(Void... params) {
                                                                Notifier.log("Response received.");
                                                                Document dicPage = Jsoup.parse(response);
                                                                Elements articleElements = dicPage.select("li#li_poszuk");

                                                                String dictionaryTitle = null;
                                                                Elements dictionaryTitles = dicPage.select("a.t3");
                                                                if (dictionaryTitles != null && dictionaryTitles.size() != 0) {
                                                                    dictionaryTitle = dictionaryTitles.first().html();
                                                                }

                                                                ArrayList<Article> list = new ArrayList<Article>();
                                                                for (Element e : articleElements) {
                                                                    Notifier.log("Element: " + e.html());
                                                                    list.add(new Article(e).setDictionary(dictionaryTitle));
                                                                }

                                                                return list;
                                                            }

                                                            @Override
                                                            protected void onPostExecute(ArrayList<Article> articles) {
                                                                setArticleList(articles);
                                                            }
                                                        }.execute();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Notifier.log("Response error: " + error.getMessage());
                                                        setArticleList(new ArrayList<Article>());
                                                    }
                                                });

                                        queue.add(eachDicRequest);
                                        Notifier.log("Request added to queue: " + eachDicRequest);
                                    }
                                }

                                return null;
                            }

                            private void setArticleList(ArrayList<Article> list) {
                                articles.addAll(list);

                                if (--dicsAmount == 0) {
                                    Notifier.log("Callback invoked.");
                                    callBack.invoke(articles);
                                }
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
