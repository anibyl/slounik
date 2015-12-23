package org.anibyl.slounik.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import org.anibyl.slounik.Notifier;
import org.anibyl.slounik.core.Preferences;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * slounik.org website communication.
 * <p/>
 * Created by Usievaład Kimajeŭ on 8.4.15 14.17.
 */
public class SlounikOrg extends DictionarySiteCommunicator {
    public SlounikOrg() {
        setUrl("slounik.org");
    }

    public void loadArticles(final String wordToSearch, final Context context, final ArticlesCallback callBack) {
        final String requestStr;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(getUrl())
                .appendPath("search")
                .appendQueryParameter("search", wordToSearch);

        if (Preferences.getSearchInTitles()) {
            builder.appendQueryParameter("un", "1");
        }

        requestStr = builder.build().toString();

        SlounikOrgRequest request = getLoadRequest(requestStr, context, callBack);

        getQueue(context).add(request);
    }

    public void loadArticleDescription(final Article article, final Context context, final ArticlesCallback callBack) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(getUrl())
                .appendPath(article.getLinkToFullDescription().substring(1));
        final String requestStr = builder.build().toString();

        SlounikOrgRequest request = getArticleDescriptionLoadRequest(requestStr, article, callBack);

        getQueue(context).add(request);
    }

    @Override
    public boolean enabled() {
        return Preferences.getUseSlounikOrg();
    }

    protected SlounikOrgRequest getLoadRequest(final String requestStr, final Context context,
            final ArticlesCallback callback) {
        return new SlounikOrgRequest(requestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        new AsyncTask<Void, Void, Void>() {
                            private int dicsAmount;

                            @Override
                            protected Void doInBackground(Void... params) {
                                Document page = Jsoup.parse(response);
                                Elements dicsElements = page.select("a.treeSearchDict");
                                dicsAmount = dicsElements.size();

                                if (dicsAmount == 0) {
                                    return null;
                                }

                                for (Element e : dicsElements) {
                                    String dicRequestStr = e.attr("href");
                                    if (dicRequestStr != null) {
                                        Uri.Builder builder = new Uri.Builder();
                                        builder.scheme("http")
                                                .authority(getUrl())
                                                .appendPath(dicRequestStr.substring(1));
                                        dicRequestStr = builder.build().toString();
                                        SlounikOrgRequest eachDicRequest = getPerDicLoadingRequest(dicRequestStr,
                                                new ArticlesCallback() {
                                                    @Override
                                                    public void invoke(ArticlesInfo info) {
                                                        setArticleList(info.getArticles());
                                                    }
                                                });

                                        getQueue(context).add(eachDicRequest);
                                        Notifier.log("Request added to queue: " + eachDicRequest);
                                    }
                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                if (dicsAmount == 0) {
                                    Notifier.log("Callback invoked: no dictionaries.");
                                    callback.invoke(new ArticlesInfo(null, ArticlesInfo.Status.SUCCESS));
                                }
                            }

                            private void setArticleList(ArrayList<Article> list) {
                                ArticlesInfo.Status status = --dicsAmount == 0 ?
                                        ArticlesInfo.Status.SUCCESS : ArticlesInfo.Status.IN_PROCESS;
                                Notifier.log("Callback invoked, " + (list != null ? list.size() : 0)
                                        + " articles added.");
                                callback.invoke(new ArticlesInfo(list, status));
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

    @Override
    protected Article parseElement(final Element element) {
        return new Article(this) {
            @Override
            Article fill() {
                if (element != null) {
                    Elements elements = element.select("a.tsb");
                    if (elements != null && elements.size() != 0) {
                        Element link = elements.first();
                        setTitle(link.html());
                        setLinkToFullDescription(link.attr("href"));

                        if (getTitle() != null) {
                            elements = element.select("a.ts");
                            if (elements != null && elements.size() != 0) {
                                setDescription(Html.fromHtml(elements.first().html()));
                            }
                        }
                    }

                    if (getTitle() == null) {
                        elements = element.select("b");
                        if (elements != null && elements.size() != 0) {
                            setTitle(elements.first().html());

                            if (getTitle() != null) {
                                setDescription(Html.fromHtml(element.html()));
                            }
                        }
                    }

                    if (getTitle() != null) {
                        setTitle(getTitle().replaceAll("<u>", ""));
                        setTitle(getTitle().replaceAll("</u>", "́"));

                        // Escape all other HTML tags, e.g. second <b>.
                        setTitle(Jsoup.parse(getTitle()).text());
                    }

                    elements = element.select("a.la1");
                    if (elements != null && elements.size() != 0) {
                        setDictionary(elements.first().html());
                    }
                }

                return this;
            }
        }.fill();
    }

    private SlounikOrgRequest getPerDicLoadingRequest(final String dicRequestStr, final ArticlesCallback callback) {
        return new SlounikOrgRequest(dicRequestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        new AsyncTask<Void, Void, ArrayList<Article>>() {
                            @Override
                            protected ArrayList<Article> doInBackground(Void... params) {
                                Notifier.log("Response received for " + dicRequestStr + ".");
                                Document dicPage = Jsoup.parse(response);
                                Elements articleElements = dicPage.select("li#li_poszuk");

                                String dictionaryTitle = null;
                                Elements dictionaryTitles = dicPage.select("a.t3");
                                if (dictionaryTitles != null && dictionaryTitles.size() != 0) {
                                    dictionaryTitle = dictionaryTitles.first().html();
                                }

                                ArrayList<Article> list = new ArrayList<>();
                                for (Element e : articleElements) {
                                    list.add(parseElement(e).setDictionary(dictionaryTitle));
                                }

                                return list;
                            }

                            @Override
                            protected void onPostExecute(ArrayList<Article> articles) {
                                callback.invoke(new ArticlesInfo(articles));
                            }
                        }.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Notifier.log("Response error: " + error.getMessage());
                        callback.invoke(new ArticlesInfo(ArticlesInfo.Status.FAILURE));
                    }
                });
    }

    private static SlounikOrgRequest getArticleDescriptionLoadRequest(final String requestStr, final Article article,
                                                                      final ArticlesCallback callback) {
        return new SlounikOrgRequest(requestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        new AsyncTask<Void, Void, ArrayList<Article>>() {
                            @Override
                            protected ArrayList<Article> doInBackground(Void... params) {
                                Notifier.log("Response received for " + requestStr + ".");
                                Document articlePage = Jsoup.parse(response);
                                Element articleElement = articlePage.select("td.n12").first();

                                article.setFullDescription(Html.fromHtml(articleElement.html()));

                                ArrayList<Article> list = new ArrayList<>();
                                list.add(article);

                                return list;
                            }

                            @Override
                            protected void onPostExecute(ArrayList<Article> articles) {
                                callback.invoke(new ArticlesInfo(articles));
                            }
                        }.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Notifier.log("Response error: " + error.getMessage());
                        callback.invoke(new ArticlesInfo(ArticlesInfo.Status.FAILURE));
                    }
                });
    }

    private static class SlounikOrgRequest extends StringRequest {
        public SlounikOrgRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }
    }
}