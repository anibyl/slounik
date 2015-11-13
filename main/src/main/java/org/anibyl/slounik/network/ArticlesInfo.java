package org.anibyl.slounik.network;

import org.anibyl.slounik.Article;

import java.util.ArrayList;

/**
 * Represents article information.
 *
 * Contains articles and connected information.
 *
 * Created by Usievaład Kimajeŭ on 29.4.15 16.08.
 */
public class ArticlesInfo {
    public enum Status {
        SUCCESS,
        IN_PROCESS,
        FAILURE
    }

    private ArrayList<Article> articles;
    private Status status;

    public ArticlesInfo(ArrayList<Article> articles, Status status) {
        this.articles = articles;
        this.status = status;
    }

    public ArticlesInfo(ArrayList<Article> articles) {
        this.articles = articles;

        if (articles == null) {
            status = Status.FAILURE;
        } else {
            status = Status.SUCCESS;
        }
    }

    public ArticlesInfo(Status status) {
        this.status = status;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public Status getStatus() {
        return status;
    }
}
