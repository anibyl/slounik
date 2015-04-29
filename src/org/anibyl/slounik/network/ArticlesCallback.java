package org.anibyl.slounik.network;

/**
 * Created by Usievaład Kimajeŭ on 29.4.15 17.47.
 */
public abstract class ArticlesCallback {
    public abstract void invoke(ArticlesInfo info);

    public void updateArticlesAmount(int amount) {
    }
}
