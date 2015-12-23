package org.anibyl.slounik.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.anibyl.slounik.Notifier;
import org.anibyl.slounik.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * skarnik.by website communication.
 * <p/>
 * Created by Usievaład Kimajeŭ on 22.12.15.
 */
public class Skarnik extends DictionarySiteCommunicator {
    private int requestCount;

    public Skarnik() {
        setUrl("skarnik.by");
    }

    @Override
    public void loadArticles(final String wordToSearch, final Context context, final ArticlesCallback callBack) {
        ArrayList<StringRequest> requests = new ArrayList<>();
        // TODO names to strings.
        requests.add(getLoadRequest(getRBRequestStr(wordToSearch), wordToSearch, context, callBack,
                getUrl() + " " + context.getResources().getString(R.string.skarnik_dictionary_rus_bel)));
        requests.add(getLoadRequest(getBRRequestStr(wordToSearch), wordToSearch, context, callBack,
                getUrl() + " " + context.getResources().getString(R.string.skarnik_dictionary_bel_rus)));
        requests.add(getLoadRequest(getExplanatoryRequestStr(wordToSearch), wordToSearch, context, callBack,
                getUrl() + " " + context.getResources().getString(R.string.skarnik_dictionary_explanatory)));

        requestCount = requests.size();

        for (StringRequest request : requests) {
            getQueue(context).add(request);
        }
    }

    @Override
    public void loadArticleDescription(final Article article, final Context context, final ArticlesCallback callBack) {
        // Skarnik has no loadable descriptions.
    }

    protected StringRequest getLoadRequest(final String requestStr, final String wordToSearch, final Context context,
            final ArticlesCallback callback, final String dictionaryTitle) {
        return new StringRequest(requestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        new AsyncTask<Void, Void, Article>() {
                            @Override
                            protected Article doInBackground(Void... params) {
                                Document page = Jsoup.parse(response);
                                Elements articleElements = page.select("p#trn");

                                if (articleElements.size() == 0) {
                                    return null;
                                } else {
                                    return parseElement(articleElements.first())
                                            .setTitle(wordToSearch)
                                            .setDictionary(dictionaryTitle);
                                }
                            }

                            @Override
                            protected void onPostExecute(final Article article) {
                                ArticlesInfo.Status status = --requestCount == 0 ?
                                        ArticlesInfo.Status.SUCCESS : ArticlesInfo.Status.IN_PROCESS;
                                ArticlesInfo info;
                                if (article != null) {
                                    info = new ArticlesInfo(new ArrayList<Article>() {{ add(article); }}, status);
                                } else {
                                    info = new ArticlesInfo(status);
                                }
                                callback.invoke(info);
                            }
                        }.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Notifier.toast(context, "Error response.", true);
                        // TODO fix it.
                        ArticlesInfo.Status status = --requestCount == 0 ?
                                ArticlesInfo.Status.FAILURE : ArticlesInfo.Status.IN_PROCESS;
                        callback.invoke(new ArticlesInfo(status));
                    }
                });
    }

    @Override
    protected Article parseElement(final Element element) {
        return new Article(this) {
            @Override
            Article fill() {
                setDescription(Html.fromHtml(element.html()));

                return this;
            }
        }.fill();
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
                .appendQueryParameter("term", wordToSearch);

        return builder.build().toString();
    }
}