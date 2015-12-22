package org.anibyl.slounik.network;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.anibyl.slounik.Article;

/**
 * Dictionary site communicator interface.
 *
 * Created by Usievaład Kimajeŭ on 22.12.15.
 */
public abstract class DictionarySiteCommunicator {
    private String url = "slounik.org";
    private RequestQueue queue;

    public abstract void loadArticles(String wordToSearch, Context context, ArticlesCallback callBack);
    public abstract void loadArticleDescription(Article article, Context context, ArticlesCallback callBack);

    public void setUrl(final String mainUrl) {
        url = mainUrl;
    }

    public String getUrl() {
        return url;
    }

    protected RequestQueue getQueue(final Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }

        return queue;
    }
}