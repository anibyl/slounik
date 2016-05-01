package org.anibyl.slounik.network;

import android.text.Spanned;

/**
 * Article.
 * <p/>
 * Created by Usievaład Čorny on 26.02.2015 14:06.
 */
public abstract class Article {
    private final DictionarySiteCommunicator communicator;
    private String title;
    private Spanned description;
    private String dictionary;
    private String linkToFullDescription;
    private Spanned fullDescription;

    public Article(DictionarySiteCommunicator communicator) {
        this.communicator = communicator;
    }

    public String getTitle() {
        return title;
    }

    public Spanned getDescription() {
        return description;
    }

    public String getDictionary() {
        return dictionary;
    }

    public String getLinkToFullDescription() {
        return linkToFullDescription;
    }

    public Spanned getFullDescription() {
        return fullDescription;
    }

    public DictionarySiteCommunicator getCommunicator() {
        return communicator;
    }

    abstract Article fill();

    Article setTitle(String title) {
        this.title = title;
        return this;
    }

    Article setDescription(Spanned description) {
        this.description = description;
        return this;
    }

    Article setDictionary(String dictionary) {
        this.dictionary = dictionary;
        return this;
    }

    Article setLinkToFullDescription(String linkToFullDescription) {
        this.linkToFullDescription = linkToFullDescription;
        return this;
    }

    void setFullDescription(Spanned fullDescription) {
        this.fullDescription = fullDescription;
    }
}