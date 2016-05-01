package org.anibyl.slounik.network;

import android.content.Context;

/**
 * Articles loader interface.
 * <p/>
 * Created by Usievaład Kimajeŭ on 23.12.15.
 */
public interface ArticlesLoader {
    void loadArticles(String wordToSearch, Context context, ArticlesCallback callBack);
}