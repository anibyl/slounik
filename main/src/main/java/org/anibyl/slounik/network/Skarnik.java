package org.anibyl.slounik.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.anibyl.slounik.Article;
import org.anibyl.slounik.Notifier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * skarnik.by website communication.
 * <p/>
 * Created by Usievaład Kimajeŭ on 22.12.15.
 */
public class Skarnik extends DictionarySiteCommunicator {
    public Skarnik() {
        setUrl("skarnik.by");
    }

    @Override
    public void loadArticles(final String wordToSearch, final Context context, final ArticlesCallback callBack) {
        getQueue(context).add(getLoadRequest(getRBRequestStr(wordToSearch), context, callBack, "РБ"));
        getQueue(context).add(getLoadRequest(getBRRequestStr(wordToSearch), context, callBack, "БР"));
        getQueue(context).add(getLoadRequest(getExplanatoryRequestStr(wordToSearch), context, callBack, "Тлумачальны"));
        // TODO names to strings.
    }

    @Override
    public void loadArticleDescription(final Article article, final Context context, final ArticlesCallback callBack) {
        // Skarnik has no loadable descriptions.
    }

    protected StringRequest getLoadRequest(final String requestStr, final Context context,
            final ArticlesCallback callback, final String dictionaryTitle) {
        return new StringRequest(requestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Notifier.toast(context, "Response received.", true);

                        new AsyncTask<Void, Void, Article>() {
                            @Override
                            protected Article doInBackground(Void... params) {
                                Document page = Jsoup.parse(response);
                                Elements articleElements = page.select("p#trn");

                                // TODO implement Skarnik parser.
                                return new Article(Skarnik.this, articleElements.first()).setDictionary(dictionaryTitle);
                            }

                            @Override
                            protected void onPostExecute(final Article article) {
                                callback.invoke(new ArticlesInfo(new ArrayList<Article>() {{ add(article); }},
                                        ArticlesInfo.Status.SUCCESS));
                            }
                        }.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Notifier.toast(context, "Error response.", true);
                        callback.invoke(new ArticlesInfo(ArticlesInfo.Status.FAILURE));
                    }
                });
    }

    private String getRBRequestStr(final String wordToSearch) {
        return getRequestStr(wordToSearch, "rus");
    }

    private String getBRRequestStr(final String wordToSearch) {
        return getRequestStr(wordToSearch, "bel");
    }

    private String getExplanatoryRequestStr(final String wordToSearch) {
        return getRequestStr(wordToSearch, "beld");
    }

    private String getRequestStr(final String wordToSearch, final String language) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(getUrl())
                .appendPath("search")
                .appendQueryParameter("lang", language)
                .appendQueryParameter("search", wordToSearch);

        return builder.build().toString();
    }
}